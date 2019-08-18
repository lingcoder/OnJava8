[TOC]

<!-- Type Information -->
# 第十九章 类型信息

> RTTI（RunTime Type Information，运行时类型信息）能够在程序运行时发现和使用类型信息

RTTI 把我们从只能在编译期进行面向类型操作的禁锢中解脱了出来，并且让我们可以使用某些非常强大的程序。对 RTTI 的需要，揭示了面向对象设计中许多有趣（并且复杂）的特性，同时也带来了关于如何组织程序的基本问题。

本章将讨论 Java 是如何在运行时识别对象和类信息的。主要有两种方式：

1. “传统的” RTTI：假定我们在编译时已经知道了所有的类型；
2. “反射”机制：允许我们在运行时发现和使用类的信息。

<!-- The Need for RTTI -->
## 为什么需要 RTTI

下面看一下我们已经很熟悉的一个例子，它使用了多态的类层次结构。基类 `Shape` 是泛化的类型，从它派生出了三个具体类： `Circle` 、`Square` 和 `Triangle`（见下图所示）。

![多态例子Shape的类层次结构图](../images/image-20190409114913825-4781754.png)

这是一个典型的类层次结构图，基类位于顶部，派生类向下扩展。面向对象编程的一个基本目的是：让代码只操纵对基类(这里即 `Shape` )的引用。这样，如果你想添加一个新类(比如从 `Shape` 派生出 `Rhomboid`)来扩展程序，就不会影响原来的代码。在这个例子中，`Shape`接口中动态绑定了 `draw()` 方法，这样做的目的就是让客户端程序员可以使用泛化的 `Shape` 引用来调用 `draw()`。`draw()` 方法在所有派生类里都会被覆盖，而且由于它是动态绑定的，所以它可以使用 `Shape` 引用来调用，这就是多态。

因此，我们通常会创建一个具体的对象(`Circle`、`Square` 或者 `Triangle`)，把它向上转型成 `Shape` (忽略对象的具体类型)，并且在后面的程序中使用 `Shape` 引用来调用在具体对象中被重载的方法（如 `draw()`）。

代码如下：

```java
// typeinfo/Shapes.java
import java.util.stream.*;

abstract class Shape {
    void draw() { System.out.println(this + ".draw()"); }
    @Override
    public abstract String toString();
}

class Circle extends Shape {
    @Override
    public String toString() { return "Circle"; }
}

class Square extends Shape {
    @Override
    public String toString() { return "Square"; }
}

class Triangle extends Shape {
    @Override
    public String toString() { return "Triangle"; }
}

public class Shapes {
    public static void main(String[] args) {
        Stream.of(
            new Circle(), new Square(), new Triangle())
            .forEach(Shape::draw);
    }
}
```

输出结果：

```
Circle.draw()
Square.draw()
Triangle.draw()
```

基类中包含 `draw()` 方法，它通过传递 `this` 参数传递给 `System.out.println()`，间接地使用 `toString()` 打印类标识符(注意：这里将 `toString()` 声明为了 `abstract`，以此强制继承者覆盖改方法，并防止对 `Shape` 的实例化)。如果某个对象出现在字符串表达式中(涉及"+"和字符串对象的表达式)，`toString()` 方法就会被自动调用，以生成表示该对象的 `String`。每个派生类都要覆盖（从 `Object` 继承来的）`toString()` 方法，这样 `draw()` 在不同情况下就打印出不同的消息(多态)。

这个例子中，在把 `Shape` 对象放入 `Stream<Shape>` 中时就会进行向上转型(隐式)，但在向上转型的时候也丢失了这些对象的具体类型。对 `stream` 而言，它们只是 `Shape` 对象。

严格来说，`Stream<Shape>` 实际上是把放入其中的所有对象都当做 `Object` 对象来持有，只是取元素时会自动将其类型转为 `Shape`。这也是 RTTI 最基本的使用形式，因为在 Java 中，所有类型转换的正确性检查都是在运行时进行的。这也正是 RTTI 的含义所在：在运行时，识别一个对象的类型。

另外在这个例子中，类型转换并不彻底：`Object` 被转型为 `Shape` ，而不是 `Circle`、`Square` 或者 `Triangle`。这是因为目前我们只能确保这个 `Stream<Shape>` 保存的都是 `Shape`：

- 编译期，`stream` 和 Java 泛型系统确保放入 `stream` 的都是 `Shape` 对象（`Shape` 子类的对象也可视为 `Shape` 的对象），否则编译器会报错；
- 运行时，自动类型转换确保了从 `stream` 中取出的对象都是 `Shape` 类型。

接下来就是多态机制的事了，`Shape` 对象实际执行什么样的代码，是由引用所指向的具体对象（`Circle`、`Square` 或者 `Triangle`）决定的。这也符合我们编写代码的一般需求，通常，我们希望大部分代码尽可能少了解对象的具体类型，而是只与对象家族中的一个通用表示打交道（本例中即为 `Shape`）。这样，代码会更容易写，更易读和维护；设计也更容易实现，更易于理解和修改。所以多态是面向对象的基本目标。

但是，有时你会碰到一些编程问题，在这些问题中如果你能知道某个泛化引用的具体类型，就可以把问题轻松解决。例如，假设我们允许用户将某些几何形状高亮显示，现在希望找到屏幕上所有高亮显示的三角形；或者，我们现在需要旋转所有图形，但是想跳过圆形(因为圆形旋转没有意义)。这时我们就希望知道 `Stream<Shape>` 里边的形状具体是什么类型，而 Java 实际上也满足了我们的这种需求。使用 RTTI，我们可以查询某个 `Shape` 引用所指向对象的确切类型，然后选择或者剔除特例。

<!-- The Class Object -->
## `Class` 对象

要理解 RTTI 在 Java 中的工作原理，首先必须知道类型信息在运行时是如何表示的。这项工作是由称为 **`Class`对象** 的特殊对象完成的，它包含了与类有关的信息。实际上，`Class` 对象就是用来创建该类所有"常规"对象的。Java 使用 `Class` 对象来实现 RTTI，即便是类型转换这样的操作都是用 `Class` 对象实现的。不仅如此，`Class` 类还提供了很多使用 RTTI 的其它方式。

类是程序的一部分，每个类都有一个 `Class` 对象。换言之，每当我们编写并且编译了一个新类，就会产生一个 `Class` 对象（更恰当的说，是被保存在一个同名的 `.class` 文件中）。为了生成这个类的对象，Java 虚拟机 (JVM) 先会调用"类加载器"子系统把这个类加载到内存中。

类加载器子系统可能包含一条类加载器链，但有且只有一个**原生类加载器**，它是JVM实现的一部分。原生类加载器加载的是”可信类”（包括 Java API 类）。它们通常是从本地盘加载的。在这条链中，通常不需要添加额外的类加载器，但是如果你有特殊需求（例如以某种特殊的方式加载类，以支持 Web 服务器应用，或者通过网络下载类），也可以挂载额外的类加载器。

所有的类都是第一次使用时动态加载到 JVM 中的，当程序创建第一个对类的静态成员的引用时，就会加载这个类。

> 其实构造器也是类的静态方法，虽然构造器前面并没有 `static` 关键字。所以，使用 `new` 操作符创建类的新对象，这个操作也算作对类的静态成员引用。

因此，Java 程序在它开始运行之前并没有被完全加载，很多部分是在需要时才会加载。这一点与许多传统编程语言不同，动态加载使得 Java 具有一些静态加载语言（如 C++）很难或者根本不可能实现的特性。

类加载器首先会检查这个类的 `Class` 对象是否已经加载，如果尚未加载，默认的类加载器就会根据类名查找 `.class` 文件（如果有附加的类加载器，这时候可能就会在数据库中或者通过其它方式获得字节码）。这个类的字节码被加载后，JVM 会对其进行验证，确保它没有损坏，并且不包含不良的 Java 代码(这是 Java 安全防范的一种措施)。

