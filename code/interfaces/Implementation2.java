// interfaces/Implementation2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class Implementation2
implements InterfaceWithDefault {
  public void firstMethod() {
    System.out.println("firstMethod");
  }
  public void secondMethod() {
    System.out.println("secondMethod");
  }
  public static void main(String[] args) {
    InterfaceWithDefault i =
      new Implementation2();
    i.firstMethod();
    i.secondMethod();
    i.newMethod();
  }
}
/* Output:
firstMethod
secondMethod
newMethod
*/
