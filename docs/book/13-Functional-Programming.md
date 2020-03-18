[TOC]

<!-- Functional Programming -->
# 第十三章 函数式编程



> 函数式编程语言操纵代码片段就像操作数据一样容易。 虽然 Java 不是函数式语言，但 Java 8 Lambda 表达式和方法引用 (Method References) 允许你以函数式编程。

在计算机时代早期，内存是稀缺和昂贵的。几乎每个人都用汇编语言编程。人们对编译器有所了解，但仅仅想到编译生成的代码肯定会比手工编码多很多字节。

通常，只是为了使程序适合有限的内存，程序员通过修改内存中的代码来节省代码空间，以便在程序执行时执行不同的操作。这种技术被称为**自修改代码** （self-modifying code）。只要程序足够小，少数人可以维护所有棘手和神秘的汇编代码，你就可以让它运行起来。

随着内存和处理器变得更便宜、更快。C 语言出现并被大多数汇编程序员认为更“高级”。人们发现使用 C 可以显著提高生产力。同时，使用 C 创建自修改代码仍然不难。

随着硬件越来越便宜，程序的规模和复杂性都在增长。这一切只是让程序工作变得困难。我们想方设法使代码更加一致和易懂。使用纯粹的自修改代码造成的结果就是：我们很难确定程序在做什么。它也难以测试：除非你想一点点测试输出，代码转换和修改等等过程？

然而，使用代码以某种方式操纵其他代码的想法也很有趣，只要能保证它更安全。从代码创建，维护和可靠性的角度来看，这个想法非常吸引人。我们不用从头开始编写大量代码，而是从易于理解、充分测试及可靠的现有小块开始，最后将它们组合在一起以创建新代码。难道这不会让我们更有效率，同时创造更健壮的代码吗？

这就是**函数式编程**（FP）的意义所在。通过合并现有代码来生成新功能而不是从头开始编写所有内容，我们可以更快地获得更可靠的代码。至少在某些情况下，这套理论似乎很有用。在这一过程中，一些非函数式语言已经习惯了使用函数式编程产生的优雅的语法。

你也可以这样想：

OO（object oriented，面向对象）是抽象数据，FP（functional programming，函数式编程）是抽象行为。

纯粹的函数式语言在安全性方面更进一步。它强加了额外的约束，即所有数据必须是不可变的：设置一次，永不改变。将值传递给函数，该函数然后生成新值但从不修改自身外部的任何东西（包括其参数或该函数范围之外的元素）。当强制执行此操作时，你知道任何错误都不是由所谓的副作用引起的，因为该函数仅创建并返回结果，而不是其他任何错误。

更好的是，“不可变对象和无副作用”范式解决了并发编程中最基本和最棘手的问题之一（当程序的某些部分同时在多个处理器上运行时）。这是可变共享状态的问题，这意味着代码的不同部分（在不同的处理器上运行）可以尝试同时修改同一块内存（谁赢了？没人知道）。如果函数永远不会修改现有值但只生成新值，则不会对内存产生争用，这是纯函数式语言的定义。 因此，经常提出纯函数式语言作为并行编程的解决方案（还有其他可行的解决方案）。

需要提醒大家的是，函数式语言背后有很多动机，这意味着描述它们可能会有些混淆。它通常取决于各种观点：为“并行编程”，“代码可靠性”和“代码创建和库复用”。[^1] 关于函数式编程能高效创建更健壮的代码这一观点仍存在部分争议。虽然已有一些好的范例[^2]，但还不足以证明纯函数式语言就是解决编程问题的最佳方法。

FP 思想值得融入非 FP 语言，如 Python。Java 8 也从中吸收并支持了 FP。我们将在此章探讨。


<!-- Old vs. New -->
## 新旧对比


