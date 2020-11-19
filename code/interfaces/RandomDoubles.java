// interfaces/RandomDoubles.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public interface RandomDoubles {
  Random RAND = new Random(47);
  default double next() { return RAND.nextDouble(); }
  static void main(String[] args) {
    RandomDoubles rd = new RandomDoubles() {};
    for(int i = 0; i < 7; i ++)
      System.out.print(rd.next() + " ");
  }
}
/* Output:
0.7271157860730044 0.5309454508634242
0.16020656493302599 0.18847866977771732
0.5166020801268457 0.2678662084200585
0.2613610344283964
*/
