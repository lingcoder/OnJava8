[TOC]

<!-- Implementation Hiding -->
# 第七章 封装

> *访问控制（Access control）*（或者*隐藏实现（implementation hiding）*）与“最初的实现不恰当”有关。

所有优秀的作者——包括那些编写软件的人——都知道一件好的作品都是经过反复打磨才变得优秀的。如果你把一段代码置于某个位置一段时间，过一会重新来看，你可能发现更好的实现方式。这是*重构*（refactoring）的原动力之一，重构就是重写可工作的代码，使之更加可读，易懂，因而更易维护。

但是，在修改和完善代码的愿望下，也存在巨大的压力。通常，一些用户（*客户端程序员（client programmers）*）希望你的代码在某些方面保持不变。所以你想修改代码，但他们希望代码保持不变。由此引出了面向对象设计中的一个基本问题：“如何区分变动的事物和不变的事物”。

这个问题对于类库（library）而言尤其重要。类库的使用者必须依赖他们所使用的那部分类库，并且知道如果使用了类库的新版本，不需要改写代码。另一方面，类库的开发者必须有修改和改进类库的自由，并保证客户代码不会受这些改动影响。

这可以通过约定解决。例如，类库开发者必须同意在修改类库中的一个类时，不会移除已有的方法，因为那样将会破坏客户端程序员的代码。与之相反的情况更加复杂。在有成员属性的情况下，类库开发者如何知道哪些属性被客户端程序员使用？这同样会发生在那些只为实现类库类而创建的方法上，它们也不是设计成可供客户端程序员调用的。如果类库开发者想删除旧的实现，添加新的实现，结果会怎样呢？任何这些成员的改动都可能破环客户端程序员的代码。因此类库开发者会被束缚，不能修改任何事物。

为了解决这一问题，Java 提供了*访问修饰符*（access specifier）供类库开发者指明哪些对于客户端程序员是可用的，哪些是不可用的。访问控制权限的等级，从“最大权限”到“最小权限”依次是：**public**，**protected**，*包访问权限（package access）*（没有关键字）和 **private**。根据上一段的内容，你可能会想，作为一名类库设计者，你会尽可能将一切都设为 **private**，仅向客户端程序员暴露你愿意他们使用的方法。这就是你通常所做的，尽管这与那些使用其他语言（尤其是 C）编程以及习惯了不受限制地访问任何东西的人们的直觉相违背。

然而，类库组件的概念和对类库组件访问的控制仍然不完善。其中仍然存在问题就是如何将类库组件捆绑到一个内聚的类库单元中。Java 中通过 **package** 关键字加以控制，类在相同包下还是在不同包下，会影响访问修饰符。所以在这章开始，你将会学习如何将类库组件置于同一个包下，之后你就能明白访问修饰符的全部含义。

<!-- package: the Library Unit -->

## 包的概念

包内包含一组类，它们被组织在一个单独的*命名空间*（namespace）下。

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

现在你就可以不加限定词，直接使用 **ArrayList** 了。但是对于 **java.util** 包下的其他类，你还是不能用。要导入其中所有的类，只需使用 **\*** ，就像本书中其他示例那样：
```java
import java.util.*
```

之所以使用导入，是为了提供一种管理命名空间的机制。所有类名之间都是相互隔离的。类 **A** 中的方法 `f()` 不会与类 **B** 中具有相同签名的方法 `f()` 冲突。但是如果类名冲突呢？假设你创建了一个 **Stack** 类，打算安装在一台已经有别人所写的 **Stack** 类的机器上，该怎么办呢？这种类名的潜在冲突，正是我们需要在 Java 中对命名空间进行完全控制的原因。为了解决冲突，我们为每个类创建一个唯一标识符组合。

到目前为止的大部分示例都只存在单个文件，并为本地使用的，所以尚未受到包名的干扰。但是，这些示例其实已经位于包中了，叫做“未命名”包或*默认包*（default package）。这当然是一种选择，为了简单起见，本书其余部分会尽可能采用这种方式。但是，如果你打算为相同机器上的其他 Java 程序创建友好的类库或程序时，就必须仔细考虑以防类名冲突。

