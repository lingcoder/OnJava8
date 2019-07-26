[TOC]

<!-- Appendix: Collection Topics -->
# 附录:集合主题

> 本附录是一些比[第十二章 集合]()中介绍的更高级的内容。

<!-- Sample Data -->
## 示例数据

这里创建一些样本数据用于集合示例。 以下数据将颜色名称与HTML颜色的RGB值相关联。请注意，每个键和值都是唯一的：

```java
// onjava/HTMLColors.java
// Sample data for collection examples
package onjava;
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

public class HTMLColors {
  public static final Object[][] ARRAY = {
    { 0xF0F8FF, "AliceBlue" },
    { 0xFAEBD7, "AntiqueWhite" },
    { 0x7FFFD4, "Aquamarine" },
    { 0xF0FFFF, "Azure" },
    { 0xF5F5DC, "Beige" },
    { 0xFFE4C4, "Bisque" },
    { 0x000000, "Black" },
    { 0xFFEBCD, "BlanchedAlmond" },
    { 0x0000FF, "Blue" },
    { 0x8A2BE2, "BlueViolet" },
    { 0xA52A2A, "Brown" },
    { 0xDEB887, "BurlyWood" },
    { 0x5F9EA0, "CadetBlue" },
    { 0x7FFF00, "Chartreuse" },
    { 0xD2691E, "Chocolate" },
    { 0xFF7F50, "Coral" },
    { 0x6495ED, "CornflowerBlue" },
    { 0xFFF8DC, "Cornsilk" },
    { 0xDC143C, "Crimson" },
    { 0x00FFFF, "Cyan" },
    { 0x00008B, "DarkBlue" },
    { 0x008B8B, "DarkCyan" },
    { 0xB8860B, "DarkGoldenRod" },
    { 0xA9A9A9, "DarkGray" },
    { 0x006400, "DarkGreen" },
    { 0xBDB76B, "DarkKhaki" },
    { 0x8B008B, "DarkMagenta" },
    { 0x556B2F, "DarkOliveGreen" },
    { 0xFF8C00, "DarkOrange" },
    { 0x9932CC, "DarkOrchid" },
    { 0x8B0000, "DarkRed" },
    { 0xE9967A, "DarkSalmon" },
    { 0x8FBC8F, "DarkSeaGreen" },
    { 0x483D8B, "DarkSlateBlue" },
    { 0x2F4F4F, "DarkSlateGray" },
    { 0x00CED1, "DarkTurquoise" },
    { 0x9400D3, "DarkViolet" },
    { 0xFF1493, "DeepPink" },
    { 0x00BFFF, "DeepSkyBlue" },
    { 0x696969, "DimGray" },
    { 0x1E90FF, "DodgerBlue" },
    { 0xB22222, "FireBrick" },
    { 0xFFFAF0, "FloralWhite" },
    { 0x228B22, "ForestGreen" },
    { 0xDCDCDC, "Gainsboro" },
    { 0xF8F8FF, "GhostWhite" },
    { 0xFFD700, "Gold" },
    { 0xDAA520, "GoldenRod" },
    { 0x808080, "Gray" },
    { 0x008000, "Green" },
    { 0xADFF2F, "GreenYellow" },
    { 0xF0FFF0, "HoneyDew" },
    { 0xFF69B4, "HotPink" },
    { 0xCD5C5C, "IndianRed" },
    { 0x4B0082, "Indigo" },
    { 0xFFFFF0, "Ivory" },
    { 0xF0E68C, "Khaki" },
    { 0xE6E6FA, "Lavender" },
    { 0xFFF0F5, "LavenderBlush" },
    { 0x7CFC00, "LawnGreen" },
    { 0xFFFACD, "LemonChiffon" },
    { 0xADD8E6, "LightBlue" },
    { 0xF08080, "LightCoral" },
    { 0xE0FFFF, "LightCyan" },
    { 0xFAFAD2, "LightGoldenRodYellow" },
    { 0xD3D3D3, "LightGray" },
    { 0x90EE90, "LightGreen" },
    { 0xFFB6C1, "LightPink" },
    { 0xFFA07A, "LightSalmon" },
    { 0x20B2AA, "LightSeaGreen" },
    { 0x87CEFA, "LightSkyBlue" },
    { 0x778899, "LightSlateGray" },
    { 0xB0C4DE, "LightSteelBlue" },
    { 0xFFFFE0, "LightYellow" },
    { 0x00FF00, "Lime" },
    { 0x32CD32, "LimeGreen" },
    { 0xFAF0E6, "Linen" },
    { 0xFF00FF, "Magenta" },
    { 0x800000, "Maroon" },
    { 0x66CDAA, "MediumAquaMarine" },
    { 0x0000CD, "MediumBlue" },
    { 0xBA55D3, "MediumOrchid" },
    { 0x9370DB, "MediumPurple" },
    { 0x3CB371, "MediumSeaGreen" },
    { 0x7B68EE, "MediumSlateBlue" },
    { 0x00FA9A, "MediumSpringGreen" },
    { 0x48D1CC, "MediumTurquoise" },
    { 0xC71585, "MediumVioletRed" },
    { 0x191970, "MidnightBlue" },
    { 0xF5FFFA, "MintCream" },
    { 0xFFE4E1, "MistyRose" },
    { 0xFFE4B5, "Moccasin" },
    { 0xFFDEAD, "NavajoWhite" },
    { 0x000080, "Navy" },
    { 0xFDF5E6, "OldLace" },
    { 0x808000, "Olive" },
    { 0x6B8E23, "OliveDrab" },
    { 0xFFA500, "Orange" },
    { 0xFF4500, "OrangeRed" },
    { 0xDA70D6, "Orchid" },
    { 0xEEE8AA, "PaleGoldenRod" },
    { 0x98FB98, "PaleGreen" },
    { 0xAFEEEE, "PaleTurquoise" },
    { 0xDB7093, "PaleVioletRed" },
    { 0xFFEFD5, "PapayaWhip" },
    { 0xFFDAB9, "PeachPuff" },
    { 0xCD853F, "Peru" },
    { 0xFFC0CB, "Pink" },
    { 0xDDA0DD, "Plum" },
    { 0xB0E0E6, "PowderBlue" },
    { 0x800080, "Purple" },
    { 0xFF0000, "Red" },
    { 0xBC8F8F, "RosyBrown" },
    { 0x4169E1, "RoyalBlue" },
    { 0x8B4513, "SaddleBrown" },
    { 0xFA8072, "Salmon" },
    { 0xF4A460, "SandyBrown" },
    { 0x2E8B57, "SeaGreen" },
    { 0xFFF5EE, "SeaShell" },
    { 0xA0522D, "Sienna" },
    { 0xC0C0C0, "Silver" },
    { 0x87CEEB, "SkyBlue" },
    { 0x6A5ACD, "SlateBlue" },
    { 0x708090, "SlateGray" },
    { 0xFFFAFA, "Snow" },
    { 0x00FF7F, "SpringGreen" },
    { 0x4682B4, "SteelBlue" },
    { 0xD2B48C, "Tan" },
    { 0x008080, "Teal" },
    { 0xD8BFD8, "Thistle" },
    { 0xFF6347, "Tomato" },
    { 0x40E0D0, "Turquoise" },
    { 0xEE82EE, "Violet" },
    { 0xF5DEB3, "Wheat" },
    { 0xFFFFFF, "White" },
    { 0xF5F5F5, "WhiteSmoke" },
    { 0xFFFF00, "Yellow" },
    { 0x9ACD32, "YellowGreen" },
  };
  public static final Map<Integer,String> MAP =
    Arrays.stream(ARRAY)
      .collect(Collectors.toMap(
        element -> (Integer)element[0],
        element -> (String)element[1],
        (v1, v2) -> { // Merge function
          throw new IllegalStateException();
        },
        LinkedHashMap::new
      ));
  // Inversion only works if values are unique:
  public static <V, K> Map<V, K>
  invert(Map<K, V> map) {
    return map.entrySet().stream()
      .collect(Collectors.toMap(
        Map.Entry::getValue,
        Map.Entry::getKey,
        (v1, v2) -> {
          throw new IllegalStateException();
        },
        LinkedHashMap::new
      ));
  }
  public static final Map<String,Integer>
    INVMAP = invert(MAP);
  // Look up RGB value given a name:
  public static Integer rgb(String colorName) {
    return INVMAP.get(colorName);
  }
  public static final List<String> LIST =
    Arrays.stream(ARRAY)
      .map(item -> (String)item[1])
      .collect(Collectors.toList());
  public static final List<Integer> RGBLIST =
    Arrays.stream(ARRAY)
      .map(item -> (Integer)item[0])
      .collect(Collectors.toList());
  public static
  void show(Map.Entry<Integer,String> e) {
    System.out.format(
      "0x%06X: %s%n", e.getKey(), e.getValue());
  }
  public static void
  show(Map<Integer,String> m, int count) {
    m.entrySet().stream()
      .limit(count)
      .forEach(e -> show(e));
  }
  public static void show(Map<Integer,String> m) {
    show(m, m.size());
  }
  public static
  void show(Collection<String> lst, int count) {
    lst.stream()
      .limit(count)
      .forEach(System.out::println);
  }
  public static void show(Collection<String> lst) {
    show(lst, lst.size());
  }
  public static
  void showrgb(Collection<Integer> lst, int count) {
    lst.stream()
      .limit(count)
      .forEach(n -> System.out.format("0x%06X%n", n));
  }
  public static void showrgb(Collection<Integer> lst) {
    showrgb(lst, lst.size());
  }
  public static
  void showInv(Map<String,Integer> m, int count) {
    m.entrySet().stream()
      .limit(count)
      .forEach(e ->
        System.out.format(
          "%-20s  0x%06X%n", e.getKey(), e.getValue()));
  }
  public static void showInv(Map<String,Integer> m) {
    showInv(m, m.size());
  }
  public static void border() {
    System.out.println(
      "******************************");
  }
}
```

