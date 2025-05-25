package display.Streaming;

import org.openqa.selenium.WebDriver;

public interface IStreamingObject {
  void stream(WebDriver paramWebDriver);
  
  StreamingTime GetStreamingTime();
}