一个 Java 源代码文件称为一个*编译单元（compilation unit）*（有时也称*翻译单元（translation unit）*）。每个编译单元的文件名后缀必须是 **.java**。在编译单元中可以有一个 **public** 类，它的类名必须与文件名相同（包括大小写，但不包括后缀名 **.java**）。每个编译单元中只能有一个 **public** 类，否则编译器不接受。如果这个编译单元中还有其他类，那么在包之外是无法访问到这些类的，因为它们不是 **public** 类，此时它们为主 **public** 类提供“支持”类 。

### 代码组织

当编译一个 **.java** 文件时，**.java** 文件的每个类都会有一个输出文件。每个输出的文件名和 **.java** 文件中每个类的类名相同，只是后缀名是 **.class**。因此，在编译少量的 **.java** 文件后，会得到大量的 **.class** 文件。如果你使用过编译型语言，那么你可能习惯编译后产生一个中间文件（通常称为“obj”文件），然后与使用链接器（创建可执行文件）或类库生成器（创建类库）产生的其他同类文件打包到一起的情况。这不是 Java 工作的方式。在 Java 中，可运行程序是一组 **.class** 文件，它们可以打包压缩成一个 Java 文档文件（JAR，使用 **jar** 文档生成器）。Java 解释器负责查找、加载和解释这些文件。

类库是一组类文件。每个源文件通常都含有一个 **public** 类和任意数量的非 **public** 类，因此每个文件都有一个 **public** 组件。如果把这些组件集中在一起，就需要使用关键字 **package**。

如果你使用了 **package** 语句，它必须是文件中除了注释之外的第一行代码。当你如下这样写：

```java
package hiding;
```

意味着这个编译单元是一个名为 **hiding** 类库的一部分。换句话说，你正在声明的编译单元中的 **public** 类名称位于名为 **hiding** 的保护伞下。任何人想要使用该名称，必须指明完整的类名或者使用 **import** 关键字导入 **hiding** 。（注意，Java 包名按惯例一律小写，即使中间的单词也需要小写，与驼峰命名不同）

例如，假设文件名是 **MyClass.java** ，这意味着文件中只能有一个 **public** 类，且类名必须是 **MyClass**（大小写也与文件名相同）：

