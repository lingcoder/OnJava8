[TOC]

<!-- File -->
# 第十七章 文件
在丑陋的Java I/O编程方式诞生多年以后，Java终于简化了文件读写的基本操作。这种"困难方式"的全部细节都在[Appendix: I/O Streams]()。如果你读过这个部分，就会认同Java设计者毫不在意他们的使用者的体验这一观念。打开并读取文件对于大多数编程语言来是非常常用的，由于I/O糟糕的设计以至于
很少有人能够在不依赖其他参考代码的情况下完成打开文件的操作。

好像Java设计者终于意识到了Java使用者多年来的痛苦，在Java7中对此引入了巨大的改进。这些新元素被放在**java.nio.file**包下面，过去人们通常把**nio**中的**n**理解为**new**即新的**io**，现在更应该当成是**non-blocking**非阻塞**io**(**io**就是*input/output输入/输出*)。**java.nio.file**库终于将Java文件操作带到与其他编程语言相同的水平。最重要的是Java8新增的streams与文件结合使得文件操作编程变得更加优雅。我们将看一下文件操作的两个基本组件：
1. 文件或者目录的路径；
2. 文件本身。

<!-- File and Directory Paths -->
## 文件和目录路径
### `Paths`
一个**Path**对象表示一个文件或者目录的路径，是一个跨操作系统（OS）和文件系统的抽象，目的是在构造路径时不必关注底层操作系统，代码可以在不进行修改的情况下运行在不同的操作系统上。**java.nio.file.Paths**类包含一个重载方法**static get()**，该方法方法接受一系列**Strings**字符串或一个*统一资源标识符*(URI)作为参数，并且进行转换返回一个**Path**对象：
```java
// files/PathInfo.java
import java.nio.file.*;
import java.net.URI;
import java.io.File;
import java.io.IOException;

public class PathInfo {
    static void show(String id, Object p) {
        System.out.println(id + ": " + p);
    }
    
    static void info(Path p) {
        show("toString", p);
        show("Exists", Files.exists(p));
        show("RegularFile", Files.isRegularFile(p));
        show("Directory", Files.isDirectory(p));
        show("Absolute", p.isAbsolute());
        show("FileName", p.getFileName());
        show("Parent", p.getParent());
        show("Root", p.getRoot());
        System.out.println("******************");
    }
    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
        info(Paths.get("C:", "path", "to", "nowhere", "NoFile.txt")); 
        Path p = Paths.get("PathInfo.java");
        info(p);
        Path ap = p.toAbsolutePath();
        info(ap);
        info(ap.getParent());
        try {
            info(p.toRealPath());
        } catch(IOException e) {
           System.out.println(e);
        }
        URI u = p.toUri();
        System.out.println("URI: " + u);
        Path puri = Paths.get(u);
        System.out.println(Files.exists(puri));
        File f = ap.toFile(); // Don't be fooled
    }
}

/* 输出:
Windows 10
toString: C:\path\to\nowhere\NoFile.txt
Exists: false
RegularFile: false
Directory: false
Absolute: true
FileName: NoFile.txt
Parent: C:\path\to\nowhere
Root: C:\
******************
toString: PathInfo.java
Exists: true
RegularFile: true
Directory: false
Absolute: false
FileName: PathInfo.java
Parent: null
Root: null
******************
toString: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files\PathInfo.java
Exists: true
RegularFile: true
Directory: false
Absolute: true
FileName: PathInfo.java
Parent: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files
Root: C:\
******************
toString: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files
Exists: true
RegularFile: false
Directory: true
Absolute: true
FileName: files
Parent: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples
Root: C:\
******************
toString: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files\PathInfo.java
Exists: true
RegularFile: true
Directory: false
Absolute: true
FileName: PathInfo.java
Parent: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files
Root: C:\
******************
URI: file:///C:/Users/Bruce/Documents/GitHub/onjava/
ExtractedExamples/files/PathInfo.java
true
*/
```

