// concurrent/DualCompletableOperations.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import static onjava.CompletableUtilities.*;

public class DualCompletableOperations {
  static CompletableFuture<Workable> cfA, cfB;
  static void init() {
    cfA = Workable.make("A", 0.15);
    cfB = Workable.make("B", 0.10); // Always wins
  }
  static void join() {
    cfA.join();
    cfB.join();
    System.out.println("*****************");
  }
  public static void main(String[] args) {
    init();
    voidr(cfA.runAfterEitherAsync(cfB, () ->
      System.out.println("runAfterEither")));
    join();

    init();
    voidr(cfA.runAfterBothAsync(cfB, () ->
      System.out.println("runAfterBoth")));
    join();

    init();
    showr(cfA.applyToEitherAsync(cfB, w -> {
      System.out.println("applyToEither: " + w);
      return w;
    }));
    join();

    init();
    voidr(cfA.acceptEitherAsync(cfB, w -> {
      System.out.println("acceptEither: " + w);
    }));
    join();

    init();
    voidr(cfA.thenAcceptBothAsync(cfB, (w1, w2) -> {
      System.out.println("thenAcceptBoth: "
        + w1 + ", " + w2);
    }));
    join();

    init();
    showr(cfA.thenCombineAsync(cfB, (w1, w2) -> {
      System.out.println("thenCombine: "
        + w1 + ", " + w2);
      return w1;
    }));
    join();

    init();
    CompletableFuture<Workable>
      cfC = Workable.make("C", 0.08),
      cfD = Workable.make("D", 0.09);
    CompletableFuture.anyOf(cfA, cfB, cfC, cfD)
      .thenRunAsync(() ->
        System.out.println("anyOf"));
    join();

    init();
    cfC = Workable.make("C", 0.08);
    cfD = Workable.make("D", 0.09);
    CompletableFuture.allOf(cfA, cfB, cfC, cfD)
      .thenRunAsync(() ->
        System.out.println("allOf"));
    join();
  }
}
/* Output:
Workable[BW]
runAfterEither
Workable[AW]
*****************
Workable[BW]
Workable[AW]
runAfterBoth
*****************
Workable[BW]
applyToEither: Workable[BW]
Workable[BW]
Workable[AW]
*****************
Workable[BW]
acceptEither: Workable[BW]
Workable[AW]
*****************
Workable[BW]
Workable[AW]
thenAcceptBoth: Workable[AW], Workable[BW]
*****************
Workable[BW]
Workable[AW]
thenCombine: Workable[AW], Workable[BW]
Workable[AW]
*****************
Workable[CW]
anyOf
Workable[DW]
Workable[BW]
Workable[AW]
*****************
Workable[CW]
Workable[DW]
Workable[BW]
Workable[AW]
*****************
allOf
*/
