// onjava/Tuple.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Tuple library using type argument inference
package onjava;

public class Tuple {
  public static <A, B> Tuple2<A, B> tuple(A a, B b) {
    return new Tuple2<>(a, b);
  }
  public static <A, B, C> Tuple3<A, B, C>
  tuple(A a, B b, C c) {
    return new Tuple3<>(a, b, c);
  }
  public static <A, B, C, D> Tuple4<A, B, C, D>
  tuple(A a, B b, C c, D d) {
    return new Tuple4<>(a, b, c, d);
  }
  public static <A, B, C, D, E>
  Tuple5<A, B, C, D, E> tuple(A a, B b, C c, D d, E e) {
    return new Tuple5<>(a, b, c, d, e);
  }
}
