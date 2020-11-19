// annotations/AtUnitExample1.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java onjava.atunit.AtUnit
// build/classes/java/main/annotations/AtUnitExample1.class}
package annotations;
import onjava.atunit.*;
import onjava.*;

public class AtUnitExample1 {
  public String methodOne() {
    return "This is methodOne";
  }
  public int methodTwo() {
    System.out.println("This is methodTwo");
    return 2;
  }
  @Test
  boolean methodOneTest() {
    return methodOne().equals("This is methodOne");
  }
  @Test
  boolean m2() { return methodTwo() == 2; }
  @Test
  private boolean m3() { return true; }
  // Shows output for failure:
  @Test
  boolean failureTest() { return false; }
  @Test
  boolean anotherDisappointment() {
    return false;
  }
}
/* Output:
annotations.AtUnitExample1
  . m3
  . methodOneTest
  . m2 This is methodTwo

  . failureTest (failed)
  . anotherDisappointment (failed)
(5 tests)

>>> 2 FAILURES <<<
  annotations.AtUnitExample1: failureTest
  annotations.AtUnitExample1: anotherDisappointment
*/
