// iostreams/BasicFileOutput.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {VisuallyInspectOutput}
import java.io.*;

public class BasicFileOutput {
  static String file = "BasicFileOutput.dat";
  public static void main(String[] args) {
    try(
      BufferedReader in = new BufferedReader(
        new StringReader(
          BufferedInputFile.read(
            "BasicFileOutput.java")));
      PrintWriter out = new PrintWriter(
        new BufferedWriter(new FileWriter(file)))
    ) {
      in.lines().forEach(out::println);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Show the stored file:
    System.out.println(BufferedInputFile.read(file));
  }
}
