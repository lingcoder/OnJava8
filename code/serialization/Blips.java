// serialization/Blips.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Simple use of Externalizable & a pitfall
import java.io.*;

class Blip1 implements Externalizable {
  public Blip1() {
    System.out.println("Blip1 Constructor");
  }
  @Override
  public void writeExternal(ObjectOutput out)
      throws IOException {
    System.out.println("Blip1.writeExternal");
  }
  @Override
  public void readExternal(ObjectInput in)
     throws IOException, ClassNotFoundException {
    System.out.println("Blip1.readExternal");
  }
}

class Blip2 implements Externalizable {
  Blip2() {
    System.out.println("Blip2 Constructor");
  }
  @Override
  public void writeExternal(ObjectOutput out)
      throws IOException {
    System.out.println("Blip2.writeExternal");
  }
  @Override
  public void readExternal(ObjectInput in)
     throws IOException, ClassNotFoundException {
    System.out.println("Blip2.readExternal");
  }
}

public class Blips {
  public static void main(String[] args) {
    System.out.println("Constructing objects:");
    Blip1 b1 = new Blip1();
    Blip2 b2 = new Blip2();
    try(
      ObjectOutputStream o = new ObjectOutputStream(
        new FileOutputStream("Blips.serialized"))
    ) {
      System.out.println("Saving objects:");
      o.writeObject(b1);
      o.writeObject(b2);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Now get them back:
    System.out.println("Recovering b1:");
    try(
      ObjectInputStream in = new ObjectInputStream(
        new FileInputStream("Blips.serialized"))
    ) {
      b1 = (Blip1)in.readObject();
    } catch(IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    // OOPS! Throws an exception:
    //- System.out.println("Recovering b2:");
    //- b2 = (Blip2)in.readObject();
  }
}
/* Output:
Constructing objects:
Blip1 Constructor
Blip2 Constructor
Saving objects:
Blip1.writeExternal
Blip2.writeExternal
Recovering b1:
Blip1 Constructor
Blip1.readExternal
*/
