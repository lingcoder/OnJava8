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
| :---: | :--- |
| **boolean add(T)** | 确保集合包含该泛型类型 **T** 的参数。如果不添加参数，则返回 **false** 。 （这是一种“可选”方法，将在下一节中介绍。） |
| **boolean addAll(Collection\<? extends T\>)** | 添加参数集合中的所有元素。只要有元素被成功添加则返回 **true**。（“可选的”） |
| **void clear()** | 删除集合中的所有元素。（“可选的”） |
| **boolean contains(T)** | 如果目标集合包含该泛型类型 **T** 的参数，则返回 **true** 。 |
| **boolean containsAll(Collection\<?\>)** | 如果目标集合包含参数集合中的所有元素，则返回 **true** |
| **boolean isEmpty()** | 如果集合为空，则返回 **true** |
| **Iterator\<T\> iterator() Spliterator\<T\> spliterator()** | 返回一个迭代器来遍历集合中的元素。 **Spliterators** 更复杂一些，它用在并发场景 |
| **boolean remove(Object)** | 如果目标集合包含该参数，则在集合中删除该参数，如果成功删除则返回 **true** 。（“可选的”） |
| **boolean removeAll(Collection\<?\>)** | 删除目标集合中，参数集合所包含的全部元素。如果有元素被成功删除则返回 **true** 。 （“可选的”） |
| **boolean removeIf(Predicate\<? super E\>)** | 删除此集合中，满足给定断言（predicate）的所有元素 |
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

[第十二章 集合]()章节中的 **Set** 有关示例对 **Set** 的基本操作做了很好的介绍。 但是，这些示例可以方便地使用预定义的 Java 类型，例如 **Integer** 和 **String** ，它们可以在集合中使用。在创建自己的类型时请注意， **Set** （以及稍后会看到的 **Map** ）需要一种维护存储顺序的方法，该顺序因 **Set** 的不同实现而异。因此，不同的 **Set** 实现不仅具有不同的行为，而且它们对可以放入特定 **Set** 中的对象类型也有不同的要求：

| **Set** 类型 | 约束 |
| :---: | :--- |
| **Set(interface)** | 添加到 **Set** 中的每个元素必须是唯一的，否则，**Set** 不会添加重复元素。添加到 **Set** 的元素必须至少定义 `equals()` 方法以建立对象唯一性。 **Set** 与 **Collection** 具有完全相同的接口。 **Set** 接口不保证它将以任何特定顺序维护其元素。 |
| **HashSet\*** | 注重快速查找元素的集合，其中元素必须定义 `hashCode()` 和 `equals()` 方法。 |
| **TreeSet** | 由树支持的有序 **Set**。这样，就可以从 **Set** 中获取有序序列，其中元素必须实现 **Comparable** 接口。 |
| **LinkedHashSet** | 具有 **HashSet** 的查找速度，但在内部使用链表维护元素的插入顺序。因此，当在遍历 **Set** 时，结果将按元素的插入顺序显示。元素必须定义 `hashCode()` 和 `equals()` 方法。 |

**HashSet** 上的星号表示，在没有其他约束的情况下，这应该是你的默认选择，因为它针对速度进行了优化。

定义 `hashCode()` 方法在[附录:理解equals和hashCode方法]()中进行了描述。必须为散列和树存储结构创建 `equals()` 方法，但只有当把类放在 **HashSet** 中时才需要 `hashCode()` （当然这很有可能，因为 **HashSet** 通常应该是作为 **Set** 实现的首选）或 **LinkedHashSet** 。 但是，作为一种良好的编程风格，在覆盖 `equals()` 时应始终覆盖 `hashCode()` 。

下面的示例演示了成功使用具有特定 **Set** 实现的类型所需的方法：

```java
// collectiontopics/TypesForSets.java
// Methods necessary to put your own type in a Set
import java.util.*;
import java.util.function.*;
import java.util.Objects;

class SetType {
  protected int i;
  SetType(int n) { i = n; }
  @Override
  public boolean equals(Object o) {
    return o instanceof SetType &&
      Objects.equals(i, ((SetType)o).i);
  }
  @Override
  public String toString() {
    return Integer.toString(i);
  }
}

class HashType extends SetType {
  HashType(int n) { super(n); }
  @Override
  public int hashCode() {
    return Objects.hashCode(i);
  }
}

class TreeType extends SetType
implements Comparable<TreeType> {
  TreeType(int n) { super(n); }
  @Override
  public int compareTo(TreeType arg) {
    return Integer.compare(arg.i, i);
    // Equivalent to:
    // return arg.i < i ? -1 : (arg.i == i ? 0 : 1);
  }
}

public class TypesForSets {
  static <T> void
  fill(Set<T> set, Function<Integer, T> type) {
    for(int i = 10; i >= 5; i--) // Descending
      set.add(type.apply(i));
    for(int i = 0; i < 5; i++) // Ascending
      set.add(type.apply(i));
  }
  static <T> void
  test(Set<T> set, Function<Integer, T> type) {
    fill(set, type);
    fill(set, type); // Try to add duplicates
    fill(set, type);
    System.out.println(set);
  }
  public static void main(String[] args) {
    test(new HashSet<>(), HashType::new);
    test(new LinkedHashSet<>(), HashType::new);
    test(new TreeSet<>(), TreeType::new);
    // Things that don't work:
    test(new HashSet<>(), SetType::new);
    test(new HashSet<>(), TreeType::new);
    test(new LinkedHashSet<>(), SetType::new);
    test(new LinkedHashSet<>(), TreeType::new);
    try {
      test(new TreeSet<>(), SetType::new);
    } catch(Exception e) {
      System.out.println(e.getMessage());
    }
    try {
      test(new TreeSet<>(), HashType::new);
    } catch(Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
/* Output:
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
[10, 9, 8, 7, 6, 5, 0, 1, 2, 3, 4]
[10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0]
[1, 6, 8, 6, 2, 7, 8, 9, 4, 10, 7, 5, 1, 3, 4, 9, 9,
10, 5, 3, 2, 0, 4, 1, 2, 0, 8, 3, 0, 10, 6, 5, 7]
[3, 1, 4, 8, 7, 6, 9, 5, 3, 0, 10, 5, 5, 10, 7, 8, 8,
9, 1, 4, 10, 2, 6, 9, 1, 6, 0, 3, 2, 0, 7, 2, 4]
[10, 9, 8, 7, 6, 5, 0, 1, 2, 3, 4, 10, 9, 8, 7, 6, 5,
0, 1, 2, 3, 4, 10, 9, 8, 7, 6, 5, 0, 1, 2, 3, 4]
[10, 9, 8, 7, 6, 5, 0, 1, 2, 3, 4, 10, 9, 8, 7, 6, 5,
0, 1, 2, 3, 4, 10, 9, 8, 7, 6, 5, 0, 1, 2, 3, 4]
SetType cannot be cast to java.lang.Comparable
HashType cannot be cast to java.lang.Comparable
*/
```

为了证明特定 **Set** 需要哪些方法，同时避免代码重复，这里创建了三个类。基类 **SetType** 存储一个 **int** 值，并通过 `toString()` 方法打印它。由于存储在 **Set** 中的所有类都必须具有 `equals()` ，因此该方法也放在基类中。基于 `int i` 来判断元素是否相等。

**HashType** 继承自 **SetType** ，并添加了 `hashCode()` 方法，该方法对于 **Set** 的散列实现是必需的。

要在任何类型的有序集合中使用对象，由 **TreeType** 实现的 **Comparable** 接口都是必需的，例如 **SortedSet** （ **TreeSet** 是其唯一实现）。在 `compareTo()` 中，请注意我没有使用“简单明了”的形式： `return i-i2` 。虽然这是一个常见的编程错误，但只有当 **i** 和 **i2** 是“无符号（unsigned）”整型时才能正常工作（如果 Java 有一个“unsigned”关键字的话，不过它没有）。它破坏了 Java 的有符号 **int** ，它不足以代表两个有符号整数的差异。如果 **i** 是一个大的正整数而 **j** 是一个大的负整数， `i-j` 将溢出并返回一个负值，这不是我们所需要的。

通常希望 `compareTo()` 方法生成与 `equals()` 方法一致的自然顺序。如果 `equals()` 对于特定比较产生 **true**，则 `compareTo()` 应该为该比较返回结果 零，并且如果 `equals()` 为比较产生 **false** ，则 `compareTo()` 应该为该比较产生非零结果。

在 **TypesForSets** 中， `fill()` 和 `test()` 都是使用泛型定义的，以防止代码重复。为了验证 **Set** 的行为， `test()` 在测试集上调用 `fill()` 三次，尝试引入重复的对象。 `fill()` 方法的参数可以接收任意一个 **Set** 类型，以及生成该类型的 **Function** 对象。因为此示例中使用的所有对象都有一个带有单个 **int** 参数的构造方法，所以可以将构造方法作为此 **Function** 传递，它将提供用于填充 **Set** 的对象。

