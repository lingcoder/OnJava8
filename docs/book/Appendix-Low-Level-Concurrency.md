[TOC]

<!-- Appendix: Low-Level Concurrency -->
# 附录:并发底层原理

> 尽管不建议你自己编写底层 Java 并发代码，但是这样通常有助于了解它是如何工作的。

[并发编程](./24-Concurrent-Programming.md) 章节中介绍了一些用于高级并发的概念，包括为 Java 并发编程而最新提出的，更安全的概念（ parallel Streams 和 CompletableFutures  ）。本附录则介绍在 Java 中底层并发概念，因此在阅读本篇时，你能有所了解掌握这些代码。你还会将进一步了解并发的普遍问题。

在 Java 的早期版本中, 底层并发概念是并发编程的重要组成部分。我们会着眼于围绕这些技巧的复杂性以及为何你应该避免它们而谈。 “并发编程” 章节展示最新的 Java 版本(尤其是 Java 8)所提供的改进技巧，这些技巧使得并发的使用，如果本来不容易使用，也会变得更容易些。

<!-- What is a Thread? -->
## 什么是线程？

并发将程序划分成独立分离运行的任务。每个任务都由一个 *执行线程* 来驱动，我们通常将其简称为 *线程* 。而一个 *线程* 就是操作系统进程中单一顺序的控制流。因此，单个进程可以有多个并发执行的任务，但是你的程序使得每个任务都好像有自己的处理器一样。此线程模型为编程带来了便利，它简化了在单一程序中处理变戏法般的多任务过程。操作系统则从处理器上分配时间片到你程序的所有线程中。

Java 并发的核心机制是 **Thread** 类，在该语言最初版本中， **Thread （线程）** 是由程序员直接创建和管理的。随着语言的发展以及人们发现了更好的一些方法，中间层机制 - 特别是 **Executor** 框架  - 被添加进来，以消除自己管理线程时候的心理负担（及错误）。 最终，甚至发展出比 **Executor** 更好的机制，如 [并发编程](./24-Concurrent-Programming.md) 一章所示。

**Thread（线程）** 是将任务关联到处理器的软件概念。虽然创建和使用 **Thread**  类看起来与任何其他类都很相似，但实际上它们是非常不同的。当你创建一个 **Thread** 时，JVM 将分配一大块内存到专为线程保留的特殊区域上，用于提供运行任务时所需的一切，包括：

* 程序计数器，指明要执行的下一个 JVM 字节码指令。
* 用于支持 Java 代码执行的栈，包含有关此线程已到达当时执行位置所调用方法的信息。它也包含每个正在执行的方法的所有局部变量(包括原语和堆对象的引用)。每个线程的栈通常在 64K 到 1M 之间 [^1] 。
* 第二个则用于 native code（本机方法代码）执行的栈
* *thread-local variables* （线程本地变量）的存储区域
* 用于控制线程的状态管理变量

包括 `main()` 在内的所有代码都会在某个线程内运行。 每当调用一个方法时，当前程序计数器被推到该线程的栈上，然后栈指针向下移动以足够来创建一个栈帧，其栈帧里存储该方法的所有局部变量，参数和返回值。所有基本类型变量都直接在栈上，虽然方法中创建（或方法中使用）对象的任何引用都位于栈帧中，但对象本身存于堆中。这仅且只有一个堆，被程序中所有线程所共享。

除此以外，线程必须绑定到操作系统，这样它就可以在某个时候连接到处理器。这是作为线程构建过程的一部分为你管理的。Java 使用底层操作系统中的机制来管理线程的执行。

### 最佳线程数

如果你查看第 24 章 [并发编程](./24-Concurrent-Programming.md) 中使用 *CachedThreadPool* 的用例，你会发现 **ExecutorService** 为每个我们提交的任务分配一个线程。然而，并行流（**parallel Stream**）在 [**CountingStream.java** ](https://github.com/BruceEckel/OnJava8-Examples/blob/master/concurrent/CountingStream.java
) 中只分配了 8 个线程（id 中 1-7 为工作线程，8 为  `main()` 方法的主线程，它巧妙地将其用作额外的并行流）。如果你尝试提高 `range()` 方法中的上限值，你会看到没有创建额外的线程。这是为什么？

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

你的操作系统可能有办法来查出关于处理器的更多信息，例如，在Windows 10上，按下 “开始” 键，输入 “任务管理器” 和 Enter 键。点击 “详细信息” 。选择 “性能” 标签,你将会看到各种各样的关于你的硬件信息,包括“内核” 和 “逻辑处理器” 。

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

只要你不断递交任务，**CachedThreadPool** 就会继续创建线程。将 **Dummy** 对象递交到 `execute()` 方法以开始任务，如果线程池无可用线程，则分配一个新线程。执行的暂停方法 `pause()` 运行时间必须足够长，使任务不会开始即完成(从而为新任务释放现有线程)。只要任务不断进入而没有完成，**CachedThreadPool** 最终就会耗尽内存。

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

因此，“我可以拥有多少线程”这一问题的答案是“几千个”。但是，如果你发现自己分配了数千个线程，那么你可能需要重新考虑你的做法; 恰当的问题是“我需要多少线程？”

### The WorkStealingPool (工作窃取线程池)

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

这个程序什么也不输出（然而，如果你用 **execute** 方法替换 `submit()` 方法，你就将会看到异常抛出。这说明在线程中抛出异常是很棘手的，需要特别注意的事情。

你无法捕获到从线程逃逸的异常。一旦异常越过了任务的 `run()` 方法，它就会传递至控制台，除非你采取特殊步骤来捕获此类错误异常。

下面是一个抛出异常的代码，该异常会传递到它的 `run()` 方法之外，而 `main()` 方法会显示运行它时会发生什么：

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

即使在 `main()` 方法体内包裹 **try-catch** 代码块来捕获异常也不成功：

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

当该线程即将死于未捕获的异常时，将自动调用 `Thread.UncaughtExceptionHandler.uncaughtException()`
 方法。为了调用该方法，我们创建一个新的 **ThreadFactory** 类型来让 **Thread.UncaughtExceptionHandler** 对象附加到每个它所新创建的 **Thread**（线程）对象上。我们赋值该工厂对象给 **Executors** 对象的 方法，让它的方法来生成新的 **ExecutorService** 对象：

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

只有在每个线程没有设置异常处理器时候，默认处理器才会被调用。系统会检查线程专有的版本，如果没有，则检查是否线程组中有专有的 `uncaughtException()` 方法；如果都没有，就会调用 **defaultUncaughtExceptionHandler** 方法。

可以将此方法与 **CompletableFuture** 的改进方法进行比较。

<!-- Sharing Resources -->
## 资源共享

你可以将单线程程序看作一个孤独的实体，在你的问题空间中移动并同一时间只做一件事。因为只有一个实体，你永远不会想到两个实体试图同时使用相同资源的问题：问题犹如两个人试图同时停放在同一个空间，同时走过一扇门，甚至同时说话。

通过并发，事情不再孤单，但现在两个或更多任务可能会相互干扰。如果你不阻止这种冲突，你将有两个任务同时尝试访问同一个银行帐户，打印到同一个打印机，调整同一个阀门，等等。

### 资源竞争

当你启动一个任务来执行某些工作时，可以通过两种不同的方式捕获该工作的结果:通过副作用或通过返回值。

从编程方式上看，副作用似乎更容易:你只需使用结果来操作环境中的某些东西。例如，你的任务可能会执行一些计算，然后直接将其结果写入集合。

伴随这种方式的问题是集合通常是共享资源。当运行多个任务时，任何任务都可能同时读写 *共享资源* 。这揭示了 *资源竞争* 问题，这是处理任务时的主要陷阱之一。

在单线程系统中，你不需要考虑资源竞争，因为你永远不可能同时做多件事。当你有多个任务时，你就必须始终防止资源竞争。

解决此问题的的一种方法是使用能够应对资源竞争的集合，如果多个任务同时尝试对此类集合进行写入，那么此类集合可以应付该问题。在 Java 并发库中，你将发现许多尝试解决资源竞争问题的类；在本附录中，你将看到其中的一些，但覆盖范围并不全面。

