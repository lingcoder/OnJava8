// concurrent/NapTask.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import onjava.Nap;

public class NapTask implements Runnable {
  final int id;
  public NapTask(int id) { this.id = id; }
  @Override
  public void run() {
    new Nap(0.1); // Seconds
    System.out.println(this + " " +
      Thread.currentThread().getName());
  }
  @Override
  public String toString() {
    return "NapTask[" + id + "]";
  }
}