```java
// hiding/mypackage/MyClass.java
package hiding.mypackage;

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

你可能注意到，一个包从未真正被打包成单一的文件，它可以由很多 **.class** 文件构成，因而事情就变得有点复杂了。为了避免这种情况，一种合乎逻辑的做法是将特定包下的所有 **.class** 文件都放在一个目录下。也就是说，利用操作系统的文件结构的层次性。这是 Java 解决混乱问题的一种方式；稍后你还会在我们介绍 **jar** 工具时看到另一种方式。

将所有的文件放在一个子目录还解决了其他的两个问题：创建独一无二的包名和查找可能隐藏于目录结构某处的类。这是通过将 **.class** 文件所在的路径位置编码成 **package** 名称来实现的。按照惯例，**package** 名称是类的创建者的反顺序的 Internet 域名。如果你遵循惯例，因为 Internet 域名是独一无二的，所以你的 **package** 名称也应该是独一无二的，不会发生名称冲突。如果你没有自己的域名，你就得构造一组不大可能与他人重复的组合（比如你的姓名），来创建独一无二的 package 名称。如果你打算发布 Java 程序代码，那么花些力气去获取一个域名是值得的。

此技巧的第二部分是把 **package** 名称分解成你机器上的一个目录，所以当 Java 解释器必须要加载一个 .class 文件时，它能定位到 **.class** 文件所在的位置。首先，它找出环境变量 **CLASSPATH**（通过操作系统设置，有时也能通过 Java 的安装程序或基于 Java 的工具设置）。**CLASSPATH** 包含一个或多个目录，用作查找 .**class** 文件的根目录。从根目录开始，Java 解释器获取包名并将每个句点替换成反斜杠，生成一个基于根目录的路径名（取决于你的操作系统，包名 **foo.bar.baz** 变成 **foo\bar\baz** 或 **foo/bar/baz** 或其它）。然后这个路径与 **CLASSPATH** 的不同项连接，解释器就在这些目录中查找与你所创建的类名称相关的 **.class** 文件（解释器还会查找某些涉及 Java 解释器所在位置的标准目录）。

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

当编译器遇到导入 **simple** 库的 **import** 语句时，它首先会在 CLASSPATH 指定的目录中查找子目录 **com/mindviewinc/simple**，然后从已编译的文件中找出名称相符者（对 **Vector** 而言是 **Vector.class**，对 **List** 而言是 **List.class**）。注意，这两个类和其中要访问的方法都必须是 **public** 修饰的。

对于 Java 新手而言，设置 CLASSPATH 是一件麻烦的事（我最初使用时是这么觉得的），后面版本的 JDK 更加智能。你会发现当你安装好 JDK 时，即使不设置 CLASSPATH，也能够编译和运行基本的 Java 程序。但是，为了编译和运行本书的代码示例（从[https://github.com/BruceEckel/OnJava8-examples](https://github.com/BruceEckel/OnJava8-examples) 取得），你必须将本书程序代码树的基本目录加入到 CLASSPATH 中（ gradlew 命令管理自身的 CLASSPATH，所以如果你想直接使用 javac 和 java，不用 Gradle 的话，就需要设置 CLASSPATH）。

### 冲突

如果通过 **\*** 导入了两个包含相同名字类名的类库，会发生什么？例如，假设程序如下：

```java
import com.mindviewinc.simple.*;
import java.util.*;
```

因为 **java.util.*** 也包含了 **Vector** 类，这就存在潜在的冲突。但是只要你不写导致冲突的代码，就不会有问题——这样很好，否则就得做很多类型检查工作来防止那些根本不会出现的冲突。

现在如果要创建一个 Vector 类，就会出现冲突：

```java
Vector v = new Vector();
```

这里的 **Vector** 类指的是谁呢？编译器不知道，读者也不知道。所以编译器报错，强制你明确指明。对于标准的 Java 类 **Vector**，你可以这么写：

```java
java.util.Vector v = new java.util.Vector();
```

这种写法完全指明了 **Vector** 类的位置（配合 CLASSPATH），那么就没有必要写 **import java.util.*** 语句，除非使用其他来自 **java.util** 中的类。

或者，可以导入单个类以防冲突——只要不在同一个程序中使用有冲突的名字（若使用了有冲突的名字，必须明确指明全名）。

### 定制工具库

具备了以上知识，现在就可以创建自己的工具库来减少重复的程序代码了。

一般来说，我会使用反转后的域名来命名要创建的工具包，比如 **com.mindviewinc.util** ，但为了简化，这里我把工具包命名为 **onjava**。

比如，下面是“控制流”一章中使用到的 `range()` 方法，采用了 for-in 语法进行简单的遍历：

```java
// onjava/Range.java
// Array creation methods that can be used without
// qualifiers, using static imports:
package onjava;

