// patterns/trash/Trash.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Base class for Trash recycling examples
package patterns.trash;
import java.util.*;
import java.lang.reflect.*;

public abstract class Trash {
  private double weight;
  public Trash(double wt) { weight = wt; }
  public Trash() {}
  public abstract double value();
  public double weight() { return weight; }
  // Sums the value of Trash in a bin:
  static double val;
  public static <T extends Trash>
  void sumValue(List<? extends T> bin) {
    val = 0.0f;
    bin.forEach( t -> {
      val += t.weight() * t.value();
      System.out.println("weight of " +
        // RTTI gets type information
        // about the class:
        t.getClass().getName() +
        " = " + t.weight());
    });
    System.out.println("Total value = " + val);
  }
  @Override
  public String toString() {
    // Print correct subclass name:
    return getClass().getName() +
      " w:" + weight() + " v:" +
      String.format("%.2f", value());
  }
  // Remainder of class supports dynamic creation:
  public static class CannotCreateTrashException
      extends RuntimeException {
    public CannotCreateTrashException(Exception why) {
      super(why);
    }
  }
  public static class TrashClassNotFoundException
      extends RuntimeException {
    public TrashClassNotFoundException(Exception why) {
      super(why);
    }
  }
  public static class Info {
    public String id;
    public double data;
    public Info(String name, double data) {
      id = name;
      this.data = data;
    }
  }
  private static List<Class> trashTypes =
    new ArrayList<>();
  @SuppressWarnings("unchecked")
  public static <T extends Trash> T factory(Info info) {
    for(Class trashType : trashTypes) {
      // Determine the type and create one:
      if(trashType.getName().contains(info.id)) {
        try {
          // Get the dynamic constructor method
          // that takes a double argument:
          Constructor ctor =
            trashType.getConstructor(double.class);
          // Call the constructor to create a
          // new object:
          return
            (T)ctor.newInstance(info.data);
        } catch(Exception e) {
          throw new CannotCreateTrashException(e);
        }
      }
    }
    // The necessary Class was not in the list. Try to
    // load it, but it must be in your class path!
    try {
      System.out.println("Loading " + info.id);
      trashTypes.add(Class.forName(info.id));
    } catch(Exception e) {
      throw new TrashClassNotFoundException(e);
    }
    // Loaded successfully. Recursive call
    // should work this time:
    return factory(info);
  }
}
