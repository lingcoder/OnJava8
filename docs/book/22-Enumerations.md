[TOC]

<!-- Enumerations -->
# 第二十二章 枚举

> 关键字 enum 可以将一组具名的值的有限集合创建为一种新的类型，而这些具名的值可以作为常规的程序组件使用。这是一种非常有用的功能

在[初始化和清理 ]() 这章结束的时候，我们已经简单地介绍了枚举的概念。现在，你对 Java 已经有了更深刻的理解，因此可以更深入地学习 Java 中的枚举了。你将在本章中看到，使用 enum 可以做很多有趣的事情，同时，我们也会深入其他的 Java 特性，例如泛型和反射。在这个过程中，我们还将学习一些设计模式。

<!-- Basic enum Features -->

## 基本 enum 特性

我们已经在[初始化和清理 ]() 这章章看到，调用 enum 的 values() 方法，可以遍历 enum 实例 .values() 方法返回 enum 实例的数组，而且该数组中的元素严格保持其在 enum 中声明时的顺序，因此你可以在循环中使用 values() 返回的数组。

创建 enum 时，编译器会为你生成一个相关的类，这个类继承自 Java.lang.Enum。下面的例子演示了 Enum 提供的一些功能：

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

ordinal() 方法返回一个 int 值，这是每个 enum 实例在声明时的次序，从 0 开始。可以使用==来比较 enum 实例，编译器会自动为你提供 equals() 和 hashCode() 方法。Enum 类实现了 Comparable 接口，所以它具有 compareTo() 方法。同时，它还实现了 Serializable 接口。

如果在 enum 实例上调用 getDeclaringClass() 方法，我们就能知道其所属的 enum 类。

name() 方法返回 enum 实例声明时的名字，这与使用 toString() 方法效果相同。valueOf() 是在 Enum 中定义的 static 方法，它根据给定的名字返回相应的 enum 实例，如果不存在给定名字的实例，将会抛出异常。

### 将静态类型导入用于 enum

先看一看 [初始化和清理 ]() 这章中 Burrito.java 的另一个版本：

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

使用 static import 能够将 enum 实例的标识符带入当前的命名空间，所以无需再用 enum 类型来修饰 enum 实例。这是一个好的想法吗？或者还是显式地修饰 enum 实例更好？这要看代码的复杂程度了。编译器可以确保你使用的是正确的类型，所以唯一需要担心的是，使用静态导入会不会导致你的代码令人难以理解。多数情况下，使用 static import 还是有好处的，不过，程序员还是应该对具体情况进行具体分析。

注意，在定义 enum 的同一个文件中，这种技巧无法使用，如果是在默认包中定义 enum，这种技巧也无法使用（在 Sun 内部对这一点显然也有不同意见）。

<!-- Adding Methods to an enum -->

## 方法添加

除了不能继承自一个 enum 之外，我们基本上可以将 enum 看作一个常规的类。也就是说我们可以向 enum 中添加方法。enum 甚至可以有 main() 方法。

一般来说，我们希望每个枚举实例能够返回对自身的描述，而不仅仅只是默认的 toString() 实现，这只能返回枚举实例的名字。为此，你可以提供一个构造器，专门负责处理这个额外的信息，然后添加一个方法，返回这个描述信息。看一看下面的示例：

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

注意，如果你打算定义自己的方法，那么必须在 enum 实例序列的最后添加一个分号。同时，Java 要求你必须先定义 enum 实例。如果在定义 enum 实例之前定义了任何方法或属性，那么在编译时就会得到错误信息。

enum 中的构造器与方法和普通的类没有区别，因为除了有少许限制之外，enum 就是一个普通的类。所以，我们可以使用 enum 做许多事情（虽然，我们一般只使用普通的枚举类型）

在这个例子中，虽然我们有意识地将 enum 的构造器声明为 private，但对于它的可访问性而言，其实并没有什么变化，因为（即使不声明为 private）我们只能在 enum 定义的内部使用其构造器创建 enum 实例。一旦 enum 的定义结束，编译器就不允许我们再使用其构造器来创建任何实例了。

### 覆盖 enum 的方法

