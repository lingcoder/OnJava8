// housekeeping/OverloadingOrder.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Overloading based on the order of the arguments

public class OverloadingOrder {
  static void f(String s, int i) {
    System.out.println("String: " + s + ", int: " + i);
  }
  static void f(int i, String s) {
    System.out.println("int: " + i + ", String: " + s);
  }
  public static void main(String[] args) {
    f("String first", 11);
    f(99, "Int first");
  }
}
/* Output:
String: String first, int: 11
int: 99, String: Int first
*/
