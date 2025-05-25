package display.Streaming;

import java.util.ArrayList;

import org.openqa.selenium.WebDriver;

import analogwatch.Utils;
import digitalweatherstation.Time;

public class Streaming {
	private ArrayList<IStreamingObject> myStreamingObjects = new ArrayList<IStreamingObject>();

	public Streaming() {
		IStreamingObject ARDSMDM = new StreamingARD(new StreamingTime(12, 0, 60));
		IStreamingObject ZDFNews = new StreamingZDF(new StreamingTime(23, 30, 30));
		IStreamingObject KIKASMDM = new StreamingKIKA(new StreamingTime(9, 30, 60));

		this.myStreamingObjects.add(ARDSMDM);
		this.myStreamingObjects.add(ZDFNews);
		this.myStreamingObjects.add(KIKASMDM);
	}

	public IStreamingObject getCurrent() {
		for (IStreamingObject streamingObject : this.myStreamingObjects) {
			if (isStreamingTime(streamingObject).booleanValue()) {
				return streamingObject;
			}
		}

		return null;
	}

	public static void stream(WebDriver webDriver, IStreamingObject streamingObject) {
		streamingObject.stream(webDriver);
	}

	private static Boolean isStreamingTime(IStreamingObject streamingObject) {
		Time time = Utils.getCurrentTimeAt("Europe/Berlin");

		int hour = time.getHour();
		int minute = time.getMinute();
		Boolean isAfternoon = time.isAfternoon();
		int dayOfWeek = time.getCalendar().get(7);

		StreamingTime streamingTime = streamingObject.GetStreamingTime();

		if (streamingTime.getDayOfWeek() != 0) {

			if (dayOfWeek == streamingTime.getDayOfWeek() && streamingTime.getMinute() == 0) {
				return (((hour == streamingTime.getHour() - 1 && minute >= 55)
						|| (hour == streamingTime.getHour() && minute <= 5))
						&& isAfternoon == streamingTime.isAfternoon()) ? Boolean.valueOf(true) : Boolean.valueOf(false);

			}

		} else {

			if (streamingTime.getMinute() == 0) {
				return (((hour == streamingTime.getHour() - 1 && minute >= 55)
						|| (hour == streamingTime.getHour() && minute <= 5))
						&& isAfternoon == streamingTime.isAfternoon()) ? Boolean.valueOf(true) : Boolean.valueOf(false);
			}

			if (streamingTime.getMinute() == 30) {
				return (hour == streamingTime.getHour() && minute >= 25 && minute <= 35
						&& isAfternoon == streamingTime.isAfternoon()) ? Boolean.valueOf(true) : Boolean.valueOf(false);
			}
		}

		return Boolean.FALSE;
	}
}