// generics/Shape.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class Shape {
  private static long counter = 0;
  private final long id = counter++;
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + id;
  }
  public void rotate() {
    System.out.println(this + " rotate");
  }
  public void resize(int newSize) {
    System.out.println(this + " resize " + newSize);
  }
}
