// strings/ArrayListDisplay.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import generics.coffee.*;

public class ArrayListDisplay {
  public static void main(String[] args) {
    List<Coffee> coffees =
      Stream.generate(new CoffeeSupplier())
        .limit(10)
        .collect(Collectors.toList());
    System.out.println(coffees);
  }
}
/* Output:
[Americano 0, Latte 1, Americano 2, Mocha 3, Mocha 4,
Breve 5, Americano 6, Latte 7, Cappuccino 8, Cappuccino
9]
*/
