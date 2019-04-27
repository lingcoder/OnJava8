[TOC]

<!-- Type Information -->
# 第十九章 类型信息

> RTTI（RunTime Type Information，运行时类型信息）能够在程序运行时发现和使用类型信息

RTTI把我们从只能在编译期进行面向类型操作的禁锢中解脱了出来，并且让我们可以使用某些非常强大的程序。对RTTI的需要，揭示了面向对象设计中许多有趣（并且复杂）的特性，同时也带来了关于如何组织程序的基本问题。

本章将讨论Java是如何在运行时识别对象和类信息的。主要有两种方式：

1. “传统的” RTTI：假定我们在编译时已经知道了所有的类型；
2. “反射”机制：允许我们在运行时发现和使用类的信息。

<!-- The Need for RTTI -->

## 为什么需要RTTI

下面看一下我们已经很熟悉的一个例子，它使用了多态的类层次结构。基类`Shape`是泛化的类型，从它派生出了三个具体类： `Circle` 、`Square` 和 `Triangle` （见下图所示）。

![多态例子Shape的类层次结构图](../images/image-20190409114913825-4781754.png)

这是一个典型的类层次结构图，基类位于顶部，派生类向下扩展。面向对象编程的一个基本目的是：让代码只操纵对基类(这里即 `Shape` )的引用。这样，如果你想添加一个新类(比如从`Shape`派生出`Rhomboid`)来扩展程序，就不会影响原来的代码。在这个例子中，`Shape`接口中动态绑定了`draw()`方法，这样做的目的就是让客户端程序员可以使用泛化的`Shape`引用来调用`draw()`。`draw()`方法在所有派生类里都会被覆盖，而且由于它是动态绑定的，所以它可以使用`Shape`引用来调用，这就是多态。

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

这个例子中，在把 `Shape` 对象放入 `Stream<Shape>` 中时就会进行向上转型(隐式)，但在向上转型的时候也丢失了这些对象的具体类型。对 `steam` 而言，它们只是 `Shape` 对象。

严格来说，`Stream<Shape>` 实际上是把放入其中的所有对象都当做 `Object` 对象来持有，只是取元素时会自动将其类型转为 `Shape`。这也是 RTTI 最基本的使用形式，因为在 Java 中，所有类型转换的正确性检查都是在运行时进行的。这也正是 RTTI 的含义所在：在运行时，识别一个对象的类型。

另外在这个例子中，类型转换并不彻底：`Object` 被转型为 `Shape` ，而不是 `Circle`、`Square` 或者 `Triangle`。这是因为目前我们只能确保这个 `Stream<Shape>` 保存的都是 `Shape`：

- 编译期，`stream` 和  Java  泛型系统确保放入 `stream` 的都是 `Shape` 对象（`Shape` 子类的对象也可视为 `Shape` 的对象），否则编译器会报错；
- 运行时，自动类型转换确保了从 `stream` 中取出的对象都是 `Shape` 类型。

接下来就是多态机制的事了，`Shape` 对象实际执行什么样的代码，是由引用所指向的具体对象（`Circle`、`Square` 或者 `Triangle`）决定的。这也符合我们编写代码的一般需求，通常，我们希望大部分代码尽可能少了解对象的具体类型，而是只与对象家族中的一个通用表示打交道（本例中即为 `Shape`）。这样，代码会更容易写，更易读和维护；设计也更容易实现，更易于理解和修改。所以多态是面向对象的基本目标。

但是，有时你会碰到一些编程问题，在这些问题中如果你能知道某个泛化引用的具体类型，就可以把问题轻松解决。例如，假设我们允许用户将某些几何形状高亮显示，现在希望找到屏幕上所有高亮显示的三角形；或者，我们现在需要旋转所有图形，但是想跳过圆形(因为圆形旋转没有意义)。这时我们就希望知道 `Stream<Shape>` 里边的形状具体是什么类型，而Java 实际上也满足了我们的这种需求。使用 RTTI，我们可以查询某个 `Shape` 引用所指向对象的确切类型，然后选择或者剔除特例。

<!-- The Class Object -->

## `Class` 对象

要理解 RTTI 在 Java 中的工作原理，首先必须知道类型信息在运行时是如何表示的。这项工作是由称为 **`Class`对象** 的特殊对象完成的，它包含了与类有关的信息。实际上，`Class` 对象就是用来创建该类所有"常规"对象的。Java 使用 `Class` 对象来实现 RTTI，即便是类型转换这样的操作都是用 `Class` 对象实现的。不仅如此，`Class` 类还提供了很多使用RTTI的其它方式。

类是程序的一部分，每个类都有一个 `Class` 对象。换言之，每当我们编写并且编译了一个新类，就会产生一个 `Class` 对象（更恰当的说，是被保存在一个同名的 `.class` 文件中）。为了生成这个类的对象，Java虚拟机(JVM)先会调用"类加载器"子系统把这个类加载到内存中。

