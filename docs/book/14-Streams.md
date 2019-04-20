[TOC]

<!-- Streams -->
# 第十四章 流式编程

Collections 优化了对象的存储。Streams 是和对象组的处理有关。流是一系列与任何特定存储机制无关的元素 — 实际上我们说流没有“存储”。

不需要迭代集合中的元素，而是在管道中绘制元素并对其操作的流。这些管道经常被组合在一起，在流上形成一个操作管道。

在大多数情况下，将对象存储在集合中的原因是为了处理他们，因此你将会发现你将把编程的主要焦点从集合转移到了流上。流的一个核心好处是，它使得程序更加短小并且更易理解。当 Lambda 表达式和方法引用（method references）和流一起使用的时候感觉自成一体。流使得 Java 8 更巨吸引力。

例如，你想展现在 5 到 20 之间随机选择的序列中只出现一次的数字，并且是排序好的。事实上，你对他们进行排序可能使得你的精力首先集中在选择一个已排序的集合。但是对于流，你只需要简单的说明你想要什么：

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

输出为：

```java
6
10
13
16
17
18
19
```

首先，我们给 **Random** 对象一个种子（以便程序再次运行时产生相同的输出）。**ints()** 方法产生一个流并且 **ints()** 方法有多种方式的重载 — 两个参数限定了数值产生的边界。这将生成一个整数流。我们告诉他使用中间流操作（intermediate stream operation） **distinct()** 来获取它们的唯一值，然后使用 **limit()** 方法获取前 7 个元素。接下来，我们使用 **sorted()** 方法希望元素是有序的。最终，我们希望显示每个条目，因此使用 **forEach()**，它根据传递给它的函数对每个流对象执行操作。在这里，我们传递了一个可以在控制台展现每个元素的方法引用 **System.out::println** 。

注意 **Randoms.java** 中没有声明任何变量。流可以对具有状态的系统建模，并且不需要使用赋值或者可变数据，这非常有用。

声明式编程是一种风格，在这种风格中，我们声明我们想要做什么而不是指定如何去做，这就是你在函数式编程中所看到的。注意，理解命令式编程的形式要困难的多：

```java
// streams/ImperativeRandoms.java
import java.util.*;
public class ImperativeRandoms {
    public static void main(String[] args) {
        Random rand = new Random(47);
        SortedSet<Integer> rints = new TreeSet<>();
        while(rints.size() < 7) {
            int r = rand.nextint(20);
            if(r < 5) continue;
            rints.add(r);
        }
        System.out.println(rints);
    }
}
```

输出为：

```java
[7, 8, 9, 11, 13, 15, 18]
```

在 **Randoms.java** 中，我们无需定义任何变量，但是在这里我们定义了 3 个变量： **rand**，**rints** 和 **r**。这个代码变的更加复杂，是由于 **nextInt()** 没有下界选项 — 其内置的下界永远为 0，因此我们生成额外的数值并过滤小于 5 的值。

注意，你必须研究代码来弄清楚发生了什么，而在 **Randoms.java** 中，代码只是告诉了你它在做什么。这种清晰度是 Java 8 中流使人最信服的原因之一。

在 **ImperativeRandoms.java** 中显式的编写迭代机制称之为外部迭代。在 **Randoms.java** 中，你没有看到这些机制，你并没有看到这样类似的机制，它是流编程中的核心特征被称之为内部迭代。内部迭代产生更可读的代码，也更容易使用多个处理器。通过放松对迭代发生的控制，你可以将控制权交给并行化机制。你将在[并发编程]()这一章了解这一点。

流另一个重要方面是他们是惰性（lazy）的，意味着它们只在绝对必要时进行评估。你可以将流看作“延迟列表”。由于评估延迟，流可以使我们表示非常大（甚至无限）的序列，并且没有内存担忧。

<!-- Java 8 Stream Support -->

## 流支持

Java 设计者面临着这样一个难题：现存的大量类库不仅为 Java 所用，同时也被应用在整个 Java 生态圈数百万行的代码中。如何将一个全新的流的概念融入到现有类库中呢？

