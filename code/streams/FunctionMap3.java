// streams/FunctionMap3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Producing numeric output streams
import java.util.*;
import java.util.stream.*;

class FunctionMap3 {
  public static void main(String[] args) {
    Stream.of("5", "7", "9")
      .mapToInt(Integer::parseInt)
      .forEach(n -> System.out.format("%d ", n));
    System.out.println();
    Stream.of("17", "19", "23")
      .mapToLong(Long::parseLong)
      .forEach(n -> System.out.format("%d ", n));
    System.out.println();
    Stream.of("17", "1.9", ".23")
      .mapToDouble(Double::parseDouble)
      .forEach(n -> System.out.format("%f ", n));
  }
}
/* Output:
5 7 9
17 19 23
17.000000 1.900000 0.230000
*/
