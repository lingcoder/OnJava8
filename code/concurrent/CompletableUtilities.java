// concurrent/CompletableUtilities.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package onjava;
import java.util.concurrent.*;

public class CompletableUtilities {
  // Get and show value stored in a CF:
  public static void showr(CompletableFuture<?> c) {
    try {
      System.out.println(c.get());
    } catch(InterruptedException
            | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
  // For CF operations that have no value:
  public static void voidr(CompletableFuture<Void> c) {
    try {
      c.get(); // Returns void
    } catch(InterruptedException
            | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
