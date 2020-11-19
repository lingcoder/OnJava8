// lowlevel/WorkStealingPool.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.stream.*;
import java.util.concurrent.*;

class ShowThread implements Runnable {
  @Override
  public void run() {
    System.out.println(
      Thread.currentThread().getName());
  }
}

public class WorkStealingPool {
  public static void main(String[] args)
    throws InterruptedException {
    System.out.println(
      Runtime.getRuntime().availableProcessors());
    ExecutorService exec =
      Executors.newWorkStealingPool();
    IntStream.range(0, 10)
      .mapToObj(n -> new ShowThread())
      .forEach(exec::execute);
    exec.awaitTermination(1, TimeUnit.SECONDS);
  }
}
/* Output:
8
ForkJoinPool-1-worker-2
ForkJoinPool-1-worker-1
ForkJoinPool-1-worker-2
ForkJoinPool-1-worker-3
ForkJoinPool-1-worker-2
ForkJoinPool-1-worker-1
ForkJoinPool-1-worker-3
ForkJoinPool-1-worker-1
ForkJoinPool-1-worker-4
ForkJoinPool-1-worker-2
*/
