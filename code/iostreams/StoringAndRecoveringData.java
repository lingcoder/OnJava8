// iostreams/StoringAndRecoveringData.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.io.*;

public class StoringAndRecoveringData {
  public static void main(String[] args) {
    try(
      DataOutputStream out = new DataOutputStream(
        new BufferedOutputStream(
          new FileOutputStream("Data.txt")))
    ) {
      out.writeDouble(3.14159);
      out.writeUTF("That was pi");
      out.writeDouble(1.41413);
      out.writeUTF("Square root of 2");
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    try(
      DataInputStream in = new DataInputStream(
        new BufferedInputStream(
          new FileInputStream("Data.txt")))
    ) {
      System.out.println(in.readDouble());
      // Only readUTF() will recover the
      // Java-UTF String properly:
      System.out.println(in.readUTF());
      System.out.println(in.readDouble());
      System.out.println(in.readUTF());
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}
/* Output:
3.14159
That was pi
1.41413
Square root of 2
*/
