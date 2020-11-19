// annotations/StackLStringTst.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Applying @Unit to generics
// {java onjava.atunit.AtUnit
// build/classes/java/main/annotations/StackLStringTst.class}
package annotations;
import onjava.atunit.*;
import onjava.*;

public class
StackLStringTst extends StackL<String> {
  @Test
  void tPush() {
    push("one");
    assert top().equals("one");
    push("two");
    assert top().equals("two");
  }
  @Test
  void tPop() {
    push("one");
    push("two");
    assert pop().equals("two");
    assert pop().equals("one");
  }
  @Test
  void tTop() {
    push("A");
    push("B");
    assert top().equals("B");
    assert top().equals("B");
  }
}
/* Output:
annotations.StackLStringTst
  . tTop
  . tPush
  . tPop
OK (3 tests)
*/
