// streams/FileToWordsRegexp.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.io.*;
import java.nio.file.*;
import java.util.stream.*;
import java.util.regex.Pattern;

public class FileToWordsRegexp {
  private String all;
  public FileToWordsRegexp(String filePath)
  throws Exception {
    all = Files.lines(Paths.get(filePath))
      .skip(1) // First (comment) line
      .collect(Collectors.joining(" "));
  }
  public Stream<String> stream() {
    return Pattern
      .compile("[ .,?]+").splitAsStream(all);
  }
  public static void
  main(String[] args) throws Exception {
    FileToWordsRegexp fw =
      new FileToWordsRegexp("Cheese.dat");
    fw.stream()
      .limit(7)
      .map(w -> w + " ")
      .forEach(System.out::print);
    fw.stream()
      .skip(7)
      .limit(2)
      .map(w -> w + " ")
      .forEach(System.out::print);
  }
}
/* Output:
Not much of a cheese shop really is it
*/