请思考以下的示例，其中一个任务负责生成偶数，其他任务则负责消费这些数字。在这里，消费者任务的唯一工作就是检查偶数的有效性。

我们将定义消费者任务 **EvenChecker** 类，以便在后续示例中可复用。为了将 **EvenChecker** 与我们的各种实验生成器类解耦，我们首先创建名为 **IntGenerator** 的抽象类，它包含 **EvenChecker** 必须知道的最低必要方法：它包含 `next()` 方法，以及可以取消它执行生成的方法。

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

`cancel()` 方法改变 **AtomicBoolean** 类型的 **canceled** 标志位的状态， 而 `isCanceled()` 方法则告诉标志位是否设置。因为 **canceled** 标志位是 **AtomicBoolean** 类型，由于它是原子性的，这意味着分配和值返回等简单操作发生时没有中断的可能性，因此你无法在这些简单操作中看到该字段处于中间状态。你将在本附录的后面部分了解有关原子性和 **Atomic** 类的更多信息

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

`test()` 方法开启了许多访问同一个 **IntGenerator** 的 **EvenChecker**。**EvenChecker** 任务们会不断读取和测试与其关联的 **IntGenerator** 对象中的生成值。如果 **IntGenerator** 导致失败，`test()` 方法会报告并返回。

依赖于 **IntGenerator** 对象的所有 **EvenChecker** 任务都会检查它是否已被取消。如果 `generator.isCanceled()` 返回值为 true ，则 `run()` 方法返回。 任何 **EvenChecker** 任务都可以在 **IntGenerator** 上调用 `cancel()` ，这会导致使用该 **IntGenerator** 的其他所有 **EvenChecker** 正常关闭。

在本设计中，共享公共资源（ **IntGenerator** ）的任务会监视该资源的终止信号。这消除所谓的竞争条件，其中两个或更多的任务竞争响应某个条件并因此冲突或不一致结果的情况。

你必须仔细考虑并防止并发系统失败的所有可能途径。例如，一个任务不能依赖于另一个任务，因为任务关闭的顺序无法得到保证。这里，通过使任务依赖于非任务对象，我们可以消除潜在的竞争条件。

一般来说，我们假设 `test()` 方法最终失败，因为各个 **EvenChecker** 的任务在 **IntGenerator** 处于 “不恰当的” 状态时，仍能够访问其中的信息。但是，直到 **IntGenerator** 完成许多循环之前，它可能无法检测到问题，具体取决于操作系统的详细信息和其他实现细节。为确保本书的自动构建不会卡住，我们使用 **TimedAbort** 类，在此处定义：

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

我们使用 lambda 表达式创建一个 **Runnable** ，该表达式使用 **CompletableFuture** 的 `runAsync()` 静态方法执行。  `runAsync()` 方法的值会立即返回。 因此，**TimedAbort** 不会保持任何打开的任务，否则已完成任务，但如果它需要太长时间，它仍将终止该任务（ **TimedAbort** 有时被称为守护进程）。

**TimedAbort** 还允许你 `restart()` 方法重启任务，在有某些有用的活动进行时保持程序打开。

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

我们将看到第一个 **IntGenerator** 示例有一个生成一系列偶数值的 `next()` 方法：

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
* [1] 一个任务有可能在另外一个任务执行第一个对 **currentEvenValue** 的自增操作之后，但是没有执行第二个操作之前，调用 `next()` 方法。这将使这个值处于 “不恰当” 的状态。

为了证明这是可能发生的， `EvenChecker.test()` 创建了一组 **EventChecker** 对象，以连续读取 **EvenProducer** 的输出并测试检查每个数值是否都是偶数。如果不是，就会报告错误，而程序也将关闭。

多线程程序的部分问题是，即使存在 bug ，如果失败的可能性很低，程序仍然可以正确显示。

重要的是要注意到自增操作自身需要多个步骤，并且在自增过程中任务可能会被线程机制挂起 - 也就是说，在 Java 中，自增不是原子性的操作。因此，如果不保护任务，即使单纯的自增也不是线程安全的。

该示例程序并不总是在第一次非偶数产生时终止。所有任务都不会立即关闭，这是并发程序的典型特征。

### 解决资源竞争

前面的示例揭示了当你使用线程时的基本问题：你永远不知道线程哪个时刻运行。想象一下坐在一张桌子上，用叉子，将最后一块食物放在盘子上，当叉子到达时，食物突然消失...仅因为你的线程被挂起而另一个用餐者进来吃了食物了。这就是在编写并发程序时要处理的问题。为了使并发工作有效，你需要某种方式来阻止两个任务访问同一个资源，至少在关键时期是这样。

防止这种冲突的方法就是当资源被一个任务使用时，在其上加锁。第一个访问某项资源的任务必须锁定这项资源，使其他任务在其被解锁之前，就无法访问它，而在其被解锁时候，另一个任务就可以锁定并使用它，以此类推。如果汽车前排座位是受限资源，那么大喊着 “冲呀” 的孩子就会（在这次旅途过程中）获得该资源的锁。

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

所有对象都自动包含独立的锁（也称为 *monitor*，即监视器）。当你调用对象上任何 **synchronized** 方法，此对象将被加锁，并且该对象上的的其他 **synchronized** 方法调用只有等到前一个方法执行完成并释放了锁之后才能被调用。如果一个任务对对象调用了 `f()` ，对于同一个对象而言，就只能等到 `f()` 调用结束并释放了锁之后，其他任务才能调用 `f()` 和 `g()`。所以，某个特定对象的所有 **synchronized** 方法共享同一个锁，这个锁可以防止多个任务同时写入对象内存。

在使用并发时，将字段设为 **private** 特别重要；否则，**synchronized** 关键字不能阻止其他任务直接访问字段，从而产生资源冲突。

一个线程可以获取对象的锁多次。如果一个方法调用在同一个对象上的第二个方法，而后者又在同一个对象上调用另一个方法，就会发生这种情况。 JVM 会跟踪对象被锁定的次数。如果对象已解锁，则其计数为 0 。当一个线程首次获得锁时，计数变为 1 。每次同一线程在同一对象上获取另一个锁时，计数就会自增。显然，只有首先获得锁的线程才允许多次获取多个锁。每当线程离开 **synchronized** 方法时，计数递减，直到计数变为 0 ，完全释放锁以给其他线程使用。每个类也有一个锁（作为该类的 **Class** 对象的一部分），因此 **synchronized** 静态方法可以在类范围的基础上彼此锁定，不让同时访问静态数据。

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

在两个自增操作之间插入 `Nap()` 构造器方法，以提高在 **currentEvenValue** 是奇数的状态时上下文切换的可能性。因为互斥锁可以阻止多个任务同时进入临界区，所有这不会产生失败。第一个进入 `next()` 方法的任务将获得锁，任何试图获取锁的后续任务都将被阻塞，直到第一个任务释放锁。此时，调度机制选择另一个等待锁的任务。通过这种方式，任何时刻只能有一个任务通过互斥锁保护的代码。

<!-- The volatile Keyword -->
## volatile 关键字

**volatile** 可能是 Java 中最微妙和最难用的关键字。幸运的是，在现代 Java 中，你几乎总能避免使用它，如果你确实看到它在代码中使用，你应该保持怀疑态度和怀疑 - 这很有可能代码是过时的，或者编写代码的人不清楚使用它在大体上（或两者都有）易变性（**volatile**） 或并发性的后果。

使用 **volatile** 有三个理由。

### 字分裂

当你的 Java 数据类型足够大（在 Java 中 **long** 和 **double** 类型都是 64 位），写入变量的过程分两步进行，就会发生 *Word tearing* （字分裂）情况。 JVM 被允许将64位数量的读写作为两个单独的32位操作执行[^3]，这增加了在读写过程中发生上下文切换的可能性，因此其他任务会看到不正确的结果。这被称为 *Word tearing* （字分裂），因为你可能只看到其中一部分修改后的值。基本上，任务有时可以在第一步之后但在第二步之前读取变量，从而产生垃圾值（对于例如 **boolean** 或 **int** 类型的小变量是没有问题的；任何 **long** 或 **double** 类型则除外）。

