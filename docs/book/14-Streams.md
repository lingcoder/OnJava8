[TOC]

<!-- Streams -->
# 第十四章 流式编程

<!-- Java 8 Stream Support -->

## 流支持

Java 设计者面临着一个难题。他们拥有着一系列已经存在的库，这些库不仅仅在 Java 库本身中所使用，并且被用户编写的数百万行代码中使用。那他们如何将一个全新的流的基本概念融入到已经存在的库中？

比如一个简单的例子 **Random**，他们只是增加了更多的方法。只要现有方法没有被改变，遗留代码就不会受到干扰。

最大的挑战来自使用接口的库。集合类是其中重要的组成部分，因为你想把一个集合转换为流。如果向接口添加新方法，则会破坏实现接口但未实现新方法的每个类。

Java 8 中引入的解决方案是在[接口](10-Interfaces.md)中添加 **default**（**默认**）方法。通过这种方案，设计者们可以很平滑地将流式（*stream*）方法转换到现有类中。同时，流方法预置的操作几乎满足了我们平常所有的需求。流操作有三种类型：创建流，修改流元素（中间操作, *Intermediate Operations*），消费流元素（终端操作, *Terminal Operations*）。最后一种类型通常意味着收集流元素（通常是到集合中）。

下面我们来看看每一种类型的流操作。

<!-- Stream Creation -->
## 流创建

你可以通过 **Stream.of()** 很容易的将一组元素转化成为流（**Bubble** 类在之前的章节中已经定义过了）：

```java
// streams/StreamOf.java
import java.util.stream.*;
public class StreamOf {
    public static void main(String[] args) {
        Stream.of(new Bubble(1), new Bubble(2), new Bubble(3))
            .forEach(System.out::println);
        Stream.of("It's ", "a ", "wonderful ", "day ", "for ", "pie!")
            .forEach(System.out::print);
        System.out.println();
        Stream.of(3.14159, 2.718, 1.618)
            .forEach(System.out::println);
    }
}
```

输出为：

```java
Bubble(1)
Bubble(2)
Bubble(3)
It's a wonderful day for pie!
3.14159
2.718
1.618
```

除此之外，每个 **Collection** 都可以通过 **stream()** 方法来产生一个流：

```java
// streams/CollectionToStream.java
import java.util.*;
import java.util.stream.*;
public class CollectionToStream {
    public static void main(String[] args) {
        List<Bubble> bubbles = Arrays.asList(new Bubble(1), new Bubble(2), new Bubble(3));
        System.out.println(bubbles.stream()
            .mapToint(b -> b.i)
            .sum());
        
        Set<String> w = new HashSet<>(Arrays.asList("It's a wonderful day for pie!".split(" ")));
        w.stream()
         .map(x -> x + " ")
         .forEach(System.out::print);
        System.out.println();
        
        Map<String, double> m = new HashMap<>();
        m.put("pi", 3.14159);
        m.put("e", 2.718);
        m.put("phi", 1.618);
        m.entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .forEach(System.out::println);
    }
}
```

输出为：

```java
6
a pie! It's for wonderful day
phi: 1.618
e: 2.718
pi: 3.14159
```

在创建 **List\<Bubble\>** 对象之后，我们只需要简单的调用所有集合中都有的方法 **stream()**。中间操作 **map()** 会获取流中的所有元素，并且对流中元素应用操作从而产生新的元素，并将其传递到流中。通常情况 **map()** 方法获取对象并产生新的对象，但是这里有特殊版本的方法用于数值类型的流。例如，**mapToInt()** 方法将一个对象流（objects stream）转换成为包含整形数字的 **IntStream**。同样有针对 **Float** 和 **Double** 的类似名字的操作。

我们通过在 **String** 类型上面应用 **split()** - split 方法会根据参数来拆分字符串 - 获取元素用于定义 **w**。稍后你会看到这个参数十分复杂，但是在这里我们只是根据空格来分割字符串。

为了从 **Map** 集合中产生流数据，我们首先调用 **entrySet()** 去产生一个对象流，每个对象都包含一个键以及与其相关联的值。然后调用 **getKey()** 和 **getValue()** 将其分开。

### 随机数流

**Random** 类被一组生成流的方法增强了：

