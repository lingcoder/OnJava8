[TOC]

<!-- Exceptions -->
# 第十五章 异常

> Java 的基本理念是“结构不佳的代码不能运行”。

改进的错误恢复机制是提供代码健壮性的最强有力的方式。错误恢复在我们所编写的每一个程序中都是基本的要素，但是在 Java 中它显得格外重要，因为 Java 的主要目标之一就是创建供他人使用的程序构件。

发现错误的理想时机是在编译阶段，也就是在你试图运行程序之前。然而，编译期间并不能找出所有的错误，余下的问题必须在运行期间解决。这就需要错误源能通过某种方式，把适当的信息传递给某个接收者——该接收者将知道如何正确处理这个问题。

> 要想创建健壮的系统，它的每一个构件都必须是健壮的。

Java 使用异常来提供一致的错误报告模型，使得构件能够与客户端代码可靠地沟通问题。

Java 中的异常处理的目的在于通过使用少于目前数量的代码来简化大型、可靠的程序的生成，并且通过这种方式可以使你更加确信：你的应用中没有未处理的错误。异常的相关知识学起来并非艰涩难懂，并且它属于那种可以使你的项目受益明显、立竿见影的特性之一。

因为异常处理是 Java 中唯一官方的错误报告机制，并且通过编译器强制执行，所以不学习异常处理的话，书中也就只能写出那么些例子了。本章将向读者介绍如何编写正确的异常处理 3] 程序，并将展示当方法出问题的时候，如何产生自定义的异常。

<!-- Concepts -->

## 异常概念

<!-- Basic Exceptions -->

C 以及其他早期语言常常具有多种错误处理模式，这些模式往往建立在约定俗成的基础之上，而并不属于语言的一部分。通常会返回某个特殊值或者设置某个标志，并且假定接收者将对这个返回值或标志进行检查，以判定是否发生了错误。然而，随着时间的推移，人们发现，高傲的程序员们在使用程序库的时候更倾向于认为：“对，错误也许会发生，但那是别人造成的，不关我的事”。所以，程序员不去检查错误情形也就不足为奇了（何况对某些错误情形的检查确实很无聊）。如果的确在每次调用方法的时候都彻底地进行错误检查，代码很可能会变得难以阅读。正是由于程序员还仍然用这些方式拼凑系统，所以他们拒绝承认这样一个事实：对于构造大型、健壮、可维护的程序而言，这种错误处理模式已经成为了主要障碍。

解决的办法是，用强制规定的形式来消除错误处理过程中随心所欲的因素。这种做法由来已久，对异常处理的实现可以迫溯到 20 世纪 60 年代的操作系统，甚至于 BASIC 语言中的“on error goto”语句。而 C++的异常处理机制基于 Ada，Java 中的异常处理则建立在 C++的基础之上（尽管看上去更像 Object Pascal）。

“异常”这个词有“我对此感到意外”的意思。问题出现了，你也许不清楚该如何处理，但你的确知道不应该置之不理，你要停下来，看看是不是有别人或在别的地方，能够处理这个问题。只是在当前的环境中还没有足够的信息来解决这个问题，所以就把这个问题提交到一个更高级别的环境中，在这里将作出正确的决定。

异常往往能降低错误处理代码的复杂度。如果不使用异常，那么就必须检查特定的错误，并在程序中的许多地方去处理它。而如果使用异常，那就不必在方法调用处进行检查，因为异常机制将保证能够捕获这个错误。并且，只需在一个地方处理错误，即所谓的异常处理程序中。这种方式不仅节省代码，而且把“描述在正常执行过程中做什么事”的代码和“出了问题怎么办”的代码相分离。总之，与以前的错误处理方法相比，异常机制使代码的阅读、编写和调试工作更加井井有条。

## 基本异常

<!-- Catching an Exception -->

异常情形（exceptional condition）是指阻止当前方法或作用城继续执行的问题。把异常情形与普通问题相区分很重要，所谓的普通问题是指，在当前环境下能得到足够的信息，总能处理这个错误。而对于异常情形，就不能继续下去了，因为在当前环境下无法获得必要的信息来解决问题。你所能做的就是从当前环境跳出，并且把问题提交给上一级环境。这就是抛出异常时所发生的事情。

除法就是一个简单的例子。除数有可能为 0，所以先进行检查很有必要。但除数为 0 代表的究竟是什么意思呢？通过当前正在解决的问题环境，或许能知道该如何处理除数为 0 的情况。但如果这是一个意料之外的值，你也不清楚该如何处理，那就要抛出异常，而不是顺着原来的路径继续执行下去。

当抛出异常后，有几件事会随之发生。首先，同 Java 中其他对象的创建一样，将使用 new 在堆上创建异常对象。然后，当前的执行路径（它不能继续下去了）被终止，并且从当前环境中弹出对异常对象的引用。此时，异常处理机制接管程序，并开始寻找一个恰当的地方来继续执行程序。这个恰当的地方就是异常处理程序，它的任务是将程序从错误状态中恢复，以使程序能要么换一种方式运行，要么继续运行下去。

举一个抛出异常的简单例子。对于对象引用 t，传给你的时候可能尚未被初始化。所以在使用这个对象引用调用其方法之前，会先对引用进行检查。可以创建一个代表错误信息的对象，并且将它从当前环境中“抛出”，这样就把错误信息传播到了“更大”的环境中。这被称为抛出一个异常，看起来像这样：

```java
if(t == null)
    throw new NullPointerException();
```

这就抛出了异常，于是在当前环境下就不必再为这个问题操心了，它将在别的地方得到处理。具体是哪个“地方”后面很快就会介绍。

异常使得我们可以将每件事都当作一个事务来考虑，而异常可以看护着这些事务的底线“…事务的基本保障是我们所需的在分布式计算中的异常处理。事务是计算机中的合同法，如果出了什么问题，我们只需要放弃整个计算。”我们还可以将异常看作是一种内建的恢复（undo）系统，因为（在细心使用的情况下）我们在程序中可以拥有各种不同的恢复点。如果程序的某部分失败了，异常将“恢复”到程序中某个已知的稳定点上。

异常最重要的方面之一就是如果发生问题，它们将不允许程序沿着其正常的路径继续走下去。在 C 和 C++这样的语言中，这可真是个问题，尤其是 C，它没有任何办法可以强制程序在出现问题时停止在某条路径上运行下去，因此我们有可能会较长时间地忽略了问题，从而陷入了完全不恰当的状态中。异常允许我们（如果没有其他手段）强制程序停止运行，并告诉我们出现了什么问题，或者（理想状态下）强制程序处理问题，并返回到稳定状态。

### 异常参数

