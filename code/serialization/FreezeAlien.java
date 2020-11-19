// serialization/FreezeAlien.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Create a serialized output file
import java.io.*;

public class FreezeAlien {
  public static void
  main(String[] args) throws Exception {
    try(
      ObjectOutputStream out = new ObjectOutputStream(
        new FileOutputStream("X.file"));
    ) {
      Alien quellek = new Alien();
      out.writeObject(quellek);
    }
  }
}
