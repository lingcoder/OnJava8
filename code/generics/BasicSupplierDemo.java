// generics/BasicSupplierDemo.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import onjava.*;
import java.util.stream.*;

public class BasicSupplierDemo {
  public static void main(String[] args) {
    Stream.generate(
      BasicSupplier.create(CountedObject.class))
      .limit(5)
      .forEach(System.out::println);
  }
}
/* Output:
CountedObject 0
CountedObject 1
CountedObject 2
CountedObject 3
CountedObject 4
*/
