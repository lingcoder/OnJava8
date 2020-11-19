// references/DepthReading.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Cloning a composed object
package references;

public class DepthReading implements Cloneable {
  private double depth;
  public DepthReading(double depth) {
    this.depth = depth;
  }
  @Override
  public DepthReading clone() {
    try {
      return (DepthReading)super.clone();
    } catch(CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
  public double getDepth() { return depth; }
  public void setDepth(double depth) {
    this.depth = depth;
  }
  @Override
  public String toString() {
    return String.valueOf(depth);
  }
}
