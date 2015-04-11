package com.cjwatts.wally.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabasePersistenceHandler<I> implements PersistenceHandler<I> {

	static {
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	protected I instance;
	protected final Connection connection;
	
	public DatabasePersistenceHandler(I instance) {
		this("jdbc:derby:db", instance);
	}
	
	public DatabasePersistenceHandler(String dbUri) {
		this(dbUri, null);
	}
	
	public DatabasePersistenceHandler(String dbUri, I instance) {
		this.instance = instance;
		
		try {
			connection = DriverManager.getConnection(dbUri);
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			throw new RuntimeException(ex);
		}
	}

	public void setInstance(I instance) {
		this.instance = instance;
	}
	
}
