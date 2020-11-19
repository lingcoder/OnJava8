// streams/Reduce.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

class Frobnitz {
  int size;
  Frobnitz(int sz) { size = sz; }
  @Override
  public String toString() {
    return "Frobnitz(" + size + ")";
  }
  // Generator:
  static Random rand = new Random(47);
  static final int BOUND = 100;
  static Frobnitz supply() {
    return new Frobnitz(rand.nextInt(BOUND));
  }
}

public class Reduce {
  public static void main(String[] args) {
    Stream.generate(Frobnitz::supply)
      .limit(10)
      .peek(System.out::println)
      .reduce((fr0, fr1) -> fr0.size < 50 ? fr0 : fr1)
      .ifPresent(System.out::println);
  }
}
/* Output:
Frobnitz(58)
Frobnitz(55)
Frobnitz(93)
Frobnitz(61)
Frobnitz(61)
Frobnitz(29)
Frobnitz(68)
Frobnitz(0)
Frobnitz(22)
Frobnitz(7)
Frobnitz(29)
*/
