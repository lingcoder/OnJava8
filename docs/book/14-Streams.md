[TOC]

<!-- Streams -->
# 第十四章 流式编程

> 集合优化了对象的存储，而流（Streams）则是关于一组组对象的处理。

流（Streams）是与任何特定存储机制无关的元素序列——实际上，我们说流是 "没有存储 "的。

取代了在集合中迭代元素的做法，使用流即可从管道中提取元素并对其操作。这些管道通常被串联在一起形成一整套的管线，来对流进行操作。

在大多数情况下，将对象存储在集合中就是为了处理它们，因此你会发现你把编程的主要焦点从集合转移到了流上。

流的一个核心好处是，它使得程序更加短小并且更易理解。当 Lambda 表达式和方法引用（method references）和流一起使用的时候会让人感觉自成一体。流使得 Java 8 更具吸引力。

举个例子，假如你要随机展示 5 至 20 之间不重复的整数并进行排序。你要对它们进行排序的事实，会使你首先关注选用哪个有序集合，然后围绕这个集合进行后续的操作。但是使用流式编程，你就可以简单陈述你要做什么：

```java
// streams/Randoms.java
import java.util.*;
public class Randoms {
    public static void main(String[] args) {
        new Random(47)
            .ints(5, 20)
            .distinct()
            .limit(7)
            .sorted()
            .forEach(System.out::println);
    }
}
```

输出结果：

```
6
10
13
16
17
18
19
```

首先，我们给 **Random** 对象一个种子值47（以便程序再次运行时产生相同的输出）。`ints()` 方法产生一个流并且 `ints()` 方法有多种方式的重载 —— 两个参数限定了产生的数值的边界。这将生成一个随机整数流。我们用 *流的中间操作*（intermediate stream operation） `distinct()` 使流中的整数不重复，然后使用 `limit()` 方法获取前 7 个元素。接下来使用 `sorted()` 方法排序。最终使用 `forEach()` 方法遍历输出，它根据传递给它的函数对流中的每个对象执行操作。在这里，我们传递了一个可以在控制台显示每个元素的方法引用：`System.out::println` 。

注意 `Randoms.java` 中没有声明任何变量。流可以在不曾使用赋值或可变数据的情况下，对有状态的系统建模，这非常有用。

*声明式编程*（Declarative programming）是一种编程风格——它声明了要做什么，而不是指明（每一步）如何做。而这正是我们在函数式编程中所看到的（编程风格）。你会注意到，命令式（Imperative）编程的形式（指明每一步如何做）会更难理解：

```java
// streams/ImperativeRandoms.java
import java.util.*;
public class ImperativeRandoms {
    public static void main(String[] args) {
        Random rand = new Random(47);
        SortedSet<Integer> rints = new TreeSet<>();
        while(rints.size() < 7) {
            int r = rand.nextInt(20);
            if(r < 5) continue;
            rints.add(r);
        }
        System.out.println(rints);
    }
}
```

输出结果：

```
[7, 8, 9, 11, 13, 15, 18]
```

在 `Randoms.java` 中，我们无需定义任何变量，但在这里我们定义了 3 个变量： `rand`，`rints` 和 `r`。由于 `nextInt()` 方法没有下限的原因（其内置的下限永远为 0），这段代码实现起来更复杂。所以我们要生成额外的值来过滤小于 5 的结果。

注意，你必须研究代码才能搞清楚`ImperativeRandoms.java`程序在做什么。而在 `Randoms.java` 中，代码会直接告诉你它在做什么。这种语义的清晰性是使用Java 8 流式编程的重要原因之一。

像在 `ImperativeRandoms.java` 中那样显式地编写迭代过程的方式称为*外部迭代（external iteration）*。而在 `Randoms.java` 中，你看不到任何上述的迭代过程，所以它被称为*内部迭代（internal iteration）*，这是流式编程的一个核心特征。内部迭代产生的代码可读性更强，而且能更简单的使用多核处理器。通过放弃对迭代过程的控制，可以把控制权交给并行化机制。我们将在[并发编程](24-Concurrent-Programming.md)一章中学习这部分内容。

另一个重要方面，流是懒加载的。这代表着它只在绝对必要时才计算。你可以将流看作“延迟列表”。由于计算延迟，流使我们能够表示非常大（甚至无限）的序列，而不需要考虑内存问题。

<!-- Java 8 Stream Support -->

## 流支持

Java 设计者面临着这样一个难题：现存的大量类库不仅为 Java 所用，同时也被应用在整个 Java 生态圈数百万行的代码中。如何将一个全新的流的概念融入到现有类库中呢？

比如在 **Random** 中添加更多的方法。只要不改变原有的方法，现有代码就不会受到干扰。

一个大的挑战来自于使用接口的库。集合类是其中关键的一部分，因为你想把集合转为流。但是如果你将一个新方法添加到接口，那就破坏了每一个实现接口的类，因为这些类都没有实现你添加的新方法。

Java 8 采用的解决方案是：在[接口](10-Interfaces.md)中添加被 `default`（`默认`）修饰的方法。通过这种方案，设计者们可以将流式（*stream*）方法平滑地嵌入到现有类中。流方法预置的操作几乎已满足了我们平常所有的需求。流操作的类型有三种：创建流，修改流元素（中间操作， Intermediate Operations），消费流元素（终端操作， Terminal Operations）。最后一种类型通常意味着收集流元素（通常是汇入一个集合）。

下面我们来看下每种类型的流操作。

<!-- Stream Creation -->
## 流创建

你可以通过 `Stream.of()` 很容易地将一组元素转化成为流（`Bubble` 类在本章的后面定义）：

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

输出结果：

```
Bubble(1)
Bubble(2)
Bubble(3)
It's a wonderful day for pie!
3.14159
2.718
1.618
```

除此之外，每个集合都可以通过调用 `stream()` 方法来产生一个流。代码示例：

```java
// streams/CollectionToStream.java
import java.util.*;
import java.util.stream.*;
public class CollectionToStream {
    public static void main(String[] args) {
        List<Bubble> bubbles = Arrays.asList(new Bubble(1), new Bubble(2), new Bubble(3));
        System.out.println(bubbles.stream()
            .mapToInt(b -> b.i)
            .sum());
        
        Set<String> w = new HashSet<>(Arrays.asList("It's a wonderful day for pie!".split(" ")));
        w.stream()
         .map(x -> x + " ")
         .forEach(System.out::print);
        System.out.println();
        
        Map<String, Double> m = new HashMap<>();
        m.put("pi", 3.14159);
        m.put("e", 2.718);
        m.put("phi", 1.618);
        m.entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .forEach(System.out::println);
    }
}
```

输出结果：

```
6
a pie! It's for wonderful day
phi: 1.618
e: 2.718
pi: 3.14159
```

在创建 `List<Bubble>` 对象之后，我们只需要简单地调用所有集合中都有的 `stream()`。中间操作 `map()` 会获取流中的所有元素，并且对流中元素应用操作从而产生新的元素，并将其传递到后续的流中。通常 `map()` 会获取对象并产生新的对象，但在这里产生了特殊的用于数值类型的流。例如，`mapToInt()` 方法将一个对象流（object stream）转换成为包含整型数字的 `IntStream`。同样，针对 `Float` 和 `Double` 也有类似名字的操作。

我们通过调用字符串的 `split()`（该方法会根据参数来拆分字符串）来获取元素用于定义变量 `w`。稍后你会知道 `split()` 参数可以是十分复杂，但在这里我们只是根据空格来分割字符串。

为了从 **Map** 集合中产生流数据，我们首先调用 `entrySet()` 产生一个对象流，每个对象都包含一个 `key` 键以及与其相关联的 `value` 值。然后分别调用 `getKey()` 和 `getValue()` 获取值。

### 随机数流

`Random` 类被一组生成流的方法增强了。代码示例：

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
        // 控制上限和下限：
        show(rand.ints(10, 20).boxed());
        show(rand.longs(50, 100).boxed());
        show(rand.doubles(20, 30).boxed());
        // 控制流大小：
        show(rand.ints(2).boxed());
        show(rand.longs(2).boxed());
        show(rand.doubles(2).boxed());
        // 控制流的大小和界限
        show(rand.ints(3, 3, 9).boxed());
        show(rand.longs(3, 12, 22).boxed());
        show(rand.doubles(3, 11.5, 12.3).boxed());
    }
}
```

输出结果：

```
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

为了消除冗余代码，我创建了一个泛型方法 `show(Stream<T> stream)` （在讲解泛型之前就使用这个特性，确实有点作弊，但是回报是值得的）。类型参数 `T` 可以是任何类型，所以这个方法对 **Integer**、**Long** 和 **Double** 类型都生效。但是 **Random** 类只能生成基本类型 **int**， **long**， **double** 的流。幸运的是， `boxed()` 流操作将会自动地把基本类型包装成为对应的装箱类型，从而使得 `show()` 能够接受流。

