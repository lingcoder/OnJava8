// concurrent/FrostedCake.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import java.util.stream.*;
import onjava.Nap;

final class Frosting {
  private Frosting() {}
  static CompletableFuture<Frosting> make() {
    new Nap(0.1);
    return CompletableFuture
      .completedFuture(new Frosting());
  }
}

public class FrostedCake {
  public FrostedCake(Baked baked, Frosting frosting) {
    new Nap(0.1);
  }
  @Override
  public String toString() { return "FrostedCake"; }
  public static void main(String[] args) {
    Baked.batch().forEach(baked -> baked
      .thenCombineAsync(Frosting.make(),
        (cake, frosting) ->
          new FrostedCake(cake, frosting))
      .thenAcceptAsync(System.out::println)
      .join());
  }
}
