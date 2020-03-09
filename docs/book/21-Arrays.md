[TOC]

<!-- Arrays -->

# 第二十一章 数组


> 在 [初始化和清理](book/06-Housekeeping.md) 一章的最后，你已经学过如何定义和初始化一个数组。

简单来看，数组需要你去创建和初始化，你可以通过下标对数组元素进行访问，数组的大小不会改变。大多数时候你只需要知道这些，但有时候你必须在数组上进行更复杂的操作，你也可能需要在数组和更加灵活的 **集合** (Collection)之间做出评估。因此本章我们将对数组进行更加深入的分析。

**注意：** 随着 Java Collection 和 Stream 类中高级功能的不断增加，日常编程中使用数组的需求也在变少，所以你暂且可以放心地略读甚至跳过这一章。但是，即使你自己避免使用数组，也总会有需要阅读别人数组代码的那一天。那时候，本章依然在这里等着你来翻阅。

<!-- Why Arrays are Special -->

## 数组特性

明明还有很多其他的办法来保存对象，那么是什么令数组如此特别？

将数组和其他类型的集合区分开来的原因有三：效率，类型，保存基本数据类型的能力。在 Java 中，使用数组存储和随机访问对象引用序列是非常高效的。数组是简单的线性序列，这使得对元素的访问变得非常快。然而这种高速也是有代价的，代价就是数组对象的大小是固定的，且在该数组的生存期内不能更改。

速度通常并不是问题，如果有问题，你保存和检索对象的方式也很少是罪魁祸首。你应该总是从 **ArrayList** (来自 [集合](book/12-Collections.md ))开始，它将数组封装起来。必要时，它会自动分配更多的数组空间，创建新数组，并将旧数组中的引用移动到新数组。这种灵活性需要开销，所以一个 **ArrayList** 的效率不如数组。在极少的情况下效率会成为问题，所以这种时候你可以直接使用数组。


数组和集合(Collections)都不能滥用。不管你使用数组还是集合，如果你越界，你都会得到一个 **RuntimeException** 的异常提醒，这表明你的程序中存在错误。


在泛型前，其他的集合类以一种宽泛的方式处理对象（就好像它们没有特定类型一样）。事实上，这些集合类把保存对象的类型默认为 **Object**，也就是 Java 中所有类的基类。而数组是优于 **预泛型** (pre-generic)集合类的，因为你创建一个数组就可以保存特定类型的数据。这意味着你获得了一个编译时的类型检查，而这可以防止你插入错误的数据类型，或者搞错你正在提取的数据类型。


当然，不管在编译时还是运行时，Java都会阻止你犯向对象发送不正确消息的错误。然而不管怎样，使用数组都不会有更大的风险。比较好的地方在于，如果编译器报错，最终的用户更容易理解抛出异常的含义。


一个数组可以保存基本数据类型，而一个预泛型的集合不可以。然而对于泛型而言，集合可以指定和检查他们保存对象的类型，而通过 **自动装箱** (autoboxing)机制，集合表现地就像它们可以保存基本数据类型一样，因为这种转换是自动的。

下面给出一例用于比较数组和泛型集合：

```java
// arrays/CollectionComparison.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import onjava.*;
import static onjava.ArrayShow.*;

class BerylliumSphere {
  private static long counter;
  private final long id = counter++;
  @Override
  public String toString() {
    return "Sphere " + id;
  }
}

public class CollectionComparison {
  public static void main(String[] args) {
    BerylliumSphere[] spheres =
      new BerylliumSphere[10];
    for(int i = 0; i < 5; i++)
      spheres[i] = new BerylliumSphere();
    show(spheres);
    System.out.println(spheres[4]);

    List<BerylliumSphere> sphereList = Suppliers.create(
      ArrayList::new, BerylliumSphere::new, 5);
    System.out.println(sphereList);
    System.out.println(sphereList.get(4));

    int[] integers = { 0, 1, 2, 3, 4, 5 };
    show(integers);
    System.out.println(integers[4]);

    List<Integer> intList = new ArrayList<>(
      Arrays.asList(0, 1, 2, 3, 4, 5));
    intList.add(97);
    System.out.println(intList);
    System.out.println(intList.get(4));
  }
}
/* Output:
[Sphere 0, Sphere 1, Sphere 2, Sphere 3, Sphere 4,
null, null, null, null, null]
Sphere 4
[Sphere 5, Sphere 6, Sphere 7, Sphere 8, Sphere 9]
Sphere 9
[0, 1, 2, 3, 4, 5]
4
[0, 1, 2, 3, 4, 5, 97]
4
*/
```

**Suppliers.create()** 方法在[泛型](book/20-Generics.md)一章中被定义。上面两种保存对象的方式都是有类型检查的，唯一比较明显的区别就是数组使用 **[ ]** 来随机存取元素，而一个 **List** 使用诸如 `add()` 和 `get()` 等方法。数组和 **ArrayList** 之间的相似是设计者有意为之，所以在概念上，两者很容易切换。但是就像你在[集合](book/12-Collections.md)中看到的，集合的功能明显多于数组。随着 Java 自动装箱技术的出现，通过集合使用基本数据类型几乎和通过数组一样简单。数组唯一剩下的优势就是效率。然而，当你解决一个更加普遍的问题时，数组可能限制太多，这种情形下，您可以使用集合类。


### 用于显示数组的实用程序

在本章中，我们处处都要显示数组。Java 提供了 **Arrays.toString()** 来将数组转换为可读字符串，然后可以在控制台上显示。然而这种方式视觉上噪音太大，所以我们创建一个小的库来完成这项工作。

```java
// onjava/ArrayShow.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package onjava;
import java.util.*;

public interface ArrayShow {
  static void show(Object[] a) {
    System.out.println(Arrays.toString(a));
  }
  static void show(boolean[] a) {
    System.out.println(Arrays.toString(a));
  }
  static void show(byte[] a) {
    System.out.println(Arrays.toString(a));
  }
  static void show(char[] a) {
    System.out.println(Arrays.toString(a));
  }
  static void show(short[] a) {
    System.out.println(Arrays.toString(a));
  }
  static void show(int[] a) {
    System.out.println(Arrays.toString(a));
  }
  static void show(long[] a) {
    System.out.println(Arrays.toString(a));
  }
  static void show(float[] a) {
    System.out.println(Arrays.toString(a));
  }
  static void show(double[] a) {
    System.out.println(Arrays.toString(a));
  }
  // Start with a description:
  static void show(String info, Object[] a) {
    System.out.print(info + ": ");
    show(a);
  }
  static void show(String info, boolean[] a) {
    System.out.print(info + ": ");
    show(a);
  }
  static void show(String info, byte[] a) {
    System.out.print(info + ": ");
    show(a);
  }
  static void show(String info, char[] a) {
    System.out.print(info + ": ");
    show(a);
  }
  static void show(String info, short[] a) {
    System.out.print(info + ": ");
    show(a);
  }
  static void show(String info, int[] a) {
    System.out.print(info + ": ");
    show(a);
  }
  static void show(String info, long[] a) {
    System.out.print(info + ": ");
    show(a);
  }
  static void show(String info, float[] a) {
    System.out.print(info + ": ");
    show(a);
  }
  static void show(String info, double[] a) {
    System.out.print(info + ": ");
    show(a);
  }
}
```

第一个方法适用于对象数组，包括那些包装基本数据类型的数组。所有的方法重载对于不同的数据类型是必要的。

第二组重载方法可以让你显示带有信息 **字符串** 前缀的数组。

为了简单起见，你通常可以静态地导入它们。

<!-- Arrays are First-Class Objects -->

## 一等对象

不管你使用的什么类型的数组，数组中的数据集实际上都是对堆中真正对象的引用。数组是保存指向其他对象的引用的对象，数组可以隐式地创建，作为数组初始化语法的一部分，也可以显式地创建，比如使用一个 **new** 表达式。数组对象的一部分（事实上，你唯一可以使用的方法）就是只读的 **length** 成员函数，它能告诉你数组对象中可以存储多少元素。**[ ]** 语法是你访问数组对象的唯一方式。

下面的例子总结了初始化数组的多种方式，并且展示了如何给不同的数组对象分配数组引用。同时也可以看出对象数组和基元数组在使用上是完全相同的。唯一的不同之处就是对象数组存储的是对象的引用，而基元数组则直接存储基本数据类型的值。

```java
// arrays/ArrayOptions.java
// Initialization & re-assignment of arrays
import java.util.*;
import static onjava.ArrayShow.*;

public class ArrayOptions {
  public static void main(String[] args) {
    // Arrays of objects:
    BerylliumSphere[] a; // Uninitialized local
    BerylliumSphere[] b = new BerylliumSphere[5];

    // The references inside the array are
    // automatically initialized to null:
    show("b", b);
    BerylliumSphere[] c = new BerylliumSphere[4];
    for(int i = 0; i < c.length; i++)
      if(c[i] == null) // Can test for null reference
        c[i] = new BerylliumSphere();

    // Aggregate initialization:
    BerylliumSphere[] d = {
      new BerylliumSphere(),
      new BerylliumSphere(),
      new BerylliumSphere()
    };

    // Dynamic aggregate initialization:
    a = new BerylliumSphere[]{
      new BerylliumSphere(), new BerylliumSphere(),
    };
    // (Trailing comma is optional)

    System.out.println("a.length = " + a.length);
    System.out.println("b.length = " + b.length);
    System.out.println("c.length = " + c.length);
    System.out.println("d.length = " + d.length);
    a = d;
    System.out.println("a.length = " + a.length);

    // Arrays of primitives:
    int[] e; // Null reference
    int[] f = new int[5];

    // The primitives inside the array are
    // automatically initialized to zero:
    show("f", f);
    int[] g = new int[4];
    for(int i = 0; i < g.length; i++)
      g[i] = i*i;
    int[] h = { 11, 47, 93 };

    //  Compile error: variable e not initialized:
    //- System.out.println("e.length = " + e.length);
    System.out.println("f.length = " + f.length);
    System.out.println("g.length = " + g.length);
    System.out.println("h.length = " + h.length);
    e = h;
    System.out.println("e.length = " + e.length);
    e = new int[]{ 1, 2 };
    System.out.println("e.length = " + e.length);
  }
}
/* Output:
b: [null, null, null, null, null]
a.length = 2
b.length = 5
c.length = 4
d.length = 3
a.length = 3
f: [0, 0, 0, 0, 0]
f.length = 5
g.length = 4
h.length = 3
e.length = 3
e.length = 2
*/
```

数组 **a** 是一个未初始化的本地变量，编译器不会允许你使用这个引用直到你正确地对其进行初始化。数组 **b** 被初始化成一系列指向 **BerylliumSphere** 对象的引用，但是并没有真正的 **BerylliumSphere** 对象被存储在数组中。尽管你仍然可以获得这个数组的大小，因为 **b** 指向合法对象。这带来了一个小问题：你无法找出到底有多少元素存储在数组中，因为 **length** 只能告诉你数组可以存储多少元素；这就是说，数组对象的大小并不是真正存储在数组中对象的个数。然而，当你创建一个数组对象，其引用将自动初始化为 **null**，因此你可以通过检查特定数组元素中的引用是否为 **null** 来判断其中是否有对象。基元数组也有类似的机制，比如自动将数值类型初始化为 **0**，char 型初始化为 **(char)0**，布尔类型初始化为 **false**。

数组 **c** 展示了创建数组对象后给数组中各元素分配 **BerylliumSphere** 对象。数组 **d** 展示了创建数组对象的聚合初始化语法（隐式地使用 **new** 在堆中创建对象，就像 **c** 一样）并且初始化成 **BeryliumSphere** 对象，这一切都在一条语句中完成。

下一个数组初始化可以被看做是一个“动态聚合初始化”。 **d** 使用的聚合初始化必须在 **d** 定义处使用，但是使用第二种语法，你可以在任何地方创建和初始化数组对象。例如，假设 **hide()** 是一个需要使用一系列的 **BeryliumSphere**对象。你可以这样调用它：

```Java
hide(d);
```

你也可以动态地创建你用作参数传递的数组：

```Java
hide(new BerylliumSphere[]{
    new BerlliumSphere(),
    new BerlliumSphere()
});
```

很多情况下这种语法写代码更加方便。

表达式：

```Java
a = d;
```

显示了你如何获取指向一个数组对象的引用并将其分配给另一个数组对象。就像你可以处理其他类型的对象引用。现在 **a** 和 **d** 都指向了堆中的同一个数组对象。

**ArrayOptions.java** 的第二部分展示了基元数组的语法就像对象数组一样，除了基元数组直接保存基本数据类型的值。

<!-- Returning an Array -->

## 返回数组

