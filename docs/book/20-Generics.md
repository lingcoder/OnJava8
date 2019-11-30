[TOC]

<!-- Generics -->

# 第二十章 泛型

> 普通的类和方法只能使用特定的类型：基本数据类型或类类型。如果编写的代码需要应用于多种类型，这种严苛的限制对代码的束缚就会很大。

多态是一种面向对象思想的泛化机制。你可以将方法的参数类型设为基类，这样的方法就可以接受任何派生类作为参数，包括暂时还不存在的类。这样的方法更通用，应用范围更广。在类内部也是如此，在任何使用特定类型的地方，基类意味着更大的灵活性。除了 `final` 类（或只提供私有构造函数的类）任何类型都可被扩展，所以大部分时候这种灵活性是自带的。

拘泥于单一的继承体系太过局限，因为只有继承体系中的对象才能适用基类作为参数的方法中。如果方法以接口而不是类作为参数，限制就宽松多了，只要实现了接口就可以。这给予调用方一种选项，通过调整现有的类来实现接口，满足方法参数要求。接口可以突破继承体系的限制。

即便是接口也还是有诸多限制。一旦指定了接口，它就要求你的代码必须使用特定的接口。而我们希望编写更通用的代码，能够适用“非特定的类型”，而不是一个具体的接口或类。

这就是泛型的概念，是 Java 5 的重大变化之一。泛型实现了*参数化类型*，这样你编写的组件（通常是集合）可以适用于多种类型。“泛型”这个术语的含义是“适用于很多类型”。编程语言中泛型出现的初衷是通过解耦类或方法与所使用的类型之间的约束，使得类或方法具备最宽泛的表达力。随后你会发现 Java 中泛型的实现并没有那么“泛”，你可能会质疑“泛型”这个词是否合适用来描述这一功能。

如果你从未接触过参数化类型机制，你会发现泛型对 Java 语言确实是个很有益的补充。在你实例化一个类型参数时，编译器会负责转型并确保类型的正确性。这是一大进步。

然而，如果你了解其他语言（例如 C++ ）的参数化机制，你会发现，Java 泛型并不能满足所有的预期。使用别人创建好的泛型相对容易，但是创建自己的泛型时，就会遇到很多意料之外的麻烦。

这并不是说 Java 泛型毫无用处。在很多情况下，它可以使代码更直接更优雅。不过，如果你见识过那种实现了更纯粹的泛型的编程语言，那么，Java 可能会令你失望。本章会介绍 Java 泛型的优点与局限。我会解释 Java 的泛型是如何发展成现在这样的，希望能够帮助你更有效地使用这个特性。[^1]

### 与 C++ 的比较

Java 的设计者曾说过，这门语言的灵感主要来自 C++ 。尽管如此，学习 Java 时基本不用参考 C++ 。

但是，Java 中的泛型需要与 C++ 进行对比，理由有两个：首先，理解 C++ *模板*（泛型的主要灵感来源，包括基本语法）的某些特性，有助于理解泛型的基础理念。同时，非常重要的一点是，你可以了解 Java 泛型的局限是什么，以及为什么会有这些局限。最终的目标是明确 Java 泛型的边界，让你成为一个程序高手。只有知道了某个技术不能做什么，你才能更好地做到所能做的（部分原因是，不必浪费时间在死胡同里）。

第二个原因是，在 Java 社区中，大家普遍对 C++ 模板有一种误解，而这种误解可能会令你在理解泛型的意图时产生偏差。

因此，本章中会介绍少量 C++ 模板的例子，仅当它们确实可以加深理解时才会引入。

<!-- Simple Generics -->

## 简单泛型

促成泛型出现的最主要的动机之一是为了创建*集合类*，参见[集合](./12-Collections.md)章节。集合用于存放要使用到的对象。数组也是如此，不过集合比数组更加灵活，功能更丰富。几乎所有程序在运行过程中都会涉及到一组对象，因此集合是可复用性最高的类库之一。

我们先看一个只能持有单个对象的类。这个类可以明确指定其持有的对象的类型：

```java
// generics/Holder1.java

class Automobile {}

public class Holder1 {
  private Automobile a;
  public Holder1(Automobile a) { this.a = a; }
  Automobile get() { return a; }
}
```

这个类的可复用性不高，它无法持有其他类型的对象。我们可不希望为碰到的每个类型都编写一个新的类。

在 Java 5 之前，我们可以让这个类直接持有 `Object` 类型的对象：

```java
// generics/ObjectHolder.java

public class ObjectHolder {
  private Object a;
  public ObjectHolder(Object a) { this.a = a; }
  public void set(Object a) { this.a = a; }
  public Object get() { return a; }
  public static void main(String[] args) {
    ObjectHolder h2 = new ObjectHolder(new Automobile());
    Automobile a = (Automobile)h2.get();
    h2.set("Not an Automobile");
    String s = (String)h2.get();
    h2.set(1); // 自动装箱为 Integer
    Integer x = (Integer)h2.get();
  }
}
```

现在，`ObjectHolder` 可以持有任何类型的对象，在上面的示例中，一个 `ObjectHolder` 先后持有了三种不同类型的对象。

一个集合中存储多种不同类型的对象的情况很少见，通常而言，我们只会用集合存储同一种类型的对象。泛型的主要目的之一就是用来约定集合要存储什么类型的对象，并且通过编译器确保规约得以满足。

因此，与其使用 `Object` ，我们更希望先指定一个类型占位符，稍后再决定具体使用什么类型。要达到这个目的，需要使用*类型参数*，用尖括号括住，放在类名后面。然后在使用这个类时，再用实际的类型替换此类型参数。在下面的例子中，`T` 就是类型参数：

```java
// generics/GenericHolder.java

public class GenericHolder<T> {
  private T a;
  public GenericHolder() {}
  public void set(T a) { this.a = a; }
  public T get() { return a; }
  public static void main(String[] args) {
    GenericHolder<Automobile> h3 = new GenericHolder<Automobile>();
    h3.set(new Automobile()); // 此处有类型校验
    Automobile a = h3.get();  // 无需类型转换
    //- h3.set("Not an Automobile"); // 报错
    //- h3.set(1);  // 报错
  }
}
```

创建 `GenericHolder` 对象时，必须指明要持有的对象的类型，将其置于尖括号内，就像 `main()` 中那样使用。然后，你就只能在 `GenericHolder` 中存储该类型（或其子类，因为多态与泛型不冲突）的对象了。当你调用 `get()` 取值时，直接就是正确的类型。

这就是 Java 泛型的核心概念：你只需告诉编译器要使用什么类型，剩下的细节交给它来处理。

你可能注意到 `h3` 的定义非常繁复。在 `=` 左边有 `GenericHolder<Automobile>`, 右边又重复了一次。在 Java 5 中，这种写法被解释成“必要的”，但在 Java 7 中设计者修正了这个问题（新的简写语法随后成为备受欢迎的特性）。以下是简写的例子：

```java
// generics/Diamond.java

class Bob {}

public class Diamond<T> {
  public static void main(String[] args) {
    GenericHolder<Bob> h3 = new GenericHolder<>();
    h3.set(new Bob());
  }
}
```

注意，在 `h3` 的定义处，`=` 右边的尖括号是空的（称为“钻石语法”），而不是重复左边的类型信息。在本书剩余部分都会使用这种语法。

一般来说，你可以认为泛型和其他类型差不多，只不过它们碰巧有类型参数罢了。在使用泛型时，你只需要指定它们的名称和类型参数列表即可。

### 一个元组类库

有时一个方法需要能返回多个对象。而 **return** 语句只能返回单个对象，解决方法就是创建一个对象，用它打包想要返回的多个对象。当然，可以在每次需要的时候，专门创建一个类来完成这样的工作。但是有了泛型，我们就可以一劳永逸。同时，还获得了编译时的类型安全。

这个概念称为*元组*，它是将一组对象直接打包存储于单一对象中。可以从该对象读取其中的元素，但不允许向其中存储新对象（这个概念也称为 *数据传输对象* 或 *信使* ）。

通常，元组可以具有任意长度，元组中的对象可以是不同类型的。不过，我们希望能够为每个对象指明类型，并且从元组中读取出来时，能够得到正确的类型。要处理不同长度的问题，我们需要创建多个不同的元组。下面是一个可以存储两个对象的元组：

```java
// onjava/Tuple2.java
package onjava;

public class Tuple2<A, B> {
  public final A a1;
  public final B a2;
  public Tuple2(A a, B b) { a1 = a; a2 = b; }
  public String rep() { return a1 + ", " + a2; }
  @Override
  public String toString() {
    return "(" + rep() + ")";
  }
}
```

构造函数传入要存储的对象。这个元组隐式地保持了其中元素的次序。

初次阅读上面的代码时，你可能认为这违反了 Java 编程的封装原则。`a1` 和 `a2` 应该声明为 **private**，然后提供 `getFirst()` 和 `getSecond()` 取值方法才对呀？考虑下这样做能提供的“安全性”是什么：元组的使用程序可以读取 `a1` 和 `a2` 然后对它们执行任何操作，但无法对 `a1` 和 `a2` 重新赋值。例子中的 `final` 可以实现同样的效果，并且更为简洁明了。

另一种设计思路是允许元组的用户给 `a1` 和 `a2` 重新赋值。然而，采用上例中的形式无疑更加安全，如果用户想存储不同的元素，就会强制他们创建新的 `Tuple2` 对象。

我们可以利用继承机制实现长度更长的元组。添加更多的类型参数就行了：

```java
// onjava/Tuple3.java
package onjava;

public class Tuple3<A, B, C> extends Tuple2<A, B> {
  public final C a3;
  public Tuple3(A a, B b, C c) {
    super(a, b);
    a3 = c;
  }
  @Override
  public String rep() {
    return super.rep() + ", " + a3;
  }
}


// onjava/Tuple4.java
package onjava;

public class Tuple4<A, B, C, D>
  extends Tuple3<A, B, C> {
  public final D a4;
  public Tuple4(A a, B b, C c, D d) {
    super(a, b, c);
    a4 = d;
  }
  @Override
  public String rep() {
    return super.rep() + ", " + a4;
  }
}


// onjava/Tuple5.java
package onjava;

public class Tuple5<A, B, C, D, E>
  extends Tuple4<A, B, C, D> {
  public final E a5;
  public Tuple5(A a, B b, C c, D d, E e) {
    super(a, b, c, d);
    a5 = e;
  }
  @Override
  public String rep() {
    return super.rep() + ", " + a5;
  }
}
```

演示需要，再定义两个类：

```java
// generics/Amphibian.java
public class Amphibian {}

// generics/Vehicle.java
public class Vehicle {}
```

使用元组时，你只需要定义一个长度适合的元组，将其作为返回值即可。注意下面例子中方法的返回类型：

```java
// generics/TupleTest.java
import onjava.*;

public class TupleTest {
  static Tuple2<String, Integer> f() {
    // 47 自动装箱为 Integer
    return new Tuple2<>("hi", 47);
  }
  
  static Tuple3<Amphibian, String, Integer> g() {
    return new Tuple3<>(new Amphibian(), "hi", 47);
  }
  
  static Tuple4<Vehicle, Amphibian, String, Integer> h() {
    return new Tuple4<>(new Vehicle(), new Amphibian(), "hi", 47);
  }
  
  static Tuple5<Vehicle, Amphibian, String, Integer, Double> k() {
    return new Tuple5<>(new Vehicle(), new Amphibian(), "hi", 47, 11.1);
  }
  
  public static void main(String[] args) {
    Tuple2<String, Integer> ttsi = f();
    System.out.println(ttsi);
    // ttsi.a1 = "there"; // 编译错误，因为 final 不能重新赋值
    System.out.println(g());
    System.out.println(h());
    System.out.println(k());
  }
}

/* 输出：
 (hi, 47)
 (Amphibian@1540e19d, hi, 47)
 (Vehicle@7f31245a, Amphibian@6d6f6e28, hi, 47)
 (Vehicle@330bedb4, Amphibian@2503dbd3, hi, 47, 11.1)
 */
```

