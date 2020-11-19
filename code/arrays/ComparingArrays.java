// arrays/ComparingArrays.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using Arrays.equals()
import java.util.*;
import onjava.*;

public class ComparingArrays {
  public static final int SZ = 15;
  static String[][] twoDArray() {
    String[][] md = new String[5][];
    Arrays.setAll(md, n -> new String[n]);
    for(int i = 0; i < md.length; i++)
      Arrays.setAll(md[i], new Rand.String()::get);
    return md;
  }
  public static void main(String[] args) {
    int[] a1 = new int[SZ], a2 = new int[SZ];
    Arrays.setAll(a1, new Count.Integer()::get);
    Arrays.setAll(a2, new Count.Integer()::get);
    System.out.println(
      "a1 == a2: " + Arrays.equals(a1, a2));
    a2[3] = 11;
    System.out.println(
      "a1 == a2: " + Arrays.equals(a1, a2));

    Integer[] a1w = new Integer[SZ],
      a2w = new Integer[SZ];
    Arrays.setAll(a1w, new Count.Integer()::get);
    Arrays.setAll(a2w, new Count.Integer()::get);
    System.out.println(
      "a1w == a2w: " + Arrays.equals(a1w, a2w));
    a2w[3] = 11;
    System.out.println(
      "a1w == a2w: " + Arrays.equals(a1w, a2w));

    String[][] md1 = twoDArray(), md2 = twoDArray();
    System.out.println(Arrays.deepToString(md1));
    System.out.println("deepEquals(md1, md2): " +
      Arrays.deepEquals(md1, md2));
    System.out.println(
      "md1 == md2: " + Arrays.equals(md1, md2));
    md1[4][1] = "#$#$#$#";
    System.out.println(Arrays.deepToString(md1));
    System.out.println("deepEquals(md1, md2): " +
      Arrays.deepEquals(md1, md2));
  }
}
/* Output:
a1 == a2: true
a1 == a2: false
a1w == a2w: true
a1w == a2w: false
[[], [btpenpc], [btpenpc, cuxszgv], [btpenpc, cuxszgv,
gmeinne], [btpenpc, cuxszgv, gmeinne, eloztdv]]
deepEquals(md1, md2): true
md1 == md2: false
[[], [btpenpc], [btpenpc, cuxszgv], [btpenpc, cuxszgv,
gmeinne], [btpenpc, #$#$#$#, gmeinne, eloztdv]]
deepEquals(md1, md2): false
*/
