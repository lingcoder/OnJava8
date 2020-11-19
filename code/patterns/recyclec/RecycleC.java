// patterns/recyclec/RecycleC.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Adding more objects to the recycling problem
// {java patterns.recyclec.RecycleC}
package patterns.recyclec;
import patterns.trash.*;
import java.util.*;

// A List that admits only the right type:
class Tbin<T extends Trash> extends ArrayList<T> {
  Class<T> binType;
  Tbin(Class<T> type) {
    binType = type;
  }
  @SuppressWarnings("unchecked")
  boolean grab(Trash t) {
    // Comparing class types:
    if(t.getClass().equals(binType)) {
      add((T)t); // Downcast to this TBin's type
      return true; // Object grabbed
    }
    return false; // Object not grabbed
  }
}

class TbinList<T extends Trash>
extends ArrayList<Tbin<? extends T>> { // [1]
  boolean sort(T t) {
    for(Tbin<? extends T> ts : this)
      if(ts.grab(t))
        return true;
    return false; // bin not found for t
  }
  void sortBin(Tbin<T> bin) { // [2]
    for(T aBin : bin)
      if(!sort(aBin))
        System.err.println("Bin not found");
  }
}

public class RecycleC {
  static Tbin<Trash> bin = new Tbin<>(Trash.class);
  public static void main(String[] args) {
    // Fill up the Trash bin:
    ParseTrash.fillBin("trash", bin);

    TbinList<Trash> trashBins = new TbinList<>();
    trashBins.add(new Tbin<>(Aluminum.class));
    trashBins.add(new Tbin<>(Paper.class));
    trashBins.add(new Tbin<>(Glass.class));
    // add one line here: [*3*]
    trashBins.add(new Tbin<>(Cardboard.class));

    trashBins.sortBin(bin); // [4]

    trashBins.forEach(Trash::sumValue);
    Trash.sumValue(bin);
  }
}
/* Output: (First and Last 10 Lines)
Loading patterns.trash.Glass
Loading patterns.trash.Paper
Loading patterns.trash.Aluminum
Loading patterns.trash.Cardboard
weight of patterns.trash.Aluminum = 89.0
weight of patterns.trash.Aluminum = 76.0
weight of patterns.trash.Aluminum = 25.0
weight of patterns.trash.Aluminum = 34.0
weight of patterns.trash.Aluminum = 27.0
weight of patterns.trash.Aluminum = 18.0
...________...________...________...________...
weight of patterns.trash.Aluminum = 93.0
weight of patterns.trash.Glass = 93.0
weight of patterns.trash.Paper = 80.0
weight of patterns.trash.Glass = 36.0
weight of patterns.trash.Glass = 12.0
weight of patterns.trash.Glass = 60.0
weight of patterns.trash.Paper = 66.0
weight of patterns.trash.Aluminum = 36.0
weight of patterns.trash.Cardboard = 22.0
Total value = 1086.0599818825722
*/
