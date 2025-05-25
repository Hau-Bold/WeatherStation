package display.Html;

import dht22_bmp180.weatherDataSets.Data;
import digitalweatherstation.Time;

public class JSData {
	private Time myTime;
	private String myValue;

	public JSData(Data data) {
		myTime = Helper.transformTimeInMilliesToTime(data.getTime());
		myValue = String.valueOf(data.getValue());
	}

	public Time getTime() {
		return myTime;
	}

	public String getValue() {
		return myValue;
	}
}