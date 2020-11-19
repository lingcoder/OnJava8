// concurrent/CollectionIntoStream.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import onjava.*;
import java.util.*;
import java.util.stream.*;

public class CollectionIntoStream {
  public static void main(String[] args) {
    List<String> strings =
      Stream.generate(new Rand.String(5))
        .limit(10)
        .collect(Collectors.toList());
    strings.forEach(System.out::println);
    // Convert to a Stream for many more options:
    String result = strings.stream()
      .map(String::toUpperCase)
      .map(s -> s.substring(2))
      .reduce(":", (s1, s2) -> s1 + s2);
    System.out.println(result);
  }
}
/* Output:
btpen
pccux
szgvg
meinn
eeloz
tdvew
cippc
ygpoa
lkljl
bynxt
:PENCUXGVGINNLOZVEWPPCPOALJLNXT
*/
