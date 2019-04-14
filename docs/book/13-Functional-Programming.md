[TOC]

<!-- Functional Programming -->
# 第十三章 函数式编程



> 函数式编程语言操纵代码片段就像操作数据一样容易。 虽然 Java 不是函数式语言，但 Java 8 Lambda 表达式和方法引用 (Method References) 允许你以函数式编程。

在计算机时代早期，内存是稀缺和昂贵的。几乎每个人都用汇编语言编程。人们对编译器有所了解，但仅仅想到编译生成的代码肯定会比手工编码多了很多字节。

通常，只是为了使程序适合有限的内存，程序员通过修改内存中的代码来保存代码空间，以便在程序执行时执行不同的操作。这种技术被称为**自修改代码** （self-modifying code）。只要程序足够小，少数人可以维护所有棘手和神秘的汇编代码，你就可以让它运行起来。

随着内存和处理器变得更便宜、更快。C 语言出现并被大多数汇编程序员认为更“高级”。人们发现使用 C 可以显著提高生产力。同时，创建自修改代码仍然不难。

随着硬件越来越便宜，程序的规模和复杂性都在增长。这一切只是让程序工作变得困难。我们想方设法使代码更加一致和易懂。使用纯粹的自修改代码造成的结果就是：我们很难确定程序在做什么。它也难以测试：除非你想一点点测试输出，代码转换和修改等等过程？

然而，使用代码以某种方式操纵其他代码的想法也很有趣，只要能保证它更安全。从代码创建，维护和可靠性的角度来看，这个想法非常吸引人。我们不用从头开始编写大量代码，而是从易于理解、充分测试及可靠的现有小块开始，最后将它们组合在一起以创建新代码。难道这不会让我们更有效率，同时创造更健壮的代码吗？

这就是**函数式编程**（FP）的意义所在。通过合并现有代码来生成新功能而不是从头开始编写所有内容，我们可以更快地获得更可靠的代码。至少在某些情况下，这套理论似乎很有用。在这一过程中，一些非函数式语言已经习惯了使用函数式编程产生的优雅的语法。

你也可以这样想：

OO（object oriented，面向对象）是抽象数据，FP（functional programming，函数式编程）是抽象行为。

纯粹的函数式语言在安全性方面更进一步。它强加了额外的约束，即所有数据必须是不可变的：设置一次，永不改变。将值传递给函数，该函数然后生成新值但从不修改自身外部的任何东西（包括其参数或该函数范围之外的元素）。当强制执行此操作时，你知道任何错误都不是由所谓的副作用引起的，因为该函数仅创建并返回结果，而不是其他任何错误。

更好的是，“不可变对象和无副作用”范例解决了并发编程中最基本和最棘手的问题之一（当程序的某些部分同时在多个处理器上运行时）。这是可变共享状态的问题，这意味着代码的不同部分（在不同的处理器上运行）可以尝试同时修改同一块内存（谁赢了？没人知道）。如果函数永远不会修改现有值但只生成新值，则不会对内存产生争用，这是纯函数式语言的定义。 因此，经常提出纯函数式语言作为并行编程的解决方案（还有其他可行的解决方案）。

需要提醒大家的是，函数式语言背后有很多动机，这意味着描述它们可能会有些混淆。它通常取决于各种观点：“为并行编程”，“代码可靠性”和“代码创建和库复用”。[^1] 同时，函数式编程的参数能帮助程序员创建更快更健壮的代码 —— 部分仍然只是假设。虽然已有一些好的范例，[^2]但我们还不能证明纯函数式语言就是解决编程问题的最佳方法。

FP 思想值得融入非 FP 语言，如 Python。Java 8 也从中吸收并支持了 FP。我们将在此章探讨。


<!-- Old vs. New -->
## 新旧对比


通常，方法会根据我们传递的数据产生不同的结果。如果我们希望某个方法在下一次调用时表现不同，该怎么办？ 假设我们可以将代码传递给方法，我们就可以控制它的行为。此前，我们通过在方法中创建包含所需行为的对象，然后将该对象传递给我们想要控制的方法来完成此操作。下面我们用传统形式和 Java 8 的方法引用和 Lambda 表达式分别演示。代码示例：

```java
// functional/Strategize.java

interface Strategy {
  String approach(String msg);
}

class Soft implements Strategy {
  public String approach(String msg) {
    return msg.toLowerCase() + "?";
  }
}

class Unrelated {
  static String twice(String msg) {
    return msg + " " + msg;
  }
}

public class Strategize {
  Strategy strategy;
  String msg;
  Strategize(String msg) {
    strategy = new Soft(); // [1]
    this.msg = msg;
  }

  void communicate() {
    System.out.println(strategy.approach(msg));
  }

  void changeStrategy(Strategy strategy) {
    this.strategy = strategy;
  }

  public static void main(String[] args) {
    Strategy[] strategies = {
      new Strategy() { // [2]
        public String approach(String msg) {
          return msg.toUpperCase() + "!";
        }
      },
      msg -> msg.substring(0, 5), // [3]
      Unrelated::twice // [4]
    };
    Strategize s = new Strategize("Hello there");
    s.communicate();
    for(Strategy newStrategy : strategies) {
      s.changeStrategy(newStrategy); // [5]
      s.communicate(); // [6]
    }
  }
}
```

输出结果:

```
hello there?
HELLO THERE!'
Hello
Hello there Hello there
```

**Strategy** 接口提供了单一 `approach()` 方法来承载函数式功能。通过实现不同的 **Strategy** 对象，我们可以创建不同的行为。

传统上，我们通过创建一个实现 **Strategy** 接口的类来实现此行为，比如在 **Soft**。

- **[1]** 在 **Strategize** 中，**Soft** 作为默认策略，在初始化构造函数中赋值的。

- **[2]** 一种略显冗长且更自发的方法是创建一个**匿名内部类**。即使这样，仍有相当数量的冗余代码。你总是要仔细观察：“哦，原来这样，这里使用了匿名内部类。”

- **[3]** Java 8 的 Lambda 表达式。由箭头 `->` 分隔开参数和函数体，箭头左边是参数，箭头右侧是从 Lambda 返回的表达式，即函数体。这实现了与定义类、匿名内部类相同的效果，但代码少得多。

- **[4]** Java 8 的**方法引用**，由 `::` 区分。在 `::` 的左边是类或对象的名称，在 `::` 的右边是方法的名称，但没有参数列表。

- **[5]** 在使用默认的 **Soft**  **strategy** 之后，我们逐步遍历数组中的所有 **Strategy**，并使用 `changeStrategy()` 方法将每个 **Strategy** 放入 变量 `s` 中。

- **[6]** 现在，每次调用 `communic()` 都会产生不同的行为，具体取决于此刻正在使用的策略**代码对象**。我们传递的是行为，而非仅数据。[^3]

在 Java 8 之前，我们能够通过 **[1]** 和 **[2]** 的方式传递功能。然而，它的读写语法非常笨拙，并且我们别无选择。方法引用和 Lambda 表达式的出现让我们可以在需要时**传递功能**，而不是仅在必要才这么做。


<!-- Lambda Expressions -->
## Lambda表达式


Lambda 表达式是使用**最小可能**语法编写的函数定义：

