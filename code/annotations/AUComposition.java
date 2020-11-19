// annotations/AUComposition.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Creating non-embedded tests
// {java onjava.atunit.AtUnit
// build/classes/java/main/annotations/AUComposition.class}
package annotations;
import onjava.atunit.*;
import onjava.*;

public class AUComposition {
  AtUnitExample1 testObject = new AtUnitExample1();
  @Test
  boolean tMethodOne() {
    return testObject.methodOne()
      .equals("This is methodOne");
  }
  @Test
  boolean tMethodTwo() {
    return testObject.methodTwo() == 2;
  }
}
/* Output:
annotations.AUComposition
  . tMethodTwo This is methodTwo

  . tMethodOne
OK (2 tests)
*/