类加载器子系统可能包含一条类加载器链，但有且只有一个**原生类加载器**，它是JVM实现的一部分。原生类加载器加载的是”可信类”（包括Java API类）。它们通常是从本地盘加载的。在这条链中，通常不需要添加额外的类加载器，但是如果你有特殊需求（例如以某种特殊的方式加载类，以支持Web服务器应用，或者通过网络下载类），也可以挂载额外的类加载器。

所有的类都是第一次使用时动态加载到JVM中的，当程序创建第一个对类的静态成员的引用时，就会加载这个类。

> 其实构造器也是类的静态方法，虽然构造器前面并没有 `static` 关键字。所以，使用 `new` 操作符创建类的新对象，这个操作也算作对类的静态成员引用。

因此，Java 程序在它开始运行之前并没有被完全加载，很多部分是在需要时才会加载。这一点与许多传统编程语言不同，动态加载使得Java具有一些静态加载语言（如C++）很难或者根本不可能实现的特性。

类加载器首先会检查这个类的 `Class` 对象是否已经加载，如果尚未加载，默认的类加载器就会根据类名查找 `.class` 文件（如果有附加的类加载器，这时候可能就会在数据库中或者通过其它方式获得字节码）。这个类的字节码被加载后，JVM会对其进行验证，确保它没有损坏，并且不包含不良的Java代码(这是Java安全防范的一种措施)。

一旦某个类的 `Class` 对象被载入内存，它就可以用来创建这个类的所有对象。下面的示范程序可以证明这点：

```java
// typeinfo/SweetShop.java
// Examination of the way the class loader works
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

上面的代码中，`Candy`、`Gum` 和 `Cookie` 这几个类都有一个 `static{...}` 静态初始化块，这些静态初始化块在类第一次被加载的时候就会执行。也就是说，静态初始化块会打印出相应的信息，告诉我们这些类分别是什么时候被加载了。而在 `main()` 里边，创建对象 的代码都放在了 `print()` 语句之间，以帮助我们判断类加载的时间点。

从输出中可以看到，`Class` 对象仅在需要的时候才会被加载，`static` 初始化是在类加载时进行的。

代码里面还有特别有趣的一行：

```java
Class.forName("Gum");
```

所有 `Class` 对象都属于 `Class` 类，而且它跟其他普通对象一样，我们可以获取和操控它的引用(这也是类加载器的工作)。`forName()` 是 `Class` 类的一个静态方法，我们可以使用 `forName()` 根据目标类的类名（`String`）得到该类的 `Class` 对象。上面的代码忽略了 `forName()` 的返回值，因为那个调用是为了得到它产生的“副作用”。从结果可以看出，`forName()` 执行的副作用是如果`Gum`类没有被加载就加载它，而在加载的过程中，`Gum` 的 `static` 初始化块被执行了。

还需要注意的是，如果 `Class.forName()` 找不到要加载的类，它就会抛出异常 `ClassNotFoundException`。上面的例子中我们只是简单地报告了问题，但在更严密的程序里，就要考虑在异常处理程序中把问题解决掉（具体例子详见[设计模式](./25-Patterns)章节）。

无论何时，只要你想在运行时使用类型信息，就必须先得到那个 `Class` 对象的引用。`Class.forName()` 就是实现这个功能的一个便捷途径，因为使用该方法你不需要先持有这个类型 的对象。但是，如果你已经拥有了目标类的对象，那就可以通过调用 `getClass()` 方法来获取 `Class` 引用了，这个方法来自根类 `Object`，它将返回表示该对象实际类型的 `Class`对象的引用。`Class` 包含很多有用的方法，下面代码展示了其中的一部分：

```java
// typeinfo/toys/ToyTest.java
// Testing class Class
// {java typeinfo.toys.ToyTest}
package typeinfo.toys;

interface HasBatteries {}
interface Waterproof {}
interface Shoots {}

