// concurrent/Summing3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {ExcludeFromTravisCI}
import java.util.*;

public class Summing3 {
  static long basicSum(Long[] ia) {
    long sum = 0;
    int size = ia.length;
    for(int i = 0; i < size; i++)
      sum += ia[i];
    return sum;
  }
  // Approximate largest value of SZ before
  // running out of memory on my machine:
  public static final int SZ = 10_000_000;
  public static final long CHECK =
    (long)SZ * ((long)SZ + 1)/2;
  public static void main(String[] args) {
    System.out.println(CHECK);
    Long[] aL = new Long[SZ+1];
    Arrays.parallelSetAll(aL, i -> (long)i);
    Summing.timeTest("Long Array Stream Reduce",
      CHECK, () ->
      Arrays.stream(aL).reduce(0L, Long::sum));
    Summing.timeTest("Long Basic Sum", CHECK, () ->
      basicSum(aL));
    // Destructive summation:
    Summing.timeTest("Long parallelPrefix",CHECK, ()-> {
      Arrays.parallelPrefix(aL, Long::sum);
      return aL[aL.length - 1];
    });
  }
}
/* Output:
50000005000000
Long Array Stream Reduce: 1038ms
Long Basic Sum: 21ms
Long parallelPrefix: 3616ms
*/