假设你写了一个方法，这个方法不是返回一个元素，而是返回多个元素。对 C++/C 这样的语言来说这是很困难的，因为你无法返回一个数组，只能是返回一个指向数组的指针。这会带来一些问题，因为对数组生存期的控制变得很混乱，这会导致内存泄露。

而在 Java 中，你只需返回数组，你永远不用为数组担心，只要你需要它，它就可用，垃圾收集器会在你用完后把它清理干净。

下面，我们返回一个 **字符串** 数组：

```Java
// arrays/IceCreamFlavors.java
// Returning arrays from methods
import java.util.*;
import static onjava.ArrayShow.*;

public class IceCreamFlavors {
  private static SplittableRandom rand =
    new SplittableRandom(47);
  static final String[] FLAVORS = {
    "Chocolate", "Strawberry", "Vanilla Fudge Swirl",
    "Mint Chip", "Mocha Almond Fudge", "Rum Raisin",
    "Praline Cream", "Mud Pie"
  };
  public static String[] flavorSet(int n) {
    if(n > FLAVORS.length)
      throw new IllegalArgumentException("Set too big");
    String[] results = new String[n];
    boolean[] picked = new boolean[FLAVORS.length];
    for(int i = 0; i < n; i++) {
      int t;
      do
        t = rand.nextInt(FLAVORS.length);
      while(picked[t]);
      results[i] = FLAVORS[t];
      picked[t] = true;
    }
    return results;
  }
  public static void main(String[] args) {
    for(int i = 0; i < 7; i++)
      show(flavorSet(3));
  }
}
/* Output:
[Praline Cream, Mint Chip, Vanilla Fudge Swirl]
[Strawberry, Vanilla Fudge Swirl, Mud Pie]
[Chocolate, Strawberry, Vanilla Fudge Swirl]
[Rum Raisin, Praline Cream, Chocolate]
[Mint Chip, Rum Raisin, Mocha Almond Fudge]
[Mocha Almond Fudge, Mud Pie, Vanilla Fudge Swirl]
[Mocha Almond Fudge, Mud Pie, Mint Chip]
*/
```

**flavorset()** 创建名为 **results** 的 **String** 类型的数组。 这个数组的大小 **n** 取决于你传进方法的参数。然后从数组 **FLAVORS** 中随机选择 flavors 并且把它们放进 **results** 里并返回。返回一个数组就像返回其他任何对象一样，实际上返回的是引用。数组是在 **flavorSet()** 中或者是在其他什么地方创建的并不重要。垃圾收集器会清理你用完的数组，你需要的数组则会保留。

如果你必须要返回一系列不同类型的元素，你可以使用 [泛型](book/20-Generics.md) 中介绍的 **元组** 。

注意，当 **flavorSet()** 随机选择 flavors，它应该确保某个特定的选项没被选中。这在一个 **do** 循环中执行，它将一直做出随机选择直到它发现一个元素不在 **picked** 数组中。（一个字符串

比较将显示出随机选中的元素是不是已经存在于 **results** 数组中）。如果成功了，它将添加条目并且寻找下一个（ **i** 递增）。输出结果显示 **flavorSet()** 每一次都是按照随机顺序选择 flavors。

一直到现在，随机数都是通过 **java.util.Random** 类生成的，这个类从 Java 1.0 就有，甚至更新过以提供 Java 8 流。现在我们可以介绍 Java 8 中的 **SplittableRandom** ,它不仅能在并行操作使用（你最终会学到），而且提供了一个高质量的随机数。这本书的剩余部分都使用  **SplittableRandom** 。

<!-- Multidimensional Arrays -->

## 多维数组

要创建多维的基元数组，你要用大括号来界定数组中的向量：

```Java
// arrays/MultidimensionalPrimitiveArray.java
import java.util.*;

public class MultidimensionalPrimitiveArray {
  public static void main(String[] args) {
    int[][] a = {
      { 1, 2, 3, },
      { 4, 5, 6, },
    };
    System.out.println(Arrays.deepToString(a));
  }
}
/* Output:
[[1, 2, 3], [4, 5, 6]]
*/。
```

每个嵌套的大括号都代表了数组的一个维度。

这个例子使用 **Arrays.deepToString()** 方法，将多维数组转换成 **String** 类型，就像输出中显示的那样。

你也可以使用 **new** 分配数组。这是一个使用 **new** 表达式分配的三维数组：

```Java
// arrays/ThreeDWithNew.java
import java.util.*;

public class ThreeDWithNew {
  public static void main(String[] args) {
    // 3-D array with fixed length:
    int[][][] a = new int[2][2][4];
    System.out.println(Arrays.deepToString(a));
  }
}
/* Output:
[[[0, 0, 0, 0], [0, 0, 0, 0]], [[0, 0, 0, 0], [0, 0, 0,
0]]]
*/
```

倘若你不对基元数组进行显式的初始化，它的值会自动初始化。而对象数组将被初始化为 **null** 。

组成矩阵的数组中每一个向量都可以是任意长度的（这叫做不规则数组）：

```Java
// arrays/RaggedArray.java
import java.util.*;

public class RaggedArray {
  static int val = 1;
  public static void main(String[] args) {
    SplittableRandom rand = new SplittableRandom(47);
    // 3-D array with varied-length vectors:
    int[][][] a = new int[rand.nextInt(7)][][];
    for(int i = 0; i < a.length; i++) {
      a[i] = new int[rand.nextInt(5)][];
      for(int j = 0; j < a[i].length; j++) {
        a[i][j] = new int[rand.nextInt(5)];
        Arrays.setAll(a[i][j], n -> val++); // [1]
      }
    }
    System.out.println(Arrays.deepToString(a));
  }
}
/* Output:
[[[1], []], [[2, 3, 4, 5], [6]], [[7, 8, 9], [10, 11,
12], []]]
*/
```

第一个 **new** 创建了一个数组，这个数组首元素长度随机，其余的则不确定。第二个 **new** 在 for 循环中给数组填充了第二个元素，第三个 **new**  为数组的最后一个索引填充元素。

* **[1]** Java 8 增加了 **Arrays.setAll()** 方法,其使用生成器来生成插入数组中的值。此生成器符合函数式接口 **IntUnaryOperator** ，只使用一个非 **默认** 的方法 **ApplyAsint(int操作数)** 。 **Arrays.setAll()** 传递当前数组索引作为操作数，因此一个选项是提供 **n -> n** 的 lambda 表达式来显示数组的索引（在上面的代码中很容易尝试）。这里，我们忽略索引，只是插入递增计数器的值。

非基元的对象数组也可以定义为不规则数组。这里，我们收集了许多使用大括号的 **new** 表达式：

```Java
// arrays/MultidimensionalObjectArrays.java
import java.util.*;

public class MultidimensionalObjectArrays {
  public static void main(String[] args) {
    BerylliumSphere[][] spheres = {
      { new BerylliumSphere(), new BerylliumSphere() },
      { new BerylliumSphere(), new BerylliumSphere(),
        new BerylliumSphere(), new BerylliumSphere() },
      { new BerylliumSphere(), new BerylliumSphere(),
        new BerylliumSphere(), new BerylliumSphere(),
        new BerylliumSphere(), new BerylliumSphere(),
        new BerylliumSphere(), new BerylliumSphere() },
    };
    System.out.println(Arrays.deepToString(spheres));
  }
}
/* Output:
[[Sphere 0, Sphere 1], [Sphere 2, Sphere 3, Sphere 4,
Sphere 5], [Sphere 6, Sphere 7, Sphere 8, Sphere 9,
Sphere 10, Sphere 11, Sphere 12, Sphere 13]]
*/
```

数组初始化时使用自动装箱技术：

```Java
// arrays/AutoboxingArrays.java
import java.util.*;

public class AutoboxingArrays {
  public static void main(String[] args) {
    Integer[][] a = { // Autoboxing:
      { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 },
      { 21, 22, 23, 24, 25, 26, 27, 28, 29, 30 },
      { 51, 52, 53, 54, 55, 56, 57, 58, 59, 60 },
      { 71, 72, 73, 74, 75, 76, 77, 78, 79, 80 },
    };
    System.out.println(Arrays.deepToString(a));
  }
}
/* Output:
[[1, 2, 3, 4, 5, 6, 7, 8, 9, 10], [21, 22, 23, 24, 25,
26, 27, 28, 29, 30], [51, 52, 53, 54, 55, 56, 57, 58,
59, 60], [71, 72, 73, 74, 75, 76, 77, 78, 79, 80]]
*/
```

以下是如何逐个构建非基元的对象数组：

```Java
// arrays/AssemblingMultidimensionalArrays.java
// Creating multidimensional arrays
import java.util.*;

public class AssemblingMultidimensionalArrays {
  public static void main(String[] args) {
    Integer[][] a;
    a = new Integer[3][];
    for(int i = 0; i < a.length; i++) {
      a[i] = new Integer[3];
      for(int j = 0; j < a[i].length; j++)
        a[i][j] = i * j; // Autoboxing
    }
    System.out.println(Arrays.deepToString(a));
  }
}
/* Output:
[[0, 0, 0], [0, 1, 2], [0, 2, 4]]
*/
```

**i * j** 在这里只是为了向 **Integer** 中添加有趣的值。

**Arrays.deepToString()** 方法同时适用于基元数组和对象数组：

```JAVA
// arrays/MultiDimWrapperArray.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Multidimensional arrays of "wrapper" objects
import java.util.*;

public class MultiDimWrapperArray {
  public static void main(String[] args) {
    Integer[][] a1 = { // Autoboxing
      { 1, 2, 3, },
      { 4, 5, 6, },
    };
    Double[][][] a2 = { // Autoboxing
      { { 1.1, 2.2 }, { 3.3, 4.4 } },
      { { 5.5, 6.6 }, { 7.7, 8.8 } },
      { { 9.9, 1.2 }, { 2.3, 3.4 } },
    };
    String[][] a3 = {
      { "The", "Quick", "Sly", "Fox" },
      { "Jumped", "Over" },
      { "The", "Lazy", "Brown", "Dog", "&", "friend" },
    };
    System.out.println(
      "a1: " + Arrays.deepToString(a1));
    System.out.println(
      "a2: " + Arrays.deepToString(a2));
    System.out.println(
      "a3: " + Arrays.deepToString(a3));
  }
}
/* Output:
a1: [[1, 2, 3], [4, 5, 6]]
a2: [[[1.1, 2.2], [3.3, 4.4]], [[5.5, 6.6], [7.7,
8.8]], [[9.9, 1.2], [2.3, 3.4]]]
a3: [[The, Quick, Sly, Fox], [Jumped, Over], [The,
Lazy, Brown, Dog, &, friend]]
*/
```

同样的，在 **Integer** 和 **Double** 数组中，自动装箱可为你创建包装器对象。

<!-- Arrays and Generics -->
## 泛型数组

一般来说，数组和泛型并不能很好的结合。你不能实例化参数化类型的数组：

```Java
Peel<Banana>[] peels = new Peel<Banana>[10]; // Illegal
```

类型擦除需要删除参数类型信息，而且数组必须知道它们所保存的确切类型，以强制保证类型安全。

但是，可以参数化数组本身的类型：

```Java
// arrays/ParameterizedArrayType.java

class ClassParameter<T> {
  public T[] f(T[] arg) { return arg; }
}

class MethodParameter {
  public static <T> T[] f(T[] arg) { return arg; }
}

public class ParameterizedArrayType {
  public static void main(String[] args) {
    Integer[] ints = { 1, 2, 3, 4, 5 };
    Double[] doubles = { 1.1, 2.2, 3.3, 4.4, 5.5 };
    Integer[] ints2 =
      new ClassParameter<Integer>().f(ints);
    Double[] doubles2 =
      new ClassParameter<Double>().f(doubles);
    ints2 = MethodParameter.f(ints);
    doubles2 = MethodParameter.f(doubles);
  }
}
```

比起使用参数化类，使用参数化方法很方便。您不必为应用它的每个不同类型都实例化一个带有参数的类，但是可以使它成为 **静态** 的。你不能总是选择使用参数化方法而不用参数化的类，但通常参数化方法是更好的选择。

你不能创建泛型类型的数组，这种说法并不完全正确。是的，编译器不会让你 *实例化* 一个泛型的数组。但是，它将允许您创建对此类数组的引用。例如：

```Java
List<String>[] ls;
```

无可争议的，这可以通过编译。尽管不能创建包含泛型的实际数组对象，但是你可以创建一个非泛型的数组并对其进行强制类型转换：

```Java
// arrays/ArrayOfGenerics.java
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
```

一旦你有了对 **List<String>[]** 的引用 , 你会发现多了一些编译时检查。问题是数组是协变的，所以 **List<String>[]** 也是一个 **Object[]**  ，你可以用这来将 **ArrayList<Integer> ** 分配进你的数组，在编译或者运行时都不会出错。

