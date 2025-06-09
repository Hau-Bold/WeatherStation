package analogwatch;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import digitalweatherstation.Time;

public class AnalogWatch extends JPanel {

	private static final int NUMBER_RADIUS = 120;
	private static final int OUTER_RADIUS = 145;
	private static final int MINUTE_MARK_START_RADIUS = 140;
	private static final int SHORT_HOUR_MARK_LENGTH = 138;
	private static final int LONG_HOUR_MARK_LENGTH = 130;
	private static final Color HOUR_LINE_DAY_COLOR = Color.DARK_GRAY;
	private static final Color HOUR_LINE_NIGHT_COLOR = Color.LIGHT_GRAY;
	private static final Color INFO_TEXT_COLOR = new Color(79, 115, 142);
	private static final int HOUR_HAND_LENGTH = 105;
	private static final int MINUTE_HAND_LENGTH = 125;

	private static final int SECOND_HAND_LENGTH = 145;
	private static final Color SECOND_HAND_COLOR = new Color(0xF86A6A);

	private static final long serialVersionUID = 1L;

	private String timeZone;

	private SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat("dd-MM");

	public AnalogWatch(String timeZone) {
		this.timeZone = timeZone;
	}

	public void paint(Graphics g) {
		paintComponents(g);

		Graphics2D graphics2d = (Graphics2D) g;

		Time currentTime = Utils.getCurrentTimeAt(timeZone);

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
		graphics2d.setColor(currentTime.isAfternoon() ? Color.BLACK : Color.WHITE);
		graphics2d.setFont(new Font("TimesRoman", 1, 20));
		graphics2d.fillOval(xCenter - 150, yCenter - 150, 300, 300);
		graphics2d.setColor(currentTime.isAfternoon() ? Color.WHITE : Color.BLACK);

		drawDate(graphics2d, xCenter, yCenter, currentTime.getCalendar().getTime());
		drawCityName(graphics2d, xCenter, yCenter);
		drawTime(graphics2d, xCenter, yCenter, currentTime);

		List<SphericalCoordinate> sphericalCoordinatesForHours = Utils.getCoordinatesOfHours();
		List<SphericalCoordinate> sphericalCoordinatesForMinutes = Utils.getCoordinatesOfMinutes();

		drawNumbers(graphics2d, xCenter, yCenter, sphericalCoordinatesForHours, currentTime.isAfternoon());

		drawMinuteLines(graphics2d, xCenter, yCenter, sphericalCoordinatesForMinutes, currentTime.isAfternoon());
		drawHourLines(graphics2d, xCenter, yCenter, sphericalCoordinatesForHours, currentTime.isAfternoon());
	}