覆盖 toSring() 方法，给我们提供了另一种方式来为枚举实例生成不同的字符串描述信息。
在下面的示例中，我们使用的就是实例的名字，不过我们希望改变其格式。覆盖 enum 的 toSring() 方法与覆盖一般类的方法没有区别：

```java
// enums/SpaceShip.java
import java.util.stream.*;
public enum SpaceShip {
    SCOUT, CARGO, TRANSPORT,
    CRUISER, BATTLESHIP, MOTHERSHIP;
    @Override
    public String toString() {
        String id = name();
        String lower = id.substring(1).toLowerCase();
        return id.charAt(0) + lower;
    }
    public static void main(String[] args) {
        Stream.of(values())
                .forEach(System.out::println);
    }
}
```

输出为：

```
Scout
Cargo
Transport
Cruiser
Battleship
Mothership
```

toString() 方法通过调用 name() 方法取得 SpaceShip 的名字，然后将其修改为只有首字母大写的格式。




<!-- enums in switch Statements -->
## switch 语句中的 enum

在 switch 中使用 enum，是 enum 提供的一项非常便利的功能。一般来说，在 switch 中只能使用整数值，而枚举实例天生就具备整数值的次序，并且可以通过 ordinal() 方法取得其次序（显然编译器帮我们做了类似的工作），因此我们可以在 switch 语句中使用 enum。

虽然一般情况下我们必须使用 enum 类型来修饰一个 enum 实例，但是在 case 语句中却不必如此。下面的例子使用 enum 构造了一个小型状态机：

```java
// enums/TrafficLight.java
// Enums in switch statements
// Define an enum type:
enum Signal { GREEN, YELLOW, RED, }

public class TrafficLight {
    Signal color = Signal.RED;
    public void change() {
        switch(color) {
           // Note you don't have to say Signal.RED
            // in the case statement:
            case RED: color = Signal.GREEN;
                break;
            case GREEN: color = Signal.YELLOW;
                break;
            case YELLOW: color = Signal.RED;
                break;
        }
    }
    @Override
    public String toString() {
        return "The traffic light is " + color;
    }
    public static void main(String[] args) {
        TrafficLight t = new TrafficLight();
        for(int i = 0; i < 7; i++) {
            System.out.println(t);
            t.change();
        }
    }
}
```

输出为：

```
The traffic light is RED
The traffic light is GREEN
The traffic light is YELLOW
The traffic light is RED
The traffic light is GREEN
The traffic light is YELLOW
The traffic light is RED
```

编译器并没有抱怨 switch 中没有 default 语句，但这并不是因为每一个 Signal 都有对应的 case 语句。如果你注释掉其中的某个 case 语句，编译器同样不会抱怨什么。这意味着，你必须确保自己覆盖了所有的分支。但是，如果在 case 语句中调用 return，那么编译器就会抱怨缺少 default 语句了。这与是否覆盖了 enum 的所有实例无关。




<!-- The Mystery of values() -->
## values 方法的神秘之处

前面已经提到，编译器为你创建的 enum 类都继承自 Enum 类。然而，如果你研究一下 Enum 类就会发现，它并没有 values() 方法。可我们明明已经用过该方法了，难道存在某种“隐藏的”方法吗？我们可以利用反射机制编写一个简单的程序，来查看其中的究竟：

```java
// enums/Reflection.java
// Analyzing enums using reflection
import java.lang.reflect.*;
import java.util.*;
import onjava.*;
enum Explore { HERE, THERE }
public class Reflection {
    public static
    Set<String> analyze(Class<?> enumClass) {
        System.out.println(
                "_____ Analyzing " + enumClass + " _____");
        System.out.println("Interfaces:");
        for(Type t : enumClass.getGenericInterfaces())
            System.out.println(t);
        System.out.println(
                "Base: " + enumClass.getSuperclass());
        System.out.println("Methods: ");
        Set<String> methods = new TreeSet<>();
        for(Method m : enumClass.getMethods())
            methods.add(m.getName());
        System.out.println(methods);
        return methods;
    }
    public static void main(String[] args) {
        Set<String> exploreMethods =
                analyze(Explore.class);
        Set<String> enumMethods = analyze(Enum.class);
        System.out.println(
                "Explore.containsAll(Enum)? " +
                        exploreMethods.containsAll(enumMethods));
        System.out.print("Explore.removeAll(Enum): ");
        exploreMethods.removeAll(enumMethods);
        System.out.println(exploreMethods);
// Decompile the code for the enum:
        OSExecute.command(
                "javap -cp build/classes/main Explore");
    }
}
```

