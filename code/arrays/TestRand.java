// arrays/TestRand.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Test random generators
import java.util.*;
import java.util.stream.*;
import onjava.*;
import static onjava.ArrayShow.*;

public class TestRand {
  static final int SZ = 5;
  public static void main(String[] args) {
    System.out.println("Boolean");
    Boolean[] a1 = new Boolean[SZ];
    Arrays.setAll(a1, new Rand.Boolean()::get);
    show(a1);
    a1 = Stream.generate(new Rand.Boolean())
      .limit(SZ + 1).toArray(Boolean[]::new);
    show(a1);
    a1 = new Rand.Boolean().array(SZ + 2);
    show(a1);
    boolean[] a1b =
      new Rand.Pboolean().array(SZ + 3);
    show(a1b);

    System.out.println("Byte");
    Byte[] a2 = new Byte[SZ];
    Arrays.setAll(a2, new Rand.Byte()::get);
    show(a2);
    a2 = Stream.generate(new Rand.Byte())
      .limit(SZ + 1).toArray(Byte[]::new);
    show(a2);
    a2 = new Rand.Byte().array(SZ + 2);
    show(a2);
    byte[] a2b = new Rand.Pbyte().array(SZ + 3);
    show(a2b);

    System.out.println("Character");
    Character[] a3 = new Character[SZ];
    Arrays.setAll(a3, new Rand.Character()::get);
    show(a3);
    a3 = Stream.generate(new Rand.Character())
      .limit(SZ + 1).toArray(Character[]::new);
    show(a3);
    a3 = new Rand.Character().array(SZ + 2);
    show(a3);
    char[] a3b = new Rand.Pchar().array(SZ + 3);
    show(a3b);

    System.out.println("Short");
    Short[] a4 = new Short[SZ];
    Arrays.setAll(a4, new Rand.Short()::get);
    show(a4);
    a4 = Stream.generate(new Rand.Short())
      .limit(SZ + 1).toArray(Short[]::new);
    show(a4);
    a4 = new Rand.Short().array(SZ + 2);
    show(a4);
    short[] a4b = new Rand.Pshort().array(SZ + 3);
    show(a4b);

    System.out.println("Integer");
    int[] a5 = new int[SZ];
    Arrays.setAll(a5, new Rand.Integer()::get);
    show(a5);
    Integer[] a5b =
      Stream.generate(new Rand.Integer())
        .limit(SZ + 1).toArray(Integer[]::new);
    show(a5b);
    a5b = new Rand.Integer().array(SZ + 2);
    show(a5b);
    a5 = IntStream.generate(new Rand.Pint())
      .limit(SZ + 1).toArray();
    show(a5);
    a5 = new Rand.Pint().array(SZ + 3);
    show(a5);

    System.out.println("Long");
    long[] a6 = new long[SZ];
    Arrays.setAll(a6, new Rand.Long()::get);
    show(a6);
    Long[] a6b = Stream.generate(new Rand.Long())
      .limit(SZ + 1).toArray(Long[]::new);
    show(a6b);
    a6b = new Rand.Long().array(SZ + 2);
    show(a6b);
    a6 = LongStream.generate(new Rand.Plong())
      .limit(SZ + 1).toArray();
    show(a6);
    a6 = new Rand.Plong().array(SZ + 3);
    show(a6);

    System.out.println("Float");
    Float[] a7 = new Float[SZ];
    Arrays.setAll(a7, new Rand.Float()::get);
    show(a7);
    a7 = Stream.generate(new Rand.Float())
      .limit(SZ + 1).toArray(Float[]::new);
    show(a7);
    a7 = new Rand.Float().array(SZ + 2);
    show(a7);
    float[] a7b = new Rand.Pfloat().array(SZ + 3);
    show(a7b);

    System.out.println("Double");
    double[] a8 = new double[SZ];
    Arrays.setAll(a8, new Rand.Double()::get);
    show(a8);
    Double[] a8b =
      Stream.generate(new Rand.Double())
        .limit(SZ + 1).toArray(Double[]::new);
    show(a8b);
    a8b = new Rand.Double().array(SZ + 2);
    show(a8b);
    a8 = DoubleStream.generate(new Rand.Pdouble())
      .limit(SZ + 1).toArray();
    show(a8);
    a8 = new Rand.Pdouble().array(SZ + 3);
    show(a8);

    System.out.println("String");
    String[] s = new String[SZ - 1];
    Arrays.setAll(s, new Rand.String()::get);
    show(s);
    s = Stream.generate(new Rand.String())
      .limit(SZ).toArray(String[]::new);
    show(s);
    s = new Rand.String().array(SZ + 1);
    show(s);

    Arrays.setAll(s, new Rand.String(4)::get);
    show(s);
    s = Stream.generate(new Rand.String(4))
      .limit(SZ).toArray(String[]::new);
    show(s);
    s = new Rand.String(4).array(SZ + 1);
    show(s);
  }
}
/* Output:
Boolean
[true, false, true, true, true]
[true, false, true, true, true, false]
[true, false, true, true, true, false, false]
[true, false, true, true, true, false, false, true]
Byte
[123, 33, 101, 112, 33]
[123, 33, 101, 112, 33, 31]
[123, 33, 101, 112, 33, 31, 0]
[123, 33, 101, 112, 33, 31, 0, -72]
Character
[b, t, p, e, n]
[b, t, p, e, n, p]
[b, t, p, e, n, p, c]
[b, t, p, e, n, p, c, c]
Short
[635, 8737, 3941, 4720, 6177]
[635, 8737, 3941, 4720, 6177, 8479]
[635, 8737, 3941, 4720, 6177, 8479, 6656]
[635, 8737, 3941, 4720, 6177, 8479, 6656, 3768]
Integer
[635, 8737, 3941, 4720, 6177]
[635, 8737, 3941, 4720, 6177, 8479]
[635, 8737, 3941, 4720, 6177, 8479, 6656]
[635, 8737, 3941, 4720, 6177, 8479]
[635, 8737, 3941, 4720, 6177, 8479, 6656, 3768]
Long
[6882, 3765, 692, 9575, 4439]
[6882, 3765, 692, 9575, 4439, 2638]
[6882, 3765, 692, 9575, 4439, 2638, 4011]
[6882, 3765, 692, 9575, 4439, 2638]
[6882, 3765, 692, 9575, 4439, 2638, 4011, 9610]
Float
[4.83, 2.89, 2.9, 1.97, 3.01]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99, 8.28]
Double
[4.83, 2.89, 2.9, 1.97, 3.01]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99, 8.28]
String
[btpenpc, cuxszgv, gmeinne, eloztdv]
[btpenpc, cuxszgv, gmeinne, eloztdv, ewcippc]
[btpenpc, cuxszgv, gmeinne, eloztdv, ewcippc, ygpoalk]
[btpe, npcc, uxsz, gvgm, einn, eelo]
[btpe, npcc, uxsz, gvgm, einn]
[btpe, npcc, uxsz, gvgm, einn, eelo]
*/
