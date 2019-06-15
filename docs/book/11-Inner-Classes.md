[TOC]

<!-- Inner Classes -->

# 第十一章 内部类

> 一个类的定义在另一个类的定义内部，这就是内部类。

内部类是一种非常有用的特性，因为它允许你把一些逻辑相关的类组织在一起，并控制位于内部的类的可视性。然而必须要了解，内部类与组合是完全不同的概念，这一点很重要。在最初，内部类看起来就像是一种代码隐藏机制：将类置于其他类的内部。但是，你将会了解到，内部类远不止如此，它了解外围类，并能与之通信，而且你用内部类写出的代码更加优雅而清晰，尽管并不总是这样。

最初，内部类可能看起来有些奇怪，而且要花些时间才能在设计中轻松地使用它们。对内部类的需求并非总是很明显的，但是在描述完内部类的基本语法与语义之后，"Why inner classes?"就应该使得内部类的益处明确显现了。

本章剩余部分包含了对内部类语法更加详尽的探索，这些特性是为了语言的完备性而设计的，但是你也许不需要使用它们，至少一开始不需要。因此，本章最初的部分也许就是你现在所需的全部，你可以将更详尽的探索当作参考资料。


<!-- Creating Inner Classes -->
## 创建内部类

创建内部类的方式就如同你想的一样——把类的定义置于外围类的里面：

```java
// innerclasses/Parcel1.java
// Creating inner classes
public class Parcel1 {
    class Contents {
        private int i = 11;
        public int value() { return i; }
    }
    class Destination {
        private String label;
        Destination(String whereTo) {
            label = whereTo;
        }
        String readLabel() { return label; }
    }
    // Using inner classes looks just like
    // using any other class, within Parcel1:
    public void ship(String dest) {
        Contents c = new Contents();
        Destination d = new Destination(dest);
        System.out.println(d.readLabel());
    }
    public static void main(String[] args) {
        Parcel1 p = new Parcel1();
        p.ship("Tasmania");
    }
}
```

输出为：

```
Tasmania
```

当我们在 ship() 方法里面使用内部类的时候，与使用普通类没什么不同。在这里，实际的区别只是内部类的名字是嵌套在 Parcel1 里面的。

更典型的情况是，外部类将有一个方法，该方法返回一个指向内部类的引用，就像在 to() 和 contents() 方法中看到的那样：

```java
// innerclasses/Parcel2.java
// Returning a reference to an inner class
public class Parcel2 {
    class Contents {
        private int i = 11;
        public int value() { return i; }
    }
    class Destination {
        private String label;
        Destination(String whereTo) {
            label = whereTo;
        }
        String readLabel() { return label; }
    }
    public Destination to(String s) {
        return new Destination(s);
    }
    public Contents contents() {
        return new Contents();
    }
    public void ship(String dest) {
        Contents c = contents();
        Destination d = to(dest);
        System.out.println(d.readLabel());
    }
    public static void main(String[] args) {
        Parcel2 p = new Parcel2();
        p.ship("Tasmania");
        Parcel2 q = new Parcel2();
// Defining references to inner classes:
        Parcel2.Contents c = q.contents();
        Parcel2.Destination d = q.to("Borneo");
    }
}
```

输出为：

```
Tasmania
```

如果想从外部类的非静态方法之外的任意位置创建某个内部类的对象，那么必须像在 main() 方法中那样，具体地指明这个对象的类型：OuterClassName.InnerClassName。

<!-- The Link to the Outer Class -->

## 链接外部类

到目前为止，内部类似乎还只是一种名字隐藏和组织代码的模式。这些是很有用，但还不是最引人注目的，它还有其他的用途。当生成一个内部类的对象时，此对象与制造它的外围对象（enclosing object）之间就有了一种联系，所以它能访问其外围对象的所有成员，而不需要任何特殊条件。此外，内部类还拥有其外围类的所有元素的访问权。

