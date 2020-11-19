// lowlevel/SerialNumberChecker.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Test SerialNumbers implementations for thread-safety
import java.util.concurrent.*;
import onjava.Nap;

public class SerialNumberChecker implements Runnable {
  private CircularSet serials = new CircularSet(1000);
  private SerialNumbers producer;
  public SerialNumberChecker(SerialNumbers producer) {
    this.producer = producer;
  }
  @Override
  public void run() {
    while(true) {
      int serial = producer.nextSerialNumber();
      if(serials.contains(serial)) {
        System.out.println("Duplicate: " + serial);
        System.exit(0);
      }
      serials.add(serial);
    }
  }
  static void test(SerialNumbers producer) {
    for(int i = 0; i < 10; i++)
      CompletableFuture.runAsync(
        new SerialNumberChecker(producer));
    new Nap(4, "No duplicates detected");
  }
}