```java
// streams/RandomGenerators.java
import java.util.*;
import java.util.stream.*;
public class RandomGenerators {
    public static <T> void show(Stream<T> stream) {
        stream
        .limit(4)
        .forEach(System.out::println);
        System.out.println("++++++++");
    }
    
    public static void main(String[] args) {
        Random rand = new Random(47);
        show(rand.ints().boxed());
        show(rand.longs().boxed());
        show(rand.doubles().boxed());
        // Control the lower and upper bounds:
        show(rand.ints(10, 20).boxed());
        show(rand.longs(50, 100).boxed());
        show(rand.doubles(20, 30).boxed());
        // Control the stream size:
        show(rand.ints(2).boxed());
        show(rand.longs(2).boxed());
        show(rand.doubles(2).boxed());
        // Control the stream size and bounds:
        show(rand.ints(3, 3, 9).boxed());
        show(rand.longs(3, 12, 22).boxed());
        show(rand.doubles(3, 11.5, 12.3).boxed());
    }
}
```

输出为：

```java
-1172028779
1717241110
-2014573909
229403722
++++++++
2955289354441303771
3476817843704654257
-8917117694134521474
4941259272818818752
++++++++
0.2613610344283964
0.0508673570556899
0.8037155449603999
0.7620665811558285
++++++++
16
10
11
12
++++++++
65
99
54
58
++++++++
29.86777681078574
24.83968447804611
20.09247112332014
24.046793846338723
++++++++
1169976606
1947946283
++++++++
2970202997824602425
-2325326920272830366
++++++++
0.7024254510631527
0.6648552384607359
++++++++
6
7
7
++++++++
17
12
20
++++++++
12.27872414236691
11.732085449736195
12.196509449817267
++++++++
```

为了消除冗余代码，我创建了一个泛型方法 **show(Stream\<T\> stream)** （在讲解泛型之前就使用这个特性，确实有点作弊，但是回报是值得的）。类型参数 **T** 可以是任何类型，所以这个方法对 **Integer**， **Long** 和 **Double** 类型都生效。但是 **Random** 类只能生成原始数据类型 **int**， **long**， **double** 的流。幸运的是， **boxed()** 流操作将会自动的把基本类型包装成为对应的装箱类型，从而使得 **show()** 能够接受流。

我们可以使用 **Random** 为任意对象集合创建 **Supplier**。如下是一个从文本文件提供 **String** 对象的例子：

```java
// streams/Cheese.dat
Not much of a cheese shop really, is it?
Finest in the district, sir.
And what leads you to that conclusion?
Well, it's so clean.
It's certainly uncontaminated by cheese.
We use the Files class to read all the lines from a file into a
List<String> :
```

```java
// streams/RandomWords.java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.io.*;
import java.nio.file.*;
public class RandomWords implements Supplier<String> {
    List<String> words = new ArrayList<>();
    Random rand = new Random(47);
    RandomWords(String fname) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(fname));
        // Skip the first line:
        for (String line : lines.subList(1, lines.size())) {
            for (String word : line.split("[ .?,]+"))
                words.add(word.toLowerCase());
        }
    }
    public String get() {
        return words.get(rand.nextint(words.size()));
    }
    @Override
    public String toString() {
        return words.stream()
            .collect(Collectors.joining(" "));
    }
    public static void main(String[] args) throws Exception {
        System.out.println(
            Stream.generate(new RandomWords("Cheese.dat"))
                .limit(10)
                .collect(Collectors.joining(" ")));
    }
}
```

输出为：

```java
it shop sir the much cheese by conclusion district is
```

在这里你可以看到更为复杂的 **split()** 的使用。在构造器中，每一行都被 **split()** 方法通过空格或者被方括号包裹的任意标点符号进行分割。在结束方括号后面的 **+** 代表「+ 前面的东西可以出现一次或者多次」。

你将注意到在构造函数中循环体使用命令式编程（外部迭代）。在以后的例子中，你将会看到我门如何消除这一点。这种旧的形式不是特别糟糕，但是到处使用流会让你觉得更好一些。

在 **toString()** 和 **main()** 中你看到了 **collect()** 收集操作，它根据参数来组合所有流中的元素。

当你使用 **Collectors.joining()**，你将会得到一个 **String** 类型的结果，每个元素都根据 **joining()** 的参数来进行分割。还有许多不同的 **Collectors** 用于获取不同的结果。

在 **main()** 中，我们看到了 **Stream.generate()** 的预览版本，它可以把任意  **Supplier\<T\>** 用于生成 **T** 类型的流。



### int 类型的范围（Ranges of int）





<!-- Intermediate Operations -->

## 中间操作


<!-- Optional -->
## Optional类


<!-- Terminal Operations -->
## 终端操作


<!-- Summary -->
## 本章小结


<!-- 分页 -->

<div style="page-break-after: always;"></div>