如果你知道你不会进行向上类型转换，你的需求相对简单，那么可以创建一个泛型数组，它将提供基本的编译时类型检查。然而，一个泛型 **Collection** 实际上是一个比泛型数组更好的选择。

一般来说，您会发现泛型在类或方法的边界上是有效的。在内部，擦除常常会使泛型不可使用。所以，就像下面的例子，不能创建泛型类型的数组：

```Java
// arrays/ArrayOfGenericType.java

public class ArrayOfGenericType<T> {
  T[] array; // OK
  @SuppressWarnings("unchecked")
  public ArrayOfGenericType(int size) {
    // error: generic array creation:
    //- array = new T[size];
    array = (T[])new Object[size]; // unchecked cast
  }
  // error: generic array creation:
  //- public <U> U[] makeArray() { return new U[10]; }
}

```

擦除再次从中作梗，这个例子试图创建已经擦除的类型数组，因此它们是未知的类型。你可以创建一个 **对象** 数组，然后对其进行强制类型转换，但如果没有 **@SuppressWarnings** 注释，你将会得到一个 "unchecked" 警告，因为数组实际上不真正支持而且将对类型 **T** 动态检查 。这就是说，如果我创建了一个 **String[]** , Java将在编译时和运行时强制执行，我只能在数组中放置字符串对象。然而，如果我创建一个 **Object[]** ,我可以把除了基元类型外的任何东西放入数组。



<!-- Arrays.fill() -->
## Arrays的fill方法

通常情况下，当对数组和程序进行实验时，能够很轻易地生成充满测试数据的数组是很有帮助的。 Java 标准库 **Arrays** 类包括一个普通的 **fill()** 方法，该方法将单个值复制到整个数组，或者在对象数组的情况下，将相同的引用复制到整个数组：

```Java
// arrays/FillingArrays.java
// Using Arrays.fill()
import java.util.*;
import static onjava.ArrayShow.*;

public class FillingArrays {
  public static void main(String[] args) {
    int size = 6;
    boolean[] a1 = new boolean[size];
    byte[] a2 = new byte[size];
    char[] a3 = new char[size];
    short[] a4 = new short[size];
    int[] a5 = new int[size];
    long[] a6 = new long[size];
    float[] a7 = new float[size];
    double[] a8 = new double[size];
    String[] a9 = new String[size];
    Arrays.fill(a1, true);
    show("a1", a1);
    Arrays.fill(a2, (byte)11);
    show("a2", a2);
    Arrays.fill(a3, 'x');
    show("a3", a3);
    Arrays.fill(a4, (short)17);
    show("a4", a4);
    Arrays.fill(a5, 19);
    show("a5", a5);
    Arrays.fill(a6, 23);
    show("a6", a6);
    Arrays.fill(a7, 29);
    show("a7", a7);
    Arrays.fill(a8, 47);
    show("a8", a8);
    Arrays.fill(a9, "Hello");
    show("a9", a9);
    // Manipulating ranges:
    Arrays.fill(a9, 3, 5, "World");
    show("a9", a9);
  }
}gedan
/* Output:
a1: [true, true, true, true, true, true]
a2: [11, 11, 11, 11, 11, 11]
a3: [x, x, x, x, x, x]
a4: [17, 17, 17, 17, 17, 17]
a5: [19, 19, 19, 19, 19, 19]
a6: [23, 23, 23, 23, 23, 23]
a7: [29.0, 29.0, 29.0, 29.0, 29.0, 29.0]
a8: [47.0, 47.0, 47.0, 47.0, 47.0, 47.0]
a9: [Hello, Hello, Hello, Hello, Hello, Hello]
a9: [Hello, Hello, Hello, World, World, Hello]
*/

```

你既可以填充整个数组，也可以像最后两个语句所示，填充一系列的元素。但是由于你只能使用单个值调用 **Arrays.fill()** ，因此结果并非特别有用。


<!-- Arrays.setAll() -->
## Arrays的setAll方法

在Java 8中， 在**RaggedArray.java** 中引入并在 **ArrayOfGenerics.java.Array.setAll()** 中重用。它使用一个生成器并生成不同的值，可以选择基于数组的索引元素（通过访问当前索引，生成器可以读取数组值并对其进行修改）。 **static Arrays.setAll()** 的重载签名为：

* **void setAll(int[] a, IntUnaryOperator gen)**
* **void setAll(long[] a, IntToLongFunction gen)**
* **void setAll(double[] a, IntToDoubleFunctiongen)**
* **<T> void setAll(T[] a, IntFunction<? extendsT> gen)**

除了 **int** , **long** , **double** 有特殊的版本，其他的一切都由泛型版本处理。生成器不是 **Supplier** 因为它们不带参数，并且必须将 **int** 数组索引作为参数。

```java
// arrays/SimpleSetAll.java

import java.util.*;
import static onjava.ArrayShow.*;

class Bob {
  final int id;
  Bob(int n) { id = n; }
  @Override
  public String toString() { return "Bob" + id; }
}

public class SimpleSetAll {
  public static final int SZ = 8;
  static int val = 1;
  static char[] chars = "abcdefghijklmnopqrstuvwxyz"
    .toCharArray();
  static char getChar(int n) { return chars[n]; }
  public static void main(String[] args) {
    int[] ia = new int[SZ];
    long[] la = new long[SZ];
    double[] da = new double[SZ];
    Arrays.setAll(ia, n -> n); // [1]
    Arrays.setAll(la, n -> n);
    Arrays.setAll(da, n -> n);
    show(ia);
    show(la);
    show(da);
    Arrays.setAll(ia, n -> val++); // [2]
    Arrays.setAll(la, n -> val++);
    Arrays.setAll(da, n -> val++);
    show(ia);
    show(la);
    show(da);

    Bob[] ba = new Bob[SZ];
    Arrays.setAll(ba, Bob::new); // [3]
    show(ba);

    Character[] ca = new Character[SZ];
    Arrays.setAll(ca, SimpleSetAll::getChar); // [4]
    show(ca);
  }
}
/* Output:
[0, 1, 2, 3, 4, 5, 6, 7]
[0, 1, 2, 3, 4, 5, 6, 7]
[0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0]
[1, 2, 3, 4, 5, 6, 7, 8]
[9, 10, 11, 12, 13, 14, 15, 16]
[17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0]
[Bob0, Bob1, Bob2, Bob3, Bob4, Bob5, Bob6, Bob7]
[a, b, c, d, e, f, g, h]
*/

```

* **[1]** 这里，我们只是将数组索引作为值插入数组。这将自动转化为 **long** 和 **double** 版本。
* **[2]** 这个函数只需要接受索引就能产生正确结果。这个，我们忽略索引值并且使用 **val** 生成结果。
* **[3]** 方法引用有效，因为 **Bob** 的构造器接收一个 **int** 参数。只要我们传递的函数接收一个 **int** 参数且能产生正确的结果，就认为它完成了工作。
* **[4]** 为了处理除了  **int** ，**long** ，**double** 之外的基元类型，请为基元创建包装类的数组。然后使用 **setAll()** 的泛型版本。请注意，**getChar（）** 生成基元类型，因此这是自动装箱到 **Character** 。


<!-- Incremental Generators -->
## 增量生成

这是一个方法库，用于为不同类型生成增量值。

