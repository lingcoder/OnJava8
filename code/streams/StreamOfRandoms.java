// streams/StreamOfRandoms.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

public class StreamOfRandoms {
  static Random rand = new Random(47);
  public static void main(String[] args) {
    Stream.of(1, 2, 3, 4, 5)
      .flatMapToInt(i -> IntStream.concat(
        rand.ints(0, 100).limit(i), IntStream.of(-1)))
      .forEach(n -> System.out.format("%d ", n));
  }
}
/* Output:
58 -1 55 93 -1 61 61 29 -1 68 0 22 7 -1 88 28 51 89 9
-1
*/
