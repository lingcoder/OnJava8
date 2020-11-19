// patterns/trashvisitor/Glass.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Glass for the visitor pattern
package patterns.trashvisitor;
import patterns.trash.*;

public class Glass extends patterns.trash.Glass
    implements Visitable {
  public Glass(double wt) { super(wt); }
  @Override
  public void accept(Visitor v) {
    v.visit(this);
  }
}