通常，传递给方法的数据不同，结果不同。如果我们希望方法在调用时行为不同，该怎么做呢？结论是：只要能将代码传递给方法，我们就可以控制它的行为。此前，我们通过在方法中创建包含所需行为的对象，然后将该对象传递给我们想要控制的方法来完成此操作。下面我们用传统形式和 Java 8 的方法引用、Lambda 表达式分别演示。代码示例：

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
HELLO THERE!
Hello
Hello there Hello there
```

**Strategy** 接口提供了单一的 `approach()` 方法来承载函数式功能。通过创建不同的 **Strategy** 对象，我们可以创建不同的行为。

传统上，我们通过创建一个实现 **Strategy** 接口的类来实现此行为，比如在 **Soft**。

- **[1]** 在 **Strategize** 中，**Soft** 作为默认策略，在构造函数中赋值。

- **[2]** 一种略显简短且更自发的方法是创建一个**匿名内部类**。即使这样，仍有相当数量的冗余代码。你总是要仔细观察：“哦，原来这样，这里使用了匿名内部类。”

- **[3]** Java 8 的 Lambda 表达式。由箭头 `->` 分隔开参数和函数体，箭头左边是参数，箭头右侧是从 Lambda 返回的表达式，即函数体。这实现了与定义类、匿名内部类相同的效果，但代码少得多。

- **[4]** Java 8 的**方法引用**，由 `::` 区分。在 `::` 的左边是类或对象的名称，在 `::` 的右边是方法的名称，但没有参数列表。

- **[5]** 在使用默认的 **Soft**  **strategy** 之后，我们逐步遍历数组中的所有 **Strategy**，并使用 `changeStrategy()` 方法将每个 **Strategy** 放入 变量 `s` 中。

- **[6]** 现在，每次调用 `communicate()` 都会产生不同的行为，具体取决于此刻正在使用的策略**代码对象**。我们传递的是行为，而非仅数据。[^3]

在 Java 8 之前，我们能够通过 **[1]** 和 **[2]** 的方式传递功能。然而，这种语法的读写非常笨拙，并且我们别无选择。方法引用和 Lambda 表达式的出现让我们可以在需要时**传递功能**，而不是仅在必要才这么做。

<!-- Lambda Expressions -->

## Lambda表达式


Lambda 表达式是使用**最小可能**语法编写的函数定义：

1. Lambda 表达式产生函数，而不是类。 在 JVM（Java Virtual Machine，Java 虚拟机）上，一切都是一个类，因此在幕后执行各种操作使 Lambda 看起来像函数 —— 但作为程序员，你可以高兴地假装它们“只是函数”。

2. Lambda 语法尽可能少，这正是为了使 Lambda 易于编写和使用。

我们在 **Strategize.java** 中看到了一个 Lambda 表达式，但还有其他语法变体：

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

递归函数是一个自我调用的函数。可以编写递归的 Lambda 表达式，但需要注意：递归方法必须是实例变量或静态变量，否则会出现编译时错误。 我们将为每个案例创建一个示例。

这两个示例都需要一个接受 **int** 型参数并生成 **int** 的接口：

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
```

输出结果：

```
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
```

这里，`fact` 是一个静态变量。 注意使用三元 **if-else**。 递归函数将一直调用自己，直到 `i == 0`。所有递归函数都有“停止条件”，否则将无限递归并产生异常。

我们可以将 `Fibonacci` 序列改为使用递归 Lambda 表达式来实现，这次使用实例变量：

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
```

输出结果：

```
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
```

将 `Fibonacci` 序列中的最后两个元素求和来产生下一个元素。

<!-- method references-->

## 方法引用


Java 8 方法引用没有历史包袱。方法引用组成：类名或对象名，后面跟 `::` [^4]，然后跟方法名称。

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
```

输出结果：

```
call()
Hello, Bob
valuable information
Help!
```

**[1]** 我们从单一方法接口开始（同样，你很快就会了解到这一点的重要性）。

**[2]** `show()` 的签名（参数类型和返回类型）符合 **Callable** 的 `call()` 的签名。

**[3]** `hello()` 也符合 `call()` 的签名。 

**[4]**  `help()` 也符合，它是静态内部类中的非静态方法。

**[5]** `assist()` 是静态内部类中的静态方法。

**[6]** 我们将 **Describe** 对象的方法引用赋值给 **Callable** ，它没有 `show()` 方法，而是 `call()` 方法。 但是，Java 似乎接受用这个看似奇怪的赋值，因为方法引用符合 **Callable** 的 `call()` 方法的签名。

**[7]** 我们现在可以通过调用 `call()` 来调用 `show()`，因为 Java 将 `call()` 映射到 `show()`。

**[8]** 这是一个**静态**方法引用。

**[9]** 这是 **[6]** 的另一个版本：对已实例化对象的方法的引用，有时称为*绑定方法引用*。

**[10]** 最后，获取静态内部类的方法引用的操作与 **[8]** 中外部类方式一样。

上例只是简短的介绍，我们很快就能看到方法引用的全部变化。

### Runnable接口

**Runnable** 接口自 1.0 版以来一直在 Java 中，因此不需要导入。它也符合特殊的单方法接口格式：它的方法 `run()` 不带参数，也没有返回值。因此，我们可以使用 Lambda 表达式和方法引用作为 **Runnable**：

```java
// functional/RunnableMethodReference.java

// 方法引用与 Runnable 接口的结合使用

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
```

输出结果：

```
Anonymous
lambda
Go::go()
```

**Thread** 对象将 **Runnable** 作为其构造函数参数，并具有会调用 `run()` 的方法  `start()`。 **注意**，只有**匿名内部类**才需要具有名为 `run()` 的方法。


<!-- Unbound Method References -->
### 未绑定的方法引用


未绑定的方法引用是指没有关联对象的普通（非静态）方法。 使用未绑定的引用之前，我们必须先提供对象：

```java
// functional/UnboundMethodReference.java

// 没有方法引用的对象

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
    System.out.println(x.f()); // 同等效果
  }
}
```

输出结果：

```
X::f()
X::f()
```


