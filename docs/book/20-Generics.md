[TOC]

<!-- Generics -->
# 第二十章 泛型

> 普通的类和方法只能使用特定的类型：基本数据类型或类类型。如果编写的代码需要应用于多种类型，这种严苛的限制对代码的束缚就会很大。

多态是一种面向对象思想的泛化机制。你可以将方法的参数类型设为基类，这样的方法就可以接受任何派生类作为参数，包括暂时还不存在的类。这样的方法更通用，应用范围更广。在类内部也是如此，在任何使用特定类型的地方，基类意味着更大的灵活性。除了`final`类（或只提供私有构造函数的类）任何类型都可被扩展，所以大部分时候这种灵活性是自带的。

拘泥于单一的继承体系太过局限，因为只有继承体系中的对象才能适用基类作为参数的方法中。如果方法以接口而不是类作为参数，限制就宽松多了，只要实现了接口就可以。这给予调用方一种选项，通过调整现有的类来实现接口，满足方法参数要求。接口可以突破继承体系的限制。

即便是接口也还是有诸多限制。一旦指定了接口，它就要求你的代码必须使用特定的接口。而我们希望编写更通用的代码，能够适用“非特定的类型”，而不是一个具体的接口或类。

这就是泛型的概念，是 Java 5 的重大变化之一。泛型实现了*参数化类型*，这样你编写的组件（通常是集合）可以适用于多种类型。“泛型”这个术语的含义是“适用于很多类型”。编程语言中泛型出现的初衷是通过解耦类或方法与所使用的类型之间的约束，使得类或方法具备最宽泛的表达力。随后你会发现 Java 中泛型的实现并没有那么“泛”，你可能会质疑“泛型”这个词是否合适用来描述这一功能。

如果你从未接触过参数化类型机制，你会发现泛型对 Java 语言确实是个很有益的补充。在你实例化一个类型参数时，编译器会负责转型并确保类型的正确性。这是一大进步。

然而，如果你了解其他语言（例如 C++ ）的参数化机制，你会发现，Java 泛型并不能满足所有的预期。使用别人创建好的泛型相对容易，但是创建自己的泛型时，就会遇到很多意料之外的麻烦。

这并不是说 Java 泛型毫无用处。在很多情况下，它可以使代码更直接更优雅。不过，如果你见识过那种实现了更纯粹的泛型的编程语言，那么，Java 可能会令你失望。本章会介绍 Java 泛型的优点与局限。我会解释 Java 的泛型是如何发展成现在这样的，希望能够帮助你更有效地使用这个特性。

### 与 C++ 的比较

Java 的设计者曾说过，这门语言的灵感主要来自 C++ 。尽管如此，学习 Java 时基本不用参考 C++ 。

但是，Java 中的泛型需要与 C++ 进行对比，理由有两个：首先，理解 C++ *模板*（泛型的主要灵感来源，包括基本语法）的某些特性，有助于理解泛型的基础理念。同时，非常重要的一点是，你可以了解 Java 泛型的局限是什么，以及为什么会有这些局限。最终的目标是明确 Java 泛型的边界，让你成为一个程序高手。只有知道了某个技术不能做什么，你才能更好的做到所能做的（部分原因是，不必浪费时间在死胡同里）。

第二个原因是，在 Java 社区中，大家普遍对 C++ 模板有一种误解，而这种误解可能会令你在理解泛型的意图时产生偏差。

因此，本章中会介绍少量 C++ 模板的例子，仅当它们确实可以加深理解时才会引入。

<!-- Simple Generics -->
## 简单泛型

促成泛型出现的最主要的动机之一是为了创建*集合类*，参见[集合](./12-Collections.md)章节。集合用于存放要使用对象。数组也是如此，不过集合比数组更加灵活，功能更丰富。几乎所有程序在运行过程中都会涉及到一组对象，因此集合是可复用性最高的类库之一。

我们先看一个只能持有单个对象的类。这个类可以明确指定其持有的对象的类型：

```java
// generics/Holder1.java

class Automobile {}

public class Holder1 {
  private Automobile a;
  public Holder1(Automobile a) { this.a = a; }
  Automobile get() { return a; }
}
```

这个类的可复用性不高，它无法持有其他类型的对象。我们可不希望为碰到每个类型都编写一个新的类。

