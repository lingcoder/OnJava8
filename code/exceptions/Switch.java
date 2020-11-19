// exceptions/Switch.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class Switch {
  private boolean state = false;
  public boolean read() { return state; }
  public void on() {
    state = true;
    System.out.println(this);
  }
  public void off() {
    state = false;
    System.out.println(this);
  }
  @Override
  public String toString() {
    return state ? "on" : "off";
  }
}
