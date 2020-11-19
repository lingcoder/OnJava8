// references/Compete.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.io.*;
import onjava.Timer;

class Thing1 implements Serializable {}
class Thing2 implements Serializable {
  Thing1 t1 = new Thing1();
}

class Thing3 implements Cloneable {
  @Override
  public Thing3 clone() {
    try {
      return (Thing3)super.clone();
    } catch(CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}

class Thing4 implements Cloneable {
  private Thing3 t3 = new Thing3();
  @Override
  public Thing4 clone() {
    Thing4 t4 = null;
    try {
      t4 = (Thing4)super.clone();
    } catch(CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
    // Clone the field, too:
    t4.t3 = t3.clone();
    return t4;
  }
}

public class Compete {
  public static final int SIZE = 100000;
  public static void
  main(String[] args) throws Exception {
    Thing2[] a = new Thing2[SIZE];
    for(int i = 0; i < SIZE; i++)
      a[i] = new Thing2();
    Thing4[] b = new Thing4[SIZE];
    for(int i = 0; i < SIZE; i++)
      b[i] = new Thing4();
    Timer timer = new Timer();
    try(
      ByteArrayOutputStream buf =
        new ByteArrayOutputStream();
      ObjectOutputStream oos =
        new ObjectOutputStream(buf)
    ) {
      for(Thing2 a1 : a) {
        oos.writeObject(a1);
      }
      // Now get copies:
      try(
        ObjectInputStream in =
          new ObjectInputStream(
            new ByteArrayInputStream(
              buf.toByteArray()))
      ) {
        Thing2[] c = new Thing2[SIZE];
        for(int i = 0; i < SIZE; i++)
          c[i] = (Thing2)in.readObject();
      }
    }
    System.out.println(
      "Duplication via serialization: " +
      timer.duration() + " Milliseconds");

    // Now try cloning:
    timer = new Timer();
    Thing4[] d = new Thing4[SIZE];
    for(int i = 0; i < SIZE; i++)
      d[i] = b[i].clone();
    System.out.println(
      "Duplication via cloning: " +
      timer.duration() + " Milliseconds");
  }
}
/* Output:
Duplication via serialization: 516 Milliseconds
Duplication via cloning: 71 Milliseconds
*/
