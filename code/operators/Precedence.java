// operators/Precedence.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class Precedence {
  public static void main(String[] args) {
    int x = 1, y = 2, z = 3;
    int a = x + y - 2/2 + z;           // [1]
    int b = x + (y - 2)/(2 + z);       // [2]
    System.out.println("a = " + a);
    System.out.println("b = " + b);
  }
}
/* Output:
a = 5
b = 1
*/
