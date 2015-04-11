package com.cjwatts.wally.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;

import org.openimaj.experiment.evaluation.classification.ClassificationResult;

import com.cjwatts.wally.analysis.Category;
import com.cjwatts.wally.analysis.Subject;
import com.cjwatts.wally.analysis.SubjectFactory;
import com.cjwatts.wally.analysis.feature.Feature;

public class SubjectMatcher {

	static {
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	private final String dbUri;
	protected final Connection connection;
	protected int maximumDistance;
	
	public SubjectMatcher(String dbUri) {
		
		maximumDistance =
				Category.values().length
				* SubjectFactory.databaseBackedSubject(dbUri).getFeatures().size();
		
		try {
			this.dbUri = dbUri;
			connection = DriverManager.getConnection(dbUri);
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * @param source The subject probe
	 * @return Closest subjects to the probe by their feature distance
	 */
	public ClassificationResult<Subject> findSimilar(Subject source) {
		return findSimilar(source, -1);
	}
	
	/**
	 * @param source The subject probe
	 * @param limit The number of results to find. Set to -1 for no limit
	 * @return Closest subjects to the probe by their feature distance
	 */
	public ClassificationResult<Subject> findSimilar(Subject source, int limit) {
		PrintableClassificationResult<Subject> matches = new PrintableClassificationResult<>(PrintableClassificationResult.RESULT_LIST);
		
		// Construct a sum of differences probe
		StringBuilder probe = new StringBuilder();
		probe.append("SELECT id, SUM(");
		
		for (Entry<Feature, Category> e : source) {
			// Format: ABS(column-value)+...
			probe.append("\nABS(");
			probe.append(e.getKey());
			probe.append("-");
			probe.append(e.getValue().ordinal());
			probe.append(")+");
		}
		// Remove last +
		probe.deleteCharAt(probe.length() - 1);
		
		probe.append(") as distance\n");
		probe.append("FROM subjects\n");
		probe.append("GROUP BY id\n");
		probe.append("ORDER BY distance ASC\n");
		if (limit > 0) {
			probe.append("FETCH FIRST ");
			probe.append(limit);
			probe.append(" ROWS ONLY");
		}

		try {
			Statement stmt = connection.createStatement();
			ResultSet result = stmt.executeQuery(probe.toString());
			while (result.next()) {
				Subject s = SubjectFactory.databaseBackedSubject(result.getString("id"), dbUri);
				double ratio = 1 - result.getFloat("distance") / maximumDistance;
				matches.put(s, ratio);
			}
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		return matches;
	}
	
}