public class Range {
    // Produce a sequence [0,n)
    public static int[] range(int n) {
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            result[i] = i;
        }
        return result;
    }
    // Produce a sequence [start..end)
    public static int[] range(int start, int end) {
        int sz = end - start;
        int[] result = new int[sz];
        for (int i = 0; i < sz; i++) {
            result[i] = start + i;
        }
        return result;
    }
    // Produce sequence [start..end) incrementing by step
    public static int[] range(int start, int end, int step) {
        int sz = (end - start) / step;
        int[] result = new int[sz];
        for (int i = 0; i < sz; i++) {
            result[i] = start + (i * step);
        }
        return result;
    }
}
```

这个文件的位置一定是在某个以一个 CLASSPATH 位置开始，然后接着是 **onjava** 的目录下。编译完之后，就可以在系统的任何地方使用 **import onjava** 语句来使用这些方法了。

从现在开始，无论何时你创建了有用的新工具，都可以把它加入到自己的类库中。在本书中，你将会看到更多的组件加入到 **onjava** 库。

### 使用 import 改变行为

Java 没有 C 的*条件编译*（conditional compilation）功能，该功能使你不必更改任何程序代码而能够切换开关产生不同的行为。Java 之所以去掉此功能，可能是因为 C 在绝大多数情况下使用该功能解决跨平台问题：程序代码的不同部分要根据不同的平台来编译。而 Java 自身就是跨平台设计的，这个功能就没有必要了。

但是，条件编译还有其他的用途。调试是一个很常见的用途，调试功能在开发过程中是开启的，在发布的产品中是禁用的。可以通过改变导入的 **package** 来实现这一目的，修改的方法是将程序中的代码从调试版改为发布版。这个技术可用于任何种类的条件代码。

### 使用包的忠告

当创建一个包时，包名就隐含了目录结构。这个包必须位于包名指定的目录中，该目录必须在以 CLASSPATH 开始的目录中可以查询到。 最初使用关键字 **package** 可能会有点不顺，因为除非遵守“包名对应目录路径”的规则，否则会收到很多意外的运行时错误信息如找不到特定的类，即使这个类就位于同一目录中。如果你收到类似信息，尝试把 **package** 语句注释掉，如果程序能运行的话，你就知道问题出现在哪里了。

注意，编译过的代码通常位于与源代码的不同目录中。这是很多工程的标准，而且集成开发环境（IDE）通常会自动为我们做这些。必须保证 JVM 通过 CLASSPATH 能找到编译后的代码。

<!-- Java Access Specifiers -->

## 访问权限修饰符

Java 访问权限修饰符 **public**，**protected** 和 **private** 位于定义的类名，属性名和方法名之前。每个访问权限修饰符只能控制它所修饰的对象。

如果不提供访问修饰符，就意味着"包访问权限"。所以无论如何，万物都有某种形式的访问控制权。接下来的几节中，你将学习各种类型的访问权限。

### 包访问权限

本章之前的所有示例要么使用 **public** 访问修饰符，要么就没使用修饰符（*默认访问权限（default access）*）。默认访问权限没有关键字，通常被称为*包访问权限（package access）*（有时也称为 **friendly**）。这意味着当前包中的所有其他类都可以访问那个成员。对于这个包之外的类，这个成员看上去是 **private** 的。由于一个编译单元（即一个文件）只能隶属于一个包，所以通过包访问权限，位于同一编译单元中的所有类彼此之间都是可访问的。

包访问权限可以把相关类聚到一个包下，以便它们能轻易地相互访问。包里的类赋予了它们包访问权限的成员相互访问的权限，所以你"拥有”了包内的程序代码。只能通过你所拥有的代码去访问你所拥有的其他代码，这样规定很有意义。构建包访问权限机制是将类聚集在包中的重要原因之一。在许多语言中，在文件中组织定义的方式是任意的，但是在 Java 中你被强制以一种合理的方式组织它们。另外，你可能会将不应该对当前包中的类具有访问权限的类排除在包外。

类控制着哪些代码有权访问自己的成员。其他包中的代码不能一上来就说"嗨，我是 **Bob** 的朋友！"，然后想看到 **Bob** 的 **protected**、包访问权限和 **private** 成员。取得对成员的访问权的唯一方式是：

1. 使成员成为 **public**。那么无论是谁，无论在哪，都可以访问它。
2. 赋予成员默认包访问权限，不用加任何访问修饰符，然后将其他类放在相同的包内。这样，其他类就可以访问该成员。
3. 在"复用"这一章你将看到，继承的类既可以访问 **public** 成员，也可以访问 **protected** 成员（但不能访问 **private** 成员）。只有当两个类处于同一个包内，它才可以访问包访问权限的成员。但现在不用担心继承和 **protected**。
4. 提供访问器（accessor）和修改器（mutator）方法（有时也称为"get/set" 方法），从而读取和改变值。

### public: 接口访问权限

当你使用关键字 **public**，就意味着紧随 public 后声明的成员对于每个人都是可用的，尤其是使用类库的客户端程序员更是如此。假设定义了一个包含下面编译单元的 **dessert** 包：

```java
// hiding/dessert/Cookie.java
// Creates a library
package hiding.dessert;

public class Cookie {
    public Cookie() {
        System.out.println("Cookie constructor");
    }
    
    void bite() {
        System.out.println("bite");
    }
}
```

记住，**Cookie.java** 文件产生的类文件必须位于名为 **dessert** 的子目录中，该子目录在 **hiding** （表明本书的"封装"章节）下，它必须在 CLASSPATH 的几个目录之下。不要错误地认为 Java 总是会将当前目录视作查找行为的起点之一。如果你的 CLASSPATH 中没有 **.**，Java 就不会查找当前目录。

现在，使用 **Cookie** 创建一个程序：
```java
// hiding/Dinner.java
// Uses the library
import hiding.dessert.*;

