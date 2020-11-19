// concurrent/CachedThreadPool3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class CachedThreadPool3 {
  public static Integer
  extractResult(Future<Integer> f) {
    try {
      return f.get();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
  public static void main(String[] args)
    throws InterruptedException {
    ExecutorService exec =
      Executors.newCachedThreadPool();
    List<CountingTask> tasks =
      IntStream.range(0, 10)
        .mapToObj(CountingTask::new)
        .collect(Collectors.toList());
    List<Future<Integer>> futures =
      exec.invokeAll(tasks);
    Integer sum = futures.stream()
      .map(CachedThreadPool3::extractResult)
      .reduce(0, Integer::sum);
    System.out.println("sum = " + sum);
    exec.shutdown();
  }
}
/* Output:
1 pool-1-thread-2 100
0 pool-1-thread-1 100
4 pool-1-thread-5 100
5 pool-1-thread-6 100
8 pool-1-thread-9 100
9 pool-1-thread-10 100
2 pool-1-thread-3 100
3 pool-1-thread-4 100
6 pool-1-thread-7 100
7 pool-1-thread-8 100
sum = 1000
*/
