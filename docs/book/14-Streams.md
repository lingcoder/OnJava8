[TOC]

<!-- Streams -->
# 第十四章 流式编程

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
