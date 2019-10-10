package util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertyManager {

  private static final Properties property = new Properties();
  private static final String propFileName = "test.properties";

  private PropertyManager() {
  }

  public static Properties getProperties() throws IOException {
    InputStream inputStream = PropertyManager.class.getClassLoader().getResourceAsStream(propFileName);
    property.load(inputStream);
    return property;
  }
}
