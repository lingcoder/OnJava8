[TOC]

<!-- Collections -->
# 第十二章 集合

> 如果一个程序只包含固定数量的对象且对象的生命周期都是已知的，那么这是一个非常简单的程序。

通常，程序总是根据运行时才知道的某些条件去创建新的对象。在此之前，无法知道所需对象的数量甚至确切类型。为了解决这个普遍的编程问题，需要在任意时刻和任意位置创建任意数量的对象。因此，不能依靠创建命名的引用来持有每一个对象：
```java
MyType aReference
```
因为从来不会知道实际需要多少个这样的引用。

大多数编程语言都提供了某种方法来解决这个基本问题。Java有多种方式保存对象（确切地说，是对象的引用）。例如前边曾经学习过的数组，它是编译器支持的类型。数组是保存一组对象的最有效的方式，如果想要保存一组基本类型数据，也推荐使用数组。但是数组具有固定的大小尺寸，而且在更一般的情况下，在写程序的时候并不知道将需要多少个对象，或者是否需要更复杂的方式来存储对象，因此数组尺寸固定这一限制就显得太过受限了。

**java.util** 库提供了一套相当完整的*集合类*（collection classes）来解决这个问题，其中基本的类型有 **List** 、**Set** 、 **Queue** 和 **Map**。这些类型也被称作*容器类*（container classes），但我将使用Java类库使用的术语。集合提供了完善的方法来保存对象，可以使用这些工具来解决大量的问题。

集合还有一些其它特性。例如， **Set** 对于每个值都只保存一个对象， **Map** 是一个关联数组，允许将某些对象与其他对象关联起来。Java集合类都可以自动地调整自己的大小。因此，与数组不同，在编程时，可以将任意数量的对象放置在集合中，而不用关心集合应该有多大。

尽管在Java中没有直接的关键字支持，集合类仍然是可以显著增强编程能力的基本工具。在本章中，将介绍Java集合类库的基本知识，并重点介绍一些典型用法。这里将专注于在日常编程中使用的集合。稍后，在[附录：集合主题]()中，还将学习到其余的那些集合和相关功能，以及如何使用它们的更多详细信息。

<!-- Generics and Type-Safe Collections -->
## 泛型和类型安全的集合

使用Java SE5之前的集合的一个主要问题是编译器允许你向集合中插入不正确的类型。例如，考虑一个 **Apple** 对象的集合，这里使用最基本最可靠的 **ArrayList** 。现在，可以把 **ArrayList** 看作“可以自动扩充自身尺寸的数组”来看待。使用 **ArrayList** 相当简单：创建一个实例，用 **add()** 插入对象；然后用 **get()** 来访问这些对象，此时需要使用索引，就像数组那样，但是不需要方括号。 **ArrayList** 还有一个 **size()** 方法，来说明集合中包含了多少个元素，所以不会不小心因数组越界而引发错误（通过抛出*运行时异常*，[异常]()章节介绍了异常）。

在本例中， **Apple** 和 **Orange** 都被放到了集合中，然后将它们取出。正常情况下，Java编译器会给出警告，因为这个示例没有使用泛型。在这里，使用特定的注解来抑制警告信息。注解以“@”符号开头，可以带参数。这里的 **@SuppressWarning** 注解及其参数表示只抑制“unchecked”类型的警告（[注解]()章节将介绍更多有关注解的信息）：

```java
// collections/ApplesAndOrangesWithoutGenerics.java
// Simple collection use (suppressing compiler warnings)
// {ThrowsException}
import java.util.*;

class Apple {
  private static long counter;
  private final long id = counter++;
  public long id() { return id; }
}

class Orange {}

public class ApplesAndOrangesWithoutGenerics {
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    ArrayList apples = new ArrayList();
    for(int i = 0; i < 3; i++)
      apples.add(new Apple());
    // No problem adding an Orange to apples:
    apples.add(new Orange());
    for(Object apple : apples) {
      ((Apple) apple).id();
      // Orange is detected only at run time
    }
  }
}
/* Output:
___[ Error Output ]___
Exception in thread "main"
java.lang.ClassCastException: Orange cannot be cast to
Apple
        at ApplesAndOrangesWithoutGenerics.main(ApplesA
ndOrangesWithoutGenerics.java:23)
*/
```

