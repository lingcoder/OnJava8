// exceptions/StreamsAreAutoCloseable.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class StreamsAreAutoCloseable {
  public static void
  main(String[] args) throws IOException{
    try(
      Stream<String> in = Files.lines(
        Paths.get("StreamsAreAutoCloseable.java"));
      PrintWriter outfile = new PrintWriter(
        "Results.txt"); // [1]
    ) {
      in.skip(5)
        .limit(1)
        .map(String::toLowerCase)
        .forEachOrdered(outfile::println);
    } // [2]
  }
}
