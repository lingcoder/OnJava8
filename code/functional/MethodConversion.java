// functional/MethodConversion.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;

class In1 {}
class In2 {}

public class MethodConversion {
  static void accept(In1 i1, In2 i2) {
    System.out.println("accept()");
  }
  static void someOtherName(In1 i1, In2 i2) {
    System.out.println("someOtherName()");
  }
  public static void main(String[] args) {
    BiConsumer<In1,In2> bic;

    bic = MethodConversion::accept;
    bic.accept(new In1(), new In2());

    bic = MethodConversion::someOtherName;
    // bic.someOtherName(new In1(), new In2()); // Nope
    bic.accept(new In1(), new In2());
  }
}
/* Output:
accept()
someOtherName()
*/
