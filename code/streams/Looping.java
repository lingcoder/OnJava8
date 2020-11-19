// streams/Looping.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import static onjava.Repeat.*;

public class Looping {
  static void hi() { System.out.println("Hi!"); }
  public static void main(String[] args) {
    repeat(3, () -> System.out.println("Looping!"));
    repeat(2, Looping::hi);
  }
}
/* Output:
Looping!
Looping!
Looping!
Hi!
Hi!
*/
