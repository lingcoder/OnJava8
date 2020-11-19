// arrays/Reverse.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// The Collections.reverseOrder() Comparator
import java.util.*;
import onjava.*;
import static onjava.ArrayShow.*;

public class Reverse {
  public static void main(String[] args) {
    CompType[] a = new CompType[12];
    Arrays.setAll(a, n -> CompType.get());
    show("Before sorting", a);
    Arrays.sort(a, Collections.reverseOrder());
    show("After sorting", a);
  }
}
/* Output:
Before sorting: [[i = 35, j = 37], [i = 41, j = 20], [i
= 77, j = 79]
, [i = 56, j = 68], [i = 48, j = 93], [i = 70, j = 7]
, [i = 0, j = 25], [i = 62, j = 34], [i = 50, j = 82]
, [i = 31, j = 67], [i = 66, j = 54], [i = 21, j = 6]
]
After sorting: [[i = 77, j = 79], [i = 70, j = 7], [i =
66, j = 54]
, [i = 62, j = 34], [i = 56, j = 68], [i = 50, j = 82]
, [i = 48, j = 93], [i = 41, j = 20], [i = 35, j = 37]
, [i = 31, j = 67], [i = 21, j = 6], [i = 0, j = 25]
]
*/
