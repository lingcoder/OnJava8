[TOC]

<!-- Appendix: Standard I/O -->
# 附录:标准IO

>*标准 I/O*这个术语参考Unix中的概念，指程序所使用的单一信息流（这种思想在大多数操作系统中，也有相似形式的实现）。

程序的所有输入都可以来自于*标准输入*，其所有输出都可以流向*标准输出*，并且其所有错误信息均可以发送到*标准错误*。*标准 I/O* 的意义在于程序之间可以很容易地连接起来，一个程序的标准输出可以作为另一个程序的标准输入。这是一个非常强大的工具。

## 从标准输入中读取

遵循标准 I/O 模型，Java 提供了标准输入流 `System.in`、标准输出流 `System.out` 和标准错误流 `System.err`。在本书中，你已经了解到如何使用 `System.out`将数据写到标准输出。 `System.out` 已经预先包装[^1]成了 `PrintStream` 对象。标准错误流 `System.err` 也预先包装为 `PrintStream` 对象，但是标准输入流 `System.in` 是原生的没有经过包装的 `InputStream`。这意味着尽管可以直接使用标准输出流 `System.in` 和标准错误流 `System.err`，但是在读取 `System.in` 之前必须先对其进行包装。

我们通常一次一行地读取输入。为了实现这个功能，将 `System.in` 包装成 `BufferedReader` 来使用，这要求我们用 `InputStreamReader` 把 `System.in` 转换[^2]成 `Reader` 。下面这个例子将键入的每一行显示出来：

```java
// standardio/Echo.java
// How to read from standard input
import java.io.*;
import onjava.TimedAbort;

public class Echo {
    public static void main(String[] args) {
        TimedAbort abort = new TimedAbort(2);
        new BufferedReader(
                new InputStreamReader(System.in))
                .lines()
                .peek(ln -> abort.restart())
                .forEach(System.out::println);
        // Ctrl-Z or two seconds inactivity
        // terminates the program
    }
}
```

`BufferedReader` 提供了 `lines()` 方法，返回类型是 `Stream<String>` 。这显示出流模型的的灵活性：仅使用标准输入就能很好地工作。 `peek()` 方法重启 `TimeAbort`，只要保证至少每隔两秒有输入就能够使程序保持开启状态。

## 将`System.out` 转换成 `PrintWriter`

`System.out` 是一个 `PrintStream`，而 `PrintStream` 是一个`OutputStream`。 `PrintWriter` 有一个把 `OutputStream` 作为参数的构造器。因此，如果你需要的话，可以使用这个构造器把 `System.out` 转换成 `PrintWriter` 。

```java
// standardio/ChangeSystemOut.java
// Turn System.out into a PrintWriter

import java.io.*;

public class ChangeSystemOut {
    public static void main(String[] args) {
        PrintWriter out =
                new PrintWriter(System.out, true);
        out.println("Hello, world");
    }
}
```

输出结果：

```
Hello, world
```

要使用 `PrintWriter` 带有两个参数的构造器，并设置第二个参数为 `true`，从而使能自动刷新到输出缓冲区的功能；否则，可能无法看到打印输出。

## 重定向标准 I/O

Java的 `System` 类提供了简单的 `static` 方法调用，从而能够重定向标准输入流、标准输出流和标准错误流：
- setIn（InputStream）
- setOut（PrintStream）
- setErr(PrintStream)

如果我们突然需要在显示器上创建大量的输出，而这些输出滚动的速度太快以至于无法阅读时，重定向输出就显得格外有用，可把输出内容重定向到文件中供后续查看。对于我们想重复测试特定的用户输入序列的命令行程序来说，重定向输入就很有价值。下例简单演示了这些方法的使用：

```java
// standardio/Redirecting.java
// Demonstrates standard I/O redirection
import java.io.*;

public class Redirecting {
    public static void main(String[] args) {
        PrintStream console = System.out;
        try (
                BufferedInputStream in = new BufferedInputStream(
                        new FileInputStream("Redirecting.java"));
                PrintStream out = new PrintStream(
                        new BufferedOutputStream(
                                new FileOutputStream("Redirecting.txt")))
        ) {
            System.setIn(in);
            System.setOut(out);
            System.setErr(out);
            new BufferedReader(
                    new InputStreamReader(System.in))
                    .lines()
                    .forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(console);
        }
    }
}
```

该程序将文件中内容载入到标准输入，并把标准输出和标准错误重定向到另一个文件。它在程序的开始保存了最初对 `System.out` 对象的引用，并且在程序结束时将系统输出恢复到了该对象上。

I/O重定向操作的是字节流而不是字符流，因此使用 `InputStream` 和 `OutputStream`，而不是 `Reader` 和 `Writer`。

<!-- Process Control -->
## 执行控制

你经常需要在Java内部直接执行操作系统的程序，并控制这些程序的输入输出，Java类库提供了执行这些操作的类。

一项常见的任务是运行程序并将输出结果发送到控制台。本节包含了一个可以简化此任务的实用工具。

在使用这个工具时可能会产生两种类型的错误：导致异常的普通错误——对于这些错误我们只需要重新抛出一个 `RuntimeException` 即可，以及进程自身的执行过程中导致的错误——我们需要用单独的异常来报告这些错误：

```java
// onjava/OSExecuteException.java
package onjava;

public class OSExecuteException extends RuntimeException {
    public OSExecuteException(String why) {
        super(why);
    }
}
```

为了运行程序，我们需要传递给 `OSExecute.command()` 一个 `String command`，我们可以在控制台键入同样的指令运行程序。该命令传递给 `java.lang.ProcessBuilder` 的构造器（需要将其作为 `String` 对象的序列），然后启动生成的 `ProcessBuilder` 对象。

```java
// onjava/OSExecute.java
// Run an operating system command
// and send the output to the console
package onjava;
import java.io.*;

public class OSExecute {
    public static void command(String command) {
        boolean err = false;
        try {
            Process process = new ProcessBuilder(
                    command.split(" ")).start();
            try (
                    BufferedReader results = new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream()));
                    BufferedReader errors = new BufferedReader(
                            new InputStreamReader(
                                    process.getErrorStream()))
            ) {
                results.lines()
                        .forEach(System.out::println);
                err = errors.lines()
                        .peek(System.err::println)
                        .count() > 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (err)
            throw new OSExecuteException(
                    "Errors executing " + command);
    }
}
```

为了捕获在程序执行时产生的标准输出流，我们可以调用 `getInputStream()`。这是因为 `InputStream` 是我们可以从中读取信息的流。

这里这些行只是被打印了出来，但是你也可以从 `command()` 捕获和返回它们。

该程序的错误被发送到了标准错误流，可以调用 `getErrorStream()` 捕获。如果存在任何错误，它们都会被打印并且抛出 `OSExcuteException` ，以便调用程序处理这个问题。

下面是展示如何使用 `OSExecute` 的示例：

```java
// standardio/OSExecuteDemo.java
// Demonstrates standard I/O redirection
// {javap -cp build/classes/main OSExecuteDemo}
import onjava.*;

public class OSExecuteDemo {}
```

这里使用 `javap` 反编译器（随JDK发布）来反编译程序，编译结果：

```
Compiled from "OSExecuteDemo.java"
public class OSExecuteDemo {
  public OSExecuteDemo();
}
```

[^1]: 译者注：这里用到了**装饰器模式**。

[^2]: 译者注：这里用到了**适配器模式**。

<!-- 分页 -->

<div style="page-break-after: always;"></div>