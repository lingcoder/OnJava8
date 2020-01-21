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

应用程序框架允许您从一个类或一组类开始，创建一个新的应用程序，重用现有类中的大部分代码，并根据需要覆盖一个或多个方法来定制应用程序。

**模板方法模式**

应用程序框架中的一个基本概念是模板方法模式，它通常隐藏在底层，通过调用基类中的各种方法来驱动应用程序(为了创建应用程序，您已经覆盖了其中的一些方法)。

模板方法模式的一个重要特性是它是在基类中定义的，并且不能更改。它有时是一个 **private** 方法，但实际上总是 **final**。它调用其他基类方法(您覆盖的那些)来完成它的工作,但是它通常只作为初始化过程的一部分被调用(因此框架使用者不一定能够直接调用它)。

```Java
// patterns/TemplateMethod.java
// Simple demonstration of Template Method

abstract class ApplicationFramework {
    ApplicationFramework() {
        templateMethod();
    }

    abstract void customize1();

    abstract void customize2(); // "private" means automatically "final": private void templateMethod() { IntStream.range(0, 5).forEach( n -> { customize1(); customize2(); }); }}// Create a new "application": class MyApp extends ApplicationFramework { @Override void customize1() { System.out.print("Hello "); }@Override

    void customize2() {
        System.out.println("World!");
    }
}

public class TemplateMethod {
    public static void main(String[] args) {
        new MyApp();
    }
}
/*
Output:
Hello World!
Hello World!
Hello World!
Hello World!
Hello World!
*/
```

基类构造函数负责执行必要的初始化，然后启动运行应用程序的“engine”(模板方法模式)(在GUI应用程序中，这个“engine”是主事件循环)。框架使用者只提供
**customize1()** 和 **customize2()** 的定义，然后“应用程序”已经就绪运行。

![](images/designproxy.png)

<!-- Fronting for an Implementation -->
## 面向实现

代理模式和桥接模式都提供了在代码中使用的代理类;完成工作的真正类隐藏在这个代理类的后面。当您在代理中调用一个方法时，它只是反过来调用实现类中的方法。这两种模式非常相似，所以代理模式只是桥接模式的一种特殊情况。人们倾向于将两者合并,称为代理模式，但是术语“代理”有一个长期的和专门的含义，这可能解释了这两种模式不同的原因。基本思想很简单:从基类派生代理，同时派生一个或多个提供实现的类:创建代理对象时，给它一个可以调用实际工作类的方法的实现。


在结构上，代理模式和桥接模式的区别很简单:代理模式只有一个实现，而桥接模式有多个实现。在设计模式中被认为是不同的:代理模式用于控制对其实现的访问，而桥接模式允许您动态更改实现。但是，如果您扩展了“控制对实现的访问”的概念，那么这两者就可以完美地结合在一起

**代理模式**

如果我们按照上面的关系图实现，它看起来是这样的:

```Java
// patterns/ProxyDemo.java
// Simple demonstration of the Proxy pattern
interface ProxyBase {
    void f();

    void g();

    void h();
}

class Proxy implements ProxyBase {
    private ProxyBase implementation;

    Proxy() {
        implementation = new Implementation();
    }
    // Pass method calls to the implementation:
    @Override
    public void f() { implementation.f(); }
    @Override
    public void g() { implementation.g(); }
    @Override
    public void h() { implementation.h(); }
}

class Implementation implements ProxyBase {
    public void f() {
        System.out.println("Implementation.f()");
    }

    public void g() {
        System.out.println("Implementation.g()");
    }

    public void h() {
        System.out.println("Implementation.h()");
    }
}

public class ProxyDemo {
    public static void main(String[] args) {
        Proxy p = new Proxy();
        p.f();
        p.g();
        p.h();
    }
}
/*
Output:
Implementation.f()
Implementation.g()
Implementation.h()
*/
```

具体实现不需要与代理对象具有相同的接口;只要代理对象以某种方式“代表具体实现的方法调用，那么基本思想就算实现了。然而，拥有一个公共接口是很方便的，因此具体实现必须实现代理对象调用的所有方法。

**状态模式**

状态模式向代理对象添加了更多的实现，以及在代理对象的生命周期内从一个实现切换到另一种实现的方法:

