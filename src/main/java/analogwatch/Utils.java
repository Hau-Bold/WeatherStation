package analogwatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import digitalweatherstation.Time;

public final class Utils {

	private static final double TWO_PI = 2 * Math.PI;
	private static final double HALF_PI = Math.PI / 2;

	private Utils() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Returns the current time (hour, minute, second) for a given time zone.
	 *
	 * @paramtimeZoneId the ID of the desired time zone (e.g., "Europe/Berlin",
	 *                  "UTC")
	 * @return a {@link Time} object containing the current time in the specified
	 *         time zone
	 * @throws IllegalArgumentException if the timeZoneId is null or empty
	 */
	public static Time getCurrentTimeAt(String timeZoneId) {
		if (timeZoneId == null || timeZoneId.isEmpty()) {
			throw new IllegalArgumentException("Time zone ID must not be null or empty");
		}

		TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
		Calendar calendar = Calendar.getInstance(timeZone);

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

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

	private static SphericalCoordinate transformToSphericalCoordinate(int entryWithSectorsToConsider, int countOfSectors,
			int entryWithoutSectorsToConsider) {

		double offsetOfEntryWithSectorsToConsider = (entryWithSectorsToConsider * TWO_PI) / countOfSectors;
		double offsetOfEntryWithoutSectorsToConsider = (entryWithoutSectorsToConsider * TWO_PI) / (60 * countOfSectors);

		double resultingOffset = offsetOfEntryWithSectorsToConsider + offsetOfEntryWithoutSectorsToConsider;

		double xEntry = Math.cos(resultingOffset - HALF_PI);
		double yEntry = Math.sin(resultingOffset - HALF_PI);

		return new SphericalCoordinate(xEntry, yEntry);
	}

	/**
	 * Converts a single clock unit (like second or minute) into a circular
	 * coordinate.
	 *
	 * @paramentry The unit to convert (e.g. second or minute in range [0, 59])
	 * @return SphericalCoordinate on the clock face
	 */
	private static SphericalCoordinate transformToSphericalCoordinate(int entry) {
		double angle = (entry / 60.0) * TWO_PI;
		double x = Math.cos(angle - HALF_PI);
		double y = Math.sin(angle - HALF_PI);

		return new SphericalCoordinate(x, y);
	}

	private static void AssertValidityOfSecondOrMinute(int value) {
		if (value < 0 || value >= 60) {
			throw new IllegalArgumentException("Value " + value + " is not a valid second or minute (expected 0–59).");
		}
	}

	private static void AssertValidityOfHour(int hour) {
		if (hour < 0 || hour > 24) {
			throw new IllegalArgumentException("Value " + hour + " is not a valid hour (expected 0–24).");
		}
	}
}