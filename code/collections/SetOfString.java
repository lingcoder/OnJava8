// collections/SetOfString.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class SetOfString {
  public static void main(String[] args) {
    Set<String> colors = new HashSet<>();
    for(int i = 0; i < 100; i++) {
      colors.add("Yellow");
      colors.add("Blue");
      colors.add("Red");
      colors.add("Red");
      colors.add("Orange");
      colors.add("Yellow");
      colors.add("Blue");
      colors.add("Purple");
    }
    System.out.println(colors);
  }
}
/* Output:
[Red, Yellow, Blue, Purple, Orange]
*/
