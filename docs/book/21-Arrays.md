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