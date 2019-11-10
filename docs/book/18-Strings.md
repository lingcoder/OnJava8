[TOC]

<!-- Strings -->

# 第十八章 字符串
>字符串操作毫无疑问是计算机程序设计中最常见的行为之一。

在 Java 大展拳脚的 Web 系统中更是如此。在本章中，我们将深入学习在 Java 语言中应用最广泛的 `String` 类，并研究与之相关的类及工具。

<!-- Immutable Strings -->

## 字符串的不可变
`String` 对象是不可变的。查看 JDK 文档你就会发现，`String` 类中每一个看起来会修改 `String` 值的方法，实际上都是创建了一个全新的 `String` 对象，以包含修改后的字符串内容。而最初的 `String` 对象则丝毫未动。

看看下面的代码：
```java
// strings/Immutable.java
public class Immutable { 
    public static String upcase(String s) { 
        return s.toUpperCase(); 
    } 
    public static void main(String[] args) { 
        String q = "howdy";
        System.out.println(q); // howdy 
        String qq = upcase(q); 
        System.out.println(qq); // HOWDY 
        System.out.println(q); // howdy 
    } 
} 
/* Output: 
howdy
HOWDY 
howdy
*/ 
```
当把 `q` 传递给 `upcase()` 方法时，实际传递的是引用的一个拷贝。其实，每当把 String 对象作为方法的参数时，都会复制一份引用，而该引用所指向的对象其实一直待在单一的物理位置上，从未动过。

回到 `upcase()` 的定义，传入其中的引用有了名字 `s`，只有 `upcase()` 运行的时候，局部引用 `s` 才存在。一旦 `upcase()` 运行结束，`s` 就消失了。当然了，`upcase()` 的返回值，其实是最终结果的引用。这足以说明，`upcase()` 返回的引用已经指向了一个新的对象，而 `q` 仍然在原来的位置。

`String` 的这种行为正是我们想要的。例如：

```java
String s = "asdf";
String x = Immutable.upcase(s);
```
难道你真的希望 `upcase()` 方法改变其参数吗？对于一个方法而言，参数是为该方法提供信息的，而不是想让该方法改变自己的。在阅读这段代码时，读者自然会有这样的感觉。这一点很重要，正是有了这种保障，才使得代码易于编写和阅读。


<!-- Overloading + vs. StringBuilder -->
## `+` 的重载与 `StringBuilder`
`String` 对象是不可变的，你可以给一个 `String` 对象添加任意多的别名。因为 `String` 是只读的，所以指向它的任何引用都不可能修改它的值，因此，也就不会影响到其他引用。

不可变性会带来一定的效率问题。为 `String` 对象重载的 `+` 操作符就是一个例子。重载的意思是，一个操作符在用于特定的类时，被赋予了特殊的意义（用于 `String` 的 `+` 与 `+=` 是 Java 中仅有的两个重载过的操作符，Java 不允许程序员重载任何其他的操作符 [^1]）。

操作符 `+` 可以用来连接 `String`：
```java
// strings/Concatenation.java

public class Concatenation {
    public static void main(String[] args) { 
        String mango = "mango"; 
        String s = "abc" + mango + "def" + 47; 
        System.out.println(s);
    } 
}
/* Output:
abcmangodef47 
*/
```
可以想象一下，这段代码是这样工作的：`String` 可能有一个 `append()` 方法，它会生成一个新的 `String` 对象，以包含“abc”与 `mango` 连接后的字符串。该对象会再创建另一个新的 `String` 对象，然后与“def”相连，生成另一个新的对象，依此类推。

这种方式当然是可行的，但是为了生成最终的 `String` 对象，会产生一大堆需要垃圾回收的中间对象。我猜想，Java 设计者一开始就是这么做的（这也是软件设计中的一个教训：除非你用代码将系统实现，并让它运行起来，否则你无法真正了解它会有什么问题），然后他们发现其性能相当糟糕。

想看看以上代码到底是如何工作的吗？可以用 JDK 自带的 `javap` 工具来反编译以上代码。命令如下：
```java
javap -c Concatenation
```
这里的 `-c` 标志表示将生成 JVM 字节码。我们剔除不感兴趣的部分，然后做细微的修改，于是有了以下的字节码：
```x86asm
public static void main(java.lang.String[]); 
 Code:
  Stack=2, Locals=3, Args_size=1
  0: ldc #2; //String mango 
  2: astore_1 
  3: new #3; //class StringBuilder 
  6: dup 
  7: invokespecial #4; //StringBuilder."<init>":() 
  10: ldc #5; //String abc 
  12: invokevirtual #6; //StringBuilder.append:(String) 
  15: aload_1 
  16: invokevirtual #6; //StringBuilder.append:(String) 
  19: ldc #7; //String def 
  21: invokevirtual #6; //StringBuilder.append:(String) 
  24: bipush 47 
  26: invokevirtual #8; //StringBuilder.append:(I) 
  29: invokevirtual #9; //StringBuilder.toString:() 
  32: astore_2 
  33: getstatic #10; //Field System.out:PrintStream;
  36: aload_2 
  37: invokevirtual #11; //PrintStream.println:(String) 
  40: return
```
如果你有汇编语言的经验，以上代码应该很眼熟(其中的 `dup` 和 `invokevirtual` 语句相当于Java虚拟机上的汇编语句。即使你完全不了解汇编语言也无需担心)。需要重点注意的是：编译器自动引入了 `java.lang.StringBuilder` 类。虽然源代码中并没有使用 `StringBuilder` 类，但是编译器却自作主张地使用了它，就因为它更高效。

在这里，编译器创建了一个 `StringBuilder` 对象，用于构建最终的 `String`，并对每个字符串调用了一次 `append()` 方法，共计 4 次。最后调用 `toString()` 生成结果，并存为 `s` (使用的命令为 `astore_2`)。

现在，也许你会觉得可以随意使用 `String` 对象，反正编译器会自动为你做性能优化。可是在这之前，让我们更深入地看看编译器能为我们优化到什么程度。下面的例子采用两种方式生成一个 `String`：方法一使用了多个 `String` 对象；方法二在代码中使用了 `StringBuilder`。
```java
// strings/WhitherStringBuilder.java

public class WhitherStringBuilder { 
    public String implicit(String[] fields) { 
        String result = ""; 
        for(String field : fields) { 
            result += field;
        }
        return result; 
    }
    public String explicit(String[] fields) { 
        StringBuilder result = new StringBuilder(); 
        for(String field : fields) { 
            result.append(field); 
        } 
        return result.toString(); 
    }
}
```
现在运行 `javap -c WitherStringBuilder`，可以看到两种不同方法（我已经去掉不相关的细节）对应的字节码。首先是 `implicit()` 方法：
```x86asm
public java.lang.String implicit(java.lang.String[]); 
0: ldc #2 // String 
2: astore_2
3: aload_1 
4: astore_3 
5: aload_3 
6: arraylength 
7: istore 4 
9: iconst_0 
10: istore 5 
12: iload 5 
14: iload 4 
16: if_icmpge 51 
19: aload_3 
20: iload 5 
22: aaload 
23: astore 6 
25: new #3 // StringBuilder 
28: dup 
29: invokespecial #4 // StringBuilder."<init>"
32: aload_2 
33: invokevirtual #5 // StringBuilder.append:(String) 
36: aload 6 
38: invokevirtual #5 // StringBuilder.append:(String;) 
41: invokevirtual #6 // StringBuilder.toString:() 
44: astore_2 
45: iinc 5, 1 
48: goto 12 
51: aload_2 
52: areturn
```
注意从第 16 行到第 48 行构成了一个循环体。第 16 行：对堆栈中的操作数进行“大于或等于的整数比较运算”，循环结束时跳转到第 51 行。第 48 行：重新回到循环体的起始位置（第 12 行）。注意：`StringBuilder` 是在循环内构造的，这意味着每进行一次循环，会创建一个新的 `StringBuilder` 对象。

下面是 `explicit()` 方法对应的字节码：
```x86asm
public java.lang.String explicit(java.lang.String[]); 
0: new #3 // StringBuilder 
3: dup
4: invokespecial #4 // StringBuilder."<init>" 
7: astore_2 
8: aload_1 
9: astore_3 
10: aload_3 
11: arraylength 
12: istore 4 
14: iconst_0 
15: istore 5 
17: iload 5 
19: iload 4 
21: if_icmpge 43 
24: aload_3 
25: iload 5 
27: aaload 
28: astore 6 
30: aload_2 
31: aload 6 
33: invokevirtual #5 // StringBuilder.append:(String) 
36: pop
37: iinc 5, 1 
40: goto 17 
43: aload_2 
44: invokevirtual #6 // StringBuilder.toString:() 
47: areturn
```
可以看到，不仅循环部分的代码更简短、更简单，而且它只生成了一个 `StringBuilder` 对象。显式地创建 `StringBuilder` 还允许你预先为其指定大小。如果你已经知道最终字符串的大概长度，那预先指定 `StringBuilder` 的大小可以避免频繁地重新分配缓冲。

因此，当你为一个类编写 `toString()` 方法时，如果字符串操作比较简单，那就可以信赖编译器，它会为你合理地构造最终的字符串结果。但是，如果你要在 `toString()` 方法中使用循环，且可能有性能问题，那么最好自己创建一个 `StringBuilder` 对象，用它来构建最终结果。请参考以下示例：
```java
// strings/UsingStringBuilder.java 

import java.util.*; 
import java.util.stream.*; 
public class UsingStringBuilder { 
    public static String string1() { 
        Random rand = new Random(47);
        StringBuilder result = new StringBuilder("["); 
        for(int i = 0; i < 25; i++) { 
            result.append(rand.nextInt(100)); 
            result.append(", "); 
        } 
        result.delete(result.length()-2, result.length()); 
        result.append("]");
        return result.toString(); 
    } 
    public static String string2() { 
        String result = new Random(47)
            .ints(25, 0, 100)
            .mapToObj(Integer::toString)
            .collect(Collectors.joining(", "));
        return "[" + result + "]"; 
    } 
    public static void main(String[] args) { 
        System.out.println(string1()); 
        System.out.println(string2()); 
    }
} 
/* Output: 
[58, 55, 93, 61, 61, 29, 68, 0, 22, 7, 88, 28, 51, 89, 
9, 78, 98, 61, 20, 58, 16, 40, 11, 22, 4] 
[58, 55, 93, 61, 61, 29, 68, 0, 22, 7, 88, 28, 51, 89,
9, 78, 98, 61, 20, 58, 16, 40, 11, 22, 4] 
*/ 
```
在方法 `string1()` 中，最终结果是用 `append()` 语句拼接起来的。如果你想走捷径，例如：`append(a + ": " + c)`，编译器就会掉入陷阱，从而为你另外创建一个 `StringBuilder` 对象处理括号内的字符串操作。如果拿不准该用哪种方式，随时可以用 `javap` 来分析你的程序。