一旦某个类的 `Class` 对象被载入内存，它就可以用来创建这个类的所有对象。下面的示范程序可以证明这点：

```java
// typeinfo/SweetShop.java
// 检查类加载器工作方式
class Cookie {
    static { System.out.println("Loading Cookie"); }
}

class Gum {
    static { System.out.println("Loading Gum"); }
}

class Candy {
    static { System.out.println("Loading Candy"); }
}

public class SweetShop {
    public static void main(String[] args) {
        System.out.println("inside main");
        new Candy();
        System.out.println("After creating Candy");
        try {
            Class.forName("Gum");
        } catch(ClassNotFoundException e) {
            System.out.println("Couldn't find Gum");
        }
        System.out.println("After Class.forName(\"Gum\")");
        new Cookie();
        System.out.println("After creating Cookie");
    }
}
```

输出结果：

```
inside main
Loading Candy
After creating Candy
Loading Gum
After Class.forName("Gum")
Loading Cookie
After creating Cookie
```

上面的代码中，`Candy`、`Gum` 和 `Cookie` 这几个类都有一个 `static{...}` 静态初始化块，这些静态初始化块在类第一次被加载的时候就会执行。也就是说，静态初始化块会打印出相应的信息，告诉我们这些类分别是什么时候被加载了。而在主方法里边，创建对象 的代码都放在了 `print()` 语句之间，以帮助我们判断类加载的时间点。

从输出中可以看到，`Class` 对象仅在需要的时候才会被加载，`static` 初始化是在类加载时进行的。

代码里面还有特别有趣的一行：

```java
Class.forName("Gum");
```

所有 `Class` 对象都属于 `Class` 类，而且它跟其他普通对象一样，我们可以获取和操控它的引用(这也是类加载器的工作)。`forName()` 是 `Class` 类的一个静态方法，我们可以使用 `forName()` 根据目标类的类名（`String`）得到该类的 `Class` 对象。上面的代码忽略了 `forName()` 的返回值，因为那个调用是为了得到它产生的“副作用”。从结果可以看出，`forName()` 执行的副作用是如果 `Gum` 类没有被加载就加载它，而在加载的过程中，`Gum` 的 `static` 初始化块被执行了。

还需要注意的是，如果 `Class.forName()` 找不到要加载的类，它就会抛出异常 `ClassNotFoundException`。上面的例子中我们只是简单地报告了问题，但在更严密的程序里，就要考虑在异常处理程序中把问题解决掉（具体例子详见[设计模式](./25-Patterns)章节）。

无论何时，只要你想在运行时使用类型信息，就必须先得到那个 `Class` 对象的引用。`Class.forName()` 就是实现这个功能的一个便捷途径，因为使用该方法你不需要先持有这个类型 的对象。但是，如果你已经拥有了目标类的对象，那就可以通过调用 `getClass()` 方法来获取 `Class` 引用了，这个方法来自根类 `Object`，它将返回表示该对象实际类型的 `Class`对象的引用。`Class` 包含很多有用的方法，下面代码展示了其中的一部分：

```java
// typeinfo/toys/ToyTest.java
// 测试 Class 类
// {java typeinfo.toys.ToyTest}
package typeinfo.toys;

interface HasBatteries {}
interface Waterproof {}
interface Shoots {}

class Toy {
    // 注释下面的无参数构造器会引起 NoSuchMethodError 错误
    Toy() {}
    Toy(int i) {}
}

class FancyToy extends Toy
implements HasBatteries, Waterproof, Shoots {
    FancyToy() { super(1); }
}

public class ToyTest {
    static void printInfo(Class cc) {
        System.out.println("Class name: " + cc.getName() +
            " is interface? [" + cc.isInterface() + "]");
        System.out.println(
            "Simple name: " + cc.getSimpleName());
        System.out.println(
            "Canonical name : " + cc.getCanonicalName());
    }

    public static void main(String[] args) {
        Class c = null;
        try {
            c = Class.forName("typeinfo.toys.FancyToy");
        } catch(ClassNotFoundException e) {
            System.out.println("Can't find FancyToy");
            System.exit(1);
        }

        printInfo(c);
        for(Class face : c.getInterfaces())
            printInfo(face);

        Class up = c.getSuperclass();
        Object obj = null;

        try {
            // Requires no-arg constructor:
            obj = up.newInstance();
        } catch(InstantiationException e) {
            System.out.println("Cannot instantiate");
            System.exit(1);
        } catch(IllegalAccessException e) {
            System.out.println("Cannot access");
            System.exit(1);
        }

        printInfo(obj.getClass());
    }
}
```

输出结果：

```
Class name: typeinfo.toys.FancyToy is interface?
[false]
Simple name: FancyToy
Canonical name : typeinfo.toys.FancyToy
Class name: typeinfo.toys.HasBatteries is interface?
[true]
Simple name: HasBatteries
Canonical name : typeinfo.toys.HasBatteries
Class name: typeinfo.toys.Waterproof is interface?
[true]
Simple name: Waterproof
Canonical name : typeinfo.toys.Waterproof
Class name: typeinfo.toys.Shoots is interface? [true]
Simple name: Shoots
Canonical name : typeinfo.toys.Shoots
Class name: typeinfo.toys.Toy is interface? [false]
Simple name: Toy
Canonical name : typeinfo.toys.Toy
```

`FancyToy` 继承自 `Toy` 并实现了 `HasBatteries`、`Waterproof` 和 `Shoots` 接口。在 `main` 方法中，我们创建了一个 `Class` 引用，然后在 `try` 语句里边用 `forName()` 方法创建了一个 `FancyToy` 的类对象并赋值给该引用。需要注意的是，传递给 `forName()` 的字符串必须使用类的全限定名（包含包名）。

`printInfo()` 函数使用 `getName()` 来产生完整类名，使用 `getSimpleName()` 产生不带包名的类名，`getCanonicalName()` 也是产生完整类名（除内部类和数组外，对大部分类产生的结果与 `getName()` 相同）。`isInterface()` 用于判断某个 `Class` 对象代表的是否为一个接口。因此，通过 `Class` 对象，你可以得到关于该类型的所有信息。

在主方法中调用的 `Class.getInterface()` 方法返回的是存放 `Class` 对象的数组，里面的 `Class` 对象表示的是那个类实现的接口。 

另外，你还可以调用 `getSuperclass()` 方法来得到父类的 `Class` 对象，再用父类的 `Class` 对象调用该方法，重复多次，你就可以得到一个对象完整的类继承结构。

`Class` 对象的 `newInstance()` 方法是实现“虚拟构造器”的一种途径，虚拟构造器可以让你在不知道一个类的确切类型的时候，创建这个类的对象。在前面的例子中，`up` 只是一个 `Class` 对象的引用，在编译期并不知道这个引用会指向哪个类的 `Class` 对象。当你创建新实例时，会得到一个 `Object` 引用，但是这个引用指向的是 `Toy` 对象。当然，由于得到的是 `Object` 引用，目前你只能给它发送 `Object` 对象能够接受的调用。而如果你想请求具体对象才有的调用，你就得先获取该对象更多的类型信息，并执行某种转型。另外，使用 `newInstance()` 来创建的类，必须带有无参数的构造书。在本章稍后部分，你将会看到如何通过 Java 的反射 API，用任意的构造器来动态的创建类的对象。

### 类字面常量

Java还提供了另一种方法来生成类对象的引用：**类字面常量**。对上述程序来说，就像这样：`FancyToy.class;`。这样做不仅更简单，而且更安全，因为它在编译时就会受到检查（因此不必放在`try`语句块中）。并且它根除了对 `forName()` 方法的调用，所以效率更高。

类字面常量不仅不仅可以应用于普通类，也可以应用于接口、数组以及基本数据类型。另外，对于基本数据类型的包装器类，还有一个标准字段 `TYPE`。`TYPE`字段是一个引用，指向对应的基本数据类型的 `Class` 对象，如下所示：

