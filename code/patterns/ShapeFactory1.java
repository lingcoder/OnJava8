// patterns/ShapeFactory1.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A simple static factory method
import java.util.*;
import java.util.stream.*;
import patterns.shapes.*;

public class ShapeFactory1 implements FactoryMethod {
  public Shape create(String type) {
    switch(type) {
      case "Circle": return new Circle();
      case "Square": return new Square();
      case "Triangle": return new Triangle();
      default:
        throw new BadShapeCreation(type);
    }
  }
  public static void main(String[] args) {
    FactoryTest.test(new ShapeFactory1());
  }
}
/* Output:
Circle[0] draw
Circle[0] erase
Square[1] draw
Square[1] erase
Triangle[2] draw
Triangle[2] erase
Square[3] draw
Square[3] erase
Circle[4] draw
Circle[4] erase
Circle[5] draw
Circle[5] erase
Triangle[6] draw
Triangle[6] erase
*/
