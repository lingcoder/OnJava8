// annotations/StackL.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A stack built on a LinkedList
package annotations;
import java.util.*;

public class StackL<T> {
  private LinkedList<T> list = new LinkedList<>();
  public void push(T v) { list.addFirst(v); }
  public T top() { return list.getFirst(); }
  public T pop() { return list.removeFirst(); }
}