<figure>
<table style="text-align:center;">
  <thead>
    <tr>
      <th colspan="2">...等价于...</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>boolean.class</td>
      <td>Boolean.TYPE</td>
    </tr>
    <tr>
      <td>char.class</td>
      <td>Character.TYPE</td>
    </tr>
    <tr>
      <td>byte.class</td>
      <td>Byte.TYPE</td>
    </tr>
    <tr>
      <td>short.class</td>
      <td>Short.TYPE</td>
    </tr>
    <tr>
      <td>int.class</td>
      <td>Integer.TYPE</td>
    </tr>
    <tr>
      <td>long.class</td>
      <td>Long.TYPE</td>
    </tr>
    <tr>
      <td>float.class</td>
      <td>Float.TYPE</td>
    </tr>
    <tr>
      <td>double.class</td>
      <td>Double.TYPE</td>
    </tr>
    <tr>
      <td>void.class</td>
      <td>Void.TYPE</td>
    </tr>
  </tbody>
</table>
</figure>

我的建议是使用 `.class` 的形式，以保持与普通类的一致性。

注意，有一点很有趣：当使用 `.class` 来创建对 `Class` 对象的引用时，不会自动地初始化该`Class` 对象。为了使用类而做的准备工作实际包含三个步骤：

1. **加载**，这是由类加载器执行的。该步骤将查找字节码（通常在 classpath 所指定的路径中查找，但这并非是必须的），并从这些字节码中创建一个 `Class` 对象。

2. **链接**。在链接阶段将验证类中的字节码，为 `static` 域分配存储空间，并且如果需要的话，将解析这个类创建的对其他类的所有引用。

3. **初始化**。如果该类具有超类，则对其进行初始化，执行 `static` 初始化器和 `static` 初始化块。

初始化被延迟到了对 `static` 方法（构造器隐式地是 `static` 的）或者非常数 `static` 域进行首次引用时才执行：

```java
// typeinfo/ClassInitialization.java
import java.util.*;

class Initable {
    static final int STATIC_FINAL = 47;
    static final int STATIC_FINAL2 =
        ClassInitialization.rand.nextInt(1000);
    static {
        System.out.println("Initializing Initable");
    }
}

class Initable2 {
    static int staticNonFinal = 147;
    static {
        System.out.println("Initializing Initable2");
    }
}

class Initable3 {
    static int staticNonFinal = 74;
    static {
        System.out.println("Initializing Initable3");
    }
}

public class ClassInitialization {
    public static Random rand = new Random(47);
    public static void
    main(String[] args) throws Exception {
        Class initable = Initable.class;
        System.out.println("After creating Initable ref");
        // Does not trigger initialization:
        System.out.println(Initable.STATIC_FINAL);
        // Does trigger initialization:
        System.out.println(Initable.STATIC_FINAL2);
        // Does trigger initialization:
        System.out.println(Initable2.staticNonFinal);
        Class initable3 = Class.forName("Initable3");
        System.out.println("After creating Initable3 ref");
        System.out.println(Initable3.staticNonFinal);
    }
}
```

输出结果：

```
After creating Initable ref
47
Initializing Initable
258
Initializing Initable2
147
Initializing Initable3
After creating Initable3 ref
74
```

初始化有效地实现了尽可能的“惰性”，从对 `initable` 引用的创建中可以看到，仅使用 `.class` 语法来获得对类对象的引用不会引发初始化。但与此相反，使用 `Class.forName()` 来产生 `Class` 引用会立即就进行初始化，如 `initable3`。

如果一个 `static final` 值是“编译期常量”（如 `Initable.staticFinal`），那么这个值不需要对 `Initable` 类进行初始化就可以被读取。但是，如果只是将一个域设置成为 `static` 和 `final`，还不足以确保这种行为。例如，对 `Initable.staticFinal2` 的访问将强制进行类的初始化，因为它不是一个编译期常量。

如果一个 `static` 域不是 `final` 的，那么在对它访问时，总是要求在它被读取之前，要先进行链接（为这个域分配存储空间）和初始化（初始化该存储空间），就像在对 `Initable2.staticNonFinal` 的访问中所看到的那样。

### 泛化的 `Class` 引用

`Class`引用总是指向某个 `Class` 对象，而 `Class` 对象可以用于产生类的实例，并且包含可作用于这些实例的所有方法代码。它还包含该类的 `static` 成员，因此 `Class` 引用表明了它所指向对象的确切类型，而该对象便是 `Class` 类的一个对象。

<!-- > 译者的理解： `Class` 对象是 `Class` 类产生的对象，而再往深一点说，`Class` 类的 `Class` 对象（`Class.class`）也是其本类产生的对象。即一切皆对象，类也是一种对象。 -->

但是，Java 设计者看准机会，将它的类型变得更具体了一些。Java 引入泛型语法之后，我们可以使用泛型对 `Class` 引用所指向的 `Class` 对象的类型进行限定。在下面的实例中，两种语法都是正确的：

```java
// typeinfo/GenericClassReferences.java

public class GenericClassReferences {
    public static void main(String[] args) {
        Class intClass = int.class;
        Class<Integer> genericIntClass = int.class;
        genericIntClass = Integer.class; // 同一个东西
        intClass = double.class;
        // genericIntClass = double.class; // 非法
    }
}
```

普通的类引用不会产生警告信息。你可以看到，普通的类引用可以重新赋值指向任何其他的 `Class` 对象，但是使用泛型限定的类引用只能指向其声明的类型。通过使用泛型语法，我们可以让编译器强制执行额外的类型检查。

那如果我们希望稍微放松一些限制，应该怎么办呢？乍一看，下面的操作好像是可以的：

```java
Class<Number> geenericNumberClass = int.class;
```

这看起来似乎是起作用的，因为 `Integer` 继承自 `Number`。但事实却是不行，因为 `Integer` 的 `Class` 对象并不是 `Number`的 `Class` 对象的子类（这看起来可能有点诡异，我们将在[泛型](./20-Generics)这一章详细讨论）。

为了在使用 `Class` 引用时放松限制，我们使用了通配符，它是 Java 泛型中的一部分。通配符就是 `?`，表示“任何事物”。因此，我们可以在上例的普通 `Class` 引用中添加通配符，并产生相同的结果：

```java
// typeinfo/WildcardClassReferences.java

public class WildcardClassReferences {
    public static void main(String[] args) {
        Class<?> intClass = int.class;
        intClass = double.class;
    }
}
```

使用 `Class<?>` 比单纯使用 `Class` 要好，虽然它们是等价的，并且单纯使用 `Class` 不会产生编译器警告信息。使用 `Class<?>` 的好处是它表示你并非是碰巧或者由于疏忽才使用了一个非具体的类引用，而是特意为之。

为了创建一个限定指向某种类型或其子类的 `Class` 引用，我们需要将通配符与 `extends` 关键字配合使用，创建一个范围限定。这与仅仅声明 `Class<Number>` 不同，现在做如下声明：

```java
// typeinfo/BoundedClassReferences.java

public class BoundedClassReferences {
    public static void main(String[] args) {
        Class<? extends Number> bounded = int.class;
        bounded = double.class;
        bounded = Number.class;
        // Or anything else derived from Number.
    }
}
```

向 `Class` 引用添加泛型语法的原因只是为了提供编译期类型检查，因此如果你操作有误，稍后就会发现这点。使用普通的 `Class` 引用你要确保自己不会犯错，因为一旦你犯了错误，就要等到运行时你才能发现它，这并不是很方便。

下面的示例使用了泛型语法，它保存了一个类引用，稍后又用 `newInstance()` 方法产生类的对象：