简单的例子,如在 **Random** 中添加更多的方法。因为只要不改变原有的方法，遗留代码就不会受到干扰。

问题是，接口部分怎么改造呢？特别是涉及集合类接口的部分。如果你想把一个集合转换为流，直接向接口添加新方法会破坏所有老的接口实现类。

Java 8 采用的解决方案是：在[接口](10-Interfaces.md)中添加被 **default**（**默认**）修饰的方法。通过这种方案，设计者们可以将流式（*stream*）方法平滑地嵌入到现有类中。流方法预置的操作几乎已满足了我们平常所有的需求。流操作的类型有三种：创建流，修改流元素（中间操作, *Intermediate Operations*），消费流元素（终端操作, *Terminal Operations*）。最后一种类型通常意味着收集流元素（通常是到集合中）。

下面我们来看下每种类型的流操作。

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

**IntStream** 类提供了  **range()** 方法用于生成整数序列的流。编写循环时，这个方法会更加便利：

```java
// streams/Ranges.java
import static java.util.stream.IntStream.*;
public class Ranges {
    public static void main(String[] args) {
        // The traditional way:
        int result = 0;
        for (int i = 10; i < 20; i++)
            result += i;
        System.out.println(result);
        // for-in with a range:
        result = 0;
        for (int i : range(10, 20).toArray())
            result += i;
        System.out.println(result);
        // Use streams:
        System.out.println(range(10, 20).sum());
    }
}
```

输出为：

```java
145
145
145
```

在 **main()** 方法中的第一种方式是我们传统编写 **for** 循环的方式。在第二种方法，我们使用 **range()** 创建了流并将其转化为数组，然后在 **for-in** 代码块中使用。但是，如果你能够像第三种方法全程使用流是很好的。在每种情况下，我们对范围中的数字进行求和，并且流中可以很方便的使用 **sum()** 操作求和。

注意 **IntStream.range()** 相比 **onjava.Range.range()** 拥有更多的限制。这是由于其可选的第三个参数，后者能够生成步长大于 1 的范围，并且可以从大到小来生成。

为了替换简单的 **for** 循环，这里是一个 **repeat()** 实用程序：

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

输出为：

```java
Looping!
Looping!
Looping!
Hi!
Hi!
```

在代码中包含并解释 **repeat()** 似乎有些不值得。它似乎是一个相当透明的工具，但它取决于你的团队和公司的运作方式

### generate()

**RandomWords.java** 在 **Stream.generate()** 中使用 **Supplier\<T\>**。这里是第二个示例：

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

输出为：

```java
YNZBRNYGCFOWZNTCQRGSEGZMMJMROE
```

使用 **Random.nextInt()** 方法来挑选字母表中的大写字母。**Random.nextInt()** 的参数代表可以接受的最大的随机数范围，所以使用数组边界是经过深思熟虑的。

如果要创建包含相同对象的流，只需要传递一个生成那些对象 **lambda** 到 **generate()** 中：

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

输出为：

```java
duplicate
duplicate
duplicate
```

如下是在这个章节中之前例子使用过的 **Bubble** 类。注意它包含了自己的静态生成器（*static generator*）方法。

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

由于 **bubbler()** 与 **Supplier\<Bubble\>** 是接口兼容的，我们可以将其方法引用直接传递给 **Stream.generate()**：

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

输出为：

```java
Bubble(0)
Bubble(1)
Bubble(2)
Bubble(3)
Bubble(4)
```

这是创建单独工厂类（ separate factory class）的另外一种方式。在很多方面它更加整洁，但是这代表着品味和代码组织的问题 - 你总是可以创建一个完全不同的工厂类。

### iterate()

**Stream.iterate()** 以种子（第一个参数）开头，并将其传给方法（第二个参数）。方法的结果将添加到流，并存储作为第一个参数用于下次调用 **iterate()**，依次类推。我们可以使用 **iterate()** 用于生成一个 Fibonacci 序列（你在上一章中遇到）：

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
                       .skip(20) // Don't use the first 20
                       .limit(10) // Then take 10 of them
                       .forEach(System.out::println);
    }
}
```

输出为：

```java
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

