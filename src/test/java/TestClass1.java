import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

public class TestClass1 extends BaseComponentIT {

  public TestClass1(DesiredCapabilities capability) throws IOException {
    super(capability);
  }

  @Test
  public void saveDate() {
    //...
  }
}
