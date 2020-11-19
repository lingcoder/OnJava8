// lowlevel/PriorityBlockingQueueDemo.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import onjava.Nap;

class Prioritized implements Comparable<Prioritized>  {
  private static AtomicInteger counter =
    new AtomicInteger();
  private final int id = counter.getAndIncrement();
  private final int priority;
  private static List<Prioritized> sequence =
    new CopyOnWriteArrayList<>();
  Prioritized(int priority) {
    this.priority = priority;
    sequence.add(this);
  }
  @Override
  public int compareTo(Prioritized arg) {
    return priority < arg.priority ? 1 :
      (priority > arg.priority ? -1 : 0);
  }
  @Override
  public String toString() {
    return String.format(
      "[%d] Prioritized %d", priority, id);
  }
  public void displaySequence() {
    int count = 0;
    for(Prioritized pt : sequence) {
      System.out.printf("(%d:%d)", pt.id, pt.priority);
      if(++count % 5 == 0)
        System.out.println();
    }
  }
  public static class EndSentinel extends Prioritized {
    EndSentinel() { super(-1); }
  }
}

class Producer implements Runnable {
  private static AtomicInteger seed =
    new AtomicInteger(47);
  private SplittableRandom rand =
    new SplittableRandom(seed.getAndAdd(10));
  private Queue<Prioritized> queue;
  Producer(Queue<Prioritized> q) {
    queue = q;
  }
  @Override
  public void run() {
    rand.ints(10, 0, 20)
      .mapToObj(Prioritized::new)
      .peek(p -> new Nap(rand.nextDouble() / 10))
      .forEach(p -> queue.add(p));
    queue.add(new Prioritized.EndSentinel());
  }
}

class Consumer implements Runnable {
  private PriorityBlockingQueue<Prioritized> q;
  private SplittableRandom rand =
    new SplittableRandom(47);
  Consumer(PriorityBlockingQueue<Prioritized> q) {
    this.q = q;
  }
  @Override
  public void run() {
    while(true) {
      try {
        Prioritized pt = q.take();
        System.out.println(pt);
        if(pt instanceof Prioritized.EndSentinel) {
          pt.displaySequence();
          break;
        }
        new Nap(rand.nextDouble() / 10);
      } catch(InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}

public class PriorityBlockingQueueDemo {
  public static void main(String[] args) {
    PriorityBlockingQueue<Prioritized> queue =
      new PriorityBlockingQueue<>();
    CompletableFuture.runAsync(new Producer(queue));
    CompletableFuture.runAsync(new Producer(queue));
    CompletableFuture.runAsync(new Producer(queue));
    CompletableFuture.runAsync(new Consumer(queue))
      .join();
  }
}
/* Output:
[15] Prioritized 2
[17] Prioritized 1
[17] Prioritized 5
[16] Prioritized 6
[14] Prioritized 9
[12] Prioritized 0
[11] Prioritized 4
[11] Prioritized 12
[13] Prioritized 13
[12] Prioritized 16
[14] Prioritized 18
[15] Prioritized 23
[18] Prioritized 26
[16] Prioritized 29
[12] Prioritized 17
[11] Prioritized 30
[11] Prioritized 24
[10] Prioritized 15
[10] Prioritized 22
[8] Prioritized 25
[8] Prioritized 11
[8] Prioritized 10
[6] Prioritized 31
[3] Prioritized 7
[2] Prioritized 20
[1] Prioritized 3
[0] Prioritized 19
[0] Prioritized 8
[0] Prioritized 14
[0] Prioritized 21
[-1] Prioritized 28
(0:12)(2:15)(1:17)(3:1)(4:11)
(5:17)(6:16)(7:3)(8:0)(9:14)
(10:8)(11:8)(12:11)(13:13)(14:0)
(15:10)(16:12)(17:12)(18:14)(19:0)
(20:2)(21:0)(22:10)(23:15)(24:11)
(25:8)(26:18)(27:-1)(28:-1)(29:16)
(30:11)(31:6)(32:-1)
*/
