// lowlevel/NumberOfProcessors.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class NumberOfProcessors {
  public static void main(String[] args) {
    System.out.println(
      Runtime.getRuntime().availableProcessors());
  }
}
/* Output:
8
*/
