// streams/StreamOfOptionals.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

public class StreamOfOptionals {
  public static void main(String[] args) {
    Signal.stream()
      .limit(10)
      .forEach(System.out::println);
    System.out.println(" ---");
    Signal.stream()
      .limit(10)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .forEach(System.out::println);
  }
}
/* Output:
Optional[Signal(dash)]
Optional[Signal(dot)]
Optional[Signal(dash)]
Optional.empty
Optional.empty
Optional[Signal(dash)]
Optional.empty
Optional[Signal(dot)]
Optional[Signal(dash)]
Optional[Signal(dash)]
 ---
Signal(dot)
Signal(dot)
Signal(dash)
Signal(dash)
*/