在缺乏任何其他保护的情况下，用 **volatile** 修饰符定义一个 **long** 或 **double** 变量，可阻止字分裂情况。然而，如果使用 **synchronized** 或 **java.util.concurrent.atomic** 类之一保护这些变量，则 **volatile** 将被取代。此外，**volatile** 不会影响到增量操作并不是原子操作的事实。

### 可见性

第二个问题属于 [Java 并发的四句格言](./24-Concurrent-Programming.md#四句格言)里第二句格言 “一切都重要” 的部分。你必须假设每个任务拥有自己的处理器，并且每个处理器都有自己的本地内存缓存。该缓存准许处理器运行的更快，因为处理器并不总是需要从比起使用缓存显著花费更多时间的主内存中获取数据。

出现这个问题是因为 Java 尝试尽可能地提高执行效率。缓存的主要目的是避免从主内存中读取数据。当并发时，有时不清楚 Java 什么时候应该将值从主内存刷新到本地缓存 — 而这个问题称为 *缓存一致性* （ *cache coherence* ）。

每个线程都可以在处理器缓存中存储变量的本地副本。将字段定义为 **volatile** 可以防止这些编译器优化，这样读写就可以直接进入内存，而不会被缓存。一旦该字段发生写操作，所有任务的读操作都将看到更改。如果一个 **volatile** 字段刚好存储在本地缓存，则会立即将其写入主内存，并且该字段的任何读取都始终发生在主内存中。

**volatile** 应该在何时适用于变量：

1. 该变量同时被多个任务访问。
2. 这些访问中至少有一个是写操作。
3. 你尝试避免同步 （在现代 Java 中，你可以使用高级工具来避免进行同步）。

举个例子，如果你使用变量作为停止任务的标志值。那么该变量至少必须声明为 **volatile** （尽管这并不一定能保证这种标志的线程安全）。否则，当一个任务更改标志值时，这些更改可以存储在本地处理器缓存中，而不会刷新到主内存。当另一个任务查看标记值时，它不会看到更改。我更喜欢在 [并发编程](./24-Concurrent-Programming.md) 中 [终止耗时任务](./24-Concurrent-Programming.md#终止耗时任务) 章节中使用 **AtomicBoolean** 类型作为标志值的办法

任务对其自身变量所做的任何写操作都始终对该任务可见，因此，如果只在任务中使用变量，你不需要使其变量声明为 **volatile** 。

如果单个线程对变量写入而其他线程只读取它，你可以放弃该变量声明为 **volatile**。通常，如果你有多个线程对变量写入，**volatile** 无法解决你的问题，并且你必须使用 **synchronized** 来防止竞争条件。 这有一个特殊的例外：可以让多个线程对该变量写入，*只要它们不需要先读取它并使用该值创建新值来写入变量* 。如果这些多个线程在结果中使用旧值，则会出现竞争条件，因为其余一个线程之一可能会在你的线程进行计算时修改该变量。即使你开始做对了，想象一下在代码修改或维护过程中忘记和引入一个重大变化是多么容易，或者对于不理解问题的不同程序员来说是多么容易（这在 Java 中尤其成问题因为程序员倾向于严重依赖编译时检查来告诉他们，他们的代码是否正确）。

重要的是要理解原子性和可见性是两个不同的概念。在非 **volatile** 变量上的原子操作是不能保证是否将其刷新到主内存。

同步也会让主内存刷新，所以如果一个变量完全由 **synchronized** 的方法或代码段(或者 **java.util.concurrent.atomic** 库里类型之一)所保护，则不需要让变量用 **volatile**。

### 重排与 *Happen-Before* 原则

只要结果不会改变程序表现，Java 可以通过重排指令来优化性能。然而，重排可能会影响本地处理器缓存与主内存交互的方式，从而产生细微的程序 bug 。直到 Java 5 才理解并解决了这个无法阻止重排的问题。现在，**volatile** 关键字可以阻止重排 **volatile** 变量周围的读写指令。这种重排规则称为 *happens before* 担保原则 。

这项原则保证在 **volatile** 变量读写之前发生的指令先于它们的读写之前发生。同样，任何跟随 **volatile** 变量之后读写的操作都保证发生在它们的读写之后。例如：

```java
// lowlevel/ReOrdering.java

public class ReOrdering implements Runnable {
  int one, two, three, four, five, six;
  volatile int volaTile;
  @Override
  public void run() {
    one = 1;
    two = 2;
    three = 3;
    volaTile = 92;
    int x = four;
    int y = five;
    int z = six;
  }
}
```

例子中 **one**，**two**，**three** 变量赋值操作就可以被重排，只要它们都发生在 **volatile** 变量写操作之前。同样，只要 **volatile** 变量写操作发生在所有语句之前， **x**，**y**，**z** 语句可以被重排。这种 **volatile** （易变性）操作通常称为 *memory barrier* （内存屏障）。 *happens before* 担保原则确保 **volatile** 变量的读写指令不能跨过内存屏障进行重排。

*happens before* 担保原则还有另一个作用：当线程向一个 **volatile** 变量写入时，在线程写入之前的其他所有变量（包括非 **volatile** 变量）也会刷新到主内存。当线程读取一个 **volatile** 变量时，它也会读取其他所有变量（包括非 **volatile** 变量）与 **volatile** 变量一起刷新到主内存。尽管这是一个重要的特性，它解决了 Java 5 版本之前出现的一些非常狡猾的 bug ，但是你不应该依赖这项特性来“自动”使周围的变量变得易变性 （ **volatile** ）的 。如果你希望变量是易变性 （ **volatile** ）的，那么维护代码的任何人都应该清楚这一点。

### 什么时候使用 volatile

对于 Java 早期版本，编写一个证明需要 **volatile** 的示例并不难。如果你进行搜索，你可以找到这样的例子，但是如果你在 Java 8 中尝试这些例子，它们就不起作用了(我没有找到任何一个)。我努力写这样一个例子，但没什么用。这可能原因是 JVM 或者硬件，或两者都得到了改进。这种效果对现有的应该  **volatile** （易变性） 但不 **volatile** 的存储的程序是有益的；对于此类程序，失误发生的频率要低得多，而且问题更难追踪。

如果你尝试使用 **volatile** ，你可能更应该尝试让一个变量线程安全而不是引起同步的成本。因为 **volatile** 使用起来非常微妙和棘手，所以我建议根本不要使用它;相反，请使用本附录后面介绍的 **java.util.concurrent.atomic** 里面类之一。它们以比同步低得多的成本提供了完全的线程安全性。

如果你正在尝试调试其他人的并发代码，请首先查找使用 **volatile** 的代码并将其替换为**Atomic** 变量。除非你确定程序员对并发性有很高的理解，否则它们很可能会误用 **volatile** 。

<!-- Atomicity -->
## 原子性

在 Java 线程的讨论中，经常反复提交但不正确的知识是：“原子操作不需要同步”。 一个 *原子操作* 是不能被线程调度机制中断的操作；一旦操作开始，那么它一定可以在可能发生的“上下文切换”之前（切换到其他线程执行）执行完毕。依赖于原子性是很棘手且很危险的，如果你是一个并发编程专家，或者你得到了来自这样的专家的帮助，你才应该使用原子性来代替同步，如果你认为自己足够聪明可以应付这种玩火似的情况，那么请接受下面的测试：

> Goetz 测试：如果你可以编写用于现代微处理器的高性能 JVM ，那么就有资格考虑是否可以避免同步[^4] 。

了解原子性是很有用的，并且知道它与其他高级技术一起用于实现一些更加巧妙的  **java.util.concurrent** 库组件。 但是要坚决抵制自己依赖它的冲动。

原子性可以应用于除 **long** 和 **double** 之外的所有基本类型之上的 “简单操作”。对于读写和写入除 **long** 和 **double** 之外的基本类型变量这样的操作，可以保证它们作为不可分 (原子) 的操作执行。


因为原子操作不能被线程机制中断。专家程序员可以利用这个来编写无锁代码（*lock-free code*），这些代码不需要被同步。但即使这样也过于简单化了。有时候，甚至看起来应该是安全的原子操作，实际上也可能不安全。本书的读者通常不会通过前面提到的 Goetz 测试，因此也就不具备用原子操作来替换同步的能力。尝试着移除同步通常是一种表示不成熟优化的信号，并且会给你带来大量的麻烦，可能不会获得太多或任何的好处。

在多核处理器系统，相对于单核处理器而言，可见性问题远比原子性问题多得多。一个任务所做的修改，即使它们是原子性的，也可能对其他任务不可见（例如，修改只是暂时性存储在本地处理器缓存中），因此不同的任务对应用的状态有不同的视图。另一方面，同步机制强制多核处理器系统上的一个任务做出的修改必须在应用程序中是可见的。如果没有同步机制，那么修改时可见性将无法确认。

什么才属于原子操作时？对于属性中的值做赋值和返回操作通常都是原子性的，但是在 C++ 中，甚至下面的操作都可能是原子性的：

```c++
i++; // Might be atomic in C++
i += 2; // Might be atomic in C++
```

但是在 C++ 中，这取决于编译器和处理器。你无法编写出依赖于原子性的 C++ 跨平台代码，因为 C++ [^5]没有像 Java 那样的一致 *内存模型* （memory model）。

在 Java 中，上面的操作肯定不是原子性的，正如下面的方法产生的 JVM 指令中可以看到的那样：

```java
// lowlevel/NotAtomic.java
// {javap -c NotAtomic}
// {VisuallyInspectOutput}

public class NotAtomic {
  int i;
  void f1() { i++; }
  void f2() { i += 3; }
}
/* Output:
Compiled from "NotAtomic.java"
public class NotAtomic {
  int i;

  public NotAtomic();
    Code:
       0: aload_0
       1: invokespecial #1 // Method
java/lang/Object."<init>":()V
       4: return

  void f1();
    Code:
       0: aload_0
       1: dup
       2: getfield      #2 // Field
i:I
       5: iconst_1
       6: iadd
       7: putfield      #2 // Field
i:I
      10: return

  void f2();
    Code:
       0: aload_0
       1: dup
       2: getfield      #2 // Field
i:I
       5: iconst_3
       6: iadd
       7: putfield      #2 // Field
i:I
      10: return
}
*/
```

每条指令都会产生一个 “get” 和 “put”，它们之间还有一些其他指令。因此在获取指令和放置指令之间，另有一个任务可能会修改这个属性，所有，这些操作不是原子性的。

让我们通过定义一个抽象类来测试原子性的概念，这个抽象类的方法是将一个整数类型进行偶数自增，并且 `run()` 不断地调用这个方法:

```java
// lowlevel/IntTestable.java
import java.util.function.*;

public abstract class
IntTestable implements Runnable, IntSupplier {
  abstract void evenIncrement();
  @Override
  public void run() {
    while(true)
      evenIncrement();
  }
}
```

**IntSupplier** 是一个带 `getAsInt()` 方法的函数式接口。

现在我们可以创建一个测试，它作为一个独立的任务启动 `run()` 方法 ，然后获取值来检查它们是否为偶数:

```java
// lowlevel/Atomicity.java
import java.util.concurrent.*;
import onjava.TimedAbort;

public class Atomicity {
  public static void test(IntTestable it) {
    new TimedAbort(4, "No failures found");
    CompletableFuture.runAsync(it);
    while(true) {
      int val = it.getAsInt();
      if(val % 2 != 0) {
        System.out.println("failed with: " + val);
        System.exit(0);
      }
    }
  }
}
```

很容易盲目地应用原子性的概念。在这里，`getAsInt()` 似乎是安全的原子性方法：

```java
// lowlevel/UnsafeReturn.java
import java.util.function.*;
import java.util.concurrent.*;

public class UnsafeReturn extends IntTestable {
  private int i = 0;
  public int getAsInt() { return i; }
  public synchronized void evenIncrement() {
    i++; i++;
  }
  public static void main(String[] args) {
    Atomicity.test(new UnsafeReturn());
  }
}
/* Output:
failed with: 79
*/
```

但是， `Atomicity.test()` 方法还是出现有非偶数的失败。尽管，返回 **i** 变量确实是原子操作，但是同步缺失允许了在对象处于不稳定的中间状态时读取值。最重要的是，由于 **i** 也不是 **volatile** 变量，所以存在可见性问题。包括 `getValue()` 和 `evenIncrement()` 都必须同步(这也顾及到没有使用 **volatile** 修饰的 **i** 变量):

```java
// lowlevel/SafeReturn.java
import java.util.function.*;
import java.util.concurrent.*;

public class SafeReturn extends IntTestable {
  private int i = 0;
  public synchronized int getAsInt() { return i; }
  public synchronized void evenIncrement() {
    i++; i++;
  }
  public static void main(String[] args) {
    Atomicity.test(new SafeReturn());
  }
}
/* Output:
No failures found
*/
```

只有并发编程专家有能力去尝试做像前面例子情况的优化；再次强调，请遵循 Brain 的同步法则。

### Josh 的序列号

作为第二个示例，考虑某些更简单的东西：创建一个产生序列号的类，灵感启发于 Joshua Bloch 的 *Effective Java Programming Language Guide* (Addison-Wesley 出版社, 2001) 第 190 页。每次调用 `nextSerialNumber()` 都必须返回唯一值。

```java
// lowlevel/SerialNumbers.java

public class SerialNumbers {
  private volatile int serialNumber = 0;
  public int nextSerialNumber() {
    return serialNumber++; // Not thread-safe
  }
}
```

**SerialNumbers**  是你可以想象到最简单的类，如果你具备 C++ 或者其他底层的知识背景，你可能会认为自增是一个原子操作，因为 C++ 的自增操作通常被单个微处理器指令所实现（尽管不是以任何一致，可靠，跨平台的方式）。但是，正如前面所提到的，Java 自增操作不是原子性的，并且操作同时涉及读取和写入，因此即使在这样一个简单的操作中，也存在有线程问题的空间。

我们在这里加入 volatile ，看看它是否有帮助。然而，真正的问题是 `nextSerialNumber()` 方法在不进行线程同步的情况下访问共享的可变变量值。

为了测试 **SerialNumbers**，我们将创建一个不会耗尽内存的集合，假如需要很长时间来检测问题。这里展示的 **CircularSet** 重用了存储 **int** 变量的内存，最终新值会覆盖旧值(复制的速度通常发生足够快，你也可以使用  **java.util.Set** 来代替):

```java
// lowlevel/CircularSet.java
// Reuses storage so we don't run out of memory
import java.util.*;

public class CircularSet {
  private int[] array;
  private int size;
  private int index = 0;
  public CircularSet(int size) {
    this.size = size;
    array = new int[size];
    // Initialize to a value not produced
    // by SerialNumbers:
    Arrays.fill(array, -1);
  }
  public synchronized void add(int i) {
    array[index] = i;
    // Wrap index and write over old elements:
    index = ++index % size;
  }
  public synchronized boolean contains(int val) {
    for(int i = 0; i < size; i++)
      if(array[i] == val) return true;
    return false;
  }
}
```

`add()` 和 `contains()` 方法是线程同步的，以防止线程冲突。
The add() and contains() methods are synchronized to prevent thread collisions.

**SerialNumberChecker** 类包含一个存储最近序列号的 **CircularSet** 变量，以及一个填充数值给 **CircularSet**  和确保它里面的序列号是唯一的 `run()` 方法。

```java
// lowlevel/SerialNumberChecker.java
// Test SerialNumbers implementations for thread-safety
import java.util.concurrent.*;
import onjava.Nap;

public class SerialNumberChecker implements Runnable {
  private CircularSet serials = new CircularSet(1000);
  private SerialNumbers producer;
  public SerialNumberChecker(SerialNumbers producer) {
    this.producer = producer;
  }
  @Override
  public void run() {
    while(true) {
      int serial = producer.nextSerialNumber();
      if(serials.contains(serial)) {
        System.out.println("Duplicate: " + serial);
        System.exit(0);
      }
      serials.add(serial);
    }
  }
  static void test(SerialNumbers producer) {
    for(int i = 0; i < 10; i++)
      CompletableFuture.runAsync(
        new SerialNumberChecker(producer));
    new Nap(4, "No duplicates detected");
  }
}
```

`test()` 方法创建多个任务来竞争单独的 **SerialNumbers** 对象。这时参于竞争的的 SerialNumberChecker 任务们就会试图生成重复的序列号（这情况在具有更多内核处理器的机器上发生得更快）。

当我们测试基本的 **SerialNumbers** 类，它会失败（产生重复序列号）：

```java
// lowlevel/SerialNumberTest.java

public class SerialNumberTest {
  public static void main(String[] args) {
    SerialNumberChecker.test(new SerialNumbers());
  }
}
/* Output:
Duplicate: 148044
*/
```

**volatile** 在这里没有帮助。要解决这个问题，将 **synchronized** 关键字添加到 `nextSerialNumber()` 方法 :

```java
// lowlevel/SynchronizedSerialNumbers.java

public class
SynchronizedSerialNumbers extends SerialNumbers {
  private int serialNumber = 0;
  public synchronized int nextSerialNumber() {
    return serialNumber++;
  }
  public static void main(String[] args) {
    SerialNumberChecker.test(
      new SynchronizedSerialNumbers());
  }
}
/* Output:
No duplicates detected
*/
```

**volatile** 不再是必需的，因为 **synchronized** 关键字保证了 volatile （易变性） 的特性。

读取和赋值原语应该是安全的原子操作。然后，正如在 **UnsafeReturn.java** 中所看到，使用原子操作访问处于不稳定中间状态的对象仍然很容易。对这个问题做出假设既棘手又危险。最明智的做法就是遵循 Brian 的同步规则(如果可以，首先不要共享变量)。

### 原子类

Java 5 引入了专用的原子变量类，例如 **AtomicInteger**、**AtomicLong**、**AtomicReference** 等。这些提供了原子性升级。这些快速、无锁的操作，它们是利用了现代处理器上可用的机器级原子性。

下面，我们可以使用 **atomicinteger** 重写 **unsafereturn.java** 示例：

```java
// lowlevel/AtomicIntegerTest.java
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;
import onjava.*;

public class AtomicIntegerTest extends IntTestable {
  private AtomicInteger i = new AtomicInteger(0);
  public int getAsInt() { return i.get(); }
  public void evenIncrement() { i.addAndGet(2); }
  public static void main(String[] args) {
    Atomicity.test(new AtomicIntegerTest());
  }
}
/* Output:
No failures found
*/
```

现在，我们通过使用 **AtomicInteger** 来消除了 **synchronized** 关键字。

下面使用 **AtomicInteger** 来重写 **SynchronizedEvenProducer.java** 示例：

```java
// lowlevel/AtomicEvenProducer.java
// Atomic classes: occasionally useful in regular code
import java.util.concurrent.atomic.*;

public class AtomicEvenProducer extends IntGenerator {
  private AtomicInteger currentEvenValue =
    new AtomicInteger(0);
  @Override
  public int next() {
    return currentEvenValue.addAndGet(2);
  }
  public static void main(String[] args) {
    EvenChecker.test(new AtomicEvenProducer());
  }
}
/* Output:
No odd numbers discovered
*/
```

再次，使用 **AtomicInteger** 消除了对所有其他同步方式的需要。

下面是一个使用 **AtomicInteger** 实现 **SerialNumbers** 的例子:

```java
// lowlevel/AtomicSerialNumbers.java
import java.util.concurrent.atomic.*;

public class
AtomicSerialNumbers extends SerialNumbers {
  private AtomicInteger serialNumber =
    new AtomicInteger();
  public int nextSerialNumber() {
    return serialNumber.getAndIncrement();
  }
  public static void main(String[] args) {
    SerialNumberChecker.test(
      new AtomicSerialNumbers());
  }
}
/* Output:
No duplicates detected
*/
```

这些都是对单一字段的简单示例； 当你创建更复杂的类时，你必须确定哪些字段需要保护，在某些情况下，你可能仍然最后在方法上使用 **synchronized** 关键字。

<!-- Critical Sections -->
## 临界区

有时，你只是想防止多线程访问方法中的部分代码，而不是整个方法。要隔离的代码部分称为临界区，它使用我们用于保护整个方法相同的 **synchronized** 关键字创建，但使用不同的语法。语法如下， **synchronized** 指定某个对象作为锁用于同步控制花括号内的代码：

```java
synchronized(syncObject) {
  // This code can be accessed
  // by only one task at a time
}
```

这也被称为 *同步控制块* （synchronized block）；在进入此段代码前，必须得到 **syncObject** 对象的锁。如果一些其他任务已经得到这个锁，那么就得等到锁被释放以后，才能进入临界区。当发生这种情况时，尝试获取该锁的任务就会挂起。线程调度会定期回来并检查锁是否已经释放；如果释放了锁则唤醒任务。

使用同步控制块而不是同步控制整个方法的主要动机是性能（有时，算法确实聪明，但还是要特别警惕来自并发性问题上的聪明）。下面的示例演示了同步控制代码块而不是整个方法可以使方法更容易被其他任务访问。该示例会统计成功访问 `method()` 的计数并且发起一些任务来尝试竞争调用 `method()` 方法。

```java
// lowlevel/SynchronizedComparison.java
// speeds up access.
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import onjava.Nap;

abstract class Guarded {
  AtomicLong callCount = new AtomicLong();
  public abstract void method();
  @Override
  public String toString() {
    return getClass().getSimpleName() +
      ": " + callCount.get();
  }
}

class SynchronizedMethod extends Guarded {
  public synchronized void method() {
    new Nap(0.01);
    callCount.incrementAndGet();
  }
}

class CriticalSection extends Guarded {
  public void method() {
    new Nap(0.01);
    synchronized(this) {
      callCount.incrementAndGet();
    }
  }
}

class Caller implements Runnable {
  private Guarded g;
  Caller(Guarded g) { this.g = g; }
  private AtomicLong successfulCalls =
    new AtomicLong();
  private AtomicBoolean stop =
    new AtomicBoolean(false);
  @Override
  public void run() {
    new Timer().schedule(new TimerTask() {
      public void run() { stop.set(true); }
    }, 2500);
    while(!stop.get()) {
      g.method();
      successfulCalls.getAndIncrement();
    }
    System.out.println(
      "-> " + successfulCalls.get());
  }
}

public class SynchronizedComparison {
  static void test(Guarded g) {
    List<CompletableFuture<Void>> callers =
      Stream.of(
        new Caller(g),
        new Caller(g),
        new Caller(g),
        new Caller(g))
        .map(CompletableFuture::runAsync)
        .collect(Collectors.toList());
    callers.forEach(CompletableFuture::join);
    System.out.println(g);
  }
  public static void main(String[] args) {
    test(new CriticalSection());
    test(new SynchronizedMethod());
  }
}
/* Output:
-> 243
-> 243
-> 243
-> 243
CriticalSection: 972
-> 69
-> 61
-> 83
-> 36
SynchronizedMethod: 249
*/
```

**Guarded** 类负责跟踪 **callCount** 中成功调用 `method()`  的次数。**SynchronizedMethod** 的方式是同步控制整个 `method` 方法，而 **CriticalSection** 的方式是使用同步控制块来仅同步 `method` 方法的一部分代码。这样，耗时的 **Nap** 对象可以被排除到同步控制块外。输出会显示 **CriticalSection** 中可用的 `method()` 有多少。

请记住，使用同步控制块是有风险；它要求你确切知道同步控制块外的非同步代码是实际上要线程安全的。

**Caller** 是尝试在给定的时间周期内尽可能多地调用 `method()` 方法（并报告调用次数）的任务。为了构建这个时间周期，我们会使用虽然有点过时但仍然可以很好地工作的 **java.util.Timer** 类。此类接收一个 **TimerTask** 参数, 但该参数并不是函数式接口，所以我们不能使用 **lambda** 表达式，必须显式创建该类对象（在这种情况下，使用匿名内部类）。当超时的时候，定时对象将设置 **AtomicBoolean** 类型的 **stop** 字段为 true ，这样循环就会退出。

`test()` 方法接收一个 **Guarded** 类对象并创建四个 **Caller** 任务。所有这些任务都添加到同一个 **Guarded** 对象上，因此它们竞争来获取使用 `method()` 方法的锁。

你通常会看到从一次运行到下一次运行的输出变化。结果表明， **CriticalSection** 方式比起 **SynchronizedMethod** 方式允许更多地访问 `method()` 方法。这通常是使用 **synchronized** 块取代同步控制整个方法的原因：允许其他任务更多访问(只要这样做是线程安全的)。

### 在其他对象上同步

**synchronized** 块必须给定一个在其上进行同步的对象。并且最合理的方式是，使用其方法正在被调用的当前对象： **synchronized(this)**，这正是前面示例中 **CriticalSection** 采取的方式。在这种方式中，当 **synchronized** 块获得锁的时候，那么该对象其他的 **synchronized** 方法和临界区就不能被调用了。因此，在进行同步时，临界区的作用是减小同步的范围。

有时必须在另一个对象上同步，但是如果你要这样做，就必须确保所有相关的任务都是在同一个任务上同步的。下面的示例演示了当对象中的方法在不同的锁上同步时，两个任务可以同时进入同一对象：

```java
// lowlevel/SyncOnObject.java
// Synchronizing on another object
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import onjava.Nap;

class DualSynch {
  ConcurrentLinkedQueue<String> trace =
    new ConcurrentLinkedQueue<>();
  public synchronized void f(boolean nap) {
    for(int i = 0; i < 5; i++) {
      trace.add(String.format("f() " + i));
      if(nap) new Nap(0.01);
    }
  }
  private Object syncObject = new Object();
  public void g(boolean nap) {
    synchronized(syncObject) {
      for(int i = 0; i < 5; i++) {
        trace.add(String.format("g() " + i));
        if(nap) new Nap(0.01);
      }
    }
  }
}

public class SyncOnObject {
  static void test(boolean fNap, boolean gNap) {
    DualSynch ds = new DualSynch();
    List<CompletableFuture<Void>> cfs =
      Arrays.stream(new Runnable[] {
        () -> ds.f(fNap), () -> ds.g(gNap) })
        .map(CompletableFuture::runAsync)
        .collect(Collectors.toList());
    cfs.forEach(CompletableFuture::join);
    ds.trace.forEach(System.out::println);
  }
  public static void main(String[] args) {
    test(true, false);
    System.out.println("****");
    test(false, true);
  }
}
/* Output:
f() 0
g() 0
g() 1
g() 2
g() 3
g() 4
f() 1
f() 2
f() 3
f() 4
****
f() 0
g() 0
f() 1
f() 2
f() 3
f() 4
g() 1
g() 2
g() 3
g() 4
*/
```

`DualSync.f()` 方法（通过同步整个方法）在 **this** 上同步，而 `g()` 方法有一个在 **syncObject** 上同步的 **synchronized** 块。因此，这两个同步是互相独立的。在 `test()` 方法中运行的两个调用 `f()` 和 `g()` 方法的独立任务演示了这一点。**fNap** 和 **gNap** 标志变量分别指示 `f()` 和 `g()` 是否应该在其 **for** 循环中调用 `Nap()` 方法。例如，当 f() 线程休眠时 ，该线程继续持有它的锁，但是你可以看到这并不阻止调用 `g()` ，反之亦然。

### 使用显式锁对象

**java.util.concurrent** 库包含在 **java.util.concurrent.locks** 中定义的显示互斥锁机制。 必须显式地创建，锁定和解锁 **Lock** 对象，因此它产出的代码没有内置 **synchronized** 关键字那么优雅。然而，它在解决某些类型的问题时更加灵活。下面是使用显式 **Lock** 对象重写 **SynchronizedEvenProducer.java** 代码：

```java
// lowlevel/MutexEvenProducer.java
// Preventing thread collisions with mutexes
import java.util.concurrent.locks.*;
import onjava.Nap;

public class MutexEvenProducer extends IntGenerator {
  private int currentEvenValue = 0;
  private Lock lock = new ReentrantLock();
  @Override
  public int next() {
    lock.lock();
    try {
      ++currentEvenValue;
      new Nap(0.01); // Cause failure faster
      ++currentEvenValue;
      return currentEvenValue;
    } finally {
      lock.unlock();
    }
  }
  public static void main(String[] args) {
    EvenChecker.test(new MutexEvenProducer());
  }
}
/*
No odd numbers discovered
*/
```
**MutexEvenProducer** 添加一个名为 **lock** 的互斥锁并在 `next()` 中使用 `lock()` 和 `unlock()` 方法创建一个临界区。当你使用 **Lock** 对象时，使用下面显示的习惯用法很重要：在调用 `Lock()` 之后，你必须放置 **try-finally** 语句，该语句在 **finally** 子句中带有 `unlock()` 方法 - 这是确保锁总是被释放的惟一方法。注意，**return** 语句必须出现在 **try** 子句中，以确保 **unlock()** 不会过早发生并将数据暴露给第二个任务。

尽管 **try-finally** 比起使用 **synchronized** 关键字需要用得更多代码，但它也代表了显式锁对象的优势之一。如果使用 **synchronized** 关键字失败，就会抛出异常，但是你没有机会进行任何清理以保持系统处于良好状态。而使用显式锁对象，可以使用 **finally** 子句在系统中维护适当的状态。

一般来说，当你使用 **synchronized** 的时候，需要编写的代码更少，并且用户出错的机会也大大减少，因此通常只在解决特殊问题时使用显式锁对象。例如，使用 **synchronized** 关键字，你不能尝试获得锁并让其失败，或者你在一段时间内尝试获得锁，然后放弃 - 为此，你必须使用这个并发库。

```java
// lowlevel/AttemptLocking.java
// Locks in the concurrent library allow you
// to give up on trying to acquire a lock
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import onjava.Nap;

public class AttemptLocking {
  private ReentrantLock lock = new ReentrantLock();
  public void untimed() {
    boolean captured = lock.tryLock();
    try {
      System.out.println("tryLock(): " + captured);
    } finally {
      if(captured)
        lock.unlock();
    }
  }
  public void timed() {
    boolean captured = false;
    try {
      captured = lock.tryLock(2, TimeUnit.SECONDS);
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    }
    try {
      System.out.println(
        "tryLock(2, TimeUnit.SECONDS): " + captured);
    } finally {
      if(captured)
        lock.unlock();
    }
  }
  public static void main(String[] args) {
    final AttemptLocking al = new AttemptLocking();
    al.untimed(); // True -- lock is available
    al.timed();   // True -- lock is available
    // Now create a second task to grab the lock:
    CompletableFuture.runAsync( () -> {
        al.lock.lock();
        System.out.println("acquired");
    });
    new Nap(0.1);  // Give the second task a chance
    al.untimed(); // False -- lock grabbed by task
    al.timed();   // False -- lock grabbed by task
  }
}
/* Output:
tryLock(): true
tryLock(2, TimeUnit.SECONDS): true
acquired
tryLock(): false
tryLock(2, TimeUnit.SECONDS): false
*/
```

**ReentrantLock** 可以尝试或者放弃获取锁，因此如果某些任务已经拥有锁，你可以决定放弃并执行其他操作，而不是一直等到锁释放，就像 `untimed()` 方法那样。而在 `timed()` 方法中，则尝试获取可能在 2 秒后没成功而放弃的锁。在 `main()` 方法中，一个单独的线程被匿名类所创建，并且它会获得锁，因此让 `untimed()` 和 `timed() ` 方法有东西可以去竞争。

显式锁比起内置同步锁提供更细粒度的加锁和解锁控制。这对于实现专门的同步并发结构，比如用于遍历链表节点的 *交替锁* ( *hand-over-hand locking* ) ，也称为 *锁耦合* （ *lock coupling* ）- 该遍历代码要求必须在当前节点的解锁之前捕获下一个节点的锁。

<!-- Library Components -->
## 库组件

**java.util.concurrent** 库提供大量旨在解决并发问题的类，可以帮助你生成更简单，更鲁棒的并发程序。但请注意，这些工具是比起并行流和 **CompletableFuture** 更底层的机制。

在本节中，我们将看一些使用不同组件的示例，然后讨论一下 *lock-free*（无锁） 库组件是如何工作的。

### DelayQueue

这是一个无界阻塞队列 （ **BlockingQueue** ），用于放置实现了 **Delayed** 接口的对象，其中的对象只能在其到期时才能从队列中取走。这种队列是有序的，因此队首对象的延迟到期的时间最长。如果没有任何延迟到期，那么就不会有队首元素，并且 `poll()` 将返回 **null**（正因为这样，你不能将 **null** 放置到这种队列中）。

下面是一个示例，其中的 **Delayed** 对象自身就是任务，而 **DelayedTaskConsumer** 将最“紧急”的任务（到期时间最长的任务）从队列中取出，然后运行它。注意的是这样 **DelayQueue** 就成为了优先级队列的一种变体。

```java
// lowlevel/DelayQueueDemo.java
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import static java.util.concurrent.TimeUnit.*;

class DelayedTask implements Runnable, Delayed {
  private static int counter = 0;
  private final int id = counter++;
  private final int delta;
  private final long trigger;
  protected static List<DelayedTask> sequence =
    new ArrayList<>();
  DelayedTask(int delayInMilliseconds) {
    delta = delayInMilliseconds;
    trigger = System.nanoTime() +
      NANOSECONDS.convert(delta, MILLISECONDS);
    sequence.add(this);
  }
  @Override
  public long getDelay(TimeUnit unit) {
    return unit.convert(
      trigger - System.nanoTime(), NANOSECONDS);
  }
  @Override
  public int compareTo(Delayed arg) {
    DelayedTask that = (DelayedTask)arg;
    if(trigger < that.trigger) return -1;
    if(trigger > that.trigger) return 1;
    return 0;
  }
  @Override
  public void run() {
    System.out.print(this + " ");
  }
  @Override
  public String toString() {
    return
      String.format("[%d] Task %d", delta, id);
  }
  public String summary() {
    return String.format("(%d:%d)", id, delta);
  }
  public static class EndTask extends DelayedTask {
    EndTask(int delay) { super(delay); }
    @Override
    public void run() {
      sequence.forEach(dt ->
        System.out.println(dt.summary()));
    }
  }
}

public class DelayQueueDemo {
  public static void
  main(String[] args) throws Exception {
    DelayQueue<DelayedTask> tasks =
      Stream.concat( // Random delays:
        new Random(47).ints(20, 0, 4000)
          .mapToObj(DelayedTask::new),
        // Add the summarizing task:
        Stream.of(new DelayedTask.EndTask(4000)))
      .collect(Collectors
        .toCollection(DelayQueue::new));
    while(tasks.size() > 0)
      tasks.take().run();
  }
}
/* Output:
[128] Task 12 [429] Task 6 [551] Task 13 [555] Task 2
[693] Task 3 [809] Task 15 [961] Task 5 [1258] Task 1
[1258] Task 20 [1520] Task 19 [1861] Task 4 [1998] Task
17 [2200] Task 8 [2207] Task 10 [2288] Task 11 [2522]
Task 9 [2589] Task 14 [2861] Task 18 [2868] Task 7
[3278] Task 16 (0:4000)
(1:1258)
(2:555)
(3:693)
(4:1861)
(5:961)
(6:429)
(7:2868)
(8:2200)
(9:2522)
(10:2207)
(11:2288)
(12:128)
(13:551)
(14:2589)
(15:809)
(16:3278)
(17:1998)
(18:2861)
(19:1520)
(20:1258)
*/
```

**DelayedTask** 包含一个称为 **sequence** 的 **List&lt;DelayedTask&gt;** ，它保存了任务被创建的顺序，因此我们可以看到排序是按照实际发生的顺序执行的。

**Delay** 接口有一个方法， `getDelay()` ， 该方法用来告知延迟到期有多长时间，或者延迟在多长时间之前已经到期了。这个方法强制我们去使用 **TimeUnit** 类，因为这就是参数类型。这会产生一个非常方便的类，因为你可以很容易地转换单位而无需作任何声明。例如，**delta** 的值是以毫秒为单位存储的，但是 `System.nanoTime()` 产生的时间则是以纳秒为单位的。你可以转换 **delta** 的值，方法是声明它的单位以及你希望以什么单位来表示，就像下面这样：

```java
NANOSECONDS.convert(delta, MILLISECONDS);
```

在 `getDelay()` 中， 所希望的单位是作为 **unit** 参数传递进来的，你使用它将当前时间与触发时间之间的差转换为调用者要求的单位，而无需知道这些单位是什么（这是*策略*设计模式的一个简单示例，在这种模式中，算法的一部分是作为参数传递进来的）。

为了排序， **Delayed** 接口还继承了 **Comparable** 接口，因此必须实现 `compareTo()` , 使其可以产生合理的比较。

从输出中可以看到，任务创建的顺序对执行顺序没有任何影响 - 相反，任务是按照所期望的延迟顺序所执行的。

### PriorityBlockingQueue

这是一个很基础的优先级队列，它具有可阻塞的读取操作。在下面的示例中， **Prioritized** 对象会被赋予优先级编号。几个 **Producer** 任务的实例会插入 **Prioritized** 对象到 **PriorityBlockingQueue** 中，但插入之间会有随机延时。然后，单个 **Consumer** 任务在执行 `take()` 时会显示多个选项，**PriorityBlockingQueue** 会将当前具有最高优先级的 **Prioritized** 对象提供给它。

在 **Prioritized** 中的静态变量 **counter** 是 **AtomicInteger** 类型。这是必要的，因为有多个 **Producer** 并行运行；如果不是 **AtomicInteger** 类型，你将会看到重复的 **id** 号。 这个问题在 [并发编程](./24-Concurrent-Programming.md) 的 [构造函数非线程安全](./24-Concurrent-Programming.md) 一节中讨论过。

```java
// lowlevel/PriorityBlockingQueueDemo.java
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import onjava.Nap;

class Prioritized implements Comparable<Prioritized>  {
  private static AtomicInteger counter =
    new AtomicInteger();
  private final int id = counter.getAndIncrement();
  private final int priority;
  private static List<Prioritized> sequence =
    new CopyOnWriteArrayList<>();
  Prioritized(int priority) {
    this.priority = priority;
    sequence.add(this);
  }
  @Override
  public int compareTo(Prioritized arg) {
    return priority < arg.priority ? 1 :
      (priority > arg.priority ? -1 : 0);
  }
  @Override
  public String toString() {
    return String.format(
      "[%d] Prioritized %d", priority, id);
  }
  public void displaySequence() {
    int count = 0;
    for(Prioritized pt : sequence) {
      System.out.printf("(%d:%d)", pt.id, pt.priority);
      if(++count % 5 == 0)
        System.out.println();
    }
  }
  public static class EndSentinel extends Prioritized {
    EndSentinel() { super(-1); }
  }
}

class Producer implements Runnable {
  private static AtomicInteger seed =
    new AtomicInteger(47);
  private SplittableRandom rand =
    new SplittableRandom(seed.getAndAdd(10));
  private Queue<Prioritized> queue;
  Producer(Queue<Prioritized> q) {
    queue = q;
  }
  @Override
  public void run() {
    rand.ints(10, 0, 20)
      .mapToObj(Prioritized::new)
      .peek(p -> new Nap(rand.nextDouble() / 10))
      .forEach(p -> queue.add(p));
    queue.add(new Prioritized.EndSentinel());
  }
}

class Consumer implements Runnable {
  private PriorityBlockingQueue<Prioritized> q;
  private SplittableRandom rand =
    new SplittableRandom(47);
  Consumer(PriorityBlockingQueue<Prioritized> q) {
    this.q = q;
  }
  @Override
  public void run() {
    while(true) {
      try {
        Prioritized pt = q.take();
        System.out.println(pt);
        if(pt instanceof Prioritized.EndSentinel) {
          pt.displaySequence();
          break;
        }
        new Nap(rand.nextDouble() / 10);
      } catch(InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}

public class PriorityBlockingQueueDemo {
  public static void main(String[] args) {
    PriorityBlockingQueue<Prioritized> queue =
      new PriorityBlockingQueue<>();
    CompletableFuture.runAsync(new Producer(queue));
    CompletableFuture.runAsync(new Producer(queue));
    CompletableFuture.runAsync(new Producer(queue));
    CompletableFuture.runAsync(new Consumer(queue))
      .join();
  }
}
/* Output:
[15] Prioritized 2
[17] Prioritized 1
[17] Prioritized 5
[16] Prioritized 6
[14] Prioritized 9
[12] Prioritized 0
[11] Prioritized 4
[11] Prioritized 12
[13] Prioritized 13
[12] Prioritized 16
[14] Prioritized 18
[15] Prioritized 23
[18] Prioritized 26
[16] Prioritized 29
[12] Prioritized 17
[11] Prioritized 30
[11] Prioritized 24
[10] Prioritized 15
[10] Prioritized 22
[8] Prioritized 25
[8] Prioritized 11
[8] Prioritized 10
[6] Prioritized 31
[3] Prioritized 7
[2] Prioritized 20
[1] Prioritized 3
[0] Prioritized 19
[0] Prioritized 8
[0] Prioritized 14
[0] Prioritized 21
[-1] Prioritized 28
(0:12)(2:15)(1:17)(3:1)(4:11)
(5:17)(6:16)(7:3)(8:0)(9:14)
(10:8)(11:8)(12:11)(13:13)(14:0)
(15:10)(16:12)(17:12)(18:14)(19:0)
(20:2)(21:0)(22:10)(23:15)(24:11)
(25:8)(26:18)(27:-1)(28:-1)(29:16)
(30:11)(31:6)(32:-1)
*/
```

与前面的示例一样，**Prioritized** 对象的创建顺序在 **sequence** 的 **list** 对象上所记入，以便与实际执行顺序进行比较。 **EndSentinel** 是用于告知 **Consumer** 对象关闭的特殊类型。

**Producer** 使用 **AtomicInteger** 变量为 **SplittableRandom** 设置随机生成种子，以便不同的 **Producer** 生成不同的队列。 这是必需的，因为多个生产者并行创建，如果不是这样，创建过程并不会是线程安全的。

**Producer** 和 **Consumer** 通过 **PriorityBlockingQueue** 相互连接。因为阻塞队列的性质提供了所有必要的同步，因为阻塞队列的性质提供了所有必要的同步，请注意，显式同步是并不需要的 — 从队列中读取数据时，你不用考虑队列中是否有任何元素，因为队列在没有元素时将阻塞读取。

### 无锁集合

[集合](./12-Collections.md) 章节强调集合是基本的编程工具，这也要求包含并发性。因此，早期的集合比如 **Vector** 和 **Hashtable** 有许多使用 **synchronized** 机制的方法。当这些集合不是在多线程应用中使用时，这就导致了不可接收的开销。在 Java 1.2 版本中，新的集合库是非同步的，而给 **Collection** 类赋予了各种 **static** **synchronized** 修饰的方法来同步不同的集合类型。虽然这是一个改进，因为它让你可以选择是否对集合使用同步，但是开销仍然基于同步锁定。 Java 5 版本添加新的集合类型，专门用于增加线程安全性能，使用巧妙的技术来消除锁定。

无锁集合有一个有趣的特性：只要读取者仅能看到已完成修改的结果，对集合的修改就可以同时发生在读取发生时。这是通过一些策略实现的。为了让你了解它们是如何工作的，我们来看看其中的一些。

#### 复制策略

使用“复制”策略，修改是在数据结构一部分的单独副本（或有时是整个数据的副本）上进行的，并且在整个修改过程期间这个副本是不可见的。仅当修改完成时，修改后的结构才与“主”数据结构安全地交换，然后读取者才会看到修改。

在 **CopyOnWriteArrayList** ，写入操作会复制整个底层数组。保留原来的数组，以便在修改复制的数组时可以线程安全地进行读取。当修改完成后，原子操作会将其交换到新数组中，以便新的读取操作能够看到新数组内容。 **CopyOnWriteArrayList** 的其中一个好处是，当多个迭代器遍历和修改列表时，它不会抛出 **ConcurrentModificationException** 异常，因此你不用就像过去必须做的那样，编写特殊的代码来防止此类异常。

**CopyOnWriteArraySet** 使用 **CopyOnWriteArrayList** 来实现其无锁行为。

**ConcurrentHashMap** 和 **ConcurrentLinkedQueue** 使用类似的技术来允许并发读写，但是只复制和修改集合的一部分，而不是整个集合。然而，读取者仍然不会看到任何不完整的修改。**ConcurrentHashMap** **不会抛出concurrentmodificationexception** 异常。

#### 比较并交换 (CAS)

在 比较并交换 (CAS) 中，你从内存中获取一个值，并在计算新值时保留原始值。然后使用 CAS 指令，它将原始值与当前内存中的值进行比较，如果这两个值是相等的，则将内存中的旧值替换为计算新值的结果，所有操作都在一个原子操作中完成。如果原始值比较失败，则不会进行交换，因为这意味着另一个线程同时修改了内存。在这种情况下，你的代码必须再次尝试，获取一个新的原始值并重复该操作。

如果内存仅轻量竞争，CAS操作几乎总是在没有重复尝试的情况下完成，因此它非常快。相反，**synchronized** 操作需要考虑每次获取和释放锁的成本，这要昂贵得多，而且没有额外的好处。随着内存竞争的增加，使用 CAS 的操作会变慢，因为它必须更频繁地重复自己的操作，但这是对更多资源竞争的动态响应。这确实是一种优雅的方法。

最重要的是，许多现代处理器的汇编语言中都有一条 CAS 指令，并且也被 JVM 中的 CAS 操作(例如 **Atomic** 类中的操作)所使用。CAS 指令在硬件层面中是原子性的，并且与你所期望的操作一样快。

<!-- Summary -->
## 本章小结

本附录主要是为了让你在遇到底层并发代码时能对此有一定的了解，尽管本文还远没对这个主题进行全面的讨论。为此，你需要先从阅读由 Brian Goetz, Tim Peierls, Joshua Bloch, Joseph Bowbeer, David Holmes, and Doug Lea (Addison-Wesley 出版社, 2006)所著作的 *Java Concurrency in Practice* （国内译名：Java并发编程实战）开始了解。理想情况下，这本书会完全吓跑你在 Java 中尝试去编写底层并发代码。如果没有，那么你几乎肯定患上了达克效应(DunningKruger Effect)，这是一种认知偏差，“你知道的越少，对自己的能力就越有信心”。请记住，当前的语言设计人员仍然在清理早期语言设计人员过于自信造成的混乱(例如，查看 Thread 类中有多少方法被弃用，而 volatile 直到 Java 5 才正确工作)。

以下是并发编程的步骤:

1. 不要使用它。想一些其他方法来使你写的程序变的更快。
2. 如果你必须使用它，请使用在 [并发编程](./24-Concurrent-Programming.md) - parallel Streams and CompletableFutures 中展示的现代高级工具。
3. 不要在任务间共享变量，在任务之间必须传递的任何信息都应该使用 Java.util.concurrent 库中的并发数据结构。
4. 如果必须在任务之间共享变量，请使用 java.util.concurrent.atomic 里面其中一种类型，或在任何直接或间接访问这些变量的方法上应用 synchronized。 当你不这样做时，很容易被愚弄，以为你已经把所有东西都包括在内。 说真的，尝试使用步骤 3。
5. 如果步骤 4 产生的结果太慢，你可以尝试使用volatile 或其他技术来调整代码，但是如果你正在阅读本书并认为你已经准备好尝试这些方法，那么你就超出了你的深度。 返回步骤＃1。

通常可以只使用 java.util.concurrent 库组件来编写并发程序，完全避免来自应用 volatile 和 synchronized 的挑战。注意，我可以通过 [并发编程](./24-Concurrent-Programming.md)  中的示例来做到这一点。

[^1]: 在某些平台上，特别是 Windows ，默认值可能非常难以查明。你可以使用 -Xss 标志调整堆栈大小。

[^2]: 引自 Brian Goetz, Java Concurrency in Practice 一书的作者 , 该书由 Brian Goetz, Tim Peierls, Joshua Bloch, Joseph Bowbeer, David Holmes, and Doug Lea 联合著作 (Addison-Wesley 出版社, 2006)。↩

[^3]: 请注意，在64位处理器上可能不会发生这种情况，从而消除了这个问题。

[^4]: 这个测试的推论是，“如果某人表示线程是容易并且简单的，请确保这个人没有对你的项目做出重要的决策。如果那个人已经做出，那么你就已经陷入麻烦之中了。”

[^5]: 这在即将产生的 C++ 的标准中得到了补救。

<!-- 分页 -->
<div style="page-break-after: always;"></div>