public class Dinner {
    public static void main(String[] args) {
        Cookie x = new Cookie();
        // -x.bite(); // Can't access
    }
}
```

输出：

```
Cookie constructor
```

你可以创建一个 **Cookie** 对象，因为它构造器和类都是 **public** 的。（后面会看到更多 **public** 的概念）但是，在 **Dinner.java** 中无法访问到 **Cookie** 对象中的 `bite()` 方法，因为 `bite()` 只提供了包访问权限，因而在 **dessert** 包之外无法访问，编译器禁止你使用它。

### 默认包

你可能惊讶地发现，以下代码尽管看上去破坏了规则，但是仍然可以编译：

```java
// hiding/Cake.java
// Accesses a class in a separate compilation unit
class Cake {
    public static void main(String[] args) {
        Pie x = new Pie();
        x.f();
    }
}
```

输出：

```
Pie.f()
```

同一目录下的第二个文件：

```java
// hiding/Pie.java
// The other class
class Pie {
    void f() {
        System.out.println("Pie.f()");
    }
}
```

最初看上去这两个文件毫不相关，但在 **Cake** 中可以创建一个 **Pie** 对象并调用它的 `f()` 方法。（注意，你的 CLASSPATH 中一定得有 **.**，这样文件才能编译）通常会认为 **Pie** 和  `f()` 具有包访问权限，因此不能被 **Cake** 访问。它们的确具有包访问权限，这是部分正确。**Cake.java** 可以访问它们是因为它们在相同的目录中且没有给自己设定明确的包名。Java 把这样的文件看作是隶属于该目录的默认包中，因此它们为该目录中所有的其他文件都提供了包访问权限。

### private: 你无法访问

关键字 **private** 意味着除了包含该成员的类，其他任何类都无法访问这个成员。同一包中的其他类无法访问 **private** 成员，因此这等于说是自己隔离自己。另一方面，让许多人合作创建一个包也是有可能的。使用 **private**，你可以自由地修改那个被修饰的成员，无需担心会影响同一包下的其他类。

默认的包访问权限通常提供了足够的隐藏措施；记住，使用类的客户端程序员无法访问包访问权限成员。这样做很好，因为默认访问权限是一种我们常用的权限（同时也是一种在忘记添加任何访问权限时自动得到的权限）。因此，通常考虑的是把哪些成员声明成 **public** 供客户端程序员使用。所以，最初不常使用关键字 **private**，因为程序没有它也可以照常工作。然而，使用 **private** 是非常重要的，尤其是在多线程环境中。（在"并发编程"一章中将看到）。

以下是一个使用 **private** 的例子：

```java
// hiding/IceCream.java
// Demonstrates "private" keyword

class Sundae {
    private Sundae() {}
    static Sundae makeASundae() {
        return new Sundae();
    }
}

public class IceCream {
    public static void main(String[] args) {
        //- Sundae x = new Sundae();
        Sundae x = Sundae.makeASundae();
    }
}
```

以上展示了 **private** 的用武之地：控制如何创建对象，防止别人直接访问某个特定的构造器（或全部构造器）。例子中，你无法通过构造器创建一个 **Sundae** 对象，而必须调用 `makeASundae()` 方法创建对象。

任何可以肯定只是该类的"助手"方法，都可以声明为 **private**，以确保不会在包中的其他地方误用它，也防止了你会去改变或删除它。将方法声明为 **private** 确保了你拥有这种选择权。

对于类中的 **private** 属性也是一样。除非必须公开底层实现（这种情况很少见），否则就将属性声明为 **private**。然而，不能因为类中某个对象的引用是 **private**，就认为其他对象也无法拥有该对象的 **public** 引用（参见附录：对象传递和返回）。

### protected: 继承访问权限

要理解 **protected** 的访问权限，我们在内容上需要作一点跳跃。首先，在介绍本书"复用"章节前，你不必真正理解本节的内容。但为了内容的完整性，这里作了简要介绍，举了个使用 **protected** 的例子。

关键字 **protected** 处理的是继承的概念，通过继承可以利用一个现有的类——我们称之为基类，然后添加新成员到现有类中而不必碰现有类。我们还可以改变类的现有成员的行为。为了从一个类中继承，需要声明新类 extends 一个现有类，像这样：

```java
class Foo extends Bar {}
```

类定义的其他部分看起来是一样的。

如果你创建了一个新包，并从另一个包继承类，那么唯一能访问的就是被继承类的 **public** 成员。（如果在同一个包中继承，就可以操作所有的包访问权限的成员。）有时，基类的创建者会希望某个特定成员能被继承类访问，但不能被其他类访问。这时就需要使用 **protected**。**protected** 也提供包访问权限，也就是说，相同包内的其他类可以访问 **protected** 元素。

回顾下先前的文件 **Cookie.java**，下面的类不能调用包访问权限的方法 `bite()`：

```java
// hiding/ChocolateChip.java
// Can't use package-access member from another package
import hiding.dessert.*;

