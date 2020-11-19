// functional/ClassFunctionals.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.function.*;

class AA {}
class BB {}
class CC {}

public class ClassFunctionals {
  static AA f1() { return new AA(); }
  static int f2(AA aa1, AA aa2) { return 1; }
  static void f3(AA aa) {}
  static void f4(AA aa, BB bb) {}
  static CC f5(AA aa) { return new CC(); }
  static CC f6(AA aa, BB bb) { return new CC(); }
  static boolean f7(AA aa) { return true; }
  static boolean f8(AA aa, BB bb) { return true; }
  static AA f9(AA aa) { return new AA(); }
  static AA f10(AA aa1, AA aa2) { return new AA(); }
  public static void main(String[] args) {
    Supplier<AA> s = ClassFunctionals::f1;
    s.get();
    Comparator<AA> c = ClassFunctionals::f2;
    c.compare(new AA(), new AA());
    Consumer<AA> cons = ClassFunctionals::f3;
    cons.accept(new AA());
    BiConsumer<AA,BB> bicons = ClassFunctionals::f4;
    bicons.accept(new AA(), new BB());
    Function<AA,CC> f = ClassFunctionals::f5;
    CC cc = f.apply(new AA());
    BiFunction<AA,BB,CC> bif = ClassFunctionals::f6;
    cc = bif.apply(new AA(), new BB());
    Predicate<AA> p = ClassFunctionals::f7;
    boolean result = p.test(new AA());
    BiPredicate<AA,BB> bip = ClassFunctionals::f8;
    result = bip.test(new AA(), new BB());
    UnaryOperator<AA> uo = ClassFunctionals::f9;
    AA aa = uo.apply(new AA());
    BinaryOperator<AA> bo = ClassFunctionals::f10;
    aa = bo.apply(new AA(), new AA());
  }
}
