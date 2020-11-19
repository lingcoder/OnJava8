// streams/OptionalFilter.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

class OptionalFilter {
  static String[] elements = {
    "Foo", "", "Bar", "Baz", "Bingo"
  };
  static Stream<String> testStream() {
    return Arrays.stream(elements);
  }
  static void
  test(String descr, Predicate<String> pred) {
    System.out.println(" ---( " + descr + " )---");
    for(int i = 0; i <= elements.length; i++) {
      System.out.println(
        testStream()
          .skip(i)
          .findFirst()
          .filter(pred));
    }
  }
  public static void main(String[] args) {
    test("true", str -> true);
    test("false", str -> false);
    test("str != \"\"", str -> str != "");
    test("str.length() == 3", str -> str.length() == 3);
    test("startsWith(\"B\")",
         str -> str.startsWith("B"));
  }
}
/* Output:
 ---( true )---
Optional[Foo]
Optional[]
Optional[Bar]
Optional[Baz]
Optional[Bingo]
Optional.empty
 ---( false )---
Optional.empty
Optional.empty
Optional.empty
Optional.empty
Optional.empty
Optional.empty
 ---( str != "" )---
Optional[Foo]
Optional.empty
Optional[Bar]
Optional[Baz]
Optional[Bingo]
Optional.empty
 ---( str.length() == 3 )---
Optional[Foo]
Optional.empty
Optional[Bar]
Optional[Baz]
Optional.empty
Optional.empty
 ---( startsWith("B") )---
Optional.empty
Optional.empty
Optional[Bar]
Optional[Baz]
Optional[Bingo]
Optional.empty
*/
