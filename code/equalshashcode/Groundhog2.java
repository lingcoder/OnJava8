// equalshashcode/Groundhog2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A class that's used as a key in a HashMap
// must override hashCode() and equals()
import java.util.*;

public class Groundhog2 extends Groundhog {
  public Groundhog2(int n) { super(n); }
  @Override
  public int hashCode() { return number; }
  @Override
  public boolean equals(Object o) {
    return o instanceof Groundhog2 &&
      Objects.equals(
        number, ((Groundhog2)o).number);
  }
}
