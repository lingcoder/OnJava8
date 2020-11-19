// exceptions/MultiCatch2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class MultiCatch2 {
  void x() throws Except1, Except2, Except3, Except4 {}
  void process1() {}
  void process2() {}
  void f() {
    try {
      x();
    } catch(Except1 | Except2 e) {
      process1();
    } catch(Except3 | Except4 e) {
      process2();
    }
  }
}
