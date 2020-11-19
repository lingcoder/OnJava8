// serialization/Blip3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Reconstructing an externalizable object
import java.io.*;

public class Blip3 implements Externalizable {
  private int i;
  private String s; // No initialization
  public Blip3() {
    System.out.println("Blip3 Constructor");
    // s, i not initialized
  }
  public Blip3(String x, int a) {
    System.out.println("Blip3(String x, int a)");
    s = x;
    i = a;
    // s & i initialized only in non-no-arg constructor.
  }
  @Override
  public String toString() { return s + i; }
  @Override
  public void writeExternal(ObjectOutput out)
  throws IOException {
    System.out.println("Blip3.writeExternal");
    // You must do this:
    out.writeObject(s);
    out.writeInt(i);
  }
  @Override
  public void readExternal(ObjectInput in)
  throws IOException, ClassNotFoundException {
    System.out.println("Blip3.readExternal");
    // You must do this:
    s = (String)in.readObject();
    i = in.readInt();
  }
  public static void main(String[] args) {
    System.out.println("Constructing objects:");
    Blip3 b3 = new Blip3("A String ", 47);
    System.out.println(b3);
    try(
      ObjectOutputStream o = new ObjectOutputStream(
        new FileOutputStream("Blip3.serialized"))
    ) {
      System.out.println("Saving object:");
      o.writeObject(b3);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Now get it back:
    System.out.println("Recovering b3:");
    try(
      ObjectInputStream in = new ObjectInputStream(
        new FileInputStream("Blip3.serialized"))
    ) {
      b3 = (Blip3)in.readObject();
    } catch(IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    System.out.println(b3);
  }
}
/* Output:
Constructing objects:
Blip3(String x, int a)
A String 47
Saving object:
Blip3.writeExternal
Recovering b3:
Blip3 Constructor
Blip3.readExternal
A String 47
*/
