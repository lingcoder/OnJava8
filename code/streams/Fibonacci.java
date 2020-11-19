// streams/Fibonacci.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.stream.*;

public class Fibonacci {
  int x = 1;
  Stream<Integer> numbers() {
    return Stream.iterate(0, i -> {
      int result = x + i;
      x = i;
      return result;
    });
  }
  public static void main(String[] args) {
    new Fibonacci().numbers()
      .skip(20) // Don't use the first 20
      .limit(10) // Then take 10 of them
      .forEach(System.out::println);
  }
}
/* Output:
6765
10946
17711
28657
46368
75025
121393
196418
317811
514229
*/
