// equalshashcode/Groundhog.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Looks plausible, but doesn't work as a HashMap key

public class Groundhog {
  protected int number;
  public Groundhog(int n) { number = n; }
  @Override
  public String toString() {
    return "Groundhog #" + number;
  }
}
