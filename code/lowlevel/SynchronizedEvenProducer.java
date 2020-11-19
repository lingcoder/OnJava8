// lowlevel/SynchronizedEvenProducer.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Simplifying mutexes with the synchronized keyword
import onjava.Nap;

public class
SynchronizedEvenProducer extends IntGenerator {
  private int currentEvenValue = 0;
  @Override
  public synchronized int next() {
    ++currentEvenValue;
    new Nap(0.01); // Cause failure faster
    ++currentEvenValue;
    return currentEvenValue;
  }
  public static void main(String[] args) {
    EvenChecker.test(new SynchronizedEvenProducer());
  }
}
/* Output:
No odd numbers discovered
*/
