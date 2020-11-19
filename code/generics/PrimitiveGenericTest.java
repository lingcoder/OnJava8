// generics/PrimitiveGenericTest.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import onjava.*;
import java.util.*;
import java.util.function.*;

// Fill an array using a generator:
interface FillArray {
  static <T> T[] fill(T[] a, Supplier<T> gen) {
    Arrays.setAll(a, n -> gen.get());
    return a;
  }
  static int[] fill(int[] a, IntSupplier gen) {
    Arrays.setAll(a, n -> gen.getAsInt());
    return a;
  }
  static long[] fill(long[] a, LongSupplier gen) {
    Arrays.setAll(a, n -> gen.getAsLong());
    return a;
  }
  static double[] fill(double[] a, DoubleSupplier gen) {
    Arrays.setAll(a, n -> gen.getAsDouble());
    return a;
  }
}

public class PrimitiveGenericTest {
  public static void main(String[] args) {
    String[] strings = FillArray.fill(
      new String[5], new Rand.String(9));
    System.out.println(Arrays.toString(strings));
    int[] integers = FillArray.fill(
      new int[9], new Rand.Pint());
    System.out.println(Arrays.toString(integers));
  }
}
/* Output:
[btpenpccu, xszgvgmei, nneeloztd, vewcippcy, gpoalkljl]
[635, 8737, 3941, 4720, 6177, 8479, 6656, 3768, 4948]
*/
