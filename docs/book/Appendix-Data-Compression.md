[TOC]

<!-- Appendix: Data Compression -->
# 附录:数据压缩

Java I/O 类库提供了可以读写压缩格式流的类。你可以将其他 I/O 类包装起来用于提供压缩功能。

这些类不是从 **Reader** 和 **Writer** 类派生的，而是 **InputStream** 和 **OutputStream** 层级结构的一部分。这是由于压缩库处理的是字节，而不是字符。但是，你可能会被迫混合使用两种类型的流（请记住，你可以使用 **InputStreamReader** 和 **OutputStreamWriter**，这两个类可以在字节类型和字符类型之间轻松转换）。

| 压缩类                   | 功能                                                         |
| ------------------------ | ------------------------------------------------------------ |
| **CheckedInputStream**   | `getCheckSum()` 可以对任意 **InputStream** 计算校验和（而不只是解压） |
| **CheckedOutputStream**  | `getCheckSum()` 可以对任意 **OutputStream** 计算校验和（而不只是压缩） |
| **DeflaterOutputStream** | 压缩类的基类                                                 |
| **ZipOutputStream**      | **DeflaterOutputStream** 类的一种，用于压缩数据到 Zip 文件结构 |
| **GZIPOutputStream**     | **DeflaterOutputStream** 类的一种，用于压缩数据到 GZIP 文件结构 |
| **InflaterInputStream**  | 解压类的基类                                                 |
| **ZipInputStream**       | **InflaterInputStream** 类的一种，用于解压 Zip 文件结构的数据 |
| **GZIPInputStream**      | **InflaterInputStream** 类的一种，用于解压 GZIP 文件结构的数据 |

尽管存在很多压缩算法，但是 Zip 和 GZIP 可能是最常见的。你可以使用许多用于读取和写入这些格式的工具，来轻松操作压缩数据。

<!-- Simple Compression with GZIP -->

## 使用 Gzip 简单压缩

<!-- Multifile Storage with Zip -->

GZIP 接口十分简单，因此当你有一个需要压缩的数据流（而不是一个包含不同数据分片的容器）时，使用 GZIP 更为合适。如下是一个压缩单个文件的示例：

```java
// compression/GZIPcompress.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java GZIPcompress GZIPcompress.java}
// {VisuallyInspectOutput}

public class GZIPcompress {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(
                    "Usage: \nGZIPcompress file\n" +
                            "\tUses GZIP compression to compress " +
                            "the file to test.gz");
            System.exit(1);
        }
        try (
                InputStream in = new BufferedInputStream(
                        new FileInputStream(args[0]));
                BufferedOutputStream out =
                        new BufferedOutputStream(
                                new GZIPOutputStream(
                                        new FileOutputStream("test.gz")))
        ) {
            System.out.println("Writing file");
            int c;
            while ((c = in.read()) != -1)
                out.write(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Reading file");
        try (
                BufferedReader in2 = new BufferedReader(
                        new InputStreamReader(new GZIPInputStream(
                                new FileInputStream("test.gz"))))
        ) {
            in2.lines().forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

```

使用压缩类非常简单，你只需要把你的输出流包装在 **GZIPOutputStream** 或 **ZipOutputStream** 中，将输入流包装在 **GZIPInputStream** 或 **ZipInputStream**。其他的一切就只是普通的 I/O 读写。这是面向字符流和面向字节流的混合示例；in 使用 Reader 类，而 **GZIPOutputStreams** 构造函数只能接受 **OutputStream** 对象，而不能接受 **Writer** 对象。当打开文件的时候，**GZIPInputStream** 会转换成为 **Reader**。

## 使用 zip 多文件存储

