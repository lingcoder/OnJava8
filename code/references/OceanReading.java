// references/OceanReading.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Cloning a composed object
package references;

public class OceanReading implements Cloneable {
  private DepthReading depth;
  private TemperatureReading temperature;
  public
  OceanReading(double tdata, double ddata) {
    temperature = new TemperatureReading(tdata);
    depth = new DepthReading(ddata);
  }
  @Override
  public OceanReading clone() {
    OceanReading or = null;
    try {
      or = (OceanReading)super.clone();
    } catch(CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
    // Must clone references:
    or.depth = (DepthReading)or.depth.clone();
    or.temperature =
      (TemperatureReading)or.temperature.clone();
    return or;
  }
  public TemperatureReading getTemperatureReading() {
    return temperature;
  }
  public void
  setTemperatureReading(TemperatureReading tr) {
    temperature = tr;
  }
  public DepthReading getDepthReading() {
    return depth;
  }
  public void setDepthReading(DepthReading dr) {
    this.depth = dr;
  }
  @Override
  public String toString() {
    return "temperature: " + temperature +
      ", depth: " + depth;
  }
}
