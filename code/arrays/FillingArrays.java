// arrays/FillingArrays.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using Arrays.fill()
import java.util.*;
import static onjava.ArrayShow.*;

public class FillingArrays {
  public static void main(String[] args) {
    int size = 6;
    boolean[] a1 = new boolean[size];
    byte[] a2 = new byte[size];
    char[] a3 = new char[size];
    short[] a4 = new short[size];
    int[] a5 = new int[size];
    long[] a6 = new long[size];
    float[] a7 = new float[size];
    double[] a8 = new double[size];
    String[] a9 = new String[size];
    Arrays.fill(a1, true);
    show("a1", a1);
    Arrays.fill(a2, (byte)11);
    show("a2", a2);
    Arrays.fill(a3, 'x');
    show("a3", a3);
    Arrays.fill(a4, (short)17);
    show("a4", a4);
    Arrays.fill(a5, 19);
    show("a5", a5);
    Arrays.fill(a6, 23);
    show("a6", a6);
    Arrays.fill(a7, 29);
    show("a7", a7);
    Arrays.fill(a8, 47);
    show("a8", a8);
    Arrays.fill(a9, "Hello");
    show("a9", a9);
    // Manipulating ranges:
    Arrays.fill(a9, 3, 5, "World");
    show("a9", a9);
  }
}
/* Output:
a1: [true, true, true, true, true, true]
a2: [11, 11, 11, 11, 11, 11]
a3: [x, x, x, x, x, x]
a4: [17, 17, 17, 17, 17, 17]
a5: [19, 19, 19, 19, 19, 19]
a6: [23, 23, 23, 23, 23, 23]
a7: [29.0, 29.0, 29.0, 29.0, 29.0, 29.0]
a8: [47.0, 47.0, 47.0, 47.0, 47.0, 47.0]
a9: [Hello, Hello, Hello, Hello, Hello, Hello]
a9: [Hello, Hello, Hello, World, World, Hello]
*/
