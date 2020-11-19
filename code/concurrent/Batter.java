// concurrent/Batter.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import onjava.Nap;

public class Batter {
  static class Eggs {}
  static class Milk {}
  static class Sugar {}
  static class Flour {}
  static <T> T prepare(T ingredient) {
    new Nap(0.1);
    return ingredient;
  }
  static <T> CompletableFuture<T> prep(T ingredient) {
    return CompletableFuture
      .completedFuture(ingredient)
      .thenApplyAsync(Batter::prepare);
  }
  public static CompletableFuture<Batter> mix() {
    CompletableFuture<Eggs> eggs = prep(new Eggs());
    CompletableFuture<Milk> milk = prep(new Milk());
    CompletableFuture<Sugar> sugar = prep(new Sugar());
    CompletableFuture<Flour> flour = prep(new Flour());
    CompletableFuture
      .allOf(eggs, milk, sugar, flour)
      .join();
    new Nap(0.1); // Mixing time
    return
      CompletableFuture.completedFuture(new Batter());
  }
}