1. Lambda 表达式产生函数，而不是类。 在 JVM（Java Virtual Machine，Java 虚拟机）上，一切都是一个类，因此在幕后执行各种操作使 Lambda 看起来像函数 —— 但作为程序员，你可以高兴地假装它们“只是函数”。

2. Lambda 语法尽可能多，这正是为了使 Lambda 易于编写和使用。

我们在 `Strategize.java` 中看到了一个 Lambda 表达式，但还有其他语法变体：

```java
// functional/LambdaExpressions.java

interface Description {
  String brief();
}

interface Body {
  String detailed(String head);
}

interface Multi {
  String twoArg(String head, Double d);
}

public class LambdaExpressions {

  static Body bod = h -> h + " No Parens!"; // [1]

  static Body bod2 = (h) -> h + " More details"; // [2]

  static Description desc = () -> "Short info"; // [3]

  static Multi mult = (h, n) -> h + n; // [4]

  static Description moreLines = () -> { // [5]
    System.out.println("moreLines()");
    return "from moreLines()";
  };

  public static void main(String[] args) {
    System.out.println(bod.detailed("Oh!"));
    System.out.println(bod2.detailed("Hi!"));
    System.out.println(desc.brief());
    System.out.println(mult.twoArg("Pi! ", 3.14159));
    System.out.println(moreLines.brief());
  }
}
```

输出结果：

```
Oh! No Parens!
Hi! More details
Short info
Pi! 3.14159
moreLines()
from moreLines()
```

我们从三个接口开始，每个接口都有一个单独的方法（很快就会理解它的重要性）。但是，每个方法都有不同数量的参数，以便演示 Lambda 表达式语法。

任何 Lambda 表达式的基本语法是：

1. 参数。

2. 接着 `->`，可视为“产出”。

3. `->` 之后的内容都是方法体。

  - **[1]** 当只用一个参数，可以不需要括号 `()`。 然而，这是一个特例。

  - **[2]** 正常情况使用括号 `()` 包裹参数。 为了保持一致性，也可以使用括号 `()` 包裹单个参数，虽然这种情况并不常见。

  - **[3]** 如果没有参数，则必须使用括号 `()` 表示空参数列表。

  - **[4]** 对于多个参数，将参数列表放在括号 `()` 中。

到目前为止，所有 Lambda 表达式方法体都是单行。 该表达式的结果自动成为 Lambda 表达式的返回值，在此处使用 **return** 关键字是非法的。 这是 Lambda 表达式缩写用于描述功能的语法的另一种方式。

**[5]** 如果在 Lambda 表达式中确实需要多行，则必须将这些行放在花括号中。 在这种情况下，就需要使用 **return**。

Lambda 表达式通常比**匿名内部类**产生更易读的代码，因此我们将在本书中尽可能使用它们。

### 递归

递归函数是一个自我调用的函数。 可以编写递归的 Lambda 表达式，但需要注意：递归方法必须是实例变量或静态变量，否则会出现编译时错误。 我们将为每个案例创建一个示例。

这两个示例都需要一个接受 **int** 并生成 **int** 的接口：

```java
// functional/IntCall.java

interface IntCall {
  int call(int arg);
}
```

整数 n 的阶乘将所有小于或等于 n 的正整数相乘。 阶乘函数是一个常见的递归示例：

```java
// functional/RecursiveFactorial.java

public class RecursiveFactorial {
  static IntCall fact;
  public static void main(String[] args) {
    fact = n -> n == 0 ? 1 : n * fact.call(n - 1);
    for(int i = 0; i <= 10; i++)
      System.out.println(fact.call(i));
  }
}
/* Output:
1
1
2
6
24
120
720
5040
40320
362880
3628800
*/
```

这里，**fact** 是一个静态变量。 注意使用三元 **if-else**。 递归函数将一直调用自己，直到 **i == 0**.所有递归函数都有某种 “停止条件”，否则它们将无限递归并产生异常。

我们可以将 **Fibonacci 序列** 实现为递归 Lambda 表达式，这次使用实例变量：

```java
// functional/RecursiveFibonacci.java

public class RecursiveFibonacci {
  IntCall fib;
  RecursiveFibonacci() {
    fib = n -> n == 0 ? 0 :
               n == 1 ? 1 :
               fib.call(n - 1) + fib.call(n - 2);
  }
  int fibonacci(int n) { return fib.call(n); }
  public static void main(String[] args) {
    RecursiveFibonacci rf = new RecursiveFibonacci();
    for(int i = 0; i <= 10; i++)
      System.out.println(rf.fibonacci(i));
  }
}
/* Output:
0
1
1
2
3
5
8
13
21
34
55
*/
```

将Fibonacci 序列对中的最后两个元素求和来产生下一个元素。

<!-- method references-->
## 方法引用


Java 8 方法引用指的是没有以前版本的 Java 所需的额外包袱的方法。 方法引用是类名或对象名，后面跟 :: [^4]，然后是方法的名称。

```java
// functional/MethodReferences.java

import java.util.*;

interface Callable { // [1]
  void call(String s);
}

class Describe {
  void show(String msg) { // [2]
    System.out.println(msg);
  }
}

public class MethodReferences {
  static void hello(String name) { // [3]
    System.out.println("Hello, " + name);
  }
  static class Description {
    String about;
    Description(String desc) { about = desc; }
    void help(String msg) { // [4]
      System.out.println(about + " " + msg);
    }
  }
  static class Helper {
    static void assist(String msg) { // [5]
      System.out.println(msg);
    }
  }
  public static void main(String[] args) {
    Describe d = new Describe();
    Callable c = d::show; // [6]
    c.call("call()"); // [7]

    c = MethodReferences::hello; // [8]
    c.call("Bob");

    c = new Description("valuable")::help; // [9]
    c.call("information");

    c = Helper::assist; // [10]
    c.call("Help!");
  }
}
/* Output:
call()
Hello, Bob
valuable information
Help!
*/
```

**[1]** 我们从单一方法接口开始（同样，你很快就会了解到这一点的重要性）。

**[2]** `show()` 的签名（参数类型和返回类型）符合 **Callable** 的 `call()` 的签名。

**[3]** `hello()` 也符合 `call()` 的签名。 

[4] ...就像是 `help()`，一个静态内部类中的非静态方法。(原文：… as is help(), a non-static method within a static inner class.)

**[5]** `assist()` 是静态内部类中的静态方法。

**[6]** 我们将 **Describe** 对象的方法引用分配给 **Callable** , 它没有 `show()` 方法，而是 `call()` 方法。 但是，Java似乎接受用这个看似奇怪的赋值，因为方法引用符合 **Callable** 的 `call()` 方法的签名。

**[7]** 我们现在可以通过调用 `call()` 来调用 `show()`，因为 Java 将 `call()` 映射到 `show()`。

**[8]** 这是一个静态方法引用。

**[9] **这是 **[6]** 的另一个版本：附加到存活对象的方法的方法参考，有时称为*绑定方法引用*。

**[10]** 最后，获取静态内部类的静态方法的方法引用，用起来就像 **[8]**中的外部类。

这不是一个详尽的例子; 我们很快就会看到方法参考的所有变化。

### Runnable

