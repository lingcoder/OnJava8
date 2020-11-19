// generics/Unconstrained.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

class Other {}
class BasicOther extends BasicHolder<Other> {}

public class Unconstrained {
  public static void main(String[] args) {
    BasicOther b = new BasicOther();
    BasicOther b2 = new BasicOther();
    b.set(new Other());
    Other other = b.get();
    b.f();
  }
}
/* Output:
Other
*/
