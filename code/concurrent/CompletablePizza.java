// concurrent/CompletablePizza.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import onjava.Timer;

public class CompletablePizza {
  static final int QUANTITY = 5;
  public static CompletableFuture<Pizza>
  makeCF(Pizza za) {
    return CompletableFuture
      .completedFuture(za)
      .thenApplyAsync(Pizza::roll)
      .thenApplyAsync(Pizza::sauce)
      .thenApplyAsync(Pizza::cheese)
      .thenApplyAsync(Pizza::toppings)
      .thenApplyAsync(Pizza::bake)
      .thenApplyAsync(Pizza::slice)
      .thenApplyAsync(Pizza::box);
  }
  public static void
  show(CompletableFuture<Pizza> cf) {
    try {
      System.out.println(cf.get());
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
  public static void main(String[] args) {
    Timer timer = new Timer();
    List<CompletableFuture<Pizza>> pizzas =
      IntStream.range(0, QUANTITY)
        .mapToObj(Pizza::new)
        .map(CompletablePizza::makeCF)
        .collect(Collectors.toList());
    System.out.println(timer.duration());
    pizzas.forEach(CompletablePizza::show);
    System.out.println(timer.duration());
  }
}
/* Output:
169
Pizza 0: ROLLED
Pizza 1: ROLLED
Pizza 2: ROLLED
Pizza 4: ROLLED
Pizza 3: ROLLED
Pizza 1: SAUCED
Pizza 0: SAUCED
Pizza 2: SAUCED
Pizza 4: SAUCED
Pizza 3: SAUCED
Pizza 0: CHEESED
Pizza 4: CHEESED
Pizza 1: CHEESED
Pizza 2: CHEESED
Pizza 3: CHEESED
Pizza 0: TOPPED
Pizza 4: TOPPED
Pizza 1: TOPPED
Pizza 2: TOPPED
Pizza 3: TOPPED
Pizza 0: BAKED
Pizza 4: BAKED
Pizza 1: BAKED
Pizza 3: BAKED
Pizza 2: BAKED
Pizza 0: SLICED
Pizza 4: SLICED
Pizza 1: SLICED
Pizza 3: SLICED
Pizza 2: SLICED
Pizza 4: BOXED
Pizza 0: BOXED
Pizza0: complete
Pizza 1: BOXED
Pizza1: complete
Pizza 3: BOXED
Pizza 2: BOXED
Pizza2: complete
Pizza3: complete
Pizza4: complete
1797
*/
