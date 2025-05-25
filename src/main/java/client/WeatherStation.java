package client;

import java.sql.SQLException;

import javax.swing.JFrame;

import analogwatch.AnalogWatchFrame;
import dht22_bmp180.database.DatabaseLogic;
import dht22_bmp180.services.OpenWeatherService;
import display.controller.DisplayController;

public class WeatherStation extends JFrame {
	private static final long serialVersionUID = 5654622750184410354L;
	private DatabaseLogic myOpenWeatherMapDatabaseLogic;
	private String myCity;
	private String myCountryCode;
	private IOperatingSystemSettings myOperatingSystemSettings;

	public WeatherStation(IOperatingSystemSettings operatingSystemSettings, String city, String countryCode) {
		myCity = city;
		myCountryCode = countryCode;

		myOpenWeatherMapDatabaseLogic = new DatabaseLogic(operatingSystemSettings);
		myOperatingSystemSettings = operatingSystemSettings;
	}

	public void runApplication() throws SQLException, InterruptedException {
		Thread analogWatchThread = new Thread((Runnable) new AnalogWatchFrame(myOperatingSystemSettings));
		analogWatchThread.start();

		Thread openWeatherServiceThread = new Thread((Runnable) new OpenWeatherService(myOpenWeatherMapDatabaseLogic,
				myCity, myCountryCode, myOperatingSystemSettings));
		openWeatherServiceThread.start();

		Thread displayControllerThread = new Thread(
				(Runnable) new DisplayController(myOperatingSystemSettings, myOpenWeatherMapDatabaseLogic));
		displayControllerThread.start();
	}
}