我们可以使用 **Random** 为任意对象集合创建 **Supplier**。从文本文件提供字符串对象的例子如下。

Cheese.dat 文件内容：

```
// streams/Cheese.dat
Not much of a cheese shop really, is it?
Finest in the district, sir.
And what leads you to that conclusion?
Well, it's so clean.
It's certainly uncontaminated by cheese.
```

我们通过 **File** 类将 Cheese.dat 文件的所有行读取到 `List<String>` 中。代码示例：

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
        // 略过第一行
        for (String line : lines.subList(1, lines.size())) {
            for (String word : line.split("[ .?,]+"))
                words.add(word.toLowerCase());
        }
    }
    public String get() {
        return words.get(rand.nextInt(words.size()));
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

输出结果：

```
it shop sir the much cheese by conclusion district is
```

在这里可以看到 `split()` 更复杂的运用。在构造器里，每一行都被 `split()` 通过方括号内的空格或其它标点符号分割。在方括号后面的 `+` 表示 `+` 前面的东西可以出现一次或者多次（正则表达式）。

你会发现构造函数使用命令式编程（外部迭代）进行循环。在以后的例子中，你会看到我们是如何去除命令式编程。这种旧的形式虽不是特别糟糕，但使用流会让人感觉更好。

在`toString()` 和`main()`方法中你看到了 `collect()` 操作，它根据参数来结合所有的流元素。当你用 `Collectors.joining()`作为 `collect()` 的参数时，将得到一个`String` 类型的结果，该结果是流中的所有元素被`joining()`的参数隔开。还有很多不同的 `Collectors` 用于产生不同的结果。

在主方法中，我们提前看到了 **Stream.**`generate()` 的用法，它可以把任意  `Supplier<T>` 用于生成 `T` 类型的流。


### int 类型的范围

`IntStream` 类提供了  `range()` 方法用于生成整型序列的流。编写循环时，这个方法会更加便利：

```java
// streams/Ranges.java
import static java.util.stream.IntStream.*;
public class Ranges {
    public static void main(String[] args) {
        // 传统方法:
        int result = 0;
        for (int i = 10; i < 20; i++)
            result += i;
        System.out.println(result);
        // for-in 循环:
        result = 0;
        for (int i : range(10, 20).toArray())
            result += i;
        System.out.println(result);
        // 使用流:
        System.out.println(range(10, 20).sum());
    }
}
```

输出结果：

```
145
145
145
```

在主方法中的第一种方式是我们传统编写 `for` 循环的方式；第二种方式，我们使用 `range()` 创建了流并将其转化为数组，然后在 `for-in` 代码块中使用。但是，如果你能像第三种方法那样全程使用流是更好的。我们对范围中的数字进行求和。在流中可以很方便的使用 `sum()` 操作求和。

注意 **IntStream.**`range()` 相比 `onjava.Range.range()` 受更多限制。这是由于其可选的第三个参数，后者允许步长大于 1，并且可以从大到小来生成。

实用小功能 `repeat()` 可以用来替换简单的 `for` 循环。代码示例：

```java
// onjava/Repeat.java
package onjava;
import static java.util.stream.IntStream.*;
public class Repeat {
    public static void repeat(int n, Runnable action) {
        range(0, n).forEach(i -> action.run());
    }
}
```

其产生的循环更加清晰：

```java
// streams/Looping.java
import static onjava.Repeat.*;
public class Looping {
    static void hi() {
        System.out.println("Hi!");
    }
    public static void main(String[] args) {
        repeat(3, () -> System.out.println("Looping!"));
        repeat(2, Looping::hi);
    }
}
```

输出结果：

```
Looping!
Looping!
Looping!
Hi!
Hi!
```

原则上，在代码中包含和解释 `repeat()` 并不值得。诚然它是一个相当透明的工具，但这取决于你的团队和公司的运作方式。

### generate()

参照 `RandomWords.java` 中 **Stream.**`generate()` 搭配 `Supplier<T>` 使用的例子。代码示例：

```java
// streams/Generator.java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Generator implements Supplier<String> {
    Random rand = new Random(47);
    char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    
    public String get() {
        return "" + letters[rand.nextInt(letters.length)];
    }
    
    public static void main(String[] args) {
        String word = Stream.generate(new Generator())
                            .limit(30)
                            .collect(Collectors.joining());
        System.out.println(word);
    }
}
```

输出结果：

```
YNZBRNYGCFOWZNTCQRGSEGZMMJMROE
```

使用 `Random.nextInt()` 方法来挑选字母表中的大写字母。`Random.nextInt()` 的参数代表可以接受的最大的随机数范围，所以使用数组边界是经过慎重考虑的。

如果要创建包含相同对象的流，只需要传递一个生成那些对象的 `lambda` 到 `generate()` 中：

```java
// streams/Duplicator.java
import java.util.stream.*;
public class Duplicator {
    public static void main(String[] args) {
        Stream.generate(() -> "duplicate")
              .limit(3)
              .forEach(System.out::println);
    }
}
```

输出结果：

```
duplicate
duplicate
duplicate
```

如下是在本章之前例子中使用过的 `Bubble` 类。**注意**它包含了自己的静态生成器（Static generator）方法。

```java
// streams/Bubble.java
import java.util.function.*;
public class Bubble {
    public final int i;
    
    public Bubble(int n) {
        i = n;
    }
    
    @Override
    public String toString() {
        return "Bubble(" + i + ")";
    }
    
    private static int count = 0;
    public static Bubble bubbler() {
        return new Bubble(count++);
    }
}
```

由于 `bubbler()` 与 `Supplier<Bubble>` 是接口兼容的，我们可以将其方法引用直接传递给 **Stream.**`generate()`：

```java
// streams/Bubbles.java
import java.util.stream.*;
public class Bubbles {
    public static void main(String[] args) {
        Stream.generate(Bubble::bubbler)
              .limit(5)
              .forEach(System.out::println);
    }
}
```

输出结果：

```
Bubble(0)
Bubble(1)
Bubble(2)
Bubble(3)
Bubble(4)
```

这是创建单独工厂类（Separate Factory class）的另一种方式。在很多方面它更加整洁，但是这是一个关于代码组织和品味的问题——你总是可以创建一个完全不同的工厂类。

### iterate()

`Stream.iterate()` 产生的流的第一个元素是种子（iterate方法的第一个参数），然后将种子传递给方法（iterate方法的第二个参数）。方法运行的结果被添加到流（作为流的下一个元素），并被存储起来，作为下次调用 `iterate()`方法时的第一个参数，以此类推。我们可以利用 `iterate()` 生成一个斐波那契数列（上一章已经遇到过Fibonacci）。代码示例：

```java
// streams/Fibonacci.java
import java.util.stream.*;
public class Fibonacci {
    int x = 1;
    
    Stream<Integer> numbers() {
        return Stream.iterate(0, i -> {
            int result = x + i;
            x = i;
            return result;
        });
    }
    
    public static void main(String[] args) {
        new Fibonacci().numbers()
                       .skip(20) // 过滤前 20 个
                       .limit(10) // 然后取 10 个
                       .forEach(System.out::println);
    }
}
```

输出结果：

```
6765
10946
17711
28657
46368
75025
121393
196418
317811
514229
```

斐波那契数列将数列中最后两个元素进行求和以产生下一个元素。`iterate()` 只能记忆结果，因此我们需要利用一个变量 `x` 追踪另外一个元素。

在主方法中，我们使用了一个之前没有见过的 `skip()` 操作。它根据参数丢弃指定数量的流元素。在这里，我们丢弃了前 20 个元素。

### 流的建造者模式

在*建造者模式*（Builder design pattern）中，首先创建一个 `builder` 对象，然后将创建流所需的多个信息传递给它，最后`builder` 对象执行”创建“流的操作。**Stream** 库提供了这样的 `Builder`。在这里，我们重新审视文件读取并将其转换成为单词流的过程。代码示例：

```java
// streams/FileToWordsBuilder.java
import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class FileToWordsBuilder {
    Stream.Builder<String> builder = Stream.builder();
    
    public FileToWordsBuilder(String filePath) throws Exception {
        Files.lines(Paths.get(filePath))
             .skip(1) // 略过开头的注释行
             .forEach(line -> {
                  for (String w : line.split("[ .?,]+"))
                      builder.add(w);
              });
    }
    
    Stream<String> stream() {
        return builder.build();
    }
    
    public static void main(String[] args) throws Exception {
        new FileToWordsBuilder("Cheese.dat")
            .stream()
            .limit(7)
            .map(w -> w + " ")
            .forEach(System.out::print);
    }
}
```

输出结果：

```
Not much of a cheese shop really
```

**注意**，构造器会添加文件中的所有单词（除了第一行，它是包含文件路径信息的注释），但是其并没有调用 `build()`。只要你不调用 `stream()` 方法，就可以继续向 `builder` 对象中添加单词。

在该类的更完整形式中，你可以添加一个标志位用于查看 `build()` 是否被调用，并且可能的话增加一个可以添加更多单词的方法。在 `Stream.Builder` 调用 `build()` 方法后继续尝试添加单词会产生一个异常。

### Arrays

`Arrays` 类中含有一个名为 `stream()` 的静态方法用于把数组转换成为流。我们可以重写 `interfaces/Machine.java` 中的主方法用于创建一个流，并将 `execute()` 应用于每一个元素。代码示例：

```java
// streams/Machine2.java
import java.util.*;
import onjava.Operations;
public class Machine2 {
    public static void main(String[] args) {
        Arrays.stream(new Operations[] {
            () -> Operations.show("Bing"),
            () -> Operations.show("Crack"),
            () -> Operations.show("Twist"),
            () -> Operations.show("Pop")
        }).forEach(Operations::execute);
    }
}
```

输出结果：

```
Bing
Crack
Twist
Pop
```

`new Operations[]` 表达式动态创建了 `Operations` 对象的数组。

`stream()` 同样可以产生 **IntStream**，**LongStream** 和 **DoubleStream**。

```java
// streams/ArrayStreams.java
import java.util.*;
import java.util.stream.*;

public class ArrayStreams {
    public static void main(String[] args) {
        Arrays.stream(new double[] { 3.14159, 2.718, 1.618 })
            .forEach(n -> System.out.format("%f ", n));
        System.out.println();
        
        Arrays.stream(new int[] { 1, 3, 5 })
            .forEach(n -> System.out.format("%d ", n));
        System.out.println();
        
        Arrays.stream(new long[] { 11, 22, 44, 66 })
            .forEach(n -> System.out.format("%d ", n));
        System.out.println();
        
        // 选择一个子域:
        Arrays.stream(new int[] { 1, 3, 5, 7, 15, 28, 37 }, 3, 6)
            .forEach(n -> System.out.format("%d ", n));
    }
}
```

输出结果：

```
3.141590 2.718000 1.618000
1 3 5
11 22 44 66
7 15 28
```

最后一次 `stream()` 的调用有两个额外的参数。第一个参数告诉 `stream()` 从数组的哪个位置开始选择元素，第二个参数用于告知在哪里停止。每种不同类型的 `stream()` 都有类似的操作。

### 正则表达式

Java 的正则表达式将在[字符串](18-Strings.md)这一章节详细介绍。Java 8 在 `java.util.regex.Pattern` 中增加了一个新的方法 `splitAsStream()`。这个方法可以根据传入的公式将字符序列转化为流。但是有一个限制，输入只能是 **CharSequence**，因此不能将流作为 `splitAsStream()` 的参数。

我们再一次查看将文件转换为单词的过程。这一次，我们使用流将文件转换为一个字符串，接着使用正则表达式将字符串转化为单词流。

```java
// streams/FileToWordsRegexp.java
import java.io.*;
import java.nio.file.*;
import java.util.stream.*;
import java.util.regex.Pattern;
public class FileToWordsRegexp {
    private String all;
    public FileToWordsRegexp(String filePath) throws Exception {
        all = Files.lines(Paths.get(filePath))
        .skip(1) // First (comment) line
        .collect(Collectors.joining(" "));
    }
    public Stream<String> stream() {
        return Pattern
        .compile("[ .,?]+").splitAsStream(all);
    }
    public static void
    main(String[] args) throws Exception {
        FileToWordsRegexp fw = new FileToWordsRegexp("Cheese.dat");
        fw.stream()
          .limit(7)
          .map(w -> w + " ")
          .forEach(System.out::print);
        fw.stream()
          .skip(7)
          .limit(2)
          .map(w -> w + " ")
          .forEach(System.out::print);
    }
}
```

输出结果：

```
Not much of a cheese shop really is it
```

在构造器中我们读取了文件中的所有内容（跳过第一行注释，并将其转化成为单行字符串）。现在，当你调用 `stream()` 的时候，可以像往常一样获取一个流，但这回你可以多次调用 `stream()` ，每次从已存储的字符串中创建一个新的流。这里有个限制，整个文件必须存储在内存中；在大多数情况下这并不是什么问题，但是这丢掉了流操作非常重要的优势：

1. “不需要把流存储起来。”当然，流确实需要一些内部存储，但存储的只是序列的一小部分，和存储整个序列不同。
2. 它们是懒加载计算的。

幸运的是，我们稍后就会知道如何解决这个问题。

<!-- Intermediate Operations -->

## 中间操作

中间操作用于从一个流中获取对象，并将对象作为另一个流从后端输出，以连接到其他操作。

### 跟踪和调试

`peek()` 操作的目的是帮助调试。它允许你无修改地查看流中的元素。代码示例：

```java
// streams/Peeking.java
class Peeking {
    public static void main(String[] args) throws Exception {
        FileToWords.stream("Cheese.dat")
        .skip(21)
        .limit(4)
        .map(w -> w + " ")
        .peek(System.out::print)
        .map(String::toUpperCase)
        .peek(System.out::print)
        .map(String::toLowerCase)
        .forEach(System.out::print);
    }
}
```

输出结果：

```
Well WELL well it IT it s S s so SO so
```

`FileToWords` 稍后定义，但它的功能实现貌似和之前我们看到的差不多：产生字符串对象的流。之后在其通过管道时调用 `peek()` 进行处理。

因为 `peek()` 符合无返回值的 **Consumer** 函数式接口，所以我们只能观察，无法使用不同的元素来替换流中的对象。

### 流元素排序

在 `Randoms.java` 中，我们熟识了 `sorted()` 的默认比较器实现。其实它还有另一种形式的实现：传入一个 **Comparator** 参数。代码示例：

```java
// streams/SortedComparator.java
import java.util.*;
public class SortedComparator {
    public static void main(String[] args) throws Exception {
        FileToWords.stream("Cheese.dat")
        .skip(10)
        .limit(10)
        .sorted(Comparator.reverseOrder())
        .map(w -> w + " ")
        .forEach(System.out::print);
    }
}
```

输出结果：

```
you what to the that sir leads in district And
```

`sorted()` 预设了一些默认的比较器。这里我们使用的是反转“自然排序”。当然你也可以把 Lambda 函数作为参数传递给 `sorted()`。

### 移除元素

* `distinct()`：在 `Randoms.java` 类中的 `distinct()` 可用于消除流中的重复元素。相比创建一个 **Set** 集合来消除重复，该方法的工作量要少得多。

* `filter(Predicate)`：过滤操作，保留如下元素：若元素传递给过滤函数产生的结果为`true` 。

在下例中，`isPrime()` 作为过滤函数，用于检测质数。

```java
// streams/Prime.java
import java.util.stream.*;
import static java.util.stream.LongStream.*;
public class Prime {
    public static Boolean isPrime(long n) {
        return rangeClosed(2, (long)Math.sqrt(n))
        .noneMatch(i -> n % i == 0);
    }
    public LongStream numbers() {
        return iterate(2, i -> i + 1)
        .filter(Prime::isPrime);
    }
    public static void main(String[] args) {
        new Prime().numbers()
        .limit(10)
        .forEach(n -> System.out.format("%d ", n));
        System.out.println();
        new Prime().numbers()
        .skip(90)
        .limit(10)
        .forEach(n -> System.out.format("%d ", n));
    }
}
```

输出结果：

```
2 3 5 7 11 13 17 19 23 29
467 479 487 491 499 503 509 521 523 541
```

`rangeClosed()` 包含了上限值。如果不能整除，即余数不等于 0，则 `noneMatch()` 操作返回 `true`，如果出现任何等于 0 的结果则返回 `false`。 `noneMatch()` 操作一旦有失败就会退出。

### 应用函数到元素

- `map(Function)`：将函数操作应用在输入流的元素中，并将返回值传递到输出流中。

- `mapToInt(ToIntFunction)`：操作同上，但结果是 **IntStream**。

- `mapToLong(ToLongFunction)`：操作同上，但结果是 **LongStream**。

- `mapToDouble(ToDoubleFunction)`：操作同上，但结果是 **DoubleStream**。

在这里，我们使用 `map()` 映射多种函数到一个字符串流中。代码示例：

```java
// streams/FunctionMap.java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
class FunctionMap {
    static String[] elements = { "12", "", "23", "45" };
    static Stream<String>
    testStream() {
        return Arrays.stream(elements);
    }
    static void test(String descr, Function<String, String> func) {
        System.out.println(" ---( " + descr + " )---");
        testStream()
        .map(func)
        .forEach(System.out::println);
    }
    public static void main(String[] args) {
        test("add brackets", s -> "[" + s + "]");
        test("Increment", s -> {
            try {
                return Integer.parseInt(s) + 1 + "";
            }
            catch(NumberFormatException e) {
                return s;
            }
        }
        );
        test("Replace", s -> s.replace("2", "9"));
        test("Take last digit", s -> s.length() > 0 ?
        s.charAt(s.length() - 1) + "" : s);
    }
}
```

输出结果：

```
 ---( add brackets )---
[12]
[]
[23]
[45]
 ---( Increment )---
13

24
46
 ---( Replace )---
19

93
45
 ---( Take last digit )---
2

3
5

```

在上面的自增示例中，我们用 `Integer.parseInt()` 尝试将一个字符串转化为整数。如果字符串不能被转化成为整数就会抛出 `NumberFormatException` 异常，此时我们就回过头来把原始字符串放到输出流中。

在以上例子中，`map()` 将一个字符串映射为另一个字符串，但是我们完全可以产生和接收类型完全不同的类型，从而改变流的数据类型。下面代码示例：

```java
// streams/FunctionMap2.java
// Different input and output types （不同的输入输出类型）
import java.util.*;
import java.util.stream.*;
class Numbered {
    final int n;
    Numbered(int n) {
        this.n = n;
    }
    @Override
    public String toString() {
        return "Numbered(" + n + ")";
    }
}
class FunctionMap2 {
    public static void main(String[] args) {
        Stream.of(1, 5, 7, 9, 11, 13)
        .map(Numbered::new)
        .forEach(System.out::println);
    }
}
```

输出结果：

```
Numbered(1)
Numbered(5)
Numbered(7)
Numbered(9)
Numbered(11)
Numbered(13)
```

我们将获取到的整数通过构造器 `Numbered::new` 转化成为 `Numbered` 类型。

如果使用 **Function** 返回的结果是数值类型的一种，我们必须使用合适的 `mapTo数值类型` 进行替代。代码示例：

```java
// streams/FunctionMap3.java
// Producing numeric output streams（ 产生数值输出流）
import java.util.*;
import java.util.stream.*;
class FunctionMap3 {
    public static void main(String[] args) {
        Stream.of("5", "7", "9")
        .mapToInt(Integer::parseInt)
        .forEach(n -> System.out.format("%d ", n));
        System.out.println();
        Stream.of("17", "19", "23")
        .mapToLong(Long::parseLong)
        .forEach(n -> System.out.format("%d ", n));
        System.out.println();
        Stream.of("17", "1.9", ".23")
        .mapToDouble(Double::parseDouble)
        .forEach(n -> System.out.format("%f ", n));
    }
}
```

输出结果：

```
5 7 9
17 19 23
17.000000 1.900000 0.230000
```

遗憾的是，Java 设计者并没有尽最大努力去消除基本类型。

### 在 `map()` 中组合流

假设我们现在有了一个传入的元素流，并且打算对流元素使用 `map()` 函数。现在你已经找到了一些可爱并独一无二的函数功能，但是问题来了：这个函数功能是产生一个流。我们想要产生一个元素流，而实际却产生了一个元素流的流。

`flatMap()` 做了两件事：将产生流的函数应用在每个元素上（与 `map()` 所做的相同），然后将每个流都扁平化为元素，因而最终产生的仅仅是元素。

`flatMap(Function)`：当 `Function` 产生流时使用。

`flatMapToInt(Function)`：当 `Function` 产生 `IntStream` 时使用。

`flatMapToLong(Function)`：当 `Function` 产生 `LongStream` 时使用。

`flatMapToDouble(Function)`：当 `Function` 产生 `DoubleStream` 时使用。

为了弄清它的工作原理，我们从传入一个刻意设计的函数给  `map()` 开始。该函数接受一个整数并产生一个字符串流：

```java
// streams/StreamOfStreams.java
import java.util.stream.*;
public class StreamOfStreams {
    public static void main(String[] args) {
        Stream.of(1, 2, 3)
        .map(i -> Stream.of("Gonzo", "Kermit", "Beaker"))
        .map(e-> e.getClass().getName())
        .forEach(System.out::println);
    }
}
```

输出结果：

```
java.util.stream.ReferencePipeline$Head
java.util.stream.ReferencePipeline$Head
java.util.stream.ReferencePipeline$Head
```

我们天真地希望能够得到字符串流，但实际得到的却是“Head”流的流。我们可以使用 `flatMap()` 解决这个问题：

```java
// streams/FlatMap.java
import java.util.stream.*;
public class FlatMap {
    public static void main(String[] args) {
        Stream.of(1, 2, 3)
        .flatMap(i -> Stream.of("Gonzo", "Fozzie", "Beaker"))
        .forEach(System.out::println);
    }
}
```

输出结果：

```
Gonzo
Fozzie
Beaker
Gonzo
Fozzie
Beaker
Gonzo
Fozzie
Beaker
```

从映射返回的每个流都会自动扁平为组成它的字符串。

下面是另一个演示，我们从一个整数流开始，然后使用每一个整数去创建更多的随机数。

```java
// streams/StreamOfRandoms.java
import java.util.*;
import java.util.stream.*;
public class StreamOfRandoms {
    static Random rand = new Random(47);
    public static void main(String[] args) {
        Stream.of(1, 2, 3, 4, 5)
            .flatMapToInt(i -> IntStream.concat(
        rand.ints(0, 100).limit(i), IntStream.of(-1)))
            .forEach(n -> System.out.format("%d ", n));
    }
}
```

输出结果：

```
58 -1 55 93 -1 61 61 29 -1 68 0 22 7 -1 88 28 51 89 9 -1
```

在这里我们引入了 `concat()`，它以参数顺序组合两个流。 如此，我们在每个随机 `Integer` 流的末尾添加一个 -1 作为标记。你可以看到最终流确实是从一组扁平流中创建的。

因为 `rand.ints()` 产生的是一个 `IntStream`，所以我必须使用 `flatMap()`、`concat()` 和 `of()` 的特定整数形式。

让我们再看一下将文件划分为单词流的任务。我们最后使用到的是 **FileToWordsRegexp.java**，它的问题是需要将整个文件读入行列表中 —— 显然需要存储该列表。而我们真正想要的是创建一个不需要中间存储层的单词流。

下面，我们再使用 ` flatMap()` 来解决这个问题：

```java
// streams/FileToWords.java
import java.nio.file.*;
import java.util.stream.*;
import java.util.regex.Pattern;
public class FileToWords {
    public static Stream<String> stream(String filePath) throws Exception {
        return Files.lines(Paths.get(filePath))
        .skip(1) // First (comment) line
        .flatMap(line ->
        Pattern.compile("\\W+").splitAsStream(line));
    }
}
```

`stream()` 现在是一个静态方法，因为它可以自己完成整个流创建过程。

注意：`\\W+` 是一个正则表达式。表示“非单词字符”，`+` 表示“可以出现一次或者多次”。小写形式的 `\\w` 表示“单词字符”。

我们之前遇到的问题是 `Pattern.compile().splitAsStream()` 产生的结果为流，这意味着当我们只是想要一个简单的单词流时，在传入的行流（stream of lines）上调用 `map()` 会产生一个单词流的流。幸运的是，`flatMap()`  可以将元素流的流扁平化为一个简单的元素流。或者，我们可以使用 `String.split()` 生成一个数组，其可以被 `Arrays.stream()` 转化成为流：

```java
.flatMap(line -> Arrays.stream(line.split("\\W+"))))
```

因为有了真正的流（而不是`FileToWordsRegexp.java` 中基于集合存储的流），所以每次需要一个新的流时，我们都必须从头开始创建，因为流不能被复用：

```java
// streams/FileToWordsTest.java
import java.util.stream.*;
public class FileToWordsTest {
    public static void main(String[] args) throws Exception {
        FileToWords.stream("Cheese.dat")
        .limit(7)
        .forEach(s -> System.out.format("%s ", s));
        System.out.println();
        FileToWords.stream("Cheese.dat")
        .skip(7)
        .limit(2)
        .forEach(s -> System.out.format("%s ", s));
    }
}
```

输出结果：

```
Not much of a cheese shop really
is it
```

在 `System.out.format()` 中的 `%s` 表明参数为 **String** 类型。

<!-- Optional -->
## Optional类

在我们学习终端操作（Terminal Operations）之前，我们必须考虑在一个空流中获取元素会发生什么。我们喜欢沿着“快乐路径”[^1]把流连接起来，同时假设流不会中断。然而，在流中放置 `null` 却会轻易令其中断。那么是否存在某种对象，可以在持有流元素的同时，即使在我们查找的元素不存在时，也能友好地对我们进行提示（也就是说，不会产生异常）？

**Optional** 可以实现这样的功能。一些标准流操作返回 **Optional** 对象，因为它们并不能保证预期结果一定存在。包括：

- `findFirst()` 返回一个包含第一个元素的 **Optional** 对象，如果流为空则返回 **Optional.empty**
- `findAny()` 返回包含任意元素的 **Optional** 对象，如果流为空则返回 **Optional.empty**
- `max()` 和 `min()` 返回一个包含最大值或者最小值的 **Optional** 对象，如果流为空则返回 **Optional.empty**

 `reduce()` 不再以 `identity` 形式开头，而是将其返回值包装在 **Optional** 中。（`identity` 对象成为其他形式的 `reduce()` 的默认结果，因此不存在空结果的风险）

对于数字流 **IntStream**、**LongStream** 和 **DoubleStream**，`average()` 会将结果包装在 **Optional** 以防止流为空。

以下是对空流进行所有这些操作的简单测试：

```java
// streams/OptionalsFromEmptyStreams.java
import java.util.*;
import java.util.stream.*;
class OptionalsFromEmptyStreams {
    public static void main(String[] args) {
        System.out.println(Stream.<String>empty()
             .findFirst());
        System.out.println(Stream.<String>empty()
             .findAny());
        System.out.println(Stream.<String>empty()
             .max(String.CASE_INSENSITIVE_ORDER));
        System.out.println(Stream.<String>empty()
             .min(String.CASE_INSENSITIVE_ORDER));
        System.out.println(Stream.<String>empty()
             .reduce((s1, s2) -> s1 + s2));
        System.out.println(IntStream.empty()
             .average());
    }
}
```

输出结果：

```
Optional.empty
Optional.empty
Optional.empty
Optional.empty
Optional.empty
OptionalDouble.empty
```

当流为空的时候你会获得一个 **Optional.empty** 对象，而不是抛出异常。**Optional** 拥有 `toString()` 方法可以用于展示有用信息。

注意，空流是通过 `Stream.<String>empty()` 创建的。如果你在没有任何上下文环境的情况下调用 `Stream.empty()`，Java 并不知道它的数据类型；这个语法解决了这个问题。如果编译器拥有了足够的上下文信息，比如：

```java
Stream<String> s = Stream.empty();
```

就可以在调用 `empty()` 时推断类型。

这个示例展示了 **Optional** 的两个基本用法：

```java
// streams/OptionalBasics.java
import java.util.*;
import java.util.stream.*;
class OptionalBasics {
    static void test(Optional<String> optString) {
        if(optString.isPresent())
            System.out.println(optString.get()); 
        else
            System.out.println("Nothing inside!");
    }
    public static void main(String[] args) {
        test(Stream.of("Epithets").findFirst());
        test(Stream.<String>empty().findFirst());
    }
}
```

输出结果：

```
Epithets
Nothing inside!
```

当你接收到 **Optional** 对象时，应首先调用 `isPresent()` 检查其中是否包含元素。如果存在，可使用 `get()` 获取。

<!-- Convenience Functions -->

### 便利函数

有许多便利函数可以解包 **Optional** ，这简化了上述“对所包含的对象的检查和执行操作”的过程：

- `ifPresent(Consumer)`：当值存在时调用 **Consumer**，否则什么也不做。
- `orElse(otherObject)`：如果值存在则直接返回，否则生成 **otherObject**。
- `orElseGet(Supplier)`：如果值存在则直接返回，否则使用 **Supplier** 函数生成一个可替代对象。
- `orElseThrow(Supplier)`：如果值存在直接返回，否则使用 **Supplier** 函数生成一个异常。

如下是针对不同便利函数的简单演示：

```java
// streams/Optionals.java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
public class Optionals {
    static void basics(Optional<String> optString) {
        if(optString.isPresent())
            System.out.println(optString.get()); 
        else
            System.out.println("Nothing inside!");
    }
    static void ifPresent(Optional<String> optString) {
        optString.ifPresent(System.out::println);
    }
    static void orElse(Optional<String> optString) {
        System.out.println(optString.orElse("Nada"));
    }
    static void orElseGet(Optional<String> optString) {
        System.out.println(
        optString.orElseGet(() -> "Generated"));
    }
    static void orElseThrow(Optional<String> optString) {
        try {
            System.out.println(optString.orElseThrow(
            () -> new Exception("Supplied")));
        } catch(Exception e) {
            System.out.println("Caught " + e);
        }
    }
    static void test(String testName, Consumer<Optional<String>> cos) {
        System.out.println(" === " + testName + " === ");
        cos.accept(Stream.of("Epithets").findFirst());
        cos.accept(Stream.<String>empty().findFirst());
    }
    public static void main(String[] args) {
        test("basics", Optionals::basics);
        test("ifPresent", Optionals::ifPresent);
        test("orElse", Optionals::orElse);
        test("orElseGet", Optionals::orElseGet);
        test("orElseThrow", Optionals::orElseThrow);
    }
}
```

输出结果：

```
=== basics ===
Epithets
Nothing inside!
=== ifPresent ===
Epithets
=== orElse ===
Epithets
Nada
=== orElseGet ===
Epithets
Generated
=== orElseThrow ===
Epithets
Caught java.lang.Exception: Supplied
```

`test()` 通过传入所有方法都适用的 **Consumer** 来避免重复代码。

`orElseThrow()` 通过 **catch** 关键字来捕获抛出的异常。更多细节，将在 [异常](./15-Exceptions.md) 这一章节中学习。

<!-- Creating Optionals -->

### 创建 Optional

当我们在自己的代码中加入 **Optional** 时，可以使用下面 3 个静态方法：

- `empty()`：生成一个空 **Optional**。
- `of(value)`：将一个非空值包装到 **Optional** 里。
- `ofNullable(value)`：针对一个可能为空的值，为空时自动生成 **Optional.empty**，否则将值包装在 **Optional** 中。

下面来看看它是如何工作的。代码示例：

```java
// streams/CreatingOptionals.java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
class CreatingOptionals {
    static void test(String testName, Optional<String> opt) {
        System.out.println(" === " + testName + " === ");
        System.out.println(opt.orElse("Null"));
    }
    public static void main(String[] args) {
        test("empty", Optional.empty());
        test("of", Optional.of("Howdy"));
        try {
            test("of", Optional.of(null));
        } catch(Exception e) {
            System.out.println(e);
        }
        test("ofNullable", Optional.ofNullable("Hi"));
        test("ofNullable", Optional.ofNullable(null));
    }
}
```

输出结果：

```
=== empty ===
Null
=== of ===
Howdy
java.lang.NullPointerException
=== ofNullable ===
Hi
=== ofNullable ===
Null
```

我们不能通过传递 `null` 到 `of()` 来创建 `Optional` 对象。最安全的方法是， 使用 `ofNullable()` 来优雅地处理 `null`。

### Optional 对象操作

当我们的流管道生成了 **Optional** 对象，下面 3 个方法可使得 **Optional** 的后续能做更多的操作：

- `filter(Predicate)`：对 **Optional** 中的内容应用**Predicate** 并将结果返回。如果 **Optional** 不满足 **Predicate** ，将 **Optional** 转化为空 **Optional** 。如果 **Optional** 已经为空，则直接返回空**Optional** 。

- `map(Function)`：如果 **Optional** 不为空，应用 **Function**  于 **Optional** 中的内容，并返回结果。否则直接返回 **Optional.empty**。

- `flatMap(Function)`：同 `map()`，但是提供的映射函数将结果包装在 **Optional** 对象中，因此 `flatMap()` 不会在最后进行任何包装。

以上方法都不适用于数值型 **Optional**。一般来说，流的 `filter()` 会在 **Predicate** 返回 `false` 时移除流元素。而 `Optional.filter()` 在失败时不会删除 **Optional**，而是将其保留下来，并转化为空。下面请看代码示例：


```java
// streams/OptionalFilter.java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
class OptionalFilter {
    static String[] elements = {
            "Foo", "", "Bar", "Baz", "Bingo"
    };
    static Stream<String> testStream() {
        return Arrays.stream(elements);
    }
    static void test(String descr, Predicate<String> pred) {
        System.out.println(" ---( " + descr + " )---");
        for(int i = 0; i <= elements.length; i++) {
            System.out.println(
                    testStream()
                            .skip(i)
                            .findFirst()
                            .filter(pred));
        }
    }
    public static void main(String[] args) {
        test("true", str -> true);
        test("false", str -> false);
        test("str != \"\"", str -> str != "");
        test("str.length() == 3", str -> str.length() == 3);
        test("startsWith(\"B\")",
                str -> str.startsWith("B"));
    }
}
```

输出结果：

```
---( true )---
Optional[Foo]
Optional[]
Optional[Bar]
Optional[Baz]
Optional[Bingo]
Optional.empty
---( false )---
Optional.empty
Optional.empty
Optional.empty
Optional.empty
Optional.empty
Optional.empty
---( str != "" )---
Optional[Foo]
Optional.empty
Optional[Bar]
Optional[Baz]
Optional[Bingo]
Optional.empty
---( str.length() == 3 )---
Optional[Foo]
Optional.empty
Optional[Bar]
Optional[Baz]
Optional.empty
Optional.empty
---( startsWith("B") )---
Optional.empty
Optional.empty
Optional[Bar]
Optional[Baz]
Optional[Bingo]
Optional.empty
```

即使输出看起来像流，要特别注意 `test()` 中的 for 循环。每一次的for循环都重新启动流，然后跳过for循环索引指定的数量的元素，这就是流只剩后续元素的原因。然后调用`findFirst()` 获取剩余元素中的第一个元素，并包装在一个 `Optional`对象中。

**注意**，不同于普通 for 循环，这里的索引值范围并不是 `i < elements.length`， 而是 `i <= elements.length`。所以最后一个元素实际上超出了流。方便的是，这将自动成为 **Optional.empty**，你可以在每一个测试的结尾中看到。

同 `map()` 一样 ， `Optional.map()` 执行一个函数。它仅在 **Optional** 不为空时才执行这个映射函数。并将 **Optional** 的内容提取出来，传递给映射函数。代码示例：

```java
// streams/OptionalMap.java
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

class OptionalMap {
    static String[] elements = {"12", "", "23", "45"};

    static Stream<String> testStream() {
        return Arrays.stream(elements);
    }

    static void test(String descr, Function<String, String> func) {
        System.out.println(" ---( " + descr + " )---");
        for (int i = 0; i <= elements.length; i++) {
            System.out.println(
                    testStream()
                            .skip(i)
                            .findFirst() // Produces an Optional
                            .map(func));
        }
    }

    public static void main(String[] args) {
        // If Optional is not empty, map() first extracts
        // the contents which it then passes
        // to the function:
        test("Add brackets", s -> "[" + s + "]");
        test("Increment", s -> {
            try {
                return Integer.parseInt(s) + 1 + "";
            } catch (NumberFormatException e) {
                return s;
            }
        });
        test("Replace", s -> s.replace("2", "9"));
        test("Take last digit", s -> s.length() > 0 ?
                s.charAt(s.length() - 1) + "" : s);
    }
    // After the function is finished, map() wraps the
    // result in an Optional before returning it:
}
```

输出结果：

```
---( Add brackets )---
Optional[[12]]
Optional[[]]
Optional[[23]]
Optional[[45]]
Optional.empty
---( Increment )---
Optional[13]
Optional[]
Optional[24]
Optional[46]
Optional.empty
---( Replace )---
Optional[19]
Optional[]
Optional[93]
Optional[45]
Optional.empty
---( Take last digit )---
Optional[2]
Optional[]
Optional[3]
Optional[5]
Optional.empty
```

映射函数的返回结果会自动包装成为 **Optional**。**Optional.empty** 会被直接跳过。

**Optional** 的 `flatMap()` 应用于已生成 **Optional** 的映射函数，所以 `flatMap()` 不会像 `map()` 那样将结果封装在 **Optional** 中。代码示例：

```java
// streams/OptionalFlatMap.java
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

class OptionalFlatMap {
    static String[] elements = {"12", "", "23", "45"};

    static Stream<String> testStream() {
        return Arrays.stream(elements);
    }

    static void test(String descr,
                     Function<String, Optional<String>> func) {
        System.out.println(" ---( " + descr + " )---");
        for (int i = 0; i <= elements.length; i++) {
            System.out.println(
                    testStream()
                            .skip(i)
                            .findFirst()
                            .flatMap(func));
        }
    }

    public static void main(String[] args) {
        test("Add brackets",
                s -> Optional.of("[" + s + "]"));
        test("Increment", s -> {
            try {
                return Optional.of(
                        Integer.parseInt(s) + 1 + "");
            } catch (NumberFormatException e) {
                return Optional.of(s);
            }
        });
        test("Replace",
                s -> Optional.of(s.replace("2", "9")));
        test("Take last digit",
                s -> Optional.of(s.length() > 0 ?
                        s.charAt(s.length() - 1) + ""
                        : s));
    }
}
```

输出结果：

```
---( Add brackets )---
Optional[[12]]
Optional[[]]
Optional[[23]]
Optional[[45]]
Optional.empty
 ---( Increment )---
Optional[13]
Optional[]
Optional[24]
Optional[46]
Optional.empty
 ---( Replace )---
Optional[19]
Optional[]
Optional[93]
Optional[45]
Optional.empty
 ---( Take last digit )---
Optional[2]
Optional[]
Optional[3]
Optional[5]
Optional.empty
```

同 `map()`，`flatMap()` 将提取非空 **Optional** 的内容并将其应用在映射函数。唯一的区别就是 `flatMap()` 不会把结果包装在 **Optional** 中，因为映射函数已经被包装过了。在如上示例中，我们已经在每一个映射函数中显式地完成了包装，但是很显然 `Optional.flatMap()` 是为那些自己已经生成 **Optional** 的函数而设计的。

<!-- Streams of Optionals -->
### Optional 流

假设你的生成器可能产生 `null` 值，那么当用它来创建流时，你会自然地想到用  **Optional** 来包装元素。如下是它的样子，代码示例：

```java
// streams/Signal.java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
public class Signal {
    private final String msg;
    public Signal(String msg) { this.msg = msg; }
    public String getMsg() { return msg; }
    @Override
    public String toString() {
        return "Signal(" + msg + ")";
    }
    static Random rand = new Random(47);
    public static Signal morse() {
        switch(rand.nextInt(4)) {
            case 1: return new Signal("dot");
            case 2: return new Signal("dash");
            default: return null;
        }
    }
    public static Stream<Optional<Signal>> stream() {
        return Stream.generate(Signal::morse)
                .map(signal -> Optional.ofNullable(signal));
    }
}
```

当我们使用这个流的时候，必须要弄清楚如何解包 **Optional**。代码示例：

```java
// streams/StreamOfOptionals.java
import java.util.*;
import java.util.stream.*;
public class StreamOfOptionals {
    public static void main(String[] args) {
        Signal.stream()
                .limit(10)
                .forEach(System.out::println);
        System.out.println(" ---");
        Signal.stream()
                .limit(10)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(System.out::println);
    }
}
```

输出结果：

```java
Optional[Signal(dash)]
Optional[Signal(dot)]
Optional[Signal(dash)]
Optional.empty
Optional.empty
Optional[Signal(dash)]
Optional.empty
Optional[Signal(dot)]
Optional[Signal(dash)]
Optional[Signal(dash)]
---
Signal(dot)
Signal(dot)
Signal(dash)
Signal(dash)
```

在这里，我们使用 `filter()` 来保留那些非空 **Optional**，然后在 `map()` 中使用 `get()` 获取元素。由于每种情况都需要定义“空值”的含义，所以通常我们要为每个应用程序采用不同的方法。

<!-- Terminal Operations -->

## 终端操作


以下操作将会获取流的最终结果。至此我们无法再继续往后传递流。可以说，终端操作（Terminal Operations）总是我们在流管道中所做的最后一件事。

<!-- Convert to an Array -->

### 数组
- `toArray()`：将流转换成适当类型的数组。
- `toArray(generator)`：在特殊情况下，生成自定义类型的数组。

当我们需要得到数组类型的数据以便于后续操作时，上面的方法就很有用。假设我们需要复用流产生的随机数时，就可以这么使用。代码示例:

```java
// streams/RandInts.java
package streams;
import java.util.*;
import java.util.stream.*;
public class RandInts {
    private static int[] rints = new Random(47).ints(0, 1000).limit(100).toArray();
    public static IntStream rands() {
        return Arrays.stream(rints);
    }
}
```

上例将100个数值范围在 0 到 1000 之间的随机数流转换成为数组并将其存储在 `rints` 中。这样一来，每次调用 `rands()` 的时候可以重复获取相同的整数流。

<!-- Apply a Final Operation to Every Element -->
### 循环

- `forEach(Consumer)`常见如 `System.out::println` 作为 **Consumer** 函数。
- `forEachOrdered(Consumer)`： 保证 `forEach` 按照原始流顺序操作。

第一种形式：无序操作，仅在引入并行流时才有意义。在 [并发编程](24-Concurrent-Programming.md) 章节之前我们不会深入研究这个问题。这里简单介绍下 `parallel()`：可实现多处理器并行操作。实现原理为将流分割为多个（通常数目为 CPU 核心数）并在不同处理器上分别执行操作。因为我们采用的是内部迭代，而不是外部迭代，所以这是可能实现的。

`parallel()` 看似简单，实则棘手。更多内容将在稍后的 [并发编程](24-Concurrent-Programming.md) 章节中学习。

下例引入 `parallel()` 来帮助理解 `forEachOrdered(Consumer)` 的作用和使用场景。代码示例：

```java
// streams/ForEach.java
import java.util.*;
import java.util.stream.*;
import static streams.RandInts.*;
public class ForEach {
    static final int SZ = 14;
    public static void main(String[] args) {
        rands().limit(SZ)
                .forEach(n -> System.out.format("%d ", n));
        System.out.println();
        rands().limit(SZ)
                .parallel()
                .forEach(n -> System.out.format("%d ", n));
        System.out.println();
        rands().limit(SZ)
                .parallel()
                .forEachOrdered(n -> System.out.format("%d ", n));
    }
}
```

输出结果：

```
258 555 693 861 961 429 868 200 522 207 288 128 551 589
551 861 429 589 200 522 555 693 258 128 868 288 961 207
258 555 693 861 961 429 868 200 522 207 288 128 551 589
```

为了方便测试不同大小的流，我们抽离出了 `SZ` 变量。然而即使 `SZ` 值为14也产生了有趣的结果。在第一个流中，未使用 `parallel()` ，因此以元素从 `rands()`出来的顺序输出结果。在第二个流中，引入`parallel()` ，即便流很小，输出的结果的顺序也和前面不一样。这是由于多处理器并行操作的原因，如果你将程序多运行几次，你会发现输出都不相同，这是多处理器并行操作的不确定性造成的结果。

在最后一个流中，同时使用了 `parallel()` 和 `forEachOrdered()` 来强制保持原始流顺序。因此，对非并行流使用 `forEachOrdered()` 是没有任何影响的。

<!-- Collecting -->

### 集合

- `collect(Collector)`：使用 **Collector** 收集流元素到结果集合中。
- `collect(Supplier, BiConsumer, BiConsumer)`：同上，第一个参数 **Supplier** 创建了一个新的结果集合，第二个参数 **BiConsumer** 将下一个元素收集到结果集合中，第三个参数 **BiConsumer** 用于将两个结果集合合并起来。

在这里我们只是简单介绍了几个 **Collectors** 的运用示例。实际上，它还有一些非常复杂的操作实现，可通过查看 `java.util.stream.Collectors` 的 API 文档了解。例如，我们可以将元素收集到任意一种特定的集合中。

假设我们现在为了保证元素有序，将元素存储在 **TreeSet** 中。**Collectors** 里面没有特定的 `toTreeSet()`，但是我们可以通过将集合的构造函数引用传递给 `Collectors.toCollection()`，从而构建任何类型的集合。下面我们来将一个文件中的单词收集到 **TreeSet** 集合中。代码示例：

```java
// streams/TreeSetOfWords.java
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;
public class TreeSetOfWords {
    public static void
    main(String[] args) throws Exception {
        Set<String> words2 =
                Files.lines(Paths.get("TreeSetOfWords.java"))
                        .flatMap(s -> Arrays.stream(s.split("\\W+")))
                        .filter(s -> !s.matches("\\d+")) // No numbers
                        .map(String::trim)
                        .filter(s -> s.length() > 2)
                        .limit(100)
                        .collect(Collectors.toCollection(TreeSet::new));
        System.out.println(words2);
    }
}
```

输出结果：

```
[Arrays, Collectors, Exception, Files, Output, Paths,
Set, String, System, TreeSet, TreeSetOfWords, args,
class, collect, file, filter, flatMap, get, import,
java, length, limit, lines, main, map, matches, new,
nio, numbers, out, println, public, split, static,
stream, streams, throws, toCollection, trim, util,
void, words2]
```

**Files.**`lines()` 打开 **Path** 并将其转换成为由行组成的流。下一行代码以一个或多个非单词字符（`\\W+`）为分界，对每一行进行分割，结果是产生一个数组，然后使用 **Arrays.**`stream()` 将数组转化成为流，最后`flatMap()`将各行形成的多个单词流，扁平映射为一个单词流。使用 `matches(\\d+)` 查找并移除全部是数字的字符串（注意,`words2` 是通过的）。然后用 **String.**`trim()` 去除单词两边的空白，`filter()` 过滤所有长度小于3的单词，并只获取前100个单词，最后将其保存到 **TreeSet** 中。

我们也可以在流中生成 **Map**。代码示例：

```java
// streams/MapCollector.java
import java.util.*;
import java.util.stream.*;
class Pair {
    public final Character c;
    public final Integer i;
    Pair(Character c, Integer i) {
        this.c = c;
        this.i = i;
    }
    public Character getC() { return c; }
    public Integer getI() { return i; }
    @Override
    public String toString() {
        return "Pair(" + c + ", " + i + ")";
    }
}
class RandomPair {
    Random rand = new Random(47);
    // An infinite iterator of random capital letters:
    Iterator<Character> capChars = rand.ints(65,91)
            .mapToObj(i -> (char)i)
            .iterator();
    public Stream<Pair> stream() {
        return rand.ints(100, 1000).distinct()
                .mapToObj(i -> new Pair(capChars.next(), i));
    }
}
public class MapCollector {
    public static void main(String[] args) {
        Map<Integer, Character> map =
                new RandomPair().stream()
                        .limit(8)
                        .collect(
                                Collectors.toMap(Pair::getI, Pair::getC));
        System.out.println(map);
    }
}
```

输出结果：

```
{688=W, 309=C, 293=B, 761=N, 858=N, 668=G, 622=F, 751=N}
```

**Pair** 只是一个基础的数据对象。**RandomPair** 创建了随机生成的 **Pair** 对象流。在 Java 中，我们不能直接以某种方式组合两个流。所以我创建了一个整数流，并且使用 `mapToObj()` 将整数流转化成为 **Pair** 流。 **capChars**的随机大写字母迭代器创建了流，然后`next()`让我们可以在`stream()`中使用这个流。就我所知，这是将多个流组合成新的对象流的唯一方法。

在这里，我们只使用最简单形式的 `Collectors.toMap()`，这个方法只需要两个从流中获取键和值的函数。还有其他重载形式，其中一种当是键发生冲突时，使用一个函数来处理冲突。

大多数情况下，`java.util.stream.Collectors` 中预设的 **Collector** 就能满足我们的要求。除此之外，你还可以使用第二种形式的 `collect()`。 我把它留作更高级的练习，下例给出基本用法：

```java
// streams/SpecialCollector.java
import java.util.*;
import java.util.stream.*;
public class SpecialCollector {
    public static void main(String[] args) throws Exception {
        ArrayList<String> words =
                FileToWords.stream("Cheese.dat")
                        .collect(ArrayList::new,
                                ArrayList::add,
                                ArrayList::addAll);
        words.stream()
                .filter(s -> s.equals("cheese"))
                .forEach(System.out::println);
    }
}
```

输出结果：

```
cheese
cheese
```

在这里， **ArrayList** 的方法已经做了你所需要的操作，但更有可能的是，如果你必须使用这种形式的 `collect()`，就要自己创建特定的定义。

<!-- Combining All Stream Elements -->

### 组合

- `reduce(BinaryOperator)`：使用 **BinaryOperator** 来组合所有流中的元素。因为流可能为空，其返回值为 **Optional**。
- `reduce(identity, BinaryOperator)`：功能同上，但是使用 **identity** 作为其组合的初始值。因此如果流为空，**identity** 就是结果。
- `reduce(identity, BiFunction, BinaryOperator)`：更复杂的使用形式（暂不介绍），这里把它包含在内，因为它可以提高效率。通常，我们可以显式地组合 `map()` 和 `reduce()` 来更简单的表达它。

下面来看下 `reduce` 的代码示例：

```java
// streams/Reduce.java
import java.util.*;
import java.util.stream.*;
class Frobnitz {
    int size;
    Frobnitz(int sz) { size = sz; }
    @Override
    public String toString() {
        return "Frobnitz(" + size + ")";
    }
    // Generator:
    static Random rand = new Random(47);
    static final int BOUND = 100;
    static Frobnitz supply() {
        return new Frobnitz(rand.nextInt(BOUND));
    }
}
public class Reduce {
    public static void main(String[] args) {
        Stream.generate(Frobnitz::supply)
                .limit(10)
                .peek(System.out::println)
                .reduce((fr0, fr1) -> fr0.size < 50 ? fr0 : fr1)
                .ifPresent(System.out::println);
    }
}
```

输出结果：

```
Frobnitz(58)
Frobnitz(55)
Frobnitz(93)
Frobnitz(61)
Frobnitz(61)
Frobnitz(29)
Frobnitz(68)
Frobnitz(0)
Frobnitz(22)
Frobnitz(7)
Frobnitz(29)
```

**Frobnitz** 包含一个可生成自身的生成器 `supply()` ；因为 `supply()` 方法作为一个 `Supplier<Frobnitz>` 是签名兼容的，我们可以把 `supply()` 作为一个方法引用传递给 `Stream.generate()` （这种签名兼容性被称作结构一致性）。我们使用了没有“初始值”作为第一个参数的 `reduce()`方法，所以产生的结果是 **Optional** 类型。`Optional.ifPresent()` 方法只有在结果非空的时候才会调用 `Consumer<Frobnitz>` （`println` 方法可以被调用是因为 **Frobnitz** 可以通过 `toString()` 方法转换成 **String**）。

Lambda 表达式中的第一个参数 `fr0` 是 `reduce()` 中上一次调用的结果。而第二个参数 `fr1` 是从流传递过来的值。

`reduce()` 中的 Lambda 表达式使用了三元表达式来获取结果，当 `fr0` 的 `size` 值小于 50 的时候，将 `fr0` 作为结果，否则将序列中的下一个元素即 `fr1`作为结果。当取得第一个 `size` 值小于 50 的 `Frobnitz`，只要得到这个结果就会忽略流中其他元素。这是个非常奇怪的限制， 但也确实让我们对 `reduce()` 有了更多的了解。

<!-- Matching -->
### 匹配

- `allMatch(Predicate)` ：如果流的每个元素提供给 **Predicate** 都返回 true ，结果返回为 true。在第一个 false 时，则停止执行计算。
- `anyMatch(Predicate)`：如果流的任意一个元素提供给 **Predicate** 返回 true ，结果返回为 true。在第一个 true 是停止执行计算。
- `noneMatch(Predicate)`：如果流的每个元素提供给 **Predicate** 都返回 false 时，结果返回为 true。在第一个 true 时停止执行计算。

我们已经在 `Prime.java` 中看到了 `noneMatch()` 的示例；`allMatch()` 和 `anyMatch()` 的用法基本上是等同的。下面我们来探究一下短路行为。为了消除冗余代码，我们创建了 `show()`。首先我们必须知道如何统一地描述这三个匹配器的操作，然后再将其转换为 **Matcher** 接口。代码示例：

```java
// streams/Matching.java
// Demonstrates short-circuiting of *Match() operations
import java.util.stream.*;
import java.util.function.*;
import static streams.RandInts.*;

interface Matcher extends BiPredicate<Stream<Integer>, Predicate<Integer>> {}
        
public class Matching {
    static void show(Matcher match, int val) {
        System.out.println(
                match.test(
                        IntStream.rangeClosed(1, 9)
                                .boxed()
                                .peek(n -> System.out.format("%d ", n)),
                        n -> n < val));
    }
    public static void main(String[] args) {
        show(Stream::allMatch, 10);
        show(Stream::allMatch, 4);
        show(Stream::anyMatch, 2);
        show(Stream::anyMatch, 0);
        show(Stream::noneMatch, 5);
        show(Stream::noneMatch, 0);
    }
}
```

输出结果：

```
1 2 3 4 5 6 7 8 9 true
1 2 3 4 false
1 true
1 2 3 4 5 6 7 8 9 false
1 false
1 2 3 4 5 6 7 8 9 true
```

**BiPredicate** 是一个二元谓词，它接受两个参数并返回 true 或者 false。第一个参数是我们要测试的流，第二个参数是一个谓词 **Predicate**。**Matcher** 可以匹配所有的 **Stream::\*Match** 方法，所以可以将每一个**Stream::\*Match**方法引用传递到 `show()` 中。对`match.test()` 的调用会被转换成 对方法引用**Stream::\*Match** 的调用。

`show()` 接受一个**Matcher**和一个 `val` 参数，`val` 在判断测试 `n < val`中指定了最大值。`show()` 方法生成了整数1-9组成的一个流。`peek()`用来展示在测试短路之前测试进行到了哪一步。从输出中可以看到每次都发生了短路。

### 查找

- `findFirst()`：返回第一个流元素的 **Optional**，如果流为空返回 **Optional.empty**。
- `findAny(`：返回含有任意流元素的 **Optional**，如果流为空返回 **Optional.empty**。

代码示例：

```java
// streams/SelectElement.java
import java.util.*;
import java.util.stream.*;
import static streams.RandInts.*;
public class SelectElement {
    public static void main(String[] args) {
        System.out.println(rands().findFirst().getAsInt());
        System.out.println(
                rands().parallel().findFirst().getAsInt());
        System.out.println(rands().findAny().getAsInt());
        System.out.println(
                rands().parallel().findAny().getAsInt());
    }
}
```

输出结果：

```
258
258
258
242
```

无论流是否为并行化，`findFirst()` 总是会选择流中的第一个元素。对于非并行流，`findAny()`会选择流中的第一个元素（即使从定义上来看是选择任意元素）。在这个例子中，用 `parallel()` 将流并行化，以展示 `findAny()` 不选择流的第一个元素的可能性。

如果必须选择流中最后一个元素，那就使用 `reduce()`。代码示例：

```java
// streams/LastElement.java
import java.util.*;
import java.util.stream.*;
public class LastElement {
    public static void main(String[] args) {
        OptionalInt last = IntStream.range(10, 20)
                .reduce((n1, n2) -> n2);
        System.out.println(last.orElse(-1));
        // Non-numeric object:
        Optional<String> lastobj =
                Stream.of("one", "two", "three")
                        .reduce((n1, n2) -> n2);
        System.out.println(
                lastobj.orElse("Nothing there!"));
    }
}
```

输出结果：

```
19
three
```

`reduce()`  的参数只是用最后一个元素替换了最后两个元素，最终只生成最后一个元素。如果为数字流，你必须使用相近的数字 **Optional** 类型（ numeric optional type），否则使用 **Optional** 类型，就像上例中的 `Optional<String>`。

<!-- Informational -->

### 信息

- `count()`：流中的元素个数。
- `max(Comparator)`：根据所传入的 **Comparator** 所决定的“最大”元素。
- `min(Comparator)`：根据所传入的 **Comparator** 所决定的“最小”元素。

**String** 类型有预设的 **Comparator** 实现。代码示例：

```java
// streams/Informational.java
import java.util.stream.*;
import java.util.function.*;
public class Informational {
    public static void
    main(String[] args) throws Exception {
        System.out.println(
                FileToWords.stream("Cheese.dat").count());
        System.out.println(
                FileToWords.stream("Cheese.dat")
                        .min(String.CASE_INSENSITIVE_ORDER)
                        .orElse("NONE"));
        System.out.println(
                FileToWords.stream("Cheese.dat")
                        .max(String.CASE_INSENSITIVE_ORDER)
                        .orElse("NONE"));
    }
}
```

输出结果：

```
32
a
you
```

`min()` 和 `max()` 的返回类型为 **Optional**，这需要我们使用 `orElse()`来解包。

<!-- Information for Numeric Streams -->
### 数字流信息

- `average()` ：求取流元素平均值。
- `max()` 和 `min()`：数值流操作无需 **Comparator**。
- `sum()`：对所有流元素进行求和。
- `summaryStatistics()`：生成可能有用的数据。目前并不太清楚这个方法存在的必要性，因为我们其实可以用更直接的方法获得需要的数据。

```java
// streams/NumericStreamInfo.java
import java.util.stream.*;
import static streams.RandInts.*;
public class NumericStreamInfo {
    public static void main(String[] args) {
        System.out.println(rands().average().getAsDouble());
        System.out.println(rands().max().getAsInt());
        System.out.println(rands().min().getAsInt());
        System.out.println(rands().sum());
        System.out.println(rands().summaryStatistics());
    }
}
```

输出结果：

```
507.94
998
8
50794
IntSummaryStatistics{count=100, sum=50794, min=8, average=507.940000, max=998}
```

上例操作对于 **LongStream** 和 **DoubleStream** 同样适用。

## 本章小结

流式操作改变并极大地提升了 Java 语言的可编程性，并可能极大地阻止了 Java 编程人员向诸如 Scala 这种函数式语言的流转。在本书的剩余部分，我们将尽可能地使用流。

[^1]: 在软件或信息建模的上下文中，快乐路径(有时称为快乐流)是没有异常或错误条件的默认场景。例如，验证信用卡号的函数的快乐路径应该是任何验证规则都不会出现错误的地方，从而让执行成功地继续到最后，生成一个积极的响应。[见 wikipedia: happy path](https://en.wikipedia.org/wiki/Happy_path)

<!-- 分页 -->

<div style="page-break-after: always;"></div>
