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

促成泛型出现的最主要的动机之一是为了创建*集合类*，参见 [集合](book/12-Collections.md) 章节。集合用于存放要使用到的对象。数组也是如此，不过集合比数组更加灵活，功能更丰富。几乎所有程序在运行过程中都会涉及到一组对象，因此集合是可复用性最高的类库之一。

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

接下来我们看一个稍微复杂一点的例子：堆栈。在 [集合](book/12-Collections.md) 一章中，我们用 `LinkedList` 实现了 `onjava.Stack` 类。在那个例子中，`LinkedList` 本身已经具备了创建堆栈所需的方法。`Stack` 是通过两个泛型类 `Stack<T>` 和 `LinkedList<T>` 的组合来创建。我们可以看出，泛型只不过是一种类型罢了（稍后我们会看到一些例外的情况）。

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

为了方便起见（不必全限定所有名称），将其静态导入到以下示例中。本示例使用 **EnumSet** 轻松从 **enum** 中创建 **Set** 。（可以在[第二十二章 枚举](book/22-Enumerations.md)一章中了解有关 **EnumSet** 的更多信息。）在这里，静态方法 `EnumSet.range()` 要求提供所要在结果 **Set** 中创建的元素范围的第一个和最后一个元素：

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

在第十二章 [集合的本章小节](book/12-Collections.md#本章小结) 部分将会用到这里的输出结果。

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

`Store.toString()` 显示了结果：尽管有复杂的层次结构，但多层的集合仍然是类型安全的和可管理的。令人印象深刻的是，组装这样的模型并不需要耗费过多精力。

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

如果没有某种类型的迁移途径，所有已经构建了很长时间的类库就需要与希望迁移到 Java 泛型上的开发者们说再见了。类库毫无争议是编程语言的一部分，对生产效率有着极大的影响，所以这种代码无法接受。擦除是否是最佳的或唯一的迁移途径，还待时间来证明。

### 擦除的问题

因此，擦除主要的正当理由是从非泛化代码到泛化代码的转变过程，以及在不破坏现有类库的情况下将泛型融入到语言中。擦除允许你继续使用现有的非泛型客户端代码，直至客户端准备好用泛型重写这些代码。这是一个崇高的动机，因为它不会骤然破坏所有现有的代码。

擦除的代价是显著的。泛型不能用于显式地引用运行时类型的操作中，例如转型、**instanceof** 操作和 **new** 表达式。因为所有关于参数的类型信息都丢失了，当你在编写泛型代码时，必须时刻提醒自己，你只是看起来拥有有关参数的类型信息而已。

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

你已经在 [集合](book/12-Collections.md) 章节中看到了一些简单示例使用了通配符——在泛型参数表达式中的问号，在 [类型信息](book/19-Type-Information.md) 一章中这种示例更多。本节将更深入地探讨这个特性。

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

Java 7 引入了 **java.util.Objects** 库，使创建 `equals()` 和 `hashCode()` 方法变得更加容易，当然还有很多其他功能。`equals()` 方法的标准形式参考 [附录：理解 equals 和 hashCode 方法](book/Appendix-Understanding-equals-and-hashCode) 一章。

### 逆变

还可以走另外一条路，即使用超类型通配符。这里，可以声明通配符是由某个特定类的任何基类来界定的，方法是指定 `<？super MyClass>` ，或者甚至使用类型参数： `<？super T>`（尽管你不能对泛型参数给出一个超类型边界；即不能声明 `<T super MyClass>` ）。这使得你可以安全地传递一个类型对象到泛型类型中。因此，有了超类型通配符，就可以向 **Collection** 写入了：

```java
// generics/SuperTypeWildcards.java
import java.util.*;
public class SuperTypeWildcards {
    static void writeTo(List<? super Apple> apples) {
        apples.add(new Apple());
        apples.add(new Jonathan());
        // apples.add(new Fruit()); // Error
    }
}
```

参数 **apples** 是 **Apple** 的某种基类型的 **List**，这样你就知道向其中添加 **Apple** 或 **Apple** 的子类型是安全的。但是因为 **Apple** 是下界，所以你知道向这样的 **List** 中添加 **Fruit** 是不安全的，因为这将使这个 **List** 敞开口子，从而可以向其中添加非 **Apple** 类型的对象，而这是违反静态类型安全的。
下面的示例复习了一下逆变和通配符的的使用：

```java
// generics/GenericReading.java
import java.util.*;

public class GenericReading {
    static List<Apple> apples = Arrays.asList(new Apple());
    static List<Fruit> fruit = Arrays.asList(new Fruit());
    
    static <T> T readExact(List<T> list) {
        return list.get(0);
    }
    
    // A static method adapts to each call:
    static void f1() {
        Apple a = readExact(apples);
        Fruit f = readExact(fruit);
        f = readExact(apples);
    }
    
    // A class type is established
    // when the class is instantiated:
    static class Reader<T> {
        T readExact(List<T> list) { 
            return list.get(0); 
        }
    }
    
    static void f2() {
        Reader<Fruit> fruitReader = new Reader<>();
        Fruit f = fruitReader.readExact(fruit);
        //- Fruit a = fruitReader.readExact(apples);
        // error: incompatible types: List<Apple>
        // cannot be converted to List<Fruit>
    }
    
    static class CovariantReader<T> {
        T readCovariant(List<? extends T> list) {
            return list.get(0);
        }
    }
    
    static void f3() {
        CovariantReader<Fruit> fruitReader = new CovariantReader<>();
        Fruit f = fruitReader.readCovariant(fruit);
        Fruit a = fruitReader.readCovariant(apples);
    }
    
    public static void main(String[] args) {
        f1(); 
        f2(); 
        f3();
    }
}
```

`readExact()` 方法使用了精确的类型。如果使用这个没有任何通配符的精确类型，就可以向 **List** 中写入和读取这个精确类型。另外，对于返回值，静态的泛型方法 `readExact()` 可以有效地“适应”每个方法调用，并能够从 `List<Apple>` 中返回一个 **Apple** ，从 `List<Fruit>` 中返回一个 **Fruit** ，就像在 `f1()` 中看到的那样。因此，如果可以摆脱静态泛型方法，那么在读取时就不需要协变类型了。
然而对于泛型类来说，当你创建这个类的实例时，就要为这个类确定参数。就像在 `f2()` 中看到的，**fruitReader** 实例可以从 `List<Fruit>` 中读取一个 **Fruit** ，因为这就是它的确切类型。但是 `List<Apple>` 也应该产生 **Fruit** 对象，而 **fruitReader** 不允许这么做。
为了修正这个问题，`CovariantReader.readCovariant()` 方法将接受 `List<？extends T>` ，因此，从这个列表中读取一个 **T** 是安全的（你知道在这个列表中的所有对象至少是一个 **T** ，并且可能是从 T 导出的某种对象）。在 `f3()` 中，你可以看到现在可以从 `List<Apple>` 中读取 **Fruit** 了。

### 无界通配符

无界通配符 `<?>` 看起来意味着“任何事物”，因此使用无界通配符好像等价于使用原生类型。事实上，编译器初看起来是支持这种判断的：

```java
// generics/UnboundedWildcards1.java
import java.util.*;

public class UnboundedWildcards1 {
    static List list1;
    static List<?> list2;
    static List<? extends Object> list3;
  
    static void assign1(List list) {
        list1 = list;
        list2 = list;
        //- list3 = list;
        // warning: [unchecked] unchecked conversion
        // list3 = list;
        //         ^
        // required: List<? extends Object>
        // found:    List
    }
    
    static void assign2(List<?> list) {
        list1 = list;
        list2 = list;
        list3 = list;
    }
    
    static void assign3(List<? extends Object> list) {
        list1 = list;
        list2 = list;
        list3 = list;
    }
    
    public static void main(String[] args) {
        assign1(new ArrayList());
        assign2(new ArrayList());
        //- assign3(new ArrayList());
        // warning: [unchecked] unchecked method invocation:
        // method assign3 in class UnboundedWildcards1
        // is applied to given types
        // assign3(new ArrayList());
        //        ^
        // required: List<? extends Object>
        // found: ArrayList
        // warning: [unchecked] unchecked conversion
        // assign3(new ArrayList());
        //         ^
        // required: List<? extends Object>
        // found:    ArrayList
        // 2 warnings
        assign1(new ArrayList<>());
        assign2(new ArrayList<>());
        assign3(new ArrayList<>());
        // Both forms are acceptable as List<?>:
        List<?> wildList = new ArrayList();
        wildList = new ArrayList<>();
        assign1(wildList);
        assign2(wildList);
        assign3(wildList);
    }
}
```

有很多情况都和你在这里看到的情况类似，即编译器很少关心使用的是原生类型还是 `<?>` 。在这些情况中，`<?>` 可以被认为是一种装饰，但是它仍旧是很有价值的，因为，实际上它是在声明：“我是想用 Java 的泛型来编写这段代码，我在这里并不是要用原生类型，但是在当前这种情况下，泛型参数可以持有任何类型。”
第二个示例展示了无界通配符的一个重要应用。当你在处理多个泛型参数时，有时允许一个参数可以是任何类型，同时为其他参数确定某种特定类型的这种能力会显得很重要：

```java
// generics/UnboundedWildcards2.java
import java.util.*;

public class UnboundedWildcards2 {
    static Map map1;
    static Map<?,?> map2;
    static Map<String,?> map3;
  
    static void assign1(Map map) { 
        map1 = map; 
    }
    
    static void assign2(Map<?,?> map) { 
        map2 = map; 
    }
    
    static void assign3(Map<String,?> map) { 
        map3 = map; 
    }
    
    public static void main(String[] args) {
        assign1(new HashMap());
        assign2(new HashMap());
        //- assign3(new HashMap());
        // warning: [unchecked] unchecked method invocation:
        // method assign3 in class UnboundedWildcards2
        // is applied to given types
        //     assign3(new HashMap());
        //            ^
        //   required: Map<String,?>
        //   found: HashMap
        // warning: [unchecked] unchecked conversion
        //     assign3(new HashMap());
        //             ^
        //   required: Map<String,?>
        //   found:    HashMap
        // 2 warnings
        assign1(new HashMap<>());
        assign2(new HashMap<>());
        assign3(new HashMap<>());
    }
}
```

但是，当你拥有的全都是无界通配符时，就像在 `Map<?,?>` 中看到的那样，编译器看起来就无法将其与原生 **Map** 区分开了。另外， **UnboundedWildcards1.java** 展示了编译器处理  `List<?>` 和 `List<? extends Object>` 是不同的。
令人困惑的是，编译器并非总是关注像 `List` 和 `List<?>` 之间的这种差异，因此它们看起来就像是相同的事物。事实上，因为泛型参数擦除到它的第一个边界，因此 `List<?>` 看起来等价于 `List<Object>` ，而 **List** 实际上也是 `List<Object>` ——除非这些语句都不为真。**List** 实际上表示“持有任何 **Object** 类型的原生 **List ** ”，而 `List<?>` 表示“具有某种特定类型的非原生 **List** ，只是我们不知道类型是什么。”
编译器何时才会关注原生类型和涉及无界通配符的类型之间的差异呢？下面的示例使用了前面定义的 `Holder<T>` 类，它包含接受 **Holder** 作为参数的各种方法，但是它们具有不同的形式：作为原生类型，具有具体的类型参数以及具有无界通配符参数：

```java
// generics/Wildcards.java
// Exploring the meaning of wildcards

public class Wildcards {
    // Raw argument:
    static void rawArgs(Holder holder, Object arg) {
        //- holder.set(arg);
        // warning: [unchecked] unchecked call to set(T)
        // as a member of the raw type Holder
        //     holder.set(arg);
        //               ^
        //   where T is a type-variable:
        //     T extends Object declared in class Holder
        // 1 warning

        // Can't do this; don't have any 'T':
        // T t = holder.get();

        // OK, but type information is lost:
        Object obj = holder.get();
    }
    
    // Like rawArgs(), but errors instead of warnings:
    static void unboundedArg(Holder<?> holder, Object arg) {
        //- holder.set(arg);
        // error: method set in class Holder<T>
        // cannot be applied to given types;
        //     holder.set(arg);
        //           ^
        //   required: CAP#1
        //   found: Object
        //   reason: argument mismatch;
        //     Object cannot be converted to CAP#1
        //   where T is a type-variable:
        //     T extends Object declared in class Holder
        //   where CAP#1 is a fresh type-variable:
        //     CAP#1 extends Object from capture of ?
        // 1 error

        // Can't do this; don't have any 'T':
        // T t = holder.get();

        // OK, but type information is lost:
        Object obj = holder.get();
    }
    
    static <T> T exact1(Holder<T> holder) {
        return holder.get();
    }
    
    static <T> T exact2(Holder<T> holder, T arg) {
        holder.set(arg);
        return holder.get();
    }
    
    static <T> T wildSubtype(Holder<? extends T> holder, T arg) {
        //- holder.set(arg);
        // error: method set in class Holder<T#2>
        // cannot be applied to given types;
        //     holder.set(arg);
        //           ^
        //   required: CAP#1
        //   found: T#1
        //   reason: argument mismatch;
        //     T#1 cannot be converted to CAP#1
        //   where T#1,T#2 are type-variables:
        //     T#1 extends Object declared in method
        //     <T#1>wildSubtype(Holder<? extends T#1>,T#1)
        //     T#2 extends Object declared in class Holder
        //   where CAP#1 is a fresh type-variable:
        //     CAP#1 extends T#1 from
        //       capture of ? extends T#1
        // 1 error
        return holder.get();
    }
    
    static <T> void wildSupertype(Holder<? super T> holder, T arg) {
        holder.set(arg);
        //- T t = holder.get();
        // error: incompatible types:
        // CAP#1 cannot be converted to T
        //     T t = holder.get();
        //                     ^
        //   where T is a type-variable:
        //     T extends Object declared in method
        //       <T>wildSupertype(Holder<? super T>,T)
        //   where CAP#1 is a fresh type-variable:
        //     CAP#1 extends Object super:
        //       T from capture of ? super T
        // 1 error

        // OK, but type information is lost:
        Object obj = holder.get();
    }
    
    public static void main(String[] args) {
        Holder raw = new Holder<>();
        // Or:
        raw = new Holder();
        Holder<Long> qualified = new Holder<>();
        Holder<?> unbounded = new Holder<>();
        Holder<? extends Long> bounded = new Holder<>();
        Long lng = 1L;

        rawArgs(raw, lng);
        rawArgs(qualified, lng);
        rawArgs(unbounded, lng);
        rawArgs(bounded, lng);

        unboundedArg(raw, lng);
        unboundedArg(qualified, lng);
        unboundedArg(unbounded, lng);
        unboundedArg(bounded, lng);

        //- Object r1 = exact1(raw);
        // warning: [unchecked] unchecked method invocation:
        // method exact1 in class Wildcards is applied
        // to given types
        //      Object r1 = exact1(raw);
        //                        ^
        //   required: Holder<T>
        //   found: Holder
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>exact1(Holder<T>)
        // warning: [unchecked] unchecked conversion
        //      Object r1 = exact1(raw);
        //                         ^
        //   required: Holder<T>
        //   found:    Holder
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>exact1(Holder<T>)
        // 2 warnings

        Long r2 = exact1(qualified);
        Object r3 = exact1(unbounded); // Must return Object
        Long r4 = exact1(bounded);

        //- Long r5 = exact2(raw, lng);
        // warning: [unchecked] unchecked method invocation:
        // method exact2 in class Wildcards is
        // applied to given types
        //     Long r5 = exact2(raw, lng);
        //                     ^
        //   required: Holder<T>,T
        //   found: Holder,Long
        //   where T is a type-variable:
        //     T extends Object declared in
        //       method <T>exact2(Holder<T>,T)
        // warning: [unchecked] unchecked conversion
        //     Long r5 = exact2(raw, lng);
        //                      ^
        //   required: Holder<T>
        //   found:    Holder
        //   where T is a type-variable:
        //     T extends Object declared in
        //       method <T>exact2(Holder<T>,T)
        // 2 warnings

        Long r6 = exact2(qualified, lng);

        //- Long r7 = exact2(unbounded, lng);
        // error: method exact2 in class Wildcards
        // cannot be applied to given types;
        //     Long r7 = exact2(unbounded, lng);
        //               ^
        //   required: Holder<T>,T
        //   found: Holder<CAP#1>,Long
        //   reason: inference variable T has
        //     incompatible bounds
        //     equality constraints: CAP#1
        //     lower bounds: Long
        //   where T is a type-variable:
        //     T extends Object declared in
        //       method <T>exact2(Holder<T>,T)
        //   where CAP#1 is a fresh type-variable:
        //     CAP#1 extends Object from capture of ?
        // 1 error

        //- Long r8 = exact2(bounded, lng);
        // error: method exact2 in class Wildcards
        // cannot be applied to given types;
        //      Long r8 = exact2(bounded, lng);
        //                ^
        //   required: Holder<T>,T
        //   found: Holder<CAP#1>,Long
        //   reason: inference variable T
        //     has incompatible bounds
        //     equality constraints: CAP#1
        //     lower bounds: Long
        //   where T is a type-variable:
        //     T extends Object declared in
        //       method <T>exact2(Holder<T>,T)
        //   where CAP#1 is a fresh type-variable:
        //     CAP#1 extends Long from
        //       capture of ? extends Long
        // 1 error

        //- Long r9 = wildSubtype(raw, lng);
        // warning: [unchecked] unchecked method invocation:
        // method wildSubtype in class Wildcards
        // is applied to given types
        //     Long r9 = wildSubtype(raw, lng);
        //                          ^
        //   required: Holder<? extends T>,T
        //   found: Holder,Long
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>wildSubtype(Holder<? extends T>,T)
        // warning: [unchecked] unchecked conversion
        //     Long r9 = wildSubtype(raw, lng);
        //                           ^
        //   required: Holder<? extends T>
        //   found:    Holder
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>wildSubtype(Holder<? extends T>,T)
        // 2 warnings

        Long r10 = wildSubtype(qualified, lng);
        // OK, but can only return Object:
        Object r11 = wildSubtype(unbounded, lng);
        Long r12 = wildSubtype(bounded, lng);

        //- wildSupertype(raw, lng);
        // warning: [unchecked] unchecked method invocation:
        //   method wildSupertype in class Wildcards
        //   is applied to given types
        //     wildSupertype(raw, lng);
        //                  ^
        //   required: Holder<? super T>,T
        //   found: Holder,Long
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>wildSupertype(Holder<? super T>,T)
        // warning: [unchecked] unchecked conversion
        //     wildSupertype(raw, lng);
        //                   ^
        //   required: Holder<? super T>
        //   found:    Holder
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>wildSupertype(Holder<? super T>,T)
        // 2 warnings

        wildSupertype(qualified, lng);

        //- wildSupertype(unbounded, lng);
        // error: method wildSupertype in class Wildcards
        // cannot be applied to given types;
        //     wildSupertype(unbounded, lng);
        //     ^
        //   required: Holder<? super T>,T
        //   found: Holder<CAP#1>,Long
        //   reason: cannot infer type-variable(s) T
        //     (argument mismatch; Holder<CAP#1>
        //     cannot be converted to Holder<? super T>)
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>wildSupertype(Holder<? super T>,T)
        //   where CAP#1 is a fresh type-variable:
        //     CAP#1 extends Object from capture of ?
        // 1 error

        //- wildSupertype(bounded, lng);
        // error: method wildSupertype in class Wildcards
        // cannot be applied to given types;
        //     wildSupertype(bounded, lng);
        //     ^
        //   required: Holder<? super T>,T
        //   found: Holder<CAP#1>,Long
        //   reason: cannot infer type-variable(s) T
        //     (argument mismatch; Holder<CAP#1>
        //     cannot be converted to Holder<? super T>)
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>wildSupertype(Holder<? super T>,T)
        //   where CAP#1 is a fresh type-variable:
        //     CAP#1 extends Long from capture of
        //     ? extends Long
        // 1 error
    }
}
```

在 `rawArgs()` 中，编译器知道 `Holder` 是一个泛型类型，因此即使它在这里被表示成一个原生类型，编译器仍旧知道向 `set()` 传递一个 **Object** 是不安全的。由于它是原生类型，你可以将任何类型的对象传递给 `set()` ，而这个对象将被向上转型为 **Object** 。因此无论何时，只要使用了原生类型，都会放弃编译期检查。对 `get()` 的调用说明了相同的问题：没有任何 **T** 类型的对象，因此结果只能是一个 **Object**。
人们很自然地会开始考虑原生 `Holder` 与 `Holder<?>` 是大致相同的事物。但是 `unboundedArg()` 强调它们是不同的——它揭示了相同的问题，但是它将这些问题作为错误而不是警告报告，因为原生 **Holder** 将持有任何类型的组合，而 `Holder<?>` 将持有具有某种具体类型的同构集合，因此不能只是向其中传递 **Object** 。
在 `exact1()` 和 `exact2()` 中，你可以看到使用了确切的泛型参数——没有任何通配符。你将看到，`exact2()`与 `exact1()` 具有不同的限制，因为它有额外的参数。
在 `wildSubtype()` 中，在 **Holder** 类型上的限制被放松为包括持有任何扩展自 **T** 的对象的 **Holder** 。这还是意味着如果 T 是 **Fruit** ，那么 `holder` 可以是 `Holder<Apple>` ，这是合法的。为了防止将 **Orange** 放置到 `Holder<Apple>` 中，对 `set()` 的调用（或者对任何接受这个类型参数为参数的方法的调用）都是不允许的。但是，你仍旧知道任何来自 `Holder<？extends Fruit>` 的对象至少是 **Fruit** ，因此 `get()` （或者任何将产生具有这个类型参数的返回值的方法）都是允许的。
`wildSupertype()` 展示了超类型通配符，这个方法展示了与 `wildSubtype()` 相反的行为：`holder` 可以是持有任何 T 的基类型的容器。因此， `set()` 可以接受 **T** ，因为任何可以工作于基类的对象都可以多态地作用于导出类（这里就是 **T** ）。但是，尝试着调用 `get()` 是没有用的，因为由 `holder` 持有的类型可以是任何超类型，因此唯一安全的类型就是 **Object** 。
这个示例还展示了对于在 `unbounded()` 中使用无界通配符能够做什么不能做什么所做出的限制：因为你没有 **T**，所以你不能将 `set()` 或 `get()` 作用于 **T** 上。

在 `main()` 方法中你看到了某些方法在接受某些类型的参数时没有报错和警告。为了迁移兼容性，`rawArgs()`  将接受所有 **Holder** 的不同变体，而不会产生警告。`unboundedArg()` 方法也可以接受相同的所有类型，尽管如前所述，它在方法体内部处理这些类型的方式并不相同。

如果向接受“确切”泛型类型（没有通配符）的方法传递一个原生 **Holder** 引用，就会得到一个警告，因为确切的参数期望得到在原生类型中并不存在的信息。如果向 `exact1()` 传递一个无界引用，就不会有任何可以确定返回类型的类型信息。
可以看到，`exact2()` 具有最多的限制，因为它希望精确地得到一个 `Holder<T>` ，以及一个具有类型 **T** 的参数，正由于此，它将产生错误或警告，除非提供确切的参数。有时，这样做很好，但是如果它过于受限，那么就可以使用通配符，这取决于是否想要从泛型参数中返回类型确定的返回值（就像在 `wildSubtype()` 中看到的那样），或者是否想要向泛型参数传递类型确定的参数（就像在 `wildSupertype()` 中看到的那样）。
因此，使用确切类型来替代通配符类型的好处是，可以用泛型参数来做更多的事，但是使用通配符使得你必须接受范围更宽的参数化类型作为参数。因此，必须逐个情况地权衡利弊，找到更适合你的需求的方法。

### 捕获转换

有一种特殊情况需要使用 `<?>` 而不是原生类型。如果向一个使用 `<?>` 的方法传递原生类型，那么对编译器来说，可能会推断出实际的类型参数，使得这个方法可以回转并调用另一个使用这个确切类型的方法。下面的示例演示了这种技术，它被称为捕获转换，因为未指定的通配符类型被捕获，并被转换为确切类型。这里，有关警告的注释只有在 `@SuppressWarnings` 注解被移除之后才能起作用：

```java
// generics/CaptureConversion.java

public class CaptureConversion {
    static <T> void f1(Holder<T> holder) {
        T t = holder.get();
        System.out.println(t.getClass().getSimpleName());
    }
  
    static void f2(Holder<?> holder) {
        f1(holder); // Call with captured type
    }
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Holder raw = new Holder<>(1);
        f1(raw);
        // warning: [unchecked] unchecked method invocation:
        // method f1 in class CaptureConversion
        // is applied to given types
        //     f1(raw);
        //       ^
        //   required: Holder<T>
        //   found: Holder
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>f1(Holder<T>)
        // warning: [unchecked] unchecked conversion
        //     f1(raw);
        //        ^
        //   required: Holder<T>
        //   found:    Holder
        //   where T is a type-variable:
        //     T extends Object declared in
        //     method <T>f1(Holder<T>)
        // 2 warnings
        f2(raw); // No warnings
        
        Holder rawBasic = new Holder();
        rawBasic.set(new Object());
        // warning: [unchecked] unchecked call to set(T)
        // as a member of the raw type Holder
        //     rawBasic.set(new Object());
        //                 ^
        //   where T is a type-variable:
        //     T extends Object declared in class Holder
        // 1 warning
        f2(rawBasic); // No warnings
        
        // Upcast to Holder<?>, still figures it out:
        Holder<?> wildcarded = new Holder<>(1.0);
        f2(wildcarded);
    }
}
/* Output:
Integer
Integer
Object
Double
*/
```

`f1()` 中的类型参数都是确切的，没有通配符或边界。在 `f2()` 中，**Holder** 参数是一个无界通配符，因此它看起来是未知的。但是，在 `f2()` 中调用了 `f1()`，而 `f1()` 需要一个已知参数。这里所发生的是：在调用 `f2()` 的过程中捕获了参数类型，并在调用 `f1()` 时使用了这种类型。
你可能想知道这项技术是否可以用于写入，但是这要求在传递 `Holder<?>` 时同时传递一个具体类型。捕获转换只有在这样的情况下可以工作：即在方法内部，你需要使用确切的类型。注意，不能从 `f2()` 中返回 **T**，因为 **T ** 对于 `f2()` 来说是未知的。捕获转换十分有趣，但是非常受限。

<!-- Issues -->

## 问题

本节将阐述在使用 Java 泛型时会出现的各类问题。

### 任何基本类型都不能作为类型参数

正如本章早先提到的，Java 泛型的限制之一是不能将基本类型用作类型参数。因此，不能创建  `ArrayList<int>` 之类的东西。
解决方法是使用基本类型的包装器类以及自动装箱机制。如果创建一个 `ArrayList<Integer>`，并将基本类型 **int** 应用于这个集合，那么你将发现自动装箱机制将自动地实现 **int** 到 **Integer** 的双向转换——因此，这几乎就像是有一个 `ArrayList<int>` 一样：

```java
// generics/ListOfInt.java
// Autoboxing compensates for the inability
// to use primitives in generics
import java.util.*;
import java.util.stream.*;

public class ListOfInt {
    public static void main(String[] args) {
        List<Integer> li = IntStream.range(38, 48)
            .boxed() // Converts ints to Integers
            .collect(Collectors.toList());
        System.out.println(li);
    }
}
/* Output:
[38, 39, 40, 41, 42, 43, 44, 45, 46, 47]
*/
```

通常，这种解决方案工作得很好——能够成功地存储和读取 **int**，自动装箱隐藏了转换的过程。但是如果性能成为问题的话，就需要使用专门为基本类型适配的特殊版本的集合；一个开源版本的实现是 **org.apache.commons.collections.primitives**。
下面是另外一种方式，它可以创建持有 **Byte** 的 **Set**：

```java
// generics/ByteSet.java
import java.util.*;

public class ByteSet {
    Byte[] possibles = { 1,2,3,4,5,6,7,8,9 };
    Set<Byte> mySet = new HashSet<>(Arrays.asList(possibles));
    // But you can't do this:
    // Set<Byte> mySet2 = new HashSet<>(
    // Arrays.<Byte>asList(1,2,3,4,5,6,7,8,9));
}
```

自动装箱机制解决了一些问题，但并没有解决所有问题。

在下面的示例中，**FillArray** 接口包含一些通用方法，这些方法使用 **Supplier** 来用对象填充数组（这使得类泛型在本例中无法工作，因为这个方法是静态的）。**Supplier** 实现来自 [数组](book/21-Arrays.md) 一章,并且在 `main()` 中，可以看到 `FillArray.fill()` 使用对象填充了数组：

```java
// generics/PrimitiveGenericTest.java
import onjava.*;
import java.util.*;
import java.util.function.*;

// Fill an array using a generator:
interface FillArray {
    static <T> T[] fill(T[] a, Supplier<T> gen) {
        Arrays.setAll(a, n -> gen.get());
        return a;
    }
    
    static int[] fill(int[] a, IntSupplier gen) {
        Arrays.setAll(a, n -> gen.getAsInt());
        return a;
    }
    
    static long[] fill(long[] a, LongSupplier gen) {
        Arrays.setAll(a, n -> gen.getAsLong());
        return a;
    }
    
    static double[] fill(double[] a, DoubleSupplier gen) {
        Arrays.setAll(a, n -> gen.getAsDouble());
        return a;
    }
}

public class PrimitiveGenericTest {
    public static void main(String[] args) {
        String[] strings = FillArray.fill(
            new String[5], new Rand.String(9));
        System.out.println(Arrays.toString(strings));
        int[] integers = FillArray.fill(
            new int[9], new Rand.Pint());
        System.out.println(Arrays.toString(integers));
    }
}
/* Output:
[btpenpccu, xszgvgmei, nneeloztd, vewcippcy, gpoalkljl]
[635, 8737, 3941, 4720, 6177, 8479, 6656, 3768, 4948]
*/
```

自动装箱不适用于数组，因此我们必须创建 `FillArray.fill()` 的重载版本，或创建产生 **Wrapped** 输出的生成器。 **FillArray** 仅比 `java.util.Arrays.setAll()` 有用一点，因为它返回填充的数组。

### 实现参数化接口

一个类不能实现同一个泛型接口的两种变体，由于擦除的原因，这两个变体会成为相同的接口。下面是产生这种冲突的情况：

```java
// generics/MultipleInterfaceVariants.java
// {WillNotCompile}
package generics;

interface Payable<T> {}

class Employee implements Payable<Employee> {}

class Hourly extends Employee implements Payable<Hourly> {}
```

**Hourly** 不能编译，因为擦除会将  `Payable<Employe>` 和 `Payable<Hourly>` 简化为相同的类 **Payable**，这样，上面的代码就意味着在重复两次地实现相同的接口。十分有趣的是，如果从 **Payable** 的两种用法中都移除掉泛型参数（就像编译器在擦除阶段所做的那样）这段代码就可以编译。

在使用某些更基本的 Java 接口，例如 `Comparable<T>` 时，这个问题可能会变得十分令人恼火，就像你在本节稍后看到的那样。

### 转型和警告

使用带有泛型类型参数的转型或 **instanceof** 不会有任何效果。下面的集合在内部将各个值存储为 **Object**，并在获取这些值时，再将它们转型回 **T**：

```java
// generics/GenericCast.java
import java.util.*;
import java.util.stream.*;

class FixedSizeStack<T> {
    private final int size;
    private Object[] storage;
    private int index = 0;
    
    FixedSizeStack(int size) {
        this.size = size;
        storage = new Object[size];
    }
    
    public void push(T item) {
        if(index < size)
            storage[index++] = item;
    }
    
    @SuppressWarnings("unchecked")
    public T pop() {
        return index == 0 ? null : (T)storage[--index];
    }
    
    @SuppressWarnings("unchecked")
    Stream<T> stream() {
        return (Stream<T>)Arrays.stream(storage);
    }
}

public class GenericCast {
    static String[] letters = "ABCDEFGHIJKLMNOPQRS".split("");
  
    public static void main(String[] args) {
        FixedSizeStack<String> strings =
            new FixedSizeStack<>(letters.length);
        Arrays.stream("ABCDEFGHIJKLMNOPQRS".split(""))
            .forEach(strings::push);
        System.out.println(strings.pop());
        strings.stream()
            .map(s -> s + " ")
            .forEach(System.out::print);
    }
}
/* Output:
S
A B C D E F G H I J K L M N O P Q R S
*/
```

如果没有 **@SuppressWarnings** 注解，编译器将对 `pop()` 产生 “unchecked cast” 警告。由于擦除的原因，编译器无法知道这个转型是否是安全的，并且 `pop()` 方法实际上并没有执行任何转型。
这是因为，**T** 被擦除到它的第一个边界，默认情况下是 **Object** ，因此 `pop()` 实际上只是将 **Object** 转型为 **Object**。
有时，泛型没有消除对转型的需要，这就会由编译器产生警告，而这个警告是不恰当的。例如：

```java
// generics/NeedCasting.java
import java.io.*;
import java.util.*;

public class NeedCasting {
    @SuppressWarnings("unchecked")
    public void f(String[] args) throws Exception {
        ObjectInputStream in = new ObjectInputStream(
            new FileInputStream(args[0]));
        List<Widget> shapes = (List<Widget>)in.readObject();
    }
}
```

正如你将在 [附录：对象序列化](book/Appendix-Object-Serialization.md) 中学到的那样，`readObject()` 无法知道它正在读取的是什么，因此它返回的是必须转型的对象。但是当注释掉 **@SuppressWarnings** 注解并编译这个程序时，就会得到下面的警告。

```
NeedCasting.java uses unchecked or unsafe operations.
Recompile with -Xlint:unchecked for details.

And if you follow the instructions and recompile with  -
Xlint:unchecked :(如果遵循这条指示，使用-Xlint:unchecked来重新编译：)

NeedCasting.java:10: warning: [unchecked] unchecked cast
    List<Widget> shapes = (List<Widget>)in.readObject();
    required: List<Widget>
    found: Object
1 warning
```

你会被强制要求转型，但是又被告知不应该转型。为了解决这个问题，必须使用 Java 5 引入的新的转型形式，既通过泛型类来转型：

```java
// generics/ClassCasting.java
import java.io.*;
import java.util.*;

public class ClassCasting {
    @SuppressWarnings("unchecked")
    public void f(String[] args) throws Exception {
        ObjectInputStream in = new ObjectInputStream(
            new FileInputStream(args[0]));
        // Won't Compile:
        //    List<Widget> lw1 =
        //    List<>.class.cast(in.readObject());
        List<Widget> lw2 = List.class.cast(in.readObject());
    }
}
```

但是，不能转型到实际类型（ `List<Widget>` ）。也就是说，不能声明：

```
List<Widget>.class.cast(in.readobject())
```

甚至当你添加一个像下面这样的另一个转型时：

```
(List<Widget>)List.class.cast(in.readobject())
```

仍旧会得到一个警告。

### 重载

下面的程序是不能编译的，即使它看起来是合理的：

```java
// generics/UseList.java
// {WillNotCompile}
import java.util.*;

public class UseList<W, T> {
    void f(List<T> v) {}
    void f(List<W> v) {}
}
```

因为擦除，所以重载方法产生了的类型签名。

因而，当擦除后的参数不能产生唯一的参数列表时，你必须提供不同的方法名：

```java
// generics/UseList2.java

import java.util.*;

public class UseList2<W, T> {
    void f1(List<T> v) {}
    void f2(List<W> v) {}
}
```

幸运的是，编译器可以检测到这类问题。

### 基类劫持接口

假设你有一个实现了 **Comparable** 接口的 **Pet** 类：

```java
// generics/ComparablePet.java

public class ComparablePet implements Comparable<ComparablePet> {
    @Override
    public int compareTo(ComparablePet o) {
        return 0;
    }
}
```

尝试缩小 **ComparablePet** 子类的比较类型是有意义的。例如，**Cat** 类可以与其他的 **Cat** 比较：

```java
// generics/HijackedInterface.java
// {WillNotCompile}

class Cat extends ComparablePet implements Comparable<Cat> {
    // error: Comparable cannot be inherited with
    // different arguments: <Cat> and <ComparablePet>
    // class Cat
    // ^
    // 1 error
    public int compareTo(Cat arg) {
        return 0;
    }
}
```

不幸的是，这不能工作。一旦 **Comparable** 的类型参数设置为 **ComparablePet**，其他的实现类只能比较 **ComparablePet**：

```java
// generics/RestrictedComparablePets.java

public class Hamster extends ComparablePet implements Comparable<ComparablePet> {

    @Override
    public int compareTo(ComparablePet arg) {
        return 0;
    }
}
// Or just:
class Gecko extends ComparablePet {
    public int compareTo(ComparablePet arg) {
        return 0;
    }
}
```

**Hamster** 显示了重新实现 **ComparableSet** 中相同的接口是可能的，只要接口完全相同，包括参数类型。然而正如 **Gecko** 中所示，这与直接覆写基类的方法完全相同。

<!-- Self-Bounded Types -->

## 自限定的类型

在 Java 泛型中，有一个似乎经常性出现的惯用法，它相当令人费解：

```java
class SelfBounded<T extends SelfBounded<T>> { // ...
```

这就像两面镜子彼此照向对方所引起的目眩效果一样，是一种无限反射。**SelfBounded** 类接受泛型参数 **T**，而 **T** 由一个边界类限定，这个边界就是拥有 **T** 作为其参数的 **SelfBounded**。

当你首次看到它时，很难去解析它，它强调的是当 **extends** 关键字用于边界与用来创建子类明显是不同的。

### 古怪的循环泛型

为了理解自限定类型的含义，我们从这个惯用法的一个简单版本入手，它没有自限定的边界。

不能直接继承一个泛型参数，但是，可以继承在其自己的定义中使用这个泛型参数的类。也就是说，可以声明：

```java
// generics/CuriouslyRecurringGeneric.java

class GenericType<T> {}

public class CuriouslyRecurringGeneric
  extends GenericType<CuriouslyRecurringGeneric> {}
```

这可以按照 Jim Coplien 在 C++ 中的*古怪的循环模版模式*的命名方式，称为古怪的循环泛型（CRG）。“古怪的循环”是指类相当古怪地出现在它自己的基类中这一事实。
为了理解其含义，努力大声说：“我在创建一个新类，它继承自一个泛型类型，这个泛型类型接受我的类的名字作为其参数。”当给出导出类的名字时，这个泛型基类能够实现什么呢？好吧，Java 中的泛型关乎参数和返回类型，因此它能够产生使用导出类作为其参数和返回类型的基类。它还能将导出类型用作其域类型，尽管这些将被擦除为 **Object** 的类型。下面是表示了这种情况的一个泛型类：

```java
// generics/BasicHolder.java

public class BasicHolder<T> {
    T element;
    void set(T arg) { element = arg; }
    T get() { return element; }
    void f() {
        System.out.println(element.getClass().getSimpleName());
    }
}
```

这是一个普通的泛型类型，它的一些方法将接受和产生具有其参数类型的对象，还有一个方法在其存储的域上执行操作（尽管只是在这个域上执行 **Object** 操作）。
我们可以在一个古怪的循环泛型中使用 **BasicHolder**：

```java
// generics/CRGWithBasicHolder.java

class Subtype extends BasicHolder<Subtype> {}

public class CRGWithBasicHolder {
    public static void main(String[] args) {
        Subtype st1 = new Subtype(), st2 = new Subtype();
        st1.set(st2);
        Subtype st3 = st1.get();
        st1.f();
    }
}
/* Output:
Subtype
*/
```

注意，这里有些东西很重要：新类 **Subtype** 接受的参数和返回的值具有 **Subtype** 类型而不仅仅是基类 **BasicHolder** 类型。这就是 CRG 的本质：基类用导出类替代其参数。这意味着泛型基类变成了一种其所有导出类的公共功能的模版，但是这些功能对于其所有参数和返回值，将使用导出类型。也就是说，在所产生的类中将使用确切类型而不是基类型。因此，在**Subtype** 中，传递给 `set()` 的参数和从 `get()` 返回的类型都是确切的 **Subtype**。

### 自限定

**BasicHolder** 可以使用任何类型作为其泛型参数，就像下面看到的那样：

```java
// generics/Unconstrained.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

class Other {}
class BasicOther extends BasicHolder<Other> {}

public class Unconstrained {
    public static void main(String[] args) {
        BasicOther b = new BasicOther();
        BasicOther b2 = new BasicOther();
        b.set(new Other());
        Other other = b.get();
        b.f();
    }
}
/* Output:
Other
*/
```

限定将采取额外的步骤，强制泛型当作其自身的边界参数来使用。观察所产生的类可以如何使用以及不可以如何使用：

```java
// generics/SelfBounding.java

class SelfBounded<T extends SelfBounded<T>> {
    T element;
    SelfBounded<T> set(T arg) {
        element = arg;
        return this;
    }
    T get() { return element; }
}

class A extends SelfBounded<A> {}
class B extends SelfBounded<A> {} // Also OK

class C extends SelfBounded<C> {
    C setAndGet(C arg) { 
        set(arg); 
        return get();
    }
}

class D {}
// Can't do this:
// class E extends SelfBounded<D> {}
// Compile error:
//   Type parameter D is not within its bound

// Alas, you can do this, so you cannot force the idiom:
class F extends SelfBounded {}

public class SelfBounding {
    public static void main(String[] args) {
        A a = new A();
        a.set(new A());
        a = a.set(new A()).get();
        a = a.get();
        C c = new C();
        c = c.setAndGet(new C());
    }
}
```

自限定所做的，就是要求在继承关系中，像下面这样使用这个类：

```java
class A extends SelfBounded<A>{}
```

这会强制要求将正在定义的类当作参数传递给基类。

自限定的参数有何意义呢？它可以保证类型参数必须与正在被定义的类相同。正如你在 B 类的定义中所看到的，还可以从使用了另一个 **SelfBounded** 参数的 **SelfBounded** 中导出，尽管在 **A** 类看到的用法看起来是主要的用法。对定义 **E** 的尝试说明不能使用不是 **SelfBounded** 的类型参数。
遗憾的是， **F** 可以编译，不会有任何警告，因此自限定惯用法不是可强制执行的。如果它确实很重要，可以要求一个外部工具来确保不会使用原生类型来替代参数化类型。
注意，可以移除自限定这个限制，这样所有的类仍旧是可以编译的，但是 **E** 也会因此而变得可编译：

```java
// generics/NotSelfBounded.java

public class NotSelfBounded<T> {
    T element;
    NotSelfBounded<T> set(T arg) {
        element = arg;
        return this;
    }
    T get() { return element; }
} 

class A2 extends NotSelfBounded<A2> {}
class B2 extends NotSelfBounded<A2> {}

class C2 extends NotSelfBounded<C2> {
    C2 setAndGet(C2 arg) { 
        set(arg); 
        return get(); 
    }
}

class D2 {}
// Now this is OK:
class E2 extends NotSelfBounded<D2> {}
```

因此很明显，自限定限制只能强制作用于继承关系。如果使用自限定，就应该了解这个类所用的类型参数将与使用这个参数的类具有相同的基类型。这会强制要求使用这个类的每个人都要遵循这种形式。
还可以将自限定用于泛型方法：

```java
// generics/SelfBoundingMethods.java
// (c)2017 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class SelfBoundingMethods {
    static <T extends SelfBounded<T>> T f(T arg) {
        return arg.set(arg).get();
    }
    
    public static void main(String[] args) {
        A a = f(new A());
    }
}
```

这可以防止这个方法被应用于除上述形式的自限定参数之外的任何事物上。

### 参数协变

自限定类型的价值在于它们可以产生*协变参数类型*——方法参数类型会随子类而变化。

尽管自限定类型还可以产生与子类类型相同的返回类型，但是这并不十分重要，因为*协变返回类型*是在 Java 5 引入：

```java
// generics/CovariantReturnTypes.java

class Base {}
class Derived extends Base {}

interface OrdinaryGetter {
    Base get();
}

interface DerivedGetter extends OrdinaryGetter {
    // Overridden method return type can vary:
    @Override
    Derived get();
}

public class CovariantReturnTypes {
    void test(DerivedGetter d) {
        Derived d2 = d.get();
    }
}
```

**DerivedGetter** 中的 `get()` 方法覆盖了 **OrdinaryGetter** 中的 `get()` ，并返回了一个从 `OrdinaryGetter.get()` 的返回类型中导出的类型。尽管这是完全合乎逻辑的事情（导出类方法应该能够返回比它覆盖的基类方法更具体的类型）但是这在早先的 Java 版本中是不合法的。

自限定泛型事实上将产生确切的导出类型作为其返回值，就像在 `get()` 中所看到的一样：

```java
// generics/GenericsAndReturnTypes.java

interface GenericGetter<T extends GenericGetter<T>> {
    T get();
}

interface Getter extends GenericGetter<Getter> {}

public class GenericsAndReturnTypes {
    void test(Getter g) {
        Getter result = g.get();
        GenericGetter gg = g.get(); // Also the base type
    }
}
```

注意，这段代码不能编译，除非是使用囊括了协变返回类型的 Java 5。

然而，在非泛型代码中，参数类型不能随子类型发生变化：

```java
// generics/OrdinaryArguments.java

class OrdinarySetter {
    void set(Base base) {
        System.out.println("OrdinarySetter.set(Base)");
    }
}

class DerivedSetter extends OrdinarySetter {
    void set(Derived derived) {
        System.out.println("DerivedSetter.set(Derived)");
    }
}

public class OrdinaryArguments {
    public static void main(String[] args) {
        Base base = new Base();
        Derived derived = new Derived();
        DerivedSetter ds = new DerivedSetter();
        ds.set(derived);
        // Compiles--overloaded, not overridden!:
        ds.set(base);
    }
}
/* Output:
DerivedSetter.set(Derived)
OrdinarySetter.set(Base)
*/
```

`set(derived)` 和 `set(base)` 都是合法的，因此 `DerivedSetter.set()` 没有覆盖 `OrdinarySetter.set()` ，而是重载了这个方法。从输出中可以看到，在 **DerivedSetter** 中有两个方法，因此基类版本仍旧是可用的，因此可以证明它被重载过。
但是，在使用自限定类型时，在导出类中只有一个方法，并且这个方法接受导出类型而不是基类型为参数：

```java
// generics/SelfBoundingAndCovariantArguments.java

interface SelfBoundSetter<T extends SelfBoundSetter<T>> {
    void set(T arg);
}

interface Setter extends SelfBoundSetter<Setter> {}

public class SelfBoundingAndCovariantArguments {
    void
    testA(Setter s1, Setter s2, SelfBoundSetter sbs) {
        s1.set(s2);
        //- s1.set(sbs);
        // error: method set in interface SelfBoundSetter<T>
        // cannot be applied to given types;
        //     s1.set(sbs);
        //       ^
        //   required: Setter
        //   found: SelfBoundSetter
        //   reason: argument mismatch;
        // SelfBoundSetter cannot be converted to Setter
        //   where T is a type-variable:
        //     T extends SelfBoundSetter<T> declared in
        //     interface SelfBoundSetter
        // 1 error
    }
}
```

编译器不能识别将基类型当作参数传递给 `set()` 的尝试，因为没有任何方法具有这样的签名。实际上，这个参数已经被覆盖。
如果不使用自限定类型，普通的继承机制就会介入，而你将能够重载，就像在非泛型的情况下一样：

```java
// generics/PlainGenericInheritance.java

class GenericSetter<T> { // Not self-bounded
    void set(T arg) {
        System.out.println("GenericSetter.set(Base)");
    }
}

class DerivedGS extends GenericSetter<Base> {
    void set(Derived derived) {
        System.out.println("DerivedGS.set(Derived)");
    }
}

public class PlainGenericInheritance {
    public static void main(String[] args) {
        Base base = new Base();
        Derived derived = new Derived();
        DerivedGS dgs = new DerivedGS();
        dgs.set(derived);
        dgs.set(base); // Overloaded, not overridden!
    }
}
/* Output:
DerivedGS.set(Derived)
GenericSetter.set(Base)
*/
```

这段代码在模仿 **OrdinaryArguments.java**；在那个示例中，**DerivedSetter** 继承自包含一个 `set(Base)` 的**OrdinarySetter** 。而这里，**DerivedGS** 继承自泛型创建的也包含有一个 `set(Base)`的 `GenericSetter<Base>`。就像 **OrdinaryArguments.java** 一样，你可以从输出中看到， **DerivedGS** 包含两个  `set()` 的重载版本。如果不使用自限定，将重载参数类型。如果使用了自限定，只能获得方法的一个版本，它将接受确切的参数类型。

<!-- Dynamic Type Safety -->

## 动态类型安全

因为可以向 Java 5 之前的代码传递泛型集合，所以旧式代码仍旧有可能会破坏你的集合。Java 5 的 **java.util.Collections** 中有一组便利工具，可以解决在这种情况下的类型检查问题，它们是：静态方法 `checkedCollection()` 、`checkedList()`、 `checkedMap()` 、 `checkedSet()` 、`checkedSortedMap()`和 `checkedSortedSet()`。这些方法每一个都会将你希望动态检查的集合当作第一个参数接受，并将你希望强制要求的类型作为第二个参数接受。

受检查的集合在你试图插入类型不正确的对象时抛出 **ClassCastException** ，这与泛型之前的（原生）集合形成了对比，对于后者来说，当你将对象从集合中取出时，才会通知你出现了问题。在后一种情况中，你知道存在问题，但是不知道罪魁祸首在哪里，如果使用受检查的集合，就可以发现谁在试图插入不良对象。
让我们用受检查的集合来看看“将猫插入到狗列表中”这个问题。这里，`oldStyleMethod()` 表示遗留代码，因为它接受的是原生的 **List** ，而 **@SuppressWarnings（“unchecked”）** 注解对于压制所产生的警告是必需的：

```java
// generics/CheckedList.java
// Using Collection.checkedList()
import typeinfo.pets.*;
import java.util.*;

public class CheckedList {
    @SuppressWarnings("unchecked")
    static void oldStyleMethod(List probablyDogs) {
        probablyDogs.add(new Cat());
    }
    
    public static void main(String[] args) {
        List<Dog> dogs1 = new ArrayList<>();
        oldStyleMethod(dogs1); // Quietly accepts a Cat
        List<Dog> dogs2 = Collections.checkedList(
            new ArrayList<>(), Dog.class);
        try {
            oldStyleMethod(dogs2); // Throws an exception
        } catch(Exception e) {
            System.out.println("Expected: " + e);
        }
        // Derived types work fine:
        List<Pet> pets = Collections.checkedList(
            new ArrayList<>(), Pet.class);
        pets.add(new Dog());
        pets.add(new Cat());
    }
}
/* Output:
Expected: java.lang.ClassCastException: Attempt to
insert class typeinfo.pets.Cat element into collection
with element type class typeinfo.pets.Dog
*/
```

运行这个程序时，你会发现插入一个 **Cat** 对于 **dogs1** 来说没有任何问题，而 **dogs2** 立即会在这个错误类型的插入操作上抛出一个异常。还可以看到，将导出类型的对象放置到将要检查基类型的受检查容器中是没有问题的。

<!-- Exceptions -->

## 泛型异常

由于擦除的原因，**catch** 语句不能捕获泛型类型的异常，因为在编译期和运行时都必须知道异常的确切类型。泛型类也不能直接或间接继承自 **Throwable**（这将进一步阻止你去定义不能捕获的泛型异常）。
但是，类型参数可能会在一个方法的 **throws** 子句中用到。这使得你可以编写随检查型异常类型变化的泛型代码：

```java
// generics/ThrowGenericException.java

import java.util.*;

interface Processor<T, E extends Exception> {
    void process(List<T> resultCollector) throws E;
}

class ProcessRunner<T, E extends Exception>
extends ArrayList<Processor<T, E>> {
    List<T> processAll() throws E {
        List<T> resultCollector = new ArrayList<>();
        for(Processor<T, E> processor : this)
            processor.process(resultCollector);
        return resultCollector;
    }
}

class Failure1 extends Exception {}

class Processor1
implements Processor<String, Failure1> {
    static int count = 3;
    @Override
    public void process(List<String> resultCollector)
    throws Failure1 {
        if(count-- > 1)
            resultCollector.add("Hep!");
        else
            resultCollector.add("Ho!");
        if(count < 0)
            throw new Failure1();
    }
}

class Failure2 extends Exception {}

class Processor2
implements Processor<Integer, Failure2> {
    static int count = 2;
    @Override
    public void process(List<Integer> resultCollector)
    throws Failure2 {
        if(count-- == 0)
            resultCollector.add(47);
        else {
            resultCollector.add(11);
        }
        if(count < 0)
            throw new Failure2();
    }
}

public class ThrowGenericException {
    public static void main(String[] args) {
        ProcessRunner<String, Failure1> runner =
            new ProcessRunner<>();
        for(int i = 0; i < 3; i++)
            runner.add(new Processor1());
        try {
            System.out.println(runner.processAll());
        } catch(Failure1 e) {
            System.out.println(e);
        }

        ProcessRunner<Integer, Failure2> runner2 =
            new ProcessRunner<>();
        for(int i = 0; i < 3; i++)
            runner2.add(new Processor2());
        try {
            System.out.println(runner2.processAll());
        } catch(Failure2 e) {
            System.out.println(e);
        }
    }
}
/* Output:
[Hep!, Hep!, Ho!]
Failure2
*/
```

**Processor** 执行 `process()` 方法，并且可能会抛出具有类型 **E** 的异常。`process()` 的结果存储在 `List<T>resultCollector` 中（这被称为*收集参数*）。**ProcessRunner** 有一个 `processAll()` 方法，它会在所持有的每个 **Process** 对象执行，并返回 **resultCollector** 。
如果不能参数化所抛出的异常，那么由于检查型异常的缘故，将不能编写出这种泛化的代码。

<!-- Mixins -->

## 混型

术语*混型*随时间的推移好像拥有了无数的含义，但是其最基本的概念是混合多个类的能力，以产生一个可以表示混型中所有类型的类。这往往是你最后的手段，它将使组装多个类变得简单易行。
混型的价值之一是它们可以将特性和行为一致地应用于多个类之上。如果想在混型类中修改某些东西，作为一种意外的好处，这些修改将会应用于混型所应用的所有类型之上。正由于此，混型有一点*面向切面编程* （AOP） 的味道，而切面经常被建议用来解决混型问题。

### C++ 中的混型

在 C++ 中，使用多重继承的最大理由，就是为了使用混型。但是，对于混型来说，更有趣、更优雅的方式是使用参数化类型，因为混型就是继承自其类型参数的类。在 C++ 中，可以很容易地创建混型，因为 C++ 能够记住其模版参数的类型。
下面是一个 C++ 示例，它有两个混型类型：一个使得你可以在每个对象中混入拥有一个时间戳这样的属性，而另一个可以混入一个序列号。

```c++
// generics/Mixins.cpp

#include <string>
#include <ctime>
#include <iostream>
using namespace std;

template<class T> class TimeStamped : public T {
    long timeStamp;
public:
    TimeStamped() { timeStamp = time(0); }
    long getStamp() { return timeStamp; }
};

template<class T> class SerialNumbered : public T {
    long serialNumber;
    static long counter;
public:
    SerialNumbered() { serialNumber = counter++; }
    long getSerialNumber() { return serialNumber; }
};

// Define and initialize the static storage:
template<class T> long SerialNumbered<T>::counter = 1;

class Basic {
    string value;
public:
    void set(string val) { value = val; }
    string get() { return value; }
};

int main() {
    TimeStamped<SerialNumbered<Basic>> mixin1, mixin2;
    mixin1.set("test string 1");
    mixin2.set("test string 2");
    cout << mixin1.get() << " " << mixin1.getStamp() <<
      " " << mixin1.getSerialNumber() << endl;
    cout << mixin2.get() << " " << mixin2.getStamp() <<
      " " << mixin2.getSerialNumber() << endl;
}
/* Output:
test string 1 1452987605 1
test string 2 1452987605 2
*/
```

在 `main()` 中， **mixin1** 和 **mixin2** 所产生的类型拥有所混入类型的所有方法。可以将混型看作是一种功能，它可以将现有类映射到新的子类上。注意，使用这种技术来创建一个混型是多么的轻而易举。基本上，只需要声明“这就是我想要的”，紧跟着它就发生了：

```c++
TimeStamped<SerialNumbered<Basic>> mixin1，mixin2；
```

遗憾的是，Java 泛型不允许这样。擦除会忘记基类类型，因此

>  泛型类不能直接继承自一个泛型参数

这突显了许多我在 Java 语言设计决策（以及与这些功能一起发布）中遇到的一大问题：处理一件事很有希望，但是当您实际尝试做一些有趣的事情时，您会发现自己做不到。

### 与接口混合

一种更常见的推荐解决方案是使用接口来产生混型效果，就像下面这样：

```java
// generics/Mixins.java

import java.util.*;

interface TimeStamped { long getStamp(); }

class TimeStampedImp implements TimeStamped {
    private final long timeStamp;
    TimeStampedImp() {
        timeStamp = new Date().getTime();
    }
    @Override
    public long getStamp() { return timeStamp; }
}

interface SerialNumbered { long getSerialNumber(); }

class SerialNumberedImp implements SerialNumbered {
    private static long counter = 1;
    private final long serialNumber = counter++;
    @Override
    public long getSerialNumber() { return serialNumber; }
}

interface Basic {
    void set(String val);
    String get();
}

class BasicImp implements Basic {
    private String value;
    @Override
    public void set(String val) { value = val; }
    @Override
    public String get() { return value; }
}

class Mixin extends BasicImp
implements TimeStamped, SerialNumbered {
    private TimeStamped timeStamp = new TimeStampedImp();
    private SerialNumbered serialNumber =
        new SerialNumberedImp();
    @Override
    public long getStamp() {
        return timeStamp.getStamp();
    }
    @Override
    public long getSerialNumber() {
        return serialNumber.getSerialNumber();
    }
}

public class Mixins {
    public static void main(String[] args) {
        Mixin mixin1 = new Mixin(), mixin2 = new Mixin();
        mixin1.set("test string 1");
        mixin2.set("test string 2");
        System.out.println(mixin1.get() + " " +
            mixin1.getStamp() +  " " + mixin1.getSerialNumber());
        System.out.println(mixin2.get() + " " +
            mixin2.getStamp() +  " " + mixin2.getSerialNumber());
    }
}
/* Output:
test string 1 1494331663026 1
test string 2 1494331663027 2
*/
```

**Mixin** 类基本上是在使用*委托*，因此每个混入类型都要求在 **Mixin** 中有一个相应的域，而你必须在 **Mixin** 中编写所有必需的方法，将方法调用转发给恰当的对象。这个示例使用了非常简单的类，但是当使用更复杂的混型时，代码数量会急速增加。

### 使用装饰器模式

当你观察混型的使用方式时，就会发现混型概念好像与*装饰器*设计模式关系很近。装饰器经常用于满足各种可能的组合，而直接子类化会产生过多的类，因此是不实际的。
装饰器模式使用分层对象来动态透明地向单个对象中添加责任。装饰器指定包装在最初的对象周围的所有对象都具有相同的基本接口。某些事物是可装饰的，可以通过将其他类包装在这个可装饰对象的四周，来将功能分层。这使得对装饰器的使用是透明的——无论对象是否被装饰，你都拥有一个可以向对象发送的公共消息集。装饰类也可以添加新方法，但是正如你所见，这将是受限的。
装饰器是通过使用组合和形式化结构（可装饰物/装饰器层次结构）来实现的，而混型是基于继承的。因此可以将基于参数化类型的混型当作是一种泛型装饰器机制，这种机制不需要装饰器设计模式的继承结构。
前面的示例可以被改写为使用装饰器：

```java
// generics/decorator/Decoration.java

// {java generics.decorator.Decoration}
package generics.decorator;
import java.util.*;

class Basic {
    private String value;
    public void set(String val) { value = val; }
    public String get() { return value; }
}

class Decorator extends Basic {
    protected Basic basic;
    Decorator(Basic basic) { this.basic = basic; }
    @Override
    public void set(String val) { basic.set(val); }
    @Override
    public String get() { return basic.get(); }
}

class TimeStamped extends Decorator {
    private final long timeStamp;
    TimeStamped(Basic basic) {
        super(basic);
        timeStamp = new Date().getTime();
    }
    public long getStamp() { return timeStamp; }
}

class SerialNumbered extends Decorator {
    private static long counter = 1;
    private final long serialNumber = counter++;
    SerialNumbered(Basic basic) { super(basic); }
    public long getSerialNumber() { return serialNumber; }
}

public class Decoration {
    public static void main(String[] args) {
        TimeStamped t = new TimeStamped(new Basic());
        TimeStamped t2 = new TimeStamped(
            new SerialNumbered(new Basic()));
        //- t2.getSerialNumber(); // Not available
        SerialNumbered s = new SerialNumbered(new Basic());
        SerialNumbered s2 = new SerialNumbered(
            new TimeStamped(new Basic()));
        //- s2.getStamp(); // Not available
  }
}
```

产生自泛型的类包含所有感兴趣的方法，但是由使用装饰器所产生的对象类型是最后被装饰的类型。也就是说，尽管可以添加多个层，但是最后一层才是实际的类型，因此只有最后一层的方法是可视的，而混型的类型是所有被混合到一起的类型。因此对于装饰器来说，其明显的缺陷是它只能有效地工作于装饰中的一层（最后一层），而混型方法显然会更自然一些。因此，装饰器只是对由混型提出的问题的一种局限的解决方案。

### 与动态代理混合

可以使用动态代理来创建一种比装饰器更贴近混型模型的机制（查看 [类型信息](book/19-Type-Information.md) 一章中关于 Java 的动态代理如何工作的解释）。通过使用动态代理，所产生的类的动态类型将会是已经混入的组合类型。
由于动态代理的限制，每个被混入的类都必须是某个接口的实现：

```java
// generics/DynamicProxyMixin.java

import java.lang.reflect.*;
import java.util.*;
import onjava.*;
import static onjava.Tuple.*;

class MixinProxy implements InvocationHandler {
    Map<String, Object> delegatesByMethod;
    @SuppressWarnings("unchecked")
    MixinProxy(Tuple2<Object, Class<?>>... pairs) {
        delegatesByMethod = new HashMap<>();
        for(Tuple2<Object, Class<?>> pair : pairs) {
            for(Method method : pair.a2.getMethods()) {
                String methodName = method.getName();
                // The first interface in the map
                // implements the method.
                if(!delegatesByMethod.containsKey(methodName))
                    delegatesByMethod.put(methodName, pair.a1);
            }
        }
    }
    @Override
    public Object invoke(Object proxy, Method method,
      Object[] args) throws Throwable {
        String methodName = method.getName();
        Object delegate = delegatesByMethod.get(methodName);
        return method.invoke(delegate, args);
    }
    
    @SuppressWarnings("unchecked")
    public static Object newInstance(Tuple2... pairs) {
        Class[] interfaces = new Class[pairs.length];
        for(int i = 0; i < pairs.length; i++) {
            interfaces[i] = (Class)pairs[i].a2;
        }
        ClassLoader cl = pairs[0].a1.getClass().getClassLoader();
        return Proxy.newProxyInstance(cl, interfaces, new MixinProxy(pairs));
    }
}

public class DynamicProxyMixin {
    public static void main(String[] args) {
        Object mixin = MixinProxy.newInstance(
          tuple(new BasicImp(), Basic.class),
          tuple(new TimeStampedImp(), TimeStamped.class),
          tuple(new SerialNumberedImp(), SerialNumbered.class));
        Basic b = (Basic)mixin;
        TimeStamped t = (TimeStamped)mixin;
        SerialNumbered s = (SerialNumbered)mixin;
        b.set("Hello");
        System.out.println(b.get());
        System.out.println(t.getStamp());
        System.out.println(s.getSerialNumber());
    }
}
/* Output:
Hello
1494331653339
1
*/
```

因为只有动态类型而不是静态类型才包含所有的混入类型，因此这仍旧不如 C++ 的方式好，因为可以在具有这些类型的对象上调用方法之前，你被强制要求必须先将这些对象向下转型到恰当的类型。但是，它明显地更接近于真正的混型。
为了让 Java 支持混型，人们已经做了大量的工作朝着这个目标努力，包括创建了至少一种附加语言（ Jam 语言），它是专门用来支持混型的。

<!-- Latent Typing -->

## 潜在类型机制

在本章的开头介绍过这样的思想，即要编写能够尽可能广泛地应用的代码。为了实现这一点，我们需要各种途径来放松对我们的代码将要作用的类型所作的限制，同时不丢失静态类型检查的好处。然后，我们就可以编写出无需修改就可以应用于更多情况的代码，即更加“泛化”的代码。

Java 泛型看起来是向这一方向迈进了一步。当你在编写或使用只是持有对象的泛型时，这些代码将可以工作于任何类型（除了基本类型，尽管正如你所见到的，自动装箱机制可以克服这一点）。或者，换个角度讲，“持有器”泛型能够声明：“我不关心你是什么类型”。如果代码不关心它将要作用的类型，那么这种代码就可以真正地应用于任何地方，并因此而相当泛化。

还是正如你所见到的，当要在泛型类型上执行操作（即调用 **Object** 方法之外的方法）时，就会产生问题。擦除强制要求指定可能会用到的泛型类型的边界，以安全地调用代码中的泛型对象上的具体方法。这是对“泛化”概念的一种明显的限制，因为必须限制你的泛型类型，使它们继承自特定的类，或者实现特定的接口。在某些情况下，你最终可能会使用普通类或普通接口，因为限定边界的泛型可能会和指定类或接口没有任何区别。

某些编程语言提供的一种解决方案称为*潜在类型机制*或*结构化类型机制*，而更古怪的术语称为*鸭子类型机制*，即“如果它走起来像鸭子，并且叫起来也像鸭子，那么你就可以将它当作鸭子对待。”鸭子类型机制变成了一种相当流行的术语，可能是因为它不像其他的术语那样承载着历史的包袱。

泛型代码典型地只能在泛型类型上调用少量方法，而具有潜在类型机制的语言只要求实现某个方法子集，而不是某个特定类或接口，从而放松了这种限制（并且可以产生更加泛化的代码）。正由于此，潜在类型机制使得你可以横跨类继承结构，调用不属于某个公共接口的方法。因此，实际上一段代码可以声明：“我不关心你是什么类型，只要你可以 `speak()` 和 `sit()` 即可。”由于不要求具体类型，因此代码就可以更加泛化。

潜在类型机制是一种代码组织和复用机制。有了它，编写出的代码相对于没有它编写出的代码，能够更容易地复用。代码组织和复用是所有计算机编程的基本手段：编写一次，多次使用，并在一个位置保存代码。因为我并未被要求去命名我的代码要操作于其上的确切接口，所以，有了潜在类型机制，我就可以编写更少的代码，并更容易地将其应用于多个地方。

支持潜在类型机制的语言包括 Python（可以从 www.Python.org 免费下载）、C++、Ruby、SmallTalk 和 Go。Python 是动态类型语言（几乎所有的类型检查都发生在运行时），而 C++ 和 Go 是静态类型语言（类型检查发生在编译期），因此潜在类型机制不要求静态或动态类型检查。

### pyhton 中的潜在类型

如果我们将上面的描述用 Python 来表示，如下所示：

```python
# generics/DogsAndRobots.py

class Dog:
    def speak(self):
        print("Arf!")
    def sit(self):
        print("Sitting")
    def reproduce(self):
        pass

class Robot:
    def speak(self):
        print("Click!")
    def sit(self):
        print("Clank!")
    def oilChange(self):
        pass

def perform(anything):
    anything.speak()
    anything.sit()

a = Dog()
b = Robot()
perform(a)
perform(b)

output = """
Arf!
Sitting
Click!
Clank!
"""
```

Python 使用缩进来确定作用域（因此不需要任何花括号），而冒号将表示新的作用域的开始。“**#**” 表示注释到行尾，就像Java中的 “ **//** ”。类的方法需要显式地指定 **this** 引用的等价物作为第一个参数，按惯例成为 **self** 。构造器调用不要求任何类型的“ **new** ”关键字，并且 Python 允许普通（非成员）函数，就像 `perform()` 所表明的那样。注意，在 `perform(anything)` 中，没有任何针对 **anything** 的类型，**anything** 只是一个标识符，它必须能够执行 `perform()` 期望它执行的操作，因此这里隐含着一个接口。但是你从来都不必显式地写出这个接口——它是潜在的。`perform()` 不关心其参数的类型，因此我可以向它传递任何对象，只要该对象支持  `speak()` 和 `sit()` 方法。如果传递给 `perform()` 的对象不支持这些操作，那么将会得到运行时异常。

输出规定使用三重引号创建带有内嵌换行符的字符串。

### C++ 中的潜在类型

我们可以用 C++ 产生相同的效果：

```c++
// generics/DogsAndRobots.cpp

#include <iostream>
using namespace std;

class Dog {
public:
    void speak() { cout << "Arf!" << endl; }
    void sit() { cout << "Sitting" << endl; }
    void reproduce() {}
};

class Robot {
public:
    void speak() { cout << "Click!" << endl; }
    void sit() { cout << "Clank!" << endl; }
    void oilChange() {}
};

template<class T> void perform(T anything) {
    anything.speak();
    anything.sit();
}

int main() {
    Dog d;
    Robot r;
    perform(d);
    perform(r);
}
/* Output:
Arf!
Sitting
Click!
Clank!
*/
```

在 Python 和 C++ 中，**Dog** 和 **Robot** 没有任何共同的东西，只是碰巧有两个方法具有相同的签名。从类型的观点看，它们是完全不同的类型。但是，`perform()` 不关心其参数的具体类型，并且潜在类型机制允许它接受这两种类型的对象。
C++ 确保了它实际上可以发送的那些消息，如果试图传递错误类型，编译器就会给你一个错误消息（这些错误消息从历史上看是相当可怕和冗长的，是 C++ 的模版名声欠佳的主要原因）。尽管它们是在不同时期实现这一点的，C++ 在编译期，而 Python 在运行时，但是这两种语言都可以确保类型不会被误用，因此被认为是强类型的。潜在类型机制没有损害强类型机制。

### Go 中的潜在类型

这里用 Go 语言编写相同的程序：

```go
// generics/dogsandrobots.go

package main
import "fmt"

type Dog struct {}
func (this Dog) speak() { fmt.Printf("Arf!\n")}
func (this Dog) sit() { fmt.Printf("Sitting\n")}
func (this Dog) reproduce() {}

type Robot struct {}
func (this Robot) speak() { fmt.Printf("Click!\n") }
func (this Robot) sit() { fmt.Printf("Clank!\n") }
func (this Robot) oilChange() {}

func perform(speaker interface { speak(); sit() }) {
  speaker.speak();
  speaker.sit();
}

func main() {
  perform(Dog{})
  perform(Robot{})
}
/* Output:
Arf!
Sitting
Click!
Clank!
*/
```

Go 没有 **class** 关键字，但是可以使用上述形式创建等效的基本类：它通常不定义为类，而是定义为 **struct** ，在其中定义数据字段（此处不存在）。 对于每种方法，都以 **func** 关键字开头，然后（为了将该方法附加到您的类上）放在括号中，该括号包含对象引用，该对象引用可以是任何标识符，但是我在这里使用 **this** 来提醒您，就像在 C ++ 或 Java 中的 **this** 一样。 然后，在Go中像这样定义其余的函数。

Go也没有继承关系，因此这种“面向对象的目标”形式是相对原始的，并且可能是我无法花更多的时间来学习该语言的主要原因。 但是，Go 的组成很简单。

`perform()` 函数使用潜在类型：参数的确切类型并不重要，只要它包含了 `speak()` 和  `sit()` 方法即可。 该接口在此处匿名定义，内联，如 `perform()` 的参数列表所示。

`main()` 证明 `perform()` 确实对其参数的确切类型不在乎，只要可以在该参数上调用 `talk()` 和 `sit()` 即可。 但是，就像 C ++ 模板函数一样，在编译时检查类型。

语法 **Dog {}** 和 **Robot {}** 创建匿名的 **Dog** 和 **Robot** 结构。

### java中的直接潜在类型

因为泛型是在这场竞赛的后期才添加到 Java 中，因此没有任何机会可以去实现任何类型的潜在类型机制，因此 Java 没有对这种特性的支持。所以，初看起来，Java 的泛型机制比支持潜在类型机制的语言更“缺乏泛化性”。（使用擦除来实现 Java 泛型的实现有时称为第二类泛型类型）例如，在 Java 8  之前如果我们试图用 Java 实现上面 dogs-and-robots 的示例，那么就会被强制要求使用一个类或接口，并在边界表达式中指定它：

```java
// generics/Performs.java

public interface Performs {
    void speak();
    void sit();
}
```

```java
// generics/DogsAndRobots.java
// No (direct) latent typing in Java
import typeinfo.pets.*;

class PerformingDog extends Dog implements Performs {
    @Override
    public void speak() { System.out.println("Woof!"); }
    @Override
    public void sit() { System.out.println("Sitting"); }
    public void reproduce() {}
}

class Robot implements Performs {
    public void speak() { System.out.println("Click!"); }
    public void sit() { System.out.println("Clank!"); }
    public void oilChange() {}
}

class Communicate {
    public static <T extends Performs>
      void perform(T performer) {
        performer.speak();
        performer.sit();
    }
}

public class DogsAndRobots {
    public static void main(String[] args) {
        Communicate.perform(new PerformingDog());
        Communicate.perform(new Robot());
    }
}
/* Output:
Woof!
Sitting
Click!
Clank!
*/
```

但是要注意，`perform()` 不需要使用泛型来工作，它可以被简单地指定为接受一个 **Performs** 对象：

```java
// generics/SimpleDogsAndRobots.java
// Removing the generic; code still works

class CommunicateSimply {
    static void perform(Performs performer) {
        performer.speak();
        performer.sit();
    }
}

public class SimpleDogsAndRobots {
    public static void main(String[] args) {
        CommunicateSimply.perform(new PerformingDog());
        CommunicateSimply.perform(new Robot());
    }
}
/* Output:
Woof!
Sitting
Click!
Clank!
*/
```

在本例中，泛型不是必需的，因为这些类已经被强制要求实现 **Performs** 接口。

<!-- Compensating for the Lack of (Direct) Latent -->

## 对缺乏潜在类型机制的补偿

尽管 Java 不直接支持潜在类型机制，但是这并不意味着泛型代码不能在不同的类型层次结构之间应用。也就是说，我们仍旧可以创建真正的泛型代码，但是这需要付出一些额外的努力。

### 反射

可以使用的一种方式是反射，下面的 `perform()` 方法就是用了潜在类型机制：

```java
// generics/LatentReflection.java
// Using reflection for latent typing
import java.lang.reflect.*;

// Does not implement Performs:
class Mime {
    public void walkAgainstTheWind() {}
    public void sit() {
        System.out.println("Pretending to sit");
    }
    public void pushInvisibleWalls() {}
    @Override
    public String toString() { return "Mime"; }
}

// Does not implement Performs:
class SmartDog {
    public void speak() { System.out.println("Woof!"); }
    public void sit() { System.out.println("Sitting"); }
    public void reproduce() {}
}

class CommunicateReflectively {
    public static void perform(Object speaker) {
        Class<?> spkr = speaker.getClass();
        try {
            try {
                Method speak = spkr.getMethod("speak");
                speak.invoke(speaker);
            } catch(NoSuchMethodException e) {
                System.out.println(speaker + " cannot speak");
            }
            try {
                Method sit = spkr.getMethod("sit");
                sit.invoke(speaker);
            } catch(NoSuchMethodException e) {
                System.out.println(speaker + " cannot sit");
            }
        } catch(SecurityException |
            IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException e) {
            throw new RuntimeException(speaker.toString(), e);
        }
    }
}

public class LatentReflection {
    public static void main(String[] args) {
        CommunicateReflectively.perform(new SmartDog());
        CommunicateReflectively.perform(new Robot());
        CommunicateReflectively.perform(new Mime());
    }
}
/* Output:
Woof!
Sitting
Click!
Clank!
Mime cannot speak
Pretending to sit
*/
```

上例中，这些类完全是彼此分离的，没有任何公共基类（除了 **Object** ）或接口。通过反射, `CommunicateReflectively.perform()` 能够动态地确定所需要的方法是否可用并调用它们。它甚至能够处理 **Mime** 只具有一个必需的方法这一事实，并能够部分实现其目标。

### 将一个方法应用于序列

反射提供了一些有用的可能性，但是它将所有的类型检查都转移到了运行时，因此在许多情况下并不是我们所希望的。如果能够实现编译期类型检查，这通常会更符合要求。但是有可能实现编译期类型检查和潜在类型机制吗？

让我们看一个说明这个问题的示例。假设想要创建一个 `apply()` 方法，它能够将任何方法应用于某个序列中的所有对象。这种情况下使用接口不适合，因为你想要将任何方法应用于一个对象集合，而接口不可能描述任何方法。如何用 Java 来实现这个需求呢？

最初，我们可以用反射来解决这个问题，由于有了 Java 的可变参数，这种方式被证明是相当优雅的：

```java
// generics/Apply.java

import java.lang.reflect.*;
import java.util.*;

public class Apply {
    public static <T, S extends Iterable<T>>
      void apply(S seq, Method f, Object... args) {
        try {
            for(T t: seq)
                f.invoke(t, args);
        } catch(IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException e) {
            // Failures are programmer errors
            throw new RuntimeException(e);
        }
    }
}
```

在 **Apply.java** 中，异常被转换为 **RuntimeException** ，因为没有多少办法可以从这种异常中恢复——在这种情况下，它们实际上代表着程序员的错误。

为什么我们不只使用 Java 8 方法参考（稍后显示）而不是反射方法 **f** ？ 注意，`invoke()` 和 `apply()` 的优点是它们可以接受任意数量的参数。 在某些情况下，灵活性可能至关重要。

为了测试 **Apply** ，我们首先创建一个 **Shape** 类：

```java
// generics/Shape.java

public class Shape {
    private static long counter = 0;
    private final long id = counter++;
    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + id;
    }
    public void rotate() {
        System.out.println(this + " rotate");
    }
    public void resize(int newSize) {
        System.out.println(this + " resize " + newSize);
    }
}
```

被一个子类 **Square** 继承：

```java
// generics/Square.java

public class Square extends Shape {}
```

通过这些，我们可以测试 **Apply**：

```java
// generics/ApplyTest.java

import java.util.*;
import java.util.function.*;
import onjava.*;

public class ApplyTest {
    public static
    void main(String[] args) throws Exception {
        List<Shape> shapes =
          Suppliers.create(ArrayList::new, Shape::new, 3);
        Apply.apply(shapes, Shape.class.getMethod("rotate"));
        Apply.apply(shapes, Shape.class.getMethod("resize", int.class), 7);

        List<Square> squares =
          Suppliers.create(ArrayList::new, Square::new, 3);
        Apply.apply(squares, Shape.class.getMethod("rotate"));
        Apply.apply(squares, Shape.class.getMethod("resize", int.class), 7);

        Apply.apply(new FilledList<>(Shape::new, 3),
          Shape.class.getMethod("rotate"));
        Apply.apply(new FilledList<>(Square::new, 3),
          Shape.class.getMethod("rotate"));

        SimpleQueue<Shape> shapeQ = Suppliers.fill(
          new SimpleQueue<>(), SimpleQueue::add,
          Shape::new, 3);
        Suppliers.fill(shapeQ, SimpleQueue::add,
          Square::new, 3);
        Apply.apply(shapeQ, Shape.class.getMethod("rotate"));
    }
}
/* Output:
Shape 0 rotate
Shape 1 rotate
Shape 2 rotate
Shape 0 resize 7
Shape 1 resize 7
Shape 2 resize 7
Square 3 rotate
Square 4 rotate
Square 5 rotate
Square 3 resize 7
Square 4 resize 7
Square 5 resize 7
Shape 6 rotate
Shape 7 rotate
Shape 8 rotate
Square 9 rotate
Square 10 rotate
Square 11 rotate
Shape 12 rotate
Shape 13 rotate
Shape 14 rotate
Square 15 rotate
Square 16 rotate
Square 17 rotate
*/
```

在 **Apply** 中，我们运气很好，因为碰巧在 Java 中内建了一个由 Java 集合类库使用的 **Iterable** 接口。正由于此， `apply()` 方法可以接受任何实现了 **Iterable** 接口的事物，包括诸如 **List** 这样的所有 **Collection** 类。但是它还可以接受其他任何事物，只要能够使这些事物是 **Iterable** 的——例如，在 `main()` 中使用下面定义的 **SimpleQueue** 类：

```java
// generics/SimpleQueue.java

// A different kind of Iterable collection
import java.util.*;

public class SimpleQueue<T> implements Iterable<T> {
    private LinkedList<T> storage = new LinkedList<>();
    public void add(T t) { storage.offer(t); }
    public T get() { return storage.poll(); }
    @Override
    public Iterator<T> iterator() {
        return storage.iterator();
    }
}
```

正如反射解决方案看起来那样优雅，我们必须观察到反射（尽管在 Java 的最新版本中得到了显着改进）通常比非反射实现要慢，因为在运行时发生了很多事情。 但它不应阻止您尝试这种解决方案，这依然是值得考虑的一点。

几乎可以肯定，你会首先使用 Java 8 的函数式方法，并且只有在解决了特殊需求时才诉诸反射。 这里对 **ApplyTest.java** 进行了重写，以利用 Java 8 的流和函数工具：

```java
// generics/ApplyFunctional.java

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import onjava.*;

public class ApplyFunctional {
    public static void main(String[] args) {
        Stream.of(
          Stream.generate(Shape::new).limit(2),
          Stream.generate(Square::new).limit(2))
        .flatMap(c -> c) // flatten into one stream
        .peek(Shape::rotate)
        .forEach(s -> s.resize(7));

        new FilledList<>(Shape::new, 2)
          .forEach(Shape::rotate);
        new FilledList<>(Square::new, 2)
          .forEach(Shape::rotate);

        SimpleQueue<Shape> shapeQ = Suppliers.fill(
          new SimpleQueue<>(), SimpleQueue::add,
          Shape::new, 2);
        Suppliers.fill(shapeQ, SimpleQueue::add,
          Square::new, 2);
        shapeQ.forEach(Shape::rotate);
    }
}
/* Output:
Shape 0 rotate
Shape 0 resize 7
Shape 1 rotate
Shape 1 resize 7
Square 2 rotate
Square 2 resize 7
Square 3 rotate
Square 3 resize 7
Shape 4 rotate
Shape 5 rotate
Square 6 rotate
Square 7 rotate
Shape 8 rotate
Shape 9 rotate
Square 10 rotate
Square 11 rotate
*/
```

由于使用 Java 8，因此不需要 `Apply.apply()` 。

我们首先生成两个 **Stream** ： 一个是 **Shape** ，一个是 **Square** ，并将它们展平为单个流。 尽管 Java 缺少功能语言中经常出现的 `flatten()` ，但是我们可以使用 `flatMap(c-> c)` 产生相同的结果，后者使用身份映射将操作简化为“  **flatten** ”。

我们使用 `peek()` 当做对 `rotate()` 的调用，因为 `peek()` 执行一个操作（此处是出于副作用），并在未更改的情况下传递对象。

注意，使用 **FilledList** 和 **shapeQ** 调用 `forEach()` 比 `Apply.apply()` 代码整洁得多。 在代码简单性和可读性方面，结果比以前的方法好得多。 并且，现在也不可能从  `main()` 引发异常。

<!-- Assisted Latent Typing in Java 8 -->

## Java8 中的辅助潜在类型

先前声明关于 Java 缺乏对潜在类型的支持在 Java 8 之前是完全正确的。但是，Java 8 中的非绑定方法引用使我们能够产生一种潜在类型的形式，以满足创建一段可工作在不相干类型上的代码。因为 Java 最初并不是如此设计，所以结果可想而知，比其他语言中要尴尬一些。但是，至少现在成为了可能，只是缺乏令人惊艳之处。

我在其他地方从没遇过这种技术，因此我将其称为辅助潜在类型。

我们将重写 **DogsAndRobots.java** 来演示该技术。 为使外观看起来与原始示例尽可能相似，我仅向每个原始类名添加了 **A**：

```java
// generics/DogsAndRobotMethodReferences.java

// "Assisted Latent Typing"
import typeinfo.pets.*;
import java.util.function.*;

class PerformingDogA extends Dog {
    public void speak() { System.out.println("Woof!"); }
    public void sit() { System.out.println("Sitting"); }
    public void reproduce() {}
}

class RobotA {
    public void speak() { System.out.println("Click!"); }
    public void sit() { System.out.println("Clank!"); }
    public void oilChange() {}
}

class CommunicateA {
    public static <P> void perform(P performer,
      Consumer<P> action1, Consumer<P> action2) {
        action1.accept(performer);
        action2.accept(performer);
    }
}

public class DogsAndRobotMethodReferences {
    public static void main(String[] args) {
        CommunicateA.perform(new PerformingDogA(),
          PerformingDogA::speak, PerformingDogA::sit);
        CommunicateA.perform(new RobotA(),
          RobotA::speak, RobotA::sit);
        CommunicateA.perform(new Mime(),
          Mime::walkAgainstTheWind,
          Mime::pushInvisibleWalls);
    }
}
/* Output:
Woof!
Sitting
Click!
Clank!
*/
```

**PerformingDogA** 和 **RobotA** 与 **DogsAndRobots.java** 中的相同，不同之处在于它们不继承通用接口 **Performs** ，因此它们没有通用性。

`CommunicateA.perform()` 在没有约束的 **P** 上生成。 只要可以使用 `Consumer <P>`，它在这里就可以是任何东西，这些 `Consumer<P>` 代表不带参数的 **P** 方法的未绑定方法引用。当您调用 **Consumer**  的 `accept()` 方法时，它将方法引用绑定到执行者对象并调用该方法。 由于 [函数式编程](book/13-Functional-Programming.md) 一章中描述的“魔术”，我们可以将任何符合签名的未绑定方法引用传递给 `CommunicateA.perform()` 。

之所以称其为“辅助”，是因为您必须显式地为 `perform()` 提供要使用的方法引用。 它不能只按名称调用方法。

尽管传递未绑定的方法引用似乎要花很多力气，但潜在类型的最终目标还是可以实现的。 我们创建了一个代码片段 `CommunicateA.perform()` ，该代码可用于任何具有符合签名的方法引用的类型。 请注意，这与我们看到的其他语言中的潜在类型有所不同，因为这些语言不仅需要签名以符合规范，还需要方法名称。 因此，该技术可以说产生了更多的通用代码。

为了证明这一点，我还从 **LatentReflection.java** 中引入了 **Mime**。

### 使用**Suppliers**类的通用方法

通过辅助潜在类型，我们可以定义本章其他部分中使用的 **Suppliers** 类。 此类包含使用生成器填充 **Collection** 的工具方法。 泛化这些操作很有意义：

```java
// onjava/Suppliers.java

// A utility to use with Suppliers
package onjava;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Suppliers {
    // Create a collection and fill it:
    public static <T, C extends Collection<T>> C
      create(Supplier<C> factory, Supplier<T> gen, int n) {
        return Stream.generate(gen)
            .limit(n)
            .collect(factory, C::add, C::addAll);
    }
    
    // Fill an existing collection:
    public static <T, C extends Collection<T>>
      C fill(C coll, Supplier<T> gen, int n) {
        Stream.generate(gen)
            .limit(n)
            .forEach(coll::add);
        return coll;
    }
    
    // Use an unbound method reference to
    // produce a more general method:
    public static <H, A> H fill(H holder,
      BiConsumer<H, A> adder, Supplier<A> gen, int n) {
        Stream.generate(gen)
            .limit(n)
            .forEach(a -> adder.accept(holder, a));
        return holder;
    }
}
```

`create()` 为你创建一个新的 **Collection** 子类型，而 `fill()` 的第一个版本将元素放入 **Collection** 的现有子类型中。 请注意，还会返回传入的容器的确切类型，因此不会丢失类型信息。

前两种方法一般都受约束，只能与 **Collection** 子类型一起使用。`fill()` 的第二个版本适用于任何类型的 **holder** 。 它需要一个附加参数：未绑定方法引用 `adder. fill()` ，使用辅助潜在类型来使其与任何具有添加元素方法的 **holder** 类型一起使用。因为此未绑定方法 **adder** 必须带有一个参数（要添加到 **holder** 的元素），所以 **adder** 必须是 `BiConsumer <H，A>` ，其中 **H** 是要绑定到的 **holder** 对象的类型，而 **A** 是要被添加的绑定元素类型。 对 `accept()` 的调用将使用参数 a 调用对象 **holder** 上的未绑定方法 **holder**。

在一个稍作模拟的测试中对 **Suppliers** 工具程序进行了测试，该仿真还使用了本章前面定义的 **RandomList** ：

```java
// generics/BankTeller.java

// A very simple bank teller simulation
import java.util.*;
import onjava.*;

class Customer {
    private static long counter = 1;
    private final long id = counter++;
    @Override
    public String toString() {
        return "Customer " + id;
    }
}

class Teller {
    private static long counter = 1;
    private final long id = counter++;
    @Override
    public String toString() {
        return "Teller " + id;
    }
}

class Bank {
    private List<BankTeller> tellers =
        new ArrayList<>();
    public void put(BankTeller bt) {
        tellers.add(bt);
    }
}

public class BankTeller {
    public static void serve(Teller t, Customer c) {
        System.out.println(t + " serves " + c);
    }
    public static void main(String[] args) {
        // Demonstrate create():
        RandomList<Teller> tellers =
            Suppliers.create(
            RandomList::new, Teller::new, 4);
        // Demonstrate fill():
        List<Customer> customers = Suppliers.fill(
            new ArrayList<>(), Customer::new, 12);
        customers.forEach(c ->
            serve(tellers.select(), c));
        // Demonstrate assisted latent typing:
        Bank bank = Suppliers.fill(
            new Bank(), Bank::put, BankTeller::new, 3);
        // Can also use second version of fill():
        List<Customer> customers2 = Suppliers.fill(
            new ArrayList<>(),
            List::add, Customer::new, 12);
    }
}
/* Output:
Teller 3 serves Customer 1
Teller 2 serves Customer 2
Teller 3 serves Customer 3
Teller 1 serves Customer 4
Teller 1 serves Customer 5
Teller 3 serves Customer 6
Teller 1 serves Customer 7
Teller 2 serves Customer 8
Teller 3 serves Customer 9
Teller 3 serves Customer 10
Teller 2 serves Customer 11
Teller 4 serves Customer 12
*/
```

可以看到 `create()` 生成一个新的 **Collection** 对象，而 `fill()` 添加到现有 **Collection** 中。第二个版本`fill()` 显示，它不仅与无关的新类型 **Bank** 一起使用，还能与 **List** 一起使用。因此，从技术上讲，`fill()` 的第一个版本在技术上不是必需的，但在使用 **Collection** 时提供了较短的语法。

<!-- Summary: Is Casting Really So Bad? -->

## 总结：类型转换真的如此之糟吗？

自从 C++ 模版出现以来，我就一直在致力于解释它，我可能比大多数人都更早地提出了下面的论点。直到最近，我才停下来，去思考这个论点到底在多少时间内是有效的——我将要描述的问题到底有多少次可以穿越障碍得以解决。

这个论点就是：使用泛型类型机制的最吸引人的地方，就是在使用集合类的地方，这些类包括诸如各种 **List** 、各种 **Set** 、各种 **Map** 等你在 [集合](book/12-Collections.md) 和 [附录：集合主题](book/Appendix-Collection-Topics.md) 这两章所见。在 Java 5 之前，当你将一个对象放置到集合中时，这个对象就会被向上转型为 **Object** ，因此你会丢失类型信息。当你想要将这个对象从集合中取回，用它去执行某些操作时，必须将其向下转型回正确的类型。我用的示例是持有 **Cat** 的 **List** （这个示例的一种使用苹果和桔子的变体在 [集合](book/12-Collections.md) 章节的开头展示过）。如果没有 Java 5 泛型版本的集合，你放到容集里和从集合中取回的都是 **Object** 。因此，我们很可能会将一个 **Dog** 放置到 **Cat** 的 **List** 中。

但是，泛型出现之前的 Java 并不会让你误用放入到集合中的对象。如果将一个 **Dog** 扔到 **Cat** 的集合中，并且试图将这个集合中的所有东西都当作 **Cat** 处理，那么当你从这个 **Cat** 集合中取回那个 **Dog** 引用，并试图将其转型为 **Cat** 时，就会得到一个 **RuntimeException** 。你仍旧可以发现问题，但是是在运行时而非编译期发现它的。

在本书以前的版本中，我曾经说过：

> 这不止令人恼火，它还可能会产生难以发现的缺陷。如果这个程序的某个部分（或数个部分）向集合中插入了对象，并且通过异常，你在程序的另一个独立的部分中发现有不良对象被放置到了集合中，那么必须发现这个不良插入到底是在何处发生的。
>

但是，随着对这个论点的进一步检查，我开始怀疑它了。首先，这会多么频繁地发生呢？我记得这类事情从未发生在我身上，并且当我在会议上询问其他人时，我也从来没有听说过有人碰上过。另一本书使用了一个称为 **files** 的 list 示例，它包含 **String** 对象。在这个示例中，向 **files** 中添加一个 **File** 对象看起来相当自然，因此这个对象的名字可能叫 **fileNames** 更好。无论 Java 提供了多少类型检查，仍旧可能会写出晦涩的程序，而编写差劲儿的程序即便可以编译，它仍旧是编写差劲儿的程序。可能大多数人都会使用命名良好的集合，例如 **cats** ，因为它们可以向试图添加非 **Cat** 对象的程序员提供可视的警告。并且即便这类事情发生了，它真正又能潜伏多久呢？只要你开始用真实数据来运行测试，就会非常快地看到异常。

有一位作者甚至断言，这样的缺陷将“*潜伏数年*”。但是我不记得有任何大量的相关报告，来说明人们在查找“狗在猫列表中”这类缺陷时困难重重，或者是说明人们会非常频繁地产生这种错误。然而，你将在 [多线程编程](book/24-Concurrent-Programming.md) 章节中看到，在使用线程时，出现那些可能看起来极罕见的缺陷，是很寻常并容易发生的事，而且，对于到底出了什么错，这些缺陷只能给你一个很模糊的概念。因此，对于泛型是添加到 Java 中的非常显著和相当复杂的特性这一点，“狗在猫列表中”这个论据真的能够成为它的理由吗？
我相信被称为*泛型*的通用语言特性（并非必须是其在 Java 中的特定实现）的目的在于可表达性，而不仅仅是为了创建类型安全的集合。类型安全的集合是能够创建更通用代码这一能力所带来的副作用。
因此，即便“狗在猫列表中”这个论据经常被用来证明泛型是必要的，但是它仍旧是有问题的。就像我在本章开头声称的，我不相信这就是泛型这个概念真正的含义。相反，泛型正如其名称所暗示的：它是一种方法，通过它可以编写出更“泛化”的代码，这些代码对于它们能够作用的类型具有更少的限制，因此单个的代码段可以应用到更多的类型上。正如你在本章中看到的，编写真正泛化的“持有器”类（ Java 的容器就是这种类）相当简单，但是编写出能够操作其泛型类型的泛化代码就需要额外的努力了，这些努力需要类创建者和类消费者共同付出，他们必须理解这些代码的概念和实现。这些额外的努力会增加使用这种特性的难度，并可能会因此而使其在某些场合缺乏可应用性，而在这些场合中，它可能会带来附加的价值。

还要注意到，因为泛型是后来添加到 Java 中，而不是从一开始就设计到这种语言中的，所以某些容器无法达到它们应该具备的健壮性。例如，观察一下 **Map** ，在特定的方法 `containsKey(Object key) `和 `get(Object key)` 中就包含这类情况。如果这些类是使用在它们之前就存在的泛型设计的，那么这些方法将会使用参数化类型而不是 **Object** ，因此也就可以提供这些泛型假设会提供的编译期检查。例如，在 C++ 的 **map** 中，键的类型总是在编译期检查的。

有一件事很明显：在一种语言已经被广泛应用之后，在其较新的版本中引入任何种类的泛型机制，都会是一项非常非常棘手的任务，并且是一项不付出艰辛就无法完成的任务。在 C++ 中，模版是在其最初的 ISO 版本中就引入的（即便如此，也引发了阵痛，因为在第一个标准 C++ 出现之前，有很多非模版版本在使用），因此实际上模版一直都是这种语言的一部分。在 Java 中，泛型是在这种语言首次发布大约 10 年之后才引入的，因此向泛型迁移的问题特别多，并且对泛型的设计产生了明显的影响。其结果就是，程序员将承受这些痛苦，而这一切都是由于 Java 设计者在设计 1.0 版本时所表现出来的短视造成的。当 Java 最初被创建时，它的设计者们当然了解 C++ 的模版，他们甚至考虑将其囊括到 Java 语言中，但是出于这样或那样的原因，他们决定将模版排除在外（其迹象就是他们过于匆忙）。因此， Java 语言和使用它的程序员都将承受这些痛苦。只有时间将会说明 Java 的泛型方式对这种语言所造成的最终影响。
某些语言，已经融入了更简洁、影响更小的方式，来实现参数化类型。我们不可能不去想象这样的语言将会成为 Java 的继任者，因为它们采用的方式，与 C++ 通过 C 来实现的方式相同：按原样使用它，然后对其进行改进。

## 进阶阅读

泛型的入门文档是 《Generics in the Java Programming Language》，作者是 Gilad Bracha，可以从 http://java.oracle.com 获取。

Angelika Langer 的《Java Generics FAQs》是一份非常有帮助的资料，可以从 http://www.angelikalanger.com/GenericsFAQ/JavaGenericsFAQ.html 获取。

你可以从 《Adding Wildcards to the Java Programming Language》中学到更多关于通配符的知识，作者是 Torgerson、Ernst、Hansen、von der Ahe、Bracha 和 Gafter，地址是 http://www.jot.fm/issues/issue_2004_12/article5。

Neal After 对于 Java 问题（尤其是擦除）的看法可以从这里找到：http://www.infoq.com/articles/neal-gafter-on-java。

[^1]: 在编写本章期间，Angelika Langer的 Java 泛型常见问题解答以及她的其他著作（与Klaus Kreft一起）是非常宝贵的。
[^2]: [http://gafter.blogspot.com/2004/09/puzzling-through-erasureanswer.html](http://gafter.blogspot.com/2004/09/puzzling-through-erasureanswer.html)
[^3]: 参见本章章末引文。
[^4]: 注意，一些编程环境，如 Eclipse 和 IntelliJ IDEA，将会自动生成委托代码。
[^5]: 因为可以使用转型，有效地禁止了类型系统，一些人就认为 C++ 是弱类型，但这太极端了。一种可能更好的说法是 C++ 是有一道暗门的强类型语言。
[^6]: 我再次从 Brian Goetz 那获得帮助。

<!-- 分页 -->

<div style="page-break-after: always;"></div>
