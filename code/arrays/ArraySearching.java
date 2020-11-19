// arrays/ArraySearching.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using Arrays.binarySearch()
import java.util.*;
import onjava.*;
import static onjava.ArrayShow.*;

public class ArraySearching {
  public static void main(String[] args) {
    Rand.Pint rand = new Rand.Pint();
    int[] a = new Rand.Pint().array(25);
    Arrays.sort(a);
    show("Sorted array", a);
    while(true) {
      int r = rand.getAsInt();
      int location = Arrays.binarySearch(a, r);
      if(location >= 0) {
        System.out.println(
          "Location of " + r + " is " + location +
          ", a[" + location + "] is " + a[location]);
        break; // Out of while loop
      }
    }
  }
}
/* Output:
Sorted array: [125, 267, 635, 650, 1131, 1506, 1634,
2400, 2766, 3063, 3768, 3941, 4720, 4762, 4948, 5070,
5682, 5807, 6177, 6193, 6656, 7021, 8479, 8737, 9954]
Location of 635 is 2, a[2] is 635
*/