```java
// typeinfo/DynamicSupplier.java
import java.util.function.*;
import java.util.stream.*;

class CountedInteger {
    private static long counter;
    private final long id = counter++;
    @Override
    public String toString() { return Long.toString(id); }
}

public class DynamicSupplier<T> implements Supplier<T> {
    private Class<T> type;
    public DynamicSupplier(Class<T> type) {
        this.type = type;
    }
    public T get() {
        try {
            return type.newInstance();
        } catch(InstantiationException |
                        IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        Stream.generate(
            new DynamicSupplier<>(CountedInteger.class))
            .skip(10)
            .limit(5)
            .forEach(System.out::println);
    }
}
```

输出结果:

```
10
11
12
13
14
```

注意，这个类必须假设与它与它一起工作的任何类型都有一个无参构造器，否者运行时会抛出异常。编译期对该程序不会产生任何警告信息。

当你将泛型语法用于 `Class` 对象时，`newInstance()` 将返回该对象的确切类型，而不仅仅只是在 `ToyTest.java` 中看到的基类 `Object`。然而，这在某种程度上有些受限：

```java
// typeinfo/toys/GenericToyTest.java
// 测试 Class 类
// {java typeinfo.toys.GenericToyTest}
package typeinfo.toys;

public class GenericToyTest {
    public static void
    main(String[] args) throws Exception {
        Class<FancyToy> ftClass = FancyToy.class;
        // Produces exact type:
        FancyToy fancyToy = ftClass.newInstance();
        Class<? super FancyToy> up =
            ftClass.getSuperclass();
        // This won't compile:
        // Class<Toy> up2 = ftClass.getSuperclass();
        // Only produces Object:
        Object obj = up.newInstance();
    }
}
```

如果你手头的是超类，那编译期将只允许你声明超类引用为“某个类，它是 `FancyToy` 的超类”，就像在表达式 `Class<? super FancyToy>` 中所看到的那样。而不会接收 `Class<Toy>` 这样的声明。这看上去显得有些怪，因为 `getSuperClass()` 方法返回的是基类（不是接口），并且编译器在编译期就知道它是什么类型了（在本例中就是 `Toy.class`），而不仅仅只是“某个类，它是 `FancyToy` 的超类”。不管怎样，正是由于这种含糊性，`up.newInstance` 的返回值不是精确类型，而只是 `Object`。

### `cast()` 方法

Java 中还有用于 `Class` 引用的转型语法，即 `cast()` 方法：

```java
// typeinfo/ClassCasts.java

class Building {}
class House extends Building {}

public class ClassCasts {
    public static void main(String[] args) {
        Building b = new House();
        Class<House> houseType = House.class;
        House h = houseType.cast(b);
        h = (House)b; // ... 或者这样做.
    }
}
```

`cast()` 方法接受参数对象，并将其类型转换为 `Class` 引用的类型。但是，如果观察上面的代码，你就会发现，与实现了相同功能的 `main` 方法中最后一行相比，这种转型好像做了很多额外的工作。

`cast()` 在无法使用普通类型转换的情况下会显得非常有用，在你编写泛型代码（你将在[泛型](./20-Generics)这一章学习到）时，如果你保存了 `Class` 引用，并希望以后通过这个引用来执行转型，你就需要用到 `cast()`。但事实却是这种情况非常少见，我发现整个 Java 类库中，只有一处使用了 `cast()`（在 `com.sun.mirror.util.DeclarationFilter` 中）。

Java 类库中另一个没有任何用处的特性就是 `Class.asSubclass()`，该方法允许你将一个 `Class` 对象转型为更加具体的类型。

## 类型转换检测

直到现在，我们已知的RTTI类型包括:

1.  传统的类型转换，如 “`(Shape)`”，由 RTTI 确保转换的正确性，如果执行了一个错误的类型转换，就会抛出一个 `ClassCastException` 异常。

2.  代表对象类型的 `Class` 对象. 通过查询 `Class` 对象可以获取运行时所需的信息.

在 C++ 中，经典的类型转换 “`(Shape)`” 并不使用 RTTI. 它只是简单地告诉编译器将这个对象作为新的类型对待. 而 Java 会进行类型检查，这种类型转换一般被称作“类型安全的向下转型”。之所以称作“向下转型”，是因为传统上类继承图是这么画的。将 `Circle` 转换为 `Shape` 是一次向上转型, 将 `Shape` 转换为 `Circle` 是一次向下转型。但是, 因为我们知道 `Circle` 肯定是一个 `Shape`，所以编译器允许我们自由地做向上转型的赋值操作，且不需要任何显示的转型操作。当你给编译器一个 `Shape` 的时候，编译器并不知道它到底是什么类型的 `Shape`——它可能是 `Shape`，也可能是 `Shape` 的子类型，例如 `Circle`、`Square`、`Triangle` 或某种其他的类型。在编译期，编译器只能知道它是 `Shape`。因此，你需要使用显式的类型转换，以告知编译器你想转换的特定类型，否则编译器就不允许你执行向下转型赋值。 （编译器将会检查向下转型是否合理，因此它不允许向下转型到实际上不是待转型类型的子类的类型上）。

RTTI 在 Java 中还有第三种形式，那就是关键字 `instanceof`。它返回一个布尔值，告诉我们对象是不是某个特定类型的实例，可以用提问的方式使用它，就像这个样子：

```java
if(x instanceof Dog)
  ((Dog)x).bark();
```

在将 `x` 的类型转换为 `Dog` 之前，`if` 语句会先检查 `x` 是否是 `Dog` 类型的对象。进行向下转型前，如果没有其他信息可以告诉你这个对象是什么类型，那么使用 `instanceof` 是非常重要的，否则会得到一个 `ClassCastException` 异常。

一般，可能想要查找某种类型（比如要找三角形，并填充为紫色），这时可以轻松地使用 `instanceof` 来计数所有对象。举个例子，假如你有一个类的继承体系，描述了 `Pet`（以及它们的主人，在后面一个例子中会用到这个特性）。在这个继承体系中的每个 `Individual` 都有一个 `id` 和一个可选的名字。尽管下面的类都继承自 `Individual`，但是 `Individual` 类复杂性较高，因此其代码将放在[附录：容器](./Appendix-Collection-Topics)中进行解释说明。正如你所看到的，此处并不需要去了解 `Individual` 的代码——你只需了解你可以创建其具名或不具名的对象，并且每个 `Individual` 都有一个 `id()` 方法，如果你没有为 `Individual` 提供名字，`toString()` 方法只产生类型名。

下面是继承自 `Individual` 的类的继承体系：

```java
// typeinfo/pets/Person.java
package typeinfo.pets;

public class Person extends Individual {
  public Person(String name) { super(name); }
}
```

```java
// typeinfo/pets/Pet.java
package typeinfo.pets;

public class Pet extends Individual {
  public Pet(String name) { super(name); }
  public Pet() { super(); }
}
```

```java
// typeinfo/pets/Dog.java
package typeinfo.pets;

public class Dog extends Pet {
  public Dog(String name) { super(name); }
  public Dog() { super(); }
}
```

```java
// typeinfo/pets/Mutt.java
package typeinfo.pets;

public class Mutt extends Dog {
  public Mutt(String name) { super(name); }
  public Mutt() { super(); }
}
```


```java
// typeinfo/pets/Pug.java
package typeinfo.pets;

public class Pug extends Dog {
  public Pug(String name) { super(name); }
  public Pug() { super(); }
}
```

```java
// typeinfo/pets/Cat.java
package typeinfo.pets;

public class Cat extends Pet {
  public Cat(String name) { super(name); }
  public Cat() { super(); }
}
```

```java
// typeinfo/pets/EgyptianMau.java
package typeinfo.pets;

public class EgyptianMau extends Cat {
  public EgyptianMau(String name) { super(name); }
  public EgyptianMau() { super(); }
}
```

```java
// typeinfo/pets/Manx.java
package typeinfo.pets;

public class Manx extends Cat {
  public Manx(String name) { super(name); }
  public Manx() { super(); }
}
```

