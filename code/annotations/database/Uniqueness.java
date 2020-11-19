// annotations/database/Uniqueness.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Sample of nested annotations
package annotations.database;

public @interface Uniqueness {
  Constraints constraints()
  default @Constraints(unique = true);
}
