[TOC]

<!-- Polymorphism -->
# 第九章 多态

> 曾经有人请教我 ” Babbage 先生，如果输入错误的数字到机器中，会得出正确结果吗？“ 我无法理解产生如此问题的概念上的困惑。 —— Charles Babbage (1791 - 1871)

多态是面向对象编程语言中，继数据抽象和继承之外的第三个重要特性。

多态提供了另一个维度的接口与实现分离，以解耦做什么和怎么做。多态不仅能改善代码的组织，提高代码的可读性，而且能创建有扩展性的程序——无论在最初创建项目时还是在添加新特性时都可以“生长”的程序。

封装通过合并特征和行为来创建新的数据类型。隐藏实现通过将细节**私有化**把接口与实现分离。这种类型的组织机制对于有面向过程编程背景的人来说，更容易理解。而多态是消除类型之间的耦合。在上一章中，继承允许把一个对象视为它本身的类型或它的基类类型。这样就能把很多派生自一个基类的类型当作同一类型处理，因而一段代码就可以无差别地运行在所有不同的类型上了。多态方法调用允许一种类型表现出与相似类型的区别，只要这些类型派生自一个基类。这种区别是当你通过基类调用时，由方法的不同行为表现出来的。

在本章中，通过一些基本、简单的例子（这些例子中只保留程序中与多态有关的行为），你将逐步学习多态（也称为*动态绑定*或*后期绑定*或*运行时绑定*）。

<!-- Upcasting Revisited -->

## 向上转型回顾

在上一章中，你看到了如何把一个对象视作它的自身类型或它的基类类型。这种把一个对象引用当作它的基类引用的做法称为向上转型，因为继承图中基类一般都位于最上方。

同样你也在下面的音乐乐器例子中发现了问题。即然几个例子都要演奏乐符（**Note**），首先我们先在包中单独创建一个 Note 枚举类：

```java
// polymorphism/music/Note.java
// Notes to play on musical instruments
package polymorphism.music;

public enum Note {
    MIDDLE_C, C_SHARP, B_FLAT; // Etc.
}
```

枚举已经在”第 6 章初始化和清理“一章中介绍过了。

这里，**Wind** 是一种 **Instrument**；因此，**Wind** 继承 **Instrument**：

```java
// polymorphism/music/Instrument.java
package polymorphism.music;

class Instrument {
    public void play(Note n) {
        System.out.println("Instrument.play()");
    }
}

// polymorphism/music/Wind.java
package polymorphism.music;
// Wind objects are instruments
// because they have the same interface:
public class Wind extends Instrument {
    // Redefine interface method:
    @Override
    public void play(Note n) {
        System.out.println("Wind.play() " + n);
    }
}
```

**Music** 的方法 `tune()` 接受一个 **Instrument** 引用，同时也接受任何派生自 **Instrument** 的类引用：

```java
// polymorphism/music/Music.java
// Inheritance & upcasting
// {java polymorphism.music.Music}
package polymorphism.music;

public class Music {
    public static void tune(Instrument i) {
        // ...
        i.play(Note.MIDDLE_C);
    }
    
    public static void main(String[] args) {
        Wind flute = new Wind();
        tune(flute); // Upcasting
    }
}
```

输出：

```
Wind.play() MIDDLE_C
```

在 `main()` 中你看到了 `tune()` 方法传入了一个 **Wind** 引用，而没有做类型转换。这样做是允许的—— **Instrument** 的接口一定存在于 **Wind** 中，因此 **Wind** 继承了 **Instrument**。从 **Wind** 向上转型为 **Instrument** 可能“缩小”接口，但不会比 **Instrument** 的全部接口更少。

###  忘掉对象类型

**Music.java** 看起来似乎有点奇怪。为什么所有人都故意忘记掉对象类型呢？当向上转型时，就会发生这种情况，而且看起来如果 `tune()` 接受的参数是一个 **Wind** 引用会更为直观。这会带来一个重要问题：如果你那么做，就要为系统内 **Instrument** 的每种类型都编写一个新的 `tune()` 方法。假设按照这种推理，再增加 **Stringed** 和 **Brass** 这两种 **Instrument** :

