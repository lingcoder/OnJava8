// lowlevel/IntGenerator.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class IntGenerator {
  private AtomicBoolean canceled =
    new AtomicBoolean();
  public abstract int next();
  public void cancel() { canceled.set(true); }
  public boolean isCanceled() {
    return canceled.get();
  }
}
