// concurrent/OnePizza.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import onjava.Timer;

public class OnePizza {
  public static void main(String[] args) {
    Pizza za = new Pizza(0);
    System.out.println(
      Timer.duration(() -> {
        while(!za.complete())
          za.next();
      }));
  }
}
/* Output:
Pizza 0: ROLLED
Pizza 0: SAUCED
Pizza 0: CHEESED
Pizza 0: TOPPED
Pizza 0: BAKED
Pizza 0: SLICED
Pizza 0: BOXED
1622
*/