截止目前，我们已经知道了与接口方法同名的方法引用。 在 **[1]**，我们尝试把 `X` 的 `f()` 方法引用赋值给 **MakeString**。结果：即使 `make()` 与 `f()` 具有相同的签名，编译也会报“invalid method reference”（无效方法引用）错误。 这是因为实际上还有另一个隐藏的参数：我们的老朋友 `this`。 你不能在没有 `X` 对象的前提下调用 `f()`。 因此，`X :: f` 表示未绑定的方法引用，因为它尚未“绑定”到对象。

要解决这个问题，我们需要一个 `X` 对象，所以我们的接口实际上需要一个额外的参数的接口，如上例中的 **TransformX**。 如果将 `X :: f` 赋值给 **TransformX**，这在 Java 中是允许的。这次我们需要调整下心里预期——使用未绑定的引用时，函数方法的签名（接口中的单个方法）不再与方法引用的签名完全匹配。 理由是：你需要一个对象来调用方法。

**[2]** 的结果有点像脑筋急转弯。 我接受未绑定的引用并对其调用 `transform()`，将其传递给 `X`，并以某种方式导致对 `x.f()` 的调用。 Java 知道它必须采用第一个参数，这实际上就是 `this`，并在其上调用方法。

```java
// functional/MultiUnbound.java

// 未绑定的方法与多参数的结合运用

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

**Dog** 有三个构造函数，函数接口内的 `make()` 方法反映了构造函数参数列表（ `make()` 方法名称可以不同）。

**注意**我们如何对 **[1]**，**[2]** 和 **[3]** 中的每一个使用 `Dog :: new`。 这 3 个构造函数只有一个相同名称：`:: new`，但在每种情况下都赋值给不同的接口。编译器可以检测并知道从哪个构造函数引用。

编译器能识别并调用你的构造函数（ 在本例中为 `make()`）。

<!-- Functional Interfaces -->
## 函数式接口


方法引用和 Lambda 表达式必须被赋值，同时编译器需要识别类型信息以确保类型正确。 Lambda 表达式特别引入了新的要求。 代码示例：

```java
x -> x.toString()
```

我们清楚这里返回类型必须是 **String**，但 `x` 是什么类型呢？

Lambda 表达式包含类型推导（编译器会自动推导出类型信息，避免了程序员显式地声明）。编译器必须能够以某种方式推导出 `x` 的类型。

下面是第 2 个代码示例：

```java
(x, y) -> x + y
```

现在 `x` 和 `y` 可以是任何支持 `+` 运算符连接的数据类型，可以是两个不同的数值类型或者是 1 个 **String** 加任意一种可自动转换为 **String** 的数据类型（这包括了大多数类型）。 但是，当 Lambda 表达式被赋值时，编译器必须确定 `x` 和 `y` 的确切类型以生成正确的代码。

该问题也适用于方法引用。 假设你要传递 `System.out :: println` 到你正在编写的方法 ，你怎么知道传递给方法的参数的类型？

为了解决这个问题，Java 8 引入了 `java.util.function` 包。它包含一组接口，这些接口是 Lambda 表达式和方法引用的目标类型。 每个接口只包含一个抽象方法，称为函数式方法。

在编写接口时，可以使用 `@FunctionalInterface` 注解强制执行此“函数式方法”模式：

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
产生错误信息:
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

`@FunctionalInterface` 注解是可选的; Java 在 `main()` 中把 **Functional** 和 **FunctionalNoAnn** 都当作函数式接口。 `@FunctionalInterface` 的值在 `NotFunctional` 的定义中可见：接口中如果有多个方法则会产生编译时错误消息。

仔细观察在定义 `f` 和 `fna` 时发生了什么。 `Functional` 和 `FunctionalNoAnn` 定义接口，然而被赋值的只是方法 `goodbye()`。首先，这只是一个方法而不是类；其次，它甚至都不是实现了该接口的类中的方法。Java 8 在这里添加了一点小魔法：如果将方法引用或 Lambda 表达式赋值给函数式接口（类型需要匹配），Java 会适配你的赋值到目标接口。 编译器会自动包装方法引用或 Lambda 表达式到实现目标接口的类的实例中。

尽管 `FunctionalAnnotation` 确实适合 `Functional` 模型，但 Java 不允许我们将 `FunctionalAnnotation` 像 `fac` 定义一样直接赋值给 `Functional`，因为它没有明确地实现 `Functional` 接口。 令人惊奇的是 ，Java 8 允许我们以简便的语法为接口赋值函数。

`java.util.function` 包旨在创建一组完整的目标接口，使得我们一般情况下不需再定义自己的接口。这主要是因为基本类型会产生一小部分接口。 如果你了解命名模式，顾名思义就能知道特定接口的作用。

 以下是基本命名准则：

1. 如果只处理对象而非基本类型，名称则为 `Function`，`Consumer`，`Predicate` 等。参数类型通过泛型添加。

2. 如果接收的参数是基本类型，则由名称的第一部分表示，如 `LongConsumer`，`DoubleFunction`，`IntPredicate` 等，但基本 `Supplier` 类型例外。

3. 如果返回值为基本类型，则用 `To` 表示，如 `ToLongFunction <T>` 和 `IntToLongFunction`。

4. 如果返回值类型与参数类型一致，则是一个运算符：单个参数使用 `UnaryOperator`，两个参数使用 `BinaryOperator`。

5. 如果接收两个参数且返回值为布尔值，则是一个谓词（Predicate）。

6. 如果接收的两个参数类型不同，则名称中有一个 `Bi`。

下表描述了 `java.util.function` 中的目标类型（包括例外情况）：

| **特征** |**函数式方法名**|**示例**|
| :---- | :----: | :----: |
|无参数； <br> 无返回值|**Runnable** <br> (java.lang)  <br>  `run()`|**Runnable**|
|无参数； <br> 返回类型任意|**Supplier** <br> `get()` <br> `getAs类型()`| **Supplier`<T>`  <br> BooleanSupplier  <br> IntSupplier  <br> LongSupplier  <br> DoubleSupplier**|
|无参数； <br> 返回类型任意|**Callable** <br> (java.util.concurrent)  <br> `call()`|**Callable`<V>`**|
|1 参数； <br> 无返回值|**Consumer** <br> `accept()`|**`Consumer<T>` <br> IntConsumer <br> LongConsumer <br> DoubleConsumer**|
|2 参数 **Consumer**|**BiConsumer** <br> `accept()`|**`BiConsumer<T,U>`**|
|2 参数 **Consumer**； <br> 1 引用； <br> 1 基本类型|**Obj类型Consumer** <br> `accept()`|**`ObjIntConsumer<T>` <br> `ObjLongConsumer<T>` <br> `ObjDoubleConsumer<T>`**|
|1 参数； <br> 返回类型不同|**Function** <br> `apply()` <br> **To类型** 和 **类型To类型** <br> `applyAs类型()`|**Function`<T,R>` <br> IntFunction`<R>` <br> `LongFunction<R>` <br> DoubleFunction`<R>` <br> ToIntFunction`<T>` <br> `ToLongFunction<T>` <br> `ToDoubleFunction<T>` <br> IntToLongFunction <br> IntToDoubleFunction <br> LongToIntFunction <br> LongToDoubleFunction <br> DoubleToIntFunction <br> DoubleToLongFunction**|
|1 参数； <br> 返回类型相同|**UnaryOperator** <br> `apply()`|**`UnaryOperator<T>` <br> IntUnaryOperator <br> LongUnaryOperator <br> DoubleUnaryOperator**|
|2 参数类型相同； <br> 返回类型相同|**BinaryOperator** <br> `apply()`|**`BinaryOperator<T>` <br> IntBinaryOperator <br> LongBinaryOperator <br> DoubleBinaryOperator**|
|2 参数类型相同; <br> 返回整型|Comparator <br> (java.util) <br> `compare()`|**`Comparator<T>`**|
|2 参数； <br> 返回布尔型|**Predicate** <br> `test()`|**`Predicate<T>` <br> `BiPredicate<T,U>` <br> IntPredicate <br> LongPredicate <br> DoublePredicate**|
|参数基本类型； <br> 返回基本类型|**类型To类型Function** <br> `applyAs类型()`|**IntToLongFunction <br> IntToDoubleFunction <br> LongToIntFunction <br> LongToDoubleFunction <br> DoubleToIntFunction <br> DoubleToLongFunction**|
|2 参数类型不同|**Bi操作** <br> (不同方法名)|**`BiFunction<T,U,R>` <br> `BiConsumer<T,U>` <br> `BiPredicate<T,U>` <br> `ToIntBiFunction<T,U>` <br> `ToLongBiFunction<T,U>` <br> `ToDoubleBiFunction<T>`**|


此表仅提供些常规方案。通过上表，你应该或多或少能自行推导出更多行的函数式接口。

可以看出，在创建 `java.util.function` 时，设计者们做出了一些选择。 

例如，为什么没有 `IntComparator`，`LongComparator` 和 `DoubleComparator` 呢？有 `BooleanSupplier` 却没有其他表示 **Boolean** 的接口；有通用的 `BiConsumer` 却没有用于 **int**，**long** 和 **double** 的 `BiConsumers` 变体（我对他们放弃的原因表示同情）。这些选择是疏忽还是有人认为其他组合的使用情况出现得很少（他们是如何得出这个结论的）？

你还可以看到基本类型给 Java 添加了多少复杂性。为了缓和效率问题，该语言的第一版中就包含了基本类型。现在，在语言的生命周期中，我们仍然受到语言设计选择不佳的影响。

下面枚举了基于 Lambda 表达式的所有不同 **Function** 变体的示例：

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

这些 Lambda 表达式尝试生成适合函数签名的最简代码。 在某些情况下，有必要进行强制类型转换，否则编译器会报截断错误。

主方法中的每个测试都显示了 `Function` 接口中不同类型的 `apply()` 方法。 每个都产生一个与其关联的 Lambda 表达式的调用。

方法引用有自己的小魔法：

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
```

