// functional/AnonymousClosure.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;

public class AnonymousClosure {
  IntSupplier makeFun(int x) {
    int i = 0;
    // Same rules apply:
    // i++; // Not "effectively final"
    // x++; // Ditto
    return new IntSupplier() {
      public int getAsInt() { return x + i; }
    };
  }
}
