// generics/ApplyTest.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.function.*;
import onjava.*;

public class ApplyTest {
  public static
  void main(String[] args) throws Exception {
    List<Shape> shapes =
      Suppliers.create(ArrayList::new, Shape::new, 3);
    Apply.apply(shapes,
      Shape.class.getMethod("rotate"));
    Apply.apply(shapes,
      Shape.class.getMethod("resize", int.class), 7);

    List<Square> squares =
      Suppliers.create(ArrayList::new, Square::new, 3);
    Apply.apply(squares,
      Shape.class.getMethod("rotate"));
    Apply.apply(squares,
      Shape.class.getMethod("resize", int.class), 7);

    Apply.apply(new FilledList<>(Shape::new, 3),
      Shape.class.getMethod("rotate"));
    Apply.apply(new FilledList<>(Square::new, 3),
      Shape.class.getMethod("rotate"));

    SimpleQueue<Shape> shapeQ = Suppliers.fill(
      new SimpleQueue<>(), SimpleQueue::add,
      Shape::new, 3);
    Suppliers.fill(shapeQ, SimpleQueue::add,
      Square::new, 3);
    Apply.apply(shapeQ,
      Shape.class.getMethod("rotate"));
  }
}
/* Output:
Shape 0 rotate
Shape 1 rotate
Shape 2 rotate
Shape 0 resize 7
Shape 1 resize 7
Shape 2 resize 7
Square 3 rotate
Square 4 rotate
Square 5 rotate
Square 3 resize 7
Square 4 resize 7
Square 5 resize 7
Shape 6 rotate
Shape 7 rotate
Shape 8 rotate
Square 9 rotate
Square 10 rotate
Square 11 rotate
Shape 12 rotate
Shape 13 rotate
Shape 14 rotate
Square 15 rotate
Square 16 rotate
Square 17 rotate
*/
