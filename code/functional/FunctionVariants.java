// functional/FunctionVariants.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;

class Foo {}

class Bar {
  Foo f;
  Bar(Foo f) { this.f = f; }
}

class IBaz {
  int i;
  IBaz(int i) {
    this.i = i;
  }
}

class LBaz {
  long l;
  LBaz(long l) {
    this.l = l;
  }
}

class DBaz {
  double d;
  DBaz(double d) {
    this.d = d;
  }
}

public class FunctionVariants {
  static Function<Foo,Bar> f1 = f -> new Bar(f);
  static IntFunction<IBaz> f2 = i -> new IBaz(i);
  static LongFunction<LBaz> f3 = l -> new LBaz(l);
  static DoubleFunction<DBaz> f4 = d -> new DBaz(d);
  static ToIntFunction<IBaz> f5 = ib -> ib.i;
  static ToLongFunction<LBaz> f6 = lb -> lb.l;
  static ToDoubleFunction<DBaz> f7 = db -> db.d;
  static IntToLongFunction f8 = i -> i;
  static IntToDoubleFunction f9 = i -> i;
  static LongToIntFunction f10 = l -> (int)l;
  static LongToDoubleFunction f11 = l -> l;
  static DoubleToIntFunction f12 = d -> (int)d;
  static DoubleToLongFunction f13 = d -> (long)d;

  public static void main(String[] args) {
    Bar b = f1.apply(new Foo());
    IBaz ib = f2.apply(11);
    LBaz lb = f3.apply(11);
    DBaz db = f4.apply(11);
    int i = f5.applyAsInt(ib);
    long l = f6.applyAsLong(lb);
    double d = f7.applyAsDouble(db);
    l = f8.applyAsLong(12);
    d = f9.applyAsDouble(12);
    i = f10.applyAsInt(12);
    d = f11.applyAsDouble(12);
    i = f12.applyAsInt(13.0);
    l = f13.applyAsLong(13.0);
  }
}
