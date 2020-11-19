// typeinfo/Position.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

class EmptyTitleException extends RuntimeException {}

class Position {
  private String title;
  private Person person;
  Position(String jobTitle, Person employee) {
    setTitle(jobTitle);
    setPerson(employee);
  }
  Position(String jobTitle) {
    this(jobTitle, null);
  }
  public String getTitle() { return title; }
  public void setTitle(String newTitle) {
    // Throws EmptyTitleException if newTitle is null:
    title = Optional.ofNullable(newTitle)
      .orElseThrow(EmptyTitleException::new);
  }
  public Person getPerson() { return person; }
  public void setPerson(Person newPerson) {
    // Uses empty Person if newPerson is null:
    person = Optional.ofNullable(newPerson)
      .orElse(new Person());
  }
  @Override
  public String toString() {
    return "Position: " + title +
      ", Employee: " + person;
  }
  public static void main(String[] args) {
    System.out.println(new Position("CEO"));
    System.out.println(new Position("Programmer",
      new Person("Arthur", "Fonzarelli")));
    try {
      new Position(null);
    } catch(Exception e) {
      System.out.println("caught " + e);
    }
  }
}
/* Output:
Position: CEO, Employee: <Empty>
Position: Programmer, Employee: Arthur Fonzarelli
caught EmptyTitleException
*/