**MAP** 是使用Streams（[第十四章 流式编程]()）创建的。 二维数组 **ARRAY** 作为流传输到 **Map** 中，但请注意我们不仅仅是使用简单版本的 `Collectors.toMap()` 。 那个版本生成一个 **HashMap** ，它使用散列函数来控制对键的排序。 为了保留原来的顺序，我们必须将键值对直接放入 **TreeMap** 中，这意味着我们需要使用更复杂的 `Collectors.toMap()` 版本。这需要两个函数从每个流元素中提取键和值，就像简单版本的`Collectors.toMap()` 一样。 然后它需要一个*合并函数*（merge function），它解决了与同一个键相关的两个值之间的冲突。这里的数据已经预先审查过，因此绝不会发生这种情况，如果有的话，这里会抛出异常。最后，传递生成所需类型的空map的函数，然后用流来填充它。

`rgb()` 方法是一个便捷函数（convenience function），它接受颜色名称 **String** 参数并生成其数字RGB值。为此，我们需要一个反转版本的 **COLORS** ，它接受一个 **String**键并查找RGB的 **Integer** 值。 这是通过 `invert()` 方法实现的，如果任何 **COLORS** 值不唯一，则抛出异常。

我们还创建包含所有名称的 **LIST** ，以及包含十六进制表示法的RGB值的 **RGBLIST** 。

第一个 `show()` 方法接受一个 **Map.Entry** 并显示以十六进制表示的键，以便轻松地对原始 **ARRAY** 进行双重检查。 名称以 **show** 开头的每个方法都会重载两个版本，其中一个版本采用 **count** 参数来指示要显示的元素数量，第二个版本显示序列中的所有元素。

这里是一个基本的测试：

```java
// collectiontopics/HTMLColorTest.java
import static onjava.HTMLColors.*;

public class HTMLColorTest {
  static final int DISPLAY_SIZE = 20;
  public static void main(String[] args) {
    show(MAP, DISPLAY_SIZE);
    border();
    showInv(INVMAP, DISPLAY_SIZE);
    border();
    show(LIST, DISPLAY_SIZE);
    border();
    showrgb(RGBLIST, DISPLAY_SIZE);
  }
}
/* Output:
0xF0F8FF: AliceBlue
0xFAEBD7: AntiqueWhite
0x7FFFD4: Aquamarine
0xF0FFFF: Azure
0xF5F5DC: Beige
0xFFE4C4: Bisque
0x000000: Black
0xFFEBCD: BlanchedAlmond
0x0000FF: Blue
0x8A2BE2: BlueViolet
0xA52A2A: Brown
0xDEB887: BurlyWood
0x5F9EA0: CadetBlue
0x7FFF00: Chartreuse
0xD2691E: Chocolate
0xFF7F50: Coral
0x6495ED: CornflowerBlue
0xFFF8DC: Cornsilk
0xDC143C: Crimson
0x00FFFF: Cyan
******************************
AliceBlue             0xF0F8FF
AntiqueWhite          0xFAEBD7
Aquamarine            0x7FFFD4
Azure                 0xF0FFFF
Beige                 0xF5F5DC
Bisque                0xFFE4C4
Black                 0x000000
BlanchedAlmond        0xFFEBCD
Blue                  0x0000FF
BlueViolet            0x8A2BE2
Brown                 0xA52A2A
BurlyWood             0xDEB887
CadetBlue             0x5F9EA0
Chartreuse            0x7FFF00
Chocolate             0xD2691E
Coral                 0xFF7F50
CornflowerBlue        0x6495ED
Cornsilk              0xFFF8DC
Crimson               0xDC143C
Cyan                  0x00FFFF
******************************
AliceBlue
AntiqueWhite
Aquamarine
Azure
Beige
Bisque
Black
BlanchedAlmond
Blue
BlueViolet
Brown
BurlyWood
CadetBlue
Chartreuse
Chocolate
Coral
CornflowerBlue
Cornsilk
Crimson
Cyan
******************************
0xF0F8FF
0xFAEBD7
0x7FFFD4
0xF0FFFF
0xF5F5DC
0xFFE4C4
0x000000
0xFFEBCD
0x0000FF
0x8A2BE2
0xA52A2A
0xDEB887
0x5F9EA0
0x7FFF00
0xD2691E
0xFF7F50
0x6495ED
0xFFF8DC
0xDC143C
0x00FFFF
*/
```

可以看到，使用 **LinkedHashMap** 确实能够保留 **HTMLColors.ARRAY** 的顺序。

<!-- List Behavior -->
## List行为

**Lists** 是存储和检索对象（次于数组）的最基本方法。基本列表操作包括：

- `add()` 用于插入元素
- `get()` 用于随机访问元素
- `iterator()` 获取序列上的一个 **Iterator**
- `stream()` 生成元素的一个 **Stream**

列表构造方法始终保留元素的添加顺序。

以下示例中的方法各自涵盖了一组不同的行为：每个 **List** 可以执行的操作（ `basicTest()` ），使用 **Iterator** （ `iterMotion()` ）遍历序列，使用 **Iterator** （ `iterManipulation()` ）更改内容，查看 **List** 操作（ `testVisual()` ）的效果，以及仅可用于 **LinkedLists** 的操作：

```java
// collectiontopics/ListOps.java
// Things you can do with Lists
import java.util.*;
import onjava.HTMLColors;

public class ListOps {
  // Create a short list for testing:
  static final List<String> LIST =
    HTMLColors.LIST.subList(0, 10);
  private static boolean b;
  private static String s;
  private static int i;
  private static Iterator<String> it;
  private static ListIterator<String> lit;
  public static void basicTest(List<String> a) {
    a.add(1, "x"); // Add at location 1
    a.add("x"); // Add at end
    // Add a collection:
    a.addAll(LIST);
    // Add a collection starting at location 3:
    a.addAll(3, LIST);
    b = a.contains("1"); // Is it in there?
    // Is the entire collection in there?
    b = a.containsAll(LIST);
    // Lists allow random access, which is cheap
    // for ArrayList, expensive for LinkedList:
    s = a.get(1); // Get (typed) object at location 1
    i = a.indexOf("1"); // Tell index of object
    b = a.isEmpty(); // Any elements inside?
    it = a.iterator(); // Ordinary Iterator
    lit = a.listIterator(); // ListIterator
    lit = a.listIterator(3); // Start at location 3
    i = a.lastIndexOf("1"); // Last match
    a.remove(1); // Remove location 1
    a.remove("3"); // Remove this object
    a.set(1, "y"); // Set location 1 to "y"
    // Keep everything that's in the argument
    // (the intersection of the two sets):
    a.retainAll(LIST);
    // Remove everything that's in the argument:
    a.removeAll(LIST);
    i = a.size(); // How big is it?
    a.clear(); // Remove all elements
  }
  public static void iterMotion(List<String> a) {
    ListIterator<String> it = a.listIterator();
    b = it.hasNext();
    b = it.hasPrevious();
    s = it.next();
    i = it.nextIndex();
    s = it.previous();
    i = it.previousIndex();
  }
  public static void iterManipulation(List<String> a) {
    ListIterator<String> it = a.listIterator();
    it.add("47");
    // Must move to an element after add():
    it.next();
    // Remove the element after the new one:
    it.remove();
    // Must move to an element after remove():
    it.next();
    // Change the element after the deleted one:
    it.set("47");
  }
  public static void testVisual(List<String> a) {
    System.out.println(a);
    List<String> b = LIST;
    System.out.println("b = " + b);
    a.addAll(b);
    a.addAll(b);
    System.out.println(a);
    // Insert, remove, and replace elements
    // using a ListIterator:
    ListIterator<String> x =
      a.listIterator(a.size()/2);
    x.add("one");
    System.out.println(a);
    System.out.println(x.next());
    x.remove();
    System.out.println(x.next());
    x.set("47");
    System.out.println(a);
    // Traverse the list backwards:
    x = a.listIterator(a.size());
    while(x.hasPrevious())
      System.out.print(x.previous() + " ");
    System.out.println();
    System.out.println("testVisual finished");
  }
  // There are some things that only LinkedLists can do:
  public static void testLinkedList() {
    LinkedList<String> ll = new LinkedList<>();
    ll.addAll(LIST);
    System.out.println(ll);
    // Treat it like a stack, pushing:
    ll.addFirst("one");
    ll.addFirst("two");
    System.out.println(ll);
    // Like "peeking" at the top of a stack:
    System.out.println(ll.getFirst());
    // Like popping a stack:
    System.out.println(ll.removeFirst());
    System.out.println(ll.removeFirst());
    // Treat it like a queue, pulling elements
    // off the tail end:
    System.out.println(ll.removeLast());
    System.out.println(ll);
  }
  public static void main(String[] args) {
    // Make and fill a new list each time:
    basicTest(new LinkedList<>(LIST));
    basicTest(new ArrayList<>(LIST));
    iterMotion(new LinkedList<>(LIST));
    iterMotion(new ArrayList<>(LIST));
    iterManipulation(new LinkedList<>(LIST));
    iterManipulation(new ArrayList<>(LIST));
    testVisual(new LinkedList<>(LIST));
    testLinkedList();
  }
}
/* Output:
[AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet]
b = [AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet]
[AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet,
AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet,
AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet]
[AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet,
AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige, one,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet,
AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet]
Bisque
Black
[AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet,
AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige, one,
47, BlanchedAlmond, Blue, BlueViolet, AliceBlue,
AntiqueWhite, Aquamarine, Azure, Beige, Bisque, Black,
BlanchedAlmond, Blue, BlueViolet]
BlueViolet Blue BlanchedAlmond Black Bisque Beige Azure
Aquamarine AntiqueWhite AliceBlue BlueViolet Blue
BlanchedAlmond 47 one Beige Azure Aquamarine
AntiqueWhite AliceBlue BlueViolet Blue BlanchedAlmond
Black Bisque Beige Azure Aquamarine AntiqueWhite
AliceBlue
testVisual finished
[AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue, BlueViolet]
[two, one, AliceBlue, AntiqueWhite, Aquamarine, Azure,
Beige, Bisque, Black, BlanchedAlmond, Blue, BlueViolet]
two
two
one
BlueViolet
[AliceBlue, AntiqueWhite, Aquamarine, Azure, Beige,
Bisque, Black, BlanchedAlmond, Blue]
*/
```

