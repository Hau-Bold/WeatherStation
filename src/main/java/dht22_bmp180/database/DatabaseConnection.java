package dht22_bmp180.database;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.sqlite.SQLiteConfig;

public class DatabaseConnection {

	private Connection myConnection;

	public DatabaseConnection(Path connectionPath) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("error while loading jdbc-driver");
			e.printStackTrace();
		}

		initDatabaseConnection(connectionPath);
	}

	private void initDatabaseConnection(Path databaseConnectionPath) {
		try {
			SQLiteConfig config = new SQLiteConfig();

			myConnection = DriverManager.getConnection("jdbc:sqlite:" + databaseConnectionPath, config.toProperties());
		} catch (SQLException e) {
			System.out.println("connection" + e.getMessage());
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				closeConnection();
			}
		});
	}

	private void closeConnection() {
		try {
			if (myConnection != null && !myConnection.isClosed()) {
				myConnection.close();
				if (myConnection.isClosed())
					System.out.println("Status(database): disconnected");
			}
		} catch (SQLException ex) {
			System.out.println("Error! " + ex.getMessage());
		}
	}

	public Connection getConnection() {
		return myConnection;
	}
}