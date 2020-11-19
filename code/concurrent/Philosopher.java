// concurrent/Philosopher.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class Philosopher implements Runnable {
  private final int seat;
  private final StickHolder left, right;
  public Philosopher(int seat,
    StickHolder left, StickHolder right) {
    this.seat = seat;
    this.left = left;
    this.right = right;
  }
  @Override
  public String toString() {
    return "P" + seat;
  }
  @Override
  public void run() {
    while(true) {
      // System.out.println("Thinking");   // [1]
      right.pickUp();
      left.pickUp();
      System.out.println(this + " eating");
      right.putDown();
      left.putDown();
    }
  }
}