请注意， `fill()` 方法按降序添加前五个元素，按升序添加后五个元素，以此来指出生成的存储顺序。输出显示 **HashSet** 按升序保留元素，但是，在[附录:理解equals和hashCode方法]()中，你会发现这只是偶然的，因为散列会创建自己的存储顺序。这里只是因为元素是一个简单的 **int** ，在这种情况下它是升序的。 **LinkedHashSet** 按照插入顺序保存元素，**TreeSet** 按排序顺序维护元素（在此示例中因为 `compareTo()` 的实现方式，所以元素按降序排列。）

特定的 **Set** 类型一般都有所必需的操作，如果尝试使用没能正确支持这些操作的类型，那么事情就会出错。将没有重新定义 `hashCode()` 方法的 **SetType** 或 **TreeType** 对象放入任何散列实现会导致重复值，因此违反了 **Set** 的主要契约。 这是相当令人不安的，因为这甚至不产生运行时错误。但是，默认的 `hashCode()` 是合法的，所以即使它是不正确的，这也是合法的行为。确保此类程序正确性的唯一可靠方法是将单元测试合并到构建系统中。

如果尝试在 **TreeSet** 中使用没有实现 **Comparable** 接口的类型，则会得到更明确的结果：当 **TreeSet** 尝试将对象用作一个 **Comparable** 时，将会抛出异常。

<!-- SortedSet -->
### SortedSet

**SortedSet** 中的元素保证按排序规则顺序， **SortedSet** 接口中的以下方法可以产生其他功能：

- `Comparator comparator()` ：生成用于此 **Set** 的**Comparator** 或 **null** 来用于自然排序。
- `Object first()` ：返回第一个元素。
- `Object last()` ：返回最后一个元素。
- `SortedSet subSet(fromElement，toElement)` ：使用 **fromElement** （包含）和 **toElement** （不包括）中的元素生成此 **Set** 的一个视图。
- `SortedSet headSet(toElement)` ：使用顺序在 **toElement** 之前的元素生成此 **Set** 的一个视图。
- `SortedSet tailSet(fromElement)` ：使用顺序在 **fromElement** 之后（包含 **fromElement** ）的元素生成此 **Set** 的一个视图。

下面是一个简单的演示：

```java
// collectiontopics/SortedSetDemo.java
import java.util.*;
import static java.util.stream.Collectors.*;

public class SortedSetDemo {
  public static void main(String[] args) {
    SortedSet<String> sortedSet =
      Arrays.stream(
        "one two three four five six seven eight"
        .split(" "))
        .collect(toCollection(TreeSet::new));
    System.out.println(sortedSet);
    String low = sortedSet.first();
    String high = sortedSet.last();
    System.out.println(low);
    System.out.println(high);
    Iterator<String> it = sortedSet.iterator();
    for(int i = 0; i <= 6; i++) {
      if(i == 3) low = it.next();
      if(i == 6) high = it.next();
      else it.next();
    }
    System.out.println(low);
    System.out.println(high);
    System.out.println(sortedSet.subSet(low, high));
    System.out.println(sortedSet.headSet(high));
    System.out.println(sortedSet.tailSet(low));
  }
}
/* Output:
[eight, five, four, one, seven, six, three, two]
eight
two
one
two
[one, seven, six, three]
[eight, five, four, one, seven, six, three]
[one, seven, six, three, two]
*/
```

注意， **SortedSet** 表示“根据对象的比较函数进行排序”，而不是“根据插入顺序”。可以使用 **LinkedHashSet** 保留元素的插入顺序。

<!-- Queues -->
## 队列

有许多 **Queue** 实现，其中大多数是为并发应用程序设计的。许多实现都是通过排序行为而不是性能来区分的。这是一个涉及大多数 **Queue** 实现的基本示例，包括基于并发的队列。队列将元素从一端放入并从另一端取出：

```java
// collectiontopics/QueueBehavior.java
// Compares basic behavior
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

public class QueueBehavior {
  static Stream<String> strings() {
    return Arrays.stream(
      ("one two three four five six seven " +
       "eight nine ten").split(" "));
  }
  static void test(int id, Queue<String> queue) {
    System.out.print(id + ": ");
    strings().map(queue::offer).count();
    while(queue.peek() != null)
      System.out.print(queue.remove() + " ");
    System.out.println();
  }
  public static void main(String[] args) {
    int count = 10;
    test(1, new LinkedList<>());
    test(2, new PriorityQueue<>());
    test(3, new ArrayBlockingQueue<>(count));
    test(4, new ConcurrentLinkedQueue<>());
    test(5, new LinkedBlockingQueue<>());
    test(6, new PriorityBlockingQueue<>());
    test(7, new ArrayDeque<>());
    test(8, new ConcurrentLinkedDeque<>());
    test(9, new LinkedBlockingDeque<>());
    test(10, new LinkedTransferQueue<>());
    test(11, new SynchronousQueue<>());
  }
}
/* Output:
1: one two three four five six seven eight nine ten
2: eight five four nine one seven six ten three two
3: one two three four five six seven eight nine ten
4: one two three four five six seven eight nine ten
5: one two three four five six seven eight nine ten
6: eight five four nine one seven six ten three two
7: one two three four five six seven eight nine ten
8: one two three four five six seven eight nine ten
9: one two three four five six seven eight nine ten
10: one two three four five six seven eight nine ten
11:
*/
```

**Deque** 接口也继承自 **Queue** 。 除优先级队列外，**Queue** 按照元素的插入顺序生成元素。 在此示例中，**SynchronousQueue** 不会产生任何结果，因为它是一个阻塞队列，其中每个插入操作必须等待另一个线程执行相应的删除操作，反之亦然。

<!-- Priority Queues -->
### 优先级队列

考虑一个待办事项列表，其中每个对象包含一个 **String** 以及主要和次要优先级值。通过实现 **Comparable** 接口来控制此待办事项列表的顺序：

```java
// collectiontopics/ToDoList.java
// A more complex use of PriorityQueue
import java.util.*;

class ToDoItem implements Comparable<ToDoItem> {
  private char primary;
  private int secondary;
  private String item;
  ToDoItem(String td, char pri, int sec) {
    primary = pri;
    secondary = sec;
    item = td;
  }
  @Override
  public int compareTo(ToDoItem arg) {
    if(primary > arg.primary)
      return +1;
    if(primary == arg.primary)
      if(secondary > arg.secondary)
        return +1;
      else if(secondary == arg.secondary)
        return 0;
    return -1;
  }
  @Override
  public String toString() {
    return Character.toString(primary) +
      secondary + ": " + item;
  }
}

class ToDoList {
  public static void main(String[] args) {
    PriorityQueue<ToDoItem> toDo =
      new PriorityQueue<>();
    toDo.add(new ToDoItem("Empty trash", 'C', 4));
    toDo.add(new ToDoItem("Feed dog", 'A', 2));
    toDo.add(new ToDoItem("Feed bird", 'B', 7));
    toDo.add(new ToDoItem("Mow lawn", 'C', 3));
    toDo.add(new ToDoItem("Water lawn", 'A', 1));
    toDo.add(new ToDoItem("Feed cat", 'B', 1));
    while(!toDo.isEmpty())
      System.out.println(toDo.remove());
  }
}
/* Output:
A1: Water lawn
A2: Feed dog
B1: Feed cat
B7: Feed bird
C3: Mow lawn
C4: Empty trash
*/
```

这展示了通过优先级队列自动排序待办事项。

<!-- Deque -->
### 双端队列

**Deque** （双端队列）就像一个队列，但是可以从任一端添加和删除元素。 Java 6为 **Deque** 添加了一个显式接口。以下是对实现了 **Deque** 的类的最基本的 **Deque** 方法的测试：

