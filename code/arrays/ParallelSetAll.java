// arrays/ParallelSetAll.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import onjava.*;

public class ParallelSetAll {
  static final int SIZE = 10_000_000;
  static void intArray() {
    int[] ia = new int[SIZE];
    Arrays.setAll(ia, new Rand.Pint()::get);
    Arrays.parallelSetAll(ia, new Rand.Pint()::get);
  }
  static void longArray() {
    long[] la = new long[SIZE];
    Arrays.setAll(la, new Rand.Plong()::get);
    Arrays.parallelSetAll(la, new Rand.Plong()::get);
  }
  public static void main(String[] args) {
    intArray();
    longArray();
  }
}