在 `basicTest()` 和 `iterMotion()` 中，方法调用是为了展示正确的语法，尽管获取了返回值，但不会使用它。在某些情况下，根本不会去获取返回值。在使用这些方法之前，请查看JDK文档中这些方法的完整用法。

<!-- Set Behavior -->
## Set行为

**Set** 的主要用处是测试成员身份，不过也可以将其用作删除重复元素的工具。如果不关心元素顺序或并发性， **HashSet** 总是最好的选择，因为它是专门为了快速查找而设计的（这里使用了在[附录：理解equals和hashCode方法]()章节中探讨的散列函数）。

其它的 **Set** 实现产生不同的排序行为：

```java
// collectiontopics/SetOrder.java
import java.util.*;
import onjava.HTMLColors;

public class SetOrder {
  static String[] sets = {
    "java.util.HashSet",
    "java.util.TreeSet",
    "java.util.concurrent.ConcurrentSkipListSet",
    "java.util.LinkedHashSet",
    "java.util.concurrent.CopyOnWriteArraySet",
  };
  static final List<String> RLIST =
    new ArrayList<>(HTMLColors.LIST);
  static {
    Collections.reverse(RLIST);
  }
  public static void
  main(String[] args) throws Exception {
    for(String type: sets) {
      System.out.format("[-> %s <-]%n",
        type.substring(type.lastIndexOf('.') + 1));
      @SuppressWarnings("unchecked")
      Set<String> set = (Set<String>)
        Class.forName(type).newInstance();
      set.addAll(RLIST);
      set.stream()
        .limit(10)
        .forEach(System.out::println);
    }
  }
}
/* Output:
[-> HashSet <-]
MediumOrchid
PaleGoldenRod
Sienna
LightSlateGray
DarkSeaGreen
Black
Gainsboro
Orange
LightCoral
DodgerBlue
[-> TreeSet <-]
AliceBlue
AntiqueWhite
Aquamarine
Azure
Beige
Bisque
Black
BlanchedAlmond
Blue
BlueViolet
[-> ConcurrentSkipListSet <-]
AliceBlue
AntiqueWhite
Aquamarine
Azure
Beige
Bisque
Black
BlanchedAlmond
Blue
BlueViolet
[-> LinkedHashSet <-]
YellowGreen
Yellow
WhiteSmoke
White
Wheat
Violet
Turquoise
Tomato
Thistle
Teal
[-> CopyOnWriteArraySet <-]
YellowGreen
Yellow
WhiteSmoke
White
Wheat
Violet
Turquoise
Tomato
Thistle
Teal
*/
```

这里需要使用 **@SuppressWarnings(“unchecked”)** ，因为这里将一个 **String** （可能是任何东西）传递给了 `Class.forName(type).newInstance()` 。编译器并不能保证这是一次成功的操作。

**RLIST** 是 **HTMLColors.LIST** 的反转版本。因为 `Collections.reverse()` 是通过修改参数来执行反向操作，而不是返回包含反向元素的新 **List** ，所以该调用在 **static** 块内执行。  **RLIST** 可以防止我们意外地认为 **Set** 对其结果进行了排序。

**HashSet** 的输出结果似乎没有可辨别的顺序，因为它是基于散列函数的。 **TreeSet** 和 **ConcurrentSkipListSet** 都对它们的元素进行了排序，它们都实现了 **SortedSet** 接口来标识这个特点。因为实现该接口的 **Set** 按顺序排列，所以该接口还有一些其他的可用操作。 **LinkedHashSet** 和 **CopyOnWriteArraySet** 尽管没有用于标识的接口，但它们还是保留了元素的插入顺序。

**ConcurrentSkipListSet** 和 **CopyOnWriteArraySet** 是线程安全的。

在附录的最后，我们将了解在非 **HashSet** 实现的 **Set** 上添加额外排序的性能成本，以及不同实现中的任何其他功能的成本。

<!-- Using Functional Operations with any Map -->
## 在Map中使用函数式操作

与 **Collection** 接口一样，`forEach()` 也内置在 **Map** 接口中。但是如果想要执行任何其他的基本功能操作，比如 `map()` ，`flatMap()` ，`reduce()` 或 `filter()` 时，该怎么办？ 查看 **Map** 接口发现并没有这些。

可以通过 `entrySet()` 连接到这些方法，该方法会生成一个由 **Map.Entry** 对象组成的 **Set** 。这个 **Set** 包含 `stream()` 和 `parallelStream()` 方法。只需要记住一件事，这里正在使用的是 **Map.Entry** 对象：

```java
// collectiontopics/FunctionalMap.java
// Functional operations on a Map
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import static onjava.HTMLColors.*;

public class FunctionalMap {
  public static void main(String[] args) {
    MAP.entrySet().stream()
      .map(Map.Entry::getValue)
      .filter(v -> v.startsWith("Dark"))
      .map(v -> v.replaceFirst("Dark", "Hot"))
      .forEach(System.out::println);
  }
}
/* Output:
HotBlue
HotCyan
HotGoldenRod
HotGray
HotGreen
HotKhaki
HotMagenta
HotOliveGreen
HotOrange
HotOrchid
HotRed
HotSalmon
HotSeaGreen
HotSlateBlue
HotSlateGray
HotTurquoise
HotViolet
*/
```

生成 **Stream** 后，所有的基本功能方法，甚至更多就都可以使用了。

<!-- Selecting Parts of a Map -->
## 选择Map片段

由 **TreeMap** 和 **ConcurrentSkipListMap** 实现的 **NavigableMap** 接口解决了需要选择Map片段的问题。下面是一个示例，使用了 **HTMLColors** ：

```java
// collectiontopics/NavMap.java
// NavigableMap produces pieces of a Map
import java.util.*;
import java.util.concurrent.*;
import static onjava.HTMLColors.*;

public class NavMap {
  public static final
  NavigableMap<Integer,String> COLORS =
    new ConcurrentSkipListMap<>(MAP);
  public static void main(String[] args) {
    show(COLORS.firstEntry());
    border();
    show(COLORS.lastEntry());
    border();
    NavigableMap<Integer, String> toLime =
      COLORS.headMap(rgb("Lime"), true);
    show(toLime);
    border();
    show(COLORS.ceilingEntry(rgb("DeepSkyBlue") - 1));
    border();
    show(COLORS.floorEntry(rgb("DeepSkyBlue") - 1));
    border();
    show(toLime.descendingMap());
    border();
    show(COLORS.tailMap(rgb("MistyRose"), true));
    border();
    show(COLORS.subMap(
      rgb("Orchid"), true,
      rgb("DarkSalmon"), false));
  }
}
/* Output:
0x000000: Black
******************************
0xFFFFFF: White
******************************
0x000000: Black
0x000080: Navy
0x00008B: DarkBlue
0x0000CD: MediumBlue
0x0000FF: Blue
0x006400: DarkGreen
0x008000: Green
0x008080: Teal
0x008B8B: DarkCyan
0x00BFFF: DeepSkyBlue
0x00CED1: DarkTurquoise
0x00FA9A: MediumSpringGreen
0x00FF00: Lime
******************************
0x00BFFF: DeepSkyBlue
******************************
0x008B8B: DarkCyan
******************************
0x00FF00: Lime
0x00FA9A: MediumSpringGreen
0x00CED1: DarkTurquoise
0x00BFFF: DeepSkyBlue
0x008B8B: DarkCyan
0x008080: Teal
0x008000: Green
0x006400: DarkGreen
0x0000FF: Blue
0x0000CD: MediumBlue
0x00008B: DarkBlue
0x000080: Navy
0x000000: Black
******************************
0xFFE4E1: MistyRose
0xFFEBCD: BlanchedAlmond
0xFFEFD5: PapayaWhip
0xFFF0F5: LavenderBlush
0xFFF5EE: SeaShell
0xFFF8DC: Cornsilk
0xFFFACD: LemonChiffon
0xFFFAF0: FloralWhite
0xFFFAFA: Snow
0xFFFF00: Yellow
0xFFFFE0: LightYellow
0xFFFFF0: Ivory
0xFFFFFF: White
******************************
0xDA70D6: Orchid
0xDAA520: GoldenRod
0xDB7093: PaleVioletRed
0xDC143C: Crimson
0xDCDCDC: Gainsboro
0xDDA0DD: Plum
0xDEB887: BurlyWood
0xE0FFFF: LightCyan
0xE6E6FA: Lavender
*/
```