class Toy {
    // Comment out the following no-arg
    // constructor to see NoSuchMethodError
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

`FancyToy` 继承自 `Toy` 并实现了 `HasBatteries`、`Waterproof` 和 `Shoots` 接口。在 `main()` 中，我们创建了一个 `Class` 引用，然后在 `try` 语句里边用 `forName()` 方法创建了一个 `FancyToy` 的类对象并赋值给该引用。需要注意的是，传递给 `forName()` 的字符串必须使用类的全限定名（包含包名）。

`printInfo()` 函数使用 `getName()` 来产生完整类名，使用 `getSimpleName()` 产生不带包名的类名，`getCanonicalName()` 也是产生完整类名（除内部类和数组外，对大部分类产生的结果与 `getName()` 相同）。`isInterface()` 用于判断某个 `Class` 对象代表的是否为一个接口。因此，通过 `Class` 对象，你可以得到关于该类型的所有信息。

在 `main()` 中调用的 `Class.getInterface()` 方法返回的是存放 `Class` 对象的数组，里面的 `Class` 对象表示的是那个类实现的接口。 

另外，你还可以调用 `getSuperclass()` 方法来得到父类的 `Class` 对象，再用父类的 `Class` 对象调用该方法，重复多次，你就可以得到一个对象完整的类继承结构。

`Class` 对象的 `newInstance()` 方法是实现“虚拟构造器”的一种途径，虚拟构造器可以让你在不知道一个类的确切类型的时候，创建这个类的对象。在前面的例子中，`up` 只是一个 `Class` 对象的引用，在编译期并不知道这个引用会指向哪个类的 `Class` 对象。当你创建新实例时，会得到一个 `Object` 引用，但是这个引用指向的是 `Toy` 对象。当然，由于得到的是 `Object` 引用，目前你只能给它发送 `Object` 对象能够接受的调用。而如果你想请求具体对象才有的调用，你就得先获取该对象更多的类型信息，并执行某种转型。另外，使用 `newInstance()` 来创建的类，必须带有无参数的构造书。在本章稍后部分，你将会看到如何通过Java的反射API，用任意的构造器来动态的创建类的对象。

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
我的建议是使用 `.class` 的形式， 以保持与普通类的一致性。

注意，有一点很有趣：当使用 `.class` 来创建对 `Class` 对象的引用时，不会自动地初始化该`Class` 对象。为了使用类而做的准备工作实际包含三个步骤：

1. **加载**，这是由类加载器执行的。该步骤将查找字节码（通常在classpath所指定的路径中查找，但这并非是必须的），并从这些字节码中创建一个 `Class` 对象。

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

但是，Java设计者看准机会，将它的类型变得更具体了一些。Java引入泛型语法之后，我们可以使用泛型对 `Class` 引用所指向的 `Class` 对象的类型进行限定。在下面的实例中，两种语法都是正确的：

```java
// typeinfo/GenericClassReferences.java

public class GenericClassReferences {
    public static void main(String[] args) {
        Class intClass = int.class;
        Class<Integer> genericIntClass = int.class;
        genericIntClass = Integer.class; // Same thing
        intClass = double.class;
        // genericIntClass = double.class; // Illegal
    }
}
```

普通的类引用不会产生警告信息。你可以看到，普通的类引用可以重新赋值指向任何其他的 `Class` 对象，但是使用泛型限定的类引用只能指向其声明的类型。通过使用泛型语法，我们可以让编译器强制执行额外的类型检查。

那如果我们希望稍微放松一些限制，应该怎么办呢？乍一看，下面的操作好像是可以的：

```java
Class<Number> geenericNumberClass = int.class;
```

这看起来似乎是起作用的，因为 `Integer` 继承自 `Number`。但事实却是不行，因为 `Integer` 的 `Class` 对象并不是 `Number`的 `Class` 对象的子类（这看起来可能有点诡异，我们将在[泛型](./20-Generics)这一章详细讨论）。

为了在使用 `Class` 引用时放松限制，我们使用了通配符，它是Java泛型中的一部分。通配符就是 `?`，表示“任何事物”。因此，我们可以在上例的普通 `Class` 引用中添加通配符，并产生相同的结果：

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
// Testing class Class
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

Java中还有用于 `Class` 引用的转型语法，即 `cast()` 方法：

```java
// typeinfo/ClassCasts.java

class Building {}
class House extends Building {}

public class ClassCasts {
    public static void main(String[] args) {
        Building b = new House();
        Class<House> houseType = House.class;
        House h = houseType.cast(b);
        h = (House)b; // ... or just do this.
    }
}
```

`cast()` 方法接受参数对象，并将其转型为 `Class` 引用的类型。但是，如果观察上面的代码，你就会发现，与实现了相同功能的 `main()` 中最后一行相比，这种转型好像做了很多额外的工作。

`cast()` 在无法使用普通转型的情况下会显得非常有用，在你编写泛型代码（你将在[泛型](./20-Generics)这一章学习到）时，如果你保存了 `Class` 引用，并希望以后通过这个引用来执行转型，你就需要用到 `cast()`。但事实却是这种情况并不常见，我发现整个Java类库中，只有一处使用了 `cast()`（在 `com.sun.mirror.util.DeclarationFilter` 中）。

Java类库中另一个没有任何用处的特性就是 `Class.asSubclass()`，该方法允许你将一个 `Class` 对象转型为更加具体的类型。

<!-- Checking Before a Cast -->

## 类型转换检测

<!-- Registered Factories -->

## 注册工厂


<!-- Instanceof vs. Class Equivalence -->
## 类的等价比较

<!-- Reflection: Runtime Class Information -->

## 反射运行时类信息


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