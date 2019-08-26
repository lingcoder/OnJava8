[TOC]

<!-- Validating Your Code -->
# 第十六章 代码校验
### 你永远不能保证你的代码是正确的，你只能证明它是错的。

让我们先暂停学习编程语言的知识，看看一些代码基础知识。特别是能让你的代码更加健壮的。



## 测试

### 如果没有测试过，它就是不能工作的。

Java是一个静态类型的语言，程序员经常对一种编程语言明显的安全性过于感到舒适，“能通过编译器，那就是没问题的”。但静态类型检查是一种非常局限性的测试。这说明编译器接受你的语法和基本类型规则，但不意味着你的代码满足程序安全的目标。随着你代码经验的丰富，你逐渐了解到你的代码从来没有满足过安全性这个目标。迈向代码的第一步就是创建代码测试，争对你的目标检查代码行为。

### 单元测试

这个过程是将集成测试构建到你创建的所有代码中，并在当你每次构建你的系统时运行这些测试。这样，构建过程检查不仅仅是检查语法的错误，同时你也教它检查语义错误。

“单元”指的是测试代码中的一小部分的想法。通常，每个类都有测试检查它所有方法的行为。“系统”测试则是不同的，它检查完成的程序是否满足要求。

C风格的语言，特别是C++，通常会认为性能比安全更重要。用Java编程比C++（一般认为大概快两倍）快的原因是Java的安全性网络：这种特征类似于垃圾回收以及键入检查。通过将单元测试集成到构建过程中，你扩大了这个安全网，从而导致了更快的开发效率。当你发现设计或实现的缺陷时，可以更容易、更大胆重构你的代码，并更快地生成更好的产品。

当我意识到，要保证书中代码的正确性时，我自己的测试经历就开始了。这本书通过Gradle构建系统， 你需要安装JDK，你可以通过输入gradlew compileJava编译本书的所有代码。自动提取和自动编译的效果对本书代码的质量是如此的直接和引人注目（在我看来）任何编程书籍的必备条件——你怎么能相信你没有编译的代码呢? 并且我发现我可以利用搜索和替换在整本书大范围的修改。如果我引入了一个错误，代码提取器和构建系统就会清除它。随着程序越来越复杂，我在我的系统发现了一个严重的漏洞. 编译程序是毫无疑问的第一步， 对于一本要出版的书而言，这看来是相当具有革命意义的发现（由于出版压力， 你经常打开一本程序设计的书并且发现了上面代码的缺陷）。然而，我收到了来自读者反馈的语法问题。我在实现一个自动化执行测试系统的时候，使用了在早期能看到效果的步骤，但这是迫于出版压力，与此同时我明白我的程序绝对有问题，这些都会变成bug让我自食其果。我也经常收到读者的抱怨说我没有显示足够的代码输出。我需要验证程序的输出，并且在书中显示验证的输出。我以前的意见是读者应该一边看书一边运行代码，许多读者就是这么做的并且从中受益。然而，这种态度背后的原因是，我无法保证证明书中所示的输出是正确的。从经验来看，我知道随着时间的推移，会发生一些事情，使得输出不再正确（或者，我一开始就没有把它弄对）。为了解决这个问题，我利用Python创建了一个工具（你将在下载的示例中找到此工具）。本书中的大多数程序都产生控制台输出，该工具将该输出与源代码清单末尾的注释中显示的预期输出进行比较，所以读者可以看到预期的输出，并且知道这个输出已经被构建程序验证的。

### JUnit

最初的Junit发布于2000年，大概是基于Java 1.0，因此不能使用Java的反射工具。因此，用旧的JUnit编写单元测试是一项相当繁忙和冗长的工作。我发现这个设计令人不爽，并编写了自己的单元测试框架作为注解一章的示例。这个框架走向了另一个极端，“尝试最简单可行的方法”（极限编程中的一个关键短语）。从那之后，Junit通过反射和注解得到了极大的改进，这大大简化了编写单元测试代码的过程。在Java8中，他们甚至增加了对lambdas表达式的支持。本书使用当时最新的Junit5版本