```Java
// patterns/StateDemo.java // Simple demonstration of the State pattern
interface StateBase {
    void f();

    void g();

    void h();

    void changeImp(StateBase newImp);
}

class State implements StateBase {
    private StateBase implementation;

    State(StateBase imp) {
        implementation = imp;
    }

    @Override
    public void changeImp(StateBase newImp) {
        implementation = newImp;
    }// Pass method calls to the implementation: @Override public void f() { implementation.f(); } @Override public void g() { implementation.g(); } @Override

    public void h() {
        implementation.h();
    }
}

class Implementation1 implements StateBase {
    @Override
    public void f() {
        System.out.println("Implementation1.f()");
    }

    @Override
    public void g() {
        System.out.println("Implementation1.g()");
    }

    @Override
    public void h() {
        System.out.println("Implementation1.h()");
    }

    @Override
    public void changeImp(StateBase newImp) {
    }
}

class Implementation2 implements StateBase {
    @Override
    public void f() {
        System.out.println("Implementation2.f()");
    }

    @Override
    public void g() {
        System.out.println("Implementation2.g()");
    }

    @Override
    public void h() {
        System.out.println("Implementation2.h()");
    }

    @Override
    public void changeImp(StateBase newImp) {
    }
}

public class StateDemo {
    static void test(StateBase b) {
        b.f();
        b.g();
        b.h();
    }

    public static void main(String[] args) {
        StateBase b = new State(new Implementation1());
        test(b);
        b.changeImp(new Implementation2());
        test(b);
    }
}
/* Output:
Implementation1.f()
Implementation1.g()
Implementation1.h()
Implementation2.f()
Implementation2.g()
Implementation2.h()
*/
```

在main()中，首先使用第一个实现，然后改变成第二个实现。代理模式和状态模式的区别在于它们解决的问题。设计模式中描述的代理模式的常见用途如下:

1. 远程代理。它在不同的地址空间中代理对象。远程方法调用(RMI)编译器rmic会自动为您创建一个远程代理。

2. 虚拟代理。这提供了“懒加载”来根据需要创建“昂贵”的对象。

3. 保护代理。当您希望对代理对象有权限访问控制时使用。

4. 智能引用。要在被代理的对象被访问时添加其他操作。例如，跟踪特定对象的引用数量，来实现写时复制用法，和防止对象别名。一个更简单的例子是跟踪特定方法的调用数量。您可以将Java引用视为一种保护代理，因为它控制在堆上实例对象的访问(例如，确保不使用空引用)。

在设计模式中，代理模式和桥接模式并不是相互关联的，因为它们被赋予(我认为是任意的)不同的结构。桥接模式,特别是使用一个单独的实现，但这似乎对我来说是不必要的,除非你确定该实现是你无法控制的(当然有可能，但是如果您编写所有代码，那么没有理由不从单基类的优雅中受益)。此外，只要代理对象控制对其“前置”对象的访问，代模式理就不需要为其实现使用相同的基类。不管具体情况如何，在代理模式和桥接模式中，代理对象都将方法调用传递给具体实现对象。

**状态机**

桥接模式允许程序员更改实现，状态机利用一个结构来自动地将实现更改到下一个。当前实现表示系统所处的状态，系统在不同状态下的行为不同(因为它使用桥接模式)。基本上，这是一个利用对象的“状态机”。将系统从一种状态移动到另一种状态的代码通常是模板方法模式，如下例所示:

```Java
// patterns/state/StateMachineDemo.java
// The StateMachine pattern and Template method
// {java patterns.state.StateMachineDemo}
package patterns.state;

import onjava.Nap;

interface State {
    void run();
}

abstract class StateMachine {
    protected State currentState;

    Nap(0.5);
System.out.println("Washing"); new

    protected abstract boolean changeState();

    // Template method:
    protected final void runAll() {
        while (changeState()) // Customizable
            currentState.run();
    }
}

// A different subclass for each state:
class Wash implements State {
    @Override
    public void run() {
    }
}

class Spin implements State {
    @Override
    public void run() {
        System.out.println("Spinning");
        new Nap(0.5);
    }
}

class Rinse implements State {
    @Override
    public void run() {
        System.out.println("Rinsing");
        new Nap(0.5);
    }
}

class Washer extends StateMachine {
    private int i = 0;

    // The state table:
    private State[] states = {new Wash(), new Spin(), new Rinse(), new Spin(),};

    Washer() {
        runAll();
    }

    @Override
    public boolean changeState() {
        if (i < states.length) {
            // Change the state by setting the
            // surrogate reference to a new object:
            currentState = states[i++];
            return true;
        } else return false;
    }
}

public class StateMachineDemo {
    public static void main(String[] args) {
        new Washer();
    }
}
/*
Output:
Washing
Spinning
Rinsing
Spinning
*/
```

