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

**List**s承诺以特定顺序保存元素。 **List** 接口在 **Collection** 的基础上添加了许多方法，允许在 **List** 的中间插入和删除元素。

有两种类型的 **List** ：

- 基本的 **ArrayList** ，擅长随机访问元素，但在 **List** 中间插入和删除元素时速度较慢。
- **LinkedList** ，它通过代价较低的在 **List** 中间进行的插入和删除操作，提供了优化的顺序访问。 **LinkedList** 对于随机访问来说相对较慢，但它具有比 **ArrayList** 更大的特征集。

下面的示例导入 **typeinfo.pets** ，超前使用了[类型信息]()一章中的类库。这个类库包含了 **Pet** 类层次结构，以及用于随机生成 **Pet** 对象的一些工具类。此时不需要了解完整的详细信息，只需要知道两点：

1. 有一个 **Pet** 类，以及 **Pet** 的各种子类型。
2. 静态的 **Pets.arrayList()** 方法返回一个填充了随机选取的 **Pet** 对象的 **ArrayList**：

```java
// collections/ListFeatures.java
import typeinfo.pets.*;
import java.util.*;

public class ListFeatures {
  public static void main(String[] args) {
    Random rand = new Random(47);
    List<Pet> pets = Pets.list(7);
    System.out.println("1: " + pets);
    Hamster h = new Hamster();
    pets.add(h); // Automatically resizes
    System.out.println("2: " + pets);
    System.out.println("3: " + pets.contains(h));
    pets.remove(h); // Remove by object
    Pet p = pets.get(2);
    System.out.println(
      "4: " +  p + " " + pets.indexOf(p));
    Pet cymric = new Cymric();
    System.out.println("5: " + pets.indexOf(cymric));
    System.out.println("6: " + pets.remove(cymric));
    // Must be the exact object:
    System.out.println("7: " + pets.remove(p));
    System.out.println("8: " + pets);
    pets.add(3, new Mouse()); // Insert at an index
    System.out.println("9: " + pets);
    List<Pet> sub = pets.subList(1, 4);
    System.out.println("subList: " + sub);
    System.out.println("10: " + pets.containsAll(sub));
    Collections.sort(sub); // In-place sort
    System.out.println("sorted subList: " + sub);
    // Order is not important in containsAll():
    System.out.println("11: " + pets.containsAll(sub));
    Collections.shuffle(sub, rand); // Mix it up
    System.out.println("shuffled subList: " + sub);
    System.out.println("12: " + pets.containsAll(sub));
    List<Pet> copy = new ArrayList<>(pets);
    sub = Arrays.asList(pets.get(1), pets.get(4));
    System.out.println("sub: " + sub);
    copy.retainAll(sub);
    System.out.println("13: " + copy);
    copy = new ArrayList<>(pets); // Get a fresh copy
    copy.remove(2); // Remove by index
    System.out.println("14: " + copy);
    copy.removeAll(sub); // Only removes exact objects
    System.out.println("15: " + copy);
    copy.set(1, new Mouse()); // Replace an element
    System.out.println("16: " + copy);
    copy.addAll(2, sub); // Insert a list in the middle
    System.out.println("17: " + copy);
    System.out.println("18: " + pets.isEmpty());
    pets.clear(); // Remove all elements
    System.out.println("19: " + pets);
    System.out.println("20: " + pets.isEmpty());
    pets.addAll(Pets.list(4));
    System.out.println("21: " + pets);
    Object[] o = pets.toArray();
    System.out.println("22: " + o[3]);
    Pet[] pa = pets.toArray(new Pet[0]);
    System.out.println("23: " + pa[3].id());
  }
}
/* Output:
1: [Rat, Manx, Cymric, Mutt, Pug, Cymric, Pug]
2: [Rat, Manx, Cymric, Mutt, Pug, Cymric, Pug, Hamster]
3: true
4: Cymric 2
5: -1
6: false
7: true
8: [Rat, Manx, Mutt, Pug, Cymric, Pug]
9: [Rat, Manx, Mutt, Mouse, Pug, Cymric, Pug]
subList: [Manx, Mutt, Mouse]
10: true
sorted subList: [Manx, Mouse, Mutt]
11: true
shuffled subList: [Mouse, Manx, Mutt]
12: true
sub: [Mouse, Pug]
13: [Mouse, Pug]
14: [Rat, Mouse, Mutt, Pug, Cymric, Pug]
15: [Rat, Mutt, Cymric, Pug]
16: [Rat, Mouse, Cymric, Pug]
17: [Rat, Mouse, Mouse, Pug, Cymric, Pug]
18: false
19: []
20: true
21: [Manx, Cymric, Rat, EgyptianMau]
22: EgyptianMau
23: 14
*/
```

