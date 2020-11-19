// concurrent/SingleThreadExecutor.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import java.util.stream.*;
import onjava.*;

public class SingleThreadExecutor {
  public static void main(String[] args) {
    ExecutorService exec =
      Executors.newSingleThreadExecutor();
    IntStream.range(0, 10)
      .mapToObj(NapTask::new)
      .forEach(exec::execute);
    System.out.println("All tasks submitted");
    exec.shutdown();
    while(!exec.isTerminated()) {
      System.out.println(
        Thread.currentThread().getName() +
        " awaiting termination");
      new Nap(0.1);
    }
  }
}
/* Output:
All tasks submitted
main awaiting termination
main awaiting termination
NapTask[0] pool-1-thread-1
main awaiting termination
NapTask[1] pool-1-thread-1
main awaiting termination
NapTask[2] pool-1-thread-1
main awaiting termination
NapTask[3] pool-1-thread-1
main awaiting termination
NapTask[4] pool-1-thread-1
main awaiting termination
NapTask[5] pool-1-thread-1
main awaiting termination
NapTask[6] pool-1-thread-1
main awaiting termination
NapTask[7] pool-1-thread-1
main awaiting termination
NapTask[8] pool-1-thread-1
main awaiting termination
NapTask[9] pool-1-thread-1
*/
