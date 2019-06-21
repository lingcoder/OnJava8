[TOC]

<!-- Implementation Hiding -->
# 第七章 封装

访问控制（或者隐藏实现）与"最初的实现不恰当"有关。

所有优秀的作者——包括这些编写软件的人——都知道一件好的作品都是经过反复打磨才变得优秀的。如果你把一段代码置于某个位置一段时间，过一会重新来看，你可能发现更好的实现方式。这是重构的原动力之一，重构就是重写可工作的代码，使之更加可读，易懂，因而更易维护。

但是，在修改和完善代码的愿望下，也存在巨大的压力。通常，客户端程序员希望你的代码在某些方面保持不变。所以你想修改代码，但他们希望代码保持不变。由此引出了面向对象设计中的一个基本问题："如何区分变动的事物和不变的事物"。

这个问题对于类库而言尤其重要。类库的使用者必须依赖他们所使用的那部分类库，并且知道如果使用了类库的新版本，不需要改写代码。另一方面，类库的开发者必须有修改和改进类库的自由，并保证客户代码不会受这些改动影响。

这可以通过约定解决。例如，类库开发者必须同意在修改类库中的一个类时，不会移除已有的方法，因为那样将会破坏客户端程序员的代码。与之相反的情况更加复杂。在有成员属性的情况下，类库开发者如何知道哪些属性被客户端程序员使用？这同样会发生在那些只为实现类库类而创建的方法上，它们也不是设计成可供客户端程序员调用的。如果类库开发者想删除旧的实现，添加新的实现，结果会怎样呢？任何这些成员的改动都可能破环客户端程序员的代码。因此类库开发者会被束缚，不能修改任何事物。

为了解决这一问题，Java 提供了访问修饰符供类库开发者指明哪些对于客户端程序员是可用的，哪些是不可用的。访问控制权限的等级，从"最大权限"到"最小权限"依次是：**public**，**protected**，包访问权限（没有关键字）和 **private**。根据上一段的内容，你可能会想，作为一名类库设计者，你会尽可能将一切都设为 **private**，仅向客户端程序员暴露你愿意他们使用的方法。这就是你通常所做的，尽管这与使用其他语言（尤其是 C）编程和访问不受任何限制的人们的直觉相违背。

然而，构建类库的概念和对类库组件的访问控制仍然不完善。其中仍然存在问题就是如何将类库组件捆绑到一个内聚到类库单元中。Java 中通过 package 关键字加以控制，类是在相同包下还是不同包下会影响访问修饰符。所以在这章开始，你将会学习如何将类库组件置于同一个包下，之后你就能明白访问修饰符的全部含义。

<!-- package: the Library Unit -->

## 包的概念

包内包含一组类，它们被组织在一个单独的命名空间下。

例如，标准 Java 发布中有一个工具库，它被组织在 **java.util** 命名空间下。**java.util** 中含有一个类，叫做 **ArrayList**。使用 **ArrayList** 的一种方式是用其全名 **java.util.ArrayList**。

```java
// hiding/FullQualification.java

public class FullQualification {
    public static void main(String[] args) {
        java.util.ArrayList list = new java.util.ArrayList();
    }
}
```

这种方式使得程序冗长乏味，因此你可以换一种方式，使用 **import** 关键字。如果需要导入某个类，就需要在 **import** 语句中声明：

```java
// hiding/SingleImport.java

import java.util.ArrayList;

public class SingleImport {
    public static void main(String[] args) {
        ArrayList list = new ArrayList();
    }
}
```

现在你就可以不加限定词，直接使用 **ArrayList** 了。但是对于 **java.util** 包下的其他类，你还是不能用。要导入其中所有的类，只需使用 ***** ，就像本书中其他示例那样：
```java
import java.util.*
```

之所以使用导入，是为了提供一种管理命名空间的机制。所有类名之间都是相互隔离的。类 **A** 中的方法 `f()` 不会与类 **B** 中具有相同签名的方法 `f()` 冲突。但是如果类名冲突呢？假设你创建了一个 **Stack** 类，打算安装在一台已经有别人所写的 **Stack** 类的机器上，该怎么办呢？这种类名的潜在冲突，正是我们需要在 Java 中对命名空间进行完全控制的原因。为了解决冲突，我们为每个类创建一个唯一标识符组合。