打印行都编了号，因此输出可以与源代码相关。 第1行输出展示了原始的由 **Pet** 组成的 **List** 。 与数组不同， **List** 可以在创建后添加或删除元素，并自行调整大小。这正是它的重要价值：一种可修改的序列。在第2行输出中可以看到添加一个 **Hamster** 的结果，该对象将被追加到列表的末尾。

可以使用 **contains()** 方法确定对象是否在列表中。如果要删除一个对象，可以将该对象的引用传递给 **remove()** 方法。同样，如果有一个对象的引用，可以使用 **indexOf()** 在 **List** 中找到该对象所在位置的下标号，如第4行输出所示中所示。

当确定元素是否是属于某个 **List** ，寻找某个元素的索引，以及通过引用从 **List** 中删除元素时，都会用到 **equals()** 方法（根类 **Object** 的一个方法）。每个 **Pet** 被定义为一个唯一的对象，所以即使列表中已经有两个 **Cymrics** ，如果再创建一个新的 **Cymric** 对象并将其传递给 **indexOf()** 方法，结果仍为 **-1** （表示未找到），并且尝试调用 **remove()** 方法来删除这个对象将返回 **false** 。对于其他类， **equals()** 的定义可能有所不同。例如，如果两个 **String** 的内容相同，则这两个 **String** 相等。因此，为了防止出现意外，请务必注意 **List** 行为会根据 **equals()** 行为而发生变化。

第7、8行输出展示了删除与 **List** 中的对象完全匹配的对象是成功的。

可以在 **List** 的中间插入一个元素，就像在第9行输出和它之前的代码那样。但这会带来一个问题：对于 **LinkedList** ，在列表中间插入和删除都是廉价操作（在本例中，除了对列表中间进行的真正的随机访问），但对于 **ArrayList** ，这可是代价高昂的操作。这是否意味着永远不应该在 **ArrayList** 的中间插入元素，并最好是转换为 **LinkedList** ？不，它只是意味着你应该意识到这个问题，如果你开始在某个 **ArrayList** 中间进执行很多插入操作，并且程序开始变慢，那么你应该看看你的 **List** 实现有可能就是罪魁祸首（发现此类瓶颈的最佳方式是使用仿真器）。优化是一个很棘手的问题，最好的策略就是置之不顾，直到发现必须要去担心它了（尽管去理解这些问题总是一个很好的主意）。

**subList()** 方法可以轻松地从更大的列表中创建切片，当将切片结果传递给原来这个较大的列表的 **containsAll()** 方法时，很自然地会得到 **true**。请注意，顺序并不重要，在第11、12行输出中可以看到，在 **sub** 上调用直观命名的 **Collections.sort()** 和 **Collections.shuffle()** 方法，不会影响 **containsAll()** 的结果。 **subList()** 所产生的列表的幕后支持就是原始列表。因此，对所返回列表的更改都将会反映在原始列表中，反之亦然。

**retainAll()** 方法实际上是一个“集合交集”操作，在本例中，它保留了同时在 **copy** 和 **sub** 中的所有元素。请再次注意，所产生的结果行为依赖于 **equals()** 方法。

第14行输出展示了使用索引号来删除元素的结果，与通过对象引用来删除元素相比，它显得更加直观，因为在使用索引时，不必担心 **equals()** 的行为。

**removeAll()** 方法也是基于 **equals()** 方法运行的。 顾名思义，它会从 **List** 中删除在参数 **List** 中的所有元素。

**set()** 方法的命名显得很不合时宜，因为它与 **Set** 类存在潜在的冲突。在这里使用“replace”可能更适合，因为它的功能是用第二个参数替换索引处的元素（第一个参数）。

第17行输出表明，对于 **List** ，有一个重载的 **addAll()** 方法可以将新列表插入到原始列表的中间，而不仅仅只能用 **Collection** 的 **addAll()** 方法将追加到列表末尾。

第18-20行输出展示了 **isEmpty()** 和 **clear()** 方法的效果。