`StringBuilder` 提供了丰富而全面的方法，包括 `insert()`、`replace()`、`substring()`，甚至还有`reverse()`，但是最常用的还是 `append()` 和 `toString()`。还有 `delete()`，上面的例子中我们用它删除最后一个逗号和空格，以便添加右括号。

`string2()` 使用了 `Stream`，这样代码更加简洁美观。可以证明，`Collectors.joining()` 内部也是使用的 `StringBuilder`，这种写法不会影响性能！

`StringBuilder `是 Java SE5 引入的，在这之前用的是 `StringBuffer`。后者是线程安全的（参见[并发编程](./24-Concurrent-Programming.md)），因此开销也会大些。使用 `StringBuilder` 进行字符串操作更快一点。

<!-- Unintended Recursion -->

## 意外递归
Java 中的每个类从根本上都是继承自 `Object`，标准集合类也是如此，它们都有 `toString()` 方法，并且覆盖了该方法，使得它生成的 `String` 结果能够表达集合自身，以及集合包含的对象。例如 `ArrayList.toString()`，它会遍历 `ArrayList` 中包含的所有对象，调用每个元素上的 `toString()` 方法：
```java
// strings/ArrayListDisplay.java 
import java.util.*;
import java.util.stream.*; 
import generics.coffee.*;
public class ArrayListDisplay { 
    public static void main(String[] args) {
        List<Coffee> coffees = 
            Stream.generate(new CoffeeSupplier())
                .limit(10)
                .collect(Collectors.toList()); 
        System.out.println(coffees); 
    } 
}
/* Output: 
[Americano 0, Latte 1, Americano 2, Mocha 3, Mocha 4, 
Breve 5, Americano 6, Latte 7, Cappuccino 8, Cappuccino 9] 
*/ 
```
如果你希望 `toString()` 打印出类的内存地址，也许你会考虑使用 `this` 关键字：
```java
// strings/InfiniteRecursion.java 
// Accidental recursion 
// {ThrowsException} 
// {VisuallyInspectOutput} Throws very long exception
import java.util.*;
import java.util.stream.*;

public class InfiniteRecursion { 
    @Override 
    public String toString() { 
        return " InfiniteRecursion address: " + this + "\n"
    } 
    public static void main(String[] args) { 
        Stream.generate(InfiniteRecursion::new) 
            .limit(10) 
            .forEach(System.out::println); 
    } 
} 
```
当你创建了 `InfiniteRecursion` 对象，并将其打印出来的时候，你会得到一串很长的异常信息。如果你将该 `InfiniteRecursion` 对象存入一个 `ArrayList` 中，然后打印该 `ArrayList`，同样也会抛出异常。其实，当运行到如下代码时：
```java
"InfiniteRecursion address: " + this 
```
这里发生了自动类型转换，由 `InfiniteRecursion` 类型转换为 `String` 类型。因为编译器发现一个 `String` 对象后面跟着一个 “+”，而 “+” 后面的对象不是 `String`，于是编译器试着将 `this` 转换成一个 `String`。它怎么转换呢？正是通过调用 `this` 上的 `toString()` 方法，于是就发生了递归调用。

如果你真的想要打印对象的内存地址，应该调用 `Object.toString()` 方法，这才是负责此任务的方法。所以，不要使用 `this`，而是应该调用 `super.toString()` 方法。


<!-- Operations on Strings -->
## 字符串操作
以下是 `String` 对象具备的一些基本方法。重载的方法归纳在同一行中：

| 方法 | 参数，重载版本 | 作用 |
| ---- | ---- | ---- |
| 构造方法 | 默认版本，`String`，`StringBuilder`，`StringBuffer`，`char`数组，`byte`数组 | 创建`String`对象 |
| `length()` | | `String`中字符的个数 |
| `charAt()` | `int`索引|获取`String`中索引位置上的`char` |
| `getChars()`，`getBytes()` | 待复制部分的开始和结束索引，复制的目标数组，目标数组的开始索引 | 复制`char`或`byte`到一个目标数组中 |
| `toCharArray()` | | 生成一个`char[]`，包含`String`中的所有字符 |
| `equals()`，`equalsIgnoreCase()` | 与之进行比较的`String` | 比较两个`String`的内容是否相同。如果相同，结果为`true` |
| `compareTo()`，`compareToIgnoreCase()` | 与之进行比较的`String` | 按词典顺序比较`String`的内容，比较结果为负数、零或正数。注意，大小写不等价 |
| `contains()` | 要搜索的`CharSequence` | 如果该`String`对象包含参数的内容，则返回`true` |
| `contentEquals()` | 与之进行比较的`CharSequence`或`StringBuffer` | 如果该`String`对象与参数的内容完全一致，则返回`true` |
| `isEmpty()` | | 返回`boolean`结果，以表明`String`对象的长度是否为0 |
| `regionMatches()` | 该`String`的索引偏移量，另一个`String`及其索引偏移量，要比较的长度。重载版本增加了“忽略大小写”功能|返回`boolean`结果，以表明所比较区域是否相等 |
| `startsWith()` | 可能的起始`String`。重载版本在参数中增加了偏移量 | 返回`boolean`结果，以表明该`String`是否以传入参数开始 |
| `endsWith()` | 该`String`可能的后缀`String` | 返回`boolean`结果，以表明此参数是否是该字符串的后缀 |
| `indexOf()`，`lastIndexOf()` | 重载版本包括：`char`，`char`与起始索引，`String`，`String`与起始索引 | 如果该`String`并不包含此参数，就返回-1；否则返回此参数在`String`中的起始索引。`lastIndexOf`()是从后往前搜索 |
| `matches()` | 一个正则表达式 | 返回`boolean`结果，以表明该`String`和给出的正则表达式是否匹配 |
| `split()` | 一个正则表达式。可选参数为需要拆分的最大数量 | 按照正则表达式拆分`String`，返回一个结果数组 |
| `join()`（Java8引入的）|分隔符，待拼字符序列。用分隔符将字符序列拼接成一个新的`String` |用分隔符拼接字符片段，产生一个新的`String` |
| `substring()`（即`subSequence()`）| 重载版本：起始索引；起始索引+终止索引 | 返回一个新的`String`对象，以包含参数指定的子串 |
| `concat()` | 要连接的`String` |返回一个新的`String`对象，内容为原始`String`连接上参数`String` |
| `replace()` | 要替换的字符，用来进行替换的新字符。也可以用一个`CharSequence`替换另一个`CharSequence` |返回替换字符后的新`String`对象。如果没有替换发生，则返回原始的`String`对象 |
| `replaceFirst()` |要替换的正则表达式，用来进行替换的`String` | 返回替换首个目标字符串后的`String`对象 |
| `replaceAll()` | 要替换的正则表达式，用来进行替换的`String` | 返回替换所有目标字符串后的`String`对象 |
| `toLowerCase()`，`toUpperCase()` | | 将字符的大小写改变后，返回一个新的`String`对象。如果没有任何改变，则返回原始的`String`对象|
| `trim()` | |将`String`两端的空白符删除后，返回一个新的`String`对象。如果没有任何改变，则返回原始的`String`对象|
| `valueOf()`（`static`）|重载版本：`Object`；`char[]`；`char[]`，偏移量，与字符个数；`boolean`；`char`；`int`；`long`；`float`；`double` | 返回一个表示参数内容的`String` |
| `intern()` | | 为每个唯一的字符序列生成一个且仅生成一个`String`引用 |
| `format()` | 要格式化的字符串，要替换到格式化字符串的参数 | 返回格式化结果`String` |

从这个表可以看出，当需要改变字符串的内容时，`String` 类的方法都会返回一个新的 `String` 对象。同时，如果内容不改变，`String` 方法只是返回原始对象的一个引用而已。这可以节约存储空间以及避免额外的开销。

本章稍后还将介绍正则表达式在 `String` 方法中的应用。

<!-- Formatting Output -->

## 格式化输出
在长久的等待之后，Java SE5 终于推出了 C 语言中 `printf()` 风格的格式化输出这一功能。这不仅使得控制输出的代码更加简单，同时也给与Java开发者对于输出格式与排列更强大的控制能力。
### `printf()`
C 语言的 `printf()` 并不像 Java 那样连接字符串，它使用一个简单的格式化字符串，加上要插入其中的值，然后将其格式化输出。 `printf()` 并不使用重载的 `+` 操作符（C语言没有重载）来连接引号内的字符串或字符串变量，而是使用特殊的占位符来表示数据将来的位置。而且它还将插入格式化字符串的参数，以逗号分隔，排成一行。例如：
```c
System.out.printf("Row 1: [%d %f]%n", x, y);
```
这一行代码在运行的时候，首先将 `x` 的值插入到 `%d_` 的位置，然后将 `y` 的值插入到 `%f` 的位置。这些占位符叫做*格式修饰符*，它们不仅指明了插入数据的位置，同时还指明了将会插入什么类型的变量，以及如何格式化。在这个例子中 `%d` 表示 `x` 是一个整数，`%f` 表示 `y` 是一个浮点数（`float` 或者 `double`）。
### `System.out.format()`
Java SE5 引入了 `format()` 方法，可用于 `PrintStream` 或者 `PrintWriter` 对象（你可以在 [附录:流式 I/O](./Appendix-IO-Streams.md) 了解更多内容），其中也包括 `System.out` 对象。`format()` 方法模仿了 C 语言的 `printf()`。如果你比较怀旧的话，也可以使用 `printf()`。以下是一个简单的示例：
```java
// strings/SimpleFormat.java 

public class SimpleFormat {   
    public static void main(String[] args) {     
        int x = 5;     
        double y = 5.332542;     
        // The old way: 
        System.out.println("Row 1: [" + x + " " + y + "]");     
        // The new way:     
        System.out.format("Row 1: [%d %f]%n", x, y);     
        // or     
        System.out.printf("Row 1: [%d %f]%n", x, y);   
    } 
} 
/* Output: 
Row 1: [5 5.332542] 
Row 1: [5 5.332542] 
Row 1: [5 5.332542] 
*/
```
可以看到，`format()` 和  `printf()` 是等价的，它们只需要一个简单的格式化字符串，加上一串参数即可，每个参数对应一个格式修饰符。

