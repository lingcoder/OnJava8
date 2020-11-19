// generics/CreatorGeneric.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

abstract class GenericWithCreate<T> {
  final T element;
  GenericWithCreate() { element = create(); }
  abstract T create();
}

class X {}

class XCreator extends GenericWithCreate<X> {
  @Override
  X create() { return new X(); }
  void f() {
    System.out.println(
      element.getClass().getSimpleName());
  }
}

public class CreatorGeneric {
  public static void main(String[] args) {
    XCreator xc = new XCreator();
    xc.f();
  }
}
/* Output:
X
*/
