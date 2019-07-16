[TOC]



<!-- Appendix: Understanding equals() and hashCode() -->
# 附录:理解equals和hashCode方法
假设有一个容器使用hash函数，当你创建一个放到这个容器时，你必须定义 **hashCode()** 函数和 **equals()** 函数。这两个函数一起被用于hash容器中的查询操作。


<!-- A Canonical equals() -->
## equals规范
当你创建一个类的时候，它自动继承自 **Objcet** 类。如果你不覆写 **equals()** ，你将会获得 **Objcet** 对象的 **equals()** 函数。默认情况下，这个函数会比较对象的地址。所以只有你在比较同一个对象的时候，你才会获得**true**。默认的情况是"区分度最高的"。

```java
// equalshashcode/DefaultComparison.java
class DefaultComparison {
    private int i, j, k;
    DefaultComparison(int i, int j, int k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }
    
    public static void main(String[] args) {
        DefaultComparison 
        a = new DefaultComparison(1, 2, 3),
        b = new DefaultComparison(1, 2, 3);
        System.out.println(a == a);
        System.out.println(a == b);
    } 
} 
/*
Output:
true
false
*/

```
通常你会希望放宽这个限制。一般来说如果两个对象有相同的类型和相同的字段，你会认为这两个对象相等，但也会有一些你不想加入 **equals()** 函数中来比较的字段。这是类型设计的一部分。

一个合适的 **equals()**函数必须满足以下五点条件：
1. 反身性：对于任何 **x**， **x.equals(x)** 应该返回 **true**。
2. 对称性：对于任何 **x** 和 **y**， **x.equals(y)** 应该返回 **true**当且仅当 **y.equals(x)** 返回 **true** 。
3. 传递性：对于任何**x**,**y**,还有**z**，如果 **x.equals(y)** 返回 **true** 并且 **y.equals(z)** 返回 **true**，那么  **x.equals(z)** 应该返回 **true**。
4. 一致性：对于任何 **x**和**y**，在对象没有被改变的情况下，多次调用 **x.equals(y)** 应该总是返回 **true** 或者**false**。
5. 对于任何非**null**的**x**，**x.equals(null)**应该返回**false**。

下面是满足这些条件的测试，并且判断对象是否和自己相等（我们这里称呼其为**右值**）：
1. 如果**右值**是**null**，那么不相等。
2. 如果**右值**是**this**，那么两个对象相等。
3. 如果**右值**不是同一个类型或者子类，那么两个对象不相等。
4. 如果所有上面的检查通过了，那么你必须决定 **右值** 中的哪些字段是重要的，然后比较这些字段。
Java 7 引入了 **Objects** 类型来帮助这个流程，这样我们能够写出更好的 **equals()** 函数。

下面的例子比较了不同类型的 **Equality**类。为了避免重复的代码，我们使用*工厂函数设计模*式来实现样例。 **EqualityFactory**接口提供**make()**函数来生成一个**Equaity**对象，这样不同的**EqualityFactory**能够生成**Equality**不同的子类。

```java
// equalshashcode/EqualityFactory.java
import java.util.*;
interface EqualityFactory {
    Equality make(int i, String s, double d);
}
```
现在我们来定义 **Equality**，它包含三个字段（所有的字段我们认为在比较中都很重要）和一个 **equals()** 函数用来满足上述的四种检查。构造函数展示了它的类名来保证我们在执行我们想要的测试：