有了泛型，你可以很容易地创建元组，令其返回一组任意类型的对象。

通过 `ttsi.a1 = "there"` 语句的报错，我们可以看出，**final** 声明确实可以确保 **public** 字段在对象被构造出来之后就不能重新赋值了。

在上面的程序中，`new` 表达式有些啰嗦。本章稍后会介绍，如何利用 *泛型方法* 简化它们。

### 一个堆栈类

接下来我们看一个稍微复杂一点的例子：堆栈。在[集合](./12-Collections.md)一章中，我们用 `LinkedList` 实现了 `onjava.Stack` 类。在那个例子中，`LinkedList` 本身已经具备了创建堆栈所需的方法。`Stack` 是通过两个泛型类 `Stack<T>` 和 `LinkedList<T>` 的组合来创建。我们可以看出，泛型只不过是一种类型罢了（稍后我们会看到一些例外的情况）。

这次我们不用 `LinkedList` 来实现自己的内部链式存储机制。

```java
// generics/LinkedStack.java
// 用链式结构实现的堆栈

public class LinkedStack<T> {
  private static class Node<U> {
    U item;
    Node<U> next;
    
    Node() { item = null; next = null; }
    Node(U item, Node<U> next) {
      this.item = item;
      this.next = next;
    }
    
    boolean end() {
      return item == null && next == null;
    }
  }
  
  private Node<T> top = new Node<>();  // 栈顶
  
  public void push(T item) {
    top = new Node<>(item, top);
  }
  
  public T pop() {
    T result = top.item;
    if (!top.end()) {
      top = top.next;
    }
    return result;
  }
  
  public static void main(String[] args) {
    LinkedStack<String> lss = new LinkedStack<>();
    for (String s : "Phasers on stun!".split(" ")) {
      lss.push(s);
    }
    String s;
    while ((s = lss.pop()) != null) {
      System.out.println(s);
    }
  }
}
```

输出结果：

```java
stun!
on
Phasers
```

内部类 `Node` 也是一个泛型，它拥有自己的类型参数。

这个例子使用了一个 *末端标识* (end sentinel) 来判断栈何时为空。这个末端标识是在构造 `LinkedStack` 时创建的。然后，每次调用 `push()` 就会创建一个 `Node<T>` 对象，并将其链接到前一个 `Node<T>` 对象。当你调用 `pop()` 方法时，总是返回 `top.item`，然后丢弃当前 `top` 所指向的 `Node<T>`，并将 `top` 指向下一个 `Node<T>`，除非到达末端标识，这时就不能再移动 `top` 了。如果已经到达末端，程序还继续调用 `pop()` 方法，它只能得到 `null`，说明栈已经空了。

### RandomList

作为容器的另一个例子，假设我们需要一个持有特定类型对象的列表，每次调用它的 `select()` 方法时都随机返回一个元素。如果希望这种列表可以适用于各种类型，就需要使用泛型：

```java
// generics/RandomList.java
import java.util.*;
import java.util.stream.*;

public class RandomList<T> extends ArrayList<T> {
  private Random rand = new Random(47);
  
  public T select() {
    return get(rand.nextInt(size()));
  }
  
  public static void main(String[] args) {
    RandomList<String> rs = new RandomList<>();
    Array.stream("The quick brown fox jumped over the lazy brown dog".split(" ")).forEach(rs::add);
    IntStream.range(0, 11).forEach(i -> 
      System.out.print(rs.select() + " "));
    );
  }
}
```

输出结果：

```java
brown over fox quick quick dog brown The brown lazy brown
```

`RandomList` 继承了 `ArrayList` 的所有方法。本例中只添加了 `select()` 这个方法。

<!-- Generic Interfaces -->

## 泛型接口

泛型也可以应用于接口。例如 *生成器*，这是一种专门负责创建对象的类。实际上，这是 *工厂方法* 设计模式的一种应用。不过，当使用生成器创建新的对象时，它不需要任何参数，而工厂方法一般需要参数。生成器无需额外的信息就知道如何创建新对象。

一般而言，一个生成器只定义一个方法，用于创建对象。例如 `java.util.function` 类库中的 `Supplier` 就是一个生成器，调用其 `get()` 获取对象。`get()` 是泛型方法，返回值为类型参数 `T`。

为了演示 `Supplier`，我们需要定义几个类。下面是个咖啡相关的继承体系：

```java
// generics/coffee/Coffee.java
package generics.coffee;

public class Coffee {
  private static long counter = 0;
  private final long id = counter++;
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + id;
  }
}


// generics/coffee/Latte.java
package generics.coffee;
public class Latte extends Coffee {}


// generics/coffee/Mocha.java
package generics.coffee;
public class Mocha extends Coffee {}


// generics/coffee/Cappuccino.java
package generics.coffee;
public class Cappuccino extends Coffee {}


// generics/coffee/Americano.java
package generics.coffee;
public class Americano extends Coffee {}


// generics/coffee/Breve.java
package generics.coffee;
public class Breve extends Coffee {}
```

现在，我们可以编写一个类，实现 `Supplier<Coffee>` 接口，它能够随机生成不同类型的 `Coffee` 对象：

```java
// generics/coffee/CoffeeSupplier.java
// {java generics.coffee.CoffeeSupplier}
package generics.coffee;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class CoffeeSupplier
implements Supplier<Coffee>, Iterable<Coffee> {
  private Class<?>[] types = { Latte.class, Mocha.class, 
    Cappuccino.class, Americano.class, Breve.class };
  private static Random rand = new Random(47);
  
  public CoffeeSupplier() {}
  // For iteration:
  private int size = 0;
  public CoffeeSupplier(int sz) { size = sz; }
  
  @Override
  public Coffee get() {
    try {
      return (Coffee) types[rand.nextInt(types.length)].newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
  
  class CoffeeIterator implements Iterator<Coffee> {
    int count = size;
    @Override
    public boolean hasNext() { return count > 0; }
    @Override
    public Coffee next() {
      count--;
      return CoffeeSupplier.this.get();
    }
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  @Override
  public Iterator<Coffee> iterator() {
    return new CoffeeIterator();
  }
  
  public static void main(String[] args) {
    Stream.generate(new CoffeeSupplier())
          .limit(5)
          .forEach(System.out::println);
    for (Coffee c : new CoffeeSupplier(5)) {
      System.out.println(c);
    }
  }
}
```

输出结果：

```java
Americano 0
Latte 1
Americano 2
Mocha 3
Mocha 4
Breve 5
Americano 6
Latte 7
Cappuccino 8
Cappuccino 9
```

参数化的 `Supplier` 接口确保 `get()` 返回值是参数的类型。`CoffeeSupplier` 同时还实现了 `Iterable` 接口，所以能用于 *for-in* 语句。不过，它还需要知道何时终止循环，这正是第二个构造函数的作用。

下面是另一个实现 `Supplier<T>` 接口的例子，它负责生成 Fibonacci 数列：

```java
// generics/Fibonacci.java
// Generate a Fibonacci sequence
import java.util.function.*;
import java.util.stream.*;

public class Fibonacci implements Supplier<Integer> {
  private int count = 0;
  @Override
  public Integer get() { return fib(count++); }
  
  private int fib(int n) {
    if(n < 2) return 1;
    return fib(n-2) + fib(n-1);
  }
  
  public static void main(String[] args) {
    Stream.generate(new Fibonacci())
          .limit(18)
          .map(n -> n + " ")
          .forEach(System.out::print);
  }
}
```

输出结果：

```java
1 1 2 3 5 8 13 21 34 55 89 144 233 377 610 987 1597 2584
```

虽然我们在 `Fibonacci` 类的里里外外使用的都是 `int` 类型，但是其参数类型却是 `Integer`。这个例子引出了 Java 泛型的一个局限性：基本类型无法作为类型参数。不过 Java 5 具备自动装箱和拆箱的功能，可以很方便地在基本类型和相应的包装类之间进行转换。通过这个例子中 `Fibonacci` 类对 `int` 的使用，我们已经看到了这种效果。

如果还想更进一步，编写一个实现了 `Iterable` 的 `Fibnoacci` 生成器。我们的一个选择是重写这个类，令其实现 `Iterable` 接口。不过，你并不是总能拥有源代码的控制权，并且，除非必须这么做，否则，我们也不愿意重写一个类。而且我们还有另一种选择，就是创建一个 *适配器* (Adapter) 来实现所需的接口，我们在前面介绍过这个设计模式。

有多种方法可以实现适配器。例如，可以通过继承来创建适配器类：

```java
// generics/IterableFibonacci.java
// Adapt the Fibonacci class to make it Iterable
import java.util.*;

public class IterableFibonacci
extends Fibonacci implements Iterable<Integer> {
  private int n;
  public IterableFibonacci(int count) { n = count; }
  
  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      @Override
      public boolean hasNext() { return n > 0; }
      @Override
      public Integer next() {
        n--;
        return IterableFibonacci.this.get();
      }
      @Override
      public void remove() { // Not implemented
        throw new UnsupportedOperationException();
      }
    };
  }
  
  public static void main(String[] args) {
    for(int i : new IterableFibonacci(18))
      System.out.print(i + " ");
  }
}
```

输出结果：

```java
1 1 2 3 5 8 13 21 34 55 89 144 233 377 610 987 1597 2584
```

在 *for-in* 语句中使用 `IterableFibonacci`，必须在构造函数中提供一个边界值，这样 `hasNext()` 才知道何时返回 **false**，结束循环。

<!-- Generic Methods -->

## 泛型方法

到目前为止，我们已经研究了参数化整个类。其实还可以参数化类中的方法。类本身可能是泛型的，也可能不是，不过这与它的方法是否是泛型的并没有什么关系。

泛型方法独立于类而改变方法。作为准则，请“尽可能”使用泛型方法。通常将单个方法泛型化要比将整个类泛型化更清晰易懂。

如果方法是 **static** 的，则无法访问该类的泛型类型参数，因此，如果使用了泛型类型参数，则它必须是泛型方法。

要定义泛型方法，请将泛型参数列表放置在返回值之前，如下所示：

```java
// generics/GenericMethods.java

public class GenericMethods {
    public <T> void f(T x) {
        System.out.println(x.getClass().getName());
    }

    public static void main(String[] args) {
        GenericMethods gm = new GenericMethods();
        gm.f("");
        gm.f(1);
        gm.f(1.0);
        gm.f(1.0F);
        gm.f('c');
        gm.f(gm);
    }
}
/* Output:
java.lang.String
java.lang.Integer
java.lang.Double
java.lang.Float
java.lang.Character
GenericMethods
*/
```

尽管可以同时对类及其方法进行参数化，但这里未将 **GenericMethods** 类参数化。只有方法 `f()` 具有类型参数，该参数由方法返回类型之前的参数列表指示。

对于泛型类，必须在实例化该类时指定类型参数。使用泛型方法时，通常不需要指定参数类型，因为编译器会找出这些类型。 这称为 *类型参数推断*。因此，对 `f()` 的调用看起来像普通的方法调用，并且 `f()` 看起来像被重载了无数次一样。它甚至会接受 **GenericMethods** 类型的参数。

如果使用基本类型调用 `f()` ，自动装箱就开始起作用，自动将基本类型包装在它们对应的包装类型中。

<!-- Varargs and Generic Methods -->
### 变长参数和泛型方法

泛型方法和变长参数列表可以很好地共存：

```java
// generics/GenericVarargs.java

import java.util.ArrayList;
import java.util.List;

public class GenericVarargs {
    @SafeVarargs
    public static <T> List<T> makeList(T... args) {
        List<T> result = new ArrayList<>();
        for (T item : args)
            result.add(item);
        return result;
    }

    public static void main(String[] args) {
        List<String> ls = makeList("A");
        System.out.println(ls);
        ls = makeList("A", "B", "C");
        System.out.println(ls);
        ls = makeList(
                "ABCDEFFHIJKLMNOPQRSTUVWXYZ".split(""));
        System.out.println(ls);
    }
}
/* Output:
[A]
[A, B, C]
[A, B, C, D, E, F, F, H, I, J, K, L, M, N, O, P, Q, R,
S, T, U, V, W, X, Y, Z]
*/
```

