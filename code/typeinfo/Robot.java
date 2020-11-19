// typeinfo/Robot.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import onjava.*;
import java.util.*;

public interface Robot {
  String name();
  String model();
  List<Operation> operations();
  static void test(Robot r) {
    if(r instanceof Null)
      System.out.println("[Null Robot]");
    System.out.println("Robot name: " + r.name());
    System.out.println("Robot model: " + r.model());
    for(Operation operation : r.operations()) {
      System.out.println(operation.description.get());
      operation.command.run();
    }
  }
}
