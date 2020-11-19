// typeinfo/DynamicSupplier.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;
import java.util.stream.*;

class CountedInteger {
  private static long counter;
  private final long id = counter++;
  @Override
  public String toString() { return Long.toString(id); }
}

public class DynamicSupplier<T> implements Supplier<T> {
  private Class<T> type;
  public DynamicSupplier(Class<T> type) {
    this.type = type;
  }
  @SuppressWarnings("deprecation")
  public T get() {
    try {
      return type.newInstance();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
  public static void main(String[] args) {
    Stream.generate(
      new DynamicSupplier<>(CountedInteger.class))
      .skip(10)
      .limit(5)
      .forEach(System.out::println);
  }
}
/* Output:
10
11
12
13
14
*/