在主方法中可以看到 **NavigableMap** 的各种功能。 因为 **NavigableMap** 具有键顺序，所以它使用了 `firstEntry()` 和 `lastEntry()` 的概念。调用 `headMap()` 会生成一个 **NavigableMap** ，其中包含了从 **Map** 的开头到 `headMap()` 参数中所指向的一组元素，其中 **boolean** 值指示结果中是否包含该参数。调用 `tailMap()` 执行了类似的操作，只不过是从参数开始到 **Map** 的末尾。 `subMap()` 则允许生成 **Map** 中间的一部分。

`ceilingEntry()` 从当前键值对向上搜索下一个键值对，`floorEntry()` 则是向下搜索。 `descendingMap()` 反转了 **NavigableMap** 的顺序。

如果需要通过分割 **Map** 来简化所正在解决的问题，则 **NavigableMap** 可以做到。具有类似的功能的其它集合实现也可以用来帮助解决问题。

<!-- Filling Collections -->
## 填充集合

与 **Arrays** 一样，这里有一个名为 **Collections** 的伴随类（companion class），包含了一些 **static** 的实用方法，其中包括一个名为 `fill()` 的方法。 `fill()` 只复制整个集合中的单个对象引用。此外，它仅适用于 **List** 对象，但结果列表可以传递给构造方法或 `addAll()` 方法：

```java
// collectiontopics/FillingLists.java
// Collections.fill() & Collections.nCopies()
import java.util.*;

class StringAddress {
  private String s;
  StringAddress(String s) { this.s = s; }
  @Override
  public String toString() {
    return super.toString() + " " + s;
  }
}

public class FillingLists {
  public static void main(String[] args) {
    List<StringAddress> list = new ArrayList<>(
      Collections.nCopies(4,
        new StringAddress("Hello")));
    System.out.println(list);
    Collections.fill(list,
      new StringAddress("World!"));
    System.out.println(list);
  }
}
/* Output:
[StringAddress@15db9742 Hello, StringAddress@15db9742
Hello, StringAddress@15db9742 Hello,
StringAddress@15db9742 Hello]
[StringAddress@6d06d69c World!, StringAddress@6d06d69c
World!, StringAddress@6d06d69c World!,
StringAddress@6d06d69c World!]
*/
```

这个示例展示了两种使用对单个对象的引用来填充 **Collection** 的方法。 第一个： `Collections.nCopies()` ，创建一个 **List**，并传递给 **ArrayList** 的构造方法，进而填充了 **ArrayList** 。

**StringAddress** 中的 `toString()` 方法调用了 `Object.toString()` ，它先生成类名，后跟着对象的哈希码的无符号十六进制表示（哈希吗由 `hashCode()` 方法生成）。 输出显示所有的引用都指向同一个对象。调用第二个方法 `Collections.fill()` 后也是如此。 `fill()` 方法的用处非常有限，它只能替换 **List** 中已有的元素,而且不会添加新元素，

### 使用 Suppliers 填充集合

[第二十章 泛型]()章节中介绍的 **onjava.Suppliers** 类为填充集合提供了通用解决方案。 这是一个使用 **Suppliers** 初始化几种不同类型的 **Collection** 的示例：

```java
// collectiontopics/SuppliersCollectionTest.java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import onjava.*;

class Government implements Supplier<String> {
  static String[] foundation = (
    "strange women lying in ponds " +
    "distributing swords is no basis " +
    "for a system of government").split(" ");
  private int index;
  @Override
  public String get() {
    return foundation[index++];
  }
}

public class SuppliersCollectionTest {
  public static void main(String[] args) {
    // Suppliers class from the Generics chapter:
    Set<String> set = Suppliers.create(
      LinkedHashSet::new, new Government(), 15);
    System.out.println(set);
    List<String> list = Suppliers.create(
      LinkedList::new, new Government(), 15);
    System.out.println(list);
    list = new ArrayList<>();
    Suppliers.fill(list, new Government(), 15);
    System.out.println(list);

    // Or we can use Streams:
    set = Arrays.stream(Government.foundation)
      .collect(Collectors.toSet());
    System.out.println(set);
    list = Arrays.stream(Government.foundation)
      .collect(Collectors.toList());
    System.out.println(list);
    list = Arrays.stream(Government.foundation)
      .collect(Collectors
        .toCollection(LinkedList::new));
    System.out.println(list);
    set = Arrays.stream(Government.foundation)
      .collect(Collectors
        .toCollection(LinkedHashSet::new));
    System.out.println(set);
  }
}
/* Output:
[strange, women, lying, in, ponds, distributing,
swords, is, no, basis, for, a, system, of, government]
[strange, women, lying, in, ponds, distributing,
swords, is, no, basis, for, a, system, of, government]
[strange, women, lying, in, ponds, distributing,
swords, is, no, basis, for, a, system, of, government]
[ponds, no, a, in, swords, for, is, basis, strange,
system, government, distributing, of, women, lying]
[strange, women, lying, in, ponds, distributing,
swords, is, no, basis, for, a, system, of, government]
[strange, women, lying, in, ponds, distributing,
swords, is, no, basis, for, a, system, of, government]
[strange, women, lying, in, ponds, distributing,
swords, is, no, basis, for, a, system, of, government]
*/
```

**LinkedHashSet** 中的的元素按插入顺序排列，因为它维护一个链表来保存该顺序。

但是请注意示例的第二部分：大多数情况下都可以使用 **Stream** 来创建和填充 **Collection** 。在本例中的 **Stream** 版本不需要声明 **Supplier** 所想要创建的元素数量;，它直接吸收了 **Stream** 中的所有元素。

尽可能优先选择 **Stream** 来解决问题。

### Map Suppliers

使用 **Supplier** 来填充 **Map** 时需要一个 **Pair** 类，因为每次调用一个 **Supplier** 的 `get()` 方法时，都必须生成一对对象（一个键和一个值）：

```java
// onjava/Pair.java
package onjava;

public class Pair<K, V> {
  public final K key;
  public final V value;
  public Pair(K k, V v) {
    key = k;
    value = v;
  }
  public K key() { return key; }
  public V value() { return value; }
  public static <K,V> Pair<K, V> make(K k, V v) {
    return new Pair<K,V>(k, v);
  }
}
```

**Pair** 是一个只读的 *数据传输对象* （Data Transfer Object）或 *信使* （Messenger）。 这与[第二十章 泛型]()章节中的 **Tuple2** 基本相同，但名字更适合 **Map** 初始化。我还添加了静态的 `make()` 方法，以便为创建 **Pair** 对象提供一个更简洁的名字。

Java 8 的 **Stream** 提供了填充 **Map** 的便捷方法：

```java
// collectiontopics/StreamFillMaps.java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import onjava.*;

class Letters
implements Supplier<Pair<Integer,String>> {
  private int number = 1;
  private char letter = 'A';
  @Override
  public Pair<Integer,String> get() {
    return new Pair<>(number++, "" + letter++);
  }
}

public class StreamFillMaps {
  public static void main(String[] args) {
    Map<Integer,String> m =
      Stream.generate(new Letters())
      .limit(11)
      .collect(Collectors
        .toMap(Pair::key, Pair::value));
    System.out.println(m);

    // Two separate Suppliers:
    Rand.String rs = new Rand.String(3);
    Count.Character cc = new Count.Character();
    Map<Character,String> mcs = Stream.generate(
      () -> Pair.make(cc.get(), rs.get()))
      .limit(8)
      .collect(Collectors
        .toMap(Pair::key, Pair::value));
    System.out.println(mcs);

    // A key Supplier and a single value:
    Map<Character,String> mcs2 = Stream.generate(
      () -> Pair.make(cc.get(), "Val"))
      .limit(8)
      .collect(Collectors
        .toMap(Pair::key, Pair::value));
    System.out.println(mcs2);
  }
}
/* Output:
{1=A, 2=B, 3=C, 4=D, 5=E, 6=F, 7=G, 8=H, 9=I, 10=J,
11=K}
{b=btp, c=enp, d=ccu, e=xsz, f=gvg, g=mei, h=nne,
i=elo}
{p=Val, q=Val, j=Val, k=Val, l=Val, m=Val, n=Val,
o=Val}
*/
```

上面的示例中出现了一个模式，可以使用它来创建一个自动创建和填充 **Map** 的工具：