在 Java 5 之前，我们可以让这个类直接持有 `Object` 类型的对象：

```java
// generics/ObjectHolder.java

public class ObjectHolder {
  private Object a;
  public ObjectHolder(Object a) { this.a = a; }
  public void set(Object a) { this.a = a; }
  public Object get() { return a; }
  public static void main(String[] args) {
    ObjectHolder h2 = new ObjectHolder(new Automobile());
    Automobile a = (Automobile)h2.get();
    h2.set("Not an Automobile");
    String s = (String)h2.get();
    h2.set(1); // 自动装箱为 Integer
    Integer x = (Integer)h2.get();
  }
}
```

现在，`ObjectHolder` 可以持有任何类型的对象，在上面的示例中，一个 `ObjectHolder` 先后持有了三种不同类型的对象。

一个集合中存储多种不同类型的对象的情况很少见，通常而言，我们只会用集合存储同一种类型的对象。泛型的主要目的之一就是用来约定集合要存储什么类型的对象，并且通过编译器确保规约得以满足。

因此，与其使用 `Object` ，我们更希望先指定一个类型占位符，稍后再决定具体使用什么类型。要达到这个目的，需要使用*类型参数*，用尖括号括住，放在类名后面。然后在使用这个类时，再用实际的类型替换此类型参数。在下面的例子中，`T` 就是类型参数：

```java
// generics/GenericHolder.java

public class GenericHolder<T> {
  private T a;
  public GenericHolder() {}
  public void set(T a) { this.a = a; }
  public T get() { return a; }
  public static void main(String[] args) {
    GenericHolder<Automobile> h3 = new GenericHolder<Automobile>();
    h3.set(new Automobile()); // 此处有类型校验
    Automobile a = h3.get();  // 无需类型转换
    //- h3.set("Not an Automobile"); // 报错
    //- h3.set(1);  // 报错
  }
}
```

创建 `GenericHolder` 对象时，必须指明要持有的对象的类型，将其置于尖括号内，就像 `main()` 中那样使用。然后，你就只能在 `GenericHolder` 中存储该类型（或其子类，因为多态与泛型不冲突）的对象了。当你调用 `get()` 取值时，直接就是正确的类型。

这就是 Java 泛型的核心概念：你只需告诉编译器要使用什么类型，剩下的细节交给它来处理。

你可能注意到 `h3` 的定义非常繁复。在 `=` 左边有 `GenericHolder<Automobile>`, 右边又重复了一次。在 Java 5 中，这种写法被解释成“必要的”，但在 Java 7 中设计者修正了这个问题（新的简写语法随后成为备受欢迎的特性）。以下是简写的例子：

```java
// generics/Diamond.java

class Bob {}

public class Diamond<T> {
  public static void main(String[] args) {
    GenericHolder<Bob> h3 = new GenericHolder<>();
    h3.set(new Bob());
  }
}
```

注意，在 `h3` 的定义处，`=` 右边的尖括号是空的（称为“钻石语法”），而不是重复左边的类型信息。在本书剩余部分都会使用这种语法。

一般来说，你可以认为泛型和其他类型差不多，只不过它们碰巧有类型参数罢了。在使用泛型时，你只需要指定类型参数即可。



<!-- Generic Interfaces -->

## 泛型接口


<!-- Generic Methods -->
## 泛型方法


<!-- Building Complex Models -->
## 复杂模型构建


<!-- The Mystery of Erasure -->
## 泛型擦除


<!-- Compensating for Erasure -->
## 补偿擦除


<!-- Bounds -->
## 边界


<!-- Wildcards -->
## 通配符


<!-- Issues -->
## 问题


<!-- Self-Bounded Types -->
## 自我约束类型


<!-- Dynamic Type Safety -->
## 动态类型安全


<!-- Exceptions -->
## 泛型异常


<!-- Mixins -->
## 混入


<!-- Latent Typing -->
## 潜在类型


<!-- Compensating for the Lack of (Direct) Latent -->
## 补偿不足


<!-- Assisted Latent Typing in Java 8 -->
## 辅助潜在类型


<!-- Summary: Is Casting Really So Bad? -->
## 泛型的优劣











<!-- 分页 -->

<div style="page-break-after: always;"></div>