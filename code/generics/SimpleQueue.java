// generics/SimpleQueue.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A different kind of Iterable collection
import java.util.*;

public class SimpleQueue<T> implements Iterable<T> {
  private LinkedList<T> storage = new LinkedList<>();
  public void add(T t) { storage.offer(t); }
  public T get() { return storage.poll(); }
  @Override
  public Iterator<T> iterator() {
    return storage.iterator();
  }
}
