# 附录:新IO

Java “新” I/O 库，是在Java 1.4引入到 **Java .nio.* package** 中。它有一个目标 : 速度。

实际上，“旧的”I/O包是使用 **nio** 重新实现了，因此速度也得到提升，因此即使不显式地使用nio 编写代码，也可以提速。这种速度的提高既发生在文件 I/O 中(这里讨论)，也发生在网络I/O 中(例如用于Internet编程)。

速度来自于使用更接近操作系统执行 I/O 方式的结构 : 通道和缓冲区。把它想象成一个煤矿;通道是包含煤层的矿井(数据)，缓冲区是您发送到矿井的小车。车里装满了煤，你从车上拿煤。也就是说，你不能直接和通道互动 ; 您与缓冲区交互并将缓冲区发送到通道中。通道要么从缓冲区中提取数据，要么将数据放入缓冲区。

本附录将深入探讨 nio 包。像I/O streams 这样的高级库使用 **nio**，但是大多数时候您不需要在这个级别使用 I/O 。在Java 7和Java 8，您(理想情况下)除了特殊情况外，甚至不需要使用I/O流。理想情况下，您将经常使用的所有内容都包含在文件一章中。只有在处理性能问题 (例如，可能需要内存映射文件)或创建自己的 I/O 库时，才需要理解 **nio**。

## ByteBuffers

直接与通道通信的缓冲区只有一个类型，就是 bytebuffer 。也就是说，一个保存原始字节的缓冲区。如果您查看 **java.nio.ByteBuffer** 的JDK文档，你会发现它很基本 : 您可以通过告诉它要分配多少存储空间来创建一个方法，并且可以使用一些方法来放置和获取数据，这些方法可以是原始字节形式的数据，也可以是原始数据类型的数据。但是没有办法放置或获取对象，甚至字符串。它是相当低层次的，因为这使得大多数操作系统的映射更加有效。

“旧的” I/O 中的三个类被修改，可以生成一个 **FileChannel** 分别是: **FileInputStream**、**FileOutputStream**，以及用于读写的 **RandomAccessFile**。

注意，这些是字节操作流，符合 **nio** 的底层特性。 字符模式类的 **Reader** 和 **Writer** 不生成通道，但通道类具有从通道生成 **Reader** 和 **Writer** 的实用方法。

在这里，我们使用所有三种类型的流来生成可写、可读/可写和可读的通道:

```java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Getting channels from streams
import java.nio.*;
import java.nio.channels.*;
import java.io.*;

public class GetChannel {
  private static String name = "data.txt";
  private static final int BSIZE = 1024;
  public static void main(String[] args) {
    // Write a file:
    try(
      FileChannel fc = new FileOutputStream(name)
        .getChannel()
    ) {
      fc.write(ByteBuffer
        .wrap("Some text ".getBytes()));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Add to the end of the file:
    try(
      FileChannel fc = new RandomAccessFile(
        name, "rw").getChannel()
    ) {
      fc.position(fc.size()); // Move to the end
      fc.write(ByteBuffer
        .wrap("Some more".getBytes()));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Read the file:
    try(
      FileChannel fc = new FileInputStream(name)
        .getChannel()
    ) {
      ByteBuffer buff = ByteBuffer.allocate(BSIZE);
      fc.read(buff);
      buff.flip();
      while(buff.hasRemaining())
        System.out.write(buff.get());
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    System.out.flush();
  }
}
/* Output:
Some text Some more
*/
```

对于这里显示的任何流类，**getChannel( )** 将生成一个 **FileChannel** 。通道是相当基本的:给它一个**ByteBuffer** 用于读写，并为独占访问锁定文件的区域(稍后将对此进行描述)。

将字节放入 **ByteBuffer** 的一种方法是直接使用 “put” 方法，将一个或多个字节填入 ByteBuffer，当然也可以是其它原始类型。但是，正如这里所看到的，您还可以使用 **wrap()** 方法在 **ByteBuffer**中“包装”现有字节数组。执行此操作时，不会复制底层数组，而是将其用作生成的 **ByteBuffer** 的存储。这样生产的 **ByteBuffer** 是由数组“支持”的。

data.txt 文件使用RandomAccessFile重新打开。注意，您可以在文件中移动 **FileChanne** l; 在这里，它被移动到末尾，以便添加额外的写操作。

对于只读访问，必须使用 **static allocate()** 方法显式地分配 **ByteBuffer**。**nio** 的目标是快速移动大量数据，因此 **ByteBuffer** 的大小应该很重要——实际上，这里使用的1K可能比您通常使用的要小很多(您必须对工作应用程序进行试验，以找到最佳大小)。

还可以通过使用 **allocateDirect()** 而不是 **allocation()** 来生成一个“直接”缓冲区来获得更快的速度，该缓冲区可以与操作系统进行更高的耦合。然而，这种分配的开销更大，而且实际实现因操作系统的不同而有所不同，因此，您必须再次试验您的工作应用程序，以发现直接缓冲区是否会为您带来速度上的优势。

