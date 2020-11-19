// hiding/Cake.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Accesses a class in a separate compilation unit

class Cake {
  public static void main(String[] args) {
    Pie x = new Pie();
    x.f();
  }
}
/* Output:
Pie.f()
*/
