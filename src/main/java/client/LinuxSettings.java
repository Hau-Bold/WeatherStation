package client;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.apache.commons.exec.util.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class LinuxSettings implements IOperatingSystemSettings {

	private Path myDirectory;
	private List<File> myBackgroundImages;
	private Random mySeed;

	public LinuxSettings(Path directory) {
		myDirectory = directory;

		myBackgroundImages = getBackGroundImages();
		mySeed = new Random(myBackgroundImages.size());
	}

	private String getPathToExecutableDriver() {
		return "chromedriver_linux64/chromedriver";
	}

	private String getDriver() {
		return "webdriver.chrome.driver";
	}

	public WebDriver getWebDriver() {

		String path = myDirectory + File.separator + Constants.BIN + File.separator + getPathToExecutableDriver();

		System.setProperty(getDriver(), path);

		ChromeOptions chromeOptions = new ChromeOptions();

		chromeOptions.addArguments(new String[] { "--disable-application-cache" });

		chromeOptions.addArguments(new String[] { "--disable-infobars" });
		chromeOptions.addArguments(new String[] { "--kiosk" });

		return (WebDriver) new ChromeDriver(chromeOptions);
	}

	public Path getDatabase() {
		// TODO Auto-generated method stub
		return null;
	}

	private List<File> getBackGroundImages() {
		// TODO Auto-generated method stub
		return null;
	}

	public Path getPathToHomePage() {
		return myDirectory.resolve(Constants.NAME_OF_HOME_PAGE);
	}

	public String getPathToDHT22Data() {
		return myDirectory + File.separator + Constants.SensorData + File.separator + Constants.DHT22_DATA;
	}

	public String getPathToBMP180Data() {
		return myDirectory + File.separator + Constants.SensorData + File.separator + Constants.BMP180_DATA;
	}

	public String getNextBackgroundImage() {

		int counter = mySeed.nextInt(myBackgroundImages.size());

		String absolutePath = ((File) myBackgroundImages.get(counter)).getPath();

		String[] absolutePathSplitted = StringUtils.split(absolutePath, File.separator);

		return "BackgroundImages\\" + absolutePathSplitted[absolutePathSplitted.length - 1];
	}

	public String getPathToImageForBackground() {
		// TODO Auto-generated method stub
		return null;
	}
}
