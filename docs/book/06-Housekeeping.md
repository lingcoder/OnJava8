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

大多数编程语言（尤其是 C 语言）要求为每个方法（在这些语言中经常称为函数）提供一个独一无二的标识符。所以，你不能有一个 `print()` 函数既能打印整型，也能打印浮点型——每个函数名都必须不同。

在 Java (C++) 中，还有一个因素也促使了必须使用方法重载：构造器。因为构造器方法名肯定是与类名相同，所以一个类中只会有一个构造器名。那么你怎么通过不同的方式创建一个对象呢？例如，你想创建一个类，这个类的初始化方式有两种：一种是标准化方式，另一种是从文件中读取信息的方式。你需要两个构造器：无参构造器和有一个 **String** 类型参数的构造器，该参数传入文件名。两个构造器具有相同的名字——与类名相同。因此，方法重载是必要的，它允许方法具有相同的方法名但接收的参数不同。尽管方法重载对于构造器是重要的，但是也可以对任何方法很方便地进行重载。

下例展示了如何重载构造器和方法：

```java
// housekeeping/Overloading.java
// Both constructor and ordinary method overloading

class Tree {
    int height;
    Tree() {
        System.out.println("Planting a seedling");
        height = 0;
    }
    Tree(int initialHeight) {
        height = initialHeight;
        System.out.println("Creating new Tree that is " + height + " feet tall");
    }
    void info() {
        System.out.println("Tree is " + height + " feet tall");
    }
    void info(String s) {
        System.out.println(s + ": Tree is " + height + " feet tall");
    }
}
public class Overloading {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            Tree t = new Tree(i);
            t.info();
            t.info("overloaded method");
        }
        new Tree(); 
    }
}
```

输出：

```java
Creating new Tree that is 0 feet tall
Tree is 0 feet tall
overloaded method: Tree is 0 feet tall
Creating new Tree that is 1 feet tall
Tree is 1 feet tall
overloaded method: Tree is 1 feet tall
Creating new Tree that is 2 feet tall
Tree is 2 feet tall
overloaded method: Tree is 2 feet tall
Creating new Tree that is 3 feet tall
Tree is 3 feet tall
overloaded method: Tree is 3 feet tall
Creating new Tree that is 4 feet tall
Tree is 4 feet tall
overloaded method: Tree is 4 feet tall
Planting a seedling
```

一个 **Tree** 对象既可以是一颗树苗，使用无参构造器创建，也可以是一颗在温室中已长大的树，已经有一定高度，这时候，就需要使用有参构造器创建。

你也许想以多种方式调用 `info()` 方法。比如，如果你想打印额外的消息，就可以使用 `info(String)` 方法。如果你无话可说，就可以使用 `info()` 方法。用两个命名定义完全相同的概念看起来很奇怪，而使用方法重载，你就可以使用一个命名来定义一个概念。

### 区分重载方法

如果两个方法命名相同，Java是怎么知道你调用的是哪个呢？有一条简单的规则：每个被重载的方法必须有独一无二的参数列表。你稍微思考下，就会很明了了，除了通过参数列表的不同来区分两个相同命名的方法，其他也没什么方式了。你甚至可以根据参数列表中的参数顺序来区分不同的方法，尽管这会造成代码难以维护。例如：

```java
// housekeeping/OverloadingOrder.java
// Overloading based on the order of the arguments

public class OverloadingOrder {
    static void f(String s, int i) {
        System.out.println("String: " + s + ", int: " + i);
    }

    static void f(int i, String s) {
        System.out.println("int: " + i + ", String: " + s);
    }

    public static void main(String[] args) {
        f("String first", 1);
        f(99, "Int first");
    }
}
```

输出：

```java
String: String first, int: 1
int: 99, String: Int first
```

两个 `f()` 方法具有相同的参数，但是参数顺序不同，根据这个就可以区分它们。

### 重载与基本类型

基本类型可以自动从较小的类型转型为较大的类型。当这与重载结合时，这会令人有点困惑，下面是一个这样的例子：

