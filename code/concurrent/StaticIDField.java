// concurrent/StaticIDField.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class StaticIDField implements HasID {
  private static int counter = 0;
  private int id = counter++;
  public int getID() { return id; }
}
