// onjava/Operations.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package onjava;
import java.util.*;

public interface Operations {
  void execute();
  static void runOps(Operations... ops) {
    for(Operations op : ops)
      op.execute();
  }
  static void show(String msg) {
    System.out.println(msg);
  }
}
