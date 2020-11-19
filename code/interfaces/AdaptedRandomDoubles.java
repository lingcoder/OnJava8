// interfaces/AdaptedRandomDoubles.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Creating an adapter with inheritance
import java.nio.*;
import java.util.*;

public class AdaptedRandomDoubles
implements RandomDoubles, Readable {
  private int count;
  public AdaptedRandomDoubles(int count) {
    this.count = count;
  }
  @Override
  public int read(CharBuffer cb) {
    if(count-- == 0)
      return -1;
    String result = Double.toString(next()) + " ";
    cb.append(result);
    return result.length();
  }
  public static void main(String[] args) {
    Scanner s =
      new Scanner(new AdaptedRandomDoubles(7));
    while(s.hasNextDouble())
      System.out.print(s.nextDouble() + " ");
  }
}
/* Output:
0.7271157860730044 0.5309454508634242
0.16020656493302599 0.18847866977771732
0.5166020801268457 0.2678662084200585
0.2613610344283964
*/
