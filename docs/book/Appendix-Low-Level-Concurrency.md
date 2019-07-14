[TOC]

<!-- Appendix: Low-Level Concurrency -->
# 附录:并发底层原理

> 尽管不建议您自己编写底层 Java 并发代码，但是这样通常有助于了解它是如何工作的。

[并发编程](./24-Concurrent-Programming.md) 章节中介绍了一些用于高级并发的概念，包括为 Java 并发编程而最新提出的,更安全的概念（ parallel Streams 和 CompletableFutures  ）。本附录则介绍在 Java 中底层并发概念，因此在阅读本篇时，您能有所了解掌握这些代码。您还会将进一步了解并发的普遍问题。

在 Java 的早期版本中, 底层并发概念是并发编程的重要组成部分。我们会着眼于围绕这些技巧的复杂性以及为何您应该避免它们而谈。 “并发编程” 章节展示最新的 Java 版本(尤其是 Java 8)所提供的改进技巧，这些技巧使得并发的使用，如果本来不容易使用，也会更容易些。



<!-- What is a Thread? -->

## 线程

并发将程序划分成分离的，独立运行的任务。每个任务都由一个 *执行线程* 来驱动，我们通常将其简称为 *线程* 。而一个 *线程* 就是操作系统进程中单一顺序的控制流。因此，单个进程可以有多个并发执行的任务，但是你的程序使得每个任务都好像有自己的处理器一样。这线程模型为编程带来了便利，它简化了在单一程序中处理变戏法般的多任务过程。操作系统则从处理器上分配时间到您程序的所有线程中。

Java 并发的核心机制是 **Thread** 类，在该语言最初版本中， **Thread （线程）** 是由程序员直接创建和管理的。随着语言的发展以及人们发现了更好的一些方法，中间层机制 - 特别是 **Executor** 框架  - 被添加进来，以消除自己管理线程时候的心理负担（及错误）。 最终，甚至发展出比 **Executor** 更好的机制，如 [并发编程](./24-Concurrent-Programming.md) 一章所示。

**Thread（线程）** 是将任务关联到处理器的软件概念。虽然创建和使用 **Thread**  类看起来与任何其他类都很相似，但实际上它们是非常不同的。当你创建一个 **Thread** 时，JVM 将分配一大块内存到专为线程保留的特殊区域上，用于提供运行任务时所需的一切，包括：

* 程序计数器，指明要执行的下一个 JVM 字节码指令。
* 用于支持 Java 代码执行的栈，包含有关此线程已到达当时执行位置所调用方法的信息。它也包含每个正在执行的方法的所有局部变量(包括原语和堆对象的引用)。每个线程的栈通常在 64K 到 1M 之间 [^1] 。
* 第二个则用于 native code（本机方法代码）执行的栈
* *thread-local variables* （线程本地变量）的存储区域
* 用于控制线程的状态管理变量

包括 `main()` 在内的所有代码都会在某个线程内运行。 每当调用一个方法时，当前程序计数器被推到该线程的栈上，然后栈指针向下移动以足够来创建一个栈帧，其栈帧里存储该方法的所有局部变量，参数和返回值。所有基本类型变量都直接在栈上，虽然方法中创建（或方法中使用）对象的任何引用都位于栈帧中，但对象本身存于堆中。这仅且只有一个堆，被程序中所有线程所共享。

除此以外，线程必须绑定到操作系统，这样它就可以在某个时候连接到处理器。这是作为线程构建过程的一部分为您管理的。Java 使用底层操作系统中的机制来管理线程的执行。

### 最佳线程数

如果你查看第 24 章 [并发编程](./24-Concurrent-Programming.md) 中使用 *CachedThreadPool* 的用例，你会发现 **ExecutorService** 为每个我们提交的任务分配一个线程。然而，并行流（**parallel Stream**）在 [**CountingStream.java** ](https://github.com/BruceEckel/OnJava8-Examples/blob/master/concurrent/CountingStream.java
) 中只分配了 8 个线程（id 中 1-7 为工作线程，8 为  **`main()`** 方法的主线程，它巧妙地将其用作额外的并行流）。如果你尝试提高 **range()** 方法中的上限值，你会看到没有创建额外的线程。这是为什么？

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

在我的机器上（使用英特尔酷睿i7），我有四个内核，每个内核呈现两个*超线程*（指一种硬件技巧，能在单个处理器上产生非常快速的上下文切换，，在某些情况下可以使内核看起来像运行两个硬件线程）。虽然这是 “最近” 计算机上的常见配置(在撰写本文时)，但你可能会看到不同的结果，包括 `CountingStream.java` 中同等数量的默认线程。

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

只要你不断递交任务，`CachedThreadPool` 就会继续创建线程。将 `Dummy` 对象递交到 `execute()` 方法以开始任务，如果线程池无可用线程，则分配一个新线程。执行的暂停方法 `pause()` 运行时间必须足够长，使任务不会开始即完成(从而为新任务释放现有线程)。只要任务不断进入而没有完成，`CachedThreadPool` 最终就会耗尽内存。

我并不总是能够在我尝试的每台机器上造成内存不足的错误。在一台机器上，我看到这样的结果:

```shell
> java ThreadSize
OutOfMemoryError: 2816
```

我们可以使用 `-Xss` 标记减少每个线程栈分配的内存大小。允许的最小线程栈大小是 64k:

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

你还可以使用 `-Xmx` 标志增加 JVM 的最大内存分配:

```shell
>java -Xss64K -Xmx5M ThreadSize
OutOfMemoryError: 5703
```

请注意的是操作系统还可能对允许的线程数施加限制。

因此，“我可以拥有多少线程”这一问题的答案是“几千个”。但是，如果你发现自己分配了数千个线程，那么您可能需要重新考虑您的做法; 恰当的问题是“我需要多少线程？”

### The WorkStealingPool

这是一个 `ExecutorService` ，它使用所有可用的(由JVM报告) 处理器自动创建线程池。

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

<!-- Sharing Resources -->

## 资源共享


<!-- The volatile Keyword -->
## volatile关键字


<!-- Atomicity -->
## 原子性


<!-- Critical Sections -->
## 关键部分


<!-- Library Components -->
## 库组件


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

[^1]: 在某些平台上，特别是 Windows，默认值可能非常难以查明。您可以使用 -Xss 标志调整堆栈大小。

[^2]: 出自 Brian Goetz, Java Concurrency in Practice 一书的作者 , 该书由 Brian Goetz, Tim Peierls, Joshua Bloch, Joseph Bowbeer, David Holmes, and Doug Lea 联合著作 (Addison-Wesley 出版社, 2006)。↩

[^3]: 请注意，在64位处理器上可能不会发生这种情况，从而消除了这个问题。

[^4]: 这个测试的一个推论是，“如果有人暗示线程是直接的，请确保这个人没有对您的项目做出重要的决策。如果那个人已经做出，那么你就有麻烦了。”

[^5]: 这版本是我参与的；这可能在以后的标准中得到了修正


<!-- 分页 -->

<div style="page-break-after: always;"></div>