输出结果：

```
accept()
someOtherName()
```

查看 `BiConsumer` 的文档，你会看到 `accept()` 方法。 实际上，如果我们将方法命名为 `accept()`，它就可以作为方法引用。 但是我们也可用不同的名称，比如 `someOtherName()`。只要参数类型、返回类型与 `BiConsumer` 的 `accept()` 相同即可。

因此，在使用函数接口时，名称无关紧要——只要参数类型和返回类型相同。 Java 会将你的方法映射到接口方法。 要调用方法，可以调用接口的函数式方法名（在本例中为 `accept()`），而不是你的方法名。

现在我们来看看所有基于类的函数式，应用于方法引用（即那些不涉及基本类型的函数）。下例我们创建了一个最简单的函数式签名。代码示例：

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

请**注意**，每个方法名称都是随意的（如 `f1()`，`f2()`等）。正如你刚才看到的，一旦将方法引用赋值给函数接口，我们就可以调用与该接口关联的函数方法。 在此示例中为 `get()`、`compare()`、`accept()`、`apply()` 和 `test()`。


<!-- Functional Interfaces with More Arguments -->
### 多参数函数式接口

`java.util.functional` 中的接口是有限的。比如有了 `BiFunction`，但它不能变化。 如果需要三参数函数的接口怎么办？ 其实这些接口非常简单，很容易查看 Java 库源代码并自行创建。代码示例：

