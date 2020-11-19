// concurrent/SynchronizedConstructor.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.atomic.*;

class SyncConstructor implements HasID {
  private final int id;
  private static Object
    constructorLock = new Object();
  SyncConstructor(SharedArg sa) {
    synchronized(constructorLock) {
      id = sa.get();
    }
  }
  @Override
  public int getID() { return id; }
}

public class SynchronizedConstructor {
  public static void main(String[] args) {
    Unsafe unsafe = new Unsafe();
    IDChecker.test(() ->
      new SyncConstructor(unsafe));
  }
}
/* Output:
0
*/
