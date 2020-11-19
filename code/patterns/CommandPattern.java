// patterns/CommandPattern.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class CommandPattern {
  public static void main(String[] args) {
    List<Runnable> macro = Arrays.asList(
      () -> System.out.print("Hello "),
      () -> System.out.print("World! "),
      () -> System.out.print("I'm the command pattern!")
    );
    macro.forEach(Runnable::run);
  }
}
/* Output:
Hello World! I'm the command pattern!
*/