```java
// onjava/FillMap.java
package onjava;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class FillMap {
  public static <K, V> Map<K,V>
  basic(Supplier<Pair<K,V>> pairGen, int size) {
    return Stream.generate(pairGen)
      .limit(size)
      .collect(Collectors
        .toMap(Pair::key, Pair::value));
  }
  public static <K, V> Map<K,V>
  basic(Supplier<K> keyGen,
        Supplier<V> valueGen, int size) {
    return Stream.generate(
      () -> Pair.make(keyGen.get(), valueGen.get()))
      .limit(size)
      .collect(Collectors
        .toMap(Pair::key, Pair::value));
  }
  public static <K, V, M extends Map<K,V>>
  M create(Supplier<K> keyGen,
           Supplier<V> valueGen,
           Supplier<M> mapSupplier, int size) {
    return Stream.generate( () ->
      Pair.make(keyGen.get(), valueGen.get()))
        .limit(size)
        .collect(Collectors
          .toMap(Pair::key, Pair::value,
                 (k, v) -> k, mapSupplier));
  }
}
```

basic() 方法生成一个默认的 **Map** ，而 `create()` 方法允许指定一个确切的 **Map** 类型，并返回那个确切的类型。

下面是一个测试：

```java
// collectiontopics/FillMapTest.java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import onjava.*;

public class FillMapTest {
  public static void main(String[] args) {
    Map<String,Integer> mcs = FillMap.basic(
      new Rand.String(4), new Count.Integer(), 7);
    System.out.println(mcs);
    HashMap<String,Integer> hashm =
      FillMap.create(new Rand.String(4),
        new Count.Integer(), HashMap::new, 7);
    System.out.println(hashm);
    LinkedHashMap<String,Integer> linkm =
      FillMap.create(new Rand.String(4),
        new Count.Integer(), LinkedHashMap::new, 7);
    System.out.println(linkm);
  }
}
/* Output:
{npcc=1, ztdv=6, gvgm=3, btpe=0, einn=4, eelo=5,
uxsz=2}
{npcc=1, ztdv=6, gvgm=3, btpe=0, einn=4, eelo=5,
uxsz=2}
{btpe=0, npcc=1, uxsz=2, gvgm=3, einn=4, eelo=5,
ztdv=6}
*/
```

<!-- Custom Collection and Map using Flyweight -->
## 使用享元（Flyweight）自定义Collection和Map

本节介绍如何创建自定义 **Collection** 和 **Map** 实现。每个 **java.util** 中的集合都有自己的 **Abstract** 类，它提供了该集合的部分实现，因此只需要实现必要的方法来生成所需的集合。你将看到通过继承 **java.util.Abstract** 类来创建自定义 **Map** 和 **Collection** 是多么简单。例如，要创建一个只读的 **Set** ，则可以从 **AbstractSet** 继承并实现 `iterator()` 和 `size()` 。最后一个示例是生成测试数据的另一种方法。生成的集合通常是只读的，并且所提供的方法最少。

该解决方案还演示了 *享元* （Flyweight）设计模式。当普通解决方案需要太多对象时，或者当生成普通对象占用太多空间时，可以使用享元。享元设计模式将对象的一部分外部化（externalizes）。相比于把对象的所有内容都包含在对象中，这样做使得对象的部分或者全部可以在更有效的外部表中查找，或通过一些节省空间的其他计算生成。

下面是一个可以是任何大小的 **List** ，并且（有效地）使用 **Integer** 数据进行预初始化。要从 **AbstractList** 创建只读 **List** ，必须实现 `get()` 和 `size()`：

```java
// onjava/CountingIntegerList.java
// List of any length, containing sample data
// {java onjava.CountingIntegerList}
package onjava;
import java.util.*;

public class CountingIntegerList
extends AbstractList<Integer> {
  private int size;
  public CountingIntegerList() { size = 0; }
  public CountingIntegerList(int size) {
    this.size = size < 0 ? 0 : size;
  }
  @Override
  public Integer get(int index) {
    return index;
  }
  @Override
  public int size() { return size; }
  public static void main(String[] args) {
    List<Integer> cil =
      new CountingIntegerList(30);
    System.out.println(cil);
    System.out.println(cil.get(500));
  }
}
/* Output:
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29]
500
*/
```

只有当想要限制 **List** 的长度时， **size** 值才是重要的，就像在主方法中那样。即使在这种情况下， `get()` 也会产生任何值。

这个类是享元模式的一个简洁的例子。当需要的时候， `get()` “计算”所需的值，因此没必要存储和初始化实际的底层 **List** 结构。

在大多数程序中，这里所保存的存储结构永远都不会改变。但是，它允许用非常大的 **index** 来调用 `List.get()` ，而 **List** 并不需要填充到这么大。此外，还可以在程序中大量使用 **CountingIntegerLists** 而无需担心存储问题。实际上，享元的一个好处是它允许使用更好的抽象而不用担心资源。

可以使用享元设计模式来实现具有任何大小数据集的其他“初始化”自定义集合。下面是一个 **Map** ，它为每一个 **Integer** 键产生唯一的值：

```java
// onjava/CountMap.java
// Unlimited-length Map containing sample data
// {java onjava.CountMap}
package onjava;
import java.util.*;
import java.util.stream.*;

public class CountMap
extends AbstractMap<Integer,String> {
  private int size;
  private static char[] chars =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  private static String value(int key) {
    return
      chars[key % chars.length] +
      Integer.toString(key / chars.length);
  }
  public CountMap(int size) {
    this.size = size < 0 ? 0 : size;
  }
  @Override
  public String get(Object key) {
    return value((Integer)key);
  }
  private static class Entry
  implements Map.Entry<Integer,String> {
    int index;
    Entry(int index) { this.index = index; }
    @Override
    public boolean equals(Object o) {
      return o instanceof Entry &&
        Objects.equals(index, ((Entry)o).index);
    }
    @Override
    public Integer getKey() { return index; }
    @Override
    public String getValue() {
      return value(index);
    }
    @Override
    public String setValue(String value) {
      throw new UnsupportedOperationException();
    }
    @Override
    public int hashCode() {
      return Objects.hashCode(index);
    }
  }
  @Override
  public Set<Map.Entry<Integer,String>> entrySet() {
    // LinkedHashSet retains initialization order:
    return IntStream.range(0, size)
      .mapToObj(Entry::new)
      .collect(Collectors
        .toCollection(LinkedHashSet::new));
  }
  public static void main(String[] args) {
    final int size = 6;
    CountMap cm = new CountMap(60);
    System.out.println(cm);
    System.out.println(cm.get(500));
    cm.values().stream()
      .limit(size)
      .forEach(System.out::println);
    System.out.println();
    new Random(47).ints(size, 0, 1000)
      .mapToObj(cm::get)
      .forEach(System.out::println);
  }
}
/* Output:
{0=A0, 1=B0, 2=C0, 3=D0, 4=E0, 5=F0, 6=G0, 7=H0, 8=I0,
9=J0, 10=K0, 11=L0, 12=M0, 13=N0, 14=O0, 15=P0, 16=Q0,
17=R0, 18=S0, 19=T0, 20=U0, 21=V0, 22=W0, 23=X0, 24=Y0,
25=Z0, 26=A1, 27=B1, 28=C1, 29=D1, 30=E1, 31=F1, 32=G1,
33=H1, 34=I1, 35=J1, 36=K1, 37=L1, 38=M1, 39=N1, 40=O1,
41=P1, 42=Q1, 43=R1, 44=S1, 45=T1, 46=U1, 47=V1, 48=W1,
49=X1, 50=Y1, 51=Z1, 52=A2, 53=B2, 54=C2, 55=D2, 56=E2,
57=F2, 58=G2, 59=H2}
G19
A0
B0
C0
D0
E0
F0

Y9
J21
R26
D33
Z36
N16
*/
```

要创建一个只读的 **Map** ，则从 **AbstractMap** 继承并实现 `entrySet()` 。私有的 `value()` 方法计算任何键的值，并在 `get()` 和 `Entry.getValue()` 中使用。可以忽略 **CountMap** 的大小。

这里是使用了 **LinkedHashSet** 而不是创建自定义 **Set** 类，因此并未完全实现享元。只有在调用 `entrySet()` 时才会生成此对象。

现在创建一个更复杂的享元。这个示例中的数据集是世界各国及其首都的 **Map** 。 `capitals()` 方法生成一个国家和首都的 **Map** 。 `names()` 方法生成一个由国家名字组成的 **List** 。 当给定了表示所需大小的 **int** 参数时，两种方法都生成对应大小的列表片段：

