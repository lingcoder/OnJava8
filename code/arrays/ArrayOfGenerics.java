// arrays/ArrayOfGenerics.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class ArrayOfGenerics {
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    List<String>[] ls;
    List[] la = new List[10];
    ls = (List<String>[])la; // Unchecked cast
    ls[0] = new ArrayList<>();

    //- ls[1] = new ArrayList<Integer>();
    // error: incompatible types: ArrayList<Integer>
    // cannot be converted to List<String>
    //     ls[1] = new ArrayList<Integer>();
    //             ^

    // The problem: List<String> is a subtype of Object
    Object[] objects = ls; // So assignment is OK
    // Compiles and runs without complaint:
    objects[1] = new ArrayList<>();

    // However, if your needs are straightforward it is
    // possible to create an array of generics, albeit
    // with an "unchecked cast" warning:
    List<BerylliumSphere>[] spheres =
      (List<BerylliumSphere>[])new List[10];
    Arrays.setAll(spheres, n -> new ArrayList<>());
  }
}