这些被作为内部类来生成容易记住的名字；比如，为了使用 **Integer** 工具你可以用 **new Conut.Interger()** , 如果你想要使用基本数据类型 **int** 工具，你可以用 **new Count.Pint()**  (基本类型的名字不能被直接使用，所以它们都在前面添加一个 **P**  来表示基本数据类型'primitive', 我们的第一选择是使用基本类型名字后面跟着下划线，比如 **int_** 和 **double_**  ,但是这种方式违背Java的命名习惯）。每个包装类的生成器都使用 **get()** 方法实现了它的 **Supplier** 。要使用**Array.setAll()** ，一个重载的 **get(int n)** 方法要接受（并忽略）其参数，以便接受 **setAll()** 传递的索引值。

注意，通过使用包装类的名称作为内部类名，我们必须调用 **java.lang** 包来保证我们可以使用实际包装类的名字：

```java
// onjava/Count.java
// Generate incremental values of different types
package onjava;
import java.util.*;
import java.util.function.*;
import static onjava.ConvertTo.*;

public interface Count {
  class Boolean
  implements Supplier<java.lang.Boolean> {
    private boolean b = true;
    @Override
    public java.lang.Boolean get() {
      b = !b;
      return java.lang.Boolean.valueOf(b);
    }
    public java.lang.Boolean get(int n) {
      return get();
    }
    public java.lang.Boolean[] array(int sz) {
      java.lang.Boolean[] result =
        new java.lang.Boolean[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pboolean {
    private boolean b = true;
    public boolean get() {
      b = !b;
      return b;
    }
    public boolean get(int n) { return get(); }
    public boolean[] array(int sz) {
      return primitive(new Boolean().array(sz));
    }
  }
  class Byte
  implements Supplier<java.lang.Byte> {
    private byte b;
    @Override
    public java.lang.Byte get() { return b++; }
    public java.lang.Byte get(int n) {
      return get();
    }
    public java.lang.Byte[] array(int sz) {
      java.lang.Byte[] result =
        new java.lang.Byte[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pbyte {
    private byte b;
    public byte get() { return b++; }
    public byte get(int n) { return get(); }
    public byte[] array(int sz) {
      return primitive(new Byte().array(sz));
    }
  }
  char[] CHARS =
    "abcdefghijklmnopqrstuvwxyz".toCharArray();
  class Character
  implements Supplier<java.lang.Character> {
    private int i;
    @Override
    public java.lang.Character get() {
      i = (i + 1) % CHARS.length;
      return CHARS[i];
    }
    public java.lang.Character get(int n) {
      return get();
    }
    public java.lang.Character[] array(int sz) {
      java.lang.Character[] result =
        new java.lang.Character[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pchar {
    private int i;
    public char get() {
      i = (i + 1) % CHARS.length;
      return CHARS[i];
    }
    public char get(int n) { return get(); }
    public char[] array(int sz) {
      return primitive(new Character().array(sz));
    }
  }
  class Short
  implements Supplier<java.lang.Short> {
    short s;
    @Override
    public java.lang.Short get() { return s++; }
    public java.lang.Short get(int n) {
      return get();
    }
    public java.lang.Short[] array(int sz) {
      java.lang.Short[] result =
        new java.lang.Short[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pshort {
    short s;
    public short get() { return s++; }
    public short get(int n) { return get(); }
    public short[] array(int sz) {
      return primitive(new Short().array(sz));
    }
  }
  class Integer
  implements Supplier<java.lang.Integer> {
    int i;
    @Override
    public java.lang.Integer get() { return i++; }
    public java.lang.Integer get(int n) {
      return get();
    }
    public java.lang.Integer[] array(int sz) {
      java.lang.Integer[] result =
        new java.lang.Integer[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pint implements IntSupplier {
    int i;
    public int get() { return i++; }
    public int get(int n) { return get(); }
    @Override
    public int getAsInt() { return get(); }
    public int[] array(int sz) {
      return primitive(new Integer().array(sz));
    }
  }
  class Long
  implements Supplier<java.lang.Long> {
    private long l;
    @Override
    public java.lang.Long get() { return l++; }
    public java.lang.Long get(int n) {
      return get();
    }
    public java.lang.Long[] array(int sz) {
      java.lang.Long[] result =
        new java.lang.Long[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Plong implements LongSupplier {
    private long l;
    public long get() { return l++; }
    public long get(int n) { return get(); }
    @Override
    public long getAsLong() { return get(); }
    public long[] array(int sz) {
      return primitive(new Long().array(sz));
    }
  }
  class Float
  implements Supplier<java.lang.Float> {
    private int i;
    @Override
    public java.lang.Float get() {
      return java.lang.Float.valueOf(i++);
    }
    public java.lang.Float get(int n) {
      return get();
    }
    public java.lang.Float[] array(int sz) {
      java.lang.Float[] result =
        new java.lang.Float[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pfloat {
    private int i;
    public float get() { return i++; }
    public float get(int n) { return get(); }
    public float[] array(int sz) {
      return primitive(new Float().array(sz));
    }
  }
  class Double
  implements Supplier<java.lang.Double> {
    private int i;
    @Override
    public java.lang.Double get() {
      return java.lang.Double.valueOf(i++);
    }
    public java.lang.Double get(int n) {
      return get();
    }
    public java.lang.Double[] array(int sz) {
      java.lang.Double[] result =
        new java.lang.Double[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pdouble implements DoubleSupplier {
    private int i;
    public double get() { return i++; }
    public double get(int n) { return get(); }
    @Override
    public double getAsDouble() { return get(0); }
    public double[] array(int sz) {
      return primitive(new Double().array(sz));
    }
  }
}

```

对于 **int** ，**long** ，**double** 这三个有特殊 **Supplier** 接口的原始数据类型来说，**Pint** ， **Plong** 和 **Pdouble** 实现了这些接口。

这里是对 **Count** 的测试，这同样给我们提供了如何使用它的例子：

```java
// arrays/TestCount.java
// Test counting generators
import java.util.*;
import java.util.stream.*;
import onjava.*;
import static onjava.ArrayShow.*;

public class TestCount {
  static final int SZ = 5;
  public static void main(String[] args) {
    System.out.println("Boolean");
    Boolean[] a1 = new Boolean[SZ];
    Arrays.setAll(a1, new Count.Boolean()::get);
    show(a1);
    a1 = Stream.generate(new Count.Boolean())
      .limit(SZ + 1).toArray(Boolean[]::new);
    show(a1);
    a1 = new Count.Boolean().array(SZ + 2);
    show(a1);
    boolean[] a1b =
      new Count.Pboolean().array(SZ + 3);
    show(a1b);

    System.out.println("Byte");
    Byte[] a2 = new Byte[SZ];
    Arrays.setAll(a2, new Count.Byte()::get);
    show(a2);
    a2 = Stream.generate(new Count.Byte())
      .limit(SZ + 1).toArray(Byte[]::new);
    show(a2);
    a2 = new Count.Byte().array(SZ + 2);
    show(a2);
    byte[] a2b = new Count.Pbyte().array(SZ + 3);
    show(a2b);

    System.out.println("Character");
    Character[] a3 = new Character[SZ];
    Arrays.setAll(a3, new Count.Character()::get);
    show(a3);
    a3 = Stream.generate(new Count.Character())
      .limit(SZ + 1).toArray(Character[]::new);
    show(a3);
    a3 = new Count.Character().array(SZ + 2);
    show(a3);
    char[] a3b = new Count.Pchar().array(SZ + 3);
    show(a3b);

    System.out.println("Short");
    Short[] a4 = new Short[SZ];
    Arrays.setAll(a4, new Count.Short()::get);
    show(a4);
    a4 = Stream.generate(new Count.Short())
      .limit(SZ + 1).toArray(Short[]::new);
    show(a4);
    a4 = new Count.Short().array(SZ + 2);
    show(a4);
    short[] a4b = new Count.Pshort().array(SZ + 3);
    show(a4b);

    System.out.println("Integer");
    int[] a5 = new int[SZ];
    Arrays.setAll(a5, new Count.Integer()::get);
    show(a5);
    Integer[] a5b =
      Stream.generate(new Count.Integer())
        .limit(SZ + 1).toArray(Integer[]::new);
    show(a5b);
    a5b = new Count.Integer().array(SZ + 2);
    show(a5b);
    a5 = IntStream.generate(new Count.Pint())
      .limit(SZ + 1).toArray();
    show(a5);
    a5 = new Count.Pint().array(SZ + 3);
    show(a5);

    System.out.println("Long");
    long[] a6 = new long[SZ];
    Arrays.setAll(a6, new Count.Long()::get);
    show(a6);
    Long[] a6b = Stream.generate(new Count.Long())
      .limit(SZ + 1).toArray(Long[]::new);
    show(a6b);
    a6b = new Count.Long().array(SZ + 2);
    show(a6b);
    a6 = LongStream.generate(new Count.Plong())
      .limit(SZ + 1).toArray();
    show(a6);
    a6 = new Count.Plong().array(SZ + 3);
    show(a6);

    System.out.println("Float");
    Float[] a7 = new Float[SZ];
    Arrays.setAll(a7, new Count.Float()::get);
    show(a7);
    a7 = Stream.generate(new Count.Float())
      .limit(SZ + 1).toArray(Float[]::new);
    show(a7);
    a7 = new Count.Float().array(SZ + 2);
    show(a7);
    float[] a7b = new Count.Pfloat().array(SZ + 3);
    show(a7b);

    System.out.println("Double");
    double[] a8 = new double[SZ];
    Arrays.setAll(a8, new Count.Double()::get);
    show(a8);
    Double[] a8b =
      Stream.generate(new Count.Double())
        .limit(SZ + 1).toArray(Double[]::new);
    show(a8b);
    a8b = new Count.Double().array(SZ + 2);
    show(a8b);
    a8 = DoubleStream.generate(new Count.Pdouble())
      .limit(SZ + 1).toArray();
    show(a8);
    a8 = new Count.Pdouble().array(SZ + 3);
    show(a8);
  }
}
/* Output:
Boolean
[false, true, false, true, false]
[false, true, false, true, false, true]
[false, true, false, true, false, true, false]
[false, true, false, true, false, true, false, true]
Byte
[0, 1, 2, 3, 4]
[0, 1, 2, 3, 4, 5]
[0, 1, 2, 3, 4, 5, 6]
[0, 1, 2, 3, 4, 5, 6, 7]
Character
[b, c, d, e, f]
[b, c, d, e, f, g]
[b, c, d, e, f, g, h]
[b, c, d, e, f, g, h, i]
Short
[0, 1, 2, 3, 4]
[0, 1, 2, 3, 4, 5]
[0, 1, 2, 3, 4, 5, 6]
[0, 1, 2, 3, 4, 5, 6, 7]
Integer
[0, 1, 2, 3, 4]
[0, 1, 2, 3, 4, 5]
[0, 1, 2, 3, 4, 5, 6]
[0, 1, 2, 3, 4, 5]
[0, 1, 2, 3, 4, 5, 6, 7]
Long
[0, 1, 2, 3, 4]
[0, 1, 2, 3, 4, 5]
[0, 1, 2, 3, 4, 5, 6]
[0, 1, 2, 3, 4, 5]
[0, 1, 2, 3, 4, 5, 6, 7]
Float
[0.0, 1.0, 2.0, 3.0, 4.0]
[0.0, 1.0, 2.0, 3.0, 4.0, 5.0]
[0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0]
[0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0]
Double
[0.0, 1.0, 2.0, 3.0, 4.0]
[0.0, 1.0, 2.0, 3.0, 4.0, 5.0]
[0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0]
[0.0, 1.0, 2.0, 3.0, 4.0, 5.0]
[0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0]
*/

```

注意到原始数组类型 **int[]** ，**long[]** ，**double[]** 可以直接被 **Arrays.setAll()** 填充，但是其他的原始类型都要求用包装器类型的数组。

通过 **Stream.generate()** 创建的包装数组显示了 **toArray（）** 的重载用法，在这里你应该提供给它要创建的数组类型的构造器。

<!-- Random Generators -->
## 随机生成

我们可以按照 **Count.java** 的结构创建一个生成随机值的工具：

```java
// onjava/Rand.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Generate random values of different types
package onjava;
import java.util.*;
import java.util.function.*;
import static onjava.ConvertTo.*;

public interface Rand {
  int MOD = 10_000;
  class Boolean
  implements Supplier<java.lang.Boolean> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Boolean get() {
      return r.nextBoolean();
    }
    public java.lang.Boolean get(int n) {
      return get();
    }
    public java.lang.Boolean[] array(int sz) {
      java.lang.Boolean[] result =
        new java.lang.Boolean[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pboolean {
    public boolean[] array(int sz) {
      return primitive(new Boolean().array(sz));
    }
  }
  class Byte
  implements Supplier<java.lang.Byte> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Byte get() {
      return (byte)r.nextInt(MOD);
    }
    public java.lang.Byte get(int n) {
      return get();
    }
    public java.lang.Byte[] array(int sz) {
      java.lang.Byte[] result =
        new java.lang.Byte[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pbyte {
    public byte[] array(int sz) {
      return primitive(new Byte().array(sz));
    }
  }
  class Character
  implements Supplier<java.lang.Character> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Character get() {
      return (char)r.nextInt('a', 'z' + 1);
    }
    public java.lang.Character get(int n) {
      return get();
    }
    public java.lang.Character[] array(int sz) {
      java.lang.Character[] result =
        new java.lang.Character[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pchar {
    public char[] array(int sz) {
      return primitive(new Character().array(sz));
    }
  }
  class Short
  implements Supplier<java.lang.Short> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Short get() {
      return (short)r.nextInt(MOD);
    }
    public java.lang.Short get(int n) {
      return get();
    }
    public java.lang.Short[] array(int sz) {
      java.lang.Short[] result =
        new java.lang.Short[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pshort {
    public short[] array(int sz) {
      return primitive(new Short().array(sz));
    }
  }
  class Integer
  implements Supplier<java.lang.Integer> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Integer get() {
      return r.nextInt(MOD);
    }
    public java.lang.Integer get(int n) {
      return get();
    }
    public java.lang.Integer[] array(int sz) {
      int[] primitive = new Pint().array(sz);
      java.lang.Integer[] result =
        new java.lang.Integer[sz];
      for(int i = 0; i < sz; i++)
        result[i] = primitive[i];
      return result;
    }
  }
  class Pint implements IntSupplier {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public int getAsInt() {
      return r.nextInt(MOD);
    }
    public int get(int n) { return getAsInt(); }
    public int[] array(int sz) {
      return r.ints(sz, 0, MOD).toArray();
    }
  }
  class Long
  implements Supplier<java.lang.Long> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Long get() {
      return r.nextLong(MOD);
    }
    public java.lang.Long get(int n) {
      return get();
    }
    public java.lang.Long[] array(int sz) {
      long[] primitive = new Plong().array(sz);
      java.lang.Long[] result =
        new java.lang.Long[sz];
      for(int i = 0; i < sz; i++)
        result[i] = primitive[i];
      return result;
    }
  }
  class Plong implements LongSupplier {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public long getAsLong() {
      return r.nextLong(MOD);
    }
    public long get(int n) { return getAsLong(); }
    public long[] array(int sz) {
      return r.longs(sz, 0, MOD).toArray();
    }
  }
  class Float
  implements Supplier<java.lang.Float> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Float get() {
      return (float)trim(r.nextDouble());
    }
    public java.lang.Float get(int n) {
      return get();
    }
    public java.lang.Float[] array(int sz) {
      java.lang.Float[] result =
        new java.lang.Float[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
  class Pfloat {
    public float[] array(int sz) {
      return primitive(new Float().array(sz));
    }
  }
  static double trim(double d) {
    return
      ((double)Math.round(d * 1000.0)) / 100.0;
  }
  class Double
  implements Supplier<java.lang.Double> {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public java.lang.Double get() {
      return trim(r.nextDouble());
    }
    public java.lang.Double get(int n) {
      return get();
    }
    public java.lang.Double[] array(int sz) {
      double[] primitive =
        new Rand.Pdouble().array(sz);
      java.lang.Double[] result =
        new java.lang.Double[sz];
      for(int i = 0; i < sz; i++)
        result[i] = primitive[i];
      return result;
    }
  }
  class Pdouble implements DoubleSupplier {
    SplittableRandom r = new SplittableRandom(47);
    @Override
    public double getAsDouble() {
      return trim(r.nextDouble());
    }
    public double get(int n) {
      return getAsDouble();
    }
    public double[] array(int sz) {
      double[] result = r.doubles(sz).toArray();
      Arrays.setAll(result,
        n -> result[n] = trim(result[n]));
      return result;
    }
  }
  class String
  implements Supplier<java.lang.String> {
    SplittableRandom r = new SplittableRandom(47);
    private int strlen = 7; // Default length
    public String() {}
    public String(int strLength) {
      strlen = strLength;
    }
    @Override
    public java.lang.String get() {
      return r.ints(strlen, 'a', 'z' + 1)
        .collect(StringBuilder::new,
                 StringBuilder::appendCodePoint,
                 StringBuilder::append).toString();
    }
    public java.lang.String get(int n) {
      return get();
    }
    public java.lang.String[] array(int sz) {
      java.lang.String[] result =
        new java.lang.String[sz];
      Arrays.setAll(result, n -> get());
      return result;
    }
  }
}

```

对于除了 **int** 、 **long** 和 **double** 之外的所有基本类型元素生成器，只生成数组，而不是 Count 中看到的完整操作集。这只是一个设计选择，因为本书不需要额外的功能。

下面是对所有 **Rand** 工具的测试：

```java
// arrays/TestRand.java
// Test random generators
import java.util.*;
import java.util.stream.*;
import onjava.*;
import static onjava.ArrayShow.*;

public class TestRand {
  static final int SZ = 5;
  public static void main(String[] args) {
    System.out.println("Boolean");
    Boolean[] a1 = new Boolean[SZ];
    Arrays.setAll(a1, new Rand.Boolean()::get);
    show(a1);
    a1 = Stream.generate(new Rand.Boolean())
      .limit(SZ + 1).toArray(Boolean[]::new);
    show(a1);
    a1 = new Rand.Boolean().array(SZ + 2);
    show(a1);
    boolean[] a1b =
      new Rand.Pboolean().array(SZ + 3);
    show(a1b);

    System.out.println("Byte");
    Byte[] a2 = new Byte[SZ];
    Arrays.setAll(a2, new Rand.Byte()::get);
    show(a2);
    a2 = Stream.generate(new Rand.Byte())
      .limit(SZ + 1).toArray(Byte[]::new);
    show(a2);
    a2 = new Rand.Byte().array(SZ + 2);
    show(a2);
    byte[] a2b = new Rand.Pbyte().array(SZ + 3);
    show(a2b);

    System.out.println("Character");
    Character[] a3 = new Character[SZ];
    Arrays.setAll(a3, new Rand.Character()::get);
    show(a3);
    a3 = Stream.generate(new Rand.Character())
      .limit(SZ + 1).toArray(Character[]::new);
    show(a3);
    a3 = new Rand.Character().array(SZ + 2);
    show(a3);
    char[] a3b = new Rand.Pchar().array(SZ + 3);
    show(a3b);

    System.out.println("Short");
    Short[] a4 = new Short[SZ];
    Arrays.setAll(a4, new Rand.Short()::get);
    show(a4);
    a4 = Stream.generate(new Rand.Short())
      .limit(SZ + 1).toArray(Short[]::new);
    show(a4);
    a4 = new Rand.Short().array(SZ + 2);
    show(a4);
    short[] a4b = new Rand.Pshort().array(SZ + 3);
    show(a4b);

    System.out.println("Integer");
    int[] a5 = new int[SZ];
    Arrays.setAll(a5, new Rand.Integer()::get);
    show(a5);
    Integer[] a5b =
      Stream.generate(new Rand.Integer())
        .limit(SZ + 1).toArray(Integer[]::new);
    show(a5b);
    a5b = new Rand.Integer().array(SZ + 2);
    show(a5b);
    a5 = IntStream.generate(new Rand.Pint())
      .limit(SZ + 1).toArray();
    show(a5);
    a5 = new Rand.Pint().array(SZ + 3);
    show(a5);

    System.out.println("Long");
    long[] a6 = new long[SZ];
    Arrays.setAll(a6, new Rand.Long()::get);
    show(a6);
    Long[] a6b = Stream.generate(new Rand.Long())
      .limit(SZ + 1).toArray(Long[]::new);
    show(a6b);
    a6b = new Rand.Long().array(SZ + 2);
    show(a6b);
    a6 = LongStream.generate(new Rand.Plong())
      .limit(SZ + 1).toArray();
    show(a6);
    a6 = new Rand.Plong().array(SZ + 3);
    show(a6);

    System.out.println("Float");
    Float[] a7 = new Float[SZ];
    Arrays.setAll(a7, new Rand.Float()::get);
    show(a7);
    a7 = Stream.generate(new Rand.Float())
      .limit(SZ + 1).toArray(Float[]::new);
    show(a7);
    a7 = new Rand.Float().array(SZ + 2);
    show(a7);
    float[] a7b = new Rand.Pfloat().array(SZ + 3);
    show(a7b);

    System.out.println("Double");
    double[] a8 = new double[SZ];
    Arrays.setAll(a8, new Rand.Double()::get);
    show(a8);
    Double[] a8b =
      Stream.generate(new Rand.Double())
        .limit(SZ + 1).toArray(Double[]::new);
    show(a8b);
    a8b = new Rand.Double().array(SZ + 2);
    show(a8b);
    a8 = DoubleStream.generate(new Rand.Pdouble())
      .limit(SZ + 1).toArray();
    show(a8);
    a8 = new Rand.Pdouble().array(SZ + 3);
    show(a8);

    System.out.println("String");
    String[] s = new String[SZ - 1];
    Arrays.setAll(s, new Rand.String()::get);
    show(s);
    s = Stream.generate(new Rand.String())
      .limit(SZ).toArray(String[]::new);
    show(s);
    s = new Rand.String().array(SZ + 1);
    show(s);

    Arrays.setAll(s, new Rand.String(4)::get);
    show(s);
    s = Stream.generate(new Rand.String(4))
      .limit(SZ).toArray(String[]::new);
    show(s);
    s = new Rand.String(4).array(SZ + 1);
    show(s);
  }
}
/* Output:
Boolean
[true, false, true, true, true]
[true, false, true, true, true, false]
[true, false, true, true, true, false, false]
[true, false, true, true, true, false, false, true]
Byte
[123, 33, 101, 112, 33]
[123, 33, 101, 112, 33, 31]
[123, 33, 101, 112, 33, 31, 0]
[123, 33, 101, 112, 33, 31, 0, -72]
Character
[b, t, p, e, n]
[b, t, p, e, n, p]
[b, t, p, e, n, p, c]
[b, t, p, e, n, p, c, c]
Short
[635, 8737, 3941, 4720, 6177]
[635, 8737, 3941, 4720, 6177, 8479]
[635, 8737, 3941, 4720, 6177, 8479, 6656]
[635, 8737, 3941, 4720, 6177, 8479, 6656, 3768]
Integer
[635, 8737, 3941, 4720, 6177]
[635, 8737, 3941, 4720, 6177, 8479]
[635, 8737, 3941, 4720, 6177, 8479, 6656]
[635, 8737, 3941, 4720, 6177, 8479]
[635, 8737, 3941, 4720, 6177, 8479, 6656, 3768]
Long
[6882, 3765, 692, 9575, 4439]
[6882, 3765, 692, 9575, 4439, 2638]
[6882, 3765, 692, 9575, 4439, 2638, 4011]
[6882, 3765, 692, 9575, 4439, 2638]
[6882, 3765, 692, 9575, 4439, 2638, 4011, 9610]
Float
[4.83, 2.89, 2.9, 1.97, 3.01]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99, 8.28]
Double
[4.83, 2.89, 2.9, 1.97, 3.01]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99, 8.28]
String
[btpenpc, cuxszgv, gmeinne, eloztdv]
[btpenpc, cuxszgv, gmeinne, eloztdv, ewcippc]
[btpenpc, cuxszgv, gmeinne, eloztdv, ewcippc, ygpoalk]
[btpe, npcc, uxsz, gvgm, einn, eelo]
[btpe, npcc, uxsz, gvgm, einn]
[btpe, npcc, uxsz, gvgm, einn, eelo]
*/

```

注意（除了 **String** 部分之外），这段代码与 **TestCount.java** 中的代码相同，**Count** 被 **Rand** 替换。

<!-- Generics and Primitive Arrays -->
## 泛型和基本数组
在本章的前面，我们被提醒，泛型不能和基元一起工作。在这种情况下，我们必须从基元数组转换为包装类型的数组，并且还必须从另一个方向转换。下面是一个转换器可以同时对所有类型的数据执行操作：

```java
// onjava/ConvertTo.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package onjava;

public interface ConvertTo {
  static boolean[] primitive(Boolean[] in) {
    boolean[] result = new boolean[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i]; // Autounboxing
    return result;
  }
  static char[] primitive(Character[] in) {
    char[] result = new char[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static byte[] primitive(Byte[] in) {
    byte[] result = new byte[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static short[] primitive(Short[] in) {
    short[] result = new short[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static int[] primitive(Integer[] in) {
    int[] result = new int[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static long[] primitive(Long[] in) {
    long[] result = new long[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static float[] primitive(Float[] in) {
    float[] result = new float[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static double[] primitive(Double[] in) {
    double[] result = new double[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  // Convert from primitive array to wrapped array:
  static Boolean[] boxed(boolean[] in) {
    Boolean[] result = new Boolean[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i]; // Autoboxing
    return result;
  }
  static Character[] boxed(char[] in) {
    Character[] result = new Character[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static Byte[] boxed(byte[] in) {
    Byte[] result = new Byte[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static Short[] boxed(short[] in) {
    Short[] result = new Short[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static Integer[] boxed(int[] in) {
    Integer[] result = new Integer[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static Long[] boxed(long[] in) {
    Long[] result = new Long[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static Float[] boxed(float[] in) {
    Float[] result = new Float[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
  static Double[] boxed(double[] in) {
    Double[] result = new Double[in.length];
    for(int i = 0; i < in.length; i++)
      result[i] = in[i];
    return result;
  }
}
```

**primitive()** 的每个版本都创建一个准确长度的适当基元数组，然后从包装类的 **in** 数组中复制元素。如果任何包装的数组元素是 **null** ，你将得到一个异常（这是合理的—否则无法选择有意义的值进行替换)。注意在这个任务中自动装箱如何发生。

