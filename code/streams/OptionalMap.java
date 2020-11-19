// streams/OptionalMap.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

class OptionalMap {
  static String[] elements = { "12", "", "23", "45" };
  static Stream<String> testStream() {
    return Arrays.stream(elements);
  }
  static void
  test(String descr, Function<String, String> func) {
    System.out.println(" ---( " + descr + " )---");
    for(int i = 0; i <= elements.length; i++) {
      System.out.println(
        testStream()
          .skip(i)
          .findFirst() // Produces an Optional
          .map(func));
    }
  }
  public static void main(String[] args) {

    // If Optional is not empty, map() first extracts
    // the contents which it then passes
    // to the function:

    test("Add brackets", s -> "[" + s + "]");

    test("Increment", s -> {
      try {
        return Integer.parseInt(s) + 1 + "";
      } catch(NumberFormatException e) {
        return s;
      }
    });

    test("Replace", s -> s.replace("2", "9"));

    test("Take last digit", s -> s.length() > 0 ?
      s.charAt(s.length() - 1) + "" : s);
  }
  // After the function is finished, map() wraps the
  // result in an Optional before returning it:
}
/* Output:
 ---( Add brackets )---
Optional[[12]]
Optional[[]]
Optional[[23]]
Optional[[45]]
Optional.empty
 ---( Increment )---
Optional[13]
Optional[]
Optional[24]
Optional[46]
Optional.empty
 ---( Replace )---
Optional[19]
Optional[]
Optional[93]
Optional[45]
Optional.empty
 ---( Take last digit )---
Optional[2]
Optional[]
Optional[3]
Optional[5]
Optional.empty
*/
