// concurrent/MoreTasksAfterShutdown.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;

public class MoreTasksAfterShutdown {
  public static void main(String[] args) {
    ExecutorService exec =
      Executors.newSingleThreadExecutor();
    exec.execute(new NapTask(1));
    exec.shutdown();
    try {
      exec.execute(new NapTask(99));
    } catch(RejectedExecutionException e) {
      System.out.println(e);
    }
  }
}
/* Output:
java.util.concurrent.RejectedExecutionException: Task
NapTask[99] rejected from java.util.concurrent.ThreadPo
olExecutor@4e25154f[Shutting down, pool size = 1,
active threads = 1, queued tasks = 0, completed tasks =
0]
NapTask[1] pool-1-thread-1
*/
