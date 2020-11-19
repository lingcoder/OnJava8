// files/Writing.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.nio.file.*;

public class Writing {
  static Random rand = new Random(47);
  static final int SIZE = 1000;
  public static void
  main(String[] args) throws Exception {
    // Write bytes to a file:
    byte[] bytes = new byte[SIZE];
    rand.nextBytes(bytes);
    Files.write(Paths.get("bytes.dat"), bytes);
    System.out.println("bytes.dat: " +
      Files.size(Paths.get("bytes.dat")));

    // Write an iterable to a file:
    List<String> lines = Files.readAllLines(
      Paths.get("../streams/Cheese.dat"));
    Files.write(Paths.get("Cheese.txt"), lines);
    System.out.println("Cheese.txt: " +
      Files.size(Paths.get("Cheese.txt")));
  }
}
/* Output:
bytes.dat: 1000
Cheese.txt: 199
*/
