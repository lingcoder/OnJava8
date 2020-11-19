// concurrent/Summing2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {ExcludeFromTravisCI}
import java.util.*;

public class Summing2 {
  static long basicSum(long[] ia) {
    long sum = 0;
    int size = ia.length;
    for(int i = 0; i < size; i++)
      sum += ia[i];
    return sum;
  }
  // Approximate largest value of SZ before
  // running out of memory on my machine:
  public static final int SZ = 20_000_000;
  public static final long CHECK =
    (long)SZ * ((long)SZ + 1)/2;
  public static void main(String[] args) {
    System.out.println(CHECK);
    long[] la = new long[SZ+1];
    Arrays.parallelSetAll(la, i -> i);
    Summing.timeTest("Array Stream Sum", CHECK, () ->
      Arrays.stream(la).sum());
    Summing.timeTest("Parallel", CHECK, () ->
      Arrays.stream(la).parallel().sum());
    Summing.timeTest("Basic Sum", CHECK, () ->
      basicSum(la));
    // Destructive summation:
    Summing.timeTest("parallelPrefix", CHECK, () -> {
      Arrays.parallelPrefix(la, Long::sum);
      return la[la.length - 1];
    });
  }
}
/* Output:
200000010000000
Array Stream Sum: 104ms
Parallel: 81ms
Basic Sum: 106ms
parallelPrefix: 265ms
*/
