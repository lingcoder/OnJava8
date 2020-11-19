// generics/ListOfInt.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Autoboxing compensates for the inability
// to use primitives in generics
import java.util.*;
import java.util.stream.*;

public class ListOfInt {
  public static void main(String[] args) {
    List<Integer> li = IntStream.range(38, 48)
      .boxed() // Converts ints to Integers
      .collect(Collectors.toList());
    System.out.println(li);
  }
}
/* Output:
[38, 39, 40, 41, 42, 43, 44, 45, 46, 47]
*/