public class ChocolateChip extends Cookie {
    public ChocolateChip() {
        System.out.println("ChocolateChip constructor");
    } 
    
    public void chomp() {
        //- bite(); // Can't access bite
    }
    
    public static void main(String[] args) {
        ChocolateChip x = new ChocolateChip();
        x.chomp();
    }
}
```

输出：

```
Cookie constructor
ChocolateChip constructor
```

如果类 **Cookie** 中存在一个方法 `bite()`，那么它的任何子类中都存在 `bite()` 方法。但是因为 `bite()` 具有包访问权限并且位于另一个包中，所以我们在这个包中无法使用它。你可以把它声明为 **public**，但这样一来每个人都能访问它，这可能也不是你想要的。如果你将 **Cookie** 改成如下这样：

```java
// hiding/cookie2/Cookie.java
package hiding.cookie2;

public class Cookie {
    public Cookie() {
        System.out.println("Cookie constructor");
    }
    
    protected void bite() {
        System.out.println("bite");
    }
}
```

这样，`bite()` 对于所有继承 **Cookie** 的类，都是可访问的：

```java
// hiding/ChocolateChip2.java
import hiding.cookie2.*;

public class ChocolateChip2 extends Cookie {
    public ChocoalteChip2() {
        System.out.println("ChocolateChip2 constructor");
    }
    
    public void chomp() {
        bite(); // Protected method
    }
    
    public static void main(String[] args) {
        ChocolateChip2 x = new ChocolateChip2();
        x.chomp();
    }
}
```

输出：

```
Cookie constructor
ChocolateChip2 constructor
bite
```

尽管 `bite()` 也具有包访问权限，但它不是 **public** 的。

### 包访问权限 Vs Public 构造器

当你定义一个具有包访问权限的类时，你可以在类中定义一个 public 构造器，编译器不会报错：

```java
// hiding/packageaccess/PublicConstructor.java
package hiding.packageaccess;

class PublicConstructor {
    public PublicConstructor() {}
}
```

有一个 Checkstyle 工具，你可以运行命令 **gradlew hiding:checkstyleMain** 使用它，它会指出这种写法是虚假的，而且从技术上来说是错误的。实际上你不能从包外访问到这个 **public** 构造器：

```java
// hiding/CreatePackageAccessObject.java
// {WillNotCompile}
import hiding.packageaccess.*;

public class CreatePackageAcessObject {
    public static void main(String[] args) {
        new PublicConstructor();
    }
}
```

如果你编译下这个类，会得到编译错误信息：

```
CreatePackageAccessObject.java:6:error:
PublicConstructor is not public in hiding.packageaccess;
cannot be accessed from outside package
new PublicConstructor();
^
1 error
```

因此，在一个具有包访问权限的类中定义一个 **public** 的构造器并不能真的使这个构造器成为 **public**，在声明的时候就应该标记为编译时错误。

<!-- Interface and Implementation -->

## 接口和实现

访问控制通常被称为*隐藏实现*（implementation hiding）。将数据和方法包装进类中并把具体实现隐藏被称作是*封装*（encapsulation）。其结果就是一个同时带有特征和行为的数据类型。

出于两个重要的原因，访问控制在数据类型内部划定了边界。第一个原因是确立客户端程序员可以使用和不能使用的边界。可以在结构中建立自己的内部机制而不必担心客户端程序员偶尔将内部实现作为他们可以使用的接口的一部分。

这直接引出了第二个原因：将接口与实现分离。如果在一组程序中使用接口，而客户端程序员只能向 **public** 接口发送消息的话，那么就可以自由地修改任何不是 **public** 的事物（例如包访问权限，protected，或 private 修饰的事物），却不会破坏客户端代码。

为了清晰起见，你可以采用一种创建类的风格：**public** 成员放在类的开头，接着是 **protected** 成员，包访问权限成员，最后是 **private** 成员。这么做的好处是类的使用者可以从头读起，首先会看到对他们而言最重要的部分（public 成员，因为可以从文件外访问它们），直到遇到非 **public** 成员时停止阅读，下面就是内部实现了：

```java
// hiding/OrganizedByAccess.java

