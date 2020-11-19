// validating/BadMicroBenchmark2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Relying on a common resource
import java.util.*;
import onjava.Timer;

public class BadMicroBenchmark2 {
  // SIZE reduced to make it run faster:
  static final int SIZE = 5_000_000;
  public static void main(String[] args) {
    long[] la = new long[SIZE];
    Random r = new Random();
    System.out.println("parallelSetAll: " +
      Timer.duration(() ->
        Arrays.parallelSetAll(la, n -> r.nextLong())));
    System.out.println("setAll: " +
      Timer.duration(() ->
        Arrays.setAll(la, n -> r.nextLong())));
    SplittableRandom sr = new SplittableRandom();
    System.out.println("parallelSetAll: " +
      Timer.duration(() ->
        Arrays.parallelSetAll(la, n -> sr.nextLong())));
    System.out.println("setAll: " +
      Timer.duration(() ->
        Arrays.setAll(la, n -> sr.nextLong())));
  }
}
/* Output:
parallelSetAll: 1147
setAll: 174
parallelSetAll: 86
setAll: 39
*/