```java
// onjava/Countries.java
// "Flyweight" Maps and Lists of sample data
// {java onjava.Countries}
package onjava;
import java.util.*;

public class Countries {
  public static final String[][] DATA = {
    // Africa
    {"ALGERIA","Algiers"},
    {"ANGOLA","Luanda"},
    {"BENIN","Porto-Novo"},
    {"BOTSWANA","Gaberone"},
    {"BURKINA FASO","Ouagadougou"},
    {"BURUNDI","Bujumbura"},
    {"CAMEROON","Yaounde"},
    {"CAPE VERDE","Praia"},
    {"CENTRAL AFRICAN REPUBLIC","Bangui"},
    {"CHAD","N'djamena"},
    {"COMOROS","Moroni"},
    {"CONGO","Brazzaville"},
    {"DJIBOUTI","Dijibouti"},
    {"EGYPT","Cairo"},
    {"EQUATORIAL GUINEA","Malabo"},
    {"ERITREA","Asmara"},
    {"ETHIOPIA","Addis Ababa"},
    {"GABON","Libreville"},
    {"THE GAMBIA","Banjul"},
    {"GHANA","Accra"},
    {"GUINEA","Conakry"},
    {"BISSAU","Bissau"},
    {"COTE D'IVOIR (IVORY COAST)","Yamoussoukro"},
    {"KENYA","Nairobi"},
    {"LESOTHO","Maseru"},
    {"LIBERIA","Monrovia"},
    {"LIBYA","Tripoli"},
    {"MADAGASCAR","Antananarivo"},
    {"MALAWI","Lilongwe"},
    {"MALI","Bamako"},
    {"MAURITANIA","Nouakchott"},
    {"MAURITIUS","Port Louis"},
    {"MOROCCO","Rabat"},
    {"MOZAMBIQUE","Maputo"},
    {"NAMIBIA","Windhoek"},
    {"NIGER","Niamey"},
    {"NIGERIA","Abuja"},
    {"RWANDA","Kigali"},
    {"SAO TOME E PRINCIPE","Sao Tome"},
    {"SENEGAL","Dakar"},
    {"SEYCHELLES","Victoria"},
    {"SIERRA LEONE","Freetown"},
    {"SOMALIA","Mogadishu"},
    {"SOUTH AFRICA","Pretoria/Cape Town"},
    {"SUDAN","Khartoum"},
    {"SWAZILAND","Mbabane"},
    {"TANZANIA","Dodoma"},
    {"TOGO","Lome"},
    {"TUNISIA","Tunis"},
    {"UGANDA","Kampala"},
    {"DEMOCRATIC REPUBLIC OF THE CONGO (ZAIRE)",
     "Kinshasa"},
    {"ZAMBIA","Lusaka"},
    {"ZIMBABWE","Harare"},
    // Asia
    {"AFGHANISTAN","Kabul"},
    {"BAHRAIN","Manama"},
    {"BANGLADESH","Dhaka"},
    {"BHUTAN","Thimphu"},
    {"BRUNEI","Bandar Seri Begawan"},
    {"CAMBODIA","Phnom Penh"},
    {"CHINA","Beijing"},
    {"CYPRUS","Nicosia"},
    {"INDIA","New Delhi"},
    {"INDONESIA","Jakarta"},
    {"IRAN","Tehran"},
    {"IRAQ","Baghdad"},
    {"ISRAEL","Jerusalem"},
    {"JAPAN","Tokyo"},
    {"JORDAN","Amman"},
    {"KUWAIT","Kuwait City"},
    {"LAOS","Vientiane"},
    {"LEBANON","Beirut"},
    {"MALAYSIA","Kuala Lumpur"},
    {"THE MALDIVES","Male"},
    {"MONGOLIA","Ulan Bator"},
    {"MYANMAR (BURMA)","Rangoon"},
    {"NEPAL","Katmandu"},
    {"NORTH KOREA","P'yongyang"},
    {"OMAN","Muscat"},
    {"PAKISTAN","Islamabad"},
    {"PHILIPPINES","Manila"},
    {"QATAR","Doha"},
    {"SAUDI ARABIA","Riyadh"},
    {"SINGAPORE","Singapore"},
    {"SOUTH KOREA","Seoul"},
    {"SRI LANKA","Colombo"},
    {"SYRIA","Damascus"},
    {"TAIWAN (REPUBLIC OF CHINA)","Taipei"},
    {"THAILAND","Bangkok"},
    {"TURKEY","Ankara"},
    {"UNITED ARAB EMIRATES","Abu Dhabi"},
    {"VIETNAM","Hanoi"},
    {"YEMEN","Sana'a"},
    // Australia and Oceania
    {"AUSTRALIA","Canberra"},
    {"FIJI","Suva"},
    {"KIRIBATI","Bairiki"},
    {"MARSHALL ISLANDS","Dalap-Uliga-Darrit"},
    {"MICRONESIA","Palikir"},
    {"NAURU","Yaren"},
    {"NEW ZEALAND","Wellington"},
    {"PALAU","Koror"},
    {"PAPUA NEW GUINEA","Port Moresby"},
    {"SOLOMON ISLANDS","Honaira"},
    {"TONGA","Nuku'alofa"},
    {"TUVALU","Fongafale"},
    {"VANUATU","Port Vila"},
    {"WESTERN SAMOA","Apia"},
    // Eastern Europe and former USSR
    {"ARMENIA","Yerevan"},
    {"AZERBAIJAN","Baku"},
    {"BELARUS (BYELORUSSIA)","Minsk"},
    {"BULGARIA","Sofia"},
    {"GEORGIA","Tbilisi"},
    {"KAZAKSTAN","Almaty"},
    {"KYRGYZSTAN","Alma-Ata"},
    {"MOLDOVA","Chisinau"},
    {"RUSSIA","Moscow"},
    {"TAJIKISTAN","Dushanbe"},
    {"TURKMENISTAN","Ashkabad"},
    {"UKRAINE","Kyiv"},
    {"UZBEKISTAN","Tashkent"},
    // Europe
    {"ALBANIA","Tirana"},
    {"ANDORRA","Andorra la Vella"},
    {"AUSTRIA","Vienna"},
    {"BELGIUM","Brussels"},
    {"BOSNIA-HERZEGOVINA","Sarajevo"},
    {"CROATIA","Zagreb"},
    {"CZECH REPUBLIC","Prague"},
    {"DENMARK","Copenhagen"},
    {"ESTONIA","Tallinn"},
    {"FINLAND","Helsinki"},
    {"FRANCE","Paris"},
    {"GERMANY","Berlin"},
    {"GREECE","Athens"},
    {"HUNGARY","Budapest"},
    {"ICELAND","Reykjavik"},
    {"IRELAND","Dublin"},
    {"ITALY","Rome"},
    {"LATVIA","Riga"},
    {"LIECHTENSTEIN","Vaduz"},
    {"LITHUANIA","Vilnius"},
    {"LUXEMBOURG","Luxembourg"},
    {"MACEDONIA","Skopje"},
    {"MALTA","Valletta"},
    {"MONACO","Monaco"},
    {"MONTENEGRO","Podgorica"},
    {"THE NETHERLANDS","Amsterdam"},
    {"NORWAY","Oslo"},
    {"POLAND","Warsaw"},
    {"PORTUGAL","Lisbon"},
    {"ROMANIA","Bucharest"},
    {"SAN MARINO","San Marino"},
    {"SERBIA","Belgrade"},
    {"SLOVAKIA","Bratislava"},
    {"SLOVENIA","Ljuijana"},
    {"SPAIN","Madrid"},
    {"SWEDEN","Stockholm"},
    {"SWITZERLAND","Berne"},
    {"UNITED KINGDOM","London"},
    {"VATICAN CITY","Vatican City"},
    // North and Central America
    {"ANTIGUA AND BARBUDA","Saint John's"},
    {"BAHAMAS","Nassau"},
    {"BARBADOS","Bridgetown"},
    {"BELIZE","Belmopan"},
    {"CANADA","Ottawa"},
    {"COSTA RICA","San Jose"},
    {"CUBA","Havana"},
    {"DOMINICA","Roseau"},
    {"DOMINICAN REPUBLIC","Santo Domingo"},
    {"EL SALVADOR","San Salvador"},
    {"GRENADA","Saint George's"},
    {"GUATEMALA","Guatemala City"},
    {"HAITI","Port-au-Prince"},
    {"HONDURAS","Tegucigalpa"},
    {"JAMAICA","Kingston"},
    {"MEXICO","Mexico City"},
    {"NICARAGUA","Managua"},
    {"PANAMA","Panama City"},
    {"ST. KITTS AND NEVIS","Basseterre"},
    {"ST. LUCIA","Castries"},
    {"ST. VINCENT AND THE GRENADINES","Kingstown"},
    {"UNITED STATES OF AMERICA","Washington, D.C."},
    // South America
    {"ARGENTINA","Buenos Aires"},
    {"BOLIVIA","Sucre (legal)/La Paz(administrative)"},
    {"BRAZIL","Brasilia"},
    {"CHILE","Santiago"},
    {"COLOMBIA","Bogota"},
    {"ECUADOR","Quito"},
    {"GUYANA","Georgetown"},
    {"PARAGUAY","Asuncion"},
    {"PERU","Lima"},
    {"SURINAME","Paramaribo"},
    {"TRINIDAD AND TOBAGO","Port of Spain"},
    {"URUGUAY","Montevideo"},
    {"VENEZUELA","Caracas"},
  };
  // Use AbstractMap by implementing entrySet()
  private static class FlyweightMap
  extends AbstractMap<String,String> {
    private static class Entry
    implements Map.Entry<String,String> {
      int index;
      Entry(int index) { this.index = index; }
      @Override
      public boolean equals(Object o) {
        return o instanceof FlyweightMap &&
          Objects.equals(DATA[index][0], o);
      }
      @Override
      public int hashCode() {
        return Objects.hashCode(DATA[index][0]);
      }
      @Override
      public String getKey() { return DATA[index][0]; }
      @Override
      public String getValue() {
        return DATA[index][1];
      }
      @Override
      public String setValue(String value) {
        throw new UnsupportedOperationException();
      }
    }
    // Implement size() & iterator() for AbstractSet:
    static class EntrySet
    extends AbstractSet<Map.Entry<String,String>> {
      private int size;
      EntrySet(int size) {
        if(size < 0)
          this.size = 0;
        // Can't be any bigger than the array:
        else if(size > DATA.length)
          this.size = DATA.length;
        else
          this.size = size;
      }
      @Override
      public int size() { return size; }
      private class Iter
      implements Iterator<Map.Entry<String,String>> {
        // Only one Entry object per Iterator:
        private Entry entry = new Entry(-1);
        @Override
        public boolean hasNext() {
          return entry.index < size - 1;
        }
        @Override
        public Map.Entry<String,String> next() {
          entry.index++;
          return entry;
        }
        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }
      }
      @Override
      public
      Iterator<Map.Entry<String,String>> iterator() {
        return new Iter();
      }
    }
    private static
    Set<Map.Entry<String,String>> entries =
      new EntrySet(DATA.length);
    @Override
    public Set<Map.Entry<String,String>> entrySet() {
      return entries;
    }
  }
  // Create a partial map of 'size' countries:
  static Map<String,String> select(final int size) {
    return new FlyweightMap() {
      @Override
      public Set<Map.Entry<String,String>> entrySet() {
        return new EntrySet(size);
      }
    };
  }
  static Map<String,String> map = new FlyweightMap();
  public static Map<String,String> capitals() {
    return map; // The entire map
  }
  public static Map<String,String> capitals(int size) {
    return select(size); // A partial map
  }
  static List<String> names =
    new ArrayList<>(map.keySet());
  // All the names:
  public static List<String> names() { return names; }
  // A partial list:
  public static List<String> names(int size) {
    return new ArrayList<>(select(size).keySet());
  }
  public static void main(String[] args) {
    System.out.println(capitals(10));
    System.out.println(names(10));
    System.out.println(new HashMap<>(capitals(3)));
    System.out.println(
      new LinkedHashMap<>(capitals(3)));
    System.out.println(new TreeMap<>(capitals(3)));
    System.out.println(new Hashtable<>(capitals(3)));
    System.out.println(new HashSet<>(names(6)));
    System.out.println(new LinkedHashSet<>(names(6)));
    System.out.println(new TreeSet<>(names(6)));
    System.out.println(new ArrayList<>(names(6)));
    System.out.println(new LinkedList<>(names(6)));
    System.out.println(capitals().get("BRAZIL"));
  }
}
/* Output:
{ALGERIA=Algiers, ANGOLA=Luanda, BENIN=Porto-Novo,
BOTSWANA=Gaberone, BURKINA FASO=Ouagadougou,
BURUNDI=Bujumbura, CAMEROON=Yaounde, CAPE VERDE=Praia,
CENTRAL AFRICAN REPUBLIC=Bangui, CHAD=N'djamena}
[ALGERIA, ANGOLA, BENIN, BOTSWANA, BURKINA FASO,
BURUNDI, CAMEROON, CAPE VERDE, CENTRAL AFRICAN
REPUBLIC, CHAD]
{BENIN=Porto-Novo, ANGOLA=Luanda, ALGERIA=Algiers}
{ALGERIA=Algiers, ANGOLA=Luanda, BENIN=Porto-Novo}
{ALGERIA=Algiers, ANGOLA=Luanda, BENIN=Porto-Novo}
{ALGERIA=Algiers, ANGOLA=Luanda, BENIN=Porto-Novo}
[BENIN, BOTSWANA, ANGOLA, BURKINA FASO, ALGERIA,
BURUNDI]
[ALGERIA, ANGOLA, BENIN, BOTSWANA, BURKINA FASO,
BURUNDI]
[ALGERIA, ANGOLA, BENIN, BOTSWANA, BURKINA FASO,
BURUNDI]
[ALGERIA, ANGOLA, BENIN, BOTSWANA, BURKINA FASO,
BURUNDI]
[ALGERIA, ANGOLA, BENIN, BOTSWANA, BURKINA FASO,
BURUNDI]
Brasilia
*/
```

