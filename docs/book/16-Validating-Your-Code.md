[TOC]

<!-- Validating Your Code -->

# 第十六章 代码校验

### 你永远不能保证你的代码是正确的，你只能证明它是错的。

让我们先暂停学习编程语言的知识，看看一些代码基础知识。特别是能让你的代码更加健壮的。



## 测试

### 如果没有测试过，它就是不能工作的。

Java是一个静态类型的语言，程序员经常对一种编程语言明显的安全性过于感到舒适，“能通过编译器，那就是没问题的”。但静态类型检查是一种非常局限性的测试。这说明编译器接受你的语法和基本类型规则，但不意味着你的代码满足程序安全的目标。随着你代码经验的丰富，你逐渐了解到你的代码从来没有满足过安全性这个目标。迈向代码的第一步就是创建代码测试，争对你的目标检查代码行为。

#### 单元测试

这个过程是将集成测试构建到你创建的所有代码中，并在当你每次构建你的系统时运行这些测试。这样，构建过程检查不仅仅是检查语法的错误，同时你也教它检查语义错误。

“单元”指的是测试代码中的一小部分的想法。通常，每个类都有测试检查它所有方法的行为。“系统”测试则是不同的，它检查程序是否满足要求。

C风格的语言，特别是C++，通常会认为性能比安全更重要。用Java编程比C++（一般认为大概快两倍）快的原因是Java的安全性网络：这种特征类似于垃圾回收以及键入检查。通过将单元测试集成到构建过程中，你扩大了这个安全网，有了更快的开发效率。当发现设计或实现的缺陷时，可以更容易、更大胆重构你的代码。

当我意识到，要确保书中代码的正确性时，我自己的测试经历就开始了。这本书通过Gradle构建系统， 你需要安装JDK，你可以通过输入gradlew compileJava编译本书的所有代码。自动提取和自动编译的效果对本书代码的质量是如此的直接和引人注目（在我看来）任何编程书籍的必备条件——你怎么能相信你没有编译的代码呢? 我发现可以利用搜索和替换在整本书大范围的修改。如果引入了一个错误，代码提取器和构建系统就会清除它。随着程序越来越复杂，我在系统发现了一个严重的漏洞。 编译程序是毫无疑问的第一步， 对于一本要出版的书而言，这看来是相当具有革命意义的发现（由于出版压力， 你经常打开一本程序设计的书并且发现了上面代码的缺陷）。尽管，我收到了来自读者反馈的语法问题。我在实现一个自动化执行测试系统的时候，使用了在早期能看到效果的步骤，但这是迫于出版压力，与此同时我明白我的程序绝对有问题，这些都会变成bug让我自食其果。我也经常收到读者的抱怨说我没有显示足够的代码输出。我需要验证程序的输出，并且在书中显示验证的输出。我以前的意见是读者应该一边看书一边运行代码，许多读者就是这么做的并且从中受益。然而，这种态度背后的原因是，我无法保证书中的输出是正确的。从经验来看，我知道随着时间的推移，会发生一些事情，使得输出不再正确（或者，一开始就没有把它弄对）。为了解决这个问题，我利用Python创建了一个工具（你将在下载的示例中找到此工具）。本书中的大多数程序都产生控制台输出，该工具将该输出与源代码清单末尾的注释中显示的预期输出进行比较，所以读者可以看到预期的输出，并且知道这个输出已经被构建程序验证的。

#### JUnit

最初的Junit发布于2000年，大概是基于Java 1.0，因此不能使用Java的反射工具。因此，用旧的JUnit编写单元测试是一项相当繁忙和冗长的工作。我发现这个设计令人不爽，并编写了自己的单元测试框架作为注解一章的示例。这个框架走向了另一个极端，“尝试最简单可行的方法”（极限编程中的一个关键短语）。从那之后，Junit通过反射和注解得到了极大的改进，这大大简化了编写单元测试代码的过程。在Java8中，他们甚至增加了对lambdas表达式的支持。本书使用当时最新的Junit5版本

在JUnit最简单的使用中，使用 **@Test** 注解标记表示测试的每个方法。JUnit将这些方法标识为单独的测试，并一次设置和运行一个测试，采取措施避免测试之间的副作用。

让我们尝试一个简单的例子。**CountedList** 继承 **ArrayList** ，添加信息来追踪有多少个**CountedLists**被创建：

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

**@BeforeAll** 注解是在任何其他测试操作之前运行一次的方法。 **@AfterAll** 是所有其他测试操作之后只运行一次的方法。两个方法都必须是静态的。

**@BeforeEach**注解是通常用于创建和初始化公共对象的方法，并在每次测试前运行。可以将所有这样的初始化放在test类的构造函数中，尽管我认为 **@BeforeEach** 更加清晰。JUnit为每个测试创建一个对象，确保测试运行之间没有副作用。然而，所有测试的所有对象都是同时创建的(而不是在测试之前创建对象)，所以使用 **@BeforeEach** 和构造函数之间的唯一区别是 **@BeforeEach** 在测试前直接调用。在大多数情况下，这不是问题，如果你愿意，可以使用构造函数方法。

如果你必须在每次测试后执行清理（如果修改了需要恢复的静态文件，打开文件需要关闭，打开数据库或者网络连接，etc），那就用注解 **@AfterEach**.

每个测试创建一个新的 **CountedListTest** 对象，任何非静态成员变量也会在同一时间创建。然后为每个测试调用 **initialize()** ，于是list被分配了一个新的 **CountedList** 对象，然后用 **String“0”、“1”** 和 **“2”** 初始化。观察 **@BeforeEach** 和 **@AfterEach** 的行为，这些方法在初始化和清理测试时显示有关测试的信息。

**insert()** 和 **replace()** 演示了典型的测试方法。JUnit使用 **@Test** 注解发现这些方法，并将每个方法作为测试运行。在方法内部，你可以执行任何所需的操作并使用 JUnit 断言方法（已"assert"开头）验证测试的正确性（更全面的"assert"说明可以在Junit文档里找到）。如果断言失败，将显示导致失败的表达式和值。这通常就足够了，但是你也可以使用每个JUnit断言语句的重载版本，它包含一个字符串，以便在断言失败时显示。

断言语句不是必须的；你可以在没有断言的情况下运行测试，如果没有异常，则认为测试是成功的。

**compare()** 是“helper方法”的一个例子，它不是由JUnit执行的，而是被类中的其他测试使用。只要没有**@Test** 注解，Junit就不会运行它，也不需要特定的签名。在这， **compare()** 是 **private** ，表示在测试类使用，但他同样可以是 **public** 。其余的测试方法通过将其重构为 **compare()** 方法来消除重复的代码。