一旦您调用 **read()** 来告诉 **FileChannel** 将字节存储到 **ByteBuffer** 中，您必须调用缓冲区上的 **flip()**来告诉它准备好提取字节(是的，这看起来有点粗糙，但是请记住，这是非常低级的，而且是为了达到最高速度)。如果我们要为进一步的 **read()** 操作使用缓冲区，我们还将调用 **clear()** 为每个 **read()** 准备缓冲区。这个简单的文件复制程序演示:

```java
// newio/ChannelCopy.java

// Copying a file using channels and buffers
// {java ChannelCopy ChannelCopy.java test.txt}
import java.nio.*;
import java.nio.channels.*;
import java.io.*;

public class ChannelCopy {
  private static final int BSIZE = 1024;
  public static void main(String[] args) {
    if(args.length != 2) {
      System.out.println(
        "arguments: sourcefile destfile");
      System.exit(1);
    }
    try(
      FileChannel in = new FileInputStream(
        args[0]).getChannel();
      FileChannel out = new FileOutputStream(
        args[1]).getChannel()
    ) {
      ByteBuffer buffer = ByteBuffer.allocate(BSIZE);
      while(in.read(buffer) != -1) {
        buffer.flip(); // Prepare for writing
        out.write(buffer);
        buffer.clear();  // Prepare for reading
      }
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}

```

打开一个 **FileChannel** 用于读取，另一个 **FileChannel** 用于写入。分配了一个 **ByteBuffer**，当**FileChannel.read() **返回 **-1** 时(毫无疑问，这是Unix和C语言中的一个剩余物 (holdover) )，意味着您已经完成了输入。每次 **read()** 将数据放入缓冲区之后，**flip()** 都会准备好缓冲区，以便 **write()** 提取它的信息。写()之后，信息仍然在缓冲区中，**clear()** 重置所有内部指针，以便在另一次 **read()** 期间接受数据。 

但是，前面的程序并不是处理这种操作的理想方法。特殊方法 transferTo() 和 transferFrom( )允许你直接连接一个通道到另一个:

```java
// newio/TransferTo.java

// Using transferTo() between channels
// {java TransferTo TransferTo.java TransferTo.txt}
import java.nio.channels.*;
import java.io.*;

public class TransferTo {
  public static void main(String[] args) {
    if(args.length != 2) {
      System.out.println(
        "arguments: sourcefile destfile");
      System.exit(1);
    }
    try(
      FileChannel in = new FileInputStream(
        args[0]).getChannel();
      FileChannel out = new FileOutputStream(
        args[1]).getChannel()
    ) {
      in.transferTo(0, in.size(), out);
      // Or:
      // out.transferFrom(in, 0, in.size());
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
}
```

你不会经常这样做，但知道这一点很好。

<!-- Converting Data -->

## 转换数据

在 **GetChannel** 中打印文件中的信息。在 java 中，我们每次提取一个字节的数据，并将每个字节转换为 char。这看起来很简单——如果您查看**java.nio.CharBuffer** 类，您将看到它有一个toString()方法，该方法说，“返回一个包含这个缓冲区中的字符的字符串。”

既然 **ByteBuffer** 可以用 **asCharBuffer()** 方法看作 **CharBuffer**，为什么不使用它呢? 从下面输出语句的第一行可以看出，这并不正确:

```java
// newio/BufferToText.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Converting text to and from ByteBuffers
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.io.*;

public class BufferToText {
  private static final int BSIZE = 1024;
  public static void main(String[] args) {
    try(
      FileChannel fc = new FileOutputStream(
        "data2.txt").getChannel()
    ) {
      fc.write(ByteBuffer.wrap("Some text".getBytes()));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    ByteBuffer buff = ByteBuffer.allocate(BSIZE);
    try(
      FileChannel fc = new FileInputStream(
        "data2.txt").getChannel()
    ) {
      fc.read(buff);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    buff.flip();
    // Doesn't work:
    System.out.println(buff.asCharBuffer());
    // Decode using this system's default Charset:
    buff.rewind();
    String encoding =
      System.getProperty("file.encoding");
    System.out.println("Decoded using " +
      encoding + ": "
      + Charset.forName(encoding).decode(buff));
    // Encode with something that prints:
    try(
      FileChannel fc = new FileOutputStream(
        "data2.txt").getChannel()
    ) {
      fc.write(ByteBuffer.wrap(
        "Some text".getBytes("UTF-16BE")));
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Now try reading again:
    buff.clear();
    try(
      FileChannel fc = new FileInputStream(
        "data2.txt").getChannel()
    ) {
      fc.read(buff);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    buff.flip();
    System.out.println(buff.asCharBuffer());
    // Use a CharBuffer to write through:
    buff = ByteBuffer.allocate(24);
    buff.asCharBuffer().put("Some text");
    try(
      FileChannel fc = new FileOutputStream(
        "data2.txt").getChannel()
    ) {
      fc.write(buff);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Read and display:
    buff.clear();
    try(
      FileChannel fc = new FileInputStream(
        "data2.txt").getChannel()
    ) {
      fc.read(buff);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    buff.flip();
    System.out.println(buff.asCharBuffer());
  }
}
/* Output:
????
Decoded using windows-1252: Some text
Some text
Some textNULNULNUL
*/
```

