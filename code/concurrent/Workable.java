// concurrent/Workable.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;
import onjava.Nap;

public class Workable {
  String id;
  final double duration;
  public Workable(String id, double duration) {
    this.id = id;
    this.duration = duration;
  }
  @Override
  public String toString() {
    return "Workable[" + id + "]";
  }
  public static Workable work(Workable tt) {
    new Nap(tt.duration); // Seconds
    tt.id = tt.id + "W";
    System.out.println(tt);
    return tt;
  }
  public static CompletableFuture<Workable>
  make(String id, double duration) {
    return
      CompletableFuture.completedFuture(
        new Workable(id, duration))
      .thenApplyAsync(Workable::work);
  }
}
