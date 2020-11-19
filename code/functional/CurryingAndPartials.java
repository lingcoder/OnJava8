// functional/CurryingAndPartials.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;

public class CurryingAndPartials {
   // Uncurried:
   static String uncurried(String a, String b) {
      return a + b;
   }
   public static void main(String[] args) {
      // Curried function:
      Function<String, Function<String, String>> sum =
         a -> b -> a + b; // [1]

      System.out.println(uncurried("Hi ", "Ho"));

      Function<String, String>
        hi = sum.apply("Hi "); // [2]
      System.out.println(hi.apply("Ho"));

      // Partial application:
      Function<String, String> sumHi =
        sum.apply("Hup ");
      System.out.println(sumHi.apply("Ho"));
      System.out.println(sumHi.apply("Hey"));
   }
}
/* Output:
Hi Ho
Hi Ho
Hup Ho
Hup Hey
*/
