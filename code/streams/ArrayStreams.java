// streams/ArrayStreams.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

public class ArrayStreams {
  public static void main(String[] args) {
    Arrays.stream(
      new double[] { 3.14159, 2.718, 1.618 })
      .forEach(n -> System.out.format("%f ", n));
    System.out.println();
    Arrays.stream(new int[] { 1, 3, 5 })
      .forEach(n -> System.out.format("%d ", n));
    System.out.println();
    Arrays.stream(new long[] { 11, 22, 44, 66 })
      .forEach(n -> System.out.format("%d ", n));
    System.out.println();
    // Select a subrange:
    Arrays.stream(
      new int[] { 1, 3, 5, 7, 15, 28, 37 }, 3, 6)
      .forEach(n -> System.out.format("%d ", n));
  }
}
/* Output:
3.141590 2.718000 1.618000
1 3 5
11 22 44 66
7 15 28
*/