本书使用**build.gradle**控制测试，运行本章节的测试，命令：

**gradlew validating:test**

Gradle不会运行已经运行过的测试，所以如果你没有得到测试结果，先运行:

**gradlew validating:clean**

可以用这个命令运行本书的所有测试：

**gradlew test**

尽管可以用最简单的方法，如CountedListTest.java所示

JUnit包含许多额外的测试业务，你可以在其上了解这些结构 

[junit.org]: junit.org.

Junit是Java最流行的单元测试框架，但也有其它可以替代的。你可以通过互联网发现更适合的那一个。

#### 测试覆盖率的幻觉

测试覆盖率，同样也称为代码覆盖率，度量代码的测试百分比。百分比越高，测试的覆盖率越大。这里有很多[方法](https://en.wikipedia.org/wiki/Code_coverage)

计算覆盖率，还有有帮助的文章[Java代码覆盖工具](https://en.wikipedia.org/wiki/Java_Code_Coverage_Tools)

对于没有知识但处于控制地位的人来说，很容易在没有任何了解的情况下也有概念认为100%的测试覆盖是唯一可接受的值。这有一个问题，因为100%并不意味着是对测试有效性的最佳测量。你可以测试所有需要它的东西，但是只需要65%的覆盖率。如果需要100%的覆盖，你将浪费大量时间来生成剩余的代码，并且在向项目添加代码时浪费的时间更多。

当分析一个未知的代码库时，测试覆盖率作为一个粗略的度量是有用的。如果覆盖率工具报告的值特别低（比如，少于百分之40），则说明覆盖不够充分。然而，一个非常高的值也同样值得怀疑，这表明对编程领域了解不足的人迫使团队做出了武断的决定。覆盖工具的最佳用途是发现代码库中未测试的部分。但是，不要依赖覆盖率来得到测试质量的任何信息。

<!-- Preconditions -->

## 前置条件

前置条件的概念来自于契约式设计(**Design By Contract, DbC**), 利用断言机制实现。我们从Java的断言机制开始来介绍DBC，最后使用谷歌Guava库作为前置条件。

#### 断言（Assertions）

断言通过验证在程序执行期间满足某些条件而增加了程序的健壮性。举例，假设在一个对象中有一个数值字段，它表示日历上的月份。这个数字总是介于1-12之间。通过断言检查，如果超出了该范围，则报告错误。如果在方法的内部，则可以使用断言检查参数的有效性。这些是确保程序正确的重要测试，但是它们不能在编译时被检查，并且它们不属于单元测试的范围。

#### Java断言语法

你可以通过其它程序设计架构来模拟断言的效果，因此，在Java中包含断言的意义在于它们易于编写。断言语句有两种形式 : 

assert boolean-expression；

assert boolean-expression: information-expression;

两者似乎告诉我们 **“我断言这个布尔表达式会产生一个真正的值”**， 否则，将抛出**AssertionError**异常。

这是**Throwable**的派生类，因此不需要异常规范。

不幸的是，第一种断言形式的异常不会生成包含布尔表达式的任何信息（与大多数其他语言的断言机制相反）。

下面是第一种形式的例子：

```java
// validating/Assert1.java

// Non-informative style of assert
// Must run using -ea flag:
// {java -ea Assert1}
// {ThrowsException}
public class Assert1 {
    public static void main(String[] args) {
    assert false;
    }
}

/* Output:
___[ Error Output ]___
Exception in thread "main" java.lang.AssertionError
at Assert1.main(Assert1.java:9)
*/
```

如果你正常运行程序，没有任何特殊的断言标志，则不会发生任何事情。你需要在运行程序时显式启用断言。一种简单的方法是使用 **-ea** flag， 它也可以表示为: **-enableassertion** ， 这将运行程序并执行任何断言语句。

输出中并没有包含多少有用的信息。另一方面，如果你使用 **information-expression** ， 将生成一条有用的消息作为异常堆栈跟踪的一部分。最有用的 **information-expression** 通常是一串针对程序员的文本:

```java
// validating/Assert2.java
// Assert with an information-expression
// {java Assert2 -ea}
// {ThrowsException}

public class Assert2 {
    public static void main(String[] args) {
    assert false:
    "Here's a message saying what happened";
    }
}
/* Output:
___[ Error Output ]___
Exception in thread "main" java.lang.AssertionError:
Here's a message saying what happened
at Assert2.main(Assert2.java:8)
*/
```

**information-expression** 可以产生任何类型的对象，因此，通常将构造一个包含对象值的更复杂的字符串，它是否与失败的断言有关。

还可以通过类名或包名打开或关闭断言；也就是说，你可以为整个包启用或禁用断言。实现这一点的详细信息在JDK的断言文档中。你想要打开或关闭某些断言时，此特性对于使用断言进行工具化的大型项目非常有用。但是，日志记录（*Logging*）或者调试（*Debugging*）,可能是捕获这类信息的更好工具。

这有另一种办法控制你的断言：编程方式，通过链接到类加载器对象（**ClassLoader**）。类加载器中有几种方法允许动态启用和禁用断言，其中 **setDefaultAssertionStatus ()** ,它为之后加载的所有类设置断言状态。因此，你可以认为你像下面这样悄悄地开启了断言：

```java
// validating/LoaderAssertions.java
// Using the class loader to enable assertions
// {ThrowsException}
public class LoaderAssertions {
public static void main(String[] args) {

	ClassLoader.getSystemClassLoader().
        setDefaultAssertionStatus(true);
		new Loaded().go();
	}
}

class Loaded {
    public void go() {
    assert false: "Loaded.go()";
    }
}
/* Output:
___[ Error Output ]___
Exception in thread "main" java.lang.AssertionError:
Loaded.go()
at Loaded.go(LoaderAssertions.java:15)
at
LoaderAssertions.main(LoaderAssertions.java:9)
*/
```

这消除了在运行程序时在命令行上使用 **-ea** 标志的需要，使用 **-ea** 标志启用断言可能同样简单。当交付独立产品时，可能必须设置一个执行脚本让用户能够启动程序，配置其他启动参数。这是有道理的，然而，决定在程序运行时启用断言可以使用下面的 **static** 块来实现这一点，该语句位于系统的主类中：

```java
static {
    boolean assertionsEnabled = false;
    // Note intentional side effect of assignment:
    assert assertionsEnabled = true;
    if(!assertionsEnabled)
    throw new RuntimeException("Assertions disabled");
}
```



如果启用断言，然后执行 **assert** 语句，**assertionsEnabled** 变为 **true** 。断言不会失败，因为分配的返回值是赋值的值。如果不启用断言，**assert** 语句不执行，**assertionsEnabled** 保持false，将导致异常。



#### Guava断言

因为启用Java本地断言很麻烦，Guava团队添加一个始终启用替换断言的 **Verify** 类。他们建议静态导入 **Verify** 方法：

```java
// validating/GuavaAssertions.java
// Assertions that are always enabled.

import com.google.common.base.*;
import static com.google.common.base.Verify.*;
public class GuavaAssertions {
    public static void main(String[] args) {
    	verify(2 + 2 == 4);
    	try {
    		verify(1 + 2 == 4);
   	 	} catch(VerifyException e) {
    		System.out.println(e);
    	}
        
		try {
			verify(1 + 2 == 4, "Bad math");
		} catch(VerifyException e) {
			System.out.println(e.getMessage());
		}
        
		try {
			verify(1 + 2 == 4, "Bad math: %s", "not 4");
		} catch(VerifyException e) {
        	System.out.println(e.getMessage());
        }
        
        String s = "";
        s = verifyNotNull(s);
        s = null;
        try {
            verifyNotNull(s);
        } catch(VerifyException e) {
        	System.out.println(e.getMessage());
        }
        
        try {
        	verifyNotNull(
        		s, "Shouldn't be null: %s", "arg s");
        } catch(VerifyException e) {
        	System.out.println(e.getMessage());
        }
	}
}
/* Output:
com.google.common.base.VerifyException
Bad math
Bad math: not 4
expected a non-null reference
Shouldn't be null: arg s
*/
```



这里有两个方法，使用变量 **verify()** 和 **verifyNotNull()** 来支持有用的错误消息。注意，**verifyNotNull()** 内置的错误消息通常就足够了，而 **verify()** 太一般，没有有用的默认错误消息。



#### 使用断言进行契约式设计

*契约式设计(DbC)*是Bertrand Meyer提出的一个概念，Eiffel语言的发明者，通过确保对象遵循某些规则来帮助创建健壮的程序。这些规则是由正在解决的问题的性质决定的，这超出了编译器可以验证的范围。虽然断言没有直接实现 **DBC**（Eiffel也是如此），但是它们创建了一种非正式的DBC编程风格。DbC假定服务供应商与该服务的消费者或客户之间存在明确指定的契约。在面向对象编程中，服务通常由对象提供，对象的边界 — 供应商和消费者之间的划分 — 是对象类的接口。当客户端调用特定的公共方法时，它们希望该调用具有特定的行为：对象状态改变，以及一个可预测的返回值。

**Meyer**认为：

1.应该明确指定行为，就好像它是一个契约一样。

2.通过实现某些运行时检查来保证这种行为，他将这些检查称为前置条件、后置条件和不变项。

不管你是否同意，第一条总是对的，在大多数情况下，DbC确实是一种有用的方法。（我认为，与任何解决方案一样，它的有用性也有界限。但如果你知道这些界限，你就知道什么时候去尝试。）尤其是，设计过程中一个有价值的部分是特定类DbC约束的表达式；如果无法指定约束，则可能对要构建的内容了解得不够。

#### 检查指令

详细研究DbC之前，思考最简单使用断言的办法，**Meyer**称它为检查指令。检查指令说明你确信代码中的某个特定属性此时已经得到满足。检查指令的思想是在代码中表达非明显性的结论，而不仅仅是为了验证测试，也同样为了将来能够满足阅读者而有一个文档。

在化学领域，你也许会用一种纯液体去滴定测量另一种液体，当达到一个特定的点时，液体变蓝了。从两个液体的颜色上并不能明显看出；这是作为其中复杂的一部分。滴定完成后一个有用的检查指令是能够断定液体变蓝了。

检查指令是对你的代码进行补充，当你可以测试并阐明对象或程序的状态时，应该使用它。

#### 前置条件

前置条件确保客户端(调用此方法的代码)履行其部分契约。这意味着在方法调用开始时几乎总是会检查参数（在你用那个方法做任何操作之前）以此保证它们的调用在方法中是合适的。因为你永远无法知道客户端会传递给你什么，前置条件是确保检查的一个好做法。

#### 后置条件

后置条件测试你在方法中所做的操作的结果。这段代码放在方法调用的末尾，在**return**语句之前(如果有的话)。对于长时间、复杂的方法，在返回计算结果之前需要对计算结果进行验证（也就是说，在某些情况下，由于某种原因，你不能总是相信结果)，后置条件很重要，但是任何时候你可以描述方法结果上的约束时，最好将这些约束在代码中表示为后置条件。

#### 不变性

不变性保证了必须在方法调用之间维护的对象的状态。但是，它并不会阻止方法在执行过程中暂时偏离这些保证，它只是在说对象的状态信息应该总是遵守状态规则：

**1**. 在进入该方法时。

**2**.  在离开方法之前。

此外，不变性是关于构造后对象状态的保证。

根据这个描述，一个有效的不变性被定义为一个方法，可能被命名为 **invariant()** ，它在构造之后以及每个方法的开始和结束时调用。方法可调用如下：

assert invariant();

这样，如果出于性能原因禁用断言，就不会产生开销。

#### 放松DBC检查 或  非严格的DBC

尽管他强调了前置条件、后置条件和不变性的价值所在，以及在开发过程中使用它们的重要性，Meyer承认在一个产品中包含所有DbC代码并不总是实用的。你可以放松DbC检查，它基于在特定的地方，你可以对代码的信任程度。以下是放松检查的顺序，最安全到最不安全：

**1**. 不变性检查在每个方法一开始的时候是不能进行的，因为在每个方法结束的时候进行不变性检查能保证一开始的时候对象处于有效状态。也就是说，通常情况下，你可以相信对象的状态不会在方法调用之间发生变化。这是一个非常安全的假设，你可以只在代码末尾使用不变性检查来编写代码。

**2**. 接下来禁用后置条件检查，当你进行合理的单元测试以验证方法是否返回了适当的值时。因为不变性检查是观察对象的状态，后置条件检查仅在方法期间验证计算结果，因此可能会被丢弃，以便进行单元测试。单元测试不会像运行时后置条件检查那样安全，但是它可能已经足够了，特别是当对自己的代码有信心时。

**3**. 如果你确信方法主体没有把对象改成无效状态，则可以禁用方法调用末尾的不变性检查。可以通过白盒单元测试(通过访问私有字段的单元测试来验证对象状态)来验证这一点。尽管它可能没有调用 **invariant()** 那么稳妥，可以将不变性检查从运行时测试 “迁移” 到构建时测试(通过单元测试)，就像使用后置条件一样。

**4**. 禁用前置条件检查，但除非这是万不得已的情况下。因为这是最不安全、最不明智的选择，因为尽管你知道并且可以控制自己的代码，但是你无法控制客户端可能会传递给方法的参数。**（A）** 在迫切需要性能和概要分析的情况下，将前置条件检查作为瓶颈，**(B)** 并且你有某种合理的保证，即客户端不会违反前置条件(如你自己编写客户端代码的情况)。禁用前置条件检查是可以接受的。

不应该直接删除检查的代码，因为只需要禁用检查(添加注释)。这样如果发现错误，就可以轻松地恢复检查以快速发现问题。

#### DBC + 单元测试

下面的例子演示了将契约式设计中的概念与单元测试相结合的有效性。它显示了一个简单的先进先出(FIFO)队列，该队列实现为一个“循环”数组，即以循环方式使用的数组。当到达数组的末尾时，将绕回到开头。

我们可以对这个队列做一些契约定义:

**1**.   前置条件(用于put())：不允许将空元素添加到队列中。

**2**.   前置条件(用于put())：将元素放入完整队列是非法的。

**3**.   前置条件(用于get())：试图从空队列中获取元素是非法的。

**4**.   后置条件用于get())：不能从数组中生成空元素。