```java
// equalshashcode/Equality.java
import java.util.*;
public class Equality {
    protected int i;
    protected String s;
    protected double d;public Equality(int i, String s, double d) {
        this.i = i;
        this.s = s;
        this.d = d;
        System.out.println("made 'Equality'");
    } 
    
    @Override
    public boolean equals(Object rval) {
        if(rval == null)
            return false;
        if(rval == this)
            return true;
        if(!(rval instanceof Equality))
            return false;
        Equality other = (Equality)rval;
        if(!Objects.equals(i, other.i))
            return false;
        if(!Objects.equals(s, other.s))
            return false;
        if(!Objects.equals(d, other.d))return false;
            return true;
    } 
    
    public void test(String descr, String expected, Object rval) {
        System.out.format("-- Testing %s --%n" + "%s instanceof Equality: %s%n" +
        "Expected %s, got %s%n",
        descr, descr, rval instanceof Equality,
        expected, equals(rval));
    } 
    
    public static void testAll(EqualityFactory eqf) {
        Equality
        e = eqf.make(1, "Monty", 3.14),
        eq = eqf.make(1, "Monty", 3.14),
        neq = eqf.make(99, "Bob", 1.618);
        e.test("null", "false", null);
        e.test("same object", "true", e);
        e.test("different type",
        "false", Integer.valueOf(99));e.test("same values", "true", eq);
        e.test("different values", "false", neq);
    } 
    
    public static void main(String[] args) {
        testAll( (i, s, d) -> new Equality(i, s, d));
    } 
    
} 
/*
Output:
made 'Equality'
made 'Equality'
made 'Equality'
-- Testing null --
null instanceof Equality: false
Expected false, got false
-- Testing same object --
same object instanceof Equality: true
Expected true, got true
-- Testing different type --
different type instanceof Equality: false
Expected false, got false-- Testing same values --
same values instanceof Equality: true
Expected true, got true
-- Testing different values --
different values instanceof Equality: true
Expected false, got false
*/
```

 **testAll()** 执行了我们期望的所有不同类型对象的比较。它使用工厂创建了**Equality**对象。

在 **main()** 里，请注意对 **testAll()** 的调用很简单。因为**EqualityFactory**有着单一的函数，它能够和lambda表达式一起使用来表示**make()**函数。

上述的 **equals()** 函数非常繁琐，并且我们能够将其简化成规范的形式，请注意：
1. **instanceof**检查减少了**null**检查的需要。
2. 和**this**的比较是多余的。一个正确书写的 **equals()** 函数能正确地和自己比较。


因为 **&&** 是一个短路比较，它会在第一次遇到失败的时候退出并返回**false**。所以，通过使用 **&&** 将检查链接起来，我们可以写出更精简的 **equals()** 函数：

```java
// equalshashcode/SuccinctEquality.java
import java.util.*;
public class SuccinctEquality extends Equality {
    public SuccinctEquality(int i, String s, double d) {
        super(i, s, d);
        System.out.println("made 'SuccinctEquality'");
    } 
    
    @Override
    public boolean equals(Object rval) {
        return rval instanceof SuccinctEquality &&
        Objects.equals(i, ((SuccinctEquality)rval).i) &&
        Objects.equals(s, ((SuccinctEquality)rval).s) &&
        Objects.equals(d, ((SuccinctEquality)rval).d);
    } 
    public static void main(String[] args) {
        Equality.testAll( (i, s, d) ->
        new SuccinctEquality(i, s, d));
    } 
    
}
/* Output:
made 'Equality'
made 'SuccinctEquality'
made 'Equality'
made 'SuccinctEquality'
made 'Equality'
made 'SuccinctEquality'
-- Testing null --
null instanceof Equality: false
Expected false, got false
-- Testing same object --
same object instanceof Equality: true
Expected true, got true
-- Testing different type --
different type instanceof Equality: false
Expected false, got false
-- Testing same values --
same values instanceof Equality: true
Expected true, got true
-- Testing different values --different values instanceof Equality: true
Expected false, got false
*/
```
对于每个 **SuccinctEquality**，基类构造函数在派生类构造函数前被调用，输出显示我们依然获得了正确的结果，你可以发现短路返回已经发生了，不然的话，**null**测试和“不同类型”的测试会在 **equals()** 函数下面的比较中强制转化的时候抛出异常。
 **Objects.equals()** 会在你组合其他类型的时候发挥很大的作用。

