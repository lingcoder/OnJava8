// generics/ListMaker.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class ListMaker<T> {
  List<T> create() { return new ArrayList<>(); }
  public static void main(String[] args) {
    ListMaker<String> stringMaker = new ListMaker<>();
    List<String> stringList = stringMaker.create();
  }
}
