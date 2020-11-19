// patterns/ShapeFactory3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Polymorphic factory methods
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import patterns.shapes.*;

interface PolymorphicFactory {
  Shape create();
}

class RandomShapes implements Supplier<Shape> {
  private final PolymorphicFactory[] factories;
  private Random rand = new Random(42);
  RandomShapes(PolymorphicFactory... factories) {
    this.factories = factories;
  }
  public Shape get() {
    return factories[
      rand.nextInt(factories.length)].create();
  }
}

public class ShapeFactory3 {
  public static void main(String[] args) {
    RandomShapes rs = new RandomShapes(
      Circle::new, Square::new, Triangle::new
    );
    Stream.generate(rs)
      .limit(6)
      .peek(Shape::draw)
      .peek(Shape::erase)
      .count();
  }
}
/* Output:
Triangle[0] draw
Triangle[0] erase
Circle[1] draw
Circle[1] erase
Circle[2] draw
Circle[2] erase
Triangle[3] draw
Triangle[3] erase
Circle[4] draw
Circle[4] erase
Square[5] draw
Square[5] erase
*/
