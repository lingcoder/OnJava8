// serialization/Logon.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Demonstrates the "transient" keyword
import java.util.concurrent.*;
import java.io.*;
import java.util.*;
import onjava.Nap;

public class Logon implements Serializable {
  private Date date = new Date();
  private String username;
  private transient String password;
  public Logon(String name, String pwd) {
    username = name;
    password = pwd;
  }
  @Override
  public String toString() {
    return "logon info: \n   username: " +
      username + "\n   date: " + date +
      "\n   password: " + password;
  }
  public static void main(String[] args) {
    Logon a = new Logon("Hulk", "myLittlePony");
    System.out.println("logon a = " + a);
    try(
      ObjectOutputStream o =
        new ObjectOutputStream(
          new FileOutputStream("Logon.dat"))
    ) {
      o.writeObject(a);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    new Nap(1);
    // Now get them back:
    try(
      ObjectInputStream in = new ObjectInputStream(
        new FileInputStream("Logon.dat"))
    ) {
      System.out.println(
        "Recovering object at " + new Date());
      a = (Logon)in.readObject();
    } catch(IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    System.out.println("logon a = " + a);
  }
}
/* Output:
logon a = logon info:
   username: Hulk
   date: Tue May 09 06:07:47 MDT 2017
   password: myLittlePony
Recovering object at Tue May 09 06:07:49 MDT 2017
logon a = logon info:
   username: Hulk
   date: Tue May 09 06:07:47 MDT 2017
   password: null
*/
