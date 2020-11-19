// references/tests/DeepCopyTest.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package references;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class DeepCopyTest {
  @Test
  public void testClone() {
    OceanReading reading =
      new OceanReading(33.9, 100.5);
    // Now clone it:
    OceanReading clone = reading.clone();
    TemperatureReading tr =
      clone.getTemperatureReading();
    tr.setTemperature(tr.getTemperature() + 1);
    clone.setTemperatureReading(tr);
    DepthReading dr = clone.getDepthReading();
    dr.setDepth(dr.getDepth() + 1);
    clone.setDepthReading(dr);
    assertEquals(reading.toString(),
      "temperature: 33.9, depth: 100.5");
    assertEquals(clone.toString(),
      "temperature: 34.9, depth: 101.5");
  }
}
