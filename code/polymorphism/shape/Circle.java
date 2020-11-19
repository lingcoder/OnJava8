// polymorphism/shape/Circle.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package polymorphism.shape;

public class Circle extends Shape {
  @Override
  public void draw() {
    System.out.println("Circle.draw()");
  }
  @Override
  public void erase() {
    System.out.println("Circle.erase()");
  }
}