缓冲区包含普通字节，为了将这些字节转换为字符，我们必须在输入时对它们进行编码(这样它们输出时就有意义了)，或者在输出时对它们进行解码。

这可以使用 **java.nio.charset** 来完成。**Charset** 类，它提供工具来编码成许多不同类型的字符集:

```java
// newio/AvailableCharSets.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Displays Charsets and aliases
import java.nio.charset.*;
import java.util.*;

public class AvailableCharSets {
  public static void main(String[] args) {
    SortedMap<String,Charset> charSets =
      Charset.availableCharsets();
    for(String csName : charSets.keySet()) {
      System.out.print(csName);
      Iterator aliases = charSets.get(csName)
        .aliases().iterator();
      if(aliases.hasNext())
        System.out.print(": ");
      while(aliases.hasNext()) {
        System.out.print(aliases.next());
        if(aliases.hasNext())
          System.out.print(", ");
      }
      System.out.println();
    }
  }
}
/* Output: (First 7 Lines)
Big5: csBig5
Big5-HKSCS: big5-hkscs, big5hk, Big5_HKSCS, big5hkscs
CESU-8: CESU8, csCESU-8
EUC-JP: csEUCPkdFmtjapanese, x-euc-jp, eucjis,
Extended_UNIX_Code_Packed_Format_for_Japanese, euc_jp,
eucjp, x-eucjp
EUC-KR: ksc5601-1987, csEUCKR, ksc5601_1987, ksc5601,
5601,
euc_kr, ksc_5601, ks_c_5601-1987, euckr
GB18030: gb18030-2000
GB2312: gb2312, euc-cn, x-EUC-CN, euccn, EUC_CN,
gb2312-80,
gb2312-1980
                  ...
*/
```

回到 **BufferToText** 。java 中，如果您 **rewind()** 缓冲区(回到数据的开头)，然后使用该平台的默认字符集 **decode()** 数据，生成的 **CharBuffer** 将在控制台上正常显示。要发现默认字符集，使用**System.getProperty(“file.encoding”)**，它生成命名字符集的字符串。

另一种方法是使用字符集 **encode()**，该字符集在读取文件时生成可打印的内容，如您在**BufferToText.java** 的第三部分中所看到的。这里，**UTF-16BE** 用于将文本写入文件，当读取文本时，您所要做的就是将其转换为 **CharBuffer**，并生成预期的文本。最后，您将看到如果通过**CharBuffer** 写入 **ByteBuffer** 会发生什么(稍后您将对此有更多的了解)。注意，为 **ByteBuffer** 分配了24个字节。

由于每个字符需要两个字节，这对于12个字符已经足够了，但是“some text”只有9个字节。其余的零字节仍然出现在由其toString()生成的CharBuffer的表示中，如输出所示。

<!-- Fetching Primitives -->

## 获取原始类型

虽然 **ByteBuffer** 只包含字节，但它包含了一些方法，用于从它所包含的字节中生成每种不同类型的基元值。这个例子展示了使用以下方法插入和提取各种值:

```java
// newio/GetData.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Getting different representations from a ByteBuffer
import java.nio.*;

public class GetData {
  private static final int BSIZE = 1024;
  public static void main(String[] args) {
    ByteBuffer bb = ByteBuffer.allocate(BSIZE);
    // Allocation automatically zeroes the ByteBuffer:
    int i = 0;
    while(i++ < bb.limit())
      if(bb.get() != 0)
        System.out.println("nonzero");
    System.out.println("i = " + i);
    bb.rewind();
    // Store and read a char array:
    bb.asCharBuffer().put("Howdy!");
    char c;
    while((c = bb.getChar()) != 0)
      System.out.print(c + " ");
    System.out.println();
    bb.rewind();
    // Store and read a short:
    bb.asShortBuffer().put((short)471142);
    System.out.println(bb.getShort());
    bb.rewind();
    // Store and read an int:
    bb.asIntBuffer().put(99471142);
    System.out.println(bb.getInt());
    bb.rewind();
    // Store and read a long:
    bb.asLongBuffer().put(99471142);
    System.out.println(bb.getLong());
    bb.rewind();
    // Store and read a float:
    bb.asFloatBuffer().put(99471142);
    System.out.println(bb.getFloat());
    bb.rewind();
    // Store and read a double:
    bb.asDoubleBuffer().put(99471142);
    System.out.println(bb.getDouble());
    bb.rewind();
  }
}
/* Output:
i = 1025
H o w d y !
12390
99471142
99471142
9.9471144E7
9.9471142E7
*/
```

