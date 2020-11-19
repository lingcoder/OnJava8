// serialization/APerson.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Use the XOM library to write and read XML
// nu.xom.Node comes from http://www.xom.nu
import nu.xom.*;
import java.io.*;
import java.util.*;

public class APerson {
  private String first, last;
  public APerson(String first, String last) {
    this.first = first;
    this.last = last;
  }
  // Produce an XML Element from this APerson object:
  public Element getXML() {
    Element person = new Element("person");
    Element firstName = new Element("first");
    firstName.appendChild(first);
    Element lastName = new Element("last");
    lastName.appendChild(last);
    person.appendChild(firstName);
    person.appendChild(lastName);
    return person;
  }
  // Constructor restores a APerson from XML:
  public APerson(Element person) {
    first = person
      .getFirstChildElement("first").getValue();
    last = person
      .getFirstChildElement("last").getValue();
  }
  @Override
  public String toString() {
    return first + " " + last;
  }
  // Make it human-readable:
  public static void
  format(OutputStream os, Document doc)
  throws Exception {
    Serializer serializer =
      new Serializer(os,"ISO-8859-1");
    serializer.setIndent(4);
    serializer.setMaxLength(60);
    serializer.write(doc);
    serializer.flush();
  }
  public static void
  main(String[] args) throws Exception {
    List<APerson> people = Arrays.asList(
      new APerson("Dr. Bunsen", "Honeydew"),
      new APerson("Gonzo", "The Great"),
      new APerson("Phillip J.", "Fry"));
    System.out.println(people);
    Element root = new Element("people");
    for(APerson p : people)
      root.appendChild(p.getXML());
    Document doc = new Document(root);
    format(System.out, doc);
    format(new BufferedOutputStream(
      new FileOutputStream("People.xml")), doc);
  }
}
/* Output:
[Dr. Bunsen Honeydew, Gonzo The Great, Phillip J. Fry]
<?xml version="1.0" encoding="ISO-8859-1"?>
<people>
    <person>
        <first>Dr. Bunsen</first>
        <last>Honeydew</last>
    </person>
    <person>
        <first>Gonzo</first>
        <last>The Great</last>
    </person>
    <person>
        <first>Phillip J.</first>
        <last>Fry</last>
    </person>
</people>
*/
