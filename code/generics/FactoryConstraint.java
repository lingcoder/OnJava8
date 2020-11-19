// generics/FactoryConstraint.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.function.*;
import onjava.*;

class IntegerFactory implements Supplier<Integer> {
  private int i = 0;
  @Override
  public Integer get() {
    return ++i;
  }
}

class Widget {
  private int id;
  Widget(int n) { id = n; }
  @Override
  public String toString() {
    return "Widget " + id;
  }
  public static
  class Factory implements Supplier<Widget> {
    private int i = 0;
    @Override
    public Widget get() { return new Widget(++i); }
  }
}

class Fudge {
  private static int count = 1;
  private int n = count++;
  @Override
  public String toString() { return "Fudge " + n; }
}

class Foo2<T> {
  private List<T> x = new ArrayList<>();
  Foo2(Supplier<T> factory) {
    Suppliers.fill(x, factory, 5);
  }
  @Override
  public String toString() { return x.toString(); }
}

public class FactoryConstraint {
  public static void main(String[] args) {
    System.out.println(
      new Foo2<>(new IntegerFactory()));
    System.out.println(
      new Foo2<>(new Widget.Factory()));
    System.out.println(
      new Foo2<>(Fudge::new));
  }
}
/* Output:
[1, 2, 3, 4, 5]
[Widget 1, Widget 2, Widget 3, Widget 4, Widget 5]
[Fudge 1, Fudge 2, Fudge 3, Fudge 4, Fudge 5]
*/
