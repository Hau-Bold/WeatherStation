package digitalweatherstation;

import java.util.Calendar;

public class Time {
	private int myHour;
	private int myMinute;
	private int mySecond;
	private Calendar myCalendar;
	private Boolean myIsAfternoon;

	public Time(int hour, int minute) {
		this(hour, minute, 0);
	}

	public Time(int hour, int minute, int second) {
		myIsAfternoon = (hour > 12) ? Boolean.TRUE : Boolean.FALSE;

		myHour = myIsAfternoon.booleanValue() ? (hour - 12) : hour;

		myMinute = minute;
		mySecond = second;
	}

	public Time(int hour, int minute, int second, Calendar calendar) {
		this(hour, minute, second);

		myCalendar = calendar;
	}

	public int getHour() {
		return myHour;
	}

	public void setHour(int hour) {
		myHour = hour;
	}

	public int getMinute() {
		return myMinute;
	}

	public void setMinute(int minute) {
		myMinute = minute;
	}

	public int getSecond() {
		return mySecond;
	}

	public void setSecond(int second) {
		mySecond = second;
	}

	public Calendar getCalendar() {
		return myCalendar;
	}

	public Boolean isAfternoon() {
		return myIsAfternoon;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		if (myHour < 10) {
			stringBuilder.append("0");
		}

		stringBuilder.append(myHour);

		stringBuilder.append(":");

		if (myMinute < 10) {
			stringBuilder.append("0");
		}

		stringBuilder.append(myMinute);

		stringBuilder.append(":");

		if (mySecond < 10) {
			stringBuilder.append("0");
		}

		stringBuilder.append(mySecond);

		return stringBuilder.toString();
	}
}