到目前为止的大部分示例都只存在单个文件，并为本地使用的，所以尚未收到包名的干扰。但是，这些示例其实已经位于包中了，叫做"未命名"包或默认包。这当然是一种选择，为了简单起见，本书其余部分会尽可能采用这种方式。但是，如果你打算为相同机器上的其他 Java 程序创建友好的类库或程序时，就必须仔细考虑以防类名冲突。

一个 Java 源代码文件称为一个*编译单元*（有时也称*翻译单元*）。每个编译单元的文件名后缀必须是 **.java**。在编译单元中可以有一个 **public** 类，它的类名必须与文件名相同（包括大小写，但不包括后缀名 **.java**）。每个编译单元中只能有一个 **public** 类，否则编译器不接受。如果这个编译单元中还有其他类，那么在包之外是无法访问到这些类的，因为它们不是 **public** 类，此时它们支持主 **public** 类。

### 代码组织

当编译一个 **.java** 文件时，**.java** 文件的每个类都会有一个输出文件。每个输出的文件名和 **.java** 文件中每个类的类名相同，只是后缀名是 **.class**。因此，在编译少量的 **.java** 文件后，会得到大量的 **.class** 文件。如果你使用过编译型语言，那么你可能习惯编译后产生一个中间文件（通常称为"obj"文件），然后与使用链接器（创建可执行文件）或类库生成器（创建类库）产生的其他同类文件打包到一起的情况。这不是 Java 工作的方式。在 Java 中，可运行程序是一组 **.class** 文件，它们可以打包压缩成一个 Java 文档文件（JAR，使用 **jar** 文档生成器）。Java 解释器负责查找、加载和解释这些文件。

类库是一组类文件。每个源文件通常都含有一个 **public** 类和任意数量的非 **public** 类，因此每个文件都有一个构件。如果把这些组件集中在一起，就需要使用关键字 **package**。

如果你使用了 **package** 语句，它必须是文件中除了注释之外的第一行代码。当你如下这样写：

```java
package hiding;
```

意味着这个编译单元是一个名为 **hiding** 类库的一部分。换句话说，你正在声明的编译单元中的 **public** 类名称位于名为 **hiding** 的保护伞下。任何人想要使用该名称，必须指明完整的类名或者使用 **import** 关键字导入 **hiding**。（注意，Java 包名按惯例一律小写，即使中间的单词也需要小写，与驼峰命名不同）

例如，假设文件名是 **MyClass.java**，这意味着文件中只能有一个 **public** 类，且类名必须是 MyClass（大小写也与文件名相同）：

```java
// hiding/mypackage/MyClass.java
package hiding.mypackage

public class MyClass {
    // ...
}
```

现在，如果有人想使用 **MyClass** 或 **hiding.mypackage** 中的其他 **public** 类，就必须使用关键字 **import** 来使 **hiding.mypackage** 中的名称可用。还有一种选择是使用完整的名称：

```java
// hiding/QualifiedMyClass.java

public class QualifiedMyClass {
    public static void main(String[] args) {
        hiding.mypackage.MyClass m = new hiding.mypackage.MyClass();
    }
}
```

关键字 **import** 使之更简洁：

```java
// hiding/ImportedMyClass.java
import hiding.mypackage.*;

public class ImportedMyClass {
    public static void main(String[] args) {
        MyClass m = new MyClass();
    }
}	
```

**package** 和 **import** 这两个关键字将单一的全局命名空间分隔开，从而避免名称冲突。

### 创建独一无二的包名

你可能注意到，一个包从未真正被打包成单一的文件，它可以由很多 **.class** 文件构成，因而事情就变得有点复杂了。为了避免这种情况，一种合乎逻辑的做法是将特定包下的所有 **.class** 文件都放在一个目录下。也就是说，利用操作系统的文件结构的层次性。这是 Java 解决解决混乱问题的一种方式；稍后你还会在我们介绍 **jar** 工具时看到另一种方式。

