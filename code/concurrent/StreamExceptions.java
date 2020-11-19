// concurrent/StreamExceptions.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import java.util.stream.*;

public class StreamExceptions {
  static Stream<Breakable>
  test(String id, int failcount) {
    return
      Stream.of(new Breakable(id, failcount))
        .map(Breakable::work)
        .map(Breakable::work)
        .map(Breakable::work)
        .map(Breakable::work);
  }
  public static void main(String[] args) {
    // No operations are even applied ...
    test("A", 1);
    test("B", 2);
    Stream<Breakable> c = test("C", 3);
    test("D", 4);
    test("E", 5);
    // ... until there's a terminal operation:
    System.out.println("Entering try");
    try {
      c.forEach(System.out::println);   // [1]
    } catch(Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
/* Output:
Entering try
Breakable_C [2]
Breakable_C [1]
Throwing Exception for C
Breakable_C failed
*/
