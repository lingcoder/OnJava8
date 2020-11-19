// files/StreamInAndOut.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class StreamInAndOut {
  public static void main(String[] args) {
    try(
      Stream<String> input =
        Files.lines(Paths.get("StreamInAndOut.java"));
      PrintWriter output =
        new PrintWriter("StreamInAndOut.txt")
    ) {
      input
        .map(String::toUpperCase)
        .forEachOrdered(output::println);
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