第22、23行输出展示了如何使用 **toArray()** 方法将任意的 **Collection** 转换为数组。这是一个重载方法，其无参版本返回一个 **Object** 数组，但是如果将目标类型的数组传递给这个重载版本，那么它会生成一个指定类型的数组（假设它通过了类型检查）。如果参数数组太小而无法容纳 **List** 中的所有元素（就像本例一样），则 **toArray()** 会创建一个具有合适尺寸的新数组。 **Pet** 对象有一个 **id()** 方法，可以在所产生的数组中的对象上调用这个方法。

<!-- Iterators -->
## 迭代器Iterators

在任何集合中，都必须有某种方式可以插入元素并再次获取它们。毕竟，保存事物是集合最基本的工作。对于 **List** ， **add()** 是插入元素的一种方式， **get()** 是获取元素的一种方式。

如果从更高层次的角度考虑，会发现这里有个缺点：要使用集合，必须对集合的确切类型编程。这一开始可能看起来不是很糟糕，但是考虑下面的情况：如果原本是对 **List** 编码的，但是后来发现如果能够将相同的代码应用于 **Set** 会更方便，此时应该怎么做？或者假设想从一开始就编写一段通用代码，它不知道或不关心它正在使用什么类型的集合，因此它可以用于不同类型的集合，那么如何才能不重写代码就可以应用于不同类型的集合？

*迭代器*（也是一种设计模式）的概念实现了这种抽象。迭代器是一个对象，它在一个序列中移动并选择该序列中的每个对象，而客户端程序员不知道或不关心该序列的底层结构。另外，迭代器通常被称为*轻量级对象*（lightweight object）：创建它的代价小。因此，经常可以看到一些对迭代器有些奇怪的约束。例如，Java的 **Iterator** 只能单向移动。这个 **Iterator** 只能用来：
1. 使用 **iterator()** 方法要求集合返回一个 **Iterator**。 **Iterator** 将准备好返回序列中的第一个元素。
2. 使用 **next()** 方法获得序列中的下一个元素。
3. 使用 **hasNext()** 方法检查序列中是否还有元素。
4. 使用 **remove()** 方法将迭代器最近返回的那个元素删除。

为了观察它的工作方式，这里再次使用[类型信息]()章节中的 **Pet** 工具：

```java
// collections/SimpleIteration.java
import typeinfo.pets.*;
import java.util.*;

public class SimpleIteration {
  public static void main(String[] args) {
    List<Pet> pets = Pets.list(12);
    Iterator<Pet> it = pets.iterator();
    while(it.hasNext()) {
      Pet p = it.next();
      System.out.print(p.id() + ":" + p + " ");
    }
    System.out.println();
    // A simpler approach, when possible:
    for(Pet p : pets)
      System.out.print(p.id() + ":" + p + " ");
    System.out.println();
    // An Iterator can also remove elements:
    it = pets.iterator();
    for(int i = 0; i < 6; i++) {
      it.next();
      it.remove();
    }
    System.out.println(pets);
  }
}
/* Output:
0:Rat 1:Manx 2:Cymric 3:Mutt 4:Pug 5:Cymric 6:Pug
7:Manx 8:Cymric 9:Rat 10:EgyptianMau 11:Hamster
0:Rat 1:Manx 2:Cymric 3:Mutt 4:Pug 5:Cymric 6:Pug
7:Manx 8:Cymric 9:Rat 10:EgyptianMau 11:Hamster
[Pug, Manx, Cymric, Rat, EgyptianMau, Hamster]
*/
```

有了 **Iterator** ，就不必再为集合中元素的数量操心了。这是由 **hasNext()** 和 **next()** 关心的事情。

如果只是想向前遍历 **List** ，并不打算修改 **List** 对象本身，那么使用 *for-in* 语法更加简洁。

**Iterator** 还可以删除由 **next()** 生成的最后一个元素，这意味着在调用 **remove()** 之前必须先调用 **next()** 。

在集合中的每个对象上执行操作，这种思想十分强大，并且贯穿于本书。

现在考虑创建一个 **display()** 方法，它不必知晓集合的确切类型：

