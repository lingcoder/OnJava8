[TOC]

<!-- Appendix: I/O Streams -->
# 附录:流式IO

> Java 7 引入了一种简单明了的方式来读写文件和操作目录。大多情况下，[文件](./17-Files.md)这一章所介绍的那些库和技术就足够你用了。但是，如果你必须面对一些特殊的需求和比较底层的操作，或者处理一些老版本的代码，那么你就必须了解本附录中的内容。

对于编程语言的设计者来说，实现良好的输入/输出（I/O）系统是一项比较艰难的任务，不同实现方案的数量就可以证明这点。其中的挑战似乎在于要涵盖所有的可能性，你不仅要覆盖到不同的 I/O 源和 I/O 接收器（如文件、控制台、网络连接等），还要实现多种与它们进行通信的方式（如顺序、随机访问、缓冲、二进制、字符、按行和按字等）。

Java 类库的设计者通过创建大量的类来解决这一难题。一开始，你可能会对 Java I/O 系统提供了如此多的类而感到不知所措。Java 1.0 之后，Java 的 I/O 类库发生了明显的改变，在原来面向字节的类中添加了面向字符和基于 Unicode 的类。在 Java 1.4 中，为了改进性能和功能，又添加了 `nio` 类（全称是 “new I/O”，Java 1.4 引入，到现在已经很多年了）。这部分在[附录：新 I/O](./Appendix-New-IO.md) 中介绍。

因此，要想充分理解 Java I/O 系统以便正确运用它，我们需要学习一定数量的类。另外，理解 I/O 类库的演化过程也很有必要，因为如果缺乏历史的眼光，很快我们就会对什么时候该使用哪些类，以及什么时候不该使用它们而感到困惑。

编程语言的 I/O 类库经常使用**流**这个抽象概念，它将所有数据源或者数据接收器表示为能够产生或者接收数据片的对象。

> **注意**：Java 8 函数式编程中的 `Stream` 类和这里的 I/O stream 没有任何关系。这又是另一个例子，如果再给设计者一次重来的机会，他们将使用不同的术语。

I/O 流屏蔽了实际的 I/O 设备中处理数据的细节：

1. 字节流对应原生的二进制数据；
2. 字符流对应字符数据，它会自动处理与本地字符集之间的转换；
3. 缓冲流可以提高性能，通过减少底层 API 的调用次数来优化 I/O。

从 JDK 文档的类层次结构中可以看到，Java 类库中的 I/O 类分成了输入和输出两部分。在设计 Java 1.0 时，类库的设计者们就决定让所有与输入有关系的类都继承自 `InputStream`，所有与输出有关系的类都继承自 `OutputStream`。所有从 `InputStream` 或 `Reader` 派生而来的类都含有名为 `read()` 的基本方法，用于读取单个字节或者字节数组。同样，所有从 `OutputStream` 或 `Writer` 派生而来的类都含有名为 `write()` 的基本方法，用于写单个字节或者字节数组。但是，我们通常不会用到这些方法，它们之所以存在是因为别的类可以使用它们，以便提供更有用的接口。

我们很少使用单一的类来创建流对象，而是通过叠合多个对象来提供所期望的功能（这是**装饰器设计模式**）。为了创建一个流，你却要创建多个对象，这也是 Java I/O 类库让人困惑的主要原因。

这里我只会提供这些类的概述，并假定你会使用 JDK 文档来获取它们的详细信息（比如某个类的所以方法的详细列表）。

<!-- Types of InputStream -->
## 输入流类型

`InputStream` 表示那些从不同数据源产生输入的类，这些数据源包括：

1. 字节数组；
2. `String` 对象；
3. 文件；
4. “管道”，工作方式与实际生活中的管道类似：从一端输入，从另一端输出；
5. 一个由其它种类的流组成的序列，然后我们可以把它们汇聚成一个流；
6. 其它数据源，如 `Internet` 连接。

每种数据源都有相应的 `InputStream` 子类。另外，`FilterInputStream` 也属于一种 `InputStream`，它的作用是为“装饰器”类提供基类。其中，“装饰器”类可以把属性或有用的接口与输入流连接在一起，这个我们稍后在讨论。

<span id="table-io-1" align="center">**表 I/O-1：`InputStream` 类型**</span>

