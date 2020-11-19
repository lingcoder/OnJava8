// annotations/AUExternalTest.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Creating non-embedded tests
// {java onjava.atunit.AtUnit
// build/classes/java/main/annotations/AUExternalTest.class}
package annotations;
import onjava.atunit.*;
import onjava.*;

public class
AUExternalTest extends AtUnitExample1 {
  @Test
  boolean tMethodOne() {
    return methodOne().equals("This is methodOne");
  }
  @Test
  boolean tMethodTwo() {
    return methodTwo() == 2;
  }
}
/* Output:
annotations.AUExternalTest
  . tMethodOne
  . tMethodTwo This is methodTwo

OK (2 tests)
*/
