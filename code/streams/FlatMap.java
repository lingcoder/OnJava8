// streams/FlatMap.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.stream.*;

public class FlatMap {
  public static void main(String[] args) {
    Stream.of(1, 2, 3)
      .flatMap(
        i -> Stream.of("Gonzo", "Fozzie", "Beaker"))
      .forEach(System.out::println);
  }
}
/* Output:
Gonzo
Fozzie
Beaker
Gonzo
Fozzie
Beaker
Gonzo
Fozzie
Beaker
*/
