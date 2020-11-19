// concurrent/QuittableTask.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.atomic.AtomicBoolean;
import onjava.Nap;

public class QuittableTask implements Runnable {
  final int id;
  public QuittableTask(int id) { this.id = id; }
  private AtomicBoolean running =
    new AtomicBoolean(true);
  public void quit() { running.set(false); }
  @Override
  public void run() {
    while(running.get())         // [1]
      new Nap(0.1);
    System.out.print(id + " ");  // [2]
  }
}
