// streams/LastElement.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

public class LastElement {
  public static void main(String[] args) {
    OptionalInt last = IntStream.range(10, 20)
      .reduce((n1, n2) -> n2);
    System.out.println(last.orElse(-1));
    // Non-numeric object:
    Optional<String> lastobj =
      Stream.of("one", "two", "three")
        .reduce((n1, n2) -> n2);
    System.out.println(
      lastobj.orElse("Nothing there!"));
  }
}
/* Output:
19
three
*/
