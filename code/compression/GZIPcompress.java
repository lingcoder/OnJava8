// compression/GZIPcompress.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java GZIPcompress GZIPcompress.java}
// {VisuallyInspectOutput}
import java.util.zip.*;
import java.io.*;

public class GZIPcompress {
  public static void main(String[] args) {
    if(args.length == 0) {
      System.out.println(
        "Usage: \nGZIPcompress file\n" +
        "\tUses GZIP compression to compress " +
        "the file to test.gz");
      System.exit(1);
    }
    try(
      InputStream in = new BufferedInputStream(
        new FileInputStream(args[0]));
      BufferedOutputStream out =
        new BufferedOutputStream(
          new GZIPOutputStream(
            new FileOutputStream("test.gz")))
    ) {
      System.out.println("Writing file");
      int c;
      while((c = in.read()) != -1)
        out.write(c);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Reading file");
    try(
      BufferedReader in2 = new BufferedReader(
        new InputStreamReader(new GZIPInputStream(
          new FileInputStream("test.gz"))))
    ) {
      in2.lines().forEach(System.out::println);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}
