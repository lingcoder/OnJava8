[TOC]

<!-- Arrays -->
# 第二十一章 数组


> 在[初始化和清理](/book/06-Housekeeping.md)一章的最后，你已经学过如何定义和初始化一个数组。

简单来看，数组需要你去创建和初始化，你可以通过下标对数组元素进行访问，数组的大小不会改变。大多数时候你只需要知道这些，但有时候你必须在数组上进行更复杂的操作，你也可能需要在数组和更加灵活的 **集合** (Collection)之间做出评估。因此本章我们将对数组进行更加深入的分析。

**注意：** 随着Java Collection 和 Stream 类中高级功能的不断增加，日常编程中使用数组的需求也在变少，所以你暂且可以放心的略读甚至跳过这一章。但是，即使你自己避免使用数组，也总会有需要阅读别人数组代码的那一天。那时候，本章依然在这里等着你来翻阅。



<!-- Why Arrays are Special -->
## 数组特性

明明还有很多其他的办法来保存对象，那么是什么令数组如此特别？

将数组和其他类型的集合区分开来的原因有三：效率，类型，保存基本数据的能力。在Java中，使用数组存储和随机访问对象引用序列是非常高效的。数组是简单的线性序列，这使得对元素的访问变得非常快。然而这种高速也是有代价的，代价就是数组对象的大小是固定的，且在该数组的生存期内不能更改。

速度通常并不是问题，如果有问题，你保存和检索对象的方式也很少是罪魁祸首。你应该总是从 **ArrayList** (来自 [集合]( ))开始，它将数组封装起来。必要时，它会自动分配更多的数组空间，创建新数组，并将旧数组中的引用移动到新数组。这种灵活性需要开销，所以一个**ArrayList**的效率不如数组。在极少的情况下效率会成为问题，所以这种时候你可以直接使用数组。


数组和集合(Collections)都不能滥用。不管你使用数组还是集合，如果你越界，你都会得到一个 **RuntimeException** 的异常提醒，这表明你的程序中存在错误。


在泛型前，其他的集合类以一种宽泛的方式处理对象（就好像它们没有特定类型一样）。事实上，这些集合类把保存对象的类型默认为 **Object** ，也就Java中所有类的基类。而数组是优于 **预泛型** (pre-generic)集合类的，因为你创建一个数组就可以保存特定类型的数据。这意味着你获得了一个编译时的类型检查，而这可以防止你插入错误的数据类型，或者搞错你正在提取的数据类型。


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

**Suppliers.create()**  方法在[泛型](/book/20-Generics.md)中被定义。上面两种保存对象的方式都是有类型检查的，唯一比较明显的区别就是数组使用[ ] 来随机存取元素，而一个List 使用诸如add()和get()等方法。数组和ArrayList之间的相似是设计者有意为之，所以在概念上，两者很容易切换。但是就像你在[集合](book/12-Collections.md)中看到的，集合的功能明显多于数组。随着Java自动装箱技术的出现，通过集合使用基本数据类型几乎和通过数组一样简单。数组唯一剩下的优势就是效率。然而，当你解决一个更加普遍的问题时，数组可能限制太多，这种情形下，您可以使用集合类。


### 用于显示数组的实用程序

在本章中，我们处处都要显示数组。Java提供了 **array.toString()** 来将数组转换为可读字符串，然后可以在控制台上显示。然而这种方式视觉上噪音太大，所以我们创建一个小的库来完成这项工作。

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

为了简单起见，你通常可以静态的导入它们。


<!-- Arrays are First-Class Objects -->
## 一等对象

不管你使用的什么类型的数组，数组中的数据集实际上都是对堆中真正对象的引用。数组是保存指向其他对象的引用的对象，数组可以隐式地地创建，作为数组初始化语法的一部分，也可以显式地创建，比如使用一个 **new** 关键字。数组对象的一部分（事实上，你唯一可以使用的方法）就是只读的 **length** 成员函数，它能告诉你数组对象中可以存储多少元素。**[ ]** 语法是你访问数组对象的唯一方式。

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



数组 **a** 是一个未初始化的本地变量，编译器不会允许你使用这个引用直到你正确地对其进行初始化。数组 **b** 被初始化成一系列指向 **BerylliumSphere**  对象的引用，但是并没有真正的 **BerylliumSphere** 对象被存储在数组中。尽管你仍然可以获得这个数组的大小，因为 **b** 指向合法对象。这带来了一个小问题：你无法找出到底有多少元素存储在数组中，因为 **length** 只能告诉你数组可以存储多少元素；这就是说，数组对象的大小并不是真正存储在数组中对象的个数。然而，当你创建一个对象数组，其引用将自动初始化为 **null** ，因此你可以通过检查特定数组元素中的引用是否为 **null** 来判断其中是否有对象。基元数组也有类似的机制，比如自动将数值类型初始化为 **0** ，char型初始化为 **(char)0** ，布尔类型初始化为 **false** 。

