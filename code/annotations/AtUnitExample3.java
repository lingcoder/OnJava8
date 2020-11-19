// annotations/AtUnitExample3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java onjava.atunit.AtUnit
// build/classes/java/main/annotations/AtUnitExample3.class}
package annotations;
import onjava.atunit.*;
import onjava.*;

public class AtUnitExample3 {
  private int n;
  public AtUnitExample3(int n) { this.n = n; }
  public int getN() { return n; }
  public String methodOne() {
    return "This is methodOne";
  }
  public int methodTwo() {
    System.out.println("This is methodTwo");
    return 2;
  }
  @TestObjectCreate
  static AtUnitExample3 create() {
    return new AtUnitExample3(47);
  }
  @Test
  boolean initialization() { return n == 47; }
  @Test
  boolean methodOneTest() {
    return methodOne().equals("This is methodOne");
  }
  @Test
  boolean m2() { return methodTwo() == 2; }
}
/* Output:
annotations.AtUnitExample3
  . initialization
  . m2 This is methodTwo

  . methodOneTest
OK (3 tests)
*/
