// validating/SimpleDebugging.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {ThrowsException}

public class SimpleDebugging {
  private static void foo1() {
    System.out.println("In foo1");
    foo2();
  }
  private static void foo2() {
    System.out.println("In foo2");
    foo3();
  }
  private static void foo3() {
    System.out.println("In foo3");
    int j = 1;
    j--;
    int i = 5 / j;
  }
  public static void main(String[] args) {
    foo1();
  }
}
/* Output:
In foo1
In foo2
In foo3
___[ Error Output ]___
Exception in thread "main"
java.lang.ArithmeticException: / by zero
        at
SimpleDebugging.foo3(SimpleDebugging.java:17)
        at
SimpleDebugging.foo2(SimpleDebugging.java:11)
        at SimpleDebugging.foo1(SimpleDebugging.java:7)
        at
SimpleDebugging.main(SimpleDebugging.java:20)
*/
