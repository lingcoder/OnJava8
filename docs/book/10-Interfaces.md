[TOC]

<!-- Interfaces -->
# 第十章 接口

接口和抽象类提供了一种将接口与实现分离的更加结构化的方法。

这种机制在编程语言中不常见，例如 C++ 只对这种概念有间接的支持。而在 Java 中存在这些关键字，说明这些思想很重要，Java 为它们提供了直接支持。

首先，我们将学习抽象类，一种介于普通类和接口之间的折中手段。尽管你的第一想法是创建接口，但是对于构建具有属性和未实现方法的类来说，抽象类也是重要且必要的工具。你不可能总是使用纯粹的接口。


<!-- Abstract Classes and Methods -->
## 抽象类和方法

在上一章的乐器例子中，基类 **Instrument** 中的方法往往是“哑”方法。如果调用了这些方法，就会出现一些错误。这是因为接口的目的是为它的派生类创建一个通用接口。

在那些例子中，创建这个通用接口的唯一理由是，不同的子类可以用不同的方式表示此接口。通用接口建立了一个基本形式，以此表达所有派生类的共同部分。另一种说法把 **Instrument** 称为抽象基类，或简称抽象类。

对于像 **Instrument** 那样的抽象类来说，它的对象几乎总是没有意义的。创建一个抽象类是为了通过通用接口操纵一系列类。因此，**Instrument** 只是表示接口，不是具体实现，所以创建一个 **Instrument** 的对象毫无意义，我们可能希望阻止用户这么做。通过让 **Instrument** 所有的方法产生错误，就可以达到这个目的，但是这么做会延迟到运行时才能得知错误信息，并且需要用户进行可靠、详尽的测试。最好能在编译时捕捉问题。

Java 提供了一个叫做*抽象方法*的机制，这个方法是不完整的：它只有声明没有方法体。下面是抽象方法的声明语法：

```java
abstract void f();
```

包含抽象方法的类叫做*抽象类*。如果一个类包含一个或多个抽象方法，那么类本身也必须限定为抽象的，否则，编译器会报错。

```java
// interface/Basic.java
abstract class Basic {
    abstract void unimplemented();
}
```

如果一个抽象类是不完整的，当试图创建这个类的对象时，Java 会怎么做呢？它不会创建抽象类的对象，所以我们只会得到编译器的错误信息。这样保证了抽象类的纯粹性，我们不用担心误用它。

```java
// interfaces/AttemptToUseBasic.java
// {WillNotCompile}
public class AttemptToUseBasic {
    Basic b = new Basic();
    // error: Basic is abstract; cannot be instantiated
}
```

如果创建一个继承抽象类的新类并为之创建对象，那么就必须为基类的所有抽象方法提供方法定义。如果不这么做（可以选择不做），新类仍然是一个抽象类，编译器会强制我们为新类加上 **abstract** 关键字。

```java
// interfaces/Basic2.java
abstract class Basic2 extends Basic {
    int f() {
        return 111;
    }
    
    abstract void g() {
        // unimplemented() still not implemented
    }
}
```

可以将一个不包含任何抽象方法的类指明为 **abstract**，在类中的抽象方法没啥意义但想阻止创建类的对象时，这么做就很有用。

```java
// interfaces/AbstractWithoutAbstracts.java
abstract class Basic3 {
    int f() {
        return 111;
    }
    
    // No abstract methods
}

public class AbstractWithoutAbstracts {
    // Basic b3 = new Basic3();
    // error: Basic 3 is abstract; cannot be instantiated
}
```

为了创建可初始化的类，就要继承抽象类，并提供所有抽象方法的定义：

```java
// interfaces/Instantiable.java
abstract class Uninstantiable {
    abstract void f();
    abstract int g();
}

public class Instantiable extends Uninstantiable {
    @Override
    void f() {
        System.out.println("f()");
    }
    
    @Override
    int g() {
        return 22;
    }
    
    public static void main(String[] args) {
        Uninstantiable ui = new Instantiable();
    }
}
```

留意 `@Override` 的使用。没有这个注解的话，如果你没有定义相同的方法名或签名，抽象机制会认为你没有实现抽象方法从而产生编译时错误。因此，你可能认为这里的 `@Override` 是多余的。但是，`@Override` 还提示了这个方法被覆写——我认为这是有用的，所以我会使用 `@Override`，即使在没有这个注解，编译器告诉我错误的时候。 

