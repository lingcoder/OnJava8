// concurrent/CompletableApplyAsync.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import onjava.*;

public class CompletableApplyAsync {
  public static void main(String[] args) {
    Timer timer = new Timer();
    CompletableFuture<Machina> cf =
      CompletableFuture.completedFuture(
        new Machina(0))
      .thenApplyAsync(Machina::work)
      .thenApplyAsync(Machina::work)
      .thenApplyAsync(Machina::work)
      .thenApplyAsync(Machina::work);
    System.out.println(timer.duration());
    System.out.println(cf.join());
    System.out.println(timer.duration());
  }
}
/* Output:
116
Machina0: ONE
Machina0: TWO
Machina0: THREE
Machina0: complete
Machina0: complete
552
*/
