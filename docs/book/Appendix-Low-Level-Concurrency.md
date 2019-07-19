[TOC]

<!-- Appendix: Low-Level Concurrency -->
# 附录:并发底层原理

> 尽管不建议您自己编写底层 Java 并发代码，但是这样通常有助于了解它是如何工作的。

[并发编程](./24-Concurrent-Programming.md) 章节中介绍了一些用于高级并发的概念，包括为 Java 并发编程而最新提出的，更安全的概念（ parallel Streams 和 CompletableFutures  ）。本附录则介绍在 Java 中底层并发概念，因此在阅读本篇时，您能有所了解掌握这些代码。您还会将进一步了解并发的普遍问题。

在 Java 的早期版本中, 底层并发概念是并发编程的重要组成部分。我们会着眼于围绕这些技巧的复杂性以及为何您应该避免它们而谈。 “并发编程” 章节展示最新的 Java 版本(尤其是 Java 8)所提供的改进技巧，这些技巧使得并发的使用，如果本来不容易使用，也会变得更容易些。



<!-- What is a Thread? -->
## 什么是线程？

并发将程序划分成独立分离运行的任务。每个任务都由一个 *执行线程* 来驱动，我们通常将其简称为 *线程* 。而一个 *线程* 就是操作系统进程中单一顺序的控制流。因此，单个进程可以有多个并发执行的任务，但是你的程序使得每个任务都好像有自己的处理器一样。这线程模型为编程带来了便利，它简化了在单一程序中处理变戏法般的多任务过程。操作系统则从处理器上分配时间片到你程序的所有线程中。

Java 并发的核心机制是 **Thread** 类，在该语言最初版本中， **Thread （线程）** 是由程序员直接创建和管理的。随着语言的发展以及人们发现了更好的一些方法，中间层机制 - 特别是 **Executor** 框架  - 被添加进来，以消除自己管理线程时候的心理负担（及错误）。 最终，甚至发展出比 **Executor** 更好的机制，如 [并发编程](./24-Concurrent-Programming.md) 一章所示。

**Thread（线程）** 是将任务关联到处理器的软件概念。虽然创建和使用 **Thread**  类看起来与任何其他类都很相似，但实际上它们是非常不同的。当你创建一个 **Thread** 时，JVM 将分配一大块内存到专为线程保留的特殊区域上，用于提供运行任务时所需的一切，包括：

* 程序计数器，指明要执行的下一个 JVM 字节码指令。
* 用于支持 Java 代码执行的栈，包含有关此线程已到达当时执行位置所调用方法的信息。它也包含每个正在执行的方法的所有局部变量(包括原语和堆对象的引用)。每个线程的栈通常在 64K 到 1M 之间 [^1] 。
* 第二个则用于 native code（本机方法代码）执行的栈
* *thread-local variables* （线程本地变量）的存储区域
* 用于控制线程的状态管理变量

包括 **main()** 在内的所有代码都会在某个线程内运行。 每当调用一个方法时，当前程序计数器被推到该线程的栈上，然后栈指针向下移动以足够来创建一个栈帧，其栈帧里存储该方法的所有局部变量，参数和返回值。所有基本类型变量都直接在栈上，虽然方法中创建（或方法中使用）对象的任何引用都位于栈帧中，但对象本身存于堆中。这仅且只有一个堆，被程序中所有线程所共享。

除此以外，线程必须绑定到操作系统，这样它就可以在某个时候连接到处理器。这是作为线程构建过程的一部分为您管理的。Java 使用底层操作系统中的机制来管理线程的执行。

### 最佳线程数

如果你查看第 24 章 [并发编程](./24-Concurrent-Programming.md) 中使用 *CachedThreadPool* 的用例，你会发现 **ExecutorService** 为每个我们提交的任务分配一个线程。然而，并行流（**parallel Stream**）在 [**CountingStream.java** ](https://github.com/BruceEckel/OnJava8-Examples/blob/master/concurrent/CountingStream.java
) 中只分配了 8 个线程（id 中 1-7 为工作线程，8 为  **main()** 方法的主线程，它巧妙地将其用作额外的并行流）。如果你尝试提高 **range()** 方法中的上限值，你会看到没有创建额外的线程。这是为什么？

我们可以查出当前机器上处理器的数量：

```Java
// lowlevel/NumberOfProcessors.java

public class NumberOfProcessors {
  public static void main(String[] args) {
    System.out.println(
    Runtime.getRuntime().availableProcessors());
  }
}
/* Output:
8
*/
```