```java
// collectiontopics/SimpleDeques.java
// Very basic test of Deques
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

class CountString implements Supplier<String> {
  private int n = 0;
  CountString() {}
  CountString(int start) { n = start; }
  @Override
  public String get() {
    return Integer.toString(n++);
  }
}

public class SimpleDeques {
  static void test(Deque<String> deque) {
    CountString s1 = new CountString(),
                s2 = new CountString(20);
    for(int n = 0; n < 8; n++) {
        deque.offerFirst(s1.get());
        deque.offerLast(s2.get()); // Same as offer()
    }
    System.out.println(deque);
    String result = "";
    while(deque.size() > 0) {
      System.out.print(deque.peekFirst() + " ");
      result += deque.pollFirst() + " ";
      System.out.print(deque.peekLast() + " ");
      result += deque.pollLast() + " ";
    }
    System.out.println("\n" + result);
  }
  public static void main(String[] args) {
    int count = 10;
    System.out.println("LinkedList");
    test(new LinkedList<>());
    System.out.println("ArrayDeque");
    test(new ArrayDeque<>());
    System.out.println("LinkedBlockingDeque");
    test(new LinkedBlockingDeque<>(count));
    System.out.println("ConcurrentLinkedDeque");
    test(new ConcurrentLinkedDeque<>());
  }
}
/* Output:
LinkedList
[7, 6, 5, 4, 3, 2, 1, 0, 20, 21, 22, 23, 24, 25, 26,
27]
7 27 6 26 5 25 4 24 3 23 2 22 1 21 0 20
7 27 6 26 5 25 4 24 3 23 2 22 1 21 0 20
ArrayDeque
[7, 6, 5, 4, 3, 2, 1, 0, 20, 21, 22, 23, 24, 25, 26,
27]
7 27 6 26 5 25 4 24 3 23 2 22 1 21 0 20
7 27 6 26 5 25 4 24 3 23 2 22 1 21 0 20
LinkedBlockingDeque
[4, 3, 2, 1, 0, 20, 21, 22, 23, 24]
4 24 3 23 2 22 1 21 0 20
4 24 3 23 2 22 1 21 0 20
ConcurrentLinkedDeque
[7, 6, 5, 4, 3, 2, 1, 0, 20, 21, 22, 23, 24, 25, 26,
27]
7 27 6 26 5 25 4 24 3 23 2 22 1 21 0 20
7 27 6 26 5 25 4 24 3 23 2 22 1 21 0 20
*/
```

我只使用了 **Deque** 方法的“offer”和“poll”版本，因为当 **LinkedBlockingDeque** 的大小有限时，这些方法不会抛出异常。请注意， **LinkedBlockingDeque** 仅填充到它的限制大小为止，然后忽略额外的添加。

<!-- Understanding Maps -->
## 理解Map

正如在[第十二章 集合]()章节中所了解到的，**Map**（也称为 *关联数组* ）维护键值关联（对），因此可以使用键来查找值。标准 Java 库包含不同的 **Map** 基本实现，例如 **HashMap** ， **TreeMap** ， **LinkedHashMap** ， **WeakHashMap** ， **ConcurrentHashMap** 和 **IdentityHashMap** 。 它们都具有相同的基本 **Map** 接口，但它们的行为不同，包括效率，键值对的保存顺序和呈现顺序，保存对象的时间，如何在多线程程序中工作，以及如何确定键的相等性。 **Map** 接口的实现数量应该告诉你一些关于此工具重要性的信息。

为了更深入地了解 **Map** ，学习如何构造关联数组会很有帮助。下面是一个非常简单的实现：

```java
// collectiontopics/AssociativeArray.java
// Associates keys with values

public class AssociativeArray<K, V> {
  private Object[][] pairs;
  private int index;
  public AssociativeArray(int length) {
    pairs = new Object[length][2];
  }
  public void put(K key, V value) {
    if(index >= pairs.length)
      throw new ArrayIndexOutOfBoundsException();
    pairs[index++] = new Object[]{ key, value };
  }
  @SuppressWarnings("unchecked")
  public V get(K key) {
    for(int i = 0; i < index; i++)
      if(key.equals(pairs[i][0]))
        return (V)pairs[i][1];
    return null; // Did not find key
  }
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for(int i = 0; i < index; i++) {
      result.append(pairs[i][0].toString());
      result.append(" : ");
      result.append(pairs[i][1].toString());
      if(i < index - 1)
        result.append("\n");
    }
    return result.toString();
  }
  public static void main(String[] args) {
    AssociativeArray<String,String> map =
      new AssociativeArray<>(6);
    map.put("sky", "blue");
    map.put("grass", "green");
    map.put("ocean", "dancing");
    map.put("tree", "tall");
    map.put("earth", "brown");
    map.put("sun", "warm");
    try {
      map.put("extra", "object"); // Past the end
    } catch(ArrayIndexOutOfBoundsException e) {
      System.out.println("Too many objects!");
    }
    System.out.println(map);
    System.out.println(map.get("ocean"));
  }
}
/* Output:
Too many objects!
sky : blue
grass : green
ocean : dancing
tree : tall
earth : brown
sun : warm
dancing
*/
```

关联数组中的基本方法是 `put()` 和 `get()` ，但为了便于显示，重写了 `toString()` 方法以打印键值对。为了显示它的工作原理，主方法加载一个带有字符串对的 **AssociativeArray** 并打印生成的映射，然后调用其中一个值的 `get()` 方法。

要使用 `get()` 方法，可以传入要查找的 **key** ，它将生成相关联的值作为结果，如果找不到则返回 **null** 。 `get()` 方法使用可能是效率最低的方法来定位值：从数组的头部开始并使用 `equals()` 来比较键。但这里是侧重于简单，而不是效率。

这个版本很有启发性，但它不是很有效，而且它只有一个固定的大小，这是不灵活的。幸运的是， **java.util** 中的那些 **Map** 没有这些问题。

<!-- Performance -->
### 性能

性能是 **Map** 的基本问题，在 `get()` 中使用线性方法搜索一个键时会非常慢。这就是 **HashMap** 要加速的地方。它使用一个称为 *哈希码* 的特殊值来替代慢速搜索一个键。哈希码是一种从相关对象中获取一些信息并将其转换为该对象的“相对唯一” **int** 的方法。 `hashCode()` 是根类 **Object** 中的一个方法，因此所有 Java 对象都可以生成哈希码。 **HashMap** 获取对象的 `hashCode()` 并使用它来快速搜索键。这就使得性能有了显著的提升。[^3]

以下是基本的 **Map** 实现。 **HashMap**上的星号表示，在没有其他约束的情况下，这应该是你的默认选择，因为它针对速度进行了优化。其他实现强调其他特性，因此不如 **HashMap** 快。

| **Map** 实现 | 描述 |
| :---: | :--- |
| **HashMap\*** | 基于哈希表的实现。（使用此类来代替 **Hashtable** 。）为插入和定位键值对提供了常数时间性能。可以通过构造方法调整性能，这些构造方法允许你设置哈希表的容量和装填因子。 |
| **LinkedHashMap** | 与 **HashMap** 类似，但是当遍历时，可以按插入顺序或最近最少使用（LRU）顺序获取键值对。只比 **HashMap** 略慢，一个例外是在迭代时，由于其使用链表维护内部顺序，所以会更快些。 |
| **TreeMap** | 基于红黑树的实现。当查看键或键值对时，它们按排序顺序（由 **Comparable** 或 **Comparator** 确定）。 **TreeMap** 的侧重点是按排序顺序获得结果。 **TreeMap** 是唯一使用 `subMap()` 方法的 **Map** ，它返回红黑树的一部分。 |
| **WeakHashMap** | 一种具有 *弱键*（weak keys） 的 **Map** ，为了解决某些类型的问题，它允许释放 **Map** 所引用的对象。如果在 **Map** 外没有对特定键的引用，则可以对该键进行垃圾回收。 |
| **ConcurrentHashMap** | 不使用同步锁定的线程安全 **Mao** 。这在[第二十四章 并发编程]() 一章中讨论。 |
| **IdentityHashMap** | 使用 `==` 而不是 `equals()` 来比较键。仅用于解决特殊问题，不适用于一般用途。 |

散列是在 **Map** 中存储元素的最常用方法。

**Map** 中使用的键的要求与 **Set** 中的元素的要求相同。可以在 **TypesForSets.java** 中看到这些。任何键必须具有 `equals()` 方法。如果键用于散列映射，则它还必须具有正确的 `hashCode()` 方法。如果键在 **TreeMap** 中使用，则必须实现 **Comparable** 接口。

以下示例使用先前定义的 **CountMap** 测试数据集显示通过 **Map** 接口可用的操作：

```java
// collectiontopics/MapOps.java
// Things you can do with Maps
import java.util.concurrent.*;
import java.util.*;
import onjava.*;

public class MapOps {
  public static
  void printKeys(Map<Integer,String> map) {
    System.out.print("Size = " + map.size() + ", ");
    System.out.print("Keys: ");
    // Produce a Set of the keys:
    System.out.println(map.keySet());
  }
  public static
  void test(Map<Integer,String> map) {
    System.out.println(
      map.getClass().getSimpleName());
    map.putAll(new CountMap(25));
    // Map has 'Set' behavior for keys:
    map.putAll(new CountMap(25));
    printKeys(map);
    // Producing a Collection of the values:
    System.out.print("Values: ");
    System.out.println(map.values());
    System.out.println(map);
    System.out.println("map.containsKey(11): " +
      map.containsKey(11));
    System.out.println(
      "map.get(11): " + map.get(11));
    System.out.println("map.containsValue(\"F0\"): "
      + map.containsValue("F0"));
    Integer key = map.keySet().iterator().next();
    System.out.println("First key in map: " + key);
    map.remove(key);
    printKeys(map);
    map.clear();
    System.out.println(
      "map.isEmpty(): " + map.isEmpty());
    map.putAll(new CountMap(25));
    // Operations on the Set change the Map:
    map.keySet().removeAll(map.keySet());
    System.out.println(
      "map.isEmpty(): " + map.isEmpty());
  }
  public static void main(String[] args) {
    test(new HashMap<>());
    test(new TreeMap<>());
    test(new LinkedHashMap<>());
    test(new IdentityHashMap<>());
    test(new ConcurrentHashMap<>());
    test(new WeakHashMap<>());
  }
}
/* Output: (First 11 Lines)
HashMap
Size = 25, Keys: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24]
Values: [A0, B0, C0, D0, E0, F0, G0, H0, I0, J0, K0,
L0, M0, N0, O0, P0, Q0, R0, S0, T0, U0, V0, W0, X0, Y0]
{0=A0, 1=B0, 2=C0, 3=D0, 4=E0, 5=F0, 6=G0, 7=H0, 8=I0,
9=J0, 10=K0, 11=L0, 12=M0, 13=N0, 14=O0, 15=P0, 16=Q0,
17=R0, 18=S0, 19=T0, 20=U0, 21=V0, 22=W0, 23=X0, 24=Y0}
map.containsKey(11): true
map.get(11): L0
map.containsValue("F0"): true
First key in map: 0
Size = 24, Keys: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24]
map.isEmpty(): true
map.isEmpty(): true
                  ...
*/
```

