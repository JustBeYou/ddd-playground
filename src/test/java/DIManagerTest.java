import di.DIManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class DIManagerTest {
  @Test
  void shouldRegisterProperly() {
    var diManager = DIManager.getInstance();
    var anyObject = new Object();
    diManager.register("Object", anyObject);
    assertSame(diManager.get("Object"), anyObject);
  }
}