```java
// collections/CrossCollectionIteration.java
import typeinfo.pets.*;
import java.util.*;

public class CrossCollectionIteration {
  public static void display(Iterator<Pet> it) {
    while(it.hasNext()) {
      Pet p = it.next();
      System.out.print(p.id() + ":" + p + " ");
    }
    System.out.println();
  }
  public static void main(String[] args) {
    List<Pet> pets = Pets.list(8);
    LinkedList<Pet> petsLL = new LinkedList<>(pets);
    HashSet<Pet> petsHS = new HashSet<>(pets);
    TreeSet<Pet> petsTS = new TreeSet<>(pets);
    display(pets.iterator());
    display(petsLL.iterator());
    display(petsHS.iterator());
    display(petsTS.iterator());
  }
}
/* Output:
0:Rat 1:Manx 2:Cymric 3:Mutt 4:Pug 5:Cymric 6:Pug
7:Manx
0:Rat 1:Manx 2:Cymric 3:Mutt 4:Pug 5:Cymric 6:Pug
7:Manx
0:Rat 1:Manx 2:Cymric 3:Mutt 4:Pug 5:Cymric 6:Pug
7:Manx
5:Cymric 2:Cymric 7:Manx 1:Manx 3:Mutt 6:Pug 4:Pug
0:Rat
*/
```

**display()** 方法不包含任何有关它所遍历的序列的类型信息。这也展示了 **Iterator** 的真正威力：能够将遍历序列的操作与该序列的底层结构分离。出于这个原因，我们有时会说：迭代器统一了对集合的访问方式。

我们可以使用 **Iterable** 接口生成上一个示例的更简洁版本，该接口描述了“可以产生 **Iterator** 的任何东西”：

```java
// collections/CrossCollectionIteration2.java
import typeinfo.pets.*;
import java.util.*;

public class CrossCollectionIteration2 {
  public static void display(Iterable<Pet> ip) {
    Iterator<Pet> it = ip.iterator();
    while(it.hasNext()) {
      Pet p = it.next();
      System.out.print(p.id() + ":" + p + " ");
    }
    System.out.println();
  }
  public static void main(String[] args) {
    List<Pet> pets = Pets.list(8);
    LinkedList<Pet> petsLL = new LinkedList<>(pets);
    HashSet<Pet> petsHS = new HashSet<>(pets);
    TreeSet<Pet> petsTS = new TreeSet<>(pets);
    display(pets);
    display(petsLL);
    display(petsHS);
    display(petsTS);
  }
}
/* Output:
0:Rat 1:Manx 2:Cymric 3:Mutt 4:Pug 5:Cymric 6:Pug
7:Manx
0:Rat 1:Manx 2:Cymric 3:Mutt 4:Pug 5:Cymric 6:Pug
7:Manx
0:Rat 1:Manx 2:Cymric 3:Mutt 4:Pug 5:Cymric 6:Pug
7:Manx
5:Cymric 2:Cymric 7:Manx 1:Manx 3:Mutt 6:Pug 4:Pug
0:Rat
*/
```

这里所有的类都是 **Iterable** ，所以现在对 **display()** 的调用显然更简单。

<!-- ListIterator -->
### ListIterator

**ListIterator** 是一个更强大的 **Iterator**子类型，它只能由各种 **List** 类生成。虽然 **Iterator** 只能向前移动，但 **ListIterator** 可以双向移动。它还可以生成相对于迭代器在列表中指向的当前位置的后一个和前一个元素的索引，并且可以使用 **set()** 方法替换它访问过的最后一个元素。可以通过调用 **listIterator()** 方法来生成指向 **List** 开头处的 **ListIterator** ，还可以通过调用** listIterator(n)** 创建一个一开始就指向列表索引号为 **n** 的元素处的 **ListIterator** 。 下面的示例演示了所有这些能力：

```java
// collections/ListIteration.java
import typeinfo.pets.*;
import java.util.*;

public class ListIteration {
  public static void main(String[] args) {
    List<Pet> pets = Pets.list(8);
    ListIterator<Pet> it = pets.listIterator();
    while(it.hasNext())
      System.out.print(it.next() +
        ", " + it.nextIndex() +
        ", " + it.previousIndex() + "; ");
    System.out.println();
    // Backwards:
    while(it.hasPrevious())
      System.out.print(it.previous().id() + " ");
    System.out.println();
    System.out.println(pets);
    it = pets.listIterator(3);
    while(it.hasNext()) {
      it.next();
      it.set(Pets.get());
    }
    System.out.println(pets);
  }
}
/* Output:
Rat, 1, 0; Manx, 2, 1; Cymric, 3, 2; Mutt, 4, 3; Pug,
5, 4; Cymric, 6, 5; Pug, 7, 6; Manx, 8, 7;
7 6 5 4 3 2 1 0
[Rat, Manx, Cymric, Mutt, Pug, Cymric, Pug, Manx]
[Rat, Manx, Cymric, Cymric, Rat, EgyptianMau, Hamster,
EgyptianMau]
*/
```

