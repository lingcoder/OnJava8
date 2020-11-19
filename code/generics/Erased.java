// generics/Erased.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {WillNotCompile}

public class Erased<T> {
  private final int SIZE = 100;
  public void f(Object arg) {

    // error: illegal generic type for instanceof
    if(arg instanceof T) {}

    // error: unexpected type
    T var = new T();

    // error: generic array creation
    T[] array = new T[SIZE];

    // warning: [unchecked] unchecked cast
    T[] array = (T[])new Object[SIZE];

  }
}