`printKeys()` 方法演示了如何生成 **Map** 的 **Collection** 视图。 `keySet()` 方法生成一个由 **Map** 中的键组成的 **Set** 。 打印 `values()` 方法的结果会生成一个包含 **Map** 中所有值的 **Collection** 。（请注意，键必须是唯一的，但值可以包含重复项。）由于这些 **Collection** 由 **Map** 支持，因此 **Collection** 中的任何更改都会反映在所关联的 **Map** 中。

程序的其余部分提供了每个 **Map** 操作的简单示例，并测试了每种基本类型的 **Map** 。

<!-- SortedMap -->
### SortedMap

使用 **SortedMap** （由 **TreeMap** 或 **ConcurrentSkipListMap** 实现），键保证按排序顺序，这允许在 **SortedMap** 接口中使用这些方法来提供其他功能：

- `Comparator comparator()` ：生成用于此 **Map** 的比较器， **null** 表示自然排序。
- `T firstKey()` ：返回第一个键。
- `T lastKey()` ：返回最后一个键。
- `SortedMap subMap(fromKey，toKey)` ：生成此 **Map** 的视图，其中键从 **fromKey**（包括），到 **toKey** （不包括）。
- `SortedMap headMap(toKey)` ：使用小于 **toKey** 的键生成此 **Map** 的视图。
- `SortedMap tailMap(fromKey)` ：使用大于或等于 **fromKey** 的键生成此 **Map** 的视图。

这是一个类似于 **SortedSetDemo.java** 的示例，显示了 **TreeMap** 的这种额外行为：

```java
// collectiontopics/SortedMapDemo.java
// What you can do with a TreeMap
import java.util.*;
import onjava.*;

public class SortedMapDemo {
  public static void main(String[] args) {
    TreeMap<Integer,String> sortedMap =
      new TreeMap<>(new CountMap(10));
    System.out.println(sortedMap);
    Integer low = sortedMap.firstKey();
    Integer high = sortedMap.lastKey();
    System.out.println(low);
    System.out.println(high);
    Iterator<Integer> it =
      sortedMap.keySet().iterator();
    for(int i = 0; i <= 6; i++) {
      if(i == 3) low = it.next();
      if(i == 6) high = it.next();
      else it.next();
    }
    System.out.println(low);
    System.out.println(high);
    System.out.println(sortedMap.subMap(low, high));
    System.out.println(sortedMap.headMap(high));
    System.out.println(sortedMap.tailMap(low));
  }
}
/* Output:
{0=A0, 1=B0, 2=C0, 3=D0, 4=E0, 5=F0, 6=G0, 7=H0, 8=I0,
9=J0}
0
9
3
7
{3=D0, 4=E0, 5=F0, 6=G0}
{0=A0, 1=B0, 2=C0, 3=D0, 4=E0, 5=F0, 6=G0}
{3=D0, 4=E0, 5=F0, 6=G0, 7=H0, 8=I0, 9=J0}
*/
```

这里，键值对按照键的排序顺序进行排序。因为 **TreeMap** 中存在顺序感，所以“位置”的概念很有意义，因此可以拥有第一个、最后一个元素或子图。

<!-- LinkedHashMap -->
### LinkedHashMap

**LinkedHashMap** 针对速度进行哈希处理，但在遍历期间也会按插入顺序生成键值对（ `System.out.println()` 可以遍历它，因此可以看到遍历的结果）。 此外，可以在构造方法中配置 **LinkedHashMap** 以使用基于访问的 *最近最少使用*（LRU） 算法，因此未访问的元素（因此是删除的候选者）会出现在列表的前面。 这样可以轻松创建一个能够定期清理以节省空间的程序。下面是一个显示这两个功能的简单示例：

```java
// collectiontopics/LinkedHashMapDemo.java
// What you can do with a LinkedHashMap
import java.util.*;
import onjava.*;

public class LinkedHashMapDemo {
  public static void main(String[] args) {
    LinkedHashMap<Integer,String> linkedMap =
      new LinkedHashMap<>(new CountMap(9));
    System.out.println(linkedMap);
    // Least-recently-used order:
    linkedMap =
      new LinkedHashMap<>(16, 0.75f, true);
    linkedMap.putAll(new CountMap(9));
    System.out.println(linkedMap);
    for(int i = 0; i < 6; i++)
      linkedMap.get(i);
    System.out.println(linkedMap);
    linkedMap.get(0);
    System.out.println(linkedMap);
  }
}
/* Output:
{0=A0, 1=B0, 2=C0, 3=D0, 4=E0, 5=F0, 6=G0, 7=H0, 8=I0}
{0=A0, 1=B0, 2=C0, 3=D0, 4=E0, 5=F0, 6=G0, 7=H0, 8=I0}
{6=G0, 7=H0, 8=I0, 0=A0, 1=B0, 2=C0, 3=D0, 4=E0, 5=F0}
{6=G0, 7=H0, 8=I0, 1=B0, 2=C0, 3=D0, 4=E0, 5=F0, 0=A0}
*/
```

这些键值对确实是按照插入顺序进行遍历，即使对于LRU版本也是如此。 但是，在LRU版本中访问前六项（仅限）后，最后三项将移至列表的前面。然后，当再次访问“ **0** ”后，它移动到了列表的后面。

<!-- Utilities -->
## 集合工具类

集合有许多独立的实用工具程序，在 **java.util.Collections** 中表示为静态方法。之前已经见过其中一些，例如 `addAll()` ， `reverseOrder()` 和 `binarySearch()` 。以下是其他内容（同步和不可修改的实用工具程序将在后面的章节中介绍）。在此表中，在需要的时候使用了泛型：

| 方法 | 描述 |
| :--- | :--- |
| **checkedCollection(Collection\<T> c, Class\<T> type)** <br><br> **checkedList(List\<T> list, Class\<T> type)** <br><br> **checkedMap(Map\<K, V> m, Class\<K> keyType, Class\<V> valueType)** <br><br> **checkedSet(Set\<T> s, Class\<T> type)** <br><br> **checkedSortedMap(SortedMap\<K, V> m, Class\<K> keyType, Class\<V> valueType)** <br><br> **checkedSortedSet(SortedSet\<T> s, Class\<T> type)** | 生成 **Collection** 的动态类型安全视图或 **Collection** 的特定子类型。 当无法使用静态检查版本时使用这个版本。 <br><br> 这些方法的使用在[第九章 多态]()章节的“动态类型安全”标题下进行了展示。 |
| **max(Collection)** <br><br> **min(Collection)** | 使用 **Collection** 中对象的自然比较方法生成参数集合中的最大或最小元素。 |
| **max(Collection, Comparator)** <br><br> **min(Collection, Comparator)** | 使用 **Comparator** 指定的比较方法生成参数集合中的最大或最小元素。 |
| **indexOfSubList(List source, List target)** | 返回 **target** 在 **source** 内第一次出现的起始索引，如果不存在则返回 -1。 |
| **lastIndexOfSubList(List source, List target)** | 返回 **target** 在 **source** 内最后一次出现的起始索引，如果不存在则返回 -1。 |
| **replaceAll(List\<T> list, T oldVal, T newVal)** | 用 **newVal** 替换列表中所有的 **oldVal** 。 |
| **reverse(List)**| 反转列表 |
| **reverseOrder()** <br><br> **reverseOrder(Comparator\<T>)** | 返回一个 **Comparator** ，它与集合中实现了 **comparable\<T>** 接口的对象的自然顺序相反。第二个版本颠倒了所提供的 **Comparator** 的顺序。 |
| **rotate(List, int distance)** | 将所有元素向前移动 **distance** ，将尾部的元素移到开头。（译者注：即循环移动） |
| **shuffle(List)** <br><br> **shuffle(List, Random)**  | 随机置换指定列表（即打乱顺序）。第一个版本使用了默认的随机化源，或者也可以使用第二个版本，提供自己的随机化源。 |
| **sort(List\<T>)** <br><br> **sort(List\<T>, Comparator\<? super T> c)** | 第一个版本使用元素的自然顺序排序该 **List\<T>** 。第二个版本根据提供的 **Comparator** 排序。 |
| **copy(List\<? super T> dest, List\<? extends T> src)** | 将 **src** 中的元素复制到 **dest** 。 |
| **swap(List, int i, int j)** | 交换 **List** 中位置 **i** 和 位置 **j** 的元素。可能比你手工编写的速度快。 |
| **fill(List\<? super T>, T x)** | 用 **x** 替换 **List** 中的所有元素。|
| **nCopies(int n, T x)** | 返回大小为 **n** 的不可变 **List\<T>** ，其引用都指向 **x** 。 |
| **disjoint(Collection, Collection)** | 如果两个集合没有共同元素，则返回 **true** 。 |
| **frequency(Collection, Object x)** | 返回 **Collection** 中，等于 **x** 的元素个数。 |
| **emptyList()** <br><br> **emptyMap()** <br><br> **emptySet()** | 返回不可变的空 **List** ， **Map** 或 **Set** 。这些是泛型的，因此生成的 **Collection** 可以被参数化为所需的类型。 |
| **singleton(T x)** <br><br> **singletonList(T x)** <br><br> **singletonMap(K key, V value)** | 生成一个不可变的 **List** ， **Set** 或 **Map** ，其中只包含基于给定参数的单个元素。 |
| **list(Enumeration\<T> e)** | 生成一个 **ArrayList\<T>** ，其中元素为（旧式） **Enumeration** （ **Iterator** 的前身）中的元素。用于从遗留代码向新式转换。 |
| **enumeration(Collection\<T>)** | 为参数集合生成一个旧式的 **Enumeration\<T>** 。 |

