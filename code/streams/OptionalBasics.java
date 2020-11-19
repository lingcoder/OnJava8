// streams/OptionalBasics.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

class OptionalBasics {
  static void test(Optional<String> optString) {
    if(optString.isPresent())
      System.out.println(optString.get());
    else
      System.out.println("Nothing inside!");
  }
  public static void main(String[] args) {
    test(Stream.of("Epithets").findFirst());
    test(Stream.<String>empty().findFirst());
  }
}
/* Output:
Epithets
Nothing inside!
*/