记住，事实上的访问权限是“friendly”。你很快会看到接口自动将其方法指明为 **public**。事实上，接口只允许 **public** 方法，如果不加访问修饰符的话，接口的方法不是 **friendly** 而是 **public**。然而，抽象类允许每件事：

```java
// interfaces/AbstractAccess.java
abstract class AbstractAccess {
    private void m1() {}
    
    // private abstract void m1a(); // illegal
    
    protected void m2() {}
    
    protected abstract void m2a();
    
    void m3() {}
    
    abstract void m3a();
    
    public void m4() {}
    
    public abstract void m4a();
}
```

**private abstract** 被禁止了是有意义的，因为你不可能在 **AbstractAccess** 的任何子类中合法地定义它。

上一章的 **Instrument** 类可以很轻易地转换为一个抽象类。只需要部分方法是 **abstract** 即可。将一个类指明为 **abstract** 并不强制类中的所有方法必须都是抽象方法。如下图所示：

![类图](../images/1562653648586.png)

下面是修改成使用抽象类和抽象方法的管弦乐器的例子：

```java
// interfaces/music4/Music4.java
// Abstract classes and methods
// {java interfaces.music4.Music4}
package interfaces.music4;
import polymorphism.music.Note;

abstract class Instrument {
    private int i; // Storage allocated for each
    
    public abstract void play(Note n);
    
    public String what() {
        return "Instrument";
    }
    
    public abstract void adjust();
}

class Wind extends Instrument {
    @Override
    public void play(Note n) {
        System.out.println("Wind.play() " + n);
    }
    
    @Override
    public String what() {
        return "Wind";
    }
    
    @Override
    public void adjust() {
        System.out.println("Adjusting Wind");
    }
}

class Percussion extends Instrument {
    @Override
    public void play(Note n) {
        System.out.println("Percussion.play() " + n);
    }
    
    @Override
    public String what() {
        return "Percussion";
    }
    
    @Override
    public void adjust() {
        System.out.println("Adjusting Percussion");
    }
}

class Stringed extends Instrument {
    @Override
    public void play(Note n) {
        System.out.println("Stringed.play() " + n);
    }
    
    @Override
    public String what() {
        return "Stringed";
    }
    
    @Override
    public void adjust() {
        System.out.println("Adjusting Stringed");
    }
}

class Brass extends Wind {
    @Override
    public void play(Note n) {
        System.out.println("Brass.play() " + n);
    }
    
    @Override
    public void adjust() {
        System.out.println("Adjusting Brass");
    }
}

class Woodwind extends Wind {
    @Override
    public void play(Note n) {
        System.out.println("Woodwind.play() " + n);
    }
    
    @Override
    public String what() {
        return "Woodwind";
    }
}

public class Music4 {
    // Doesn't care about type, so new types
    // added to system still work right:
    static void tune(Instrument i) {
        // ...
        i.play(Note.MIDDLE_C);
    }
    
    static void tuneAll(Instrument[] e) {
        for (Instrument i: e) {
            tune(i);
        }
    }
    
    public static void main(String[] args) {
        // Upcasting during addition to the array:
        Instrument[] orchestra = {
            new Wind(),
            new Percussion(),
            new Stringed(),
            new Brass(),
            new Woodwind()
        };
        tuneAll(orchestra);
    }
}
```

输出：

```
Wind.play() MIDDLE_C
Percussion.play() MIDDLE_C
Stringed.play() MIDDLE_C
Brass.play() MIDDLE_C
Woodwind.play() MIDDLE_C
```

除了 **Instrument**，基本没区别。

创建抽象类和抽象方法是有帮助的，因为它们使得类的抽象性很明确，并能告知用户和编译器使用意图。抽象类同时也是一种有用的重构工具，使用它们使得我们很容易地将沿着继承层级结构上移公共方法。

<!-- Interfaces -->

## 接口创建

使用 **interface** 关键字创建接口。在本书中，interface 和 class 一样随处常见，除非特指关键字 **interface**，其他情况下都采用正常字体书写 interface。

描述 Java 8 之前的接口更加容易，因为它们只允许抽象方法。像下面这样：

```java
// interfaces/PureInterface.java
// Interface only looked like this before Java 8
public interface PureInterface {
    int m1(); 
    void m2();
    double m3();
}
```

我们甚至不用为方法加上 **abstract** 关键字，因为方法在接口中。Java 知道这些方法不能有方法体（仍然可以为方法加上 **abstract** 关键字，但是看起来像是不明白接口，徒增难堪罢了）。