在分配 **ByteBuffer** 之后，将检查它的值，以确定缓冲区分配是否会自动将内容归零——它确实会这样做。检查所有1,024个值(直到 position的值为 缓冲区的 **limit()** )，所有值都为零。

将原始值插入ByteBuffer的最简单方法是使用 **asCharBuffer()** 、**asShortBuffer()** 等获取该缓冲区的适当“视图”，然后使用该视图的 **put()** 方法。


这将对每个基本数据类型执行。其中唯一有点奇怪的是 **ShortBuffer** 的 **put()**，它需要强制转换 (强制转换并更改结果值)。所有其他视图缓冲区都不需要在它们的 **put()** 方法中强制转换。

<!-- View Buffers -->

## 视图缓冲区

“视图缓冲区”通过特定原始类型的窗口来查看底层 **ByteBuffer**。**ByteBuffer** 仍然是“支持”视图的实际存储，因此对视图所做的任何更改都反映在对 **ByteBuffer** 中的数据的修改中。

如前面的示例所示，这方便地将基本类型插入 **ByteBuffer**。视图缓冲区还可以从 **ByteBuffer** 读取原始值，一次读取一个(ByteBuffer允许)，或者批量读取(数组)。下面是一个通过 **IntBuffer**在**ByteBuffer** 中操作 int 的例子:

```java
// newio/IntBufferDemo.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Manipulating ints in a ByteBuffer with an IntBuffer
import java.nio.*;

public class IntBufferDemo {
  private static final int BSIZE = 1024;
  public static void main(String[] args) {
    ByteBuffer bb = ByteBuffer.allocate(BSIZE);
    IntBuffer ib = bb.asIntBuffer();
    // Store an array of int:
    ib.put(new int[]{ 11, 42, 47, 99, 143, 811, 1016 });
    // Absolute location read and write:
    System.out.println(ib.get(3));
    ib.put(3, 1811);
    // Setting a new limit before rewinding the buffer.
    ib.flip();
    while(ib.hasRemaining()) {
      int i = ib.get();
      System.out.println(i);
    }
  }
}
/* Output:
99
11
42
47
1811
143
811
1016
*/
```

重载的 **put()** 方法首先用于存储 **int** 数组。下面的 **get()** 和 **put()** 方法调用直接访问底层 **ByteBuffer** 中的 **int** 位置。请注意，通过直接操作 **ByteBuffer** ，这些绝对位置访问也可以用于基本类型。

一旦底层 **ByteBuffer** 通过视图缓冲区填充了 **int** 或其他基本类型，那么就可以直接将该 **ByteBuffer **写入通道。您可以轻松地从通道读取数据，并使用视图缓冲区将所有内容转换为特定类型的原语。下面是一个例子，通过在同一个 **ByteBuffer** 上生成不同的视图缓冲区，将相同的字节序列解释为 **short**、**int**、**float**、**long **和 **double**:

```java
// newio/ViewBuffers.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.nio.*;

public class ViewBuffers {
  public static void main(String[] args) {
    ByteBuffer bb = ByteBuffer.wrap(
      new byte[]{ 0, 0, 0, 0, 0, 0, 0, 'a' });
    bb.rewind();
    System.out.print("Byte Buffer ");
    while(bb.hasRemaining())
      System.out.print(
        bb.position()+ " -> " + bb.get() + ", ");
    System.out.println();
    CharBuffer cb =
      ((ByteBuffer)bb.rewind()).asCharBuffer();
    System.out.print("Char Buffer ");
    while(cb.hasRemaining())
      System.out.print(
        cb.position() + " -> " + cb.get() + ", ");
    System.out.println();
    FloatBuffer fb =
      ((ByteBuffer)bb.rewind()).asFloatBuffer();
    System.out.print("Float Buffer ");
    while(fb.hasRemaining())
      System.out.print(
        fb.position()+ " -> " + fb.get() + ", ");
    System.out.println();
    IntBuffer ib =
      ((ByteBuffer)bb.rewind()).asIntBuffer();
    System.out.print("Int Buffer ");
    while(ib.hasRemaining())
      System.out.print(
        ib.position()+ " -> " + ib.get() + ", ");
    System.out.println();
    LongBuffer lb =
      ((ByteBuffer)bb.rewind()).asLongBuffer();
    System.out.print("Long Buffer ");
    while(lb.hasRemaining())
      System.out.print(
        lb.position()+ " -> " + lb.get() + ", ");
    System.out.println();
    ShortBuffer sb =
      ((ByteBuffer)bb.rewind()).asShortBuffer();
    System.out.print("Short Buffer ");
    while(sb.hasRemaining())
      System.out.print(
        sb.position()+ " -> " + sb.get() + ", ");
    System.out.println();
    DoubleBuffer db =
      ((ByteBuffer)bb.rewind()).asDoubleBuffer();
    System.out.print("Double Buffer ");
    while(db.hasRemaining())
      System.out.print(
        db.position()+ " -> " + db.get() + ", ");
  }
}
/* Output:
Byte Buffer 0 -> 0, 1 -> 0, 2 -> 0, 3 -> 0, 4 -> 0, 5
-> 0, 6 -> 0, 7 -> 97,
Char Buffer 0 -> NUL, 1 -> NUL, 2 -> NUL, 3 -> a,
Float Buffer 0 -> 0.0, 1 -> 1.36E-43,
Int Buffer 0 -> 0, 1 -> 97,
Long Buffer 0 -> 97,
Short Buffer 0 -> 0, 1 -> 0, 2 -> 0, 3 -> 97,
Double Buffer 0 -> 4.8E-322,
*/
```