下面是对 **ConvertTo** 中所有方法的测试：

```java
// arrays/TestConvertTo.java
import java.util.*;
import onjava.*;
import static onjava.ArrayShow.*;
import static onjava.ConvertTo.*;

public class TestConvertTo {
  static final int SIZE = 6;
  public static void main(String[] args) {
    Boolean[] a1 = new Boolean[SIZE];
    Arrays.setAll(a1, new Rand.Boolean()::get);
    boolean[] a1p = primitive(a1);
    show("a1p", a1p);
    Boolean[] a1b = boxed(a1p);
    show("a1b", a1b);

    Byte[] a2 = new Byte[SIZE];
    Arrays.setAll(a2, new Rand.Byte()::get);
    byte[] a2p = primitive(a2);
    show("a2p", a2p);
    Byte[] a2b = boxed(a2p);
    show("a2b", a2b);

    Character[] a3 = new Character[SIZE];
    Arrays.setAll(a3, new Rand.Character()::get);
    char[] a3p = primitive(a3);
    show("a3p", a3p);
    Character[] a3b = boxed(a3p);
    show("a3b", a3b);

    Short[] a4 = new Short[SIZE];
    Arrays.setAll(a4, new Rand.Short()::get);
    short[] a4p = primitive(a4);
    show("a4p", a4p);
    Short[] a4b = boxed(a4p);
    show("a4b", a4b);

    Integer[] a5 = new Integer[SIZE];
    Arrays.setAll(a5, new Rand.Integer()::get);
    int[] a5p = primitive(a5);
    show("a5p", a5p);
    Integer[] a5b = boxed(a5p);
    show("a5b", a5b);

    Long[] a6 = new Long[SIZE];
    Arrays.setAll(a6, new Rand.Long()::get);
    long[] a6p = primitive(a6);
    show("a6p", a6p);
    Long[] a6b = boxed(a6p);
    show("a6b", a6b);

    Float[] a7 = new Float[SIZE];
    Arrays.setAll(a7, new Rand.Float()::get);
    float[] a7p = primitive(a7);
    show("a7p", a7p);
    Float[] a7b = boxed(a7p);
    show("a7b", a7b);

    Double[] a8 = new Double[SIZE];
    Arrays.setAll(a8, new Rand.Double()::get);
    double[] a8p = primitive(a8);
    show("a8p", a8p);
    Double[] a8b = boxed(a8p);
    show("a8b", a8b);
  }
}
/* Output:
a1p: [true, false, true, true, true, false]
a1b: [true, false, true, true, true, false]
a2p: [123, 33, 101, 112, 33, 31]
a2b: [123, 33, 101, 112, 33, 31]
a3p: [b, t, p, e, n, p]
a3b: [b, t, p, e, n, p]
a4p: [635, 8737, 3941, 4720, 6177, 8479]
a4b: [635, 8737, 3941, 4720, 6177, 8479]
a5p: [635, 8737, 3941, 4720, 6177, 8479]
a5b: [635, 8737, 3941, 4720, 6177, 8479]
a6p: [6882, 3765, 692, 9575, 4439, 2638]
a6b: [6882, 3765, 692, 9575, 4439, 2638]
a7p: [4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
a7b: [4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
a8p: [4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
a8b: [4.83, 2.89, 2.9, 1.97, 3.01, 0.18]
*/
```

在每种情况下，原始数组都是为包装类型创建的，并使用 **Arrays.setAll()** 填充，正如我们在 **TestCouner.java** 中所做的那样（这也验证了 **Arrays.setAll()** 是否能同 **Integer** ，**Long** ，和 **Double** ）。然后 **ConvertTo.primitive()**  将包装器数组转换为对应的基元数组，**ConverTo.boxed()** 将其转换回来。

<!-- Modifying Existing Array Elements -->
## 数组元素修改

传递给 **Arrays.setAll()** 的生成器函数可以使用它接收到的数组索引修改现有的数组元素:

```JAVA
// arrays/ModifyExisting.java

import java.util.*;
import onjava.*;
import static onjava.ArrayShow.*;

public class ModifyExisting {
    public static void main(String[] args) {
        double[] da = new double[7];
        Arrays.setAll(da, new Rand.Double()::get);
        show(da);
        Arrays.setAll(da, n -> da[n] / 100); // [1]
        show(da);

    }
}

/* Output:
[4.83, 2.89, 2.9, 1.97, 3.01, 0.18, 0.99]
[0.0483, 0.028900000000000002, 0.028999999999999998,
0.0197, 0.0301, 0.0018, 0.009899999999999999]
*/

```

