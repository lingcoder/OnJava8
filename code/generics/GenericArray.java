// generics/GenericArray.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class GenericArray<T> {
  private T[] array;
  @SuppressWarnings("unchecked")
  public GenericArray(int sz) {
    array = (T[])new Object[sz];
  }
  public void put(int index, T item) {
    array[index] = item;
  }
  public T get(int index) { return array[index]; }
  // Method that exposes the underlying representation:
  public T[] rep() { return array; }
  public static void main(String[] args) {
    GenericArray<Integer> gai = new GenericArray<>(10);
    try {
      Integer[] ia = gai.rep();
    } catch(ClassCastException e) {
      System.out.println(e.getMessage());
    }
    // This is OK:
    Object[] oa = gai.rep();
  }
}
/* Output:
[Ljava.lang.Object; cannot be cast to
[Ljava.lang.Integer;
*/
