// references/TemperatureReading.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Cloning a composed object
package references;

public class TemperatureReading implements Cloneable {
  private long time;
  private double temperature;
  public TemperatureReading(double temperature) {
    time = System.currentTimeMillis();
    this.temperature = temperature;
  }
  @Override
  public TemperatureReading clone() {
    try {
      return (TemperatureReading)super.clone();
    } catch(CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
  public double getTemperature() {
    return temperature;
  }
  public void setTemperature(double temp) {
    this.temperature = temp;
  }
  @Override
  public String toString() {
    return String.valueOf(temperature);
  }
}
