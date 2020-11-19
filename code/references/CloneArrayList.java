// references/CloneArrayList.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// The clone() operation works for only a few
// items in the standard Java library
import java.util.*;
import java.util.stream.*;

class Int {
  private int i;
  Int(int ii) { i = ii; }
  public void increment() { i++; }
  @Override
  public String toString() {
    return Integer.toString(i);
  }
}

public class CloneArrayList {
  public static void main(String[] args) {
    ArrayList<Int> v = IntStream.range(0, 10)
      .mapToObj(Int::new)
      .collect(Collectors
        .toCollection(ArrayList::new));
    System.out.println("v: " + v);
    @SuppressWarnings("unchecked")
    ArrayList<Int> v2 = (ArrayList<Int>)v.clone();
    // Increment all v2's elements:
    v2.forEach(Int::increment);
    // See if it changed v's elements:
    System.out.println("v: " + v);
  }
}
/* Output:
v: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
v: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
*/
