// concurrent/SingleThreadExecutor2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import java.util.stream.*;

public class SingleThreadExecutor2 {
  public static void main(String[] args)
    throws InterruptedException {
    ExecutorService exec =
      Executors.newSingleThreadExecutor();
    IntStream.range(0, 10)
      .mapToObj(NapTask::new)
      .forEach(exec::execute);
    exec.shutdown();
  }
}
/* Output:
NapTask[0] pool-1-thread-1
NapTask[1] pool-1-thread-1
NapTask[2] pool-1-thread-1
NapTask[3] pool-1-thread-1
NapTask[4] pool-1-thread-1
NapTask[5] pool-1-thread-1
NapTask[6] pool-1-thread-1
NapTask[7] pool-1-thread-1
NapTask[8] pool-1-thread-1
NapTask[9] pool-1-thread-1
*/
