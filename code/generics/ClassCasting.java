// generics/ClassCasting.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.io.*;
import java.util.*;

public class ClassCasting {
  @SuppressWarnings("unchecked")
  public void f(String[] args) throws Exception {
    ObjectInputStream in = new ObjectInputStream(
      new FileInputStream(args[0]));
      // Won't Compile:
//    List<Widget> lw1 =
//    List<>.class.cast(in.readObject());
    List<Widget> lw2 = List.class.cast(in.readObject());
  }
}
