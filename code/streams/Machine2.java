// streams/Machine2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import onjava.Operations;

public class Machine2 {
  public static void main(String[] args) {
    Arrays.stream(new Operations[] {
      () -> Operations.show("Bing"),
      () -> Operations.show("Crack"),
      () -> Operations.show("Twist"),
      () -> Operations.show("Pop")
    }).forEach(Operations::execute);
  }
}
/* Output:
Bing
Crack
Twist
Pop
*/