```java
// functional/TriFunction.java

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
```

简单测试，验证它是否有效：

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

这里我们测试了方法引用和 Lambda 表达式。

### 缺少基本类型的函数

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
```

输出结果：

```
47, 11.340000
92, 22.450000
1, 11
```

这里使用 `System.out.format()` 来显示。它类似于 `System.out.println()` 但提供了更多的显示选项。 这里，`%f` 表示我将 `n` 作为浮点值给出，`%d` 表示 `n` 是一个整数值。 这其中可以包含空格，输入 `%n` 会换行 — 当然使用传统的 `\n` 也能换行，但 `%n` 是自动跨平台的，这是使用 `format()` 的另一个原因。

上例简单使用了包装类型，装箱和拆箱用于在基本类型之间来回转换。 我们也可以使用包装类型，如 `Function`，而不是预定义的基本类型。代码示例：

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

如果没有强制转换，则会收到错误消息：“Integer cannot be converted to Double”（**Integer** 无法转换为 **Double**），而使用 **IntToDoubleFunction** 就没有此类问题。 **IntToDoubleFunction** 接口的源代码是这样的：

```java
@FunctionalInterface 
public interface IntToDoubleFunction { 
  double applyAsDouble(int value); 
}
```

之所以我们可以简单地编写 `Function <Integer，Double>` 并返回合适的结果，很明显是为了性能。使用基本类型可以防止传递参数和返回结果过程中的自动装箱和自动拆箱。

似乎是考虑到使用频率，某些函数类型并没有预定义。

当然，如果因缺少基本类型而造成的性能问题，你也可以轻松编写自己的接口（ 参考 Java 源代码）——尽管这里出现性能瓶颈的可能性不大。

<!-- Higher-Order Functions-->
## 高阶函数


这个名字可能听起来令人生畏，但是：[高阶函数](https://en.wikipedia.org/wiki/Higher-order_function)（Higher-order Function）只是一个消费或产生函数的函数。

我们先来看看如何产生一个函数：

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
```

输出结果：
```
yelling
```

这里，`produce()` 是高阶函数。

**[1]** 使用继承，可以轻松地为专用接口创建别名。

**[2]** 使用 Lambda 表达式，可以轻松地在方法中创建和返回一个函数。

要消费一个函数，消费函数需要在参数列表正确地描述函数类型。代码示例：

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

当基于消费函数生成新函数时，事情就变得相当有趣了。代码示例如下：

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
```

输出结果：

```
I
O
```

在这里，`transform()` 生成一个与传入的函数具有相同签名的函数，但是你可以生成任何你想要的类型。

这里使用到了 `Function` 接口中名为 `andThen()` 的默认方法，该方法专门用于操作函数。 顾名思义，在调用 `in` 函数之后调用 `toThen()`（还有个 `compose()` 方法，它在 `in` 函数之前应用新函数）。 要附加一个 `andThen()` 函数，我们只需将该函数作为参数传递。 `transform()` 产生的是一个新函数，它将 `in` 的动作与 `andThen()` 参数的动作结合起来。

<!-- Closures -->

## 闭包


在上一节的 `ProduceFunction.java` 中，我们从方法中返回 Lambda 函数。 虽然过程简单，但是有些问题必须再回过头来探讨一下。

**闭包**（Closure）一词总结了这些问题。 它非常重要，利用闭包可以轻松生成函数。

考虑一个更复杂的 Lambda，它使用函数作用域之外的变量。 返回该函数会发生什么？ 也就是说，当你调用函数时，它对那些 “外部 ”变量引用了什么?  如果语言不能自动解决这个问题，那将变得非常具有挑战性。 能够解决这个问题的语言被称为**支持闭包**，或者叫作在词法上限定范围( 也使用术语*变量捕获* )。Java 8 提供了有限但合理的闭包支持，我们将用一些简单的例子来研究它。

首先，下例函数中，方法返回访问对象字段和方法参数。代码示例：

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

但是，仔细考虑一下，`i` 的这种用法并非是个大难题，因为对象很可能在你调用 `makeFun()` 之后就存在了——实际上，垃圾收集器几乎肯定会保留一个对象，并将现有的函数以这种方式绑定到该对象上[^5]。当然，如果你对同一个对象多次调用 `makeFun()` ，你最终会得到多个函数，它们共享 `i` 的存储空间：
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
```

