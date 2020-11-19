// streams/OptionalsFromEmptyStreams.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

class OptionalsFromEmptyStreams {
  public static void main(String[] args) {
    System.out.println(Stream.<String>empty()
      .findFirst());
    System.out.println(Stream.<String>empty()
      .findAny());
    System.out.println(Stream.<String>empty()
      .max(String.CASE_INSENSITIVE_ORDER));
    System.out.println(Stream.<String>empty()
      .min(String.CASE_INSENSITIVE_ORDER));
    System.out.println(Stream.<String>empty()
      .reduce((s1, s2) -> s1 + s2));
    System.out.println(IntStream.empty()
      .average());
  }
}
/* Output:
Optional.empty
Optional.empty
Optional.empty
Optional.empty
Optional.empty
OptionalDouble.empty
*/
