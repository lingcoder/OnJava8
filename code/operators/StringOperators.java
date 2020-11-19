// operators/StringOperators.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class StringOperators {
  public static void main(String[] args) {
    int x = 0, y = 1, z = 2;
    String s = "x, y, z ";
    System.out.println(s + x + y + z);
    // Converts x to a String:
    System.out.println(x + " " + s);
    s += "(summed) = "; // Concatenation operator
    System.out.println(s + (x + y + z));
    // Shorthand for Integer.toString():
    System.out.println("" + x);
  }
}
/* Output:
x, y, z 012
0 x, y, z
x, y, z (summed) = 3
0
*/
