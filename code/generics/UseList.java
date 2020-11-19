// generics/UseList.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {WillNotCompile}
import java.util.*;

public class UseList<W, T> {
  void f(List<T> v) {}
  void f(List<W> v) {}
}
