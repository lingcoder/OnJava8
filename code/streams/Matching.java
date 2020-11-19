// streams/Matching.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Demonstrates short-circuiting of *Match() operations
import java.util.stream.*;
import java.util.function.*;
import static streams.RandInts.*;

interface Matcher extends
  BiPredicate<Stream<Integer>, Predicate<Integer>> {}

public class Matching {
  static void show(Matcher match, int val) {
    System.out.println(
      match.test(
        IntStream.rangeClosed(1, 9)
          .boxed()
          .peek(n -> System.out.format("%d ", n)),
        n -> n < val));
  }
  public static void main(String[] args) {
    show(Stream::allMatch, 10);
    show(Stream::allMatch, 4);
    show(Stream::anyMatch, 2);
    show(Stream::anyMatch, 0);
    show(Stream::noneMatch, 5);
    show(Stream::noneMatch, 0);
  }
}
/* Output:
1 2 3 4 5 6 7 8 9 true
1 2 3 4 false
1 true
1 2 3 4 5 6 7 8 9 false
1 false
1 2 3 4 5 6 7 8 9 true
*/