```java
// housekeeping/PrimitiveOverloading.java
// Promotion of primitives and overloading

public class PrimitiveOverloading {
    void f1(char x) {
        System.out.print("f1(char)");
    }
    void f1(byte x) {
        System.out.print("f1(byte)");
    }
    void f1(short x) {
        System.out.print("f1(short)");
    }
    void f1(int x) {
        System.out.print("f1(int)");
    }
    void f1(long x) {
        System.out.print("f1(long)");
    }
    void f1(float x) {
        System.out.print("f1(float)");
    }
    void f1(double x) {
        System.out.print("f1(double)");
    }
    void f2(byte x) {
        System.out.print("f2(byte)");
    }
    void f2(short x) {
        System.out.print("f2(short)");
    }
    void f2(int x) {
        System.out.print("f2(int)");
    }
    void f2(long x) {
        System.out.print("f2(long)");
    }
    void f2(float x) {
        System.out.print("f2(float)");
    }
    void f2(double x) {
        System.out.print("f2(double)");
    }
    void f3(short x) {
        System.out.print("f3(short)");
    }
    void f3(int x) {
        System.out.print("f3(int)");
    }
    void f3(long x) {
        System.out.print("f3(long)");
    }
    void f3(float x) {
        System.out.print("f3(float)");
    }
    void f3(double x) {
        System.out.print("f3(double)");
    }
    void f4(int x) {
        System.out.print("f4(int)");
    }
    void f4(long x) {
        System.out.print("f4(long)");
    }
    void f4(float x) {
        System.out.print("f4(float)");
    }
    void f4(double x) {
        System.out.print("f4(double)");
    }
    void f5(long x) {
        System.out.print("f5(long)");
    }
    void f5(float x) {
        System.out.print("f5(float)");
    }
    void f5(double x) {
        System.out.print("f5(double)");
    }
    void f6(float x) {
        System.out.print("f6(float)");
    }
    void f6(double x) {
        System.out.print("f6(double)");
    }
    void f7(double x) {
        System.out.print("f7(double)");
    }
    void testConstVal() {
        System.out.print("5: ");
        f1(5);f2(5);f3(5);f4(5);f5(5);f6(5);f7(5);
        System.out.println();
    }
    void testChar() {
        char x = 'x';
        System.out.print("char: ");
        f1(x);f2(x);f3(x);f4(x);f5(x);f6(x);f7(x);
        System.out.println();
    }
    void testByte() {
        byte x = 0;
        System.out.print("byte: ");
        f1(x);f2(x);f3(x);f4(x);f5(x);f6(x);f7(x);
        System.out.println();
    }
    void testShort() {
        short x = 0;
        System.out.print("short: ");
        f1(x);f2(x);f3(x);f4(x);f5(x);f6(x);f7(x);
        System.out.println();
    }
    void testInt() {
        int x = 0;
        System.out.print("int: ");
        f1(x);f2(x);f3(x);f4(x);f5(x);f6(x);f7(x);
        System.out.println();
    }
    void testFloat() {
        float x = 0;
        System.out.print("float: ");
        f1(x);f2(x);f3(x);f4(x);f5(x);f6(x);f7(x);
        System.out.println();
    }
    void testDouble() {
        double x = 0;
        System.out.print("double: ");
        f1(x);f2(x);f3(x);f4(x);f5(x);f6(x);f7(x);
        System.out.println();
    }

    public static void main(String[] args) {
        PrimitiveOverloading p = new PrimitiveOverloading();
        p.testConstVal();
        p.testChar();
        p.testByte();
        p.testShort();
        p.testInt();
        p.testFloat();
        p.testDouble();
    }
}
```

输出：

```java
5: f1(int)f2(int)f3(int)f4(int)f5(long)f6(float)f7(double)
char: f1(char)f2(int)f3(int)f4(int)f5(long)f6(float)f7(double)
byte: f1(byte)f2(byte)f3(short)f4(int)f5(long)f6(float)f7(double)
short: f1(short)f2(short)f3(short)f4(int)f5(long)f6(float)f7(double)
int: f1(int)f2(int)f3(int)f4(int)f5(long)f6(float)f7(double)
float: f1(float)f2(float)f3(float)f4(float)f5(float)f6(float)f7(double)
double: f1(double)f2(double)f3(double)f4(double)f5(double)f6(double)f7(double)
```

如果传入的参数类型大于方法期望接收的参数类型，你必须首先做下转换，如果你不做的话，编译器就会报错。

### 返回值的重载

经常会有人困惑，"为什么只能通过类名和参数列表，不能通过方法的返回值区分方法呢?"。例如以下两个方法，它们有相同的命名和参数，但是很容易区分：