与使用 Java 中的其他对象一样，我们总是用 new 在堆上创建异常对象，这也伴随着存储空间的分配和构造器的调用。所有标准异常类都有两个构造器：一个是默认构造器；另一个是接受字符串作为参数，以便能把相关信息放入异常对象的构造器：

```java
throw new NullPointerException("t = null");
```

不久读者将看到，要把这个字符串的内容提取出来可以有多种不同的方法。

关键字 throw 将产生许多有趣的结果。在使用 new 创建了异常对象之后，此对象的引用将传给 throw。尽管返回的异常对象其类型通常与方法设计的返回类型不同，但从效果上看，它就像是从方法“返回”的。可以简单地把异常处理看成一种不同的返回机制，当然若过分强调这种类比的话，就会有麻烦了。另外还能用抛出异常的方式从当前的作用域退出。在这两种情况下，将会返回一个异常对象，然后退出方法或作用域。

抛出异常与方法正常返回值的相似之处到此为止。因为异常返回的“地点”与普通方法调用返回的“地点”完全不同。（异常将在一个恰当的异常处理程序中得到解决，它的位置可能离异常被抛出的地方很远，也可能会跨越方法调用栈的许多层次。）此外，能够抛出任意类型的 Throwable 对象，它是异常类型的根类。通常，对于不同类型的错误，要抛出相应的异常。错误信息可以保存在异常对象内部或者用异常类的名称来暗示。上一层环境通过这些信息来决定如何处理异常。（通常，异常对象中仅有的信息就是异常类型，除此之外不包含任何有意义的内容。）

## 异常捕获

要明白异常是如何被捕获的，必须首先理解监控区域（guarded region）的概念。它是一段可能产生异常的代码，并且后面跟着处理这些异常的代码。

### try 语句块

如果在方法内部抛出了异常（或者在方法内部调用的其他方法抛出了异常），这个方法将在抛出异常的过程中结束。要是不希望方法就此结束，可以在方法内设置一个特殊的块来捕获异常。因为在这个块里“尝试”各种（可能产生异常的）方法调用，所以称为 try 块。它是跟在 try 关键字之后的普通程序块：

```java
try {
    // Code that might generate exceptions
}
```

对于不支持异常处理的程序语言，要想仔细检查错误，就得在每个方法调用的前后加上设置和错误检查的代码，甚至在每次调用同一方法时也得这么做。有了异常处理机制，可以把所有动作都放在 ry 块里，然后只需在一个地方就可以捕获所有异常。这意味着代码将更容易编写和阅读，因为完成任务的代码没有与错误检查的代码混在一起。

### 异常处理程序

当然，抛出的异常必须在某处得到处理。这个“地点”就是异常处理程序，而且针对每个要捕获的异常，得准备相应的处理程序。异常处理程序紧跟在 try 块之后，以关键字 catch 表示：

```java
try {
    // Code that might generate exceptions
} catch(Type1 id1) {
    // Handle exceptions of Type1
} catch(Type2 id2) {
    // Handle exceptions of Type2
} catch(Type3 id3) {
    // Handle exceptions of Type3
}
// etc.
```

每个 catch 子句（异常处理程序）看起来就像是接收一个且仅接收一个特殊类型的参数的方法。可以在处理程序的内部使用标识符（id1，id2 等等），这与方法参数的使用很相似。有时可能用不到标识符，因为异常的类型已经给了你足够的信息来对异常进行处理，但标识符并不可以省略。

异常处理程序必须紧跟在 try 块之后。当异常被抛出时，异常处理机制将负责搜寻参数与异常类型相匹配的第一个处理程序。然后进入 catch 子句执行，此时认为异常得到了处理。一旦 catch 子句结束，则处理程序的查找过程结束。注意，只有匹配的 catch 子句才能得到执行；这与 switch 语句不同，switch 语句需要在每一个 case 后面跟一个 break，以避免执行后续的 case 子句。

注意在 try 块的内部，许多不同的方法调用可能会产生类型相同的异常，而你只需要提供一个针对此类型的异常处理程序。

### 终止与恢复

异常处理理论上有两种基本模型。Java 支持终止模型（它是 Java 和 C++所支持的模型）。在这种模型中，将假设错误非常关键，以至于程序无法返回到异常发生的地方继续执行。一旦异常被抛出，就表明错误已无法挽回，也不能回来继续执行。

另一种称为恢复模型。意思是异常处理程序的工作是修正错误，然后重新尝试调用出问题的方法，并认为第二次能成功。对于恢复模型，通常希望异常被处理之后能继续执行程序。如果想要用 Java 实现类似恢复的行为，那么在遇见错误时就不能抛出异常，而是调用方法来修正该错误。或者，把 try 块放在 while 循环里，这样就不断地进入 try 块，直到得到满意的结果。

长久以来，尽管程序员们使用的操作系统支持恢复模型的异常处理，但他们最终还是转向使用类似“终止模型”的代码，并且忽略恢复行为。所以虽然恢复模型开始显得很吸引人，但不是很实用。其中的主要原因可能是它所导致的耦合：恢复性的处理程序需要了解异常抛出的地点，这势必要包含依赖于抛出位置的非通用性代码。这增加了代码编写和维护的困难，对于异常可能会从许多地方抛出的大型程序来说，更是如此。

<!-- Creating Your Own Exceptions -->

## 自定义异常

不必拘泥于 Java 中已有的异常类型。Java 提供的异常体系不可能预见所有的希望加以报告的错误，所以可以自己定义异常类来表示程序中可能会遇到的特定问题。

要自己定义异常类，必须从已有的异常类继承，最好是选择意思相近的异常类继承（不过这样的异常并不容易找）。建立新的异常类型最简单的方法就是让编译器为你产生默认构造器，所以这几乎不用写多少代码：

```java
// exceptions/InheritingExceptions.java
// Creating your own exceptions
class SimpleException extends Exception {}

public class InheritingExceptions {
    public void f() throws SimpleException {
        System.out.println(
                "Throw SimpleException from f()");
        throw new SimpleException();
    }
    public static void main(String[] args) {
        InheritingExceptions sed =
                new InheritingExceptions();
        try {
            sed.f();
        } catch(SimpleException e) {
            System.out.println("Caught it!");
        }
    }
}
```

输出为：

```
Throw SimpleException from f()
Caught it!
```

编译器创建了默认构造器，它将自动调用基类的默认构造器。本例中不会得到像 SimpleException(String) 这样的构造器，这种构造器也不实用。你将看到，对异常来说，最重要的部分就是类名，所以本例中建立的异常类在大多数情况下已经够用了。

