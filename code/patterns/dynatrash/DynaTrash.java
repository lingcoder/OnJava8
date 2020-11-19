// patterns/dynatrash/DynaTrash.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using a Map of Lists and RTTI to automatically
// sort trash into Lists. This solution, despite
// the use of RTTI, is extensible.
// {java patterns.dynatrash.DynaTrash}
package patterns.dynatrash;
import patterns.trash.*;
import java.util.*;
import java.util.stream.*;

// Generic TypeMap works in any situation:
class TypeMap<T> {
  private Map<Class,List<T>> t = new HashMap<>();
  public void add(T o) {
    Class type = o.getClass();
    if(t.containsKey(type))
      t.get(type).add(o);
    else {
      List<T> v = new ArrayList<>();
      v.add(o);
      t.put(type,v);
    }
  }
  public Stream<List<T>> values() {
    return t.values().stream();
  }
}

// Adapter class for callbacks
// from ParseTrash.fillBin():
class TypeMapAdapter implements Fillable {
  TypeMap<Trash> map;
  TypeMapAdapter(TypeMap<Trash> tm) {
    map = tm;
  }
  @Override
  public void addTrash(Trash t) { map.add(t); }
}

public class DynaTrash {
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    TypeMap<Trash> bin = new TypeMap<>();
    ParseTrash.fillBin(
      "trash", new TypeMapAdapter(bin));
    bin.values().forEach(Trash::sumValue);
  }
}
/* Output: (First and Last 10 Lines)
Loading patterns.trash.Glass
Loading patterns.trash.Paper
Loading patterns.trash.Aluminum
Loading patterns.trash.Cardboard
weight of patterns.trash.Paper = 22.0
weight of patterns.trash.Paper = 11.0
weight of patterns.trash.Paper = 88.0
weight of patterns.trash.Paper = 91.0
weight of patterns.trash.Paper = 80.0
weight of patterns.trash.Paper = 66.0
...________...________...________...________...
weight of patterns.trash.Aluminum = 81.0
weight of patterns.trash.Aluminum = 36.0
weight of patterns.trash.Aluminum = 93.0
weight of patterns.trash.Aluminum = 36.0
Total value = 860.0499778985977
weight of patterns.trash.Cardboard = 96.0
weight of patterns.trash.Cardboard = 44.0
weight of patterns.trash.Cardboard = 12.0
weight of patterns.trash.Cardboard = 22.0
Total value = 40.02000072598457
*/
