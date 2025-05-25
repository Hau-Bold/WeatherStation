package dht22_bmp180.weatherDataSets;

public class DHT22SensorObject {
	private long myTime;
	private double myTemperature;
	private double myHumidity;

	public DHT22SensorObject(double temperature, double humidity, long time) {
		myTime = time;
		myTemperature = temperature;
		myHumidity = humidity;
	}

	public double getTemperature() {
		return myTemperature;
	}

	public double getHumidity() {
		return myHumidity;
	}

	public long getTime() {
		return myTime;
	}
}