```java
// polymorphism/music/Music2.java
// Overloading instead of upcasting
// {java polymorphism.music.Music2}
package polymorphism.music;

class Stringed extends Instrument {
    @Override
    public void play(Note n) {
        System.out.println("Stringed.play() " + n);
    }
}

class Brass extends Instrument {
    @Override
    public void play(Note n) {
        System.out.println("Brass.play() " + n);
    }
}

public class Music2 {
    public static void tune(Wind i) {
        i.play(Note.MIDDLE_C);
    }
    
    public static void tune(Stringed i) {
        i.play(Note.MIDDLE_C);
    }
    
    public static void tune(Brass i) {
        i.play(Note.MIDDLE_C);
    }
    
    public static void main(String[] args) {
        Wind flute = new Wind();
        Stringed violin = new Stringed();
        Brass frenchHorn = new Brass();
        tune(flute); // No upcasting
        tune(violin);
        tune(frenchHorn);
    }
}
```

输出：

```
Wind.play() MIDDLE_C
Stringed.play() MIDDLE_C
Brass.play() MIDDLE_C
```

这样行得通，但是有一个主要缺点：必须为添加的每个新 **Instrument** 类编写特定的方法。这意味着开始时就需要更多的编程，而且以后如果添加类似 `tune()` 的新方法或 **Instrument** 的新类型时，还有大量的工作要做。考虑到如果你忘记重载某个方法，编译器也不会提示你，这会造成类型的整个处理过程变得难以管理。

如果只写一个方法以基类作为参数，而不用管是哪个具体派生类，这样会变得更好吗？也就是说，如果忘掉派生类，编写的代码只与基类打交道，会不会更好呢？

这正是多态所允许的。但是大部分拥有面向过程编程背景的程序员会对多态的运作方式感到一些困惑。

<!-- The Twist -->

## 转机

运行程序后会看到 **Music.java** 的难点。**Wind.play()** 的输出结果正是我们期望的，但它看起来似乎不能产生我们所期望的结果。观察 `tune()` 方法：

```java
public static void tune(Instrument i) {
    // ...
    i.play(Note.MIDDLE_C);
}
```

它接受一个 **Instrument** 引用。那么编译器是如何知道这里的 **Instrument** 引用指向的是 **Wind**，而不是 **Brass** 或 **Stringed** 呢？编译器无法得知。为了深入理解这个问题，有必要研究一下*绑定*这个主题。

### 方法调用绑定

将一个方法调用和一个方法主体关联起来称作*绑定*。若绑定发生在程序运行前（如果有的话，由编译器和链接器实现），叫做*前期绑定*。你可能从来没有听说这个术语，因为它是面向过程语言不需选择默认的绑定方式，例如在 C 语言中就只有*前期绑定*这一种方法调用。

上述程序让人困惑的地方就在于前期绑定，因为编译器只知道一个 **Instrument** 引用，它无法得知究竟会调用哪个方法。

解决方法就是*后期绑定*，意味着在运行时根据对象的类型进行绑定。后期绑定也称为*动态绑定*或*运行时绑定*。当一种语言实现了后期绑定，就必须具有某种机制在运行时能判断对象的类型，从而调用恰当的方法。也就是说，编译器仍然不知道对象的类型，但是方法调用机制能找到正确的方法体并调用。每种语言的后期绑定机制都不同，但是可以想到，对象中一定存在某种类型信息。

Java 中除了 **static** 和 **final** 方法（**private** 方法也是隐式的 **final**）外，其他所有方法都是后期绑定。这意味着通常情况下，我们不需要判断后期绑定是否会发生——它自动发生。

为什么将一个对象指明为 **final** ？正如前一章所述，它可以防止方法被覆写。但更重要的一点可能是，它有效地”关闭了“动态绑定，或者说告诉编译器不需要对其进行动态绑定。这可以让编译器为 **final** 方法生成更高效的代码。然而，大部分情况下这样做不会对程序的整体性能带来什么改变，因此最好是为了设计使用 **final**，而不是为了提升性能而使用。

### 产生正确的行为



<!-- Constructors and Polymorphism -->

## 构造器和多态

<!-- Covariant Return Types -->

## 返回类型协变


<!-- Designing with Inheritance -->
## 使用继承设计


<!-- Summary -->
## 本章小结

<!-- 分页 -->

<div style="page-break-after: always;"></div>