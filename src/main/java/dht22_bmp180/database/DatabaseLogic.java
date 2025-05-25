package dht22_bmp180.database;

import static dht22_bmp180.database.ConstraintHelper.CREATE_TABLE_IF_NOT_EXIST;
import static dht22_bmp180.database.ConstraintHelper.DELETE_FROM;
import static dht22_bmp180.database.ConstraintHelper.DROP_TABLE_IF_EXISTS;
import static dht22_bmp180.database.ConstraintHelper.INSERT_INTO;
import static dht22_bmp180.database.ConstraintHelper.SELECT_TIMESTAMP_AND_COLUMN_FROM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import client.Constants;
import client.IOperatingSystemSettings;
import dht22_bmp180.weatherDataSets.Data;

public class DatabaseLogic {
	private DatabaseConnection myConnection;
	private final long myPeriodDaily = 86400000L;
	private final long myPeriodYearly = 1471228928L;
	private IOperatingSystemSettings myOperatingSystemSettings;

	public DatabaseLogic(IOperatingSystemSettings operatingSystemSettings) {

		myOperatingSystemSettings = operatingSystemSettings;
	}

	private void connect() throws SQLException {
		if (myConnection != null) {
			myConnection.getConnection().close();
		}
		myConnection = new DatabaseConnection(myOperatingSystemSettings.getDatabase());
		myConnection.getConnection().setAutoCommit(true);
	}

	private void disconnect() {
		try {
			if (myConnection != null) {
				myConnection.getConnection().close();
			}
		} catch (SQLException e) {
			System.out.println("Database: Status: Not able to disconnect");
			e.printStackTrace();
		}
	}

	public void createDHT22Table() throws SQLException {
		HashMap<String, String> map = ConstraintHelper.getConstraintsForDHT22Data();

		createTable(Constants.DHT22Table, map);
	}

	public void createBMP180Table() throws SQLException {
		HashMap<String, String> map = ConstraintHelper.getConstraintsForBMP180Data();

		createTable(Constants.BMP180Table, map);
	}

	private void createTable(String nameOfTable, Map<String, String> queryMap) throws SQLException {
		connect();
		StringBuilder sqlBuilder = new StringBuilder();

		sqlBuilder.append(String.format(CREATE_TABLE_IF_NOT_EXIST, new Object[] { nameOfTable }));
		sqlBuilder.append(" ( ");

		Iterator<String> iter = queryMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			sqlBuilder.append(String.valueOf(key) + " ");
			sqlBuilder.append(queryMap.get(key));
			boolean isLastEntry = (queryMap.keySet().size() == 1);
			if (!isLastEntry) {
				sqlBuilder.append(",");
			}
			iter.remove();
		}
		sqlBuilder.append(");");

		Statement statement = null;

