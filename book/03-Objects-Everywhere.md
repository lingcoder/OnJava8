# 第三章 万物皆对象

>如果我们说不同的语言，我们会感觉到一个不同的世界！— Ludwig Wittgenstein (1889-1951)

尽管 Java 基于 C++ ，但 Java 是一种更纯粹的面向对象程序设计语言。Java 和 C++ 都是混合语言。在 Java 中，语言设计者认为混合并不像 C++ 那样重要。混合语言允许多种编程风格；这也是 C++ 支持与 C 语言的向后兼容性原因。因为 C++ 是 C 语言的超集，所以它也包含了许多 C 语言的不良特性，这可能使得 C++ 在某些方面过于复杂。

 Java 语言预设你已经编写过面对对象的程序。在此之前，你必须将自己的思维置于面对对象的世界。在本章中你将了解 Java 语言的基本组成，学习 Java （几乎）万物皆对象的思想。

<!-- You Manipulate Objects with References -->
## 对象操纵

名字的意义在于，当我们听到“玫瑰”这个词时，就会想到一种闻起来很甜蜜的的花。（引用自 莎士比亚，《罗密欧与朱丽叶》）。

所有的编程语言都会操纵内存中的元素。有时程序员必须要有意识地直接或间接地操纵它们。示例：在 C/C++ 语言中是通过指针来完成操作的。

Java 使用万物皆对象的思想和特有的语法方式来简化问题。虽然万物皆可为对象，但你操纵的标识符实际上是只对象的“引用” [^1]。 示例：我们可以将这种“引用”想象成电视（对象）和遥控器（引用）之间的关系。只要拥有对象的“引用”，就可以操纵该“对象”。我们无需直接接触电视，只要掌握遥控器就可以在房中自由地控制电视（对象）的频道和音量。此外，没有电视机，遥控器也可以单独存在。引申来说，仅仅因为你有一个“引用”并不意味着你必然有一个关联的“对象”。 

下面来创建一个 String 的引用，用于保存单词语句。代码示例：

```java
    String s;
```

这里我们仅仅只是创建了一个 String 对象的引用，而非对象。直接拿来使用会出现错误：因为此时你并没有给变量 s 赋值--附加任何引用的对象。通常更安全的做法是：在声明变量引用的同时初始化对象信息。代码示例：

```java
    String s = "asdf";
```

Java 语法允许我们使用带双引号的文本内容来初始化字符串。同样，其他类型的对象也有相应的初始化方式。

<!-- You Must Create All the Objects -->
## 对象创建

“引用”用来连接“对象”。在 Java 中，通常我们使用`new`这个操作符来来创建一个新的对象。`new`关键字代表：创建一个新的对象实例。所以，前面的代码实例我们也可以这样来表示：

```java
    String s = new String("asdf");
```
以上的代码示例展示了字符串对象的创建过程，以及如何初始化生成字符串。Java 本身自带了许多现成的数据类型，在此基础之上我们还可以创建自己的数据类型。类型的创建是 Java 的基本操作。在本书后面的学习中将会接触到。

<!-- Where Storage Lives -->
### 数据存储

那么, 程序在运行时是如何存储的呢？尤其是内存。下面我们就来形象地描述下， Java 中数据存储的5个不同的地方：

1. **寄存器** （*Registers*） 最快的保存区域，位于CPU内部 [^2]。然而，寄存器的数量十分有限，所以寄存器是根据需要由编译器分配。我们对其没有直接的控制权，也无法在自己的程序里找到寄存器存在的踪迹（另一方面，C/C++ 允许开发者向编译器建议寄存器的分配）。

2. **栈内存**（*Stack*） 存在于常规内存（RAM）区域中，可通过栈指针获得处理器的直接支持。栈指针下移创建新内存，上移释放该内存，顺序后进先出，速度仅次于寄存器。创建程序时，Java 编译器必须准确地知道栈内保存的所有数据的“长度”以及生命周期。栈内存的这种约束限制了程序的灵活性。因此，虽然在栈内存上存在一些 Java 数据，特别是对象引用，但 Java 对象本身却是保存在堆内存的。

3. **堆内存**（*Heap*） 这是一种常规用途的内存池（也在 RAM区域），所有 Java 对象都存在于其中。与栈内存不同，编译器不需要知道对象必须在堆内存上停留多长时间。因此，用堆内存保存数据更具灵活性。创建一个对象时，只需用 new 命令实例化代码即可。执行这些代码时，数据会在堆内存里自动进行保存。这种灵活性是有代价的：分配和清理堆内存要比栈内存需要更多的时间（如果你甚至可以用 Java 在栈内存上创建对象，就像在C++ 中那样）。随着时间的推移，Java 的堆内存分配机制现已非常快，因此这不是一个值得关心的问题了。

