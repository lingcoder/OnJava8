// lowlevel/MutexEvenProducer.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Preventing thread collisions with mutexes
import java.util.concurrent.locks.*;
import onjava.Nap;

public class MutexEvenProducer extends IntGenerator {
  private int currentEvenValue = 0;
  private Lock lock = new ReentrantLock();
  @Override
  public int next() {
    lock.lock();
    try {
      ++currentEvenValue;
      new Nap(0.01); // Cause failure faster
      ++currentEvenValue;
      return currentEvenValue;
    } finally {
      lock.unlock();
    }
  }
  public static void main(String[] args) {
    EvenChecker.test(new MutexEvenProducer());
  }
}
/*
No odd numbers discovered
*/
