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

**MAP** 是使用Streams（[第十四章 流式编程]()）创建的。 二维数组 **ARRAY** 作为流传输到 **Map** 中，但请注意我们不仅仅是使用简单版本的 **Collectors.toMap()** 。 那个版本生成一个 **HashMap** ，它使用散列函数来控制对键的排序。 为了保留原来的顺序，我们必须将键值对直接放入 **TreeMap** 中，这意味着我们需要使用更复杂的 **Collectors.toMap()** 版本。这需要两个函数从每个流元素中提取键和值，就像简单版本的**Collectors.toMap()** 一样。 然后它需要一个*合并函数*（merge function），它解决了与同一个键相关的两个值之间的冲突。这里的数据已经预先审查过，因此绝不会发生这种情况，如果有的话，这里会抛出异常。最后，传递生成所需类型的空map的函数，然后用流来填充它。

**rgb()** 方法是一个便捷函数（convenience function），它接受颜色名称 **String** 参数并生成其数字RGB值。为此，我们需要一个反转版本的 **COLORS** ，它接受一个 **String**键并查找RGB的 **Integer** 值。 这是通过 **invert()** 方法实现的，如果任何 **COLORS** 值不唯一，则抛出异常。

我们还创建包含所有名称的 **LIST** ，以及包含十六进制表示法的RGB值的 **RGBLIST** 。

第一个 **show()** 方法接受一个 **Map.Entry** 并显示以十六进制表示的键，以便轻松地对原始 **ARRAY** 进行双重检查。 名称以 **show** 开头的每个方法都会重载两个版本，其中一个版本采用 **count** 参数来指示要显示的元素数量，第二个版本显示序列中的所有元素。

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
## List表现


<!-- Set Behavior -->
## Set表现


<!-- Using Functional Operations with any Map -->
## 在Map中使用函数式操作


<!-- Selecting Parts of a Map -->
## 选择Map的部分


<!-- Filling Collections -->
## 集合的fill方法


<!-- Custom Collection and Map using Flyweight -->
## 使用Flyweight自定义集合和Map


<!-- Collection Functionality -->
## 集合功能


<!-- Optional Operations -->
## 可选操作


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







<!-- 分页 -->

<div style="page-break-after: always;"></div>