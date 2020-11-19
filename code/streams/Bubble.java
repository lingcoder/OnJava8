// streams/Bubble.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;

public class Bubble {
  public final int i;
  public Bubble(int n) { i = n; }
  @Override
  public String toString() {
    return "Bubble(" + i + ")";
  }
  private static int count = 0;
  public static Bubble bubbler() {
    return new Bubble(count++);
  }
}