因此，在 Java 8之前我们可以这么说：**interface** 关键字产生一个完全抽象的类，没有提供任何实现。我们只能描述类应该像什么，做什么，但不能描述怎么做，即只能决定方法名、参数列表和返回类型，但是无法确定方法体。接口只提供形式，通常来说没有实现，尽管在某些受限制的情况下可以有实现。

一个接口表示：所有实现了该接口的类看起来都像这样。因此，任何使用某特定接口的代码都知道可以调用该接口的哪些方法，而且仅需知道这些。所以，接口被用来建立类之间的协议。（一些面向对象编程语言中，使用 protocol 关键字完成相同的功能。）

Java 8 中接口稍微有些变化，因为 Java 8 允许接口包含默认方法和静态方法——基于某些重要原因，看到后面你会理解。接口的基本概念仍然没变，介于类型之上、实现之下。接口与抽象类最明显的区别可能就是使用上的惯用方式。接口的典型使用是代表一个类的类型或一个形容词，如 Runnable 或 Serializable，而抽象类通常是类层次结构的一部分或一件事物的类型，如 String 或 ActionHero。

使用关键字 **interface** 而不是 **class** 来创建接口。和类一样，需要在关键字 **interface** 前加上 **public** 关键字（但只是在接口名与文件名相同的情况下），否则接口只有包访问权限，只能在接口相同的包下才能使用它。

接口同样可以包含属性，这些属性被隐式指明为 **static** 和 **final**。

使用 **implements** 关键字使一个类遵循某个特定接口（或一组接口），它表示：接口只是外形，现在我要说明它是如何工作的。除此之外，它看起来像继承。

```java
// interfaces/ImplementingAnInterface.java
interface Concept { // Package access
    void idea1();
    void idea2();
}

class Implementation implements Concept {
    @Override
    public void idea1() {
        System.out.println("idea1");
    }
    
    @Override
    public void idea2() {
        System.out.println("idea2");
    }
}
```

你可以选择显式地声明接口中的方法为 **public**，但是即使你不这么做，它们也是 **public** 的。所以当实现一个接口时，来自接口中的方法必须被定义为 **public**。否则，它们只有包访问权限，这样在继承时，它们的可访问权限就被降低了，这是 Java 编译器所不允许的。

### 默认方法

Java 8 为关键字 **default** 增加了一个新的用途（之前只用于 **switch** 语句和注解中）。当在接口中使用它时，任何实现接口却没有定义方法的时候可以使用 **default** 创建的方法体。默认方法比抽象类中的方法受到更多的限制，但是非常有用，我们将在“流式编程”一章中看到。现在让我们看下如何使用：

```java
// interfaces/AnInterface.java
interface AnInterface {
    void firstMethod();
    void secondMethod();
}
```

我们可以像这样实现接口：

```java
// interfaces/AnImplementation.java
public class AnImplementation implements AnInterface {
    public void firstMethod() {
        System.out.println("firstMethod");
    }
    
    public void secondMethod() {
        System.out.println("secondMethod");
    }
    
    public static void main(String[] args) {
        AnInterface i = new AnImplementation();
        i.firstMethod();
        i.secondMethod();
    }
}
```

输出：

```
firstMethod
secondMethod
```

如果我们在 **AnInterface** 中增加一个新方法 `newMethod()`，而在 **AnImplementation** 中没有实现它，编译器就会报错：

```
AnImplementation.java:3:error: AnImplementation is not abstract and does not override abstract method newMethod() in AnInterface
public class AnImplementation implements AnInterface {
^
1 error
```

如果我们使用关键字 **default** 为 `newMethod()` 方法提供默认的实现，那么所有与接口有关的代码能正常工作，不受影响，而且这些代码还可以调用新的方法 `newMethod()`：

```java
// interfaces/InterfaceWithDefault.java
interface InterfaceWithDefault {
    void firstMethod();
    void secondMethod();
    
    default void newMethod() {
        System.out.println("newMethod");
    }
}
```

关键字 **default** 允许在接口中提供方法实现——在 Java 8 之前被禁止。

```java
// interfaces/Implementation2.java
public class Implementation2 implements InterfaceWithDefault {
    @Override
    public void firstMethod() {
        System.out.println("firstMethod");
    }
    
    @Override
    public void secondMethod() {
        System.out.println("secondMethod")
    }
    
    public static void main(String[] args) {
        InterfaceWithDefault i = new Implementation2();
        i.firstMethod();
        i.secondMethod();
        i.newMethod();
    }
}
```

