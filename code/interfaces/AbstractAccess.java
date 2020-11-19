// interfaces/AbstractAccess.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

abstract class AbstractAccess {
  private void m1() {}
  // private abstract void m1a(); // illegal
  protected void m2() {}
  protected abstract void m2a();
  void m3() {}
  abstract void m3a();
  public void m4() {}
  public abstract void m4a();
}