在我的机器上（使用英特尔酷睿i7），我有四个内核，每个内核呈现两个*超线程*（指一种硬件技巧，能在单个处理器上产生非常快速的上下文切换，在某些情况下可以使内核看起来像运行两个硬件线程）。虽然这是 “最近” 计算机上的常见配置(在撰写本文时)，但你可能会看到不同的结果，包括 **CountingStream.java ** 中同等数量的默认线程。

你的操作系统可能有办法来查出关于处理器的更多信息，例如，在Windows 10上，按下 “开始” 键，输入 “任务管理器” 和 Enter 键。点击 “详细信息” 。选择 “性能” 标签,您将会看到各种各样的关于您的硬件信息,包括“内核” 和 “逻辑处理器” 。

事实证明，“通用”线程的最佳数量就算是可用处理器的数量(对于特定的问题可能不是这样)。这原因来自在Java线程之间切换上下文的代价：存储被挂起线程的当前状态，并检索另一个线程的当前状态，以便从它进入挂起的位置继续执行。对于 8 个处理器和 8 个（计算密集型）Java线程，JVM 在运行这8个任务时从不需要切换上下文。对于比处理器数量少的任务，分配更多线程没有帮助。

定义了 “逻辑处理器” 数量的 Intel 超线程，但并没有增加计算能力 - 该特性在硬件级别维护额外的线程上下文，从而加快了上下文切换，这有助于提高用户界面的响应能力。对于计算密集型任务，请考虑将线程数量与物理内核(而不是超线程)的数量匹配。尽管Java认为每个超线程都是一个处理器，但这似乎是由于 Intel 对超线程的过度营销造成的错误。尽管如此，为了简化编程，我只允许 JVM 决定默认的线程数。 你将需要试验你的产品应用。 这并不意味着将线程数与处理器数相匹配就适用于所有问题; 相反，它主要用于计算密集型解决方案。

### 我可以创建多少个线程？

Thread（线程）对象的最大部分是用于执行方法的 Java 堆栈。查看 Thread （线程）对象的大小因操作系统而异。该程序通过创建 Thread 对象来测试它，直到 JVM 内存不足为止：

```java
// lowlevel/ThreadSize.java
// {ExcludeFromGradle} Takes a long time or hangs
import java.util.concurrent.*;
import onjava.Nap;

public class ThreadSize {
  static class Dummy extends Thread {
    @Override
    public void run() { new Nap(1); }
  }
  public static void main(String[] args) {
    ExecutorService exec =
      Executors.newCachedThreadPool();
    int count = 0;
    try {
      while(true) {
        exec.execute(new Dummy());
        count++;
      }
    } catch(Error e) {
      System.out.println(
      e.getClass().getSimpleName() + ": " + count);
      System.exit(0);
    } finally {
      exec.shutdown();
    }
  }
}
```

只要你不断递交任务，**CachedThreadPool** 就会继续创建线程。将 **Dummy** 对象递交到 **execute()** 方法以开始任务，如果线程池无可用线程，则分配一个新线程。执行的暂停方法 **pause()** 运行时间必须足够长，使任务不会开始即完成(从而为新任务释放现有线程)。只要任务不断进入而没有完成，**CachedThreadPool** 最终就会耗尽内存。

我并不总是能够在我尝试的每台机器上造成内存不足的错误。在一台机器上，我看到这样的结果:

```shell
> java ThreadSize
OutOfMemoryError: 2816
```

我们可以使用 **-Xss** 标记减少每个线程栈分配的内存大小。允许的最小线程栈大小是 64k:

```shell
>java -Xss64K ThreadSize
OutOfMemoryError: 4952
```

如果我们将线程栈大小增加到 2M ，我们就可以分配更少的线程。

```shell
>java -Xss2M ThreadSize
OutOfMemoryError: 722
```

Windows 操作系统默认栈大小是 320K，我们可以通过验证它给出的数字与我们完全不设置栈大小时的数字是大致相同:

```shell
>java -Xss320K ThreadSize
OutOfMemoryError: 2816
```

你还可以使用 **-Xmx** 标志增加 JVM 的最大内存分配:

```shell
>java -Xss64K -Xmx5M ThreadSize
OutOfMemoryError: 5703
```

请注意的是操作系统还可能对允许的线程数施加限制。

因此，“我可以拥有多少线程”这一问题的答案是“几千个”。但是，如果你发现自己分配了数千个线程，那么您可能需要重新考虑您的做法; 恰当的问题是“我需要多少线程？”

### The WorkStealingPool

这是一个 **ExecutorService** ，它使用所有可用的(由JVM报告) 处理器自动创建线程池。

