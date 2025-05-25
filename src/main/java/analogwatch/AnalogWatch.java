package analogwatch;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPanel;

import digitalweatherstation.Time;

public class AnalogWatch extends JPanel {

	private int myOuterLengthOfSecondHand = 145;

	private static final long serialVersionUID = 1L;

	private String myTimeZone;

	private SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat("dd-MM");

	public AnalogWatch(String timeZone) {
		myTimeZone = timeZone;
	}

	public void paint(Graphics g) {
		paintComponents(g);

		Graphics2D graphics2d = (Graphics2D) g;

		Time currentTime = Utils.getCurrentTimeAt(this.myTimeZone);

		var xCenter = getWidth() / 2;
		var yCenter = getHeight() / 2;

		drawAnalogWatch(graphics2d, xCenter, yCenter, currentTime);

		drawHourHand(graphics2d, xCenter, yCenter, currentTime);
		drawMinuteHand(graphics2d, xCenter, yCenter, currentTime);
		drawSecondHand(graphics2d, xCenter, yCenter, currentTime.getSecond());

		graphics2d.setColor(Color.RED);
		graphics2d.fillOval(xCenter - 6, yCenter - 6, 12, 12);
		graphics2d.setColor(Color.WHITE);
		graphics2d.fillOval(xCenter - 4, yCenter - 4, 8, 8);
	}

	private void drawAnalogWatch(Graphics2D graphics2d, int xCenter, int yCenter, Time currentTime) {
		graphics2d.setColor(currentTime.isAfternoon().booleanValue() ? Color.BLACK : Color.decode("#FFFFFF"));
		graphics2d.setFont(new Font("TimesRoman", 1, 20));
		graphics2d.fillOval(xCenter - 150, yCenter - 150, 300, 300);
		graphics2d.setColor(currentTime.isAfternoon().booleanValue() ? Color.decode("#FFFFFF") : Color.BLACK);

		drawDate(graphics2d, xCenter, yCenter, currentTime.getCalendar().getTime());
		drawNameOfCity(graphics2d, xCenter, yCenter);
		drawTime(graphics2d, xCenter, yCenter, currentTime);

		ArrayList<SphericalCoordinate> sphericalCoordinatesForHours = Utils.getCoordinatesOfHours();
		ArrayList<SphericalCoordinate> sphericalCoordinatesForMinutes = Utils.getCoordinatesOfMinutes();

		drawNumbers(graphics2d, xCenter, yCenter, sphericalCoordinatesForHours, currentTime.isAfternoon());

		drawMinuteLines(graphics2d, xCenter, yCenter, sphericalCoordinatesForMinutes, currentTime.isAfternoon());
		drawHourLines(graphics2d, xCenter, yCenter, sphericalCoordinatesForHours, currentTime.isAfternoon());
	}

	private void drawNumbers(Graphics2D graphics2d, int xCenter, int yCenter,
			ArrayList<SphericalCoordinate> sphericalCoordinatesForHours, Boolean isAfternoon) {
		graphics2d.setColor(isAfternoon.booleanValue() ? Color.decode("#FFFFFF") : Color.BLACK);

		for (int hour = 0; hour < sphericalCoordinatesForHours.size(); hour++) {
			SphericalCoordinate sphericalCoordinatesForHour = sphericalCoordinatesForHours.get(hour);

			var charWidth = graphics2d.getFontMetrics().stringWidth(String.valueOf(hour));
			var charHeight = graphics2d.getFontMetrics().getHeight();

			var xEnd = (int) (sphericalCoordinatesForHour.getXEntry() * 120.0D + xCenter);
			var yEnd = (int) (sphericalCoordinatesForHour.getYEntry() * 120.0D + yCenter);

			if (hour == 6) {
				yEnd -= charHeight / 3;
			} else if (hour == 12) {
				yEnd += charHeight / 3;
			}

			graphics2d.translate(xEnd, yEnd);

			graphics2d.drawString(String.valueOf(hour), -charWidth / 2, charHeight / 3);

			graphics2d.translate(-xEnd, -yEnd);
		}
	}

	private void drawMinuteLines(Graphics2D graphics2d, int xCenter, int yCenter,
			ArrayList<SphericalCoordinate> sphericalCoordinatesForMinutes, Boolean isAfternoon) {
		graphics2d.setColor(isAfternoon.booleanValue() ? Color.LIGHT_GRAY : Color.DARK_GRAY);
		graphics2d.setStroke(new BasicStroke(1.0F));

		for (SphericalCoordinate sphericalCoordinate : sphericalCoordinatesForMinutes) {

			var xEntryStart = (int) (sphericalCoordinate.getXEntry() * 140.0D + xCenter);
			var yEntryStart = (int) (sphericalCoordinate.getYEntry() * 140.0D + yCenter);

			var xEntryEnd = (int) (sphericalCoordinate.getXEntry() * 145.0D + xCenter);
			var yEntryEnd = (int) (sphericalCoordinate.getYEntry() * 145.0D + yCenter);

			graphics2d.drawLine(xEntryStart, yEntryStart, xEntryEnd, yEntryEnd);
		}
	}