4. **常量存储** （*Constant storage*） 常量值通常直接放在程序代码中，因为它们永远不会改变。如需严格保护，可考虑将它们置于只读存储器（ROM）中 [^3]。

5. **非 RAM 存储** （*Non-RAM storage*） 数据完全存在于程序之外，在程序未运行以及脱离程序控制后依然存在。两个主要的例子：（1）序列化对象：对象被转换为字节流，通常被发送到另一台机器；（2）持久化对象：对象被放置在磁盘上，即使程序终止，数据依然存在。这些存储的方式都是将对象转存于另一个介质中，并在需要时恢复到常规内存中。Java 为轻量级持久性提供支持。诸如 JDBC 和 Hibernate 之类的库为使用数据库存储和检索对象信息提供了更复杂的支持。


<!-- Special Case: Primitive Types -->
### 基本类型的存储

有一组类型在 Java 中使用频率很高,这就是 Java 的基本类型。对于此类数据的存储我们需要特别对待。之所以说这么说，是因为它们的创建并不是通过 `new` 关键字来产生。通常 `new` 出来的对象都是保存在 **Heap** 内存中的，  用它来创建小、简单的基本类型的数据是不划算的。所以对于这些基本类型的创建方法， Java 使用了和 C/C++ 一样的策略。也就是说，不是使用 `new` 创建变量，而是使用一个“自动”变量。 这个变量容纳了具体的值，并置于栈内存中，能够更高效地存取。

Java 预设了每种基本类型的初始内存占用大小。 这些大小标准不会随着机器环境的变化而变化。这种不变性也是Java 的跨平台的一个原因。

| 基本类型  |    大小 |  最小值  | 最大值  | 包装类型 |
| :------: | :------: | :------: | :------: | :------: |
| boolean | —  | — | — | Boolean |
| char | 16 bits | Unicode 0  | Unicode $2^{16}-1$  | Character |
| byte | 8 bits | $-128$ | $+127$ | Byte |
| short | 16 bits | $-2^{15}$ | $+2^{15}-1$ | Short |
| int | 32 bits | $-2^{31}$ | $-2^{31}-1$ | Integer |
| long | 64 bits | $-2^{63}$ | $-2^{63}-1$ | Long |
| float | 32 bits | IEEE754 | IEEE754 | Float |
| double | 64 bits |IEEE754 | IEEE754 | Double |
| void | — | — | — | Void |

所有的数值类型都是有正/负符号的。布尔（boolean）类型的大小没有明确的规定，通常定义为采用文字 “true” 和 “false”。基本类型有自己对应的包装类型，如果你希望在堆内存里表示基本类型的数据，就需要用到它们的包装类。代码示例：

```java
char c = 'x';
Character ch = new Character(c);
```
或者你也可以使用下面的形式 基本类型自动转换成包装类型（自动装箱）：

```java
Character ch = new Character('x');
```
相对的，包装类型转化为基本类型（自动拆箱）：

```java
char c = ch;
```

个中原因将在以后的章节里解释。

<!-- High-Precision Numbers -->
### 高精数值的存储

在 Java 中有两种类型的数据可用于高精度的计算。它们是 `BigInteger` 和 `BigDecimal`。尽管它们大致可以划归为“包装类型”，但是它们并没有相应的基本类型形式。

这两个类都有自己特殊的“方法”，对应于我们针对基本类型数值执行的操作。也就是说，能对 int 或 float 做的运算，在 BigInteger 和 BigDecimal 这里也同样可以做一样可以，只不过必须要通过调用它们的方法来实现而非运算符。此外，由于涉及到的计算量更多，所以运算速度会慢一些。诚然，我们牺牲了速度，但换来了精度。

BigInteger 支持任意精度的整数。可用于精确表示任意大小的整数值，同时在运算过程中不会丢失精度。
BigDecimal 支持任意精度的定点数字。例如，可用它进行精确的币值计算。至于具体使用什么方法，跟多详情，请参考 JDK 官方文档。


<!-- Arrays in Java -->
### 数组的存储


许多编程语言都支持数组类型。在 C 和 C++ 中使用数组是危险的，因为那些数组只是内存块。如果程序访问了其内存块之外的数组或在初始化之前使用该段内存（常见编程错误），则结果是不可预测的。