请注意， `min（)` 和 `max()` 使用 **Collection** 对象，而不使用 **List** ，因此不必担心是否应对 **Collection** 进行排序。（如前所述，在执行 `binarySearch()` 之前，将会对 **List** 或数组进行`sort()` 排序。）

下面是一个示例，展示了上表中大多数实用工具程序的基本用法：

```java
// collectiontopics/Utilities.java
// Simple demonstrations of the Collections utilities
import java.util.*;

public class Utilities {
  static List<String> list = Arrays.asList(
    "one Two three Four five six one".split(" "));
  public static void main(String[] args) {
    System.out.println(list);
    System.out.println("'list' disjoint (Four)?: " +
      Collections.disjoint(list,
        Collections.singletonList("Four")));
    System.out.println(
      "max: " + Collections.max(list));
    System.out.println(
      "min: " + Collections.min(list));
    System.out.println(
      "max w/ comparator: " + Collections.max(list,
      String.CASE_INSENSITIVE_ORDER));
    System.out.println(
      "min w/ comparator: " + Collections.min(list,
      String.CASE_INSENSITIVE_ORDER));
    List<String> sublist =
      Arrays.asList("Four five six".split(" "));
    System.out.println("indexOfSubList: " +
      Collections.indexOfSubList(list, sublist));
    System.out.println("lastIndexOfSubList: " +
      Collections.lastIndexOfSubList(list, sublist));
    Collections.replaceAll(list, "one", "Yo");
    System.out.println("replaceAll: " + list);
    Collections.reverse(list);
    System.out.println("reverse: " + list);
    Collections.rotate(list, 3);
    System.out.println("rotate: " + list);
    List<String> source =
      Arrays.asList("in the matrix".split(" "));
    Collections.copy(list, source);
    System.out.println("copy: " + list);
    Collections.swap(list, 0, list.size() - 1);
    System.out.println("swap: " + list);
    Collections.shuffle(list, new Random(47));
    System.out.println("shuffled: " + list);
    Collections.fill(list, "pop");
    System.out.println("fill: " + list);
    System.out.println("frequency of 'pop': " +
      Collections.frequency(list, "pop"));
    List<String> dups =
      Collections.nCopies(3, "snap");
    System.out.println("dups: " + dups);
    System.out.println("'list' disjoint 'dups'?: " +
      Collections.disjoint(list, dups));
    // Getting an old-style Enumeration:
    Enumeration<String> e =
      Collections.enumeration(dups);
    Vector<String> v = new Vector<>();
    while(e.hasMoreElements())
      v.addElement(e.nextElement());
    // Converting an old-style Vector
    // to a List via an Enumeration:
    ArrayList<String> arrayList =
      Collections.list(v.elements());
    System.out.println("arrayList: " + arrayList);
  }
}
/* Output:
[one, Two, three, Four, five, six, one]
'list' disjoint (Four)?: false
max: three
min: Four
max w/ comparator: Two
min w/ comparator: five
indexOfSubList: 3
lastIndexOfSubList: 3
replaceAll: [Yo, Two, three, Four, five, six, Yo]
reverse: [Yo, six, five, Four, three, Two, Yo]
rotate: [three, Two, Yo, Yo, six, five, Four]
copy: [in, the, matrix, Yo, six, five, Four]
swap: [Four, the, matrix, Yo, six, five, in]
shuffled: [six, matrix, the, Four, Yo, five, in]
fill: [pop, pop, pop, pop, pop, pop, pop]
frequency of 'pop': 7
dups: [snap, snap, snap]
'list' disjoint 'dups'?: true
arrayList: [snap, snap, snap]
*/
```

输出解释了每种实用方法的行为。请注意由于大小写的缘故，普通版本的 `min()` 和 `max()` 与带有 **String.CASE_INSENSITIVE_ORDER** 比较器参数的版本的区别。

<!-- Sorting and Searching Lists -->
### 排序和搜索列表

用于执行排序和搜索 **List** 的实用工具程序与用于排序对象数组的程序具有相同的名字和方法签名，只不过是 **Collections** 的静态方法而不是 **Arrays** 。 这是一个使用 **Utilities.java** 中的 **list** 数据的示例：

```java
// collectiontopics/ListSortSearch.java
// Sorting/searching Lists with Collections utilities
import java.util.*;

public class ListSortSearch {
  public static void main(String[] args) {
    List<String> list =
      new ArrayList<>(Utilities.list);
    list.addAll(Utilities.list);
    System.out.println(list);
    Collections.shuffle(list, new Random(47));
    System.out.println("Shuffled: " + list);
    // Use ListIterator to trim off last elements:
    ListIterator<String> it = list.listIterator(10);
    while(it.hasNext()) {
      it.next();
      it.remove();
    }
    System.out.println("Trimmed: " + list);
    Collections.sort(list);
    System.out.println("Sorted: " + list);
    String key = list.get(7);
    int index = Collections.binarySearch(list, key);
    System.out.println(
      "Location of " + key + " is " + index +
      ", list.get(" + index + ") = " +
      list.get(index));
    Collections.sort(list,
      String.CASE_INSENSITIVE_ORDER);
    System.out.println(
      "Case-insensitive sorted: " + list);
    key = list.get(7);
    index = Collections.binarySearch(list, key,
      String.CASE_INSENSITIVE_ORDER);
    System.out.println(
      "Location of " + key + " is " + index +
      ", list.get(" + index + ") = " +
      list.get(index));
  }
}
/* Output:
[one, Two, three, Four, five, six, one, one, Two,
three, Four, five, six, one]
Shuffled: [Four, five, one, one, Two, six, six, three,
three, five, Four, Two, one, one]
Trimmed: [Four, five, one, one, Two, six, six, three,
three, five]
Sorted: [Four, Two, five, five, one, one, six, six,
three, three]
Location of six is 7, list.get(7) = six
Case-insensitive sorted: [five, five, Four, one, one,
six, six, three, three, Two]
Location of three is 7, list.get(7) = three
*/
```

就像使用数组进行搜索和排序一样，如果使用 **Comparator** 进行排序，则必须使用相同的 **Comparator** 执行 `binarySearch()` 。

该程序还演示了 **Collections** 中的 `shuffle()` 方法，该方法随机打乱了 **List** 的顺序。 **ListIterator** 是在打乱后的列表中的特定位置创建的，用于从该位置删除元素，直到列表末尾。

<!-- Making a Collection or Map Unmodifiable -->
### 创建不可修改的 Collection 或 Map

通常，创建 **Collection** 或 **Map** 的只读版本会很方便。 **Collections** 类通过将原始集合传递给一个方法然后返回一个只读版本的集合。 对于 **Collection** （如果不能将 **Collection** 视为更具体的类型）， **List** ， **Set** 和 **Map** ，这类方法有许多变体。这个示例展示了针对每种类型，正确构建只读版本集合的方法：