Fibonacci 序列将序列中最后两个元素进行求和以产生下一个元素。**iterate()** 只能记忆结果，因此我们需要使用一个变量 **x** 来用于追踪另外一个元素。

在 **main()** 中，我们使用了一个你之前没有见过的 **skip() ** 操作。它只是根据它的参数丢弃指定数量的流元素。在这里，我们丢弃了前 20 个元素。

### Stream Builders

在建造者设计模式中，首先创建一个 builder 对象，传递给它多个构造器信息，最后执行“构造”。**Stream** 库提供了这样的 **Builder**。在这里，我们重新审视读取文件并将其转换成为单词流的过程：

```java
// streams/FileToWordsBuilder.java
import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class FileToWordsBuilder {
    Stream.Builder<String> builder = Stream.builder();
    
    public FileToWordsBuilder(String filePath) throws Exception {
        Files.lines(Paths.get(filePath))
             .skip(1) // Skip the comment line at the beginning
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

输出为：

```java
Not much of a cheese shop really
```

注意，构造器会添加文件中的所有单词（除了第一行，它是包含文件路径信息的注释），但是其并没有调用 **build()** 方法。这意味着，只要你不调用 **stream()** 方法，就可以继续向 **builder** 对象中添加单词。

在此类的更完整的版本中，你可以添加一个标志位用于查看 **build()** 方法是否被调用，并且可能的话增加一个可以添加更多单词的方法。在 **Stream.Builder** 调用 **build()** 方法后继续尝试添加单词会产生一个异常。

### Arrays

**Arrays** 类中含有一个名为 **stream()** 的静态方法用于把数组转换成为流。我们可以重写 **interfaces/Machine.java** 中的 **main()** 方法用于创建一个流，并将 **execute()** 应用于每一个元素：

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

输出为：

```java
Bing
Crack
Twist
Pop
```

**new Operations[]** 表达式动态创建了 **Operations** 对象的数组。

**stream()** 方法同样可以产生 **IntStream**，**LongStream** 和 **DoubleStream**。

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
        
        // Select a subrange:
        Arrays.stream(new int[] { 1, 3, 5, 7, 15, 28, 37 }, 3, 6)
            .forEach(n -> System.out.format("%d ", n));
    }
}
```

输出为：

```java
3.141590 2.718000 1.618000
1 3 5
11 22 44 66
7 15 28
```

最后一次 **stream()** 的调用有两个额外的参数。第一个参数告诉 **stream()** 从哪里开始在数组中选择元素，第二个参数用于告知在哪里停止。每种不同类型的 **stream()** 方法都有这个版本。

### 正则表达式（Regular Expressions）

Java 的正则表达式已经在[字符串]()这一章节介绍过了。Java 8 在 **java.util.regex.Pattern** 中增加了一个新的方法 `splitAsStream()`，这个方法可以根据你所传入的公式将字符序列转化为流。但是这里有一个限制，输入只能是 **CharSequence**，因此不能将流作为 `splitAsStream()` 的参数。

我们再一次查看将文件处理为单词流的过程。这一次，我们使用流将文件分割为单独的字符串，接着使用正则表达式将字符串转化为单词流。

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

输出为：

```java
Not much of a cheese shop really is it
```

在构造器中我们读取了文件中的所有内容（再一次跳过了第一行注释），并将其转化成为单行字符串。现在，当你调用 `stream()` 方法的时候，可以像往常一样获取一个流，但这次你可以多次调用 `stream()` 方法，在已存储的字符串中创建一个新的流。这里有个限制，整个文件必须存储在内存中；在大多数情况下这并不是什么问题，但是这损失了流中非常重要的好处：

1. 流“不需要存储”。当然它们需要一些内部存储，但是这只是序列的一小部分，和持有整个序列所需要的并不相同。
2. 它们是惰性评估的。幸运的是，我们将在稍晚一些的时候查看如何如解决这个问题。

<!-- Intermediate Operations -->

## 中间操作

