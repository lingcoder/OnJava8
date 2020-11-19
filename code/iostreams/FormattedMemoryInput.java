// iostreams/FormattedMemoryInput.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {VisuallyInspectOutput}
import java.io.*;

public class FormattedMemoryInput {
  public static void main(String[] args) {
    try(
      DataInputStream in = new DataInputStream(
        new ByteArrayInputStream(
          BufferedInputFile.read(
            "FormattedMemoryInput.java")
              .getBytes()))
    ) {
      while(true)
        System.out.write((char)in.readByte());
    } catch(EOFException e) {
      System.out.println("\nEnd of stream");
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}
