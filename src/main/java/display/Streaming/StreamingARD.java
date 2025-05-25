package display.Streaming;

import java.util.HashMap;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import display.WebDriver.WebDriverHelper;

public class StreamingARD
  implements IStreamingObject
{
  private static final String LIVESTREAM_URL_ARD = "https://www.daserste.de/live/ard-livestream-embed-de-100.html";
  private static final String PLAYER_SELECTOR_ARD = ".ardplayer-button-posterframe";
  private StreamingTime myStreamingTime;
  private HashMap<Keys, String> keysToSendingKey = new HashMap<Keys, String>()
    {
    
    };
  
  public StreamingARD(StreamingTime streamingTime) {
    myStreamingTime = streamingTime;
  }
  
  public StreamingTime GetStreamingTime() {
    return myStreamingTime;
  }
  
  public void stream(WebDriver webDriver) {
    webDriver.get("https://www.daserste.de/live/ard-livestream-embed-de-100.html");
    WebDriverHelper.waitForPageLoad(webDriver);
    
    WebElement element = WebDriverHelper.waitUntilVisibilityOfElementLocatedByCssSelector(webDriver, ".ardplayer-button-posterframe");
    
    for (Keys key : this.keysToSendingKey.keySet()) {
      String sendingKey = this.keysToSendingKey.get(key);
      if (sendingKey != "") {
        element.sendKeys(new CharSequence[] { (CharSequence)key, sendingKey }); continue;
      } 
      element.sendKeys(new CharSequence[] { (CharSequence)key });
    } 
  }
}