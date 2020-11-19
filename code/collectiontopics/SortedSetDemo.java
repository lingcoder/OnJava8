// collectiontopics/SortedSetDemo.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import static java.util.stream.Collectors.*;

public class SortedSetDemo {
  public static void main(String[] args) {
    SortedSet<String> sortedSet =
      Arrays.stream(
        "one two three four five six seven eight"
        .split(" "))
        .collect(toCollection(TreeSet::new));
    System.out.println(sortedSet);
    String low = sortedSet.first();
    String high = sortedSet.last();
    System.out.println(low);
    System.out.println(high);
    Iterator<String> it = sortedSet.iterator();
    for(int i = 0; i <= 6; i++) {
      if(i == 3) low = it.next();
      if(i == 6) high = it.next();
      else it.next();
    }
    System.out.println(low);
    System.out.println(high);
    System.out.println(sortedSet.subSet(low, high));
    System.out.println(sortedSet.headSet(high));
    System.out.println(sortedSet.tailSet(low));
  }
}
/* Output:
[eight, five, four, one, seven, six, three, two]
eight
two
one
two
[one, seven, six, three]
[eight, five, four, one, seven, six, three]
[one, seven, six, three, two]
*/