```java
// typeinfo/pets/Cymric.java
package typeinfo.pets;

public class Cymric extends Manx {
  public Cymric(String name) { super(name); }
  public Cymric() { super(); }
}
```

```java
// typeinfo/pets/Rodent.java
package typeinfo.pets;

public class Rodent extends Pet {
  public Rodent(String name) { super(name); }
  public Rodent() { super(); }
}
```

```java
// typeinfo/pets/Rat.java
package typeinfo.pets;

public class Rat extends Rodent {
  public Rat(String name) { super(name); }
  public Rat() { super(); }
}
```

```java
// typeinfo/pets/Mouse.java
package typeinfo.pets;

public class Mouse extends Rodent {
  public Mouse(String name) { super(name); }
  public Mouse() { super(); }
}
```

```java
// typeinfo/pets/Hamster.java
package typeinfo.pets;

public class Hamster extends Rodent {
  public Hamster(String name) { super(name); }
  public Hamster() { super(); }
}
```

我们必须显式地为每一个子类编写无参构造器。因为我们有一个带一个参数的构造器，所以编译器不会自动地为我们加上无参构造器。

接下来，我们需要一个类，它可以随机地创建不同类型的宠物，同时，它还可以创建宠物数组和持有宠物的 `List`。为了这个类更加普遍适用，我们将其定义为抽象类：

```java
// typeinfo/pets/PetCreator.java
// Creates random sequences of Pets
package typeinfo.pets;
import java.util.*;
import java.util.function.*;

public abstract
class PetCreator implements Supplier<Pet> {
  private Random rand = new Random(47);
  // The List of the different types of Pet to create:
  public abstract List<Class<? extends Pet>> types();
  public Pet get() { // Create one random Pet
    int n = rand.nextInt(types().size());
    try {
      return types().get(n).newInstance();
    } catch(InstantiationException |
            IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
```

抽象的 `types()` 方法需要子类来实现，以此来获取 `Class` 对象构成的 `List`（这是模板方法设计模式的一种变体）。注意，其中类的类型被定义为“任何从 `Pet` 导出的类型”，因此 `newInstance()` 不需要转型就可以产生 `Pet`。`get()` 随机的选取出一个 `Class` 对象，然后可以通过 `Class.newInstance()` 来生成该类的新实例。

在调用 `newInstance()` 时，可能会出现两种异常。在紧跟 `try` 语句块后面的 `catch` 子句中可以看到对它们的处理。异常的名字再次成为了一种对错误类型相对比较有用的解释（`IllegalAccessException` 违反了 Java 安全机制，在本例中，表示默认构造器为 `private` 的情况）。

当你导出 `PetCreator` 的子类时，你需要为 `get()` 方法提供 `Pet` 类型的 `List`。`types()` 方法会简单地返回一个静态 `List` 的引用。下面是使用 `forName()` 的一个具体实现：

```java
// typeinfo/pets/ForNameCreator.java
package typeinfo.pets;
import java.util.*;

public class ForNameCreator extends PetCreator {
  private static List<Class<? extends Pet>> types =
    new ArrayList<>();

  // 需要随机生成的类型名:
  private static String[] typeNames = {
    "typeinfo.pets.Mutt",
    "typeinfo.pets.Pug",
    "typeinfo.pets.EgyptianMau",
    "typeinfo.pets.Manx",
    "typeinfo.pets.Cymric",
    "typeinfo.pets.Rat",
    "typeinfo.pets.Mouse",
    "typeinfo.pets.Hamster"
  };

  @SuppressWarnings("unchecked")
  private static void loader() {
    try {
      for(String name : typeNames)
        types.add(
          (Class<? extends Pet>)Class.forName(name));
    } catch(ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  static { loader(); }
  @Override
  public List<Class<? extends Pet>> types() {
    return types;
  }
}
```

`loader()` 方法使用 `Class.forName()` 创建了 `Class` 对象的 `List`。这可能会导致 `ClassNotFoundException` 异常，因为你传入的是一个 `String` 类型的参数，它不能再编译期间被确认是否合理。由于 `Pet` 相关的文件在 `typeinfo` 包里面，所以使用它们的时候需要填写完整的包名。

为了使得 `List` 装入的是具体的 `Class` 对象，类型转换是必须的，它会产生一个编译时警告。`loader()` 方法是分开编写的，然后它被放入到一个静态代码块里，因为 `@SuppressWarning` 注解不能够直接放置在静态代码块之上。

为了对 `Pet` 进行计数，我们需要一个能跟踪不同类型的 `Pet` 的工具。`Map` 的是这个需求的首选，我们将 `Pet` 类型名作为键，将保存 `Pet` 数量的 `Integer` 作为值。通过这种方式，你就看可以询问：“有多少个 `Hamster` 对象？”我们可以使用 `instanceof` 来对 `Pet` 进行计数：

```java
// typeinfo/PetCount.java
// 使用 instanceof
import typeinfo.pets.*;
import java.util.*;

public class PetCount {
  static class Counter extends HashMap<String,Integer> {
    public void count(String type) {
      Integer quantity = get(type);
      if(quantity == null)
        put(type, 1);
      else
        put(type, quantity + 1);
    }
  }
  public static void
  countPets(PetCreator creator) {
    Counter counter = new Counter();
    for(Pet pet : Pets.array(20)) {
      // List each individual pet:
      System.out.print(
        pet.getClass().getSimpleName() + " ");
      if(pet instanceof Pet)
        counter.count("Pet");
      if(pet instanceof Dog)
        counter.count("Dog");
      if(pet instanceof Mutt)
        counter.count("Mutt");
      if(pet instanceof Pug)
        counter.count("Pug");
      if(pet instanceof Cat)
        counter.count("Cat");
      if(pet instanceof EgyptianMau)
        counter.count("EgyptianMau");
      if(pet instanceof Manx)
        counter.count("Manx");
      if(pet instanceof Cymric)
        counter.count("Cymric");
      if(pet instanceof Rodent)
        counter.count("Rodent");
      if(pet instanceof Rat)
        counter.count("Rat");
      if(pet instanceof Mouse)
        counter.count("Mouse");
      if(pet instanceof Hamster)
        counter.count("Hamster");
    }
    // Show the counts:
    System.out.println();
    System.out.println(counter);
  }
  public static void main(String[] args) {
    countPets(new ForNameCreator());
  }
}
/* Output:
Rat Manx Cymric Mutt Pug Cymric Pug Manx Cymric Rat
EgyptianMau Hamster EgyptianMau Mutt Mutt Cymric Mouse
Pug Mouse Cymric
{EgyptianMau=2, Pug=3, Rat=2, Cymric=5, Mouse=2, Cat=9,
Manx=7, Rodent=5, Mutt=3, Dog=6, Pet=20, Hamster=1}
*/
```

在 `countPets()` 中，一个简短的静态方法 `Pets.array()` 生产出了一个随机动物的集合。每个 `Pet` 都被 `instanceof` 检测到并数了一遍。

`instanceof` 有一个严格的限制：只可以将它与命名类型进行比较，而不能与 `Class` 对象作比较。在前面的例子中，你可能会觉得写出一大堆 `instanceof` 表达式很乏味，事实也是如此。但是，也没有办法让 `instanceof` 聪明起来，让它能够自动地创建一个 `Class` 对象的数组，然后将目标与这个数组中的对象逐一进行比较（稍后会看到一种替代方案）。其实这并不是那么大的限制，如果你在程序中写了大量的 `instanceof`，那就说明你的设计可能存在瑕疵。

### 使用类字面量

如果我们使用类字面量重新实现 `PetCreator` 类的话，其结果在很多方面都会更清晰：