`String` 类也有一个 `static format()` 方法，可以格式化字符串。

### `Formatter` 类
在 Java 中，所有的格式化功能都是由 `java.util.Formatter` 类处理的。可以将 `Formatter` 看做一个翻译器，它将你的格式化字符串与数据翻译成需要的结果。当你创建一个 `Formatter` 对象时，需要向其构造器传递一些信息，告诉它最终的结果将向哪里输出：
```java
// strings/Turtle.java 
import java.io.*;
import java.util.*;

public class Turtle {   
    private String name;   
    private Formatter f;  
    public Turtle(String name, Formatter f) {
        this.name = name;     
        this.f = f;   
    }   
    public void move(int x, int y) {     
        f.format("%s The Turtle is at (%d,%d)%n",       
            name, x, y);   
    }
    public static void main(String[] args) {    
        PrintStream outAlias = System.out;     
        Turtle tommy = new Turtle("Tommy",
            new Formatter(System.out));     
        Turtle terry = new Turtle("Terry",       
            new Formatter(outAlias));     
        tommy.move(0,0);     
        terry.move(4,8);     
        tommy.move(3,4);     
        terry.move(2,5);     
        tommy.move(3,3);     
        terry.move(3,3);   
    } 
} 
/* Output: 
Tommy The Turtle is at (0,0) 
Terry The Turtle is at (4,8) 
Tommy The Turtle is at (3,4) 
Terry The Turtle is at (2,5) 
Tommy The Turtle is at (3,3) 
Terry The Turtle is at (3,3) 
*/
```
格式化修饰符 `%s` 表明这里需要 `String` 参数。

所有的 `tommy` 都将输出到 `System.out`，而所有的 `terry` 则都输出到 `System.out` 的一个别名中。`Formatter` 的重载构造器支持输出到多个路径，不过最常用的还是 `PrintStream()`（如上例）、`OutputStream` 和 `File`。你可以在 [附录:流式 I/O](././Appendix-IO-Streams.md) 中了解更多信息。
### 格式化修饰符
在插入数据时，如果想要优化空格与对齐，你需要更精细复杂的格式修饰符。以下是其通用语法：
```java
%[argument_index$][flags][width][.precision]conversion 
```
最常见的应用是控制一个字段的最小长度，这可以通过指定 *width* 来实现。`Formatter `对象通过在必要时添加空格，来确保一个字段至少达到设定长度。默认情况下，数据是右对齐的，不过可以通过使用 `-` 标志来改变对齐方向。

与 *width* 相对的是 *precision*，用于指定最大长度。*width* 可以应用于各种类型的数据转换，并且其行为方式都一样。*precision* 则不然，当应用于不同类型的数据转换时，*precision* 的意义也不同。在将 *precision* 应用于 `String` 时，它表示打印 `string` 时输出字符的最大数量。而在将 *precision* 应用于浮点数时，它表示小数部分要显示出来的位数（默认是 6 位小数），如果小数位数过多则舍入，太少则在尾部补零。由于整数没有小数部分，所以 *precision* 无法应用于整数，如果你对整数应用 *precision*，则会触发异常。

下面的程序应用格式修饰符来打印一个购物收据。这是 *Builder* 设计模式的一个简单实现，即先创建一个初始对象，然后逐渐添加新东西，最后调用 `build()` 方法完成构建：
```java
// strings/ReceiptBuilder.java 
import java.util.*; 

public class ReceiptBuilder {   
    private double total = 0;   
    private Formatter f =     
        new Formatter(new StringBuilder());   
    public ReceiptBuilder() {     
        f.format(       
          "%-15s %5s %10s%n", "Item", "Qty", "Price");     
        f.format(       
          "%-15s %5s %10s%n", "----", "---", "-----");   
        }   
    public void add(String name, int qty, double price) {     
        f.format("%-15.15s %5d %10.2f%n", name, qty, price);     
        total += price * qty;   
    }  
    public String build() {     
        f.format("%-15s %5s %10.2f%n", "Tax", "",       
          total * 0.06);     
        f.format("%-15s %5s %10s%n", "", "", "-----");     
        f.format("%-15s %5s %10.2f%n", "Total", "",       
          total * 1.06);     
        return f.toString();   
    }   
    public static void main(String[] args) {     
        ReceiptBuilder receiptBuilder =       
          new ReceiptBuilder();     
        receiptBuilder.add("Jack's Magic Beans", 4, 4.25);     
        receiptBuilder.add("Princess Peas", 3, 5.1);     
        receiptBuilder.add(       
          "Three Bears Porridge", 1, 14.29);     
        System.out.println(receiptBuilder.build());   
    } 
} 
/* Output: 
Item              Qty      Price 
----              ---      ----- 
Jack's Magic Be     4       4.25 
Princess Peas       3       5.10 
Three Bears Por     1      14.29 
Tax                         2.80 
                           ----- 
Total                      49.39 
*/ 
```
通过传入一个 `StringBuilder` 对象到 `Formatter` 的构造器，我们指定了一个容器来构建目标 `String`。你也可以通过不同的构造器参数，把结果输出到标准输出，甚至是一个文件里。

正如你所见，通过相当简洁的语法，`Formatter ` 提供了对空格与对齐的强大控制能力。在该程序中，为了恰当地控制间隔，格式化字符串被重复利用了多遍。
### `Formatter` 转换
下面的表格展示了最常用的类型转换：

| 类型 | 含义 |
| :----: | :---- |
| `d` | 整型（十进制） |
| `c` | Unicode字符 |
| `b` | Boolean值 |
| `s` | String |
| `f` | 浮点数（十进制） |
| `e` | 浮点数（科学计数） |
| `x` | 整型（十六进制） |
| `h` | 散列码（十六进制） |
| `%` | 字面值“%” |

下面的程序演示了这些转换是如何工作的：

```java
// strings/Conversion.java 
import java.math.*;
import java.util.*; 

public class Conversion {   
    public static void main(String[] args) {     
        Formatter f = new Formatter(System.out); 

        char u = 'a';     
        System.out.println("u = 'a'");     
        f.format("s: %s%n", u);     
        // f.format("d: %d%n", u);     
        f.format("c: %c%n", u);     
        f.format("b: %b%n", u);     
        // f.format("f: %f%n", u);     
        // f.format("e: %e%n", u);     
        // f.format("x: %x%n", u);     
        f.format("h: %h%n", u); 

        int v = 121;     
        System.out.println("v = 121");    
        f.format("d: %d%n", v);     
        f.format("c: %c%n", v);     
        f.format("b: %b%n", v);     
        f.format("s: %s%n", v);     
        // f.format("f: %f%n", v);     
        // f.format("e: %e%n", v);     
        f.format("x: %x%n", v);     
        f.format("h: %h%n", v); 

        BigInteger w = new BigInteger("50000000000000");     
        System.out.println(       
          "w = new BigInteger(\"50000000000000\")");     
        f.format("d: %d%n", w);     
        // f.format("c: %c%n", w);     
        f.format("b: %b%n", w);     
        f.format("s: %s%n", w);     
        // f.format("f: %f%n", w);     
        // f.format("e: %e%n", w);     
        f.format("x: %x%n", w);     
        f.format("h: %h%n", w); 

        double x = 179.543;     
        System.out.println("x = 179.543");     
        // f.format("d: %d%n", x);     
        // f.format("c: %c%n", x);     
        f.format("b: %b%n", x);     
        f.format("s: %s%n", x);     
        f.format("f: %f%n", x);     
        f.format("e: %e%n", x);     
        // f.format("x: %x%n", x);     
        f.format("h: %h%n", x); 

        Conversion y = new Conversion();     
        System.out.println("y = new Conversion()"); 

        // f.format("d: %d%n", y);     
        // f.format("c: %c%n", y);     
        f.format("b: %b%n", y);     
        f.format("s: %s%n", y);     
        // f.format("f: %f%n", y);     
        // f.format("e: %e%n", y);     
        // f.format("x: %x%n", y);     
        f.format("h: %h%n", y); 

        boolean z = false;     
        System.out.println("z = false");     
        // f.format("d: %d%n", z);     
        // f.format("c: %c%n", z);     
        f.format("b: %b%n", z);     
        f.format("s: %s%n", z);     
        // f.format("f: %f%n", z);     
        // f.format("e: %e%n", z);     
        // f.format("x: %x%n", z);     
        f.format("h: %h%n", z);   
    } 
} 
/* Output: 
u = 'a' 
s: a 
c: a 
b: true 
h: 61 
v = 121 
d: 121 
c: y 
b: true 
s: 121 
x: 79 
h: 79 
w = new BigInteger("50000000000000") 
d: 50000000000000 
b: true 
s: 50000000000000 
x: 2d79883d2000 
h: 8842a1a7 
x = 179.543 
b: true 
s: 179.543 
f: 179.543000 
e: 1.795430e+02 
h: 1ef462c 
y = new Conversion() 
b: true 
s: Conversion@15db9742 
h: 15db9742 
z = false 
b: false 
s: false
h: 4d5 
*/
```
被注释的代码表示，针对相应类型的变量，这些转换是无效的。如果执行这些转换，则会触发异常。

