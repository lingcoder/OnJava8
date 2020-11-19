// references/AddingClone.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// You must go through a few gyrations
// to add cloning to your own class
import java.util.*;
import java.util.stream.*;

class Int2 implements Cloneable {
  private int i;
  Int2(int ii) { i = ii; }
  public void increment() { i++; }
  @Override
  public String toString() {
    return Integer.toString(i);
  }
  @Override
  public Int2 clone() {
    try {
      return (Int2)super.clone();
    } catch(CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}

// Inheritance doesn't remove cloneability:
class Int3 extends Int2 {
  private int j; // Automatically duplicated
  Int3(int i) { super(i); }
}

public class AddingClone {
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    Int2 x = new Int2(10);
    Int2 x2 = x.clone();
    x2.increment();
    System.out.println(
      "x = " + x + ", x2 = " + x2);
    // Anything inherited is also cloneable:
    Int3 x3 = new Int3(7);
    x3 = (Int3)x3.clone();
    ArrayList<Int2> v = IntStream.range(0, 10)
      .mapToObj(Int2::new)
      .collect(Collectors
        .toCollection(ArrayList::new));
    System.out.println("v: " + v);
    ArrayList<Int2> v2 =
      (ArrayList<Int2>)v.clone();
    // Now clone each element:
    IntStream.range(0, v.size())
      .forEach(i -> v2.set(i, v.get(i).clone()));
    // Increment all v2's elements:
    v2.forEach(Int2::increment);
    System.out.println("v2: " + v2);
    // See if it changed v's elements:
    System.out.println("v: " + v);
  }
}
/* Output:
x = 10, x2 = 11
v: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
v2: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
v: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
*/
