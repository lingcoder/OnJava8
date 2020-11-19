// streams/Bubbles.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.stream.*;

public class Bubbles {
  public static void main(String[] args) {
    Stream.generate(Bubble::bubbler)
      .limit(5)
      .forEach(System.out::println);
  }
}
/* Output:
Bubble(0)
Bubble(1)
Bubble(2)
Bubble(3)
Bubble(4)
*/
