// annotations/AtUnitExample2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Assertions and exceptions can be used in @Tests
// {java onjava.atunit.AtUnit
// build/classes/java/main/annotations/AtUnitExample2.class}
package annotations;
import java.io.*;
import onjava.atunit.*;
import onjava.*;

public class AtUnitExample2 {
  public String methodOne() {
    return "This is methodOne";
  }
  public int methodTwo() {
    System.out.println("This is methodTwo");
    return 2;
  }
  @Test
  void assertExample() {
    assert methodOne().equals("This is methodOne");
  }
  @Test
  void assertFailureExample() {
    assert 1 == 2: "What a surprise!";
  }
  @Test
  void exceptionExample() throws IOException {
    try(FileInputStream fis =
        new FileInputStream("nofile.txt")) {} // Throws
  }
  @Test
  boolean assertAndReturn() {
    // Assertion with message:
    assert methodTwo() == 2: "methodTwo must equal 2";
    return methodOne().equals("This is methodOne");
  }
}
/* Output:
annotations.AtUnitExample2
  . exceptionExample java.io.FileNotFoundException:
nofile.txt (The system cannot find the file specified)
(failed)
  . assertExample
  . assertAndReturn This is methodTwo

  . assertFailureExample java.lang.AssertionError: What
a surprise!
(failed)
(4 tests)

>>> 2 FAILURES <<<
  annotations.AtUnitExample2: exceptionExample
  annotations.AtUnitExample2: assertFailureExample
*/
