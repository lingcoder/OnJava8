[TOC]

# 第五章 控制流

> 程序必须在执行过程中控制它的世界并做出选择。 在 Java 中，你需要执行控制语句来做出选择。

Java 使用了 C 的所有执行控制语句，因此对于熟悉 C/C++ 编程的人来说，这部分内容轻车熟路。大多数面向过程编程语言都有共通的某种控制语句。在 Java 中，涉及的关键字包括 **if-else，while，do-while，for，return，break** 和选择语句 **switch**。 Java 并不支持备受诟病的 **goto**（尽管它在某些特殊场景中依然是最行之有效的方法）。 尽管如此，在 Java 中我们仍旧可以进行类似的逻辑跳转，但较之典型的 **goto** 用法限制更多。


## true和false

所有的条件语句都利用条件表达式的“真”或“假”来决定执行路径。举例：
`a == b`。它利用了条件表达式 `==` 来比较 `a` 与 `b` 的值是否相等。 该表达式返回 `true` 或 `false`。代码示例：

```java
// control/TrueFalse.java
public class TrueFalse {
	public static void main(String[] args) {
		System.out.println(1 == 1);
		System.out.println(1 == 2);
	}
}
```

输出结果：

```
true false 
```

通过上一章的学习，我们知道任何关系运算符都可以产生条件语句。 **注意**：在 Java 中使用数值作为布尔值是非法的，即便这种操作在 C/C++ 中是被允许的（在这些语言中，“真”为非零，而“假”是零）。如果想在布尔测试中使用一个非布尔值，那么首先需要使用条件表达式来产生 **boolean** 类型的结果，例如 `if(a != 0)`。

## if-else

**if-else** 语句是控制程序执行流程最基本的形式。 其中 `else` 是可选的，因此可以有两种形式的 `if`。代码示例：

```java
if(Boolean-expression) 
	“statement” 
```

或

```java
if(Boolean-expression) 
	“statement”
else
  “statement”
```

布尔表达式（Boolean-expression）必须生成 **boolean** 类型的结果，执行语句 `statement` 既可以是以分号 `;` 结尾的一条简单语句，也可以是包含在大括号 `{}` 内的的复合语句 —— 封闭在大括号内的一组简单语句。 凡本书中提及“statement”一词，皆表示类似的执行语句。

下面是一个有关 **if-else** 语句的例子。`test()` 方法可以告知你两个数值之间的大小关系。代码示例：

```java
// control/IfElse.java
public class IfElse {
  static int result = 0;
  static void test(int testval, int target) {
    if(testval > target)
      result = +1;
    else if(testval < target) // [1]
      result = -1;
    else
      result = 0; // Match
  }

  public static void main(String[] args) {
    test(10, 5);
    System.out.println(result);
    test(5, 10);
    System.out.println(result);
    test(5, 5);
    System.out.println(result);
  }
}
```

输出结果：

```
1
-1
0
```

<sub>**注解**：`else if` 并非新关键字，它仅是 `else` 后紧跟的一条新 `if` 语句。</sub>

Java 和 C/C++ 同属“自由格式”的编程语言，但通常我们会在 Java 控制流程语句中采用首部缩进的规范，以便代码更具可读性。

<!--Iteration Statements-->
## 迭代语句

**while**，**do-while** 和 **for** 用来控制循环语句（有时也称迭代语句）。只有控制循环的布尔表达式计算结果为 `false`，循环语句才会停止。 


### while

**while** 循环的形式是：

```java
while(Boolean-expression) 
  statement
```

执行语句会在每一次循环前，判断布尔表达式返回值是否为 `true`。下例可产生随机数，直到满足特定条件。代码示例：

```java
// control/WhileTest.java
// 演示 while 循环
public class WhileTest {
  static boolean condition() {
    boolean result = Math.random() < 0.99;
    System.out.print(result + ", ");
    return result;
  }
  public static void main(String[] args) {
    while(condition())
      System.out.println("Inside 'while'");
    System.out.println("Exited 'while'");
  }
}
```