```java
// collectiontopics/ReadOnly.java
// Using the Collections.unmodifiable methods
import java.util.*;
import onjava.*;

public class ReadOnly {
  static Collection<String> data =
    new ArrayList<>(Countries.names(6));
  public static void main(String[] args) {
    Collection<String> c =
      Collections.unmodifiableCollection(
        new ArrayList<>(data));
    System.out.println(c); // Reading is OK
    //- c.add("one"); // Can't change it

    List<String> a = Collections.unmodifiableList(
        new ArrayList<>(data));
    ListIterator<String> lit = a.listIterator();
    System.out.println(lit.next()); // Reading is OK
    //- lit.add("one"); // Can't change it

    Set<String> s = Collections.unmodifiableSet(
      new HashSet<>(data));
    System.out.println(s); // Reading is OK
    //- s.add("one"); // Can't change it

    // For a SortedSet:
    Set<String> ss =
      Collections.unmodifiableSortedSet(
        new TreeSet<>(data));

    Map<String,String> m =
      Collections.unmodifiableMap(
        new HashMap<>(Countries.capitals(6)));
    System.out.println(m); // Reading is OK
    //- m.put("Ralph", "Howdy!");

    // For a SortedMap:
    Map<String,String> sm =
      Collections.unmodifiableSortedMap(
        new TreeMap<>(Countries.capitals(6)));
  }
}
/* Output:
[ALGERIA, ANGOLA, BENIN, BOTSWANA, BURKINA FASO,
BURUNDI]
ALGERIA
[BENIN, BOTSWANA, ANGOLA, BURKINA FASO, ALGERIA,
BURUNDI]
{BENIN=Porto-Novo, BOTSWANA=Gaberone, ANGOLA=Luanda,
BURKINA FASO=Ouagadougou, ALGERIA=Algiers,
BURUNDI=Bujumbura}
*/
```

为特定类型调用 “unmodifiable” 方法不会导致编译时检查，但是一旦发生转换，对修改特定集合内容的任何方法调用都将产生 **UnsupportedOperationException** 异常。

在每种情况下，在将集合设置为只读之前，必须使用有意义的数据填充集合。填充完成后，最好的方法是用 “unmodifiable” 方法调用生成的引用替换现有引用。这样，一旦使得内容无法修改，那么就不会冒有意外更改内容的风险。另一方面，此工具还允许将可修改的集合保留为类中的**私有**集合，并从方法调用处返回对该集合的只读引用。所以，你可以在类内修改它，但其他人只能读它。

<!-- Synchronizing a Collection or Map -->
### 同步 Collection 或 Map

**synchronized** 关键字是多线程主题的重要组成部分，更复杂的内容在[第二十四章 并发编程]()中介绍。在这里，只需要注意到 **Collections** 类包含一种自动同步整个集合的方法。 语法类似于 “unmodifiable” 方法：

```java
// collectiontopics/Synchronization.java
// Using the Collections.synchronized methods
import java.util.*;

public class Synchronization {
  public static void main(String[] args) {
    Collection<String> c =
      Collections.synchronizedCollection(
        new ArrayList<>());
    List<String> list = Collections
      .synchronizedList(new ArrayList<>());
    Set<String> s = Collections
      .synchronizedSet(new HashSet<>());
    Set<String> ss = Collections
      .synchronizedSortedSet(new TreeSet<>());
    Map<String,String> m = Collections
      .synchronizedMap(new HashMap<>());
    Map<String,String> sm = Collections
      .synchronizedSortedMap(new TreeMap<>());
  }
}
```

最好立即通过适当的 “synchronized” 方法传递新集合，如上所示。这样，就不会意外地暴露出非同步版本。

<!-- Fail Fast -->
#### Fail Fast

Java 集合还具有防止多个进程修改集合内容的机制。如果当前正在迭代集合，然后有其他一些进程介入并插入，删除或更改该集合中的对象，则会出现此问题。也许在集合中已经遍历过了那个元素，也许还没有遍历到，也许在调用 `size()` 之后集合的大小会缩小...有许多灾难情景。 Java 集合库使用一种 *fail-fast* 的机制，该机制可以检测到除了当前进程引起的更改之外，其它任何对集合的更改操作。如果它检测到其他人正在修改集合，则会立即生成 **ConcurrentModificationException** 异常。这就是“fail-fast”的含义——它不会在以后使用更复杂的算法尝试检测问题（快速失败）。

通过创建迭代器并向迭代器指向的集合中添加元素，可以很容易地看到操作中的 fail-fast 机制，如下所示：

```java
// collectiontopics/FailFast.java
// Demonstrates the "fail-fast" behavior
import java.util.*;

public class FailFast {
  public static void main(String[] args) {
    Collection<String> c = new ArrayList<>();
    Iterator<String> it = c.iterator();
    c.add("An object");
    try {
      String s = it.next();
    } catch(ConcurrentModificationException e) {
      System.out.println(e);
    }
  }
}
/* Output:
java.util.ConcurrentModificationException
*/
```

异常来自于在从集合中获得迭代器之后，又尝试在集合中添加元素。程序的两个部分可能会修改同一个集合，这种可能性的存在会产生不确定状态，因此异常会通知你更改代码。在这种情况下，应先将所有元素添加到集合，然后再获取迭代器。

**ConcurrentHashMap** ， **CopyOnWriteArrayList** 和 **CopyOnWriteArraySet** 使用了特定的技术来避免产生 **ConcurrentModificationException** 异常。

<!-- Holding References -->
## 持有引用

**java.lang.ref** 中库包含一组类，这些类允许垃圾收集具有更大的灵活性。特别是当拥有可能导致内存耗尽的大对象时，这些类特别有用。这里有三个从抽象类 **Reference** 继承来的类： **SoftReference** （软引用）， **WeakReference** （弱引用）和 **PhantomReference** （虚引用）继承了三个类。如果一个对象只能通过这其中的一个 **Reference** 对象访问，那么这三种类型每个都为垃圾收集器提供不同级别的间接引用（indirection）。

如果一个对象是 *可达的*（reachable），那么意味着在程序中的某个位置可以找到该对象。这可能意味着在栈上有一个直接引用该对象的普通引用，但也有可能是引用了一个对该对象有引用的对象，这可以有很多中间环节。如果某个对象是可达的，则垃圾收集器无法释放它，因为它仍然被程序所使用。如果某个对象是不可达的，则程序无法使用它，那么垃圾收集器回收该对象就是安全的。

使用 **Reference** 对象继续保持对该对象的引用，以到达该对象，但也允许垃圾收集器释放该对象。因此，程序可以使用该对象，但如果内存即将耗尽，则允许释放该对象。

可以通过使用 **Reference** 对象作为你和普通引用之间的中介（代理）来实现此目的。此外，必须没有对象的普通引用（未包含在 **Reference** 对象中的对象）。如果垃圾收集器发现对象可通过普通引用访问，则它不会释放该对象。

按照 **SoftReference** ， **WeakReference** 和 **PhantomReference** 的顺序，每个都比前一个更“弱”，并且对应于不同的可达性级别。软引用用于实现对内存敏感的缓存。弱引用用于实现“规范化映射”（ canonicalized mappings）——对象的实例可以在程序的多个位置同时使用，以节省存储，但不会阻止其键（或值）被回收。虚引用用于调度 pre-mortem 清理操作，这是一种比 Java 终结机制（Java finalization mechanism）更灵活的方式。

使用 **SoftReference** 和 **WeakReference** ，可以选择是否将它们放在 **ReferenceQueue** （用于 pre-mortem 清理操作的设备）中，但 **PhantomReference** 只能在 **ReferenceQueue** 上构建。下面是一个简单的演示：

```java
// collectiontopics/References.java
// Demonstrates Reference objects
import java.lang.ref.*;
import java.util.*;

class VeryBig {
  private static final int SIZE = 10000;
  private long[] la = new long[SIZE];
  private String ident;
  VeryBig(String id) { ident = id; }
  @Override
  public String toString() { return ident; }
  @Override
  protected void finalize() {
    System.out.println("Finalizing " + ident);
  }
}

public class References {
  private static ReferenceQueue<VeryBig> rq =
    new ReferenceQueue<>();
  public static void checkQueue() {
    Reference<? extends VeryBig> inq = rq.poll();
    if(inq != null)
      System.out.println("In queue: " + inq.get());
  }
  public static void main(String[] args) {
    int size = 10;
    // Or, choose size via the command line:
    if(args.length > 0)
      size = Integer.valueOf(args[0]);
    LinkedList<SoftReference<VeryBig>> sa =
      new LinkedList<>();
    for(int i = 0; i < size; i++) {
      sa.add(new SoftReference<>(
        new VeryBig("Soft " + i), rq));
      System.out.println(
        "Just created: " + sa.getLast());
      checkQueue();
    }
    LinkedList<WeakReference<VeryBig>> wa =
      new LinkedList<>();
    for(int i = 0; i < size; i++) {
      wa.add(new WeakReference<>(
        new VeryBig("Weak " + i), rq));
      System.out.println(
        "Just created: " + wa.getLast());
      checkQueue();
    }
    SoftReference<VeryBig> s =
      new SoftReference<>(new VeryBig("Soft"));
    WeakReference<VeryBig> w =
      new WeakReference<>(new VeryBig("Weak"));
    System.gc();
    LinkedList<PhantomReference<VeryBig>> pa =
      new LinkedList<>();
    for(int i = 0; i < size; i++) {
      pa.add(new PhantomReference<>(
        new VeryBig("Phantom " + i), rq));
      System.out.println(
        "Just created: " + pa.getLast());
      checkQueue();
    }
  }
}
/* Output: (First and Last 10 Lines)
Just created: java.lang.ref.SoftReference@15db9742
Just created: java.lang.ref.SoftReference@6d06d69c
Just created: java.lang.ref.SoftReference@7852e922
Just created: java.lang.ref.SoftReference@4e25154f
Just created: java.lang.ref.SoftReference@70dea4e
Just created: java.lang.ref.SoftReference@5c647e05
Just created: java.lang.ref.SoftReference@33909752
Just created: java.lang.ref.SoftReference@55f96302
Just created: java.lang.ref.SoftReference@3d4eac69
Just created: java.lang.ref.SoftReference@42a57993
...________...________...________...________...
Just created: java.lang.ref.PhantomReference@45ee12a7
In queue: null
Just created: java.lang.ref.PhantomReference@330bedb4
In queue: null
Just created: java.lang.ref.PhantomReference@2503dbd3
In queue: null
Just created: java.lang.ref.PhantomReference@4b67cf4d
In queue: null
Just created: java.lang.ref.PhantomReference@7ea987ac
In queue: null
*/
```