**5**.   不变性：包含对象的区域不能包含任何空元素。

**6**.   不变性：不包含对象的区域必须只有空值。

下面是实现这些规则的一种方法，为每个DbC元素类型使用显式方法调用。首先，我们创建一个专用的

##### Exception:

- ```java
  // validating/CircularQueueException.java
  package validating;
  public class CircularQueueException extends RuntimeException {
          public CircularQueueException(String why) {
          super(why);
      }
  }
  
  This is used to report errors with the CircularQueue class:
  // validating/CircularQueue.java
  // Demonstration of Design by Contract (DbC)
  package validating;
  import java.util.*;
  public class CircularQueue {
      private Object[] data;
      private int in = 0, // Next available storage space 
      out = 0; // Next gettable object
      // Has it wrapped around the circular queue?
      private boolean wrapped = false;
      public CircularQueue(int size) {
      data = new Object[size];
      // Must be true after construction:
      assert invariant();
  	}
      
      public boolean empty() {
      	return !wrapped && in == out;
      }
      
      public boolean full() {
      	return wrapped && in == out;
      }
      
  	public boolean isWrapped() { return wrapped; }
      
      public void put(Object item) {
      	precondition(item != null, "put() null item");
      	precondition(!full(),
      	"put() into full CircularQueue");
      	assert invariant();
      	data[in++] = item;
      	if(in >= data.length) {
              in = 0;
              wrapped = true;
      	}
  		assert invariant();
  	}
      
      public Object get() {
      	precondition(!empty(),
      	"get() from empty CircularQueue");
      	assert invariant();
      	Object returnVal = data[out];
      	data[out] = null;
      	out++;
          if(out >= data.length) {
              out = 0;
              wrapped = false;
          }
          assert postcondition(
          returnVal != null,
          "Null item in CircularQueue");
          assert invariant();
          return returnVal;
  }
      
  	// Design-by-contract support methods:
      private static void precondition(boolean cond, String msg) {
          if(!cond) throw new CircularQueueException(msg);
      }
      
      private static boolean postcondition(boolean cond, String msg) {
      	if(!cond) throw new CircularQueueException(msg);
      	return true;
      }
      
      private boolean invariant() {
      // Guarantee that no null values are in the
      // region of 'data' that holds objects:
      for(int i = out; i != in; i = (i + 1) % data.length)
      if(data[i] == null)
      throw new CircularQueueException(
      "null in CircularQueue");
      // Guarantee that only null values are outside the
      // region of 'data' that holds objects:
      if(full()) return true;
      for(int i = in; i != out; i = (i + 1) % data.length)
      if(data[i] != null)
      throw new CircularQueueException(
      "non-null outside of CircularQueue range: "
          dump());
        return true;
        }
      
        public String dump() {
        return "in = " + in +
        ", out = " + out +
        ", full() = " + full() +
        ", empty() = " + empty() +
        ", CircularQueue = " + Arrays.asList(data);
        }
  }
  ```

  **in** 计数器指示数组中下一个对象所在的位置。**out** 计数器指示下一个对象来自何处。**wrapped** 的flag表示 **in** 已经“绕着圆圈”走了，现在从后面出来了。当**in**和 **out** 重合时，队列为空(如果包装为 **false** )或满(如果 **wrapped** 为 **true** )。

  **put()** 和 **get()** 方法调用 **precondition()** ，**postcondition()**, 和 **invariant**()，这些都是在类中定义的私有方法。前置**precondition()** 和 **postcondition()** 是用来阐明代码的辅助方法。

  

  注意，**precondition()** 返回 **void** , 因为它不与断言一起使用。按照之前所说的，通常你会在代码中保留前置条件。通过将它们封装在 **precondition()**  方法调用中，如果你不得不做出关掉它们的可怕举动，你会有更好的选择。

  

  **postcondition()** 和 **constant()** 都返回一个布尔值，因此可以在 **assert** 语句中使用它们。此外，如果出于性能考虑禁用断言，则根本不存在方法调用。**invariant()** 对对象执行内部有效性检查，如果你在每个方法调用的开始和结束都这样做，这是一个花销巨大的操作，就像 **Meyer** 建议的那样。所以， 用代码清晰地表明是有帮助的，它帮助我调试了实现。此外，如果你对代码实现做任何更改，那么 **invariant()** 将确保你没有破坏代码，将不变性测试从方法调用移到单元测试代码中是相当简单的。如果你的单元测试是足够的，那么你应当对不变性保持一定的信心。

  

  **dump()** helper方法返回一个包含所有数据的字符串，而不是直接打印数据。这表示你可以展示更多的信息。

  

  现在我们可以为类创建JUnit测试:

  ```java
  // validating/tests/CircularQueueTest.java
  package validating;
  import org.junit.jupiter.api.*;
  import static org.junit.jupiter.api.Assertions.*;
  public class CircularQueueTest {
      private CircularQueue queue = new CircularQueue(10);
      private int i = 0;
      
      @BeforeEach
      public void initialize() {
          while(i < 5) // Pre-load with some data
          queue.put(Integer.toString(i++));
  	}
      
      // Support methods:
      private void showFullness() {
          assertTrue(queue.full());
          assertFalse(queue.empty());
          System.out.println(queue.dump());
      }
      
      private void showEmptiness() {
          assertFalse(queue.full());
          assertTrue(queue.empty());
          System.out.println(queue.dump());
      }
      
      @Test
      public void full() {
          System.out.println("testFull");
          System.out.println(queue.dump());
          System.out.println(queue.get());
          System.out.println(queue.get());
          while(!queue.full())
              queue.put(Integer.toString(i++));
              String msg = "";
          try {
          	queue.put("");
          } catch(CircularQueueException e) {
          	msg = e.getMessage();
          	System.out.println(msg);
          }
          assertEquals(msg, "put() into full CircularQueue");
          showFullness();
      }
      
      @Test
      public void empty() {
          System.out.println("testEmpty");
          while(!queue.empty())
      		System.out.println(queue.get());
      		String msg = "";
          try {
          	queue.get();
          } catch(CircularQueueException e) {
              msg = e.getMessage();
              System.out.println(msg);
          }
          assertEquals(msg, "get() from empty CircularQueue");
          showEmptiness();
      }
      @Test
      public void nullPut() {
          System.out.println("testNullPut");
          	String msg = "";
          try {
          	queue.put(null);
          } catch(CircularQueueException e) {
              msg = e.getMessage();
              System.out.println(msg);
          }
          	assertEquals(msg, "put() null item");
      }
      
      @Test
      public void circularity() {
      	System.out.println("testCircularity");
      	while(!queue.full())
      		queue.put(Integer.toString(i++));
      		showFullness();
      		assertTrue(queue.isWrapped());
          
          while(!queue.empty())
          	System.out.println(queue.get());
          	showEmptiness();
          
          while(!queue.full())
          	queue.put(Integer.toString(i++));
          	showFullness();
          
          while(!queue.empty())
          	System.out.println(queue.get());
          	showEmptiness();
      }
  }
  /* Output:
  testNullPut
  put() null item
  testCircularity
  in = 0, out = 0, full() = true, empty() = false,
  CircularQueue =
  [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
  0
  1
  2
  3
  4
  5
  6
  7
  8
  9
  in = 0, out = 0, full() = false, empty() = true,
  CircularQueue =
  [null, null, null, null, null, null, null, null, null,
  null]
  in = 0, out = 0, full() = true, empty() = false,
  CircularQueue =
  [10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
  10
  11
  12
  13
  14
  15
  16
  17
  18
  19
  in = 0, out = 0, full() = false, empty() = true,
  CircularQueue =
  [null, null, null, null, null, null, null, null, null,
  null]
  testFull
  in = 5, out = 0, full() = false, empty() = false,
  CircularQueue =
  [0, 1, 2, 3, 4, null, null, null, null, null]
  0
  1
  put() into full CircularQueue
  in = 2, out = 2, full() = true, empty() = false,
  CircularQueue =
  [10, 11, 2, 3, 4, 5, 6, 7, 8, 9]
  testEmpty
  0
  1
  2
  3
  4
  get() from empty CircularQueue
  in = 5, out = 5, full() = false, empty() = true,
  CircularQueue =
  [null, null, null, null, null, null, null, null, null,
  null]
  */
  ```

  **initialize()** 添加了一些数据，因此每个测试的 **CircularQueue** 都是部分满的。**showFullness()** 和 **showempty()** 表明 **CircularQueue** 是满的还是空的，这四种测试方法中的每一种都确保了**CircularQueue**功能在不同地方的正确运行。

