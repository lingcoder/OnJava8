// exceptions/BodyException.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

class Third extends Reporter {}

public class BodyException {
  public static void main(String[] args) {
    try(
      First f = new First();
      Second s2 = new Second()
    ) {
      System.out.println("In body");
      Third t = new Third();
      new SecondExcept();
      System.out.println("End of body");
    } catch(CE e) {
      System.out.println("Caught: " + e);
    }
  }
}
/* Output:
Creating First
Creating Second
In body
Creating Third
Creating SecondExcept
Closing Second
Closing First
Caught: CE
*/
