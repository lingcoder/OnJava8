// patterns/recyclea/RecycleA.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Recycling with RTTI
// {java patterns.recyclea.RecycleA}
package patterns.recyclea;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

abstract class Trash {
  double weight;
  Trash(double wt) { weight = wt; }
  abstract double value();
  // Sums the value of Trash in a bin:
  private static double val;
  static void sumValue(List<? extends Trash> bin) {
    val = 0.0f;
    bin.forEach( t -> {
      // Polymorphism in action:
      val += t.weight * t.value();
      System.out.println(
        "weight of " +
        // Using RTTI to get type
        // information about the class:
        t.getClass().getSimpleName() +
        " = " + t.weight);
    });
    System.out.println("Total value = " + val);
  }
}

class Aluminum extends Trash {
  static double val  = 1.67f;
  Aluminum(double wt) { super(wt); }
  @Override
  double value() { return val; }
  static void value(double newval) {
    val = newval;
  }
}

class Paper extends Trash {
  static double val = 0.10f;
  Paper(double wt) { super(wt); }
  @Override
  double value() { return val; }
  static void value(double newval) {
    val = newval;
  }
}

class Glass extends Trash {
  static double val = 0.23f;
  Glass(double wt) { super(wt); }
  @Override
  double value() { return val; }
  static void value(double newval) {
    val = newval;
  }
}

class TrashFactory {
  static List<Function<Double, Trash>> ttypes =
    Arrays.asList(
      Aluminum::new, Paper::new, Glass::new);
  static final int SZ = ttypes.size();
  private static SplittableRandom rand =
    new SplittableRandom(47);
  public static Trash newTrash() {
    return ttypes
      .get(rand.nextInt(SZ))
      .apply(rand.nextDouble());
  }
}

public class RecycleA {
  public static void main(String[] args) {
    List<Trash> bin =
      Stream.generate(TrashFactory::newTrash)
        .limit(25)
        .collect(Collectors.toList());
    List<Glass> glassBin = new ArrayList<>();
    List<Paper> paperBin = new ArrayList<>();
    List<Aluminum> alBin = new ArrayList<>();
    // Sort the Trash:
    bin.forEach( t -> {
      // RTTI to discover Trash type:
      if(t instanceof Aluminum)
        alBin.add((Aluminum)t);
      if(t instanceof Paper)
        paperBin.add((Paper)t);
      if(t instanceof Glass)
        glassBin.add((Glass)t);
    });
    Trash.sumValue(alBin);
    Trash.sumValue(paperBin);
    Trash.sumValue(glassBin);
    Trash.sumValue(bin);
  }
}
/* Output: (First and Last 11 Lines)
weight of Aluminum = 0.2893030122276371
weight of Aluminum = 0.1970234961398979
weight of Aluminum = 0.36295525806274787
weight of Aluminum = 0.4825532324565849
weight of Aluminum = 0.8036398273294586
weight of Aluminum = 0.510430896154935
weight of Aluminum = 0.6703377164093444
weight of Aluminum = 0.41477933066243455
weight of Aluminum = 0.3603022312124007
weight of Aluminum = 0.43690089841661006
weight of Aluminum = 0.6708820087907101
...________...________...________...________...
weight of Aluminum = 0.41477933066243455
weight of Aluminum = 0.3603022312124007
weight of Aluminum = 0.43690089841661006
weight of Glass = 0.5999637765664924
weight of Glass = 0.7748836191212746
weight of Paper = 0.5735994548427199
weight of Glass = 0.5362827750851034
weight of Aluminum = 0.6708820087907101
weight of Paper = 0.8370669795210507
weight of Glass = 0.3397919679731668
Total value = 9.90671597531968
*/