**ByteBuffer** 通过“包装”一个8字节数组生成，然后通过所有不同基本类型的视图缓冲区显示该数组。下图显示了从不同类型的缓冲区读取数据时，数据显示的差异:

![image-20190324153222402](/Users/langdon/Library/Application Support/typora-user-images/image-20190324153222402.png)

<!-- Endians -->

### 端

不同的机器可以使用不同的字节顺序方法来存储数据。“Big endian”将最重要的字节（the most significant byte）放在最低内存地址中，而“little endian”将最重要的字节放在最高内存地址中。

当存储一个大于一个字节的量时，例如 **int**、**float** 等，您可能需要考虑字节排序。字节缓冲区以大端字节形式存储数据，通过网络发送的数据总是使用大端字节顺序。您可以使用 **order()** 和**ByteOrder** 参数来更改 **ByteBuffer** 端，可选参数只有2个：**ByteOrder.BIG_ENDIAN** 或 **ByteOrder.LITTLE_ENDIAN**。

考虑一个包含以下两个字节的 **ByteBuffer** :将数据作为一个**short** (**ByteBuffer. asshortbuffer()**) 读取，生成数字 97 (00000000 01100001)。更改为 little endian 将生成数字 24832 (01100001 00000000)。

这显示了字节顺序的变化取决于 endian 设置:

```java
// newio/Endians.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Endian differences and data storage
import java.nio.*;
import java.util.*;

public class Endians {
  public static void main(String[] args) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[12]);
    bb.asCharBuffer().put("abcdef");
    System.out.println(Arrays.toString(bb.array()));
    bb.rewind();
    bb.order(ByteOrder.BIG_ENDIAN);
    bb.asCharBuffer().put("abcdef");
    System.out.println(Arrays.toString(bb.array()));
    bb.rewind();
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.asCharBuffer().put("abcdef");
    System.out.println(Arrays.toString(bb.array()));
  }
}
/* Output:
[0, 97, 0, 98, 0, 99, 0, 100, 0, 101, 0, 102]
[0, 97, 0, 98, 0, 99, 0, 100, 0, 101, 0, 102]
[97, 0, 98, 0, 99, 0, 100, 0, 101, 0, 102, 0]
*/

```

**ByteBuffer** 分配空间将 **charArray** 中的所有字节作为外部缓冲区保存，因此可以调用 **array()** 方法来显示底层字节。**array()** 方法是“可选的”，您只能在数组支持的缓冲区上调用它 ，否则您将得到一个 **UnsupportedOperationException**。

**charArray** 通过 **CharBuffer** 视图插入到 **ByteBuffer **中。当显示底层字节时，默认顺序与随后的大端序相同，而小端序则交换字节。

<!-- Data Manipulation with Buffers -->

## 使用缓冲区进行数据操作

下图说明了 nio 类之间的关系，展示了如何移动和转换数据。例如，要将字节数组写入文件，使用ByteBuffer.wrap() 方法包装字节数组，使用 **getChannel()** 在 **FileOutputStream** 上打开通道，然后从 **ByteBuffer** 将数据写入 **FileChannel**。

![image-20190324153202297](/Users/langdon/Library/Application Support/typora-user-images/image-20190324153202297.png)

**ByteBuffer** 是将数据移入和移出通道的唯一方法，您只能创建一个独立的原始类型的缓冲区，或者使用“as”方法从ByteBuffer获得一个新缓冲区。也就是说，不能将基元类型的缓冲区转换为ByteBuffer。但您能够通过视图缓冲区将原始数据移动到 **ByteBuffer** 中或移出 **ByteBuffer**。

 

### 缓冲区的细节

缓冲区由数据和四个索引组成，以有效地访问和操作该数据: 标记、位置、限制 和 容量（mark, position, limit and capacity）。有一些方法可以设置和重置这些索引并查询它们的值。