输出结果：

```
true, Inside 'while'
true, Inside 'while'
true, Inside 'while'
true, Inside 'while'
true, Inside 'while'
...________...________...________...________...
true, Inside 'while'
true, Inside 'while'
true, Inside 'while'
true, Inside 'while'
false, Exited 'while'
```

`condition()` 方法使用到了 **Math** 库的**静态**方法 `random()`。该方法的作用是产生 0 和 1 之间 (包括 0，但不包括 1) 的一个 **double** 值。

**result** 的值是通过比较运算符 `<` 产生的 **boolean** 类型的结果。当控制台输出 **boolean** 型值时，会自动将其转换为对应的文字形式 `true` 或 `false`。此处 `while` 条件表达式代表：“仅在 `condition()` 返回 `false` 时停止循环”。


### do-while

**do-while** 的格式如下：

```java
do 
	statement
while(Boolean-expression);
```

**while** 和 **do-while** 之间的唯一区别是：即使条件表达式返回结果为 `false`， **do-while** 语句也至少会执行一次。 在 **while** 循环体中，如布尔表达式首次返回的结果就为 `false`，那么循环体内的语句不会被执行。实际应用中，**while** 形式比 **do-while** 更为常用。


### for

**for** 循环可能是最常用的迭代形式。 该循环在第一次迭代之前执行初始化。随后，它会执行布尔表达式，并在每次迭代结束时，进行某种形式的步进。**for** 循环的形式是：

```java
for(initialization; Boolean-expression; step)
  statement
```

初始化 (initialization) 表达式、布尔表达式 (Boolean-expression) ，或者步进 (step) 运算，都可以为空。每次迭代之前都会判断布尔表达式的结果是否成立。一旦计算结果为 `false`，则跳出 **for** 循环体并继续执行后面代码。 每次循环结束时，都会执行一次步进。

**for** 循环通常用于“计数”任务。代码示例：

```java
// control/ListCharacters.java

public class ListCharacters {
  public static void main(String[] args) {
    for(char c = 0; c < 128; c++)
      if(Character.isLowerCase(c))
        System.out.println("value: " + (int)c +
          " character: " + c);
  }
}
```

输出结果（前 10 行）：

```
value: 97 character: a
value: 98 character: b
value: 99 character: c
value: 100 character: d
value: 101 character: e
value: 102 character: f
value: 103 character: g
value: 104 character: h
value: 105 character: i
value: 106 character: j
  ...
```


**注意**：变量 **c** 是在 **for** 循环执行时才被定义的，并不是在主方法的开头。**c** 的作用域范围仅在 **for** 循环体内。

传统的面向过程语言如 C 需要先在代码块（block）前定义好所有变量才能够使用。这样编译器才能在创建块时，为这些变量分配内存空间。在 Java 和 C++ 中，我们可以在整个块使用变量声明，并且可以在需要时才定义变量。 这种自然的编码风格使我们的代码更容易被人理解 [^1]。

上例使用了 **java.lang.Character** 包装类，该类不仅包含了基本类型 `char` 的值，还封装了一些有用的方法。例如这里就用到了静态方法 `isLowerCase()` 来判断字符是否为小写。

<!--The Comma Operator-->

#### 逗号操作符

在 Java 中逗号运算符（这里并非指我们平常用于分隔定义和方法参数的逗号分隔符）仅有一种用法：在 **for** 循环的初始化和步进控制中定义多个变量。我们可以使用逗号分隔多个语句，并按顺序计算这些语句。**注意**：要求定义的变量类型相同。代码示例：

```java
// control/CommaOperator.java

public class CommaOperator {
  public static void main(String[] args) {
    for(int i = 1, j = i + 10; i < 5; i++, j = i * 2) {
      System.out.println("i = " + i + " j = " + j);
    }
  }
}
```

输出结果：

```
i = 1 j = 11
i = 2 j = 4
i = 3 j = 6
i = 4 j = 8
```


