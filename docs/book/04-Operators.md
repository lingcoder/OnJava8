[TOC]

<!-- Operators -->
# 第四章 运算符


>运算符操纵数据。

Java 是从 C++ 的基础上做了一些改进和简化发展而成的。对于 C/C++ 程序员来说，Java 的运算符并不陌生。如果你已了解 C 或 C++，大可以跳过本章和下一章，直接阅读 Java 与 C/C++ 不同的地方。

如果理解这两章的内容对你来说还有点困难，那么我推荐你先了解下 *Thinking in C* 再继续后面的学习。 这本书现在可以在 [www.OnJava8.com](http://www.OnJava8.com]) 上免费下载。它的内容包含音频讲座、幻灯片、练习和解决方案，专门用于帮助你快速掌握学习 Java 所需的基础知识。


<!-- Using-Java-Operators -->
## 使用说明


运算符接受一个或多个参数并生成新值。这个参数与普通方法调用的形式是不同的，但效果是相同的。加法 `+`，减法 `-`，乘法 `*`，除法 `/` 以及赋值 `=` 在任何编程语言中的工作方式都是类似的。所有运算符都能根据自己的运算对象生成一个值。除此以外，一个运算符可改变运算对象的值，这叫作“副作用”（**Side Effect**）。运算符最常见的用途就是修改自己的运算对象，从而产生副作用。但要注意生成的值亦可由没有副作用的运算符生成。

几乎所有运算符都只能操作基本类型（*Primitives*）。唯一的例外是 `=`、`==` 和 `!=`，它们能操作所有对象（这也是令人混淆的一个地方）。除此以外，**String** 类支持 `+` 和 `+=`。


<!-- Precedence -->
## 优先级


运算符的优先级决定了存在多个运算符时一个表达式各部分的计算顺序。Java 对计算顺序作出了特别的规定。其中，最简单的规则就是乘法和除法在加法和减法之前完成。程序员经常都会忘记其他优先级规则，所以应该用括号明确规定计算顺序。代码示例:

```JAVA
// operators/Precedence.java
public class Precedence {
    
    public static void main(String[] args) {
        int x = 1, y = 2, z = 3;
        int a = x + y - 2/2 + z; // [1]
        int b = x + (y - 2)/(2 + z); // [2]
        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }
}
```

 输出结果:

```
    a = 5
    b = 1
```

这些语句看起来大致相同，但从输出中我们可以看出它们具有非常不同的含义，具体取决于括号的使用。

我们注意到，在 `System.out.println()` 语句中使用了 `+` 运算符。 但是在这里 `+` 代表的意思是字符串连接符。编译器会将 `+` 连接的非字符串尝试转换为字符串。上例中的输出结果说明了 a 和 b 都已经被转化成了字符串。


<!-- Assignment -->
## 赋值


运算符的赋值是由符号 `=` 完成的。它代表着获取 `=` 右边的值并赋给左边的变量。右边可以是任何常量、变量或者是可产生一个返回值的表达式。但左边必须是一个明确的、已命名的变量。也就是说，必须要有一个物理的空间来存放右边的值。举个例子来说，可将一个常数赋给一个变量（ A = 4 ），但不可将任何东西赋给一个常数（比如不能 4 = A）。

基本类型的赋值都是直接的，而不像对象，赋予的只是其内存的引用。举个例子，a = b ，如果 b 是基本类型，那么 赋值操作会将 b 的值复制一根给变量 a， 此后若 a 的值发生改变是不会影响到 b 的。作为一名程序员，着应该成为我们的常识。

如果是为对象赋值，那么结果就不一样了。对一个对象进行操作时，我们实际上操作的是它的引用。所以我们将右边的对象赋予给左边时，赋予的只是该对象的引用。此时，两者指向的堆中的对象还是同一个。代码示例：

```JAVA 
// operators/Assignment.java
// Assignment with objects is a bit tricky
class Tank {
    int level;
}

public class Assignment {

    public static void main(String[] args) {
        Tank t1 = new Tank();
        Tank t2 = new Tank();
        t1.level = 9;
        t2.level = 47;
        System.out.println("1: t1.level: " + t1.level +
            ", t2.level: " + t2.level);
        t1 = t2;
        System.out.println("2: t1.level: " + t1.level +
            ", t2.level: " + t2.level);
        t1.level = 27;
        System.out.println("3: t1.level: " + t1.level +
            ", t2.level: " + t2.level);
    }
}
```

输出结果：

```
1: t1.level: 9, t2.level: 47
2: t1.level: 47, t2.level: 47
3: t1.level: 27, t2.level: 27
```

这是个简单的 `Tank` 类，通过 main 方法 `new` 出了 2 个实例对象。 两个对象的 `level` 属性分别被赋予了不同的值。 然后，t2 的值被赋予给 t1。在许多编程语言里，预期的结果是 t1 和 t2 的值会一直相对独立。但是，在 Java 中，由于赋予的只是对象的引用，改变 t1 也就改变了 t2。 这是因为 t1 和 t2 此时指向的是堆中同一个对象。（t1 原始对象的引用在 t2 赋值给其时被丢失，它就将会在垃圾回收时被清理）。

这种现象通常称为别名（*aliasing*），这是 Java 处理对象的一种基本方式。但是假若你不想这里出现这样的混淆的话，你可以这么做。代码示例：

```JAVA
t1.level = t2.level;
```

较之前的做法，这样做保留了两个单独的对象，而不是丢弃一个并将 t1 和 t2 绑定到同一个对象。但是这样的操作有点违背 JAVA 的设计原则。对象的赋值是个需要重视的环节，否则你可能收获意外的“惊喜”。

 <!-- Aliasing During Method Calls -->
### 方法调用中的别名现象

当我们把对象传递给方法时，会发生别名现象。

```JAVA
// operators/PassObject.java
// 正在传递的对象可能不是你之前使用的
class Letter {
    char c;
 }

public class PassObject {
    static void f(Letter y) {
    y.c = 'z';
 }

    public static void main(String[] args) {
        Letter x = new Letter();
         x.c = 'a';
        System.out.println("1: x.c: " + x.c);
        f(x);
        System.out.println("2: x.c: " + x.c);
     }
}
```

输出结果：

```
1: x.c: a
2: x.c: z
```

在许多编程语言中，方法 **f()** 似乎在内部复制其参数 **Letter y**。但是一旦传递了一个引用，那么实际上 `y.c ='z';` 是在方法 **f()** 之外改变对象。别名现象以及其解决方案是个复杂的问题，在附录中有包含：[对象传递和返回](./Appendix-Passing-and-Returning-Objects.md)。意识到这一点，我们可以警惕类似的陷阱。


<!-- Mathematical Operators -->
## 算术运算符

Java 的基本算术运算符与其他大多编程语言是相同的。其中包括加号 `+`、减号 `-`、除号 `/`、乘号 `*` 以及模数`%`（从整数除法中获得余数）。整数除法会直接砍掉小数，而不是进位。

Java 也用一种简写形式同时进行运算和赋值操作，由运算符后跟等号表示，并且与语言中的所有运算符一致（只要有意义）。为了将 4 的值赋予给变量 x 同时将结果赋予给 x ， 可用 x += 4 来表示。下面带来代码更多代码示例：


```JAVA
// operators/MathOps.java
// The mathematical operators
import java.util.*;

public class MathOps {
    public static void main(String[] args) {
        // Create a seeded random number generator:
        Random rand = new Random(47);
        int i, j, k;
        // Choose value from 1 to 100:
        j = rand.nextInt(100) + 1;
        System.out.println("j : " + j);
        k = rand.nextInt(100) + 1;
        System.out.println("k : " + k);
        i = j + k;
        System.out.println("j + k : " + i);
        i = j - k;
        System.out.println("j - k : " + i);
        i = k / j;
        System.out.println("k / j : " + i);
        i = k * j;
        System.out.println("k * j : " + i);
        i = k % j;
        System.out.println("k % j : " + i);
        j %= k;
        System.out.println("j %= k : " + j);
        // 浮点运算测试
        float u, v, w; // Applies to doubles, too
        v = rand.nextFloat();
        System.out.println("v : " + v);
        w = rand.nextFloat();
        System.out.println("w : " + w);
        u = v + w;
        System.out.println("v + w : " + u);
        u = v - w;
        System.out.println("v - w : " + u);
        u = v * w;
        System.out.println("v * w : " + u);
        u = v / w;
        System.out.println("v / w : " + u);
        // 下面的操作同样适用于 char, 
        // byte, short, int, long, and double:
        u += v;
        System.out.println("u += v : " + u);
        u -= v;
        System.out.println("u -= v : " + u);
        u *= v;
        System.out.println("u *= v : " + u);
        u /= v;
        System.out.println("u /= v : " + u);    
    }
}

```

输出结果：

```
j : 59
k : 56
j + k : 115
j - k : 3
k / j : 0
k * j : 3304
k % j : 56
j %= k : 3
v : 0.5309454
w : 0.0534122
v + w : 0.5843576
v - w : 0.47753322
v * w : 0.028358962
v / w : 9.940527
u += v : 10.471473
u -= v : 9.940527
u *= v : 5.2778773
u /= v : 9.940527
```

为了生成随机数字，程序首先创建一个 **Random** 对象。不带参数的 **Random** 对象会利用当前的时间用作随机数生成器的“种子”（*seed*），从而为程序的每次执行生成不同的输出。在本书的示例中，重要的是每个示例末尾的输出尽可能一致，以便可以使用外部工具进行验证。所以我们通过在创建 **Random** 对象时提供种子（随机数生成器的初始化值，其始终为特定种子值产生相同的序列），让程序每次执行都生成相同的随机数，如此以来输出结果就是可验证的 [^1]。 若需要生成随机值，可删除代码示例中的种子参数。该对象通过调用方法 **nextInt()** 和 **nextFloat()**（还可以调用 **nextLong()** 或 **nextDouble()**），使用 **Random** 对象生成许多不同类型的随机数。**nextInt()** 的参数设置生成的数字的上限，下限为零，为了避免零除的可能性，结果偏移1。


<!-- Unary Minus and Plus Operators -->
### 一元加减运算符

一元加 `+` 减 `-` 运算符的操作和二元是相同的。编译器可自动识别使用何种方式解析运算：

```JAVA
x = -a;
```

上例的代码表意清晰，编译器可正确识别。下面再看一个示例：

```JAVA
x = a * -b;
```

虽然编译器可以正确的识别，但是程序员可能会迷惑。为了避免混淆，推荐下面的写法：

```JAVA
x = a * (-b);
```

一元减号可以得到数据的负值。一元加号的作用相反，不过它唯一能影响的就是把较小的数值类型自动转换为了 **int** 类型。


<!-- Auto-Increment-and-Decrement -->
## 递增和递减


和 C 语言类似，Java 提供了许多快捷运算方式。快捷运算可使代码可读性,可写性都更强。其中包括递增 `++` 和递减 `--`,意为“增加或减少一个单位”。举个例子来说，假设 a 是一个 **int** 类型的值，则表达式 `++a` 就等价于 `a = a + 1`。 递增和递减运算符不仅可以修改变量，还可以生成变量的值。

每种类型的运算符，都有两个版本可供选用；通常将其称为“前缀版”和“后缀版”。“前递增”表示 `++` 运算符位于变量或表达式的前面；而“后递增”表示 `++` 运算符位于变量或表达式的后面。类似地，“前递减”意味着 `--` 运算符位于变量或表达式的前面；而“后递减”意味着 `--` 运算符位于变量或表达式的后面。对于前递增和前递减（如 `++a` 或 `--a`），会先执行递增/减运算，再返回值。而对于后递增和后递减（如 `a++` 或 `a--`），会先返回值，再执行递增/减运算。代码示例：

```JAVA
// operators/AutoInc.java
// 演示 ++ 和 -- 运算符
public class AutoInc {
    public static void main(String[] args) {
        int i = 1;
        System.out.println("i: " + i);
        System.out.println("++i: " + ++i); // 前递增
        System.out.println("i++: " + i++); // 后递增
        System.out.println("i: " + i);
        System.out.println("--i: " + --i); // 前递减
        System.out.println("i--: " + i--); // 后递减
        System.out.println("i: " + i);
    }
}
```

输出结果：

```
i: 1
++i: 2
i++: 2
i: 3
--i: 2
i--: 2
i: 1
```

对于前缀形式，我们将在执行递增/减操作后获取值；使用后缀形式，我们将在执行递增/减操作之前获取值。它们是唯一具有“副作用”的运算符（除那些涉及赋值的以外） —— 它们既改变操作数又改变值。

C++ 名称来自于递增运算符，同时也代表着“比 C 更进一步”。在早期的 Java 演讲中，*Bill Joy*（ Java 创建者之一）说“**Java = C ++ --**”（C++ 减减）。这意味着 Java 是在 C++  的基础上减少了许多不必要的东西，因此语言更简单。随着进一步地学习，我们会发现 Java 的确有许多地方相对 C++ 来说更简便，但是在其他方面，难度并不会比 C++ 小多少。


<!-- Relational-Operators -->
## 关系运算符


关系运算符会通过产生一个布尔（**boolean**）结果来表示被操作的数值之间的关系。如果关系为真，则结果为 **true**，如果关系非真，则结果为 **false**。关系运算符包括小于 `<`，大于 `>`，小于或等于 `<=`，大于或等于 `>=`，等价 `==` 和不等价 `！=`。`==` 和 `!=` 可与所有基本类型搭配使用。但与其他类型的比较就不太适合了，因为布尔值只能表示 **true** 或 **false**，所以比较它们之间的“大于”或“小于”没有意义。

<!-- Testing Object Equivalence -->
### 测试对象等价

关系运算符 `==` 和 `!=` 同样适用于所有对象之间的比较运算，但产生的结果却经常混淆 Java 的初学者。下面是代码示例：

```JAVA
// operators/Equivalence.java
public class Equivalence {
    public static void main(String[] args) {
        Integer n1 = 47;
        Integer n2 = 47;
        System.out.println(n1 == n2);
        System.out.println(n1 != n2);
    }
}
```

输出结果：

```
true
false
```

上例的结果看起来是我们所期望的。但其实事情并非那么简单。下面我们来创建自己的类：

```JAVA
// operators/EqualsMethod2.java
// 默认的 equals() 方法没有比较内容
class Value {
int i;
}

public class EqualsMethod2 {
    public static void main(String[] args) {
        Value v1 = new Value();
        Value v2 = new Value();
        v1.i = v2.i = 100;
        System.out.println(v1.equals(v2));
    }
}
```

输出结果:

```
false
```

上例的结果再次令人困惑：结果是错误的。原因： **equals()** 的默认行为是比较对象的引用而非具体内容。因此，除非你在新类中重写 **equals()** 方法，否则我们将获取不到想要的结果。不幸的是，在学习 [复用](./08-Reuse.md)（**Reuse**） 章节后我们才能接触到“覆盖”（**override**），并且直到 [附录:集合主题](./Appendix-Collection-Topics.md)，才能知道定义 **equals()** 方法的正确方式,但是现在明白 **equals()** 行为方式也可能为你节省一些时间。

大多数 Java 库类通过覆写 **equals()** 方法比较对象的内容而不是其引用。

<!-- Logical-Operators -->
## 逻辑运算符

每个逻辑运算符 `&&` （**AND**）、`||`（**OR**）和 `!`（**非**）根据参数的逻辑关系生成布尔值 `true` 或 `false`。下面的代码示例使用了关系运算符和逻辑运算符：

```JAVA
// operators/Bool.java
// 关系运算符和逻辑运算符
import java.util.*;
public class Bool {
    public static void main(String[] args) {
        Random rand = new Random(47);
        int i = rand.nextInt(100);
        int j = rand.nextInt(100);
        System.out.println("i = " + i);
        System.out.println("j = " + j);
        System.out.println("i > j is " + (i > j));
        System.out.println("i < j is " + (i < j));
        System.out.println("i >= j is " + (i >= j));
        System.out.println("i <= j is " + (i <= j));
        System.out.println("i == j is " + (i == j));
        System.out.println("i != j is " + (i != j));
        // 将 int 作为布尔处理不是合法的 Java 写法
        //- System.out.println("i && j is " + (i && j));
        //- System.out.println("i || j is " + (i || j));
        //- System.out.println("!i is " + !i);
        System.out.println("(i < 10) && (j < 10) is "
        + ((i < 10) && (j < 10)) );
        System.out.println("(i < 10) || (j < 10) is "
        + ((i < 10) || (j < 10)) );
    }
}
```

输出结果：

```
i = 58
j = 55
i > j is true
i < j is false
i >= j is true
i <= j is false
i == j is false
i != j is true
(i < 10) && (j < 10) is false
(i < 10) || (j < 10) is false
```

在 JAVA 逻辑运算中，我们不能像 C/C++ 那样使用非布尔值， 而仅能使用 **AND**、**OR**、**NOT**。
下面是一次错误的尝试： ~~a || -~~。 但是，后续表达式使用关系比较生成布尔值，然后对结果使用逻辑运算结果。请注意，如果在预期为 **String** 类型的位置使用 **boolean**类型的值，则结果会自动转为适当的文本格式。

我们可以将前一个程序中 **int** 的定义替换为除 **boolean** 之外的任何其他基本数据类型。
但请注意，**float** 类型的数值比较非常严格。只要最小位部分的数字不同则两个数值之间的比较仍然是“非等”的；只要数字最小位是大于 0 的，那么它就不等于 0。


<!-- Short-Circuiting -->
### 短路

逻辑运算符支持一种称为“短路”（*short-circuiting*）的现象。整个表达式会在运算到可以明确结果时就停止并返回结果，这意味着该逻辑表达式的后半部分不会被执行到。代码示例：

```JAVA
// operators / ShortCircuit.java 
// 逻辑运算符的短路行为
public class ShortCircuit {

    static boolean test1(int val) {
        System.out.println("test1(" + val + ")");
        System.out.println("result: " + (val < 1));
        return val < 1;
    }

    static boolean test2(int val) {
        System.out.println("test2(" + val + ")");
        System.out.println("result: " + (val < 2));
        return val < 2;
    }

    static boolean test3(int val) {
        System.out.println("test3(" + val + ")");
        System.out.println("result: " + (val < 3));
        return val < 3;
    }

    public static void main(String[] args) {
        boolean b = test1(0) && test2(2) && test3(2);
        System.out.println("expression is " + b);
    }
}
```

输出结果：

```
test1(0)
result: true
test2(2)
result: false
expression is false
```

每个测试都对参数执行比较并返回 `true` 或 `false`。同时控制台也会在方法执行时打印他们的执行状态。 下面的表达式：

```JAVA
test1（0）&& test2（2）&& test3（2）
```

可能你的预期是程序会执行 3 个 **test** 方法并返回。我们来分析一下：第一个方法的结果返回 `true`,因此表达式会继续走下去。紧接着，第二个方法的返回结果是 `false`。这就代表这整个表达式的结果肯定为 `false`，所以就没有必要再判断剩下的表达式部分了。

所以，运用“短路”可以节省部分不必要的运算，从而提高程序潜在的性能。


<!-- Literals -->
## 字面值常量


通常，当我们向程序中插入一个字面值常量（**Literal**）时，编译器会确切地识别它的类型。当类型不明确时，必须辅以字面值常量关联来帮助编译器识别。代码示例：

```JAVA
// operators/Literals.java
public class Literals {
    public static void main(String[] args) {
        int i1 = 0x2f; // 16进制 (小写)
        System.out.println(
        "i1: " + Integer.toBinaryString(i1));
        int i2 = 0X2F; // 16进制 (大写)
        System.out.println(
        "i2: " + Integer.toBinaryString(i2));
        int i3 = 0177; // 8进制 (前导0)
        System.out.println(
        "i3: " + Integer.toBinaryString(i3));
        char c = 0xffff; // 最大 char 型16进制值
        System.out.println(
        "c: " + Integer.toBinaryString(c));
        byte b = 0x7f; // 最大 byte 型16进制值  10101111;
        System.out.println(
        "b: " + Integer.toBinaryString(b));
        short s = 0x7fff; // 最大 short 型16进制值
        System.out.println(
        "s: " + Integer.toBinaryString(s));
        long n1 = 200L; // long 型后缀
        long n2 = 200l; // long 型后缀 (容易与数值1混淆)
        long n3 = 200;
    
        // Java 7 二进制字面值常量:
        byte blb = (byte)0b00110101;
        System.out.println(
        "blb: " + Integer.toBinaryString(blb));
        short bls = (short)0B0010111110101111;
        System.out.println(
        "bls: " + Integer.toBinaryString(bls));
        int bli = 0b00101111101011111010111110101111;
        System.out.println(
        "bli: " + Integer.toBinaryString(bli));
        long bll = 0b00101111101011111010111110101111;
        System.out.println(
        "bll: " + Long.toBinaryString(bll));
        float f1 = 1;
        float f2 = 1F; // float 型后缀
        float f3 = 1f; // float 型后缀
        double d1 = 1d; // double 型后缀
        double d2 = 1D; // double 型后缀
        // (long 型的字面值同样适用于十六进制和8进制 )
    }
}
```

输出结果:

```
i1: 101111
i2: 101111
i3: 1111111
c: 1111111111111111
b: 1111111
s: 111111111111111
blb: 110101
bls: 10111110101111
bli: 101111101011111010111110101111
bll: 101111101011111010111110101111
```

在文本值的后面添加字面值常量可以让编译器识别该文本值的类型。对于 **Long** 型数值，结尾使用大写 `L` 或小写 `l` 皆可（不推荐使用 `l`，因为容易与阿拉伯数值 1 混淆）。大写 `F` 或小写 `f` 表示 **float** 浮点数。大写 `D` 或小写 `d` 表示 **double** 双精度。


十六进制（以 16 为基数），适用于所有整型数据类型，由前导 `0x` 或 `0x` 表示，后跟 0-9 或 a-f （大写或小写）。如果我们在初始化某个类型的数值时，赋值超出其范围，那么编译器会报错（不管值的数字形式如何）。在上例的代码中，**char**、**byte** 和 **short** 的值已经是最大了。如果超过这些值，编译器将自动转型为 **int**，并且我们需要声明强制转换（强制转换将在本章后面定义）。这告诉我们已越过该类型的范围界限。

八进制（以 8 为基数）由 0~7 之间的数字和前导零 `0` 表示。

Java 7 引入了二进制的字面值常量，由前导 `0B` 或 `0B` 表示，它可以初始化所有的整数类型。

使用整型数值类型时，显示其二进制形式会很有用。在 Long 型和 Integer 型中这很容易实现，调用其静态的 **toBinaryString()** 方法即可。 但是请注意，若将较小的类型传递给 **Integer.tobinarystring()** 时，类型将自动转换为 **int**。

<!-- Underscores in Literals -->
### 下划线


Java 7 中有一个深思熟虑的补充：我们可以在数字文字中包含下划线 `_`，以使结果更清晰。这对于大数值的分组数字特别有用。代码示例：


```JAVA
// operators/Underscores.java
public class Underscores {
    public static void main(String[] args) {
        double d = 341_435_936.445_667;
        System.out.println(d);
        int bin = 0b0010_1111_1010_1111_1010_1111_1010_1111;
        System.out.println(Integer.toBinaryString(bin));
        System.out.printf("%x%n", bin); // [1]
        long hex = 0x7f_e9_b7_aa;
        System.out.printf("%x%n", hex);
    }
}
```

输出结果:

```
3.41435936445667E8
101111101011111010111110101111
2fafafaf
7fe9b7aa
```


下面是合理使用的规则：

1. 仅限单 `_`，不能多条相连。
2. 数值开头和结尾不允许出现 `_`。
3. `F`、`D` 和 `L`的前后禁止出现 `_`。
4. 二进制前导 `b` 和 十六进制 `x` 前后禁止出现 `_`。


[1] 注意 `％n`的使用。熟悉 C 风格的程序员可能习惯于看到 `\n` 来表示换行符。问题在于它给你的是一个“Unix风格”的换行符。此外，如果我们使用的是 Windows，则必须指定 `\r\n`。这种差异的包袱应该由编程语言来解决。这就是Java用 `％n` 实现的，忽略平台间差异生成适当的换行符。但当你使用 `System.out.printf()` 或 `System.out.format()` 时。对于 `System.out.println()`，我们仍然必须使用 `\n`;如果你使用 `％n`，`println()` 只会输出 `％n` 而不是换行符。


<!-- Exponential Notation -->
### 指数计数法

 指数总是采用一种我认为很不直观的记号方法:

```JAVA
// operators/Exponents.java
// "e" 表示 10 的几次幂
public class Exponents {
    public static void main(String[] args) {
        // 大写 E 和小写 e 的效果相同:
        float expFloat = 1.39e-43f;
        expFloat = 1.39E-43f;
        System.out.println(expFloat);
        double expDouble = 47e47d; // 'd' 是可选的
        double expDouble2 = 47e47; // 自动转换为 double
        System.out.println(expDouble);
    }
}
```

输出结果:

```
1.39E-43
4.7E48
```

在科学与工程学领域，“e”代表自然对数的基数，约等于 2.718 （Java 一种更精确的 **double** 值采用 **Math.E** 的形式）。它在类似 “1.39×e 的 -47 次方”这样的指数表达式中使用，意味着“1.39×2.718 的-47次方”。然而，自 FORTRAN 语言发明后，人们自然而然地觉得e 代表 “10几次幂”。这种做法显得颇为古怪，因为 FORTRAN 最初面向的是科学与工程设计领域。

理所当然，它的设计者应对这样的混淆概念持谨慎态度 [^2]。但不管怎样，这种特别的表达方法在 C，C++ 以及现在的 Java 中顽固地保留下来了。所以倘若习惯 e 作为自然对数的基数使用，那么在 Java 中看到类似“1.39e-47f”这样的表达式时，请转换你的思维，从程序设计的角度思考它；它真正的含义是“1.39×10 的-47次方”。

注意如果编译器能够正确地识别类型，就不必使用尾随字面值常量。对于下述语句：

```JAVA
long n3 = 200;
```

它并不存在含糊不清的地方，所以 200 后面的 L 大可省去。然而，对于下述语句：

```
float f4 = 1e-47f; //10 的幂数
```

编译器通常会将指数作为 **double** 类型来处理，所以假若没有这个尾随的 `f`，编辑器就会报错，提示我们应该将 **double** 型转换成 **float** 型。


<!-- Bitwise-Operators -->
## 按位运算符


<!-- Shift Operators -->
## 移位运算符


<!-- Ternary-if-else-Operator -->
## 三元运算符


<!-- String-Operator-+-and-+= -->
## 字符串运算符


<!-- Common-Pitfalls-When-Using-Operators -->
## 常见陷阱


<!-- Casting-Operators -->
## 类型转换


<!-- Java-Has-No-sizeof -->
## Java没有sizeof


<!-- A-Compendium-of-Operators -->
## 运算符总结


<!-- Summary -->
## 本章小结


<!-- 分页 -->
<div style="page-break-after: always;"></div>