在这里，控制状态的类(本例中是状态机)负责决定下一个状态。然而，状态对象本身也可以决定下一步移动到什么状态，通常基于系统的某种输入。这是更灵活的解决方案。


<!-- Factories: Encapsulating Object Creation -->
## 工厂模式

当你发现必须将新类型添加到系统中时，合理的第一步是使用多态性为这些新类型创建一个通用接口。这会将你系统中的其余代码与要添加的特定类型的信息分开，使得可以在不改变现有代码的情况下添加新类型……或者看起来如此。起初，在这种设计中，似乎你必须更改代码的唯一地方就是你继承新类型的地方，但这并不是完全正确的。 你仍然必须创建新类型的对象，并且在创建时必须指定要使用的确切构造器。因此，如果创建对象的代码分布在整个应用程序中，那么在添加新类型时，你将遇到相同的问题——你仍然必须追查你代码中新类型碍事的所有地方。恰好是类型的创建碍事，而不是类型的使用（通过多态处理），但是效果是一样的：添加新类型可能会引起问题。

解决方案是强制对象的创建都通过通用工厂进行，而不是允许创建代码在整个系统中传播。 如果你程序中的所有代码都必须执行通过该工厂创建你的一个对象，那么在添加新类时只需要修改工厂即可。

由于每个面向对象的程序都会创建对象，并且很可能会通过添加新类型来扩展程序，因此工厂是最通用的设计模式之一。

举例来说，让我们重新看一下**Shape**系统。 首先，我们需要一个用于所有示例的基本框架。 如果无法创建**Shape**对象，则需要抛出一个合适的异常：

```java
// patterns/shapes/BadShapeCreation.java package patterns.shapes;
public class BadShapeCreation extends RuntimeException {
    public BadShapeCreation(String msg) {
        super(msg);
    }
}
```

接下来，是一个**Shape**基类：

```java
// patterns/shapes/Shape.java
package patterns.shapes;
public class Shape {
	private static int counter = 0;
    private int id = counter++;
    @Override
    public String toString(){
        return getClass().getSimpleName() + "[" + id + "]";
    }
    public void draw() {
        System.out.println(this + " draw");
    }
    public void erase() {
        System.out.println(this + " erase");
    }
}
```

该类自动为每一个**Shape**对象创建一个唯一的`id`。

`toString()`使用运行期信息来发现特定的**Shape**子类的名字。

现在我们能很快创建一些**Shape**子类了：

```java
// patterns/shapes/Circle.java
package patterns.shapes;
public class Circle extends Shape {}
```

```java
// patterns/shapes/Square.java
package patterns.shapes;
public class Square extends Shape {}
```

```java
// patterns/shapes/Triangle.java
package patterns.shapes;
public class Triangle extends Shape {} 
```

工厂是具有能够创建对象的方法的类。 我们有几个示例版本，因此我们将定义一个接口：

```java
// patterns/shapes/FactoryMethod.java
package patterns.shapes;
public interface FactoryMethod {
    Shape create(String type);
}
```

`create()`接收一个参数，这个参数使其决定要创建哪一种**Shape**对象，这里是`String`，但是它其实可以是任何数据集合。对象的初始化数据（这里是字符串）可能来自系统外部。 这个例子将测试工厂：

```java
// patterns/shapes/FactoryTest.java
package patterns.shapes;
import java.util.stream.*;
public class FactoryTest {
    public static void test(FactoryMethod factory) {
        Stream.of("Circle", "Square", "Triangle",
                  "Square", "Circle", "Circle", "Triangle")
        .map(factory::create)
        .peek(Shape::draw)
        .peek(Shape::erase)
        .count(); // Terminal operation
    }
} 
```

在主函数`main()`里，要记住除非你在最后使用了一个终结操作，否则**Stream**不会做任何事情。在这里，`count()`的值被丢弃了。

创建工厂的一种方法是显式创建每种类型：