		try {
			statement = myConnection.getConnection().createStatement();
			statement.executeUpdate(sqlBuilder.toString());
			System.out.println(String.format("Table %s was created with query %s",
					new Object[] { nameOfTable, sqlBuilder.toString() }));
			statement.close();
		} catch (Exception ex) {
			System.err.println(String.format("Was not able to create Table %s with query %s",
					new Object[] { nameOfTable, sqlBuilder.toString() }));
			statement.close();
			ex.printStackTrace();
		}
		disconnect();
	}

	public void insertMaxMinData(long currentTime, double maxTemperatureOut, double maxTemperatureIn,
			double minTemperatureOut, double minTemperatureIn, double maxHumidityOut, double maxHumidityIn,
			double minHumidityOut, double minHumidityIn, double maxPressure, double minPressure) throws SQLException {
		connect();

		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = myConnection.getConnection()

					.prepareStatement(String.valueOf(String.format(INSERT_INTO, new Object[] { Constants.MaxMinTable }))
							+ "(" + Constants.Time + "," + Constants.MaxTemperature_Out + ","
							+ Constants.MaxTemperature_In + "," + Constants.MinTemperature_Out + ","
							+ Constants.MinTemperature_In + "," + Constants.MaxHumidity_Out + ","
							+ Constants.MaxHumidity_In + "," + Constants.MinHumidity_Out + ","
							+ Constants.MinHumidity_In + "," + Constants.MaxPressure + "," + Constants.MinPressure + ")"
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			preparedStatement.setLong(1, currentTime);

			preparedStatement.setDouble(2, maxTemperatureOut);
			preparedStatement.setDouble(3, maxTemperatureIn);
			preparedStatement.setDouble(4, minTemperatureOut);
			preparedStatement.setDouble(5, minTemperatureIn);

			preparedStatement.setDouble(6, maxHumidityOut);
			preparedStatement.setDouble(7, maxHumidityIn);
			preparedStatement.setDouble(8, minHumidityOut);
			preparedStatement.setDouble(9, minHumidityIn);
			preparedStatement.setDouble(10, maxPressure);
			preparedStatement.setDouble(11, minPressure);

			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		disconnect();
	}

	public void insertDHT22Data(long currentTime, double temperatureOut, double temperatureIn, double humidityOut,
			double humidityIn) throws SQLException {
		connect();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = ConstraintHelper.getInsertStatementForDHT22(myConnection, currentTime,
					temperatureOut - 273.5, temperatureIn, humidityOut, humidityIn);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		disconnect();
	}

	public void insertBMP180Data(long currentTime, double pressure) throws SQLException {
		connect();

		PreparedStatement preparedStatement = ConstraintHelper.getInsertStatementForBMP180(myConnection, currentTime,
				pressure);

		preparedStatement.executeUpdate();
		preparedStatement.close();

		disconnect();
	}

	public void createMinMaxTable() throws SQLException {
		HashMap<String, String> map = ConstraintHelper.getConstraintsForMaxMinData();
		createTable(Constants.MaxMinTable, map);
	}

	public List<Data> getData_MaxMin(String columnName) throws SQLException, InterruptedException {
		Statement statement = null;
		ResultSet resultSet = null;
		List<Data> response = new ArrayList<Data>();
		String sql = String.valueOf(
				String.format(SELECT_TIMESTAMP_AND_COLUMN_FROM, new Object[] { columnName, Constants.MaxMinTable }))
				+ ";";

		waitUntilDatabaseHasFinished();

		connect();

		statement = myConnection.getConnection().createStatement();
		resultSet = statement.executeQuery(sql);

		if (resultSet != null) {
			while (resultSet.next()) {

				long currentTime = resultSet.getLong(1);
				double value = resultSet.getDouble(2);

				response.add(new Data(currentTime, value));
			}
		}
		resultSet.close();
		statement.close();

		disconnect();
		return response;
	}

	public List<Data> getDHT22Data(String columnName) throws SQLException, InterruptedException {
		Statement statement = null;
		ResultSet resultSet = null;
		List<Data> response = new ArrayList<Data>();
		String sql = String.valueOf(
				String.format(SELECT_TIMESTAMP_AND_COLUMN_FROM, new Object[] { columnName, Constants.DHT22Table }))
				+ ";";

		waitUntilDatabaseHasFinished();
		connect();

		statement = myConnection.getConnection().createStatement();
		resultSet = statement.executeQuery(sql);

		if (resultSet != null) {
			while (resultSet.next()) {

				long currentTime = resultSet.getLong(1);
				double temperature = resultSet.getDouble(2);

				response.add(new Data(currentTime, temperature));
			}
		}
		resultSet.close();
		statement.close();

		disconnect();
		return response;
	}

	public List<Data> getBMP180Data() throws SQLException, InterruptedException {
		Statement statement = null;
		ResultSet resultSet = null;
		List<Data> response = new ArrayList<Data>();

		waitUntilDatabaseHasFinished();
		connect();

		String sql = String.valueOf(String.format(SELECT_TIMESTAMP_AND_COLUMN_FROM,
				new Object[] { Constants.Pressure, Constants.BMP180Table })) + ";";
		statement = myConnection.getConnection().createStatement();
		resultSet = statement.executeQuery(sql);

		if (resultSet != null) {
			while (resultSet.next()) {

				long currentTime = resultSet.getLong(1);
				double pressure = resultSet.getDouble(2);
				response.add(new Data(currentTime, pressure));
			}
		}
		resultSet.close();
		statement.close();

		disconnect();

		return response;
	}

	private void dropTable(String nameOfTable) throws SQLException {
		connect();

		String sqlCommandString = String.format(DROP_TABLE_IF_EXISTS, new Object[] { nameOfTable });

		Statement statement = null;

		try {
			statement = myConnection.getConnection().createStatement();
			statement.executeUpdate(sqlCommandString);
		} catch (SQLException e) {
			System.err.println(String.format("dropping table %s was impossible", new Object[] { nameOfTable }));
			e.printStackTrace();
			statement.close();
		}

		disconnect();
	}

	private double getConstraint(String constraint, String nameOfColumn, String nameOfTable) throws SQLException {
		Statement statement = null;
		ResultSet resultSet = null;
		connect();

		String sql = "SELECT " + constraint + "(" + nameOfColumn + ") From " + nameOfTable + ";";
		statement = myConnection.getConnection().createStatement();
		resultSet = statement.executeQuery(sql);

		double result = 0.0D;

		if (resultSet != null) {
			while (resultSet.next()) {
				result = resultSet.getDouble(1);
			}
		}
		resultSet.close();
		statement.close();

		disconnect();

		return result;
	}

	public void doHouseKeepingOfWeatherData(long currentTimeInMillis) throws SQLException {
		doHouseKeeping(Constants.DHT22Table, currentTimeInMillis, myPeriodDaily);
		doHouseKeeping(Constants.BMP180Table, currentTimeInMillis, myPeriodDaily);
	}

	public void doHouseKeepingOfMaxMinData(long currentTimeInMillis) throws SQLException {
		doHouseKeeping(Constants.MaxMinTable, currentTimeInMillis, myPeriodYearly);
	}

	private void doHouseKeeping(String nameOfTable, long currentTimeInMillis, long period) throws SQLException {
		connect();

		String sqlCommandString = String.valueOf(String.format(DELETE_FROM, new Object[] { nameOfTable }))
				+ currentTimeInMillis + " - " + Constants.Time + " > " + period;

		try {
			Statement statement = myConnection.getConnection().createStatement();
			statement.executeUpdate(sqlCommandString);
			statement.close();
		} catch (SQLException e) {

			System.err.println(sqlCommandString);
			e.printStackTrace();
		}

		disconnect();
	}

	public void dropDHT22Table() throws SQLException {
		dropTable(Constants.DHT22Table);
	}

	public void dropBMP180Table() throws SQLException {
		dropTable(Constants.BMP180Table);
	}

	public void dropMaxMinTable() throws SQLException {
		dropTable(Constants.MaxMinTable);
	}

	public double getMaximalTemperatureOut() throws SQLException {
		return getConstraint(Constants.Max, Constants.Temperature_Out, Constants.DHT22Table);
	}

	public double getMaximalTemperatureIn() throws SQLException {
		return getConstraint(Constants.Max, Constants.Temperature_In, Constants.DHT22Table);
	}

	public double getMinimalTemperatureOut() throws SQLException {
		return getConstraint(Constants.Min, Constants.Temperature_Out, Constants.DHT22Table);
	}

	public double getMinimalTemperatureIn() throws SQLException {
		return getConstraint(Constants.Min, Constants.Temperature_In, Constants.DHT22Table);
	}

	public double getMaximalHumidityOut() throws SQLException {
		return getConstraint(Constants.Max, Constants.Humidity_Out, Constants.DHT22Table);
	}

	public double getMaximalHumidityIn() throws SQLException {
		return getConstraint(Constants.Max, Constants.Humidity_In, Constants.DHT22Table);
	}

	public double getMinimalHumidityOut() throws SQLException {
		return getConstraint(Constants.Min, Constants.Humidity_Out, Constants.DHT22Table);
	}

	public double getMinimalHumidityIn() throws SQLException {
		return getConstraint(Constants.Min, Constants.Humidity_In, Constants.DHT22Table);
	}

	public double getMaximalPressure() throws SQLException {
		return getConstraint(Constants.Max, Constants.Pressure, Constants.BMP180Table);
	}

	public double getMinimalPressure() throws SQLException {
		return getConstraint(Constants.Min, Constants.Pressure, Constants.BMP180Table);
	}

	public DatabaseConnection getConnection() {
		return myConnection;
	}

	private void waitUntilDatabaseHasFinished() throws SQLException, InterruptedException {
		while (!getConnection().getConnection().isClosed())
			Thread.sleep(100L);
	}
}