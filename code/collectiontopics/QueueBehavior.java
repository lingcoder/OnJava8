// collectiontopics/QueueBehavior.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Compares basic behavior
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

public class QueueBehavior {
  static Stream<String> strings() {
    return Arrays.stream(
      ("one two three four five six seven " +
       "eight nine ten").split(" "));
  }
  static void test(int id, Queue<String> queue) {
    System.out.print(id + ": ");
    strings().map(queue::offer).count();
    while(queue.peek() != null)
      System.out.print(queue.remove() + " ");
    System.out.println();
  }
  public static void main(String[] args) {
    int count = 10;
    test(1, new LinkedList<>());
    test(2, new PriorityQueue<>());
    test(3, new ArrayBlockingQueue<>(count));
    test(4, new ConcurrentLinkedQueue<>());
    test(5, new LinkedBlockingQueue<>());
    test(6, new PriorityBlockingQueue<>());
    test(7, new ArrayDeque<>());
    test(8, new ConcurrentLinkedDeque<>());
    test(9, new LinkedBlockingDeque<>());
    test(10, new LinkedTransferQueue<>());
    test(11, new SynchronousQueue<>());
  }
}
/* Output:
1: one two three four five six seven eight nine ten
2: eight five four nine one seven six ten three two
3: one two three four five six seven eight nine ten
4: one two three four five six seven eight nine ten
5: one two three four five six seven eight nine ten
6: eight five four nine one seven six ten three two
7: one two three four five six seven eight nine ten
8: one two three four five six seven eight nine ten
9: one two three four five six seven eight nine ten
10: one two three four five six seven eight nine ten
11:
*/
