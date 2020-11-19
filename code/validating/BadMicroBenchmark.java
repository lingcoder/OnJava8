// validating/BadMicroBenchmark.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {ExcludeFromTravisCI}
import java.util.*;
import onjava.Timer;

public class BadMicroBenchmark {
  static final int SIZE = 250_000_000;
  public static void main(String[] args) {
    try { // For machines with insufficient memory
      long[] la = new long[SIZE];
      System.out.println("setAll: " +
        Timer.duration(() ->
          Arrays.setAll(la, n -> n)));
      System.out.println("parallelSetAll: " +
        Timer.duration(() ->
          Arrays.parallelSetAll(la, n -> n)));
    } catch(OutOfMemoryError e) {
      System.out.println("Insufficient memory");
      System.exit(0);
    }
  }
}
/* Output:
setAll: 272
parallelSetAll: 301
*/
