// housekeeping/AutoboxingVarargs.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class AutoboxingVarargs {
  public static void f(Integer... args) {
    for(Integer i : args)
      System.out.print(i + " ");
    System.out.println();
  }
  public static void main(String[] args) {
    f(1, 2);
    f(4, 5, 6, 7, 8, 9);
    f(10, 11, 12);
  }
}
/* Output:
1 2
4 5 6 7 8 9
10 11 12
*/
