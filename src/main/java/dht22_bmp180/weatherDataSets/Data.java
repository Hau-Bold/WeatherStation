package dht22_bmp180.weatherDataSets;

public class Data {
	private long myTime;
	private double myValue;

	public Data(long time, double value) {
		myTime = time;
		myValue = value;
	}

	public long getTime() {
		return myTime;
	}

	public double getValue() {
		return myValue;
	}
}