本例的结果被打印到了控制台上，本书的输出显示系统正是在控制台上自动地捕获和测试这些结果的。但是，你也许想通过写入 System.err 而将错误发送给标准错误流。通常这比把错误信息输出到 System.out 要好，因为 System.out 也许会被重定向。如果把结果送到 System.err，它就不会随 System.out 一起被重定向，这样更容易被用户注意。

你也可以为异常类创建一个接受字符串参数的构造器：

```java
// exceptions/FullConstructors.java
class MyException extends Exception {
    MyException() {}
    MyException(String msg) { super(msg); }
}
public class FullConstructors {
    public static void f() throws MyException {
        System.out.println("Throwing MyException from f()");
        throw new MyException();
    }
    public static void g() throws MyException {
        System.out.println("Throwing MyException from g()");
        throw new MyException("Originated in g()");
    }
    public static void main(String[] args) {
        try {
            f();
        } catch(MyException e) {
            e.printStackTrace(System.out);
        }
        try {
            g();
        } catch(MyException e) {
            e.printStackTrace(System.out);
        }
    }
}
```

输出为：

```
Throwing MyException from f()
MyException
    at FullConstructors.f(FullConstructors.java:11)
    at
FullConstructors.main(FullConstructors.java:19)
Throwing MyException from g()
MyException: Originated in g()
    at FullConstructors.g(FullConstructors.java:15)
    at
FullConstructors.main(FullConstructors.java:24)
```

新增的代码非常简短：两个构造器定义了 MyException 类型对象的创建方式。对于第二个构造器，使用 super 关键宇明确调用了其基类构造器，它接受一个字符串作为参数。

在异常处理程序中，调用了在 Throwable 类声明（Exception 即从此类继承）的 printStackTrace0 方法。就像从输出中看到的，它将打印“从方法调用处直到异常抛出处”的方法调用序列。这里，信息被发送到了 System.out，并自动地被捕获和显示在输出中。但是，如果调用默认版本：

```java
e.printStackTrace();
```

信息就会被输出到标准错误流。

### 异常与记录日志

你可能还想使用 java.util.logging 工具将输出记录到日志中。基本的日志记录功能还是相当简单易懂的：

```java
// exceptions/LoggingExceptions.java
// An exception that reports through a Logger
// {ErrorOutputExpected}
import java.util.logging.*;
import java.io.*;
class LoggingException extends Exception {
    private static Logger logger =
            Logger.getLogger("LoggingException");
    LoggingException() {
        StringWriter trace = new StringWriter();
        printStackTrace(new PrintWriter(trace));
        logger.severe(trace.toString());
    }
}
public class LoggingExceptions {
    public static void main(String[] args) {
        try {
            throw new LoggingException();
        } catch(LoggingException e) {
            System.err.println("Caught " + e);
        }
        try {
            throw new LoggingException();
        } catch(LoggingException e) {
            System.err.println("Caught " + e);
        }
    }
}
```

输出为：

```
___[ Error Output ]___
May 09, 2017 6:07:17 AM LoggingException <init>
SEVERE: LoggingException
at
LoggingExceptions.main(LoggingExceptions.java:20)
Caught LoggingException
May 09, 2017 6:07:17 AM LoggingException <init>
SEVERE: LoggingException
at
LoggingExceptions.main(LoggingExceptions.java:25)
Caught LoggingException
```

静态的 Logger.getLogger() 方法创建了一个 String 参数相关联的 Logger 对象（通常与错误相关的包名和类名），这个 Logger 对象会将其输出发送到 System.err。向 Logger 写人的最简单方式就是直接调用与日志记录消息的级别相关联的方法，这里使用的是 severe()。为了产生日志记录消息，我们欲获取异常抛出处的栈轨迹，但是 printStackTrace() 不会默认地产生字符串。为了获取字符串，我们需要使用重载的 printStackTrace() 方法，它接受一 java.io.PrintWriter 对象作为参数（PrintWriter 会在[附录：I/O 流 ]() 一章详细介绍）。如果我们将一个 java.io.StringWriter 对象传递给这个 PrintWriter 的构造器，那么通过调用 toString() 方法，就可以将输出抽取为一个 String。

尽管由于 LoggingException 将所有记录日志的基础设施都构建在异常自身中，使得它所使用的方式非常方便，并因此不需要客户端程序员的干预就可以自动运行，但是更常见的情形是我们需要捕获和记录其他人编写的异常，因此我们必须在异常处理程序中生成日志消息；

```java
// exceptions/LoggingExceptions2.java
// Logging caught exceptions
// {ErrorOutputExpected}
import java.util.logging.*;
import java.io.*;
public class LoggingExceptions2 {
    private static Logger logger =
            Logger.getLogger("LoggingExceptions2");
    static void logException(Exception e) {
        StringWriter trace = new StringWriter();
        e.printStackTrace(new PrintWriter(trace));
        logger.severe(trace.toString());
    }
    public static void main(String[] args) {
        try {
            throw new NullPointerException();
        } catch(NullPointerException e) {
            logException(e);
        }
    }
}
```

输出结果为：

```
___[ Error Output ]___
May 09, 2017 6:07:17 AM LoggingExceptions2 logException
SEVERE: java.lang.NullPointerException
at
LoggingExceptions2.main(LoggingExceptions2.java:17)
```

还可以更进一步自定义异常，比如加入额外的构造器和成员：

```java
// exceptions/ExtraFeatures.java
// Further embellishment of exception classes
class MyException2 extends Exception {
    private int x;
    MyException2() {}
    MyException2(String msg) { super(msg); }
    MyException2(String msg, int x) {
        super(msg);
        this.x = x;
    }
    public int val() { return x; }
    @Override
    public String getMessage() {
        return "Detail Message: "+ x
                + " "+ super.getMessage();
    }
}
public class ExtraFeatures {
    public static void f() throws MyException2 {
        System.out.println(
                "Throwing MyException2 from f()");
        throw new MyException2();
    }
    public static void g() throws MyException2 {
        System.out.println(
                "Throwing MyException2 from g()");
        throw new MyException2("Originated in g()");
    }
    public static void h() throws MyException2 {
        System.out.println(
                "Throwing MyException2 from h()");
        throw new MyException2("Originated in h()", 47);
    }
    public static void main(String[] args) {
        try {
            f();
        } catch(MyException2 e) {
            e.printStackTrace(System.out);
        }
        try {
            g();
        } catch(MyException2 e) {
            e.printStackTrace(System.out);
        }
        try {
            h();
        } catch(MyException2 e) {
            e.printStackTrace(System.out);
            System.out.println("e.val() = " + e.val());
        }
    }
}
```

输出为：

