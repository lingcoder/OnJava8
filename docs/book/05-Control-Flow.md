[TOC]

# 第五章 控制流

> 程序必须在执行过程中控制它的世界并做出选择。 在 Java 中，你需要使用执行控制语句来做出选择。

Java 使用了 C 的所有执行控制语句，因此对于熟悉 C/C++ 编程的人来说，这部分内容轻车熟路。 大多数面向过程编程语言都有共通的某种控制语句。在 Java 中，涉及的关键字包括 **if-else，while，do-while，for，return，break** 和选择语句 **switch**。 然而，Java 并不支持备受诟病的 **goto**（尽管它仍然是解决某些特殊问题最有效的方法）。 在 Java 中,我们依然可以进行类似的跳转,但较之典型的 **goto** 更受限制。


## true和flase

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

通过上一章的学习，我们知道任何关系运算符都可以产生条件语句。 **注意**：在 Java 中使用数值作为布尔值是非法的，即便这种操作在 C/C++ 中是被允许的（在这些语言中，“真”为非零，而“假”是零）。如果想在布尔测试中使用一个非布尔值，那么首先需要使用条件表达式来产生 **boolean** 类型的结果，例如 `if(a! = 0)`。

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

布尔表达式（*Boolean-expression*）必须生成 **boolean** 类型的结果，执行语句 `statement` 既可以是只包含单个分号 `;` 的简单语句，也可以是包含大括号 `{}` 的复合语句 —— 封闭在大括号内的一组简单语句。 凡本书中提及的“statement”一词，皆表示类似的执行语句。

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

<sub>**注解**：`else if` 并非是新关键词，它仅是 `else` 后紧跟的一个新的 `if` 语句。</sub>

尽管 Java 如 C/C++ 都属“自由格式”的编程语言，但通常我们会在控制流程语句中采用首尾缩进的规范，以便代码具备更好的可读性。

<!--Iteration Statements-->

## 迭代语句

循环由 **while**，**do-while** 和 **for** 控制，有时称为迭代语句。 语句会重复执行，直到起控制作用的 **布尔表达式** （Boolean expression）计算为“假”。 


### while

while循环的形式是：

```java
while(Boolean-expression) 
  statement
```

在循环开始时，会计算一次布尔表达式的值，而在语句的每次进一步迭代之前再次计算一次。

下面这个简单的例子将可产生随机数，直到满足特定条件：

```java
// control/WhileTest.java

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
/* Output: (First and Last 5 Lines)
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
*/
```

**condition()** 方法用到了 **Math** 库里的 **static** (静态)方法 **random()**，该方法的作用是产生 0 和 1 之间 (包括 0，但不包括 1) 的一个**double**值。**result** 的值是通过比较操作符<而得到它的，这个操作符产生 **boolean** 类型的结果。在打印 **boolean** 类型的值时，将自动地得到适合的字符串 **true** 或 **false**。while 的条件表达式意思是说：“只要 **condition()**返回 **true**，就重复执行循环体中的语句”。

### do-while

**do-while** 的格式如下：

```java
do 
	statement
while(Boolean-expression);
```

**while** 和 **do-while** 之间的唯一区别是 **do-while** 的语句总是至少执行一次，即使表达式第一次计算为 false 也是如此。 在 **while** 循环结构中，如果条件第一次就为 **false**，那么其中的语句根本不会执行。在实际应用中，**while** 比 **do-while** 更常用一点。

### for

**for** 循环可能是最常用的迭代形式。 该循环在第一次迭代之前执行初始化。 随后，它会执行条件测试，并在每次迭代结束时，进行某种形式的“步进”。**for** 循环的形式是：

```java
for(initialization; Boolean-expression; step)
  statement
```

初始化 (initialization) 表达式、布尔表达式 (Boolean-expression) ，或者步进 (step) 运算，都可以为空。在每次迭代之前都会测试表达式，并且一旦计算结果为 false，就会在 for 语句后面的行继续执行。 在每个循环结束时，执行一次步进。