**Runnable** 接口自 1.0 版以来一直在 Java 中，因此不需要导入。 它也符合特殊的单方法接口格式：它的方法run()不带参数，也没有返回值。 因此，我们可以使用 Lambda 表达式和方法引用作为 **Runnable**：

```java
// functional/RunnableMethodReference.java

// Method references with interface Runnable

class Go {
  static void go() {
    System.out.println("Go::go()");
  }
}

public class RunnableMethodReference {
  public static void main(String[] args) {

    new Thread(new Runnable() {
      public void run() {
        System.out.println("Anonymous");
      }
    }).start();

    new Thread(
      () -> System.out.println("lambda")
    ).start();

    new Thread(Go::go).start();
  }
}
/* Output:
Anonymous
lambda
Go::go()
*/
```

**Thread** 对象将 **Runnable** 作为其构造函数参数，并具有会调用 `run()` 的方法  `start()`。 请注意，只有**匿名内部类**才需要具有名为 `run()` 的方法。

<!-- Unbound Method References -->

### 未绑定的方法引用

未绑定的方法引用是指没有关联对象的普通（非静态）方法。 要使用未绑定的引用，你必须提供以下对象：

```java
// functional/UnboundMethodReference.java

// Method reference without an object

class X {
  String f() { return "X::f()"; }
}

interface MakeString {
  String make();
}

interface TransformX {
  String transform(X x);
}

public class UnboundMethodReference {
  public static void main(String[] args) {
    // MakeString ms = X::f; // [1]
    TransformX sp = X::f;
    X x = new X();
    System.out.println(sp.transform(x)); // [2]
    System.out.println(x.f()); // Same effect
  }
}
/* Output:
X::f()
X::f()
*/
```

到目前为止，我们已经看到了对与其关联接口具有相同签名的方法的引用。 在 **[1]**，我们尝试对X中的 `f()` 做同样的事情，试图分配给 **MakeString**。 这会产生编译器关于“无效方法引用”的错误  (“invalid method reference)，即使 `make()` 与 `f()` 具有相同的签名。 问题是实际上还有另一个（隐藏的）参数：我们的老朋友 `this`。 你不能在没有 `X` 对象的情况下调用 `f()` 来调用它。 因此，`X :: f` 表示未绑定的方法引用，因为它尚未“绑定”到对象。

要解决这个问题，我们需要一个 `X` 对象，所以我们的接口实际上需要一个额外的参数，就像你在 **TransformX** 中看到的那样。 如果将 `X :: f` 分配给 **TransformX**，Java 很高兴。 我们现在必须进行第二次心理调整 - 使用未绑定的引用时，函数方法的签名（接口中的单个方法）不再与方法引用的签名完全匹配。 有一个很好的理由说服你，那就是你需要一个对象来调用方法。

**[2]** 的结果有点像脑筋急转弯。 我接受未绑定的引用并对其调用 `transform()`，将其传递给X，并以某种方式导致对 `x.f()`的调用。 Java知道它必须采用第一个参数，实际上是这个，并在其上调用方法。

```java
// functional/MultiUnbound.java

// Unbound methods with multiple arguments

class This {
  void two(int i, double d) {}
  void three(int i, double d, String s) {}
  void four(int i, double d, String s, char c) {}
}

interface TwoArgs {
  void call2(This athis, int i, double d);
}

interface ThreeArgs {
  void call3(This athis, int i, double d, String s);
}

interface FourArgs {
  void call4(
    This athis, int i, double d, String s, char c);
}

public class MultiUnbound {
  public static void main(String[] args) {
    TwoArgs twoargs = This::two;
    ThreeArgs threeargs = This::three;
    FourArgs fourargs = This::four;
    This athis = new This();
    twoargs.call2(athis, 11, 3.14);
    threeargs.call3(athis, 11, 3.14, "Three");
    fourargs.call4(athis, 11, 3.14, "Four", 'Z');
  }
}
```

为了说明这一点，我将类命名为 **This** ，函数方法的第一个参数则是 **athis**，但是你应该选择其他名称以防止生产代码混淆。

### 构造函数引用

你还可以捕获构造函数的引用，然后通过引用调用该构造函数。

```java
// functional/CtorReference.java

class Dog {
  String name;
  int age = -1; // For "unknown"
  Dog() { name = "stray"; }
  Dog(String nm) { name = nm; }
  Dog(String nm, int yrs) { name = nm; age = yrs; }
}

interface MakeNoArgs {
  Dog make();
}

interface Make1Arg {
  Dog make(String nm);
}

interface Make2Args {
  Dog make(String nm, int age);
}

public class CtorReference {
  public static void main(String[] args) {
    MakeNoArgs mna = Dog::new; // [1]
    Make1Arg m1a = Dog::new;   // [2]
    Make2Args m2a = Dog::new;  // [3]

    Dog dn = mna.make();
    Dog d1 = m1a.make("Comet");
    Dog d2 = m2a.make("Ralph", 4);
  }
}
```

**Dog** 有三个构造函数，函数接口内的 `make()` 方法反映了构造函数参数列表（ make()方法可以有不同的名称）。

注意我们如何对 **[1]**，**[2]** 和 **[3]** 中的每一个使用 `Dog :: new`。 所有三个构造函数只有一个名称：`:: new`。 但是构造函数引用在每种情况下都分配给不同的接口，并且编译器可以知道从哪个构造函数引用中进行检测。

编译器可以看到调用函数方法（ 在本例中为make()）意味着调用构造函数。

<!-- Functional Interfaces -->
## 函数式接口


方法引用和 Lambda 表达式都是必须赋值的，并且这些赋值需要编译器的类型信息以确保类型正确性。 Lambda 表达式特别引入了新的要求。 考虑：

```java
x -> x.toString()
```

我们看到返回类型必须是String，但x是什么类型？

因为 Lambda 表达式包含一种类型推断形式（编译器会对类型进行描述，而不是要求程序员显式），编译器必须能够以某种方式推导出 x 的类型。

这是第二个例子：

```java
(x, y) -> x + y
```

现在 `x` 和 `y` 可以是支持 `+` 运算符的任何类型，包括两个不同的数字类型或一个 **String** 以及一些将自动转换为 **String** 的类型（这包括大多数类型）。 但是，当分配此 Lambda 表达式时，编译器必须确定 `x` 和 `y` 的确切类型以生成正确的代码。

同样的问题适用于方法引用。 假设你要传递 System.out :: println 到你正在编写的方法 ，你为方法的参数给出了什么类型？

为了解决这个问题，Java 8 引入了 `java.util.function`，它包含一组接口，这些接口是 Lambda 表达式和方法引用的目标类型。 每个接口只包含一个抽象方法，称为函数式方法。

在编写接口时，可以使用 `@FunctionalInterface` 注释强制执行此“函数方法”模式：

```java
// functional/FunctionalAnnotation.java

@FunctionalInterface
interface Functional {
  String goodbye(String arg);
}

interface FunctionalNoAnn {
  String goodbye(String arg);
}

/*
@FunctionalInterface
interface NotFunctional {
  String goodbye(String arg);
  String hello(String arg);
}
Produces error message:
NotFunctional is not a functional interface
multiple non-overriding abstract methods
found in interface NotFunctional
*/

public class FunctionalAnnotation {
  public String goodbye(String arg) {
    return "Goodbye, " + arg;
  }
  public static void main(String[] args) {
    FunctionalAnnotation fa =
      new FunctionalAnnotation();
    Functional f = fa::goodbye;
    FunctionalNoAnn fna = fa::goodbye;
    // Functional fac = fa; // Incompatible
    Functional fl = a -> "Goodbye, " + a;
    FunctionalNoAnn fnal = a -> "Goodbye, " + a;
  }
}
```

