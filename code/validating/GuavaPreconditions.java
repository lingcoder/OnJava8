// validating/GuavaPreconditions.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Demonstrating Guava Preconditions
import java.util.function.*;
import static com.google.common.base.Preconditions.*;

public class GuavaPreconditions {
  static void test(Consumer<String> c, String s) {
    try {
      System.out.println(s);
      c.accept(s);
      System.out.println("Success");
    } catch(Exception e) {
      String type = e.getClass().getSimpleName();
      String msg = e.getMessage();
      System.out.println(type +
        (msg == null ? "" : ": " + msg));
    }
  }
  public static void main(String[] args) {
    test(s -> s = checkNotNull(s), "X");
    test(s -> s = checkNotNull(s), null);
    test(s -> s = checkNotNull(s, "s was null"), null);
    test(s -> s = checkNotNull(
      s, "s was null, %s %s", "arg2", "arg3"), null);

    test(s -> checkArgument(s == "Fozzie"), "Fozzie");
    test(s -> checkArgument(s == "Fozzie"), "X");
    test(s -> checkArgument(s == "Fozzie"), null);
    test(s -> checkArgument(
      s == "Fozzie", "Bear Left!"), null);
    test(s -> checkArgument(
      s == "Fozzie", "Bear Left! %s Right!", "Frog"),
      null);

    test(s -> checkState(s.length() > 6), "Mortimer");
    test(s -> checkState(s.length() > 6), "Mort");
    test(s -> checkState(s.length() > 6), null);

    test(s ->
      checkElementIndex(6, s.length()), "Robert");
    test(s ->
      checkElementIndex(6, s.length()), "Bob");
    test(s ->
      checkElementIndex(6, s.length()), null);

    test(s ->
      checkPositionIndex(6, s.length()), "Robert");
    test(s ->
      checkPositionIndex(6, s.length()), "Bob");
    test(s ->
      checkPositionIndex(6, s.length()), null);

    test(s -> checkPositionIndexes(
      0, 6, s.length()), "Hieronymus");
    test(s -> checkPositionIndexes(
      0, 10, s.length()), "Hieronymus");
    test(s -> checkPositionIndexes(
      0, 11, s.length()), "Hieronymus");
    test(s -> checkPositionIndexes(
      -1, 6, s.length()), "Hieronymus");
    test(s -> checkPositionIndexes(
      7, 6, s.length()), "Hieronymus");
    test(s -> checkPositionIndexes(
      0, 6, s.length()), null);
  }
}
/* Output:
X
Success
null
NullPointerException
null
NullPointerException: s was null
null
NullPointerException: s was null, arg2 arg3
Fozzie
Success
X
IllegalArgumentException
null
IllegalArgumentException
null
IllegalArgumentException: Bear Left!
null
IllegalArgumentException: Bear Left! Frog Right!
Mortimer
Success
Mort
IllegalStateException
null
NullPointerException
Robert
IndexOutOfBoundsException: index (6) must be less than
size (6)
Bob
IndexOutOfBoundsException: index (6) must be less than
size (3)
null
NullPointerException
Robert
Success
Bob
IndexOutOfBoundsException: index (6) must not be
greater than size (3)
null
NullPointerException
Hieronymus
Success
Hieronymus
Success
Hieronymus
IndexOutOfBoundsException: end index (11) must not be
greater than size (10)
Hieronymus
IndexOutOfBoundsException: start index (-1) must not be
negative
Hieronymus
IndexOutOfBoundsException: end index (6) must not be
less than start index (7)
null
NullPointerException
*/