```java
// innerclasses/Sequence.java
// Holds a sequence of Objects
interface Selector {
    boolean end();
    Object current();
    void next();
}
public class Sequence {
    private Object[] items;
    private int next = 0;
    public Sequence(int size) {
        items = new Object[size];
    }
    public void add(Object x) {
        if(next < items.length)
            items[next++] = x;
    }
    private class SequenceSelector implements Selector {
        private int i = 0;
        @Override
        public boolean end() { return i == items.length; }
        @Override
        public Object current() { return items[i]; }
        @Override
        public void next() { if(i < items.length) i++; }
    }
    public Selector selector() {
        return new SequenceSelector();
    }
    public static void main(String[] args) {
        Sequence sequence = new Sequence(10);
        for(int i = 0; i < 10; i++)
            sequence.add(Integer.toString(i));
        Selector selector = sequence.selector();
        while(!selector.end()) {
            System.out.print(selector.current() + " ");
            selector.next();
        }
    }
}
```

输出为：

```
0 1 2 3 4 5 6 7 8 9
```

Sequence 类只是一个固定大小的 Object 的数组，以类的形式包装了起来。可以调用 add）在序列末增加新的 Object（只要还有空间），要获取 Sequence 中的每一个对象，可以使用 Selector 接口。这是“迭代器”设计模式的一个例子，在本书稍后的部分将更多地学习它。Selector 允许你检查序列是否到末尾了（end()），访问当前对象（current()），以及移到序列中的下一个对象（next()），因为 Selector 是一个接口，所以别的类可以按它们自己的方式来实现这个接口，并且另的方法能以此接口为参数，来生成更加通用的代码。

这里，SequenceSelector 是提供 Selector 功能的 private 类。可以看到，在 main() 中创建了一个 Sequence，并向其中添加了一些 String 对象。然后通过调用 selector() 获取一个 Selector，并用它在 Sequence 中移动和选择每一个元素。
最初看到 SequenceSelector，可能会觉得它只不过是另一个内部类罢了。但请仔细观察它，注意方法 end()，current() 和 next() 都用到了 items，这是一个引用，它并不是 SequenceSelector 的一部分，而是外围类中的一个 private 字段。然而内部类可以访问其外围类的方法和字段，就像自己拥有它们似的，这带来了很大的方便，就如前面的例子所示。

所以内部类自动拥有对其外围类所有成员的访问权。这是如何做到的呢？当某个外围类的对象创建了一个内部类对象时，此内部类对象必定会秘密地捕获一个指向那个外围类对象的引用。然后，在你访问此外围类的成员时，就是用那个引用来选择外围类的成员。幸运的是，编译器会帮你处理所有的细节，但你现在可以看到：内部类的对象只能在与其外围类的对象相关联的情况下才能被创建（就像你应该看到的，在内部类是非 static 类时）。构建内部类对象时，需要一个指向其外围类对象的引用，如果编译器访问不到这个引用就会报错。不过绝大多数时候这都无需程序员操心。

<!-- Using .this and .new -->
## 使用 .this 和 .new

如果你需要生成对外部类对象的引用，可以使用外部类的名字后面紧跟圆点和 this。这样产生的引用自动地具有正确的类型，这一点在编译期就被知晓并受到检查，因此没有任何运行时开销。下面的示例展示了如何使用.this：

```java
// innerclasses/DotThis.java
// Accessing the outer-class object
public class DotThis {
    void f() { System.out.println("DotThis.f()"); }
    public class Inner {
        public DotThis outer() {
            return DotThis.this;
            // A plain "this" would be Inner's "this"
        }
    }
    public Inner inner() { return new Inner(); }
    public static void main(String[] args) {
        DotThis dt = new DotThis();
        DotThis.Inner dti = dt.inner();
        dti.outer().f();
    }
}
```

输出为：

```
DotThis.f()
```

有时你可能想要告知某些其他对象，去创建其某个内部类的对象。要实现此目的，你必须在 mew 表达式中提供对其他外部类对象的引用，这是需要使用.new 语法，就像下面这样：

```java
// innerclasses/DotNew.java
// Creating an inner class directly using .new syntax
public class DotNew {
    public class Inner {}
    public static void main(String[] args) {
        DotNew dn = new DotNew();
        DotNew.Inner dni = dn.new Inner();
    }
}
```

要想直接创建内部类的对象，你不能按照你想象的方式，去引用外部类的名字 DotNew，而是必须使用外部类的对象来创建该内部类对象，就像在上面的程序中所看到的那样。这也解决了内部类名字作用域的问题，因此你不必声明（实际上你不能声明）dn.new DotNew.Innero。

下面你可以看到将.new 应用于 Parcel 的示例：

