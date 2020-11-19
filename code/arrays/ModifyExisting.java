// arrays/ModifyExisting.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import onjava.*;
import static onjava.ArrayShow.*;

public class ModifyExisting {
  public static void main(String[] args) {
    double[] da = new double[7];
    Arrays.setAll(da, new Rand.Double()::get);
    show(da);
    Arrays.setAll(da, n -> da[n] / 100); // [1]
    show(da);
  }
}
/* Output:
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99]
[0.0483, 0.028900000000000002, 0.028999999999999998,
0.0197, 0.0301, 0.0018, 0.009899999999999999]
*/
