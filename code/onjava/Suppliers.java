// onjava/Suppliers.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A utility to use with Suppliers
package onjava;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Suppliers {
  // Create a collection and fill it:
  public static <T, C extends Collection<T>> C
  create(Supplier<C> factory, Supplier<T> gen, int n) {
    return Stream.generate(gen)
      .limit(n)
      .collect(factory, C::add, C::addAll);
  }
  // Fill an existing collection:
  public static <T, C extends Collection<T>>
  C fill(C coll, Supplier<T> gen, int n) {
    Stream.generate(gen)
      .limit(n)
      .forEach(coll::add);
    return coll;
  }
  // Use an unbound method reference to
  // produce a more general method:
  public static <H, A> H fill(H holder,
    BiConsumer<H, A> adder, Supplier<A> gen, int n) {
    Stream.generate(gen)
      .limit(n)
      .forEach(a -> adder.accept(holder, a));
    return holder;
  }
}