注意，程序中的每个变量都用到了 `b` 转换。虽然它对各种类型都是合法的，但其行为却不一定与你想象的一致。对于 `boolean` 基本类型或 `Boolean` 对象，其转换结果是对应的 `true` 或 `false`。但是，对其他类型的参数，只要该参数不为 `null`，其转换结果永远都是 `true`。即使是数字 0，转换结果依然为 `true`，而这在其他语言中（包括C），往往转换为 `false`。所以，将 `b` 应用于非布尔类型的对象时请格外小心。

还有许多不常用的类型转换与格式修饰符选项，你可以在 JDK 文档中的 `Formatter` 类部分找到它们。
### `String.format()`
Java SE5 也参考了 C 中的 `sprintf()` 方法，以生成格式化的 `String` 对象。`String.format()` 是一个 `static` 方法，它接受与 `Formatter.format()` 方法一样的参数，但返回一个 `String` 对象。当你只需使用一次 `format()` 方法的时候，`String.format()` 用起来很方便。例如：
```java
// strings/DatabaseException.java 

public class DatabaseException extends Exception {   
    public DatabaseException(int transactionID,     
      int queryID, String message) {     
      super(String.format("(t%d, q%d) %s", transactionID,         
        queryID, message));   
    }   
    public static void main(String[] args) {     
      try {       
        throw new DatabaseException(3, 7, "Write failed");     
      } catch(Exception e) {       
        System.out.println(e);     
      }   
    } 
} 
/* 
Output: 
DatabaseException: (t3, q7) Write failed 
*/
```
其实在 `String.format()` 内部，它也是创建了一个 `Formatter` 对象，然后将你传入的参数转给 `Formatter`。不过，与其自己做这些事情，不如使用便捷的 `String.format()` 方法，何况这样的代码更清晰易读。
#### 一个十六进制转储（dump）工具
在第二个例子中，我们把二进制文件转换为十六进制格式。下面的小工具使用了 `String.format()` 方法，以可读的十六进制格式将字节数组打印出来：
```java
// strings/Hex.java 
// {java onjava.Hex} 
package onjava;
import java.io.*; 
import java.nio.file.*; 

public class Hex {   
    public static String format(byte[] data) {     
        StringBuilder result = new StringBuilder();     
        int n = 0;     
        for(byte b : data) {       
            if(n % 16 == 0)         
                result.append(String.format("%05X: ", n));       
            result.append(String.format("%02X ", b));       
            n++;       
            if(n % 16 == 0) result.append("\n");     
        }     
        result.append("\n");     
        return result.toString();   
    }   
    public static void main(String[] args) throws Exception {  
        if(args.length == 0)       
            // Test by displaying this class file:       
            System.out.println(format(         
                Files.readAllBytes(Paths.get(           
                  "build/classes/main/onjava/Hex.class"))));     
        else       
            System.out.println(format(         
                Files.readAllBytes(Paths.get(args[0]))));   
    } 
} 
/* Output: (First 6 Lines) 
00000: CA FE BA BE 00 00 00 34 00 61 0A 00 05 00 31 07 
00010: 00 32 0A 00 02 00 31 08 00 33 07 00 34 0A 00 35 
00020: 00 36 0A 00 0F 00 37 0A 00 02 00 38 08 00 39 0A 
00030: 00 3A 00 3B 08 00 3C 0A 00 02 00 3D 09 00 3E 00 
00040: 3F 08 00 40 07 00 41 0A 00 42 00 43 0A 00 44 00 
00050: 45 0A 00 14 00 46 0A 00 47 00 48 07 00 49 01 00
                  ... 
*/
```
为了打开及读入二进制文件，我们用到了另一个工具 `Files.readAllBytes()`，这已经在 [Files章节](./17-Files.md) 介绍过了。这里的 `readAllBytes()` 方法将整个文件以 `byte` 数组的形式返回。

<!-- Regular Expressions -->

## 正则表达式
很久之前，*正则表达式*就已经整合到标准 Unix 工具集之中，例如 sed、awk 和程序语言之中了，如 Python 和Perl（有些人认为正是正则表达式促成了 Perl 的成功）。而在 Java 中，字符串操作还主要集中于`String`、`StringBuffer` 和 `StringTokenizer` 类。与正则表达式相比较，它们只能提供相当简单的功能。

正则表达式是一种强大而灵活的文本处理工具。使用正则表达式，我们能够以编程的方式，构造复杂的文本模式，并对输入 `String` 进行搜索。一旦找到了匹配这些模式的部分，你就能随心所欲地对它们进行处理。初学正则表达式时，其语法是一个难点，但它确实是一种简洁、动态的语言。正则表达式提供了一种完全通用的方式，能够解决各种 `String` 处理相关的问题：匹配、选择、编辑以及验证。
### 基础
一般来说，正则表达式就是以某种方式来描述字符串，因此你可以说：“如果一个字符串含有这些东西，那么它就是我正在找的东西。”例如，要找一个数字，它可能有一个负号在最前面，那你就写一个负号加上一个问号，就像这样：
```java
-?
```
要描述一个整数，你可以说它有一位或多位阿拉伯数字。在正则表达式中，用 `\d` 表示一位数字。如果在其他语言中使用过正则表达式，那你可能就能发现 Java 对反斜线 \ 的不同处理方式。在其他语言中，`\\` 表示“我想要在正则表达式中插入一个普通的（字面上的）反斜线，请不要给它任何特殊的意义。”而在Java中，`\\` 的意思是“我要插入一个正则表达式的反斜线，所以其后的字符具有特殊的意义。”例如，如果你想表示一位数字，那么正则表达式应该是 `\\d`。如果你想插入一个普通的反斜线，应该这样写 `\\\`。不过换行符和制表符之类的东西只需要使用单反斜线：`\n\t`。 [^2]

要表示“一个或多个之前的表达式”，应该使用 `+`。所以，如果要表示“可能有一个负号，后面跟着一位或多位数字”，可以这样：
```java
-?\\d+ 
```
应用正则表达式最简单的途径，就是利用 `String` 类内建的功能。例如，你可以检查一个 `String` 是否匹配如上所述的正则表达式：
```java
// strings/IntegerMatch.java 

public class IntegerMatch {  
    public static void main(String[] args) {     
        System.out.println("-1234".matches("-?\\d+"));    
        System.out.println("5678".matches("-?\\d+"));     
        System.out.println("+911".matches("-?\\d+"));     
        System.out.println("+911".matches("(-|\\+)?\\d+"));   
    }
}
/* Output: 
true 
true 
false 
true 
*/ 
```
前两个字符串都满足对应的正则表达式，匹配成功。第三个字符串以 `+` 开头，这也是一个合法的符号，但与对应的正则表达式却不匹配。因此，我们的正则表达式应该描述为：“可能以一个加号或减号开头”。在正则表达式中，用括号将表达式进行分组，用竖线 ` | ` 表示或操作。也就是：
```java
(-|\\+)? 
```
这个正则表达式表示字符串的起始字符可能是一个 `-` 或 `+`，或者二者都没有（因为后面跟着 `?` 修饰符）。因为字符 `+` 在正则表达式中有特殊的意义，所以必须使用 `\\` 将其转义，使之成为表达式中的一个普通字符。

`String`类还自带了一个非常有用的正则表达式工具——`split()` 方法，其功能是“将字符串从正则表达式匹配的地方切开。”

```java
// strings/Splitting.java import java.util.*; 

public class Splitting {
    public static String knights =   
      "Then, when you have found the shrubbery, " +
      "you must cut down the mightiest tree in the " +
      "forest...with... a herring!";
    public static void split(String regex) {
        System.out.println(
          Arrays.toString(knights.split(regex)));
        }
    public static void main(String[] args) {
        split(" "); // Doesn't have to contain regex chars
        split("\\W+"); // Non-word characters
        split("n\\W+"); // 'n' followed by non-words
    }
}
/* Output:
[Then,, when, you, have, found, the, shrubbery,, you,
must, cut, down, the, mightiest, tree, in, the,
forest...with..., a, herring!]
[Then, when, you, have, found, the, shrubbery, you,
must, cut, down, the, mightiest, tree, in, the, forest,
with, a, herring]
[The, whe, you have found the shrubbery, you must cut
dow, the mightiest tree i, the forest...with... a
herring!]
*/
```
首先看第一个语句，注意这里用的是普通的字符作为正则表达式，其中并不包含任何特殊字符。因此第一个 `split()` 只是按空格来划分字符串。

第二个和第三个 `split()` 都用到了 `\\W`，它的意思是一个非单词字符（如果 W 小写，`\\w`，则表示一个单词字符）。通过第二个例子可以看到，它将标点字符删除了。第三个 `split()` 表示“字母 `n` 后面跟着一个或多个非单词字符。”可以看到，在原始字符串中，与正则表达式匹配的部分，在最终结果中都不存在了。

`String.split()` 还有一个重载的版本，它允许你限制字符串分割的次数。

用正则表达式进行替换操作时，你可以只替换第一处匹配，也可以替换所有的匹配：
```java
// strings/Replacing.java 