上例中 **int** 类型声明包含了 `i` 和 `j`。实际上，在初始化部分我们可以定义任意数量的同类型变量。**注意**：在 Java 中，仅允许 **for** 循环在控制表达式中定义变量。 我们不能将此方法与其他的循环语句和选择语句中一起使用。同时，我们可以看到：无论在初始化还是在步进部分，语句都是顺序执行的。

## for-in 语法 

Java 5 引入了更为简洁的“增强版 **for** 循环”语法来操纵数组和集合。（更多细节，可参考 [数组](./21-Arrays.md) 和 [集合](./12-Collections.md) 章节内容）。大部分文档也称其为 **for-each** 语法，但因为了不与 Java 8 新添的 `forEach()` 产生混淆，因此我称之为 **for-in** 循环。 （Python 已有类似的先例，如：**for x in sequence**）。**注意**：你可能会在其他地方看到不同叫法。

**for-in** 无需你去创建 **int** 变量和步进来控制循环计数。 下面我们来遍历获取 **float** 数组中的元素。代码示例：

```java
// control/ForInFloat.java

import java.util.*;

public class ForInFloat {
  public static void main(String[] args) {
    Random rand = new Random(47);
    float[] f = new float[10];
    for(int i = 0; i < 10; i++)
      f[i] = rand.nextFloat();
    for(float x : f)
      System.out.println(x);
  }
}
```

输出结果：

```
0.72711575
0.39982635
0.5309454
0.0534122
0.16020656
0.57799757
0.18847865
0.4170137
0.51660204
0.73734957
```

上例中我们展示了传统 **for** 循环的用法。接下来再来看下 **for-in** 的用法。代码示例：

```java
for(float x : f) {
```

这条语句定义了一个 **float** 类型的变量 `x`，继而将每一个 `f` 的元素赋值给它。

任何一个返回数组的方法都可以使用 **for-in** 循环语法来遍历元素。例如 **String** 类有一个方法 `toCharArray()`，返回值类型为 **char** 数组，我们可以很容易地在 **for-in** 循环中遍历它。代码示例：

```java
// control/ForInString.java

public class ForInString {
  public static void main(String[] args) {
    for(char c: "An African Swallow".toCharArray())
      System.out.print(c + " ");
  }
}
```

输出结果：

```
A n   A f r i c a n   S w a l l o w
```

很快我们能在 [集合](./12-Collections.md) 章节里学习到，**for-in** 循环适用于任何可迭代（*iterable*）的 对象。

通常，**for** 循环语句都会在一个整型数值序列中步进。代码示例：

```java
for(int i = 0; i < 100; i++)
```

正因如此，除非先创建一个 **int** 数组，否则我们无法使用 **for-in** 循环来操作。为简化测试过程，我已在 `onjava` 包中封装了 **Range** 类，利用其 `range()` 方法可自动生成恰当的数组。

在 [封装](./07-Implementation-Hiding.md)（Implementation Hiding）这一章里我们介绍了静态导入（static import），无需了解细节就可以直接使用。 有关静态导入的语法，可以在 **import** 语句中看到：

```java
// control/ForInInt.java

import static onjava.Range.*;

public class ForInInt {
  public static void main(String[] args) {
    for(int i : range(10)) // 0..9
      System.out.print(i + " ");
    System.out.println();
    for(int i : range(5, 10)) // 5..9
      System.out.print(i + " ");
    System.out.println();
    for(int i : range(5, 20, 3)) // 5..20 step 3
      System.out.print(i + " ");
    System.out.println();
    for(int i : range(20, 5, -3)) // Count down
      System.out.print(i + " ");
    System.out.println();
  }
}
```

输出结果：
```
0 1 2 3 4 5 6 7 8 9
5 6 7 8 9
5 8 11 14 17
20 17 14 11 8
```

