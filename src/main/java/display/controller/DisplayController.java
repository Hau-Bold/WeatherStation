package display.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.WebDriver;

import analogwatch.AnalogWatchFrame;
import client.Constants;
import client.IOperatingSystemSettings;
import dht22_bmp180.database.DatabaseLogic;
import dht22_bmp180.weatherDataSets.Data;
import display.Html.HtmlExecutorOverviewImpl;
import display.Html.JSData;
import display.Streaming.IStreamingObject;
import display.Streaming.Streaming;
import display.WebDriver.WebDriverHelper;

public class DisplayController implements Runnable {
	private long mySleepingTime = 10000L;

	private static final int myCountOfModes = 8;
	private WebDriver myWebDriver;

	private static Path myPathToHtmlPage;
	private DatabaseLogic myOpenWeatherMapDatabaseLogic;
	private static int myMode = 0;

	private Streaming myStreaming;

	private IOperatingSystemSettings myOperatingSystemSettings;

	public DisplayController(IOperatingSystemSettings operatingSystemSettings,
			DatabaseLogic openWeatherMapDatabaseLogic) throws SQLException {
		myOperatingSystemSettings = operatingSystemSettings;

		myOpenWeatherMapDatabaseLogic = openWeatherMapDatabaseLogic;
		myPathToHtmlPage = operatingSystemSettings.getPathToHomePage();
		myWebDriver = WebDriverHelper.initDriver(operatingSystemSettings);
		myStreaming = new Streaming();
	}

	private void display(IStreamingObject streamingObject) throws IOException, SQLException, InterruptedException {
		if (streamingObject != null) {
			streamingObject.stream(myWebDriver);
		} else {
			displayWeatherData();
		}
	}

	private void displayWeatherData() throws IOException, SQLException, InterruptedException {
		getNextMode();
		var pathToBackgroundImage = myOperatingSystemSettings.getNextBackgroundImage();

		myWebDriver.get("file:///" + myPathToHtmlPage);

		List<Data> dataOut = null;
		List<Data> dataIn = null;
		String colorOut = null;
		String colorIn = null;
		String mode = null;

		switch (myMode) {

		case 0:
			dataOut = myOpenWeatherMapDatabaseLogic.getDHT22Data(Constants.Temperature_Out);
			dataIn = myOpenWeatherMapDatabaseLogic.getDHT22Data(Constants.Temperature_In);
			mode = Constants.Temperature;

			colorOut = Constants.RED;
			colorIn = Constants.VERY_LIGHT_BLUE;
			break;

		case 1:
			dataOut = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MaxTemperature_Out);
			dataIn = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MaxTemperature_In);
			mode = Constants.TemperatureMax;
			colorOut = Constants.RED;
			colorIn = Constants.VERY_LIGHT_BLUE;
			break;

		case 2:
			dataOut = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MinTemperature_Out);
			dataIn = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MinTemperature_In);
			mode = Constants.TemperatureMin;
			colorOut = Constants.RED;
			colorIn = Constants.VERY_LIGHT_BLUE;
			break;

		case 3:
			dataOut = myOpenWeatherMapDatabaseLogic.getDHT22Data(Constants.Humidity_Out);
			dataIn = myOpenWeatherMapDatabaseLogic.getDHT22Data(Constants.Humidity_In);
			mode = Constants.Humidity;
			colorOut = Constants.BLUE;
			colorIn = Constants.VERY_LIGHT_GREEN;
			break;

		case 4:
			dataOut = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MaxHumidity_Out);
			dataIn = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MaxHumidity_In);
			mode = Constants.HumidityMax;
			colorOut = Constants.BLUE;
			colorIn = Constants.VERY_LIGHT_GREEN;
			break;

		case 5:
			dataOut = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MinHumidity_Out);
			dataIn = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MinHumidity_In);
			mode = Constants.HumidityMin;
			colorOut = Constants.BLUE;
			colorIn = Constants.VERY_LIGHT_GREEN;
			break;

		case 6:
			dataOut = myOpenWeatherMapDatabaseLogic.getBMP180Data();
			mode = Constants.Pressure;
			colorOut = Constants.MEDIUM_SPRING_GREEN;
			break;

		case 7:
			dataOut = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MaxPressure);
			mode = Constants.PressureMax;
			colorOut = Constants.MEDIUM_SPRING_GREEN;
			break;

		case 8:
			dataOut = myOpenWeatherMapDatabaseLogic.getData_MaxMin(Constants.MinPressure);
			mode = Constants.PressureMin;
			colorOut = Constants.MEDIUM_SPRING_GREEN;
			break;

		default:
			throw new NotImplementedException("mode not implemented yet");
		}

		generateOverview(pathToBackgroundImage, dataOut, dataIn, colorOut, colorIn, mode);

		myWebDriver.navigate().refresh();

		WebDriverHelper.waitForPageLoad(myWebDriver);
	}

	private void generateOverview(String pathToBackgroundImage, List<Data> dataOut, List<Data> dataIn, String colorOut,
			String colorIn, String mode) throws IOException {
		List<JSData> jsDatasOut = Convert(dataOut);
		List<JSData> jsDatasIn = Convert(dataIn);

		HtmlExecutorOverviewImpl executor = new HtmlExecutorOverviewImpl(myPathToHtmlPage, pathToBackgroundImage,
				jsDatasOut, jsDatasIn, Calendar.getInstance(TimeZone.getDefault()).getTime(), mode, colorOut, colorIn);

		executor.write();
	}

	private List<JSData> Convert(List<Data> datas) {
		List<JSData> jsDatas = new ArrayList<JSData>();

		if (datas != null) {
			for (var data : datas) {
				jsDatas.add(new JSData(data));
			}
		}

		return jsDatas;
	}

	public void run() {
		IStreamingObject current = myStreaming.getCurrent();

		try {
			display(current);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		while (!AnalogWatchFrame.hasEnded.booleanValue()) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		waitAsLongAsDataShouldBeDisplayed(current);

		AnalogWatchFrame.hasEnded = Boolean.FALSE;

		reInitWebDriverAtEndOfStreamingTime(myWebDriver, current);

		run();
	}

	private void reInitWebDriverAtEndOfStreamingTime(WebDriver webDriver, IStreamingObject streamingObject) {
		if (streamingObject != null) {
			webDriver.close();
			myWebDriver = WebDriverHelper.initDriver(myOperatingSystemSettings);
		}
	}

	private void waitAsLongAsDataShouldBeDisplayed(IStreamingObject streamingObject) {
		try {
			Thread.sleep((streamingObject != null) ? streamingObject.GetStreamingTime().getStreamingDurationInMinutes()
					: mySleepingTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void getNextMode() {
		myMode++;
		myMode %= myCountOfModes;
	}
}