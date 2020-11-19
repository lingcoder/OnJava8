// operators/TernaryIfElse.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class TernaryIfElse {
  static int ternary(int i) {
    return i < 10 ? i * 100 : i * 10;
  }
  static int standardIfElse(int i) {
    if(i < 10)
      return i * 100;
    else
      return i * 10;
  }
  public static void main(String[] args) {
    System.out.println(ternary(9));
    System.out.println(ternary(10));
    System.out.println(standardIfElse(9));
    System.out.println(standardIfElse(10));
  }
}
/* Output:
900
100
900
100
*/
