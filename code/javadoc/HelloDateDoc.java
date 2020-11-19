// javadoc/HelloDateDoc.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

/** The first On Java 8 example program.
 * Displays a String and today's date.
 * @author Bruce Eckel
 * @author www.MindviewInc.com
 * @version 5.0
 */
public class HelloDateDoc {
  /** Entry point to class & application.
   * @param args array of String arguments
   * @throws exceptions No exceptions thrown
   */
  public static void main(String[] args) {
    System.out.println("Hello, it's: ");
    System.out.println(new Date());
  }
}
/* Output:
Hello, it's:
Tue May 09 06:07:27 MDT 2017
*/