```
Throwing MyException2 from f()
MyException2: Detail Message: 0 null
at ExtraFeatures.f(ExtraFeatures.java:24)
at ExtraFeatures.main(ExtraFeatures.java:38)
Throwing MyException2 from g()
MyException2: Detail Message: 0 Originated in g()
at ExtraFeatures.g(ExtraFeatures.java:29)
at ExtraFeatures.main(ExtraFeatures.java:43)
Throwing MyException2 from h()
MyException2: Detail Message: 47 Originated in h()
at ExtraFeatures.h(ExtraFeatures.java:34)
at ExtraFeatures.main(ExtraFeatures.java:48)
e.val() = 47
```

新的异常添加了字段 x 以及设定 x 值的构造器和读取数据的方法。此外，还覆盖了 Throwable.
getMessage() 方法，以产生更详细的信息。对于异常类来说，getMessage() 方法有点类似于 toString() 方法。

既然异常也是对象的一种，所以可以继续修改这个异常类，以得到更强的功能。但要记住，使用程序包的客户端程序员可能仅仅只是查看一下抛出的异常类型，其他的就不管了（大多数 Java 库里的异常都是这么用的），所以对异常所添加的其他功能也许根本用不上。

## 异常声明

Java 鼓励人们把方法可能会抛出的异常告知使用此方法的客户端程序员。这是种优雅的做法，它使得调用者能确切知道写什么样的代码可以捕获所有潜在的异常。当然，如果提供了源代码，客户端程序员可以在源代码中查找 throw 语句来获知相关信息，然而程序库通常并不与源代码一起发布。为了预防这样的问题，Java 提供了相应的语法（并强制使用这个语法），使你能以礼貌的方式告知客户端程序员某个方法可能会抛出的异常类型，然后客户端程序员就可以进行相应的处理。这就是异常说明，它属于方法声明的一部分，紧跟在形式参数列表之后。

异常说明使用了附加的关键字 throws，后面接一个所有潜在异常类型的列表，所以方法定义可能看起来像这样：

```java
void f() throws TooBig, TooSmall, DivZero { // ...
```

但是，要是这样写：

```java
void f() { // ...
```

就表示此方法不会抛出任何异常（除了从 RuntimeException 继承的异常，它们可以在没有异常说明的情况下被抛出，这些将在后面进行讨论）。

代码必须与异常说明保持一致。如果方法里的代码产生了异常却没有进行处理，编译器会发现这个问题并提醒你：要么处理这个异常，要么就在异常说明中表明此方法将产生异常。通过这种自顶向下强制执行的异常说明机制，Java 在编译时就可以保证一定水平的异常正确性。

不过还是有个能“作弊”的地方：可以声明方法将抛出异常，实际上却不抛出。编译器相信了这个声明，并强制此方法的用户像真的抛出异常那样使用这个方法。这样做的好处是，为异常先占个位子，以后就可以抛出这种异常而不用修改已有的代码。在定义抽象基类和接口时这种能力很重要，这样派生类或接口实现就能够抛出这些预先声明的异常。

这种在编译时被强制检查的异常称为被检查的异常。

## 任意所有捕获

可以只写一个异常处理程序来捕获所有类型的异常。通过捕获异常类型的基类 Exception，就可以做到这一点（事实上还有其他的基类，但 Exception 是所有编程行为相关的基类）：

```java
catch(Exception e) {
    System.out.println("Caught an exception");
}
```

这将捕获所有异常，所以最好把它放在处理程序列表的末尾，以防它抢在其他处理程序之前先把异常捕获了。

因为 Exception 是与编程有关的所有异常类的基类，所以它不会含有太多具体的信息，不过可以调用它从其基类 Throwable 继承的方法：

```java
String getMessage()
String getLocalizedMessage()
```

用来获取详细信息，或用本地语言表示的详细信息。

```java
String toString()
```

返回对 Throwable 的简单描述，要是有详细信息的话，也会把它包含在内。

```java
void printStackTrace()
void printStackTrace(PrintStream)
void printStackTrace(java.io.PrintWriter)
```

打印 Throwable 和 Throwable 的调用栈轨迹。调用栈显示了“把你带到异常抛出地点”的方法调用序列。其中第一个版本输出到标准错误，后两个版本允许选择要输出的流（在[附录 I/O 流 ]() 中，你将会理解为什么有两种不同的流）。

```java
Throwable fillInStackTrace()
```

用于在 Throwable 对象的内部记录栈帧的当前状态。这在程序重新抛出错误或异常（很快就会讲到）时很有用。

此外，也可以使用 Throwable 从其基类 Object（也是所有类的基类）继承的方法。对于异常来说，getClass）也许是个很好用的方法，它将返回一个表示此对象类型的对象。然后可以使用 getName）方法查询这个 Class 对象包含包信息的名称，或者使用只产生类名称的 getSimple Name0 方法。

下面的例子演示了如何使用 Exception 类型的方法：

```java
// exceptions/ExceptionMethods.java
// Demonstrating the Exception Methods
public class ExceptionMethods {
    public static void main(String[] args) {
        try {
            throw new Exception("My Exception");
        } catch(Exception e) {
            System.out.println("Caught Exception");
            System.out.println(
                    "getMessage():" + e.getMessage());
            System.out.println("getLocalizedMessage():" +
                    e.getLocalizedMessage());
            System.out.println("toString():" + e);
            System.out.println("printStackTrace():");
            e.printStackTrace(System.out);
        }
    }
}
```

输出为：

```java
Caught Exception
getMessage():My Exception
getLocalizedMessage():My Exception
toString():java.lang.Exception: My Exception
printStackTrace():
java.lang.Exception: My Exception
at
ExceptionMethods.main(ExceptionMethods.java:7)
```

可以发现每个方法都比前一个提供了更多的信息一一实际上它们每一个都是前一个的超集。

### 多重捕获

如果有一组具有相同基类的异常，你想使用同一方式进行捕获，那你直接 catch 它们的基类型。但是，如果这些异常没有共同的基类型，在 Java 7 之前，你必须为每一个类型编写一个 catch：

```java
// exceptions/SameHandler.java
class EBase1 extends Exception {}
class Except1 extends EBase1 {}
class EBase2 extends Exception {}
class Except2 extends EBase2 {}
class EBase3 extends Exception {}
class Except3 extends EBase3 {}
class EBase4 extends Exception {}
class Except4 extends EBase4 {}

public class SameHandler {
    void x() throws Except1, Except2, Except3, Except4 {}
    void process() {}
    void f() {
        try {
            x();
        } catch(Except1 e) {
            process();
        } catch(Except2 e) {
            process();
        } catch(Except3 e) {
            process();
        } catch(Except4 e) {
            process();
        }
    }
}
```

