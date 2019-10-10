[TOC]

<!-- Patterns -->
# 第二十五章 设计模式


<!-- The Pattern Concept -->
## 概念
最初，你可以将模式视为解决特定类问题的一种特别巧妙且有深刻见解的方法。这就像前辈已经从所有角度去解决问题，并提出了最通用，最灵活的解决方案。问题可能是你之前看到并解决过的问题，但你的解决方案可能没有你在模式中体现的那种完整性。

虽然它们被称为“设计模式”，但它们实际上并不与设计领域相关联。模式似乎与传统的分析、设计和实现的思维方式不同。相反，模式在程序中体现了一个完整的思想，因此它有时会出现在分析阶段或高级设计阶段。因为模式在代码中有一个直接的实现，所以你可能不会期望模式在低级设计或实现之前出现(而且通常在到达这些阶段之前，你不会意识到需要一个特定的模式)。

模式的基本概念也可以看作是程序设计的基本概念:添加抽象层。当你抽象一些东西的时候，就像在剥离特定的细节，而这背后最重要的动机之一是:
> **将易变的事物与不变的事物分开**

另一种方法是，一旦你发现程序的某些部分可能因某种原因而发生变化，你要保持这些变化不会引起整个代码中其他变化。 如果代码更容易理解，那么维护起来会更容易。

通常，开发一个优雅且易维护设计中最困难的部分是发现我称之为变化的载体（也就是最易改变的地方）。这意味着找到系统中最重要的变化，换而言之，找到变化会导致最严重后果的地方。一旦发现变化载体，就可以围绕构建设计的焦点。

因此，设计模式的目标是隔离代码中的更改。 如果以这种方式去看，你已经在本书中看到了设计模式。 例如，继承可以被认为是一种设计模式（虽然是由编译器实现的）。它允许你表达所有具有相同接口的对象（即保持相同的行为）中的行为差异（这就是变化的部分）。组合也可以被视为一种模式，因为它允许你动态或静态地更改实现类的对象，从而改变类的工作方式。

你还看到了设计模式中出现的另一种模式：迭代器（Java 1.0和1.1随意地将其称为枚举; Java 2 集合才使用Iterator）。当你逐个选择元素时并逐步处理，这会隐藏集合的特定实现。迭代器允许你编写通用代码，该代码对序列中的所有元素执行操作，而不考虑序列的构建方式。因此，你的通用代码可以与任何可以生成迭代器的集合一起使用。

即使模式是非常有用的，但有些人断言：
> **设计模式代表语言的失败。**

这是一个非常重要的见解，因为一个模式在 C++ 有意义，可能在JAVA或者其他语言中就没有意义。出于这个原因，所以一个模式可能出现在设计模式书上，不意味着应用于你的编程语言是有用的。

我认为“语言失败”这个观点是有道理的，但是我也认为这个观点过于简单化。如果你试图解决一个特定的问题，而你使用的语言没有直接提供支持你使用的技巧，你可以说这个是语言的失败。但是，你使用特定的技巧的频率的是多少呢？也许平衡是对的：当你使用特定的技巧的时候，你必须付出更多的努力，但是你又没有足够的理由去使得语言支持这个技术。另一方面，没有语言的支持，使用这种技术常常会很混乱，但是在语言支持下，你可能会改变编程方式（例如，Java 8流实现此目的）。

### 单例模式
也许单例模式是最简单的设计模式，它是一种提供一个且只有一个对象实例的方法。这在java库中使用，但是这有个更直接的示例：

```java
// patterns/SingletonPattern.java
interface Resource {
    int getValue();
    void setValue(int x);
}

/*
* 由于这不是从Cloneable基类继承而且没有添加可克隆性，
* 因此将其设置为final可防止通过继承添加可克隆性。
* 这也实现了线程安全的延迟初始化：
*/
final class Singleton {
    private static final class ResourceImpl implements Resource {
        private int i;
        private ResourceImpl(int i) {
            this.i = i;
        }
        public synchronized int getValue() {
            return i;
        }
        public synchronized void setValue(int x) {
            i = x;
        }
    }

    private static class ResourceHolder {
        private static Resource resource = new ResourceImpl(47);
    }
    public static Resource getResource() {
        return ResourceHolder.resource;
    }
}

public class SingletonPattern {
    public static void main(String[] args) {
        Resource r = Singleton.getResource();
        System.out.println(r.getValue());
        Resource s2 = Singleton.getResource();
        s2.setValue(9);
        System.out.println(r.getValue());
        try {     
             // 不能这么做，会发生：compile-time error（编译时错误）.     
             // Singleton s3 = (Singleton)s2.clone();    
             } catch(Exception e) {      
                 throw new RuntimeException(e);    
             }  
        } 
} /* Output: 47 9 */
```
创建单例的关键是防止客户端程序员直接创建对象。 在这里，这是通过在Singleton类中将Resource的实现作为私有类来实现的。

此时，你将决定如何创建对象。在这里，它是按需创建的，在第一次访问的时候创建。 该对象是私有的，只能通过public getResource（）方法访问。


懒惰地创建对象的原因是它嵌套的私有类resourceHolder在首次引用之前不会加载（在getResource（）中）。当Resource对象加载的时候，静态初始化块将被调用。由于JVM的工作方式，这种静态初始化是线程安全的。为保证线程安全，Resource中的getter和setter是同步的。

### 模式分类

“设计模式”一书讨论了23种不同的模式，分为以下三种类别（所有这些模式都围绕着可能变化的特定方面）。

1. **创建型**：如何创建对象。 这通常涉及隔离对象创建的细节，这样你的代码就不依赖于具体的对象的类型，因此在添加新类型的对象时不会更改。单例模式（Singleton）被归类为创作模式，本章稍后你将看到Factory Method的示例。

2. **构造型**：设计对象以满足特定的项目约束。它们处理对象与其他对象连接的方式，以确保系统中的更改不需要更改这些连接。

3. **行为型**：处理程序中特定类型的操作的对象。这些封装要执行的过程，例如解释语言、实现请求、遍历序列(如在迭代器中)或实现算法。本章包含观察者和访问者模式的例子。

《设计模式》一书中每个设计模式都有单独的一个章节，每个章节都有一个或者多个例子，通常使用C++，但有时也使用SmallTalk。 本章不重复设计模式中显示的所有模式，因为该书独立存在，应单独研究。 相反，你会看到一些示例，可以为你提供关于模式的理解以及它们如此重要的原因。

<!-- Building Application Frameworks -->
## 构建应用程序框架


<!-- Fronting for an Implementation -->
## 面向实施


<!-- Factories: Encapsulating Object Creation -->
## 工厂模式


<!-- Function Objects -->
## 函数对象


<!-- Changing the Interface -->
## 接口改变


<!-- Interpreter: Run-Time Flexibility -->
## 解释器


<!-- Callbacks -->
## 回调


<!-- Multiple Dispatching -->
## 多次调度


<!-- Pattern Refactoring -->
## 模式重构


<!-- Abstracting Usage -->
## 抽象用法


<!-- Multiple Dispatching -->
## 多次派遣


<!-- The Visitor Pattern -->
## 访问者模式


<!-- RTTI Considered Harmful? -->
## RTTI的优劣


<!-- Summary -->
## 本章小结





<!-- 分页 -->

<div style="page-break-after: always;"></div>