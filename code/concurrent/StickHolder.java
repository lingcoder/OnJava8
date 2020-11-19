// concurrent/StickHolder.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;

public class StickHolder {
  private static class Chopstick {}
  private Chopstick stick = new Chopstick();
  private BlockingQueue<Chopstick> holder =
    new ArrayBlockingQueue<>(1);
  public StickHolder() { putDown(); }
  public void pickUp() {
    try {
      holder.take(); // Blocks if unavailable
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
  public void putDown() {
    try {
      holder.put(stick);
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
