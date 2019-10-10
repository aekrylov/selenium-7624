import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public abstract class BaseTest {

  private static final long IMPLICIT_WAIT = 500L;

  protected static final String gridUrl = System.getProperty("selenium.grid.url"); //todo name
  protected static final String applicationUrl = System.getProperty("application.url");

  protected final WebDriver browser;
  protected final DesiredCapabilities capability;

  public BaseTest(DesiredCapabilities capability) throws MalformedURLException {

    if (capability.getBrowserName().equals("chrome")) {
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--no-sandbox");
      options.addArguments("--window-size=1280,1024");
      capability.setCapability(ChromeOptions.CAPABILITY, options);
    }

    this.capability = capability;
    LoggingPreferences logPrefs = new LoggingPreferences(); //логирование в браузере
    logPrefs.enable(LogType.BROWSER, Level.ALL);

    capability.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

    // Selenoid features: session logging and VNC for live session display
    capability.setCapability("enableLog", System.getProperty("enableLog") != null);
    capability.setCapability("enableVNC", System.getProperty("enableVNC") != null);

    RemoteWebDriver remoteDriver = new RemoteWebDriver(new URL(gridUrl), capability);
    // Configure to upload local files to remote Selenium instance
    remoteDriver.setFileDetector(new LocalFileDetector());

    // RemoteWebDriver does not implement the TakesScreenshot class
    // if the driver does have the Capabilities to take a screenshot
    // then Augmenter will add the TakesScreenshot methods to the instance
    browser = new Augmenter().augment(remoteDriver);
    if (!this.capability.equals(DesiredCapabilities.chrome())) {
      browser.manage().window().maximize();
    }
    else {
      //для очердной версии хрома сломалась команда maximize, временное решение - задать фиксированный размер окна
      browser.manage().window().setSize(new Dimension(1600, 900));
    }
    browser.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.MILLISECONDS);
    browser.get(applicationUrl);
    assertTrue("browser window size is less than expected: expected width - 1024px, actual width - "
               + browser.manage().window().getSize().getWidth() + "px",
        waitForWindowWidthToBe(1024, 15)
    );
  }

  @Parameterized.Parameters(name = "{index}: {0}")
  public static LinkedList<DesiredCapabilities[]> getBrowsers() {
    LinkedList<DesiredCapabilities[]> browsers = new LinkedList<>();

    if (System.getProperty("browser.chrome") != null)
      browsers.add(new DesiredCapabilities[] {DesiredCapabilities.chrome()});
    if (System.getProperty("browser.firefox") != null)
      browsers.add(new DesiredCapabilities[] {DesiredCapabilities.firefox()});

    if (browsers.isEmpty()) {
      browsers.add(new DesiredCapabilities[] {DesiredCapabilities.chrome()});
      browsers.add(new DesiredCapabilities[] {DesiredCapabilities.firefox()});
    }

    return browsers;
  }

  private boolean waitForWindowWidthToBe(int width, long timeOutInSeconds) {
    try {
      return new WebDriverWait(browser, timeOutInSeconds)
          .until(webDriver -> webDriver.manage().window().getSize().getWidth() > width);
    }
    catch (TimeoutException ignored) {
      return false;
    }
  }

  @Rule
  public TestRule testWatcher = new TestWatcher() {
    @Override
    protected void finished(Description description) {
      super.finished(description);
      browser.quit();
    }
  };
}
