// lowlevel/Atomicity.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import onjava.TimedAbort;

public class Atomicity {
  public static void test(IntTestable it) {
    new TimedAbort(4, "No failures found");
    CompletableFuture.runAsync(it);
    while(true) {
      int val = it.getAsInt();
      if(val % 2 != 0) {
        System.out.println("failed with: " + val);
        System.exit(0);
      }
    }
  }
}