Java 的设计主要目标之一是安全性，因此许多困扰 C 和 C++ 程序员的问题不会在 Java 中再现。在 Java 中，数组使用前需要被初始化，并且不能访问数组长度以外数据。这种长度检查的代价是每个阵列都有少量的内存开销以及在运行时验证索引的额外时间，但是这种安全性的前提对于提高的生产率是值得的。（并且 Java 经常可以优化这些操作）。

当我们创建对象数组时，实际上是创建了一个数据的引用，并且每个引用的初始值都为 **null** 。在使用该数组之前，我们必须为每个引用分配一个对象 。如果我们尝试使用为**null**的引用，则会在运行时报告该问题。因此，在 Java 中就防止了数组操作的典型错误。

我们还可创建基本类型的数组。编译器通过将该数组的内存归零来保证初始化。本书稍后将详细介绍数组，特别是在数组章节中。

  [^1]: 脚注预留
  [^2]: 脚注预留
  [^3]: 脚注预留

<!-- Comments -->
## 代码注释


Java 中有两种类型的注释。第一种是传统的 C 风格的注释，以 `/*` 开头，可以跨越多行，到 `*/ ` 结束。**注意**，许多程序员在多行注释的每一行开头添加 `*`，所以你经常会看到：

```java
/* This is a comment
* that continues
* across lines
*/
```

但请记住， `/*` 和 `*/` 之间的内容都是被忽略的。所以你将其改为下面的风格也是没有区别的。

```JAVA
/* This is a comment that
continues across lines */
```

第二种注释形式来自 C++ 。它是单行注释，以 `//` 开头并一直持续到行结束。这种注释方便且常用，因为它很直观和简单。所以你经常看到：

```JAVA
// 这是单行注释
```

<!-- You Never Need to Destroy an Object -->
## 对象清理

在一些编程语言中，管理存储的生命周期需要大量的工作。一个变量需要存续多久？如果我们想销毁它，应该什么时候去做呢？存储生命周期的混乱会导致许多错误，本小结将会向你介绍 Java 是如何通过释放存储来简化这个问题的。

<!-- Scoping -->
### 作用域

大多数程序语言都有作用域的概念。这将确定在该范围内定义的名称的可见性和生存周期。在 C、C++ 和 Java 中，作用域是由大括号 `{}` 的位置决定的。下面是 Java 代码作用域的一个示例：

```JAVA
{
    int x = 12;
// 仅 x 变量可用
{
    int q = 96;
// x 和 q 变量皆可用
}
// 仅 x 变量可用
// 变量 q 不在作用域内
}
```

Java 的变量只有在其作用域内才可用。缩进使得 Java 代码更易于阅读。由于 Java 是一种自由形式的语言，额外的空格、制表符和回车并不会影响程序的生成结果。在 Java 中，你不能执行以下操作，即使这在 C 和 C++ 中是合法的：

```JAVA
{
    int x = 12;
    {
        int x = 96; // Illegal
    }
}
```

在上例中， Java 便编译器会在提示变量 x 已经被定义过了。因此，在 C 和 C++ 中可以于更大作用域中“隐藏”变量的能力在 Java 中是不被允许的。 因为 Java 的设计者认为这样的定义会混淆编程。

<!-- Scope of Objects -->
### 对象作用域

Java 对象与基本类型具有不同的生命周期。当我们使用 `new` 关键字来创建 Java 对象时，它的存续将会超出作用域的末尾。因此，下面这段代码示例

```JAVA
{
    String s = new String("a string");
} 
// 作用域结束
```
上例中，变量 s 的范围在标注的地方结束了。但是，引用的字符串对象依然还在占用内存。在这段代码中，变量 s 的唯一引用超出了作用域，因此它无法在作用域外被访问。在后面的章节中，你将学习到如何在编程过程中传递和复制对象的引用。

只要你还需要它们，这些用 `new` 关键字创建出来的对象就不会消失。因此，这个在 C++ 编程中大量存在的问题在 Java 中消失了。在 C++ 中你不仅要确保对象在你操作的范围内存在，而且还必须在完成后销毁对象。

随之而来的问题：如果 Java 并没有清理周围的这些对象，那么它是如何避免 C++ 中出现的内存泄漏和程序终止的问题呢？答案是：Java 的垃圾收集器会查找所有 `new` 出来的对象并判断哪些不再可达。然后释放那些对象所占用的内存。这意味着我们不必再需要自己操作回收内存了。我们只需要简单的创建对象，当这些对象不再被需要时，它们能自行被垃圾收集器释放。
。这意味着您不必担心回收内存你自己。您只需创建对象，当您不再需要时他们，他们自己消失。这可以防止重要的一类编程问题：所谓的“内存泄漏”时程序员忘记释放内存。