输出：

```
firstMethod
secondMethod
newMethod
```

尽管 **Implementation2** 中定义 `newMethod()`，但是可以使用 `newMethod()` 了。 

增加默认方法的极具说服力的理由是它允许在不破坏已使用接口的代码的情况下，在接口中增加新的方法。默认方法有时也被称为*守卫方法*或*虚拟扩展方法*。

### 多继承

多继承意味着一个类可能从多个父类型中继承特征和特性。

Java 在设计之初，C++ 的多继承机制饱受诟病。Java 过去是一种严格要求单继承的语言：只能继承自一个类（或抽象类），但可以实现任意多个接口。在 Java 8 之前，接口没有包袱——它只是方法外貌的描述。

多年后的现在，Java 通过默认方法具有了某种多继承的特性。结合带有默认方法的接口意味着结合了多个基类中的行为。因为接口中仍然不允许存在属性（只有静态属性，不适用），所以属性仍然只会来自单个基类或抽象类，也就是说，不会存在状态的多继承。正如下面这样：

```java
// interfaces/MultipleInheritance.java
import java.util.*;

interface One {
    default void first() {
        System.out.println("first");
    }
}

interface Two {
    default void second() {
        System.out.println("second");
    }
}

interface Three {
    default void third() {
        System.out.println("third");
    }
}

class MI implements One, Two, Three {}

public class MultipleInheritance {
    public static void main(String[] args) {
        MI mi = new MI();
        mi.first();
        mi.second();
        mi.third();
    }
}
```

输出：

```
first
second
third
```

现在我们做些在 Java 8 之前不可能完成的事：结合多个源的实现。只要基类方法中的方法名和参数列表不同，就能工作得很好，否则会得到编译器错误：

```java
// interface/MICollision.java
import java.util.*;

interface Bob1 {
    default void bob() {
        System.out.println("Bob1::bob");
    }
}

interface Bob2 {
    default void bob() {
        System.out.println("Bob2::bob");
    }
}

// class Bob implements Bob1, Bob2 {}
/* Produces:
error: class Bob inherits unrelated defaults
for bob() from types Bob1 and Bob2
class Bob implements Bob1, Bob2 {}
^
1 error
*/

interface Sam1 {
    default void sam() {
        System.out.println("Sam1::sam");
    }
}

interface Sam2 {
    default void sam(int i) {
        System.out.println(i * 2);
    }
}

// This works because the argument lists are distinct:
class Sam implements Sam1, Sam2 {}

interface Max1 {
    default void max() {
        System.out.println("Max1::max");
    }
}

interface Max2 {
    default int max() {
        return 47;
    }
}

// class Max implements Max1, Max2 {}
/* Produces:
error: types Max2 and Max1 are imcompatible;
both define max(), but with unrelated return types
class Max implements Max1, Max2 {}
^
1 error
*/
```

**Sam** 类中的两个 `sam()` 方法有相同的方法名但是签名不同——方法签名包括方法名和参数类型，编译器也是用它来区分方法。但是从 **Max** 类可看出，返回类型不是方法签名的一部分，因此不能用来区分方法。为了解决这个问题，需要覆写冲突的方法：

```java
// interfaces/Jim.java
import java.util.*;

interface Jim1 {
    default void jim() {
        System.out.println("Jim1::jim");
    }
}

interface Jim2 {
    default void jim() {
        System.out.println("Jim2::jim");
    }
}

public class Jim implements Jim1, Jim2 {
    @Override
    public void jim() {
        Jim2.super.jim();
    }
    
    public static void main(String[] args) {
        new Jim().jim();
    }
}
```

输出：

```
Jim2::jim
```

当然，你可以重定义 `jim()` 方法，但是也能像上例中那样使用 **super** 关键字选择基类实现中的一种。

### 接口中的静态方法

Java 8 允许在接口中添加静态方法。这么做能恰当地把工具功能置于接口中，从而操作接口，或者成为通用的工具：

```java
// onjava/Operations.java
package onjava;
import java.util.*;

public interface Operations {
    void execute();
    
    static void runOps(Operations... ops) {
        for (Operations op: ops) {
            op.execute();
        }
    }
    
    static void show(String msg) {
        System.out.println(msg);
    }
}
```

