// lowlevel/TestAbort.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import onjava.*;

public class TestAbort {
  public static void main(String[] args) {
    new TimedAbort(1);
    System.out.println("Napping for 4");
    new Nap(4);
  }
}
/* Output:
Napping for 4
TimedAbort 1.0
*/