这就有效地防止了一类重要的编程问题：因程序员忘记释放内存而造成的“内存泄漏”。

<!-- Creating New Data Types: class -->
## 类的创建

### 类型

如果一切都是对象，那么我们用什么来表示对象类的具体展现和行为呢？顾名思义，你可能很自然地想到 `type` 关键字。但是，事实上大多数面向对象的语言都使用 `class` 关键字类来描述一种新的对象。 通常在 `class` 关键字的后面的紧跟类的的名称。如下代码示例：

```java
 class ATypeName {
 // 这里是类的内部
}
```

在上例中，我们介绍了如何创建一个新的类型，尽管这个类里只有一行注释。但是我们一样可以通过 `new` 关键字来创建一个对象。如下：

```JAVA
ATypeName a = new ATypeName();
```

到现在为止，我们还不能用这个对象来做什么事，比如发送一些有趣的信息啊。除非我们在这个类里定义一些方法。

<!-- Fields -->
### 属性

当我们创建好了一个类之后，我们可以往类里存放两种类型的元素。方法（**method**）和属性（**field**）。类的属性可以是基本类型。如果类的属性是对象的话，那么必须要初始化该引用将其关联到一个实际的对象上（通过之前介绍的创建对象的方法）。每个对象都都会为其属性保留独立的存储空间。通常，属性不再对象之间做共享。下面是一个包含部分属性的类的代码示例：

```JAVA
class DataOnly {
    int i;
    double d;
    boolean b;
}
```

除非持有数据，不然这个类不能做任何是。在此之前，我们可以通过下面的代码示例来创建它的对象：

```JAVA
    DataOnly data = new DataOnly();
```

我们必须通过这个对象的引用来指定属性值。格式：对象名称.方法名称或成员名称。代码示例：

```JAVA
    data.i = 47;
    data.d = 1.1;
    data.b = false;
```

如果你想修改对象内部包含的另一个对象的数据，可以通过这样的格式修改。代码示例：

```JAVA
    myPlane.leftTank.capacity = 100;
```

你可以用这种方式嵌套许多对象（尽管这样的设计会带来混淆）。


<!-- Default Values for Primitive Members -->
### 基本类型默认值

如果类的成员变量（属性）是基本类型，那么在类初始化时，这些类型将会被赋予一个初始值。

| 基本类型 | 初始值 |
| :-----: |:-----: |
| boolean | false |
| char | \u0000 |
| byte | 0 |
| short |0 |
| int | 0 |
| long | 0L |
| float | 0.0f |
| double | 0.0d |

这些默认值仅在 Java 初始化类的时候才会被赋予。这种方式确保了基本类型的属性始终能被初始化（在C++ 中不会），从而减少了 bug 的来源。但是，这些初始值对于程序来说并不一定是合法或者正确的。 所以,为了安全，我们最好始终显式地初始化变量。

这种默认值的赋予并不适用于局部变量 ---- 那些不属于类的属性的变量。 因此，若在方法中定义的基本类型数据，如下：

```JAVA
    int x;
```

这里的变量 x 不会自动初始化为0，因而在使用变量 x 之前，程序员有责任主动地为其赋值（和 C 、C++ 一致）。如果我们忘记了这一步，在 JAVA 中将会提示我们“编译时错误，该变量尚未被初始化”。 这一点做的比 C++ 要更好，在后者中，编译器只是提示警告，而在 JAVA 中则直接报错。


<!-- Methods, Arguments,and Return Values -->
### 方法使用

在许多语言（如 C 和 C++）中，术语函数（**function**）用于描述命名子程序。在 Java 中，我们使用术语方法（**method**）来表示“做某事的方式”。

在 JAVA 中，方法决定着对象对象能接收哪些信息。方法的基础部分包含，名称、参数、返回类型、方法体。格式如：

```java
 [返回类型][方法名](/*参数列表*/){
      // 方法体
 }
```

方法的返回类型表明了当你调用它时会返回的结果类型。参数列表则显示了可被传递到方法内部的参数类型及名称。方法的名称和参数列表被统称为**方法签名**（**signature of the method**）。签名作为方法的唯一性标识。

Java 中的方法只能作为类的一部分创建。它只能被对象所调用，并且该对象必须有权限来执行调用。若对象调用错误的方法，则程序将在编译时报错。

我们可以通过在对象名的后面跟上 `.` 符号+方法名及其参数来调用一个方法。代码示例：

```JAVA
[对象引用].[方法名](参数1, 参数2, 参数3);
```