**capacity()** 返回缓冲区的 capacity。

**clear()** 清除缓冲区，将 position 设置为零并 设 limit 为 capacity。您可以调用此方法来覆盖现有缓冲区。

**flip()**  将 limit 设置为位置，并将 position 设置为零。此方法用于准备缓冲区，以便在数据写入缓冲区后进行读取。

**limit()** 返回 limit 的值。

**limit(int lim)** 重设 limit

**mark()**  设置 mark 为当前的 position 

**position()** 返回 position 

**position(int pos) ** 设置 position

**remaining()** 返回 limit - position 。

**hasRemaining()** 如果在 position 与 limit 中间有元素，返回 **true**

从缓冲区插入和提取数据的方法更新这些索引来反映所做的更改。这个例子使用了一个非常简单的算法(交换相邻的字符)来打乱和整理字符在CharBuffer:

```java
// newio/UsingBuffers.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.nio.*;

public class UsingBuffers {
  private static
  void symmetricScramble(CharBuffer buffer) {
    while(buffer.hasRemaining()) {
      buffer.mark();
      char c1 = buffer.get();
      char c2 = buffer.get();
      buffer.reset();
      buffer.put(c2).put(c1);
    }
  }
  public static void main(String[] args) {
    char[] data = "UsingBuffers".toCharArray();
    ByteBuffer bb =
      ByteBuffer.allocate(data.length * 2);
    CharBuffer cb = bb.asCharBuffer();
    cb.put(data);
    System.out.println(cb.rewind());
    symmetricScramble(cb);
    System.out.println(cb.rewind());
    symmetricScramble(cb);
    System.out.println(cb.rewind());
  }
}
/* Output:
UsingBuffers
sUniBgfuefsr
UsingBuffers
*/
```

虽然可以通过使用 **char** 数组调用 **wrap()** 直接生成 **CharBuffer**，但是底层的 **ByteBuffer** 将被分配，而 **CharBuffer** 将作为 **ByteBuffer** 上的视图生成。这强调了目标始终是操作 **ByteBuffer**，因为它与通道交互。

下面是程序在 **symmetricgrab()** 方法入口时缓冲区的样子:

![image-20190324155153600](/Users/langdon/Library/Application Support/typora-user-images/image-20190324155153600.png)

position 指向缓冲区中的第一个元素，capacity 和 limie 紧接在最后一个元素之后。在**symmetricgrab()** 中，while循环迭代到 position 等于 limit。当在缓冲区上调用相对位置的 get() 或 put() 函数时，缓冲区的位置会发生变化。您还可以调用绝对位置的 get() 和 put() 方法，它们包含索引参数 : get()或put()发生的位置。这些方法不修改缓冲区 position 的值。

当控件进入while循环时，使用 **mark()** 调用设置 mark 的值。缓冲区的状态为:

两个相对 get() 调用将前两个字符的值保存在变量c1和c2中。在这两个调用之后，缓冲区看起来是这样的 : 为了执行交换，我们在位置0处编写c2，在位置1处编写c1。我们可以使用绝对put方法来实现这一点，或者用 reset() 方法，将 position 的值设置为 mark 。

两个put()方法分别编写c2和c1 : 在循环的下一次迭代中，将mark设置为position的当前值 : 该过程将继续，直到遍历整个缓冲区为止。在while循环的末尾，position位于缓冲区的末尾。如果显示缓冲区，则只显示位置和限制之间的字符。因此，要显示缓冲区的全部内容，必须使用 rewind() 将位置设置为缓冲区的开始位置。这是 rewind() 调用后 buffer 的状态(mark的值变成undefined):

![image-20190324155528149](/Users/langdon/Library/Application Support/typora-user-images/image-20190324155528149.png)

再次调用 **symmetricgrab()** 函数时，**CharBuffer** 将经历相同的过程并恢复到原始状态。

<!-- Memory-Mapped Files -->

##  内存映射文件

内存映射文件允许您创建和修改太大而无法放入内存的文件。使用内存映射文件，您可以假装整个文件都在内存中，并将其视为一个非常大的数组来访问它。这种方法大大简化了您编写的修改文件的代码:

```java
// newio/LargeMappedFiles.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Creating a very large file using mapping
import java.nio.*;
import java.nio.channels.*;
import java.io.*;

public class LargeMappedFiles {
  static int length = 0x8000000; // 128 MB
  public static void
  main(String[] args) throws Exception {
    try(
      RandomAccessFile tdat =
        new RandomAccessFile("test.dat", "rw")
    ) {
      MappedByteBuffer out = tdat.getChannel().map(
        FileChannel.MapMode.READ_WRITE, 0, length);
      for(int i = 0; i < length; i++)
        out.put((byte)'x');
      System.out.println("Finished writing");
      for(int i = length/2; i < length/2 + 6; i++)
        System.out.print((char)out.get(i));
    }
  }
}
/* Output:
Finished writing
xxxxxx
*/
```

