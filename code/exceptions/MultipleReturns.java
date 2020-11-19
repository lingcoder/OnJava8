// exceptions/MultipleReturns.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class MultipleReturns {
  public static void f(int i) {
    System.out.println(
      "Initialization that requires cleanup");
    try {
      System.out.println("Point 1");
      if(i == 1) return;
      System.out.println("Point 2");
      if(i == 2) return;
      System.out.println("Point 3");
      if(i == 3) return;
      System.out.println("End");
      return;
    } finally {
      System.out.println("Performing cleanup");
    }
  }
  public static void main(String[] args) {
    for(int i = 1; i <= 4; i++)
      f(i);
  }
}
/* Output:
Initialization that requires cleanup
Point 1
Performing cleanup
Initialization that requires cleanup
Point 1
Point 2
Performing cleanup
Initialization that requires cleanup
Point 1
Point 2
Point 3
Performing cleanup
Initialization that requires cleanup
Point 1
Point 2
Point 3
End
Performing cleanup
*/