输出结果：

```
0
1
2
```

每次调用 `getAsInt()` 都会增加 `i`，表明存储是共享的。

如果 `i` 是 `makeFun()` 的局部变量怎么办？ 在正常情况下，当 `makeFun()` 完成时 `i` 就消失。 但它仍可以编译：

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

由 `makeFun()` 返回的 `IntSupplier` “关闭” `i` 和 `x`，因此当你调用返回的函数时两者仍然有效。 但请**注意**，我没有像 `Closure1.java` 那样递增 `i`，因为会产生编译时错误。代码示例：

```java
// functional/Closure3.java

// {WillNotCompile}
import java.util.function.*;

public class Closure3 {
  IntSupplier makeFun(int x) {
    int i = 0;
    // x++ 和 i++ 都会报错：
    return () -> x++ + i++;
  }
}
```

`x` 和 `i` 的操作都犯了同样的错误：从 Lambda 表达式引用的局部变量必须是 `final` 或者是等同 `final` 效果的。

如果使用 `final` 修饰 `x`和 `i`，就不能再递增它们的值了。代码示例：

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

那么为什么在 `Closure2.java` 中， `x` 和 `i` 非 `final` 却可以运行呢？

这就叫做**等同 final 效果**（Effectively Final）。这个术语是在 Java 8 才开始出现的，表示虽然没有明确地声明变量是 `final` 的，但是因变量值没被改变过而实际有了 `final` 同等的效果。 如果局部变量的初始值永远不会改变，那么它实际上就是 `final` 的。

如果 `x` 和 `i` 的值在方法中的其他位置发生改变（但不在返回的函数内部），则编译器仍将视其为错误。每个递增操作则会分别产生错误消息。代码示例：

```java
/ functional/Closure5.java

// {无法编译成功}
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

**等同 final 效果**意味着可以在变量声明前加上 **final** 关键字而不用更改任何其余代码。 实际上它就是具备 `final` 效果的，只是没有明确说明。

通过在闭包中使用 `final` 关键字提前修饰变量 `x` 和  `i` ， 我们解决了 `Closure5.java` 中的问题。代码示例：

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

上例中 `iFinal` 和 `xFinal` 的值在赋值后并没有改变过，因此在这里使用 `final` 是多余的。

如果这里是引用的话，需要把 **int** 型更改为 **Integer** 型。代码示例：

```java
// functional/Closure7.java

// {无法编译成功}
import java.util.function.*;

public class Closure7 {
  IntSupplier makeFun(int x) {
    Integer i = 0;
    i = i + 1;
    return () -> x + i;
  }
}
```

编译器非常智能，它能识别变量 `i` 的值被更改过了。 对于包装类型的处理可能比较特殊，因此我们尝试下 **List**：

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
```

输出结果：

```
[1]
[1]
[1, 42]
[1, 96]
```

可以看到，这次一切正常。我们改变了 **List** 的值却没产生编译时错误。通过观察本例的输出结果，我们发现这看起来非常安全。这是因为每次调用 `makeFun()` 时，其实都会创建并返回一个全新的 `ArrayList`。 也就是说，每个闭包都有自己独立的 `ArrayList`， 它们之间互不干扰。

请**注意**我已经声明 `ai` 是 `final` 的了。尽管在这个例子中你可以去掉 `final` 并得到相同的结果（试试吧！）。 应用于对象引用的 `final` 关键字仅表示不会重新赋值引用。 它并不代表你不能修改对象本身。

下面我们来看看 `Closure7.java` 和 `Closure8.java` 之间的区别。我们看到：在 `Closure7.java` 中变量 `i` 有过重新赋值。 也许这就是**等同 final 效果**错误消息的触发点。

