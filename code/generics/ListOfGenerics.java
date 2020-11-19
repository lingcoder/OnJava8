// generics/ListOfGenerics.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class ListOfGenerics<T> {
  private List<T> array = new ArrayList<>();
  public void add(T item) { array.add(item); }
  public T get(int index) { return array.get(index); }
}
