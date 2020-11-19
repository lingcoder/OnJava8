// streams/CollectionToStream.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

public class CollectionToStream {
  public static void main(String[] args) {
    List<Bubble> bubbles = Arrays.asList(
      new Bubble(1), new Bubble(2), new Bubble(3));
    System.out.println(
      bubbles.stream()
        .mapToInt(b -> b.i)
        .sum());

    Set<String> w = new HashSet<>(Arrays.asList(
      "It's a wonderful day for pie!".split(" ")));
    w.stream()
      .map(x -> x + " ")
      .forEach(System.out::print);
    System.out.println();

    Map<String, Double> m = new HashMap<>();
    m.put("pi", 3.14159);
    m.put("e", 2.718);
    m.put("phi", 1.618);
    m.entrySet().stream()
      .map(e -> e.getKey() + ": " + e.getValue())
      .forEach(System.out::println);
  }
}
/* Output:
6
a pie! It's for wonderful day
phi: 1.618
e: 2.718
pi: 3.14159
*/