```java
// lowlevel/WorkStealingPool.java
import java.util.stream.*;
import java.util.concurrent.*;

class ShowThread implements Runnable {
  @Override
  public void run() {
    System.out.println(
    Thread.currentThread().getName());
  }
}

public class WorkStealingPool {
  public static void main(String[] args)
    throws InterruptedException {
    System.out.println(
      Runtime.getRuntime().availableProcessors());
    ExecutorService exec =
      Executors.newWorkStealingPool();
    IntStream.range(0, 10)
      .mapToObj(n -> new ShowThread())
      .forEach(exec::execute);
    exec.awaitTermination(1, TimeUnit.SECONDS);
  }
}
/* Output:
8
ForkJoinPool-1-worker-2
ForkJoinPool-1-worker-1
ForkJoinPool-1-worker-2
ForkJoinPool-1-worker-3
ForkJoinPool-1-worker-2
ForkJoinPool-1-worker-1
ForkJoinPool-1-worker-3
ForkJoinPool-1-worker-1
ForkJoinPool-1-worker-4
ForkJoinPool-1-worker-2
*/
```

工作窃取算法允许已经耗尽输入队列中的工作项的线程从其他队列“窃取”工作项。目标是在处理器之间分配工作项，从而最大限度地利用所有可用的处理器来完成计算密集型任务。这项算法也用于 Java 的fork/join 框架。

<!-- Catching Exceptions -->
## 异常捕获

这可能会让你感到惊讶：

```java
// lowlevel/SwallowedException.java
import java.util.concurrent.*;

public class SwallowedException {
  public static void main(String[] args)
    throws InterruptedException {
    ExecutorService exec =
      Executors.newSingleThreadExecutor();
    exec.submit(() -> {
      throw new RuntimeException();
    });
    exec.shutdown();
  }
}
```

这个程序什么也不输出（然而，如果你用 **execute** 方法替换 **submit()** 方法，你就将会看到异常抛出。这说明在线程中抛出异常是很棘手的，需要特别注意的事情。

你无法捕获到从线程逃逸的异常。一旦异常越过了任务的 **run()** 方法，它就会传递至控制台，除非您采取特殊步骤来捕获此类错误异常。

下面是一个抛出异常的代码，该异常会传递到它的 **run()** 方法之外，而 **main()** 方法会显示运行它时会发生什么：

```java
// lowlevel/ExceptionThread.java
// {ThrowsException}
import java.util.concurrent.*;

public class ExceptionThread implements Runnable {
  @Override
  public void run() {
    throw new RuntimeException();
  }
  public static void main(String[] args) {
    ExecutorService es =
      Executors.newCachedThreadPool();
    es.execute(new ExceptionThread());
    es.shutdown();
  }
}
/* Output:
___[ Error Output ]___
Exception in thread "pool-1-thread-1"
java.lang.RuntimeException
        at ExceptionThread.run(ExceptionThread.java:8)
        at java.util.concurrent.ThreadPoolExecutor.runW
orker(ThreadPoolExecutor.java:1142)
        at java.util.concurrent.ThreadPoolExecutor$Work
er.run(ThreadPoolExecutor.java:617)
        at java.lang.Thread.run(Thread.java:745)
*/
```

输出是(经过调整一些限定符以适应阅读)：

```
Exception in thread "pool-1-thread-1" RuntimeException
  at ExceptionThread.run(ExceptionThread.java:9)
  at ThreadPoolExecutor.runWorker(...)
  at ThreadPoolExecutor$Worker.run(...)
  at java.lang.Thread.run(Thread.java:745)
```

即使在 **main()** 方法体内包裹 **try-catch** 代码块来捕获异常也不成功：

```java
// lowlevel/NaiveExceptionHandling.java
// {ThrowsException}
import java.util.concurrent.*;

public class NaiveExceptionHandling {
  public static void main(String[] args) {
    ExecutorService es =
      Executors.newCachedThreadPool();
    try {
      es.execute(new ExceptionThread());
    } catch(RuntimeException ue) {
      // This statement will NOT execute!
      System.out.println("Exception was handled!");
    } finally {
      es.shutdown();
    }
  }
}
/* Output:
___[ Error Output ]___
Exception in thread "pool-1-thread-1"
java.lang.RuntimeException
        at ExceptionThread.run(ExceptionThread.java:8)
        at java.util.concurrent.ThreadPoolExecutor.runW
orker(ThreadPoolExecutor.java:1142)
        at java.util.concurrent.ThreadPoolExecutor$Work
er.run(ThreadPoolExecutor.java:617)
        at java.lang.Thread.run(Thread.java:745)
*/
```

这会产生与前一个示例相同的结果:未捕获异常。

为解决这个问题，需要改变 **Executor** （执行器）生成线程的方式。 **Thread.UncaughtExceptionHandler** 是一个添加给每个 **Thread** 对象，用于进行异常处理的接口。

