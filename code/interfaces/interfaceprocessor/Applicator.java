// interfaces/interfaceprocessor/Applicator.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package interfaces.interfaceprocessor;

public class Applicator {
  public static void apply(Processor p, Object s) {
    System.out.println("Using Processor " + p.name());
    System.out.println(p.process(s));
  }
}
