package display.controller;

import client.Constants;

public enum DisplayMode {

	TEMPERATURE(Constants.Temperature, Constants.RED, Constants.VERY_LIGHT_BLUE),
	TEMPERATURE_MAX(Constants.TemperatureMax, Constants.RED, Constants.VERY_LIGHT_BLUE),
	Temperature_Min(Constants.TemperatureMin, Constants.RED, Constants.VERY_LIGHT_BLUE),

	HUMIDITY(Constants.Humidity, Constants.BLUE, Constants.VERY_LIGHT_GREEN),
	HUMIDITY_MAX(Constants.HumidityMax, Constants.BLUE, Constants.VERY_LIGHT_GREEN),
	HUMIDITY_MIN(Constants.HumidityMin, Constants.BLUE, Constants.VERY_LIGHT_GREEN),

	PRESSURE(Constants.Pressure, Constants.MEDIUM_SPRING_GREEN),
	PRESSURE_MAX(Constants.PressureMax, Constants.MEDIUM_SPRING_GREEN),
	PRESSURE_MIN(Constants.PressureMin, Constants.MEDIUM_SPRING_GREEN);

	private final String modeName;
	private final String colorOut;
	private final String colorIn;

	DisplayMode(String modeName, String colorOut, String colorIn) {
		this.modeName = modeName;
		this.colorOut = colorOut;
		this.colorIn = colorIn;
	}

	DisplayMode(String modeName, String colorOut) {
		this(modeName, colorOut, null);
	}

	public String getModeName() {
		return modeName;
	}

	public String getColorOut() {
		return colorOut;
	}

	public String getColorIn() {
		return colorIn;
	}
}