当该线程即将死于未捕获的异常时，将自动调用 **Thread.UncaughtExceptionHandler.uncaughtException()**
 方法。为了调用该方法，我们创建一个新的 `ThreadFactory` 类型来让 **Thread.UncaughtExceptionHandler** 对象附加到每个它所新创建的 **Thread**（线程）对象上。我们赋值该工厂对象给 **Executors** 对象的 方法，让它的方法来生成新的 **ExecutorService** 对象：

```java
// lowlevel/CaptureUncaughtException.java
import java.util.concurrent.*;

class ExceptionThread2 implements Runnable {
  @Override
  public void run() {
    Thread t = Thread.currentThread();
    System.out.println("run() by " + t.getName());
    System.out.println(
      "eh = " + t.getUncaughtExceptionHandler());
    throw new RuntimeException();
  }
}

class MyUncaughtExceptionHandler implements
Thread.UncaughtExceptionHandler {
  @Override
  public void uncaughtException(Thread t, Throwable e) {
    System.out.println("caught " + e);
  }
}

class HandlerThreadFactory implements ThreadFactory {
  @Override
  public Thread newThread(Runnable r) {
    System.out.println(this + " creating new Thread");
    Thread t = new Thread(r);
    System.out.println("created " + t);
    t.setUncaughtExceptionHandler(
      new MyUncaughtExceptionHandler());
    System.out.println(
      "eh = " + t.getUncaughtExceptionHandler());
    return t;
  }
}

public class CaptureUncaughtException {
  public static void main(String[] args) {
    ExecutorService exec =
      Executors.newCachedThreadPool(
        new HandlerThreadFactory());
    exec.execute(new ExceptionThread2());
    exec.shutdown();
  }
}
/* Output:
HandlerThreadFactory@4e25154f creating new Thread
created Thread[Thread-0,5,main]
eh = MyUncaughtExceptionHandler@70dea4e
run() by Thread-0
eh = MyUncaughtExceptionHandler@70dea4e
caught java.lang.RuntimeException
*/
```

额外会在代码中添加跟踪机制，用来验证工厂对象创建的线程是否获得新 **UncaughtExceptionHandler** 。现在未捕获的异常由 **uncaughtException** 方法捕获。

上面的示例根据具体情况来设置处理器。如果你知道你将要在代码中处处使用相同的异常处理器，那么更简单的方式是在 **Thread** 类中设置一个 **static**（静态） 字段，并将这个处理器设置为默认的未捕获异常处理器：

```java
// lowlevel/SettingDefaultHandler.java
import java.util.concurrent.*;

public class SettingDefaultHandler {
  public static void main(String[] args) {
    Thread.setDefaultUncaughtExceptionHandler(
      new MyUncaughtExceptionHandler());
    ExecutorService es =
      Executors.newCachedThreadPool();
    es.execute(new ExceptionThread());
    es.shutdown();
  }
}
/* Output:
caught java.lang.RuntimeException
*/
```

只有在每个线程没有设置异常处理器时候，默认处理器才会被调用。系统会检查线程专有的版本，如果没有，则检查是否线程组中有专有的 **uncaughtException()** 方法；如果都没有，就会调用 **defaultUncaughtExceptionHandler** 方法。

可以将此方法与 **CompletableFuture**s 的改进方法进行比较。

<!-- Sharing Resources -->
## 资源共享

你可以将单线程程序看作一个孤独的实体，在你的问题空间中移动并一次只做一件事。因为只有一个实体，你永远不会想到两个实体试图同时使用相同资源的问题：问题犹如两个人试图同时停放在同一个空间，同时走过一扇门，甚至同时说话。

通过并发，事情不再孤单，但现在两个或更多任务可能会相互干扰。如果您不阻止这种冲突，您将有两个任务同时尝试访问同一个银行帐户，打印到同一个打印机，调整同一个阀门，等等。

### 资源竞争

当你启动一个任务来执行某些工作时，可以通过两种不同的方式捕获该工作的结果:通过副作用或通过返回值。

从编程方式上看，副作用似乎更容易:你只需使用结果来操作环境中的某些东西。例如，你的任务可能会执行一些计算，然后直接将其结果写入集合。

这种方法的问题是集合通常是共享资源。当运行多个任务时，任何任务都可能同时读写 *共享资源* 。这揭示了 *资源竞争* 问题，这是处理任务时的主要陷阱之一。

在单线程系统中，您不会考虑资源竞争，因为你一次只做一件事。当你有多个任务时，必须始终防止资源竞争。

