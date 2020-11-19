// lowlevel/EvenChecker.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import onjava.TimedAbort;

public class EvenChecker implements Runnable {
  private IntGenerator generator;
  private final int id;
  public EvenChecker(IntGenerator generator, int id) {
    this.generator = generator;
    this.id = id;
  }
  @Override
  public void run() {
    while(!generator.isCanceled()) {
      int val = generator.next();
      if(val % 2 != 0) {
        System.out.println(val + " not even!");
        generator.cancel(); // Cancels all EvenCheckers
      }
    }
  }
  // Test any IntGenerator:
  public static void test(IntGenerator gp, int count) {
    List<CompletableFuture<Void>> checkers =
      IntStream.range(0, count)
        .mapToObj(i -> new EvenChecker(gp, i))
        .map(CompletableFuture::runAsync)
        .collect(Collectors.toList());
    checkers.forEach(CompletableFuture::join);
  }
  // Default value for count:
  public static void test(IntGenerator gp) {
    new TimedAbort(4, "No odd numbers discovered");
    test(gp, 10);
  }
}