```java
void f(){}
int f() {return 1;}
```

有些情况下，编译器很容易就可以从上下文准确推断出该调用哪个方法，如 `int x = f()`。

但是，你可以调用一个方法且忽略返回值。这叫做调用一个函数的副作用，因为你不在乎返回值，只是想利用方法做些事。所以如果你直接调用 `f()`，Java 编译器就不知道你想调用哪个方法，阅读者也不明所以。因为这个原因，所以你不能根据返回值类型区分重载的方法。为了支持新特性，Java 8 在一些具体情形下提高了猜测的准确度，但是通常来说并不起作用。

<!-- No-arg Constructors -->

## 无参构造器

如前文所说，一个无参构造器就是不接收参数的构造器，用来创建一个"默认的对象"。如果你创建一个类，类中没有构造器，那么编译器就会自动为你创建一个无参构造器。例如：

```java
// housekeeping/DefaultConstructor.java
class Bird {}
public class DefaultConstructor {
    public static void main(String[] args) {
        Bird bird = new Bird(); // 默认的
    }
}
```

表达式 `new Bird()` 创建了一个新对象，调用了无参构造器，尽管在 **Bird** 类中并没有显式的定义无参构造器。试想如果没有构造器，我们如何创建一个对象呢。但是,一旦你显式地定义了构造器（无论有参还是无参），编译器就不会自动为你创建无参构造器。如下：

```java
// housekeeping/NoSynthesis.java
class Bird2 {
    Bird2(int i) {}
    Bird2(double d) {}
}
public class NoSynthesis {
    public static void main(String[] args) {
        //- Bird2 b = new Bird2(); // No default
        Bird2 b2 = new Bird2(1);
        Bird2 b3 = new Bird2(1.0);
    }
}
```

如果你调用了 `new Bird2()` ，编译器会提示找不到匹配的构造器。当类中没有构造器时，编译器会说"你一定需要构造器，那么让我为你创建一个吧"。但是如果类中有构造器，编译器会说"你已经写了构造器了，所以肯定知道你在做什么，如果你没有创建默认构造器，说明你本来就不需要"。

<!-- The this Keyword -->

## this关键字

对于两个相同类型的对象 **a** 和 **b**，你可能在想如何调用这两个对象的 `peel()` 方法：

```java
// housekeeping/BananaPeel.java

class Banana {
    void peel(int i) {
        /*...*/
    }
}
public class BananaPeel {
    public static void main(String[] args) [
        Banana a = new Banana(), b = new Banana();
        a.peel(1);
        b.peel(2);
    ]
}
```

如果只有一个方法 `peel()` ，那么怎么知道调用的是对象 **a** 的 `peel()`方法还是对象 **b** 的 `peel()` 方法呢？编译器做了一些底层工作，所以你可以像这样编写代码。`peel()` 方法中第一个参数隐密地传入了一个指向操作对象的

引用。因此，上述例子中的方法调用像下面这样：

```java
Banana.peel(a, 1)
Banana.peel(b, 1)
```

这是在内部实现的，你不可以直接这么编写代码，编译器不会接受，但能说明到底发生了什么。假设现在在方法内部，你想获得对当前对象的引用。但是，对象引用是被秘密地传达给编译器——并不在参数列表中。方便的是，有一个关键字: **this** 。**this** 关键字只能在非静态方法内部使用。当你调用一个对象的方法时，**this** 生成了一个对象引用。你可以像对待其他引用一样对待这个引用。如果你在一个类的方法里调用其他该类中的方法，不要使用 **this**，直接调用即可，**this** 自动地应用于其他方法上了。因此你可以像这样：

```java
// housekeeping/Apricot.java

public class Apricot {
    void pick() {
        /* ... */
    }

    void pit() {
        pick();
        /* ... */
    }
}
```

在 `pit()` 方法中，你可以使用 `this.pick()`，但是没有必要。编译器自动为你做了这些。**this** 关键字只用在一些必须显式使用当前对象引用的特殊场合。例如，用在 **return** 语句中返回对当前对象的引用。

```java
// housekeeping/Leaf.java
// Simple use of the "this" keyword

public class Leaf {

    int i = 0;

    Leaf increment() {
        i++;
        return this;
    }

    void print() {
        System.out.println("i = " + i);
    }

    public static void main(String[] args) {
        Leaf x = new Leaf();
        x.increment().increment().increment().print();
    }
}
```

