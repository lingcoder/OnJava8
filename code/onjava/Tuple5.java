// onjava/Tuple5.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package onjava;

public class Tuple5<A, B, C, D, E>
extends Tuple4<A, B, C, D> {
  public final E a5;
  public Tuple5(A a, B b, C c, D d, E e) {
    super(a, b, c, d);
    a5 = e;
  }
  @Override
  public String rep() {
    return super.rep() + ", " + a5;
  }
}
