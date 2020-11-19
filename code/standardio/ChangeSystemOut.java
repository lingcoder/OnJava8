// standardio/ChangeSystemOut.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Turn System.out into a PrintWriter
import java.io.*;

public class ChangeSystemOut {
  public static void main(String[] args) {
    PrintWriter out =
      new PrintWriter(System.out, true);
    out.println("Hello, world");
  }
}
/* Output:
Hello, world
*/