public class Replacing {
    static String s = Splitting.knights;   
    public static void main(String[] args) {
        System.out.println(
          s.replaceFirst("f\\w+", "located"));
        System.out.println(       
          s.replaceAll("shrubbery|tree|herring","banana"));   
    } 
}
/* Output: 
Then, when you have located the shrubbery, you must cut 
down the mightiest tree in the forest...with... a 
herring! 
Then, when you have found the banana, you must cut down
the mightiest banana in the forest...with... a banana! 
*/
```
第一个表达式要匹配的是，以字母 `f` 开头，后面跟一个或多个字母（注意这里的 `w` 是小写的）。并且只替换掉第一个匹配的部分，所以 “found” 被替换成 “located”。

第二个表达式要匹配的是三个单词中的任意一个，因为它们以竖线分割表示“或”，并且替换所有匹配的部分。

稍后你会看到，`String` 之外的正则表达式还有更强大的替换工具，例如，可以通过方法调用执行替换。而且，如果正则表达式不是只使用一次的话，非 `String` 对象的正则表达式明显具备更佳的性能。
### 创建正则表达式
我们首先从正则表达式可能存在的构造集中选取一个很有用的子集，以此开始学习正则表达式。正则表达式的完整构造子列表，请参考JDK文档 `java.util.regex` 包中的 `Pattern`类。

| 表达式 | 含义 |
| :---- | :---- |
| `B` | 指定字符`B` |
| `\xhh` | 十六进制值为`0xhh`的字符 |
| `\uhhhh` | 十六进制表现为`0xhhhh`的Unicode字符 |
| `\t` | 制表符Tab |
| `\n` | 换行符 |
| `\r` | 回车 |
| `\f` | 换页 |
| `\e` | 转义（Escape） |

当你学会了使用字符类（character classes）之后，正则表达式的威力才能真正显现出来。以下是一些创建字符类的典型方式，以及一些预定义的类：

| 表达式 | 含义 |
| :---- | :---- |
| `.` | 任意字符 |
| `[abc]` |包含`a`、`b`或`c`的任何字符（和`a|b|c`作用相同）|
| `[^abc]` | 除`a`、`b`和`c`之外的任何字符（否定） |
| `[a-zA-Z]` | 从`a`到`z`或从`A`到`Z`的任何字符（范围） |
| `[abc[hij]]` | `a`、`b`、`c`、`h`、`i`、`j`中的任意字符（与`a|b|c|h|i|j`作用相同）（合并） |
| `[a-z&&[hij]]` | 任意`h`、`i`或`j`（交） |
| `\s` | 空白符（空格、tab、换行、换页、回车） |
| `\S` | 非空白符（`[^\s]`） |
| `\d` | 数字（`[0-9]`） |
| `\D` | 非数字（`[^0-9]`） |
| `\w` | 词字符（`[a-zA-Z_0-9]`） |
| `\W` | 非词字符（`[^\w]`） |

这里只列出了部分常用的表达式，你应该将JDK文档中 `java.util.regex.Pattern` 那一页加入浏览器书签中，以便在需要的时候方便查询。

| 逻辑操作符 | 含义 |
| :----: | :---- |
| `XY` | `Y`跟在`X`后面 |
| `X|Y` | `X`或`Y` |
| `(X)` | 捕获组（capturing group）。可以在表达式中用`\i`引用第i个捕获组 |

下面是不同的边界匹配符：

| 边界匹配符 | 含义 |
| :----: | :---- |
| `^` | 一行的开始 |
| `$` | 一行的结束 |
| `\b` | 词的边界 |
| `\B` | 非词的边界 |
| `\G` | 前一个匹配的结束 |

作为演示，下面的每一个正则表达式都能成功匹配字符序列“Rudolph”：
```java
// strings/Rudolph.java 

public class Rudolph {   
    public static void main(String[] args) {     
        for(String pattern : new String[]{       
          "Rudolph",       
          "[rR]udolph",       
          "[rR][aeiou][a-z]ol.*",       
          "R.*" })       
        System.out.println("Rudolph".matches(pattern));   
    } 
} 
/* Output: 
true 
true 
true 
true 
*/
```
我们的目的并不是编写最难理解的正则表达式，而是尽量编写能够完成任务的、最简单以及最必要的正则表达式。一旦真正开始使用正则表达式了，你就会发现，在编写新的表达式之前，你通常会参考代码中已经用到的正则表达式。
### 量词
量词描述了一个模式捕获输入文本的方式：

+ **贪婪型**：
量词总是贪婪的，除非有其他的选项被设置。贪婪表达式会为所有可能的模式发现尽可能多的匹配。导致此问题的一个典型理由就是假定我们的模式仅能匹配第一个可能的字符组，如果它是贪婪的，那么它就会继续往下匹配。

+ **勉强型**：
用问号来指定，这个量词匹配满足模式所需的最少字符数。因此也被称作懒惰的、最少匹配的、非贪婪的或不贪婪的。

+ **占有型**：
目前，这种类型的量词只有在 Java 语言中才可用（在其他语言中不可用），并且也更高级，因此我们大概不会立刻用到它。当正则表达式被应用于 `String` 时，它会产生相当多的状态，以便在匹配失败时可以回溯。而“占有的”量词并不保存这些中间状态，因此它们可以防止回溯。它们常常用于防止正则表达式失控，因此可以使正则表达式执行起来更高效。

| 贪婪型 | 勉强型 | 占有型 | 如何匹配 |
| ---- | ---- | ---- | ---- |
| `X?` | `X??` | `X?+` | 一个或零个`X` |
| `X*` | `X*?` | `X*+` | 零个或多个`X` |
| `X+` | `X+?` | `X++` | 一个或多个`X` |
| `X{n}` | `X{n}?` | `X{n}+` | 恰好`n`次`X` |
| `X{n,}` | `X{n,}?` | `X{n,}+` | 至少`n`次`X` |
| `X{n,m}` | `X{n,m}?` | `X{n,m}+` | `X`至少`n`次，但不超过`m`次 |

应该非常清楚地意识到，表达式 `X` 通常必须要用圆括号括起来，以便它能够按照我们期望的效果去执行。例如：
```java
abc+
```
看起来它似乎应该匹配1个或多个`abc`序列，如果我们把它应用于输入字符串`abcabcabc`，则实际上会获得3个匹配。然而，这个表达式实际上表示的是：匹配`ab`，后面跟随1个或多个`c`。要表明匹配1个或多个完整的字符串`abc`，我们必须这样表示：
```java
(abc)+
```
你会发现，在使用正则表达式时很容易混淆，因为它是一种在 Java 之上的新语言。
### `CharSequence`
接口 `CharSequence` 从 `CharBuffer`、`String`、`StringBuffer`、`StringBuilder` 类中抽象出了字符序列的一般化定义：
```java
interface CharSequence {   
    char charAt(int i);   
    int length();
    CharSequence subSequence(int start, int end);
    String toString(); 
}
```
因此，这些类都实现了该接口。多数正则表达式操作都接受 `CharSequence` 类型参数。
### `Pattern` 和 `Matcher`
通常，比起功能有限的 `String` 类，我们更愿意构造功能强大的正则表达式对象。只需导入 `java.util.regex`包，然后用 `static Pattern.compile()` 方法来编译你的正则表达式即可。它会根据你的 `String` 类型的正则表达式生成一个 `Pattern` 对象。接下来，把你想要检索的字符串传入 `Pattern` 对象的 `matcher()` 方法。`matcher()` 方法会生成一个 `Matcher` 对象，它有很多功能可用（可以参考 `java.util.regext.Matcher` 的 JDK 文档）。例如，它的 `replaceAll()` 方法能将所有匹配的部分都替换成你传入的参数。

作为第一个示例，下面的类可以用来测试正则表达式，看看它们能否匹配一个输入字符串。第一个控制台参数是将要用来搜索匹配的输入字符串，后面的一个或多个参数都是正则表达式，它们将被用来在输入的第一个字符串中查找匹配。在Unix/Linux上，命令行中的正则表达式必须用引号括起来。这个程序在测试正则表达式时很有用，特别是当你想验证它们是否具备你所期待的匹配功能的时候。[^3]
```java
// strings/TestRegularExpression.java 
// Simple regular expression demonstration 
// {java TestRegularExpression 
// abcabcabcdefabc "abc+" "(abc)+" } 
import java.util.regex.*; 

public class TestRegularExpression {
    public static void main(String[] args) {     
        if(args.length < 2) {     
            System.out.println(       
              "Usage:\njava TestRegularExpression " +         
              "characterSequence regularExpression+");      
            System.exit(0);    
        }
        System.out.println("Input: \"" + args[0] + "\"");     
        for(String arg : args) {       
            System.out.println(         
              "Regular expression: \"" + arg + "\"");       
            Pattern p = Pattern.compile(arg);       
            Matcher m = p.matcher(args[0]);       
            while(m.find()) {         
                System.out.println(           
                  "Match \"" + m.group() + "\" at positions " +           
                m.start() + "-" + (m.end() - 1));       
            }     
        }  
    }
}
/* Output: 
Input: "abcabcabcdefabc" 
Regular expression: "abcabcabcdefabc" 
Match "abcabcabcdefabc" at positions 0-14 
Regular expression: "abc+" 
Match "abc" at positions 0-2 
Match "abc" at positions 3-5 
Match "abc" at positions 6-8 
Match "abc" at positions 12-14 
Regular expression: "(abc)+"
Match "abcabcabc" at positions 0-8 
Match "abc" at positions 12-14 
*/
```
还可以在控制台参数中加入`“(abc){2,}”`，看看执行结果。

`Pattern` 对象表示编译后的正则表达式。从这个例子可以看到，我们使用已编译的 `Pattern` 对象上的 `matcher()` 方法，加上一个输入字符串，从而共同构造了一个 `Matcher` 对象。同时，`Pattern` 类还提供了一个`static`方法：

```java
static boolean matches(String regex, CharSequence input)
```
该方法用以检查 `regex` 是否匹配整个 `CharSequence` 类型的 `input` 参数。编译后的 `Pattern` 对象还提供了 `split()` 方法，它从匹配了 `regex` 的地方分割输入字符串，返回分割后的子字符串 `String` 数组。

通过调用 `Pattern.matcher()` 方法，并传入一个字符串参数，我们得到了一个 `Matcher` 对象。使用 `Matcher` 上的方法，我们将能够判断各种不同类型的匹配是否成功：
```java
boolean matches() 
boolean lookingAt() 
boolean find() 
boolean find(int start)
```
其中的 `matches()` 方法用来判断整个输入字符串是否匹配正则表达式模式，而 `lookingAt()` 则用来判断该字符串（不必是整个字符串）的起始部分是否能够匹配模式。
### `find()`
`Matcher.find()` 方法可用来在 `CharSequence` 中查找多个匹配。例如：

```java
// strings/Finding.java 
import java.util.regex.*; 

