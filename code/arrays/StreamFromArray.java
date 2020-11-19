// arrays/StreamFromArray.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import onjava.*;

public class StreamFromArray {
  public static void main(String[] args) {
    String[] s = new Rand.String().array(10);
    Arrays.stream(s)
      .skip(3)
      .limit(5)
      .map(ss -> ss + "!")
      .forEach(System.out::println);

    int[] ia = new Rand.Pint().array(10);
    Arrays.stream(ia)
      .skip(3)
      .limit(5)
      .map(i -> i * 10)
      .forEach(System.out::println);

    Arrays.stream(new long[10]);
    Arrays.stream(new double[10]);

    // Only int, long and double work:
    //- Arrays.stream(new boolean[10]);
    //- Arrays.stream(new byte[10]);
    //- Arrays.stream(new char[10]);
    //- Arrays.stream(new short[10]);
    //- Arrays.stream(new float[10]);

    // For the other types you must use wrapped arrays:
    float[] fa = new Rand.Pfloat().array(10);
    Arrays.stream(ConvertTo.boxed(fa));
    Arrays.stream(new Rand.Float().array(10));
  }
}
/* Output:
eloztdv!
ewcippc!
ygpoalk!
ljlbynx!
taprwxz!
47200
61770
84790
66560
37680
*/
