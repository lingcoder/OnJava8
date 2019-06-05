[TOC]

<!-- Housekeeping -->

# 第六章 初始化和清理

"不安全"的编程是造成编程代价昂贵的罪魁祸首之一。有两个安全性问题：初始化和清理。C 语言中很多的 bug 都是因为程序员忘记初始化导致的。尤其是很多类库的使用者不知道如何初始化类库组件，甚至他们必须得去初始化。清理则是另一个特殊的问题，因为当你使用一个元素做完事后就不会去关心这个元素，所以你很容易忘记清理它。这样就造成了元素使用的资源滞留不会被回收，直到程序消耗完所有的资源（特别是内存）。

C++ 引入了构造器的概念，这是一个特殊的方法，每创建一个对象，这个方法就会被自动调用。Java 采用了构造器的概念，另外还使用了垃圾收集器（Garbage Collector, GC）去自动回收不再被使用的对象所占的资源。这一章将讨论初始化和清理的问题，以及在 Java 中对它们的支持。

<!-- Guaranteed Initialization with the Constructor -->

## 利用构造器保证初始化

你可能想为每个类创建一个 `initialize()` 方法，该方法名暗示着在使用类之前需要先调用它。不幸的是，用户必须得记得去调用它。在 Java 中，类的设计者通过构造器保证每个对象的初始化。如果一个类有构造器，那么 Java 会在用户使用对象之前（即对象刚创建完成）自动调用对象的构造器方法，从而保证初始化。下个挑战是如何命名构造器方法。存在两个问题：第一个是任何命名都可能与类中其他已有元素的命名冲突；第二个是编译器必须始终知道构造器方法名称，从而调用它。C++ 的解决方法看起来是最简单且最符合逻辑的，所以 Java 中使用了同样的方式：构造器名称与类名相同。在初始化过程中自动调用构造器方法是有意义的。

以下示例是包含了一个构造器的类：

```java
// housekeeping/SimpleConstructor.java
// Demonstration of a simple constructor

class Rock {
    Rock() { // 这是一个构造器
        System.out.print("Rock ");
    }
}

public class SimpleConstructor {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Rock();
        }
    }
}
```

输出：

````java
Rock Rock Rock Rock Rock Rock Rock Rock Rock Rock 
````

现在，当创建一个对象时：`new Rock()` ，内存被分配，构造器被调用。构造器保证了对象在你使用它之前进行了正确的初始化。

有一点需要注意，构造器方法名与类名相同，不需要符合首字母小写的编程风格。在 C++ 中，无参构造器被称为默认构造器，这个术语在 Java 出现之前使用了很多年。但是，出于一些原因，Java 设计者们决定使用无参构造器这个名称，我（作者）认为这种叫法笨拙而且没有必要，所以我打算继续使用默认构造器。Java 8 引入了 **default** 关键字修饰方法，所以算了，我还是用无参构造器的叫法吧。

跟其他方法一样，构造器方法也可以传入参数来定义如何创建一个对象。之前的例子稍作修改，使得构造器接收一个参数：

```java
// housekeeping/SimpleConstructor2.java
// Constructors can have arguments

class Rock2 {
    Rock2(int i) {
        System.out.print("Rock " + i + " ");
    }
}

public class SimpleConstructor2 {
    public static void main(String[] args) {
        for (int i = 0; i < 8; i++) {
            new Rock2(i);
        }
    }
}
```

输出：

```java
Rock 0 Rock 1 Rock 2 Rock 3 Rock 4 Rock 5 Rock 6 Rock 7
```

如果类 **Tree** 有一个构造方法，只接收一个参数用来表示树的高度，那么你可以像下面这样创建一棵树:

```java
Tree t = new Tree(12); // 12-foot 树
```

如果 **Tree(int)** 是唯一的构造器，那么编译器就不允许你以其他任何方式创建 **Tree** 类型的对象。

构造器消除了一类重要的问题，使得代码更易读。例如，在上面的代码块中，你看不到对 `initialize()` 方法的显式调用，而从概念上来看，`initialize()` 方法应该与对象的创建分离。在 Java 中，对象的创建与初始化是统一的概念，二者不可分割。

构造器是一种特殊的方法，因为它没有返回值。这与返回 **void** 值的方法不同，在返回 **void** 值的方法中，方法返回空值，但是你还是有选择返回一些其他值。构造器返回空值，你没有选择（**new** 表达式的确返回了新创建对象的引用，但是构造器自身并没有返回值 ）。假如有返回值，而且你可以自由选择，那么编译器得知道如何去处理这个返回值。

<!-- Method Overloading -->

## 方法重载

任何编程语言中都具备的一项重要特性就是命名。当你创建一个对象时，就会给此对象分配的内存空间命名。方法是行为的命名。你通过名字指代所有的对象，属性和方法。良好命名的系统易于理解和修改。就好比写散文——目的是与读者沟通。

将人类语言细微的差别映射到编程语言中会产生一个问题。通常，相同的词可以表达多种不同的含义——它们被"重载"了。特别是当含义的差别很小时，这会更加有用。你会说"清洗衬衫"、"清洗车"和"清洗狗"。而如果硬要这么说就会显得很愚蠢："以洗衬衫的方式洗衬衫"、"以洗车的方式洗车"和"以洗狗的方式洗狗"，因为听众根本不需要区分行为的动作。大多数人类语言都具有"冗余"性，所以即使漏掉几个词，你也能明白含义。你不需要对每个概念都使用不同的词汇——可以从上下文推断出含义。

大多数编程语言（尤其是 C 语言）要求为每个方法（在这些语言中经常称为方法）提供一个独一无二的标识符。

<!-- No-arg Constructors -->

## 无参构造器


<!-- The this Keyword -->
## this关键字


<!-- Cleanup: Finalization and Garbage Collection -->
## 垃圾回收器


<!-- Member Initialization -->
## 成员初始化


<!-- Constructor Initialization -->
## 构造器初始化

<!-- Array Initialization -->

## 数组初始化


<!-- Enumerated Types -->
## 枚举类型


<!-- Summary -->
## 本章小结

<!-- 分页 -->

<div style="page-break-after: always;"></div>