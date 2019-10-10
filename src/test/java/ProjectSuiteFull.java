import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestClass1.class,
    TestClass2.class
})
public class ProjectSuiteFull {
  @BeforeClass
  public static void checkApp() throws MalformedURLException {
    String applicationUrl = System.getProperty("application.url");
    String gridUrl = System.getProperty("selenium.grid.url");

    WebDriver driver = new RemoteWebDriver(new URL(gridUrl), DesiredCapabilities.firefox());
    try {
      driver.get(applicationUrl);
    }
    catch (Exception ignored) {
      Assert.fail("the application hasn't been deployed");
    }
    finally {
      driver.close();
    }
  }
}