**Apple** 和 **Orange** 是截然不同的，它们除了都是 **Object** 之外没有任何共同点（如果一个类没有显式地声明继承自哪个类，那么它就自动继承自 **Object**）。因为 **ArrayList** 保存的是 **Object** ，所以不仅可以通过 **ArrayList** 的 **add()** 方法将 **Apple** 对象放入这个集合，而且可以放入 **Orange** 对象，这无论在编译期还是运行时都不会有问题。当使用 **ArrayList** 的 **get()** 方法来取出你认为是 **Apple** 的对象时，得到的只是 **Object** 引用，必须将其转型为 **Apple**。然后需要将整个表达式用括号括起来，以便在调用 **Apple** 的 **id()** 方法之前，强制执行转型。否则，将会产生语法错误。

在运行时，当尝试将 **Orange** 对象转为 **Apple**时，会出现输出中显示的错误。

在[泛型]()章节中，你将了解到使用Java泛型来创建类可能很复杂。但是，使用预先定义的泛型类却相当简单。例如，要定义一个用于保存 **Apple**  对象的 **ArrayList** ，只需要使用 **ArrayList<Apple>** 来代替 **ArrayList** 。尖括号括起来的是*类型参数*（可能会有多个），它指定了这个集合实例可以保存的类型。

通过使用泛型，就可以在编译期防止将错误类型的对象放置到集合中。下面还是这个示例，但是使用了泛型：
```java
// collections/ApplesAndOrangesWithGenerics.java
import java.util.*;

public class ApplesAndOrangesWithGenerics {
  public static void main(String[] args) {
    ArrayList<Apple> apples = new ArrayList<>();
    for(int i = 0; i < 3; i++)
      apples.add(new Apple());
    // Compile-time error:
    // apples.add(new Orange());
    for(Apple apple : apples) {
      System.out.println(apple.id());
    }
  }
}
/* Output:
0
1
2
*/
```

在 **apples** 定义的右侧，可以看到 **new ArrayList<>()** 。这有时被称为“菱形语法”(diamond syntax)。在Java 7之前，必须要在两端都进行类型声明，如下所示：

```java
ArrayList<Apple> apples = new ArrayList<Apple>();
```

随着类型变得越来越复杂，这种重复产生的代码非常混乱且难以阅读。程序员发现所有类型信息都可以从左侧获得，因此，编译器没有理由强迫右侧在重复这些。Java语言团队采纳了这种*类型推断*（type inference）的请求，即使只是这么小的一点。

有了 **ArrayList** 声明中的类型指定，编译器会阻止将 **Orange** 放入 **apples** ，因此，这会成为一个编译期错误而不是运行时错误。

使用泛型，从 **List** 中获取元素不需要强制类型转换。因为 **List** 知道它持有什么类型，因此当调用 **get()** 时，它会替你执行转型。这样，不仅可以知道编译器将会检查放入集合中的对象的类型，而且在使用集合中的对象时，可以使用更加清晰的语法。

当指定了某个类型为泛型参数时，并不仅限于只能将确切类型的对象放入集合中。向上转型也可以像作用于其他类型一样作用于泛型：
```java
// collections/GenericsAndUpcasting.java
import java.util.*;

class GrannySmith extends Apple {}
class Gala extends Apple {}
class Fuji extends Apple {}
class Braeburn extends Apple {}

public class GenericsAndUpcasting {
  public static void main(String[] args) {
    ArrayList<Apple> apples = new ArrayList<>();
    apples.add(new GrannySmith());
    apples.add(new Gala());
    apples.add(new Fuji());
    apples.add(new Braeburn());
    for(Apple apple : apples)
      System.out.println(apple);
  }
}
/* Output:
GrannySmith@15db9742
Gala@6d06d69c
Fuji@7852e922
Braeburn@4e25154f
*/
```

因此，可以将 **Apple** 的子类型添加到被指定为保存 **Apple** 对象的集合中。