```java
// functional/Closure9.java

// {无法编译成功}
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

上例，重新赋值引用会触发错误消息。如果只修改指向的对象则没问题，只要没有其他人获得对该对象的引用（这意味着你有多个实体可以修改对象，此时事情会变得非常混乱），基本上就是安全的[^6]。

让我们回顾一下 `Closure1.java`。那么现在问题来了：为什么变量 `i` 被修改编译器却没有报错呢。 它既不是 `final` 的，也不是**等同 final 效果**的。因为 `i` 是外围类的成员，所以这样做肯定是安全的（除非你正在创建共享可变内存的多个函数）。是的，你可以辩称在这种情况下不会发生变量捕获（Variable Capture）。但可以肯定的是，`Closure3.java` 的错误消息是专门针对局部变量的。因此，规则并非只是“在 Lambda 之外定义的任何变量必须是 `final` 的或**等同 final 效果**那么简单。相反，你必须考虑捕获的变量是否是**等同 final 效果**的。 如果它是对象中的字段，那么它拥有独立的生存周期，并且不需要任何特殊的捕获，以便稍后在调用 Lambda 时存在。

<!-- Inner Classes as Closures -->

### 作为闭包的内部类

我们可以使用匿名内部类重写之前的例子:

```java
// functional/AnonymousClosure.java

import java.util.function.*;

public class AnonymousClosure {
  IntSupplier makeFun(int x) {
    int i = 0;
    // 同样规则的应用:
    // i++; // 非等同 final 效果
    // x++; // 同上
    return new IntSupplier() {
      public int getAsInt() { return x + i; }
    };
  }
}
```

实际上只要有内部类，就会有闭包（Java 8 只是简化了闭包操作）。在 Java 8 之前，变量 `x` 和 `i` 必须被明确声明为 `final`。在 Java 8 中，内部类的规则放宽，包括**等同 final 效果**。

<!-- Function Composition -->
## 函数组合


函数组合（Function Composition）意为“多个函数组合成新函数”。它通常是函数式编程的基本组成部分。在前面的 `TransformFunction.java` 类中，有一个使用 `andThen()` 的函数组合示例。一些 `java.util.function` 接口中包含支持函数组合的方法 [^7]。

| 组合方法 | 支持接口 |
| :----- | :----- |
| `andThen(argument)` <br> 根据参数执行原始操作 | **Function <br> BiFunction <br> Consumer <br> BiConsumer <br> IntConsumer <br> LongConsumer <br> DoubleConsumer <br> UnaryOperator <br> IntUnaryOperator <br> LongUnaryOperator <br> DoubleUnaryOperator <br> BinaryOperator** |
| `compose(argument)` <br> 根据参数执行原始操作 | **Function <br> UnaryOperator <br> IntUnaryOperator <br> LongUnaryOperator <br> DoubleUnaryOperator** |
| `and(argument)`  <br> 短路**逻辑与**原始谓词和参数谓词 | **Predicate <br> BiPredicate <br> IntPredicate <br> LongPredicate <br> DoublePredicate** |
| `or(argument)` <br> 短路**逻辑或**原始谓词和参数谓词 | **Predicate <br> BiPredicate <br> IntPredicate <br> LongPredicate <br> DoublePredicate** |
| `negate()` <br> 该谓词的**逻辑否**谓词| **Predicate <br> BiPredicate <br> IntPredicate <br> LongPredicate <br> DoublePredicate** |


下例使用了 `Function` 里的 `compose()`和 `andThen()`。代码示例：

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
```

输出结果：

```
AFTER ALL AMBULANCES
_fter _ll _mbul_nces
```

这里我们重点看正在创建的新函数 `f4`。它调用 `apply()` 的方式与常规几乎无异[^8]。

当 `f1` 获得字符串时，它已经被`f2` 剥离了前三个字符。这是因为 `compose（f2）` 表示 `f2` 的调用发生在 `f1` 之前。

下例是 `Predicate` 的逻辑运算演示.代码示例：

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
```

输出结果：

```
foobar
foobaz
```

`p4` 获取到了所有谓词并组合成一个更复杂的谓词。解读：如果字符串中不包含 `bar` 且长度小于 5，或者它包含 `foo` ，则结果为 `true`。

正因它产生如此清晰的语法，我在主方法中采用了一些小技巧，并借用了下一章的内容。首先，我创建了一个字符串对象的流，然后将每个对象传递给 `filter()` 操作。 `filter()` 使用 `p4` 的谓词来确定对象的去留。最后我们使用 `forEach()` 将 `println` 方法引用应用在每个留存的对象上。

从输出结果我们可以看到 `p4` 的工作流程：任何带有 `foo` 的东西都会留下，即使它的长度大于 5。 `fongopuckey` 因长度超出和不包含 `bar` 而被丢弃。

<!-- Currying and  Partial Evaluation -->
## 柯里化和部分求值

[柯里化](https://en.wikipedia.org/wiki/Currying)（Currying）的名称来自于其发明者之一 *Haskell Curry*。他可能是计算机领域唯一名字被命名重要概念的人（另外就是 Haskell 编程语言）。 柯里化意为：将一个多参数的函数，转换为一系列单参数函数。

```java
// functional/CurryingAndPartials.java

