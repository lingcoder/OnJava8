[TOC]

<!-- Polymorphism -->

# 第九章 多态

> “曾有人问我，‘请问，巴贝奇先生，如果你把错误的数字输入机器，会得到正确的答案吗？’我无法恰当地理解产生这种问题的概念上的混淆。”
>
> ​																															——Charles Babbage(1791-1871)

在面向对象程序设计语言中，多态是继数据抽象和继承之后的第三种基本特性。

通过分离做什么和怎么做，多态从另一个角度把接口与实现分离开来。多态不仅改善了代码组织结构和可读性，而且还提高了代码的可扩展性（无论在项目最初创建时还是在项目需要添加新功能时）。

“封装”通过合并特征和行为来创建新的数据类型。“实现隐藏”则通过将细节“私有化“把接口和实现分离开来。这种类型的组织机制对那些拥有过程化程序设计背景的人来说，更容易理解。而多态的作用则是消除类型之间的耦合关系。在前一章中我们已经知道，继承允许将对象视为它自己本身的类型或其基类型来加以处理。这种能力极为重要，因为它允许将多种类型(从同一基类导出的)视为同一类型来处理，而同一份代码也就可以毫无差别地运行在这些不同类型之上了。多态方法调用允许一种类型表现出与其他相似类型之间的区别，只要它们都是从同一基类导出而来的。这种区别是根据方法行为的不同而表示出来的，虽然这些方法都可以通过同一个基类来调用。

在本章中，通过一些基本、简单的例子(这些例子中所有与多态无关的代码都被删掉，只剩下与多态有关的部分)，深人浅出地介绍多态(也称作动态绑定、后期绑定或运行时绑定)。

## 再议向上转型

在第上一章中我们已经知道，对象既可以作为它自己本身的类型使用，也可以作为它的基类型使用。而这种把对某个对象的引用视为对其基类型的引用的做法被称作向上转型——因为在继承树的画法中，基类是放置在子类上方的。

但是，这样做也有一个问题，具体看下面这个有关乐器的例子。

首先，既然几个例子都要演奏乐符(**Note**), 我们]就应该在包中单独创建一个**Note**类。

```java
// polymorphism/music/Note.java 
// Notes to play on musical instruments 
package polymorphism.music;

public enum Note {
    MIDDLE_C, C_SHARP, B_FLAT;// Etc.
}
```

枚举（enum）在[初始化和清理]()中介绍过

在这里，**Wind**是一种**Instrument**, 因此可以从**Instrument**类继承。

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

// Wind 对象同时也是 Instrument 对象
// 因为它们都有相同的接口
public class Wind extends Instrument {
    // Redefine interface method:
    @Override
    public void play(Note n) {
        System.out.println("Wind.play() " + n);
    }
}

// polymorphism/music/Music.java
// 继承 和 向上转型
// {java polymorphism.music.Music}
package polymorphism.music;

public class Music {
    public static void tune(Instrument i) {
        // ...
        i.play(Note.MIDDLE_C);
    }

    public static void main(String[] args) {
        Wind flute = new Wind();
        tune(flute); // 向上转型
    }
}
/* Output:
Wind.play() MIDDLE_C
*/
```

在**main()**方法中，我们把**Wind**引用传递给了**tune()**方法，并且不需要做任何类型转换。这样做是允许的，因为**Wind**从**Instrument**继承而来，所以**Instrument**的接口必定存在于**Wind**中。从**Wind**向上转型到**Instrument**可能会“缩小”接口，但不会比**Instrument**的全部接口更窄。

### 忘记对象类型

**Music.java**看起来似乎有些奇怪。为什么有人会要故意忘记对象的类型呢？在进行向上转型时，就会产生这种情况;并且如果让**tune()**方法直接接受一个**Wind**引用作为自己的参数，看起来可能会更为直观。但这样引发的一个重要问题是:如果那样做，就必须为系统内的每一种**Instrument**都编写一个新的**tune()**方法。假设按照这种推理，现在再加入**Stringed** (弦乐)和**Brass** (管乐)这两种**Instrument** (乐器):

```java
// polymorphism/music/Music2.java
// 重载而非向上转型
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
        tune(flute); // 不是向上转型
        tune(violin);
        tune(frenchHorn);
    }
}
/* Output:
Wind.play() MIDDLE_C
Stringed.play() MIDDLE_C
Brass.play() MIDDLE_C
*/
```

这样做是可行的，但有一个主要缺点：必须为添加的每一个新**Instrument**类编写特定类型的方法。这意味着在开始时就需要做更多的编程工作，同时也意味着如果以后想添加类似**tune()**的新方法，或者添加自**Instrument**导出的新类，仍需要做大量的工作。此外,如果我们忘记重载某个方法，编译器不会返回任何错误信息，这样关于类型的整个处理过程就变得难以操纵。

如果我们只写这样一个简单方法，它仅接收基类作为参数，而不是那些特殊的导出类。这样做会使情况变得更好吗？也就是说，如果我们不管导出类的存在，编写的代码只是与基类打交道，会不会更好呢?

这正是多态所允许的。然而，大多数程序员具有面向过程程序设计的背景，对多态的运作方式可能会有一点迷惑。

## 深入理解

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