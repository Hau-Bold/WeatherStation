package analogwatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import digitalweatherstation.Time;

public class Utils {

	public static Time getCurrentTimeAt(String timeZone) {
		var calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));

		var hour = calendar.get(11);
		var minute = calendar.get(12);
		var second = calendar.get(13);

		return new Time(hour, minute, second, calendar);
	}

	static SphericalCoordinate getCoordinateOfHourOnClock(int hour, int minute) {
		AssertValidityOfHour(hour);
		AssertValidityOfSecondOrMinute(minute);

		return transformToSphericalCoordinate(hour, 12, minute);
	}

	static SphericalCoordinate getCoordinateOfMinuteOnClock(int minute, int second) {
		AssertValidityOfSecondOrMinute(minute);
		AssertValidityOfSecondOrMinute(second);

		return transformToSphericalCoordinate(minute, 60, second);
	}

	static SphericalCoordinate getCoordinateOfSecondOnClock(int second) {
		AssertValidityOfSecondOrMinute(second);

		return transformToSphericalCoordinate(second);
	}

	static ArrayList<SphericalCoordinate> getCoordinatesOfHours() {
		ArrayList<SphericalCoordinate> sphericalCoordinatesForHours = new ArrayList<SphericalCoordinate>();

		for (int hour = 0; hour < 12; hour++) {
			sphericalCoordinatesForHours.add(getCoordinateOfHourOnClock(hour, 0));
		}

		return sphericalCoordinatesForHours;
	}

	static ArrayList<SphericalCoordinate> getCoordinatesOfMinutes() {
		ArrayList<SphericalCoordinate> sphericalCoordinatesForHours = new ArrayList<SphericalCoordinate>();

		for (int minute = 0; minute < 60; minute++) {
			sphericalCoordinatesForHours.add(getCoordinateOfMinuteOnClock(minute, 0));
		}

		return sphericalCoordinatesForHours;
	}

	private static SphericalCoordinate transformToSphericalCoordinate(int entryWithSectorsToConsider, int coutOfSectors,
			int entryWithoutSectorsToConsider) {
		double offsetOfEntryWithSectorsToConsider = (entryWithSectorsToConsider * 2) * Math.PI / coutOfSectors;

		double offsetOfEntryWithoutSectorsToConsider = entryWithoutSectorsToConsider / 60.0D * 6.283185307179586D
				/ coutOfSectors;

		double resultingOffset = offsetOfEntryWithSectorsToConsider + offsetOfEntryWithoutSectorsToConsider;

		double xEntry = Math.cos(resultingOffset - 1.5707963267948966D);
		double yEntry = Math.sin(resultingOffset - 1.5707963267948966D);

		return new SphericalCoordinate(xEntry, yEntry);
	}

	private static SphericalCoordinate transformToSphericalCoordinate(int entry) {
		double xEntry = Math.cos(entry * Math.PI / 30.0D - 1.5707963267948966D);
		double yEntry = Math.sin(entry * Math.PI / 30.0D - 1.5707963267948966D);

		return new SphericalCoordinate(xEntry, yEntry);
	}

	private static void AssertValidityOfSecondOrMinute(int entry) {
		if (entry < 0 || entry >= 60) {
			throw new IllegalArgumentException(String.format("argument %s is not allowed as second or minute",
					new Object[] { Integer.valueOf(entry) }));
		}
	}

	private static void AssertValidityOfHour(int hour) {
		if (hour < 0 || hour > 24)
			throw new IllegalArgumentException(
					String.format("argument %s is not allowed as hour", new Object[] { Integer.valueOf(hour) }));
	}
}