// streams/CreatingOptionals.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

class CreatingOptionals {
  static void
  test(String testName, Optional<String> opt) {
    System.out.println(" === " + testName + " === ");
    System.out.println(opt.orElse("Null"));
  }
  public static void main(String[] args) {
    test("empty", Optional.empty());
    test("of", Optional.of("Howdy"));
    try {
      test("of", Optional.of(null));
    } catch(Exception e) {
      System.out.println(e);
    }
    test("ofNullable", Optional.ofNullable("Hi"));
    test("ofNullable", Optional.ofNullable(null));
  }
}
/* Output:
 === empty ===
Null
 === of ===
Howdy
java.lang.NullPointerException
 === ofNullable ===
Hi
 === ofNullable ===
Null
*/
