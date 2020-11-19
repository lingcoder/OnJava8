// streams/Generator.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Generator implements Supplier<String> {
  Random rand = new Random(47);
  char[] letters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  public String get() {
    return "" + letters[rand.nextInt(letters.length)];
  }
  public static void main(String[] args) {
    String word = Stream.generate(new Generator())
      .limit(30)
      .collect(Collectors.joining());
    System.out.println(word);
  }
}
/* Output:
YNZBRNYGCFOWZNTCQRGSEGZMMJMROE
*/