public class Finding {   
    public static void main(String[] args) {     
        Matcher m = Pattern.compile("\\w+")       
          .matcher(         
            "Evening is full of the linnet's wings");     
        while(m.find())       
            System.out.print(m.group() + " ");   
        System.out.println();     
        int i = 0;     
        while(m.find(i)) {       
            System.out.print(m.group() + " ");       
            i++;     
        }   
    }
}
/* Output: 
Evening is full of the linnet s wings
Evening vening ening ning ing ng g is is s full full 
ull ll l of of f the the he e linnet linnet innet nnet 
net et t s s wings wings ings ngs gs s 
*/
```
模式 `\\w+` 将字符串划分为词。`find()` 方法像迭代器那样向前遍历输入字符串。而第二个重载的 `find()` 接收一个整型参数，该整数表示字符串中字符的位置，并以其作为搜索的起点。从结果可以看出，后一个版本的 `find()` 方法能够根据其参数的值，不断重新设定搜索的起始位置。
### 组（Groups）
组是用括号划分的正则表达式，可以根据组的编号来引用某个组。组号为 0 表示整个表达式，组号 1 表示被第一对括号括起来的组，以此类推。因此，下面这个表达式，
```java
A(B(C))D
```
中有三个组：组 0 是 `ABCD`，组 1 是 `BC`，组 2 是 `C`。

`Matcher` 对象提供了一系列方法，用以获取与组相关的信息：

+ `public int groupCount()` 返回该匹配器的模式中的分组数目，组 0 不包括在内。
+ `public String group()` 返回前一次匹配操作（例如 `find()`）的第 0 组（整个匹配）。
+ `public String group(int i)` 返回前一次匹配操作期间指定的组号，如果匹配成功，但是指定的组没有匹配输入字符串的任何部分，则将返回 `null`。
+ `public int start(int group)` 返回在前一次匹配操作中寻找到的组的起始索引。
+ `public int end(int group)` 返回在前一次匹配操作中寻找到的组的最后一个字符索引加一的值。

下面是正则表达式组的例子：
```java
// strings/Groups.java
import java.util.regex.*; 

public class Groups {   
    public static final String POEM =     
      "Twas brillig, and the slithy toves\n" +     
      "Did gyre and gimble in the wabe.\n" +     
      "All mimsy were the borogoves,\n" +     
      "And the mome raths outgrabe.\n\n" +     
      "Beware the Jabberwock, my son,\n" +     
      "The jaws that bite, the claws that catch.\n" +     
      "Beware the Jubjub bird, and shun\n" +     
      "The frumious Bandersnatch.";   
    public static void main(String[] args) {     
        Matcher m = Pattern.compile(
          "(?m)(\\S+)\\s+((\\S+)\\s+(\\S+))$")       
          .matcher(POEM);     
        while(m.find()) {       
            for(int j = 0; j <= m.groupCount(); j++)         
                System.out.print("[" + m.group(j) + "]");       
            System.out.println();     
        }   
    } 
}
/* Output: 
[the slithy toves][the][slithy toves][slithy][toves] 
[in the wabe.][in][the wabe.][the][wabe.] 
[were the borogoves,][were][the 
borogoves,][the][borogoves,] 
[mome raths outgrabe.][mome][raths 
outgrabe.][raths][outgrabe.] 
[Jabberwock, my son,][Jabberwock,][my son,][my][son,] 
[claws that catch.][claws][that catch.][that][catch.] 
[bird, and shun][bird,][and shun][and][shun] 
[The frumious Bandersnatch.][The][frumious 
Bandersnatch.][frumious][Bandersnatch.] 
*/
```
这首诗来自于 Lewis Carroll 所写的 *Through the Looking Glass* 中的 “Jabberwocky”。可以看到这个正则表达式模式有许多圆括号分组，由任意数目的非空白符（`\\S+`）及随后的任意数目的空白符（`\\s+`）所组成。目的是捕获每行的最后3个词，每行最后以 `\$` 结束。不过，在正常情况下是将 `\$` 与整个输入序列的末端相匹配。所以我们一定要显式地告知正则表达式注意输入序列中的换行符。这可以由序列开头的模式标记 `(?m)` 来完成（模式标记马上就会介绍）。
### `start()` 和 `end()`
在匹配操作成功之后，`start()` 返回先前匹配的起始位置的索引，而 `end()` 返回所匹配的最后字符的索引加一的值。匹配操作失败之后（或先于一个正在进行的匹配操作去尝试）调用 `start()` 或 `end()` 将会产生 `IllegalStateException`。下面的示例还同时展示了 `matches()` 和 `lookingAt()` 的用法 [^4]：
```java
// strings/StartEnd.java 
import java.util.regex.*; 

public class StartEnd {
    public static String input =
      "As long as there is injustice, whenever a\n" +     
      "Targathian baby cries out, wherever a distress\n" +     
      "signal sounds among the stars " +     
      "... We'll be there.\n"+     
      "This fine ship, and this fine crew ...\n" +     
      "Never give up! Never surrender!";   
    private static class Display {
        private boolean regexPrinted = false;     
        private String regex;
        Display(String regex) { this.regex = regex; }     
    
        void display(String message) {       
            if(!regexPrinted) {         
                System.out.println(regex);         
                regexPrinted = true;       
            }       
            System.out.println(message);     
        }   
    }   
  
    static void examine(String s, String regex) {     
        Display d = new Display(regex);     
        Pattern p = Pattern.compile(regex);     
        Matcher m = p.matcher(s);     
        while(m.find())       
            d.display("find() '" + m.group() +         
              "' start = "+ m.start() + " end = " + m.end());     
            if(m.lookingAt()) // No reset() necessary       
                d.display("lookingAt() start = "         
                  + m.start() + " end = " + m.end());     
        if(m.matches()) // No reset() necessary       
            d.display("matches() start = "         
              + m.start() + " end = " + m.end());   
    }
    
    public static void main(String[] args) {     
        for(String in : input.split("\n")) {       
            System.out.println("input : " + in);       
            for(String regex : new String[]{"\\w*ere\\w*",         
              "\\w*ever", "T\\w+", "Never.*?!"})         
                examine(in, regex);     
        }   
    } 
} 
/* Output: 
input : As long as there is injustice, whenever a 
\w*ere\w* 
find() 'there' start = 11 end = 16 
\w*ever 
find() 'whenever' start = 31 end = 39 
input : Targathian baby cries out, wherever a distress 
\w*ere\w* 
find() 'wherever' start = 27 end = 35 
\w*ever 
find() 'wherever' start = 27 end = 35 
T\w+ find() 'Targathian' start = 0 end = 10 
lookingAt() start = 0 end = 10 
input : signal sounds among the stars ... We'll be 
there. 
\w*ere\w* 
find() 'there' start = 43 end = 48 
input : This fine ship, and this fine crew ... 
T\w+ find() 'This' start = 0 end = 4
lookingAt() start = 0 end = 4 
input : Never give up! Never surrender! 
\w*ever 
find() 'Never' start = 0 end = 5 
find() 'Never' start = 15 end = 20 
lookingAt() start = 0 end = 5 
Never.*?! 
find() 'Never give up!' start = 0 end = 14 
find() 'Never surrender!' start = 15 end = 31 
lookingAt() start = 0 end = 14 
matches() start = 0 end = 31 
*/ 
```
注意，`find()` 可以在输入的任意位置定位正则表达式，而 `lookingAt()` 和 `matches()` 只有在正则表达式与输入的最开始处就开始匹配时才会成功。`matches()` 只有在整个输入都匹配正则表达式时才会成功，而 `lookingAt()` [^5] 只要输入的第一部分匹配就会成功。
### `Pattern` 标记
`Pattern` 类的 `compile()` 方法还有另一个版本，它接受一个标记参数，以调整匹配行为：

```java
Pattern Pattern.compile(String regex, int flag)
```
其中的 `flag` 来自以下 `Pattern` 类中的常量

| 编译标记 | 效果 |
| ---- |---- |
| `Pattern.CANON_EQ` | 当且仅当两个字符的完全规范分解相匹配时，才认为它们是匹配的。例如，如果我们指定这个标记，表达式`\u003F`就会匹配字符串`?`。默认情况下，匹配不考虑规范的等价性 |
| `Pattern.CASE_INSENSITIVE(?i)` | 默认情况下，大小写不敏感的匹配假定只有US-ASCII字符集中的字符才能进行。这个标记允许模式匹配不考虑大小写（大写或小写）。通过指定`UNICODE_CASE`标记及结合此标记。基于Unicode的大小写不敏感的匹配就可以开启了 |
| `Pattern.COMMENTS(?x)` | 在这种模式下，空格符将被忽略掉，并且以`#`开始直到行末的注释也会被忽略掉。通过嵌入的标记表达式也可以开启Unix的行模式 |
| `Pattern.DOTALL(?s)` | 在dotall模式下，表达式`.`匹配所有字符，包括行终止符。默认情况下，`.`不会匹配行终止符 |
| `Pattern.MULTILINE(?m)` | 在多行模式下，表达式`^`和`$`分别匹配一行的开始和结束。`^`还匹配输入字符串的开始，而`$`还匹配输入字符串的结尾。默认情况下，这些表达式仅匹配输入的完整字符串的开始和结束 |
| `Pattern.UNICODE_CASE(?u)` | 当指定这个标记，并且开启`CASE_INSENSITIVE`时，大小写不敏感的匹配将按照与Unicode标准相一致的方式进行。默认情况下，大小写不敏感的匹配假定只能在US-ASCII字符集中的字符才能进行 |
| `Pattern.UNIX_LINES(?d)` | 在这种模式下，在`.`、`^`和`$`的行为中，只识别行终止符`\n` |

在这些标记中，`Pattern.CASE_INSENSITIVE`、`Pattern.MULTILINE` 以及 `Pattern.COMMENTS`（对声明或文档有用）特别有用。请注意，你可以直接在正则表达式中使用其中的大多数标记，只需要将上表中括号括起来的字符插入到正则表达式中，你希望它起作用的位置即可。

