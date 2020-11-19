// generics/GenericCast.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

class FixedSizeStack<T> {
  private final int size;
  private Object[] storage;
  private int index = 0;
  FixedSizeStack(int size) {
    this.size = size;
    storage = new Object[size];
  }
  public void push(T item) {
    if(index < size)
      storage[index++] = item;
  }
  @SuppressWarnings("unchecked")
  public T pop() {
    return index == 0 ? null : (T)storage[--index];
  }
  @SuppressWarnings("unchecked")
  Stream<T> stream() {
    return (Stream<T>)Arrays.stream(storage);
  }
}

public class GenericCast {
  static String[] letters =
    "ABCDEFGHIJKLMNOPQRS".split("");
  public static void main(String[] args) {
    FixedSizeStack<String> strings =
      new FixedSizeStack<>(letters.length);
    Arrays.stream("ABCDEFGHIJKLMNOPQRS".split(""))
      .forEach(strings::push);
    System.out.println(strings.pop());
    strings.stream()
      .map(s -> s + " ")
      .forEach(System.out::print);
  }
}
/* Output:
S
A B C D E F G H I J K L M N O P Q R S
*/
