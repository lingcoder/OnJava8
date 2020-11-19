// arrays/ArrayOfGenericType.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class ArrayOfGenericType<T> {
  T[] array; // OK
  @SuppressWarnings("unchecked")
  public ArrayOfGenericType(int size) {
    // error: generic array creation:
    //- array = new T[size];
    array = (T[])new Object[size]; // unchecked cast
  }
  // error: generic array creation:
  //- public <U> U[] makeArray() { return new U[10]; }
}