**for**循环通常用于“计数”任务：

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
/* Output: (First 10 Lines)
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
*/
```

请注意，变量 **c** 在程序用到它的地方被定义的，也就是在for循环的控制表达式内，而不是在 **main()** 的开头。**c** 的范围是由 **for** 控制的语句。

像C这样的传统过程语言要求在块的开头定义所有变量。 当编译器创建块时，它可以为这些变量分配空间。 在Java和C ++中，您可以在整个块中传播变量声明，并在需要时定义它们。 这允许更自然的编码风格并使代码更容易理解。[1]

这个程序也使用了 **java.lang.Character** 包装器类，这个类不但能把 **char** 基本类型的值包装进对象，还提供了一些别的有用的方法。这里用到了 **static isLowerCase()** 方法来检查问题中的字符是否为小写字母。

<!--The Comma Operator-->

#### 逗号操作符

逗号运算符（不是逗号分隔符，逗号作为分隔符用于分隔定义和方法参数）在 Java 中只有一个用法：在**for** 循环的控制表达式中。 在控制表达式的初始化和步进控制中，您可以使用逗号分隔多个语句，并按顺序计算这些语句。使用逗号运算符，您可以在 **for** 语句中定义多个变量，但它们必须属于同一类型：

```java
// control/CommaOperator.java

public class CommaOperator {
  public static void main(String[] args) {
    for(int i = 1, j = i + 10; i < 5; i++, j = i * 2) {
      System.out.println("i = " + i + " j = " + j);
    }
  }
}
/* Output:
i = 1 j = 11
i = 2 j = 4
i = 3 j = 6
i = 4 j = 8
*/
```

**for** 语句中的 **int** 定义覆盖了 **i** 和 **j**，在初始化部分实际上可以拥有任意数量的具有相同类型的变量定义。在控制表达式中定义变量的能力仅限于 **for** 循环。 您不能将此方法与任何其他选择或迭代语句一起使用。

可以看到，无论在初始化还是在步进部分，语句都是顺序执行的。

## for-in语法 

Java 5引入了更简洁的 **for** 语法，用于数组和集合（您将在“数组和集合”章节中了解更多有关这些内容的信息）。 这有时被称为 **增强版for循环**，并且您将看到的大部分文档称为 *for-each* 语法，但Java 8添加了大量使用的 **forEach()**。 这会混淆术语，因此我称之为*for-in* （例如，在Python中，你实际上是 **for x in sequence**，所以有合理的先例）。 请记住，您可能会在其他地方看到它的不同叫法。

**for-in** 会自动为您生成每个项，因此你不必创建 **int** 变量去对由访问项构成的序列进行计数。 例如，假设您有一个 **float** 数组，并且您想要选取该数组中的每个元素：

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
/* Output:
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
*/
```

这个数组是用旧式的for循环组装的，因为在组装时必须按索引访问它。在下面这行中可以看到foreach方法：

```java
for(float x : f) {
```

这条语句定义了一个 **float** 类型的变量 **x**，继而将每一个 **f** 的元素赋值给 **x**。

任何返回一个数组的方法都可以使用 **for-in**。例如 **String** 类有一个方法 **toCharArray()**，它返回一个 **char** 数组，因此可以很容易地下像下面这样迭代在字符串里面的所有字符：

```java
// control/ForInString.java

public class ForInString {
  public static void main(String[] args) {
    for(char c : "An African Swallow".toCharArray())
      System.out.print(c + " ");
  }
}
/* Output:
A n   A f r i c a n   S w a l l o w
*/
```

就像在集合的章节所看到的，for-in还可以用于任何 **iterable** 对象。

许多 **for** 语句都会在一个整型值序列中步进，就像下面这样：

```java
for(int i = 0; i < 100; i++)
```

