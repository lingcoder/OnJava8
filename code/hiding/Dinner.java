// hiding/Dinner.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Uses the library
import hiding.dessert.*;

public class Dinner {
  public static void main(String[] args) {
    Cookie x = new Cookie();
    //- x.bite(); // Can't access
  }
}
/* Output:
Cookie constructor
*/
