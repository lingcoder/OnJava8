// generics/ReturnGenericType.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

class ReturnGenericType<T extends HasF> {
  private T obj;
  ReturnGenericType(T x) { obj = x; }
  public T get() { return obj; }
}