二维数组 **String DATA** 是 **public** 的，因此可以在别处使用。 **FlyweightMap** 必须实现 `entrySet()` 方法，该方法需要一个自定义 **Set** 实现和一个自定义 **Map.Entry** 类。这是实现享元的另一种方法：每个 **Map.Entry** 对象存储它自身的索引，而不是实际的键和值。当调用 `getKey()` 或 `getValue()` 时，它使用索引返回相应的 **DATA** 元素。 **EntrySet** 确保它的 **size** 不大于 **DATA** 。

享元的另一部分在 **EntrySet.Iterator** 中实现。相比于为 **DATA** 中的每个数据对创建一个 **Map.Entry** 对象，这里每个迭代器只有一个 **Map.Entry** 对象。 **Entry** 对象作为数据的窗口，它只包含 **String** 静态数组的索引。每次为迭代器调用 `next()` 时，**Entry** 中的索引都会递增，因此它会指向下一个数据对，然后从 `next()` 返回 **Iterators** 的单个 **Entry** 对象。[^1]

`select()` 方法生成一个包含所需大小的 **EntrySet** 的 **FlyweightMap** ，这用于在主方法中演示的重载的 `capitals()` 和 `names()` 方法。

<!-- Collection Functionality -->
## 集合功能

下面这个表格展示了可以对 **Collection** 执行的所有操作（不包括自动继承自 **Object** 的方法），因此，可以用 **List** ， **Set** ， **Queue** 或 **Deque** 执行这里的所有操作（这些接口可能也提供了一些其他的功能）。**Map** 不是从 **Collection** 继承的，所以要单独处理它。

| 方法名 | 描述 |
| :---: | :---: |
| **boolean add(T)** | 确保集合包含该泛型类型 **T** 的参数。如果不添加参数，则返回 **false** 。 （这是一种“可选”方法，将在下一节中介绍。） |
| **boolean addAll(Collection\<? extends T\>)** | 添加参数集合中的所有元素。只要有元素被成功添加则返回 **true**。（“可选的”） |
| **void clear()** | 删除集合中的所有元素。（“可选的”） |
| **boolean contains(T)** | 如果目标集合包含该泛型类型 **T** 的参数，则返回 **true** 。 |
| **boolean containsAll(Collection\<?\>)** | 如果目标集合包含参数集合中的所有元素，则返回 **true** |
| **boolean isEmpty()** | 如果集合为空，则返回 **true** |
| **Iterator\<T\> iterator() Spliterator\<T\> spliterator()** | 返回一个迭代器来遍历集合中的元素。 **Spliterators** 更复杂一些，它用在并发场景 |
| **boolean remove(Object)** | 如果目标集合包含该参数，则在集合中删除该参数，如果成功删除则返回 **true** 。（“可选的”） |
| **boolean removeAll(Collection\<?\>)** | 删除目标集合中，参数集合所包含的全部元素。如果有元素被成功删除则返回 **true** 。 （“可选的”） |
| **boolean removeIf(Predicate\<? super E\>)** | 删除此集合中，满足给定谓词（predicate）的所有元素 |
| **Stream\<E\> stream() Stream\<E\> parallelStream()** | 返回由该 **Collection** 中元素所组成的一个 **Stream** |
| **int size()** | 返回集合中所包含元素的个数 |
| **Object[] toArrat()** | 返回包含该集合所有元素的一个数组 |
| **\<T\> T[] toArray(T[] a)** | 返回包含该集合所有元素的一个数组。结果的运行时类型是参数数组而不是普通的 **Object** 数组。 |

这里没有提供用于随机访问元素的 **get()** 方法，因为 **Collection** 还包含 **Set** ，它维护自己的内部排序，所以随机访问查找就没有意义了。因此，要查找 **Collection** 中的元素必须使用迭代器。

下面这个示例演示了 **Collection** 的所有方法。这里以 **ArrayList** 为例：

```java
// collectiontopics/CollectionMethods.java
// Things you can do with all Collections
import java.util.*;
import static onjava.HTMLColors.*;

public class CollectionMethods {
  public static void main(String[] args) {
    Collection<String> c =
        new ArrayList<>(LIST.subList(0, 4));
    c.add("ten");
    c.add("eleven");
    show(c);
    border();
    // Make an array from the List:
    Object[] array = c.toArray();
    // Make a String array from the List:
    String[] str = c.toArray(new String[0]);
    // Find max and min elements; this means
    // different things depending on the way
    // the Comparable interface is implemented:
    System.out.println(
      "Collections.max(c) = " + Collections.max(c));
    System.out.println(
      "Collections.min(c) = " + Collections.min(c));
    border();
    // Add a Collection to another Collection
    Collection<String> c2 =
        new ArrayList<>(LIST.subList(10, 14));
    c.addAll(c2);
    show(c);
    border();
    c.remove(LIST.get(0));
    show(c);
    border();
    // Remove all components that are
    // in the argument collection:
    c.removeAll(c2);
    show(c);
    border();
    c.addAll(c2);
    show(c);
    border();
    // Is an element in this Collection?
    String val = LIST.get(3);
    System.out.println(
      "c.contains(" + val  + ") = " + c.contains(val));
    // Is a Collection in this Collection?
    System.out.println(
      "c.containsAll(c2) = " + c.containsAll(c2));
    Collection<String> c3 =
      ((List<String>)c).subList(3, 5);
    // Keep all the elements that are in both
    // c2 and c3 (an intersection of sets):
    c2.retainAll(c3);
    show(c2);
    // Throw away all the elements
    // in c2 that also appear in c3:
    c2.removeAll(c3);
    System.out.println(
      "c2.isEmpty() = " +  c2.isEmpty());
    border();
    // Functional operation:
    c = new ArrayList<>(LIST);
    c.removeIf(s -> !s.startsWith("P"));
    c.removeIf(s -> s.startsWith("Pale"));
    // Stream operation:
    c.stream().forEach(System.out::println);
    c.clear(); // Remove all elements
    System.out.println("after c.clear():" + c);
  }
}
/* Output:
AliceBlue
AntiqueWhite
Aquamarine
Azure
ten
eleven
******************************
Collections.max(c) = ten
Collections.min(c) = AliceBlue
******************************
AliceBlue
AntiqueWhite
Aquamarine
Azure
ten
eleven
Brown
BurlyWood
CadetBlue
Chartreuse
******************************
AntiqueWhite
Aquamarine
Azure
ten
eleven
Brown
BurlyWood
CadetBlue
Chartreuse
******************************
AntiqueWhite
Aquamarine
Azure
ten
eleven
******************************
AntiqueWhite
Aquamarine
Azure
ten
eleven
Brown
BurlyWood
CadetBlue
Chartreuse
******************************
c.contains(Azure) = true
c.containsAll(c2) = true
c2.isEmpty() = true
******************************
PapayaWhip
PeachPuff
Peru
Pink
Plum
PowderBlue
Purple
after c.clear():[]
*/
```