为了读写，我们从 **RandomAccessFile** 开始，获取该文件的通道，然后调用 **map()** 来生成**MappedByteBuffer** ，这是一种特殊的直接缓冲区。您必须指定要在文件中映射的区域的起始点和长度—这意味着您可以选择映射大文件的较小区域。**MappedByteBuffer**  继承了它的**ByteBuffer**，所以它有所有的 **ByteBuffer **方法。这里只展示了 **put()** 和 **get()** 的最简单用法，但是您也可以使用 **asCharBuffer()** 等方法。

使用前面的程序创建的文件有128mb长，可能比您的操作系统一次允许的内存要大。该文件似乎可以同时访问，因为它只有一部分被带进内存，而其他部分被交换出去。这样，一个非常大的文件(最多2gb)可以很容易地修改。注意，底层操作系统的文件映射工具用于最大化性能。

### 性能

虽然“旧”流I/O的性能通过使用 **nio** 实现得到了改进，但是映射文件访问往往要快得多。这个程序做一个简单的性能比较:

```java
// newio/MappedIO.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
import java.io.*;

public class MappedIO {
  private static int numOfInts =      4_000_000;
  private static int numOfUbuffInts = 100_000;
  private abstract static class Tester {
    private String name;
    Tester(String name) {
      this.name = name;
    }
    public void runTest() {
      System.out.print(name + ": ");
      long start = System.nanoTime();
      test();
      double duration = System.nanoTime() - start;
      System.out.format("%.3f%n", duration/1.0e9);
    }
    public abstract void test();
  }
  private static Tester[] tests = {
    new Tester("Stream Write") {
      @Override
      public void test() {
        try(
          DataOutputStream dos =
            new DataOutputStream(
              new BufferedOutputStream(
                new FileOutputStream(
                  new File("temp.tmp"))))
        ) {
          for(int i = 0; i < numOfInts; i++)
            dos.writeInt(i);
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Mapped Write") {
      @Override
      public void test() {
        try(
          FileChannel fc =
            new RandomAccessFile("temp.tmp", "rw")
              .getChannel()
        ) {
          IntBuffer ib =
            fc.map(FileChannel.MapMode.READ_WRITE,
              0, fc.size()).asIntBuffer();
          for(int i = 0; i < numOfInts; i++)
            ib.put(i);
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Stream Read") {
      @Override
      public void test() {
        try(
          DataInputStream dis =
            new DataInputStream(
              new BufferedInputStream(
                new FileInputStream("temp.tmp")))
        ) {
          for(int i = 0; i < numOfInts; i++)
            dis.readInt();
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Mapped Read") {
      @Override
      public void test() {
        try(
          FileChannel fc = new FileInputStream(
            new File("temp.tmp")).getChannel()
        ) {
          IntBuffer ib =
            fc.map(FileChannel.MapMode.READ_ONLY,
              0, fc.size()).asIntBuffer();
          while(ib.hasRemaining())
            ib.get();
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Stream Read/Write") {
      @Override
      public void test() {
        try(
          RandomAccessFile raf =
            new RandomAccessFile(
              new File("temp.tmp"), "rw")
        ) {
          raf.writeInt(1);
          for(int i = 0; i < numOfUbuffInts; i++) {
            raf.seek(raf.length() - 4);
            raf.writeInt(raf.readInt());
          }
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Mapped Read/Write") {
      @Override
      public void test() {
        try(
          FileChannel fc = new RandomAccessFile(
            new File("temp.tmp"), "rw").getChannel()
        ) {
          IntBuffer ib =
            fc.map(FileChannel.MapMode.READ_WRITE,
              0, fc.size()).asIntBuffer();
          ib.put(0);
          for(int i = 1; i < numOfUbuffInts; i++)
            ib.put(ib.get(i - 1));
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  };
  public static void main(String[] args) {
    Arrays.stream(tests).forEach(Tester::runTest);
  }
}
/* Output:
Stream Write: 0.615
Mapped Write: 0.050
Stream Read: 0.577
Mapped Read: 0.015
Stream Read/Write: 4.069
Mapped Read/Write: 0.013
*/
```

**Tester** 是一个模板方法模式，它为匿名内部子类中定义的 **test()** 的各种实现创建一个测试框架。每个子类都执行一种测试，因此 test() 方法还为您提供了执行各种I/O活动的原型。

虽然映射的写似乎使用 **FileOutputStream**，但是文件映射中的所有输出必须使用 **RandomAccessFile**，就像前面代码中的读/写一样。

请注意，**test()** 方法包括初始化各种I/O对象的时间，因此，尽管映射文件的设置可能很昂贵，但是与流I/O相比，总体收益非常可观。

<!-- File Locking -->

### 文件锁定

