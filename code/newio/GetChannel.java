// newio/GetChannel.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Getting channels from streams
import java.nio.*;
import java.nio.channels.*;
import java.io.*;

public class GetChannel {
  private static String name = "data.txt";
  private static final int BSIZE = 1024;
  public static void main(String[] args) {
    // Write a file:
    try(
      FileChannel fc = new FileOutputStream(name)
        .getChannel()
    ) {
      fc.write(ByteBuffer
        .wrap("Some text ".getBytes()));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Add to the end of the file:
    try(
      FileChannel fc = new RandomAccessFile(
        name, "rw").getChannel()
    ) {
      fc.position(fc.size()); // Move to the end
      fc.write(ByteBuffer
        .wrap("Some more".getBytes()));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Read the file:
    try(
      FileChannel fc = new FileInputStream(name)
        .getChannel()
    ) {
      ByteBuffer buff = ByteBuffer.allocate(BSIZE);
      fc.read(buff);
      buff.flip();
      while(buff.hasRemaining())
        System.out.write(buff.get());
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    System.out.flush();
  }
}
/* Output:
Some text Some more
*/