为了只演示 **Collection** 接口的方法，而没有其它额外的内容，所以这里创建包含不同数据集的 **ArrayList** ，并向上转型为 **Collection** 对象。

<!-- Optional Operations -->
## 可选操作

在 **Collection** 接口中执行各种添加和删除操作的方法是 *可选操作* （optional operations）。这意味着实现类不需要为这些方法提供功能定义。

这是一种非常不寻常的定义接口的方式。正如我们所知，接口是一种合约（contract）。它表达的意思是，“无论你如何选择实现这个接口，我保证你可以将这些消息发送到这个对象”（我在这里使用术语“接口”来描述正式的 **interface** 关键字和“任何类或子类都支持的方法”的更一般含义）。但“可选”操作违反了这一基本原则，它表示调用某些方法不会执行有意义的行为。相反，它们会抛出异常！这看起来似乎丢失了编译时的类型安全性。

其实没那么糟糕。如果操作是可选的，编译器仍然能够限制你仅调用该接口中的方法。它不像动态语言那样，可以为任何对象调用任何方法，并在运行时查找特定的调用是否可行。[^2]此外，大多数将 **Collection** 作为参数的方法仅从该 **Collection** 中读取，并且 **Collection** 的所有“读取”方法都不是可选的。

为什么要将方法定义为“可选”的？因为这样做可以防止设计中的接口爆炸。集合库的其他设计往往会产生令人困惑的过多接口来描述主题的每个变体。这甚至使得不可能捕获到接口中的所有特殊情况，因为总有人能发明一个新的接口。“不支持的操作（unsupported operation）”这种方式实现了Java集合库的一个重要目标：集合要易于学习和使用。不支持的操作是一种特殊情况，可以推迟到必要的时候。但是，要使用此方法：

1. **UnsupportedOperationException** 必须是一个罕见的事件。也就是说，对于大多数类，所有操作都应该起作用，并且只有在特殊情况下才应该不支持某项操作。这在Java集合库中是正确的，因为99%的时间使用到的类 —— **ArrayList** ， **LinkedList** ， **HashSet** 和 **HashMap** ，以及其他具体实现，都支持所有操作。该设计确实为创建一个新的 **Collection** 提供了一个“后门”，可以不为 **Collection** 接口中的所有方法都提供有意义的定义，这些定义仍然适合现有的类库。

2. 当不支持某个操作时， **UnsupportedOperationException** 应该出现在实现阶段，而不是在将产品发送给客户之后。毕竟，这个异常表示编程错误：错误地使用了一个具体实现。

值得注意的是，不支持的操作只能在运行时检测到，因此这代表动态类型检查。如果你来自像 C++ 这样的静态类型语言，Java 可能看起来只是另一种静态类型语言。当然， Java 肯定有静态类型检查，但它也有大量的动态类型，因此很难说它只是静态语言或动态语言。一旦你开始注意到这一点，你就会开始看到 Java 中动态类型检查的其他示例。

<!-- Unsupported Operations -->
### 不支持的操作

不支持的操作的常见来源是由固定大小的数据结构所支持的集合。使用 `Arrays.asList()` 方法将数组转换为 **List** 时，就会得到这样的集合。此外，还可以选择使用 **Collections** 类中的“不可修改（unmodifiable）”方法使任何集合（包括 **Map** ）抛出 **UnsupportedOperationException** 异常。此示例展示了这两种情况：

```java
// collectiontopics/Unsupported.java
// Unsupported operations in Java collections
import java.util.*;

public class Unsupported {
  static void
  check(String description, Runnable tst) {
    try {
      tst.run();
    } catch(Exception e) {
      System.out.println(description + "(): " + e);
    }
  }
  static void test(String msg, List<String> list) {
    System.out.println("--- " + msg + " ---");
    Collection<String> c = list;
    Collection<String> subList = list.subList(1,8);
    // Copy of the sublist:
    Collection<String> c2 = new ArrayList<>(subList);
    check("retainAll", () -> c.retainAll(c2));
    check("removeAll", () -> c.removeAll(c2));
    check("clear", () -> c.clear());
    check("add", () -> c.add("X"));
    check("addAll", () -> c.addAll(c2));
    check("remove", () -> c.remove("C"));
    // The List.set() method modifies the value but
    // doesn't change the size of the data structure:
    check("List.set", () -> list.set(0, "X"));
  }
  public static void main(String[] args) {
    List<String> list = Arrays.asList(
      "A B C D E F G H I J K L".split(" "));
    test("Modifiable Copy", new ArrayList<>(list));
    test("Arrays.asList()", list);
    test("unmodifiableList()",
      Collections.unmodifiableList(
        new ArrayList<>(list)));
  }
}
/* Output:
--- Modifiable Copy ---
--- Arrays.asList() ---
retainAll(): java.lang.UnsupportedOperationException
removeAll(): java.lang.UnsupportedOperationException
clear(): java.lang.UnsupportedOperationException
add(): java.lang.UnsupportedOperationException
addAll(): java.lang.UnsupportedOperationException
remove(): java.lang.UnsupportedOperationException
--- unmodifiableList() ---
retainAll(): java.lang.UnsupportedOperationException
removeAll(): java.lang.UnsupportedOperationException
clear(): java.lang.UnsupportedOperationException
add(): java.lang.UnsupportedOperationException
addAll(): java.lang.UnsupportedOperationException
remove(): java.lang.UnsupportedOperationException
List.set(): java.lang.UnsupportedOperationException
*/
```

因为 `Arrays.asList()` 生成的 **List** 由一个固定大小的数组所支持，所以唯一支持的操作是那些不改变数组大小的操作。任何会导致更改基础数据结构大小的方法都会产生 **UnsupportedOperationException** 异常，来说明这是对不支持的方法的调用（编程错误）。

请注意，始终可以将 `Arrays.asList()` 的结果作为一个参数传递给任何 **Collection** 的构造方法（或使用 `addAll()` 方法或静态的 `Collections.addAll()` 方法）来创建一个允许使用所有方法的常规集合，在主方法中第一次调用 `test()` 时显示了这种情况。这种调用产生了一个新的可调整大小的底层数据结构。

**Collections** 类中的“unmodifiable”方法会将集合包装一个代理中，如果执行任何想要修改集合的操作，则该代理会生成 **UnsupportedOperationException** 异常。使用这些方法的目的是生成一个“常量”集合对象。稍后将描述“unmodifiable“集合方法的完整列表。

`test()` 中的最后一个 `check()` 用于测试**List** 的 `set()` 方法。这里，“不支持的操作”技术的粒度（granularity）就派上用场了，得到的“接口”可以通过一种方法在 `Arrays.asList()` 返回的对象和 `Collections.unmodifiableList()` 返回的对象之间变换。 `Arrays.asList()` 返回固定大小的 **List** ，而 `Collections.unmodifiableList()` 生成无法更改的 **List** 。如输出中所示， `Arrays.asList()` 返回的 **List** 中的元素是可以修改的，因为这不会违反该 **List** 的“固定大小”特性。但很明显， `unmodifiableList()` 的结果不应该以任何方式修改。如果使用接口来描述，则需要两个额外的接口，一个具有可用的 `set()` 方法，而另一个没有。 **Collection** 的各种不可修改的子类型都将需要额外的接口。

如果一个方法将一个集合作为它的参数，那么它的文档应该说明必须实现哪些可选方法。

<!-- Sets and Storage Order -->
## Set和存储顺序


<!-- Queues -->
## 队列


<!-- Understanding Maps -->
## 理解Map


<!-- Utilities -->
## 集合工具类


<!-- Holding References -->
## 持有引用


<!-- Java 1.0/1.1 Collections -->
## 避免旧式类库


<!-- Summary -->
## 本章小结





[^1]: **java.util** 中的 **Map** 使用 **Map** 的 `getKey()` 和 `getValue()` 执行批量复制，因此这是有效的。如果自定义 **Map** 只是复制整个 **Map.Entry** ，那么这种方法就会出现问题。

[^2]: 虽然当我用这种方式描述它的时候听起来很奇怪而且好像没什么用处，但在[第十九章 类型信息]()章节中已经看到过，这种动态行为也可以非常强大有用。


<!-- 分页 -->

<div style="page-break-after: always;"></div>