对于这样的语句，for-in语法将不起作用，除非先创建一个 **int** 数组。为了简化这些任务，我会在**onjava.Range** 包中创建一个名为 **range()** 的方法，它将自动生成恰当的数组。

隐藏实施过程这一章节（ Implementation Hiding ）介绍了静态导入。 但是，您无需了解这些详细信息即可开始使用此库。 你可以在 **import** 语句中看到 **static import** 语法：

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
/* Output:
0 1 2 3 4 5 6 7 8 9
5 6 7 8 9
5 8 11 14 17
20 17 14 11 8
*/
```

**range()** 方法已经被重载，重载表示相同的方法名可以具有不同的参数列表(你将很快学习重载)。**range()** 的第一个种重载形式是从0开始产生值，直到范围的上限，但不包括该上限。第二种重载形式是从第一个值开始产生值，直至比第二值小1的值为止。第三种形式有一个步进值，因此它每次的增量为该值。第四种 **range()** 表明还可以递减。**range()**是所谓生成器的一个非常简单的版本，有关生成器的内容将在本书稍后进行介绍。

**range()** 允许在更多地方使用 for-in 语法，因此可以说提高可读性。

请注意，**System.out.print()** 不会输出换行符，因此您可以分段输出一行。

*for-in* 语法不仅可以节省编写代码的时间。 更重要的是，它更容易阅读并说明你要做什么（获取数组的每个元素）而不是详细说明你是如何做到的（“我正在创建这个索引，所以我可以使用它来选择每个数组元素。“）。 本书只要有可能就会使用 *for-in* 语法。

## return

在Java中有几个关键字代表无条件分支，这意味着分支在没有任何测试的情况下发生。 这些包括**return**，**break**，**continue** 和跳转到带标签语句的方法，类似于其他语言中的 **goto**。

**return**关键字有两方面的用途：一方面指定一个方法返回什么值(假设它没有 **void** 返回值)，另一方面它会导致当前的方法退出，并返回那个值。可据此改写上面的 **IfElse.java** 里的 **test()** 方法，使其利用这些特点：

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
/* Output:
1
-1
0
*/
```

不需要 **else**，因为该方法在执行返回后不会继续执行。

如果在返回 **void** 的方法中没有 **return** 语句，则在该方法结束时会有一个隐式的 **return**，因此一个方法中并不总是需要包含 **return** 语句。 但是，如果您的方法声明它将返回除 **void** 之外的任何返回类型，则必须确保每个代码路径都返回一个值。

## break和continue

在任何迭代语句的主体部分，都可以使用 **break** 和 **continue** 来控制循环的流程。 其中，**break** 退出循环而不执行循环中的其余语句。 而 **continue** 停止执行当前的迭代，然后退回循环起始处，以开始下一次迭代。

下面这个程序向大家展示了 **break** 和 **continue** 在 **for** 和 **while** 循环中的例子：

```java
// control/BreakAndContinue.java
// Break and continue keywords

import static onjava.Range.*;

public class BreakAndContinue {
  public static void main(String[] args) {
    for(int i = 0; i < 100; i++) { // [1]
      if(i == 74) break; // Out of for loop
      if(i % 9 != 0) continue; // Next iteration
      System.out.print(i + " ");
    }
    System.out.println();
    // Using for-in:
    for(int i : range(100)) { // [2]
      if(i == 74) break; // Out of for loop
      if(i % 9 != 0) continue; // Next iteration
      System.out.print(i + " ");
    }
    System.out.println();
    int i = 0;
    // An "infinite loop":
    while(true) { // [3]
      i++;
      int j = i * 27;
      if(j == 1269) break; // Out of loop
      if(i % 10 != 0) continue; // Top of loop
      System.out.print(i + " ");
    }
  }
}
/* Output:
0 9 18 27 36 45 54 63 72
0 9 18 27 36 45 54 63 72
10 20 30 40
*/
```

