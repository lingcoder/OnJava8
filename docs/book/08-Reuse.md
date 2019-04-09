[TOC]

<!-- Reuse -->
# 第八章 复用


> 代码复用是面向对象编程（OOP）最具魅力的原因之一。

对于像 C 语言等面向过程语言来说，“复用”通常指的就是“复制代码”。任何语言都可通过简单复制来达到代码复用的目的，但是这样做的效果并不好。Java 围绕“类”（**Class**）来解决问题。我们可以直接使用别人构建或调试过的代码，而非创建新类、重新开始。

如何在不污染源代码的前提下使用现存代码是需要技巧的。在本章里，你将学习到两种方式来达到这个目的：

1. 第一种方式直接了当。在新类中创建现有类的对象。这种方式叫做“组合”（**Composition**），通过这种方式复用代码的功能，而非其形式。

2. 第二种方式更为微妙。通过现有类创建新类。照字面理解：采用现有类形式，又无需在编码时改动其代码，这种方式就叫做“继承”（**Inheritance**）。**继承**是面向对象编程（**OOP**）的重要基础之一。更多功能相关将在[多态](./09-Polymorphism.md)（**Polymorphism**）章节中介绍。

组合与继承的语法、行为上有许多相似的地方（这其实是有道理的，毕竟都是基于现有类型构建新的类型）。在本章中，你会学到这两种代码复用的方法。

<!-- Composition Syntax -->
## 组合语法
在前面的学习中，“组合”（**Composition**）已经被多次使用。你仅需要把对象的引用（**object references**）放置在一个新的类里，这就使用了组合。例如，假设你需要一个对象，其中内置了几个String对象，两个基本类型（**primitives**）的属性字段，一个自定义Class的对象。对于非基本类型对象，将引用直接放置在Class中，对于基本类型属性字段则仅进行声明（**define**）。
// reuse/SprinklerSystem.java
// Composition for code reuse
class WaterSource {
private String s;
WaterSource() {
System.out.println("WaterSource()");
s = "Constructed";
}
@Override
public String toString() { return s; }
}
public class SprinklerSystem {
private String valve1, valve2, valve3, valve4;
private WaterSource source = new WaterSource();
private int i;
private float f;
@Override
public String toString() {
return
"valve1 = " + valve1 + " " +
"valve2 = " + valve2 + " " +
"valve3 = " + valve3 + " " +
"valve4 = " + valve4 + "\n" +
"i = " + i + " " + "f = " + f + " " +
"source = " + source; // [1]
}
public static void main(String[] args) {
SprinklerSystem sprinklers = new SprinklerSystem();
System.out.println(sprinklers);
}
}
/* Output:
WaterSource()
valve1 = null valve2 = null valve3 = null valve4 = null
i = 0 f = 0.0 source = Constructed
*/

<!-- Inheritance Syntax -->
## 继承语法


<!-- Delegation -->
## 委托


<!-- Combining Composition and Inheritance -->
## 结合组合与继承


<!-- Choosing Composition vs. Inheritance -->
## 组合与继承的选择


<!-- protected -->
## protected


<!-- Upcasting -->
## 向上转型


<!-- The final Keyword -->
## final关键字


<!-- Initialization and Class Loading -->
## 类初始化和加载


<!-- Summary -->
## 本章小结


<!-- 分页 -->
<div style="page-break-after: always;"></div>