通过 Java 7 的多重捕获机制，你可以讲不同类型的异常使用“或”将它们组合起来，只在一个 catch 块中使用：

```java
// exceptions/MultiCatch.java
public class MultiCatch {
    void x() throws Except1, Except2, Except3, Except4 {}
    void process() {}
    void f() {
        try {
            x();
        } catch(Except1 | Except2 | Except3 | Except4 e) {
            process();
        }
    }
}
```

或者以其他组合的方式：

```java
// exceptions/MultiCatch2.java
public class MultiCatch2 {
    void x() throws Except1, Except2, Except3, Except4 {}
    void process1() {}
    void process2() {}
    void f() {
        try {
            x();
        } catch(Except1 | Except2 e) {
            process1();
        } catch(Except3 | Except4 e) {
            process2();
        }
    }
}
```

这对书写更整洁的代码很有帮助

### 栈轨迹

printStackTrace() 方法所提供的信息可以通过 getStackTrace() 方法来直接访问，这个方法将返回一个由栈轨迹中的元素所构成的数组，其中每一个元素都表示栈中的一桢。元素 0 是栈顶元素，并且是调用序列中的最后一个方法调用（这个 Throwable 被创建和抛出之处）。数组中的最后一个元素和栈底是调用序列中的第一个方法调用。下面的程序是一个简单的演示示例：

```java
// exceptions/WhoCalled.java
// Programmatic access to stack trace information
public class WhoCalled {
    static void f() {
// Generate an exception to fill in the stack trace
        try {
            throw new Exception();
        } catch(Exception e) {
            for(StackTraceElement ste : e.getStackTrace())
                System.out.println(ste.getMethodName());
        }
    }
    static void g() { f(); }
    static void h() { g(); }
    public static void main(String[] args) {
        f();
        System.out.println("*******");
        g();
        System.out.println("*******");
        h();
    }
}
```

输出为：

```
f
main
*******
f
g
main
*******
f
g
h
main
```

这里，我们只打印了方法名，但实际上还可以打印整个 StackTraceElement，它包含其他附加的信息。

### 重新抛出异常

有时希望把刚捕获的异常重新抛出，尤其是在使用 Exception 捕获所有异常的时候。既然已经得到了对当前异常对象的引用，可以直接把它重新抛出：

```java
catch(Exception e) {
    System.out.println("An exception was thrown");
    throw e;
}
```

重抛异常会把异常抛给上一级环境中的异常处理程序，同一个 try 块的后续 catch 子句将被忽略。此外，异常对象的所有信息都得以保持，所以高一级环境中捕获此异常的处理程序可以从这个异常对象中得到所有信息。

如果只是把当前异常对象重新抛出，那么 printStackTrace() 方法显示的将是原来异常抛出点的调用栈信息，而并非重新抛出点的信息。要想更新这个信息，可以调用 filInStackTrace() 方法，这将返回一个 Throwable 对象，它是通过把当前调用栈信息填入原来那个异常对象而建立的，就像这样：

```java
// exceptions/Rethrowing.java
// Demonstrating fillInStackTrace()
public class Rethrowing {
    public static void f() throws Exception {
        System.out.println(
                "originating the exception in f()");
        throw new Exception("thrown from f()");
    }
    public static void g() throws Exception {
        try {
            f();
        } catch(Exception e) {
            System.out.println(
                    "Inside g(), e.printStackTrace()");
            e.printStackTrace(System.out);
            throw e;
        }
    }
    public static void h() throws Exception {
        try {
            f();
        } catch(Exception e) {
            System.out.println(
                    "Inside h(), e.printStackTrace()");
            e.printStackTrace(System.out);
            throw (Exception)e.fillInStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            g();
        } catch(Exception e) {
            System.out.println("main: printStackTrace()");
            e.printStackTrace(System.out);
        }
        try {
            h();
        } catch(Exception e) {
            System.out.println("main: printStackTrace()");
            e.printStackTrace(System.out);
        }
    }
}
```

输出为：

```java
originating the exception in f()
Inside g(), e.printStackTrace()
java.lang.Exception: thrown from f()
at Rethrowing.f(Rethrowing.java:8)
at Rethrowing.g(Rethrowing.java:12)
at Rethrowing.main(Rethrowing.java:32)
main: printStackTrace()
java.lang.Exception: thrown from f()
at Rethrowing.f(Rethrowing.java:8)
at Rethrowing.g(Rethrowing.java:12)
at Rethrowing.main(Rethrowing.java:32)
originating the exception in f()
Inside h(), e.printStackTrace()
java.lang.Exception: thrown from f()
at Rethrowing.f(Rethrowing.java:8)
at Rethrowing.h(Rethrowing.java:22)
at Rethrowing.main(Rethrowing.java:38)
main: printStackTrace()
java.lang.Exception: thrown from f()
at Rethrowing.h(Rethrowing.java:27)
at Rethrowing.main(Rethrowing.java:38)
```

调用 fillInStackTrace() 的那一行就成了异常的新发生地了。

有可能在捕获异常之后抛出另一种异常。这么做的话，得到的效果类似于使用 filInStackTrace()，有关原来异常发生点的信息会丢失，剩下的是与新的抛出点有关的信息：

```java
// exceptions/RethrowNew.java
// Rethrow a different object from the one you caught
class OneException extends Exception {
    OneException(String s) { super(s); }
}
class TwoException extends Exception {
    TwoException(String s) { super(s); }
}
public class RethrowNew {
    public static void f() throws OneException {
        System.out.println(
                "originating the exception in f()");
        throw new OneException("thrown from f()");
    }
    public static void main(String[] args) {
        try {
            try {
                f();
            } catch(OneException e) {
                System.out.println(
                        "Caught in inner try, e.printStackTrace()");
                e.printStackTrace(System.out);
                throw new TwoException("from inner try");
            }
        } catch(TwoException e) {
            System.out.println(
                    "Caught in outer try, e.printStackTrace()");
            e.printStackTrace(System.out);
        }
    }
}
```

输出为：

```java
originating the exception in f()
Caught in inner try, e.printStackTrace()
OneException: thrown from f()
at RethrowNew.f(RethrowNew.java:16)
at RethrowNew.main(RethrowNew.java:21)
Caught in outer try, e.printStackTrace()
TwoException: from inner try
at RethrowNew.main(RethrowNew.java:26)
```

最后那个异常仅知道自己来自 main()，而对 f() 一无所知。

永远不必为清理前一个异常对象而担心，或者说为异常对象的清理而担心。它们都是用 new 在堆上创建的对象，所以垃圾回收器会自动把它们清理掉。

### 精准的重新抛出异常

在 Java 7 之前，如果遇到异常，则只能重新抛出该类型的异常。这导致在 Java 7 中修复的代码不精确。所以在 Java 7 之前，这无法编译：

