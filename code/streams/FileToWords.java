// streams/FileToWords.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.nio.file.*;
import java.util.stream.*;
import java.util.regex.Pattern;

public class FileToWords {
  public static Stream<String> stream(String filePath)
  throws Exception {
    return Files.lines(Paths.get(filePath))
      .skip(1) // First (comment) line
      .flatMap(line ->
        Pattern.compile("\\W+").splitAsStream(line));
  }
}
