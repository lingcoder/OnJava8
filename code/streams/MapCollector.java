// streams/MapCollector.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

class Pair {
  public final Character c;
  public final Integer i;
  Pair(Character c, Integer i) {
    this.c = c;
    this.i = i;
  }
  public Character getC() { return c; }
  public Integer getI() { return i; }
  @Override
  public String toString() {
    return "Pair(" + c + ", " + i + ")";
  }
}

class RandomPair {
  Random rand = new Random(47);
  // An infinite iterator of random capital letters:
  Iterator<Character> capChars = rand.ints(65,91)
    .mapToObj(i -> (char)i)
    .iterator();
  public Stream<Pair> stream() {
    return rand.ints(100, 1000).distinct()
      .mapToObj(i -> new Pair(capChars.next(), i));
  }
}

public class MapCollector {
  public static void main(String[] args) {
    Map<Integer, Character> map =
      new RandomPair().stream()
        .limit(8)
        .collect(
          Collectors.toMap(Pair::getI, Pair::getC));
    System.out.println(map);
  }
}
/* Output:
{688=W, 309=C, 293=B, 761=N, 858=N, 668=G, 622=F,
751=N}
*/
