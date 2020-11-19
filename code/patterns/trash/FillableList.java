// patterns/trash/FillableList.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Adapter that makes a List Fillable
package patterns.trash;
import java.util.*;

public class FillableList<T extends Trash>
implements Fillable<T> {
  private List<T> v;
  public FillableList(List<T> vv) {
    v = vv;
  }
  @Override
  public void addTrash(T t) { v.add(t); }
}