public class OrganizedByAccess {
    public void pub1() {/* ... */}
    public void pub2() {/* ... */}
    public void pub3() {/* ... */}
    private void priv1() {/* ... */}
    private void priv2() {/* ... */}
    private void priv3() {/* ... */}
    private int i;
    // ...
}
```

这么做只能是程序阅读起来稍微容易一些，因为实现和接口还是混合在一起。也就是说，你仍然能看到源代码——实现部分，因为它就在类中。另外，javadoc 提供的注释文档功能降低了程序代码的可读性对客户端程序员的重要性。将接口展现给类的使用者实际上是类浏览器的任务，类浏览器会展示所有可用的类，并告诉你如何使用它们（比如说哪些成员可用）。在 Java 中，JDK 文档起到了类浏览器的作用。

<!-- Class Access -->

## 类访问权限

访问权限修饰符也可以用于确定类库中的哪些类对于类库的使用者是可用的。如果希望某个类可以被客户端程序员使用，就把关键字 **public** 作用于整个类的定义。这甚至控制着客户端程序员能否创建类的对象。

为了控制一个类的访问权限，修饰符必须出现在关键字 **class** 之前：

```java
public class Widget {
```

如果你的类库名是 **hiding**，那么任何客户端程序员都可以通过如下声明访问 **Widget**：

```java
import hiding.Widget;
```

或者

```java
import hiding.*;
```

这里有一些额外的限制：

1. 每个编译单元（即每个文件）中只能有一个 **public** 类。这表示，每个编译单元有一个公共的接口用 **public** 类表示。该接口可以包含许多支持包访问权限的类。一旦一个编译单元中出现一个以上的 **public** 类，编译就会报错。
2. **public** 类的名称必须与含有该编译单元的文件名相同，包括大小写。所以对于 **Widget** 来说，文件名必须是 **Widget.java**，不能是 **widget.java** 或 **WIDGET.java**。再次强调，如果名字不匹配，编译器会报错。
3. 虽然不是很常见，但是编译单元内没有 **public** 类也是可能的。这时可以随意命名文件（尽管随意命名会让代码的阅读者和维护者感到困惑）。

如果是一个在 **hiding** 包中的类，只用来完成 **Widget** 或 **hiding** 包下一些其他 **public** 类所要执行的任务，怎么设置它的访问权限呢？ 你不想自找麻烦为客户端程序员创建说明文档，并且你认为不久后会完全改变原有方案并将旧版本删除，替换成新版本。为了保留此灵活性，需要确保客户端程序员不依赖隐藏在 **hiding** 中的任何特定细节，那么把 **public** 关键字从类中去掉，给予它包访问权限，就可以了。

当你创建了一个包访问权限的类，把类中的属性声明为 **private** 仍然是有意义的——应该尽可能将所有属性都声明为 **private**，但是通常把方法声明成与类（包访问权限）相同的访问权限也是合理的。一个包访问权限的类只能被用于包内，除非强制将某些方法声明为 **public**，这种情况下，编译器会告诉你。

注意，类既不能是 **private** 的（这样除了该类自身，任何类都不能访问它），也不能是 **protected** 的。所以对于类的访问权限只有两种选择：包访问权限或者 **public**。为了防止类被外界访问，可以将所有的构造器声明为 **private**，这样只有你自己能创建对象（在类的 static 成员中）：

```java
// hiding/Lunch.java
// Demonstrates class access specifiers. Make a class
// effectively private with private constructors:

class Soup1 {
    private Soup1() {}
    
    public static Soup1 makeSoup() { // [1]
        return new Soup1();
    }
}

class Soup2 {
    private Soup2() {}
    
    private static Soup2 ps1 = new Soup2(); // [2]
    
    public static Soup2 access() {
        return ps1;
    }
    
    public void f() {}
}
// Only one public class allowed per file:
public class Lunch {
    void testPrivate() {
        // Can't do this! Private constructor:
        //- Soup1 soup = new Soup1();
    }
    
    void testStatic() {
        Soup1 soup = Soup1.makeSoup();
    }
    
