// iostreams/BufferedInputFile.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {VisuallyInspectOutput}
import java.io.*;
import java.util.stream.*;

public class BufferedInputFile {
  public static String read(String filename) {
    try(BufferedReader in = new BufferedReader(
      new FileReader(filename))) {
      return in.lines()
        .collect(Collectors.joining("\n"));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  public static void main(String[] args) {
    System.out.print(
      read("BufferedInputFile.java"));
  }
}