	private void drawHourLines(Graphics2D graphics2d, int xCenter, int yCenter,
			ArrayList<SphericalCoordinate> sphericalCoordinatesForHours, Boolean isAfternoon) {
		graphics2d.setColor(isAfternoon.booleanValue() ? Color.LIGHT_GRAY : Color.DARK_GRAY);
		graphics2d.setStroke(new BasicStroke(2.0F));

		for (int hour = 0; hour < sphericalCoordinatesForHours.size(); hour++) {

			var sphericalCoordinate = sphericalCoordinatesForHours.get(hour);

			var xEntryStart = 0;
			var yEntryStart = 0;

			if (hour % 3 == 0) {
				xEntryStart = (int) (sphericalCoordinate.getXEntry() * 130.0D + xCenter);
				yEntryStart = (int) (sphericalCoordinate.getYEntry() * 130.0D + yCenter);
			} else {
				xEntryStart = (int) (sphericalCoordinate.getXEntry() * 138.0D + xCenter);
				yEntryStart = (int) (sphericalCoordinate.getYEntry() * 138.0D + yCenter);
			}

			var xEntryEnd = (int) (sphericalCoordinate.getXEntry() * 145.0D + xCenter);
			var yEntryEnd = (int) (sphericalCoordinate.getYEntry() * 145.0D + yCenter);

			graphics2d.drawLine(xEntryStart, yEntryStart, xEntryEnd, yEntryEnd);
		}
	}

	private void drawSecondHand(Graphics2D graphics2d, int xCenter, int yCenter, int second) {
		SphericalCoordinate sphericalCoordinate = Utils.getCoordinateOfSecondOnClock(second);

		var xCoordinate = sphericalCoordinate.getXEntry();
		var yCoordinate = sphericalCoordinate.getYEntry();

		var xMinute = (int) (xCoordinate * this.myOuterLengthOfSecondHand + xCenter);
		var yMinute = (int) (yCoordinate * this.myOuterLengthOfSecondHand + yCenter);

		graphics2d.setColor(Color.decode("#F86A6A"));

		graphics2d.drawLine((int) (xCenter - xCoordinate * 0.0D), (int) (yCenter - yCoordinate * 0.0D), xMinute,
				yMinute);
	}

	private void drawMinuteHand(Graphics2D graphics2d, int xCenter, int yCenter, Time currentTime) {
		SphericalCoordinate sphericalCoordinate = Utils.getCoordinateOfMinuteOnClock(currentTime.getMinute(),
				currentTime.getSecond());

		var xMinute = (int) (sphericalCoordinate.getXEntry() * 125.0D + xCenter);
		var yMinute = (int) (sphericalCoordinate.getYEntry() * 125.0D + yCenter);

		graphics2d.setColor(currentTime.isAfternoon().booleanValue() ? Color.WHITE : Color.BLACK);
		graphics2d.drawLine(xCenter, yCenter, xMinute, yMinute);
	}

	private void drawHourHand(Graphics2D graphics2d, int xCenter, int yCenter, Time currentTime) {
		var hourCoordinate = Utils.getCoordinateOfHourOnClock(currentTime.getHour(), currentTime.getMinute());

		var xHour = (int) (hourCoordinate.getXEntry() * 105.0D + xCenter);
		var yHour = (int) (hourCoordinate.getYEntry() * 105.0D + yCenter);

		graphics2d.setColor(currentTime.isAfternoon().booleanValue() ? Color.WHITE : Color.BLACK);

		graphics2d.drawLine(xCenter, yCenter, xHour, yHour);
	}

	private void drawDate(Graphics2D graphics2d, int xCenter, int yCenter, Date currentDate) {
		graphics2d.setColor(new Color(79, 115, 142));

		graphics2d.drawString(this.mySimpleDateFormat.format(currentDate), xCenter + 30, yCenter - 20);
		graphics2d.drawString(getNameOfCityFromTimeZone(), xCenter - 20, yCenter + 30);
	}

	private void drawTime(Graphics2D graphics2d, int xCenter, int yCenter, Time currentTime) {
		graphics2d.setColor(currentTime.isAfternoon().booleanValue() ? Color.WHITE : Color.BLACK);

		graphics2d.drawString(currentTime.toString(), xCenter - 20, yCenter + 60);
	}

	private void drawNameOfCity(Graphics2D graphics2d, int xCenter, int yCenter) {
		graphics2d.setColor(new Color(79, 115, 142));

		graphics2d.drawString(getNameOfCityFromTimeZone(), xCenter - 20, yCenter + 30);
	}

	private String getNameOfCityFromTimeZone() {
		String[] xyz = this.myTimeZone.split("/");

		return xyz[xyz.length - 1];
	}
}
