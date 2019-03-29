[TOC]

<!-- Reuse -->
# 第八章 复用
代码复用是使用面向对象编程（OOP）最令人信服的理由之一

对于像C语言等面向过程语言来说，“复用”通常指的是“复制代码”，对于任何语言来说都可以这样简单来做，但实际效果并不会好。就像Java里的所有东西一样，解决问题要围绕“类（class）”展开。你可以通过创建新的class来重用代码，但并不是白手起家，而是使用别人已经构建或调试过的已经存在的class。

如何在不弄脏已经存在的代码的情况下来使用class，是需要技巧的，在本章，你将学到两种方式来达到这个目的。
第一种方式相当直接了当：在新的class中创建已有class的对象。这种方式叫做“组合”（composition），因为新的class是已经存在的类的对象里一个组成部分，通过这种方式你可以重用代码的功能而不是它原本的形式。
第二种方式就比较微妙了，通过已有的class的“样式”，创建一种新的class。照字面理解就是，你可以采用已有class的形式，但编程的时候不会改动已有的class。这种方式叫做“继承”，编译器会处理大量的工作。继承是面向对象编程（OOP）的基础之一，由此带来的附加功能将会在“多态”章节中介绍。

组合和继承在很多语法和行为上是相似的（这其实是有道理的，因为他们都是用已有的类型构建新的类型）。在本章中，你会学到这两种代码重用的方法。

<!-- Composition Syntax -->
## 组合语法


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

