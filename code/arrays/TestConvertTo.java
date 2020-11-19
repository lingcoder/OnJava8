// arrays/TestConvertTo.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import onjava.*;
import static onjava.ArrayShow.*;
import static onjava.ConvertTo.*;

public class TestConvertTo {
  static final int SIZE = 6;
  public static void main(String[] args) {
    Boolean[] a1 = new Boolean[SIZE];
    Arrays.setAll(a1, new Rand.Boolean()::get);
    boolean[] a1p = primitive(a1);
    show("a1p", a1p);
    Boolean[] a1b = boxed(a1p);
    show("a1b", a1b);

    Byte[] a2 = new Byte[SIZE];
    Arrays.setAll(a2, new Rand.Byte()::get);
    byte[] a2p = primitive(a2);
    show("a2p", a2p);
    Byte[] a2b = boxed(a2p);
    show("a2b", a2b);

    Character[] a3 = new Character[SIZE];
    Arrays.setAll(a3, new Rand.Character()::get);
    char[] a3p = primitive(a3);
    show("a3p", a3p);
    Character[] a3b = boxed(a3p);
    show("a3b", a3b);

    Short[] a4 = new Short[SIZE];
    Arrays.setAll(a4, new Rand.Short()::get);
    short[] a4p = primitive(a4);
    show("a4p", a4p);
    Short[] a4b = boxed(a4p);
    show("a4b", a4b);

    Integer[] a5 = new Integer[SIZE];
    Arrays.setAll(a5, new Rand.Integer()::get);
    int[] a5p = primitive(a5);
    show("a5p", a5p);
    Integer[] a5b = boxed(a5p);
    show("a5b", a5b);

    Long[] a6 = new Long[SIZE];
    Arrays.setAll(a6, new Rand.Long()::get);
    long[] a6p = primitive(a6);
    show("a6p", a6p);
    Long[] a6b = boxed(a6p);
    show("a6b", a6b);

    Float[] a7 = new Float[SIZE];
    Arrays.setAll(a7, new Rand.Float()::get);
    float[] a7p = primitive(a7);
    show("a7p", a7p);
    Float[] a7b = boxed(a7p);
    show("a7b", a7b);

    Double[] a8 = new Double[SIZE];
    Arrays.setAll(a8, new Rand.Double()::get);
    double[] a8p = primitive(a8);
    show("a8p", a8p);
    Double[] a8b = boxed(a8p);
    show("a8b", a8b);
  }
}
/* Output:
a1p: [true, false, true, true, true, false]
a1b: [true, false, true, true, true, false]
a2p: [123, 33, 101, 112, 33, 31]
a2b: [123, 33, 101, 112, 33, 31]
a3p: [b, t, p, e, n, p]
a3b: [b, t, p, e, n, p]
a4p: [635, 8737, 3941, 4720, 6177, 8479]
a4b: [635, 8737, 3941, 4720, 6177, 8479]
a5p: [635, 8737, 3941, 4720, 6177, 8479]
a5b: [635, 8737, 3941, 4720, 6177, 8479]
a6p: [6882, 3765, 692, 9575, 4439, 2638]
a6b: [6882, 3765, 692, 9575, 4439, 2638]
a7p: [4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
a7b: [4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
a8p: [4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
a8b: [4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
*/
