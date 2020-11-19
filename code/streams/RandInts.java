// streams/RandInts.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package streams;
import java.util.*;
import java.util.stream.*;

public class RandInts {
  private static int[] rints =
    new Random(47).ints(0, 1000).limit(100).toArray();
  public static IntStream rands() {
    return Arrays.stream(rints);
  }
}
