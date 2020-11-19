// serialization/People.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// nu.xom.Node comes from http://www.xom.nu
// {RunFirst: APerson}
import nu.xom.*;
import java.io.File;
import java.util.*;

public class People extends ArrayList<APerson> {
  public People(String fileName) throws Exception  {
    Document doc =
      new Builder().build(new File(fileName));
    Elements elements =
      doc.getRootElement().getChildElements();
    for(int i = 0; i < elements.size(); i++)
      add(new APerson(elements.get(i)));
  }
  public static void
  main(String[] args) throws Exception {
    People p = new People("People.xml");
    System.out.println(p);
  }
}
/* Output:
[Dr. Bunsen Honeydew, Gonzo The Great, Phillip J. Fry]
*/