输出：

```
i = 3
```

因为 `increment()` 通过 **this** 关键字返回当前对象的引用，因此在相同的对象上可以轻易地执行多次操作。

**this** 关键字在向其他方法传递当前对象时也很有用：

```java
// housekeeping/PassingThis.java

class Person {
    public void eat(Apple apple) {
        Apple peeled = apple.getPeeled();
        System.out.println("Yummy");
    }
}

public class Peeler {
    static Apple peel(Apple apple) {
        // ... remove peel
        return apple; // Peeled
    }
}

public class Apple {
    Apple getPeeled() {
        return Peeler.peel(this);
    }
}

public class PassingThis {
    public static void main(String[] args) {
        new Person().eat(new Apple());
    }
}
```

输出：

```
Yummy
```

**Apple** 因为某些原因（比如说工具类中的方法在多个类中重复出现，你不想代码重复），必须调用一个外部工具方法 `Peeler.peel()` 做一些行为。必须使用 **this** 才能将自身传递给外部方法。

### 在构造器中调用构造器

当你在一个类中写了多个构造器，有时你想在一个构造器中调用另一个构造器来避免代码重复。你通过 **this** 关键字实现这样的调用。

通常当你说 **this**，意味着"这个对象"或"当前对象"，它本身生成对当前对象的引用。在一个构造器中，当你给 **this** 一个参数列表时，它是另一层意思。它通过最直接的方式显式地调用匹配参数列表的构造器：

```java
// housekeeping/Flower.java
// Calling constructors with "this"

public class Flower {
    int petalCount = 0;
    String s = "initial value";

    Flower(int petals) {
        petalCount = petals;
        System.out.println("Constructor w/ int arg only, petalCount = " + petalCount);
    }

    Flower(String ss) {
        System.out.println("Constructor w/ string arg only, s = " + ss);
        s = ss;
    }

    Flower(String s, int petals) {
        this(petals);
        //- this(s); // Can't call two!
        this.s = s; // Another use of "this"
        System.out.println("String & int args");
    }

    Flower() {
        this("hi", 47);
        System.out.println("no-arg constructor");
    }

    void printPetalCount() {
        //- this(11); // Not inside constructor!
        System.out.println("petalCount = " + petalCount + " s = " + s);
    }

    public static void main(String[] args) {
        Flower x = new Flower();
        x.printPetalCount();
    }
}
```

输出：

```
Constructor w/ int arg only, petalCount = 47
String & int args
no-arg constructor
petalCount = 47 s = hi
```

从构造器 `Flower(String s, int petals)` 可以看出，其中只能通过 **this** 调用一次构造器。另外，必须首先调用构造器，否则编译器会报错。这个例子同样展示了 **this** 的另一个用法。参数列表中的变量名 **s** 和成员变量名 **s** 相同，会引起混淆。你可以通过 `this.s` 表明你指的是成员变量 **s**，从而避免重复。你经常会在 Java 代码中看到这种用法，同时本书中也会多次出现这种写法。在 `printPetalCount()` 方法中，编译器不允许你在一个构造器之外的方法里调用构造器。

### static 的含义

记住了 **this** 关键字的内容，你会对 **static** 修饰的方法有更加深入的理解：**static** 方法中不会存在 **this**。你不能在静态方法中调用非静态方法（反之可以）。静态方法是为类而创建的，不需要任何对象。事实上，这就是静态方法的主要目的，静态方法看起来就像全局方法一样，但是 Java 中不允许全局方法，一个类中的静态方法可以被其他的静态方法和静态属性访问。一些人认为静态方法不是面向对象的，因为它们的确具有全局方法的语义。使用静态方法，因为不存在 **this**，所以你没有向一个对象发送消息。的确，如果你发现代码中出现了大量的 **static** 方法，就该重新考虑自己的设计了。然而，**static** 的概念很实用，许多时候都要用到它。至于它是否真的"面向对象"，就留给理论家去讨论吧。

<!-- Cleanup: Finalization and Garbage Collection -->

## 垃圾回收器