输出为：

```java
_____ Analyzing class Explore _____
Interfaces:
Base: class java.lang.Enum
Methods:
[compareTo, equals, getClass, getDeclaringClass,
hashCode, name, notify, notifyAll, ordinal, toString,
valueOf, values, wait]
_____ Analyzing class java.lang.Enum _____
Interfaces:
java.lang.Comparable<E>
interface java.io.Serializable
Base: class java.lang.Object
Methods:
[compareTo, equals, getClass, getDeclaringClass,
hashCode, name, notify, notifyAll, ordinal, toString,
valueOf, wait]
Explore.containsAll(Enum)? true
Explore.removeAll(Enum): [values]
Compiled from "Reflection.java"
final class Explore extends java.lang.Enum<Explore> {
  public static final Explore HERE;
  public static final Explore THERE;
  public static Explore[] values();
  public static Explore valueOf(java.lang.String);
  static {};
}
```

答案是，values() 是由编译器添加的 static 方法。可以看出，在创建 Explore 的过程中，编译器还为其添加了 valueOf() 方法。这可能有点令人迷惑，Enum 类不是已经有 valueOf() 方法了吗。

不过 Enum 中的 valueOf() 方法需要两个参数，而这个新增的方法只需一个参数。由于这里使用的 Set 只存储方法的名字，而不考虑方法的签名，所以在调用 Explore.removeAll(Enum) 之后，就只剩下[values] 了。

从最后的输出中可以看到，编译器将 Explore 标记为 final 类，所以无法继承自 enum，其中还有一个 static 的初始化子句，稍后我们将学习如何重定义该句。

由于擦除效应（在[泛型 ]() 章节中介绍过），反编译无法得到 Enum 的完整信息，所以它展示的 Explore 的父类只是一个原始的 Enum，而非事实上的 Enum\<Explore\>。

由于 values() 方法是由编译器插入到 enum 定义中的 static 方法，所以，如果你将 enum 实例向上转型为 Enum，那么 values() 方法就不可访问了。不过，在 Class 中有一个 getEnumConstants0 方法，所以即便 Enum 接口中没有 values0 方法，我们仍然可以通过 Class 对象取得所有 enum 实例。

```java
// enums/UpcastEnum.java
// No values() method if you upcast an enum
enum Search { HITHER, YON }
public class UpcastEnum {
    public static void main(String[] args) {
        Search[] vals = Search.values();
        Enum e = Search.HITHER; // Upcast
// e.values(); // No values() in Enum
        for(Enum en : e.getClass().getEnumConstants())
            System.out.println(en);
    }
}
```

输出为：

```
HITHER
YON
```

因为 getEnumConstants() 是 Class 上的方法，所以你甚至可以对不是枚举的类调用此方法：

```java
// enums/NonEnum.java
public class NonEnum {
    public static void main(String[] args) {
        Class<Integer> intClass = Integer.class;
        try {
            for(Object en : intClass.getEnumConstants())
                System.out.println(en);
        } catch(Exception e) {
            System.out.println("Expected: " + e);
        }
    }
}
```

输出为：

```java
Expected: java.lang.NullPointerException
```

只不过，此时该方法返回 null，所以当你试图使用其返回的结果时会发生异常。


<!-- Implements, not Inherits -->
## 实现而非继承

我们已经知道，所有的 enum 都继承自 Java.lang.Enum 类。由于 Java 不支持多重继承，所以你的 enum 不能再继承其他类：

```java
enum NotPossible extends Pet { ... // Won't work
```

然而，在我们创建一个新的 enum 时，可以同时实现一个或多个接口：

