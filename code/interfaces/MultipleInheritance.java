// interfaces/MultipleInheritance.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

interface One {
  default void first() { System.out.println("first"); }
}

interface Two {
  default void second() {
    System.out.println("second");
  }
}

interface Three {
  default void third() { System.out.println("third"); }
}

class MI implements One, Two, Three {}

public class MultipleInheritance {
  public static void main(String[] args) {
    MI mi = new MI();
    mi.first();
    mi.second();
    mi.third();
  }
}
/* Output:
first
second
third
*/
