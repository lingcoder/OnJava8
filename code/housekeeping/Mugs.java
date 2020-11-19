// housekeeping/Mugs.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Instance initialization

class Mug {
  Mug(int marker) {
    System.out.println("Mug(" + marker + ")");
  }
}

public class Mugs {
  Mug mug1;
  Mug mug2;
  { // [1]
    mug1 = new Mug(1);
    mug2 = new Mug(2);
    System.out.println("mug1 & mug2 initialized");
  }
  Mugs() {
    System.out.println("Mugs()");
  }
  Mugs(int i) {
    System.out.println("Mugs(int)");
  }
  public static void main(String[] args) {
    System.out.println("Inside main()");
    new Mugs();
    System.out.println("new Mugs() completed");
    new Mugs(1);
    System.out.println("new Mugs(1) completed");
  }
}
/* Output:
Inside main()
Mug(1)
Mug(2)
mug1 & mug2 initialized
Mugs()
new Mugs() completed
Mug(1)
Mug(2)
mug1 & mug2 initialized
Mugs(int)
new Mugs(1) completed
*/
