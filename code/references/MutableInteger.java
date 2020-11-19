// references/MutableInteger.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A changeable wrapper class
import java.util.*;
import java.util.stream.*;

class IntValue {
  private int n;
  IntValue(int x) { n = x; }
  public int getValue() { return n; }
  public void setValue(int n) { this.n = n; }
  public void increment() { n++; }
  @Override
  public String toString() {
    return Integer.toString(n);
  }
}

public class MutableInteger {
  public static void main(String[] args) {
    List<IntValue> v = IntStream.range(0, 10)
      .mapToObj(IntValue::new)
      .collect(Collectors.toList());
    System.out.println(v);
    v.forEach(IntValue::increment);
    System.out.println(v);
  }
}
/* Output:
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
[1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
*/
