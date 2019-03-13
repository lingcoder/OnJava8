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

“引用”用来连接“对象”。在 Java 中，通常我们使用`new`这个操作符来创建一个新的对象。`new`关键字代表：创建一个新的对象实例。所以，前面的代码实例我们也可以这样来表示：

```java
    String s = new String("asdf");
```
以上的代码示例展示了字符串对象的创建过程，以及如何初始化生成字符串。Java 本身自带了许多现成的数据类型，在此基础之上我们还可以创建自己的数据类型。类型的创建是 Java 的基本操作。在本书后面的学习中将会接触到。

<!-- Where Storage Lives -->
### 数据存储

那么, 程序在运行时是如何存储的呢？尤其是内存。下面我们就来形象地描述下， Java 中数据存储的5个不同的地方：

1. **寄存器** （*Registers*） 最快的保存区域，位于CPU内部 [^2]。然而，寄存器的数量十分有限，所以寄存器是根据需要由编译器分配。我们对其没有直接的控制权，也无法在自己的程序里找到寄存器存在的踪迹（另一方面，C/C++ 允许开发者向编译器建议寄存器的分配）。

2. **栈内存**（*Stack*） 存在于常规内存（RAM）区域中，可通过栈指针获得处理器的直接支持。栈指针下移创建新内存，上移释放该内存，顺序后进先出，速度仅次于寄存器。创建程序时，Java 编译器必须准确地知道栈内保存的所有数据的“长度”以及生命周期。栈内存的这种约束限制了程序的灵活性。因此，虽然在栈内存上存在一些 Java 数据，特别是对象引用，但 Java 对象本身却是保存在堆内存的。

3. **堆内存**（*Heap*） 这是一种常规用途的内存池（也在 RAM区域），所有 Java 对象都存在于其中。与栈内存不同，编译器不需要知道对象必须在堆内存上停留多长时间。因此，用堆内存保存数据更具灵活性。创建一个对象时，只需用 `new` 命令实例化代码即可。执行这些代码时，数据会在堆内存里自动进行保存。这种灵活性是有代价的：分配和清理堆内存要比栈内存需要更多的时间（如果你甚至可以用 Java 在栈内存上创建对象，就像在C++ 中那样）。随着时间的推移，Java 的堆内存分配机制现已非常快，因此这不是一个值得关心的问题了。

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
BigDecimal 支持任意精度的定点数字。例如，可用它进行精确的币值计算。至于具体使用什么方法，更多详情，请参考 JDK 官方文档。


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

Java 对象与基本类型具有不同的生命周期。当我们使用 `new` 关键字来创建 Java 对象时，它的生命周期将会超出作用域的末尾。因此，下面这段代码示例

```JAVA
{
    String s = new String("a string");
} 
// 作用域结束
```
上例中，变量 s 的范围在标注的地方结束了。但是，引用的字符串对象依然还在占用内存。在这段代码中，变量 s 的唯一引用超出了作用域，因此它无法在作用域外被访问。在后面的章节中，你将学习到如何在编程过程中传递和复制对象的引用。

只要你还需要它们，这些用 `new` 关键字创建出来的对象就不会消失。因此，这个在 C++ 编程中大量存在的问题在 Java 中消失了。在 C++ 中你不仅要确保对象在你操作的范围内存在，而且还必须在完成后销毁对象。

随之而来的问题：如果 Java 并没有清理周围的这些对象，那么它是如何避免 C++ 中出现的内存泄漏和程序终止的问题呢？答案是：Java 的垃圾收集器会查找所有 `new` 出来的对象并判断哪些不再可达。然后释放那些对象所占用的内存。这意味着我们不必再需要自己操作回收内存了。我们只需要简单的创建对象，当这些对象不再被需要时，它们能自行被垃圾收集器释放。这意味着您不必担心回收内存。您只需创建对象，当您不再需要时他们，他们自己消失。
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