你还可以通过“或”(`|`)操作符组合多个标记的功能：
```java
// strings/ReFlags.java 
import java.util.regex.*; 

public class ReFlags {   
    public static void main(String[] args) {     
        Pattern p =  Pattern.compile("^java",       
          Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);     
        Matcher m = p.matcher(       
          "java has regex\nJava has regex\n" +       
          "JAVA has pretty good regular expressions\n" +       
          "Regular expressions are in Java");     
        while(m.find())       
            System.out.println(m.group());   
    } 
}
/* Output: 
java 
Java 
JAVA 
*/
```
在这个例子中，我们创建了一个模式，它将匹配所有以“java”、“Java”和“JAVA”等开头的行，并且是在设置了多行标记的状态下，对每一行（从字符序列的第一个字符开始，至每一个行终止符）都进行匹配。注意，`group()` 方法只返回已匹配的部分。
### `split()`
`split()`方法将输入 `String` 断开成 `String` 对象数组，断开边界由正则表达式确定：

```java
String[] split(CharSequence input) 
String[] split(CharSequence input, int limit)
```
这是一个快速而方便的方法，可以按照通用边界断开输入文本：
```java
// strings/SplitDemo.java 
import java.util.regex.*; 
import java.util.*; 

public class SplitDemo {  
    public static void main(String[] args) {     
        String input =       
          "This!!unusual use!!of exclamation!!points";     
        System.out.println(Arrays.toString(       
        Pattern.compile("!!").split(input)));     
        // Only do the first three:     
        System.out.println(Arrays.toString(       
        Pattern.compile("!!").split(input, 3)));   
    }
}
/* Output: 
[This, unusual use, of exclamation, points] 
[This, unusual use, of exclamation!!points]
*/
```
第二种形式的 `split()` 方法可以限制将输入分割成字符串的数量。
### 替换操作
正则表达式在进行文本替换时特别方便，它提供了许多方法：
+ `replaceFirst(String replacement)` 以参数字符串 `replacement` 替换掉第一个匹配成功的部分。
+ `replaceAll(String replacement)` 以参数字符串 `replacement` 替换所有匹配成功的部分。
+ `appendReplacement(StringBuffer sbuf, String replacement)` 执行渐进式的替换，而不是像 `replaceFirst()` 和 `replaceAll()` 那样只替换第一个匹配或全部匹配。这是一个非常重要的方法。它允许你调用其他方法来生成或处理 `replacement`（`replaceFirst()` 和 `replaceAll()` 则只能使用一个固定的字符串），使你能够以编程的方式将目标分割成组，从而具备更强大的替换功能。
+ `appendTail(StringBuffer sbuf)` 在执行了一次或多次 `appendReplacement()` 之后，调用此方法可以将输入字符串余下的部分复制到 `sbuf` 中。

下面的程序演示了如何使用这些替换方法。开头部分注释掉的文本，就是正则表达式要处理的输入字符串：
```java
// strings/TheReplacements.java 
import java.util.regex.*; 
import java.nio.file.*; 
import java.util.stream.*;

/*! Here's a block of text to use as input to 
    the regular expression matcher. Note that we 
    first extract the block of text by looking for 
    the special delimiters, then process the     
    extracted block. !*/

public class TheReplacements {   
    public static void main(String[] args) throws Exception {     
        String s = Files.lines(       
          Paths.get("TheReplacements.java"))       
          .collect(Collectors.joining("\n"));     
        // Match specially commented block of text above:     
        Matcher mInput = Pattern.compile(       
          "/\\*!(.*)!\\*/", Pattern.DOTALL).matcher(s);     
        if(mInput.find())       
            s = mInput.group(1); // Captured by parentheses     
        // Replace two or more spaces with a single space:     
        s = s.replaceAll(" {2,}", " ");     
        // Replace 1+ spaces at the beginning of each     
        // line with no spaces. Must enable MULTILINE mode:     
        s = s.replaceAll("(?m)^ +", "");     
        System.out.println(s);     
        s = s.replaceFirst("[aeiou]", "(VOWEL1)");     
        StringBuffer sbuf = new StringBuffer();     
        Pattern p = Pattern.compile("[aeiou]");     
        Matcher m = p.matcher(s);     
        // Process the find information as you     
        // perform the replacements:     
        while(m.find())      
            m.appendReplacement(sbuf, m.group().toUpperCase());     
        // Put in the remainder of the text:     
        m.appendTail(sbuf);     
        System.out.println(sbuf);
    } 
}
/* Output: 
Here's a block of text to use as input to 
the regular expression matcher. Note that we 
first extract the block of text by looking for 
the special delimiters, then process the 
extracted block. 
H(VOWEL1)rE's A blOck Of tExt tO UsE As InpUt tO 
thE rEgUlAr ExprEssIOn mAtchEr. NOtE thAt wE 
fIrst ExtrAct thE blOck Of tExt by lOOkIng fOr 
thE spEcIAl dElImItErs, thEn prOcEss thE 
ExtrActEd blOck. 
*/
```
此处使用上一章介绍过的 [`Files`](./17-Files.md) 类打开并读入文件。`Files.lines()` 返回一个 `Stream` 对象，包含读入的所有行，`Collectors.joining()` 在每一行的结尾追加参数字符序列，最终拼接成一个 `String` 对象。

`mInput` 匹配 `/*!` 和 `！*/` 之间的所有文字（注意分组的括号）。接下来，将存在两个或两个以上空格的地方，缩减为一个空格，并且删除每行开头部分的所有空格（为了使每一行都达到这个效果，而不仅仅是删除文本开头部分的空格，这里特意开启了多行模式）。这两个替换操作所使用的的 `replaceAll()` 是 `String` 对象自带的方法，在这里，使用此方法更方便。注意，因为这两个替换操作都只使用了一次 `replaceAll()`，所以，与其编译为 `Pattern`，不如直接使用 `String` 的 `replaceAll()` 方法，而且开销也更小些。

`replaceFirst()` 只对找到的第一个匹配进行替换。此外，`replaceFirst()` 和 `replaceAll()` 方法用来替换的只是普通字符串，所以，如果想对这些替换字符串进行某些特殊处理，这两个方法时无法胜任的。如果你想要那么做，就应该使用 `appendReplacement()` 方法。该方法允许你在执行替换的过程中，操作用来替换的字符串。在这个例子中，先构造了 `sbuf` 用来保存最终结果，然后用 `group()` 选择一个组，并对其进行处理，将正则表达式找到的元音字母替换成大些字母。一般情况下，你应该遍历执行所有的替换操作，然后再调用 `appendTail()` 方法，但是，如果你想模拟 `replaceFirst()`（或替换n次）的行为，那就只需要执行一次替换，然后调用 `appendTail()` 方法，将剩余未处理的部分存入 `sbuf` 即可。

同时，`appendReplacement()` 方法还允许你通过 `\$g` 直接找到匹配的某个组，这里的 `g` 就是组号。然而，它只能应付一些简单的处理，无法实现类似前面这个例子中的功能。
### `reset()`
通过 `reset()` 方法，可以将现有的 `Matcher` 对象应用于一个新的字符序列：
```java
// strings/Resetting.java 
import java.util.regex.*; 

public class Resetting {   
    public static void main(String[] args) throws Exception {     
        Matcher m = Pattern.compile("[frb][aiu][gx]")       
          .matcher("fix the rug with bags");     
        while(m.find())       
            System.out.print(m.group() + " ");     
        System.out.println();     
        m.reset("fix the rig with rags");     
        while(m.find())       
            System.out.print(m.group() + " ");   
    } 
} 
/* Output: 
fix rug bag 
fix rig rag 
*/
```
使用不带参数的 `reset()` 方法，可以将 `Matcher` 对象重新设置到当前字符序列的起始位置。
### 正则表达式与 Java I/O
到目前为止，我们看到的例子都是将正则表达式用于静态的字符串。下面的例子将向你演示，如何应用正则表达式在一个文件中进行搜索匹配操作。`JGrep.java` 的灵感源自于 Unix 上的 *grep*。它有两个参数：文件名以及要匹配的正则表达式。输出的是每行有匹配的部分以及匹配部分在行中的位置。
```java
// strings/JGrep.java 
// A very simple version of the "grep" program 
// {java JGrep 
// WhitherStringBuilder.java 'return|for|String'} 
import java.util.regex.*; 
import java.nio.file.*; 
import java.util.stream.*;

public class JGrep {  
    public static void main(String[] args) throws Exception {    
        if(args.length < 2) {      
            System.out.println(        
              "Usage: java JGrep file regex");      
            System.exit(0);   
        }    
        Pattern p = Pattern.compile(args[1]);    
        // Iterate through the lines of the input file:    
        int index = 0;    
        Matcher m = p.matcher("");    
        for(String line: Files.readAllLines(Paths.get(args[0]))) {      
            m.reset(line);      
            while(m.find())        
                System.out.println(index++ + ": " +          
                  m.group() + ": " + m.start());    
        }  
    } 
} 
/* Output: 
0: for: 4 
1: for: 4 
*/
```
`Files.readAllLines()` 返回一个 `List<String>` 对象，这意味着可以用 *for-in* 进行遍历。虽然可以在 `for` 循环内部创建一个新的 `Matcher` 对象，但是，在循环体外创建一个空的 `Matcher` 对象，然后用 `reset()` 方法每次为 `Matcher` 加载一行输入，这种处理会有一定的性能优化。最后用 `find()` 搜索结果。

这里读入的测试参数是 `JGrep.java` 文件，然后搜索以 `[Ssct]` 开头的单词。

如果想要更深入地学习正则表达式，你可以阅读 Jeffrey E. F. Friedl 的《精通正则表达式（第2版）》。网络上也有很多正则表达式的介绍，你还可以从 Perl 和 Python 等其他语言的文档中找到有用的信息。


