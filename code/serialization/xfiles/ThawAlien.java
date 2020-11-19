// serialization/xfiles/ThawAlien.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Recover a serialized file
// {java serialization.xfiles.ThawAlien}
// {RunFirst: FreezeAlien}
package serialization.xfiles;
import java.io.*;

public class ThawAlien {
  public static void
  main(String[] args) throws Exception {
    ObjectInputStream in = new ObjectInputStream(
      new FileInputStream(new File("X.file")));
    Object mystery = in.readObject();
    System.out.println(mystery.getClass());
  }
}
/* Output:
class Alien
*/