`range()` 方法已被 [重载](./06-Housekeeping.md#方法重载)（重载：同名方法，参数列表或类型不同）。上例中 `range()` 方法有多种重载形式：第一种产生从 0 至范围上限（不包含）的值；第二种产生参数一至参数二（不包含）范围内的整数值；第三种形式有一个步进值，因此它每次的增量为该值；第四种 `range()` 表明还可以递减。`range()` 无参方法是该生成器最简单的版本。有关内容会在本书稍后介绍。

`range()` 的使用提高了代码可读性，让 **for-in** 循环在本书中适应更多的代码示例场景。

请注意，`System.out.print()` 不会输出换行符，所以我们可以分段输出同一行。

*for-in* 语法可以节省我们编写代码的时间。 更重要的是，它提高了代码可读性以及更好地描述代码意图（获取数组的每个元素）而不是详细说明这操作细节（创建索引，并用它来选择数组元素） 本书推荐使用 *for-in* 语法。

## return

在 Java 中有几个关键字代表无条件分支，这意味无需任何测试即可发生。这些关键字包括 **return**，**break**，**continue** 和跳转到带标签语句的方法，类似于其他语言中的 **goto**。

**return** 关键字有两方面的作用：1.指定一个方法返回值 (在方法返回类型非 **void** 的情况下)；2.退出当前方法，并返回作用 1 中值。我们可以利用 `return` 的这些特点来改写上例 `IfElse.java` 文件中的 `test()` 方法。代码示例：

```java
// control/TestWithReturn.java

public class TestWithReturn {
  static int test(int testval, int target) {
    if(testval > target)
      return +1;
    if(testval < target)
      return -1;
    return 0; // Match
  }

  public static void main(String[] args) {
    System.out.println(test(10, 5));
    System.out.println(test(5, 10));
    System.out.println(test(5, 5));
  }
}
```

输出结果：

```
1
-1
0
```

这里不需要 `else`，因为该方法执行到 `return` 就结束了。

如果在方法签名中定义了返回值类型为 **void**，那么在代码执行结束时会有一个隐式的 **return**。 也就是说我们不用在总是在方法中显式地包含 **return** 语句。 **注意**：如果你的方法声明的返回值类型为非 **void** 类型，那么则必须确保每个代码路径都返回一个值。

## break 和 continue

在任何迭代语句的主体内，都可以使用 **break** 和 **continue** 来控制循环的流程。 其中，**break** 表示跳出当前循环体。而 **continue** 表示停止本次循环，开始下一次循环。

下例向大家展示 **break** 和 **continue** 在 **for**、**while** 循环中的使用。代码示例：

```java
// control/BreakAndContinue.java
// Break 和 continue 关键字

import static onjava.Range.*;

public class BreakAndContinue {
  public static void main(String[] args) {
    for(int i = 0; i < 100; i++) { // [1]
      if(i == 74) break; // 跳出循环
      if(i % 9 != 0) continue; // 下一次循环
      System.out.print(i + " ");
    }
    System.out.println();
    // 使用 for-in 循环:
    for(int i : range(100)) { // [2]
      if(i == 74) break; // 跳出循环
      if(i % 9 != 0) continue; // 下一次循环
      System.out.print(i + " ");
    }
    System.out.println();
    int i = 0;
    //  "无限循环":
    while(true) { // [3]
      i++;
      int j = i * 27;
      if(j == 1269) break; // 跳出循环
      if(i % 10 != 0) continue; // 循环顶部
      System.out.print(i + " ");
    }
  }
}
```

输出结果:

```
0 9 18 27 36 45 54 63 72
0 9 18 27 36 45 54 63 72
10 20 30 40
```

  <sub>**[1]** 在这个 **for** 循环中，`i` 的值永远不会达到 100，因为一旦 `i` 等于 74，**break** 语句就会中断循环。通常，只有在不知道中断条件何时满足时，才需要 **break**。因为 `i` 不能被 9 整除，**continue** 语句就会使循环从头开始。这使 **i** 递增)。如果能够整除，则将值显示出来。</sub>
  <sub>**[2]** 使用 **for-in** 语法，结果相同。</sub>
  <sub>**[3]** 无限 **while** 循环。循环内的 **break** 语句可中止循环。**注意**，**continue** 语句可将控制权移回循环的顶部，而不会执行 **continue** 之后的任何操作。 因此，只有当 `i` 的值可被 10 整除时才会输出。在输出中，显示值 0，因为 `0％9` 产生 0。还有一种无限循环的形式： `for(;;)`。 在编译器看来，它与 `while(true)` 无异，使用哪种完全取决于你的编程品味。</sub>

