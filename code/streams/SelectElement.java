// streams/SelectElement.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import static streams.RandInts.*;

public class SelectElement {
  public static void main(String[] args) {
    System.out.println(rands().findFirst().getAsInt());
    System.out.println(
      rands().parallel().findFirst().getAsInt());
    System.out.println(rands().findAny().getAsInt());
    System.out.println(
      rands().parallel().findAny().getAsInt());
  }
}
/* Output:
258
258
258
242
*/