|  类  | 功能 | 构造器参数 | 如何使用 |
| :--: | :-- | :-------- | :----- |
| `ByteArrayInputStream`    | 允许将内存的缓冲区当做 `InputStream` 使用 | 缓冲区，字节将从中取出 | 作为一种数据源：将其与 `FilterInputStream` 对象相连以提供有用接口 |
| `StringBufferInputStream` | 将 `String` 转换成 `InputStream` | 字符串。底层实现实际使用 `StringBuffer` | 作为一种数据源：将其与 `FilterInputStream` 对象相连以提供有用接口 |
| `FileInputStream`         | 用于从文件中读取信息 | 字符串，表示文件名、文件或 `FileDescriptor` 对象 | 作为一种数据源：将其与 `FilterInputStream` 对象相连以提供有用接口 |
| `PipedInputStream`        | 产生用于写入相关 `PipedOutputStream` 的数据。实现“管道化”概念 | `PipedOutputSteam`           | 作为多线程中的数据源：将其与 `FilterInputStream` 对象相连以提供有用接口 |
| `SequenceInputStream`     | 将两个或多个 `InputStream` 对象转换成一个 `InputStream` | 两个 `InputStream` 对象或一个容纳 `InputStream` 对象的容器 `Enumeration` | 作为一种数据源：将其与 `FilterInputStream` 对象相连以提供有用接口          |
| `FilterInputStream`       | 抽象类，作为“装饰器”的接口。其中，“装饰器”为其它的 `InputStream` 类提供有用的功能。见[表 I/O-3](#table-io-3) | 见[表 I/O-3](#table-io-3) | 见[表 I/O-3](#table-io-3) |

<!-- Types of OutputStream -->
## 输出流类型

<span id="table-io-2" align="center">**表 I/O-2：`OutputStream` 类型**</span>

| 类 | 功能 | 构造器参数 | 如何使用 |
| :--: | :-- | :-------- | :----- |
| `ByteArrayOutputStream` | 在内存中创建缓冲区。所有送往“流”的数据都要放置在此缓冲区 | 缓冲区初始大小（可选） | 用于指定数据的目的地：将其与 `FilterOutputStream` 对象相连以提供有用接口 |
| `FileOutputStream`      | 用于将信息接入文件 | 字符串，表示文件名、文件或 `FileDescriptor` 对象 | 用于指定数据的目的地：将其与 `FilterOutputStream` 对象相连以提供有用接口 |
| `PipedOutputStream`     | 任何写入其中的信息都会自动作为相关 `PipedInputStream` 的输出。实现“管道化”概念 | `PipedInputStream` | 指定用于多线程的数据的目的地：将其与 `FilterOutputStream` 对象相连以提供有用接口 |
| `FilterOutputStream`    | 抽象类，作为“装饰器”的接口。其中，“装饰器”为其它 `OutputStream` 提供有用功能。见[表 I/O-4](#table-io-4) | 见[表 I/O-4](#table-io-4) | 见[表 I/O-4](#table-io-4) |

<!-- Adding Attributes and Useful Interfaces -->

## 添加属性和有用的接口


### 通过 `FilterInputStream` 从 `InputStream` 读取

<span id="table-io-3" align="center">**表 I/O-3：`FilterInputStream` 类型**</span>

| 类 | 功能 | 构造器参数 | 如何使用 |
| :--: | :-- | :-------- | :----- |
| `DataInputStream` |  |  |  |
| `BufferedInputStream`      |  |  |  |
| `LineNumberInputStream`     |  |  |  |
| `PushbackInputStream`    |  |  |  |

### 通过 `FilterOutputStream` 向 `OutputStream` 写入

<span id="table-io-4" align="center">**表 I/O-4：`FilterOutputStream` 类型**</span>

| 类 | 功能 | 构造器参数 | 如何使用 |
| :--: | :-- | :-------- | :----- |
| `DataOutputStream` |  |  |  |
| `PrintStream`      |  |  |  |
| `BufferedOutputStream`     |  |  |  |

<!-- Readers & Writers -->
## Reader和Writer



<!-- Off By Itself: RandomAccessFile -->
## RandomAccessFile类


<!-- Typical Uses of I/O Streams -->
## IO流典型用途


<!-- Summary -->
## 本章小结


<!-- 分页 -->

<div style="page-break-after: always;"></div>
[^表 IO-3]: 