程序的输出是从 **Object** 默认的 **toString()** 方法产生的，该方法打印类名，后边跟着对象的散列码的无符号十六进制表示（这个散列码是通过 **hashCode()** 方法产生的）。将在[附录：理解equals和hashcode方法]()中了解有关散列码的内容。

<!-- Basic Concepts -->
## 基本概念

Java集合类库采用“保持对象”（holding objects）的思想，并将其分为两个不同的概念，表示为类库的基本接口：

1. **集合（Collection）** ：一个独立元素的序列，这些元素都服从一条或多条规则。 **List** 必须以插入的方式保存元素， **Set** 不能包含重复元素， **Queue** 按照*排队规则*来确定对象产生的顺序（通常与它们被插入的顺序相同）。
2. **映射（Map）** ： 一组成对的“键值对”对象，允许使用键来查找值。 **ArrayList** 使用数字来查找对象，因此在某种意义上讲，它是将数字和对象关联在一起。 **map**允许我们使用一个对象来查找另一个对象，它也被称作*关联数组*（associative array），因为它将对象和其它对象关联在一起；或者称作*字典*（dictionary），因为可以使用一个键对象来查找值对象，就像在字典中使用单词查找定义一样。 **Map**s是强大的编程工具。

尽管并非总是可行，但在理想情况下，你编写的大部分代码都在与这些接口打交道，并且唯一需要指定所使用的精确类型的地方就是在创建的时候。因此，可以像下面这样创建一个 **List** ：

```java
List<Apple> apples = new ArrayList<>();
```

请注意， **ArrayList** 已经被向上转型为了 **List** ，这与之前示例中的处理方式正好相反。使用接口的目的是，如果想要改变具体实现，只需在创建时修改它就行了，就像下面这样：

```java
List<Apple> apples = new LinkedList<>();
```

因此，应该创建一个具体类的对象，将其向上转型为对应的接口，然后在其余代码中都是用这个接口。

这种方式并非总是有效的，因为某些具体类有额外的功能。例如， **LinkedList** 具有 **List** 接口中未包含的额外方法，而 **TreeMap** 也具有在 **Map** 接口中未包含的方法。如果需要使用这些方法，就不能将它们向上转型为更通用的接口。

**Collection** 接口概括了*序列*的概念——一种存放一组对象的方式。下面是个简单的示例，用 **Integer** 对象填充了一个 **Collection** （这里用 **ArrayList** 表示），然后打印集合中的每个元素：
```java
// collections/SimpleCollection.java
import java.util.*;

public class SimpleCollection {
  public static void main(String[] args) {
    Collection<Integer> c = new ArrayList<>();
    for(int i = 0; i < 10; i++)
      c.add(i); // Autoboxing
    for(Integer i : c)
      System.out.print(i + ", ");
  }
}
/* Output:
0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
*/
```

这个例子仅适用 **Collection** 方法，所以任何继承自 **Collection** 的类的对象都可以使用。但是 **ArrayList** 是最基本的序列类型。

**add()** 方法的名称就表明它是在 **Collection** 中添加一个新元素。但是，文档中非常详细地叙述到 **add()** “要确保这个 **Collection** 包含指定的元素。”这是因为考虑到了 **Set** 的含义，因为在 **Set**中，只有当元素不存在时才会添加元素。在使用 **ArrayList** ，或任何其他类型的 **List** 时，**add()** 总是表示“把它放进去”，因为 **List** 不关心是否存在重复元素。

可以使用 *for-in* 语法来遍历所有的 **Collection** ，就像这里所展示的那样。在本章的后续部分，还将学习到一个更灵活的概念，*迭代器*。

<!-- Adding Groups of Elements -->
## 添加元素组

在**java.util**包中的 **Arrays** 和 **Collections** 类中都有很多实用的方法，可以在一个 **Collection** 中添加一组元素。 **Arrays.asList()** 方法接受一个数组或是逗号分隔的元素列表（使用可变参数），并将其转换为 **List** 对象。 **Collections.addAll()** 方法接受一个 **Collection** 对象，以及一个数组或是一个逗号分隔的列表，将其中元素添加到 **Collection** 中。下边的示例展示了这两个方法，以及更通用的 **addAll()** 方法，所有 **Collection** 类型都包含该方法：

