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

`InputStream` 表示那些从不同数据源产生输入的类，如[表 I/O-1](#table-io-1) 所示，这些数据源包括：

1. 字节数组；
2. `String` 对象；
3. 文件；
4. “管道”，工作方式与实际生活中的管道类似：从一端输入，从另一端输出；
5. 一个由其它种类的流组成的序列，然后我们可以把它们汇聚成一个流；
6. 其它数据源，如 Internet 连接。

每种数据源都有相应的 `InputStream` 子类。另外，`FilterInputStream` 也属于一种 `InputStream`，它的作用是为“装饰器”类提供基类。其中，“装饰器”类可以把属性或有用的接口与输入流连接在一起，这个我们稍后再讨论。

<span id="table-io-1">**表 I/O-1 `InputStream` 类型**</span>

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

如[表 I/O-2](#table-io-2) 所示，该类别的类决定了输出所要去往的目标：字节数组（但不是 `String`，当然，你也可以用字节数组自己创建）、文件或管道。

另外，`FilterOutputStream` 为“装饰器”类提供了一个基类，“装饰器”类把属性或者有用的接口与输出流连接了起来，这些稍后会讨论。

<span id="table-io-2">**表 I/O-2：`OutputStream` 类型**</span>

| 类 | 功能 | 构造器参数 | 如何使用 |
| :--: | :-- | :-------- | :----- |
| `ByteArrayOutputStream` | 在内存中创建缓冲区。所有送往“流”的数据都要放置在此缓冲区 | 缓冲区初始大小（可选） | 用于指定数据的目的地：将其与 `FilterOutputStream` 对象相连以提供有用接口 |
| `FileOutputStream`      | 用于将信息写入文件 | 字符串，表示文件名、文件或 `FileDescriptor` 对象 | 用于指定数据的目的地：将其与 `FilterOutputStream` 对象相连以提供有用接口 |
| `PipedOutputStream`     | 任何写入其中的信息都会自动作为相关 `PipedInputStream` 的输出。实现“管道化”概念 | `PipedInputStream` | 指定用于多线程的数据的目的地：将其与 `FilterOutputStream` 对象相连以提供有用接口 |
| `FilterOutputStream`    | 抽象类，作为“装饰器”的接口。其中，“装饰器”为其它 `OutputStream` 提供有用功能。见[表 I/O-4](#table-io-4) | 见[表 I/O-4](#table-io-4) | 见[表 I/O-4](#table-io-4) |

<!-- Adding Attributes and Useful Interfaces -->

## 添加属性和有用的接口

装饰器在[泛型](./20-Generics.md)这一章引入。Java I/O 类库需要多种不同功能的组合，这正是使用装饰器模式的原因所在[^1]。而之所以存在 **filter**（过滤器）类，是因为让抽象类 **filter** 作为所有装饰器类的基类。装饰器必须具有和它所装饰对象相同的接口，但它也可以扩展接口，不过这种情况只发生在个别 **filter** 类中。

但是，装饰器模式也有一个缺点：在编写程序的时候，它给我们带来了相当多的灵活性（因为我们可以很容易地对属性进行混搭），但它同时也增加了代码的复杂性。Java I/O 类库操作不便的原因在于：我们必须创建许多类（“核心” I/O 类型加上所有的装饰器）才能得到我们所希望的单个 I/O 对象。

`FilterInputStream` 和 `FilterOutputStream` 是用来提供装饰器类接口以控制特定输入流 `InputStream` 和 输出流 `OutputStream` 的两个类，但它们的名字并不是很直观。`FilterInputStream` 和 `FilterOutputStream` 分别从 I/O 类库中的基类 `InputStream` 和 `OutputStream` 派生而来，这两个类是创建装饰器的必要条件（这样它们才能为所有被装饰的对象提供统一接口）。

### 通过 `FilterInputStream` 从 `InputStream` 读取

`FilterInputStream` 类能够完成两件截然不同的事情。其中，`DataInputStream` 允许我们读取不同的基本数据类型和 `String` 类型的对象（所有方法都以 “read” 开头，例如 `readByte()`、`readFloat()`等等）。搭配其对应的 `DataOutputStream`，我们就可以通过数据“流”将基本数据类型的数据从一个地方迁移到另一个地方。具体是那些“地方”是由[表 I/O-1](#table-io-1) 中的那些类决定的。

其它 `FilterInputStream` 类则在内部修改 `InputStream` 的行为方式：是否缓冲，是否保留它所读过的行（允许我们查询行数或设置行数），以及是否允许把单个字符推回输入流等等。最后两个类看起来就像是为了创建编译器提供的（它们被添加进来可能是为了对“用 Java 构建编译器”实现提供支持），因此我们在一般编程中不会用到它们。

在实际应用中，不管连接的是什么 I/O 设备，我们基本上都会对输入进行缓冲。所以当初 I/O 类库如果能默认都让输入进行缓冲，同时将无缓冲输入作为一种特殊情况（或者只是简单地提供一个方法调用），这样会更加合理，而不是像现在这样迫使我们基本上每次都得手动添加缓冲。
<!-- 译者注：感觉第四版中文版（536页）把上面这一段的意思弄反了 -->

<span id="table-io-3">**表 I/O-3：`FilterInputStream` 类型**</span>

| 类 | 功能 | 构造器参数 | 如何使用 |
| :--: | :-- | :-------- | :----- |
| `DataInputStream` | 与 `DataOutputStream` 搭配使用，按照移植方式从流读取基本数据类型（`int`、`char`、`long` 等） | `InputStream` | 包含用于读取基本数据类型的全部接口 |
| `BufferedInputStream`      | 使用它可以防止每次读取时都得进行实际写操作。代表“使用缓冲区” | `InputStream`，可以指定缓冲区大小（可选） | 本质上不提供接口，只是向进程添加缓冲功能。与接口对象搭配 |
| `LineNumberInputStream`     | 跟踪输入流中的行号，可调用 `getLineNumber()` 和 `setLineNumber(int)` | `InputStream` | 仅增加了行号，因此可能要与接口对象搭配使用 |
| `PushbackInputStream`    | 具有能弹出一个字节的缓冲区，因此可以将读到的最后一个字符回退 | `InputStream` | 通常作为编译器的扫描器，我们可能永远也不会用到 |

### 通过 `FilterOutputStream` 向 `OutputStream` 写入

与 `DataInputStream` 对应的是 `DataOutputStream`，它可以将各种基本数据类型和 `String` 类型的对象格式化输出到“流”中，。这样一来，任何机器上的任何 `DataInputStream` 都可以读出它们。所有方法都以 “write” 开头，例如 `writeByte()`、`writeFloat()` 等等。

`PrintStream` 最初的目的就是为了以可视化格式打印所有基本数据类型和 `String` 类型的对象。这和 `DataOutputStream` 不同，后者的目的是将数据元素置入“流”中，使 `DataInputStream` 能够可移植地重构它们。

`PrintStream` 内有两个重要方法：`print()` 和 `println()`。它们都被重载了，可以打印各种各种数据类型。`print()` 和 `println()` 之间的差异是，后者在操作完毕后会添加一个换行符。

`PrintStream` 可能会造成一些问题，因为它捕获了所有 `IOException`（因此，我们必须使用 `checkError()` 自行测试错误状态，如果出现错误它会返回 `true`）。另外，`PrintStream` 没有处理好国际化问题。这些问题都在 `PrintWriter` 中得到了解决，这在后面会讲到。

`BufferedOutputStream` 是一个修饰符，表明这个“流”使用了缓冲技术，因此每次向流写入的时候，不是每次都会执行物理写操作。我们在进行输出操作的时候可能会经常用到它。

<span id="table-io-4">**表 I/O-4：`FilterOutputStream` 类型**</span>

| 类 | 功能 | 构造器参数 | 如何使用 |
| :--: | :-- | :-------- | :----- |
| `DataOutputStream` | 与 `DataInputStream` 搭配使用，因此可以按照移植方式向流中写入基本数据类型（`int`、`char`、`long` 等） | `OutputStream` | 包含用于写入基本数据类型的全部接口 |
| `PrintStream`      | 用于产生格式化输出。其中 `DataOutputStream` 处理数据的存储，`PrintStream` 处理显示 | `OutputStream`，可以用 `boolean` 值指示是否每次换行时清空缓冲区（可选） | 应该是对 `OutputStream` 对象的 `final` 封装。可能会经常用到它 |
| `BufferedOutputStream`     | 使用它以避免每次发送数据时都进行实际的写操作。代表“使用缓冲区”。可以调用 `flush()` 清空缓冲区 | `OutputStream`，可以指定缓冲区大小（可选） | 本质上并不提供接口，只是向进程添加缓冲功能。与接口对象搭配 |

<!-- Readers & Writers -->

## Reader和Writer

Java 1.1 对基本的 I/O 流类库做了重大的修改。你初次遇到 `Reader` 和 `Writer` 时，可能会以为这两个类是用来替代 `InputStream` 和 `OutputStream` 的，但实际上并不是这样。尽管一些原始的“流”类库已经过时了（如果使用它们，编译器会发出警告），但是 `InputStream` 和 `OutputStream` 在面向字节 I/O 这方面仍然发挥着极其重要的作用，而 `Reader` 和 `Writer` 则提供兼容 Unicode 和面向字符 I/O 的功能。另外：

1. Java 1.1 往 `InputStream` 和 `OutputStream` 的继承体系中又添加了一些新类，所以这两个类显然是不会被取代的；

2. 有时我们必须把来自“字节”层级结构中的类和来自“字符”层次结构中的类结合起来使用。为了达到这个目的，需要用到“适配器（adapter）类”：`InputStreamReader` 可以把 `InputStream` 转换为 `Reader`，而 `OutputStreamWriter` 可以把 `OutputStream` 转换为 `Writer`。

设计 `Reader` 和 `Writer` 继承体系主要是为了国际化。老的 I/O 流继承体系仅支持 8 比特的字节流，并且不能很好地处理 16 比特的 Unicode 字符。由于 Unicode 用于字符国际化（Java 本身的 `char` 也是 16 比特的 Unicode），所以添加 `Reader` 和 `Writer` 继承体系就是为了让所有的 I/O 操作都支持 Unicode。另外，新类库的设计使得它的操作比旧类库要快。

### 数据的来源和去处

几乎所有原始的 Java I/O 流类都有相应的 `Reader` 和 `Writer` 类来提供原生的 Unicode 操作。但是在某些场合，面向字节的 `InputStream` 和 `OutputStream` 才是正确的解决方案。特别是 `java.util.zip` 类库就是面向字节而不是面向字符的。因此，最明智的做法是尽量**尝试**使用 `Reader` 和 `Writer`，一旦代码没法成功编译，你就会发现此时应该使用面向字节的类库了。

下表展示了在两个继承体系中，信息的来源和去处（即数据物理上来自哪里又去向哪里）之间的对应关系：

| 来源与去处：Java 1.0 类 | 相应的 Java 1.1 类 |
| :-------------------: | :--------------: |
| `InputStream`  |  `Reader` <br/> 适配器：`InputStreamReader` |
| `OutputStream`  |  `Writer` <br/> 适配器：`OutputStreamWriter` |
| `FileInputStream`  | `FileReader` |
| `FileOutputStream`  |  `FileWriter` |
| `StringBufferInputStream`（已弃用） | `StringReader` |
| （无相应的类） |  `StringWriter` |
| `ByteArrayInputStream`  |  `CharArrayReader` |
| `ByteArrayOutputStream`  | `CharArrayWriter` |
| `PipedInputStream`  |  `PipedReader` |
| `PipedOutputStream`  | `PipedWriter` |

总的来说，这两个不同的继承体系中的接口即便不能说完全相同，但也是非常相似的。

### 更改流的行为

对于 `InputStream` 和 `OutputStream` 来说，我们会使用 `FilterInputStream` 和 `FilterOutputStream` 的装饰器子类来修改“流”以满足特殊需要。`Reader` 和 `Writer` 的类继承体系沿用了相同的思想——但是并不完全相同。

在下表中，左右之间对应关系的近似程度现比上一个表格更加粗略一些。造成这种差别的原因是类的组织形式不同，`BufferedOutputStream` 是 `FilterOutputStream` 的子类，但 `BufferedWriter` 却不是 `FilterWriter` 的子类（尽管 `FilterWriter` 是抽象类，但却没有任何子类，把它放在表格里只是占个位置，不然你可能奇怪 `FilterWriter` 上哪去了）。然而，这些类的接口却又十分相似。

| 过滤器：Java 1.0 类 | 相应 Java 1.1 类 |
| :---------------  | :-------------- |
| `FilterInputStream` | `FilterReader` |
| `FilterOutputStream` | `FilterWriter` (抽象类，没有子类) |
| `BufferedInputStream` | `BufferedReader`（也有 `readLine()`) |
| `BufferedOutputStream` | `BufferedWriter` |
| `DataInputStream` | 使用 `DataInputStream`（ 如果必须用到 `readLine()`，那你就得使用 `BufferedReader`。否则，一般情况下就用 `DataInputStream` |
| `PrintStream` | `PrintWriter` |
| `LineNumberInputStream` | `LineNumberReader` |
| `StreamTokenizer` | `StreamTokenizer`（使用具有 `Reader` 参数的构造器） |
| `PushbackInputStream` | `PushbackReader` |

有一条限制需要明确：一旦要使用 `readLine()`，我们就不应该用 `DataInputStream`（否则，编译时会得到使用了过时方法的警告），而应该使用 `BufferedReader`。除了这种情况之外的情形中，`DataInputStream` 仍是 I/O 类库的首选成员。

为了使用时更容易过渡到 `PrintWriter`，它提供了一个既能接受 `Writer` 对象又能接受任何 `OutputStream` 对象的构造器。`PrintWriter` 的格式化接口实际上与 `PrintStream` 相同。

Java 5 添加了几种 `PrintWriter` 构造器，以便在将输出写入时简化文件的创建过程，你马上就会见到它们。

其中一种 `PrintWriter` 构造器还有一个执行**自动 flush**[^2] 的选项。如果构造器设置了该选项，就会在每个 `println()` 调用之后，自动执行 flush。

### 未发生改变的类

有一些类在 Java 1.0 和 Java 1.1 之间未做改变。

| 以下这些 Java 1.0 类在 Java 1.1 中没有相应类 |
| --- |
| `DataOutputStream` |
| `File` |
| `RandomAccessFile` |
| `SequenceInputStream` |

特别是 `DataOutputStream`，在使用时没有任何变化；因此如果想以可传输的格式存储和检索数据，请用 `InputStream` 和 `OutputStream` 继承体系。

<!-- Off By Itself: RandomAccessFile -->
## RandomAccessFile类

`RandomAccessFile` 适用于由大小已知的记录组成的文件，所以我们可以使用 `seek()` 将文件指针从一条记录移动到另一条记录，然后对记录进行读取和修改。文件中记录的大小不一定都相同，只要我们能确定那些记录有多大以及它们在文件中的位置即可。

最初，我们可能难以相信 `RandomAccessFile` 不是 `InputStream` 或者 `OutputStream` 继承体系中的一部分。除了实现了 `DataInput` 和 `DataOutput` 接口（`DataInputStream` 和 `DataOutputStream` 也实现了这两个接口）之外，它和这两个继承体系没有任何关系。它甚至都不使用 `InputStream` 和 `OutputStream` 类中已有的任何功能。它是一个完全独立的类，其所有的方法（大多数都是 `native` 方法）都是从头开始编写的。这么做是因为 `RandomAccessFile` 拥有和别的 I/O 类型本质上不同的行为，因为我们可以在一个文件内向前和向后移动。在任何情况下，它都是自我独立的，直接继承自 `Object`。

从本质上来讲，`RandomAccessFile` 的工作方式类似于把 `DataIunputStream` 和 `DataOutputStream` 组合起来使用。另外它还有一些额外的方法，比如使用 `getFilePointer()` 可以得到当前文件指针在文件中的位置，使用 `seek()` 可以移动文件指针，使用 `length()` 可以得到文件的长度。另外，其构造器还需要传入第二个参数（和 C 语言中的 `fopen()` 相同）用来表示我们是准备对文件进行 “随机读”（r）还是“读写”（rw）。它并不支持只写文件，从这点来看，如果当初 `RandomAccessFile` 能设计成继承自 `DataInputStream`，可能也是个不错的实现方式。

在 Java 1.4 中，`RandomAccessFile` 的大多数功能（但不是全部）都被 nio 中的**内存映射文件**（mmap）取代，详见[附录：新 I/O](./Appendix-New-IO.md)。

<!-- Typical Uses of I/O Streams -->

## IO流典型用途

尽管我们可以用不同的方式来组合 I/O 流类，但常用的也就其中几种。你可以下面的例子可以作为 I/O 典型用法的基本参照（在你确定无法使用[文件](./17-Files.md)这一章所述的库之后）。

在这些示例中，异常处理都被简化为将异常传递给控制台，但是这样做只适用于小型的示例和工具。在你自己的代码中，你需要考虑更加复杂的错误处理方式。

### 缓冲输入文件

如果想要打开一个文件进行字符输入，我们可以使用一个 `FileInputReader` 对象，然后传入一个 `String` 或者 `File` 对象作为文件名。为了提高速度，我们希望对那个文件进行缓冲，那么我们可以将所产生的引用传递给一个 `BufferedReader` 构造器。`BufferedReader` 提供了 `line()` 方法，它会产生一个 `Stream<String>` 对象：

```java
// iostreams/BufferedInputFile.java
// {VisuallyInspectOutput}
import java.io.*;
import java.util.stream.*;

public class BufferedInputFile {
    public static String read(String filename) {
        try (BufferedReader in = new BufferedReader(
                new FileReader(filename))) {
            return in.lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.print(
                read("BufferedInputFile.java"));
    }
}
```

`Collectors.joining()` 在其内部使用了一个 `StringBuilder` 来累加其运行结果。该文件会通过 `try-with-resources` 子句自动关闭。

### 从内存输入

下面示例中，从 `BufferedInputFile.read()` 读入的 `String` 被用来创建一个 `StringReader` 对象。然后调用其 `read()` 方法，每次读取一个字符，并把它显示在控制台上：

```java
// iostreams/MemoryInput.java
// {VisuallyInspectOutput}
import java.io.*;

public class MemoryInput {
    public static void
    main(String[] args) throws IOException {
        StringReader in = new StringReader(
                BufferedInputFile.read("MemoryInput.java"));
        int c;
        while ((c = in.read()) != -1)
            System.out.print((char) c);
    }
}
```

注意 `read()` 是以 `int` 形式返回下一个字节，所以必须类型转换为 `char` 才能正确打印。

### 格式化内存输入

要读取格式化数据，我们可以使用 `DataInputStream`，它是一个面向字节的 I/O 类（不是面向字符的）。这样我们就必须使用 `InputStream` 类而不是 `Reader` 类。我们可以使用 `InputStream` 以字节形式读取任何数据（比如一个文件），但这里使用的是字符串。

```java
// iostreams/FormattedMemoryInput.java
// {VisuallyInspectOutput}
import java.io.*;

public class FormattedMemoryInput {
    public static void main(String[] args) {
        try (
            DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(
                    BufferedInputFile.read(
                        "FormattedMemoryInput.java")
                            .getBytes()))
        ) {
            while (true)
                System.out.write((char) in.readByte());
        } catch (EOFException e) {
            System.out.println("\nEnd of stream");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

`ByteArrayInputStream` 必须接收一个字节数组，所以这里我们调用了 `String.getBytes()` 方法。所产生的的 `ByteArrayInputStream` 是一个适合传递给 `DataInputStream` 的 `InputStream`。

如果我们用 `readByte()` 从 `DataInputStream` 一次一个字节地读取字符，那么任何字节的值都是合法结果，因此返回值不能用来检测输入是否结束。取而代之的是，我们可以使用 `available()` 方法得到剩余可用字符的数量。下面例子演示了怎么一次一个字节地读取文件：

```java
// iostreams/TestEOF.java
// Testing for end of file
// {VisuallyInspectOutput}
import java.io.*;

public class TestEOF {
    public static void main(String[] args) {
        try (
            DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                    new FileInputStream("TestEOF.java")))
        ) {
            while (in.available() != 0)
                System.out.write(in.readByte());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

注意，`available()` 的工作方式会随着所读取媒介类型的不同而有所差异，它的字面意思就是“在没有阻塞的情况下所能读取的字节数”。对于文件，能够读取的是整个文件；但是对于其它类型的“流”，可能就不是这样，所以要谨慎使用。

我们也可以通过捕获异常来检测输入的末尾。但是，用异常作为控制流是对异常的一种错误使用方式。

### 基本文件的输出

`FileWriter` 对象用于向文件写入数据。实际使用时，我们通常会用 `BufferedWriter` 将其包装起来以增加缓冲的功能（可以试试移除此包装来感受一下它对性能的影响——缓冲往往能显著地增加 I/O 操作的性能）。在本例中，为了提供格式化功能，它又被装饰成了 `PrintWriter`。按照这种方式创建的数据文件可作为普通文本文件来读取。

```java
// iostreams/BasicFileOutput.java
// {VisuallyInspectOutput}
import java.io.*;

public class BasicFileOutput {
    static String file = "BasicFileOutput.dat";

    public static void main(String[] args) {
        try (
            BufferedReader in = new BufferedReader(
                new StringReader(
                     BufferedInputFile.read(
                         "BasicFileOutput.java")));
                PrintWriter out = new PrintWriter(
                    new BufferedWriter(new FileWriter(file)))
        ) {
            in.lines().forEach(out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Show the stored file:
        System.out.println(BufferedInputFile.read(file));
    }
}
```

`try-with-resources` 语句会自动 flush 并关闭文件。

### 文本文件输出快捷方式

Java 5 在 `PrintWriter` 中添加了一个辅助构造器，有了它，你在创建并写入文件时，就不必每次都手动执行一些装饰的工作。下面的代码使用这种快捷方式重写了 `BasicFileOutput.java`：

```java
// iostreams/FileOutputShortcut.java
// {VisuallyInspectOutput}
import java.io.*;

public class FileOutputShortcut {
    static String file = "FileOutputShortcut.dat";

    public static void main(String[] args) {
        try (
            BufferedReader in = new BufferedReader(
                new StringReader(BufferedInputFile.read(
                    "FileOutputShortcut.java")));
            // Here's the shortcut:
            PrintWriter out = new PrintWriter(file)
        ) {
            in.lines().forEach(out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(BufferedInputFile.read(file));
    }
}
```

使用这种方式仍具备了缓冲的功能，只是现在不必自己手动添加缓冲了。但遗憾的是，其它常见的写入任务都没有快捷方式，因此典型的 I/O 流依旧涉及大量冗余的代码。本书[文件](./17-Files.md)一章中介绍的另一种方式，对此类任务进行了极大的简化。

### 存储和恢复数据

`PrintWriter` 是用来对可读的数据进行格式化。但如果要输出可供另一个“流”恢复的数据，我们可以用 `DataOutputStream` 写入数据，然后用 `DataInputStream` 恢复数据。当然，这些流可能是任何形式，在下面的示例中使用的是一个文件，并且对读写都进行了缓冲。注意 `DataOutputStream` 和 `DataInputStream` 是面向字节的，因此要使用 `InputStream` 和 `OutputStream` 体系的类。

```java
// iostreams/StoringAndRecoveringData.java
import java.io.*;

public class StoringAndRecoveringData {
    public static void main(String[] args) {
        try (
            DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                    new FileOutputStream("Data.txt")))
        ) {
            out.writeDouble(3.14159);
            out.writeUTF("That was pi");
            out.writeDouble(1.41413);
            out.writeUTF("Square root of 2");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (
            DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                    new FileInputStream("Data.txt")))
        ) {
            System.out.println(in.readDouble());
            // Only readUTF() will recover the
            // Java-UTF String properly:
            System.out.println(in.readUTF());
            System.out.println(in.readDouble());
            System.out.println(in.readUTF());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

输出结果：

```
3.14159
That was pi
1.41413
Square root of 2
```

如果我们使用 `DataOutputStream` 进行数据写入，那么 Java 就保证了即便读和写数据的平台多么不同，我们仍可以使用 `DataInputStream` 准确地读取数据。这一点很有价值，众所周知，人们曾把大量精力耗费在数据的平台相关性问题上。但现在，只要两个平台上都有 Java，就不会存在这样的问题[^3]。

当我们使用 `DastaOutputStream` 时，写字符串并且让 `DataInputStream` 能够恢复它的唯一可靠方式就是使用 UTF-8 编码，在这个示例中是用 `writeUTF()` 和 `readUTF()` 来实现的。UTF-8 是一种多字节格式，其编码长度根据实际使用的字符集会有所变化。如果我们使用的只是 ASCII 或者几乎都是 ASCII 字符（只占 7 比特），那么就显得及其浪费空间和带宽，所以 UTF-8 将 ASCII 字符编码成一个字节的形式，而非 ASCII 字符则编码成两到三个字节的形式。另外，字符串的长度保存在 UTF-8 字符串的前两个字节中。但是，`writeUTF()` 和 `readUTF()` 使用的是一种适用于 Java 的 UTF-8 变体（JDK 文档中有这些方法的详尽描述），因此如果我们用一个非 Java 程序读取用 `writeUTF()` 所写的字符串时，必须编写一些特殊的代码才能正确读取。

有了 `writeUTF()` 和 `readUTF()`，我们就可以在 `DataOutputStream` 中把字符串和其它数据类型混合使用。因为字符串完全可以作为 Unicode 格式存储，并且可以很容易地使用 `DataInputStream` 来恢复它。

`writeDouble()` 将 `double` 类型的数字存储在流中，并用相应的 `readDouble()` 恢复它（对于其它的书类型，也有类似的方法用于读写）。但是为了保证所有的读方法都能够正常工作，我们必须知道流中数据项所在的确切位置，因为极有可能将保存的 `double` 数据作为一个简单的字节序列、`char` 或其它类型读入。因此，我们必须：要么为文件中的数据采用固定的格式；要么将额外的信息保存到文件中，通过解析额外信息来确定数据的存放位置。注意，对象序列化和 XML （二者都在[附录：对象序列化](Appendix-Object-Serialization.md)中介绍）是存储和读取复杂数据结构的更简单的方式。

### 读写随机访问文件

使用 `RandomAccessFile` 就像是使用了一个 `DataInputStream` 和 `DataOutputStream` 的结合体（因为它实现了相同的接口：`DataInput` 和 `DataOutput`）。另外，我们还可以使用 `seek()` 方法移动文件指针并修改对应位置的值。

在使用 `RandomAccessFile` 时，你必须清楚文件的结构，否则没法正确使用它。`RandomAccessFile` 有一套专门的方法来读写基本数据类型的数据和 UTF-8 编码的字符串：

```java
// iostreams/UsingRandomAccessFile.java
import java.io.*;

public class UsingRandomAccessFile {
    static String file = "rtest.dat";

    public static void display() {
        try (
            RandomAccessFile rf =
                new RandomAccessFile(file, "r")
        ) {
            for (int i = 0; i < 7; i++)
                System.out.println(
                    "Value " + i + ": " + rf.readDouble());
            System.out.println(rf.readUTF());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try (
            RandomAccessFile rf =
                new RandomAccessFile(file, "rw")
        ) {
            for (int i = 0; i < 7; i++)
                rf.writeDouble(i * 1.414);
            rf.writeUTF("The end of the file");
            rf.close();
            display();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (
            RandomAccessFile rf =
                new RandomAccessFile(file, "rw")
        ) {
            rf.seek(5 * 8);
            rf.writeDouble(47.0001);
            rf.close();
            display();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

输出结果：

```
Value 0: 0.0
Value 1: 1.414
Value 2: 2.828
Value 3: 4.242
Value 4: 5.656
Value 5: 7.069999999999999
Value 6: 8.484
The end of the file
Value 0: 0.0
Value 1: 1.414
Value 2: 2.828
Value 3: 4.242
Value 4: 5.656
Value 5: 47.0001
Value 6: 8.484
The end of the file
```

`display()` 方法打开了一个文件，并以 `double` 值的形式显示了其中的七个元素。在 `main()` 中，首先创建了文件，然后打开并修改了它。因为 `double` 总是 8 字节长，所以如果要用 `seek()` 定位到第 5 个（从 0 开始计数） `double` 值，则要传入的地址值应该为 `5*8`。

正如前面所诉，虽然 `RandomAccess` 实现了 `DataInput` 和 `DataOutput` 接口，但实际上它和 I/O 继承体系中的其它部分是分离的。它不支持装饰，故而不能将其与 `InputStream` 及 `OutputStream` 子类中的任何一个组合起来，所以我们也没法给它添加缓冲的功能。

该类的构造器还有第二个必选参数：我们可以指定让 `RandomAccessFile` 以“只读”（r）方式或“读写”
（rw）方式打开文件。

除此之外，还可以使用 `nio` 中的“内存映射文件”代替 `RandomAccessFile`，这在[附录：新 I/O](Appendix-New-IO.md)中有介绍。

<!-- Summary -->
## 本章小结

Java 的 I/O 流类库的确能够满足我们的基本需求：我们可以通过控制台、文件、内存块，甚至因特网进行读写。通过继承，我们可以创建新类型的输入和输出对象。并且我们甚至可以通过重新定义“流”所接受对象类型的 `toString()` 方法，进行简单的扩展。当我们向一个期望收到字符串的方法传送一个非字符串对象时，会自动调用对象的 `toString()` 方法（这是 Java 中有限的“自动类型转换”功能之一）。

在 I/O 流类库的文档和设计中，仍留有一些没有解决的问题。例如，我们打开一个文件用于输出，如果在我们试图覆盖这个文件时能抛出一个异常，这样会比较好（有的编程系统只有当该文件不存在时，才允许你将其作为输出文件打开）。在 Java 中，我们应该使用一个 `File` 对象来判断文件是否存在，因为如果我们用 `FileOutputStream` 或者 `FileWriter` 打开，那么这个文件肯定会被覆盖。

I/O 流类库让我们喜忧参半。它确实挺有用的，而且还具有可移植性。但是如果我们没有理解“装饰器”模式，那么这种设计就会显得不是很直观。所以，它的学习成本相对较高。而且它并不完善，比如说在过去，我不得不编写相当数量的代码去实现一个读取文本文件的工具——所幸的是，Java 7 中的 nio 消除了此类需求。

一旦你理解了装饰器模式，并且开始在某些需要这种灵活性的场景中使用该类库，那么你就开始能从这种设计中受益了。到那时候，为此额外多写几行代码的开销应该不至于让人觉得太麻烦。但还是请务必检查一下，确保使用[文件](./17-Files.md)一章中的库和技术没法解决问题后，再考虑使用本章的 I/O 流库。

[^1]: 很难说这就是一个很好的设计选择，尤其是与其它编程语言中简单的 I/O 类库相比较。但它确实是如此选择的一个正当理由。

[^2]: 译者注：“flush” 直译是“清空”，意思是把缓冲中的数据清空，输送到对应的目的地（如文件和屏幕）。

[^3]: XML 是另一种方式，可以解决在不同计算平台之间移动数据，而不依赖于所有平台上都有 Java 这一问题。XML 将在[附录：对象序列化](./Appendix-Object-Serialization.md)一章中进行介绍。

<!-- 分页 -->

<div style="page-break-after: always;"></div>
