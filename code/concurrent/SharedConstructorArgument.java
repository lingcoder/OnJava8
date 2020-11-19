// concurrent/SharedConstructorArgument.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.atomic.*;

interface SharedArg {
  int get();
}

class Unsafe implements SharedArg {
  private int i = 0;
  public int get() { return i++; }
}

class Safe implements SharedArg {
  private static AtomicInteger counter =
    new AtomicInteger();
  public int get() {
    return counter.getAndIncrement();
  }
}

class SharedUser implements HasID {
  private final int id;
  SharedUser(SharedArg sa) {
    id = sa.get();
  }
  @Override
  public int getID() { return id; }
}

public class SharedConstructorArgument {
  public static void main(String[] args) {
    Unsafe unsafe = new Unsafe();
    IDChecker.test(() -> new SharedUser(unsafe));
    Safe safe = new Safe();
    IDChecker.test(() -> new SharedUser(safe));
  }
}
/* Output:
24838
0
*/