在给数组中各元素分配 **BerylliumSphere** 对象后，数组 **c** 展示数组对象的创建。数组 **d** 展示了创建数组对象的聚合初始化语法（隐式地使用new在堆中创建对象，就像 **c** 一样）并且初始化成 **BeryliumSphere** 对象，这一切都在一条语句中完成。

下一个数组初始化可以被看做是一个“动态聚合初始化”。 **d** 使用的聚合初始化必须在 **d** 定义的点使用，但是使用第二种语法，你可以在任何地方创建和初始化数组对象。例如，假设 **hide()** 是一个需要使用一系列的 **BeryliumSphere**对象。你可以这样调用它：

```Java
hide(d);
```

你也可以动态的创建你用作参数传递的数组：

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

假设你写了一个方法，这个方法不是返回一个元素，而是返回多个元素。对C++/C这样的语言来说这是很困难的，因为你无法返回一个数组，只能是返回一个指向数组的指针。这会带来一些问题，因为对数组生存期的控制变得很混乱，这会导致内存泄露。

而在Java中，你只需返回数组，你永远不用为数组担心，只要你需要它，它就可用，垃圾收集器会在你用完后把它清理干净。

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



**flaverset()** 创建名为 **results** 的 **String** 类型的数组。 这个数组的大小 **n** 取决于你传进方法的参数。然后选择从数组 **FLAVORS** 中随机选择flavors并且把它们放进 **results** 里并返回。返回一个数组就像返回其他任何的对象一样，实际上返回的是引用。数组是在 **flavorSet()** 中或者在其他的什么地方创建的并不重要。垃圾收集器会清理你用完的数组，你需要的数组则会保留。

如果你必须要返回一系列不同类型的元素，你可以使用 [泛型](book/generics) 中介绍的 **元组** 。

注意，当 **flavorSet()** 随机选择 flavors，它应该确保某个特定的选项被选中。这在一个 **do** 循环中执行，它将一直做出随机选择直到它发现一个元素不在 **picked** 数组中。（一个字符串

比较将显示出随机选中的元素是不是已经存在于 **results** 数组中）。如果成功了，它将添加条目并且寻找下一个（ **i** 递增）。输出结果显示 **flvorSet()** 每一次都是按照随机顺序选择 flavors。

直到书中的这个点，随机数通过 **java.util.Random** 类生成的，这个类从Java 1.0就有，甚至被更新以提供Java 8 流。现在我们可以介绍Java 8中的 **SplittableRandom** ,它不只是以线性操作工作（你最终会学到），还提供了一个高质量的随机数。我们将在这本书的后面部分使用  **SplittableRandom**  。

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

这个例子使用 **array.deepToString（）** 方法，这将多维数组转换成 **String**  类型，就像在输出中显示的那样。

你也可以使用 **new** 分配数组。 这是一个使用 **new** 表达式分配的三维数组：

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

第一个 **new** 创建了一个数组，这个数组首元素长度随机，其余的则不确定。第二个在for循环中的 **new**  给数组填充了元素，第三个 **new**  为数组的最后一个索引填充元素。

* **[1]** Java 8 增加了 **Arrays.setAll()**  方法,其使用生成器来生成插入数组中的值。此生成器符合函数接口 **IntunaryOperator** ，只使用一个非 **默认** 的方法 **ApplyAsint(int操作数)** 。 **Arrays.setAll（）** 传递当前数组索引作为操作数，因此一个选项是提供 **n -> n** 的lambda表达式来显示数组的索引（在上面的代码中很容易尝试）。这里，我们忽略索引，只是插入递增计数器的值。

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

**i  * j** 在这里只是为了向 **Integer** 中添加有趣的值。

**Arrays.deepToString（）** 方法同时适用于基元数组和对象数组：

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

同样的，在 **Integer** 和 **Double** 数组中，自动装箱为可为你创建包装器对象。

<!-- Arrays and Generics -->
## 泛型数组


<!-- Arrays.fill() -->
## Arrays的fill方法


<!-- Arrays.setAll() -->
## Arrays的setAll方法


<!-- Incremental Generators -->
## 增量生成


<!-- Random Generators -->
## 随机生成


<!-- Generics and Primitive Arrays -->
## 泛型和基本数组


<!-- Modifying Existing Array Elements -->
## 数组元素修改


<!-- An Aside On Parallelism -->
## 数组并行


<!-- Arrays Utilities -->
## Arrays工具类


<!-- Copying an Array -->
## 数组拷贝


<!-- Comparing Arrays -->
## 数组比较


<!-- Streams and Arrays -->
## 流和数组


<!-- Sorting Arrays -->
## 数组排序


<!-- Searching with Arrays.binarySearch() -->
## binarySearch二分查找


<!-- Accumulating with parallelPrefix() -->
## parallelPrefix并行前缀


<!-- Summary -->
## 本章小结



<!-- 分页 -->

<div style="page-break-after: always;"></div>