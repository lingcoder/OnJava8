[TOC]

<!-- Enumerations -->
# 第二十二章 枚举

> 关键字enum可以将一组具名的值的有限集合创建为一种新的类型，而这些具名的值可以作为常规的程序组件使用。这是一种非常有用的功能

在[初始化和清理]()这章结束的时候，我们已经简单地介绍了枚举的概念。现在，你对Java已经有了更深刻的理解，因此可以更深入地学习 Java 中的枚举了。你将在本章中看到，使用enum可以做很多有趣的事情，同时，我们也会深入其他的Java特性，例如泛型和反射。在这个过程中，我们还将学习一些设计模式。

<!-- Basic enum Features -->

## 基本 enum 特性

我们已经在[初始化和清理]()这章章看到，调用enum的values()方法，可以遍历enum实例 .values()方法返回enum实例的数组，而且该数组中的元素严格保持其在enum中声明时的顺序，因此你可以在循环中使用values()返回的数组。

创建enum时，编译器会为你生成一个相关的类，这个类继承自Java.lang.Enum。下面的例子演示了Enum提供的一些功能：

```java
// enums/EnumClass.java
// Capabilities of the Enum class
enum Shrubbery { GROUND, CRAWLING, HANGING }
public class EnumClass {
    public static void main(String[] args) {
        for(Shrubbery s : Shrubbery.values()) {
            System.out.println(
                    s + " ordinal: " + s.ordinal());
            System.out.print(
                    s.compareTo(Shrubbery.CRAWLING) + " ");
            System.out.print(
                    s.equals(Shrubbery.CRAWLING) + " ");
            System.out.println(s == Shrubbery.CRAWLING);
            System.out.println(s.getDeclaringClass());
            System.out.println(s.name());
            System.out.println("********************");
        }
// Produce an enum value from a String name:
        for(String s :
                "HANGING CRAWLING GROUND".split(" ")) {
            Shrubbery shrub =
                    Enum.valueOf(Shrubbery.class, s);
            System.out.println(shrub);
        }
    }
}
```

输出：

```
GROUND ordinal: 0
-1 false false
class Shrubbery
GROUND
********************
CRAWLING ordinal: 1
0 true true
class Shrubbery
CRAWLING
********************
HANGING ordinal: 2
1 false false
class Shrubbery
HANGING
********************
HANGING
CRAWLING
GROUND
```

ordinal()方法返回一个int值，这是每个enum实例在声明时的次序，从0开始。可以使用==来比较enum实例，编译器会自动为你提供equals()和hashCode()方法。Enum类实现了Comparable接口，所以它具有compareTo()方法。同时，它还实现了Serializable接口。

如果在enum实例上调用getDeclaringClass()方法，我们就能知道其所属的enum类。

name()方法返回enum实例声明时的名字，这与使用toString()方法效果相同。valueOf()是在Enum中定义的static方法，它根据给定的名字返回相应的enum实例，如果不存在给定名字的实例，将会抛出异常。

### 将静态类型导入用于 enum

先看一看 [初始化和清理]() 这章中Burrito.java的另一个版本：

```java
// enums/SpicinessEnum.java
package enums;
public enum SpicinessEnum {
    NOT, MILD, MEDIUM, HOT, FLAMING
}
// enums/Burrito2.java
// {java enums.Burrito2}
package enums;
import static enums.SpicinessEnum.*;
public class Burrito2 {
    SpicinessEnum degree;
    public Burrito2(SpicinessEnum degree) {
        this.degree = degree;
    }
    @Override
    public String toString() {
        return "Burrito is "+ degree;
    }
    public static void main(String[] args) {
        System.out.println(new Burrito2(NOT));
        System.out.println(new Burrito2(MEDIUM));
        System.out.println(new Burrito2(HOT));
    }
}
```

输出为：

```
Burrito is NOT
Burrito is MEDIUM
Burrito is HOT
```

使用static import能够将enum实例的标识符带入当前的命名空间，所以无需再用enum类型来修饰enum实例。这是一个好的想法吗？或者还是显式地修饰enum实例更好？这要看代码的复杂程度了。编译器可以确保你使用的是正确的类型，所以唯一需要担心的是，使用静态导入会不会导致你的代码令人难以理解。多数情况下，使用static import还是有好处的，不过，程序员还是应该对具体情况进行具体分析。

注意，在定义enum的同一个文件中，这种技巧无法使用，如果是在默认包中定义enum，这种技巧也无法使用（在Sun内部对这一点显然也有不同意见）。

<!-- Adding Methods to an enum -->

## 方法添加

除了不能继承自一个enum之外，我们基本上可以将enum看作一个常规的类。也就是说我们可以向enum中添加方法。enum甚至可以有main()方法。

一般来说，我们希望每个枚举实例能够返回对自身的描述，而不仅仅只是默认的toString()实现，这只能返回枚举实例的名字。为此，你可以提供一个构造器，专门负责处理这个额外的信息，然后添加一个方法，返回这个描述信息。看一看下面的示例：

```java
// enums/OzWitch.java
// The witches in the land of Oz
public enum OzWitch {
    // Instances must be defined first, before methods:
    WEST("Miss Gulch, aka the Wicked Witch of the West"),
    NORTH("Glinda, the Good Witch of the North"),
    EAST("Wicked Witch of the East, wearer of the Ruby " +
            "Slippers, crushed by Dorothy's house"),
    SOUTH("Good by inference, but missing");
    private String description;
    // Constructor must be package or private access:
    private OzWitch(String description) {
        this.description = description;
    }
    public String getDescription() { return description; }
    public static void main(String[] args) {
        for(OzWitch witch : OzWitch.values())
            System.out.println(
                    witch + ": " + witch.getDescription());
    }
}
```

输出为：

```
WEST: Miss Gulch, aka the Wicked Witch of the West
NORTH: Glinda, the Good Witch of the North
EAST: Wicked Witch of the East, wearer of the Ruby
Slippers, crushed by Dorothy's house
SOUTH: Good by inference, but missing
```

注意，如果你打算定义自己的方法，那么必须在enum实例序列的最后添加一个分号。同时，Java要求你必须先定义enum实例。如果在定义enum实例之前定义了任何方法或属性，那么在编译时就会得到错误信息。

enum中的构造器与方法和普通的类没有区别，因为除了有少许限制之外，enum就是一个普通的类。所以，我们可以使用enum做许多事情（虽然，我们一般只使用普通的枚举类型）

在这个例子中，虽然我们有意识地将enum的构造器声明为private，但对于它的可访问性而言，其实并没有什么变化，因为（即使不声明为private）我们只能在enum定义的内部使用其构造器创建enum实例。一旦enum的定义结束，编译器就不允许我们再使用其构造器来创建任何实例了。


<!-- enums in switch Statements -->
## switch语句


<!-- The Mystery of values() -->
## values方法


<!-- Implements, not Inherits -->
## 实现而非继承

<!-- Random Selection -->

## 随机选择


<!-- Using Interfaces for Organization -->
## 使用接口组织


<!-- Using EnumSet Instead of Flags -->
## 使用EnumSet替代Flags


<!-- Using EnumMap -->
## 使用EnumMap


<!-- Constant-Specific Methods -->
## 常量特定方法


<!-- Multiple Dispatching -->
## 多次调度


<!-- Summary -->
## 本章小结



<!-- 分页 -->

<div style="page-break-after: always;"></div>