当我们创建好了一个类之后，我们可以往类里存放两种类型的元素。方法（**method**）和属性（**field**）。类的属性可以是基本类型。如果类的属性是对象的话，那么必须要初始化该引用将其关联到一个实际的对象上（通过之前介绍的创建对象的方法）。每个对象都会为其属性保留独立的存储空间。通常，属性不再对象之间做共享。下面是一个包含部分属性的类的代码示例：

```JAVA
class DataOnly {
    int i;
    double d;
    boolean b;
}
```

除非持有数据，不然这个类不能做任何事。在此之前，我们可以通过下面的代码示例来创建它的对象：

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

这些默认值仅在 Java 初始化类的时候才会被赋予。这种方式确保了基本类型的属性始终能被初始化（在C++ 中不会），从而减少了 bug 的来源。但是，这些初始值对于程序来说并不一定是合法或者正确的。 所以，为了安全，我们最好始终显式地初始化变量。

这种默认值的赋予并不适用于局部变量 —— 那些不属于类的属性的变量。 因此，若在方法中定义的基本类型数据，如下：

```JAVA
    int x;
```

这里的变量 x 不会自动初始化为0，因而在使用变量 x 之前，程序员有责任主动地为其赋值（和 C 、C++ 一致）。如果我们忘记了这一步，在 JAVA 中将会提示我们“编译时错误，该变量尚未被初始化”。 这一点做的比 C++ 要更好，在后者中，编译器只是提示警告，而在 JAVA 中则直接报错。


<!-- Methods, Arguments,and Return Values -->
### 方法使用

在许多语言（如 C 和 C++）中，术语函数（**function**）用于描述命名子程序。在 Java 中，我们使用术语方法（**method**）来表示“做某事的方式”。

在 JAVA 中，方法决定着对象能接收哪些信息。方法的基础部分包含名称、参数、返回类型、方法体。格式如：

```java
 [返回类型][方法名](/*参数列表*/){
      // 方法体
 }
```

#### 返回类型

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
#### 参数列表

方法参数列表指定传递给方法的信息。正如你可能猜到的，这些信息 —— 就像 Java 中的其他所有信息 —— 采用对象的形式。参数列表必须指定对象类型和每个对象的名称。同样，我们并没有直接处理对象，而是在传递对象引用。但是引用的类型必须是正确的。如果方法需要 String 参数，则必须传入 String，否则编译器将报错。

```JAVA
int storage(String s) {

    return s.length() * 2;
}
```

此方法计算并返回某个字符串的长度。参数 s 的类型为 String 。将 字符串变量 s 传递给 storage() 后，我们可以将其视为任何其他对象一样 —— 我们可以想起传递信息。在这里，我们调用 length() 方法，它是一个 String 方法，返回字符串长度。字符串中每个字符的大小为16位或两个字节。您还可以看到 **return** 关键字，它执行两项操作。首先，它意味着“方法执行结束”。其次，如果方法有返回值，那么该值就位于 **return** 语句之后。这里，返回值是通过计算

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

在看到第一个 Java 程序之前，我们还必须了解其他几个问题。

#### 命名可见性

命名控制在任何一门编程语言中都是一个问题。如果程序员在两个模块中使用相同的命名，那么如何区分这两个名称，并防止两个名称发生“冲突”呢？在 C 语言编程中这是很具有挑战性的，因为程序通常是一个无法管理的名称海洋。C++ 将函数嵌套在类中，它们不能与嵌套在其他类中的函数名冲突。然而，C++ 继续允许全局数据和全局函数，因此仍有可能发生冲突。为了解决这个问题，C++ 使用附加的关键字引入命名空间。

Java 采取了一种新的方法避免了以上这些问题：为一个库生成一个明确的名称，Java 创建者希望我们反向使用自己的网络域名，因为域名通常是唯一的。自从我的域名为 MindviewInc.com 开始，我就将我的 foibles 工具库命名为 com.mindviewinc.utility.foibles。根据你的域名的反向信息，`.`代表着一个子目录。

