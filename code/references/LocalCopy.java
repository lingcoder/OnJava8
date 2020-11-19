// references/LocalCopy.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Creating local copies with clone()

class Duplo implements Cloneable {
  private int n;
  Duplo(int n) { this.n = n; }
  @Override
  public Duplo clone() { // [1]
    try {
      return (Duplo)super.clone();
    } catch(CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
  public int getValue() { return n; }
  public void setValue(int n) { this.n = n; }
  public void increment() { n++; }
  @Override
  public String toString() {
    return Integer.toString(n);
  }
}

public class LocalCopy {
  public static Duplo g(Duplo v) {
    // Passing a reference, modifies outside object:
    v.increment();
    return v;
  }
  public static Duplo f(Duplo v) {
    v = v.clone(); // [2] Local copy
    v.increment();
    return v;
  }
  public static void main(String[] args) {
    Duplo a = new Duplo(11);
    Duplo b = g(a);
    // Reference equivalence, not object equivalence:
    System.out.println("a == b: " + (a == b) +
      "\na = " + a + "\nb = " + b);
    Duplo c = new Duplo(47);
    Duplo d = f(c);
    System.out.println("c == d: " + (c == d) +
      "\nc = " + c + "\nd = " + d);
  }
}
/* Output:
a == b: true
a = 12
b = 12
c == d: false
c = 47
d = 48
*/
