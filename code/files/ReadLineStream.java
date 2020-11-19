// files/ReadLineStream.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.nio.file.*;

public class ReadLineStream {
  public static void
  main(String[] args) throws Exception {
    Files.lines(Paths.get("PathInfo.java"))
      .skip(13)
      .findFirst()
      .ifPresent(System.out::println);
  }
}
/* Output:
    show("RegularFile", Files.isRegularFile(p));
*/