通过将Dbc和单元测试结合起来，你不仅可以同时使用这两种方法，还可以有一个迁移路径—你可以将一些Dbc测试迁移到单元测试中，而不是简单地禁用它们，这样你仍然有一定程度的测试。

#### 使用Guava前置条件

在非严格DBC中，前置条件是DbC中你不想删除的那一部分，因为它可以检查方法参数的有效性。那是你没有办法控制的事情，所以你需要对其检查。因为Java在默认情况下禁用断言，所以通常最好使用另外一个始终验证方法参数的库。

谷歌的Guava库包含了一组很好的前置条件测试，这些测试不仅易于使用，而且命名也足够好。在这里你可以看到它们的简单用法。库的设计人员建议静态导入前置条件:

```java
// validating/GuavaPreconditions.java
// Demonstrating Guava Preconditions
import java.util.function.*;
import static com.google.common.base.Preconditions.*;
public class GuavaPreconditions {
    static void test(Consumer<String> c, String s) {
        try {
            System.out.println(s);
            c.accept(s);
            System.out.println("Success");
        } catch(Exception e) {
            String type = e.getClass().getSimpleName();
            String msg = e.getMessage();
            System.out.println(type +
            (msg == null ? "" : ": " + msg));
        }
    }
    
    public static void main(String[] args) {
        test(s -> s = checkNotNull(s), "X");
        test(s -> s = checkNotNull(s), null);
        test(s -> s = checkNotNull(s, "s was null"), null);
        test(s -> s = checkNotNull(
        s, "s was null, %s %s", "arg2", "arg3"), null);
        test(s -> checkArgument(s == "Fozzie"), "Fozzie");
        test(s -> checkArgument(s == "Fozzie"), "X");
        test(s -> checkArgument(s == "Fozzie"), null);
        test(s -> checkArgument(
        s == "Fozzie", "Bear Left!"), null);
        test(s -> checkArgument(
        s == "Fozzie", "Bear Left! %s Right!", "Frog"),
        null);
        test(s -> checkState(s.length() > 6), "Mortimer");
        test(s -> checkState(s.length() > 6), "Mort");
        test(s -> checkState(s.length() > 6), null);
        test(s ->
        checkElementIndex(6, s.length()), "Robert");
        test(s ->
        checkElementIndex(6, s.length()), "Bob");
        test(s ->
        checkElementIndex(6, s.length()), null);
        test(s ->
        checkPositionIndex(6, s.length()), "Robert");
        test(s ->
        checkPositionIndex(6, s.length()), "Bob");
        test(s ->
        checkPositionIndex(6, s.length()), null);
        test(s -> checkPositionIndexes(
        0, 6, s.length()), "Hieronymus");
        test(s -> checkPositionIndexes(
        0, 10, s.length()), "Hieronymus");
        test(s -> checkPositionIndexes(
        0, 11, s.length()), "Hieronymus");
        test(s -> checkPositionIndexes(
        -1, 6, s.length()), "Hieronymus");
        test(s -> checkPositionIndexes(
        7, 6, s.length()), "Hieronymus");
        test(s -> checkPositionIndexes(
        0, 6, s.length()), null);
    }
}
/* Output:
X
Success
null
NullPointerException
null
NullPointerException: s was null
null
NullPointerException: s was null, arg2 arg3
Fozzie
Success
X
IllegalArgumentException
null
IllegalArgumentException
null
IllegalArgumentException: Bear Left!
null
IllegalArgumentException: Bear Left! Frog Right!
Mortimer
Success
Mort
IllegalStateException
null
NullPointerException
Robert
IndexOutOfBoundsException: index (6) must be less than
size (6)
Bob
IndexOutOfBoundsException: index (6) must be less than
size (3)
null
NullPointerException
Robert
Success
Bob
IndexOutOfBoundsException: index (6) must not be
greater than size (3)
null
NullPointerException
Hieronymus
Success
Hieronymus
Success
Hieronymus
IndexOutOfBoundsException: end index (11) must not be
greater than size (10)
Hieronymus
IndexOutOfBoundsException: start index (-1) must not be
negative
Hieronymus
IndexOutOfBoundsException: end index (6) must not be	
less than start index (7)
null
NullPointerException
*/
```

