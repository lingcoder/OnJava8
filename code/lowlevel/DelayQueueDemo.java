// lowlevel/DelayQueueDemo.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import static java.util.concurrent.TimeUnit.*;

class DelayedTask implements Runnable, Delayed {
  private static int counter = 0;
  private final int id = counter++;
  private final int delta;
  private final long trigger;
  protected static List<DelayedTask> sequence =
    new ArrayList<>();
  DelayedTask(int delayInMilliseconds) {
    delta = delayInMilliseconds;
    trigger = System.nanoTime() +
      NANOSECONDS.convert(delta, MILLISECONDS);
    sequence.add(this);
  }
  @Override
  public long getDelay(TimeUnit unit) {
    return unit.convert(
      trigger - System.nanoTime(), NANOSECONDS);
  }
  @Override
  public int compareTo(Delayed arg) {
    DelayedTask that = (DelayedTask)arg;
    if(trigger < that.trigger) return -1;
    if(trigger > that.trigger) return 1;
    return 0;
  }
  @Override
  public void run() {
    System.out.print(this + " ");
  }
  @Override
  public String toString() {
    return
      String.format("[%d] Task %d", delta, id);
  }
  public String summary() {
    return String.format("(%d:%d)", id, delta);
  }
  public static class EndTask extends DelayedTask {
    EndTask(int delay) { super(delay); }
    @Override
    public void run() {
      sequence.forEach(dt ->
        System.out.println(dt.summary()));
    }
  }
}

public class DelayQueueDemo {
  public static void
  main(String[] args) throws Exception {
    DelayQueue<DelayedTask> tasks =
      Stream.concat( // Random delays:
        new Random(47).ints(20, 0, 4000)
          .mapToObj(DelayedTask::new),
        // Add the summarizing task:
        Stream.of(new DelayedTask.EndTask(4000)))
      .collect(Collectors
        .toCollection(DelayQueue::new));
    while(tasks.size() > 0)
      tasks.take().run();
  }
}
/* Output:
[128] Task 12 [429] Task 6 [551] Task 13 [555] Task 2
[693] Task 3 [809] Task 15 [961] Task 5 [1258] Task 1
[1258] Task 20 [1520] Task 19 [1861] Task 4 [1998] Task
17 [2200] Task 8 [2207] Task 10 [2288] Task 11 [2522]
Task 9 [2589] Task 14 [2861] Task 18 [2868] Task 7
[3278] Task 16 (0:4000)
(1:1258)
(2:555)
(3:693)
(4:1861)
(5:961)
(6:429)
(7:2868)
(8:2200)
(9:2522)
(10:2207)
(11:2288)
(12:128)
(13:551)
(14:2589)
(15:809)
(16:3278)
(17:1998)
(18:2861)
(19:1520)
(20:1258)
*/
