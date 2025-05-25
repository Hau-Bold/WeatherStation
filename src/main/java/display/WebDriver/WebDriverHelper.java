package display.WebDriver;

import java.time.Duration;
import java.util.function.Function;

import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import client.IOperatingSystemSettings;

public class WebDriverHelper {
	@SuppressWarnings("unchecked")
	public static void waitForPageLoad(WebDriver driver) {

		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return Boolean.valueOf(((JavascriptExecutor) driver)
						.executeScript("return document.readyState", new Object[0]).equals("complete"));
			}
		};
		WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
		try {
			webDriverWait.until((Function) expectation);
		} catch (Throwable throwable) {
		}
	}

	public static WebElement waitUntilVisibilityOfElementLocatedByCssSelector(WebDriver webDriver, String cssSelector) {
		WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(2000));
		@SuppressWarnings("unchecked")
		WebElement webElement = (WebElement) wait
				.until((Function) ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));

		return webElement;
	}

	public static WebDriver initDriver(IOperatingSystemSettings operatingSystemSettings) {

		WebDriver webDriver = operatingSystemSettings.getWebDriver();

		if (SystemUtils.IS_OS_WINDOWS) {
			webDriver.manage().window().maximize();
			webDriver.manage().window().fullscreen();
		}

		return webDriver;
	}
}