文件锁定同步访问，因此文件可以是共享资源。但是，争用同一个文件的两个线程可能位于不同的jvm 中，可能一个是 Java 线程，另一个是操作系统中的某个本机线程。文件锁定对其他操作系统进程是可见的，因为Java文件锁定直接映射到本机操作系统锁定工具。

```java
// newio/FileLocking.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.nio.channels.*;
import java.util.concurrent.*;
import java.io.*;

public class FileLocking {
  public static void main(String[] args) {
    try(
      FileOutputStream fos =
        new FileOutputStream("file.txt");
      FileLock fl = fos.getChannel().tryLock()
    ) {
      if(fl != null) {
        System.out.println("Locked File");
        TimeUnit.MILLISECONDS.sleep(100);
        fl.release();
        System.out.println("Released Lock");
      }
    } catch(IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
/* Output:
Locked File
Released Lock
*/
```

通过调用 **FileChannel** 上的 **tryLock()** 或 **lock()**，可以获得整个文件的 **FileLock**。( **SocketChannel、DatagramChannel **和 **ServerSocketChannel **不需要锁定，因为它们本质上是单进程实体 ; 通常不会在两个进程之间共享一个网络套接字) **tryLock()** 是非阻塞的。它试图抓住锁，但如果它不能抓住了(当其他进程已经持有相同的锁，并且它不是共享的)，它只是从方法调用返回。

**lock()** 会阻塞，直到获得锁，或者调用 **lock()** 的线程中断，或者调用 **lock()** 方法的通道关闭。使 用**FileLock.release()** 释放锁。还可以使用 **tryLock(long position, long size, boolean shared)** 或**lock(long position, long size, boolean shared)** 锁定文件的一部分，锁住该区域(size-position)。第三个参数指定是否共享此锁。

虽然零参数锁定方法适应文件大小的变化，但是如果文件大小发生变化，具有固定大小的锁不会发生变化。如果从一个位置到另一个位置获得一个锁，并且文件的增长超过了 position + size ，那么超出位置+大小的部分没有被锁定。零参数锁定方法锁定整个文件，即使它在增长。

底层操作系统必须提供对独占锁或共享锁的支持。如果操作系统不支持共享锁，并且请求共享锁，则使用独占锁。可以使用 **FileLock.isShared()** 查询锁的类型 (共享或独占)。

<!-- Locking Portions of a Mapped File -->

### 锁定映射文件的某些部分

文件映射通常用于非常大的文件。您可能需要锁定此类文件的某些部分，以便其他进程可以修改未锁定的部分。例如，数据库必须同时对许多用户可用。这里你可以看到两个线程，每个线程都锁定文件的不同部分:

```java
// newio/LockingMappedFiles.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Locking portions of a mapped file
import java.nio.*;
import java.nio.channels.*;
import java.io.*;

public class LockingMappedFiles {
  static final int LENGTH = 0x8FFFFFF; // 128 MB
  static FileChannel fc;
  public static void
  main(String[] args) throws Exception {
    fc = new RandomAccessFile("test.dat", "rw")
      .getChannel();
    MappedByteBuffer out = fc.map(
      FileChannel.MapMode.READ_WRITE, 0, LENGTH);
    for(int i = 0; i < LENGTH; i++)
      out.put((byte)'x');
    new LockAndModify(out, 0, 0 + LENGTH/3);
    new LockAndModify(
      out, LENGTH/2, LENGTH/2 + LENGTH/4);
  }
  private static class LockAndModify extends Thread {
    private ByteBuffer buff;
    private int start, end;
    LockAndModify(ByteBuffer mbb, int start, int end) {
      this.start = start;
      this.end = end;
      mbb.limit(end);
      mbb.position(start);
      buff = mbb.slice();
      start();
    }
    @Override
    public void run() {
      try {
        // Exclusive lock with no overlap:
        FileLock fl = fc.lock(start, end, false);
        System.out.println(
          "Locked: "+ start +" to "+ end);
        // Perform modification:
        while(buff.position() < buff.limit() - 1)
          buff.put((byte)(buff.get() + 1));
        fl.release();
        System.out.println(
          "Released: " + start + " to " + end);
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
/* Output:
Locked: 75497471 to 113246206
Locked: 0 to 50331647
Released: 75497471 to 113246206
Released: 0 to 50331647
*/

```

**LockAndModify** 线程类设置缓冲区并创建要修改的 **slice()**，在 **run()** 中，锁在文件通道上获取(不能在缓冲区上获取锁—只能在通道上获取锁)。**lock()** 的调用非常类似于获取对象上的线程锁——现在有了一个“临界区”，可以对文件的这部分进行独占访问。[^1]

当 JVM 退出或关闭获取锁的通道时，锁会自动释放，但是您也可以显式地调用 **FileLock** 对象上的**release()**，如上所示。

<!-- 脚注 -->

1. 您可以在附录:低级并发中找到关于线程的更多细节。