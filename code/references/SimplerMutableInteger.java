// references/SimplerMutableInteger.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A trivial wrapper class
import java.util.*;
import java.util.stream.*;

class IntValue2 {
  public int n;
  IntValue2(int n) { this.n = n; }
}

public class SimplerMutableInteger {
  public static void main(String[] args) {
    List<IntValue2> v = IntStream.range(0, 10)
      .mapToObj(IntValue2::new)
      .collect(Collectors.toList());
    v.forEach(iv2 ->
      System.out.print(iv2.n + " "));
    System.out.println();
    v.forEach(iv2 -> iv2.n += 1);
    v.forEach(iv2 ->
      System.out.print(iv2.n + " "));
  }
}
/* Output:
0 1 2 3 4 5 6 7 8 9
1 2 3 4 5 6 7 8 9 10
*/
