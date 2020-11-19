// polymorphism/shape/RandomShapes.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A "factory" that randomly creates shapes
package polymorphism.shape;
import java.util.*;

public class RandomShapes {
  private Random rand = new Random(47);
  public Shape get() {
    switch(rand.nextInt(3)) {
      default:
      case 0: return new Circle();
      case 1: return new Square();
      case 2: return new Triangle();
    }
  }
  public Shape[] array(int sz) {
    Shape[] shapes = new Shape[sz];
    // Fill up the array with shapes:
    for(int i = 0; i < shapes.length; i++)
      shapes[i] = get();
    return shapes;
  }
}
