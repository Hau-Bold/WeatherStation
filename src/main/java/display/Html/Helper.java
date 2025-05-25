package display.Html;

import java.util.Calendar;
import java.util.TimeZone;

import client.Constants;
import digitalweatherstation.Time;

class Helper {
	static String getCurrentDataToDisplayString(double valueOut, double valueIn, String mode) {

		switch (mode) {
		case Constants.Pressure:
			return String.format(Constants.CURRENT_PRESSURE, valueOut);

		case Constants.PressureMax:
			return String.format(Constants.CURRENT_PRESSURE, valueOut);

		case Constants.PressureMin:
			return String.format(Constants.CURRENT_PRESSURE, valueOut);

		case Constants.Temperature:
			return String.format(Constants.CURRENT_TEMPERATURE, valueOut, valueIn);

		case Constants.TemperatureMax:
			return String.format(Constants.CURRENT_TEMPERATURE, valueOut, valueIn);

		case Constants.TemperatureMin:
			return String.format(Constants.CURRENT_TEMPERATURE, valueOut, valueIn);

		case Constants.Humidity:
			return String.format(Constants.CURRENT_HUMIDITY, valueOut, valueIn);

		case Constants.HumidityMax:
			return String.format(Constants.CURRENT_HUMIDITY, valueOut, valueIn);

		case Constants.HumidityMin:
			return String.format(Constants.CURRENT_HUMIDITY, valueOut, valueIn);

		default:
			return mode;
		}
	}

	static Time transformTimeInMilliesToTime(long currentTimeInMillies) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
		calendar.setTimeInMillis(currentTimeInMillies);

		int hour = calendar.get(11);
		int minute = calendar.get(12);
		int second = calendar.get(13);

		return new Time(hour, minute, second);
	}
}