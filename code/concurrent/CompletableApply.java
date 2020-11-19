// concurrent/CompletableApply.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;

public class CompletableApply {
  public static void main(String[] args) {
    CompletableFuture<Machina> cf =
      CompletableFuture.completedFuture(
        new Machina(0));
    CompletableFuture<Machina> cf2 =
      cf.thenApply(Machina::work);
    CompletableFuture<Machina> cf3 =
      cf2.thenApply(Machina::work);
    CompletableFuture<Machina> cf4 =
      cf3.thenApply(Machina::work);
    CompletableFuture<Machina> cf5 =
      cf4.thenApply(Machina::work);
  }
}
/* Output:
Machina0: ONE
Machina0: TWO
Machina0: THREE
Machina0: complete
*/
