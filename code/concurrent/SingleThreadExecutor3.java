// concurrent/SingleThreadExecutor3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import java.util.stream.*;

public class SingleThreadExecutor3 {
  public static void main(String[] args)
    throws InterruptedException {
    ExecutorService exec =
      Executors.newSingleThreadExecutor();
    IntStream.range(0, 10)
      .mapToObj(InterferingTask::new)
      .forEach(exec::execute);
    exec.shutdown();
  }
}
/* Output:
0 pool-1-thread-1 100
1 pool-1-thread-1 200
2 pool-1-thread-1 300
3 pool-1-thread-1 400
4 pool-1-thread-1 500
5 pool-1-thread-1 600
6 pool-1-thread-1 700
7 pool-1-thread-1 800
8 pool-1-thread-1 900
9 pool-1-thread-1 1000
*/
