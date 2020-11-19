// annotations/database/Member.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package annotations.database;

@DBTable(name = "MEMBER")
public class Member {
  @SQLString(30) String firstName;
  @SQLString(50) String lastName;
  @SQLInteger Integer age;
  @SQLString(value = 30,
  constraints = @Constraints(primaryKey = true))
  String reference;
  static int memberCount;
  public String getReference() { return reference; }
  public String getFirstName() { return firstName; }
  public String getLastName() { return lastName; }
  @Override
  public String toString() { return reference; }
  public Integer getAge() { return age; }
}
