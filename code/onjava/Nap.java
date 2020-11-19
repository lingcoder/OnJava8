// onjava/Nap.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package onjava;
import java.util.concurrent.*;

public class Nap {
  public Nap(double t) { // Seconds
    try {
      TimeUnit.MILLISECONDS.sleep((int)(1000 * t));
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
  public Nap(double t, String msg) {
    this(t);
    System.out.println(msg);
  }
}