在 Java 1.0和 Java 1.1中，域扩展 com，edu，org，net 等按惯例大写，因此类库中会出现这样类似的名称：Com.mindviewinc.utility.foibles。然而，在 Java 2 的开发过程中，他们发现这会导致问题，所以现在整个包名都是小写的。此机制意味着所有文件都自动存在于自己的命名空间中，文件中的每个类都具有唯一标识符。这样，该语言可以防止名称冲突。

使用反向 URL 是一种新的命名空间方法，在此之前尚未有其他语言这么做过。Java 中有许多这些“创造性”地解决问题的方法。正如你想象，如果我们未经测试就添加一个功能并用于生产，那么在将来发现该功能的问题再想纠正，通常为时已晚。（有些问题错误得足以从语言中删除。）

使用反向 URL 将命名空间与文件路径相关联不会导致BUG，但它却给源代码管理带来麻烦。例如在 com.mindviewinc.utility.foiles 这样的目录结构中，我们创建了com、mindviewinc 空目录。它们存在的唯一目的就是用来表示这个反向的 URL。

这种方式似乎为我们在编写 Java 程序中的某个问题打开了大门。空目录填充了深层次结构，它们不仅用于表示反向 URL，还用于捕获其他信息。这些长路径基本上用于存储有关目录中的内容的数据。如果你希望以最初设计的方式使用目录，这种方法可以从“令人沮丧”到“令人抓狂”，对于产生的 Java 代码，你基本上不得不使用专门为此设计的 IDE 来管理代码。例如 NetBeans，Eclipse 或 IntelliJ IDEA。实际上，这些 IDE 都为我们管理和创建深度空目录层次结构。

对于这本书中的例子，我不想让深层次的层次结构给你的学习带来额外的麻烦，这实际上需要你在开始之前学习熟悉一种重量级的 IDE。所以，我们的每个章节的示例都位于一个浅的子目录中，以章节标题为名。这导致我偶尔会与遵循深度层次方法的工具发生冲突。

<!-- Using Other Components -->
#### 使用其他组件


无论何时在程序中使用预定义的类，编译器都必须找到该类。在一般情况下，该类已存在于被调用的源代码文件中。此时我们使用该类 —— 即使该类未在文件中稍后定义（Java 消除了所谓的“前向引用”问题）。而那些存在于其他文件中的类怎么样？你可能认为编译器应该足够智能去找到它，但这样是有问题的。想象一下，假如你要使用某个类，但目录中存在多个同名的类（可能用途不同）。或者更糟糕的是，假设你正在编写程序，并且在构建它时，你将向库中添加一个与现有类名称冲突的新类。

要解决此问题，你必须通过使用 `import` 关键字来告诉 Java 编译器具体要使用的类。import 表示编译器引入一个包，它是一个类库。（在其他语言中，库可以包含函数和数据以及类，但请记住，Java 中的所有活动都在类中进行。）大多数时候，我们都在使用 JAVA 标准库中的组件。有了这些，你不用担心长的反向域名。你只用说，例如：

```JAVA
import java.util.ArrayList;
```

上例可以告诉编译器使用位于标准库 util 下的 ArrayList 类。在 util 包含许多类，我们可以使用通配符 `*` 来导入其中部分类，无需显式导入。代码示例：

```JAVA
import java.util.*;
```

本书中的示例很小，为简单起见，我们通常会使用 `.*` 形式略过导入。然而，许多教程书籍都会要求程序员单独导入每个类。 


<!-- The static Keyword -->
#### static关键字

类是对象的外观及行为方式的描述。通常只有在使用 `new` 关键字之后程序才能被分配存储空间以及使用其方法。

这种方式在两种情况下是不足的。1. 有时你只需要为特定字段分配一个共享存储空间，无论该类创建了多少个对象，或者即使没有创建任何对象；第二种情况是，创建一个与此类本身任何对象无关的方法。也就是说，即使没有创建对象，也能调用该方法。

