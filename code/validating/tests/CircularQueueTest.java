// validating/tests/CircularQueueTest.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package validating;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CircularQueueTest {
  private CircularQueue queue = new CircularQueue(10);
  private int i = 0;
  @BeforeEach
  public void initialize() {
    while(i < 5) // Pre-load with some data
      queue.put(Integer.toString(i++));
  }
  // Support methods:
  private void showFullness() {
    assertTrue(queue.full());
    assertFalse(queue.empty());
    System.out.println(queue.dump());
  }
  private void showEmptiness() {
    assertFalse(queue.full());
    assertTrue(queue.empty());
    System.out.println(queue.dump());
  }
  @Test
  public void full() {
    System.out.println("testFull");
    System.out.println(queue.dump());
    System.out.println(queue.get());
    System.out.println(queue.get());
    while(!queue.full())
      queue.put(Integer.toString(i++));
    String msg = "";
    try {
      queue.put("");
    } catch(CircularQueueException e) {
      msg = e.getMessage();
      System.out.println(msg);
    }
    assertEquals(msg, "put() into full CircularQueue");
    showFullness();
  }
  @Test
  public void empty() {
    System.out.println("testEmpty");
    while(!queue.empty())
      System.out.println(queue.get());
    String msg = "";
    try {
      queue.get();
    } catch(CircularQueueException e) {
      msg = e.getMessage();
      System.out.println(msg);
    }
    assertEquals(msg, "get() from empty CircularQueue");
    showEmptiness();
  }
  @Test
  public void nullPut() {
    System.out.println("testNullPut");
    String msg = "";
    try {
      queue.put(null);
    } catch(CircularQueueException e) {
      msg = e.getMessage();
      System.out.println(msg);
    }
    assertEquals(msg, "put() null item");
  }
  @Test
  public void circularity() {
    System.out.println("testCircularity");
    while(!queue.full())
      queue.put(Integer.toString(i++));
    showFullness();
    assertTrue(queue.isWrapped());
    while(!queue.empty())
      System.out.println(queue.get());
    showEmptiness();
    while(!queue.full())
      queue.put(Integer.toString(i++));
    showFullness();
    while(!queue.empty())
      System.out.println(queue.get());
    showEmptiness();
  }
}
/* Output:
testNullPut
put() null item
testCircularity
in = 0, out = 0, full() = true, empty() = false,
CircularQueue =
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
0
1
2
3
4
5
6
7
8
9
in = 0, out = 0, full() = false, empty() = true,
CircularQueue =
[null, null, null, null, null, null, null, null, null,
null]
in = 0, out = 0, full() = true, empty() = false,
CircularQueue =
[10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
10
11
12
13
14
15
16
17
18
19
in = 0, out = 0, full() = false, empty() = true,
CircularQueue =
[null, null, null, null, null, null, null, null, null,
null]
testFull
in = 5, out = 0, full() = false, empty() = false,
CircularQueue =
[0, 1, 2, 3, 4, null, null, null, null, null]
0
1
put() into full CircularQueue
in = 2, out = 2, full() = true, empty() = false,
CircularQueue =
[10, 11, 2, 3, 4, 5, 6, 7, 8, 9]
testEmpty
0
1
2
3
4
get() from empty CircularQueue
in = 5, out = 5, full() = false, empty() = true,
CircularQueue =
[null, null, null, null, null, null, null, null, null,
null]
*/