**[1]** 在这个 **for** 循环中，**i** 的值永远不会达到 100；因为一旦 **i** 达到 74，**break** 语句就会中断循环。通常，只有在不知道中断条件何时满足时，才需要这样使用 **break**。因为**i** 不能被 9 整除，**continue** 语句就会使执行过程返回到循环的最开头(这使 **i** 递增)。如果能够整除，则将值显示出来。

**[2]** 使用 for-in 语法将产生相同的结果。

**[3]** 最后，可以看到一个“无穷 **while** 循环”的情况。然而，循环内部有一个 **break** 语句，可中止循环。请注意，**continue** 语句将控制权移回循环的顶部，而不会执行 **continue** 之后的任何操作。 因此，只有当 i 的值可被 10 整除时才会显示。在输出中，显示值 0，因为 0％9 产生0。

无限循环的另一种形式是 **for（;;）**。 编译器以同样的方式处理 **while（true）** 和 **for（;;）**，因此使用哪种取决于编程品味。

<!--The Infamous “Goto”-->

## 臭名昭著的goto

goto 关键字很早就在程序设计语言中出现。事实上，goto 是汇编语言的程序控制结构的始祖：“若条件A，则跳到这里；否则跳到那里”。若阅读由几乎所有编译器生成的汇编代码，就会发现程序控制里包含了许多跳转。然而，goto是在源码的级别跳转的，所以招致了不好的声誉。若程序总是从一个地方跳到另一个地方，还有什么办法能识别代码的流程呢？随着 Edsger Dijkstra 著名的 “Goto 有害” 论的问世，goto 便从此失宠。

事实上，真正的问题并不在于使用 goto，而在于 goto 的滥用。而且在一些少见的情况下，goto 是组织控制流程的最佳手段。

尽管 goto 仍是 Java 的一个保留字，但并未在语言中得到正式使用；Java 没有 goto。然而，在 break和 continue 这两个关键字的身上，我们仍然能看出一些goto的影子。它并不属于一次跳转，而是中断循环语句的一种方法。之所以把它们纳入 goto 问题中一起讨论，是由于它们使用了相同的机制：标签。

“标签”是后面跟一个冒号的标识符，就像下面这样：

```java
label1:
```

对 Java 来说，唯一用到标签的地方是在循环语句之前。进一步说，它实际需要紧靠在循环语句的前方——在标签和循环之间置入任何语句都是不明智的。而在循环之前设置标签的唯一理由是：我们希望在其中嵌套另一个循环或者一个开关。这是由于 break 和 continue 关键字通常只中断当前循环，但若随同标签使用，它们就会中断到存在标签的地方。如下所示：

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

**[1]** **break** 中断内部循环，并在外部循环结束。

**[2]** **continue** 移回内部循环的起始处。但在条件3中，**continue label1** 却同时中断内部循环以及外部循环，并移至 **label1** 处。

**[3]** 随后，它实际是继续循环，但却从外部循环开始。

**[4]** **break label1** 也会中断所有循环，并回到 **label1** 处，但并不重新进入循环。也就是说，它实际是完全中止了两个循环。

下面是 for 循环的一个例子：

```java
// control/LabeledFor.java
// For loops with "labeled break"/"labeled continue."

public class LabeledFor {
  public static void main(String[] args) {
    int i = 0;
    outer: // Can't have statements here
    for(; true ;) { // infinite loop
      inner: // Can't have statements here
      for(; i < 10; i++) {
        System.out.println("i = " + i);
        if(i == 2) {
          System.out.println("continue");
          continue;
        }
        if(i == 3) {
          System.out.println("break");
          i++; // Otherwise i never
               // gets incremented.
          break;
        }
        if(i == 7) {
          System.out.println("continue outer");
          i++; // Otherwise i never
               // gets incremented.
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
    // Can't break or continue to labels here
  }
}
/* Output:
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
*/
```

