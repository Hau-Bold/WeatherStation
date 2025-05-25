 package display.Streaming;
 
 import org.openqa.selenium.WebDriver;
 import org.openqa.selenium.WebElement;

import display.WebDriver.WebDriverHelper;
 
 
 public class StreamingZDF
   implements IStreamingObject
 {
   private static final String LIVESTREAM_URL_ZDF = "https://www.zdf.de/live-tv";
   private static final String ONETRUST_ACCEPT_BUTTON_SELECTOR_ZDF = "#onetrust-accept-btn-handler";
   private static final String PLAYER_FULL_SCREEN_ZDF = ".button-fullscreen";
   private static StreamingTime myStreamingTime;
   
   public StreamingZDF(StreamingTime streamingTime) {
     myStreamingTime = streamingTime;
   }
   
   public StreamingTime GetStreamingTime() {
     return myStreamingTime;
   }
 
   
   public void stream(WebDriver webDriver) {
     webDriver.get("https://www.zdf.de/live-tv");
     WebDriverHelper.waitForPageLoad(webDriver);
 
     
     WebElement element = WebDriverHelper.waitUntilVisibilityOfElementLocatedByCssSelector(webDriver, "#onetrust-accept-btn-handler");
     element.click();
     
     element = WebDriverHelper.waitUntilVisibilityOfElementLocatedByCssSelector(webDriver, ".button-fullscreen");
     element.click();
   }
 }
