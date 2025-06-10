package client;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.exec.util.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class WindowsSettings implements IOperatingSystemSettings {

	private Path myDirectory;
	private Random mySeed;
	private List<File> myBackgroundImages;

	public WindowsSettings(Path directory) {

		myDirectory = directory;

		myBackgroundImages = getBackGroundImages();
		mySeed = new Random(myBackgroundImages.size());
	}

	private String getPathToExecutableDriver() {

		return "geckodriver-v0.33.0-win-aarch64/geckodriver.exe";
	}

	private String getDriver() {
		return "webdriver.gecko.driver";
	}

	public WebDriver getWebDriver() {

		String path = myDirectory + File.separator + Constants.BIN + File.separator + getPathToExecutableDriver();

		// System.setProperty(getDriver(), path);

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, Boolean.TRUE);
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.merge((Capabilities) capabilities);

		return (WebDriver) new FirefoxDriver(firefoxOptions);
	}

	public Path getDatabase() {
		// return "E:\\OWM" + Constants.NameOfDatabase;
		return myDirectory.resolve(Paths.get(Constants.BIN, Constants.NameOfDatabase));
	}

	private List<File> getBackGroundImages() {
		Path pathToBackgroundImageRepository = myDirectory.resolve(Constants.BACKGROUND_IMAGES);

		File backGroundImageRepository = pathToBackgroundImageRepository.toFile();
		return Arrays.asList(backGroundImageRepository.listFiles());
	}

	public Path getPathToHomePage() {
		return myDirectory.resolve(Constants.NAME_OF_HOME_PAGE);
	}

	public String getPathToDHT22Data() {
		return myDirectory + File.separator + Constants.SensorData + File.separator + Constants.DHT22_DATA;
	}

	public String getPathToImageForBackground() {
		return myDirectory + File.separator + "Images/peppaFamily.jpg";
	}

	public String getNextBackgroundImage() {

		int counter = mySeed.nextInt(myBackgroundImages.size());

		String absolutePath = ((File) myBackgroundImages.get(counter)).getPath();

		String[] absolutePathSplitted = StringUtils.split(absolutePath, File.separator);

		return "BackgroundImages\\" + File.separator + absolutePathSplitted[absolutePathSplitted.length - 1];
	}
}