**static** 关键字（从 C++ 采用）就符合我们的要求。当我们说某些东西是静态的时，它意味着该字段或方法不依赖于任何特定的对象实例。即使我们从未创建过该类的对象，也可以调用其静态方法或访问静态字段。相反，对于普通的非静态字段和方法，我们必须要先创建一个对象并使用该对象来访问该字段或方法，因为非静态字段和方法必须与替对象关联.

一些面向对象的语言使用类数据（**class data**）和类方法（**class method**）这样的术语来表述静态。静态的数据意味着该数据和方法仅存在于类中，而非类的任何实例对象中。有时 Java 文献也使用这些术语。我们可以通过在类的属性或方法前添加 `static` 修饰来表示这是一个静态属性或静态方法。

代码示例：

```JAVA
class StaticTest {
    static int i = 47;
}
```

现在，即使你创建了两个 StaticTest 对象，但是静态变量 i 仍只占一份存储空间。两个对象都会共享相同的变量 i。
代码示例：

```JAVA
StaticTest st1 = new StaticTest();
StaticTest st2 = new StaticTest();
```

st1.i和st2.i的值都是47，因为它们属于同一段内存。引用静态变量有两种方法。在前面的示例中，我们可以通过一个对象来命名它；例如，st2.i。同时，你也可以通过它的类名直接调用它（这是非静态成员不能执行的操作）：

```JAVA
StaticTest.i ++;
```

`++` 运算符将会使变量结果 + 1。此时 st1.i 和 st2.i 的值就变成了48了。

使用类名直接引用静态变量的首选方法，因为它强调了变量的静态属性。类似的逻辑也适用于静态方法。我们可以通过对象引用静态方法，就像使用任何方法一样，也可以使用特殊的附加语法 classname.method（）来直接调用静态属性或方法。

代码示例：

```JAVA
class Incrementable {
    static void increment() { 
      StaticTest.i++; 
    }
}
```
上例中 Incrementable 类调用静态方法 increment（）。后者再使用 `++` 运算符递增静态变量 int i。我们依然可以先实例化对象再调用该方法。

代码示例：

```JAVA
Incrementable sf = new Incrementable();
sf.increment();
```

当然了，首选的方法是直接通过类来调用它。代码示例：

```JAVA
Incrementable.increment（）；
```

相比非静态的对象，`static` 属性改变了创建数据的方式。同样，当 `static` 关键字修饰方法时，它允许我们无需创建对象就可以直接通过类的引用来调用该方法。正如我们所知，`static` 关键字的这些特性对于应用程序入口点的 main() 方法尤为重要。
应用于字段的 `static` 肯定会更改创建数据的方式 —— `static` 针对每个类和非 `static` 针对每个对象。当应用于方法时，`static` 允许您在不创建对象的情况下调用该方法。正如您将看到的，在定义作为运行应用程序入口点的main（）方法时，这是非常重要的。


<!-- Your First Java Program -->
## 小试牛刀

最后，我们来开始编写第一个完整的程序。我们使用 Java 标准库来展示一个字符串和日期。
Finally, here’s the first complete program. It starts by displaying a
String, followed by the date, using the Date class from the Java
standard library.

```JAVA

// objects/HelloDate.java
import java.util.*;

    public class HelloDate {
    public static void main(String[] args) {
    System.out.println("Hello, it's: ");
    System.out.println(new Date());
    }
}

```

在这本书中，代码块的第一行，我将使用注释行，其中包含文件的路径信息（使用本章的目录名对象），后跟文件名。我的工具可以根据这些信息自动提取和测试书籍的代码，你也可以通过参考第一行注释信息轻松地在 Github 库中找到相应的代码示例。

如果你想在代码中使用到一些额外的库，那么你需要在程序文件的开始处使用 **import** 关键字来导入它们。之所以说是额外的，因为有一些库已经默认自动包含到每个文件里了。例如：**java.lang** 包。

