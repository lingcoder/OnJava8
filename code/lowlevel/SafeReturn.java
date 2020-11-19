// lowlevel/SafeReturn.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;
import java.util.concurrent.*;

public class SafeReturn extends IntTestable {
  private int i = 0;
  public synchronized int getAsInt() { return i; }
  public synchronized void evenIncrement() {
    i++; i++;
  }
  public static void main(String[] args) {
    Atomicity.test(new SafeReturn());
  }
}
/* Output:
No failures found
*/