```java
// collections/AddingGroups.java
// Adding groups of elements to Collection objects
import java.util.*;

public class AddingGroups {
  public static void main(String[] args) {
    Collection<Integer> collection =
      new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
    Integer[] moreInts = { 6, 7, 8, 9, 10 };
    collection.addAll(Arrays.asList(moreInts));
    // Runs significantly faster, but you can't
    // construct a Collection this way:
    Collections.addAll(collection, 11, 12, 13, 14, 15);
    Collections.addAll(collection, moreInts);
    // Produces a list "backed by" an array:
    List<Integer> list = Arrays.asList(16,17,18,19,20);
    list.set(1, 99); // OK -- modify an element
    // list.add(21); // Runtime error; the underlying
                     // array cannot be resized.
  }
}
```

**Collection** 的构造器可以接受另一个 **Collection**，用它来将自身初始化。因此，可以使用 **Arrays.asList()** 来为这个构造器产生输入。但是， **Collections.addAll()** 运行得更快，而且很容易构建一个不包含元素的 **Collection** ，然后调用 **Collections.addAll()** ，因此这是首选方式。

**Collection.addAll()** 方法只能接受另一个 **Collection** 作为参数，因此它没有 **Arrays.asList()** 或 **Collections.addAll()** 灵活。这两个方法都使用可变参数列表。

也可以直接使用 **Arrays.asList()** 的输出作为一个 **List** ，但是这里的底层实现是数组，没法调整大小。如果尝试在这个 **List** 上调用 **add()** 或 **delete()**，由于这两个方法会尝试修改数组大小，所以会在运行时得到“Unsupported Operation（不支持的操作）”错误：

```java
// collections/AsListInference.java
import java.util.*;

class Snow {}
class Powder extends Snow {}
class Light extends Powder {}
class Heavy extends Powder {}
class Crusty extends Snow {}
class Slush extends Snow {}

public class AsListInference {
  public static void main(String[] args) {
    List<Snow> snow1 = Arrays.asList(
      new Crusty(), new Slush(), new Powder());
    //- snow1.add(new Heavy()); // Exception

    List<Snow> snow2 = Arrays.asList(
      new Light(), new Heavy());
    //- snow2.add(new Slush()); // Exception

    List<Snow> snow3 = new ArrayList<>();
    Collections.addAll(snow3,
      new Light(), new Heavy(), new Powder());
    snow3.add(new Crusty());

    // Hint with explicit type argument specification:
    List<Snow> snow4 = Arrays.<Snow>asList(
       new Light(), new Heavy(), new Slush());
    //- snow4.add(new Powder()); // Exception
  }
}
```

在 **snow4** 中，注意 **Arrays.asList()** 中间的“hint”，告诉编译器 **Arrays.asList()** 生成的结果 **List** 类型的实际目标类型是什么。这称为*显式类型参数说明*（explicit type argument specification）。

<!-- Printing Collections -->
## 集合的打印

必须使用 **Arrays.toString()** 来生成数组的可打印形式。但是打印集合无需任何帮助。下面是一个例子，这个例子中也介绍了基本的Java集合：
```java
// collections/PrintingCollections.java
// Collections print themselves automatically
import java.util.*;

public class PrintingCollections {
  static Collection
  fill(Collection<String> collection) {
    collection.add("rat");
    collection.add("cat");
    collection.add("dog");
    collection.add("dog");
    return collection;
  }
  static Map fill(Map<String, String> map) {
    map.put("rat", "Fuzzy");
    map.put("cat", "Rags");
    map.put("dog", "Bosco");
    map.put("dog", "Spot");
    return map;
  }
  public static void main(String[] args) {
    System.out.println(fill(new ArrayList<>()));
    System.out.println(fill(new LinkedList<>()));
    System.out.println(fill(new HashSet<>()));
    System.out.println(fill(new TreeSet<>()));
    System.out.println(fill(new LinkedHashSet<>()));
    System.out.println(fill(new HashMap<>()));
    System.out.println(fill(new TreeMap<>()));
    System.out.println(fill(new LinkedHashMap<>()));
  }
}
/* Output:
[rat, cat, dog, dog]
[rat, cat, dog, dog]
[rat, cat, dog]
[cat, dog, rat]
[rat, cat, dog]
{rat=Fuzzy, cat=Rags, dog=Spot}
{cat=Rags, dog=Spot, rat=Fuzzy}
{rat=Fuzzy, cat=Rags, dog=Spot}
*/
```

