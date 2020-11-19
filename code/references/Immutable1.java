// references/Immutable1.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Immutable objects are immune to aliasing

public class Immutable1 {
  private int data;
  public Immutable1(int initVal) {
    data = initVal;
  }
  public int read() { return data; }
  public boolean nonzero() { return data != 0; }
  public Immutable1 multiply(int multiplier) {
    return new Immutable1(data * multiplier);
  }
  public static void f(Immutable1 i1) {
    Immutable1 quad = i1.multiply(4);
    System.out.println("i1 = " + i1.read());
    System.out.println("quad = " + quad.read());
  }
  public static void main(String[] args) {
    Immutable1 x = new Immutable1(47);
    System.out.println("x = " + x.read());
    f(x);
    System.out.println("x = " + x.read());
  }
}
/* Output:
x = 47
i1 = 47
quad = 188
x = 47
*/
