// references/CopyConstructor.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A constructor to copy an object of the same
// type, as an attempt to create a local copy
import java.lang.reflect.*;

class FruitQualities {
  private int weight;
  private int color;
  private int firmness;
  private int ripeness;
  private int smell;
  // etc.
  // No-arg constructor:
  FruitQualities() {
    // Do something meaningful...
  }
  // Other constructors:
  // ...
  // Copy constructor:
  FruitQualities(FruitQualities f) {
    weight = f.weight;
    color = f.color;
    firmness = f.firmness;
    ripeness = f.ripeness;
    smell = f.smell;
    // etc.
  }
}

class Seed {
  // Members...
  Seed() { /* No-arg constructor */ }
  Seed(Seed s) { /* Copy constructor */ }
}

class Fruit {
  private FruitQualities fq;
  private int seeds;
  private Seed[] s;
  Fruit(FruitQualities q, int seedCount) {
    fq = q;
    seeds = seedCount;
    s = new Seed[seeds];
    for(int i = 0; i < seeds; i++)
      s[i] = new Seed();
  }
  // Other constructors:
  // ...
  // Copy constructor:
  Fruit(Fruit f) {
    fq = new FruitQualities(f.fq);
    seeds = f.seeds;
    s = new Seed[seeds];
    // Call all Seed copy-constructors:
    for(int i = 0; i < seeds; i++)
      s[i] = new Seed(f.s[i]);
    // Other copy-construction activities...
  }
  // This allows derived constructors (or other
  // methods) to put in different qualities:
  protected void addQualities(FruitQualities q) {
    fq = q;
  }
  protected FruitQualities getQualities() {
    return fq;
  }
}

class Tomato extends Fruit {
  Tomato() {
    super(new FruitQualities(), 100);
  }
  Tomato(Tomato t) { // Copy-constructor
    super(t); // Upcast to base copy-constructor
    // Other copy-construction activities...
  }
}

class ZebraQualities extends FruitQualities {
  private int stripedness;
  // No-arg constructor:
  ZebraQualities() {
    super();
    // do something meaningful...
  }
  ZebraQualities(ZebraQualities z) {
    super(z);
    stripedness = z.stripedness;
  }
}

class GreenZebra extends Tomato {
  GreenZebra() {
    addQualities(new ZebraQualities());
  }
  GreenZebra(GreenZebra g) {
    super(g); // Calls Tomato(Tomato)
    // Restore the right qualities:
    addQualities(new ZebraQualities());
  }
  public void evaluate() {
    ZebraQualities zq =
      (ZebraQualities)getQualities();
    // Do something with the qualities
    // ...
  }
}

public class CopyConstructor {
  public static void ripen(Tomato t) {
    // Use the "copy constructor":
    t = new Tomato(t); // [1]
    System.out.println("In ripen, t is a " +
      t.getClass().getName());
  }
  public static void slice(Fruit f) {
    f = new Fruit(f); // [2] Hmmm... will this work?
    System.out.println("In slice, f is a " +
      f.getClass().getName());
  }
  @SuppressWarnings("unchecked")
  public static void ripen2(Tomato t) {
    try {
      Class c = t.getClass();
      // Use the "copy constructor":
      Constructor ct =
        c.getConstructor(new Class[] { c });
      Object obj =
        ct.newInstance(new Object[] { t });
      System.out.println("In ripen2, t is a " +
        obj.getClass().getName());
    } catch(NoSuchMethodException |
            SecurityException |
            InstantiationException |
            IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException e) {
      System.out.println(e);
    }
  }
  @SuppressWarnings("unchecked")
  public static void slice2(Fruit f) {
    try {
      Class c = f.getClass();
      Constructor ct =
        c.getConstructor(new Class[] { c });
      Object obj =
        ct.newInstance(new Object[] { f });
      System.out.println("In slice2, f is a " +
        obj.getClass().getName());
    } catch(NoSuchMethodException |
            SecurityException |
            InstantiationException |
            IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException e) {
      System.out.println(e);
    }
  }
  public static void main(String[] args) {
    Tomato tomato = new Tomato();
    ripen(tomato); // OK
    slice(tomato); // OOPS!
    ripen2(tomato); // OK
    slice2(tomato); // OK
    GreenZebra g = new GreenZebra();
    ripen(g); // OOPS!
    slice(g); // OOPS!
    ripen2(g); // OK
    slice2(g); // OK
    g.evaluate();
  }
}
/* Output:
In ripen, t is a Tomato
In slice, f is a Fruit
java.lang.NoSuchMethodException: Tomato.<init>(Tomato)
java.lang.NoSuchMethodException: Tomato.<init>(Tomato)
In ripen, t is a Tomato
In slice, f is a Fruit
java.lang.NoSuchMethodException:
GreenZebra.<init>(GreenZebra)
java.lang.NoSuchMethodException:
GreenZebra.<init>(GreenZebra)
*/