**Pets.get()** 方法用来从位置3开始替换 **List** 中的所有Pet对象。

<!-- LinkedList -->
## 链表LinkedList

**LinkedList** 也像 **ArrayList** 一样实现了基本的 **List** 接口，但它在 **List** 中间执行插入和删除操作时，比 **ArrayList** 更高效。但在随机访问操作效率方面却要逊色一些。

**LinkedList还添加了一些方法，使其可以被用作栈、队列或双端队列（deque）** 。在这些方法中，有些彼此之间可能只是名称有些差异，或者只存在些许差异，以使得这些名字在特定用法的上下文环境中更加适用（特别是在 **Queue** 中）。例如：

- **getFirst()** 和 **element()** 是相同的，它们都返回列表的头部（第一个元素）而并不删除它，如果 **List** 为空，则抛出 **NoSuchElementException** 异常。 **peek()** 方法与这两个方法只是稍有差异，它在列表为空时返回 **null** 。
- **removeFirst()** 和 **remove()** 也是相同的，它们删除并返回列表的头部元素，并在列表为空时抛出 **NoSuchElementException** 异常。 **poll()** 稍有差异，它在列表为空时返回 **null** 。
- **addFirst()** 在列表的开头插入一个元素。
- **offer()** 与 **add()** 和 **addLast()** 相同。 它们都在列表的尾部（末尾）添加一个元素。
- **removeLast()** 删除并返回列表的最后一个元素。

下面的示例展示了这些功能之间基本的相似性和差异性。它并不是重复执行 **ListFeatures.java** 中所示的行为：

```java
// collections/LinkedListFeatures.java
import typeinfo.pets.*;
import java.util.*;

public class LinkedListFeatures {
  public static void main(String[] args) {
    LinkedList<Pet> pets =
      new LinkedList<>(Pets.list(5));
    System.out.println(pets);
    // Identical:
    System.out.println(
      "pets.getFirst(): " + pets.getFirst());
    System.out.println(
      "pets.element(): " + pets.element());
    // Only differs in empty-list behavior:
    System.out.println("pets.peek(): " + pets.peek());
    // Identical; remove and return the first element:
    System.out.println(
      "pets.remove(): " + pets.remove());
    System.out.println(
      "pets.removeFirst(): " + pets.removeFirst());
    // Only differs in empty-list behavior:
    System.out.println("pets.poll(): " + pets.poll());
    System.out.println(pets);
    pets.addFirst(new Rat());
    System.out.println("After addFirst(): " + pets);
    pets.offer(Pets.get());
    System.out.println("After offer(): " + pets);
    pets.add(Pets.get());
    System.out.println("After add(): " + pets);
    pets.addLast(new Hamster());
    System.out.println("After addLast(): " + pets);
    System.out.println(
      "pets.removeLast(): " + pets.removeLast());
  }
}
/* Output:
[Rat, Manx, Cymric, Mutt, Pug]
pets.getFirst(): Rat
pets.element(): Rat
pets.peek(): Rat
pets.remove(): Rat
pets.removeFirst(): Manx
pets.poll(): Cymric
[Mutt, Pug]
After addFirst(): [Rat, Mutt, Pug]
After offer(): [Rat, Mutt, Pug, Cymric]
After add(): [Rat, Mutt, Pug, Cymric, Pug]
After addLast(): [Rat, Mutt, Pug, Cymric, Pug, Hamster]
pets.removeLast(): Hamster
*/
```

**Pets.list()** 的结果被传递给 **LinkedList** 的构造器，以便使用它来填充 **LinkedList** 。如果查看 **Queue** 接口就会发现，它在 **LinkedList** 的基础上添加了 **element()** ， **offer()** ， **peek()** ， **poll()** 和 **remove()** 方法，以使其可以成为一个 **Queue** 的实现。 **Queue** 的完整示例将在本章稍后给出。
 
<!-- Stack -->
## 堆栈Stack

