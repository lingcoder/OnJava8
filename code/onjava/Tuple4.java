// onjava/Tuple4.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package onjava;

public class Tuple4<A, B, C, D>
  extends Tuple3<A, B, C> {
  public final D a4;
  public Tuple4(A a, B b, C c, D d) {
    super(a, b, c);
    a4 = d;
  }
  @Override
  public String rep() {
    return super.rep() + ", " + a4;
  }
}
