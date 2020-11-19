// generics/CountedObject.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class CountedObject {
  private static long counter = 0;
  private final long id = counter++;
  public long id() { return id; }
  @Override
  public String toString() {
    return "CountedObject " + id;
  }
}