程序员都了解初始化的重要性，但通常会忽略清理的重要性。毕竟，谁会去清理一个 **int** 呢？但是使用完一个对象就不管它并非总是安全的。Java 中有垃圾回收器回收无用对象占用的内存。但现在考虑一种特殊情况：你创建的对象不是通过 **new** 来分配内存的，而垃圾回收器只知道如何释放用 **new** 创建的对象的内存，所以它不知道如何回收不是 **new** 分配的内存。为了处理这种情况，Java 允许在类中定义一个名为 `finalize()` 的方法。

它的工作原理"假定"是这样的：当垃圾回收器准备回收对象的内存时，首先会调用其 `finalize()` 方法，并在下一轮的垃圾回收动作发生时，才会真正回收对象占用的内存。所以如果你打算使用 `finalize()` ，就能在垃圾回收时做一些重要的清理工作。`finalize()` 是一个潜在的编程陷阱，因为一些程序员（尤其是 C++ 程序员）会一开始把它误认为是 C++ 中的析构函数（C++ 在销毁对象时会调用这个函数）。所以有必要明确区分一下：在 C++ 中，对象总是被销毁的（在一个 bug-free 的程序中），而在 Java 中，对象并非总是被垃圾回收，或者换句话说：

1. 对象可能不被垃圾回收。
2. 垃圾回收不等同于析构。

这意味着在你不再需要某个对象之前，如果必须执行某些动作，你得自己去做。Java 没有析构器或类似的概念，所以你必须得自己创建一个普通的方法完成这项清理工作。例如，对象在创建的过程中会将自己绘制到屏幕上。如果不是明确地从屏幕上将其擦除，它可能永远得不到清理。如果在 `finalize()` 方法中加入某种擦除功能，那么当垃圾回收发生时，`finalize()` 方法被调用（不保证一定会发生），图像就会被擦除，要是"垃圾回收"没有发生，图像则仍会保留下来。

也许你会发现，只要程序没有濒临内存用完的那一刻，对象占用的空间就总也得不到释放。如果程序执行结束，而垃圾回收器一直没有释放你创建的任何对象的内存，则当程序退出时，那些资源会全部交还给操作系统。这个策略是恰当的，因为垃圾回收本身也有开销，要是不使用它，那就不用支付这部分开销了。

### `finalize()` 的用途

如果你不能将 `finalize()` 作为通用的清理方法，那么这个方法有什么用呢？

这引入了要记住的第3点：

3. 垃圾回收只与内存有关。

也就是说，使用垃圾回收的唯一原因就是为了回收程序不再使用的内存。所以对于与垃圾回收有关的任何行为来说（尤其是 `finalize()` 方法），它们也必须同内存及其回收有关。

但这是否意味着如果对象中包括其他对象，`finalize()` 方法就应该明确释放那些对象呢？不是，无论对象是如何创建的，垃圾回收器都会负责释放对象所占用的所有内存。这就将对 `finalize()` 的需求限制到一种特殊情况，即通过某种创建对象方式之外的方式为对象分配了存储空间。不过，你可能会想，Java 中万物皆对象，这种情况怎么可能发生？

看起来之所以有 `finalize()` 方法，是因为在分配内存时可能采用了类似 C 语言中的做法，而非 Java 中的通常做法。这种情况主要发生在使用"本地方法"的情况下，本地方法是一种用 Java 语言调用非 Java 语言代码的形式（关于本地方法的讨论，见本书电子版第2版的附录B）。本地方法目前只支持 C 和 C++，但是它们可以调用其他语言写的代码，所以实际上可以调用任何代码。在非 Java 代码中，也许会调用 C 的 `malloc()` 函数系列来分配存储空间，而且除非调用 `free()` 函数，不然存储空间永远得不到释放，造成内存泄露。但是，`free()` 是 C 和 C++ 中的函数，所以你需要在 `finalize()` 方法里用本地方法调用它。

读到这里，你可能明白了不会过多使用 `finalize()` 方法。对，它确实不是进行普通的清理工作的合适场所。那么，普通的清理工作在哪里执行呢？

### 你必须实施清理