若方法不带参数，例如一个对象 a 的方法 f 不带参数并返回 int 型结果，我们可以如下表示。代码示例：

```JAVA
int x = a.f();
```

上例中方法 f 的返回值必须兼容接收的变量 x 。这种调用方法的行为有时被称为向对象传递信息。面向对象编程可以被总结为：向对象传递信息。

<!-- The Argument List -->
**参数列表**

方法参数列表指定传递给方法的信息。正如你可能猜到的，这些信息 - 就像 Java 中的其他所有信息 - 采用对象的形式。参数列表必须指定对象类型和每个对象的名称。同样，我们并没有直接处理对象，而是在传递对象引用。但是引用的类型必须是正确的。如果方法需要 String 参数，则必须传入 String，否则编译器将报错。

```JAVA
int storage(String s) {

    return s.length() * 2;
}
```

此方法计算并返回某个字符串的长度。参数 s 的类型为 String 。将 字符串变量 s 传递给 storage() 后，我们可以将其视为任何其他对象一样 ---- 我们可以想起传递信息。在这里，我们调用 length() 方法，它是一个 String 方法，返回字符串长度。字符串中每个字符的大小为16位或两个字节。您还可以看到 **return** 关键字，它执行两项操作。首先，它意味着“方法执行结束”。其次，如果方法有返回值，那么该值就位于 **return** 语句之后。这里，返回值是通过计算

```JAVA
s.length() * 2
```
产生的。在方法中，我们可以返回任意的数据。如果我们不想方法返回什么数据，则可以通过给方法标识 `void` 来表明这是一个无需返回值的方法。

代码示例：

```JAVA
boolean flag() { 
    return true; 
    }

double naturalLogBase() { 
    return 2.718; 
    }

void nothing() {
     return;
      }

void nothing2() {

}
```

当返回类型为 **void** 时， **return** 关键字仅用于退出方法，因此在方法结束处的 **return** 可被省略。我们可以随时从方法中返回。若方法返回类型为非 `void`，则编译器会强制返回相应类型的值。

上面的描述可能会让你感觉程序只不过是一堆包含各种方法的对象，将对象作为方法参数来传递信息给其他的对象。从表面上来看的确如此。但在下一章的运算符中我们将会学习如何在方法中做出决策来完成更底层、详细的工作。对于本章，知道如何传递信息就够了。


<!-- Writing a Java Program -->
## 程序编写