```java
// enums/cartoons/EnumImplementation.java
// An enum can implement an interface
// {java enums.cartoons.EnumImplementation}
package enums.cartoons;
import java.util.*;
import java.util.function.*;
enum CartoonCharacter
        implements Supplier<CartoonCharacter> {
    SLAPPY, SPANKY, PUNCHY,
    SILLY, BOUNCY, NUTTY, BOB;
    private Random rand =
            new Random(47);
    @Override
    public CartoonCharacter get() {
        return values()[rand.nextInt(values().length)];
    }
}
public class EnumImplementation {
    public static <T> void printNext(Supplier<T> rg) {
        System.out.print(rg.get() + ", ");
    }
    public static void main(String[] args) {
// Choose any instance:
        CartoonCharacter cc = CartoonCharacter.BOB;
        for(int i = 0; i < 10; i++)
            printNext(cc);
    }
}
```

输出为：

```
BOB, PUNCHY, BOB, SPANKY, NUTTY, PUNCHY, SLAPPY, NUTTY,
NUTTY, SLAPPY,
```

这个结果有点奇怪，不过你必须要有一个 enum 实例才能调用其上的方法。现在，在任何接受 Supplier 参数的方法中，例如 printNext()，都可以使用 CartoonCharacter。

<!-- Random Selection -->

## 随机选择

就像你在 CartoonCharacter.get() 中看到的那样，本章中的很多示例都需要从 enum 实例中进行随机选择。我们可以利用泛型，从而使得这个工作更一般化，并将其加入到我们的工具库中。

```java
// onjava/Enums.java
package onjava;
import java.util.*;
public class Enums {
    private static Random rand = new Random(47);
    
    public static <T extends Enum<T>> T random(Class<T> ec) {
        return random(ec.getEnumConstants());
    }
    
    public static <T> T random(T[] values) {
        return values[rand.nextInt(values.length)];
    }
}
```

古怪的语法\<T extends Enum\<T\>\> 表示 T 是一个 enum 实例。而将 Class\<T\> 作为参数的话，我们就可以利用 Class 对象得到 enum 实例的数组了。重载后的 random() 方法只需使用 T[] 作为参数，因为它并不会调用 Enum 上的任何操作，它只需从数组中随机选择一个元素即可。这样，最终的返回类型正是 enum 的类型。

下面是 random() 方法的一个简单示例：

```java
// enums/RandomTest.java
import onjava.*;
enum Activity { SITTING, LYING, STANDING, HOPPING,
    RUNNING, DODGING, JUMPING, FALLING, FLYING }
    
public class RandomTest {
    public static void main(String[] args) {
        for(int i = 0; i < 20; i++)
            System.out.print(
                    Enums.random(Activity.class) + " ");
    }
}
```

输出为：

```
STANDING FLYING RUNNING STANDING RUNNING STANDING LYING
DODGING SITTING RUNNING HOPPING HOPPING HOPPING RUNNING
STANDING LYING FALLING RUNNING FLYING LYING
```




<!-- Using Interfaces for Organization -->
## 使用接口组织枚举

无法从 enum 继承子类有时很令人沮丧。这种需求有时源自我们希望扩展原 enum 中的元素，有时是因为我们希望使用子类将一个 enum 中的元素进行分组。

在一个接口的内部，创建实现该接口的枚举，以此将元素进行分组，可以达到将枚举元素分类组织的目的。举例来说，假设你想用 enum 来表示不同类别的食物，同时还希望每个 enum 元素仍然保持 Food 类型。那可以这样实现：

```java
// enums/menu/Food.java
// Subcategorization of enums within interfaces
package enums.menu;
public interface Food {
    enum Appetizer implements Food {
        SALAD, SOUP, SPRING_ROLLS;
    }
    enum MainCourse implements Food {
        LASAGNE, BURRITO, PAD_THAI,
        LENTILS, HUMMOUS, VINDALOO;
    }
    enum Dessert implements Food {
        TIRAMISU, GELATO, BLACK_FOREST_CAKE,
        FRUIT, CREME_CARAMEL;
    }
    enum Coffee implements Food {
        BLACK_COFFEE, DECAF_COFFEE, ESPRESSO,
        LATTE, CAPPUCCINO, TEA, HERB_TEA;
    }
}
```




<!-- Using EnumSet Instead of Flags -->
## 使用 EnumSet 替代 Flags


<!-- Using EnumMap -->
## 使用 EnumMap


<!-- Constant-Specific Methods -->
## 常量特定方法


<!-- Multiple Dispatching -->
## 多次调度


<!-- Summary -->
## 本章小结



<!-- 分页 -->

<div style="page-break-after: always;"></div>