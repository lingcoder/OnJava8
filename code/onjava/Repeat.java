// onjava/Repeat.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package onjava;
import static java.util.stream.IntStream.*;

public class Repeat {
  public static void repeat(int n, Runnable action) {
    range(0, n).forEach(i -> action.run());
  }
}