当运行此程序（将输出重定向到文本文件以查看页面中的输出）时，将会看到对象是被垃圾收集了的，虽然仍然可以通过 **Reference** 对象访问它们（使用 `get()` 来获取实际的对象引用）。 还可以看到 **ReferenceQueue** 始终生成包含 **null** 对象的 **Reference** 。 要使用它，请从特定的 **Reference** 类继承，并为新类添加更多有用的方法。


<!-- The WeakHashMap -->
### WeakHashMap

集合类库中有一个特殊的 **Map** 来保存弱引用： **WeakHashMap** 。 此类可以更轻松地创建规范化映射。在这种映射中，可以通过仅仅创建一个特定值的实例来节省存储空间。当程序需要该值时，它会查找映射中的现有对象并使用它（而不是从头开始创建一个）。 该映射可以将值作为其初始化的一部分，但更有可能的是在需要时创建该值。

由于这是一种节省存储空间的技术，因此 **WeakHashMap** 允许垃圾收集器自动清理键和值，这是非常方便的。不能对放在 **WeakHashMap** 中的键和值做任何特殊操作，它们由 map 自动包装在 **WeakReference** 中。当键不再被使用的时候才允许清理，如下所示：

```java
// collectiontopics/CanonicalMapping.java
// Demonstrates WeakHashMap
import java.util.*;

class Element {
  private String ident;
  Element(String id) { ident = id; }
  @Override
  public String toString() { return ident; }
  @Override
  public int hashCode() {
    return Objects.hashCode(ident);
  }
  @Override
  public boolean equals(Object r) {
    return r instanceof Element &&
      Objects.equals(ident, ((Element)r).ident);
  }
  @Override
  protected void finalize() {
    System.out.println("Finalizing " +
      getClass().getSimpleName() + " " + ident);
  }
}

class Key extends Element {
  Key(String id) { super(id); }
}

class Value extends Element {
  Value(String id) { super(id); }
}

public class CanonicalMapping {
  public static void main(String[] args) {
    int size = 1000;
    // Or, choose size via the command line:
    if(args.length > 0)
      size = Integer.valueOf(args[0]);
    Key[] keys = new Key[size];
    WeakHashMap<Key,Value> map =
      new WeakHashMap<>();
    for(int i = 0; i < size; i++) {
      Key k = new Key(Integer.toString(i));
      Value v = new Value(Integer.toString(i));
      if(i % 3 == 0)
        keys[i] = k; // Save as "real" references
      map.put(k, v);
    }
    System.gc();
  }
}
```

**Key** 类必须具有 `hashCode()` 和 `equals()` ，因为它将被用作散列数据结构中的键。 `hashCode()` 的内容在[附录：理解hashCode和equals方法]()中进行了描述。

运行程序，你会看到垃圾收集器每三个键跳过一次。对该键的普通引用也被放置在 **keys** 数组中，因此这些对象不能被垃圾收集。

<!-- Java 1.0/1.1 Collections -->
## Java 1.0 / 1.1 的集合类

不幸的是，许多代码是使用 Java 1.0 / 1.1 中的集合编写的，甚至新代码有时也是使用这些类编写的。编写新代码时切勿使用旧集合。旧的集合类有限，所以关于它们的讨论不多。由于它们是不合时宜的，所以我会尽量避免过分强调一些可怕的设计决定。

<!-- Vector & Enumeration -->
### Vector 和 Enumeration

Java 1.0 / 1.1 中唯一的自扩展序列是 **Vector** ，因此它被用于很多地方。它的缺陷太多了，无法在这里描述（参见《Java编程思想》第1版，可从[www.OnJava8.com](www.OnJava8.com)免费下载）。基本上，你可以将它看作是具有冗长且笨拙的方法名称的 **ArrayList** 。在修订后的 Java 集合库中，**Vector** 已经被调整适配过，因此可以作为 **Collection** 和 **List** 来使用。事实证明这有点不正常，集合类库仍然包含它只是为了支持旧的 Java 代码，但这会让一些人误以为 **Vector** 已经变得更好了。

迭代器的 Java 1.0 / 1.1 版本选择创建一个新名称“enumeration”，而不是使用每个人都熟悉的术语（“iterator”）。 **Enumeration** 接口小于 **Iterator** ，只包含两个方法，并且它使用更长的方法名称：如果还有更多元素，则 `boolean hasMoreElements()` 返回 `true` ， `Object nextElement()` 返回此enumeration的下一个元素 （否则会抛出异常）。

**Enumeration** 只是一个接口，而不是一个实现，甚至新的类库有时仍然使用旧的 **Enumeration** ，这是不幸的，但通常是无害的。应该总是在自己的代码中使用 **Iterator** ，但要做好准备应对那些提供 **Enumeration** 的类库。

此外，可以使用 `Collections.enumeration()` 方法为任何 **Collection** 生成 **Enumeration** ，如下例所示：

```java
// collectiontopics/Enumerations.java
// Java 1.0/1.1 Vector and Enumeration
import java.util.*;
import onjava.*;

public class Enumerations {
  public static void main(String[] args) {
    Vector<String> v =
      new Vector<>(Countries.names(10));
    Enumeration<String> e = v.elements();
    while(e.hasMoreElements())
      System.out.print(e.nextElement() + ", ");
    // Produce an Enumeration from a Collection:
    e = Collections.enumeration(new ArrayList<>());
  }
}
/* Output:
ALGERIA, ANGOLA, BENIN, BOTSWANA, BURKINA FASO,
BURUNDI, CAMEROON, CAPE VERDE, CENTRAL AFRICAN
REPUBLIC, CHAD,
*/
```

要生成 **Enumeration** ，可以调用 `elements()` ，然后可以使用它来执行向前迭代。

最后一行创建一个 **ArrayList** ，并使用 `enumeration() ` 来将 **ArrayList** 适配为一个 **Enumeration** 。 因此，如果有旧代码需要使用 **Enumeration** ，你仍然可以使用新集合。

<!-- Hashtable -->
### Hashtable

正如你在本附录中的性能比较中所看到的，基本的 **Hashtable** 与 **HashMap** 非常相似，甚至方法名称都相似。在新代码中没有理由使用 **Hashtable** 而不是 **HashMap** 。

<!-- Stack -->
### Stack

之前使用 **LinkedList** 引入了栈的概念。 Java 1.0 / 1.1 **Stack** 的奇怪之处在于，不是以组合方式使用 **Vector** ，而是继承自 **Vector** 。 因此它具有 **Vector** 的所有特征和行为以及一些额外的 **Stack** 行为。很难去知道设计师是否有意识地认为这样做是有用的，或者它是否只是太天真了，无论如何，它在进入发行版之前显然没有经过审查，所以这个糟糕的设计仍然存在（但不要使用它）。

这是 **Stack** 的简单演示，向栈中放入枚举中每一个类型的 **String** 形式。它还展示了如何轻松地将 **LinkedList** 用作栈，或者使用在[第十二章：集合]()章节中创建的 **Stack** 类：

