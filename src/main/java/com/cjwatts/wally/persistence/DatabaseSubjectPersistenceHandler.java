package com.cjwatts.wally.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.Subject;
import com.cjwatts.wally.analysis.feature.Feature;

public class DatabaseSubjectPersistenceHandler extends
		DatabasePersistenceHandler<Subject> {

	public DatabaseSubjectPersistenceHandler(String dbPath) {
		super(dbPath);
	}	
	
	public DatabaseSubjectPersistenceHandler(String dbPath, Subject instance) {
		super(dbPath, instance);
	}																				

	@Override
	public boolean load() {
		if (instance == null) return false;
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM subjects WHERE id = ?");
			stmt.setString(1, instance.toString());
			ResultSet result = stmt.executeQuery();
			
			// If there are no results, return false
			if (result.next() == false) return false;
			
			// Restore the values for each known feature
			for (Feature f : instance.getFeatures().keySet()) {
				Integer cat = result.getInt(f.toString());
				instance.setCategory(f, Category.values()[cat]);
			}
			
			return true;
			
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean save() {
		if (instance == null) return false;
		
		// Create two queries - update and insert (for when update fails)
		StringBuilder update = new StringBuilder();
		StringBuilder insert = new StringBuilder();
		StringBuilder cols = new StringBuilder();
		StringBuilder vals = new StringBuilder();
		
		update.append("UPDATE subjects SET ");
		insert.append("INSERT INTO subjects (");
		
		for (Entry<Feature, Category> e : instance) {
			update.append(e.getKey());
			update.append('=');
			update.append(e.getValue().ordinal());
			update.append(',');
			
			cols.append(e.getKey());
			cols.append(',');
			vals.append(e.getValue().ordinal());
			vals.append(',');
		}
		// Remove last commas
		update.deleteCharAt(update.length() - 1);
		cols.deleteCharAt(cols.length() - 1);
		vals.deleteCharAt(vals.length() - 1);
		
		// Compile update statement
		update.append(" WHERE id = ?");
		
		// Compile insert statement
		insert.append("id,");
		insert.append(cols);
		insert.append(") VALUES(?,");
		insert.append(vals);
		insert.append(')');
		
		try {
			
			// Try the update first
			PreparedStatement stmt = connection.prepareStatement(update.toString());
			stmt.setString(1, instance.toString());
			int rows = stmt.executeUpdate();
			
			if (rows > 0) return true;
			
			// No existing row, insert
			stmt = connection.prepareStatement(insert.toString());
			stmt.setString(1, instance.toString());
			rows = stmt.executeUpdate();
			
			return rows > 0;
			
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return false;
		}
	}

}
