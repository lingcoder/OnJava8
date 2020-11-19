// interfaces/AnImplementation.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class AnImplementation implements AnInterface {
  public void firstMethod() {
    System.out.println("firstMethod");
  }
  public void secondMethod() {
    System.out.println("secondMethod");
  }
  public static void main(String[] args) {
    AnInterface i = new AnImplementation();
    i.firstMethod();
    i.secondMethod();
  }
}
/* Output:
firstMethod
secondMethod
*/
