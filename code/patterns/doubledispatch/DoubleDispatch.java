// patterns/doubledispatch/DoubleDispatch.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using multiple dispatching to handle more
// than one unknown type during a method call
// {java patterns.doubledispatch.DoubleDispatch}
package patterns.doubledispatch;
import patterns.trash.*;
import java.util.*;

class AluminumBin extends TypedBin {
  @Override
  public boolean add(Aluminum a) {
    return addIt(a);
  }
}

class PaperBin extends TypedBin {
  @Override
  public boolean add(Paper a) {
    return addIt(a);
  }
}

class GlassBin extends TypedBin {
  @Override
  public boolean add(Glass a) {
    return addIt(a);
  }
}

class CardboardBin extends TypedBin {
  @Override
  public boolean add(Cardboard a) {
    return addIt(a);
  }
}

class TrashBinSet {
  private List<TypedBin> binSet = Arrays.asList(
    new AluminumBin(),
    new PaperBin(),
    new GlassBin(),
    new CardboardBin()
  );
  @SuppressWarnings("unchecked")
  public void sortIntoBins(List bin) {
    bin.forEach( aBin -> {
      TypedBinMember t = (TypedBinMember)aBin;
      if(!t.addToBin(binSet))
        System.err.println("Couldn't add " + t);
    });
  }
  public List<TypedBin> binSet() { return binSet; }
}

public class DoubleDispatch {
  public static void main(String[] args) {
    List<Trash> bin = new ArrayList<>();
    TrashBinSet bins = new TrashBinSet();
    // ParseTrash still works, without changes:
    ParseTrash.fillBin("doubledispatch", bin);
    // Sort from the master bin into the
    // individually-typed bins:
    bins.sortIntoBins(bin);
    // Perform sumValue for each bin...
    bins.binSet()
      .forEach(tb -> Trash.sumValue(tb.v));
    // ... and for the master bin
    Trash.sumValue(bin);
  }
}
/* Output: (First and Last 10 Lines)
Loading patterns.doubledispatch.Glass
Loading patterns.doubledispatch.Paper
Loading patterns.doubledispatch.Aluminum
Loading patterns.doubledispatch.Cardboard
weight of patterns.doubledispatch.Aluminum = 89.0
weight of patterns.doubledispatch.Aluminum = 76.0
weight of patterns.doubledispatch.Aluminum = 25.0
weight of patterns.doubledispatch.Aluminum = 34.0
weight of patterns.doubledispatch.Aluminum = 27.0
weight of patterns.doubledispatch.Aluminum = 18.0
...________...________...________...________...
weight of patterns.doubledispatch.Aluminum = 93.0
weight of patterns.doubledispatch.Glass = 93.0
weight of patterns.doubledispatch.Paper = 80.0
weight of patterns.doubledispatch.Glass = 36.0
weight of patterns.doubledispatch.Glass = 12.0
weight of patterns.doubledispatch.Glass = 60.0
weight of patterns.doubledispatch.Paper = 66.0
weight of patterns.doubledispatch.Aluminum = 36.0
weight of patterns.doubledispatch.Cardboard = 22.0
Total value = 1086.0599818825722
*/