此处显示的 `makeList()` 方法产生的功能与标准库的 `java.util.Arrays.asList()` 方法相同。

`@SafeVarargs` 注解保证我们不会对变长参数列表进行任何修改，这是正确的，因为我们只从中读取。如果没有此注解，编译器将无法知道这些并会发出警告。

<!-- A General-Purpose Supplier -->
### 一个泛型的 Supplier

这是一个为任意具有无参构造方法的类生成 **Supplier** 的类。为了减少键入，它还包括一个用于生成 **BasicSupplier** 的泛型方法：

```java
// onjava/BasicSupplier.java
// Supplier from a class with a no-arg constructor
package onjava;

import java.util.function.Supplier;

public class BasicSupplier<T> implements Supplier<T> {
    private Class<T> type;

    public BasicSupplier(Class<T> type) {
        this.type = type;
    }

    @Override
    public T get() {
        try {
            // Assumes type is a public class:
            return type.newInstance();
        } catch (InstantiationException |
                IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    // Produce a default Supplier from a type token:
    public static <T> Supplier<T> create(Class<T> type) {
        return new BasicSupplier<>(type);
    }
}
```

此类提供了产生以下对象的基本实现：

1. 是 **public** 的。 因为 **BasicSupplier** 在单独的包中，所以相关的类必须具有 **public** 权限，而不仅仅是包级访问权限。

2. 具有无参构造方法。要创建一个这样的 **BasicSupplier** 对象，请调用 `create()` 方法，并将要生成类型的类型令牌传递给它。通用的 `create()` 方法提供了 `BasicSupplier.create(MyType.class)` 这种较简洁的语法来代替较笨拙的 `new BasicSupplier <MyType>(MyType.class)`。

例如，这是一个具有无参构造方法的简单类：

```java
// generics/CountedObject.java

public class CountedObject {
    private static long counter = 0;
    private final long id = counter++;

    public long id() {
        return id;
    }

    @Override
    public String toString() {
        return "CountedObject " + id;
    }
}
```

**CountedObject** 类可以跟踪自身创建了多少个实例，并通过 `toString()` 报告这些实例的数量。 **BasicSupplier** 可以轻松地为 **CountedObject** 创建 **Supplier**：

```java
  // generics/BasicSupplierDemo.java

import onjava.BasicSupplier;

import java.util.stream.Stream;

public class BasicSupplierDemo {
    public static void main(String[] args) {
        Stream.generate(
                BasicSupplier.create(CountedObject.class))
                .limit(5)
                .forEach(System.out::println);
    }
}
/* Output:
CountedObject 0
CountedObject 1
CountedObject 2
CountedObject 3
CountedObject 4
*/
```

泛型方法减少了产生 **Supplier** 对象所需的代码量。 Java 泛型强制传递 **Class** 对象，以便在 `create()` 方法中将其用于类型推断。

<!-- Simplifying Tuple Use -->
### 简化元组的使用

使用类型参数推断和静态导入，我们将把早期的元组重写为更通用的库。在这里，我们使用重载的静态方法创建元组：

```java
// onjava/Tuple.java
// Tuple library using type argument inference
package onjava;

public class Tuple {
    public static <A, B> Tuple2<A, B> tuple(A a, B b) {
        return new Tuple2<>(a, b);
    }

    public static <A, B, C> Tuple3<A, B, C>
    tuple(A a, B b, C c) {
        return new Tuple3<>(a, b, c);
    }

    public static <A, B, C, D> Tuple4<A, B, C, D>
    tuple(A a, B b, C c, D d) {
        return new Tuple4<>(a, b, c, d);
    }

    public static <A, B, C, D, E>
    Tuple5<A, B, C, D, E> tuple(A a, B b, C c, D d, E e) {
        return new Tuple5<>(a, b, c, d, e);
    }
}
```

我们修改 **TupleTest.java** 来测试 **Tuple.java** :

```java
// generics/TupleTest2.java

import onjava.Tuple2;
import onjava.Tuple3;
import onjava.Tuple4;
import onjava.Tuple5;

import static onjava.Tuple.tuple;

public class TupleTest2 {
    static Tuple2<String, Integer> f() {
        return tuple("hi", 47);
    }

    static Tuple2 f2() {
        return tuple("hi", 47);
    }

    static Tuple3<Amphibian, String, Integer> g() {
        return tuple(new Amphibian(), "hi", 47);
    }

    static Tuple4<Vehicle, Amphibian, String, Integer> h() {
        return tuple(
                new Vehicle(), new Amphibian(), "hi", 47);
    }

    static Tuple5<Vehicle, Amphibian,
            String, Integer, Double> k() {
        return tuple(new Vehicle(), new Amphibian(),
                "hi", 47, 11.1);
    }

    public static void main(String[] args) {
        Tuple2<String, Integer> ttsi = f();
        System.out.println(ttsi);
        System.out.println(f2());
        System.out.println(g());
        System.out.println(h());
        System.out.println(k());
    }
}
/* Output:
(hi, 47)
(hi, 47)
(Amphibian@14ae5a5, hi, 47)
(Vehicle@135fbaa4, Amphibian@45ee12a7, hi, 47)
(Vehicle@4b67cf4d, Amphibian@7ea987ac, hi, 47, 11.1)
*/
```

请注意，`f()` 返回一个参数化的 **Tuple2** 对象，而 `f2()` 返回一个未参数化的 **Tuple2** 对象。编译器不会在这里警告 `f2()` ，因为返回值未以参数化方式使用。从某种意义上说，它被“向上转型”为一个未参数化的 **Tuple2** 。 但是，如果尝试将 `f2()` 的结果放入到参数化的 **Tuple2** 中，则编译器将发出警告。

<!-- A Set Utility -->
### 一个 Set 工具

对于泛型方法的另一个示例，请考虑由 **Set** 表示的数学关系。这些被方便地定义为可用于所有不同类型的泛型方法：

```java
// onjava/Sets.java

package onjava;

import java.util.HashSet;
import java.util.Set;

public class Sets {
    public static <T> Set<T> union(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }

    public static <T>
    Set<T> intersection(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.retainAll(b);
        return result;
    }

    // Subtract subset from superset:
    public static <T> Set<T>
    difference(Set<T> superset, Set<T> subset) {
        Set<T> result = new HashSet<>(superset);
        result.removeAll(subset);
        return result;
    }

    // Reflexive--everything not in the intersection:
    public static <T> Set<T> complement(Set<T> a, Set<T> b) {
        return difference(union(a, b), intersection(a, b));
    }
}
```

前三个方法通过将第一个参数的引用复制到新的 **HashSet** 对象中来复制第一个参数，因此不会直接修改参数集合。因此，返回值是一个新的 **Set** 对象。

这四种方法代表数学集合操作： `union()` 返回一个包含两个参数并集的 **Set** ， `intersection()` 返回一个包含两个参数集合交集的 **Set** ， `difference()` 从 **superset** 中减去 **subset** 的元素 ，而 `complement()` 返回所有不在交集中的元素的 **Set**。作为显示这些方法效果的简单示例的一部分，下面是一个包含不同水彩名称的 **enum** ：

```java
// generics/watercolors/Watercolors.java

package watercolors;

public enum Watercolors {
    ZINC, LEMON_YELLOW, MEDIUM_YELLOW, DEEP_YELLOW,
    ORANGE, BRILLIANT_RED, CRIMSON, MAGENTA,
    ROSE_MADDER, VIOLET, CERULEAN_BLUE_HUE,
    PHTHALO_BLUE, ULTRAMARINE, COBALT_BLUE_HUE,
    PERMANENT_GREEN, VIRIDIAN_HUE, SAP_GREEN,
    YELLOW_OCHRE, BURNT_SIENNA, RAW_UMBER,
    BURNT_UMBER, PAYNES_GRAY, IVORY_BLACK
}
```

为了方便起见（不必全限定所有名称），将其静态导入到以下示例中。本示例使用 **EnumSet** 轻松从 **enum** 中创建 **Set** 。（可以在[第二十二章 枚举](./22-Enumerations.md)一章中了解有关 **EnumSet** 的更多信息。）在这里，静态方法 `EnumSet.range()` 要求提供所要在结果 **Set** 中创建的元素范围的第一个和最后一个元素：

```java
// generics/WatercolorSets.java

import watercolors.*;

import java.util.EnumSet;
import java.util.Set;

import static watercolors.Watercolors.*;
import static onjava.Sets.*;

public class WatercolorSets {
    public static void main(String[] args) {
        Set<Watercolors> set1 =
                EnumSet.range(BRILLIANT_RED, VIRIDIAN_HUE);
        Set<Watercolors> set2 =
                EnumSet.range(CERULEAN_BLUE_HUE, BURNT_UMBER);
        System.out.println("set1: " + set1);
        System.out.println("set2: " + set2);
        System.out.println(
                "union(set1, set2): " + union(set1, set2));
        Set<Watercolors> subset = intersection(set1, set2);
        System.out.println(
                "intersection(set1, set2): " + subset);
        System.out.println("difference(set1, subset): " +
                difference(set1, subset));
        System.out.println("difference(set2, subset): " +
                difference(set2, subset));
        System.out.println("complement(set1, set2): " +
                complement(set1, set2));
    }
}
/* Output:
set1: [BRILLIANT_RED, CRIMSON, MAGENTA, ROSE_MADDER,
VIOLET, CERULEAN_BLUE_HUE, PHTHALO_BLUE, ULTRAMARINE,
COBALT_BLUE_HUE, PERMANENT_GREEN, VIRIDIAN_HUE]
set2: [CERULEAN_BLUE_HUE, PHTHALO_BLUE, ULTRAMARINE,
COBALT_BLUE_HUE, PERMANENT_GREEN, VIRIDIAN_HUE,
SAP_GREEN, YELLOW_OCHRE, BURNT_SIENNA, RAW_UMBER,
BURNT_UMBER]
union(set1, set2): [BURNT_SIENNA, BRILLIANT_RED,
YELLOW_OCHRE, MAGENTA, SAP_GREEN, CERULEAN_BLUE_HUE,
ULTRAMARINE, VIRIDIAN_HUE, VIOLET, RAW_UMBER,
ROSE_MADDER, PERMANENT_GREEN, BURNT_UMBER,
PHTHALO_BLUE, CRIMSON, COBALT_BLUE_HUE]
intersection(set1, set2): [PERMANENT_GREEN,
CERULEAN_BLUE_HUE, ULTRAMARINE, VIRIDIAN_HUE,
PHTHALO_BLUE, COBALT_BLUE_HUE]
difference(set1, subset): [BRILLIANT_RED, MAGENTA,
VIOLET, CRIMSON, ROSE_MADDER]
difference(set2, subset): [BURNT_SIENNA, YELLOW_OCHRE,
BURNT_UMBER, SAP_GREEN, RAW_UMBER]
complement(set1, set2): [BURNT_SIENNA, BRILLIANT_RED,
YELLOW_OCHRE, MAGENTA, SAP_GREEN, VIOLET, RAW_UMBER,
ROSE_MADDER, BURNT_UMBER, CRIMSON]
*/
```

接下来的例子使用 `Sets.difference()` 方法来展示 **java.util** 包中各种 **Collection** 和 **Map** 类之间的方法差异：

