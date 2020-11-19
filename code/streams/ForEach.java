// streams/ForEach.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import static streams.RandInts.*;

public class ForEach {
  static final int SZ = 14;
  public static void main(String[] args) {
    rands().limit(SZ)
      .forEach(n -> System.out.format("%d ", n));
    System.out.println();
    rands().limit(SZ)
      .parallel()
      .forEach(n -> System.out.format("%d ", n));
    System.out.println();
    rands().limit(SZ)
      .parallel()
      .forEachOrdered(n -> System.out.format("%d ", n));
  }
}
/* Output:
258 555 693 861 961 429 868 200 522 207 288 128 551 589
551 861 429 589 200 522 555 693 258 128 868 288 961 207
258 555 693 861 961 429 868 200 522 207 288 128 551 589
*/