我已经在这一章第一个程序的**main()**方法添加了第一行用于展示操作系统的名称，因此你可以看到不同操作系统之间存在哪些差异。理想情况下，差别会相对较小，并且使用**/**或者**\\**路径分隔符进行分隔。你可以看到我运行在Windows 10上的程序输出。

当**toString()**方法生成完整形式的路径，你可以看到**getFileName()** 方法总是返回当前文件名。
通过使用**Files**工具类(我们接下类将会更多的使用它)，可以测试一个文件是否存在，测试是否是一个"真正"的文件还是一个目录等等。"Nofile.txt"这个示例展示我们描述的文件可能并不在指定的位置；这样可以允许你创建一个新的路径。"PathInfo.java"存在于当前目录中，最初它只是没有路径的文件名，但它仍然被检测为"存在"。一旦我们将其转换为绝对路径，我们将会得到一个从"C:"盘(因为我们是在Windows机器下进行测试)开始的完整路径，现在它也拥有一个父路径。“真实”路径的定义在文档中有点模糊,因为它取决于具体的文件系统。例如，如果文件名不区分大小写，即使路径由于大小写的缘故而不是完全相同，也可能得到肯定的匹配结果。在这样的平台上，**toRealPath()** 将返回实际情况下的**Path**，并且还会删除任何冗余元素。

这里你会看到**URI**看起来只能用于描述文件，实际上**URI**可以用于描述更多的东西；通过[维基百科]()可以了解更多细节。现在我们成功地将**URI**转为一个**Path**对象。

最后，你会在**Path**中看到一些有点欺骗的东西，这就是调用**toFile()**方法会生成一个**File**对象。听起来似乎可以得到一个类似文件的东西(毕竟被称为**File**)，但是这个方法的存在仅仅是为了向后兼容。虽然看上去应该被称为"路径"，实际上却应该表示目录或者文件本身。这是个非常草率并且令人困惑的命名，但是由于**java.nio.file**的存在我们可以安全的忽略它的存在。

### 选取路径部分片段
**Path**对象可以非常容易的生成路径的某一部分：

```java
// files/PartsOfPaths.java
import java.nio.file.*;

public class PartsOfPaths {
    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
        Path p = Paths.get("PartsOfPaths.java").toAbsolutePath();
        for(int i = 0; i < p.getNameCount(); i++)
            System.out.println(p.getName(i));
        System.out.println("ends with '.java': " +
        p.endsWith(".java"));
        for(Path pp : p) {
            System.out.print(pp + ": ");
            System.out.print(p.startsWith(pp) + " : ");
            System.out.println(p.endsWith(pp));
        }
        System.out.println("Starts with " + p.getRoot() + " " + p.startsWith(p.getRoot()));
    }
}

/* 输出:
Windows 10
Users
Bruce
Documents
GitHub
on-java
ExtractedExamples
files
PartsOfPaths.java
ends with '.java': false
Users: false : false
Bruce: false : false
Documents: false : false
GitHub: false : false
on-java: false : false
ExtractedExamples: false : false
files: false : false
PartsOfPaths.java: false : true
Starts with C:\ true
*/

```
可以通过**getName()**来索引**Path**的各个部分，直到达到上限**getNameCount()**。**Path**也继承了**Iterable**接口，因此我们也可以通过增强的for循环进行遍历。请注意，即使路径以 **.java**结尾，使用**endsWith()** 方法也会返回**false**。这是因为使用**endsWith()** 比较的是整个路径部分，而不会包含文件路径的后缀。通过使用**startsWith()** 和**endsWith()**也可以完成路径的遍历。但是我们可以看到，遍历**Path**对象并不包含根路径，只有使用
**startsWith()**检测根路径时才会返回**true**。

### 路径分析
**Files**工具类包含一系列完整的方法用于获得**Path**相关的信息。
```java
// files/PathAnalysis.java
import java.nio.file.*;
import java.io.IOException;

public class PathAnalysis {
    static void say(String id, Object result) {
        System.out.print(id + ": ");
        System.out.println(result);
    }
    
    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("os.name"));
        Path p = Paths.get("PathAnalysis.java").toAbsolutePath();
        say("Exists", Files.exists(p));
        say("Directory", Files.isDirectory(p));
        say("Executable", Files.isExecutable(p));
        say("Readable", Files.isReadable(p));
        say("RegularFile", Files.isRegularFile(p));
        say("Writable", Files.isWritable(p));
        say("notExists", Files.notExists(p));
        say("Hidden", Files.isHidden(p));
        say("size", Files.size(p));
        say("FileStore", Files.getFileStore(p));
        say("LastModified: ", Files.getLastModifiedTime(p));
        say("Owner", Files.getOwner(p));
        say("ContentType", Files.probeContentType(p));
        say("SymbolicLink", Files.isSymbolicLink(p));
        if(Files.isSymbolicLink(p))
            say("SymbolicLink", Files.readSymbolicLink(p));
        if(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"))
            say("PosixFilePermissions",
        Files.getPosixFilePermissions(p));
    }
}