    void testSingleton() {
        Soup2.access().f();
    }
}
```

可以像 [1] 那样通过 **static** 方法创建对象，也可以像 [2] 那样先创建一个静态对象，当用户需要访问它时返回对象的引用即可。

到目前为止，大部分的方法要么返回 void，要么返回基本类型，所以 [1] 处的定义乍看之下会有点困惑。方法名（**makeSoup**）前面的 **Soup1** 表明了方法返回的类型。到目前为止，这里经常是 **void**，即不返回任何东西。然而也可以返回对象的引用，就像这里一样。这个方法返回了对 **Soup1** 类对象的引用。

**Soup1** 和 **Soup2** 展示了如何通过将你所有的构造器声明为 **private** 的方式防止直接创建某个类的对象。记住，如果你不显式地创建构造器，编译器会自动为你创建一个无参构造器（没有参数的构造器）。如果我们编写了无参构造器，那么编译器就不会自动创建构造器了。将构造器声明为 **private**，那么谁也无法创建该类的对象了。但是现在别人该怎么使用这个类呢？上述例子给出了两个选择。在 **Soup1** 中，有一个 **static** 方法，它的作用是创建一个新的 **Soup1** 对象并返回对象的引用。如果想要在返回引用之前在 **Soup1** 上做一些额外操作，或是记录创建了多少个 **Soup1** 对象（可以用来限制数量），这种做法是有用的。

**Soup2** 用到了所谓的*设计模式*（design pattern）。这种模式叫做*单例模式*（singleton），因为它只允许创建类的一个对象。**Soup2** 类的对象是作为 **Soup2** 的 **static** **private** 成员而创建的，所以有且只有一个，你只能通过 **public** 修饰的 `access()` 方法访问到这个对象。

<!-- Summary -->

## 本章小结

无论在什么样的关系中，划定一些供各成员共同遵守的界限是很重要的。当你创建了一个类库，也就与该类库的使用者产生了联系，他们是类库的客户端程序员，需要使用你的类库创建应用或更大的类库。

没有规则，客户端程序员就可以对类的所有成员为所欲为，即使你希望他们不要操作部分成员。这种情况下，所有事物都是公开的。

本章讨论了类库是如何通过类构建的：首先，介绍了将一组类打包到类库的方式，其次介绍了类如何控制对其成员的访问。

据估计，用 C 语言开发项目，当代码量达到 5 万行和 10 万行时就会出现问题，因为 C 语言只有单一的命名空间，名称开始冲突造成额外的管理开销。在 Java 中，关键字 **package**，包命名模式和关键字 **import** 给了你对于名称的完全控制权，因此可以轻易地避免名称冲突的问题。

控制成员访问权限有两个原因。第一个原因是使用户不要接触他们不该接触的部分，这部分对于类内部来说是必要的，但是不属于客户端程序员所需接口的一部分。因此将方法和属性声明为 **private** 对于客户端程序员来说是一种服务，可以让他们清楚地看到什么是重要的，什么可以忽略。这可以简化他们对类的理解。

第二个也是最重要的原因是为了让类库设计者更改类内部的工作方式，而不用担心会影响到客户端程序员。比如最初以某种方式创建一个类，随后发现如果更改代码结构可以极大地提高运行速度。如果接口与实现被明确地隔离和保护，你可以实现这一目的，而不必强制客户端程序员重新编写代码。访问权限控制确保客户端程序员不会依赖某个类的底层实现的任何部分。

当你具备更改底层实现的能力时，不但可以自由地改善设计，还可能会随意地犯错。无论如何细心地计划和设计，都有可能犯错。当了解到犯错是相对安全的时候，你可以更加放心地实验，更快地学会，更快地完成项目。

类的 **public** 接口是用户真正看到的，所以在分析和设计阶段决定这部分接口是最重要的部分。尽管如此，你仍然有改变的空间。如果最初没有创建出正确的接口，可以添加更多的方法，只要你不删除那些客户端程序员已经在他们的代码中使用的东西。

注意到访问权限控制关注的是类库创建者和外部使用者之间的关系，这是一种交流方式。很多情况下，事实并非如此。例如，你自己编写了所有的代码，或者在一个小组中工作，所有的东西都放在同一个包下。这些情况下，交流方式则是另外一种，此时严格地遵循访问权限规则也许不是最佳选择，默认（包）访问权限也许就足够好了。

<!-- 分页 -->

<div style="page-break-after: always;"></div>

