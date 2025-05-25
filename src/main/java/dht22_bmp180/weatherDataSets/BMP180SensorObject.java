package dht22_bmp180.weatherDataSets;

public class BMP180SensorObject {
	private long myTime;
	private double myPressure;

	public BMP180SensorObject(long time, double pressure) {
		myTime = time;
		myPressure = pressure;
	}

	public double getPressure() {
		return myPressure;
	}

	public long getTime() {
		return myTime;
	}
}
