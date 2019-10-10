import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.remote.DesiredCapabilities;
import util.PropertyManager;

import static org.junit.Assert.assertTrue;

public abstract class BaseComponentIT extends BaseTest {

  protected String login;
  protected String password;

  BaseComponentIT(DesiredCapabilities capability) throws IOException {
    super(capability);
    initCredentials();
  }

  private void initCredentials() {
    Properties config;
    try {
      config = PropertyManager.getProperties();
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to get properties. " + e);
    }

    login = config.getProperty("login");
    password = config.getProperty("password");
  }

}
