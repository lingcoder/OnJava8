// equalshashcode/SuccinctEquality.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class SuccinctEquality extends Equality {
  public SuccinctEquality(int i, String s, double d) {
    super(i, s, d);
    System.out.println("made 'SuccinctEquality'");
  }
  @Override
  public boolean equals(Object rval) {
    return rval instanceof SuccinctEquality &&
      Objects.equals(i, ((SuccinctEquality)rval).i) &&
      Objects.equals(s, ((SuccinctEquality)rval).s) &&
      Objects.equals(d, ((SuccinctEquality)rval).d);
  }
  public static void main(String[] args) {
    Equality.testAll( (i, s, d) ->
      new SuccinctEquality(i, s, d));
  }
}
/* Output:
made 'Equality'
made 'SuccinctEquality'
made 'Equality'
made 'SuccinctEquality'
made 'Equality'
made 'SuccinctEquality'
-- Testing null --
null instanceof Equality: false
Expected false, got false
-- Testing same object --
same object instanceof Equality: true
Expected true, got true
-- Testing different type --
different type instanceof Equality: false
Expected false, got false
-- Testing same values --
same values instanceof Equality: true
Expected true, got true
-- Testing different values --
different values instanceof Equality: true
Expected false, got false
*/