[1] Lambdas在这里特别有用，因为数组总是在lambda表达式的范围内。


<!-- An Aside On Parallelism -->
## 数组并行

我们很快就不得不面对并行的主题。例如，“并行”一词在许多Java库方法中使用。您可能听说过类似“并行程序运行得更快”这样的说法，这是有道理的—当您可以有多个处理器时，为什么只有一个处理器在您的程序上工作呢? 如果您认为您应该利用其中的“并行”，这是很容易被原谅的。
要是这么简单就好了。不幸的是，通过采用这种方法，您可以很容易地编写比非并行版本运行速度更慢的代码。在你深刻理解所有的问题之前，并行编程看起来更像是一门艺术而非科学。
以下是简短的版本:用简单的方法编写代码。不要开始处理并行性，除非它成为一个问题。您仍然会遇到并行性。在本章中，我们将介绍一些为并行执行而编写的Java库方法。因此，您必须对它有足够的了解，以便进行基本的讨论，并避免出现错误。

在阅读并发编程这一章之后，您将更深入地理解它(但是，唉，这还远远不够。只是这些的话，充分理解这个主题是不可能的)。
在某些情况下，即使您只有一个处理器，无论您是否显式地尝试并行，并行实现是惟一的、最佳的或最符合逻辑的选择。它是一个可以一直使用的工具，所以您必须了解它的相关问题。

最好从数据的角度来考虑并行性。对于大量数据(以及可用的额外处理器)，并行可能会有所帮助。但您也可能使事情变得更糟。

在本书的其余部分，我们将遇到不同的情况:

- 1、所提供的惟一选项是并行的。这很简单，因为我们别无选择，只能使用它。这种情况是比较罕见的。

- 2、有多个选项，但是并行版本(通常是最新的版本)被设计成在任何地方都可以使用(甚至在那些不关心并行性的代码中)，如案例#1。我们将按预期使用并行版本。

- 3、案例1和案例2并不经常发生。相反，您将遇到某些算法的两个版本，一个用于并行使用，另一个用于正常使用。我将描述并行的一个，但不会在普通代码中使用它，因为它也许会产生所有可能的问题。

我建议您在自己的代码中采用这种方法。

