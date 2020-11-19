// streams/StreamOf.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.stream.*;

public class StreamOf {
  public static void main(String[] args) {
    Stream.of(
      new Bubble(1), new Bubble(2), new Bubble(3))
      .forEach(System.out::println);
    Stream.of("It's ", "a ", "wonderful ",
      "day ", "for ", "pie!")
      .forEach(System.out::print);
    System.out.println();
    Stream.of(3.14159, 2.718, 1.618)
      .forEach(System.out::println);
  }
}
/* Output:
Bubble(1)
Bubble(2)
Bubble(3)
It's a wonderful day for pie!
3.14159
2.718
1.618
*/