`@FunctionalInterface` 注释是可选的; Java 将 `Functional` 和 `FunctionalNoAnn` 视为 `main()` 中的函数接口。 `@FunctionalInterface` 的值在 `NotFunctional` 的定义中可见。接口中的如果有多个方法则会产生编译时错误消息。

仔细观察 `f` 和 `fna` 定义中会发生什么。 `Functional` 和 `FunctionalNoAnn` 定义接口。 然而，分配的只是方法 `goodbye()`。首先，这只是一种方法而不是一种类。 其次，它甚至不是一个实现了该接口的类里的方法。这是添加到 Java 8 中的一些魔力：如果将方法引用或 Lambda 表达式分配给函数接口（以及类型适合），Java将使你的分配适应目标接口。 在背后，编译器将方法引用或 Lambda 表达式包装在实现目标接口的类的实例中。

尽管 `FunctionalAnnotation` 确实适合 `Functional` 模型，但如果我们尝试将 `FunctionalAnnotation` 直接分配给 `Functional`，就像 `fac` 的定义一样，Java 将不会让我们成功，因为它没有明确地实现 `Functional` 接口。 但令人惊讶的是 ，Java8 允许我们为接口分配函数，从而产生更好，更简单的语法。

`java.util.function` 的目标是创建一组完整的目标接口，这样你通常不需要定义自己的接口。 主要是因为原始类型，这会产生一小部分接口。 如果你了解命名模式，通常可以通过查看名称来检测特定接口的作用。

 以下是基本命名准则：

1. 如果它只处理对象而不是原语，那么它只是一个简单的名称，如 `Function`，`Consumer`，`Predicate` 等。参数类型通过泛型添加。

2. 如果它采用原始参数，则由名称的第一部分表示，如 `LongConsumer`，`DoubleFunction`，`IntPredicate` 等。例外情况是原始的供应商类型。

3. 如果它返回原始结果，则用 `To` 表示，如 `ToLongFunction <T>` 和 `IntToLongFunction`。

4. 如果它返回与其参数相同的类型，则它是一个运算符，其中一个参数使用 `UnaryOperator`，两个参数使用 `BinaryOperator`。

5. 如果它需要两个参数并返回一个布尔值，那么它就是一个谓词（ Predicate ）。

6. 如果它需要两个不同类型的参数，则名称中有一个 `Bi`。

该表描述了java.util.function中的目标类型 

……..下面的表格内容，直接复制PDF文档下来很难看懂。( 待整理 )

(with noted exceptions):

**Name Characteristic Functional Usage Method Runnable** No arguments;

 (java.lang) **Runnable** Returns nothing **run() Supplier<T> Supplier BooleanSupplier** No arguments; 

get()  IntSupplier  Returns any type getAstype() LongSupplier DoubleSupplier Callable No arguments; (java.util.concurrent)  Callable<V> Returns any type call() Consumer<T> One argument;

Consumer IntConsumer Returns nothing accept() LongConsumer DoubleConsumer 

Two-argument BiConsumer BiConsumer<T,U> Consumer accept() Two-argument Consumer; ObjIntConsumer<T> First arg is a ObjtypeConsumer ObjLongConsumer<T> reference; accept() Second arg is a ObjDoubleConsumer<T> primitive Function<T,R> IntFunction<R> LongFunction<R> DoubleFunction<R> ToIntFunction<T> Function One argument; ToLongFunction<T> apply() Returns a different ToDoubleFunction<T> type Totype & typeTotype: IntToLongFunction applyAstype() IntToDoubleFunction LongToIntFunction LongToDoubleFunction DoubleToIntFunction DoubleToLongFunction UnaryOperator<T> One argument; UnaryOperator IntUnaryOperator Returns the same type apply() LongUnaryOperator DoubleUnaryOperator BinaryOperator<T> Two arguments, same type; BinaryOperator IntBinaryOperator Returns the same apply() LongBinaryOperator type DoubleBinaryOperator Two arguments, Comparator same type; (java.util) Comparator<T> Returns int compare() Predicate<T> BiPredicate<T,U> Two arguments; Predicate IntPredicate Returns boolean test() LongPredicate DoublePredicate IntToLongFunction IntToDoubleFunction Primitive argument; typeTotypeFunction LongToIntFunction Returns a primitive applyAstype() LongToDoubleFunction DoubleToIntFunction DoubleToLongFunction BiFunction<T,U,R> BiConsumer<T,U> Two arguments; Bioperation BiPredicate<T,U> Different types (method name varies) ToIntBiFunction<T,U> ToLongBiFunction<T,U> ToDoubleBiFunction<T,U>



你可能会想到用于进一步添加更多行，但此表提供了基本概念，并且应该帮到或多或少地推断出你需要的函数接口。

你可以看到在创建 `java.util.function` 时做出了一些选择。 例如，为什么没有 `IntComparator`，`LongComparator` 和 `DoubleComparator` ？ 有一个 `BooleanSupplier`，但没有其他接口表示 **Boolean**。 有一个通用的 `BiConsumer`，但没有用于所有 **int**，**long** 和 **double** 的 `BiConsumers` 变体（ 我可以支持他们为什么放弃那个）。 这是疏忽还是有人决定（他们是如何得出这个结论的）？

你还可以看到原始类型为 Java 添加了多少复杂性。 由于效率问题，它们被包含在该语言的第一版中 - 这很快就得到了缓解。 现在，在语言的生命周期中，我们仍然受到语言设计选择不佳的影响。

下面枚举基于 Lambda 表达式的所有不同 Function 变体的示例：

```java
// functional/FunctionVariants.java

import java.util.function.*;

class Foo {}

class Bar {
  Foo f;
  Bar(Foo f) { this.f = f; }
}

class IBaz {
  int i;
  IBaz(int i) {
    this.i = i;
  }
}

class LBaz {
  long l;
  LBaz(long l) {
    this.l = l;
  }
}

class DBaz {
  double d;
  DBaz(double d) {
    this.d = d;
  }
}

public class FunctionVariants {
  static Function<Foo,Bar> f1 = f -> new Bar(f);
  static IntFunction<IBaz> f2 = i -> new IBaz(i);
  static LongFunction<LBaz> f3 = l -> new LBaz(l);
  static DoubleFunction<DBaz> f4 = d -> new DBaz(d);
  static ToIntFunction<IBaz> f5 = ib -> ib.i;
  static ToLongFunction<LBaz> f6 = lb -> lb.l;
  static ToDoubleFunction<DBaz> f7 = db -> db.d;
  static IntToLongFunction f8 = i -> i;
  static IntToDoubleFunction f9 = i -> i;
  static LongToIntFunction f10 = l -> (int)l;
  static LongToDoubleFunction f11 = l -> l;
  static DoubleToIntFunction f12 = d -> (int)d;
  static DoubleToLongFunction f13 = d -> (long)d;

  public static void main(String[] args) {
    Bar b = f1.apply(new Foo());
    IBaz ib = f2.apply(11);
    LBaz lb = f3.apply(11);
    DBaz db = f4.apply(11);
    int i = f5.applyAsInt(ib);
    long l = f6.applyAsLong(lb);
    double d = f7.applyAsDouble(db);
    l = f8.applyAsLong(12);
    d = f9.applyAsDouble(12);
    i = f10.applyAsInt(12);
    d = f11.applyAsDouble(12);
    i = f12.applyAsInt(13.0);
    l = f13.applyAsLong(13.0);
  }
}
```

