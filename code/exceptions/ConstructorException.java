// exceptions/ConstructorException.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

class CE extends Exception {}

class SecondExcept extends Reporter {
  SecondExcept() throws CE {
    super();
    throw new CE();
  }
}

public class ConstructorException {
  public static void main(String[] args) {
    try(
      First f = new First();
      SecondExcept s = new SecondExcept();
      Second s2 = new Second()
    ) {
      System.out.println("In body");
    } catch(CE e) {
      System.out.println("Caught: " + e);
    }
  }
}
/* Output:
Creating First
Creating SecondExcept
Closing First
Caught: CE
*/
