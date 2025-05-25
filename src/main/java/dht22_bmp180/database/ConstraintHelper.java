package dht22_bmp180.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import client.Constants;

class ConstraintHelper {
	static final String CREATE_TABLE_IF_NOT_EXIST = "CREATE TABLE IF NOT EXISTS %s ";
	static final String INSERT_INTO = "INSERT INTO %s ";
	static final String DELETE_FROM = "DELETE FROM %s Where ";
	static final String SELECT_FROM = "SELECT * FROM %s ;";
	static final String SELECT_TIMESTAMP_AND_COLUMN_FROM = "SELECT TIME, %s FROM %s ;";
	static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS %s";
	static final String INTEGER = "INTEGER";
	static final String DOUBLEDEFAULTNULL = "DOUBLE DEFAULT NULL";

	static HashMap<String, String> getConstraintsForDHT22Data() {
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuilder columnConstraint = new StringBuilder();

		columnConstraint.append(INTEGER);
		map.put(Constants.Time, columnConstraint.toString());
		columnConstraint.setLength(0);

		columnConstraint.append(DOUBLEDEFAULTNULL);

		map.put(Constants.Temperature_Out, columnConstraint.toString());
		map.put(Constants.Temperature_In, columnConstraint.toString());
		map.put(Constants.Humidity_Out, columnConstraint.toString());
		map.put(Constants.Humidity_In, columnConstraint.toString());
		return map;
	}

	static HashMap<String, String> getConstraintsForBMP180Data() {
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuilder columnConstraint = new StringBuilder();

		columnConstraint.append(INTEGER);
		map.put(Constants.Time, columnConstraint.toString());
		columnConstraint.setLength(0);

		columnConstraint.append(DOUBLEDEFAULTNULL);

		map.put(Constants.Pressure, columnConstraint.toString());
		return map;
	}

	static HashMap<String, String> getConstraintsForMaxMinData() {
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuilder columnConstraint = new StringBuilder();

		columnConstraint.append(INTEGER);
		map.put(Constants.Time, columnConstraint.toString());
		columnConstraint.setLength(0);

		columnConstraint.append(DOUBLEDEFAULTNULL);

		map.put(Constants.MaxTemperature_Out, columnConstraint.toString());
		map.put(Constants.MaxTemperature_In, columnConstraint.toString());
		map.put(Constants.MinTemperature_Out, columnConstraint.toString());
		map.put(Constants.MinTemperature_In, columnConstraint.toString());

		map.put(Constants.MaxHumidity_Out, columnConstraint.toString());
		map.put(Constants.MaxHumidity_In, columnConstraint.toString());
		map.put(Constants.MinHumidity_Out, columnConstraint.toString());
		map.put(Constants.MinHumidity_In, columnConstraint.toString());

		map.put(Constants.MaxPressure, columnConstraint.toString());
		map.put(Constants.MinPressure, columnConstraint.toString());

		return map;
	}

	static PreparedStatement getInsertStatementForDHT22(DatabaseConnection connection, long time, double temperatureOut,
			double temperatureIn, double humidityOut, double humidityIn) throws SQLException {
		PreparedStatement preparedStatement = connection.getConnection()
				.prepareStatement(String.valueOf(String.format(INSERT_INTO, new Object[] { Constants.DHT22Table }))
						+ "(" + Constants.Time + "," + Constants.Temperature_Out + "," + Constants.Temperature_In + ","
						+ Constants.Humidity_Out + "," + Constants.Humidity_In + ")" + "values (?, ?, ?, ?, ?)");
		preparedStatement.setLong(1, time);
		preparedStatement.setDouble(2, temperatureOut);
		preparedStatement.setDouble(3, temperatureIn);
		preparedStatement.setDouble(4, humidityOut);
		preparedStatement.setDouble(5, humidityIn);

		return preparedStatement;
	}

	static PreparedStatement getInsertStatementForBMP180(DatabaseConnection connection, long time, double pressure)
			throws SQLException {
		PreparedStatement preparedStatement = connection.getConnection()
				.prepareStatement(String.valueOf(String.format(INSERT_INTO, new Object[] { Constants.BMP180Table }))
						+ "(" + Constants.Time + "," + Constants.Pressure + ")" + "values (?, ?)");
		preparedStatement.setLong(1, time);
		preparedStatement.setDouble(2, pressure);

		return preparedStatement;
	}
}