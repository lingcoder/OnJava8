// functional/Closure7.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {WillNotCompile}
import java.util.function.*;

public class Closure7 {
  IntSupplier makeFun(int x) {
    Integer i = 0;
    i = i + 1;
    return () -> x + i;
  }
}