解决此问题的的一种方法是使用能够应对资源竞争的集合，如果多个任务同时尝试对此类集合进行写入，那么此类集合可以应付该问题。在 Java 并发库中，你将发现许多尝试解决资源争用问题的类；在本附录中，您将看到其中的一些，但覆盖范围并不全面。

请思考以下的示例，其中一个任务负责生成偶数，其他任务则负责消费这些数字。在这里，消费者任务的唯一工作就是检查偶数的有效性。

我们将定义消费者任务 **EvenChecker** 类，以便在后续示例中可复用。为了将 **EvenChecker** 与我们的各种实验生成器类解耦，我们首先创建一个名为 **IntGenerator** 的抽象类，它包含 **EvenChecker** 必须知道的最少必要方法：它包含一个 **next()** 方法，以及可以取消生成的方法。

```java
// lowlevel/IntGenerator.java
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class IntGenerator {
  private AtomicBoolean canceled =
    new AtomicBoolean();
  public abstract int next();
  public void cancel() { canceled.set(true); }
  public boolean isCanceled() {
    return canceled.get();
  }
}
```

**cancel()** 方法改变 **AtomicBoolean canceled** 标志位的状态， 而 **isCanceled()** 方法则告诉标志位是否设置。因为 **canceled** 标志位是 **AtomicBoolean** 类型，所以它是原子性的，这意味着分配和值返回等简单操作发生时没有中断的可能性，因此你无法在这些简单操作中看到该字段处于中间状态。您将在本附录的后面部分了解有关原子性和 **Atomic** 类的更多信息

任何 **IntGenerator** 都可以使用下面的 **EvenChecker** 类进行测试:

```java
// lowlevel/EvenChecker.java
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import onjava.TimedAbort;

public class EvenChecker implements Runnable {
  private IntGenerator generator;
  private final int id;
  public EvenChecker(IntGenerator generator, int id) {
    this.generator = generator;
    this.id = id;
  }
  @Override
  public void run() {
    while(!generator.isCanceled()) {
      int val = generator.next();
      if(val % 2 != 0) {
        System.out.println(val + " not even!");
        generator.cancel(); // Cancels all EvenCheckers
      }
    }
  }
  // Test any IntGenerator:
  public static void test(IntGenerator gp, int count) {
    List<CompletableFuture<Void>> checkers =
      IntStream.range(0, count)
        .mapToObj(i -> new EvenChecker(gp, i))
        .map(CompletableFuture::runAsync)
        .collect(Collectors.toList());
    checkers.forEach(CompletableFuture::join);
  }
  // Default value for count:
  public static void test(IntGenerator gp) {
    new TimedAbort(4, "No odd numbers discovered");
    test(gp, 10);
  }
}
```

**test()** 方法开启了许多访问同一个 **IntGenerator** 的 **EvenChecker**。**EvenChecker** 任务们会不断读取和测试与其关联的 **IntGenerator** 对象中的生成值。如果 **IntGenerator** 导致失败，**test()** 方法会报告并返回。

依赖于 **IntGenerator** 对象的所有 **EvenChecker** 任务都会检查它是否已被取消。如果 **generator.isCanceled()** 返回值为 true ，则 **run()** 方法返回。 任何 **EvenChecker** 任务都可以在 **IntGenerator** 上调用**cancel()** ，这会导致使用该 **IntGenerator** 的其他所有 **EvenChecker** 正常关闭。

在本设计中，共享公共资源（ **IntGenerator** ）的任务会监视该资源的终止信号。这消除所谓的竞争条件，其中两个或更多的任务竞争响应某个条件并因此冲突或不一致结果的情况。

你必须仔细考虑并防止并发系统失败的所有可能途径。例如，一个任务不能依赖于另一个任务，因为任务关闭的顺序无法得到保证。这里，通过使任务依赖于非任务对象，我们可以消除潜在的竞争条件。

一般来说，我们假设 **test()** 方法最终失败，因为各个 **EvenChecker** 的任务在 **IntGenerator** 处于 “不恰当的” 状态时，仍能够访问其中的信息。但是，直到 **IntGenerator** 完成许多循环之前，它可能无法检测到问题，具体取决于操作系统的详细信息和其他实现细节。为确保本书的自动构建不会卡住，我们使用 **TimedAbort** 类，在此处定义：

```java
// onjava/TimedAbort.java
// Terminate a program after t seconds
package onjava;
import java.util.concurrent.*;

public class TimedAbort {
  private volatile boolean restart = true;
  public TimedAbort(double t, String msg) {
    CompletableFuture.runAsync(() -> {
      try {
        while(restart) {
          restart = false;
          TimeUnit.MILLISECONDS
            .sleep((int)(1000 * t));
        }
      } catch(InterruptedException e) {
        throw new RuntimeException(e);
      }
      System.out.println(msg);
      System.exit(0);
    });
  }
  public TimedAbort(double t) {
    this(t, "TimedAbort " + t);
  }
  public void restart() { restart = true; }
}
```