```java
// innerclasses/Parcel3.java
// Using .new to create instances of inner classes
public class Parcel3 {
    class Contents {
        private int i = 11;
        public int value() { return i; }
    }
    class Destination {
        private String label;
        Destination(String whereTo) { label = whereTo; }
        String readLabel() { return label; }
    }
    public static void main(String[] args) {
        Parcel3 p = new Parcel3();
// Must use instance of outer class
// to create an instance of the inner class:
        Parcel3.Contents c = p.new Contents();
        Parcel3.Destination d =
                p.new Destination("Tasmania");
    }
}
```

在拥有外部类对象之前是不可能创建内部类对象的。这是因为内部类对象会暗暗地连接到建它的外部类对象上。但是，如果你创建的是嵌套类（静态内部类），那么它就不需要对外部类对象的引用。

<!-- Inner Classes and Upcasting -->
## 内部类与向上转型

当将内部类向上转型为其基类，尤其是转型为一个接口的时候，内部类就有了用武之地。（从实现了某个接口的对象，得到对此接口的引用，与向上转型为这个对象的基类，实质上效果是一样的。）这是因为此内部类-某个接口的实现-能够完全不可见，并且不可用。所得到的只是指向基类或接口的引用，所以能够很方便地隐藏实现细节。

我们可以创建前一个示例的接口：

```java
// innerclasses/Destination.java
public interface Destination {
    String readLabel();
}
```

```java
// innerclasses/Contents.java
public interface Contents {
    int value();
}
```

现在 Contents 和 Destination 表示客户端程序员可用的接口。记住，接口的所有成员自动被设置为 public 的。

当取得了一个指向基类或接口的引用时，甚至可能无法找出它确切的类型，看下面的例子：

```java
// innerclasses/TestParcel.java
class Parcel4 {
    private class PContents implements Contents {
        private int i = 11;
        @Override
        public int value() { return i; }
    }
    protected final class PDestination implements Destination {
        private String label;
        private PDestination(String whereTo) {
            label = whereTo;
        }
        @Override
        public String readLabel() { return label; }
    }
    public Destination destination(String s) {
        return new PDestination(s);
    }
    public Contents contents() {
        return new PContents();
    }
}
public class TestParcel {
    public static void main(String[] args) {
        Parcel4 p = new Parcel4();
        Contents c = p.contents();
        Destination d = p.destination("Tasmania");
// Illegal -- can't access private class:
//- Parcel4.PContents pc = p.new PContents();
    }
}
```

在 Parcel4 中，内部类 PContents 是 private，所以除了 Parcel4，没有人能访问它。普通（非内部）类的访问权限不能被设为 private 或者 protected；他们只能设置为 public 或 package 访问权限。

PDestination 是 protected，所以只有 Parcel4 及其子类、还有与 Parcel4 同一个包中的类（因为 protected 也给予了包访问权）能访问 PDestination，其他类都不能访问 PDestination，这意味着，如果客户端程序员想了解或访问这些成员，那是要受到限制的。实际上，甚至不能向下转型成 private 内部类（或 protected 内部类，除非是继承自它的子类），因为不能访问其名字，就像在 TestParcel 类中看到的那样。

private 内部类给类的设计者提供了一种途径，通过这种方式可以完全阻止任何依赖于类型的编码，并且完全隐藏了实现的细节。此外，从客户端程序员的角度来看，由于不能访问任何新增加的、原本不属于公共接口的方法，所以扩展接口是没值的。这也给 Java 编译器

<!-- Inner Classes in Methods and Scopes -->

## 内部类方法和作用域

到目前为止，读者所看到的只是内部类的典型用途。通常，如果所读、写的代码包含了内部类，那么它们都是“平凡的”内部类，简单并且容易理解。然而，内部类的语法覆盖了大量其他的更加难以理解的技术。例如，可以在一个方法里面或者在任意的作用域内定义内部类。

这么做有两个理由：

1. 如前所示，你实现了某类型的接口，于是可以创建并返回对其的引用。
2. 你要解决一个复杂的问题，想创建一个类来辅助你的解决方案，但是又不希望这个类是公共可用的。

在后面的例子中，先前的代码将被修改，以用来实现：

