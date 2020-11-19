// typeinfo/InnerImplementation.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Private inner classes can't hide from reflection
import typeinfo.interfacea.*;

class InnerA {
  private static class C implements A {
    public void f() {
      System.out.println("public C.f()");
    }
    public void g() {
      System.out.println("public C.g()");
    }
    void u() {
      System.out.println("package C.u()");
    }
    protected void v() {
      System.out.println("protected C.v()");
    }
    private void w() {
      System.out.println("private C.w()");
    }
  }
  public static A makeA() { return new C(); }
}

public class InnerImplementation {
  public static void
  main(String[] args) throws Exception {
    A a = InnerA.makeA();
    a.f();
    System.out.println(a.getClass().getName());
    // Reflection still gets into the private class:
    HiddenImplementation.callHiddenMethod(a, "g");
    HiddenImplementation.callHiddenMethod(a, "u");
    HiddenImplementation.callHiddenMethod(a, "v");
    HiddenImplementation.callHiddenMethod(a, "w");
  }
}
/* Output:
public C.f()
InnerA$C
public C.g()
package C.u()
protected C.v()
private C.w()
*/