中间操作用于在一个流中获取元素，并将元素放入另一个流的后端用以连接不同的操作。

### 跟踪和调试

`peek()` 操作的目的是帮助调试。它允许你无修改的查看流中的元素：

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

输出为：

```java
Well WELL well it IT it s S s so SO so
```

**FileToWords** 很快就被定义好了，但是它的功能就像我们之前所看到的的版本那样：产生 **String** 对象的流。之后在他们通过管道的时候使用 `peek()` 偷窥它们。

因为 `peek()` 符合 没有返回值的 **Consumer** 函数式接口，所以不可能使用不同的元素来替换流中的对象。你只能观察它们。

### 排序流中元素

你已经在 **Randoms.java** 看到了使用默认比较器的 `sorted()` 函数。还有 `sorted()` 的第二种形式，需要传入一个 **Comparator** 参数：

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

输出为：

```java
you what to the that sir leads in district And
```

你可以为 `sorted()` 传入一个 lambda 函数作为其参数，但是这里也有预先实现好的比较器 —— 在这里我们所使用的是反转“自然顺序”。

### 移除元素

`distinct()`：在 **Randoms.java** 中，`distinct()` 去除了流中的重复元素。使用 `distinct()` 相比创建一个 `Set` 用于消除重复元素的工作量要小得多。

`filter(Predicate)`：过滤操作只会保留那些传递给参数是产生 true 的元素 - 过滤器函数。

在这个例子中，过滤器函数是 ` isPrime()`，用于检测质数。

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

输出为：

```java
2 3 5 7 11 13 17 19 23 29
467 479 487 491 499 503 509 521 523 541
```

`rangeClosed() ` 包含了上界值。如果余数没有产生 0，则 `noneMatch()` 操作返回 ture，如果出现任何等于 0 的则返回 false。 `noneMatch()`  操作在第一次失败之后就会推出，而不是进行全部尝试。

### 应用操作到所有元素

`map(Function) `：将 **Function** 操作应用在输入流的每一个元素中，并将返回值传递到输出流中。

`mapToInt(ToIntFunction)`：操作同上，但结果是 **IntStream**。

`mapToLong(ToLongFunction)`：操作同上，但结果是 **LongStream**。

`mapToDouble(ToDoubleFunction)` ： 操作同上，但结果是 **DoubleStream**。

在这里，我们使用 `map()` 映射多种函数到一个字符串流中：

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
                return Integer.parseint(s) + 1 + "";
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

输出为：

```java
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

在“Increment”测试中，我们使用 `Integer.parseInt()` 去试图将一个字符串转化为整数。如果字符串不能转化成为整数就会抛出一个 **NumberFormatException** 异常，我们只需回过头来将原始字符串放回到输出流中。

在以上例子中，` map()` 将一个字符串映射为另一个字符串，但是我们完全可以产生和接收类型完全不同的类型，从而改变流的数据类型。这里是一个例子：

```java
// streams/FunctionMap2.java
// Different input and output types
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

输出为：

```java
Numbered(1)
Numbered(5)
Numbered(7)
Numbered(9)
Numbered(11)
Numbered(13)
```

我们获取了许多 int 类型整数，并通过构造器 `Numbered::new` 将它们转化成为 **Numbereds** 类型。

如果使用 **Function** 产生的结果是数值类型的一种，你必须使用相似的 **mapTo**-operations 操作进行替代：

```java
// streams/FunctionMap3.java
// Producing numeric output streams
import java.util.*;
import java.util.stream.*;
class FunctionMap3 {
    public static void main(String[] args) {
        Stream.of("5", "7", "9")
        .mapToint(Integer::parseint)
        .forEach(n -> System.out.format("%d ", n));
        System.out.println();
        Stream.of("17", "19", "23")
        .mapTolong(long::parselong)
        .forEach(n -> System.out.format("%d ", n));
        System.out.println();
        Stream.of("17", "1.9", ".23")
        .mapTodouble(double::parsedouble)
        .forEach(n -> System.out.format("%f ", n));
    }
}
```

输出为：

