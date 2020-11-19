// streams/Randoms.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class Randoms {
  public static void main(String[] args) {
    new Random(47)
      .ints(5, 20)
      .distinct()
      .limit(7)
      .sorted()
      .forEach(System.out::println);
  }
}
/* Output:
6
10
13
16
17
18
19
*/