	private void drawNumbers(Graphics2D graphics2d, int xCenter, int yCenter,
			List<SphericalCoordinate> sphericalCoordinatesForHours, Boolean isAfternoon) {
		graphics2d.setColor(isAfternoon ? Color.WHITE : Color.BLACK);

		for (int hour = 0; hour < sphericalCoordinatesForHours.size(); hour++) {
			SphericalCoordinate sphericalCoordinatesForHour = sphericalCoordinatesForHours.get(hour);

			var charWidth = graphics2d.getFontMetrics().stringWidth(String.valueOf(hour));
			var charHeight = graphics2d.getFontMetrics().getHeight();

			var xEnd = (int) (sphericalCoordinatesForHour.getX() * NUMBER_RADIUS + xCenter);
			var yEnd = (int) (sphericalCoordinatesForHour.getY() * NUMBER_RADIUS + yCenter);

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

	private void drawMinuteLines(Graphics2D g2d, int xCenter, int yCenter, List<SphericalCoordinate> minuteCoordinates,
			Boolean isAfternoon) {

		g2d.setColor(isAfternoon ? Color.LIGHT_GRAY : Color.DARK_GRAY);
		g2d.setStroke(new BasicStroke(1.0f));

		for (SphericalCoordinate coord : minuteCoordinates) {
			int xStart = (int) (coord.getX() * MINUTE_MARK_START_RADIUS + xCenter);
			int yStart = (int) (coord.getY() * MINUTE_MARK_START_RADIUS + yCenter);

			int xEnd = (int) (coord.getX() * SECOND_HAND_LENGTH + xCenter);
			int yEnd = (int) (coord.getY() * SECOND_HAND_LENGTH + yCenter);

			g2d.drawLine(xStart, yStart, xEnd, yEnd);
		}
	}

	private void drawHourLines(Graphics2D g2d, int xCenter, int yCenter, List<SphericalCoordinate> hourCoordinates,
			Boolean isAfternoon) {

		g2d.setColor(isAfternoon ? HOUR_LINE_NIGHT_COLOR : HOUR_LINE_DAY_COLOR);
		g2d.setStroke(new BasicStroke(2.0f));

		for (int i = 0; i < hourCoordinates.size(); i++) {
			SphericalCoordinate coord = hourCoordinates.get(i);

			int innerLength = (i % 3 == 0) ? LONG_HOUR_MARK_LENGTH : SHORT_HOUR_MARK_LENGTH;

			int xStart = (int) (coord.getX() * innerLength + xCenter);
			int yStart = (int) (coord.getY() * innerLength + yCenter);

			int xEnd = (int) (coord.getX() * OUTER_RADIUS + xCenter);
			int yEnd = (int) (coord.getY() * OUTER_RADIUS + yCenter);

			g2d.drawLine(xStart, yStart, xEnd, yEnd);
		}
	}

	private void drawSecondHand(Graphics2D g2d, int xCenter, int yCenter, int second) {
		SphericalCoordinate coordinate = Utils.getCoordinateOfSecondOnClock(second);

		int xEnd = (int) (coordinate.getX() * SECOND_HAND_LENGTH + xCenter);
		int yEnd = (int) (coordinate.getY() * SECOND_HAND_LENGTH + yCenter);

		g2d.setColor(SECOND_HAND_COLOR);
		g2d.drawLine(xCenter, yCenter, xEnd, yEnd);
	}

	private void drawMinuteHand(Graphics2D g2d, int xCenter, int yCenter, Time time) {
		SphericalCoordinate coordinate = Utils.getCoordinateOfMinuteOnClock(time.getMinute(), time.getSecond());

		int xEnd = (int) (coordinate.getX() * MINUTE_HAND_LENGTH + xCenter);
		int yEnd = (int) (coordinate.getY() * MINUTE_HAND_LENGTH + yCenter);

		g2d.setColor(time.isAfternoon() ? Color.WHITE : Color.BLACK);
		g2d.drawLine(xCenter, yCenter, xEnd, yEnd);
	}

	private void drawHourHand(Graphics2D g2d, int xCenter, int yCenter, Time time) {
		SphericalCoordinate coordinate = Utils.getCoordinateOfHourOnClock(time.getHour(), time.getMinute());

		int xEnd = (int) (coordinate.getX() * HOUR_HAND_LENGTH + xCenter);
		int yEnd = (int) (coordinate.getY() * HOUR_HAND_LENGTH + yCenter);

		g2d.setColor(time.isAfternoon() ? Color.WHITE : Color.BLACK);
		g2d.drawLine(xCenter, yCenter, xEnd, yEnd);
	}

	private void drawDate(Graphics2D g2d, int xCenter, int yCenter, Date date) {
		g2d.setColor(INFO_TEXT_COLOR);
		g2d.drawString(mySimpleDateFormat.format(date), xCenter + 30, yCenter - 20);
		drawCityName(g2d, xCenter, yCenter);
	}

	private void drawTime(Graphics2D g2d, int xCenter, int yCenter, Time currentTime) {
		g2d.setColor(currentTime.isAfternoon() ? Color.WHITE : Color.BLACK);
		g2d.drawString(currentTime.toString(), xCenter - 20, yCenter + 60);
	}

	private void drawCityName(Graphics2D g2d, int xCenter, int yCenter) {
		g2d.setColor(INFO_TEXT_COLOR);
		g2d.drawString(getCityName(), xCenter - 20, yCenter + 30);
	}

	private String getCityName() {
		String[] parts = timeZone.split("/");
		return parts.length > 0 ? parts[parts.length - 1] : timeZone;
	}
}