这些 Lambda 表达式尝试生成适合对应函数签名的最简代码。 在某些情况下，强制转换是必要的，否则编译器会抱怨截断错误。

`main()` 中的每个测试都显示了 `Function` 接口中不同类型的 `apply` 方法。 每个都产生一个对其相关的 Lambda 表达式的调用。

方法引用有自己的魔力：

```java
/ functional/MethodConversion.java

import java.util.function.*;

class In1 {}
class In2 {}

public class MethodConversion {
  static void accept(In1 i1, In2 i2) {
    System.out.println("accept()");
  }
  static void someOtherName(In1 i1, In2 i2) {
    System.out.println("someOtherName()");
  }
  public static void main(String[] args) {
    BiConsumer<In1,In2> bic;

    bic = MethodConversion::accept;
    bic.accept(new In1(), new In2());

    bic = MethodConversion::someOtherName;
    // bic.someOtherName(new In1(), new In2()); // Nope
    bic.accept(new In1(), new In2());
  }
}
/* Output:
accept()
someOtherName()
*/
```

查看 `BiConsumer` 的文档。 你会看到它的函数方法是 `accept()`。 实际上，如果我们将方法命名为 `accept()`，它就可以作为方法引用。 但是我们可以给它一个完全不同的名称，比如 `someOtherName()`，它也可以运行，只要参数类型和返回类型与 `BiConsumer` 的 `accept()` 相同。

因此，在使用函数接口时，名称无关紧要 - 只有参数类型和返回类型相同。 Java 将你的名称映射到接口的函数方法。 要调用方法，可以调用函数方法名称（在本例中为 `accept()`），而不是你的方法名称。

现在我们将查看应用于方法引用的所有基于类的 Functionals（即那些不涉及原始类型的函数）。 我再次创建了适合函数签名的最简单方法：

```java
// functional/ClassFunctionals.java

import java.util.*;
import java.util.function.*;

class AA {}
class BB {}
class CC {}

public class ClassFunctionals {
  static AA f1() { return new AA(); }
  static int f2(AA aa1, AA aa2) { return 1; }
  static void f3(AA aa) {}
  static void f4(AA aa, BB bb) {}
  static CC f5(AA aa) { return new CC(); }
  static CC f6(AA aa, BB bb) { return new CC(); }
  static boolean f7(AA aa) { return true; }
  static boolean f8(AA aa, BB bb) { return true; }
  static AA f9(AA aa) { return new AA(); }
  static AA f10(AA aa1, AA aa2) { return new AA(); }
  public static void main(String[] args) {
    Supplier<AA> s = ClassFunctionals::f1;
    s.get();
    Comparator<AA> c = ClassFunctionals::f2;
    c.compare(new AA(), new AA());
    Consumer<AA> cons = ClassFunctionals::f3;
    cons.accept(new AA());
    BiConsumer<AA,BB> bicons = ClassFunctionals::f4;
    bicons.accept(new AA(), new BB());
    Function<AA,CC> f = ClassFunctionals::f5;
    CC cc = f.apply(new AA());
    BiFunction<AA,BB,CC> bif = ClassFunctionals::f6;
    cc = bif.apply(new AA(), new BB());
    Predicate<AA> p = ClassFunctionals::f7;
    boolean result = p.test(new AA());
    BiPredicate<AA,BB> bip = ClassFunctionals::f8;
    result = bip.test(new AA(), new BB());
    UnaryOperator<AA> uo = ClassFunctionals::f9;
    AA aa = uo.apply(new AA());
    BinaryOperator<AA> bo = ClassFunctionals::f10;
    aa = bo.apply(new AA(), new AA());
  }
}
```

请注意，每个方法名称都是任意的（ `f1()，f2()`等），但正如你刚才看到的，一旦将方法引用分配给函数接口，你就可以调用与该接口关联的函数方法。 在此示例中，这些是 `get()，compare()，accept()，apply()` 和 `test()`。

<!-- Functional Interfaces with More Arguments -->

### 有着更多参数的函数接口

`java.util.functional` 中的接口是有限的。 比如有了 `BiFunction`，但它不能变化。 如果需要三参数函数的接口怎么办？ 其实这些接口非常简单，很容易查看 Java 库源代码并自行创建：

```java
// functional/TriFunction.java

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
```

一个简短的测试将验证它是否有效：

```java
// functional/TriFunctionTest.java

public class TriFunctionTest {
  static int f(int i, long l, double d) { return 99; }
  public static void main(String[] args) {
    TriFunction<Integer, Long, Double, Integer> tf =
      TriFunctionTest::f;
    tf = (i, l, d) -> 12;
  }
}
```

这里我们测试方法引用和 Lambda 表达式。

### 缺少原始类型的函数

让我们重温一下 `BiConsumer`，看看我们如何创建缺少 **int**，**long** 和 **double** 的各种排列：

```java
// functional/BiConsumerPermutations.java

import java.util.function.*;

public class BiConsumerPermutations {
  static BiConsumer<Integer, Double> bicid = (i, d) ->
    System.out.format("%d, %f%n", i, d);
  static BiConsumer<Double, Integer> bicdi = (d, i) ->
    System.out.format("%d, %f%n", i, d);
  static BiConsumer<Integer, Long> bicil = (i, l) ->
    System.out.format("%d, %d%n", i, l);
  public static void main(String[] args) {
    bicid.accept(47, 11.34);
    bicdi.accept(22.45, 92);
    bicil.accept(1, 11L);
  }
}
/* Output:
47, 11.340000
92, 22.450000
1, 11
*/
```

为了显示，我使用 `System.out.format()`，它类似于 `System.out.println()`，除了它提供了更多的显示选项。 这里，`%f` 表示我将 `n` 作为浮点值给出，`%d` 表示 `n` 是一个整数值。 我能够包含空格，并且它不会添加换行符，除非你输入 `%n`  - 它也会接受传统 `\n` 换行符，但 `%n` 是自动跨平台的，这是使用的 `format()` 的另一个原因。

该示例仅使用适当的包装器类型，装箱和拆箱负责在原始类型之间来回转换。 我们也可以使用包装类型，例如 Function，而不是预定义的原始类型：

```java
// functional/FunctionWithWrapped.java

import java.util.function.*;

public class FunctionWithWrapped {
  public static void main(String[] args) {
    Function<Integer, Double> fid = i -> (double)i;
    IntToDoubleFunction fid2 = i -> i;
  }
}
```

如果没有强制转换，则会收到错误消息：“ Integer 无法转换为 Double ”，而 **IntToDoubleFunction** 版本没有此类问题。 Java库代码里 **IntToDoubleFunction** 是这样子的：