```java
class BaseException extends Exception {}
class DerivedException extends BaseException {}

public class PreciseRethrow {
    void catcher() throws DerivedException {
        try {
            throw new DerivedException();
        } catch(BaseException e) {
            throw e;
        }
    }
}
```

因为 catch 捕获了一个 BaseException，编译器强迫你声明 catcher() 抛出 BaseException，即使它实际上抛出了更具体的 DerivedException。从 Java 7 开始，这段代码就可以编译，这是一个很小但很有用的修复。

### 异常链

常常会想要在捕获一个异常后抛出另一个异常，并且希望把原始异常的信息保存下来，这被称为异常链。在 JDK1.4 以前，程序员必须自己编写代码来保存原始异常的信息。现在所有 Throwable 的子类在构造器中都可以接受一个 cause（因由）对象作为参数。这个 cause 就用来表示原始异常，这样通过把原始异常传递给新的异常，使得即使在当前位置创建并抛出了新的异常，也能通过这个异常链追踪到异常最初发生的位置。

有趣的是，在 Throwable 的子类中，只有三种基本的异常类提供了带 cause 参数的构造器。它们是 Error（用于 Java 虚拟机报告系统错误）、Exception 以及 RuntimeException。如果要把其他类型的异常链接起来，应该使用 initCause0 方法而不是构造器。

下面的例子能让你在运行时动态地向 DymamicFields 对象添加字段：

```java
// exceptions/DynamicFields.java
// A Class that dynamically adds fields to itself to
// demonstrate exception chaining
class DynamicFieldsException extends Exception {}
public class DynamicFields {
    private Object[][] fields;
    public DynamicFields(int initialSize) {
        fields = new Object[initialSize][2];
        for(int i = 0; i < initialSize; i++)
            fields[i] = new Object[] { null, null };
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(Object[] obj : fields) {
            result.append(obj[0]);
            result.append(": ");
            result.append(obj[1]);
            result.append("\n");
        }
        return result.toString();
    }
    private int hasField(String id) {
        for(int i = 0; i < fields.length; i++)
            if(id.equals(fields[i][0]))
                return i;
        return -1;
    }
    private int getFieldNumber(String id)
            throws NoSuchFieldException {
        int fieldNum = hasField(id);
        if(fieldNum == -1)
            throw new NoSuchFieldException();
        return fieldNum;
    }
    private int makeField(String id) {
        for(int i = 0; i < fields.length; i++)
            if(fields[i][0] == null) {
                fields[i][0] = id;
                return i;
            }
// No empty fields. Add one:
        Object[][] tmp = new Object[fields.length + 1][2];
        for(int i = 0; i < fields.length; i++)
            tmp[i] = fields[i];
        for(int i = fields.length; i < tmp.length; i++)
            tmp[i] = new Object[] { null, null };
        fields = tmp;
// Recursive call with expanded fields:
        return makeField(id);
    }
    public Object
    getField(String id) throws NoSuchFieldException {
        return fields[getFieldNumber(id)][1];
    }
    public Object setField(String id, Object value)
            throws DynamicFieldsException {
        if(value == null) {
// Most exceptions don't have a "cause"
// constructor. In these cases you must use
// initCause(), available in all
// Throwable subclasses.
            DynamicFieldsException dfe =
                    new DynamicFieldsException();
            dfe.initCause(new NullPointerException());
            throw dfe;
        }
        int fieldNumber = hasField(id);
        if(fieldNumber == -1)
            fieldNumber = makeField(id);
        Object result = null;
        try {
            result = getField(id); // Get old value
        } catch(NoSuchFieldException e) {
// Use constructor that takes "cause":
            throw new RuntimeException(e);
        }
        fields[fieldNumber][1] = value;
        return result;
    }
    public static void main(String[] args) {
        DynamicFields df = new DynamicFields(3);
        System.out.println(df);
        try {
            df.setField("d", "A value for d");
            df.setField("number", 47);
            df.setField("number2", 48);
            System.out.println(df);
            df.setField("d", "A new value for d");
            df.setField("number3", 11);
            System.out.println("df: " + df);
            System.out.println("df.getField(\"d\") : "
                    + df.getField("d"));
            Object field =
                    df.setField("d", null); // Exception
        } catch(NoSuchFieldException |
                DynamicFieldsException e) {
            e.printStackTrace(System.out);
        }
    }
}
```

输出为：

```java
null: null
null: null
null: null
d: A value for d
number: 47
number2: 48
df: d: A new value for d
number: 47
number2: 48
number3: 11
df.getField("d") : A new value for d
DynamicFieldsException
at
DynamicFields.setField(DynamicFields.java:65)
at DynamicFields.main(DynamicFields.java:97)
Caused by: java.lang.NullPointerException
at
DynamicFields.setField(DynamicFields.java:67)
... 1 more
```

每个 DynamicFields 对象都含有一个数组，其元素是“成对的对象”。第一个对象表示字段标识符（一个字符串），第二个表示字段值，值的类型可以是除基本类型外的任意类型。当创建对象的时候，要合理估计一下需要多少字段。当调用 setField() 方法的时候，它将试图通过标识修改已有字段值，否则就建一个新的字段，并把值放入。如果空间不够了，将建立一个更长的数组，并把原来数组的元素复制进去。如果你试图为字段设置一个空值，将抛出一个 DynamicFieldsException 异常，它是通过使用 initCause() 方法把 NullPointerException 对象插入而建立的。

至于返回值，setField() 将用 getField() 方法把此位置的旧值取出，这个操作可能会抛出 NoSuchFieldException 异常。如果客户端程序员调用了 getField() 方法，那么他就有责任处理这个可能抛出的 NoSuchFieldException 异常，但如果异常是从 setField0 方法里抛出的，这种情况将被视为编程错误，所以就使用接受 cause 参数的构造器把 NoSuchFieldException 异常转换为 RuntimeException 异常。

你会注意到，toString0 方法使用了一个 StringBuilder 来创建其结果。在[字符串 ]() 这章中你将会了解到更多的关于 StringBuilder 的知识，但是只要你编写设计循环的 toString() 方法，通常都会想使用它，就像本例一样。

主方法中的 catch 子句看起来不同 - 它使用相同的子句处理两种不同类型的异常，并结合“或（|）”符号。此 Java 7 功能有助于减少代码重复，并使你更容易指定要捕获的确切类型，而不是简单地捕获基本类型。您可以通过这种方式组合多种异常类型。

<!-- Standard Java Exceptions -->

## Java 标准异常