虽然Guava的前置条件适用于所有类型，但我只演示 **字符串（String）** 类型。**test()** 方法需要一个Consumer<String>，因此我们可以传递一个lambda表达式作为第一个参数字符串。以及作为第二个参数传递给lambda的字符串。它显示字符串，以便在查看输出时确定方向，然后将字符串传递给lambda表达式。try块中的第二个 **println**() 仅在lambda表达式成功时才显示; 否则catch将捕获并显示错误信息。注意 **test()** 方法消除了多少重复的代码。

每个前置条件都有三种不同的重载形式：一个什么都没有，一个带有简单字符串消息，以及带有一个字符串和替换值。为了提高效率，只允许 **%s** (字符串类型)替换标记。在上面的例子中，演示了**checkNotNull()** 和 **checkArgument()** 这两种形式。但是它们对于所有前置条件方法都是相同的。注意 **checkNotNull()** 的返回参数， 所以你可以在表达式中内联使用它。下面是如何在构造函数中使用它来防止包含 **Null** 值的对象构造：

/

```java
/ validating/NonNullConstruction.java
import static com.google.common.base.Preconditions.*;
public class NonNullConstruction {
    private Integer n;
    private String s;
    NonNullConstruction(Integer n, String s) {
        this.n = checkNotNull(n);	
        this.s = checkNotNull(s);
    }
    public static void main(String[] args) {
        NonNullConstruction nnc =
        new NonNullConstruction(3, "Trousers");
    }
}
```

