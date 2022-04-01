[TOC]

<!-- Appendix: Javadoc -->
# 附录:文档注释

编写代码文档的最大问题可能是维护该文档。如果文档和代码是分开的，每次更改代码时都要很繁琐地再去更改文档。解决方案似乎很简单：将代码链接到文档。最简单的方法是将所有内容放在同一个文件中。然而，要完成这个任务，需要一个特殊的注释语法来标记文档，以及一个工具将这些注释提取为有用的形式，这就是Java所做的。

提取注释的工具称为Javadoc，它是 JDK 安装的一部分。它使用Java编译器中的一些技术来寻找特殊的注释标记。它不仅提取由这些标记所标记的信息，还提取与注释相邻的类名或方法名。通过这种方式，您就可以用最少的工作量来生成合适的程序文档。

Javadoc的输出是一个html文件，可以用web浏览器查看它。有了Javadoc，就有一个简单的标准来创建文档，因此你可以期望所有Java库都有文档。

此外，你可以编写自己的Javadoc处理程序doclet，对javadoc处理的信息做特殊的处理（例如以不同的格式生成输出）。

以下是对Javadoc基础知识的介绍和概述。在 JDK 文档中可以找到完整的描述。

## 句法规则

所有Javadoc指令都发生在以 `/**` 开头(但仍然以 `*/`)结尾)的注释中。使用Javadoc有两种主要方法：嵌入HTML或使用“doc标签”。独立的doc标签是以 **@** 开头并且放在注释行的开头的指令(注释行开头的`*`将被忽略)。内联的doc标签可以出现在Javadoc注释的任何位置，它也以 `@` 开头，但被花括号包围。

有三种类型的注释文档，它们对应于注释前面的元素:类、字段或方法。也就是说，类注释出现在类定义之前，字段注释出现在字段定义之前，方法注释出现在方法定义之前。举个简单的例子:

```java
// javadoc/Documentation1.java 
/** 一个类注释 */
public class Documentation1 {
    /** 一个属性注释 */
    public int i;
    /** 一个方法注释 */ 
    public void f() {}
}

```

Javadoc仅处理 **公共成员** 和 **继承访问权限成员** 的注释文档。 默认情况下，将忽略对 **私有成员** 和**包访问权限成员**的注释（请参阅["隐藏实现"](/docs/book/07-Implementation-Hiding.md)一章），并且你将看不到任何输出。 这是有道理的，因为从客户端程序员的视角看，在文件外部只有 **公共成员** 和 **继承访问权限成员** 是可用的。 你可以使用 **-private** 标志来包含 **私有成员**。

要通过Javadoc处理前面的代码，命令是：

```cmd
javadoc Documentation1.java
```

这将产生一组HTML文件。如果你在浏览器中打开index.html，将看到输出的结果与其他Java文档具有相同的标准格式，因此使用者对这种格式很熟悉，并可以轻松地浏览你的类。

## 内嵌 HTML

Javadoc不作修改地将HTML代码传递到生成的HTML文档。这使你可以充分利用HTML。但是这样做的主要目的是让你格式化代码，例如：

```java
// javadoc/Documentation2.java
/** <pre>
* System.out.println(new Date());
* </pre>
*/
public class Documentation2 {}
```

你也可以像在其他任何Web文档中一样使用HTML来格式化说明中的文字：

```java
// javadoc/Documentation3.java
/** You can <em>even</em> insert a list:
* <ol>
* <li> Item one
* <li> Item two
* <li> Item three
* </ol>
*/
public class Documentation3 {}
```

请注意，在文档注释中，Javadoc会删除行首的星号以及前导空格。 Javadoc重新格式化了所有内容，使其符合文档的标准外观。不要将`<h1>` 和`<hr>` 之类的标题用作嵌入式HTML，因为Javadoc会插入自己的标题，你插入的标题将对其产生干扰。

所有类型的注释文档（类，字段和方法）都可以支持嵌入式HTML。

## 示例标签

以下是一些可用于代码文档的Javadoc标记。在尝试使用Javadoc进行任何认真的操作之前，请查阅JDK文档中的Javadoc参考，以了解使用Javadoc的所有不同方法。

### @see

这个标签可以将其它的类链接到本文档中。Javadoc 用 `@see` 标签产生链接到其它类的的HTML。格式为：

```java
@see classname
@see fully-qualified-classname
@see fully-qualified-classname#method-name
```

每个都向生成的文档中添加超链接的“另请参阅”条目。 Javadoc 不会检查超链接的有效性。

### {@link package.class#member label}

和 @see 非常相似，不同之处在于它可以内联使用，并使用标签作为超链接文本，而不是“另请参阅”。

### {@docRoot}

生成文档根目录的相对路径。对于显式超链接到文档树中的页面很有用。

### {@inheritDoc}

将文档从此类的最近基类继承到当前文档注释中。

### @version

其形式为：

```java
@version version-information
```

其中 version-information 是你认为适合包含的任何重要信息。当在Javadoc命令行上放置 -version 标志时，特别在生成的HTML文档中用于生成version信息。

### @author

其形式为：

```
@author author-information
```

 author-information 大概率是你的名字，但是一样可以包含你的 email 地址或者其他合适的信息。当在 Javadoc 命令行上放置 -author 标志的时候，在生成的HTML文档中特别注明了作者信息。

你可以对作者列表使用多个作者标签，但是必须连续放置它们。所有作者信息都集中在生成的HTML中的单个段落中。

### @since

此标记指示此代码的版本开始使用特定功能。例如，它出现在HTML Java文档中，以指示功能首次出现的JDK版本。

### @param

这将生成有关方法参数的文档：

```java
@param parameter-name description
```

其中 `parameter-name` 是方法参数列表中的标识符， `description` 是可以在后续行中继续的文本。当遇到新的文档标签时，这个说明被视为结束。`@param`标签可以使用任意次，大概每个参数使用一次。

### @return

这记录了返回值：

```java
@return description
```

其中description给出了返回值的含义。它可延续到后面的行内。

### @throws

一个方法可以产生许多不同类型的异常，所有这些异常都需要描述。异常标记的形式为：

```java
@throws fully-qualified-class-name description
```

*fully-qualified-class-name* 给出异常类的确切名称，并且 description （可在后面的行内继续展开）告诉你为什么这个特定类型的异常会在方法调用后出现。

### @deprecated

表示已被改进的功能取代的功能。deprecated 标记建议你不要再使用此功能，因为将来它有可能被删除。使用标记为 `@deprecated` 的方法会使编译器发出警告。在Java 5中，`@deprecated`  Javadoc 标记被 `@Deprecated` *注解* 取代（在[注解]()一章中进行了描述）。

## 文档示例

**objects/HelloDate.java** 是带有文档注释的例子。

```java
// javadoc/HelloDateDoc.java
import java.util.*;
/** The first On Java 8 example program.
 * Displays a String and today's date.
 * @author Bruce Eckel
 * @author www.MindviewInc.com
 * @version 5.0
 */
public class HelloDateDoc {
    /** Entry point to class & application.
     * @param args array of String arguments
     * @throws exceptions No exceptions thrown
     */
    public static void main(String[] args) {
        System.out.println("Hello, it's: ");
        System.out.println(new Date());
    }
}
/* Output:
Hello, it's:
Tue May 09 06:07:27 MDT 2017
*/
```

你可以在Java标准库的源代码中找到许多Javadoc注释文档的示例。



<!-- 分页 -->

<div style="page-break-after: always;"></div>
