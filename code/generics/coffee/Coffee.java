// generics/coffee/Coffee.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package generics.coffee;

public class Coffee {
  private static long counter = 0;
  private final long id = counter++;
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + id;
  }
}
