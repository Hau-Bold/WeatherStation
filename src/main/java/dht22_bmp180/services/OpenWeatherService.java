package dht22_bmp180.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import client.Constants;
import client.IOperatingSystemSettings;
import dht22_bmp180.database.DatabaseLogic;
import dht22_bmp180.weatherDataSets.OpenWeatherServiceObject;

public class OpenWeatherService implements Runnable {
	public static long LastCallTime;
	private static final String API_KEY = "cc4dad9971d3677c11f2fa5c455dbf7c";
	private String myCity;
	private String myCountryCode;
	private static JSONParser myJsonParser;
	private static final String OPEN_WEATHER_MAP_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s,%s&APPID=%s";
	private DatabaseLogic myDatabaseLogic;
	private int myCurrentDayOfMonth;
	private DHT22DataProvider myDHT22DataProvider;

	public OpenWeatherService(DatabaseLogic databaseLogic, String city, String countryCode,
			IOperatingSystemSettings operatingSystemSettings) throws SQLException {
		myCity = city;
		myCountryCode = countryCode;

		myJsonParser = new JSONParser();
		myDatabaseLogic = databaseLogic;

		myCurrentDayOfMonth = Calendar.getInstance(TimeZone.getDefault()).get(5);

		initTables();

		myDHT22DataProvider = new DHT22DataProvider(operatingSystemSettings);
	}

	private OpenWeatherServiceObject getResponse(String city, String abbCountry) throws ParseException, IOException {
		URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, new Object[] { city, abbCountry, API_KEY }));

		String queryResult = getRequest(url);

		JSONObject jsonObject = (JSONObject) myJsonParser.parse(queryResult);

		JSONObject jsonObjectWeather = (JSONObject) jsonObject.get("main");

		return getJSONWeatherObject(jsonObjectWeather);
	}

	private long updateOWMData()
			throws SQLException, InterruptedException, IOException, java.text.ParseException, TimeoutException {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		int currentDayOfMonth = calendar.get(5);

		long currentTimeInMillis = System.currentTimeMillis();

		OpenWeatherServiceObject weatherObject = null;

		try {
			weatherObject = getResponse(myCity, myCountryCode);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		waitUntilDatabaseHasFinished();

		var latestDHT22 = myDHT22DataProvider.getLatest(currentTimeInMillis);

		myDatabaseLogic.insertDHT22Data(currentTimeInMillis, weatherObject.getTemperature(),
				latestDHT22.getTemperature(), weatherObject.getHumidity(), latestDHT22.getHumidity());
		myDatabaseLogic.insertBMP180Data(currentTimeInMillis, weatherObject.getPressure());
		myDatabaseLogic.doHouseKeepingOfWeatherData(currentTimeInMillis);
		myDatabaseLogic.doHouseKeepingOfMaxMinData(currentTimeInMillis);

		if (currentDayOfMonth != this.myCurrentDayOfMonth) {
			updateMaxMinData(currentTimeInMillis);
			myCurrentDayOfMonth = currentDayOfMonth;
		}

		return currentTimeInMillis;
	}

	private String getRequest(URL urlObject) throws IOException {
		HttpURLConnection httpUrlConnection = (HttpURLConnection) urlObject.openConnection();

		httpUrlConnection.setRequestMethod("GET");
		httpUrlConnection.setRequestProperty("accept", "application/json");

		int responseCode = httpUrlConnection.getResponseCode();

		if (responseCode != 200) {
			return null;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));

		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}

	private OpenWeatherServiceObject getJSONWeatherObject(JSONObject jsonObject) {
		String temperature = jsonObject.get("temp").toString();
		String humidity = jsonObject.get("humidity").toString();
		String pressure = jsonObject.get("pressure").toString();

		return new OpenWeatherServiceObject(Double.valueOf(temperature), Double.valueOf(humidity),
				Double.valueOf(pressure));
	}

	private void initTables() throws SQLException {

//		myDatabaseLogic.dropBMP180Table();
//		myDatabaseLogic.dropDHT22Table();
//		myDatabaseLogic.dropMaxMinTable();

		myDatabaseLogic.createDHT22Table();
		myDatabaseLogic.createBMP180Table();

		myDatabaseLogic.createMinMaxTable();
	}

	private void updateMaxMinData(long currentTimeInMillis) throws SQLException {
		double maxTemperatureOut = myDatabaseLogic.getMaximalTemperatureOut();
		double maxTemperatureIn = myDatabaseLogic.getMaximalTemperatureIn();
		double minTemperatureOut = myDatabaseLogic.getMinimalTemperatureOut();
		double minTemperatureIn = myDatabaseLogic.getMinimalTemperatureIn();

		double maxHumidityOut = myDatabaseLogic.getMaximalHumidityOut();
		double maxHumidityIn = myDatabaseLogic.getMaximalHumidityIn();
		double minHumidityOut = myDatabaseLogic.getMinimalHumidityOut();
		double minHumidityIn = myDatabaseLogic.getMinimalHumidityIn();

		double maxPressureOut = myDatabaseLogic.getMaximalPressure();
		double minPressureOut = myDatabaseLogic.getMinimalPressure();

		myDatabaseLogic.insertMaxMinData(currentTimeInMillis, maxTemperatureOut, maxTemperatureIn, minTemperatureOut,
				minTemperatureIn, maxHumidityOut, maxHumidityIn, minHumidityOut, minHumidityIn, maxPressureOut,
				minPressureOut);

		myDatabaseLogic.doHouseKeepingOfMaxMinData(currentTimeInMillis);
	}

	public void run() {
		try {
			LastCallTime = updateOWMData();
			Thread.sleep(Constants.SLEEPING_TIME);
			run();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	private void waitUntilDatabaseHasFinished() throws SQLException, InterruptedException {
		while (!this.myDatabaseLogic.getConnection().getConnection().isClosed()) {
			Thread.sleep(100);
		}
	}
}