在JUnit最简单的使用中，使用**@Test**注解标记表示测试的每个方法。JUnit将这些方法标识为单独的测试，并一次设置和运行一个测试，采取措施避免测试之间的副作用。

让我们尝试一个简单的例子。**CountedLis**t继承**ArrayList**，添加信息来追踪有多少个**CountedLists**被创建：

```java
// validating/CountedList.java
// Keeps track of how many of itself are created.
package validating;
import java.util.*;	
public class CountedList extends ArrayList<String> {
    private static int counter = 0;
    private int id = counter++;
    public CountedList() {
    System.out.println("CountedList #" + id);
    }
	public int getId() { return id; }
}
```

标准实例是将测试放在它们自己的子目录中。测试还必须放在包中，以便JUnit能够发现它们:

```java
// validating/tests/CountedListTest.java
// Simple use of JUnit to test CountedList.
package validating;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
public class CountedListTest {
private CountedList list;
	@BeforeAll
    static void beforeAllMsg() {
    System.out.println(">>> Starting CountedListTest");
    }
    
    @AfterAll
    static void afterAllMsg() {
    System.out.println(">>> Finished CountedListTest");
    }
    
    @BeforeEach
    public void initialize() {
    	list = new CountedList();
    	System.out.println("Set up for " + list.getId());
        for(int i = 0; i < 3; i++)
        list.add(Integer.toString(i));
    }
    
    @AfterEach
    public void cleanup() {
    	System.out.println("Cleaning up " + list.getId());
    }
    
    @Test
    public void insert() {
        System.out.println("Running testInsert()");
        assertEquals(list.size(), 3);
        list.add(1, "Insert");
        assertEquals(list.size(), 4);
        assertEquals(list.get(1), "Insert");
    }
    
    @Test
    public void replace() {
    	System.out.println("Running testReplace()");
    	assertEquals(list.size(), 3);
    	list.set(1, "Replace");
   		assertEquals(list.size(), 3);
    	assertEquals(list.get(1), "Replace");
    }
    	
    // A helper method to simplify the code. As
    // long as it's not annotated with @Test, it will
    // not be automatically executed by JUnit.
    private void compare(List<String> lst, String[] strs) {
        assertArrayEquals(lst.toArray(new String[0]), strs);
    }
    
    @Test
    public void order() {
    System.out.println("Running testOrder()");
    compare(list, new String[] { "0", "1", "2" });
    }
    
    @Test
    public void remove() {
    	System.out.println("Running testRemove()");
    	assertEquals(list.size(), 3);
    	list.remove(1);
    	assertEquals(list.size(), 2);
    	compare(list, new String[] { "0", "2" });
    }
    
    @Test
    public void addAll() {
    	System.out.println("Running testAddAll()");
    	list.addAll(Arrays.asList(new String[] {
    	"An", "African", "Swallow"}));
    	assertEquals(list.size(), 6);
    	compare(list, new String[] { "0", "1", "2",
    	"An", "African", "Swallow" });
    }
}

/* Output:
>>> Starting CountedListTest
CountedList #0
Set up for 0
Running testRemove()
Cleaning up 0
CountedList #1
Set up for 1
Running testReplace()
Cleaning up 1
CountedList #2
Set up for 2
Running testAddAll()
Cleaning up 2
CountedList #3
Set up for 3
Running testInsert()
Cleaning up 3
CountedList #4
Set up for 4
Running testOrder()
Cleaning up 4
>>> Finished CountedListTest
*/
```

**@BeforeAll**注解是在任何其他测试操作之前运行一次的方法。**@AfterAll**是所有其他测试操作之后只运行一次的方法。两个方法都必须是静态的。

**@BeforeEach**注解是通常用于创建和初始化公共对象的方法，并在每次测试前运行。或者，您可以将所有这样的初始化放在test类的构造函数中，尽管我认为**@BeforeEach**更加清晰。JUnit为每个测试创建一个对象，以确保测试运行之间没有副作用。然而，所有测试的所有对象都是同时创建的(而不是在测试之前创建对象)，所以使用**@BeforeEach**和构造函数之间的唯一区别是**@BeforeEach**在测试前直接调用。在大多数情况下，这不是问题，如果您愿意，可以使用构造函数方法。

