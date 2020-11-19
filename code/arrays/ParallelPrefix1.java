// arrays/ParallelPrefix1.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import onjava.*;
import static onjava.ArrayShow.*;

public class ParallelPrefix1 {
  public static void main(String[] args) {
    int[] nums = new Count.Pint().array(10);
    show(nums);
    System.out.println(Arrays.stream(nums)
      .reduce(Integer::sum).getAsInt());
    Arrays.parallelPrefix(nums, Integer::sum);
    show(nums);
    System.out.println(Arrays.stream(
      new Count.Pint().array(6))
      .reduce(Integer::sum).getAsInt());
  }
}
/* Output:
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
45
[0, 1, 3, 6, 10, 15, 21, 28, 36, 45]
15
*/