```java
@FunctionalInterface 
public interface IntToDoubleFunction { 
  double applyAsDouble(int value); 
}
```

因为我们可以简单地编写 `Function <Integer，Double>` 并产生工作结果，所以很明显，函数的原始类型的唯一原因是为了防止传递参数和返回结果所涉及的自动装箱和自动装箱。 也就是说，为了性能。

似乎可以安全地推测，某些函数类型具有定义而其他类型没有定义是因为考虑到了使用频率。

当然，如果由于缺少原始类型的函数而导致性能实际上成为问题，你可以轻松编写自己的接口（ 使用Java库源进行参考 ） - 尽管这似乎不太可能是你的性能瓶颈。

<!-- Higher-Order Functions-->
## 高阶函数


这个名字听起来有点令人生畏，但是：高阶函数只是一个消耗或产生函数的函数。

我们先来看看产生一个函数：

```java
// functional/ProduceFunction.java

import java.util.function.*;

interface
FuncSS extends Function<String, String> {} // [1]

public class ProduceFunction {
  static FuncSS produce() {
    return s -> s.toLowerCase(); // [2]
  }
  public static void main(String[] args) {
    FuncSS f = produce();
    System.out.println(f.apply("YELLING"));
  }
}
/* Output:
yelling
*/
```

这里，`produce()` 是高阶函数。

**[1]** 使用继承，你可以轻松地为你的专用接口创建别名。

**[2]** 使用 Lambda 表达式，在方法中创建和返回一个函数几乎毫不费力。

要 `consume` 函数（consume a function），其对应的 `consuming` 方法 （the consuming method）的参数列表必须正确描述函数类型：

```java
// functional/ConsumeFunction.java

import java.util.function.*;

class One {}
class Two {}

public class ConsumeFunction {
  static Two consume(Function<One,Two> onetwo) {
    return onetwo.apply(new One());
  }
  public static void main(String[] args) {
    Two two = consume(one -> new Two());
  }
}
```

当你根据 `consume` 的函数生成新函数时，事情变得特别有趣，比如：

```java
// functional/TransformFunction.java

import java.util.function.*;

class I {
  @Override
  public String toString() { return "I"; }
}

class O {
  @Override
  public String toString() { return "O"; }
}

public class TransformFunction {
  static Function<I,O> transform(Function<I,O> in) {
    return in.andThen(o -> {
      System.out.println(o);
      return o;
    });
  }
  public static void main(String[] args) {
    Function<I,O> f2 = transform(i -> {
      System.out.println(i);
      return new O();
    });
    O o = f2.apply(new I());
  }
}
/* Output:
I
O
*/
```

在这里，`transform()` 生成一个与传入的函数具有相同签名的函数，但是你可以生成任何你想要的类型。这在 `Function` 接口中使用名为 `andThen()` 的默认方法，该方法专门用于操作函数。 顾名思义，在调用in函数之后调用 `toThen()`（还有 `compose()`，它在 `in` 函数之前应用新函数）。 要附加一个 `andThen()` 函数，我们只需将该函数作为参数传递。 `transform()` 产生的是一个新函数，它将 `in` 的动作与 `andThen()` 参数的动作结合起来。

<!-- Closures -->
## 闭包


在上一节的 `ProduceFunction.java` 中，我们从方法返回了一个 Lambda 函数。 这个例子让事情变得简单，但是我们必须在返回 Lambdas 时探讨一些问题。

*闭包*一词概括了这些问题。 闭包非常重要，因为它们可以轻松生成函数。

考虑一个更复杂的 Lambda，它使用函数作用域之外的变量。 返回该函数会发生什么？ 也就是说，当你调用函数时，它对那些 “外部 ”变量引用了什么?  如果语言不能自动解决这个问题，那将变得非常具有挑战性。 能够解决这个问题的语言被称为**支持闭包**，或者在词法上限定范围( 也使用术语变量捕获 )。Java 8提供了有限但合理的闭包支持，

我们将用一些简单的例子来研究它。

首先，这里有一个方法返回一个访问对象字段和方法参数的函数:

```java
// functional/Closure1.java

import java.util.function.*;

public class Closure1 {
  int i;
  IntSupplier makeFun(int x) {
    return () -> x + i++;
  }
}
```

但是，仔细考虑一下，`i` 的这种用法并不是一个很大的挑战，因为对象很可能在你调用 `makeFun()` 之后就存在了——实际上，垃圾收集器几乎肯定会保留一个对象，并将现有的函数以这种方式绑定到该对象上。当然，如果你对同一个对象多次调用 `makeFun()` ，你最终会得到多个函数，它们都为 `i` 共享相同的存储空间:

```java
// functional/SharedStorage.java

import java.util.function.*;

public class SharedStorage {
  public static void main(String[] args) {
    Closure1 c1 = new Closure1();
    IntSupplier f1 = c1.makeFun(0);
    IntSupplier f2 = c1.makeFun(0);
    IntSupplier f3 = c1.makeFun(0);
    System.out.println(f1.getAsInt());
    System.out.println(f2.getAsInt());
    System.out.println(f3.getAsInt());
  }
}
/* Output:
0
1
2
*/
```

每次调用 `getAsInt()` 都会增加 `i`，表明存储是共享的。

如果 `i` 是 `makeFun()` 的本地怎么办？ 在正常情况下，当 `makeFun()` 完成时 `i` 就消失。 但它仍然编译：

```java
// functional/Closure2.java

import java.util.function.*;

public class Closure2 {
  IntSupplier makeFun(int x) {
    int i = 0;
    return () -> x + i;
  }
}
```

由 `makeFun()` 返回的 `IntSupplier` “关闭” `i` 和 `x`，因此当你调用返回的函数时两者仍然有效。 但请注意，我没有像 `Closure1.java` 那样增加i。 尝试递增它会产生编译时错误：

```java
// functional/Closure3.java

// {WillNotCompile}
import java.util.function.*;

public class Closure3 {
  IntSupplier makeFun(int x) {
    int i = 0;
    // Neither x++ nor i++ will work:
    return () -> x++ + i++;
  }
}
```

`x` 和 `i` 的操作都犯了同样的错误：

显然，从 Lambda 表达式引用的局部变量必须是 `final` 或者 实际的 `final` （effectively final），如果我们声明 `x`和 `i` 是 `final` ，它将起作用，因为那时我们不能增加任何一个：

```java
// functional/Closure4.java

import java.util.function.*;

public class Closure4 {
  IntSupplier makeFun(final int x) {
    final int i = 0;
    return () -> x + i;
  }
}
```

但是为什么 `Closure2.java` 在 `x` 和 `i `不是 `final` 却可以运行？

这就是 “实际” `final `（effectively final）的含义出现的地方。 这个术语是为 Java 8 创建的，表示你没有明确地声明变量是 `final` 的，但你仍然是这样对待它 - 你没有改变它。 如果局部变量的初始值永远不会改变，那么它实际上是最终的。

如果 `x` 和 `i` 在方法中的其他位置更改（但不在返回函数内部），则编译器仍将其视为错误。 每个增量产生一个单独的错误消息：

```java
/ functional/Closure5.java

// {WillNotCompile}
import java.util.function.*;

public class Closure5 {
  IntSupplier makeFun(int x) {
    int i = 0;
    i++;
    x++;
    return () -> x + i;
  }
}
```

