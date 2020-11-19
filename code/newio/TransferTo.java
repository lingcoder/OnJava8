// newio/TransferTo.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using transferTo() between channels
// {java TransferTo TransferTo.java TransferTo.txt}
import java.nio.channels.*;
import java.io.*;

public class TransferTo {
  public static void main(String[] args) {
    if(args.length != 2) {
      System.out.println(
        "arguments: sourcefile destfile");
      System.exit(1);
    }
    try(
      FileChannel in = new FileInputStream(
        args[0]).getChannel();
      FileChannel out = new FileOutputStream(
        args[1]).getChannel()
    ) {
      in.transferTo(0, in.size(), out);
      // Or:
      // out.transferFrom(in, 0, in.size());
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}