/* 输出:
Windows 10
Exists: true
Directory: false
Executable: true
Readable: true
RegularFile: true
Writable: true
notExists: false
Hidden: false
size: 1631
FileStore: SSD (C:)
LastModified: : 2017-05-09T12:07:00.428366Z
Owner: MINDVIEWTOSHIBA\Bruce (User)
ContentType: null
SymbolicLink: false
*/
```
在调用最后一个测试方法**getPosixFilePermissions()** 之前我们需要确认一下当前文件系统是否支持**Posix**接口，否则会抛出运行时异常。

### **Paths**的增减修改
我们必须能通过对**Path**对象增加或者删除一部分来构造一个新的**Path**对象。我们使用**relativize()**构造一个路径与给定路径的相对路径，使用**resolve()**解析为一个新的**Path**对象(不一定是“可发现”的名称)。对于下面代码中的示例，我使用**relativize()** 方法从所有的输出中移除根路径，部分原因是为了示范，部分原因是为了简化输出结果，这说明你可以使用该方法将绝对路径转为相对路径。  
这个版本的代码中包含**id**，以便于跟踪输出结果：

```java
// files/AddAndSubtractPaths.java
import java.nio.file.*;
import java.io.IOException;

public class AddAndSubtractPaths {
    static Path base = Paths.get("..", "..", "..").toAbsolutePath().normalize();
    
    static void show(int id, Path result) {
        if(result.isAbsolute())
            System.out.println("(" + id + ")r " + base.relativize(result));
        else
            System.out.println("(" + id + ") " + result);
        try {
            System.out.println("RealPath: " + result.toRealPath());
        } catch(IOException e) {
            System.out.println(e);
        }
    }
    
    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
        System.out.println(base);
        Path p = Paths.get("AddAndSubtractPaths.java").toAbsolutePath();
        show(1, p);
        Path convoluted = p.getParent().getParent()
        .resolve("strings").resolve("..")
        .resolve(p.getParent().getFileName());
        show(2, convoluted);
        show(3, convoluted.normalize());
        Path p2 = Paths.get("..", "..");
        show(4, p2);
        show(5, p2.normalize());
        show(6, p2.toAbsolutePath().normalize());
        Path p3 = Paths.get(".").toAbsolutePath();
        Path p4 = p3.resolve(p2);
        show(7, p4);
        show(8, p4.normalize());
        Path p5 = Paths.get("").toAbsolutePath();
        show(9, p5);
        show(10, p5.resolveSibling("strings"));
        show(11, Paths.get("nonexistent"));
    }
}

/* 输出:
Windows 10
C:\Users\Bruce\Documents\GitHub
(1)r onjava\
ExtractedExamples\files\AddAndSubtractPaths.java
RealPath: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files\AddAndSubtractPaths.java
(2)r on-java\ExtractedExamples\strings\..\files
RealPath: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files
(3)r on-java\ExtractedExamples\files
RealPath: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files
(4) ..\..
RealPath: C:\Users\Bruce\Documents\GitHub\on-java
(5) ..\..
RealPath: C:\Users\Bruce\Documents\GitHub\on-java
(6)r on-java
RealPath: C:\Users\Bruce\Documents\GitHub\on-java
(7)r on-java\ExtractedExamples\files\.\..\..
RealPath: C:\Users\Bruce\Documents\GitHub\on-java
(8)r on-java
RealPath: C:\Users\Bruce\Documents\GitHub\on-java
(9)r on-java\ExtractedExamples\files
RealPath: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files
(10)r on-java\ExtractedExamples\strings
RealPath: C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\strings
(11) nonexistent
java.nio.file.NoSuchFileException:
C:\Users\Bruce\Documents\GitHub\onjava\
ExtractedExamples\files\nonexistent
*/
```
我还为**toRealPath()** 添加了进一步的测试，这是为了扩展和规则化，防止路径不存在以免产生运行时异常。

<!-- File Systems -->

<!-- Directories -->
## 目录

## 文件系统


<!-- Watching a Path -->
## 路径监听


<!-- Finding Files -->
## 文件查找


<!-- Reading & Writing Files -->
## 文件读写


<!-- Summary -->
## 本章小结



<!-- 分页 -->
<div style="page-break-after: always;"></div>