1. 一个定义在方法中的类。
2. 一个定义在作用域内的类，此作用域在方法的内部。
3. 一个实现了接口的匿名类。
4. 一个匿名类，它扩展了有非默认构造器的类。
5. 一个匿名类，它执行字段初始化。
6. 一个匿名类，它通过实例初始化实现构造（匿名类不可能有构造器）。

第一个例子展示了在方法的作用域内（而不是在其他类的作用域内）创建一个完整的类。这被称作局部内部类：

```java
// innerclasses/Parcel5.java
// Nesting a class within a method
public class Parcel5 {
    public Destination destination(String s) {
        final class PDestination implements Destination {
            private String label;
            private PDestination(String whereTo) {
                label = whereTo;
            }
            @Override
            public String readLabel() { return label; }
        }
        return new PDestination(s);
    }
    public static void main(String[] args) {
        Parcel5 p = new Parcel5();
        Destination d = p.destination("Tasmania");
    }
}
```

PDestination 类是 destination() 方法的一部分，而不是 Parcel5 的一部分。所以，在 destination() 之外不能访问 PDestination，注意出现在 return 语句中的向上转型-返回的是 Destination 的引用，它是 PDestination 的基类。当然，在 destination() 中定义了内部类 PDestination，并不意味着一旦 dest() 方法执行完毕，PDestination 就不可用了。

你可以在同一个子目录下的任意类中对某个内部类使用类标识符 PDestination，这并不会有命名冲突。

下面的例子展示了如何在任意的作用域内嵌入一个内部类：

```java
// innerclasses/Parcel6.java
// Nesting a class within a scope
public class Parcel6 {
    private void internalTracking(boolean b) {
        if(b) {
            class TrackingSlip {
                private String id;
                TrackingSlip(String s) {
                    id = s;
                }
                String getSlip() { return id; }
            }
            TrackingSlip ts = new TrackingSlip("slip");
            String s = ts.getSlip();
        }
        // Can't use it here! Out of scope:
        //- TrackingSlip ts = new TrackingSlip("x");
    }
    public void track() { internalTracking(true); }
    public static void main(String[] args) {
        Parcel6 p = new Parcel6();
        p.track();
    }
}
```

TrackingSlip 类被嵌入在 if 语句的作用域内，这并不是说该类的创建是有条件的，它其实与别的类一起编译过了。然而，在定义 Trackingslip 的作用域之外，它是不可用的，除此之外，它与普通的类一样。

<!-- Anonymous Inner Classes -->

## 匿名内部类

下面的例子看起来有点奇怪：

```java
// innerclasses/Parcel7.java
// Returning an instance of an anonymous inner class
public class Parcel7 {
    public Contents contents() {
        return new Contents() { // Insert class definition
            private int i = 11;
            @Override
            public int value() { return i; }
        }; // Semicolon required
    }
    public static void main(String[] args) {
        Parcel7 p = new Parcel7();
        Contents c = p.contents();
    }
}
```

contents()方法将返回值的生成与表示这个返回值的类的定义结合在一起！另外，这个类是匿名的，它没有名字。更糟的是，看起来似乎是你正要创建一个Contents对象。但是然后（在到达语句结束的分号之前）你却说：“等一等，我想在这里插入一个类的定义。

这种奇怪的语法指的是：“创建一个继承自Contents的匿名类的对象。”通过new表达式返回的引用被自动向上转型为对Contents的引用。上述匿名内部类的语法是下述形式的简化形式：

```java
// innerclasses/Parcel7b.java
// Expanded version of Parcel7.java
public class Parcel7b {
    class MyContents implements Contents {
        private int i = 11;
        @Override
        public int value() { return i; }
    }
    public Contents contents() {
        return new MyContents();
    }
    public static void main(String[] args) {
        Parcel7b p = new Parcel7b();
        Contents c = p.contents();
    }
}
```

在这个匿名内部类中，使用了默认的构造器来生成Contents。下面的代码展示的是，如果你的基类需要一个有参数的构造器，应该怎么办：

```java
// innerclasses/Parcel8.java
// Calling the base-class constructor
public class Parcel8 {
    public Wrapping wrapping(int x) {
// Base constructor call:
        return new Wrapping(x) { // [1]
            @Override
            public int value() {
                return super.value() * 47;
            }
        }; // [2]
    }
    public static void main(String[] args) {
        Parcel8 p = new Parcel8();
        Wrapping w = p.wrapping(10);
    }
}
```