```java
// typeinfo/pets/LiteralPetCreator.java
// 使用类字面量
// {java typeinfo.pets.LiteralPetCreator}
package typeinfo.pets;
import java.util.*;

public class LiteralPetCreator extends PetCreator {
  // try 代码块不再需要
  @SuppressWarnings("unchecked")
  public static
  final List<Class<? extends Pet>> ALL_TYPES =
    Collections.unmodifiableList(Arrays.asList(
      Pet.class, Dog.class, Cat.class, Rodent.class,
      Mutt.class, Pug.class, EgyptianMau.class,
      Manx.class, Cymric.class, Rat.class,
      Mouse.class, Hamster.class));
  // 用于随机创建的类型:
  private static final
  List<Class<? extends Pet>> TYPES =
    ALL_TYPES.subList(ALL_TYPES.indexOf(Mutt.class),
      ALL_TYPES.size());
  @Override
  public List<Class<? extends Pet>> types() {
    return TYPES;
  }
  public static void main(String[] args) {
    System.out.println(TYPES);
  }
}
/* 输出:
[class typeinfo.pets.Mutt, class typeinfo.pets.Pug,
class typeinfo.pets.EgyptianMau, class
typeinfo.pets.Manx, class typeinfo.pets.Cymric, class
typeinfo.pets.Rat, class typeinfo.pets.Mouse, class
typeinfo.pets.Hamster]
*/
```

在即将到来的 `PetCount3.java` 示例中，我们用所有 `Pet` 类型预先加载一个 `Map`（不仅仅是随机生成的），因此 `ALL_TYPES` 类型的列表是必要的。`types` 列表是 `ALL_TYPES` 类型（使用 `List.subList()` 创建）的一部分，它包含精确的宠物类型，因此用于随机生成 `Pet`。

这次，`types` 的创建没有被 `try` 块包围，因为它是在编译时计算的，因此不会引发任何异常，不像 `Class.forName()`。

我们现在在 `typeinfo.pets` 库中有两个 `PetCreator` 的实现。为了提供第二个作为默认实现，我们可以创建一个使用 `LiteralPetCreator` 的 *外观模式*：

```java
// typeinfo/pets/Pets.java
// Facade to produce a default PetCreator
package typeinfo.pets;
import java.util.*;
import java.util.stream.*;

public class Pets {
  public static final PetCreator CREATOR =
    new LiteralPetCreator();

  public static Pet get() {
    return CREATOR.get();
  }

  public static Pet[] array(int size) {
    Pet[] result = new Pet[size];
    for(int i = 0; i < size; i++)
      result[i] = CREATOR.get();
    return result;
  }

  public static List<Pet> list(int size) {
    List<Pet> result = new ArrayList<>();
    Collections.addAll(result, array(size));
    return result;
  }

  public static Stream<Pet> stream() {
    return Stream.generate(CREATOR);
  }
}
```

这还提供了对 `get()`、`array()` 和 `list()` 的间接调用，以及生成 `Stream<Pet>` 的新方法。

因为 `PetCount.countPets()` 采用了 `PetCreator` 参数，所以我们可以很容易地测试 `LiteralPetCreator`（通过上面的外观模式）：

```java
// typeinfo/PetCount2.java
import typeinfo.pets.*;

public class PetCount2 {
  public static void main(String[] args) {
    PetCount.countPets(Pets.CREATOR);
  }
}
/* 输出:
Rat Manx Cymric Mutt Pug Cymric Pug Manx Cymric Rat
EgyptianMau Hamster EgyptianMau Mutt Mutt Cymric Mouse
Pug Mouse Cymric
{EgyptianMau=2, Pug=3, Rat=2, Cymric=5, Mouse=2, Cat=9,
Manx=7, Rodent=5, Mutt=3, Dog=6, Pet=20, Hamster=1}
*/
```

输出与 `PetCount.java` 的输出相同。

### 一个动态 `instanceof` 函数

`Class.isInstance()` 方法提供了一种动态测试对象类型的方法。因此，所有这些繁琐的 `instanceof` 语句都可以从 `PetCount.java` 中删除：

```java
// typeinfo/PetCount3.java
// 使用 isInstance() 方法
import java.util.*;
import java.util.stream.*;
import onjava.*;
import typeinfo.pets.*;

public class PetCount3 {
  static class Counter extends
  LinkedHashMap<Class<? extends Pet>, Integer> {

    Counter() {
      super(LiteralPetCreator.ALL_TYPES.stream()
        .map(lpc -> Pair.make(lpc, 0))
        .collect(
          Collectors.toMap(Pair::key, Pair::value)));
    }

    public void count(Pet pet) {
      // Class.isInstance() 替换 instanceof:
      entrySet().stream()
        .filter(pair -> pair.getKey().isInstance(pet))
        .forEach(pair ->
          put(pair.getKey(), pair.getValue() + 1));
    }

    @Override
    public String toString() {
      String result = entrySet().stream()
        .map(pair -> String.format("%s=%s",
          pair.getKey().getSimpleName(),
          pair.getValue()))
        .collect(Collectors.joining(", "));
      return "{" + result + "}";
    }
  }

  public static void main(String[] args) {
    Counter petCount = new Counter();
    Pets.stream()
      .limit(20)
      .peek(petCount::count)
      .forEach(p -> System.out.print(
        p.getClass().getSimpleName() + " "));
    System.out.println("\n" + petCount);
  }
}
/* 输出:
Rat Manx Cymric Mutt Pug Cymric Pug Manx Cymric Rat
EgyptianMau Hamster EgyptianMau Mutt Mutt Cymric Mouse
Pug Mouse Cymric
{Rat=2, Pug=3, Mutt=3, Mouse=2, Cat=9, Dog=6, Cymric=5,
EgyptianMau=2, Rodent=5, Hamster=1, Manx=7, Pet=20}
*/
```

为了计算所有不同类型的 `Pet`，`Counter Map` 预先加载了来自 `LiteralPetCreator.ALL_TYPES` 的类型。如果不预先加载 `Map`，将只计数随机生成的类型，而不是像 `Pet` 和 `Cat` 这样的基本类型。

`isInstance()` 方法消除了对 `instanceof` 表达式的需要。此外，这意味着你可以通过更改 `LiteralPetCreator.types` 数组来添加新类型的 `Pet`；程序的其余部分不需要修改（就像使用 `instanceof` 表达式时那样）。

`toString()` 方法被重载，以便更容易读取输出，该输出仍与打印 `Map` 时看到的典型输出匹配。

### 递归计数

`PetCount3.Counter` 中的 `Map` 预先加载了所有不同的 `Pet` 类。我们可以使用 `Class.isAssignableFrom()` 而不是预加载地图，并创建一个不限于计数 `Pet` 的通用工具：

```java
// onjava/TypeCounter.java
// 计算类型家族的实例数
package onjava;
import java.util.*;
import java.util.stream.*;

public class
TypeCounter extends HashMap<Class<?>, Integer> {
  private Class<?> baseType;

  public TypeCounter(Class<?> baseType) {
    this.baseType = baseType;
  }

  public void count(Object obj) {
    Class<?> type = obj.getClass();
    if(!baseType.isAssignableFrom(type))
      throw new RuntimeException(
        obj + " incorrect type: " + type +
        ", should be type or subtype of " + baseType);
    countClass(type);
  }

  private void countClass(Class<?> type) {
    Integer quantity = get(type);
    put(type, quantity == null ? 1 : quantity + 1);
    Class<?> superClass = type.getSuperclass();
    if(superClass != null &&
       baseType.isAssignableFrom(superClass))
      countClass(superClass);
  }

  @Override
  public String toString() {
    String result = entrySet().stream()
      .map(pair -> String.format("%s=%s",
        pair.getKey().getSimpleName(),
        pair.getValue()))
      .collect(Collectors.joining(", "));
    return "{" + result + "}";
  }
}
```

