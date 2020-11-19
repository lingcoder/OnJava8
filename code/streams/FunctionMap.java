// streams/FunctionMap.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

class FunctionMap {
  static String[] elements = { "12", "", "23", "45" };
  static Stream<String> testStream() {
    return Arrays.stream(elements);
  }
  static void
  test(String descr, Function<String, String> func) {
    System.out.println(" ---( " + descr + " )---");
    testStream()
      .map(func)
      .forEach(System.out::println);
  }
  public static void main(String[] args) {

    test("add brackets", s -> "[" + s + "]");

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
}
/* Output:
 ---( add brackets )---
[12]
[]
[23]
[45]
 ---( Increment )---
13

24
46
 ---( Replace )---
19

93
45
 ---( Take last digit )---
2

3
5
*/
