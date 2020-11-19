// patterns/shapes/FactoryTest.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package patterns.shapes;
import java.util.stream.*;

public class FactoryTest {
  public static void test(FactoryMethod factory) {
    Stream.of("Circle", "Square", "Triangle",
      "Square", "Circle", "Circle", "Triangle")
      .map(factory::create)
      .peek(Shape::draw)
      .peek(Shape::erase)
      .count(); // Terminal operation
  }
}