```java
// collectiontopics/Stacks.java
// Demonstration of Stack Class
import java.util.*;

enum Month { JANUARY, FEBRUARY, MARCH, APRIL,
  MAY, JUNE, JULY, AUGUST, SEPTEMBER,
  OCTOBER, NOVEMBER }

public class Stacks {
  public static void main(String[] args) {
    Stack<String> stack = new Stack<>();
    for(Month m : Month.values())
      stack.push(m.toString());
    System.out.println("stack = " + stack);
    // Treating a stack as a Vector:
    stack.addElement("The last line");
    System.out.println(
      "element 5 = " + stack.elementAt(5));
    System.out.println("popping elements:");
    while(!stack.empty())
      System.out.print(stack.pop() + " ");

    // Using a LinkedList as a Stack:
    LinkedList<String> lstack = new LinkedList<>();
    for(Month m : Month.values())
      lstack.addFirst(m.toString());
    System.out.println("lstack = " + lstack);
    while(!lstack.isEmpty())
      System.out.print(lstack.removeFirst() + " ");

    // Using the Stack class from
    // the Collections Chapter:
    onjava.Stack<String> stack2 =
      new onjava.Stack<>();
    for(Month m : Month.values())
      stack2.push(m.toString());
    System.out.println("stack2 = " + stack2);
    while(!stack2.isEmpty())
      System.out.print(stack2.pop() + " ");

  }
}
/* Output:
stack = [JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER]
element 5 = JUNE
popping elements:
The last line NOVEMBER OCTOBER SEPTEMBER AUGUST JULY
JUNE MAY APRIL MARCH FEBRUARY JANUARY lstack =
[NOVEMBER, OCTOBER, SEPTEMBER, AUGUST, JULY, JUNE, MAY,
APRIL, MARCH, FEBRUARY, JANUARY]
NOVEMBER OCTOBER SEPTEMBER AUGUST JULY JUNE MAY APRIL
MARCH FEBRUARY JANUARY stack2 = [NOVEMBER, OCTOBER,
SEPTEMBER, AUGUST, JULY, JUNE, MAY, APRIL, MARCH,
FEBRUARY, JANUARY]
NOVEMBER OCTOBER SEPTEMBER AUGUST JULY JUNE MAY APRIL
MARCH FEBRUARY JANUARY
*/
```

**String** 形式是由 **Month** 中的枚举常量生成的，使用 `push()` 压入到栈中，然后使用 `pop()` 从栈顶部取出。为了说明一点，将 **Vector** 的操作也在 **Stack** 对象上执行， 这是可能的，因为凭借继承， **Stack** 是 **Vector** 。 因此，可以在 **Vector** 上执行的所有操作也可以在 **Stack** 上执行，例如 `elementAt()` 。

如前所述，在需要栈行为时使用 **LinkedList** ，或者从 **LinkedList** 类创建的 **onjava.Stack** 类。

<!-- BitSet -->
### BitSet

**BitSet** 用于有效地存储大量的开关信息。仅从尺寸大小的角度来看它是有效的，如果你正在寻找有效的访问，它比使用本机数组（native array）稍慢。

此外， **BitSet** 的最小大小是 **long** ：64位。这意味着如果你要存储更小的东西，比如8位， **BitSet** 就是浪费，如果尺寸有问题，你最好创建自己的类，或者只是用一个数组来保存你的标志。（只有在你创建许多包含开关信息列表的对象时才会出现这种情况，并且只应根据分析和其他指标来决定。如果你做出此决定只是因为您认为 **BitSet** 太大，那么最终会产生不必要的复杂性并且浪费大量时间。）

当添加更多元素时，普通集合会扩展， **BitSet**也会这样做。以下示例显示了 **BitSet** 的工作原理：

```java
// collectiontopics/Bits.java
// Demonstration of BitSet
import java.util.*;

public class Bits {
  public static void printBitSet(BitSet b) {
    System.out.println("bits: " + b);
    StringBuilder bbits = new StringBuilder();
    for(int j = 0; j < b.size() ; j++)
      bbits.append(b.get(j) ? "1" : "0");
    System.out.println("bit pattern: " + bbits);
  }
  public static void main(String[] args) {
    Random rand = new Random(47);
    // Take the LSB of nextInt():
    byte bt = (byte)rand.nextInt();
    BitSet bb = new BitSet();
    for(int i = 7; i >= 0; i--)
      if(((1 << i) &  bt) != 0)
        bb.set(i);
      else
        bb.clear(i);
    System.out.println("byte value: " + bt);
    printBitSet(bb);

    short st = (short)rand.nextInt();
    BitSet bs = new BitSet();
    for(int i = 15; i >= 0; i--)
      if(((1 << i) &  st) != 0)
        bs.set(i);
      else
        bs.clear(i);
    System.out.println("short value: " + st);
    printBitSet(bs);

    int it = rand.nextInt();
    BitSet bi = new BitSet();
    for(int i = 31; i >= 0; i--)
      if(((1 << i) &  it) != 0)
        bi.set(i);
      else
        bi.clear(i);
    System.out.println("int value: " + it);
    printBitSet(bi);

    // Test bitsets >= 64 bits:
    BitSet b127 = new BitSet();
    b127.set(127);
    System.out.println("set bit 127: " + b127);
    BitSet b255 = new BitSet(65);
    b255.set(255);
    System.out.println("set bit 255: " + b255);
    BitSet b1023 = new BitSet(512);
    b1023.set(1023);
    b1023.set(1024);
    System.out.println("set bit 1023: " + b1023);
  }
}
/* Output:
byte value: -107
bits: {0, 2, 4, 7}
bit pattern: 101010010000000000000000000000000000000000
0000000000000000000000
short value: 1302
bits: {1, 2, 4, 8, 10}
bit pattern: 011010001010000000000000000000000000000000
0000000000000000000000
int value: -2014573909
bits: {0, 1, 3, 5, 7, 9, 11, 18, 19, 21, 22, 23, 24,
25, 26, 31}
bit pattern: 110101010101000000110111111000010000000000
0000000000000000000000
set bit 127: {127}
set bit 255: {255}
set bit 1023: {1023, 1024}
*/
```

随机数生成器用于创建随机 **byte** ， **short** 和 **int** ，并且每个都在 **BitSet** 中转换为相应的位模式。这样可以正常工作，因为 **BitSet** 是64位，所以这些都不会导致它的大小增加，然后创建更大的 **BitSet** 。 请注意， **BitSet** 会根据需要进行扩展。

对于可以命名的固定标志集， **EnumSet** （参见[第二十二章：枚举]()章节）通常比 **BitSet** 更好，因为 **EnumSet** 允许操作名称而不是数字位位置，从而可以减少错误。 **EnumSet** 还可以防止意外地添加新的标记位置，这可能会导致一些严重的，难以发现的错误。使用 **BitSet** 而不是 **EnumSet** 的唯一原因是，不知道在运行时需要多少标志，或者为标志分配名称是不合理的，或者需要 **BitSet** 中的一个特殊操作（请参阅 **BitSet** 和 **EnumSet** 的 JDK 文档）。

<!-- Summary -->
## 本章小结

集合可以说是编程语言中最常用的工具。有些语言（例如Python）甚至将基本集合组件（列表，映射和集合）作为内置函数包含在其中。

正如在[第十二章：集合]()章节中看到的那样，可以使用集合执行许多非常有用的操作，而不需要太多努力。但是，在某些时候，为了正确地使用它们而不得不更多地了解集合，特别是，必须充分了解散列操作以编写自己的 `hashCode()` 方法（并且必须知道何时需要），并且你必须充分了解各种集合实现，以根据你的需求选择合适的集合。本附录涵盖了这些概念，并讨论了有关集合库的其他有用详细信息。你现在应该已经准备好在日常编程任务中使用 Java 集合了。

集合库的设计很困难（大多数库设计问题都是如此）。在 C++ 中，集合类涵盖了许多不同类的基础。这比之前可用的 C++ 集合类更好，但它没有很好地转换为 Java 。在另一个极端，我看到了一个由单个类“collection”组成的集合库，它同时充当线性序列和关联数组。 Java 集合库试图在功能和复杂性之间取得平衡。结果在某些地方看起来有点奇怪。与早期 Java 库中的一些决策不同，这些奇怪的不是事故，而是在基于复杂性的权衡下而仔细考虑的决策。


[^1]: **java.util** 中的 **Map** 使用 **Map** 的 `getKey()` 和 `getValue()` 执行批量复制，因此这是有效的。如果自定义 **Map** 只是复制整个 **Map.Entry** ，那么这种方法就会出现问题。

[^2]: 虽然当我用这种方式描述它的时候听起来很奇怪而且好像没什么用处，但在[第十九章 类型信息]()章节中已经看到过，这种动态行为也可以非常强大有用。

[^3]: 如果这些加速仍然无法满足性能需求，则可以通过编写自己的 **Map** 并将其自定义为特定类型来进一步加速表查找，以避免因向 **对象** 转换而导致的延迟。为了达到更高的性能水平，速度爱好者可以使用 Donald Knuth 的《计算机程序设计艺术（第3卷）：排序与查找》（第二版），将溢出桶列表（overflow bucket lists）替换为具有两个额外优势的阵列：它们可以针对磁盘存储进行优化，并且它们可以节省大部分创建和回收个别记录（individual records）的时间。

<!-- 分页 -->

<div style="page-break-after: always;"></div>