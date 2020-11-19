// concurrent/SynchronizedFactory.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.atomic.*;

final class SyncFactory implements HasID {
  private final int id;
  private SyncFactory(SharedArg sa) {
    id = sa.get();
  }
  @Override
  public int getID() { return id; }
  public static synchronized
  SyncFactory factory(SharedArg sa) {
    return new SyncFactory(sa);
  }
}

public class SynchronizedFactory {
  public static void main(String[] args) {
    Unsafe unsafe = new Unsafe();
    IDChecker.test(() ->
      SyncFactory.factory(unsafe));
  }
}
/* Output:
0
*/