我们使用 lambda 表达式创建一个 **Runnable** ，该表达式使用 **CompletableFuture** 的 **runAsync()** 静态方法执行。  **runAsync()** 方法的值会立即返回。 因此，**TimedAbort** 不会保持任何打开的任务，否则已完成任务，但如果它需要太长时间，它仍将终止该任务（ **TimedAbort** 有时被称为守护进程）。

**TimedAbort** 还允许你 **restart()** 方法重启任务，在有某些有用的活动进行时保持程序打开。

我们可以看到正在运行的 **TimedAbort** 示例:

```java
// lowlevel/TestAbort.java
import onjava.*;

public class TestAbort {
  public static void main(String[] args) {
    new TimedAbort(1);
    System.out.println("Napping for 4");
    new Nap(4);
  }
}
/* Output:
Napping for 4
TimedAbort 1.0
*/
```

如果你注释掉 **Nap** 创建实列那行，程序执行会立即退出，表明 **TimedAbort** 没有维持程序打开。

我们将看到第一个 **IntGenerator** 示例有一个生成一系列偶数值的 **next()** 方法：

```java
// lowlevel/EvenProducer.java
// When threads collide
// {VisuallyInspectOutput}

public class EvenProducer extends IntGenerator {
  private int currentEvenValue = 0;
  @Override
  public int next() {
    ++currentEvenValue; // [1]
    ++currentEvenValue;
    return currentEvenValue;
  }
  public static void main(String[] args) {
    EvenChecker.test(new EvenProducer());
  }
}
/* Output:
419 not even!
425 not even!
423 not even!
421 not even!
417 not even!
*/
```
* [1] 一个任务有可能在另外一个任务执行第一个对 **currentEvenValue** 的递增操作之后，但是没有执行第二个操作之前，调用 **next()** 方法。这将使这个值处于 “不恰当” 的状态。

为了证明这是可能发生的， **EvenChecker.test()** 创建了一组 **EventChecker** 对象，以连续读取 **EvenProducer** 的输出并测试检查每个数值是否都是偶数。如果不是，就会报告错误，而程序也将关闭。

多线程程序的部分问题是，即使存在 bug ，如果失败的可能性很低，程序仍然可以正确显示。

重要的是要注意到递增操作自身需要多个步骤，并且在递增过程中任务可能会被线程机制挂起 - 也就是说，在 Java 中，递增不是原子性的操作。因此，如果不保护任务，即使单一的递增也不是线程安全的。

该示例程序并不总是在第一次非偶数产生时终止。所有任务都不会立即关闭，这是并发程序的典型特征。

### 解决资源竞争

前面的示例揭示了当你使用线程时的基本问题：你永远不知道线程何时运行。想象一下坐在一张桌子上，用叉子，将最后一块食物放在盘子上，当叉子到达时，食物突然消失...因为你的线程被挂起而另一个用餐者进来吃了食物了。这就是在编写并发程序时要处理的问题。为了使并发工作，您需要某种方式来阻止两个任务访问同一个资源，至少在关键时期是这样。

防止这种冲突的方法就是当资源被一个任务使用时，在其上加锁。第一个访问某项资源的任务必须锁定这项资源，使其他任务在其被解锁之前，就无法访问它了，而在其被解锁之时，另一个任务就可以锁定并使用它，以此类推。如果汽车前排座位是受限资源，那么大喊着 “冲呀” 的孩子就会（在这次旅途过程中）获得该资源的锁。

为了解决线程冲突的问题，基本的并发方案将序列化访问共享资源。这意味着一次只允许一个任务访问共享资源。这通常是通过在访问资源的代码片段周围加上一个子句来实现的，该子句一次只允许一个任务访问这段代码。因为这个子句产生 *互斥* 效果，所以这种机制的通常称为是 *mutex* （互斥量）。

考虑一下屋子里的浴室：多个人（即多个由线程驱动的任务）都希望能独立使用浴室（即共享资源）。为了使用浴室，一个人先敲门来看看是否可用。如果没人的话，他就能进入浴室并锁上门。任何其他想使用浴室的任务就会被 “阻挡”，因此这些任务就在门口等待，直到浴室是可用的。

当浴室使用完毕，就是时候给其他任务进入，这时比喻就有点不准确了。事实上没有人排队，我们也不知道下一个使用浴室是谁，因为线程调度机制并不是确定性的。相反，就好像在浴室前面有一组被阻止的任务一样，当锁定浴室的任务解锁并出现时，线程调度机制将会决定下一个要进入的任务。