注意 **break** 会中断 **for** 循环，而且在抵达 **for** 循环的末尾之前，递增表达式不会执行。由于 **break** 跳过了递增表达式，所以递增会在 **i==3** 的情况下直接执行。在 **i==7** 的情况下，**continue outer** 语句也会到达循环顶部，而且也会跳过递增，所以它也是直接递增的。

如果没有 **break outer** 语句，就没有办法在一个内部循环里找到出外部循环的路径。这是由于  **break** 本身只能中断最内层的循环（对于 **continue** 同样如此）。 当然，若想在中断循环的同时退出方法，简单地用一个 **return** 即可。

下面这个例子向大家展示了带标签的 **break** 以及 **continue** 语句在 **while** 循环中的用法：

```java
// control/LabeledWhile.java
// "While" with "labeled break" and "labeled continue."

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
/* Output:
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
*/
```

同样的规则亦适用于 **while**：

(1) 简单的一个 **continue** 会退回最内层循环的开头（顶部），并继续执行。

(2) 带有标签的 **continue** 会到达标签的位置，并重新进入紧接在那个标签后面的循环。

(3) **break** 会中断当前循环，并移离当前标签的末尾。

(4) 带标签的 **break** 会中断当前循环，并移离由那个标签指示的循环的末尾。

大家要记住的重点是：在 Java 里需要使用标签的唯一理由就是因为有循环嵌套存在，而且想从多层嵌套中 **break** 或 **continue**。

**break** 和 **continue** 标签已经成为相对少用的推测特征(在前面的语言中很少或没有先例)，所以你很少在代码里看到它们。

在 **Dijkstra** 的 **“goto 有害”** 论中，他最反对的就是标签，而非 **goto**。随着标签在一个程序里数量的增多，他发现产生错误的机会也越来越多。标签和 **goto** 使我们难于对程序作静态分析。但是，Java 标签不会造成这方面的问题，因为它们的应用场合已经收到了限制，没有特别的方式用于概念程序的控制。由此也引出了一个有趣的问题：通过限制语句的能力，反而能使一项语言特性更加有用。

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

其中，integral-selector (整数选择因子)是一个能够产生整数值的表达式，**switch** 能够将这个表达式的结果与每个 integral-value (整数值)相比较。若发现相符的，就执行对应的语句（简单或复合语句，其中并不需要括号）。若没有发现相符的，就执行 default 语句。

在上面的定义中，大家会注意到每个 **case** 均以一个 **break** 结尾。这样可使执行流程跳转至 switch 主体的末尾。这是构建 **switch** 语句的一种传统方式，但 **break** 是可选的。若省略 break， 会继续执行后面的 **case** 语句的代码，直到遇到一个 **break** 为止。尽管通常不想出现这种情况，但对有经验的程序员来说，也许能够善加利用。注意最后的 **default** 语句没有 **break**，因为执行流程已到了break的跳转目的地。当然，如果考虑到编程风格方面的原因，完全可以在**default** 语句的末尾放置一个 **break**，尽管它并没有任何实际的用处。

switch 语句是实现多路选择的一种干净利落的一种方式（比如从一系列执行路径中挑选一个）。但它要求使用一个选择因子，并且必须是 int 或 char 那样的整数值。例如，假若将一个字串或者浮点数作为选择因子使用，那么它们在 switch 语句里是不会工作的。对于非整数类型，则必须使用一系列 if 语句。 

下面这个例子可随机生成字母，并判断它们是元音还是辅音字母：

```java
// control/VowelsAndConsonants.java

// Demonstrates the switch statement
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
/* Output: (First 13 Lines)
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
*/
```

由于 **Random.nextInt(26)** 会产生 0 到 26 之间的一个值，所以在其上加上一个偏移量 "a"，即可产生小写字母。在 **case** 语句中，使用单引号引起的字符也会产生用于比较的整数值。

请注意 **case** 语句能够堆叠在一起，为一段代码形成多重匹配，即只要符合多种条件中的一种，就执行那段特别的代码。这时也应该注意将 **break** 语句置于特定 **case** 的末尾，否则控制流程会简单地下移，处理后面的 **case**。

