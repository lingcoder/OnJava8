// patterns/TemplateMethod.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Simple demonstration of Template Method
import java.util.stream.*;

abstract class ApplicationFramework {
  ApplicationFramework() {
    templateMethod();
  }
  abstract void customize1();
  abstract void customize2();
  // "private" means automatically "final":
  private void templateMethod() {
    IntStream.range(0, 5).forEach(
      n -> { customize1(); customize2(); });
  }
}

// Create a new "application":
class MyApp extends ApplicationFramework {
  @Override
  void customize1() {
    System.out.print("Hello ");
  }
  @Override
  void customize2() {
    System.out.println("World!");
  }
}

public class TemplateMethod {
  public static void main(String[] args) {
    new MyApp();
  }
}
/* Output:
Hello World!
Hello World!
Hello World!
Hello World!
Hello World!
*/
