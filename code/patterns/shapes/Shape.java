// patterns/shapes/Shape.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package patterns.shapes;

public class Shape {
  private static int counter = 0;
  private int id = counter++;
  @Override
  public String toString() {
    return
      getClass().getSimpleName() + "[" + id + "]";
  }
  public void draw() {
    System.out.println(this + " draw");
  }
  public void erase() {
    System.out.println(this + " erase");
  }
}