```java
// onjava/CollectionMethodDifferences.java
// {java onjava.CollectionMethodDifferences}

package onjava;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionMethodDifferences {
    static Set<String> methodSet(Class<?> type) {
        return Arrays.stream(type.getMethods())
                .map(Method::getName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    static void interfaces(Class<?> type) {
        System.out.print("Interfaces in " +
                type.getSimpleName() + ": ");
        System.out.println(
                Arrays.stream(type.getInterfaces())
                        .map(Class::getSimpleName)
                        .collect(Collectors.toList()));
    }

    static Set<String> object = methodSet(Object.class);

    static {
        object.add("clone");
    }

    static void
    difference(Class<?> superset, Class<?> subset) {
        System.out.print(superset.getSimpleName() +
                " extends " + subset.getSimpleName() +
                ", adds: ");
        Set<String> comp = Sets.difference(
                methodSet(superset), methodSet(subset));
        comp.removeAll(object); // Ignore 'Object' methods
        System.out.println(comp);
        interfaces(superset);
    }

    public static void main(String[] args) {
        System.out.println("Collection: " +
                methodSet(Collection.class));
        interfaces(Collection.class);
        difference(Set.class, Collection.class);
        difference(HashSet.class, Set.class);
        difference(LinkedHashSet.class, HashSet.class);
        difference(TreeSet.class, Set.class);
        difference(List.class, Collection.class);
        difference(ArrayList.class, List.class);
        difference(LinkedList.class, List.class);
        difference(Queue.class, Collection.class);
        difference(PriorityQueue.class, Queue.class);
        System.out.println("Map: " + methodSet(Map.class));
        difference(HashMap.class, Map.class);
        difference(LinkedHashMap.class, HashMap.class);
        difference(SortedMap.class, Map.class);
        difference(TreeMap.class, Map.class);
    }
}
/* Output:
Collection: [add, addAll, clear, contains, containsAll,
equals, forEach, hashCode, isEmpty, iterator,
parallelStream, remove, removeAll, removeIf, retainAll,
size, spliterator, stream, toArray]
Interfaces in Collection: [Iterable]
Set extends Collection, adds: []
Interfaces in Set: [Collection]
HashSet extends Set, adds: []
Interfaces in HashSet: [Set, Cloneable, Serializable]
LinkedHashSet extends HashSet, adds: []
Interfaces in LinkedHashSet: [Set, Cloneable,
Serializable]
TreeSet extends Set, adds: [headSet,
descendingIterator, descendingSet, pollLast, subSet,
floor, tailSet, ceiling, last, lower, comparator,
pollFirst, first, higher]
Interfaces in TreeSet: [NavigableSet, Cloneable,
Serializable]
List extends Collection, adds: [replaceAll, get,
indexOf, subList, set, sort, lastIndexOf, listIterator]
Interfaces in List: [Collection]
ArrayList extends List, adds: [trimToSize,
ensureCapacity]
Interfaces in ArrayList: [List, RandomAccess,
Cloneable, Serializable]
LinkedList extends List, adds: [offerFirst, poll,
getLast, offer, getFirst, removeFirst, element,
removeLastOccurrence, peekFirst, peekLast, push,
pollFirst, removeFirstOccurrence, descendingIterator,
pollLast, removeLast, pop, addLast, peek, offerLast,
addFirst]
Interfaces in LinkedList: [List, Deque, Cloneable,
Serializable]
Queue extends Collection, adds: [poll, peek, offer,
element]
Interfaces in Queue: [Collection]
PriorityQueue extends Queue, adds: [comparator]
Interfaces in PriorityQueue: [Serializable]
Map: [clear, compute, computeIfAbsent,
computeIfPresent, containsKey, containsValue, entrySet,
equals, forEach, get, getOrDefault, hashCode, isEmpty,
keySet, merge, put, putAll, putIfAbsent, remove,
replace, replaceAll, size, values]
HashMap extends Map, adds: []
Interfaces in HashMap: [Map, Cloneable, Serializable]
LinkedHashMap extends HashMap, adds: []
Interfaces in LinkedHashMap: [Map]
SortedMap extends Map, adds: [lastKey, subMap,
comparator, firstKey, headMap, tailMap]
Interfaces in SortedMap: [Map]
TreeMap extends Map, adds: [descendingKeySet,
navigableKeySet, higherEntry, higherKey, floorKey,
subMap, ceilingKey, pollLastEntry, firstKey, lowerKey,
headMap, tailMap, lowerEntry, ceilingEntry,
descendingMap, pollFirstEntry, lastKey, firstEntry,
floorEntry, comparator, lastEntry]
Interfaces in TreeMap: [NavigableMap, Cloneable,
Serializable]
*/
```

