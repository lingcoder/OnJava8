// streams/Duplicator.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.stream.*;

public class Duplicator {
  public static void main(String[] args) {
    Stream.generate(() -> "duplicate")
      .limit(3)
      .forEach(System.out::println);
  }
}
/* Output:
duplicate
duplicate
duplicate
*/
