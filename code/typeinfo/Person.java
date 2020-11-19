// typeinfo/Person.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using Optional with regular classes
import onjava.*;
import java.util.*;

class Person {
  public final Optional<String> first;
  public final Optional<String> last;
  public final Optional<String> address;
  // etc.
  public final boolean empty;
  Person(String first, String last, String address) {
    this.first = Optional.ofNullable(first);
    this.last = Optional.ofNullable(last);
    this.address = Optional.ofNullable(address);
    empty = !this.first.isPresent()
         && !this.last.isPresent()
         && !this.address.isPresent();
  }
  Person(String first, String last) {
    this(first, last, null);
  }
  Person(String last) { this(null, last, null); }
  Person() { this(null, null, null); }
  @Override
  public String toString() {
    if(empty)
      return "<Empty>";
    return (first.orElse("") +
      " " + last.orElse("") +
      " " + address.orElse("")).trim();
  }
  public static void main(String[] args) {
    System.out.println(new Person());
    System.out.println(new Person("Smith"));
    System.out.println(new Person("Bob", "Smith"));
    System.out.println(new Person("Bob", "Smith",
      "11 Degree Lane, Frostbite Falls, MN"));
  }
}
/* Output:
<Empty>
Smith
Bob Smith
Bob Smith 11 Degree Lane, Frostbite Falls, MN
*/
