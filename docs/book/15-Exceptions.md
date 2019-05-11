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

<!-- The Exception Specification -->

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

## 异常规范


<!-- Catching Any Exception -->
## 任意异常捕获


<!-- Standard Java Exceptions -->
## Java 标准异常


<!-- Performing Cleanup with finally -->
## finally 关键字


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