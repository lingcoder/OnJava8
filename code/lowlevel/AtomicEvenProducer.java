// lowlevel/AtomicEvenProducer.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Atomic classes: occasionally useful in regular code
import java.util.concurrent.atomic.*;

public class AtomicEvenProducer extends IntGenerator {
  private AtomicInteger currentEvenValue =
    new AtomicInteger(0);
  @Override
  public int next() {
    return currentEvenValue.addAndGet(2);
  }
  public static void main(String[] args) {
    EvenChecker.test(new AtomicEvenProducer());
  }
}
/* Output:
No odd numbers discovered
*/
