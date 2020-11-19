// concurrent/Summing4.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {ExcludeFromTravisCI}
import java.util.*;

public class Summing4 {
  public static void main(String[] args) {
    System.out.println(Summing3.CHECK);
    Long[] aL = new Long[Summing3.SZ+1];
    Arrays.parallelSetAll(aL, i -> (long)i);
    Summing.timeTest("Long Parallel",
      Summing3.CHECK, () ->
      Arrays.stream(aL)
        .parallel()
        .reduce(0L,Long::sum));
  }
}
/* Output:
50000005000000
Long Parallel: 1014ms
*/
