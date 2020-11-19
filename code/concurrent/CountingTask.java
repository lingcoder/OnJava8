// concurrent/CountingTask.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;

public class CountingTask implements Callable<Integer> {
  final int id;
  public CountingTask(int id) { this.id = id; }
  @Override
  public Integer call() {
    Integer val = 0;
    for(int i = 0; i < 100; i++)
      val++;
    System.out.println(id + " " +
      Thread.currentThread().getName() + " " + val);
    return val;
  }
}
