// onjava/BasicSupplier.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Supplier from a class with a no-arg constructor
package onjava;
import java.util.function.*;
import java.lang.reflect.InvocationTargetException;

public class BasicSupplier<T> implements Supplier<T> {
  private Class<T> type;
  public BasicSupplier(Class<T> type) {
    this.type = type;
  }
  @Override
  public T get() {
    try {
      // Assumes type is a public class:
      return type.getConstructor().newInstance();
    } catch(InstantiationException |
            NoSuchMethodException |
            InvocationTargetException |
            IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
  // Produce a default Supplier from a type token:
  public static <T> Supplier<T> create(Class<T> type) {
    return new BasicSupplier<>(type);
  }
}
