// exceptions/InputFile2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class InputFile2 {
  private String fname;
  public InputFile2(String fname) {
    this.fname = fname;
  }
  public
  Stream<String> getLines() throws IOException {
    return Files.lines(Paths.get(fname));
  }
  public static void
  main(String[] args) throws IOException {
    new InputFile2("InputFile2.java").getLines()
      .skip(15)
      .limit(1)
      .forEach(System.out::println);
  }
}
/* Output:
  main(String[] args) throws IOException {
*/
