// arrays/RaggedArray.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class RaggedArray {
  static int val = 1;
  public static void main(String[] args) {
    SplittableRandom rand = new SplittableRandom(47);
    // 3-D array with varied-length vectors:
    int[][][] a = new int[rand.nextInt(7)][][];
    for(int i = 0; i < a.length; i++) {
      a[i] = new int[rand.nextInt(5)][];
      for(int j = 0; j < a[i].length; j++) {
        a[i][j] = new int[rand.nextInt(5)];
        Arrays.setAll(a[i][j], n -> val++); // [1]
      }
    }
    System.out.println(Arrays.deepToString(a));
  }
}
/* Output:
[[[1], []], [[2, 3, 4, 5], [6]], [[7, 8, 9], [10, 11,
12], []]]
*/
