// lowlevel/AtomicIntegerTest.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;
import onjava.*;

public class AtomicIntegerTest extends IntTestable {
  private AtomicInteger i = new AtomicInteger(0);
  public int getAsInt() { return i.get(); }
  public void evenIncrement() { i.addAndGet(2); }
  public static void main(String[] args) {
    Atomicity.test(new AtomicIntegerTest());
  }
}
/* Output:
No failures found
*/