- \[1\] 将合适的参数传递给基类的构造器。
- \[2\] 在匿名内部类末尾的分号，并不是用来标记此内部类结束的。实际上，它标记的是表达式的结束，只不过这个表达式正巧包含了匿名内部类罢了。因此，这与别的地方使用的分号是一致的。

尽管Wrapping只是一个具有具体实现的普通类，但它还是被共导出类当作公共“接口”来使用。

```java
// innerclasses/Wrapping.java
public class Wrapping {
    private int i;
    public Wrapping(int x) { i = x; }
    public int value() { return i; }
}
```

为了多样性，Wrapping拥有一个要求传递一个参数的构造器。

在匿名类中定义字段时，还能够对其执行初始化操作：

```java
// innerclasses/Parcel9.java
public class Parcel9 {
    // Argument must be final or "effectively final"
// to use within the anonymous inner class:
    public Destination destination(final String dest) {
        return new Destination() {
            private String label = dest;
            @Override
            public String readLabel() { return label; }
        };
    }
    public static void main(String[] args) {
        Parcel9 p = new Parcel9();
        Destination d = p.destination("Tasmania");
    }
}
```

如果定义一个匿名内部类，并且希望它使用一个在其外部定义的对象，那么编译器会要求其参数引用是final的，就像你在destination()的参数中看到的那样。如果你忘记了，将会得到一个编译时错误消息。

如果只是简单地给一个字段赋值，那么此例中的方法是很好的。但是，如果想做一些类似勾造器的行为，该怎么办呢？在匿名类中不可能有命名构造器（因为它根本没名字！），但通过实例初始化，就能够达到为匿名内部类创建一个构造器的效果，就像这样：

```java
// innerclasses/AnonymousConstructor.java
// Creating a constructor for an anonymous inner class
abstract class Base {
    Base(int i) {
        System.out.println("Base constructor, i = " + i);
    }
    public abstract void f();
}
public class AnonymousConstructor {
    public static Base getBase(int i) {
        return new Base(i) {
            { System.out.println(
                    "Inside instance initializer"); }
            @Override
            public void f() {
                System.out.println("In anonymous f()");
            }
        };
    }
    public static void main(String[] args) {
        Base base = getBase(47);
        base.f();
    }
}
```

输出为：

```
Base constructor, i = 47
Inside instance initializer
In anonymous f()
```

在此例中，不要求变量一定是final的。因为被传递给匿名类的基类的构造器，它并不会在匿名类内部被直接使用。

下例是带实例初始化的"parcel"形式。注意destination() 的参数必须是 final 的，因为它们是在匿名类内部使用的。

```java
// innerclasses/Parcel10.java
// Using "instance initialization" to perform
// construction on an anonymous inner class
public class Parcel10 {
    public Destination
    destination(final String dest, final float price) {
        return new Destination() {
            private int cost;
            // Instance initialization for each object:
            {
                cost = Math.round(price);
                if(cost > 100)
                    System.out.println("Over budget!");
            }
            private String label = dest;
            @Override
            public String readLabel() { return label; }
        };
    }
    public static void main(String[] args) {
        Parcel10 p = new Parcel10();
        Destination d = p.destination("Tasmania", 101.395F);
    }
}
```

输出为：

```
Over budget!
```

在实例初始化操作的内部，可以看到有一段代码，它们不能作为字段初始化动作的一部分来执行（就是if语句）。所以对于匿名类而言，实例初始化的实际效果就是构造器。当然它受到了限制-你不能重载实例初始化方法，所以你仅有一个这样的构造器。

匿名内部类与正规的继承相比有些受限，因为匿名内部类既可以扩展类，也可以实现接口，但是不能两者兼备。而且如果是实现接口，也只能实现一个接口。

<!-- Nested Classes -->

## 嵌套类


<!-- Why Inner Classes? -->
## 内部类应用场景


<!-- Inheriting from Inner Classes -->
## 继承内部类


<!-- Can Inner Classes Be Overridden? -->
## 重写内部类


<!-- Local Inner Classes -->
## 内部类局部变量


<!-- Inner-Class Identifiers -->
## 内部类标识符


<!-- Summary -->
## 本章小结

<!-- 分页 -->

<div style="page-break-after: always;"></div>