```java
5 7 9
17 19 23
17.000000 1.900000 0.230000
```

不幸的是，Java 设计者并没有尽最大的努力来消除原始类型。

### 在 map() 期间组合流

假如你有一个即将到来的元素流，并且你打算对流元素使用 `map()` 函数。你已经找到了那些你在其他地方找不到的可爱的函数功能，但是这里有一个问题：这个函数功能产生一个流。你想要的只是产生一个元素流，但是你生成的是一个元素流的流。

`flatMap()` 做了两件事情：它获取你的流产生（ stream-producing）函数，并将其应用于新到的元素（正如 `map()` 所做的），然后获取每一个流并将其“展平”为元素。所以它的输出只是元素。

`flatMap(Function)`：当 **Function** 产生流时使用。

`flatMapToInt(Function)`：当 **Function** 产生 **IntStream**  时使用。

`flatMapToLong(Function)`：当 **Function** 产生 **LongStream** 时使用。

`flatMapToDouble(Function)`：当 **Function** 产生 **DoubleStream** 时使用。

为了了解它是如何工作的，我们将从 `map()` 的一个刻意设计的函数开始，这个函数接受一个整数并产生一个字符串流：

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

输出为：

```java
java.util.stream.ReferencePipeline$Head
java.util.stream.ReferencePipeline$Head
java.util.stream.ReferencePipeline$Head
```

我们天真的希望能够得到字符串流，但是我们得到的确实流元素为“Head”流的流。但是我们可以使用 `flatMap()` 来轻松解决这个问题：

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

输出为：

```java
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

因此，从映射返回的每个流都会自动展平为其组件字符串。

如下是另一演示，我们从一个整数流开始，然后使用每一个整数去创建许多随机数。

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

输出为：

```java
58 -1 55 93 -1 61 61 29 -1 68 0 22 7 -1 88 28 51 89 9 -1
```

我在这里引入了`concat()`，它以参数顺序组合了两个流。 因此，在每个随机 Integer 流的末尾，我添加一个 -1 作为标记，因此ni你可以看到最终流确实是从一组展平流中创建的。

因为 `rand.ints()` 产生了一个 **IntStream**，所以我必须使用 `flatMap()`、`concat()` 和 `of()` 的特定整数版本。

让我们再看一下将文件划分为单词流的任务。我们上一次遇到的是 **FileToWordsRegexp.java**，它的问题是它需要我们将整个文件读入行列表中 —— 因此我们需要存储该列表。我们真正想要的是创建以一个不需要中间存储的单词流。

再一次，我们使用 ` flatMap()` 来解决这个问题：

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
        Pattern.compile("\W+").splitAsStream(line));
    }
}
```

因为 `stream()` 方法可以自己完成整个创建流的构成，所以它现在是个静态方法。

注意 **\\\\W+** 是一个正则表达式。**\\\\W** 的意思是 “非单词字符”，**+** 的意思是“可以出现一次或者多次”。小写版本的 “**\\\\w**” 代表“单词字符”。

我们之前遇到的问题是 `Pattern.compile().splitAsStream()`产生的结果为流，这意味着当我们只是想要一个简单的单词流，在传入的行流（stream of lines）上调用 `map()` 会产生一个单词流的流。幸运的是，`flatMap()`  可以将元素流的流展平为一个简单的元素流。或者，我们可以使用 `String.split()` 生成一个数组，其可以被 `Arrays.stream()` 转化成为流：

```java
.flatMap(line -> Arrays.stream(line.split("\\W+"))))
```

因为我们拥有了一个真的流（而不是在 **FileToWordsRegexp.java** 基于集合存储的流），每一次我们想要一个新的流就必须从头创建，因为这个流并不能被重复使用：

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

输出为：

```java
Not much of a cheese shop really
```

在 `System.out.format()` 中的 **%s** 表名参数为 String 类型。



<!-- Optional -->

## Optional类


<!-- Terminal Operations -->
## 终端操作


<!-- Summary -->
## 本章小结


<!-- 分页 -->

<div style="page-break-after: always;"></div>