`count()` 方法获取其参数的 `Class`，并使用 `isAssignableFrom()` 进行运行时检查，以验证传递的对象实际上属于感兴趣的层次结构。`countClass()` 首先计算类的确切类型。然后，如果 `baseType` 可以从超类赋值，则在超类上递归调用 `countClass()`。

```java
// typeinfo/PetCount4.java
import typeinfo.pets.*;
import onjava.*;

public class PetCount4 {
  public static void main(String[] args) {
    TypeCounter counter = new TypeCounter(Pet.class);
    Pets.stream()
      .limit(20)
      .peek(counter::count)
      .forEach(p -> System.out.print(
        p.getClass().getSimpleName() + " "));
    System.out.println("\n" + counter);
  }
}
/* 输出:
Rat Manx Cymric Mutt Pug Cymric Pug Manx Cymric Rat
EgyptianMau Hamster EgyptianMau Mutt Mutt Cymric Mouse
Pug Mouse Cymric
{Dog=6, Manx=7, Cat=9, Rodent=5, Hamster=1, Rat=2,
Pug=3, Mutt=3, Cymric=5, EgyptianMau=2, Pet=20,
Mouse=2}
*/
```

输出表明两个基类型以及精确类型都被计数了。

<!-- Registered Factories -->
## 注册工厂

从 `Pet` 层次结构生成对象的问题是，每当向层次结构中添加一种新类型的 `Pet` 时，必须记住将其添加到 `LiteralPetCreator.java` 中的条目中。在一个定期添加更多类的系统中，这可能会成为问题。

你可能会考虑向每个子类添加静态初始值设定项，因此初始值设定项会将其类添加到某个列表中。不幸的是，静态初始值设定项仅在首次加载类时调用，因此存在鸡和蛋的问题：生成器的列表中没有类，因此它无法创建该类的对象，因此类不会被加载并放入列表中。

基本上，你必须自己手工创建列表（除非你编写了一个工具来搜索和分析源代码，然后创建和编译列表）。所以你能做的最好的事情就是把列表集中放在一个明显的地方。层次结构的基类可能是最好的地方。

我们在这里所做的另一个更改是使用*工厂方法*设计模式将对象的创建推迟到类本身。工厂方法可以以多态方式调用，并为你创建适当类型的对象。事实证明，`java.util.function.Supplier` 用 `T get()` 描述了原型工厂方法。协变返回类型允许 `get()` 为 `Supplier` 的每个子类实现返回不同的类型。

在本例中，基类 `Part` 包含一个工厂对象的静态列表，列表成员类型为 `Supplier<Part>`。对于应该由 `get()` 方法生成的类型的工厂，通过将它们添加到 `prototypes` 列表向基类“注册”。奇怪的是，这些工厂本身就是对象的实例。此列表中的每个对象都是用于创建其他对象的*原型*：

```java
// typeinfo/RegisteredFactories.java
// 注册工厂到基础类
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

class Part implements Supplier<Part> {
  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  static List<Supplier<? extends Part>> prototypes =
    Arrays.asList(
      new FuelFilter(),
      new AirFilter(),
      new CabinAirFilter(),
      new OilFilter(),
      new FanBelt(),
      new PowerSteeringBelt(),
      new GeneratorBelt()
    );

  private static Random rand = new Random(47);
  public Part get() {
    int n = rand.nextInt(prototypes.size());
    return prototypes.get(n).get();
  }
}

class Filter extends Part {}

class FuelFilter extends Filter {
  @Override
  public FuelFilter get() { return new FuelFilter(); }
}

class AirFilter extends Filter {
  @Override
  public AirFilter get() { return new AirFilter(); }
}

class CabinAirFilter extends Filter {
  @Override
  public CabinAirFilter get() {
    return new CabinAirFilter();
  }
}

class OilFilter extends Filter {
  @Override
  public OilFilter get() { return new OilFilter(); }
}

class Belt extends Part {}

class FanBelt extends Belt {
  @Override
  public FanBelt get() { return new FanBelt(); }
}

class GeneratorBelt extends Belt {
  @Override
  public GeneratorBelt get() {
    return new GeneratorBelt();
  }
}

class PowerSteeringBelt extends Belt {
  @Override
  public PowerSteeringBelt get() {
    return new PowerSteeringBelt();
  }
}

public class RegisteredFactories {
  public static void main(String[] args) {
    Stream.generate(new Part())
      .limit(10)
      .forEach(System.out::println);
  }
}

/* 输出:
GeneratorBelt
CabinAirFilter
GeneratorBelt
AirFilter
PowerSteeringBelt
CabinAirFilter
FuelFilter
PowerSteeringBelt
PowerSteeringBelt
FuelFilter
*/
```

并非层次结构中的所有类都应实例化；这里的 `Filter` 和 `Belt` 只是分类器，这样你就不会创建任何一个类的实例，而是只创建它们的子类（请注意，如果尝试这样做，你将获得 `Part` 基类的行为）。

因为 `Part implements Supplier<Part>`，`Part` 通过其 `get()` 方法供应其他 `Part`。如果为基类 `Part` 调用 `get()`（或者如果 `generate()` 调用 `get()`），它将创建随机特定的 `Part` 子类型，每个子类型最终都从 `Part` 继承，并重写相应的 `get()` 以生成它们中的一个。

<!-- Instanceof vs. Class Equivalence -->
## 类的等价比较

When you are querying for type information, there's an important difference between either form of `instanceof` (that is, `instanceof` or `isInstance()`, which produce equivalent results) and the direct comparison of the `Class` objects. Here's an example that demonstrates the difference:

```java
// typeinfo/FamilyVsExactType.java
// instanceof 与 class 的差别
// {java typeinfo.FamilyVsExactType}
package typeinfo;

class Base {}
class Derived extends Base {}

public class FamilyVsExactType {
  static void test(Object x) {
    System.out.println(
      "Testing x of type " + x.getClass());
    System.out.println(
      "x instanceof Base " + (x instanceof Base));
    System.out.println(
      "x instanceof Derived " + (x instanceof Derived));
    System.out.println(
      "Base.isInstance(x) " + Base.class.isInstance(x));
    System.out.println(
      "Derived.isInstance(x) " +
      Derived.class.isInstance(x));
    System.out.println(
      "x.getClass() == Base.class " +
      (x.getClass() == Base.class));
    System.out.println(
      "x.getClass() == Derived.class " +
      (x.getClass() == Derived.class));
    System.out.println(
      "x.getClass().equals(Base.class)) "+
      (x.getClass().equals(Base.class)));
    System.out.println(
      "x.getClass().equals(Derived.class)) " +
      (x.getClass().equals(Derived.class)));
  }
  public static void main(String[] args) {
    test(new Base());
    test(new Derived());
  }
}
/* 输出:
Testing x of type class typeinfo.Base
x instanceof Base true
x instanceof Derived false
Base.isInstance(x) true
Derived.isInstance(x) false
x.getClass() == Base.class true
x.getClass() == Derived.class false
x.getClass().equals(Base.class)) true
x.getClass().equals(Derived.class)) false
Testing x of type class typeinfo.Derived
x instanceof Base true
x instanceof Derived true
Base.isInstance(x) true
Derived.isInstance(x) true
x.getClass() == Base.class false
x.getClass() == Derived.class true
x.getClass().equals(Base.class)) false
x.getClass().equals(Derived.class)) true
*/
```

`test()` 方法使用两种形式的 `instanceof` 对其参数执行类型检查。然后，它获取 `Class` 引用，并使用 `==` 和 `equals()` 测试 `Class` 对象的相等性。令人放心的是，`instanceof` 和 `isInstance()` 产生的结果与 `equals()` 和 `==` 完全相同。但测试本身得出了不同的结论。与类型的概念一致，`instanceof` 说的是“你是这个类，还是从这个类派生的类？”。另一方面，如果使用 `==` 比较实际的 `Class` 对象，则与继承无关 —— 它要么是确切的类型，要么不是。

<!-- Reflection: Runtime Class Information -->
## 反射：运行时类信息

