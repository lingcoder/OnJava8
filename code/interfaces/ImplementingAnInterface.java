// interfaces/ImplementingAnInterface.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

interface Concept { // Package access
  void idea1();
  void idea2();
}

class Implementation implements Concept {
  public void idea1() { System.out.println("idea1"); }
  public void idea2() { System.out.println("idea2"); }
}
