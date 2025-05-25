package display.Streaming;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import display.WebDriver.WebDriverHelper;

public class StreamingKIKA
  implements IStreamingObject
{
  private static final String LIVESTREAM_URL_KIKA = "https://www.kika.de/videos/livestream/index.html";
  private static final String PLAYER_SELECTOR_KIKA = "img.img:nth-child(3)";
  private static final String FULLSCREEN_SELECTOR_KIKA = "button.btn:nth-child(5)";
  private StreamingTime myStreamingTime;
  
  public StreamingKIKA(StreamingTime streamingTime) {
    this.myStreamingTime = streamingTime;
  }
  
  public StreamingTime GetStreamingTime() {
    return this.myStreamingTime;
  }
  
  public void stream(WebDriver webDriver) {
    webDriver.get("https://www.kika.de/videos/livestream/index.html");
    WebDriverHelper.waitForPageLoad(webDriver);

    
    WebElement element = WebDriverHelper.waitUntilVisibilityOfElementLocatedByCssSelector(webDriver, "img.img:nth-child(3)");
    element.click();
    
    element = WebDriverHelper.waitUntilVisibilityOfElementLocatedByCssSelector(webDriver, "button.btn:nth-child(5)");
    element.click();
  }
}