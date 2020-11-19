// annotations/simplest/SimpleTest.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Test the "Simple" annotation
// {java annotations.simplest.SimpleTest}
package annotations.simplest;

@Simple
public class SimpleTest {
  @Simple
  int i;
  @Simple
  public SimpleTest() {}
  @Simple
  public void foo() {
    System.out.println("SimpleTest.foo()");
  }
  @Simple
  public void bar(String s, int i, float f) {
    System.out.println("SimpleTest.bar()");
  }
  @Simple
  public static void main(String[] args) {
    @Simple
    SimpleTest st = new SimpleTest();
    st.foo();
  }
}
/* Output:
SimpleTest.foo()
*/