Throwable 这个 Java 类被用来表示任何可以作为异常被抛出的类。Throwable 对象可分为两种类型（指从 Throwable 继承而得到的类型）：Error 用来表示编译时和系统错误（除特殊情况外，一般不用你关心）；Exception 是可以被抛出的基本类型，在 Java 类库、用户方法以及运行时故障中都可能抛出 Exception 型异常。所以 Java 程序员关心的基类型通常是 Exception。要想对异常有全面的了解，最好去浏览一下 HTML 格式的 Java 文档（可以从 java.sun.com 下载）。为了对不同的异常有个感性的认识，这么做是值得的。但很快你就会发现，这些异常除了名称外其实都差不多。同时，Java 中异常的数目在持续增加，所以在书中简单罗列它们毫无意义。所使用的第三方类库也可能会有自己的异常。对异常来说，关键是理解概念以及如何使用。

异常的基本的概念是用名称代表发生的问题，并且异常的名称应该可以望文知意。异常并非全是在 java.lang 包里定义的；有些异常是用来支持其他像 util、net 和 io 这样的程序包，这些异常可以通过它们的完整名称或者从它们的父类中看出端倪。比如，所有的输入/输出异常都是从 java.io.IOException 继承而来的。

### 特例：RuntimeException

在本章的第一个例子中：

```java
if(t == null)
    throw new NullPointerException();
```

如果必须对传递给方法的每个引用都检查其是否为 nul（因为无法确定调用者是否传入了非法引用），这听起来着实吓人。幸运的是，这不必由你亲自来做，它属于 Java 的标准运行时检测的一部分。如果对 null 引用进行调用，Java 会自动抛出 NullPointerException 异常，所以上述代码是多余的，尽管你也许想要执行其他的检查以确保 NullPointerException 不会出现。

属于运行时异常的类型有很多，它们会自动被 lava 虚拟机抛出，所以不必在异常说明中把它们列出来。这些异常都是从 RuntimeException 类继承而来，所以既体现了继承的优点，使用起来也很方便。这构成了一组具有相同特征和行为的异常类型。并且，也不再需要在异常说明中声明方法将抛出 RuntimeException 类型的异常（或者任何从 RuntimeException 继承的异常），它们也被称为“不受检查异常”。这种异常属于错误，将被自动捕获，就不用你亲自动手了。要是自己去检查 RuntimeException 的话，代码就显得太混乱了。不过尽管通常不用捕获 RuntimeException 异常，但还是可以在代码中抛出 RuntimeException 类型的异常。

RuntimeException 代表的是编程错误：

1. 无法预料的错误。比如从你控制范围之外传递进来的 null 引用。
2. 作为程序员，应该在代码中进行检查的错误。（比如对于 ArrayIndexOutOfBoundsException，就得注意一下数组的大小了。）在一个地方发生的异常，常常会在另一个地方导致错误。

在这些情况下使用异常很有好处，它们能给调试带来便利。

如果不捕获这种类型的异常会发生什么事呢？因为编译器没有在这个问题上对异常说明进行强制检查，RuntimeException 类型的异常也许会穿越所有的执行路径直达 main() 方法，而不会被捕获。要明白到底发生了什么，可以试试下面的例子：

```java
// exceptions/NeverCaught.java
// Ignoring RuntimeExceptions
// {ThrowsException}
public class NeverCaught {
    static void f() {
        throw new RuntimeException("From f()");
    }
    static void g() {
        f();
    }
    public static void main(String[] args) {
        g();
    }
}
```

输出结果为：

```java
___[ Error Output ]___
Exception in thread "main" java.lang.RuntimeException:
From f()
at NeverCaught.f(NeverCaught.java:7)
at NeverCaught.g(NeverCaught.java:10)
at NeverCaught.main(NeverCaught.java:13)
```

如果 RuntimeException 没有被捕获而直达 main()，那么在程序退出前将调用异常的 printStackTrace() 方法。

你会发现，RuntimeException（或任何从它继承的异常）是一个特例。对于这种异常类型，编译器不需要异常说明，其输出被报告给了 System.err。

请务必记住：只能在代码中忽略 RuntimeException（及其子类）类型的异常，因为所有受检查类型异常的处理都是由编译器强制实施的。

值得注意的是：不应把 Java 的异常处理机制当成是单一用途的工具。是的，它被设计用来处理一些烦人的运行时错误，这些错误往往是由代码控制能力之外的因素导致的；然而，它对于发现某些编译器无法检测到的编程错误，也是非常重要的。

<!-- Performing Cleanup with finally -->

## 使用 finally 进行清理

有一些代码片段，可能会希望无论 try 块中的异常是否抛出，它们都能得到执行。这通常适用于内存回收之外的情况（因为回收由垃圾回收器完成），为了达到这个效果，可以在异常处理程序后面加上 finally 子句。完整的异常处理程序看起来像这样：

```java
try {
// The guarded region: Dangerous activities
// that might throw A, B, or C
} catch(A a1) {
// Handler for situation A
} catch(B b1) {
// Handler for situation B
} catch(C c1) {
// Handler for situation C
} finally {
// Activities that happen every time
}
```

为了证明 finally 子句总能运行，可以试试下面这个程序：

```java
// exceptions/FinallyWorks.java
// The finally clause is always executed
class ThreeException extends Exception {}
public class FinallyWorks {
    static int count = 0;
    public static void main(String[] args) {
        while(true) {
            try {
				// Post-increment is zero first time:
                if(count++ == 0)
                    throw new ThreeException();
                System.out.println("No exception");
            } catch(ThreeException e) {
                System.out.println("ThreeException");
            } finally {
                System.out.println("In finally clause");
                if(count == 2) break; // out of "while"
            }
        }
    }
}
```

输出为：

```
ThreeException
In finally clause
No exception
In finally clause
```

可以从输出中发现，无论异常是否被抛出，finally 子句总能被执行。这个程序也给了我们一些思路，当 Java 中的异常不允许我们回到异常抛出的地点时，那么该如何应对呢？如果把 try 块放在循环里，就建立了一个“程序继续执行之前必须要达到”的条件。还可以加入一个 static 类型的计数器或者别的装置，使循环在放弃以前能尝试一定的次数。这将使程序的健壮性更上一个台阶。

### finally 用来做什么？

对于没有垃圾回收和析构函数自动调用机制的语言来说，finally 非常重要。它能使程序员保证：无论 try 块里发生了什么，内存总能得到释放。但 Java 有垃圾回收机制，所以内存释放不再是问题。而且，Java 也没有析构函数可供调用。那么，Java 在什么情况下才能用到 finally 呢？

当要把除内存之外的资源恢复到它们的初始状态时，就要用到 finally 子句。这种需要清理的资源包括：已经打开的文件或网络连接，在屏幕上画的图形，甚至可以是外部世界的某个开关，如下面例子所示：

