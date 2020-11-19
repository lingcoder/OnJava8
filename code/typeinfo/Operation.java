// typeinfo/Operation.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;

public class Operation {
  public final Supplier<String> description;
  public final Runnable command;
  public
  Operation(Supplier<String> descr, Runnable cmd) {
    description = descr;
    command = cmd;
  }
}