这显示了Java集合库中的两个主要类型。它们的区别在于集合中的每个“槽”（slot）保存的元素个数。 **Collection** 类型在每个槽中只能保存一个元素。此类集合包括： **List**， 它以特定的顺序保存一组元素； **Set** ，其中元素不允许重复； **Queue** ，只能在集合一端插入对象，并从另一端移除对象（就本例而言，这只是查看序列的另一种方式，因此并没有显示它）。 **Map** 在每个槽中存放了两个元素，即*键*和与之关联的*值*。

默认的打印行为，（使用集合几桶的toString（）方法）即可生成可读性很好的结果。 **Collection** 打印出的内容用方括号括住，每个元素由逗号分隔。 **Map** 则由大括号括住，每个键和值用等号连接（键在左侧，值在右侧）。

第一个 **fill()** 方法适用于所有类型的 **Collection** ，这些类型都实现了 **add()** 方法以添加新元素。

**ArrayList** 和 **LinkedList** 都是 **List** 的类型，从输出中可以看出，它们都按插入顺序保存元素。两者之间的区别不仅在于执行某些类型的操作时的性能，而且 **LinkedList** 包含的操作多于 **ArrayList** 。本章后面将对这些内容进行更全面的探讨。

**HashSet** ， **TreeSet** 和 **LinkedHashSet** 是 **Set** 的类型。从输出中可以看到， **Set** 仅保存每个相同项中的一个，并且不同的 **Set** 实现存储元素的方式也不同。 **HashSet** 使用相当复杂的方法存储元素，这在[附录：集合主题]()中进行了探讨。现在只需要知道，这种技术是检索元素的最快方法，因此，存储顺序看似荒谬（通常只关心某事物是否是 **Set** 的成员，而存储顺序并不重要）。如果存储顺序很重要，则可以使用**TreeSet** ，它将按比较结果的升序保存对象）或 **LinkedHashSet** ，它按照被添加的先后顺序保存对象。

**Map** （也称为*关联数组*）使用*键*来查找对象，就像一个简单的数据库。所关联的对象称为*值*。 假设有一个 **Map** 将美国州名与它们的首府联系在一起，如果想要俄亥俄州（Ohio）的首府，可以用“Ohio”作为键来查找，几乎就像使用数组下标一样。 这是由于这种行为，对于每个键， **Map** 只接受一次。

**Map.put(key, value)** 添加一个所想要添加的值并将它与一个键（用来查找值）相关联。 **Map.get(key)** 生成与该键相关联的值。上面的示例仅添加键值对，并没有执行查找。这将在稍后展示。


请注意，这里没有指定（或考虑） **Map** 的大小，因为它会自动调整大小。 此外， **Map** 还知道如何打印自己，它会显示相关联的键和值。

本例使用了 **Map** 的三种基本风格： **HashMap** ， **TreeMap**和 **LinkedHashMap** 。

键和值保存在 **HashMap** 中的顺序不是插入顺序，因为 **HashMap** 实现使用了非常快速的算法来控制顺序。 **TreeMap** 通过比较结果的升序来保存键， **LinkedHashMap** 在保持 **HashMap** 查找速度的同时按键的插入顺序保存键。

<!-- List -->
## 列表List


<!-- Iterators -->
## 迭代器Iterators


<!-- LinkedList -->
## 链表LinkedList


<!-- Stack -->
## 堆栈Stack


<!-- Set -->
## 集合Set


<!-- Map -->
## 映射Map


<!-- Queue -->
## 队列Queue


<!-- Collection vs. Iterator -->
## 集合与迭代器


<!-- for-in and Iterators -->
## for-in和迭代器


<!-- Summary -->
## 本章小结

<!-- 分页 -->

<div style="page-break-after: always;"></div>