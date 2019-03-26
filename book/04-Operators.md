[TOC]

<!-- Operators -->
# 第四章 运算符


>运算符操纵数据。

Java 是从 C++ 的基础上做了一些改进和简化发展而成的。对于 C/C++ 程序员来说，Java 的运算符并不陌生。如果你已了解 C 或 C++，大可以跳过本章和下一章，直接阅读 Java 与 C/C++ 不同的地方。

如果理解这两章的内容对你来说还有点困难，那么我推荐你先了解下 *Thinking in C* 再继续后面的学习。 这本书现在可以在 [www.OnJava8.com](http://www.OnJava8.com]) 上免费下载。它的内容包含音频讲座、幻灯片、练习和解决方案，专门用于帮助你快速掌握学习 Java 所需的基础知识。


<!-- Using-Java-Operators -->
## 使用说明


运算符接受一个或多个参数并生成新值。这个参数与普通方法调用的形式是不同的，但效果是相同的。加法 `+`，减法 `-`，乘法 `*`，除法 `/` 以及赋值 `=` 在任何编程语言中的工作方式都是类似的。所有运算符都能根据自己的运算对象生成一个值。除此以外，一个运算符可改变运算对象的值，这叫作“副作用”（**Side Effect**）。算符最常见的用途就是修改自己的运算对象，从而产生副作用。但要注意生成的值亦可由没有副作用的运算符生成。

几乎所有运算符都只能操作基本类型（*Primitives*）。唯一的例外是`=`、`==`和`!=`，它们能操作所有对象（这也是令人混淆的一个地方）。除此以外，**String** 类支持`+`和`+=`。


<!-- Precedence -->
## 优先级


运算符的优先级决定了存在多个运算符时一个表达式各部分的计算顺序。Java 对计算顺序作出了特别的规定。其中，最简单的规则就是乘法和除法在加法和减法之前完成。程序员经常都会忘记其他优先级规则，所以应该用括号明确规定计算顺序。代码示例:

```JAVA
// operators/Precedence.java
public class Precedence {
    
    public static void main(String[] args) {
        int x = 1, y = 2, z = 3;
        int a = x + y - 2/2 + z; // [1]
        int b = x + (y - 2)/(2 + z); // [2]
        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }
}
```

 输出结果:

```
    a = 5
    b = 1
```

这些语句看起来大致相同，但从输出中我们可以看出它们具有非常不同的含义，具体取决于括号的使用。

我们注意到，在 `System.out.println()` 语句中使用了`+`运算符。 但是在这里 `+`代表的意思是字符串连接符。编译器会将`+`连接的非字符串尝试转换为字符串。上例中的输出结果说明了 a 和 b 都已经被转化成了字符串。


<!-- Assignment -->
## 赋值


<!-- Mathematical Operators -->
## 算术运算符


<!-- Auto-Increment-and-Decrement -->
## 自动递增和递减


<!-- Relational-Operators -->
## 关系运算符


<!-- Logical-Operators -->
## 逻辑运算符


<!-- Literals -->
## 字面值


<!-- Bitwise-Operators -->
## 按位运算符


<!-- Shift Operators -->
## 移位运算符


<!-- Ternary-if-else-Operator -->
## 三元运算符


<!-- String-Operator-+-and-+= -->
## 字符串运算符


<!-- Common-Pitfalls-When-Using-Operators -->
## 常见陷阱


<!-- Casting-Operators -->
## 类型转换


<!-- Java-Has-No-sizeof -->
## Java没有sizeof


<!-- A-Compendium-of-Operators -->
## 运算符总结


<!-- Summary -->
## 本章小结


<!-- 分页 -->
<div style="page-break-after: always;"></div>
