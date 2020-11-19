// generics/UnboundedWildcards1.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class UnboundedWildcards1 {
  static List list1;
  static List<?> list2;
  static List<? extends Object> list3;
  static void assign1(List list) {
    list1 = list;
    list2 = list;
    //- list3 = list;
    // warning: [unchecked] unchecked conversion
    // list3 = list;
    //         ^
    // required: List<? extends Object>
    // found:    List
  }
  static void assign2(List<?> list) {
    list1 = list;
    list2 = list;
    list3 = list;
  }
  static void assign3(List<? extends Object> list) {
    list1 = list;
    list2 = list;
    list3 = list;
  }
  public static void main(String[] args) {
    assign1(new ArrayList());
    assign2(new ArrayList());
    //- assign3(new ArrayList());
    // warning: [unchecked] unchecked method invocation:
    // method assign3 in class UnboundedWildcards1
    // is applied to given types
    // assign3(new ArrayList());
    //        ^
    // required: List<? extends Object>
    // found: ArrayList
    // warning: [unchecked] unchecked conversion
    // assign3(new ArrayList());
    //         ^
    // required: List<? extends Object>
    // found:    ArrayList
    // 2 warnings
    assign1(new ArrayList<>());
    assign2(new ArrayList<>());
    assign3(new ArrayList<>());
    // Both forms are acceptable as List<?>:
    List<?> wildList = new ArrayList();
    wildList = new ArrayList<>();
    assign1(wildList);
    assign2(wildList);
    assign3(wildList);
  }
}
