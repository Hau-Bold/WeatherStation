package dht22_bmp180.weatherDataSets;

public class OpenWeatherServiceObject {

	private Double myTemperature;
	private Double myHumidity;
	private Double myPressure;

	public OpenWeatherServiceObject(Double temperature, Double humidity, Double pressure) {
		myTemperature = Double.valueOf(temperature.doubleValue());
		myHumidity = humidity;
		myPressure = pressure;
	}

	public double getTemperature() {
		return myTemperature.doubleValue();
	}

	public double getHumidity() {
		return myHumidity.doubleValue();
	}

	public double getPressure() {
		return myPressure.doubleValue();
	}
}