<!--The Infamous “Goto”-->
## 臭名昭著的 goto

[**goto** 关键字](https://en.wikipedia.org/wiki/Goto) 很早就在程序设计语言中出现。事实上，**goto** 起源于[汇编](https://en.wikipedia.org/wiki/Assembly_language)（assembly language）语言中的程序控制：“若条件 A 成立，则跳到这里；否则跳到那里”。如果你读过由编译器编译后的代码，你会发现在其程序控制中充斥了大量的跳转。较之汇编产生的代码直接运行在硬件 CPU 中，Java 也会产生自己的“汇编代码”（字节码），只不过它是运行在 Java 虚拟机里的（Java Virtual Machine）。

一个源码级别跳转的 **goto**，为何招致名誉扫地呢？若程序总是从一处跳转到另一处，还有什么办法能识别代码的控制流程呢？随着 *Edsger Dijkstra*发表著名的 “Goto 有害” 论（*Goto considered harmful*）以后，**goto** 便从此失宠。甚至有人建议将它从关键字中剔除。

正如上述提及的经典情况，我们不应走向两个极端。问题不在 **goto**，而在于过度使用 **goto**。在极少数情况下，**goto** 实际上是控制流程的最佳方式。

尽管 **goto** 仍是 Java 的一个保留字，但其并未被正式启用。可以说， Java 中并不支持 **goto**。然而，在 **break** 和 **continue** 这两个关键字的身上，我们仍能看出一些 **goto** 的影子。它们并不属于一次跳转，而是中断循环语句的一种方法。之所以把它们纳入 **goto** 问题中一起讨论，是由于它们使用了相同的机制：标签。

“标签”是后面跟一个冒号的标识符。代码示例：

```java
label1:
```

对 Java 来说，唯一用到标签的地方是在循环语句之前。进一步说，它实际需要紧靠在循环语句的前方 —— 在标签和循环之间置入任何语句都是不明智的。而在循环之前设置标签的唯一理由是：我们希望在其中嵌套另一个循环或者一个开关。这是由于 **break** 和 **continue** 关键字通常只中断当前循环，但若搭配标签一起使用，它们就会中断并跳转到标签所在的地方开始执行。代码示例：

```java
label1:
outer-iteration { 
  inner-iteration {
  // ...
  break; // [1] 
  // ...
  continue; // [2] 
  // ...
  continue label1; // [3] 
  // ...
  break label1; // [4] 
  } 
}
```

<sub>**[1]** **break** 中断内部循环，并在外部循环结束。</sub>
<sub>**[2]** **continue** 移回内部循环的起始处。但在条件 3 中，**continue label1** 却同时中断内部循环以及外部循环，并移至 **label1** 处。</sub>
<sub>**[3]** 随后，它实际是继续循环，但却从外部循环开始。</sub>
<sub>**[4]** **break label1** 也会中断所有循环，并回到 **label1** 处，但并不重新进入循环。也就是说，它实际是完全中止了两个循环。</sub>

下面是 **for** 循环的一个例子：

```java
// control/LabeledFor.java
// 搭配“标签 break”的 for 循环中使用 break 和 continue

public class LabeledFor {
  public static void main(String[] args) {
    int i = 0;
    outer: // 此处不允许存在执行语句
    for(; true ;) { // 无限循环
      inner: // 此处不允许存在执行语句
      for(; i < 10; i++) {
        System.out.println("i = " + i);
        if(i == 2) {
          System.out.println("continue");
          continue;
        }
        if(i == 3) {
          System.out.println("break");
          i++; // 否则 i 永远无法获得自增 
               // 获得自增 
          break;
        }
        if(i == 7) {
          System.out.println("continue outer");
          i++;  // 否则 i 永远无法获得自增 
                // 获得自增 
          continue outer;
        }
        if(i == 8) {
          System.out.println("break outer");
          break outer;
        }
        for(int k = 0; k < 5; k++) {
          if(k == 3) {
            System.out.println("continue inner");
            continue inner;
          }
        }
      }
    }
    // 在此处无法 break 或 continue 标签
  }
}
```

输出结果：

```
i = 0
continue inner
i = 1
continue inner
i = 2
continue
i = 3
break
i = 4
continue inner
i = 5
continue inner
i = 6
continue inner
i = 7
continue outer
i = 8
break outer
```


注意 **break** 会中断 **for** 循环，而且在抵达 **for** 循环的末尾之前，递增表达式不会执行。由于 **break** 跳过了递增表达式，所以递增会在 `i==3` 的情况下直接执行。在 `i==7` 的情况下，`continue outer` 语句也会到达循环顶部，而且也会跳过递增，所以它也是直接递增的。

如果没有 **break outer** 语句，就没有办法在一个内部循环里找到出外部循环的路径。这是由于 **break** 本身只能中断最内层的循环（对于 **continue** 同样如此）。 当然，若想在中断循环的同时退出方法，简单地用一个 **return** 即可。

下面这个例子向大家展示了带标签的 **break** 以及 **continue** 语句在 **while** 循环中的用法：

```java
// control/LabeledWhile.java
// 带标签的 break 和 conitue 在 while 循环中的使用

public class LabeledWhile {
  public static void main(String[] args) {
    int i = 0;
    outer:
    while(true) {
      System.out.println("Outer while loop");
      while(true) {
        i++;
        System.out.println("i = " + i);
        if(i == 1) {
          System.out.println("continue");
          continue;
        }
        if(i == 3) {
          System.out.println("continue outer");
          continue outer;
        }
        if(i == 5) {
          System.out.println("break");
          break;
        }
        if(i == 7) {
          System.out.println("break outer");
          break outer;
        }
      }
    }
  }
}
```

输出结果：

```
Outer while loop
i = 1
continue
i = 2
i = 3
continue outer
Outer while loop
i = 4
i = 5
break
Outer while loop
i = 6
i = 7
break outer
```

同样的规则亦适用于 **while**：

1. 简单的一个 **continue** 会退回最内层循环的开头（顶部），并继续执行。

2. 带有标签的 **continue** 会到达标签的位置，并重新进入紧接在那个标签后面的循环。

3. **break** 会中断当前循环，并移离当前标签的末尾。

4. 带标签的 **break** 会中断当前循环，并移离由那个标签指示的循环的末尾。

大家要记住的重点是：在 Java 里需要使用标签的唯一理由就是因为有循环嵌套存在，而且想从多层嵌套中 **break** 或 **continue**。

**break** 和 **continue** 标签在编码中的使用频率相对较低 (此前的语言中很少使用或没有先例)，所以我们很少在代码里看到它们。

在 *Dijkstra* 的 **“Goto 有害”** 论文中，他最反对的就是标签，而非 **goto**。他观察到 BUG 的数量似乎随着程序中标签的数量而增加[^2]。标签和 **goto** 使得程序难以分析。但是，Java 标签不会造成这方面的问题，因为它们的应用场景受到限制，无法用于以临时方式传输控制。由此也引出了一个有趣的情形：对语言能力的限制，反而使它这项特性更加有价值。


## switch

**switch** 有时也被划归为一种选择语句。根据整数表达式的值，**switch** 语句可以从一系列代码中选出一段去执行。它的格式如下：

```java
switch(integral-selector) {
	case integral-value1 : statement; break;
	case integral-value2 : statement;	break;
	case integral-value3 : statement;	break;
	case integral-value4 : statement;	break;
	case integral-value5 : statement;	break;
	// ...
	default: statement;
}
```

其中，**integral-selector** （整数选择因子）是一个能够产生整数值的表达式，**switch** 能够将这个表达式的结果与每个 **integral-value** （整数值）相比较。若发现相符的，就执行对应的语句（简单或复合语句，其中并不需要括号）。若没有发现相符的，就执行  **default** 语句。

在上面的定义中，大家会注意到每个 **case** 均以一个 **break** 结尾。这样可使执行流程跳转至 **switch** 主体的末尾。这是构建 **switch** 语句的一种传统方式，但 **break** 是可选的。若省略 **break，** 会继续执行后面的 **case** 语句的代码，直到遇到一个 **break** 为止。通常我们不想出现这种情况，但对有经验的程序员来说，也许能够善加利用。注意最后的 **default** 语句没有 **break**，因为执行流程已到了 **break** 的跳转目的地。当然，如果考虑到编程风格方面的原因，完全可以在 **default** 语句的末尾放置一个 **break**，尽管它并没有任何实际的作用。

**switch** 语句是一种实现多路选择的干净利落的一种方式（比如从一系列执行路径中挑选一个）。但它要求使用一个选择因子，并且必须是 **int** 或 **char** 那样的整数值。例如，假若将一个字串或者浮点数作为选择因子使用，那么它们在 switch 语句里是不会工作的。对于非整数类型（Java 7 以上版本中的 String 型除外），则必须使用一系列 **if** 语句。 在[下一章的结尾](./06-Housekeeping.md#枚举类型) 中，我们将会了解到**枚举类型**被用来搭配 **switch** 工作，并优雅地解决了这种限制。

下面这个例子可随机生成字母，并判断它们是元音还是辅音字母：

```java
// control/VowelsAndConsonants.java

// switch 执行语句的演示
import java.util.*;

public class VowelsAndConsonants {
  public static void main(String[] args) {
    Random rand = new Random(47);
    for(int i = 0; i < 100; i++) {
      int c = rand.nextInt(26) + 'a';
      System.out.print((char)c + ", " + c + ": ");
      switch(c) {
        case 'a':
        case 'e':
        case 'i':
        case 'o':
        case 'u': System.out.println("vowel");
                  break;
        case 'y':
        case 'w': System.out.println("Sometimes vowel");
                  break;
        default:  System.out.println("consonant");
      }
    }
  }
}
```

输出结果：

```
y, 121: Sometimes vowel
n, 110: consonant
z, 122: consonant
b, 98: consonant
r, 114: consonant
n, 110: consonant
y, 121: Sometimes vowel
g, 103: consonant
c, 99: consonant
f, 102: consonant
o, 111: vowel
w, 119: Sometimes vowel
z, 122: consonant
  ...
```

由于 `Random.nextInt(26)` 会产生 0 到 25 之间的一个值，所以在其上加上一个偏移量 `a`，即可产生小写字母。在 **case** 语句中，使用单引号引起的字符也会产生用于比较的整数值。

请注意 **case** 语句能够堆叠在一起，为一段代码形成多重匹配，即只要符合多种条件中的一种，就执行那段特别的代码。这时也应该注意将 **break** 语句置于特定 **case** 的末尾，否则控制流程会继续往下执行，处理后面的 **case**。在下面的语句中：

```java
int c = rand.nextInt(26) + 'a';
```

此处 `Random.nextInt()` 将产生 0~25 之间的一个随机 **int** 值，它将被加到 `a` 上。这表示 `a` 将自动被转换为 **int** 以执行加法。为了把 `c` 当作字符打印，必须将其转型为 **char**；否则，将会输出整数。

<!-- Switching on Strings -->

## switch 字符串

Java 7 增加了在字符串上 **switch** 的用法。 下例展示了从一组 **String** 中选择可能值的传统方法，以及新式方法：

```java
// control/StringSwitch.java

public class StringSwitch {
  public static void main(String[] args) {
    String color = "red";
    // 老的方式: 使用 if-then 判断
    if("red".equals(color)) {
      System.out.println("RED");
    } else if("green".equals(color)) {
      System.out.println("GREEN");
    } else if("blue".equals(color)) {
      System.out.println("BLUE");
    } else if("yellow".equals(color)) {
      System.out.println("YELLOW");
    } else {
      System.out.println("Unknown");
    }
    // 新的方法: 字符串搭配 switch
    switch(color) {
      case "red":
        System.out.println("RED");
        break;
      case "green":
        System.out.println("GREEN");
        break;
      case "blue":
        System.out.println("BLUE");
        break;
      case "yellow":
        System.out.println("YELLOW");
        break;
      default:
        System.out.println("Unknown");
        break;
    }
  }
}
```

输出结果：

```
RED
RED
```


一旦理解了 **switch**，你会明白这其实就是一个逻辑扩展的语法糖。新的编码方式能使得结果更清晰，更易于理解和维护。

作为 **switch** 字符串的第二个例子，我们重新访问 `Math.random()`。 它是否产生从 0 到 1 的值，包括还是不包括值 1 呢？在数学术语中，它属于 (0,1)、 [0,1)、(0,1] 、[0,1] 中的哪种呢？（方括号表示“包括”，而括号表示“不包括”）

下面是一个可能提供答案的测试程序。 所有命令行参数都作为 **String** 对象传递，因此我们可以 **switch** 参数来决定要做什么。 那么问题来了：如果用户不提供参数 ，索引到 `args` 的数组就会导致程序失败。 解决这个问题，我们需要预先检查数组的长度，若长度为 0，则使用**空字符串** `""` 替代；否则，选择 `args` 数组中的第一个元素：

```java
// control/RandomBounds.java

// Math.random() 会产生 0.0 和 1.0 吗？
// {java RandomBounds lower}
import onjava.*;

public class RandomBounds {
  public static void main(String[] args) {
    new TimedAbort(3);
    switch(args.length == 0 ? "" : args[0]) {
      case "lower":
        while(Math.random() != 0.0)
          ; // 保持重试
        System.out.println("Produced 0.0!");
        break;
      case "upper":
        while(Math.random() != 1.0)
          ; // 保持重试
        System.out.println("Produced 1.0!");
        break;
      default:
        System.out.println("Usage:");
        System.out.println("\tRandomBounds lower");
        System.out.println("\tRandomBounds upper");
        System.exit(1);
    }
  }
}
```

要运行该程序，请键入以下任一命令：

```java
java RandomBounds lower 
// 或者
java RandomBounds upper
```

使用 `onjava` 包中的 **TimedAbort** 类可使程序在三秒后中止。从结果来看，似乎 `Math.random()` 产生的随机值里不包含 0.0 或 1.0。 这就是该测试容易混淆的地方：若要考虑 0 至 1 之间所有不同 **double** 数值的可能性，那么这个测试的耗费的时间可能超出一个人的寿命了。 这里我们直接给出正确的结果：`Math.random()` 的结果集范围包含 0.0 ，不包含 1.0。 在数学术语中，可用 [0,1）来表示。由此可知，我们必须小心分析实验并了解它们的局限性。


## 本章小结

本章总结了我们对大多数编程语言中出现的基本特性的探索：计算，运算符优先级，类型转换，选择和迭代。 现在让我们准备好，开始步入面向对象和函数式编程的世界吧。 下一章的内容涵盖了 Java 编程中的重要问题：对象的[初始化和清理](./06-Housekeeping.md)。紧接着，还会介绍[封装](./07-Implementation-Hiding.md)（implementation hiding）的核心概念。

<!--下面是脚注-->
[^1]: 在早期的语言中，许多决策都是基于让编译器设计者的体验更好。 但在现代语言设计中，许多决策都是为了提高语言使用者的体验，尽管有时会有妥协 —— 这通常会让语言设计者后悔。

[^2]: **注意**，此处观点似乎难以让人信服，很可能只是一个因认知偏差而造成的[因果关系谬误](https://en.wikipedia.org/wiki/Correlation_does_not_imply_causation) 的例子。

<!-- 分页 -->

<div style="page-break-after: always;"></div>