```java
// exceptions/Switch.java
public class Switch {
    private boolean state = false;
    public boolean read() { return state; }
    public void on() {
        state = true;
        System.out.println(this);
    }
    public void off() {
        state = false;
        System.out.println(this);
    }
    @Override
    public String toString() {
        return state ? "on" : "off";
    }
}
// exceptions/OnOffException1.java
public class OnOffException1 extends Exception {}
// exceptions/OnOffException2.java
public class OnOffException2 extends Exception {}
// exceptions/OnOffSwitch.java
// Why use finally?
public class OnOffSwitch {
    private static Switch sw = new Switch();
    public static void f()
            throws OnOffException1, OnOffException2 {}
    public static void main(String[] args) {
        try {
            sw.on();
			// Code that can throw exceptions...
            f();
            sw.off();
        } catch(OnOffException1 e) {
            System.out.println("OnOffException1");
            sw.off();
        } catch(OnOffException2 e) {
            System.out.println("OnOffException2");
            sw.off();
        }
    }
}
```

输出为：

```
on
off
```

程序的目的是要确保 main() 结束的时候开关必须是关闭的，所以在每个 try 块和异常处理程序的末尾都加入了对 sw.offo 方法的调用。但也可能有这种情况：异常被抛出，但没被处理程序捕获，这时 sw.off() 就得不到调用。但是有了 finally，只要把 try 块中的清理代码移放在一处即可：

```java
// exceptions/WithFinally.java
// Finally Guarantees cleanup
public class WithFinally {
    static Switch sw = new Switch();
    public static void main(String[] args) {
        try {
            sw.on();
			// Code that can throw exceptions...
            OnOffSwitch.f();
        } catch(OnOffException1 e) {
            System.out.println("OnOffException1");
        } catch(OnOffException2 e) {
            System.out.println("OnOffException2");
        } finally {
            sw.off();
        }
    }
}
```

输出为：

```java
on
off
```

这里 sw.off() 被移到一处，并且保证在任何情况下都能得到执行。

甚至在异常没有被当前的异常处理程序捕获的情况下，异常处理机制也会在跳到更高一层的异常处理程序之前，执行 finally 子句：

```java
// exceptions/AlwaysFinally.java
// Finally is always executed
class FourException extends Exception {}
public class AlwaysFinally {
    public static void main(String[] args) {
        System.out.println("Entering first try block");
        try {
            System.out.println("Entering second try block");
            try {
                throw new FourException();
            } finally {
                System.out.println("finally in 2nd try block");
            }
        } catch(FourException e) {
            System.out.println(
                    "Caught FourException in 1st try block");
        } finally {
            System.out.println("finally in 1st try block");
        }
    }
}
```

输出为：

```java
Entering first try block
Entering second try block
finally in 2nd try block
Caught FourException in 1st try block
finally in 1st try block
```

当涉及 break 和 continue 语句的时候，finally 子句也会得到执行。请注意，如果把 finally 子句和带标签的 break 及 continue 配合使用，在 Java 里就没必要使用 goto 语句了。

### 在 return 中使用 finally

因为 finally 子句，总是会执行的，所以在一个方法中，可以从多个点返回，并且可以保证重要的清理工作仍旧会执行：

```java
// exceptions/MultipleReturns.java
public class MultipleReturns {
    public static void f(int i) {
        System.out.println(
                "Initialization that requires cleanup");
        try {
            System.out.println("Point 1");
            if(i == 1) return;
            System.out.println("Point 2");
            if(i == 2) return;
            System.out.println("Point 3");
            if(i == 3) return;
            System.out.println("End");
            return;
        } finally {
            System.out.println("Performing cleanup");
        }
    }
    public static void main(String[] args) {
        for(int i = 1; i <= 4; i++)
            f(i);
    }
}
```

输出为：

```java
Initialization that requires cleanup
Point 1
Performing cleanup
Initialization that requires cleanup
Point 1
Point 2
Performing cleanup
Initialization that requires cleanup
Point 1
Point 2
Point 3
Performing cleanup
Initialization that requires cleanup
Point 1
Point 2
Point 3
End
Performing cleanup
```

从输出中可以看出，从何处返回无关紧要，finally 子句永远会执行。

### 缺憾：异常丢失

遗憾的是，Java 的异常实现也有瑕疵。异常作为程序出错的标志，决不应该被忽略，但它还是有可能被轻易地忽略。用某些特殊的方式使用 finally 子句，就会发生这种情况：

```java
// exceptions/LostMessage.java
// How an exception can be lost
class VeryImportantException extends Exception {
    @Override
    public String toString() {
        return "A very important exception!";
    }
}
class HoHumException extends Exception {
    @Override
    public String toString() {
        return "A trivial exception";
    }
}
public class LostMessage {
    void f() throws VeryImportantException {
        throw new VeryImportantException();
    }
    void dispose() throws HoHumException {
        throw new HoHumException();
    }
    public static void main(String[] args) {
        try {
            LostMessage lm = new LostMessage();
            try {
                lm.f();
            } finally {
                lm.dispose();
            }
        } catch(VeryImportantException | HoHumException e) {
            System.out.println(e);
        }
    }
}
```

输出为：

```
A trivial exception
```

从输出中可以看到，VeryImportantException 不见了，它被 finally 子句里的 HoHumException 所取代。这是相当严重的缺陷，因为异常可能会以一种比前面例子所示更微妙和难以察党的方式完全丢失。相比之下，C++把“前一个异常还没处理就抛出下一个异常”的情形看成是糟糕的编程错误。也许在 Java 的未来版本中会修正这个问题（另一方面，要把所有抛出异常的方法，如上例中的 dispose() 方法，全部打包放到 try-catch 子句里面）。

一种更加简单的丢失异常的方式是从 finally 子句中返回：

```java
// exceptions/ExceptionSilencer.java
public class ExceptionSilencer {
    public static void main(String[] args) {
        try {
            throw new RuntimeException();
        } finally {
// Using 'return' inside the finally block
// will silence any thrown exception.
            return;
        }
    }
}
```

如果运行这个程序，就会看到即使抛出了异常，它也不会产生任何输出。

<!-- Exception Restrictions -->

## 异常限制


<!-- Constructors -->
## 异常构造


<!-- Try-With-Resources -->
## Try-With-Resources 用法


<!-- Exception Matching -->
## 异常匹配


<!-- Alternative Approaches -->
## 异常准则


<!-- Exception Guidelines -->
## 异常指南


<!-- Summary -->
## 本章小结







<!-- 分页 -->

<div style="page-break-after: always;"></div>
```

```