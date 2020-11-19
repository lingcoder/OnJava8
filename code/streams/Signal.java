// streams/Signal.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

public class Signal {
  private final String msg;
  public Signal(String msg) { this.msg = msg; }
  public String getMsg() { return msg; }
  @Override
  public String toString() {
    return "Signal(" + msg + ")";
  }
  static Random rand = new Random(47);
  public static Signal morse() {
    switch(rand.nextInt(4)) {
      case 1: return new Signal("dot");
      case 2: return new Signal("dash");
      default: return null;
    }
  }
  public static Stream<Optional<Signal>> stream() {
    return Stream.generate(Signal::morse)
      .map(signal -> Optional.ofNullable(signal));
  }
}
