// patterns/trash/Fillable.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Any object that can be filled with Trash
package patterns.trash;

public interface Fillable<T extends Trash> {
  void addTrash(T t);
}