There are several other issues you must understand before seeing your
first Java program.
Name Visibility
A problem in any programming language is the control of names. If
you use a name in one module of the program, and another
programmer uses the same name in another module, how do you
distinguish one name from another and prevent the two names from
“clashing?” In C this is especially challenging because a program is
often an unmanageable sea of names. C++ classes (on which Java
classes are modeled) nest functions within classes so they cannot clash
with function names nested within other classes. However, C++
continues to allow global data and global functions, so clashing is still
possible. To solve this problem, C++ introduced namespaces using
additional keywords.
Java avoided all these problems by taking a fresh approach. To
produce an unambiguous name for a library, the Java creators want
you to use your Internet domain name in reverse, because domain
names are guaranteed to be unique. Since my domain name is
MindviewInc.com, my foibles utility library is named
com.mindviewinc.utility.foibles. Following your
reversed domain name, the dots are intended to represent
subdirectories.
In Java 1.0 and Java 1.1 the domain extensions com, edu, org, net, etc., were
capitalized by convention, so the library would appear:
Com.mindviewinc.utility.foibles. Partway through the
development of Java 2, however, they discovered this caused
problems, so now the entire package name is lowercase.
This mechanism means all your files automatically live in their own
namespaces, and each class within a file has a unique identifier. This
way, the language prevents name clashes.
Using reversed URLs was a new approach to namespaces, never before
tried in another language. Java has a number of these “inventive”
approaches to problems. As you might imagine, adding a feature
without experimenting with it first risks discovering problems with
that feature in the future, after the feature is used in production code,
typically when it’s too late to do anything about it (some mistakes were
bad enough to actually remove things from the language).
The problem with associating namespaces with file paths using
reversed URLs is by no means one that causes bugs, but it does make
it challenging to manage source code. By using
com.mindviewinc.utility.foibles, I create a directory
hierarchy with “empty” directories com and mindviewinc whose
only job is to reflect the reversed URL. This approach seemed to open
the door to what you will encounter in production Java programs:
deep directory hierarchies filled with empty directories, not just for the
reversed URLs but also to capture other information. These long paths
are basically being used to store data about what is in the directory. If
you expect to use directories in the way they were originally designed,
this approach lands anywhere from “frustrating” to “maddening,” and
for production Java code you are essentially forced to use one of the
IDEs specifically designed to manage code that is laid out in this
fashion, such as NetBeans, Eclipse, or IntelliJ IDEA. Indeed, those
IDEs both manage and create the deep empty directory hierarchies for
you.
For this book’s examples, I didn’t want to burden you with the extra
annoyance of the deep hierarchies, which would have effectively
required you to learn one of the big IDEs before getting started. The
examples for each chapter are in a shallow subdirectory with a name
reflecting the chapter title. This caused me occasional struggles with
tools that follow the deep-hierarchy approach.
Using Other Components
Whenever you use a predefined class in your program, the compiler
must locate that class. In the simplest case, the class already exists in
the source-code file it’s being called from. In that case, you simply use
the class—even if the class doesn’t get defined until later in the file
(Java eliminates the so-called “forward referencing” problem).
What about a class that exists in some other file? You might think the
compiler should be smart enough to go and find it, but there is a
problem. Imagine you use a class with a particular name, but more
than one definition for that class exists (presumably these are different
definitions). Or worse, imagine that you’re writing a program, and as
you’re building it you add a new class to your library that conflicts with
the name of an existing class.
To solve this problem, you must eliminate all potential ambiguities by
telling the Java compiler exactly what classes you want using the
import keyword. import tells the compiler to bring in a package,
which is a library of classes. (In other languages, a library could
consist of functions and data as well as classes, but remember that all
activities in Java take place within classes.)
Much of the time you’ll use components from the standard Java
libraries that come with your compiler. With these, you don’t worry
about long, reversed domain names; you just say, for example:
import java.util.ArrayList;
This tells the compiler to use Java’s ArrayList class, located in its
util library.
However, util contains a number of classes, and you might want to
use several of them without declaring them all explicitly. This is easily
accomplished by using * to indicate a wild card:
import java.util.*;
The examples in this book are small and for simplicity’s sake we’ll
usually use the * form. However, many style guides specify that each
class should be individually imported.
The static Keyword
Creating a class describes the look of its objects and the way they
behave. You don’t actually get an object until you create one using
new, at which point storage is allocated and methods become
available.
This approach is insufficient in two cases. Sometimes you want only a
single, shared piece of storage for a particular field, regardless of how
many objects of that class are created, or even if no objects are created.
The second case is if you need a method that isn’t associated with any
particular object of this class. That is, you need a method you can call
even if no objects are created.
The static keyword (adopted from C++) produces both these
effects. When you say something is static, it means the field or
method is not tied to any particular object instance. Even if you’ve
never created an object of that class, you can call a static method or
access a static field. With ordinary, non-static fields and
methods, you must create an object and use that object to access the
field or method, because non-static fields and methods must target
a particular object.6
Some object-oriented languages use the terms class data and class
methods, meaning that the data and methods exist only for the class as
a whole, and not for any particular objects of the class. Sometimes
Java literature uses these terms too.
To make a field or method static, you place the keyword before the
definition. The following produces and initializes a static field:
class StaticTest {
static int i = 47;
}
Now even if you make two StaticTest objects, there is still only
one piece of storage for StaticTest.i. Both objects share the same
i. For example:
StaticTest st1 = new StaticTest();
StaticTest st2 = new StaticTest();
Both st1.i and st2.i have the same value of 47 since they are the
same piece of memory.
There are two ways to refer to a static variable. As in the preceding
example, you can name it via an object; for example, st2.i. You can
also refer to it directly through its class name, something you cannot
do with a non-static member:
StaticTest.i++;
The ++ operator adds one to the variable. Now both st1.i and
st2.i have the value 48.
Using the class name is the preferred way to refer to a static
variable because it emphasizes the variable’s static nature7.
Similar logic applies to static methods. You can refer to a static
method either through an object as you can with any method, or with
the special additional syntax ClassName.method(). You define a
static method like this:
class Incrementable {
static void increment() { StaticTest.i++; }
}
The Incrementable method increment() increments the
static int i using the ++ operator. You can call increment()
in the typical way, through an object:
Incrementable sf = new Incrementable();
sf.increment();
However, the preferred approach is to call it directly through its class:
Incrementable.increment();
static applied to a field definitely changes the way the data is
created—one for each class versus the non-static one for each
object. When applied to a method, static allows you to call that
method without creating an object. This is essential, as you will see, in
defining the main() method that is the entry point for running an
application.

<!-- Your First Java Program -->
## 小试牛刀