```java
// patterns/ShapeFactory1.java
// A simple static factory method
import java.util.*;
import java.util.stream.*;
import patterns.shapes.*;
public class ShapeFactory1 implements FactoryMethod {
    public Shape create(String type) {
        switch(type) {
            case "Circle": return new Circle();
            case "Square": return new Square();
            case "Triangle": return new Triangle();
            default: throw new BadShapeCreation(type);
        }
    }
    public static void main(String[] args) {
        FactoryTest.test(new ShapeFactory1());
    }
}
```

输出结果：

```java
Circle[0] draw
Circle[0] erase
Square[1] draw
Square[1] erase
Triangle[2] draw
Triangle[2] erase
Square[3] draw
Square[3] erase
Circle[4] draw
Circle[4] erase
Circle[5] draw
Circle[5] erase
Triangle[6] draw
Triangle[6] erase 
```

`create()`现在是添加新类型的Shape时系统中唯一需要更改的其他代码。

### 动态工厂

前面例子中的**静态**`create()`方法强制所有创建操作都集中在一个位置，因此这是添加新类型的**Shape**时唯一必须更改代码的地方。这当然是一个合理的解决方案，因为它把创建对象的过程限制在一个框内。但是，如果你在添加新类时无需修改任何内容，那就太好了。 以下版本使用反射在首次需要时将**Shape**的构造器动态加载到工厂列表中：

```java
// patterns/ShapeFactory2.java
import java.util.*;
import java.lang.reflect.*;
import java.util.stream.*;
import patterns.shapes.*;
public class ShapeFactory2 implements FactoryMethod {
    Map<String, Constructor> factories = new HashMap<>();
    static Constructor load(String id) {
        System.out.println("loading " + id);
        try {
            return Class.forName("patterns.shapes." + id)
                .getConstructor();
        } catch(ClassNotFoundException |
                NoSuchMethodException e) {
            throw new BadShapeCreation(id);
        }
    }
    public Shape create(String id) {
        try {
            return (Shape)factories
                .computeIfAbsent(id, ShapeFactory2::load)
                .newInstance();
        } catch(InstantiationException |
                IllegalAccessException |
                InvocationTargetException e) {
            throw new BadShapeCreation(id);
        }
    }
    public static void main(String[] args) {
        FactoryTest.test(new ShapeFactory2());
    }
}
```

输出结果：

```java
loading Circle
Circle[0] draw
Circle[0] erase
loading Square
Square[1] draw
Square[1] erase
loading Triangle
Triangle[2] draw
Triangle[2] erase
Square[3] draw
Square[3] erase
Circle[4] draw
Circle[4] erase
Circle[5] draw
Circle[5] erase
Triangle[6] draw
Triangle[6] erase
```

和之前一样，`create()`方法基于你传递给它的**String**参数生成新的**Shape**s，但是在这里，它是通过在**HashMap**中查找作为键的**String**来实现的。 返回的值是一个构造器，该构造器用于通过调用`newInstance()`创建新的**Shape**对象。

然而，当你开始运行程序时，工厂的`map`为空。`create()`使用`map`的`computeIfAbsent()`方法来查找构造器（如果该构造器已存在于`map`中）。如果不存在则使用`load()`计算出该构造器，并将其插入到`map`中。 从输出中可以看到，每种特定类型的**Shape**都是在第一次请求时才加载的，然后只需要从`map`中检索它。

### 多态工厂

《设计模式》这本书强调指出，采用“工厂方法”模式的原因是可以从基本工厂中继承出不同类型的工厂。 再次修改示例，使工厂方法位于单独的类中：

```java
// patterns/ShapeFactory3.java
// Polymorphic factory methods
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import patterns.shapes.*;
interface PolymorphicFactory {
    Shape create();
}
class RandomShapes implements Supplier<Shape> {
    private final PolymorphicFactory[] factories;
    private Random rand = new Random(42);
    RandomShapes(PolymorphicFactory... factories){
        this.factories = factories;
    }
    public Shape get() {
        return factories[ rand.nextInt(factories.length)].create();
    }
}
public class ShapeFactory3 {
    public static void main(String[] args) {
        RandomShapes rs = new RandomShapes(
            Circle::new,
            Square::new,
            Triangle::new);
        Stream.generate(rs)
            .limit(6)
            .peek(Shape::draw)
            .peek(Shape::erase)
            .count();
    }
}
```

输出结果：

```java
Triangle[0] draw
Triangle[0] erase
Circle[1] draw
Circle[1] erase
Circle[2] draw
Circle[2] erase
Triangle[3] draw
Triangle[3] erase
Circle[4] draw
Circle[4] erase
Square[5] draw
Square[5] erase 
```