<!-- Scanning Input -->
## 扫描输入
到目前为止，从文件或标准输入读取数据还是一件相当痛苦的事情。一般的解决办法就是读入一行文本，对其进行分词，然后使用 `Integer`、`Double` 等类的各种解析方法来解析数据：
```java
// strings/SimpleRead.java 
import java.io.*;

public class SimpleRead {  
    public static BufferedReader input =    
      new BufferedReader(new StringReader(    
        "Sir Robin of Camelot\n22 1.61803"));  
    public static void main(String[] args) {    
        try {      
            System.out.println("What is your name?");      
            String name = input.readLine();      
            System.out.println(name);      
            System.out.println("How old are you? " +        
              "What is your favorite double?");      
            System.out.println("(input: <age> <double>)");      
            String numbers = input.readLine();      
            System.out.println(numbers);      
            String[] numArray = numbers.split(" ");      
            int age = Integer.parseInt(numArray[0]);      
            double favorite = Double.parseDouble(numArray[1]);      
            System.out.format("Hi %s.%n", name);      
            System.out.format("In 5 years you will be %d.%n", age + 5);      
            System.out.format("My favorite double is %f.", favorite / 2);    
        } catch(IOException e) {      
            System.err.println("I/O exception");    
        }  
    } 
}
/* Output: 
What is your name? 
Sir Robin of Camelot 
How old are you? What is your favorite double? 
(input: <age> <double>) 
22 1.61803
Hi Sir Robin of Camelot. 
In 5 years you will be 27. 
My favorite double is 0.809015. 
*/
```
`input` 字段使用的类来自 `java.io`，[附录:流式 I/O](./Appendix-IO-Streams.md) 详细介绍了相关内容。`StringReader` 将 `String` 转化为可读的流对象，然后用这个对象来构造 `BufferedReader` 对象，因为我们要使用 `BufferedReader` 的 `readLine()` 方法。最终，我们可以使用 `input` 对象一次读取一行文本，就像从控制台读入标准输入一样。

`readLine()` 方法将一行输入转为 `String` 对象。如果每一行数据正好对应一个输入值，那这个方法也还可行。但是，如果两个输入值在同一行中，事情就不好办了，我们必须分解这个行，才能分别解析所需的输入值。在这个例子中，分解的操作发生在创建 `numArray`时。

终于，Java SE5 新增了 `Scanner` 类，它可以大大减轻扫描输入的工作负担：
```java
// strings/BetterRead.java 
import java.util.*; 

public class BetterRead {
  public static void main(String[] args) {
    Scanner stdin = new Scanner(SimpleRead.input);
    System.out.println("What is your name?");
    String name = stdin.nextLine();
    System.out.println(name);
    System.out.println(
      "How old are you? What is your favorite double?");
    System.out.println("(input: <age> <double>)");
    int age = stdin.nextInt();
    double favorite = stdin.nextDouble();
    System.out.println(age);
    System.out.println(favorite);
    System.out.format("Hi %s.%n", name);
    System.out.format("In 5 years you will be %d.%n",
      age + 5);
    System.out.format("My favorite double is %f.",
      favorite / 2);
  }
}
/* Output: 
What is your name? 
Sir Robin of Camelot 
How old are you? What is your favorite double? 
(input: <age> <double>) 
22 
1.61803 
Hi Sir Robin of Camelot. 
In 5 years you will be 27. 
My favorite double is 0.809015. 
*/
```
`Scanner` 的构造器可以接收任意类型的输入对象，包括 `File`、`InputStream`、`String` 或者像此例中的`Readable` 实现类。`Readable` 是 Java SE5 中新加入的一个接口，表示“具有 `read()` 方法的某种东西”。上一个例子中的 `BufferedReader` 也归于这一类。

有了 `Scanner`，所有的输入、分词、以及解析的操作都隐藏在不同类型的 `next` 方法中。普通的 `next()` 方法返回下一个 `String`。所有的基本类型（除 `char` 之外）都有对应的 `next` 方法，包括 `BigDecimal` 和 `BigInteger`。所有的 next 方法，只有在找到一个完整的分词之后才会返回。`Scanner` 还有相应的 `hasNext` 方法，用以判断下一个输入分词是否是所需的类型，如果是则返回 `true`。

在 `BetterRead.java` 中没有用 `try` 区块捕获`IOException`。因为，`Scanner` 有一个假设，在输入结束时会抛出 `IOException`，所以 `Scanner` 会把 `IOException` 吞掉。不过，通过 `ioException()` 方法，你可以找到最近发生的异常，因此，你可以在必要时检查它。
### `Scanner` 分隔符
默认情况下，`Scanner` 根据空白字符对输入进行分词，但是你可以用正则表达式指定自己所需的分隔符：
```java
// strings/ScannerDelimiter.java 
import java.util.*;
public class ScannerDelimiter {  
    public static void main(String[] args) {    
        Scanner scanner = new Scanner("12, 42, 78, 99, 42");    
        scanner.useDelimiter("\\s*,\\s*");    
        while(scanner.hasNextInt())    
            System.out.println(scanner.nextInt());  
    } 
}
/* Output: 
12 
42 
78 
99 
42 
*/
```
这个例子使用逗号（包括逗号前后任意的空白字符）作为分隔符，同样的技术也可以用来读取逗号分隔的文件。我们可以用 `useDelimiter()` 来设置分隔符，同时，还有一个 `delimiter()` 方法，用来返回当前正在作为分隔符使用的 `Pattern` 对象。
### 用正则表达式扫描
除了能够扫描基本类型之外，你还可以使用自定义的正则表达式进行扫描，这在扫描复杂数据时非常有用。下面的例子将扫描一个防火墙日志文件中的威胁数据：
```java
// strings/ThreatAnalyzer.java 
import java.util.regex.*; 
import java.util.*;
public class ThreatAnalyzer { 
    static String threatData =    
      "58.27.82.161@08/10/2015\n" +   
      "204.45.234.40@08/11/2015\n" +    
      "58.27.82.161@08/11/2015\n" +    
      "58.27.82.161@08/12/2015\n" +    
      "58.27.82.161@08/12/2015\n" +
      "[Next log section with different data format]";  
    public static void main(String[] args) { 
        Scanner scanner = new Scanner(threatData);    
        String pattern = "(\\d+[.]\\d+[.]\\d+[.]\\d+)@" +      
            "(\\d{2}/\\d{2}/\\d{4})";    
        while(scanner.hasNext(pattern)) {      
            scanner.next(pattern);      
            MatchResult match = scanner.match();      
            String ip = match.group(1);      
            String date = match.group(2);      
            System.out.format(        
                "Threat on %s from %s%n", date,ip);    
        }  
    } 
} 
/* Output: 
Threat on 08/10/2015 from 58.27.82.161 
Threat on 08/11/2015 from 204.45.234.40 
Threat on 08/11/2015 from 58.27.82.161 
Threat on 08/12/2015 from 58.27.82.161 
Threat on 08/12/2015 from 58.27.82.161 
*/  
```
当 `next()` 方法配合指定的正则表达式使用时，将找到下一个匹配该模式的输入部分，调用 `match()` 方法就可以获得匹配的结果。如上所示，它的工作方式与之前看到的正则表达式匹配相似。

在配合正则表达式使用扫描时，有一点需要注意：它仅仅针对下一个输入分词进行匹配，如果你的正则表达式中含有分隔符，那永远不可能匹配成功。

<!-- StringTokenizer -->

## StringTokenizer类
在 Java 引入正则表达式（J2SE1.4）和 `Scanner` 类（Java SE5）之前，分割字符串的唯一方法是使用 `StringTokenizer` 来分词。不过，现在有了正则表达式和 `Scanner`，我们可以使用更加简单、更加简洁的方式来完成同样的工作了。下面的例子中，我们将 `StringTokenizer` 与另外两种技术简单地做了一个比较：
```java
// strings/ReplacingStringTokenizer.java 
import java.util.*; 

public class ReplacingStringTokenizer {   
    public static void main(String[] args) {     
        String input =       
          "But I'm not dead yet! I feel happy!";     
        StringTokenizer stoke = new StringTokenizer(input);     
        while(stoke.hasMoreElements())       
            System.out.print(stoke.nextToken() + " ");     
        System.out.println(); 
        System.out.println(Arrays.toString(input.split(" ")));     
        Scanner scanner = new Scanner(input);     
        while(scanner.hasNext())       
            System.out.print(scanner.next() + " ");   
    }
} 
/* Output: 
But I'm not dead yet! I feel happy! 
[But, I'm, not, dead, yet!, I, feel, happy!] 
But I'm not dead yet! I feel happy! 
*/
```
使用正则表达式或 `Scanner` 对象，我们能够以更加复杂的模式来分割一个字符串，而这对于 `StringTokenizer` 来说就很困难了。基本上，我们可以放心地说，`StringTokenizer` 已经可以废弃不用了。


<!-- Summary -->
## 本章小结
过去，Java 对于字符串操作的技术相当不完善。不过随着近几个版本的升级，我们可以看到，Java 已经从其他语言中吸取了许多成熟的经验。到目前为止，它对字符串操作的支持已经很完善了。不过，有时你还要在细节上注意效率问题，例如恰当地使用 `StringBuilder` 等。


[^1]: C++允许编程人员任意重载操作符。这通常是很复杂的过程（参见Prentice Hall于2000年编写的《Thinking in C++（第2版）》第10章），因此Java设计者认为这是很糟糕的功能，不应该纳入到Java中。起始重载操作符并没有糟糕到只能自己去重载的地步，但具有讽刺意味的是，与C++相比，在Java中使用操作符重载要容易得多。这一点可以在Python(参见[www.Python.org](http://www.python.org))和C#中看到，它们都有垃圾回收机制，操作符重载也简单易懂。


[^2]: Java并非在一开始就支持正则表达式，因此这个令人费解的语法是硬塞进来的。


[^3]: 网上还有很多实用并且成熟的正则表达式工具。


[^4]: input来自于[Galaxy Quest](https://en.wikipedia.org/wiki/Galaxy_Quest)中Taggart司令的一篇演讲。


[^5]: 我不知道他们是如何想出这个方法名的，或者它到底指的什么。这只是代码审查很重要的原因之一。

<!-- 分页 -->

<div style="page-break-after: always;"></div>