Finally, here’s the first complete program. It starts by displaying a
String, followed by the date, using the Date class from the Java
standard library.
// objects/HelloDate.java
import java.util.*;
public class HelloDate {
public static void main(String[] args) {
System.out.println("Hello, it's: ");
System.out.println(new Date());
}
}
In this book I treat the first line specially; it’s always a comment line
containing the the path information to the file (using the directory
name objects for this chapter) followed by the file name. I have
tools to automatically extract and test the book’s code based on this
information, and you will easily find the code example in the
repository by referring to the first line.
At the beginning of each program file, you must place import
statements to bring in any extra classes you need for the code in that
file. I say “extra” because there’s a certain library of classes
automatically included in every Java file: java.lang. Start up your
Web browser and look at the documentation from Oracle. If you
haven’t downloaded the JDK documentation from the Oracle Java site, do so
now8, or find it on the Internet. If you look at the list of packages, you’ll see
all the different class libraries that come with Java.
Select java.lang. This will bring up a list of all the classes that are
part of that library. Since java.lang is implicitly included in every
Java code file, these classes are automatically available. There’s no
Date class listed in java.lang, which means you must import
another library to use that. If you don’t know the library where a
particular class is, or if you want to see all classes, select “Tree” in the
Java documentation. Now you can find every single class that comes
with Java. Use the browser’s “find” function to find Date. You’ll see it
listed as java.util.Date, which tells you it’s in the util library
and you must import java.util.* in order to use Date.
If inside the documentation you select java.lang, then System,
you’ll see that the System class has several fields, and if you select
out, you’ll discover it’s a static PrintStream object. Since it’s
static, you don’t need to use new—the out object is always there,
and you can just use it. What you can do with this out object is
determined by its type: PrintStream. Conveniently,
PrintStream is shown in the description as a hyperlink, so if you
click on that, you’ll see a list of all the methods you can call for
PrintStream. There are quite a few, and these are covered later in
the book. For now all we’re interested in is println(), which in
effect means “Print what I’m giving you out to the console and end
with a newline.” Thus, in any Java program you can write something
like this:
System.out.println("A String of things");
whenever you want to display information to the console.
One of the classes in the file must have the same name as the file. (The
compiler complains if you don’t do this.) When you’re creating a
standalone program such as this one, the class with the name of the
file must contain an entry point from which the program starts. This
special method is called main(), with the following signature and
return type:
public static void main(String[] args) {
The public keyword means the method is available to the outside
world (described in detail in the Implementation Hiding chapter). The
argument to main() is an array of String objects. The args won’t
be used in the current program, but the Java compiler insists they be
there because they hold the arguments from the command line.
The line that prints the date is interesting:
System.out.println(new Date());
The argument is a Date object that is only created to send its value
(automatically converted to a String) to println(). As soon as
this statement is finished, that Date is unnecessary, and the garbage
collector can come along and get it anytime. We don’t worry about
cleaning it up.
When you look at the JDK documentation, you see that System has
many other useful methods (one of Java’s assets is its large set of
standard libraries). For example:
// objects/ShowProperties.java
public class ShowProperties {
public static void main(String[] args) {
System.getProperties().list(System.out);
System.out.println(System.getProperty("user.name"));
System.out.println(
System.getProperty("java.library.path"));
}
}
/* Output: (First 20 Lines)
-- listing properties --
java.runtime.name=Java(TM) SE Runtime Environment
sun.boot.library.path=C:\Program
Files\Java\jdk1.8.0_112\jr...
java.vm.version=25.112-b15
java.vm.vendor=Oracle Corporation
java.vendor.url=http://java.oracle.com/
path.separator=;
java.vm.name=Java HotSpot(TM) 64-Bit Server VM
file.encoding.pkg=sun.io
user.script=
user.country=US
sun.java.launcher=SUN_STANDARD
sun.os.patch.level=
java.vm.specification.name=Java Virtual Machine
Specification
user.dir=C:\Users\Bruce\Documents\GitHub\on-ja...
java.runtime.version=1.8.0_112-b15
java.awt.graphicsenv=sun.awt.Win32GraphicsEnvironment
java.endorsed.dirs=C:\Program
Files\Java\jdk1.8.0_112\jr...
os.arch=amd64
java.io.tmpdir=C:\Users\Bruce\AppData\Local\Temp\
...
*/
The first line in main() displays all “properties” from the system
where you are running the program, so it gives you environment
information. The list() method sends the results to its argument,
System.out. You will see later in the book that you can send the
results elsewhere, to a file, for example. You can also ask for a specific
property—here, user.name and java.library.path.
The /* Output: tag at the end of the file indicates the beginning of
the output generated by this file. Most examples in this book that
produce output will contain the output in this commented form, so
you see the output and know it is correct. The tag allows the output to
be automatically updated into the text of this book after being checked
with a compiler and executed.
Compiling and Running
To compile and run this program, and all the other programs in this
book, you must first have a Java programming environment. The
installation process was described in Installing Java and the Book
Examples. If you followed these instructions, you are using the Java
Developer’s Kit (JDK), free from Oracle. If you use another
development system, look in the documentation for that system to
determine how to compile and run programs.
Installing Java and the Book Examples also describes how to install the
examples for this book. Move to the subdirectory named
objects and type:
javac HelloDate.java
This command should produce no response. If you get any kind of an
error message, it means you haven’t installed the JDK properly and
you must investigate those problems.
On the other hand, if you just get your command prompt back, you can
type:
java HelloDate
and you’ll get the message and the date as output.
This is the process to compile and run each program (containing a
main()) in this book9. However, the source code for this book also has a file
called build.gradle in the root directory, and this
contains the Gradle configuration for automatically building, testing,
and running the files for the book. When you run the gradlew
command for the first time, Gradle will automatically install itself
(assuming you have Java installed).

<!-- Coding Style -->
## 编码风格

The style described in the document Code Conventions for the Java
Programming Language 10 is to capitalize the first letter of a class name. If
the class name consists of several words, they are run
together (that is, you don’t use underscores to separate the names),
and the first letter of each embedded word is capitalized, such as:
class AllTheColorsOfTheRainbow { // ...
This is sometimes called “camel-casing.” For almost everything else—
methods, fields (member variables), and object reference names—the
accepted style is just as it is for classes except that the first letter of the
identifier is lowercase. For example:
class AllTheColorsOfTheRainbow {
int anIntegerRepresentingColors;
void changeTheHueOfTheColor(int newHue) {
// ...
}
// ...
}
The user must also type these long names, so be merciful.
The Java code you find in the Oracle libraries also follows the
placement of open-and-close curly braces in this book.

## 本章小结

This chapter shows you just enough Java so you understand how to
write a simple program. You’ve also seen an overview of the language
and some of its basic ideas. However, the examples so far have all been
of the form “Do this, then do that, then do something else.” The next
two chapters will introduce the basic operators used in Java
programming, and show you how to control the flow of your program.
1. This can be a flashpoint. There are those who say, “Clearly, it’s a
pointer,” but this presumes an underlying implementation. Also,
the syntax of Java references is much more akin to C++ references
than to pointers. In the 1st edition of Thinking in Java, I chose to
invent a new term, “handle,” because C++ references and Java
references have some important differences. I was coming out of
C++ and did not want to confuse the C++ programmers whom I
assumed would be the largest audience for Java. In the 2nd
edition of Thinking in Java, I decided that “reference” was the
more commonly used term, and that anyone changing from C++
would have a lot more to cope with than the terminology of
references, so they might as well jump in with both feet. However,
there are people who disagree even with the term “reference.” In
one book I read that it was “completely wrong to say that Java
supports pass by reference,” because Java object identifiers
(according to that author) are actually “object references.” And
(he goes on) everything is actually pass by value. So you’re not
passing by reference, you’re “passing an object reference by
value.” One could argue for the precision of such convoluted
explanations, but I think my approach simplifies the
understanding of the concept without hurting anything (well,
language lawyers may claim I’m lying to you, but I’ll say that I’m
providing an appropriate abstraction).↩
2. Most microprocessor chips do have additional cache memory but
this is organized as traditional memory and not as registers↩
3. An example of this is the String pool. All literal Strings and
String-valued constant expressions are interned automatically
and put into special static storage. ↩
4. static methods, which you’ll learn about soon, can be called for
the class, without an object.↩
5. With the usual exception of the aforementioned “special” data
types boolean, char, byte, short, int, long, float, and double. In general,
though, you pass objects, which really means
you pass references to objects. ↩
6. static methods don’t require objects to be created before they
are used, so they cannot directly access non-static members or
methods by calling those other members without referring to a
named object (since non-static members and methods must be
tied to a particular object).↩
7. In some cases it also gives the compiler better opportunities for
optimization↩
8. Note this documentation doesn’t come packed with the JDK; you
must do a separate download to get it. ↩
9. For every program in this book to compile and run through the
command line, you might also need to set your CLASSPATH. ↩
10. (Search the Internet; also look for “Google Java Style”). To keep
code listings narrow for this book, not all these guidelines could
be followed, but you’ll see that the style I use here matches the
Java standard as much as possible.↩
<!-- 分页 -->
<div style="page-break-after: always;"></div>