在[第十二章 集合](./12-Collections.md)的[本章小结](./12-Collections.md#本章小结)部分将会用到这里的输出结果。

<!-- Building Complex Models -->

## 构建复杂模型

泛型的一个重要好处是能够简单安全地创建复杂模型。例如，我们可以轻松地创建一个元组列表：

```java
// generics/TupleList.java
// Combining generic types to make complex generic types

import onjava.Tuple4;

import java.util.ArrayList;

public class TupleList<A, B, C, D>
        extends ArrayList<Tuple4<A, B, C, D>> {
    public static void main(String[] args) {
        TupleList<Vehicle, Amphibian, String, Integer> tl =
                new TupleList<>();
        tl.add(TupleTest2.h());
        tl.add(TupleTest2.h());
        tl.forEach(System.out::println);
    }
}
/* Output:
(Vehicle@7cca494b, Amphibian@7ba4f24f, hi, 47)
(Vehicle@3b9a45b3, Amphibian@7699a589, hi, 47)
*/
```

这将产生一个功能强大的数据结构，而无需太多代码。

下面是第二个例子。每个类都是组成块，总体包含很多个块。在这里，该模型是一个具有过道，货架和产品的零售商店：

```java
// generics/Store.java
// Building a complex model using generic collections

import onjava.Suppliers;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

class Product {
    private final int id;
    private String description;
    private double price;

    Product(int idNumber, String descr, double price) {
        id = idNumber;
        description = descr;
        this.price = price;
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return id + ": " + description +
                ", price: $" + price;
    }

    public void priceChange(double change) {
        price += change;
    }

    public static Supplier<Product> generator =
            new Supplier<Product>() {
                private Random rand = new Random(47);

                @Override
                public Product get() {
                    return new Product(rand.nextInt(1000), "Test",
                            Math.round(
                                    rand.nextDouble() * 1000.0) + 0.99);
                }
            };
}

class Shelf extends ArrayList<Product> {
    Shelf(int nProducts) {
        Suppliers.fill(this, Product.generator, nProducts);
    }
}

class Aisle extends ArrayList<Shelf> {
    Aisle(int nShelves, int nProducts) {
        for (int i = 0; i < nShelves; i++)
            add(new Shelf(nProducts));
    }
}

class CheckoutStand {
}

class Office {
}

public class Store extends ArrayList<Aisle> {
    private ArrayList<CheckoutStand> checkouts =
            new ArrayList<>();
    private Office office = new Office();

    public Store(
            int nAisles, int nShelves, int nProducts) {
        for (int i = 0; i < nAisles; i++)
            add(new Aisle(nShelves, nProducts));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Aisle a : this)
            for (Shelf s : a)
                for (Product p : s) {
                    result.append(p);
                    result.append("\n");
                }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(new Store(5, 4, 3));
    }
}
/* Output: (First 8 Lines)
258: Test, price: $400.99
861: Test, price: $160.99
868: Test, price: $417.99
207: Test, price: $268.99
551: Test, price: $114.99
278: Test, price: $804.99
520: Test, price: $554.99
140: Test, price: $530.99
                  ...
*/
```

`Store.toString()` 显示了结果：尽管有复杂的层次结构，但多层的集合仍然是类型安全的和可管理的。令人印象深刻的是，组装这样的模型在理论上并不是禁止的。

**Shelf** 使用 `Suppliers.fill()` 这个实用程序，该实用程序接受 **Collection** （第一个参数），并使用 **Supplier** （第二个参数），以元素的数量为 **n** （第三个参数）来填充它。 **Suppliers** 类将会在本章末尾定义，其中的方法都是在执行某种填充操作，并在本章的其他示例中使用。

<!-- The Mystery of Erasure -->
## 泛型擦除

当你开始更深入地钻研泛型时，会发现有大量的东西初看起来是没有意义的。例如，尽管可以说  `ArrayList.class`，但不能说成 `ArrayList<Integer>.class`。考虑下面的情况：

```java
// generics/ErasedTypeEquivalence.java

import java.util.*;

public class ErasedTypeEquivalence {
    
    public static void main(String[] args) {
        Class c1 = new ArrayList<String>().getClass();
        Class c2 = new ArrayList<Integer>().getClass();
        System.out.println(c1 == c2);
    }
    
}
/* Output:
true
*/
```

`ArrayList<String>` 和 `ArrayList<Integer>` 应该是不同的类型。不同的类型会有不同的行为。例如，如果尝试向 `ArrayList<String>` 中放入一个 `Integer`，所得到的行为（失败）和向 `ArrayList<Integer>` 中放入一个 `Integer` 所得到的行为（成功）完全不同。然而上面的程序认为它们是相同的类型。

下面的例子是对该谜题的补充：

```java
// generics/LostInformation.java

import java.util.*;

class Frob {}
class Fnorkle {}
class Quark<Q> {}

class Particle<POSITION, MOMENTUM> {}

public class LostInformation {

    public static void main(String[] args) {
        List<Frob> list = new ArrayList<>();
        Map<Frob, Fnorkle> map = new HashMap<>();
        Quark<Fnorkle> quark = new Quark<>();
        Particle<Long, Double> p = new Particle<>();
        System.out.println(Arrays.toString(list.getClass().getTypeParameters()));
        System.out.println(Arrays.toString(map.getClass().getTypeParameters()));
        System.out.println(Arrays.toString(quark.getClass().getTypeParameters()));
        System.out.println(Arrays.toString(p.getClass().getTypeParameters()));
    }

}
/* Output:
[E]
[K,V]
[Q]
[POSITION,MOMENTUM]
```

根据 JDK 文档，**Class.getTypeParameters()** “返回一个 **TypeVariable** 对象数组，表示泛型声明中声明的类型参数...” 这暗示你可以发现这些参数类型。但是正如上例中输出所示，你只能看到用作参数占位符的标识符，这并非有用的信息。

残酷的现实是：

在泛型代码内部，无法获取任何有关泛型参数类型的信息。

因此，你可以知道如类型参数标识符和泛型边界这些信息，但无法得知实际的类型参数从而用来创建特定的实例。如果你曾是 C++ 程序员，那么这个事实会让你很沮丧，在使用 Java 泛型工作时，它是必须处理的最基本的问题。

Java 泛型是使用擦除实现的。这意味着当你在使用泛型时，任何具体的类型信息都被擦除了，你唯一知道的就是你在使用一个对象。因此，`List<String>` 和 `List<Integer>` 在运行时实际上是相同的类型。它们都被擦除成原生类型 `List`。

理解擦除并知道如何处理它，是你在学习 Java 泛型时面临的最大障碍之一。这也是本节将要探讨的内容。

### C++ 的方式

下面是使用模版的 C++ 示例。你会看到类型参数的语法十分相似，因为 Java 是受 C++ 启发的：

```c++
// generics/Templates.cpp

#include <iostream>
using namespace std;

template<class T> class Manipulator {
    T obj;
public:
    Manipulator(T x) { obj = x; }
    void manipulate() { obj.f(); }
};

class HasF {
public:
    void f() { cout << "HasF::f()" << endl; }
};

int main() {
    HasF hf;
    Manipulator<HasF> manipulator(hf);
    manipulator.manipulate();
}
/* Output:
HasF::f()
*/
```

**Manipulator** 类存储了一个 **T** 类型的对象。`manipulate()` 方法会调用 **obj** 上的 `f()` 方法。它是如何知道类型参数 **T** 中存在 `f()` 方法的呢？C++ 编译器会在你实例化模版时进行检查，所以在 `Manipulator<HasF>` 实例化的那一刻，它看到 **HasF** 中含有一个方法 `f()`。如果情况并非如此，你就会得到一个编译期错误，保持类型安全。

用 C++ 编写这种代码很简单，因为当模版被实例化时，模版代码就知道模版参数的类型。Java 泛型就不同了。下面是 **HasF** 的 Java 版本：

```java
// generics/HasF.java

public class HasF {
    public void f() {
        System.out.println("HasF.f()");
    }
}
```

如果我们将示例的其余代码用 Java 实现，就不会通过编译：

```java
// generics/Manipulation.java
// {WillNotCompile}

class Manipulator<T> {
    private T obj;
    
    Manipulator(T x) {
        obj = x;
    }
    
    // Error: cannot find symbol: method f():
    public void manipulate() {
        obj.f();
    }
}

public class Manipulation {
	public static void main(String[] args) {
        HasF hf = new HasF();
        Manipulator<HasF> manipulator = new Manipulator<>(hf);
        manipulator.manipulate();
    }
}
```

因为擦除，Java 编译器无法将 `manipulate()` 方法必须能调用 **obj** 的 `f()` 方法这一需求映射到 HasF 具有 `f()` 方法这个事实上。为了调用 `f()`，我们必须协助泛型类，给定泛型类一个边界，以此告诉编译器只能接受遵循这个边界的类型。这里重用了 **extends** 关键字。由于有了边界，下面的代码就能通过编译：

```java
public class Manipulator2<T extends HasF> {
    private T obj;

    Manipulator2(T x) {
        obj = x;
    }

    public void manipulate() {
        obj.f();
    }
}
```

边界 `<T extends HasF>` 声明 T 必须是 HasF 类型或其子类。如果情况确实如此，就可以安全地在 **obj** 上调用 `f()` 方法。

我们说泛型类型参数会擦除到它的第一个边界（可能有多个边界，稍后你将看到）。我们还提到了类型参数的擦除。编译器实际上会把类型参数替换为它的擦除，就像上面的示例，**T** 擦除到了 **HasF**，就像在类的声明中用 **HasF** 替换了 **T** 一样。

你可能正确地观察到了泛型在 **Manipulator2.java** 中没有贡献任何事。你可以很轻松地自己去执行擦除，生成没有泛型的类：

```java
// generics/Manipulator3.java

class Manipulator3 {
    private HasF obj;
    
    Manipulator3(HasF x) {
        ojb = x;
    }
    
    public void manipulate() {
        obj.f();
    }
}
```

这提出了很重要的一点：泛型只有在类型参数比某个具体类型（以及其子类）更加“泛化”——代码能跨多个类工作时才有用。因此，类型参数和它们在有用的泛型代码中的应用，通常比简单的类替换更加复杂。但是，不能因此认为使用 `<T extends HasF>` 形式就是有缺陷的。例如，如果某个类有一个返回 **T** 的方法，那么泛型就有所帮助，因为它们之后将返回确切的类型：

```java
// generics/ReturnGenericType.java

public class ReturnGenericType<T extends HasF> {
    private T obj;
    
    ReturnGenericType(T x) {
        obj = x;
    }
    
    public T get() {
        return obj;
    }
}
```

你必须查看所有的代码，从而确定代码是否复杂到必须使用泛型的程度。

我们将在本章稍后看到有关边界的更多细节。

### 迁移兼容性

为了减少潜在的关于擦除的困惑，你必须清楚地认识到这不是一个语言特性。它是 Java 实现泛型的一种妥协，因为泛型不是 Java 语言出现时就有的，所以就有了这种妥协。它会使你痛苦，因此你需要尽早习惯它并了解为什么它会这样。

如果 Java 1.0 就含有泛型的话，那么这个特性就不会使用擦除来实现——它会使用具体化，保持参数类型为第一类实体，因此你就能在类型参数上执行基于类型的语言操作和反射操作。本章稍后你会看到，擦除减少了泛型的泛化性。泛型在 Java 中仍然是有用的，只是不如它们本来设想的那么有用，而原因就是擦除。

在基于擦除的实现中，泛型类型被当作第二类类型处理，即不能在某些重要的上下文使用泛型类型。泛型类型只有在静态类型检测期间才出现，在此之后，程序中的所有泛型类型都将被擦除，替换为它们的非泛型上界。例如， `List<T>` 这样的类型注解会被擦除为 **List**，普通的类型变量在未指定边界的情况下会被擦除为 **Object**。

擦除的核心动机是你可以在泛化的客户端上使用非泛型的类库，反之亦然。这经常被称为“迁移兼容性”。在理想情况下，所有事物将在指定的某天被泛化。在现实中，即使程序员只编写泛型代码，他们也必须处理 Java 5 之前编写的非泛型类库。这些类库的作者可能从没想过要泛化他们的代码，或许他们可能刚刚开始接触泛型。

因此 Java 泛型不仅必须支持向后兼容性——现有的代码和类文件仍然合法，继续保持之前的含义——而且还必须支持迁移兼容性，使得类库能按照它们自己的步调变为泛型，当某个类库变为泛型时，不会破坏依赖于它的代码和应用。在确定了这个目标后，Java 设计者们和从事此问题相关工作的各个团队决策认为擦除是唯一可行的解决方案。擦除使得这种向泛型的迁移成为可能，允许非泛型的代码和泛型代码共存。

例如，假设一个应用使用了两个类库 **X** 和 **Y**，**Y** 使用了类库 **Z**。随着 Java 5 的出现，这个应用和这些类库的创建者最终可能希望迁移到泛型上。但是当进行迁移时，它们有着不同的动机和限制。为了实现迁移兼容性，每个类库与应用必须与其他所有的部分是否使用泛型无关。因此，它们不能探测其他类库是否使用了泛型。因此，某个特定的类库使用了泛型这样的证据必须被”擦除“。

如果没有某种类型的迁移途径，所有已经构建了很长时间的类库就需要与希望迁移到 Java 泛型上的开发者们说再见了。类库毫无争议是编程语言的一部分，对生产效率有着极大的影响，所以这种代码无法接受。擦除是否是最佳的活唯一的迁移途径，还待时间来证明。

### 擦除的问题

因此，擦除主要的正当理由是从非泛化代码到泛化代码的转变过程，以及在不破坏现有类库的情况下将泛型融入到语言中。擦除允许你继续使用现有的非泛型客户端代码，直至客户端准备好用泛型重写这些代码。这是一个崇高的动机，因为它不会骤然破坏所有现有的代码。

擦除的代码是显著的。泛型不能用于显式地引用运行时类型的操作中，例如转型、**instanceof** 操作和 **new** 表达式。因为所有关于参数的类型信息都丢失了，当你在编写泛型代码时，必须时刻提醒自己，你只是看起来拥有有关参数的类型信息而已。

考虑如下的代码段：

```java
class Foo<T> {
    T var;
}
```

看上去当你创建一个 **Foo** 实例时：

```java
Foo<Cat> f = new Foo<>();
```

**class** **Foo** 中的代码应该知道现在工作于 **Cat** 之上。泛型语法也在强烈暗示整个类中所有 T 出现的地方都被替换，就像在 C++ 中一样。但是事实并非如此，当你在编写这个类的代码时，必须提醒自己：“不，这只是一个 **Object**“。

另外，擦除和迁移兼容性意味着，使用泛型并不是强制的，尽管你可能希望这样：

```java
// generics/ErasureAndInheritance.java

class GenericBase<T> {
    private T element;
    
    public void set(T arg) {
        element = arg;
    }
    
    public T get() {
        return element;
    }
}

class Derived1<T> extends GenericBase<T> {}

class Derived2 extends GenericBase {} // No warning

// class Derived3 extends GenericBase<?> {}
// Strange error:
// unexpected type
// required: class or interface without bounds
public class ErasureAndInteritance {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Derived2 d2 = new Derived2();
        Object obj = d2.get();
        d2.set(obj); // Warning here!
    }
}
```

**Derived2** 继承自 **GenericBase**，但是没有任何类型参数，编译器没有发出任何警告。直到调用 `set()` 方法时才出现警告。

为了关闭警告，Java 提供了一个注解，我们可以在列表中看到它：

```java
@SuppressWarnings("unchecked")
```

这个注解放置在产生警告的方法上，而不是整个类上。当你要关闭警告时，最好尽可能地“聚焦”，这样就不会因为过于宽泛地关闭警告，而导致意外地遮蔽掉真正的问题。

可以推断，**Derived3** 产生的错误意味着编译器期望得到一个原生基类。

当你希望将类型参数不仅仅当作 Object 处理时，就需要付出额外努力来管理边界，并且与在 C++、Ada 和 Eiffel 这样的语言中获得参数化类型相比，你需要付出多得多的努力来获得少得多的回报。这并不是说，对于大多数的编程问题而言，这些语言通常都会比 Java 更得心应手，只是说它们的参数化类型机制相比 Java 更灵活、更强大。

### 边界处的动作

因为擦除，我发现了泛型最令人困惑的方面是可以表示没有任何意义的事物。例如：

```java
// generics/ArrayMaker.java

import java.lang.reflect.*;
import java.util.*;

public class ArrayMaker<T> {
    private Class<T> kind;

    public ArrayMaker(Class<T> kind) {
        this.kind = kind;
    }

    @SuppressWarnings("unchecked")
    T[] create(int size) {
        return (T[]) Array.newInstance(kind, size);
    }

    public static void main(String[] args) {
        ArrayMaker<String> stringMaker = new ArrayMaker<>(String.class);
        String[] stringArray = stringMaker.create(9);
        System.out.println(Arrays.toString(stringArray));
    }
}
/* Output
[null,null,null,null,null,null,null,null,null]
*/
```

即使 **kind** 被存储为 `Class<T>`，擦除也意味着它实际被存储为没有任何参数的 **Class**。因此，当你在使用它时，例如创建数组，`Array.newInstance()` 实际上并未拥有 **kind** 所蕴含的类型信息。所以它不会产生具体的结果，因而必须转型，这会产生一条令你无法满意的警告。

注意，对于在泛型中创建数组，使用 `Array.newInstance()` 是推荐的方式。

如果我们创建一个集合而不是数组，情况就不同了：

```java
// generics/ListMaker.java

import java.util.*;

public class ListMaker<T> {
    List<T> create() {
        return new ArrayList<>();
    }
    
    public static void main(String[] args) {
        ListMaker<String> stringMaker = new ListMaker<>();
        List<String> stringList = stringMaker.create();
    }
}
```

编译器不会给出任何警告，尽管我们知道（从擦除中）在 `create()` 内部的 `new ArrayList<>()` 中的 `<T>` 被移除了——在运行时，类内部没有任何 `<T>`，因此这看起来毫无意义。但是如果你遵从这种思路，并将这个表达式改为 `new ArrayList()`，编译器就会发出警告。

本例中这么做真的毫无意义吗？如果在创建 **List** 的同时向其中放入一些对象呢，像这样：

```java
// generics/FilledList.java

import java.util.*;
import java.util.function.*;
import onjava.*;

public class FilledList<T> extends ArrayList<T> {
    FilledList<Supplier<T> gen, int size) {
        Suppliers.fill(this, gen, size);
    }
    
    public FilledList(T t, int size) {
        for (int i = 0; i < size; i++) {
            this.add(t);
        }
    }
    
    public static void main(String[] args) {
        List<String> list = new FilledList<>("Hello", 4);
        System.out.println(list);
        // Supplier version:
        List<Integer> ilist = new FilledList<>(() -> 47, 4);
        System.out.println(ilist);
    }
}
/* Output:
[Hello,Hello,Hello,Hello]
[47,47,47,47]
```

即使编译器无法得知 `add()` 中的 **T** 的任何信息，但它仍可以在编译期确保你放入 **FilledList** 中的对象是 **T** 类型。因此，即使擦除移除了方法或类中的实际类型的信息，编译器仍可以确保方法或类中使用的类型的内部一致性。

因为擦除移除了方法体中的类型信息，所以在运行时的问题就是*边界*：即对象进入和离开方法的地点。这些正是编译器在编译期执行类型检查并插入转型代码的地点。

考虑如下这段非泛型示例：

```java
// generics/SimpleHolder.java

public class SimpleHolder {
    private Object obj;
    
    public void set(Object obj) {
        this.obj = obj;
    }
    
    public Object get() {
        return obj;
    }
    
    public static void main(String[] args) {
        SimpleHolder holder = new SimpleHolder();
        holder.set("Item");
        String s = (String) holder.get();
    }
}
```

如果用 **javap -c SimpleHolder** 反编译这个类，会得到如下内容（经过编辑）：

```java
public void set(java.lang.Object);
   0: aload_0
   1: aload_1
   2: putfield #2; // Field obj:Object;
   5: return
    
public java.lang.Object get();
   0: aload_0
   1: getfield #2; // Field obj:Object;
   4: areturn
    
public static void main(java.lang.String[]);
   0: new #3; // class SimpleHolder
   3: dup
   4: invokespecial #4; // Method "<init>":()V
   7: astore_1
   8: aload_1
   9: ldc #5; // String Item
   11: invokevirtual #6; // Method set:(Object;)V
   14: aload_1
   15: invokevirtual #7; // Method get:()Object;
   18: checkcast #8; // class java/lang/String
   21: astore_2
   22: return
```

`set()` 和 `get()` 方法存储和产生值，转型在调用 `get()` 时接受检查。

现在将泛型融入上例代码中：

```java
// generics/GenericHolder2.java

public class GenericHolder2<T> {
    private T obj;

    public void set(T obj) {
        this.obj = obj;
    }

    public T get() {
        return obj;
    }

    public static void main(String[] args) {
        GenericHolder2<String> holder =  new GenericHolder2<>();
        holder.set("Item");
        String s = holder.get();
    }
}
```

从 `get()` 返回后的转型消失了，但是我们还知道传递给 `set()` 的值在编译期会被检查。下面是相关的字节码：

```java
public void set(java.lang.Object);
   0: aload_0
   1: aload_1
   2: putfield #2; // Field obj:Object;
   5: return
       
public java.lang.Object get();
   0: aload_0
   1: getfield #2; // Field obj:Object;
   4: areturn
       
public static void main(java.lang.String[]);
   0: new #3; // class GenericHolder2
   3: dup
   4: invokespecial #4; // Method "<init>":()V
   7: astore_1
   8: aload_1
   9: ldc #5; // String Item
   11: invokevirtual #6; // Method set:(Object;)V
   14: aload_1
   15: invokevirtual #7; // Method get:()Object;
   18: checkcast #8; // class java/lang/String
   21: astore_2
   22: return
```

所产生的字节码是相同的。对进入 `set()` 的类型进行检查是不需要的，因为这将由编译器执行。而对 `get()` 返回的值进行转型仍然是需要的，只不过不需要你来操作，它由编译器自动插入，这样你就不用编写（阅读）杂乱的代码。

`get()` 和 `set()` 产生了相同的字节码，这就告诉我们泛型的所有动作都发生在边界处——对入参的编译器检查和对返回值的转型。这有助于澄清对擦除的困惑，记住：“边界就是动作发生的地方”。

<!-- Compensating for Erasure -->

## 补偿擦除

因为擦除，我们将失去执行泛型代码中某些操作的能力。无法在运行时知道确切类型：

```java
// generics/Erased.java
// {WillNotCompile}

public class Erased<T> {
    private final int SIZE = 100;

    public void f(Object arg) {
        // error: illegal generic type for instanceof
        if (arg instanceof T) {
        }
        // error: unexpected type
        T var = new T();
        // error: generic array creation
        T[] array = new T[SIZE];
        // warning: [unchecked] unchecked cast
        T[] array = (T[]) new Object[SIZE];

    }
}
```

有时，我们可以对这些问题进行编程，但是有时必须通过引入类型标签来补偿擦除。这意味着为所需的类型显式传递一个 **Class** 对象，以在类型表达式中使用它。

例如，由于擦除了类型信息，因此在上一个程序中尝试使用 **instanceof** 将会失败。类型标签可以使用动态 `isInstance()` ：

```java
// generics/ClassTypeCapture.java

class Building {
}

class House extends Building {
}

public class ClassTypeCapture<T> {
    Class<T> kind;

    public ClassTypeCapture(Class<T> kind) {
        this.kind = kind;
    }

    public boolean f(Object arg) {
        return kind.isInstance(arg);
    }

    public static void main(String[] args) {
        ClassTypeCapture<Building> ctt1 =
                new ClassTypeCapture<>(Building.class);
        System.out.println(ctt1.f(new Building()));
        System.out.println(ctt1.f(new House()));
        ClassTypeCapture<House> ctt2 =
                new ClassTypeCapture<>(House.class);
        System.out.println(ctt2.f(new Building()));
        System.out.println(ctt2.f(new House()));
    }
}
/* Output:
true
true
false
true
*/
```

编译器来保证类型标签与泛型参数相匹配。

<!-- Creating Instances of Types -->
### 创建类型的实例

试图在 **Erased.java** 中 `new T()` 是行不通的，部分原因是由于擦除，部分原因是编译器无法验证 **T** 是否具有默认（无参）构造函数。但是在 C++ 中，此操作自然，直接且安全（在编译时检查）：

```C++
// generics/InstantiateGenericType.cpp
// C++, not Java!

template<class T> class Foo {
  T x; // Create a field of type T
  T* y; // Pointer to T
public:
  // Initialize the pointer:
  Foo() { y = new T(); }
};

class Bar {};

int main() {
  Foo<Bar> fb;
  Foo<int> fi; // ... and it works with primitives
}
```

Java 中的解决方案是传入一个工厂对象，并使用该对象创建新实例。方便的工厂对象只是 **Class** 对象，因此，如果使用类型标记，则可以使用 `newInstance()` 创建该类型的新对象：

```java
// generics/InstantiateGenericType.java

import java.util.function.Supplier;

class ClassAsFactory<T> implements Supplier<T> {
    Class<T> kind;

    ClassAsFactory(Class<T> kind) {
        this.kind = kind;
    }

    @Override
    public T get() {
        try {
            return kind.newInstance();
        } catch (InstantiationException |
                IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

class Employee {
    @Override
    public String toString() {
        return "Employee";
    }
}

public class InstantiateGenericType {
    public static void main(String[] args) {
        ClassAsFactory<Employee> fe =
                new ClassAsFactory<>(Employee.class);
        System.out.println(fe.get());
        ClassAsFactory<Integer> fi =
                new ClassAsFactory<>(Integer.class);
        try {
            System.out.println(fi.get());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
/* Output:
Employee
java.lang.InstantiationException: java.lang.Integer
*/
```

这样可以编译，但对于 `ClassAsFactory<Integer>` 会失败，这是因为 **Integer** 没有无参构造函数。由于错误不是在编译时捕获的，因此语言创建者不赞成这种方法。他们建议使用显式工厂（**Supplier**）并约束类型，以便只有实现该工厂的类可以这样创建对象。这是创建工厂的两种不同方法：

```java
// generics/FactoryConstraint.java

import onjava.Suppliers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class IntegerFactory implements Supplier<Integer> {
    private int i = 0;

    @Override
    public Integer get() {
        return ++i;
    }
}

class Widget {
    private int id;

    Widget(int n) {
        id = n;
    }

    @Override
    public String toString() {
        return "Widget " + id;
    }

    public static
    class Factory implements Supplier<Widget> {
        private int i = 0;

        @Override
        public Widget get() {
            return new Widget(++i);
        }
    }
}

class Fudge {
    private static int count = 1;
    private int n = count++;

    @Override
    public String toString() {
        return "Fudge " + n;
    }
}

class Foo2<T> {
    private List<T> x = new ArrayList<>();

    Foo2(Supplier<T> factory) {
        Suppliers.fill(x, factory, 5);
    }

    @Override
    public String toString() {
        return x.toString();
    }
}

public class FactoryConstraint {
    public static void main(String[] args) {
        System.out.println(
                new Foo2<>(new IntegerFactory()));
        System.out.println(
                new Foo2<>(new Widget.Factory()));
        System.out.println(
                new Foo2<>(Fudge::new));
    }
}
/* Output:
[1, 2, 3, 4, 5]
[Widget 1, Widget 2, Widget 3, Widget 4, Widget 5]
[Fudge 1, Fudge 2, Fudge 3, Fudge 4, Fudge 5]
*/
```

**IntegerFactory** 本身就是通过实现 `Supplier<Integer>` 的工厂。 **Widget** 包含一个内部类，它是一个工厂。还要注意，**Fudge** 并没有做任何类似于工厂的操作，并且传递 `Fudge::new` 仍然会产生工厂行为，因为编译器将对函数方法 `::new` 的调用转换为对 `get()` 的调用。

另一种方法是模板方法设计模式。在以下示例中，`create()` 是模板方法，在子类中被重写以生成该类型的对象：

```java
// generics/CreatorGeneric.java

abstract class GenericWithCreate<T> {
    final T element;

    GenericWithCreate() {
        element = create();
    }

    abstract T create();
}

class X {
}

class XCreator extends GenericWithCreate<X> {
    @Override
    X create() {
        return new X();
    }

    void f() {
        System.out.println(
                element.getClass().getSimpleName());
    }
}

public class CreatorGeneric {
    public static void main(String[] args) {
        XCreator xc = new XCreator();
        xc.f();
    }
}
/* Output:
X
*/
```

**GenericWithCreate** 包含 `element` 字段，并通过无参构造函数强制其初始化，该构造函数又调用抽象的 `create()` 方法。这种创建方式可以在子类中定义，同时建立 **T** 的类型。

<!-- Arrays of Generics -->

### 泛型数组

正如在 **Erased.java** 中所看到的，我们无法创建泛型数组。通用解决方案是在试图创建泛型数组的时候使用 **ArrayList** ：

```java
// generics/ListOfGenerics.java

import java.util.ArrayList;
import java.util.List;

public class ListOfGenerics<T> {
    private List<T> array = new ArrayList<>();

    public void add(T item) {
        array.add(item);
    }

    public T get(int index) {
        return array.get(index);
    }
}
```

这样做可以获得数组的行为，并且还具有泛型提供的编译时类型安全性。

有时，仍然会创建泛型类型的数组（例如， **ArrayList** 在内部使用数组）。可以通过使编译器满意的方式定义对数组的通用引用：

```java
// generics/ArrayOfGenericReference.java

class Generic<T> {
}

public class ArrayOfGenericReference {
    static Generic<Integer>[] gia;
}
```

编译器接受此操作而不产生警告。但是我们永远无法创建具有该确切类型（包括类型参数）的数组，因此有点令人困惑。由于所有数组，无论它们持有什么类型，都具有相同的结构（每个数组插槽的大小和数组布局），因此似乎可以创建一个 **Object** 数组并将其转换为所需的数组类型。实际上，这确实可以编译，但是会产生 **ClassCastException** ：

```java
// generics/ArrayOfGeneric.java

public class ArrayOfGeneric {
    static final int SIZE = 100;
    static Generic<Integer>[] gia;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            gia = (Generic<Integer>[]) new Object[SIZE];
        } catch (ClassCastException e) {
            System.out.println(e.getMessage());
        }
        // Runtime type is the raw (erased) type:
        gia = (Generic<Integer>[]) new Generic[SIZE];
        System.out.println(gia.getClass().getSimpleName());
        gia[0] = new Generic<>();
        //- gia[1] = new Object(); // Compile-time error
        // Discovers type mismatch at compile time:
        //- gia[2] = new Generic<Double>();
    }
}
/* Output:
[Ljava.lang.Object; cannot be cast to [LGeneric;
Generic[]
*/
```

问题在于数组会跟踪其实际类型，而该类型是在创建数组时建立的。因此，即使 `gia` 被强制转换为 `Generic<Integer>[]` ，该信息也仅在编译时存在（并且没有 **@SuppressWarnings** 注解，将会收到有关该强制转换的警告）。在运行时，它仍然是一个 **Object** 数组，这会引起问题。成功创建泛型类型的数组的唯一方法是创建一个已擦除类型的新数组，并将其强制转换。

让我们看一个更复杂的示例。考虑一个包装数组的简单泛型包装器：

```java
// generics/GenericArray.java

public class GenericArray<T> {
    private T[] array;

    @SuppressWarnings("unchecked")
    public GenericArray(int sz) {
        array = (T[]) new Object[sz];
    }

    public void put(int index, T item) {
        array[index] = item;
    }

    public T get(int index) {
        return array[index];
    }

    // Method that exposes the underlying representation:
    public T[] rep() {
        return array;
    }

    public static void main(String[] args) {
        GenericArray<Integer> gai = new GenericArray<>(10);
        try {
            Integer[] ia = gai.rep();
        } catch (ClassCastException e) {
            System.out.println(e.getMessage());
        }
        // This is OK:
        Object[] oa = gai.rep();
    }
}
/* Output:
[Ljava.lang.Object; cannot be cast to
[Ljava.lang.Integer;
*/
```

和以前一样，我们不能说 `T[] array = new T[sz]` ，所以我们创建了一个 **Object** 数组并将其强制转换。

`rep()` 方法返回一个 `T[]` ，在主方法中它应该是 `gai` 的 `Integer[]`，但是如果调用它并尝试将结果转换为 `Integer[]` 引用，则会得到 **ClassCastException** ，这再次是因为实际的运行时类型为 `Object[]` 。

如果再注释掉 **@SuppressWarnings** 注解后编译 **GenericArray.java** ，则编译器会产生警告：

```java
GenericArray.java uses unchecked or unsafe operations.
Recompile with -Xlint:unchecked for details.
```

在这里，我们收到了一个警告，我们认为这是有关强制转换的。

但是要真正确定，请使用 `-Xlint：unchecked` 进行编译：

```java
GenericArray.java:7: warning: [unchecked] unchecked cast    array = (T[])new Object[sz];                 ^  required: T[]  found:    Object[]  where T is a type-variable:    T extends Object declared in class GenericArray 1 warning
```

确实是在抱怨那个强制转换。由于警告会变成噪音，因此，一旦我们确认预期会出现特定警告，我们可以做的最好的办法就是使用 **@SuppressWarnings** 将其关闭。这样，当警告确实出现时，我们将进行实际调查。

由于擦除，数组的运行时类型只能是 `Object[]` 。 如果我们立即将其转换为 `T[]` ，则在编译时会丢失数组的实际类型，并且编译器可能会错过一些潜在的错误检查。因此，最好在集合中使用 `Object[]` ，并在使用数组元素时向 **T** 添加强制类型转换。让我们来看看在 **GenericArray.java** 示例中会是怎么样的：

```java
// generics/GenericArray2.java

public class GenericArray2<T> {
    private Object[] array;

    public GenericArray2(int sz) {
        array = new Object[sz];
    }

    public void put(int index, T item) {
        array[index] = item;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T) array[index];
    }

    @SuppressWarnings("unchecked")
    public T[] rep() {
        return (T[]) array; // Unchecked cast
    }

    public static void main(String[] args) {
        GenericArray2<Integer> gai =
                new GenericArray2<>(10);
        for (int i = 0; i < 10; i++)
            gai.put(i, i);
        for (int i = 0; i < 10; i++)
            System.out.print(gai.get(i) + " ");
        System.out.println();
        try {
            Integer[] ia = gai.rep();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
/* Output:
0 1 2 3 4 5 6 7 8 9
java.lang.ClassCastException: [Ljava.lang.Object;
cannot be cast to [Ljava.lang.Integer;
*/
```

最初，看起来并没有太大不同，只是转换的位置移动了。没有 **@SuppressWarnings** 注解，仍然会收到“unchecked”警告。但是，内部表示现在是 `Object[]` 而不是 `T[]` 。 调用 `get()` 时，它将对象强制转换为 **T** ，实际上这是正确的类型，因此很安全。但是，如果调用 `rep()` ，它将再次尝试将 `Object[]` 强制转换为 `T[]` ，但仍然不正确，并在编译时生成警告，并在运行时生成异常。因此，无法破坏基础数组的类型，该基础数组只能是 `Object[]` 。在内部将数组视为 `Object[]` 而不是 `T[]` 的优点是，我们不太可能会忘记数组的运行时类型并意外地引入了bug，尽管大多数（也许是全部）此类错误会在运行时被迅速检测到。

对于新代码，请传入类型标记。在这种情况下，**GenericArray** 如下所示：

```java
// generics/GenericArrayWithTypeToken.java

import java.lang.reflect.Array;

public class GenericArrayWithTypeToken<T> {
    private T[] array;

    @SuppressWarnings("unchecked")
    public GenericArrayWithTypeToken(Class<T> type, int sz) {
        array = (T[]) Array.newInstance(type, sz);
    }

    public void put(int index, T item) {
        array[index] = item;
    }

    public T get(int index) {
        return array[index];
    }

    // Expose the underlying representation:
    public T[] rep() {
        return array;
    }

    public static void main(String[] args) {
        GenericArrayWithTypeToken<Integer> gai =
                new GenericArrayWithTypeToken<>(
                        Integer.class, 10);
        // This now works:
        Integer[] ia = gai.rep();
    }
}
```

类型标记 **Class\<T\>** 被传递到构造函数中以从擦除中恢复，因此尽管必须使用 **@SuppressWarnings** 关闭来自强制类型转换的警告，但我们仍可以创建所需的实际数组类型。一旦获得了实际的类型，就可以返回它并产生所需的结果，如在主方法中看到的那样。数组的运行时类型是确切的类型 `T[]` 。

不幸的是，如果查看 Java 标准库中的源代码，你会发现到处都有从 **Object** 数组到参数化类型的转换。例如，这是**ArrayList** 中，复制一个 **Collection** 的构造函数，这里为了简化，去除了源码中对此不重要的代码：

```java
public ArrayList(Collection c) {
  size = c.size();
  elementData = (E[])new Object[size];
  c.toArray(elementData);
}
```

如果你浏览 **ArrayList.java** 的代码，将会发现很多此类强制转换。当我们编译它时会发生什么？

```java
Note: ArrayList.java uses unchecked or unsafe operations
Note: Recompile with -Xlint:unchecked for details.
```

果然，标准库会产生很多警告。如果你使用过 C 语言，尤其是使用 ANSI C 之前的语言，你会记住警告的特殊效果：发现警告后，可以忽略它们。因此，除非程序员必须对其进行处理，否则最好不要从编译器发出任何类型的消息。

Neal Gafter（Java 5 的主要开发人员之一）在他的博客中[^2]指出，他在重写 Java 库时是很随意、马虎的，我们不应该像他那样做。Neal 还指出，他在不破坏现有接口的情况下无法修复某些 Java 库代码。因此，即使在 Java 库源代码中出现了一些习惯用法，它们也不一定是正确的做法。当查看库代码时，我们不能认为这就是要在自己代码中必须遵循的示例。

请注意，在 Java 文献中推荐使用类型标记技术，例如 Gilad Bracha 的论文《Generics in the Java Programming Language》[^3]，他指出：“例如，这种用法已广泛用于新的 API 中以处理注解。” 我发现此技术在人们对于舒适度的看法方面存在一些不一致之处；有些人强烈喜欢本章前面介绍的工厂方法。

<!-- Bounds -->

## 边界

*边界*（bounds）在本章的前面进行了简要介绍。边界允许我们对泛型使用的参数类型施加约束。尽管这可以强制执行有关应用了泛型类型的规则，但潜在的更重要的效果是我们可以在绑定的类型中调用方法。

由于擦除会删除类型信息，因此唯一可用于无限制泛型参数的方法是那些 **Object** 可用的方法。但是，如果将该参数限制为某类型的子集，则可以调用该子集中的方法。为了应用约束，Java 泛型使用了 `extends` 关键字。

重要的是要理解，当用于限定泛型类型时，`extends` 的含义与通常的意义截然不同。此示例展示边界的基础应用：

```java
// generics/BasicBounds.java

interface HasColor {
    java.awt.Color getColor();
}

class WithColor<T extends HasColor> {
    T item;

    WithColor(T item) {
        this.item = item;
    }

    T getItem() {
        return item;
    }

    // The bound allows you to call a method:
    java.awt.Color color() {
        return item.getColor();
    }
}

class Coord {
    public int x, y, z;
}

// This fails. Class must be first, then interfaces:
// class WithColorCoord<T extends HasColor & Coord> {

// Multiple bounds:
class WithColorCoord<T extends Coord & HasColor> {
    T item;

    WithColorCoord(T item) {
        this.item = item;
    }

    T getItem() {
        return item;
    }

    java.awt.Color color() {
        return item.getColor();
    }

    int getX() {
        return item.x;
    }

    int getY() {
        return item.y;
    }

    int getZ() {
        return item.z;
    }
}

interface Weight {
    int weight();
}

// As with inheritance, you can have only one
// concrete class but multiple interfaces:
class Solid<T extends Coord & HasColor & Weight> {
    T item;

    Solid(T item) {
        this.item = item;
    }

    T getItem() {
        return item;
    }

    java.awt.Color color() {
        return item.getColor();
    }

    int getX() {
        return item.x;
    }

    int getY() {
        return item.y;
    }

    int getZ() {
        return item.z;
    }

    int weight() {
        return item.weight();
    }
}

class Bounded
        extends Coord implements HasColor, Weight {
    @Override
    public java.awt.Color getColor() {
        return null;
    }

    @Override
    public int weight() {
        return 0;
    }
}

public class BasicBounds {
    public static void main(String[] args) {
        Solid<Bounded> solid =
                new Solid<>(new Bounded());
        solid.color();
        solid.getY();
        solid.weight();
    }
}
```

你可能会观察到 **BasicBounds.java** 中似乎包含一些冗余，它们可以通过继承来消除。在这里，每个继承级别还添加了边界约束：

```java
// generics/InheritBounds.java

class HoldItem<T> {
    T item;

    HoldItem(T item) {
        this.item = item;
    }

    T getItem() {
        return item;
    }
}

class WithColor2<T extends HasColor>
        extends HoldItem<T> {
    WithColor2(T item) {
        super(item);
    }

    java.awt.Color color() {
        return item.getColor();
    }
}

class WithColorCoord2<T extends Coord & HasColor>
        extends WithColor2<T> {
    WithColorCoord2(T item) {
        super(item);
    }

    int getX() {
        return item.x;
    }

    int getY() {
        return item.y;
    }

    int getZ() {
        return item.z;
    }
}

class Solid2<T extends Coord & HasColor & Weight>
        extends WithColorCoord2<T> {
    Solid2(T item) {
        super(item);
    }

    int weight() {
        return item.weight();
    }
}

public class InheritBounds {
    public static void main(String[] args) {
        Solid2<Bounded> solid2 =
                new Solid2<>(new Bounded());
        solid2.color();
        solid2.getY();
        solid2.weight();
    }
}
```

**HoldItem** 拥有一个对象，因此此行为将继承到 **WithColor2** 中，这也需要其参数符合 **HasColor**。 **WithColorCoord2** 和 **Solid2** 进一步扩展了层次结构，并在每个级别添加了边界。现在，这些方法已被继承，并且在每个类中不再重复。

这是一个具有更多层次的示例：

```java
// generics/EpicBattle.java
// Bounds in Java generics

import java.util.List;

interface SuperPower {
}

interface XRayVision extends SuperPower {
    void seeThroughWalls();
}

interface SuperHearing extends SuperPower {
    void hearSubtleNoises();
}

interface SuperSmell extends SuperPower {
    void trackBySmell();
}

class SuperHero<POWER extends SuperPower> {
    POWER power;

    SuperHero(POWER power) {
        this.power = power;
    }

    POWER getPower() {
        return power;
    }
}

class SuperSleuth<POWER extends XRayVision>
        extends SuperHero<POWER> {
    SuperSleuth(POWER power) {
        super(power);
    }

    void see() {
        power.seeThroughWalls();
    }
}

class
CanineHero<POWER extends SuperHearing & SuperSmell>
        extends SuperHero<POWER> {
    CanineHero(POWER power) {
        super(power);
    }

    void hear() {
        power.hearSubtleNoises();
    }

    void smell() {
        power.trackBySmell();
    }
}

class SuperHearSmell
        implements SuperHearing, SuperSmell {
    @Override
    public void hearSubtleNoises() {
    }

    @Override
    public void trackBySmell() {
    }
}

class DogPerson extends CanineHero<SuperHearSmell> {
    DogPerson() {
        super(new SuperHearSmell());
    }
}

public class EpicBattle {
    // Bounds in generic methods:
    static <POWER extends SuperHearing>
    void useSuperHearing(SuperHero<POWER> hero) {
        hero.getPower().hearSubtleNoises();
    }

    static <POWER extends SuperHearing & SuperSmell>
    void superFind(SuperHero<POWER> hero) {
        hero.getPower().hearSubtleNoises();
        hero.getPower().trackBySmell();
    }

    public static void main(String[] args) {
        DogPerson dogPerson = new DogPerson();
        useSuperHearing(dogPerson);
        superFind(dogPerson);
        // You can do this:
        List<? extends SuperHearing> audioPeople;
        // But you can't do this:
        // List<? extends SuperHearing & SuperSmell> dogPs;
    }
}
```

接下来将要研究的通配符将会把范围限制在单个类型。

<!-- Wildcards -->

## 通配符

你已经在 [集合](./12-Collections.md) 章节中看到了一些简单示例使用了通配符——在泛型参数表达式中的问号，在 [类型信息](./19-Type-Information.md) 一章中这种示例更多。本节将更深入地探讨这个特性。

我们的起始示例要展示数组的一种特殊行为：你可以将派生类的数组赋值给基类的引用：

```java
// generics/CovariantArrays.java

class Fruit {}

class Apple extends Fruit {}

class Jonathan extends Apple {}

class Orange extends Fruit {}

public class CovariantArrays {
    
    public static void main(String[] args) {
        Fruit[] fruit = new Apple[10];
        fruit[0] = new Apple(); // OK
        fruit[1] = new Jonathan(); // OK
        // Runtime type is Apple[], not Fruit[] or Orange[]:
        try {
            // Compiler allows you to add Fruit:
            fruit[0] = new Fruit(); // ArrayStoreException
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            // Compiler allows you to add Oranges:
            fruit[0] = new Orange(); // ArrayStoreException
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
/* Output:
java.lang.ArrayStoreException: Fruit
java.lang.ArrayStoreException: Orange
```

`main()` 中的第一行创建了 **Apple** 数组，并赋值给一个 **Fruit** 数组引用。这是有意义的，因为 **Apple** 也是一种 **Fruit**，因此 **Apple** 数组应该也是一个 **Fruit** 数组。

但是，如果实际的数组类型是 **Apple[]**，你可以在其中放置 **Apple** 或 **Apple** 的子类型，这在编译期和运行时都可以工作。但是你也可以在数组中放置 **Fruit** 对象。这对编译器来说是有意义的，因为它有一个 **Fruit[]** 引用——它有什么理由不允许将 **Fruit** 对象或任何从 **Fruit** 继承出来的对象（比如 **Orange**），放置到这个数组中呢？因此在编译期，这是允许的。然而，运行时的数组机制知道它处理的是 **Apple[]**，因此会在向数组中放置异构类型时抛出异常。

向上转型用在这里不合适。你真正在做的是将一个数组赋值给另一个数组。数组的行为是持有其他对象，这里只是因为我们能够向上转型而已，所以很明显，数组对象可以保留有关它们包含的对象类型的规则。看起来就像数组对它们持有的对象是有意识的，因此在编译期检查和运行时检查之间，你不能滥用它们。

数组的这种赋值并不是那么可怕，因为在运行时你可以发现插入了错误的类型。但是泛型的主要目标之一是将这种错误检测移到编译期。所以当我们试图使用泛型集合代替数组时，会发生什么呢？

```java
// generics/NonCovariantGenerics.java
// {WillNotCompile}

import java.util.*;

public class NonCovariantGenerics {
    // Compile Error: incompatible types:
    List<Fruit> flist = new ArrayList<Apple>();
}
```

尽管你在首次阅读这段代码时会认为“不能将一个 **Apple** 集合赋值给一个 **Fruit** 集合”。记住，泛型不仅仅是关于集合，它真正要表达的是“不能把一个涉及 **Apple** 的泛型赋值给一个涉及 **Fruit** 的泛型”。如果像在数组中的情况一样，编译器对代码的了解足够多，可以确定所涉及到的集合，那么它可能会留下一些余地。但是它不知道任何有关这方面的信息，因此它拒绝向上转型。然而实际上这也不是向上转型—— **Apple** 的 **List** 不是 **Fruit** 的 **List**。**Apple** 的 **List** 将持有 **Apple** 和 **Apple** 的子类型，**Fruit** 的 **List** 将持有任何类型的 **Fruit**。是的，这包括 **Apple**，但是它不是一个 **Apple** 的 **List**，它仍然是 **Fruit** 的 **List**。**Apple** 的 **List** 在类型上不等价于 **Fruit** 的 **List**，即使 **Apple** 是一种 **Fruit** 类型。

真正的问题是我们在讨论的集合类型，而不是集合持有对象的类型。与数组不同，泛型没有内建的协变类型。这是因为数组是完全在语言中定义的，因此可以具有编译期和运行时的内建检查，但是在使用泛型时，编译器和运行时系统不知道你想用类型做什么，以及应该采用什么规则。

但是，有时你想在两个类型间建立某种向上转型关系。通配符可以产生这种关系。

```java
// generics/GenericsAndCovariance.java

import java.util.*;

public class GenericsAndCovariance {
    
    public static void main(String[] args) {
        // Wildcards allow covariance:
        List<? extends Fruit> flist = new ArrayList<>();
        // Compile Error: can't add any type of object:
        // flist.add(new Apple());
        // flist.add(new Fruit());
        // flist.add(new Object());
        flist.add(null); // Legal but uninteresting
        // We know it returns at least Fruit:
        Fruit f = flist.get(0);
    }
    
}
```

**flist** 的类型现在是 `List<? extends Fruit>`，你可以读作“一个具有任何从 **Fruit** 继承的类型的列表”。然而，这实际上并不意味着这个 **List** 将持有任何类型的 **Fruit**。通配符引用的是明确的类型，因此它意味着“某种 **flist** 引用没有指定的具体类型”。因此这个被赋值的 **List** 必须持有诸如 **Fruit** 或 **Apple** 这样的指定类型，但是为了向上转型为 **Fruit**，这个类型是什么没人在意。

**List** 必须持有一种具体的 **Fruit** 或 **Fruit** 的子类型，但是如果你不关心具体的类型是什么，那么你能对这样的 **List** 做什么呢？如果不知道 **List** 中持有的对象是什么类型，你怎能保证安全地向其中添加对象呢？就像在 **CovariantArrays.java** 中向上转型一样，你不能，除非编译器而不是运行时系统可以阻止这种操作的发生。你很快就会发现这个问题。

你可能认为事情开始变得有点走极端了，因为现在你甚至不能向刚刚声明过将持有 **Apple** 对象的 **List** 中放入一个 **Apple** 对象。是的，但编译器并不知道这一点。`List<? extends Fruit>` 可能合法地指向一个 `List<Orange>`。一旦执行这种类型的向上转型，你就丢失了向其中传递任何对象的能力，甚至传递 **Object** 也不行。

另一方面，如果你调用了一个返回 **Fruit** 的方法，则是安全的，因为你知道这个 **List** 中的任何对象至少具有 **Fruit** 类型，因此编译器允许这么做。

### 编译器有多聪明

现在你可能会猜想自己不能去调用任何接受参数的方法，但是考虑下面的代码：

```java
// generics/CompilerIntelligence.java

import java.util.*;

public class CompilerIntelligence {
    
    public static void main(String[] args) {
        List<? extends Fruit> flist = Arrays.asList(new Apple());
        Apple a = (Apple) flist.get(0); // No warning
        flist.contains(new Apple()); // Argument is 'Object'
        flist.indexOf(new Apple()); // Argument is 'Object'
    }
    
}
```

这里对 `contains()` 和 `indexOf()` 的调用接受 **Apple** 对象作为参数，执行没问题。这是否意味着编译器实际上会检查代码，以查看是否有某个特定的方法修改了它的对象？

通过查看 **ArrayList** 的文档，我们发现编译器没有那么聪明。尽管 `add()` 接受一个泛型参数类型的参数，但 `contains()` 和 `indexOf()` 接受的参数类型是 **Object**。因此当你指定一个 `ArrayList<? extends Fruit>` 时，`add()` 的参数就变成了"**? extends Fruit**"。从这个描述中，编译器无法得知这里需要 **Fruit** 的哪个具体子类型，因此它不会接受任何类型的 **Fruit**。如果你先把 **Apple** 向上转型为 **Fruit**，也没有关系——编译器仅仅会拒绝调用像 `add()` 这样参数列表中涉及通配符的方法。

`contains()` 和 `indexOf()` 的参数类型是 **Object**，不涉及通配符，所以编译器允许调用它们。这意味着将由泛型类的设计者来决定哪些调用是“安全的”，并使用 **Object** 类作为它们的参数类型。为了禁止对类型中使用了通配符的方法调用，需要在参数列表中使用类型参数。

下面展示一个简单的 **Holder** 类：

```java
public class Holder<T> {

    private T value;

    public Holder() {}

    public Holder(T val) {
        value = val;
    }

    public void set(T val) {
        value = val;
    }

    public T get() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Holder && Objects.equals(value, ((Holder) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public static void main(String[] args) {
        Holder<Apple> apple = new Holder<>(new Apple());
        Apple d = apple.get();
        apple.set(d);
//        Holder<Fruit> fruit = apple; // Cannot upcast
        Holder<? extends Fruit> fruit = apple; // OK
        Fruit p = fruit.get();
        d = (Apple) fruit.get();
        try {
            Orange c = (Orange) fruit.get(); // No warning
        } catch (Exception e) {
            System.out.println(e);
        }
//        fruit.set(new Apple()); // Cannot call set()
//        fruit.set(new Fruit()); // Cannot call set()
        System.out.println(fruit.equals(d)); // OK
    }
}
/* Output
java.lang.ClassCastException: Apple cannot be cast to Orange
false
*/
```

**Holder** 有一个接受 **T** 类型对象的 `set()` 方法，一个返回 T 对象的 `get()` 方法和一个接受 Object 对象的 `equals()` 方法。正如你所见，如果创建了一个 `Holder<Apple>`，就不能将其向上转型为 `Holder<Fruit>`，但是可以向上转型为 `Holder<? extends Fruit>`。如果调用 `get()`，只能返回一个 **Fruit**——这就是在给定“任何；额扩展自 **Fruit** 的对象”这一边界后，它所能知道的一切了。如果你知道更多的信息，就可以将其转型到某种具体的 **Fruit** 而不会导致任何警告，但是存在得到 **ClassCastException** 的风险。`set()` 方法不能工作在 **Apple** 和 **Fruit** 上，因为 `set()` 的参数也是"**? extends Fruit**"，意味着它可以是任何事物，编译器无法验证“任何事物”的类型安全性。

但是，`equals()` 方法可以正常工作，因为它接受的参数是 **Object** 而不是 **T** 类型。因此，编译器只关注传递进来和要返回的对象类型。它不会分析代码，以查看是否执行了任何实际的写入和读取操作。

Java 7 引入了 **java.util.Objects** 库，使创建 `equals()` 和 `hashCode()` 方法变得更加容易，当然还有很多其他功能。`equals()` 方法的标准形式参考 [附录：理解 equals 和 hashCode 方法](./Appendix-Understanding-equals-and-hashCode) 一章。

### 逆变



<!-- Issues -->

## 问题


<!-- Self-Bounded Types -->
## 自我约束类型


<!-- Dynamic Type Safety -->
## 动态类型安全


<!-- Exceptions -->
## 泛型异常


<!-- Mixins -->
## 混入


<!-- Latent Typing -->
## 潜在类型


<!-- Compensating for the Lack of (Direct) Latent -->
## 补偿不足


<!-- Assisted Latent Typing in Java 8 -->
## 辅助潜在类型


<!-- Summary: Is Casting Really So Bad? -->
## 泛型的优劣



[^1]: 在编写本章期间，Angelika Langer的 Java 泛型常见问题解答以及她的其他著作（与Klaus Kreft一起）是非常宝贵的。
[^2]: [http://gafter.blogspot.com/2004/09/puzzling-through-erasureanswer.html](http://gafter.blogspot.com/2004/09/puzzling-through-erasureanswer.html)
[^3]: 参见本章章末引文。




<!-- 分页 -->

<div style="page-break-after: always;"></div>