```java
// equalshashcode/ComposedEquality.java
import java.util.*;
class Part {
    String ss;
    double dd;
    
    Part(String ss, double dd) {
        this.ss = ss;
        this.dd = dd;
    }
    
    @Override
    public boolean equals(Object rval) {
        return rval instanceof Part &&
        Objects.equals(ss, ((Part)rval).ss) &&
        Objects.equals(dd, ((Part)rval).dd);
    } 
    
} 
    
public class ComposedEquality extends SuccinctEquality {
    Part part;
    public ComposedEquality(int i, String s, double d) {
        super(i, s, d);
        part = new Part(s, d);
        System.out.println("made 'ComposedEquality'");
    }
    @Override
    public boolean equals(Object rval) {
        return rval instanceof ComposedEquality &&
        super.equals(rval) &&
        Objects.equals(part,
        ((ComposedEquality)rval).part);
        
    } 
        
    public static void main(String[] args) {
        Equality.testAll( (i, s, d) ->
        new ComposedEquality(i, s, d));
    }
}
/*
Output:
made 'Equality'
made 'SuccinctEquality'
made 'ComposedEquality'
made 'Equality'
made 'SuccinctEquality'
made 'ComposedEquality'
made 'Equality'
made 'SuccinctEquality'
made 'ComposedEquality'
-- Testing null --null instanceof Equality: false
Expected false, got false
-- Testing same object --
same object instanceof Equality: true
Expected true, got true
-- Testing different type --
different type instanceof Equality: false
Expected false, got false
-- Testing same values --
same values instanceof Equality: true
Expected true, got true
-- Testing different values --
different values instanceof Equality: true
Expected false, got false
*/
```
注意super.equals()这个调用，没有必要重新发明它（因为你不总是有权限访问基类所有的必要字段）

<!--Equality Across Subtypes -->
### 不同子类的相等性
继承意味着两个不同子类的对象当其向上转型的时候可以是相等的。假设你有一个Animal对象的集合。这个集合天然接受**Animal**的子类。在这个例子中是**Dog**和**Pig**。每个**Animal**有一个**name**和**size**，还有唯一的内部**id**数字。

我们通过**Objects**类，以规范的形式定义 **equals()**函数和**hashCode()**。但是我们只能在基类**Animal**中定义他们。并且我们在这两个函数中没有包含**id**字段。从**equals()**函数的角度看待，这意味着我们只关心它是否是**Animal**，而不关心是否是**Animal**的某个子类。

```java
// equalshashcode/SubtypeEquality.java
import java.util.*;
enum Size { SMALL, MEDIUM, LARGE }
class Animal {
    private static int counter = 0;
    private final int id = counter++;
    private final String name;
    private final Size size;
    Animal(String name, Size size) {
        this.name = name;
        this.size = size;
    } 
    @Override
    public boolean equals(Object rval) {
        return rval instanceof Animal &&
        // Objects.equals(id, ((Animal)rval).id) && // [1]
        Objects.equals(name, ((Animal)rval).name) &&
        Objects.equals(size, ((Animal)rval).size);
    } 
    
    @Override
    public int hashCode() {
        return Objects.hash(name, size);
        // return Objects.hash(name, size, id); // [2]
    } 
    
    @Override
    public String toString() {
        return String.format("%s[%d]: %s %s %x",
        getClass().getSimpleName(), id,
        name, size, hashCode());
    } 
} 
    
class Dog extends Animal {
    Dog(String name, Size size) {
    super(name, size);
    } 
} 

class Pig extends Animal {
    Pig(String name, Size size) {
    super(name, size);
    } 
} 
    
public class SubtypeEquality {
    public static void main(String[] args) {
        Set<Animal> pets = new HashSet<>();
        pets.add(new Dog("Ralph", Size.MEDIUM));
        pets.add(new Pig("Ralph", Size.MEDIUM));
        pets.forEach(System.out::println);
    } 
} 
/*
Output:
Dog[0]: Ralph MEDIUM a752aeee
*/
```
如果我们只考虑类型的话，某些情况下它的确说得通——只从基类的角度看待问题，这是李氏替换原则的基石。这个代码完美符合替换理论因为派生类没有添加任何额外不再基类中的额外函数。派生类只是在表现上不同，而不是在接口上。（当然这不是常态）

