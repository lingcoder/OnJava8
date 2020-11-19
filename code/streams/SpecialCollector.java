// streams/SpecialCollector.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

public class SpecialCollector {
  public static void
  main(String[] args) throws Exception {
    ArrayList<String> words =
      FileToWords.stream("Cheese.dat")
        .collect(ArrayList::new,
                 ArrayList::add,
                 ArrayList::addAll);
    words.stream()
      .filter(s -> s.equals("cheese"))
      .forEach(System.out::println);
  }
}
/* Output:
cheese
cheese
*/
