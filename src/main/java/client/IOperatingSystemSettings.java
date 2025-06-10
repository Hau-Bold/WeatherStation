package client;

import java.nio.file.Path;

import org.openqa.selenium.WebDriver;

public interface IOperatingSystemSettings {

	/**
	 * yields WebDriver used on System
	 */
	WebDriver getWebDriver();

	/**
	 * yields Path to database
	 */
	Path getDatabase();

	Path getPathToHomePage();

	String getPathToDHT22Data();

	String getPathToImageForBackground();

	// TODO: this should point to a repository for the images....
	String getNextBackgroundImage();
}