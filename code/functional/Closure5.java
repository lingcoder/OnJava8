// functional/Closure5.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {WillNotCompile}
import java.util.function.*;

public class Closure5 {
  IntSupplier makeFun(int x) {
    int i = 0;
    i++;
    x++;
    return () -> x + i;
  }
}
