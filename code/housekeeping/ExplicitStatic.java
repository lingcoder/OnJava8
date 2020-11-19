// housekeeping/ExplicitStatic.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Explicit static initialization with "static" clause

class Cup {
  Cup(int marker) {
    System.out.println("Cup(" + marker + ")");
  }
  void f(int marker) {
    System.out.println("f(" + marker + ")");
  }
}

class Cups {
  static Cup cup1;
  static Cup cup2;
  static {
    cup1 = new Cup(1);
    cup2 = new Cup(2);
  }
  Cups() {
    System.out.println("Cups()");
  }
}

public class ExplicitStatic {
  public static void main(String[] args) {
    System.out.println("Inside main()");
    Cups.cup1.f(99);  // [1]
  }
  // static Cups cups1 = new Cups();  // [2]
  // static Cups cups2 = new Cups();  // [2]
}
/* Output:
Inside main()
Cup(1)
Cup(2)
f(99)
*/