在下面的语句中：

```java
int c = rand.nextInt(26) + 'a';
```

此处 **Random.nextInt()** 将产生 0~25 之间的一个随机 **int** 值，它将被加到 **a** 上。这表示 **a** 将自动被转换为 **int** 以执行假发。为了把 **c** 当作字符打印，必须将其转型为 **char**；否则，将产生整数输出。


<!-- Switching on Strings -->
## switch字符串

Java 7 增加了在字符串上 **switch** 的用法。 此示例展示了您从一组 String 可能性中选择的旧方法，以及使用 switch 的新方法：

```java
// control/StringSwitch.java

public class StringSwitch {
  public static void main(String[] args) {
    String color = "red";
    // Old way: using if-then
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
    // New way: Strings in switch
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
/* Output:
RED
RED
*/
```

一旦理解了 switch，这种语法就是一个逻辑扩展。 结果更清晰，更易于理解和维护。

作为 **switch** 字符串的第二个例子，我们重新访问 Math.random()。 它是否产生从 0 到 1 的值，包括还是不包括值 “1”？ 在数学术语中，是（0,1），还是 [0,1]，还是（0,1）还是 [0,1]？ （方括号表示“包括”，而括号表示“不包括”。）

下面一个可能提供答案的测试程序。 所有命令行参数都作为 **String** 对象传递，因此我们可以 **switch** 参数来决定要做什么。 有一个问题：用户可能不提供任何参数，因此索引到 args 数组会导致程序失败。 要解决这个问题，我们检查数组的长度，如果它为零，我们使用一个空字符串，否则我们选择 **args** 数组中的第一个元素：

```java
// control/RandomBounds.java

// Does Math.random() produce 0.0 and 1.0?
// {java RandomBounds lower}
import onjava.*;

public class RandomBounds {
  public static void main(String[] args) {
    new TimedAbort(3);
    switch(args.length == 0 ? "" : args[0]) {
      case "lower":
        while(Math.random() != 0.0)
          ; // Keep trying
        System.out.println("Produced 0.0!");
        break;
      case "upper":
        while(Math.random() != 1.0)
          ; // Keep trying
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
or
java RandomBounds upper
```

使用 onjava 包中的 TimedAbort 类，程序在三秒后中止，因此看起来 Math.random() 从不生成 0.0 或 1.0。 但这就是这样一个实验可以欺骗的地方。 如果考虑0到1之间所有不同 **double** 类型的分数（double fractions）的数量，实验中达到任何一个值的可能性可能超过一台计算机甚至一个实验者的寿命。 结果是 0.0 包含在 **Math.random()** 的输出中，而 1.0 则不包括在内。 在数学术语中，它是 [0,1）。 您必须小心分析您的实验并了解它们的局限性。结果是 0.0 包含在 **Math.random()** 的输出中，而 1.0 则不包括在内。 在数学术语中，它是 [0,1）。 您必须小心分析您的实验并了解它们的局限性。



## 本章小结

本章总结了我们对大多数编程语言中出现的基本特征的探索：计算，运算符优先级，类型转换，选择和迭代。 现在，您已准备好开始采取措施，使您更接近面向对象和函数式编程的世界。 下一章将介绍初始化和清理对象的重要问题，接下来的章节将介绍隐藏实现细节（implementation hiding）的这一核心概念。

<!--下面是脚注-->

1. 在早期的语言中，大量的决策都是基于使编译器编写者的生活更轻松。 你会发现，在现代语言中，大多数设计决策都会让语言用户的生活更轻松，尽管有时会有妥协 - 这通常会让语言设计师感到后悔。

2. 请注意，这似乎是一个难以支持的主张，并且很可能是称为相关因果关系谬误的认知偏差的一个例子。

<!-- 分页 -->

<div style="page-break-after: always;"></div>