但是当我们提供了两个有着相同数据的不同的对象类型，然后将他们放置在 **HashSet<Animal>** 中。只有他们中的一个能存活。这强调了 **equals()** 不是完美的数学理论，而只是机械般的理论。
 **hashCode()** 和 **equals()** 必须能够允许类型在hash数据结构中正常工作。例子中 **Dog** 和 **Pig** 会被映射到同 **HashSet** 的同一个桶中。这个时候，**HashSet** 回退到 **equals()** 来区分对象，但是 **equals()** 也认为两个对象是相同的。**HashSet**因为已经有一个相同的对象了，所以没有添加 **Pig**。
我们依然能够通过使得其他字段对象不同来让例子能够正常工作。在这里每个 **Animal** 已经有了一个独一无二的 **id** ，所以你能够取消  **equals()** 函数中的 **[1]** 行注释，或者取消 **hashCode()** 函数中的 **[2]** 行注释。按照规范，你应该同时完成这两个操作，如此能够将所有“不变的”字段包含在两个操作中（“不变”所以 **equals()** 和 **hashCode()** 在哈希数据结构中的排序和取值时，不会生成不同的值。我将“不变的”放在引号中因为你必须计算出是否已经发生变化）。

> **旁注**： 在**hashCode()**中，如果你只能够使用一个字段，使用**Objcets.hashCode()**。如果你使用多个字段，那么使用 **Objects.hash()**。

我们也可以通过标准方式，将 **equals()** 定义在子类中（不包含 **id** ）解决这个问题：

```java
// equalshashcode/SubtypeEquality2.java
import java.util.*;
class Dog2 extends Animal {
    Dog2(String name, Size size) {
        super(name, size);
    } 
    
    @Override
    public boolean equals(Object rval) {
        return rval instanceof Dog2 &&super.equals(rval);
    } 
} 

class Pig2 extends Animal {
    Pig2(String name, Size size) {
    super(name, size);
    } 
    
    @Override
    public boolean equals(Object rval) {
        return rval instanceof Pig2 &&
        super.equals(rval);
    } 
}

public class SubtypeEquality2 {
    public static void main(String[] args) {
        Set<Animal> pets = new HashSet<>();
        pets.add(new Dog2("Ralph", Size.MEDIUM));
        pets.add(new Pig2("Ralph", Size.MEDIUM));
        pets.forEach(System.out::println);
    }
} 
/*
Output:
Dog2[0]: Ralph MEDIUM a752aeee
Pig2[1]: Ralph MEDIUM a752aeee
*/
```
注意 **hashCode()** 是独一无二的，但是因为对象不再 **equals()** ，所以两个函数都出现在**HashSet**中。另外，**super.equals()** 意味着我们不需要访问基类的**private**字段。


一种说法是Java从**equals()** 和**hashCode()** 的定义中分离了可替代性。我们仍然能够将**Dog**和**Pig**放置在 **Set<Animal>** 中，无论 **equals()** 和 **hashCode()** 是如何定义的，但是对象不会在哈希数据结构中正常工作，除非这些函数能够被合理定义。不幸的是，**equals()** 不总是和 **hashCode()** 一起使用，这在你尝试为了某个特殊类型避免定义它的时候会让问题复杂化。并且这也是为什么遵循规范是有价值的。然而这会变得更加复杂，因为你不总是需要定义其中一个函数。



<!-- Hashing and Hash Codes -->
## 哈希和哈希码


<!-- Tuning a HashMap -->
## 调整HashMap

<!-- 分页 -->
<div style="page-break-after: always;"></div>