如果你不知道对象的确切类型，RTTI 会告诉你。但是，有一个限制：必须在编译时知道类型，才能使用 RTTI 检测它，并对信息做一些有用的事情。换句话说，编译器必须知道你使用的所有类。

起初，这看起来并没有那么大的限制，但是假设你被赋予了一个对不在程序空间中的对象的引用。实际上，该对象的类在编译时甚至对程序都不可用。也许你从磁盘文件或网络连接中获得了大量的字节，并被告知这些字节代表一个类。由于这个类在编译器为你的程序生成代码后很长时间才会出现，你如何使用这样的类？

在传统编程环境中，这是一个牵强的场景。但是，当我们进入一个更大的编程世界时，会有一些重要的情况发生。第一个是基于组件的编程，你可以在应用程序构建器*集成开发环境*中使用*快速应用程序开发*（RAD）构建项目。这是一种通过将表示组件的图标移动到窗体上来创建程序的可视化方法。然后，通过在程序时设置这些组件的一些值来配置这些组件。这种设计时配置要求任何组件都是可实例化的，它公开自己的部分，并且允许读取和修改其属性。此外，处理*图形用户界面*（GUI）事件的组件必须公开有关适当方法的信息，以便 IDE 可以帮助程序员覆盖这些事件处理方法。反射提供了检测可用方法并生成方法名称的机制。

在运行时发现类信息的另一个令人信服的动机是提供跨网络在远程平台上创建和执行对象的能力。这称为*远程方法调用*（RMI），它使 Java 程序的对象分布在许多机器上。这种分布有多种原因。如果你想加速一个计算密集型的任务，你可以把它分解成小块放到空闲的机器上。或者你可以将处理特定类型任务的代码（例如，多层次客户机/服务器体系结构中的“业务规则”）放在特定的机器上，这样机器就成为描述这些操作的公共存储库，并且可以很容易地更改它以影响系统中的每个人。分布式计算还支持专门的硬件，这些硬件可能擅长于某个特定的任务——例如矩阵转换——但对于通用编程来说不合适或过于昂贵。

类 `Class` 支持*反射*的概念，以及 `java.lang.reflect` 库，其中包含类 `Field`、`Method` 和 `Constructor`（每一个都实现了 `Member` 接口）。这些类型的对象由 JVM 在运行时创建，以表示未知类中的对应成员。然后，可以使用 `Constructor` 创建新对象，`get()` 和 `set()` 方法读取和修改与 `Field` 对象关联的字段，`invoke()` 方法调用与 `Method` 对象关联的方法。此外，还可以调用便利方法 `getFields()`、`getMethods()`、`getConstructors()` 等，以返回表示字段、方法和构造函数的对象数组。（你可以通过在 JDK 文档中查找类 `Class` 来了解更多信息。）因此，匿名对象的类信息可以在运行时完全确定，编译时不需要知道任何信息。

重要的是要意识到反射没有什么魔力。当你使用反射与未知类型的对象交互时，JVM 将查看该对象，并看到它属于特定的类（就像普通的 RTTI）。在对其执行任何操作之前，必须加载 `Class` 对象。因此，该特定类型的 `.class` 文件必须在本地计算机上或通过网络对 JVM 仍然可用。因此，RTTI 和反射的真正区别在于，使用 RTTI 时，编译器在编译时会打开并检查 `.class` 文件。换句话说，你可以用“正常”的方式调用一个对象的所有方法。通过反射，`.class` 文件在编译时不可用；它由运行时环境打开并检查。

### 类方法提取器

通常，你不会直接使用反射工具，但它们可以帮助你创建更多的动态代码。反射是用来支持其他 Java 特性的，例如对象序列化（参见[附录：对象序列化](#ch040.xhtml#appendix-object-serialization)）。但是，有时动态提取有关类的信息很有用。

考虑一个类方法提取器。查看类定义的源代码或 JDK 文档，只显示*在该类定义中*定义或重写的方法。但是，可能还有几十个来自基类的可用方法。找到它们既单调又费时。

```java
// typeinfo/ShowMethods.java
// 使用反射展示一个类的所有方法，甚至包括定义在基类中方法
// {java ShowMethods ShowMethods}
import java.lang.reflect.*;
import java.util.regex.*;

public class ShowMethods {
  private static String usage =
    "usage:\n" +
    "ShowMethods qualified.class.name\n" +
    "To show all methods in class or:\n" +
    "ShowMethods qualified.class.name word\n" +
    "To search for methods involving 'word'";

  private static Pattern p = Pattern.compile("\\w+\\.");
  public static void main(String[] args) {

    if(args.length < 1) {
      System.out.println(usage);
      System.exit(0);
    }
    int lines = 0;
    try {
      Class<?> c = Class.forName(args[0]);
      Method[] methods = c.getMethods();
      Constructor[] ctors = c.getConstructors();
      if(args.length == 1) {
        for(Method method : methods)
          System.out.println(
            p.matcher(
              method.toString()).replaceAll(""));
        for(Constructor ctor : ctors)
          System.out.println(
            p.matcher(ctor.toString()).replaceAll(""));
        lines = methods.length + ctors.length;
      } else {
        for(Method method : methods)
          if(method.toString().contains(args[1])) {
            System.out.println(p.matcher(
              method.toString()).replaceAll(""));
            lines++;
          }
        for(Constructor ctor : ctors)
          if(ctor.toString().contains(args[1])) {
            System.out.println(p.matcher(
              ctor.toString()).replaceAll(""));
            lines++;
          }
      }
    } catch(ClassNotFoundException e) {
      System.out.println("No such class: " + e);
    }
  }
}
/* 输出:
public static void main(String[])
public final void wait() throws InterruptedException
public final void wait(long,int) throws
InterruptedException
public final native void wait(long) throws
InterruptedException
public boolean equals(Object)
public String toString()
public native int hashCode()
public final native Class getClass()
public final native void notify()
public final native void notifyAll()
public ShowMethods()
*/
```

`Class` 方法 `getmethods()` 和'getconstructors()`  分别返回 `Method` 数组和 `Constructor` 数组。这些类中的每一个都有进一步的方法来解析它们所表示的方法的名称、参数和返回值。但你也可以像这里所做的那样，使用 `toString()`，生成带有整个方法签名的 `String`。代码的其余部分提取命令行信息，确定特定签名是否与目标 `String`（使用 `indexOf()`）匹配，并使用正则表达式（在 [Strings](#ch021.xhtml#strings) 一章中介绍）删除名称限定符。

编译时无法知道 `Class.forName()` 生成的结果，因此所有方法签名信息都是在运行时提取的。如果你研究 JDK 反射文档，你将看到有足够的支持来实际设置和对编译时完全未知的对象进行方法调用（本书后面有这样的例子）。虽然最初你可能认为你永远都不需要这样做，但是反射的全部价值可能会令人惊讶。

上面的输出来自命令行：

```java
java ShowMethods ShowMethods
```

输出包含一个 `public` 无参数构造函数，即使未定义构造函数。你看到的构造函数是由编译器自动合成的。如果将 `ShowMethods` 设置为非 `public` 类（即只有包级访问权），则合成的无参数构造函数将不再显示在输出中。自动为合成的无参数构造函数授予与类相同的访问权。

尝试运行 `java ShowMethods java.lang.String`，并附加一个 `char`、`int`、`String` 等参数。

编程时，当你不记得某个类是否有特定的方法，并且不想在 JDK 文档中搜索索引或类层次结构时，或者如果你不知道该类是否可以对 `Color` 对象执行任何操作时，该工具能节省不少时间。

<!-- Dynamic Proxies -->
## 动态代理


<!-- Using Optional -->
## Optional类


<!-- Interfaces and Type -->
## 接口和类型


<!-- Summary -->
## 本章小结


<!-- 分页 -->

<div style="page-break-after: always;"></div>