import java.util.function.*;

public class CurryingAndPartials {
   // 未柯里化:
   static String uncurried(String a, String b) {
      return a + b;
   }
   public static void main(String[] args) {
      // 柯里化的函数:
      Function<String, Function<String, String>> sum =
         a -> b -> a + b; // [1]

      System.out.println(uncurried("Hi ", "Ho"));

      Function<String, String>
        hi = sum.apply("Hi "); // [2]
      System.out.println(hi.apply("Ho"));

      // 部分应用:
      Function<String, String> sumHi =
        sum.apply("Hup ");
      System.out.println(sumHi.apply("Ho"));
      System.out.println(sumHi.apply("Hey"));
   }
}
```

输出结果：

```
Hi Ho
Hi Ho
Hup Ho
Hup Hey
```

**[1]** 这一连串的箭头很巧妙。*注意*，在函数接口声明中，第二个参数是另一个函数。

**[2]** 柯里化的目的是能够通过提供一个参数来创建一个新函数，所以现在有了一个“带参函数”和剩下的 “无参函数” 。实际上，你从一个双参数函数开始，最后得到一个单参数函数。

我们可以通过添加级别来柯里化一个三参数函数：

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
```

输出结果：

```
Hi Ho Hup
```

对于每个级别的箭头级联（Arrow-cascading），你在类型声明中包裹了另一个 **Function**。

处理基本类型和装箱时，请使用适当的 **Function** 接口：

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
```

输出结果：

```
9
```

可以在互联网上找到更多的柯里化示例。通常它们是用 Java 之外的语言实现的，但如果理解了柯里化的基本概念，你可以很轻松地用 Java 实现它们。

<!-- Pure Functional Programming -->
## 纯函数式编程


即使没有函数式支持，像 C 这样的基础语言，也可以按照一定的原则编写纯函数式程序。Java 8 让函数式编程更简单，不过我们要确保一切是 `final` 的，同时你的所有方法和函数没有副作用。因为 Java 在本质上并非是不可变语言，我们无法通过编译器查错。

这种情况下，我们可以借助第三方工具[^9]，但使用 Scala 或 Clojure 这样的语言可能更简单。因为它们从一开始就是为保持不变性而设计的。你可以采用这些语言来编写你的 Java 项目的一部分。如果必须要用纯函数式编写，则可以用 Scala（需要一些规则） 或 Clojure （需要的规则更少）。虽然 Java 支持[并发编程](./24-Concurrent-Programming.md)，但如果这是你项目的核心部分，你应该考虑在项目部分功能中使用 `Scala` 或 `Clojure` 之类的语言。

<!-- Summary -->
## 本章小结


Lambda 表达式和方法引用并没有将 Java 转换成函数式语言，而是提供了对函数式编程的支持。这对 Java 来说是一个巨大的改进。因为这允许你编写更简洁明了，易于理解的代码。在下一章中，你会看到它们在流式编程中的应用。相信你会像我一样，喜欢上流式编程。

这些特性满足大部分 Java 程序员的需求。他们开始羡慕嫉妒 Clojure、Scala 这类新语言的功能，并试图阻止 Java 程序员流失到其他阵营 （就算不能阻止，起码提供了更好的选择）。

但是，Lambdas 和方法引用远非完美，我们永远要为 Java 设计者早期的草率决定付出代价。特别是没有泛型 Lambda，所以 Lambda 在 Java 中并非一等公民。虽然我不否认 Java 8 的巨大改进，但这意味着和许多 Java 特性一样，它的使用还是会让人感觉沮丧和鸡肋。

当你遇到学习困难时，请记住通过 IDE（NetBeans、IntelliJ Idea 和 Eclipse）获得帮助，因为 IDE 可以智能提示你何时使用 Lambda 表达式或方法引用，甚至有时还能为你优化代码。

<!--下面是脚注-->

[^1]: 功能粘贴在一起的方法的确有点与众不同，但它仍不失为一个库。
[^2]: 例如,这个电子书是利用 [Pandoc](http://pandoc.org/) 制作出来的，它是用纯函数式语言 [Haskell](https://www.haskell.org/) 编写的一个程序 。
[^3]: 有时函数式语言将其描述为“代码即数据”。
[^4]: 这个语法来自 C++。
[^5]: 我还没有验证过这种说法。
[^6]: 当你理解了[并发编程](./24-Concurrent-Programming.md)章节的内容，你就能明白为什么更改共享变量 “不是线程安全的” 的了。
[^7]: 接口能够支持方法的原因是它们是 Java 8 默认方法，你将在下一章中了解到。
[^8]: 一些语言，如 Python，允许像调用其他函数一样调用组合函数。但这是 Java，所以我们做做可为之事。
[^9]: 例如，[Immutables](https://immutables.github.io/) 和 [Mutability Detector](https://mutabilitydetector.github.io/MutabilityDetector/)。


<!-- 分页 -->
<div style="page-break-after: always;"></div>