将所有的文件放在一个子目录还解决了其他的两个问题：创建独一无二的包名和查找可能隐藏于目录结构某处的类。这是通过将 **.class** 文件所在的路径位置编码成 **package** 名称来实现的。按照惯例，**package** 名称是类的创建者的反顺序的 Internet 域名。如果你遵循惯例，因为 Internet 域名是独一无二的，所以你的 **package** 名称也应该是独一无二的，不会发生名称冲突。如果你没有自己的域名，你就得构造一组不大可能与他人重复的组合（比如你的姓名），来创建独一无二的 package 名称。如果你打算发布 Java 程序代码，那么花些力气去获取一个域名是值得的。

此技巧的第二部分是把 **package** 名称分解成你机器上的一个目录，所以当 Java 解释器必须要加载一个 .class 文件时，它能定位到 **.class** 文件所在的位置。首先，它找出环境变量 **CLASSPATH**（通过操作系统设置，有时也能通过 Java 的安装程序或基于 Java 的工具设置）。**CLASSPATH** 包含一个或多个目录，用作查找 .**class** 文件的根目录。从根目录开始，Java 解释器获取包名并将每个句点替换成反斜杠，生成一个基于根目录的路径名（包名 foo.bar.baz 变成 foo\bar\baz 或 foo/bar/baz 或其它，取决于你的操作系统）。然后这个路径与 **CLASSPATH** 的不同项连接，解释器就在这些目录中查找与你所创建的类名称相关的 **.class** 文件（解释器还会查找某些涉及 Java 解释器所在位置的标准目录）。

为了理解这点，比如说我的域名 **MindviewInc.com**，将之反转并全部改为小写后就是 **com.mindviewinc**，这将作为我创建的类的独一无二的全局名称。（com、edu、org等扩展名之前在 Java 包中都是大写，但是 Java 2 之后都统一用小写。）我决定再创建一个名为 **simple** 的类库，从而细分名称：

```java
package com.mindviewinc.simple;
```

这个包名可以用作下面两个文件的命名空间保护伞：

```java
// com/mindviewinc/simple/Vector.java
// Creating a package
package com.mindviewinc.simple;

public class Vector {
    public Vector() {
        System.out.println("com.mindviewinc.simple.Vector");
    }
}
```

如前所述，**package** 语句必须是文件的第一行非注释代码。第二个文件看上去差不多：

```java
// com/mindviewinc/simple/List.java
// Creating a package
package com.mindviewinc.simple;

public class List {
    System.out.println("com.mindview.simple.List");
}
```

这两个文件都位于我机器上的子目录中，如下：

```
C:\DOC\Java\com\mindviewinc\simple
```

（注意，本书的每个文件的第一行注释都指明了文件在源代码目录树中的位置——供本书的自动代码提取工具使用。）

如果你回头看这个路径，会看到包名 **com.mindviewinc.simple**，但是路径的第一部分呢？CLASSPATH 环境变量会处理它。我机器上的环境变量部分如下：

```
CLASSPATH=.;D:\JAVA\LIB;C:\DOC\Java
```

CLASSPATH 可以包含多个不同的搜索路径。

但是在使用 JAR 文件时，有点不一样。你必须在类路径写清楚 JAR 文件的实际名称，不能仅仅是 JAR 文件所在的目录。因此，对于一个名为 **grape.jar** 的 JAR 文件，类路径应包括：

```
CLASSPATH=.;D\JAVA\LIB;C:\flavors\grape.jar
```

一旦设置好类路径，下面的文件就可以放在任意目录：

```java
// hiding/LibTest.java
// Uses the library
import com.mindviewinc.simple.*;

public class LibTest {
    public static void main(String[] args) {
        Vector v = new Vector();
        List l = new List();
    }
}
```

输出：

```
com.mindviewinc.simple.Vector
com.mindviewinc.simple.List
```

<!-- Java Access Specifiers -->

## 访问权限修饰符


<!-- Interface and Implementation -->
## 接口和实现


<!-- Class Access -->
## 类访问权限


<!-- Summary -->
## 本章小结



<!-- 分页 -->

<div style="page-break-after: always;"></div>