支持 Zip 格式的库比 GZIP 库更广泛。有了它，你可以轻松存储多个文件，甚至还有一个单独的类可以轻松地读取 Zip 文件。该库使用标准 Zip 格式，因此它可以与当前可在 Internet 上下载的所有 Zip 工具无缝协作。以下示例与前一个示例具有相同的形式，但它可以根据需要处理任意数量的命令行参数。此外，它还显示了 **Checksum** 类计算和验证文件的校验和。有两种校验和类型：Adler32（更快）和 CRC32（更慢但更准确）。

```java
// compression/ZipCompress.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Uses Zip compression to compress any
// number of files given on the command line
// {java ZipCompress ZipCompress.java}
// {VisuallyInspectOutput}
public class ZipCompress {
    public static void main(String[] args) {
        try (
                FileOutputStream f =
                        new FileOutputStream("test.zip");
                CheckedOutputStream csum =
                        new CheckedOutputStream(f, new Adler32());
                ZipOutputStream zos = new ZipOutputStream(csum);
                BufferedOutputStream out =
                        new BufferedOutputStream(zos)
        ) {
            zos.setComment("A test of Java Zipping");
            // No corresponding getComment(), though.
            for (String arg : args) {
                System.out.println("Writing file " + arg);
                try (
                        InputStream in = new BufferedInputStream(
                                new FileInputStream(arg))
                ) {
                    zos.putNextEntry(new ZipEntry(arg));
                    int c;
                    while ((c = in.read()) != -1)
                        out.write(c);
                }
                out.flush();
            }
            // Checksum valid only after the file is closed!
            System.out.println(
                    "Checksum: " + csum.getChecksum().getValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Now extract the files:
        System.out.println("Reading file");
        try (
                FileInputStream fi =
                        new FileInputStream("test.zip");
                CheckedInputStream csumi =
                        new CheckedInputStream(fi, new Adler32());
                ZipInputStream in2 = new ZipInputStream(csumi);
                BufferedInputStream bis =
                        new BufferedInputStream(in2)
        ) {
            ZipEntry ze;
            while ((ze = in2.getNextEntry()) != null) {
                System.out.println("Reading file " + ze);
                int x;
                while ((x = bis.read()) != -1)
                    System.out.write(x);
            }
            if (args.length == 1)
                System.out.println(
                        "Checksum: " + csumi.getChecksum().getValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Alternative way to open and read Zip files:
        try (
                ZipFile zf = new ZipFile("test.zip")
        ) {
            Enumeration e = zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze2 = (ZipEntry) e.nextElement();
                System.out.println("File: " + ze2);
                // ... and extract the data as before
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

对于要添加到存档的每个文件，必须调用 `putNextEntry()` 并传递 **ZipEntry** 对象。 **ZipEntry** 对象包含一个扩展接口，用于获取和设置 Zip 文件中该特定条目的所有可用数据：名称，压缩和未压缩大小，日期，CRC 校验和，额外字段数据，注释，压缩方法以及它是否是目录条目。但是，即使 Zip 格式有设置密码的方法，Java 的 Zip 库也不支持。虽然 **CheckedInputStream** 和 **CheckedOutputStream** 都支持 Adler32 和 CRC32 校验和，但 **ZipEntry** 类仅支持 CRC 接口。这是对基础 Zip 格式的限制，但它可能会限制你使用更快的 Adler32。

要提取文件，**ZipInputStream** 有一个 `getNextEntry()` 方法，这个方法在有文件存在的情况下调用，会返回下一个 **ZipEntry**。作为一个更简洁的替代方法，你可以使用 **ZipFile** 对象读取该文件，该对象具有方法 entries() 返回一个包裹 **ZipEntries** 的 **Enumeration**。

要读取校验和，你必须以某种方式访问关联的 **Checksum** 对象。这里保留了对 **CheckedOutputStream** 和 **CheckedInputStream** 对象的引用，但你也可以保持对 **Checksum** 对象的引用。 Zip 流中的一个令人困惑的方法是 `setComment()`。如 **ZipCompress** 所示。在 Java 中，你可以在编写文件时设置注释，但是没有办法恢复 **ZipInputStream** 中的注释。注释似乎仅通过 **ZipEntry** 在逐个条目的基础上完全支持。

使用 GZIP 或 Zip 库时，你不仅被限制于文件——你可以压缩任何内容，包括通过网络连接发送的数据。



<!-- Java Archives (Jars) -->

## Java 的 jar

Zip 格式也用于 JAR（Java ARchive）文件格式，这是一种将一组文件收集到单个压缩文件中的方法，就像 Zip 一样。但是，与 Java 中的其他所有内容一样，JAR 文件是跨平台的，因此你不必担心平台问题。你还可以将音频和图像文件像类文件一样包含在其中。

JAR 文件由一个包含压缩文件集合的文件和一个描述它们的“清单（manifest）”组成。（你可以创建自己的清单文件；否则，jar 程序将为你执行此操作。）你可以在 JDK 文档中，找到更多关于 JAR 清单的信息。

JDK 附带的 jar 工具会自动压缩你选择的文件。你可以在命令行上调用它：

```shell
jar [options] destination [manifest] inputfile(s)
```

选项是一组字母（不需要连字符或任何其他指示符）。 Unix / Linux 用户会注意到这些选项与 tar 命令选项的相似性。这些是：

| 选项       | 功能                                                         |
| ---------- | ------------------------------------------------------------ |
| **c**      | 创建一个新的或者空的归档文件                                 |
| **t**      | 列出内容目录                                                 |
| **x**      | 提取所有文件                                                 |
| **x** file | 提取指定的文件                                               |
| **f**      | 这代表着，“传递文件的名称。”如果你不使用它，jar 假定它的输入将来自标准输入，或者，如果它正在创建一个文件，它的输出将转到标准输出。 |
| **m**      | 代表第一个参数是用户创建的清单文件的名称。                   |
| **v**      | 生成详细的输出用于表述 jar 所作的事情                        |
| **0**      | 仅存储文件;不压缩文件（用于创建放在类路径中的 JAR 文件）。   |
| **M**      | 不要自动创建清单文件                                         |

如果放入 JAR 文件的文件中包含子目录，则会自动添加该子目录，包括其所有子目录等。还会保留路径信息。

以下是一些调用 jar 的典型方法。以下命令创建名为 myJarFile 的 JAR 文件。 jar 包含当前目录中的所有类文件，以及自动生成的清单文件：

```shell
jar cf myJarFile.jar *.class
```

下一个命令与前面的示例类似，但它添加了一个名为 myManifestFile.mf 的用户创建的清单文件。 ：

```shell
jar cmf myJarFile.jar myManifestFile.mf *.class
```

这个命令输出了 myJarFile.jar 中的文件目录：

```shell
jar tf myJarFile.jar
```

如下添加了一个“verbose”的标志，用于生成更多关于 myJarFile.jar 中文件的详细信息：

```shell
jar tvf myJarFile.jar
```

假设 audio，classes 和 image 都是子目录，它将所有子目录组合到文件 myApp.jar 中。还包括“verbose”标志，以便在 jar 程序工作时提供额外的反馈：

```shell
jar cvf myApp.jar audio classes image
```

如果你在创建 JAR 文件时使用了 0（零） 选项，该文件将会被替换在你的类路径（CLASSPATH）中：

```shell
CLASSPATH="lib1.jar;lib2.jar;"
```

然后 Java 可以搜索到 lib1.jar 和 lib2.jar 的类文件。

jar 工具不像 Zip 实用程序那样通用。例如，你无法将文件添加或更新到现有 JAR 文件；只能从头开始创建 JAR 文件。

此外，你无法将文件移动到 JAR 文件中，在移动文件时将其删除。

但是，在一个平台上创建的 JAR 文件可以通过任何其他平台上的 jar 工具透明地读取（这个问题有时会困扰 Zip 实用程序）。

<!-- 分页 -->

<div style="page-break-after: always;"></div>