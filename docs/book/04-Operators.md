[TOC]

<!-- Operators -->
# 第四章 运算符

>运算符操纵数据。

Java 是从 C++ 的基础上做了一些改进和简化发展而成的。对于 C/C++ 程序员来说，Java 的运算符并不陌生。如果你已了解 C 或 C++，大可以跳过本章和下一章，直接阅读 Java 与 C/C++ 不同的地方。

如果理解这两章的内容对你来说还有点困难，那么我推荐你先了解下 《Thinking in C》 再继续后面的学习。 这本书现在可以在 [www.OnJava8.com](http://www.OnJava8.com]) 上免费下载。它的内容包含音频讲座、幻灯片、练习和解答，专门用于帮助你快速掌握学习 Java 所需的基础知识。

<!-- Using-Java-Operators -->
## 开始使用

运算符接受一个或多个参数并生成新值。这个参数与普通方法调用的形式不同，但效果是相同的。加法 `+`、减法 `-`、乘法 `*`、除法 `/` 以及赋值 `=` 在任何编程语言中的工作方式都是类似的。所有运算符都能根据自己的运算对象生成一个值。除此以外，一些运算符可改变运算对象的值，这叫作“副作用”（**Side Effect**）。运算符最常见的用途就是修改自己的运算对象，从而产生副作用。但要注意生成的值亦可由没有副作用的运算符生成。

几乎所有运算符都只能操作基本类型（Primitives）。唯一的例外是 `=`、`==` 和 `!=`，它们能操作所有对象（这也是令人混淆的一个地方）。除此以外，**String** 类支持 `+` 和 `+=`。

<!-- Precedence -->
## 优先级

运算符的优先级决定了存在多个运算符时一个表达式各部分的运算顺序。Java 对运算顺序作出了特别的规定。其中，最简单的规则就是乘法和除法在加法和减法之前完成。程序员经常都会忘记其他优先级规则，所以应该用括号明确规定运算顺序。代码示例:

```java
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

运算符的赋值是由符号 `=` 完成的。它代表着获取 `=` 右边的值并赋给左边的变量。右边可以是任何常量、变量或者可产生一个返回值的表达式。但左边必须是一个明确的、已命名的变量。也就是说，必须要有一个物理的空间来存放右边的值。举个例子来说，可将一个常数赋给一个变量（A = 4），但不可将任何东西赋给一个常数（比如不能 4 = A）。

基本类型的赋值都是直接的，而不像对象，赋予的只是其内存的引用。举个例子，a = b ，如果 b 是基本类型，那么赋值操作会将 b 的值复制一份给变量 a， 此后若 a 的值发生改变是不会影响到 b 的。作为一名程序员，这应该成为我们的常识。

如果是为对象赋值，那么结果就不一样了。对一个对象进行操作时，我们实际上操作的是它的引用。所以我们将右边的对象赋予给左边时，赋予的只是该对象的引用。此时，两者指向的堆中的对象还是同一个。代码示例：

```java 
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

这是一个简单的 `Tank` 类，在 `main()` 方法创建了两个实例对象。 两个对象的 `level` 属性分别被赋予不同的值。 然后，t2 的值被赋予给 t1。在许多编程语言里，预期的结果是 t1 和 t2 的值会一直相对独立。但是，在 Java 中，由于赋予的只是对象的引用，改变 t1 也就改变了 t2。 这是因为 t1 和 t2 此时指向的是堆中同一个对象。（t1 原始对象的引用在 t2 赋值给其时丢失，它引用的对象会在垃圾回收时被清理）。

这种现象通常称为别名（aliasing），这是 Java 处理对象的一种基本方式。但是假若你不想出现这里的别名引起混淆的话，你可以这么做。代码示例：

```java
t1.level = t2.level;
```

较之前的做法，这样做保留了两个单独的对象，而不是丢弃一个并将 t1 和 t2 绑定到同一个对象。但是这样的操作有点违背 Java 的设计原则。对象的赋值是个需要重视的环节，否则你可能收获意外的“惊喜”。

 <!-- Aliasing During Method Calls -->
### 方法调用中的别名现象

当我们把对象传递给方法时，会发生别名现象。

```java
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

在许多编程语言中，方法 `f()` 似乎会在内部复制其参数 **Letter y**。但是一旦传递了一个引用，那么实际上 `y.c ='z';` 是在方法 `f()` 之外改变对象。别名现象以及其解决方案是个复杂的问题，在附录中有包含：[对象传递和返回](./Appendix-Passing-and-Returning-Objects.md)。意识到这一点，我们可以警惕类似的陷阱。

<!-- Mathematical Operators -->
## 算术运算符

Java 的基本算术运算符与其他大多编程语言是相同的。其中包括加号 `+`、减号 `-`、除号 `/`、乘号 `*` 以及取模 `%`（从整数除法中获得余数）。整数除法会直接砍掉小数，而不是进位。

Java 也用一种与 C++ 相同的简写形式同时进行运算和赋值操作，由运算符后跟等号表示，并且与语言中的所有运算符一致（只要有意义）。  可用 x += 4 来表示：将 x 的值加上4的结果再赋值给 x。更多代码示例：

```java
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

为了生成随机数字，程序首先创建一个 **Random** 对象。不带参数的 **Random** 对象会利用当前的时间用作随机数生成器的“种子”（seed），从而为程序的每次执行生成不同的输出。在本书的示例中，重要的是每个示例末尾的输出尽可能一致，以便可以使用外部工具进行验证。所以我们通过在创建 **Random** 对象时提供种子（随机数生成器的初始化值，其始终为特定种子值产生相同的序列），让程序每次执行都生成相同的随机数，如此以来输出结果就是可验证的 [^1]。 若需要生成随机值，可删除代码示例中的种子参数。该对象通过调用方法 `nextInt()` 和 `nextFloat()`（还可以调用 `nextLong()` 或 `nextDouble()`），使用 **Random** 对象生成许多不同类型的随机数。`nextInt()` 的参数设置生成的数字的上限，下限为零，为了避免零除的可能性，结果偏移1。

<!-- Unary Minus and Plus Operators -->
### 一元加减运算符

一元加 `+` 减 `-` 运算符的操作和二元是相同的。编译器可自动识别使用何种方式解析运算：

```java
x = -a;
```

上例的代码表意清晰，编译器可正确识别。下面再看一个示例：

```java
x = a * -b;
```

虽然编译器可以正确的识别，但是程序员可能会迷惑。为了避免混淆，推荐下面的写法：

```java
x = a * (-b);
```

一元减号可以得到数据的负值。一元加号的作用相反，不过它唯一能影响的就是把较小的数值类型自动转换为 **int** 类型。

<!-- Auto-Increment-and-Decrement -->
## 递增和递减

和 C 语言类似，Java 提供了许多快捷运算方式。快捷运算可使代码可读性，可写性都更强。其中包括递增 `++` 和递减 `--`，意为“增加或减少一个单位”。举个例子来说，假设 a 是一个 **int** 类型的值，则表达式 `++a` 就等价于 `a = a + 1`。 递增和递减运算符不仅可以修改变量，还可以生成变量的值。

每种类型的运算符，都有两个版本可供选用；通常将其称为“前缀”和“后缀”。“前递增”表示 `++` 运算符位于变量或表达式的前面；而“后递增”表示 `++` 运算符位于变量的后面。类似地，“前递减”意味着 `--` 运算符位于变量的前面；而“后递减”意味着 `--` 运算符位于变量的后面。对于前递增和前递减（如 `++a` 或 `--a`），会先执行递增/减运算，再返回值。而对于后递增和后递减（如 `a++` 或 `a--`），会先返回值，再执行递增/减运算。代码示例：

```java
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

对于前缀形式，我们将在执行递增/减操作后获取值；使用后缀形式，我们将在执行递增/减操作之前获取值。它们是唯一具有“副作用”的运算符（除那些涉及赋值的以外） —— 它们修改了操作数的值。

C++ 名称来自于递增运算符，暗示着“比 C 更进一步”。在早期的 Java 演讲中，*Bill Joy*（Java 作者之一）说“**Java = C++ --**”（C++ 减减），意味着 Java 在 C++  的基础上减少了许多不必要的东西，因此语言更简单。随着进一步地学习，我们会发现 Java 的确有许多地方相对 C++ 来说更简便，但是在其他方面，难度并不会比 C++ 小多少。

<!-- Relational-Operators -->
## 关系运算符

关系运算符会通过产生一个布尔（**boolean**）结果来表示操作数之间的关系。如果关系为真，则结果为 **true**，如果关系为假，则结果为 **false**。关系运算符包括小于 `<`，大于 `>`，小于或等于 `<=`，大于或等于 `>=`，等于 `==` 和不等于 `！=`。`==` 和 `!=` 可用于所有基本类型，但其他运算符不能用于基本类型 **boolean**，因为布尔值只能表示 **true** 或 **false**，所以比较它们之间的“大于”或“小于”没有意义。

<!-- Testing Object Equivalence -->
### 测试对象等价

关系运算符 `==` 和 `!=` 同样适用于所有对象之间的比较运算，但它们比较的内容却经常困扰 Java 的初学者。下面是代码示例：

```java
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

表达式 `System.out.println(n1 == n2)` 将会输出比较的结果。因为两个 **Integer** 对象相同，所以先输出 **true**，再输出 **false**。但是，尽管对象的内容一样，对象的引用却不一样。`==` 和 `!=` 比较的是对象引用，所以输出实际上应该是先输出 **false**，再输出 **true**（译者注：如果你把 47 改成 128，那么打印的结果就是这样，因为 Integer 内部维护着一个 IntegerCache 的缓存，默认缓存范围是 [-128, 127]，所以 [-128, 127] 之间的值用 `==` 和 `!=` 比较也能能到正确的结果，但是不推荐用关系运算符比较，具体见 JDK 中的 Integer 类源码）。

那么怎么比较两个对象的内容是否相同呢？你必须使用所有对象（不包括基本类型）中都存在的 `equals()` 方法，下面是如何使用 `equals()` 方法的示例：

```java
// operators/EqualsMethod.java
public class EqualsMethod {
    public static void main(String[] args) {
        Integer n1 = 47;
        Integer n2 = 47;
        System.out.println(n1.equals(n2));
    }
}
```

输出结果:

```java
true
```

上例的结果看起来是我们所期望的。但其实事情并非那么简单。下面我们来创建自己的类：

```java
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

```java
false
```

上例的结果再次令人困惑：结果是 **false**。原因： `equals()` 的默认行为是比较对象的引用而非具体内容。因此，除非你在新类中覆写 `equals()` 方法，否则我们将获取不到想要的结果。不幸的是，在学习 [复用](./08-Reuse.md)（**Reuse**） 章节后我们才能接触到“覆写”（**Override**），并且直到 [附录:集合主题](./Appendix-Collection-Topics.md)，才能知道定义 `equals()` 方法的正确方式，但是现在明白 `equals()` 行为方式也可能为你节省一些时间。

大多数 Java 库类通过覆写 `equals()` 方法比较对象的内容而不是其引用。

<!-- Logical Operators -->
## 逻辑运算符

每个逻辑运算符 `&&` （**AND**）、`||`（**OR**）和 `!`（**非**）根据参数的逻辑关系生成布尔值 `true` 或 `false`。下面的代码示例使用了关系运算符和逻辑运算符：

```java
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

在 Java 逻辑运算中，我们不能像 C/C++ 那样使用非布尔值， 而仅能使用 **AND**、 **OR**、 **NOT**。上面的例子中，我们将使用非布尔值的表达式注释掉了（你可以看到表达式前面是 //-）。但是，后续的表达式使用关系比较生成布尔值，然后对结果使用了逻辑运算。请注意，如果在预期为 **String** 类型的位置使用 **boolean** 类型的值，则结果会自动转为适当的文本格式（即 "true" 或 "false" 字符串）。

我们可以将前一个程序中 **int** 的定义替换为除 **boolean** 之外的任何其他基本数据类型。但请注意，**float** 类型的数值比较非常严格，只要两个数字的最小位不同则两个数仍然不相等；只要数字最小位是大于 0 的，那么它就不等于 0。

<!-- Short-Circuiting -->
### 短路

逻辑运算符支持一种称为“短路”（short-circuiting）的现象。整个表达式会在运算到可以明确结果时就停止并返回结果，这意味着该逻辑表达式的后半部分不会被执行到。代码示例：

```java
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

```java
test1（0）&& test2（2）&& test3（2）
```

可能你的预期是程序会执行 3 个 **test** 方法并返回。我们来分析一下：第一个方法的结果返回 `true`，因此表达式会继续走下去。紧接着，第二个方法的返回结果是 `false`。这就代表这整个表达式的结果肯定为 `false`，所以就没有必要再判断剩下的表达式部分了。

所以，运用“短路”可以节省部分不必要的运算，从而提高程序潜在的性能。

<!-- Literals -->
## 字面值常量

通常，当我们向程序中插入一个字面值常量（**Literal**）时，编译器会确切地识别它的类型。当类型不明确时，必须辅以字面值常量关联来帮助编译器识别。代码示例：

```java
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

在文本值的后面添加字符可以让编译器识别该文本值的类型。对于 **Long** 型数值，结尾使用大写 `L` 或小写 `l` 皆可（不推荐使用 `l`，因为容易与阿拉伯数值 1 混淆）。大写 `F` 或小写 `f` 表示 **float** 浮点数。大写 `D` 或小写 `d` 表示 **double** 双精度。

十六进制（以 16 为基数），适用于所有整型数据类型，由前导 `0x` 或 `0X` 表示，后跟 0-9 或 a-f （大写或小写）。如果我们在初始化某个类型的数值时，赋值超出其范围，那么编译器会报错（不管值的数字形式如何）。在上例的代码中，**char**、**byte** 和 **short** 的值已经是最大了。如果超过这些值，编译器将自动转型为 **int**，并且提示我们需要声明强制转换（强制转换将在本章后面定义），意味着我们已越过该类型的范围界限。

八进制（以 8 为基数）由 0~7 之间的数字和前导零 `0` 表示。

Java 7 引入了二进制的字面值常量，由前导 `0b` 或 `0B` 表示，它可以初始化所有的整数类型。

使用整型数值类型时，显示其二进制形式会很有用。在 Long 型和 Integer 型中这很容易实现，调用其静态的 `toBinaryString()` 方法即可。 但是请注意，若将较小的类型传递给 **Integer.**`tobinarystring()` 时，类型将自动转换为 **int**。

<!-- Underscores in Literals -->
### 下划线

Java 7 中有一个深思熟虑的补充：我们可以在数字字面量中包含下划线 `_`，以使结果更清晰。这对于大数值的分组特别有用。代码示例：

```java
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

[1] 注意 `%n`的使用。熟悉 C 风格的程序员可能习惯于看到 `\n` 来表示换行符。问题在于它给你的是一个“Unix风格”的换行符。此外，如果我们使用的是 Windows，则必须指定 `\r\n`。这种差异的包袱应该由编程语言来解决。这就是 Java 用 `%n` 实现的可以忽略平台间差异而生成适当的换行符，但只有当你使用 `System.out.printf()` 或 `System.out.format()` 时。对于 `System.out.println()`，我们仍然必须使用 `\n`；如果你使用 `%n`，`println()` 只会输出 `%n` 而不是换行符。

<!-- Exponential Notation -->
### 指数计数法

指数总是采用一种我认为很不直观的记号方法:

```java
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

在科学与工程学领域，**e** 代表自然对数的基数，约等于 2.718 （Java 里用一种更精确的 **double** 值 **Math.E** 来表示自然对数）。指数表达式 "1.39 x e-43"，意味着 “1.39 × 2.718 的 -43 次方”。然而，自 FORTRAN 语言发明后，人们自然而然地觉得e 代表 “10 的几次幂”。这种做法显得颇为古怪，因为 FORTRAN 最初是为科学与工程领域设计的。

理所当然，它的设计者应对这样的混淆概念持谨慎态度 [^2]。但不管怎样，这种特别的表达方法在 C，C++ 以及现在的 Java 中顽固地保留下来了。所以倘若习惯 e 作为自然对数的基数使用，那么在 Java 中看到类似“1.39e-43f”这样的表达式时，请转换你的思维，从程序设计的角度思考它；它真正的含义是 “1.39 × 10 的 -43 次方”。

注意如果编译器能够正确地识别类型，就不必使用后缀字符。对于下述语句：

```java
long n3 = 200;
```

它并不存在含糊不清的地方，所以 200 后面的 L 大可省去。然而，对于下述语句：

```java
float f4 = 1e-43f; //10 的幂数
```

编译器通常会将指数作为 **double** 类型来处理，所以假若没有这个后缀字符 `f`，编译器就会报错，提示我们应该将 **double** 型转换成 **float** 型。

<!-- Bitwise-Operators -->
## 位运算符

位运算符允许我们操作一个整型数字中的单个二进制位。位运算符会对两个整数对应的位执行布尔代数，从而产生结果。

位运算源自 C 语言的底层操作。我们经常要直接操纵硬件，频繁设置硬件寄存器内的二进制位。Java 的设计初衷是电视机顶盒嵌入式开发，所以这种底层的操作仍被保留了下来。但是，你可能不会使用太多位运算。

若两个输入位都是 1，则按位“与运算符” `&` 运算后结果是 1，否则结果是 0。若两个输入位里至少有一个是 1，则按位“或运算符” `|` 运算后结果是 1；只有在两个输入位都是 0 的情况下，运算结果才是 0。若两个输入位的某一个是 1，另一个不是 1，那么按位“异或运算符” `^` 运算后结果才是 1。按位“非运算符” `~` 属于一元运算符；它只对一个自变量进行操作（其他所有运算符都是二元运算符）。按位非运算后结果与输入位相反。例如输入 0，则输出 1；输入 1，则输出 0。

位运算符和逻辑运算符都使用了同样的字符，只不过数量不同。位短，所以位运算符只有一个字符。位运算符可与等号 `=` 联合使用以接收结果及赋值：`&=`，`|=` 和 `^=` 都是合法的（由于 `~` 是一元运算符，所以不可与 `=` 联合使用）。

我们将 **Boolean** 类型被视为“单位值”（one-bit value），所以它多少有些独特的地方。我们可以对 boolean 型变量执行与、或、异或运算，但不能执行非运算（大概是为了避免与逻辑“非”混淆）。对于布尔值，位运算符具有与逻辑运算符相同的效果，只是它们不会中途“短路”。此外，针对布尔值进行的位运算为我们新增了一个“异或”逻辑运算符，它并未包括在逻辑运算符的列表中。在移位表达式中，禁止使用布尔值，原因将在下面解释。

<!-- Shift Operators -->
## 移位运算符

移位运算符面向的运算对象也是二进制的“位”。它们只能用于处理整数类型（基本类型的一种）。左移位运算符 `<<` 能将其左边的运算对象向左移动右侧指定的位数（在低位补 0）。右移位运算符 `>>` 则相反。右移位运算符有“正”、“负”值：若值为正，则在高位插入 0；若值为负，则在高位插入 1。Java 也添加了一种“不分正负”的右移位运算符（>>>），它使用了“零扩展”（zero extension）：无论正负，都在高位插入 0。这一运算符是 C/C++ 没有的。

如果移动 **char**、**byte** 或 **short**，则会在移动发生之前将其提升为 **int**，结果为 **int**。仅使用右值（rvalue）的 5 个低阶位。这可以防止我们移动超过 **int** 范围的位数。若对一个 **long** 值进行处理，最后得到的结果也是 **long**。

移位可以与等号 `<<=` 或 `>>=` 或 `>>>=` 组合使用。左值被替换为其移位运算后的值。但是，问题来了，当无符号右移与赋值相结合时，若将其与 **byte** 或 **short** 一起使用的话，则结果错误。取而代之的是，它们被提升为 **int** 型并右移，但在重新赋值时被截断。在这种情况下，结果为 -1。下面是代码示例：

```java
// operators/URShift.java
// 测试无符号右移
public class URShift {
    public static void main(String[] args) {
        int i = -1;
        System.out.println(Integer.toBinaryString(i));
        i >>>= 10;
        System.out.println(Integer.toBinaryString(i));
        long l = -1;
        System.out.println(Long.toBinaryString(l));
        l >>>= 10;
        System.out.println(Long.toBinaryString(l));
        short s = -1;
        System.out.println(Integer.toBinaryString(s));
        s >>>= 10;
        System.out.println(Integer.toBinaryString(s));
        byte b = -1;
        System.out.println(Integer.toBinaryString(b));
        b >>>= 10;
        System.out.println(Integer.toBinaryString(b));
        b = -1;
        System.out.println(Integer.toBinaryString(b));
        System.out.println(Integer.toBinaryString(b>>>10));
    }
}
```

输出结果：

```
11111111111111111111111111111111
1111111111111111111111
1111111111111111111111111111111111111111111111111111111111111111
111111111111111111111111111111111111111111111111111111
11111111111111111111111111111111
11111111111111111111111111111111
11111111111111111111111111111111
11111111111111111111111111111111
11111111111111111111111111111111
1111111111111111111111
```


在上例中，结果并未重新赋值给变量 **b** ，而是直接打印出来，因此一切正常。下面是一个涉及所有位运算符的代码示例：

```java
// operators/BitManipulation.java
// 使用位运算符
import java.util.*;
public class BitManipulation {
    public static void main(String[] args) {
        Random rand = new Random(47);
        int i = rand.nextInt();
        int j = rand.nextInt();
        printBinaryInt("-1", -1);
        printBinaryInt("+1", +1);
        int maxpos = 2147483647;
        printBinaryInt("maxpos", maxpos);
        int maxneg = -2147483648;
        printBinaryInt("maxneg", maxneg);
        printBinaryInt("i", i);
        printBinaryInt("~i", ~i);
        printBinaryInt("-i", -i);
        printBinaryInt("j", j);
        printBinaryInt("i & j", i & j);
        printBinaryInt("i | j", i | j);
        printBinaryInt("i ^ j", i ^ j);
        printBinaryInt("i << 5", i << 5);
        printBinaryInt("i >> 5", i >> 5);
        printBinaryInt("(~i) >> 5", (~i) >> 5);
        printBinaryInt("i >>> 5", i >>> 5);
        printBinaryInt("(~i) >>> 5", (~i) >>> 5);
        long l = rand.nextLong();
        long m = rand.nextLong();
        printBinaryLong("-1L", -1L);
        printBinaryLong("+1L", +1L);
        long ll = 9223372036854775807L;
        printBinaryLong("maxpos", ll);
        long lln = -9223372036854775808L;
        printBinaryLong("maxneg", lln);
        printBinaryLong("l", l);
        printBinaryLong("~l", ~l);
        printBinaryLong("-l", -l);
        printBinaryLong("m", m);
        printBinaryLong("l & m", l & m);
        printBinaryLong("l | m", l | m);
        printBinaryLong("l ^ m", l ^ m);
        printBinaryLong("l << 5", l << 5);
        printBinaryLong("l >> 5", l >> 5);
        printBinaryLong("(~l) >> 5", (~l) >> 5);
        printBinaryLong("l >>> 5", l >>> 5);
        printBinaryLong("(~l) >>> 5", (~l) >>> 5);
    }

    static void printBinaryInt(String s, int i) {
        System.out.println(
        s + ", int: " + i + ", binary:\n " +
        Integer.toBinaryString(i));
    }

    static void printBinaryLong(String s, long l) {
        System.out.println(
        s + ", long: " + l + ", binary:\n " +
        Long.toBinaryString(l));
    }
}
```

输出结果（前 32 行）：

```
-1, int: -1, binary:
11111111111111111111111111111111
+1, int: 1, binary:
1
maxpos, int: 2147483647, binary:
1111111111111111111111111111111
maxneg, int: -2147483648, binary:
10000000000000000000000000000000
i, int: -1172028779, binary:
10111010001001000100001010010101
~i, int: 1172028778, binary:
 1000101110110111011110101101010
-i, int: 1172028779, binary:
1000101110110111011110101101011
j, int: 1717241110, binary:
1100110010110110000010100010110
i & j, int: 570425364, binary:
100010000000000000000000010100
i | j, int: -25213033, binary:
11111110011111110100011110010111
i ^ j, int: -595638397, binary:
11011100011111110100011110000011
i << 5, int: 1149784736, binary:
1000100100010000101001010100000
i >> 5, int: -36625900, binary:
11111101110100010010001000010100
(~i) >> 5, int: 36625899, binary:
10001011101101110111101011
i >>> 5, int: 97591828, binary:
101110100010010001000010100
(~i) >>> 5, int: 36625899, binary:
10001011101101110111101011
    ...
```

结尾的两个方法 `printBinaryInt()` 和 `printBinaryLong()` 分别操作一个 **int** 和 **long** 值，并转换为二进制格式输出，同时附有简要的文字说明。除了演示 **int** 和 **long** 的所有位运算符的效果之外，本示例还显示 **int** 和 **long** 的最小值、最大值、+1 和 -1 值，以便我们了解它们的形式。注意高位代表符号：0 表示正，1 表示负。上面显示了 **int** 部分的输出。以上数字的二进制表示形式是带符号的补码（2's complement）。

<!-- Ternary-if-else-Operator -->
## 三元运算符

三元运算符，也称为条件运算符。这种运算符比较罕见，因为它有三个运算对象。但它确实属于运算符的一种，因为它最终也会生成一个值。这与本章后一节要讲述的普通 **if-else** 语句是不同的。下面是它的表达式格式：

**布尔表达式 ? 值 1 : 值 2**

若表达式计算为 **true**，则返回结果 **值 1** ；如果表达式的计算为 **false**，则返回结果 **值 2**。

当然，也可以换用普通的 **if-else** 语句（在后面介绍），但三元运算符更加简洁。作为三元运算符的创造者， C 自诩为一门简练的语言。三元运算符的引入多半就是为了高效编程，但假若我们打算频繁使用它的话，还是先多作一些思量： 它易于产生可读性差的代码。与 **if-else** 不同的是，三元运算符是有返回结果的。请看下面的代码示例：

```java
// operators/TernaryIfElse.java
public class TernaryIfElse {
    
static int ternary(int i) {
    return i < 10 ? i * 100 : i * 10;
}

static int standardIfElse(int i) {
    if(i < 10)
        return i * 100;
    else
        return i * 10;
}

    public static void main(String[] args) {
        System.out.println(ternary(9));
        System.out.println(ternary(10));
        System.out.println(standardIfElse(9));
        System.out.println(standardIfElse(10));
    }
}
```

输出结果：

```
900
100
900
100
```

可以看出，`ternary()` 中的代码更简短。然而，**standardIfElse()** 中的代码更易理解且不要求更多的录入。所以我们在挑选三元运算符时，请务必权衡一下利弊。

<!-- String-Operator-+-and-+= -->
## 字符串运算符

这个运算符在 Java 里有一项特殊用途：连接字符串。这点已在前面展示过了。尽管与 `+` 的传统意义不符，但如此使用也还是比较自然的。这一功能看起来还不错，于是在 C++ 里引入了“运算符重载”机制，以便 C++ 程序员为几乎所有运算符增加特殊的含义。但遗憾得是，与 C++ 的一些限制结合以后，它变得复杂。这要求程序员在设计自己的类时必须对此有周全的考虑。虽然在 Java 中实现运算符重载机制并非难事（如 C# 所展示的，它具有简单的运算符重载），但因该特性过于复杂，因此 Java 并未实现它。

我们注意到运用 `String +` 时有一些有趣的现象。若表达式以一个 **String** 类型开头（编译器会自动将双引号 `""` 标注的的字符序列转换为字符串），那么后续所有运算对象都必须是字符串。代码示例：

```java
// operators/StringOperators.java
public class StringOperators {
    public static void main(String[] args) {
        int x = 0, y = 1, z = 2;
        String s = "x, y, z ";
        System.out.println(s + x + y + z);
        // 将 x 转换为字符串
        System.out.println(x + " " + s);
        s += "(summed) = "; 
        // 级联操作
        System.out.println(s + (x + y + z));
        // Integer.toString()方法的简写:
        System.out.println("" + x);
    }
}
```

输出结果：

```
x, y, z 012
0 x, y, z
x, y, z (summed) = 3
0
```

**注意**：上例中第 1 输出语句的执行结果是 `012` 而并非 `3`，这是因为编译器将其分别转换为其字符串形式然后与字符串变量 **s** 连接。在第 2 条输出语句中，编译器将开头的变量转换为了字符串，由此可以看出，这种转换与数据的位置无关，只要当中有一条数据是字符串类型，其他非字符串数据都将被转换为字符串形式并连接。最后一条输出语句，我们可以看出 `+=` 运算符可以拼接其右侧的字符串连接结果并重赋值给自身变量 `s`。括号 `()` 可以控制表达式的计算顺序，以便在显示 **int** 之前对其进行实际求和。

请注意主方法中的最后一个例子：我们经常会看到一个空字符串 `""` 跟着一个基本类型的数据。这样可以隐式地将其转换为字符串，以代替繁琐的显式调用方法（如这里可以使用 **Integer.toString()**）。

<!-- Common-Pitfalls-When-Using-Operators -->
## 常见陷阱

使用运算符时很容易犯的一个错误是，在还没搞清楚表达式的计算方式时就试图忽略括号 `()`。在 Java 中也一样。 在 C++ 中你甚至可能犯这样极端的错误.代码示例：

```java
while(x = y) {
// ...
}
```

显然，程序员原意是测试等价性 `==`，而非赋值 `=`。若变量 **y** 非 0 的话，在 C/C++ 中，这样的赋值操作总会返回 `true`。于是，上面的代码示例将会无限循环。而在 Java 中，这样的表达式结果并不会转化为一个布尔值。 而编译器会试图把这个 **int** 型数据转换为预期应接收的布尔类型。最后，我们将会在试图运行前收到编译期错误。因此，Java 天生避免了这种陷阱发生的可能。

唯一有种情况例外：当变量 `x` 和 `y` 都是布尔值，例如  `x=y` 是一个逻辑表达式。除此之外，之前的那个例子，很大可能是错误。

在 C/C++ 里，类似的一个问题还有使用按位“与” `&` 和“或” `|` 运算，而非逻辑“与” `&&` 和“或” `||`。就象 `=` 和 `==` 一样，键入一个字符当然要比键入两个简单。在 Java 中，编译器同样可防止这一点，因为它不允许我们强行使用另一种并不符的类型。

<!-- Casting-Operators -->
## 类型转换

“类型转换”（Casting）的作用是“与一个模型匹配”。在适当的时候，Java 会将一种数据类型自动转换成另一种。例如，假设我们为 **float** 变量赋值一个整数值，计算机会将 **int** 自动转换成 **float**。我们可以在程序未自动转换时显式、强制地使此类型发生转换。

要执行强制转换，需要将所需的数据类型放在任何值左侧的括号内，如下所示：

```java
// operators/Casting.java
public class Casting {
    public static void main(String[] args) {
        int i = 200;
        long lng = (long)i;
        lng = i; // 没有必要的类型提升
        long lng2 = (long)200;
        lng2 = 200;
        // 类型收缩
        i = (int)lng2; // Cast required
    }
}
```

诚然，你可以这样地去转换一个数值类型的变量。但是上例这种做法是多余的：因为编译器会在必要时自动提升 **int** 型数据为 **long** 型。

当然，为了程序逻辑清晰或提醒自己留意，我们也可以显式地类型转换。在其他情况下，类型转换型只有在代码编译时才显出其重要性。在 C/C++ 中，类型转换有时会让人头痛。在 Java 里，类型转换则是一种比较安全的操作。但是，若将数据类型进行“向下转换”（**Narrowing Conversion**）的操作（将容量较大的数据类型转换成容量较小的类型），可能会发生信息丢失的危险。此时，编译器会强迫我们进行转型，好比在提醒我们：该操作可能危险，若你坚持让我这么做，那么对不起，请明确需要转换的类型。 对于“向上转换”（**Widening conversion**），则不必进行显式的类型转换，因为较大类型的数据肯定能容纳较小类型的数据，不会造成任何信息的丢失。

除了布尔类型的数据，Java 允许任何基本类型的数据转换为另一种基本类型的数据。此外，类是不能进行类型转换的。为了将一个类转换为另一个类型，需要使用特殊的方法（后面将会学习到如何在父子类之间进行向上/向下转型，例如，“橡树”可以转换为“树”，反之亦然。而对于“岩石”是无法转换为“树”的）。

<!-- Truncation and Rounding -->
### 截断和舍入

在执行“向下转换”时，必须注意数据的截断和舍入问题。若从浮点值转换为整型值，Java 会做什么呢？例如：浮点数 29.7 被转换为整型值，结果会是 29 还是 30 呢？下面是代码示例：

```java
// operators/CastingNumbers.java
// 尝试转换 float 和 double 型数据为整型数据
public class CastingNumbers {
    public static void main(String[] args) {
        double above = 0.7, below = 0.4;
        float fabove = 0.7f, fbelow = 0.4f;
        System.out.println("(int)above: " + (int)above);
        System.out.println("(int)below: " + (int)below);
        System.out.println("(int)fabove: " + (int)fabove);
        System.out.println("(int)fbelow: " + (int)fbelow);
    }
}
```

输出结果：

```
(int)above: 0
(int)below: 0
(int)fabove: 0
(int)fbelow: 0
```

因此，答案是，从 **float** 和 **double** 转换为整数值时，小数位将被截断。若你想对结果进行四舍五入，可以使用 `java.lang.Math` 的 ` round()` 方法：

```java
// operators/RoundingNumbers.java
// float 和 double 类型数据的四舍五入
public class RoundingNumbers {
    public static void main(String[] args) {
        double above = 0.7, below = 0.4;
        float fabove = 0.7f, fbelow = 0.4f;
        System.out.println(
        "Math.round(above): " + Math.round(above));
        System.out.println(
        "Math.round(below): " + Math.round(below));
        System.out.println(
        "Math.round(fabove): " + Math.round(fabove));
        System.out.println(
        "Math.round(fbelow): " + Math.round(fbelow));
    }
}
```

输出结果：
```
Math.round(above): 1
Math.round(below): 0
Math.round(fabove): 1
Math.round(fbelow): 0
```

因为 `round()` 方法是 `java.lang` 的一部分，所以我们无需通过 `import` 就可以使用。

<!-- Promotion -->
### 类型提升

你会发现，如果我们对小于 **int** 的基本数据类型（即 **char**、**byte** 或 **short**）执行任何算术或按位操作，这些值会在执行操作之前类型提升为 **int**，并且结果值的类型为 **int**。若想重新使用较小的类型，必须使用强制转换（由于重新分配回一个较小的类型，结果可能会丢失精度）。通常，表达式中最大的数据类型是决定表达式结果的数据类型。**float** 型和 **double** 型相乘，结果是 **double** 型的；**int** 和 **long** 相加，结果是 **long** 型。

<!-- Java-Has-No-sizeof -->
## Java没有sizeof

在 C/C++ 中，经常需要用到 `sizeof()` 方法来获取数据项被分配的字节大小。C/C++ 中使用 `sizeof()` 最有说服力的原因是为了移植性，不同数据在不同机器上可能有不同的大小，所以在进行大小敏感的运算时，程序员必须对这些类型有多大做到心中有数。例如，一台计算机可用 32 位来保存整数，而另一台只用 16 位保存。显然，在第一台机器中，程序可保存更大的值。所以，移植是令 C/C++ 程序员颇为头痛的一个问题。

Java 不需要 ` sizeof()` 方法来满足这种需求，因为所有类型的大小在不同平台上是相同的。我们不必考虑这个层次的移植问题 —— Java 本身就是一种“与平台无关”的语言。

<!-- A-Compendium-of-Operators -->
## 运算符总结

上述示例分别向我们展示了哪些基本类型能被用于特定的运算符。基本上，下面的代码示例是对上述所有示例的重复，只不过概括了所有的基本类型。这个文件能被正确地编译，因为我已经把编译不通过的那部分用注释 `//` 过滤了。代码示例：

```java
// operators/AllOps.java
// 测试所有基本类型的运算符操作
// 看看哪些是能被 Java 编译器接受的
public class AllOps {
    // 布尔值的接收测试：
    void f(boolean b) {}
    void boolTest(boolean x, boolean y) {
        // 算数运算符：
        //- x = x * y;
        //- x = x / y;
        //- x = x % y;
        //- x = x + y;
        //- x = x - y;
        //- x++;
        //- x--;
        //- x = +y;
        //- x = -y;
        // 关系运算符和逻辑运算符：
        //- f(x > y);
        //- f(x >= y);
        //- f(x < y);
        //- f(x <= y);
        f(x == y);
        f(x != y);
        f(!y);
        x = x && y;
        x = x || y;
        // 按位运算符：
        //- x = ~y;
        x = x & y;
        x = x | y;
        x = x ^ y;
        //- x = x << 1;
        //- x = x >> 1;
        //- x = x >>> 1;
        // 联合赋值：
        //- x += y;
        //- x -= y;
        //- x *= y;
        //- x /= y;
        //- x %= y;
        //- x <<= 1;
        //- x >>= 1;
        //- x >>>= 1;
        x &= y;
        x ^= y;
        x |= y;
        // 类型转换：
        //- char c = (char)x;
        //- byte b = (byte)x;
        //- short s = (short)x;
        //- int i = (int)x;
        //- long l = (long)x;
        //- float f = (float)x;
        //- double d = (double)x;
    }

    void charTest(char x, char y) {
        // 算数运算符：
        x = (char)(x * y);
        x = (char)(x / y);
        x = (char)(x % y);
        x = (char)(x + y);
        x = (char)(x - y);
        x++;
        x--;
        x = (char) + y;
        x = (char) - y;
        // 关系和逻辑运算符：
        f(x > y);
        f(x >= y);
        f(x < y);
        f(x <= y);
        f(x == y);
        f(x != y);
        //- f(!x);
        //- f(x && y);
        //- f(x || y);
        // 按位运算符：
        x= (char)~y;
        x = (char)(x & y);
        x = (char)(x | y);
        x = (char)(x ^ y);
        x = (char)(x << 1);
        x = (char)(x >> 1);
        x = (char)(x >>> 1);
        // 联合赋值：
        x += y;
        x -= y;
        x *= y;
        x /= y;
        x %= y;
        x <<= 1;
        x >>= 1;
        x >>>= 1;
        x &= y;
        x ^= y;
        x |= y;
        // 类型转换
        //- boolean bl = (boolean)x;
        byte b = (byte)x;
        short s = (short)x;
        int i = (int)x;
        long l = (long)x;
        float f = (float)x;
        double d = (double)x;
    }

    void byteTest(byte x, byte y) {
        // 算数运算符：
        x = (byte)(x* y);
        x = (byte)(x / y);
        x = (byte)(x % y);
        x = (byte)(x + y);
        x = (byte)(x - y);
        x++;
        x--;
        x = (byte) + y;
        x = (byte) - y;
        // 关系和逻辑运算符：
        f(x > y);
        f(x >= y);
        f(x < y);
        f(x <= y);
        f(x == y);
        f(x != y);
        //- f(!x);
        //- f(x && y);
        //- f(x || y);
        //按位运算符：
        x = (byte)~y;
        x = (byte)(x & y);
        x = (byte)(x | y);
        x = (byte)(x ^ y);
        x = (byte)(x << 1);
        x = (byte)(x >> 1);
        x = (byte)(x >>> 1);
        // 联合赋值：
        x += y;
        x -= y;
        x *= y;
        x /= y;
        x %= y;
        x <<= 1;
        x >>= 1;
        x >>>= 1;
        x &= y;
        x ^= y;
        x |= y;
        // 类型转换：
        //- boolean bl = (boolean)x;
        char c = (char)x;
        short s = (short)x;
        int i = (int)x;
        long l = (long)x;
        float f = (float)x;
        double d = (double)x;
    }

    void shortTest(short x, short y) {
        // 算术运算符：
        x = (short)(x * y);
        x = (short)(x / y);
        x = (short)(x % y);
        x = (short)(x + y);
        x = (short)(x - y);
        x++;
        x--;
        x = (short) + y;
        x = (short) - y;
        // 关系和逻辑运算符：
        f(x > y);
        f(x >= y);
        f(x < y);
        f(x <= y);
        f(x == y);
        f(x != y);
        //- f(!x);
        //- f(x && y);
        //- f(x || y);
        // 按位运算符：
        x = (short) ~ y;
        x = (short)(x & y);
        x = (short)(x | y);
        x = (short)(x ^ y);
        x = (short)(x << 1);
        x = (short)(x >> 1);
        x = (short)(x >>> 1);
        // Compound assignment:
        x += y;
        x -= y;
        x *= y;
        x /= y;
        x %= y;
        x <<= 1;
        x >>= 1;
        x >>>= 1;
        x &= y;
        x ^= y;
        x |= y;
        // 类型转换：
        //- boolean bl = (boolean)x;
        char c = (char)x;
        byte b = (byte)x;
        int i = (int)x;
        long l = (long)x;
        float f = (float)x;
        double d = (double)x;
    }

    void intTest(int x, int y) {
        // 算术运算符：
        x = x * y;
        x = x / y;
        x = x % y;
        x = x + y;
        x = x - y;
        x++;
        x--;
        x = +y;
        x = -y;
        // 关系和逻辑运算符：
        f(x > y);
        f(x >= y);
        f(x < y);
        f(x <= y);
        f(x == y);
        f(x != y);
        //- f(!x);
        //- f(x && y);
        //- f(x || y);
        // 按位运算符：
        x = ~y;
        x = x & y;
        x = x | y;
        x = x ^ y;
        x = x << 1;
        x = x >> 1;
        x = x >>> 1;
        // 联合赋值：
        x += y;
        x -= y;
        x *= y;
        x /= y;
        x %= y;
        x <<= 1;
        x >>= 1;
        x >>>= 1;
        x &= y;
        x ^= y;
        x |= y;
        // 类型转换：
        //- boolean bl = (boolean)x;
        char c = (char)x;
        byte b = (byte)x;
        short s = (short)x;
        long l = (long)x;
        float f = (float)x;
        double d = (double)x;
    }

    void longTest(long x, long y) {
        // 算数运算符：
        x = x * y;
        x = x / y;
        x = x % y;
        x = x + y;
        x = x - y;
        x++;
        x--;
        x = +y;
        x = -y;
        // 关系和逻辑运算符：
        f(x > y);
        f(x >= y);
        f(x < y);
        f(x <= y);
        f(x == y);
        f(x != y);
        //- f(!x);
        //- f(x && y);
        //- f(x || y);
        // 按位运算符：
        x = ~y;
        x = x & y;
        x = x | y;
        x = x ^ y;
        x = x << 1;
        x = x >> 1;
        x = x >>> 1;
        // 联合赋值：
        x += y;
        x -= y;
        x *= y;
        x /= y;
        x %= y;
        x <<= 1;
        x >>= 1;
        x >>>= 1;
        x &= y;
        x ^= y;
        x |= y;
        // 类型转换：
        //- boolean bl = (boolean)x;
        char c = (char)x;
        byte b = (byte)x;
        short s = (short)x;
        int i = (int)x;
        float f = (float)x;
        double d = (double)x;
    }

    void floatTest(float x, float y) {
        // 算数运算符：
        x = x * y;
        x = x / y;
        x = x % y;
        x = x + y;
        x = x - y;
        x++;
        x--;
        x = +y;
        x = -y;
        // 关系和逻辑运算符：
        f(x > y);
        f(x >= y);
        f(x < y);
        f(x <= y);
        f(x == y);
        f(x != y);
        //- f(!x);
        //- f(x && y);
        //- f(x || y);
        // 按位运算符：
        //- x = ~y;
        //- x = x & y;
        //- x = x | y;
        //- x = x ^ y;
        //- x = x << 1;
        //- x = x >> 1;
        //- x = x >>> 1;
        // 联合赋值：
        x += y;
        x -= y;
        x *= y;
        x /= y;
        x %= y;
        //- x <<= 1;
        //- x >>= 1;
        //- x >>>= 1;
        //- x &= y;
        //- x ^= y;
        //- x |= y;
        // 类型转换：
        //- boolean bl = (boolean)x;
        char c = (char)x;
        byte b = (byte)x;
        short s = (short)x;
        int i = (int)x;
        long l = (long)x;
        double d = (double)x;
    }

    void doubleTest(double x, double y) {
        // 算术运算符：
        x = x * y;
        x = x / y;
        x = x % y;
        x = x + y;
        x = x - y;
        x++;
        x--;
        x = +y;
        x = -y;
        // 关系和逻辑运算符：
        f(x > y);
        f(x >= y);
        f(x < y);
        f(x <= y);
        f(x == y);
        f(x != y);
        //- f(!x);
        //- f(x && y);
        //- f(x || y);
        // 按位运算符：
        //- x = ~y;
        //- x = x & y;
        //- x = x | y;
        //- x = x ^ y;
        //- x = x << 1;
        //- x = x >> 1;
        //- x = x >>> 1;
        // 联合赋值：
        x += y;
        x -= y;
        x *= y;
        x /= y;
        x %= y;
        //- x <<= 1;
        //- x >>= 1;
        //- x >>>= 1;
        //- x &= y;
        //- x ^= y;
        //- x |= y;
        // 类型转换：
        //- boolean bl = (boolean)x;
        char c = (char)x;
        byte b = (byte)x;
        short s = (short)x;
        int i = (int)x;
        long l = (long)x;
        float f = (float)x;
    }
}
```

**注意** ：**boolean** 类型的运算是受限的。你能为其赋值 `true` 或 `false`，也可测试它的值是否是 `true` 或 `false`。但你不能对其作加减等其他运算。

在 **char**，**byte** 和 **short** 类型中，我们可以看到算术运算符的“类型转换”效果。我们必须要显式强制类型转换才能将结果重新赋值为原始类型。对于 **int** 类型的运算则不用转换，因为默认就是 **int** 型。虽然我们不用再停下来思考这一切是否安全，但是两个大的 int 型整数相乘时，结果有可能超出 **int** 型的范围，这种情况下结果会发生溢出。下面的代码示例：

```java
// operators/Overflow.java
// 厉害了！内存溢出
public class Overflow {
    public static void main(String[] args) {
        int big = Integer.MAX_VALUE;
        System.out.println("big = " + big);
        int bigger = big * 4;
        System.out.println("bigger = " + bigger);
    }
}
```

输出结果：

```text
big = 2147483647
bigger = -4
```

编译器没有报错或警告，运行时一切看起来都无异常。诚然，Java 是优秀的，但是还不足够优秀。

对于 **char**，**byte** 或者 **short**，混合赋值并不需要类型转换。即使为它们执行转型操作，也会获得与直接算术运算相同的结果。另外，省略类型转换可以使代码显得更加简练。总之，除 **boolean** 以外，其他任何两种基本类型间都可进行类型转换。当我们进行向下转换类型时，需要注意结果的范围是否溢出，否则我们就很可能在不知不觉中丢失精度。

<!-- Summary -->
## 本章小结

如果你已接触过一门 C 语法风格编程语言，那么你在学习 Java 的运算符时实际上没有任何曲线。如果你觉得有难度，那么我推荐你要先去 www.OnJava8.com 观看 《Thinking in C》 的视频教程来补充一些前置知识储备。

[^1]: 我在 *Pomona College* 大学读过两年本科，在那里 47 被称之为“魔法数字”（*magic number*），详见 [维基百科](https://en.wikipedia.org/wiki/47_(number)) 。

[^2]: *John Kirkham* 说过：“自 1960 年我开始在 IBM 1620 上开始编程起，至 1970 年之间，FORTRAN 一直都是一种全大写的编程语言。这可能是因为许多早期的输入设备都是旧的电传打字机，使用了 5 位波特码，没有小写字母的功能。指数符号中的 e 也总是大写的，并且从未与自然对数底数 e 混淆，自然对数底数 e 总是小写的。 e 简单地代表指数，通常 10 是基数。那时，八进制也被程序员广泛使用。虽然我从未见过它的用法，但如果我看到一个指数符号的八进制数，我会认为它是以 8 为基数的。我记得第一次看到指数使用小写字母 e 是在 20 世纪 70 年代末，我也发现它令人困惑。这个问题出现的时候，小写字母悄悄进入了 Fortran。如果你真的想使用自然对数底，我们实际上有一些函数要使用，但是它们都是大写的。”

<!-- 分页 -->
<div style="page-break-after: always;"></div>
