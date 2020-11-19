// functional/CurriedIntAdd.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;

public class CurriedIntAdd {
  public static void main(String[] args) {
    IntFunction<IntUnaryOperator>
      curriedIntAdd = a -> b -> a + b;
    IntUnaryOperator add4 = curriedIntAdd.apply(4);
    System.out.println(add4.applyAsInt(5));
  }
}
/* Output:
9
*/