[http://gee.cs.oswego.edu/dl/html/StreamParallelGuidance.html](要进一步了解为什么这是一个难题，请参阅Doug Lea的文章。)

**parallelSetAll()**

流式编程产生优雅的代码。例如，假设我们想要创建一个数值由从零开始填充的长数组：

```JAVA
// arrays/CountUpward.java

import java.util.stream.LongStream;

public class CountUpward {
    static long[] fillCounted(int size) {
        return LongStream.iterate(0, i -> i + 1).limit(size).toArray();
    }

    public static void main(String[] args) {
        long[] l1 = fillCounted(20); // No problem
        show(l1);
        // On my machine, this runs out of heap space:
        // - long[] l2 = fillCounted(10_000_000);
    }
}

/* Output:
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
16, 17, 18, 19]
*/
```

**流** 实际上可以存储到将近1000万，但是之后就会耗尽堆空间。常规的 **setAll()** 是有效的，但是如果我们能更快地处理如此大量的数字，那就更好了。
我们可以使用 **setAll()** 初始化更大的数组。如果速度成为一个问题，**Arrays.parallelSetAll()** 将(可能)更快地执行初始化(请记住并行性中描述的问题)。

```JAVA

// arrays/ParallelSetAll.java

import onjava.*;
import java.util.Arrays;

public class ParallelSetAll {
    static final int SIZE = 10_000_000;

    static void intArray() {
        int[] ia = new int[SIZE];
        Arrays.setAll(ia, new Rand.Pint()::get);
        Arrays.parallelSetAll(ia, new Rand.Pint()::get);
    }

    static void longArray() {
        long[] la = new long[SIZE];
        Arrays.setAll(la, new Rand.Plong()::get);
        Arrays.parallelSetAll(la, new Rand.Plong()::get);
    }

    public static void main(String[] args) {
        intArray();
        longArray();
    }
}
```

数组分配和初始化是在单独的方法中执行的，因为如果两个数组都在 **main()** 中分配，它会耗尽内存(至少在我的机器上是这样。还有一些方法可以告诉Java在启动时分配更多的内存)。



<!-- Arrays Utilities -->
## Arrays工具类

您已经看到了 **java.util.Arrays** 中的 **fill()** 和 **setAll()/parallelSetAll()** 。该类包含许多其他有用的 **静态** 程序方法，我们将对此进行研究。

概述:

- **asList()**: 获取任何序列或数组，并将其转换为一个 **列表集合** （集合章节介绍了此方法）。

- **copyOf()**：以新的长度创建现有数组的新副本。

- **copyOfRange()**：创建现有数组的一部分的新副本。

- **equals()**：比较两个数组是否相等。

- **deepEquals()**：多维数组的相等性比较。

- **stream()**：生成数组元素的流。

- **hashCode()**：生成数组的哈希值(您将在附录中了解这意味着什么:理解equals()和hashCode())。

- **deepHashCode()**: 多维数组的哈希值。

- **sort()**：排序数组

- **parallelSort()**：对数组进行并行排序，以提高速度。

- **binarySearch()**：在已排序的数组中查找元素。

- **parallelPrefix()**：使用提供的函数并行累积(以获得速度)。基本上，就是数组的reduce()。

- **spliterator()**：从数组中产生一个Spliterator;这是本书没有涉及到的流的高级部分。

- **toString()**：为数组生成一个字符串表示。你在整个章节中经常看到这种用法。

- **deepToString()**：为多维数组生成一个字符串。你在整个章节中经常看到这种用法。对于所有基本类型和对象，所有这些方法都是重载的。

<!-- Copying an Array -->
## 数组拷贝

与使用for循环手工执行复制相比，**copyOf()** 和 **copyOfRange()** 复制数组要快得多。这些方法被重载以处理所有类型。

我们从复制 **int** 和 **Integer** 数组开始:
```JAVA
// arrays/ArrayCopying.java
// Demonstrate Arrays.copy() and Arrays.copyOf()

import onjava.*;

import java.util.Arrays;

import static onjava.ArrayShow.*;

class Sup {
    // Superclass
    private int id;

    Sup(int n) {
        id = n;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + id;
    }
}

class Sub extends Sup { // Subclass

    Sub(int n) {
        super(n);
    }
}

public class ArrayCopying {
    public static final int SZ = 15;

    public static void main(String[] args) {
        int[] a1 = new int[SZ];
        Arrays.setAll(a1, new Count.Integer()::get);
        show("a1", a1);
        int[] a2 = Arrays.copyOf(a1, a1.length); // [1]
        // Prove they are distinct arrays:
        Arrays.fill(a1, 1);
        show("a1", a1);
        show("a2", a2);
        // Create a shorter result:
        a2 = Arrays.copyOf(a2, a2.length / 2); // [2]
        show("a2", a2);
        // Allocate more space:
        a2 = Arrays.copyOf(a2, a2.length + 5);
        show("a2", a2);
        // Also copies wrapped arrays:
        Integer[] a3 = new Integer[SZ]; // [3]
        Arrays.setAll(a3, new Count.Integer()::get);
        Integer[] a4 = Arrays.copyOfRange(a3, 4, 12);
        show("a4", a4);
        Sub[] d = new Sub[SZ / 2];
        Arrays.setAll(d, Sub::new); // Produce Sup[] from Sub[]:
        Sup[] b = Arrays.copyOf(d, d.length, Sup[].class); // [4]
        show(b); // This "downcast" works fine:
        Sub[] d2 = Arrays.copyOf(b, b.length, Sub[].class); // [5]
        show(d2); // Bad "downcast" compiles but throws exception:
        Sup[] b2 = new Sup[SZ / 2];
        Arrays.setAll(b2, Sup::new);
        try {
            Sub[] d3 = Arrays.copyOf(b2, b2.length, Sub[].class); // [6]
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
/* Output: a1: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14] a1: [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
           a2:[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]a2:[0, 1, 2, 3, 4, 5, 6]a2:[
           0, 1, 2, 3, 4, 5, 6, 0, 0, 0, 0, 0]a4:[4, 5, 6, 7, 8, 9, 10, 11][Sub0, Sub1, Sub2, Sub3, Sub4, Sub5, Sub6][
           Sub0, Sub1, Sub2, Sub3, Sub4, Sub5, Sub6]java.lang.ArrayStoreException */

```

[1] 这是复制的基本方法;只需给出返回的复制数组的大小。这对于编写需要调整存储大小的算法很有帮助。复制之后，我们把a1的所有元素都设为1，以证明a1的变化不会影响a2中的任何东西。

[2] 通过更改最后一个参数，我们可以缩短或延长返回的复制数组。

[3] **copyOf()** 和 **copyOfRange()** 也可以使用包装类型。**copyOfRange()** 需要一个开始和结束索引。

[4] **copyOf()** 和 **copyOfRange()** 都有一个版本，该版本通过在方法调用的末尾添加目标类型来创建不同类型的数组。我首先想到的是，这可能是一种从原生数组生成包装数组的方法，反之亦然。
但这没用。它的实际用途是“向上转换”和“向下转换”数组。也就是说，如果您有一个子类型(派生类型)的数组，而您想要一个基类型的数组，那么这些方法将生成所需的数组。

[5] 您甚至可以成功地“向下强制转换”，并从超类型的数组生成子类型的数组。这个版本运行良好，因为我们只是“upcast”。

[6] 这个“数组转换”将编译，但是如果类型不兼容，您将得到一个运行时异常。在这里，强制将基类型转换为派生类型是非法的，因为派生对象中可能有基对象中没有的属性和方法。

实例表明，原生数组和对象数组都可以被复制。但是，如果复制对象的数组，那么只复制引用—不复制对象本身。这称为浅拷贝(有关更多细节，请参阅附录:传递和返回对象)。

还有一个方法 **System.arraycopy()** ，它将一个数组复制到另一个已经分配的数组中。这将不会执行自动装箱或自动卸载—两个数组必须是完全相同的类型。

<!-- Comparing Arrays -->
## 数组比较

**数组** 提供了 **equals()** 来比较一维数组，以及 **deepEquals()** 来比较多维数组。对于所有原生类型和对象，这些方法都是重载的。

数组相等的含义：数组必须有相同数量的元素，并且每个元素必须与另一个数组中的对应元素相等，对每个元素使用 **equals()**(对于原生类型，使用原生类型的包装类的 **equals()** 方法;例如，int的Integer.equals()。

```JAVA
// arrays/ComparingArrays.java
// Using Arrays.equals()

import java.util.*;
import onjava.*;

public class ComparingArrays {
    public static final int SZ = 15;

    static String[][] twoDArray() {
        String[][] md = new String[5][];
        Arrays.setAll(md, n -> new String[n]);
        for (int i = 0; i < md.length; i++) Arrays.setAll(md[i], new Rand.String()::get);
        return md;
    }

    public static void main(String[] args) {
        int[] a1 = new int[SZ], a2 = new int[SZ];
        Arrays.setAll(a1, new Count.Integer()::get);
        Arrays.setAll(a2, new Count.Integer()::get);
        System.out.println("a1 == a2: " + Arrays.equals(a1, a2));
        a2[3] = 11;
        System.out.println("a1 == a2: " + Arrays.equals(a1, a2));
        Integer[] a1w = new Integer[SZ], a2w = new Integer[SZ];
        Arrays.setAll(a1w, new Count.Integer()::get);
        Arrays.setAll(a2w, new Count.Integer()::get);
        System.out.println("a1w == a2w: " + Arrays.equals(a1w, a2w));
        a2w[3] = 11;
        System.out.println("a1w == a2w: " + Arrays.equals(a1w, a2w));
        String[][] md1 = twoDArray(), md2 = twoDArray();
        System.out.println(Arrays.deepToString(md1));
        System.out.println("deepEquals(md1, md2): " + Arrays.deepEquals(md1, md2));
        System.out.println("md1 == md2: " + Arrays.equals(md1, md2));
        md1[4][1] = "#$#$#$#";
        System.out.println(Arrays.deepToString(md1));
        System.out.println("deepEquals(md1, md2): " + Arrays.deepEquals(md1, md2));
    }
}

/* Output:
a1 == a2: true
a1 == a2: false
a1w == a2w: true
a1w == a2w: false
[[], [btpenpc], [btpenpc, cuxszgv], [btpenpc, cuxszgv,
 gmeinne], [btpenpc, cuxszgv, gmeinne, eloztdv]]
 deepEquals(md1, md2): true
 md1 == md2: false
 [[], [btpenpc], [btpenpc, cuxszgv], [btpenpc, cuxszgv,
 gmeinne], [btpenpc, #$#$#$#, gmeinne, eloztdv]]
 deepEquals(md1, md2): false
 */
```

最初，a1和a2是完全相等的，所以输出是true，但是之后其中一个元素改变了，这使得结果为false。a1w和a2w是对一个封装类型数组重复该练习。

**md1** 和 **md2** 是通过 **twoDArray()** 以相同方式初始化的多维字符串数组。注意，**deepEquals()** 返回 **true**，因为它执行了适当的比较，而普通的 **equals()** 错误地返回 **false**。如果我们更改数组中的一个元素，**deepEquals()** 将检测它。

<!-- Streams and Arrays -->
## 流和数组

**stream()** 方法很容易从某些类型的数组中生成元素流。

```JAVA
// arrays/StreamFromArray.java

import java.util.*;
import onjava.*;

public class StreamFromArray {
    public static void main(String[] args) {
        String[] s = new Rand.String().array(10);
        Arrays.stream(s).skip(3).limit(5).map(ss -> ss + "!").forEach(System.out::println);
        int[] ia = new Rand.Pint().array(10);
        Arrays.stream(ia).skip(3).limit(5)
                .map(i -> i * 10).forEach(System.out::println);
        Arrays.stream(new long[10]);
        Arrays.stream(new double[10]);
        // Only int, long and double work:
        // - Arrays.stream(new boolean[10]);
        // - Arrays.stream(new byte[10]);
        // - Arrays.stream(new char[10]);
        // - Arrays.stream(new short[10]);
        // - Arrays.stream(new float[10]);
        // For the other types you must use wrapped arrays:
        float[] fa = new Rand.Pfloat().array(10);
        Arrays.stream(ConvertTo.boxed(fa));
        Arrays.stream(new Rand.Float().array(10));
    }
}
/* Output:
    eloztdv!
    ewcippc!
    ygpoalk!
    ljlbynx!
    taprwxz!
    47200
    61770
    84790
    66560
    37680
*/
```

只有“原生类型” **int**、**long** 和 **double** 可以与 **Arrays.stream()** 一起使用;对于其他的，您必须以某种方式获得一个包装类型的数组。

通常，将数组转换为流来生成所需的结果要比直接操作数组容易得多。请注意，即使流已经“用完”(您不能重复使用它)，您仍然拥有该数组，因此您可以以其他方式使用它----包括生成另一个流。

<!-- Sorting Arrays -->
## 数组排序

根据对象的实际类型执行比较排序。一种方法是为不同的类型编写对应的排序方法，但是这样的代码不能复用。

编程设计的一个主要目标是“将易变的元素与稳定的元素分开”，在这里，保持不变的代码是一般的排序算法，但是变化的是对象的比较方式。因此，使用策略设计模式而不是将比较代码放入许多不同的排序源码中。使用策略模式时，变化的代码部分被封装在一个单独的类(策略对象)中。

您将一个策略对象交给相同的代码，该代码使用策略模式来实现其算法。通过这种方式，您将使用相同的排序代码，使不同的对象表达不同的比较方式。

Java有两种方式提供比较功能。第一种方法是通过实现 **java.lang.Comparable** 接口的原生方法。这是一个简单的接口，只含有一个方法 **compareTo()**。该方法接受另一个与参数类型相同的对象作为参数，如果当前对象小于参数，则产生一个负值;如果参数相等，则产生零值;如果当前对象大于参数，则产生一个正值。

这里有一个类，它实现了 **Comparable** 接口并演示了可比性，而且使用Java标准库方法 **Arrays.sort()**:

```JAVA
// arrays/CompType.java
// Implementing Comparable in a class

import onjava.*;

import java.util.Arrays;
import java.util.SplittableRandom;

import static onjava.ArrayShow.*;

public class CompType implements Comparable<CompType> {
    private static int count = 1;
    private static SplittableRandom r = new SplittableRandom(47);
    int i;
    int j;

    public CompType(int n1, int n2) {
        i = n1;
        j = n2;
    }

    public static CompType get() {
        return new CompType(r.nextInt(100), r.nextInt(100));
    }

    public static void main(String[] args) {
        CompType[] a = new CompType[12];
        Arrays.setAll(a, n -> get());
        show("Before sorting", a);
        Arrays.sort(a);
        show("After sorting", a);
    }

    @Override
    public String toString() {
        String result = "[i = " + i + ", j = " + j + "]";
        if (count++ % 3 == 0) result += "\n";
        return result;
    }

    @Override
    public int compareTo(CompType rv) {
        return (i < rv.i ? -1 : (i == rv.i ? 0 : 1));
    }
}
/* Output:
Before sorting: [[i = 35, j = 37], [i = 41, j = 20], [i = 77, j = 79] ,
                [i = 56, j = 68], [i = 48, j = 93],
                [i = 70, j = 7] , [i = 0, j = 25],
                [i = 62, j = 34], [i = 50, j = 82] ,
                [i = 31, j = 67], [i = 66, j = 54],
                [i = 21, j = 6] ]
After sorting: [[i = 0, j = 25], [i = 21, j = 6], [i = 31, j = 67] ,
               [i = 35, j = 37], [i = 41, j = 20], [i = 48, j = 93] ,
               [i = 50, j = 82], [i = 56, j = 68], [i = 62, j = 34] ,
               [i = 66, j = 54], [i = 70, j = 7], [i = 77, j = 79] ]
*/
```

当您定义比较方法时，您有责任决定将一个对象与另一个对象进行比较意味着什么。这里，在比较中只使用i值和j值
将被忽略。

**get()** 方法通过使用随机值初始化CompType对象来构建它们。在 **main()** 中，**get()** 与 **Arrays.setAll()** 一起使用，以填充一个 **CompType类型** 数组，然后对其排序。如果没有实现 **Comparable接口**，那么当您试图调用 **sort()** 时，您将在运行时获得一个 **ClassCastException** 。这是因为 **sort()** 将其参数转换为 **Comparable类型**。

现在假设有人给了你一个没有实现 **Comparable接口** 的类，或者给了你一个实现 **Comparable接口** 的类，但是你不喜欢它的工作方式而愿意有一个不同的对于此类型的比较方法。为了解决这个问题，创建一个实现 **Comparator** 接口的单独的类(在集合一章中简要介绍)。它有两个方法，**compare()** 和 **equals()**。但是，除了特殊的性能需求外，您不需要实现 **equals()**，因为无论何时创建一个类，它都是隐式地继承自 **Object**，**Object** 有一个equals()。您可以只使用默认的 **Object equals()** 来满足接口的规范。

集合类(注意复数;我们将在下一章节讨论它) 包含一个方法 **reverseOrder()**，它生成一个来 **Comparator**（比较器）反转自然排序顺序。这可以应用到比较对象：

```JAVA
// arrays/Reverse.java
// The Collections.reverseOrder() Comparator

import onjava.*;

import java.util.Arrays;
import java.util.Collections;

import static onjava.ArrayShow.*;

public class Reverse {
    public static void main(String[] args) {
        CompType[] a = new CompType[12];
        Arrays.setAll(a, n -> CompType.get());
        show("Before sorting", a);
        Arrays.sort(a, Collections.reverseOrder());
        show("After sorting", a);
    }
}
/* Output:
Before sorting: [[i = 35, j = 37], [i = 41, j = 20],
                [i = 77, j = 79] , [i = 56, j = 68],
                [i = 48, j = 93], [i = 70, j = 7],
                [i = 0, j = 25], [i = 62, j = 34],
                [i = 50, j = 82] , [i = 31, j = 67],
                [i = 66, j = 54], [i = 21, j = 6] ]
After sorting: [[i = 77, j = 79], [i = 70, j = 7],
                [i = 66, j = 54] , [i = 62, j = 34],
                [i = 56, j = 68], [i = 50, j = 82] ,
                [i = 48, j = 93], [i = 41, j = 20],
                [i = 35, j = 37] , [i = 31, j = 67],
                [i = 21, j = 6], [i = 0, j = 25] ]
*/
```

您还可以编写自己的比较器。这个比较CompType对象基于它们的j值而不是它们的i值:

```JAVA
// arrays/ComparatorTest.java
// Implementing a Comparator for a class

import onjava.*;

import java.util.Arrays;
import java.util.Comparator;

import static onjava.ArrayShow.*;

class CompTypeComparator implements Comparator<CompType> {
    public int compare(CompType o1, CompType o2) {
        return (o1.j < o2.j ? -1 : (o1.j == o2.j ? 0 : 1));
    }
}

public class ComparatorTest {
    public static void main(String[] args) {
        CompType[] a = new CompType[12];
        Arrays.setAll(a, n -> CompType.get());
        show("Before sorting", a);
        Arrays.sort(a, new CompTypeComparator());
        show("After sorting", a);
    }
}
/* Output:
Before sorting:[[i = 35, j = 37], [i = 41, j = 20], [i = 77, j = 79] ,
                [i = 56, j = 68], [i = 48, j = 93], [i = 70, j = 7] ,
                [i = 0, j = 25], [i = 62, j = 34], [i = 50, j = 82],
                [i = 31, j = 67], [i = 66, j = 54], [i = 21, j = 6] ]
After sorting: [[i = 21, j = 6], [i = 70, j = 7], [i = 41, j = 20] ,
                [i = 0, j = 25], [i = 62, j = 34], [i = 35, j = 37] ,
                [i = 66, j = 54], [i = 31, j = 67], [i = 56, j = 68] ,
                [i = 77, j = 79], [i = 50, j = 82], [i = 48, j = 93] ]
*/
```

<!-- Using Arrays.sort() -->
## Arrays.sort()的使用

使用内置的排序方法，您可以对实现了 **Comparable** 接口或具有 **Comparator** 的任何对象数组 或 任何原生数组进行排序。这里我们生成一个随机字符串对象数组并对其排序:

```JAVA
// arrays/StringSorting.java
// Sorting an array of Strings

import onjava.*;

import java.util.Arrays;
import java.util.Collections;

import static onjava.ArrayShow.*;

public class StringSorting {
    public static void main(String[] args) {
        String[] sa = new Rand.String().array(20);
        show("Before sort", sa);
        Arrays.sort(sa);
        show("After sort", sa);
        Arrays.sort(sa, Collections.reverseOrder());
        show("Reverse sort", sa);
        Arrays.sort(sa, String.CASE_INSENSITIVE_ORDER);
        show("Case-insensitive sort", sa);
    }
}
/* Output:
Before sort: [btpenpc, cuxszgv, gmeinne, eloztdv, ewcippc,
            ygpoalk, ljlbynx, taprwxz, bhmupju, cjwzmmr,
            anmkkyh, fcjpthl, skddcat, jbvlgwc, mvducuj,
            ydpulcq, zehpfmm, zrxmclh, qgekgly, hyoubzl]

After sort: [anmkkyh, bhmupju, btpenpc, cjwzmmr, cuxszgv,
            eloztdv, ewcippc, fcjpthl, gmeinne, hyoubzl,
            jbvlgwc, ljlbynx, mvducuj, qgekgly, skddcat,
            taprwxz, ydpulcq, ygpoalk, zehpfmm, zrxmclh]

Reverse sort: [zrxmclh, zehpfmm, ygpoalk, ydpulcq,taprwxz,
            skddcat, qgekgly, mvducuj, ljlbynx, jbvlgwc,
            hyoubzl, gmeinne, fcjpthl, ewcippc, eloztdv,
            cuxszgv, cjwzmmr, btpenpc, bhmupju, anmkkyh]

Case-insensitive sort: [anmkkyh, bhmupju, btpenpc, cjwzmmr,
                cuxszgv, eloztdv, ewcippc, fcjpthl, gmeinne,
                hyoubzl, jbvlgwc, ljlbynx, mvducuj, qgekgly,
                skddcat, taprwxz, ydpulcq, ygpoalk, zehpfmm, zrxmclh]
*/
```

注意字符串排序算法中的输出。它是字典式的，所以它把所有以大写字母开头的单词放在前面，然后是所有以小写字母开头的单词。(电话簿通常是这样分类的。)无论大小写，要将单词组合在一起，请使用 **String.CASE_INSENSITIVE_ORDER** ，如对sort()的最后一次调用所示。

Java标准库中使用的排序算法被设计为最适合您正在排序的类型----原生类型的快速排序和对象的归并排序。


<!-- Sorting in Parallel -->
## 并行排序

如果排序性能是一个问题，那么可以使用 **Java 8 parallelSort()**，它为所有不可预见的情况(包括数组的排序区域或使用了比较器)提供了重载版本。为了查看相比于普通的sort(), **parallelSort()** 的优点，我们使用了用来验证代码时的 **JMH**：

```java
// arrays/jmh/ParallelSort.java
package arrays.jmh;

import onjava.*;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;

@State(Scope.Thread)
public class ParallelSort {
    private long[] la;

    @Setup
    public void setup() {
        la = new Rand.Plong().array(100_000);
    }

    @Benchmark
    public void sort() {
        Arrays.sort(la);
    }

    @Benchmark
    public void parallelSort() {
        Arrays.parallelSort(la);
    }
}
```

**parallelSort()** 算法将大数组拆分成更小的数组，直到数组大小达到极限，然后使用普通的 **Arrays .sort()** 方法。然后合并结果。该算法需要不大于原始数组的额外工作空间。

您可能会看到不同的结果，但是在我的机器上，并行排序将速度提高了大约3倍。由于并行版本使用起来很简单，所以很容易考虑在任何地方使用它，而不是
**Arrays.sort ()**。当然，它可能不是那么简单—看看微基准测试。


<!-- Searching with Arrays.binarySearch() -->
## binarySearch二分查找

一旦数组被排序，您就可以通过使用 **Arrays.binarySearch()** 来执行对特定项的快速搜索。但是，如果尝试在未排序的数组上使用 **binarySearch()**，结果是不可预测的。下面的示例使用 **Rand.Pint** 类来创建一个填充随机整形值的数组，然后调用 **getAsInt()** (因为 **Rand.Pint** 是一个 **IntSupplier**)来产生搜索值:

```JAVA
// arrays/ArraySearching.java
// Using Arrays.binarySearch()

import onjava.*;

import java.util.Arrays;

import static onjava.ArrayShow.*;

public class ArraySearching {
    public static void main(String[] args) {
        Rand.Pint rand = new Rand.Pint();
        int[] a = new Rand.Pint().array(25);
        Arrays.sort(a);
        show("Sorted array", a);
        while (true) {
            int r = rand.getAsInt();
            int location = Arrays.binarySearch(a, r);
            if (location >= 0) {
                System.out.println("Location of " + r + " is " + location + ", a[" + location + "] is " + a[location]);
                break; // Out of while loop
            }
        }
    }
}
/* Output:
Sorted array: [125, 267, 635, 650, 1131, 1506, 1634, 2400, 2766,
               3063, 3768, 3941, 4720, 4762, 4948, 5070, 5682,
               5807, 6177, 6193, 6656, 7021, 8479, 8737, 9954]
Location of 635 is 2, a[2] is 635
*/
```

在while循环中，随机值作为搜索项生成，直到在数组中找到其中一个为止。

如果找到了搜索项，**Arrays.binarySearch()** 将生成一个大于或等于零的值。否则，它将产生一个负值，表示如果手动维护已排序的数组，则应该插入元素的位置。产生的值是 -(插入点) - 1 。插入点是大于键的第一个元素的索引，如果数组中的所有元素都小于指定的键，则是 **a.size()** 。

如果数组包含重复的元素，则无法保证找到其中的那些重复项。搜索算法不是为了支持重复的元素，而是为了容忍它们。如果需要没有重复元素的排序列表，可以使用 **TreeSet** (用于维持排序顺序)或 **LinkedHashSet** (用于维持插入顺序)。这些类自动为您处理所有的细节。只有在出现性能瓶颈的情况下，才应该使用手工维护的数组替换这些类中的一个。

如果使用比较器(原语数组不允许使用比较器进行排序)对对象数组进行排序，那么在执行 **binarySearch()** (使用重载版本的binarySearch())时必须包含相同的比较器。例如，可以修改 **StringSorting.java** 来执行搜索:

```JAVA
// arrays/AlphabeticSearch.java
// Searching with a Comparator

import onjava.*;

import java.util.Arrays;

import static onjava.ArrayShow.*;

public class AlphabeticSearch {
    public static void main(String[] args) {
        String[] sa = new Rand.String().array(30);
        Arrays.sort(sa, String.CASE_INSENSITIVE_ORDER);
        show(sa);
        int index = Arrays.binarySearch(sa, sa[10], String.CASE_INSENSITIVE_ORDER);
        System.out.println("Index: " + index + "\n" + sa[index]);
    }
}
/* Output:
[anmkkyh, bhmupju, btpenpc, cjwzmmr, cuxszgv, eloztdv, ewcippc,
ezdeklu, fcjpthl, fqmlgsh, gmeinne, hyoubzl, jbvlgwc, jlxpqds,
ljlbynx, mvducuj, qgekgly, skddcat, taprwxz, uybypgp, vjsszkn,
vniyapk, vqqakbm, vwodhcf, ydpulcq, ygpoalk, yskvett, zehpfmm,
zofmmvm, zrxmclh]
Index: 10 gmeinne
*/
```
比较器必须作为第三个参数传递给重载的 **binarySearch()** 。在本例中，成功是有保证的，因为搜索项是从数组本身中选择的。

<!-- Accumulating with parallelPrefix() -->
## parallelPrefix并行前缀

没有“prefix()”方法，只有 **parallelPrefix()**。这类似于 **Stream** 类中的 **reduce()** 方法:它对前一个元素和当前元素执行一个操作，并将结果放入当前元素位置:

```JAVA
// arrays/ParallelPrefix1.java

import onjava.*;

import java.util.Arrays;

import static onjava.ArrayShow.*;

public class ParallelPrefix1 {
    public static void main(String[] args) {
        int[] nums = new Count.Pint().array(10);
        show(nums);
        System.out.println(Arrays.stream(nums).reduce(Integer::sum).getAsInt());
        Arrays.parallelPrefix(nums, Integer::sum);
        show(nums);
        System.out.println(Arrays.stream(new Count.Pint().array(6)).reduce(Integer::sum).getAsInt());
    }
}
/* Output:
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
45
[0, 1, 3, 6, 10, 15, 21, 28, 36, 45]
15
*/
```

这里我们对数组应用Integer::sum。在位置0中，它将先前计算的值(因为没有先前的值)与原始数组位置0中的值组合在一起。在位置1中，它获取之前计算的值(它只是存储在位置0中)，并将其与位置1中先前计算的值相结合。依次往复。

使用 **Stream.reduce()**，您只能得到最终结果，而使用 **Arrays.parallelPrefix()**，您还可以得到所有中间计算，以确保它们是有用的。注意，第二个 **Stream.reduce()** 计算的结果已经在 **parallelPrefix()** 计算的数组中。

使用字符串可能更清楚:

```JAVA
// arrays/ParallelPrefix2.java

import onjava.*;

import java.util.Arrays;

import static onjava.ArrayShow.*;

public class ParallelPrefix2 {
    public static void main(String[] args) {
        String[] strings = new Rand.String(1).array(8);
        show(strings);
        Arrays.parallelPrefix(strings, (a, b) -> a + b);
        show(strings);
    }
}
/* Output:
[b, t, p, e, n, p, c, c]
[b, bt, btp, btpe, btpen, btpenp, btpenpc, btpenpcc]
*/
```

如前所述，使用流进行初始化非常优雅，但是对于大型数组，这种方法可能会耗尽堆空间。使用 **setAll()** 执行初始化更节省内存:

```JAVA
// arrays/ParallelPrefix3.java
// {ExcludeFromTravisCI}

import java.util.Arrays;

public class ParallelPrefix3 {
    static final int SIZE = 10_000_000;

    public static void main(String[] args) {
        long[] nums = new long[SIZE];
        Arrays.setAll(nums, n -> n);
        Arrays.parallelPrefix(nums, Long::sum);
        System.out.println("First 20: " + nums[19]);
        System.out.println("First 200: " + nums[199]);
        System.out.println("All: " + nums[nums.length - 1]);
    }
}
/* Output:
First 20: 190
First 200: 19900
All: 49999995000000
*/
```
因为正确使用 **parallelPrefix()** 可能相当复杂，所以通常应该只在存在内存或速度问题(或两者都有)时使用。否则，**Stream.reduce()** 应该是您的首选。


<!-- Summary -->
## 本章小结

Java为固定大小的低级数组提供了合理的支持。这种数组强调的是性能而不是灵活性，就像C和c++数组模型一样。在Java的最初版本中，固定大小的低级数组是绝对必要的，这不仅是因为Java设计人员选择包含原生类型(也考虑到性能)，还因为那个版本对集合的支持非常少。因此，在早期的Java版本中，选择数组总是合理的。

在Java的后续版本中，集合支持得到了显著的改进，现在集合在除性能外的所有方面都优于数组，即使这样，集合的性能也得到了显著的改进。正如本书其他部分所述，无论如何，性能问题通常不会出现在您设想的地方。

使用自动装箱和泛型，在集合中保存原生类型是毫不费力的，这进一步鼓励您用集合替换低级数组。由于泛型产生类型安全的集合，数组在这方面也不再有优势。

如本章所述，当您尝试使用泛型时，您将看到泛型对数组是相当不友好的。通常，即使可以让泛型和数组以某种形式一起工作(在下一章中您将看到)，在编译期间仍然会出现“unchecked”警告。

有几次，当我们讨论特定的例子时，我直接从Java语言设计人员那里听到我应该使用集合而不是数组(我使用数组来演示特定的技术，所以我没有这个选项)。

所有这些问题都表明，在使用Java的最新版本进行编程时，应该“优先选择集合而不是数组”。只有当您证明性能是一个问题(并且切换到一个数组实际上会有很大的不同)时，才应该重构到数组。这是一个相当大胆的声明，但是有些语言根本没有固定大小的低级数组。它们只有可调整大小的集合，而且比C/C++/java风格的数组功能多得多。例如，Python有一个使用基本数组语法的列表类型，但是具有更大的功能—您甚至可以从它继承:

```Python
# arrays/PythonLists.py

aList=[1,2,3,4,5]print(type(aList)) #<type 'list'>
print(aList) # [1,2,3,4,5]
        print(aList[4]) # 5Basic list indexing
        aList.append(6) # lists can be resized
        aList+=[7,8] # Add a list to a list
        print(aList) # [1,2,3,4,5,6,7,8]
        aSlice=aList[2:4]
        print(aSlice) # [3,4]

class MyList(list): # Inherit from list
        # Define a method;'this'pointer is explicit:
        def getReversed(self):
            reversed=self[:] # Copy list using slices
            reversed.reverse() # Built-in list method
            return reversed
        # No'new'necessary for object creation:
        list2=MyList(aList)
        print(type(list2)) #<class '__main__.MyList'>
        print(list2.getReversed()) # [8,7,6,5,4,3,2,1]
        output="""
        <class 'list'>
        [1, 2, 3, 4, 5]
        5
        [1, 2, 3, 4, 5, 6, 7, 8]
        [3, 4]
        <class '__main__.MyList'>
        [8, 7, 6, 5, 4, 3, 2, 1]
        """

```
前一章介绍了基本的Python语法。在这里，通过用方括号包围以逗号分隔的对象序列来创建列表。结果是一个运行时类型为list的对象(print语句的输出显示为同一行中的注释)。打印列表的结果与在Java中使用Arrays.toString()的结果相同。
通过将 : 操作符放在索引操作中，通过切片来创建列表的子序列。list类型有更多的内置操作，通常只需要序列类型。
MyList是一个类定义;基类放在括号内。在类内部，def语句生成方法，该方法的第一个参数在Java中自动与之等价，除了在Python中它是显式的，而且标识符self是按约定使用的(它不是关键字)。注意构造函数是如何自动继承的。

虽然一切在Python中真的是一个对象(包括整数和浮点类型),你仍然有一个安全门,因为你可以优化性能关键型的部分代码编写扩展的C, c++或使用特殊的工具设计容易加速您的Python代码(有很多)。通过这种方式，可以在不影响性能改进的情况下保持对象的纯度。

PHP甚至更进一步，它只有一个数组类型，既充当int索引数组，又充当关联数组(Map)。

在经历了这么多年的Java发展之后，我们可以很有趣地推测，如果重新开始，设计人员是否会将原生类型和低级数组放在该语言中(同样在JVM上运行的Scala语言不包括这些)。如果不考虑这些，就有可能开发出一种真正纯粹的面向对象语言(尽管有这样的说法，Java并不是一种纯粹的面向对象语言，这正是因为它的底层缺陷)。关于效率的最初争论总是令人信服的，但是随着时间的推移，我们已经看到了从这个想法向更高层次的组件(如集合)的演进。此外，如果集合可以像在某些语言中一样构建到核心语言中，那么编译器就有更好的机会进行优化。

撇开““Green-fields”的推测不谈，我们肯定会被数组所困扰，当你阅读代码时就会看到它们。然而，集合几乎总是更好的选择。


<!-- 分页 -->

<div style="page-break-after: always;"></div>