要成为 “effectively final” ，意味着你可以将 final 关键字应用于变量声明而不更改任何其余代码。 它实际上是 `final`的，你只是没有明说。

我们实际上可以通过在闭包中使用它们之前将 `x` 和  `i` 分配给 `final` 变量来解决 `Closure5.java` 中的问题：

```java

// functional/Closure6.java

import java.util.function.*;

public class Closure6 {
  IntSupplier makeFun(int x) {
    int i = 0;
    i++;
    x++;
    final int iFinal = i;
    final int xFinal = x;
    return () -> xFinal + iFinal;
  }
}
```

由于我们在分配后永远不会更改 `iFinal` 和 `xFinal` ，因此在这里使用 `final` 是多余的。

如果你使用引用怎么办？ 我们可以从 **int** 更改为 **Integer**：

```java
// functional/Closure7.java

// {WillNotCompile}
import java.util.function.*;

public class Closure7 {
  IntSupplier makeFun(int x) {
    Integer i = 0;
    i = i + 1;
    return () -> x + i;
  }
}
```

编译器仍然足够聪明，可以看到 `i` 正在被更改。 包装器类型可能正在进行特殊处理，所以让我们尝试一下List：

```java
// functional/Closure8.java

import java.util.*;
import java.util.function.*;

public class Closure8 {
  Supplier<List<Integer>> makeFun() {
    final List<Integer> ai = new ArrayList<>();
    ai.add(1);
    return () -> ai;
  }
  public static void main(String[] args) {
    Closure8 c7 = new Closure8();
    List<Integer>
      l1 = c7.makeFun().get(),
      l2 = c7.makeFun().get();
    System.out.println(l1);
    System.out.println(l2);
    l1.add(42);
    l2.add(96);
    System.out.println(l1);
    System.out.println(l2);
  }
}
/* Output:
[1]
[1]
[1, 42]
[1, 96]
*/
```

这次它可以运行：我们修改 `List` 的内容而没产生编译时错误。 当你查看此示例的输出时，它看起来确实非常安全，因为每次调用 `makeFun()`时，都会创建并返回一个全新的 `ArrayList`  - 这意味着它不会被共享，因此每个生成的闭包都有自己独立的 `ArrayList`  他们不能互相干扰。

并且请注意我已经声明 `ai` 是 `final` 的，尽管在这个例子中你可以去掉 `final` 并得到相同的结果（试试吧！）。 应用于对象引用的 `final` 关键字仅表示不会重新分配引用。 它并没有说你无法修改对象本身。

看看 `Closure7.java` 和 `Closure8.java` 之间的区别，我们看到 `Closure7.java` 实际上有一个 `i` 的重新分配。 也许这是 “effectively final” 错误消息的触发点：

```java
// functional/Closure9.java

// {WillNotCompile}
import java.util.*;
import java.util.function.*;

public class Closure9 {
  Supplier<List<Integer>> makeFun() {
    List<Integer> ai = new ArrayList<>();
    ai = new ArrayList<>(); // Reassignment
    return () -> ai;
  }
}
```

引用的重新分配确实会触发错误消息。 如果只修改指向的对象，Java 会接受它。 只要没有其他人获得对该对象的引用（这意味着你有多个可以修改对象的实体，此时事情会变得非常混乱），这可能是安全的。[^6]

然而，如果我们现在回顾一下 `Closure1.java.` ，那就有一个难题：`i` 被修改却没有编译器投诉。 它既不是 `final` 的，也不是“effectively final"的。因为 `i` 是外围类的成员，所以这样做肯定是安全的（ 除非你正在创建共享可变内存的多个函数）。实际上，你可以争辩说在这种情况下不会发生变量捕获（variable capture）。 可以肯定的是，`Closure3.java` 的错误消息专门针对局部变量。 因此，规则并不像说“在Lambda之外定义的任何变量必须是 `final` 的或 `effectively final` 那么简单。相反，你必须考虑捕获的变量是否实际 `final`。 如果它是对象中的字段，那么它有一个独立的生存期，并且不需要任何特殊的捕获，以便稍后在调用 Lambda 时存在。

<!-- Inner Classes as Closures -->

### 作为闭包的内部类

我们可以复制我们的例子使用匿名内部类:

```java
// functional/AnonymousClosure.java

import java.util.function.*;

public class AnonymousClosure {
  IntSupplier makeFun(int x) {
    int i = 0;
    // Same rules apply:
    // i++; // Not "effectively final"
    // x++; // Ditto
    return new IntSupplier() {
      public int getAsInt() { return x + i; }
    };
  }
}
```

事实证明，只要有内部类，就会有闭包（Java 8只 会使闭包变得更容易）。 在 Java 8 之前，要求是 `x` 和 `i` 被明确声明为 `final`。 使用 Java 8，内部类的规则已经放宽，包括 “effectively final”。

<!-- Function Composition -->
## 函数组合


函数组合基本上意味着“将函数粘贴在一起以创建新函数”，它通常被认为是函数编程的一部分。你在`TransformFunction.java` 中看到了一个使用 `andThen()` 的函数组合示例。一些 `java.util` 的函数接口包含支持函数组合的方法。

Compositional Supporting Method Interfaces Function BiFunction Consumer BiConsumer IntConsumer andThen(argument) Performs the original LongConsumer operation followed by DoubleConsumer the argument operation.

UnaryOperator IntUnaryOperator LongUnaryOperator DoubleUnaryOperator BinaryOperator Function compose(argument) UnaryOperator Performs the argument IntUnaryOperator operation followed by the original operation. LongUnaryOperator DoubleUnaryOperator Predicate and(argument) BiPredicate Short-circuiting logical IntPredicate AND of the original predicate and the LongPredicate argument predicate. DoublePredicate Predicate or(argument) BiPredicate Short-circuiting logical OR of the original IntPredicate predicate and the LongPredicate argument predicate. DoublePredicate Predicate negate() BiPredicate A predicate that is the IntPredicate logical negation of this predicate. LongPredicate DoublePredicate

( 待整理 )

此示例使用 `Function` 里的  `compose()`和 `andThen()`

```java
// functional/FunctionComposition.java

import java.util.function.*;

public class FunctionComposition {
  static Function<String, String>
    f1 = s -> {
      System.out.println(s);
      return s.replace('A', '_');
    },
    f2 = s -> s.substring(3),
    f3 = s -> s.toLowerCase(),
    f4 = f1.compose(f2).andThen(f3);
  public static void main(String[] args) {
    System.out.println(
      f4.apply("GO AFTER ALL AMBULANCES"));
  }
}
/* Output:
AFTER ALL AMBULANCES
_fter _ll _mbul_nces
*/
```

这里要看的重要一点是我们正在创建一个新函数 `f4`，然后可以使用 `apply()`（几乎）像任何其他函数一样调用它。[^8]

当 `f1` 获得String时，它已经被`f2` 剥离了前三个字符。 这是因为对 `compose（f2）`的调用意味着在 `f1` 之前调用 `f2`。

这是Predicate逻辑运算的演示：

