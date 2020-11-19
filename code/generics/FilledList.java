// generics/FilledList.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.function.*;
import onjava.*;

public class FilledList<T> extends ArrayList<T> {
  FilledList(Supplier<T> gen, int size) {
    Suppliers.fill(this, gen, size);
  }
  public FilledList(T t, int size) {
    for(int i = 0; i < size; i++)
      this.add(t);
  }
  public static void main(String[] args) {
    List<String> list = new FilledList<>("Hello", 4);
    System.out.println(list);
    // Supplier version:
    List<Integer> ilist = new FilledList<>(() -> 47, 4);
    System.out.println(ilist);
  }
}
/* Output:
[Hello, Hello, Hello, Hello]
[47, 47, 47, 47]
*/
