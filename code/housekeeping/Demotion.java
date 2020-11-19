// housekeeping/Demotion.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Demotion of primitives

public class Demotion {
  void f1(double x) {
    System.out.println("f1(double)");
  }
  void f2(float x) { System.out.println("f2(float)"); }
  void f3(long x) { System.out.println("f3(long)"); }
  void f4(int x) { System.out.println("f4(int)"); }
  void f5(short x) { System.out.println("f5(short)"); }
  void f6(byte x) { System.out.println("f6(byte)"); }
  void f7(char x) { System.out.println("f7(char)"); }

  void testDouble() {
    double x = 0;
    System.out.println("double argument:");
    f1(x);
    f2((float)x);
    f3((long)x);
    f4((int)x);
    f5((short)x);
    f6((byte)x);
    f7((char)x);
  }
  public static void main(String[] args) {
    Demotion p = new Demotion();
    p.testDouble();
  }
}
/* Output:
double argument:
f1(double)
f2(float)
f3(long)
f4(int)
f5(short)
f6(byte)
f7(char)
*/