要清理一个对象，用户必须在需要清理的时候调用执行清理动作的方法。这听上去相当直接，但却与 C++ 中的"析构函数"的概念稍有抵触。在 C++ 中，所有对象都会被销毁，或者说应该被销毁。如果在 C++ 中创建了一个局部对象（在栈上创建，在 Java 中不行），此时的销毁动作发生在以"右花括号"为边界的、此对象作用域的末尾处。如果对象是用 **new** 创建的（类似于 Java 中），那么当程序员调用 C++ 的 **delete** 操作符时（Java 中不存在），就会调用相应的析构函数。如果程序员忘记调用 **delete**，那么永远不会调用析构函数，这样就会导致内存泄露，对象的其他部分也不会得到清理。这种 bug 很难跟踪，也是让 C++ 程序员转向 Java 的一个主要因素。相反，在 Java 中，没有用于释放对象的 **delete**，因为垃圾回收器会帮助你释放存储空间。甚至可以肤浅地认为，正是由于垃圾回收的存在，使得 Java 没有析构函数。然而，随着学习的深入，你会明白垃圾回收器的存在并不能完全替代析构函数（而且绝对不能直接调用 `finalize()`，所以这也不是一种解决方案）。如果希望进行除释放存储空间之外的清理工作，还是得明确调用某个恰当的 Java 方法：这就等同于使用析构函数了，只是没有它方便。

记住，无论是"垃圾回收"还是"终结"，都不保证一定会发生。如果 Java 虚拟机（JVM）并未面临内存耗尽的情形，它可能不会浪费时间执行垃圾回收以恢复内存。

### 终结条件

通常，不能指望 `finalize()` ，你必须创建其他的"清理"方法，并明确地调用它们。所以看起来，`finalize()` 只对大部分程序员很难用到的一些晦涩内存清理里有用了。但是，`finalize()` 还有一个有趣的用法，它不依赖于每次都要对 `finalize()` 进行调用，这就是对象终结条件的验证。

当对某个对象不感兴趣时——也就是它将被清理了，这个对象应该处于某种状态，这种状态下它占用的内存可以被安全地释放掉。例如，如果对象代表了一个打开的文件，在对象被垃圾回收之前程序员应该关闭这个文件。只要对象中存在没有被适当清理的部分，程序就存在很隐晦的 bug。`finalize()` 可以用来最终发现这个情况，尽管它并不总是被调用。如果某次 `finalize()` 的动作使得 bug 被发现，那么就可以据此找出问题所在——这才是人们真正关心的。以下是个简单的例子，示范了 `finalize()` 的可能使用方式：

```java
// housekeeping/TerminationCondition.java
// Using finalize() to detect a object that
// hasn't been properly cleaned up

import onjava.*;

class Book {
    boolean checkedOut = false;

    Book(boolean checkOut) {
        checkedOut = checkOut;
    }

    void checkIn() {
        checkedOut = false;
    }

    @Override
    protected void finalize() throws Throwable {
        if (checkedOut) {
            System.out.println("Error: checked out");
        }
        // Normally, you'll also do this:
        // super.finalize(); // Call the base-class version
    }
}

public class TerminationCondition {

    public static void main(String[] args) {
        Book novel = new Book(true);
        // Proper cleanup:
        novel.checkIn();
        // Drop the reference, forget to clean up:
        new Book(true);
        // Force garbage collection & finalization:
        System.gc();
        new Nap(1); // One second delay
    }

}
```

输出：

```
Error: checked out
```

本例的终结条件是：所有的 **Book** 对象在被垃圾回收之前必须被登记。但在 `main()` 方法中，有一本书没有登记。要是没有 `finalize()` 方法来验证终结条件，将会很难发现这个 bug。

你可能注意到使用了 `@Override`。`@` 意味着这是一个注解，注解是关于代码的额外信息。在这里，该注解告诉编译器这不是偶然地重定义在每个对象中都存在的 `finalize()` 方法——程序员知道自己在做什么。编译器确保你没有拼错方法名，而且确保那个方法存在于基类中。注解也是对读者的提醒，`@Override` 在 Java 5 引入，在 Java 7 中改善，本书通篇会出现。

注意，`System.gc()` 用于强制进行终结动作。但是即使不这么做，只要重复地执行程序（假设程序将分配大量的存储空间而导致垃圾回收动作的执行），最终也能找出错误的 **Book** 对象。

你应该总是假设基类版本的 `finalize()` 也要做一些重要的事情，使用 **super** 调用它，就像在 `Book.finalize()` 中看到的那样。本例中，它被注释掉了，因为它需要进行异常处理，而我们到现在还没有涉及到。

### 垃圾回收器如何工作

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