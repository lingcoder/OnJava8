// enums/OverrideConstantSpecific.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public enum OverrideConstantSpecific {
  NUT, BOLT,
  WASHER {
    @Override
    void f() {
      System.out.println("Overridden method");
    }
  };
  void f() {
    System.out.println("default behavior");
  }
  public static void main(String[] args) {
    for(OverrideConstantSpecific ocs : values()) {
      System.out.print(ocs + ": ");
      ocs.f();
    }
  }
}
/* Output:
NUT: default behavior
BOLT: default behavior
WASHER: Overridden method
*/