```java
// functional/PredicateComposition.java

import java.util.function.*;
import java.util.stream.*;

public class PredicateComposition {
  static Predicate<String>
    p1 = s -> s.contains("bar"),
    p2 = s -> s.length() < 5,
    p3 = s -> s.contains("foo"),
    p4 = p1.negate().and(p2).or(p3);
  public static void main(String[] args) {
    Stream.of("bar", "foobar", "foobaz", "fongopuckey")
      .filter(p4)
      .forEach(System.out::println);
  }
}
/* Output:
foobar
foobaz
*/
```

`p4` 获取所有谓词并将它们组合成一个更复杂的谓词，其中包含：“如果 `String` 不包含 'bar' 且长度小于5，或者它包含 'foo' ，则结果为 `true`。”因为它产生如此清晰的语法，我在`main()`中作了一些小伎俩，并借用了下一章的内容。 首先，我创建一个 `String` 对象的 “流”（序列），然后将每个对象提供给 `filter()`操作。 `filter()`使用我们的 `p4` 谓词来决定要保留流中的哪个对象以及要丢弃的对象。 最后，我使用 `forEach()`将 `println` 方法引用应用于每个幸存的对象。

你可以从输出中看到 `p4` 是如何工作的：任何带有 “foo ”的东西都会存活，即使它的长度大于5。 “fongopuckey” 太长了，没有 “bar” 来保存它。

<!-- Currying and  Partial Evaluation -->
## Currying和Partial-Evaluation


*Currying* 以 Haskell Curry 命名，Haskell Curry 是其发明者之一，可能是唯一一个以他的名字命名的重要事物的计算机领域的人物（另一个是 Haskell 编程语言）。 Currying 意味着从一个函数开始，该函数接受多个参数，并将其转换为一系列函数，每个函数只接受一个参数。

```java
// functional/CurryingAndPartials.java

import java.util.function.*;

public class CurryingAndPartials {
   // Uncurried:
   static String uncurried(String a, String b) {
      return a + b;
   }
   public static void main(String[] args) {
      // Curried function:
      Function<String, Function<String, String>> sum =
         a -> b -> a + b; // [1]

      System.out.println(uncurried("Hi ", "Ho"));

      Function<String, String>
        hi = sum.apply("Hi "); // [2]
      System.out.println(hi.apply("Ho"));

      // Partial application:
      Function<String, String> sumHi =
        sum.apply("Hup ");
      System.out.println(sumHi.apply("Ho"));
      System.out.println(sumHi.apply("Hey"));
   }
}
/* Output:
Hi Ho
Hi Ho
Hup Ho
Hup Hey
*/
```

**[1]** 这是一条巧妙的线:一连串的箭头。注意，在函数接口声明中，函数的第二个参数是另一个函数。

**[2]** currying 的目标是能够通过提供一个参数来创建一个新函数，所以现在有了一个 “带参函数” 和剩下的 “无参函数” 。实际上，你从一个双参数函数开始，最后得到一个单参数函数。

你可以通过添加另一个级别来 curry 一个三参数函数:

```java
// functional/Curry3Args.java

import java.util.function.*;

public class Curry3Args {
   public static void main(String[] args) {
      Function<String,
        Function<String,
          Function<String, String>>> sum =
            a -> b -> c -> a + b + c;
      Function<String,
        Function<String, String>> hi =
          sum.apply("Hi ");
      Function<String, String> ho =
        hi.apply("Ho ");
      System.out.println(ho.apply("Hup"));
   }
}
/* Output:
Hi Ho Hup
*/
```

对于每个级别的箭头级联（arrow-cascading），你可以围绕类型声明包装另一个函数。

处理原始类型和装箱时，请使用适当的功能接口：

```java
// functional/CurriedIntAdd.java

import java.util.function.*;

public class CurriedIntAdd {
  public static void main(String[] args) {
    IntFunction<IntUnaryOperator>
      curriedIntAdd = a -> b -> a + b;
    IntUnaryOperator add4 = curriedIntAdd.apply(4);
    System.out.println(add4.applyAsInt(5));
  }
}
/* Output:
9
*/
```

你可以在因特网上找到更多 currying 示例。 通常这些是 Java 以外的语言，但如果你理解它们的基本概念，它们应该很容易翻译。

<!-- Pure Functional Programming -->
## 纯函数式编程


在没有函数支持的情况下，即使用像C这样的原始语言，也可以按照一定的原则编写纯函数程序。Java使它比这更容易，但是你必须小心地使一切都成为 `final`，并确保你的所有方法和函数没有副作用。因为 Java 本质上不是一种不可变的语言，所以如果你犯了错误，编译器不会提供任何帮助。

有第三方工具可以帮助你[^9]，但是使用 `Scala` 或 `Clojure` 这样的语言可能更容易，因为它们从一开始就是为保持不变性而设计的。这些语言使你可以用 Java 编写项目的一部分，如果你必须用纯函数式编写，则可以用 `Scala` 编写其他部分 (这需要一些规则) 或 `Clojure` (这需要的少得多)。虽然你将在并发编程一章中看到 Java确实支持并发，但是如果这是你项目的核心部分，你可能会考虑至少在项目的一部分中使用 `Scala` 或 `Clojure`之类的语言。

<!-- Summary -->
## 本章小结


Lambda 表达式和方法引用并没有将 Java 转换成函数式语言，而是提供了对函数式编程的支持。它们对 Java 是一个巨大的改进，因为它们允许你编写更简洁、更干净、更容易理解的代码。在下一章中，你将看到它们如何启用流。如果你像我一样，你会喜欢流媒体。

这些特性可能会满足大部分 Java 程序员的需求，他们已经对 Clojure 和 Scala 等新的、功能更强的语言感到不安和嫉妒，并阻止 Java 程序员流向这些语言 (或者，如果他们仍然决定迁移，至少会为他们做好更好的准备)。

但是，Lambdas 和方法引用远非完美，我们永远要为 Java 设计人员在早期令人兴奋的语言中做出的草率决定付出代价。特别是，没有泛型 Lambda，所以 Lambda 实际上不是 Java 中的第一类公民。这并不意味着 Java 8不是一个很大的改进，但它确实意味着，就像许多 Java 特性一样，你最终会感到沮丧。

当你遇到学习困难时，请记住，你可以从 ide (如 NetBeans、IntelliJ Idea 和 Eclipse )获得帮助，这些 ide 将建议你何时可以使用 Lambda 表达式或方法引用 (并且经常为你重写代码!)

<!--下面是脚注-->

1. 粘贴功能结合在一起是一个非常不同的方法,但它仍然使一种图书馆。
2. 例如,这个电子书是利用 Pandoc 制作出来的，纯函数式语言编写的一个程序 Haskell。
3. 有时函数语言将其描述为“代码即数据”。“
4. 这个语法来自 C++。
5. 我还没有验证过这种说法。
6. 在并发编程一章中，当你理解更改共享变量 “不是线程安全的” 时，这将更有意义。
7. 接口能够支持方法的原因是它们是 Java 8 默认方法，你将在下一章中了解到。
8. 一些语言，例如 Python，允许像调用其他函数一样调用组合函数。但这是 Java，所以我们取我们能得到的。
9. 见,例如，不可变和可变性检测器。( Immutables and Mutability Detector)



<!-- 分页 -->

<div style="page-break-after: always;"></div>