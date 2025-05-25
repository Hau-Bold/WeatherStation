package client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.SystemUtils;

public class App {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				Path directory = Paths.get(args[0], Constants.WeatherStation);

				String city = args[1];
				String countryCode = args[2];

				IOperatingSystemSettings setting = SystemUtils.IS_OS_WINDOWS ? new WindowsSettings(directory)
						: new LinuxSettings(directory);

				WeatherStation weatherStation = new WeatherStation(setting, city, countryCode);
				try {
					weatherStation.runApplication();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
}