堆栈是“后进先出”（LIFO）集合。它有时被称为*叠加栈*（pushdown stack），因为最后“压入”（push）栈的元素，第一个被“弹出”（pop）栈。经常用来类比栈的事物是带有弹簧支架的自助餐厅托盘。最后装入的托盘总是最先拿出来使用的。

Java 1.0中附带了一个 **Stack** 类，结果设计得很糟糕（为了向后兼容，我们永远坚持Java中的旧设计错误）。Java 6添加了 **ArrayDeque** ，其中包含直接实现堆栈功能的方法：

```java
// collections/StackTest.java
import java.util.*;

public class StackTest {
  public static void main(String[] args) {
    Deque<String> stack = new ArrayDeque<>();
    for(String s : "My dog has fleas".split(" "))
      stack.push(s);
    while(!stack.isEmpty())
      System.out.print(stack.pop() + " ");
  }
}
/* Output:
fleas has dog My
*/
```

即使它是作为一个堆栈在使用，我们仍然必须将其声明为 **Deque** 。有时一个名为 **Stack** 的类更能把事情讲清楚：

```java
// onjava/Stack.java
// A Stack class built with an ArrayDeque
package onjava;
import java.util.Deque;
import java.util.ArrayDeque;

public class Stack<T> {
  private Deque<T> storage = new ArrayDeque<>();
  public void push(T v) { storage.push(v); }
  public T peek() { return storage.peek(); }
  public T pop() { return storage.pop(); }
  public boolean isEmpty() { return storage.isEmpty(); }
  @Override
  public String toString() {
    return storage.toString();
  }
}
```

这里引入了使用泛型的类定义的最简单的可能示例。类名称后面的 **<T>** 告诉编译器这是一个参数化类型，而其中的类型参数，即在类被使用时将会被实际类型替换的参数，就是 **T** 。基本上，这个类是在声明“我们在定义一个可以持有 **T** 类型对象的 **Stack** 。” **Stack** 是使用 **ArrayDeque** 实现的，而 **ArrayDeque** 也被告知它将持有 **T** 类型对象。注意， **push()** 接受类型为 **T** 的对象，而 **peek()** 和 **pop()** 返回类型为 **T** 的对象。 **peek()** 方法将返回栈顶元素，但并不将其从栈顶删除，而 **pop()** 删除并返回顶部元素。

如果只需要栈的行为，那么使用继承是不合适的，因为这将产生一个具有 **ArrayDeque** 的其它所有方法的类（在[附录：集合主题]()中将会看到， **Java 1.0** 设计者在创建 **java.util.Stack** 时，就犯了这个错误）。使用组合，可以选择要公开的方法以及如何命名它们。

下面将使用 **StackTest.java** 中的相同代码来演示这个新的 **Stack** 类：

```java
// collections/StackTest2.java
import onjava.*;

public class StackTest2 {
  public static void main(String[] args) {
    Stack<String> stack = new Stack<>();
    for(String s : "My dog has fleas".split(" "))
      stack.push(s);
    while(!stack.isEmpty())
      System.out.print(stack.pop() + " ");
  }
}
/* Output:
fleas has dog My
*/
```

如果想在自己的代码中使用这个 **Stack** 类，当在创建其实例时，就需要完整指定包名，或者更改这个类的名称；否则，就有可能会与 **java.util** 包中的 **Stack** 发生冲突。例如，如果我们在上面的例子中导入 **java.util.***，那么就必须使用包名来防止冲突：

```java
// collections/StackCollision.java

public class StackCollision {
  public static void main(String[] args) {
    onjava.Stack<String> stack = new onjava.Stack<>();
    for(String s : "My dog has fleas".split(" "))
      stack.push(s);
    while(!stack.isEmpty())
      System.out.print(stack.pop() + " ");
    System.out.println();
    java.util.Stack<String> stack2 =
      new java.util.Stack<>();
    for(String s : "My dog has fleas".split(" "))
      stack2.push(s);
    while(!stack2.empty())
      System.out.print(stack2.pop() + " ");
  }
}
/* Output:
fleas has dog My
fleas has dog My
*/
```

尽管已经有了 **java.util.Stack** ，但是 **ArrayDeque** 可以产生更好的 **Stack** ，因此更可取。

还可以使用显式导入来控制对“首选” **Stack** 实现的选择：
```java
import onjava.Stack;
```

现在,任何对 **Stack** 的引用都将选择 **onjava** 版本，而在选择 **java.util.Stack** 时，必须使用全限定名称（full qualification）。

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