Java 以提供关键字 **synchronized** 的形式，为防止资源冲突提供了内置支持。当任务希望执行被 **synchronized** 关键字保护的代码片段的时候，Java 编译器会生成代码以查看锁是否可用。如果可用，该任务获取锁，执行代码，然后释放锁。

共享资源一般是以对象形式存在的内存片段，但也可以是文件、I/O 端口，或者类似打印机的东西。要控制对共享资源的访问，得先把它包装进一个对象。然后把任何访问该资源的方法标记为 **synchronized** 。 如果一个任务在调用其中一个 **synchronized** 方法之内，那么在这个任务从该方法返回之前，其他所有要调用该对象的 **synchronized** 方法的任务都会被阻塞。

通常你会将字段设为 **private**，并仅通过方法访问这些字段。你可用通过使用 **synchronized** 关键字声明方法来防止资源冲突。如下所示：

```java
synchronized void f() { /* ... */ }
synchronized void g() { /* ... */ }
```

所有对象都自动包含单一的锁（也称为 *monitor*，即监视器）。当你调用对象上任何 **synchronized** 方法，此对象将被加锁，并且该对象上的的其他 **synchronized** 方法调用只有等到前一个方法执行完成并释放了锁之后才能被调用。如果一个任务对对象调用了 **f()** ，对于同一个对象而言，就只能等到 **f()** 调用结束并释放了锁之后，其他任务才能调用 **f()** 和 **g()**。所以，某个特定对象的所有 **synchronized** 方法共享同一个锁，这个锁可以防止多个任务同时写入对象内存。

在使用并发时，将字段设为 **private** 特别重要；否则，**synchronized** 关键字不能阻止其他任务直接访问字段，从而产生资源冲突。

一个线程可以多次获取对象的锁。如果一个方法在同一个对象上调用第二个方法，而后者又在同一个对象上调用另一个方法，就会发生这种情况。 JVM 会跟踪对象被锁定的次数。如果对象已解锁，则其计数为 0 。当一个线程第一次锁时，计数变为 1 。每次同一线程在同一对象上获取另一个锁时，计数就会递增。显然，只有首先获得锁的线程才允许多次获取多个锁。每当线程离开 **synchronized** 方法时，计数递减，当、直到计数变为 0 ，完全释放锁以给其他线程使用。每个类也有一个锁（作为该类的 **Class** 对象的一部分），因此 **synchronized** 静态方法可以在类范围的基础上彼此锁定，不让同时访问静态数据。

你应该什么时候使用同步呢？可以永远 *Brian* 的同步法则[^2]。

> 如果你正在写一个变量，它可能接下来被另一个线程读取，或者正在读取一个上一次已经被另一个线程写过的变量，那么你必须使用同步，并且，读写线程都必须用相同的监视器锁同步。

如果在你的类中有超过一个方法在处理临界数据，那么你必须同步所有相关方法。如果只同步其中一个方法，那么其他方法可以忽略对象锁，并且可以不受惩罚地调用。这是很重要的一点：每个访问临界共享资源的方法都必须被同步，否则将不会正确地工作。

### 同步控制 EventProducer

通过在 **EvenProducer.java** 文件中添加 **synchronized** 关键字，可以防止不希望的线程访问：

```java
// lowlevel/SynchronizedEvenProducer.java
// Simplifying mutexes with the synchronized keyword
import onjava.Nap;

public class
SynchronizedEvenProducer extends IntGenerator {
  private int currentEvenValue = 0;
  @Override
  public synchronized int next() {
    ++currentEvenValue;
    new Nap(0.01); // Cause failure faster
    ++currentEvenValue;
    return currentEvenValue;
  }
  public static void main(String[] args) {
    EvenChecker.test(new SynchronizedEvenProducer());
  }
}
/* Output:
No odd numbers discovered
*/
```

在两个递增操作之间插入 **Nap()** 构造器方法，以提高在 **currentEvenValue** 是奇数的状态时上下文切换的可能性。因为互斥锁可以阻止多个任务同时进入临界区，所有这不会产生失败。第一个进入 **next()** 方法的任务将获得锁，任何试图获取锁的后续任务都将被阻塞，直到第一个任务释放锁。此时，调度机制选择另一个等待锁的任务。通过这种方式，任何时刻只能有一个任务通过互斥锁保护的代码。

<!-- The volatile Keyword -->
## volatile 关键字

**volatile** 可能是Java中最微妙和最难用的关键字。幸运的是，在现代 Java 中，你几乎总能避免使用它，如果你确实看到它在代码中使用，你应该保持怀疑态度和怀疑 - 这很有可能代码是过时的，或者编写代码的人不清楚在大体上（或两者都有）易变性（**volatile**） 或并发性的后果。