现在打开你的浏览器在 [Oracle](https://www.oracle.com/) 上查看文档。如果你还没有在 [Oracle](https://www.oracle.com/) 网站上下载 JDK 文档，那就趁现在 ^[4] 。查看包列表，你会看到 Java 附带的所有不同的类库。

[^4]: 脚注预留


选择 **java.lang**。这里显示的是该库中所有类的列表。由于 **java.lang** 隐式包含在每个 Java代码文件中，因此这些类是自动可用的。**java.lang** 中没有列出 **Date** 类，所以我们必须将其导入库才能使用它。如果你不清楚某个类名或者想查看所有的类，可以在 Java 文档中选择“Tree”。

现在，我们可以找到 Java 附带的每个类。使用浏览器的“查找”功能查找 “Date”。搜索结果中将会列出 java.util.Date，显而易见，它在 util 库中，所以我们必须导入 java.util.* 才能使用 Date。

如果你在文档中选择 java.lang，然后选择 System，你会看到 System 类中有几个字段，如果你选择 `out`，你会发现它是一个静态的 PrintStream 对象。 所以，即使我们没有使用 new 创建， `out` 对象就已经存在并可以使用。 `out` 对象可以执行的操作取决于 PrintStream 。 其在文档中的描述中显示为超链接，如果单击该链接，我们将可以看到 PrintStream 所对应的方法列表。（更多详情，将在本书后面介绍。） 现在我们重点说的是 println（）这个方法。 它的作用是“j将信息输出到控制台，并以换行符结束。”  既然如此，我们可以这样编码来输出信息到控制带。

代码示例：

```JAVA

System.out.println("A String of things");
```

每个 java 源文件中允许有多个类。与此同时，源文件的名称必须要和其中一个类名相同，否则编译器将会报错。每个独立的程序应该包含一个 mian 方法作为程序运行的入口。其方法签名和返回类型如下

代码示例：

```JAVA
public static void main(String[] args) {

}
```
关键字 `public` 表示方法可以被外界锁访问到。（ 更多详情将在 **隐藏实现** 章节讲到）
main() 方法的参数是一个 字符串（String） 数组。 参数 `args` 并没有在当前的程序中使用到，但是 Java 虚拟机强制要求必须要有。 这是因为它们被用于保存命令行中的参数。

下面我们来看一段有趣的代码：

```JAVA
System.out.println(new Date());
```

上面的示例中，我们创建了一个日期（Date）型对象并将其转化为字符串类型并输出到控制台中。 一旦这一行语句执行完毕，我们就不再需要该日期对象了。这时， JAVA 的垃圾回收器就可以将其占用的内存回收，我们无需去主动清除它们。

查看 JDK 文档，我们可以看到， **System** 类下还有很多其他有用的方法。（ Java 的牛逼之处还在于，它拥有一个庞大的标准库资源。） 代码示例：

```JAVA

// objects/ShowProperties.java
public class ShowProperties {
public static void main(String[] args) {
System.getProperties().list(System.out);
System.out.println(System.getProperty("user.name"));
System.out.println(
System.getProperty("java.library.path"));
}
}

/* Output：(前20行)
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
```

上例主方法中的第一行，会输出所有的系统属性，也就是环境信息。 **list()** 方法将结果发送给它的参数 **System.out**  在本书的后面，我们还会接触到将结果输出到其他地方，如文件中。另外，我们还可以请求特定的属性。该例中我们使用到了 **user.name** 和 **java.library.path**。 末尾的 “/* Output：”标记表示此文件生成的输出的开头。本书中产生输出的大多数示例将会以包含此注释形式的输出，因此我们可以看到输出并知道它是正确的。带有这个标签允许在使用编译器检查并执行后将输出自动更新到本书的文本中。

您将在本书后面看到，您可以将结果发送到其他地方，例如。您还可以要求特定的属性 -  here，user.name和java.library.path。文件末尾的/ * Output：标记表示此文件生成的输出的开头。本书中产生输出的大多数示例将包含此注释形式的输出，因此您可以看到输出并知道它是正确的。标签允许在使用编译器检查并执行后将输出自动更新到本书的文本中。


<!-- Compiling and Running -->
### 编译和运行


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

