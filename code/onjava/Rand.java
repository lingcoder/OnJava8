// onjava/Rand.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Generate random values of different types
package onjava;
import java.util.*;
import java.util.function.*;
import static onjava.ConvertTo.*;

public interface Rand {
  int MOD = 10_000;
  class Boolean
  implements Supplier<java.lang.Boolean> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Boolean get() {
      return r.nextBoolean();
    }
    public java.lang.Boolean get(int n) {
      return get();
    }
    public java.lang.Boolean[] array(int sz) {
      java.lang.Boolean[] result =
        new java.lang.Boolean[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pboolean {
    public boolean[] array(int sz) {
      return primitive(new Boolean().array(sz));
    }
  }
  class Byte
  implements Supplier<java.lang.Byte> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Byte get() {
      return (byte)r.nextInt(MOD);
    }
    public java.lang.Byte get(int n) {
      return get();
    }
    public java.lang.Byte[] array(int sz) {
      java.lang.Byte[] result =
        new java.lang.Byte[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pbyte {
    public byte[] array(int sz) {
      return primitive(new Byte().array(sz));
    }
  }
  class Character
  implements Supplier<java.lang.Character> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Character get() {
      return (char)r.nextInt('a', 'z' + 1);
    }
    public java.lang.Character get(int n) {
      return get();
    }
    public java.lang.Character[] array(int sz) {
      java.lang.Character[] result =
        new java.lang.Character[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pchar {
    public char[] array(int sz) {
      return primitive(new Character().array(sz));
    }
  }
  class Short
  implements Supplier<java.lang.Short> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Short get() {
      return (short)r.nextInt(MOD);
    }
    public java.lang.Short get(int n) {
      return get();
    }
    public java.lang.Short[] array(int sz) {
      java.lang.Short[] result =
        new java.lang.Short[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pshort {
    public short[] array(int sz) {
      return primitive(new Short().array(sz));
    }
  }
  class Integer
  implements Supplier<java.lang.Integer> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Integer get() {
      return r.nextInt(MOD);
    }
    public java.lang.Integer get(int n) {
      return get();
    }
    public java.lang.Integer[] array(int sz) {
      int[] primitive = new Pint().array(sz);
      java.lang.Integer[] result =
        new java.lang.Integer[sz];
      for(int i = 0; i < sz; i++)
        result[i] = primitive[i];
      return result;
    }
  }
  class Pint implements IntSupplier {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public int getAsInt() {
      return r.nextInt(MOD);
    }
    public int get(int n) { return getAsInt(); }
    public int[] array(int sz) {
      return r.ints(sz, 0, MOD).toArray();
    }
  }
  class Long
  implements Supplier<java.lang.Long> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Long get() {
      return r.nextLong(MOD);
    }
    public java.lang.Long get(int n) {
      return get();
    }
    public java.lang.Long[] array(int sz) {
      long[] primitive = new Plong().array(sz);
      java.lang.Long[] result =
        new java.lang.Long[sz];
      for(int i = 0; i < sz; i++)
        result[i] = primitive[i];
      return result;
    }
  }
  class Plong implements LongSupplier {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public long getAsLong() {
      return r.nextLong(MOD);
    }
    public long get(int n) { return getAsLong(); }
    public long[] array(int sz) {
      return r.longs(sz, 0, MOD).toArray();
    }
  }
  class Float
  implements Supplier<java.lang.Float> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Float get() {
      return (float)trim(r.nextDouble());
    }
    public java.lang.Float get(int n) {
      return get();
    }
    public java.lang.Float[] array(int sz) {
      java.lang.Float[] result =
        new java.lang.Float[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pfloat {
    public float[] array(int sz) {
      return primitive(new Float().array(sz));
    }
  }
  static double trim(double d) {
    return
      ((double)Math.round(d * 1000.0)) / 100.0;
  }
  class Double
  implements Supplier<java.lang.Double> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Double get() {
      return trim(r.nextDouble());
    }
    public java.lang.Double get(int n) {
      return get();
    }
    public java.lang.Double[] array(int sz) {
      double[] primitive =
        new Rand.Pdouble().array(sz);
      java.lang.Double[] result =
        new java.lang.Double[sz];
      for(int i = 0; i < sz; i++)
        result[i] = primitive[i];
      return result;
    }
  }
  class Pdouble implements DoubleSupplier {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public double getAsDouble() {
      return trim(r.nextDouble());
    }
    public double get(int n) {
      return getAsDouble();
    }
    public double[] array(int sz) {
      double[] result = r.doubles(sz).toArray();
      Arrays.setAll(result,
        n -> result[n] = trim(result[n]));
      return result;
    }
  }
  class String
  implements Supplier<java.lang.String> {
    SplittableRandom r = new SplittableRandom(47);
    private int strlen = 7; // Default length
    public String() {}
    public String(int strLength) {
      strlen = strLength;
    }
    @Override
    public java.lang.String get() {
      return r.ints(strlen, 'a', 'z' + 1)
        .collect(StringBuilder::new,
                 StringBuilder::appendCodePoint,
                 StringBuilder::append).toString();
    }
    public java.lang.String get(int n) {
      return get();
    }
    public java.lang.String[] array(int sz) {
      java.lang.String[] result =
        new java.lang.String[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
}