**RandomShapes**实现了**Supplier \<Shape>**，因此可用于通过`Stream.generate()`创建**Stream**。 它的构造器采用**PolymorphicFactory**对象的可变参数列表。 变量参数列表以数组形式出现，因此列表是以数组形式在内部存储的。`get()`方法随机获取此数组中一个对象的索引，并在结果上调用`create()`以产生新的**Shape**对象。 添加新类型的**Shape**时，**RandomShapes**构造器是唯一需要更改的地方。 请注意，此构造器需要**Supplier \<Shape>**。 我们传递给其**Shape**构造器的方法引用，该引用可满足**Supplier \<Shape>**约定，因为Java 8支持结构一致性。

鉴于**ShapeFactory2.java**可能会抛出异常，使用此方法则没有任何异常——它在编译时完全确定。

### 抽象工厂

抽象工厂模式看起来像我们之前所见的工厂对象，但拥有不是一个工厂方法而是几个工厂方法， 每个工厂方法都会创建不同种类的对象。 这个想法是在创建工厂对象时，你决定如何使用该工厂创建的所有对象。 《设计模式》中提供的示例实现了跨各种图形用户界面（GUI）的可移植性：你创建一个适合你正在使用的GUI的工厂对象，然后从中请求菜单，按钮，滑块等等，它将自动为GUI创建适合该项目版本的组件。 因此，你可以将从一个GUI更改为另一个所产生的影响隔离限制在一处。 作为另一个示例，假设你正在创建一个通用游戏环境来支持不同类型的游戏。 使用抽象工厂看起来就像下文那样：

```java
// patterns/abstractfactory/GameEnvironment.java
// An example of the Abstract Factory pattern
// {java patterns.abstractfactory.GameEnvironment}
package patterns.abstractfactory;
import java.util.function.*;
interface Obstacle {
    void action();
}

interface Player {
    void interactWith(Obstacle o);
}

class Kitty implements Player {
    @Override
    public void interactWith(Obstacle ob) {
        System.out.print("Kitty has encountered a ");
        ob.action();
    }
}

class KungFuGuy implements Player {
    @Override
    public void interactWith(Obstacle ob) {
        System.out.print("KungFuGuy now battles a ");
        ob.action();
    }
}

class Puzzle implements Obstacle {
    @Override
    public void action() {
        System.out.println("Puzzle");
    }
}

class NastyWeapon implements Obstacle {
    @Override
    public void action() {
        System.out.println("NastyWeapon");
    }
}

// The Abstract Factory:
class GameElementFactory {
    Supplier<Player> player;
    Supplier<Obstacle> obstacle;
}

// Concrete factories:
class KittiesAndPuzzles extends GameElementFactory {
    KittiesAndPuzzles() {
        player = Kitty::new;
        obstacle = Puzzle::new;
    }
}

class KillAndDismember extends GameElementFactory {
    KillAndDismember() {
        player = KungFuGuy::new;
        obstacle = NastyWeapon::new;
    }
}

public class GameEnvironment {
    private Player p;
    private Obstacle ob;

    public GameEnvironment(GameElementFactory factory) {
        p = factory.player.get();
        ob = factory.obstacle.get();
    }
    public void play() {
        p.interactWith(ob);
    }
    public static void main(String[] args) {
        GameElementFactory kp = new KittiesAndPuzzles(), kd = new KillAndDismember();
        GameEnvironment g1 = new GameEnvironment(kp), g2 = new GameEnvironment(kd);
        g1.play();
        g2.play();
    }
}

```

输出结果：

```java
Kitty has encountered a Puzzle
KungFuGuy now battles a NastyWeapon
```

在这种环境中，**Player**对象与**Obstacle**对象进行交互，但是根据你所玩游戏的类型，存在不同类型的玩家和障碍物。 你可以通过选择特定的**GameElementFactory**来确定游戏的类型，然后**GameEnvironment**控制游戏的设置和玩法。 在此示例中，设置和玩法非常简单，但是这些活动（初始条件和状态变化）可以决定游戏的大部分结果。 这里，**GameEnvironment**不是为继承而设计的，尽管这样做很有意义。 它还包含“双重调度”和“工厂方法”的示例，稍后将对这两个示例进行说明。

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