**checkArgument()** 接受布尔表达式来对参数进行更具体的测试， 失败时抛出 **IllegalArgumentException**，**checkState()** 用于测试对象的状态（例如，不变性检查），而不是检查参数，并在失败时抛出 **IllegalStateException** 。

最后三个方法在失败时抛出 **IndexOutOfBoundsException**。**checkElementIndex**() 确保其第一个参数是列表、字符串或数组的有效元素索引，其大小由第二个参数指定。**checkPositionIndex()** 确保它的第一个参数在 0 到第二个参数(包括第二个参数)的范围内。 **checkPositionIndexes()**  检查 **[first_arg, second_arg]** 是一个列表的有效子列表，由第三个参数指定大小的字符串或数组。

所有Guava前置条件对于基本类型和对象都有必要的重载。

<!-- Test-Driven Development -->

## 测试驱动开发

之所以可以有测试驱动开发（TDD）这种开发方式，是因为如果你在设计和编写代码时考虑到了测试，那么你不仅可以写出可测试性更好的代码，而且还可以得到更好的代码设计。 一般情况下这个说法都是正确的。 一旦我想到“我将如何测试我的代码？”，这个想法将使我的代码产生变化，并且往往是从“可测试”转变为“可用”。

纯粹的TDD主义者会在实现新功能之前就为其编写测试，这称为测试优先的开发。 我们采用一个简易的示例程序来进行说明，它的功能是反转 **String** 中字符的大小写。 让我们随意添加一些约束：**String** 必须小于或等于30个字符，并且必须只包含字母，空格，逗号和句号(英文)。

此示例与标准TDD不同，因为它的作用在于接收 **StringInverter** 的不同实现，以便在我们逐步满足测试的过程中来体现类的演变。 所以 **StringInverter** 将作为接口来满足这个要求：

```java
// validating/StringInverter.java
package validating;

interface StringInverter {
	String invert(String str);
}
```

现在我们通过可以编写测试来表述我们的要求。 以下所述通常不是你编写测试的方式，但由于我们在此处有一个特殊的约束：我们要对 **StringInverter **多个版本的实现进行测试，为此，我们利用了JUnit5中最复杂的新功能之一：动态测试生成。 顾名思义，通过它你可以使你所编写的代码在运行时生成测试，而不需要你对每个测试显式编码。 这带来了许多新的可能性，特别是在明确地需要编写一整套测试而令人望而却步的情况下。

JUnit5 提供了几种动态生成测试的方法，但这里使用的方法可能是最复杂的。  **DynamicTest.stream() **方法采用了：

- 对象集合上的迭代器 (versions) ，这个迭代器在不同组的测试中是不同的。 迭代器生成的对象可以是任何类型，但是只能有一种对象生成，因此对于存在多个不同的对象类型时，必须人为地将它们打包成单个类型。
- **Function**，它从迭代器获取对象并生成描述测试的 **String** 。
- **Consumer**，它从迭代器获取对象并包含基于该对象的测试代码。

在此示例中，所有代码将在 **testVersions()**  中进行组合以防止代码重复。 迭代器生成的对象是对 **DynamicTest** 的不同实现，这些对象体现了对接口不同版本的实现：

