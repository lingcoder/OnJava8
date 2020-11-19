// patterns/observer/ObservedFlower.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Demonstration of "Observer" pattern
// {java patterns.observer.ObservedFlower}
package patterns.observer;
import java.util.*;

@SuppressWarnings("deprecation")
class Flower {
  private boolean isOpen;
  private boolean alreadyOpen;
  private boolean alreadyClosed;
  Flower() { isOpen = false; }
  OpenNotifier opening = new OpenNotifier();
  CloseNotifier closing = new CloseNotifier();
  public void open() { // Opens its petals
    isOpen = true;
    opening.notifyObservers();
    alreadyClosed = false;
  }
  public void close() { // Closes its petals
    isOpen = false;
    closing.notifyObservers();
    alreadyOpen = false;
  }
  class OpenNotifier extends Observable {
    @Override
    public void notifyObservers() {
      if(isOpen && !alreadyOpen) {
        setChanged();
        super.notifyObservers();
        alreadyOpen = true;
      }
    }
  }
  class CloseNotifier extends Observable{
    @Override
    public void notifyObservers() {
      if(!isOpen && !alreadyClosed) {
        setChanged();
        super.notifyObservers();
        alreadyClosed = true;
      }
    }
  }
}

@SuppressWarnings("deprecation")
class Bee {
  private String name;
  Bee(String nm)  { name = nm; }
  // Observe openings:
  public Observer openObserver() {
    return (ob, a) -> System.out.println(
      "Bee " + name + "'s breakfast time!");
  }
  // Observe closings:
  public Observer closeObserver() {
    return (ob, a) -> System.out.println(
      "Bee " + name + "'s bed time!");
  }
}

@SuppressWarnings("deprecation")
class Hummingbird {
  private String name;
  Hummingbird(String nm) { name = nm; }
  public Observer openObserver() {
    return (ob, a) -> System.out.println(
      "Hummingbird " + name +
      "'s breakfast time!");
  }
  public Observer closeObserver() {
    return (ob, a) -> System.out.println(
      "Hummingbird " + name + "'s bed time!");
  }
}

public class ObservedFlower {
  public static void main(String[] args) {
    Flower f = new Flower();
    Bee
      ba = new Bee("A"),
      bb = new Bee("B");
    Hummingbird
      ha = new Hummingbird("A"),
      hb = new Hummingbird("B");
    f.opening.addObserver(ha.openObserver());
    f.opening.addObserver(hb.openObserver());
    f.opening.addObserver(ba.openObserver());
    f.opening.addObserver(bb.openObserver());
    f.closing.addObserver(ha.closeObserver());
    f.closing.addObserver(hb.closeObserver());
    f.closing.addObserver(ba.closeObserver());
    f.closing.addObserver(bb.closeObserver());
    // Hummingbird B decides to sleep in:
    f.opening.deleteObserver(hb.openObserver());
    // A change that interests observers:
    f.open();
    f.open(); // It's already open, no change.
    // Bee A doesn't want to go to bed:
    f.closing.deleteObserver(ba.closeObserver());
    f.close();
    f.close(); // It's already closed; no change
    f.opening.deleteObservers();
    f.open();
    f.close();
  }
}
/* Output:
Bee B's breakfast time!
Bee A's breakfast time!
Hummingbird B's breakfast time!
Hummingbird A's breakfast time!
Bee B's bed time!
Bee A's bed time!
Hummingbird B's bed time!
Hummingbird A's bed time!
Bee B's bed time!
Bee A's bed time!
Hummingbird B's bed time!
Hummingbird A's bed time!
*/