这是模版方法设计模式的一个版本（在“设计模式”一章中详细描述），`runOps()` 是一个模版方法。`runOps()` 使用可变参数列表，因而我们可以传入任意多的 **Operation** 参数并按顺序运行它们：

```java
// interface/Machine.java
import java.util.*;
import onjava.Operations;

class Bing implements Operations {
    @Override
    public void execute() {
        Operations.show("Bing");
    }
}

class Crack implements Operations {
    @Override
    public void execute() {
        Operations.show("Crack");
    }
}

class Twist implements Operations {
    @Override
    public void execute() {
        Operations.show("Twist");
    }
}

public class Machine {
    public static void main(String[] args) {
        Operations.runOps(
        	new Bing(), new Crack(), new Twist());
    }
}
```

输出：

```
Bing
Crack
Twist
```

这里展示了创建 **Operations** 的不同方式：一个外部类(Bing)，一个匿名类，一个方法引用和 lambda 表达式——毫无疑问用在这里是最好的解决方法。

这个特性是一项改善，因为它允许把静态方法放在更合适的地方。

### Instrument 作为接口

回顾下乐器的例子，使用接口的话：

![类图](../images/1562737974623.png)

类 **Woodwind** 和 **Brass** 说明一旦实现了某个接口，那么其实现就变成一个普通类，可以按常规方式扩展它。

接口的工作方式使得我们不需要显式声明其中的方法为 **public**，它们自动就是 **public** 的。`play()` 和 `adjust()` 使用 **default** 关键字定义实现。在 Java 8 之前，这些定义要在每个实现中重复实现，显得多余且令人烦恼：

```java
// interfaces/music5/Music5.java
// {java interfaces.music5.Music5}
package interfaces.music5;
import polymorphism.music.Note;

interface Instrument {
    // Compile-time constant:
    int VALUE = 5; // static & final
    
    default void play(Note n)  // Automatically public 
        System.out.println(this + ".play() " + n);
    }
    
    default void adjust() {
        System.out.println("Adjusting " + this);
    }
}

class Wind implements Instrument {
    @Override
    public String toString() {
        return "Wind";
    }
}

class Percussion implements Instrument {
    @Override
    public String toString() {
        return "Percussion";
    }
}

class Stringed implements Instrument {
    @Override
    public String toString() {
        return "Stringed";
    }
}

class Brass extends Wind {
    @Override
    public String toString() {
        return "Brass";
    }
}

class Woodwind extends Wind {
    @Override
    public String toString() {
        return "Woodwind";
    }
}

public class Music5 {
    // Doesn't care about type, so new types
    // added to the system still work right:
    static void tune(Instrument i) {
        // ...
        i.play(Note.MIDDLE_C);
    }
    
    static void tuneAll(Instrument[] e) {
        for (Instrument i: e) {
            tune(i);
        }
    }
    
    public static void main(String[] args) {
        // Upcasting during addition to the array:
        Instrument[] orchestra = {
            new Wind(),
            new Percussion(),
            new Stringed(),
            new Brass(),
            new Woodwind()
        }
        tuneAll(orchestra);
    }
}
```

输出：

```
Wind.play() MIDDLE_C
Percussion.play() MIDDLE_C
Stringed.play() MIDDLE_C
Brass.play() MIDDLE_C
Woodwind.play() MIDDLE_C
```

这个版本的例子的另一个变化是：`what()` 被修改为 `toString()` 方法，因为 `toString()` 实现的正是 `what()` 方法要实现的逻辑。因为 `toString()` 是根基类 **Object** 的方法，所以它不需要出现在接口中。

注意到，无论是将其向上转型为称作 **Instrument** 的普通类，或称作 **Instrument** 的抽象类，还是叫作 **Instrument** 的接口，其行为都是相同的。事实上，从 `tune()` 方法上看不出来 **Instrument** 到底是一个普通类、抽象类，还是一个接口。

<!-- Abstract Classes vs. Interfaces -->

## 抽象类和接口




<!-- Complete Decoupling -->
## 完全解耦


<!-- Combining Multiple Interfaces -->
## 多接口结合


<!-- Extending an Interface with Inheritance -->
## 使用继承扩展接口


<!-- Adapting to an Interface -->
## 接口适配


<!-- Fields in Interfaces -->
## 接口字段


<!-- Nesting Interfaces -->
## 接口嵌套


<!-- Interfaces and Factories -->
## 接口和工厂方法模式


<!-- Summary -->
## 本章小结

<!-- 分页 -->

<div style="page-break-after: always;"></div>