如果您必须在每次测试后执行清理（如果修改了需要恢复的静态文件，打开文件需要关闭，打开数据库或者网络连接，etc），那就用注解**@AfterEach**.

每个测试创建一个新的**CountedListTest**对象，任何非静态成员变量也会在同一时间创建。然后为每个测试调用**initialize()**，于是list被分配了一个新的**CountedList**对象，然后用**String“0”、“1”**和**“2”**初始化。观察**@BeforeEach**和**@AfterEach**的行为，这些方法在初始化和清理测试时显示有关测试的信息。

**insert()**和**replace()**演示了典型的测试方法。JUnit使用**@Test**注解发现这些方法，并将每个方法作为测试运行。在方法内部，您可以执行任何所需的操作并使用 JUnit 断言方法（已"assert"开头）验证测试的正确性（更全面的"assert"说明可以在Junit文档里找到）。如果断言失败，将显示导致失败的表达式和值。这通常就足够了，但是你也可以使用每个JUnit断言语句的重载版本，它包含一个字符串，以便在断言失败时显示。

断言语句不是必须的；你可以在没有断言的情况下运行测试，如果没有异常，则认为测试是成功的。

**compare()**是“helper方法”的一个例子，它不是由JUnit执行的，而是被类中的其他测试使用。只要没有**@Test**注解，Junit就不会运行它，也不需要特定的签名。在这，**compare()**是**private**，表示在测试类使用，但他同样可以是**public**。其余的测试方法通过将其重构为compare()方法来消除重复的代码。

本书使用**build.gradle**控制测试，运行本章节的测试，命令：

**gradlew validating:test**

Gradle不运行已经运行过的测试，所以如果你没有得到测试结果，先运行:

**gradlew validating:clean**

你可以用这个命令运行本书的所有测试：

**gradlew test**

尽管你可以用最简单的方法，如CountedListTest.java所示

JUnit包含许多额外的测试业务，您可以在其上了解这些结构 

[junit.org]: junit.org.

Junit是Java最流行的单元测试框架，但也有其它可以替代的。你可以通过互联网发现更适合你的那一个。

### 测试覆盖率的幻觉

测试覆盖率，同样也称为代码覆盖率，度量代码的测试百分比。百分比越高，测试的覆盖率越大。这里有很多[方法](https://en.wikipedia.org/wiki/Code_coverage)

计算覆盖率，还有有帮助的文章[Java代码覆盖工具](https://en.wikipedia.org/wiki/Java_Code_Coverage_Tools)

对于没有知识但处于控制地位的人来说，很容易在没有任何了解的情况下也有概念认为100%的测试覆盖是唯一可接受的值。这是一个问题，因为100%并不意味着是对测试有效性的最佳测量。您可以测试所有需要它的东西，但是只需要65%的覆盖率。如果需要100%的覆盖，您将浪费大量时间来生成剩余的代码，并且在向项目添加代码时浪费的时间更多。

当您分析一个未知的代码库时，测试覆盖率作为一个粗略的度量是有用的。如果覆盖率工具报告的值特别低（比如，少于百分之40），则说明覆盖不够充分。然而，一个非常高的值也同样值得怀疑，这表明对编程领域了解不足的人迫使团队做出了武断的决定。覆盖工具的最佳用途是发现代码库中未测试的部分。但是，不要依赖覆盖率来告诉你关于测试质量的任何信息。

<!-- Preconditions -->

## 前提条件

<!-- Test-Driven Development -->

## 测试驱动开发

<!-- Logging -->

## 日志

<!-- Debugging -->

## 调试

<!-- Benchmarking -->

## 基准测试

<!-- Profiling and Optimizing -->

## 分析和优化

<!-- Style Checking -->

## 风格检测

<!-- Static Error Analysis -->

## 静态错误分析

<!-- Code Reviews -->

## 代码重审

<!-- Pair Programming -->

## 结对编程

<!-- Refactoring -->

## 重构

<!-- Continuous Integration -->

## 持续集成

<!-- Summary -->

## 本章小结

<!-- 分页 -->
