// equalshashcode/ComposedEquality.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

class Part {
  String ss;
  double dd;
  Part(String ss, double dd) {
    this.ss = ss;
    this.dd = dd;
  }
  @Override
  public boolean equals(Object rval) {
    return rval instanceof Part &&
      Objects.equals(ss, ((Part)rval).ss) &&
      Objects.equals(dd, ((Part)rval).dd);
  }
}

public class ComposedEquality extends SuccinctEquality {
  Part part;
  public ComposedEquality(int i, String s, double d) {
    super(i, s, d);
    part = new Part(s, d);
    System.out.println("made 'ComposedEquality'");
  }
  @Override
  public boolean equals(Object rval) {
    return rval instanceof ComposedEquality &&
      super.equals(rval) &&
      Objects.equals(part,
        ((ComposedEquality)rval).part);
  }
  public static void main(String[] args) {
    Equality.testAll( (i, s, d) ->
      new ComposedEquality(i, s, d));
  }
}
/* Output:
made 'Equality'
made 'SuccinctEquality'
made 'ComposedEquality'
made 'Equality'
made 'SuccinctEquality'
made 'ComposedEquality'
made 'Equality'
made 'SuccinctEquality'
made 'ComposedEquality'
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
