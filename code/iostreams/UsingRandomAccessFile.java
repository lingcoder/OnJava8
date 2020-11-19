// iostreams/UsingRandomAccessFile.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.io.*;

public class UsingRandomAccessFile {
  static String file = "rtest.dat";
  public static void display() {
    try(
      RandomAccessFile rf =
        new RandomAccessFile(file, "r")
    ) {
      for(int i = 0; i < 7; i++)
        System.out.println(
          "Value " + i + ": " + rf.readDouble());
      System.out.println(rf.readUTF());
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  public static void main(String[] args) {
    try(
      RandomAccessFile rf =
        new RandomAccessFile(file, "rw")
    ) {
      for(int i = 0; i < 7; i++)
        rf.writeDouble(i*1.414);
      rf.writeUTF("The end of the file");
      rf.close();
      display();
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    try(
      RandomAccessFile rf =
        new RandomAccessFile(file, "rw")
    ) {
      rf.seek(5*8);
      rf.writeDouble(47.0001);
      rf.close();
      display();
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}
/* Output:
Value 0: 0.0
Value 1: 1.414
Value 2: 2.828
Value 3: 4.242
Value 4: 5.656
Value 5: 7.069999999999999
Value 6: 8.484
The end of the file
Value 0: 0.0
Value 1: 1.414
Value 2: 2.828
Value 3: 4.242
Value 4: 5.656
Value 5: 47.0001
Value 6: 8.484
The end of the file
*/
