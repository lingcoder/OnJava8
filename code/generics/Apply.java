// generics/Apply.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.lang.reflect.*;
import java.util.*;

public class Apply {
  public static <T, S extends Iterable<T>>
  void apply(S seq, Method f, Object... args) {
    try {
      for(T t: seq)
        f.invoke(t, args);
    } catch(IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException e) {
      // Failures are programmer errors
      throw new RuntimeException(e);
    }
  }
}
