// generics/GenericHolder2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class GenericHolder2<T> {
  private T obj;
  public void set(T obj) { this.obj = obj; }
  public T get() { return obj; }
  public static void main(String[] args) {
    GenericHolder2<String> holder =
      new GenericHolder2<>();
    holder.set("Item");
    String s = holder.get();
  }
}