```java
// validating/tests/DynamicStringInverterTests.java
package validating;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

class DynamicStringInverterTests {
	// Combine operations to prevent code duplication:
	Stream<DynamicTest> testVersions(String id,
		Function<StringInverter, String> test) {
		List<StringInverter> versions = Arrays.asList(
			new Inverter1(), new Inverter2(),
			new Inverter3(), new Inverter4());
		return DynamicTest.stream(
			versions.iterator(),
			inverter -> inverter.getClass().getSimpleName(),
			inverter -> {
				System.out.println(
					inverter.getClass().getSimpleName() +
						": " + id);
				try {
					if(test.apply(inverter) != "fail")
						System.out.println("Success");
				} catch(Exception | Error e) {
					System.out.println(
						"Exception: " + e.getMessage());
				}
			}
		);
	}
    String isEqual(String lval, String rval) {
		if(lval.equals(rval))
			return "success";
		System.out.println("FAIL: " + lval + " != " + rval);
		return "fail";
	}
    @BeforeAll
	static void startMsg() {
		System.out.println(
			">>> Starting DynamicStringInverterTests <<<");
	}
    @AfterAll
	static void endMsg() {
		System.out.println(
			">>> Finished DynamicStringInverterTests <<<");
	}
	@TestFactory
	Stream<DynamicTest> basicInversion1() {
		String in = "Exit, Pursued by a Bear.";
		String out = "eXIT, pURSUED BY A bEAR.";
		return testVersions(
			"Basic inversion (should succeed)",
			inverter -> isEqual(inverter.invert(in), out)
		);
	}
	@TestFactory
	Stream<DynamicTest> basicInversion2() {
		return testVersions(
			"Basic inversion (should fail)",
			inverter -> isEqual(inverter.invert("X"), "X"));
	}
	@TestFactory
	Stream<DynamicTest> disallowedCharacters() {
		String disallowed = ";-_()*&^%$#@!~`0123456789";
		return testVersions(
			"Disallowed characters",
			inverter -> {
				String result = disallowed.chars()
					.mapToObj(c -> {
						String cc = Character.toString((char)c);
						try {
							inverter.invert(cc);
							return "";
						} catch(RuntimeException e) {
							return cc;
						}
					}).collect(Collectors.joining(""));
				if(result.length() == 0)
					return "success";
				System.out.println("Bad characters: " + result);
				return "fail";
			}
		);
	}
    @TestFactory
	Stream<DynamicTest> allowedCharacters() {
		String lowcase = "abcdefghijklmnopqrstuvwxyz ,.";
		String upcase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ,.";
		return testVersions(
			"Allowed characters (should succeed)",
            inverter -> {
				assertEquals(inverter.invert(lowcase), upcase);
				assertEquals(inverter.invert(upcase), lowcase);
				return "success";
			}
		);
	}
	@TestFactory
	Stream<DynamicTest> lengthNoGreaterThan30() {
		String str = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		assertTrue(str.length() > 30);
		return testVersions(
			"Length must be less than 31 (throws exception)",
			inverter -> inverter.invert(str)
		);
	}
	@TestFactory
	Stream<DynamicTest> lengthLessThan31() {
		String str = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		assertTrue(str.length() < 31);
		return testVersions(
			"Length must be less than 31 (should succeed)",
			inverter -> inverter.invert(str)
		);
	}
}
```

在一般的测试中，你可能认为在进行一个结果为失败的测试时应该停止代码构建。 但是在这里，我们只希望系统报告问题，但仍然继续运行，以便你可以看到不同版本的 **StringInverter** 的效果。

每个使用 **@TestFactory** 注释的方法都会生成一个 **DynamicTest** 对象的 **Stream**（通过 **testVersions()** ），每个 JUnit 都像常规的 **@Test** 方法一样执行。

现在测试都已经准备好了，我们就可以开始实现 **StringInverter **了。 我们从一个仅返回其参数的假的实现类开始：

```java
// validating/Inverter1.java
package validating;
public class Inverter1 implements StringInverter {
	public String invert(String str) { return str; }
}
```

接下来我们实现反转操作：

```java
// validating/Inverter2.java
package validating;
import static java.lang.Character.*;
public class Inverter2 implements StringInverter {
	public String invert(String str) {
		String result = "";
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			result += isUpperCase(c) ?
					  toLowerCase(c) :
					  toUpperCase(c);
		}
		return result;
	}
}
```

现在添加代码以确保输入不超过30个字符：

```java
// validating/Inverter3.java
package validating;
import static java.lang.Character.*;
public class Inverter3 implements StringInverter {
	public String invert(String str) {
		if(str.length() > 30)
			throw new RuntimeException("argument too long!");
		String result = "";
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			result += isUpperCase(c) ?
					  toLowerCase(c) :
					  toUpperCase(c);
        }
		return result;
	}
}
```

最后，我们排除了不允许的字符：

```java
// validating/Inverter4.java
package validating;
import static java.lang.Character.*;
public class Inverter4 implements StringInverter {
	static final String ALLOWED =
		"abcdefghijklmnopqrstuvwxyz ,." +
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public String invert(String str) {
		if(str.length() > 30)
			throw new RuntimeException("argument too long!");
		String result = "";
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if(ALLOWED.indexOf(c) == -1)
				throw new RuntimeException(c + " Not allowed");
			result += isUpperCase(c) ?
					  toLowerCase(c) :
					  toUpperCase(c);
		}
		return result;
	}
}
```

你将从测试输出中看到，每个版本的 **Inverter** 都几乎能通过所有测试。 当你在进行测试优先的开发时会有相同的体验。

**DynamicStringInverterTests.java** 仅是为了显示TDD过程中不同 **StringInverter** 实现的开发。 通常，你只需编写一组如下所示的测试，并修改单个 **StringInverter** 类直到它满足所有测试：

```java
// validating/tests/StringInverterTests.java
package validating;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class StringInverterTests {
	StringInverter inverter = new Inverter4();
	@BeforeAll
	static void startMsg() {
		System.out.println(">>> StringInverterTests <<<");
	}
    @Test
	void basicInversion1() {
		String in = "Exit, Pursued by a Bear.";
		String out = "eXIT, pURSUED BY A bEAR.";
		assertEquals(inverter.invert(in), out);
	}
	@Test
	void basicInversion2() {
		expectThrows(Error.class, () -> {
			assertEquals(inverter.invert("X"), "X");
		});
	}
	@Test
	void disallowedCharacters() {
		String disallowed = ";-_()*&^%$#@!~`0123456789";
		String result = disallowed.chars()
			.mapToObj(c -> {
				String cc = Character.toString((char)c);
				try {
					inverter.invert(cc);
					return "";
				} catch(RuntimeException e) {
					return cc;
				}
			}).collect(Collectors.joining(""));
		assertEquals(result, disallowed);
	}
	@Test
	void allowedCharacters() {
		String lowcase = "abcdefghijklmnopqrstuvwxyz ,.";
		String upcase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ,.";
		assertEquals(inverter.invert(lowcase), upcase);
		assertEquals(inverter.invert(upcase), lowcase);
	}
	@Test
	void lengthNoGreaterThan30() {
		String str = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		assertTrue(str.length() > 30);
		expectThrows(RuntimeException.class, () -> {
			inverter.invert(str);
		});
	}
    @Test
	void lengthLessThan31() {
		String str = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		assertTrue(str.length() < 31);
		inverter.invert(str);
	}
}
```

你可以通过这种方式进行开发：一开始在测试中建立你期望程序应有的所有特性，然后你就能在实现中一步步添加功能，直到所有测试通过。 完成后，你还可以在将来通过这些测试来得知（或让其他任何人得知）当修复错误或添加功能时，代码是否被破坏了。 TDD的目标是产生更好，更周全的测试，因为在完全实现之后尝试实现完整的测试覆盖通常会产生匆忙或无意义的测试。

### 测试驱动 vs. 测试优先

虽然我自己还没有达到测试优先的意识水平，但我最感兴趣的是来自测试优先中的“测试失败的书签”这一概念。 当你离开你的工作一段时间后，重新回到工作进展中，甚至找到你离开时工作到的地方有时会很有挑战性。 然而，以失败的测试为书签能让你找到之前停止的地方。 这似乎让你能更轻松地暂时离开你的工作，因为不用担心找不到工作进展的位置。

纯粹的测试优先编程的主要问题是它假设你事先了解了你正在解决的问题。 根据我自己的经验，我通常是从实验开始，而只有当我处理问题一段时间后，我对它的理解才会达到能给它编写测试的程度。 当然，偶尔会有一些问题在你开始之前就已经完全定义，但我个人并不常遇到这些问题。 实际上，可能用“*面向测试的开发* ( *Test-Oriented Development* )”这个短语来描述编写测试良好的代码或许更好。

<!-- Logging -->

## 日志

### 日志会给出正在运行的程序的各种信息。

在调试程序中，日志可以是显示程序运行过程中的普通状态数据（例如，安装程序可能会记录安装过程中采取的步骤，存储文件的目录，程序的启动值等）。

在调试期间，日志也能带来好处。 如果没有日志，你可能会尝试通过插入 **println()** 语句来打印出程序的行为。 本书中的一些例子使用了这种技术，并且在没有调试器的情况下（下文中很快就会介绍这样一个主题），它就是你唯一的工具。 但是，一旦你确定程序正常运行，你可能会将 **println()** 语句注释或者删除。 然而，如果你遇到更多错误，你可能又需要运行它们。因此，如果能够只在需要时轻松启用输出程序状态就好多了。

程序员在日志包可供使用之前，都只能依赖 Java 编译器移除未调用的代码。 如果 **debug** 是一个 **static final boolean **，你就可以这么写：

```java
if(debug) {
	System.out.println("Debug info");
}
```

然后，当 **debug **为 **false **时，编译器将移除大括号内的代码。 因此，未调用的代码不会对运行时产生影响。 使用这种方法，你可以在整个程序中放置跟踪代码，并轻松启用和关闭它。 但是，该技术的一个缺点是你必须重新编译代码才能启用和关闭跟踪语句。因此，通过更改配置文件来修改日志属性，从而起到启用跟踪语句但不用重新编译程序会更方便。

业内普遍认为标准 Java 发行版本中的日志包 **(java.util.logging)** 的设计相当糟糕。 大多数人会选择其他的替代日志包。如 *Simple Logging Facade for Java(SLF4J)* ,它为多个日志框架提供了一个封装好的调用方式，这些日志框架包括 **java.util.logging** ， **logback** 和 **log4j **。 SLF4J 允许用户在部署时插入所需的日志框架。

SLF4J 提供了一个复杂的工具来报告程序的信息，它的效率与前面示例中的技术几乎相同。 对于非常简单的信息日志记录，你可以执行以下操作：

```java
// validating/SLF4JLogging.java
import org.slf4j.*;
public class SLF4JLogging {
	private static Logger log =
		LoggerFactory.getLogger(SLF4JLogging.class);
	public static void main(String[] args) {
		log.info("hello logging");
	}
}
/* Output:
2017-05-09T06:07:53.418
[main] INFO SLF4JLogging - hello logging
*/
```

日志输出中的格式和信息，甚至输出是否正常或“错误”都取决于 SLF4J 所连接的后端程序包是怎样实现的。 在上面的示例中，它连接到的是 **logback** 库（通过本书的 **build.gradle** 文件），并显示为标准输出。

如果我们修改 **build.gradle **从而不使用内置在 JDK 中的日志包作为后端，则输出显示为错误输出，如下所示：

**Aug 16, 2016 5:40:31 PM InfoLogging main**
**INFO: hello logging**

日志系统会检测日志消息处所在的的类名和方法名。 但它不能保证这些名称是正确的，所以不要纠结于其准确性。

### 日志等级

SLF4J 提供了多个等级的日志消息。下面这个例子以“严重性”的递增顺序对它们作出演示：

```java
// validating/SLF4JLevels.java
import org.slf4j.*;
public class SLF4JLevels {
	private static Logger log =
		LoggerFactory.getLogger(SLF4JLevels.class);
	public static void main(String[] args) {
		log.trace("Hello");
		log.debug("Logging");
		log.info("Using");
		log.warn("the SLF4J");
		log.error("Facade");
	}
}
/* Output:
2017-05-09T06:07:52.846
[main] TRACE SLF4JLevels - Hello
2017-05-09T06:07:52.849
[main] DEBUG SLF4JLevels - Logging
2017-05-09T06:07:52.849
[main] INFO SLF4JLevels - Using
2017-05-09T06:07:52.850
[main] WARN SLF4JLevels - the SLF4J
2017-05-09T06:07:52.851
[main] ERROR SLF4JLevels - Facade
*/
```

你可以按等级来查找消息。 级别通常设置在单独的配置文件中，因此你可以重新配置而无需重新编译。 配置文件格式取决于你使用的后端日志包实现。 如 **logback** 使用 XML ：

```xml
<!-- validating/logback.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<appender name="STDOUT"
		class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<pattern>
%d{yyyy-MM-dd'T'HH:mm:ss.SSS}
[%thread] %-5level %logger - %msg%n
			</pattern>
		</encoder>
	</appender>
	<root level="TRACE">
		<appender-ref ref="STDOUT" />
	</root>
</configuration>
```

你可以尝试将 **<root level =“TRACE"> **行更改为其他级别，然后重新运行该程序查看日志输出的更改情况。 如果你没有写 **logback.xml** 文件，日志系统将采取默认配置。

这只是 SLF4J 最简单的介绍和一般的日志消息，但也足以作为使用日志的基础 - 你可以沿着这个进行更长久的学习和实践。你可以查阅 [SLF4J 文档](http://www.slf4j.org/manual.html)来获得更深入的信息。

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
