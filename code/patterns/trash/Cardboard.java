// patterns/trash/Cardboard.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package patterns.trash;

public class Cardboard extends Trash {
  private static double val = 0.23f;
  public Cardboard(double wt) { super(wt); }
  @Override
  public double value() { return val; }
  public static void value(double newVal) {
    val = newVal;
  }
}