使用 **volatile** 有三个理由。

### 字分裂

当你的 Java 数据类型足够大（在 Java 中 **long** 和 **double** 类型都是 64 位），写入变量的过程分两步进行，就会发生 *Word tearing* （字分裂）情况。 JVM 被允许将64位数量的读写作为两个单独的32位操作执行[^3] ，这增加了在读写过程中发生上下文切换的可能性，因此其他任务会看到不正确的结果。这被称为 *Word tearing* （字分裂），因为你可能只看到其中一部分修改后的值。基本上，任务有时可以在第一步之后但在第二步之前读取变量，从而产生垃圾值（对于例如 **boolean** 或 **int** 类型的小变量是没有问题的；任何 **long** 或 **double** 类型则除外）。

在缺乏任何其他保护的情况下，用 **volatile** 修饰符定义一个 **long** 或 **double** 变量，可阻止字分裂情况。然而，如果使用 **synchronized** 或 **java.util.concurrent.atomic** 类之一保护这些变量，则volatile将被取代。此外，**volatile** 不会影响到增量操作并不是原子操作的事实。

### 可见性

### 重排与 *Happen-Before* 原则

### 什么时候使用 volatile

<!-- Atomicity -->
## 原子性

### Josh 的序列数字

### 使用显式锁定对象

### 原子类

<!-- Critical Sections -->
## 临界区

### 在其他对象上同步

<!-- Library Components -->
## 库组件

### DelayQueue

### PriorityBlockingQueue

### Lock-Free Collections

<!-- Summary -->
## 本章小结

本附录主要是为了让您在遇到底层并发代码时能对此有一定的了解，尽管本文还远没对这个主题进行全面的讨论。为此，你需要先从阅读由 Brian Goetz, Tim Peierls, Joshua Bloch, Joseph Bowbeer, David Holmes, and Doug Lea (Addison-Wesley 出版社, 2006)所著作的 *Java Concurrency in Practice* （国内译名：Java并发编程实战）开始了解。理想情况下，这本书会完全吓跑你在 Java 中尝试去编写底层并发代码。如果没有，那么你几乎肯定患上了达克效应(DunningKruger Effect)，这是一种认知偏差，“你知道的越少，对自己的能力就越有信心”。请记住，当前的语言设计人员仍然在清理早期语言设计人员过于自信造成的混乱(例如，查看 Thread 类中有多少方法被弃用，而 volatile 直到 Java 5 才正确工作)。

以下是并发编程的步骤:

1. 不要使用它。想一些其他方法来使你写的程序变的更快。
2. 如果你必须使用它，请使用在 [并发编程](./24-Concurrent-Programming.md) - parallel Streams and CompletableFutures 中展示的现代高级工具。
3. 不要在任务间共享变量，必须在任务之间传递的任何信息都应该使用 Java.util.concurrent 库中的并发数据结构。
4. 如果必须在任务之间共享变量，请使用 java.util.concurrent.atomic 里面其中一种类型，或在任何直接或间接访问这些变量的方法上应用 synchronized。 当你不这样做时，很容易被愚弄，以为你已经把所有东西都包括在内。 说真的，尝试使用步骤 3。
5. 如果步骤 4 产生的结果太慢，你可以尝试使用volatile 或其他技术来调整代码，但是如果你正在阅读本书并认为你已经准备好尝试这些方法，那么你就超出了你的深度。 返回步骤＃1。

通常可以只使用 java.util.concurrent 库组件来编写并发程序，完全避免来自应用 volatile 和 synchronized 的挑战。注意，我可以通过 [并发编程](./24-Concurrent-Programming.md)  中的示例来做到这一点。

[^1]: 在某些平台上，特别是 Windows ，默认值可能非常难以查明。您可以使用 -Xss 标志调整堆栈大小。

[^2]: 引自 Brian Goetz, Java Concurrency in Practice 一书的作者 , 该书由 Brian Goetz, Tim Peierls, Joshua Bloch, Joseph Bowbeer, David Holmes, and Doug Lea 联合著作 (Addison-Wesley 出版社, 2006)。↩

[^3]: 请注意，在64位处理器上可能不会发生这种情况，从而消除了这个问题。

[^4]: 这个测试的推论是，“如果某人表示线程是容易并且简单的，请确保这个人没有对你的项目做出重要的决策。如果那个人已经做出，那么你就已经陷入麻烦之中了。”

[^5]: 这版本是我参与的；这可能在以后的标准中得到了修正


<!-- 分页 -->
<div style="page-break-after: always;"></div>
