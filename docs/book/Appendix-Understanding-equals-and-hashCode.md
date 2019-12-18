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


一种说法是Java从**equals()** 和**hashCode()** 的定义中分离了可替代性。我们仍然能够将**Dog**和**Pig**放置在 **Set\<Animal\>** 中，无论 **equals()** 和 **hashCode()** 是如何定义的，但是对象不会在哈希数据结构中正常工作，除非这些函数能够被合理定义。不幸的是，**equals()** 不总是和 **hashCode()** 一起使用，这在你尝试为了某个特殊类型避免定义它的时候会让问题复杂化。并且这也是为什么遵循规范是有价值的。然而这会变得更加复杂，因为你不总是需要定义其中一个函数。



<!-- Hashing and Hash Codes -->

## 哈希和哈希码

在 [集合]() 章节中，我们使用预先定义的类作为 HashMap 的键。这些示例之所以有用，是因为预定义的类具有所有必需的连线，以使它们正确地充当键。

当创建自己的类作为HashMap的键时，会发生一个常见的陷阱，从而忘记进行必要的接线。例如，考虑一个将Earthhog 对象与 Prediction 对象匹配的天气预报系统。这似乎很简单：使用Groundhog作为键，使用Prediction作为值：

```java
// equalshashcode/Groundhog.java
// Looks plausible, but doesn't work as a HashMap key
public class Groundhog {
    protected int number;
    public Groundhog(int n) { number = n; }
    @Override
    public String toString() {
        return "Groundhog #" + number;
    }
}
```

```java
// equalshashcode/Prediction.java
// Predicting the weather
import java.util.*;
public class Prediction {
    private static Random rand = new Random(47);
    @Override
    public String toString() {
        return rand.nextBoolean() ?
                "Six more weeks of Winter!" : "Early Spring!";
    }
}
```

```java
// equalshashcode/SpringDetector.java
// What will the weather be?
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.lang.reflect.*;
public class SpringDetector {
    public static <T extends Groundhog>
    void detectSpring(Class<T> type) {
        try {
            Constructor<T> ghog =
                    type.getConstructor(int.class);
            Map<Groundhog, Prediction> map =
                    IntStream.range(0, 10)
                            .mapToObj(i -> {
                                try {
                                    return ghog.newInstance(i);
                                } catch(Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toMap(
                                    Function.identity(),
                                    gh -> new Prediction()));
            map.forEach((k, v) ->
                    System.out.println(k + ": " + v));
            Groundhog gh = ghog.newInstance(3);
            System.out.println(
                    "Looking up prediction for " + gh);
            if(map.containsKey(gh))
                System.out.println(map.get(gh));
            else
                System.out.println("Key not found: " + gh);
        } catch(NoSuchMethodException |
                IllegalAccessException |
                InvocationTargetException |
                InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        detectSpring(Groundhog.class);
    }
}
/* Output:
Groundhog #3: Six more weeks of Winter!
Groundhog #0: Early Spring!
Groundhog #8: Six more weeks of Winter!
Groundhog #6: Early Spring!
Groundhog #4: Early Spring!
Groundhog #2: Six more weeks of Winter!
Groundhog #1: Early Spring!
Groundhog #9: Early Spring!
Groundhog #5: Six more weeks of Winter!
Groundhog #7: Six more weeks of Winter!
Looking up prediction for Groundhog #3
Key not found: Groundhog #3
*/
```

每个 Groundhog 都被赋予了一个常数，因此你可以通过如下的方式在 HashMap 中寻找对应的 Prediction。“给我一个和 Groundhog#3 相关联的 Prediction”。而 Prediction 通过一个随机生成的 boolean 来选择天气。`detectSpring()` 方法通过反射来实例化 Groundhog 类，或者它的子类。稍后，当我们继承一种新型的“Groundhog ”以解决此处演示的问题时，这将派上用场。

这里的 HashMap 被 Groundhog  和其相关联的 Prediction 充满。并且上面展示了 HashMap 里面填充的内容。接下来我们使用填充了常数 3 的 Groundhog 作为 key 用于寻找对应的 Prediction 。（这个键值对肯定在 Map 中）。

这看起来十分简单，但是这样做并没有奏效 —— 它无法找到数字3这个键。问题出在Groundhog自动地继承自基类Object，所以这里使用Object的hashCode0方法生成散列码，而它默认是使用对象的地址计算散列码。因此，由Groundhog(3)生成的第一个实例的散列码与由Groundhog(3)生成的第二个实例的散列码是不同的，而我们正是使用后者进行查找的。

我们需要恰当的重写hashCode()方法。但是它仍然无法正常运行，除非你同时重写 equals()方法，它也是Object的一部分。HashMap使用equals()判断当前的键是否与表中存在的键相同。

这是因为默认的Object.equals()只是比较对象的地址，所以一个Groundhog(3)并不等于另一个Groundhog(3)，因此，如果要使用自己的类作为HashMap的键，必须同时重载hashCode()和equals()，如下所示：

```java
// equalshashcode/Groundhog2.java
// A class that's used as a key in a HashMap
// must override hashCode() and equals()
import java.util.*;
public class Groundhog2 extends Groundhog {
    public Groundhog2(int n) { super(n); }
    @Override
    public int hashCode() { return number; }
    @Override
    public boolean equals(Object o) {
        return o instanceof Groundhog2 &&
                Objects.equals(
                        number, ((Groundhog2)o).number);
    }
}
```

```java
// equalshashcode/SpringDetector2.java
// A working key
public class SpringDetector2 {
    public static void main(String[] args) {
        SpringDetector.detectSpring(Groundhog2.class);
    }
}
/* Output:
Groundhog #0: Six more weeks of Winter!
Groundhog #1: Early Spring!
Groundhog #2: Six more weeks of Winter!
Groundhog #3: Early Spring!
Groundhog #4: Early Spring!
Groundhog #5: Six more weeks of Winter!
Groundhog #6: Early Spring!
Groundhog #7: Early Spring!
Groundhog #8: Six more weeks of Winter!
Groundhog #9: Six more weeks of Winter!
Looking up prediction for Groundhog #3
Early Spring!
*/
```

Groundhog2.hashCode0返回Groundhog的标识数字（编号）作为散列码。在此例中，程序员负责确保不同的Groundhog具有不同的编号。hashCode()并不需要总是能够返回唯一的标识码（稍后你会理解其原因），但是equals() 方法必须严格地判断两个对象是否相同。此处的equals()是判断Groundhog的号码，所以作为HashMap中的键，如果两个Groundhog2对象具有相同的Groundhog编号，程序就出错了。

如何定义 equals() 方法在上一节 [equals 规范]()中提到了。输出表明我们现在的输出是正确的。

### 理解 hashCode

前面的例子只是正确解决问题的第一步。它只说明，如果不为你的键覆盖hashCode() 和equals() ，那么使用散列的数据结构（HashSet，HashMap，LinkedHashst或LinkedHashMap）就无法正确处理你的键。然而，要很好地解决此问题，你必须了解这些数据结构的内部构造。

首先，使用散列的目的在于：想要使用一个对象来查找另一个对象。不过使用TreeMap或者你自己实现的Map也可以达到此目的。与散列实现相反，下面的示例用一对ArrayLists实现了一个Map，与AssociativeArray.java不同，这其中包含了Map接口的完整实现，因此提供了entrySet()方法：

```java
// equalshashcode/SlowMap.java
// A Map implemented with ArrayLists
import java.util.*;
import onjava.*;
public class SlowMap<K, V> extends AbstractMap<K, V> {
    private List<K> keys = new ArrayList<>();
    private List<V> values = new ArrayList<>();
    @Override
    public V put(K key, V value) {
        V oldValue = get(key); // The old value or null
        if(!keys.contains(key)) {
            keys.add(key);
            values.add(value);
        } else
            values.set(keys.indexOf(key), value);
        return oldValue;
    }
    @Override
    public V get(Object key) { // key: type Object, not K
        if(!keys.contains(key))
            return null;
        return values.get(keys.indexOf(key));
    }
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set= new HashSet<>();
        Iterator<K> ki = keys.iterator();
        Iterator<V> vi = values.iterator();
        while(ki.hasNext())
            set.add(new MapEntry<>(ki.next(), vi.next()));
        return set;
    }
    public static void main(String[] args) {
        SlowMap<String,String> m= new SlowMap<>();
        m.putAll(Countries.capitals(8));
        m.forEach((k, v) ->
                System.out.println(k + "=" + v));
        System.out.println(m.get("BENIN"));
        m.entrySet().forEach(System.out::println);
    }
}
/* Output:
CAMEROON=Yaounde
ANGOLA=Luanda
BURKINA FASO=Ouagadougou
BURUNDI=Bujumbura
ALGERIA=Algiers
BENIN=Porto-Novo
CAPE VERDE=Praia
BOTSWANA=Gaberone
Porto-Novo
CAMEROON=Yaounde
ANGOLA=Luanda
BURKINA FASO=Ouagadougou
BURUNDI=Bujumbura
ALGERIA=Algiers
BENIN=Porto-Novo
CAPE VERDE=Praia
BOTSWANA=Gaberone
*/
```

put()方法只是将键与值放入相应的ArrayList。为了与Map接口保持一致，它必须返回旧的键，或者在没有任何旧键的情况下返回null。

同样遵循了Map规范，get()会在键不在SlowMap中的时候产生null。如果键存在，它将被用来查找表示它在keys列表中的位置的数值型索引，并且这个数字被用作索引来产生与values列表相关联的值。注意，在get()中key的类型是Object，而不是你所期望的参数化类型K（并且是在AssociativeArrayjava中真正使用的类型），这是将泛型注入到Java语言中的时刻如此之晚所导致的结果-如果泛型是Java语言最初就具备的属性，那么get()就可以执行其参数的类型。

Map.entrySet() 方法必须产生一个Map.Entry对象集。但是，Map.Entry是一个接口，用来描述依赖于实现的结构，因此如果你想要创建自己的Map类型，就必须同时定义Map.Entry的实现：

```java
// equalshashcode/MapEntry.java
// A simple Map.Entry for sample Map implementations
import java.util.*;
public class MapEntry<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;
    public MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }
    @Override
    public K getKey() { return key; }
    @Override
    public V getValue() { return value; }
    @Override
    public V setValue(V v) {
        V result = value;
        value = v;
        return result;
    }
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object rval) {
        return rval instanceof MapEntry &&
                Objects.equals(key,
                        ((MapEntry<K, V>)rval).getKey()) &&
                Objects.equals(value,
                        ((MapEntry<K, V>)rval).getValue());
    }
    @Override
    public String toString() {
        return key + "=" + value;
    }
}
```

这里 equals 方法的实现遵循了[equals 规范]()。在 Objects 类中有一个非常熟悉的方法可以帮助创建 hashCode() 方法： Objects.hash()。当你定义含有超过一个属性的对象的 `hashCode()` 时，你可以使用这个方法。如果你的对象只有一个属性，可以直接使用 ` Objects.hashCode()`。

尽管这个解决方案非常简单，并且看起来在SlowMap.main() 的琐碎测试中可以正常工作，但是这并不是一个恰当的实现，因为它创建了键和值的副本。entrySet()  的恰当实现应该在Map中提供视图，而不是副本，并且这个视图允许对原始映射表进行修改（副本就不行）。

### 为了速度而散列

SlowMap.java 说明了创建一种新的Map并不困难。但是正如它的名称SlowMap所示，它不会很快，所以如果有更好的选择，就应该放弃它。它的问题在于对键的查询，键没有按照任何特定顺序保存，所以只能使用简单的线性查询，而线性查询是最慢的查询方式。

散列的价值在于速度：散列使得查询得以快速进行。由于瓶颈位于键的查询速度，因此解决方案之一就是保持键的排序状态，然后使用Collections.binarySearch()进行查询。

散列则更进一步，它将键保存在某处，以便能够很快找到。存储一组元素最快的数据结构是数组，所以使用它来表示键的信息（请小心留意，我是说键的信息，而不是键本身）。但是因为数组不能调整容量，因此就有一个问题：我们希望在Map中保存数量不确定的值，但是如果键的数量被数组的容量限制了，该怎么办呢？

答案就是：数组并不保存键本身。而是通过键对象生成一个数字，将其作为数组的下标。这个数字就是散列码，由定义在Object中的、且可能由你的类覆盖的hashCode()方法（在计算机科学的术语中称为散列函数）生成。

于是查询一个值的过程首先就是计算散列码，然后使用散列码查询数组。如果能够保证没有冲突（如果值的数量是固定的，那么就有可能），那可就有了一个完美的散列函数，但是这种情况只是特例。。通常，冲突由外部链接处理：数组并不直接保存值，而是保存值的 list。然后对 list中的值使用equals()方法进行线性的查询。这部分的查询自然会比较慢，但是，如果散列函数好的话，数组的每个位置就只有较少的值。因此，不是查询整个list，而是快速地跳到数组的某个位置，只对很少的元素进行比较。这便是HashMap会如此快的原因。

理解了散列的原理，我们就能够实现一个简单的散列Map了：

```java
// equalshashcode/SimpleHashMap.java
// A demonstration hashed Map
import java.util.*;
import onjava.*;
public
class SimpleHashMap<K, V> extends AbstractMap<K, V> {
    // Choose a prime number for the hash table
// size, to achieve a uniform distribution:
    static final int SIZE = 997;
    // You can't have a physical array of generics,
// but you can upcast to one:
    @SuppressWarnings("unchecked")
    LinkedList<MapEntry<K, V>>[] buckets =
            new LinkedList[SIZE];
    @Override
    public V put(K key, V value) {
        V oldValue = null;
        int index = Math.abs(key.hashCode()) % SIZE;
        if(buckets[index] == null)
            buckets[index] = new LinkedList<>();
        LinkedList<MapEntry<K, V>> bucket = buckets[index];
        MapEntry<K, V> pair = new MapEntry<>(key, value);
        boolean found = false;
        ListIterator<MapEntry<K, V>> it =
                bucket.listIterator();
        while(it.hasNext()) {
            MapEntry<K, V> iPair = it.next();
            if(iPair.getKey().equals(key)) {
                oldValue = iPair.getValue();
                it.set(pair); // Replace old with new
                found = true;
                break;
            }
        }
        if(!found)
            buckets[index].add(pair);
        return oldValue;
    }
    @Override
    public V get(Object key) {
        int index = Math.abs(key.hashCode()) % SIZE;
        if(buckets[index] == null) return null;
        for(MapEntry<K, V> iPair : buckets[index])
            if(iPair.getKey().equals(key))
                return iPair.getValue();
        return null;
    }
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set= new HashSet<>();
        for(LinkedList<MapEntry<K, V>> bucket : buckets) {
            if(bucket == null) continue;
            for(MapEntry<K, V> mpair : bucket)
                set.add(mpair);
        }
        return set;
    }
    public static void main(String[] args) {
        SimpleHashMap<String,String> m =
                new SimpleHashMap<>();
        m.putAll(Countries.capitals(8));
        m.forEach((k, v) ->
                System.out.println(k + "=" + v));
        System.out.println(m.get("BENIN"));
        m.entrySet().forEach(System.out::println);
    }
}
/* Output:
CAMEROON=Yaounde
ANGOLA=Luanda
BURKINA FASO=Ouagadougou
BURUNDI=Bujumbura
ALGERIA=Algiers
BENIN=Porto-Novo
CAPE VERDE=Praia
BOTSWANA=Gaberone
Porto-Novo
CAMEROON=Yaounde
ANGOLA=Luanda
BURKINA FASO=Ouagadougou
BURUNDI=Bujumbura
ALGERIA=Algiers
BENIN=Porto-Novo
CAPE VERDE=Praia
BOTSWANA=Gaberone
*/
```

由于散列表中的“槽位”（slot）通常称为桶位（bucket），因此我们将表示实际散列表的数组命名为bucket，为使散列分布均匀，桶的数量通常使用质数[^2]。注意，为了能够自动处理冲突，使用了一个LinkedList的数组；每一个新的元素只是直接添加到list尾的某个特定桶位中。即使Java不允许你创建泛型数组，那你也可以创建指向这种数组的引用。这里，向上转型为这种数组是很方便的，这样可以防止在后面的代码中进行额外的转型。

对于put()  方法，hashCode() 将针对键而被调用，并且其结果被强制转换为正数。为了使产生的数字适合bucket数组的大小，取模操作符将按照该数组的尺寸取模。如果数组的某个位置是 null，这表示还没有元素被散列至此，所以，为了保存刚散列到该定位的对象，需要创建一个新的LinkedList。一般的过程是，查看当前位置的ist中是否有相同的元素，如果有，则将旧的值赋给oldValue，然后用新的值取代旧的值。标记found用来跟踪是否找到（相同的）旧的键值对，如果没有，则将新的对添加到list的末尾。

get()方法按照与put()方法相同的方式计算在buckets数组中的索引（这很重要，因为这样可以保证两个方法可以计算出相同的位置）如果此位置有LinkedList存在，就对其进行查询。

注意，这个实现并不意味着对性能进行了调优，它只是想要展示散列映射表执行的各种操作。如果你浏览一下java.util.HashMap的源代码，你就会看到一个调过优的实现。同样，为了简单，SimpleHashMap使用了与SlowMap相同的方式来实现entrySet()，这个方法有些过于简单，不能用于通用的Map。

### 重写 hashCode()

在明白了如何散列之后，编写自己的hashCode()就更有意义了。

首先，你无法控制bucket数组的下标值的产生。这个值依赖于具体的HashMap对象的容量，而容量的改变与容器的充满程度和负载因子（本章稍后会介绍这个术语）有关。hashCode()生成的结果，经过处理后成为桶位的下标（在SimpleHashMap中，只是对其取模，模数为bucket数组的大小）。

设计hashCode()时最重要的因素就是：无论何时，对同一个对象调用hashCode()都应该生成同样的值。如果在将一个对象用put()添加进HashMap时产生一个hashCode()值，而用get()取出时却产生了另一个hashCode()值，那么就无法重新取得该对象了。所以，如果你的hashCode()方法依赖于对象中易变的数据，用户就要当心了，因为此数据发生变化时，hashCode()就会生成一个不同的散列码，相当于产生了一个不同的键。

此外，也不应该使hashCode()依赖于具有唯一性的对象信息，尤其是使用this值，这只能产生很糟糕的hashCode()，因为这样做无法生成一个新的键，使之与put()中原始的键值对中的键相同。这正是SpringDetector.java的问题所在，因为它默认的hashCode0使用的是对象的地址。所以，应该使用对象内有意义的识别信息。

下面以String类为例。String有个特点：如果程序中有多个String对象，都包含相同的字符串序列，那么这些String对象都映射到同一块内存区域。所以new String("hello")生成的两个实例，虽然是相互独立的，但是对它们使用hashCode()应该生成同样的结果。通过下面的程序可以看到这种情况：

```java
// equalshashcode/StringHashCode.java
public class StringHashCode {
    public static void main(String[] args) {
        String[] hellos = "Hello Hello".split(" ");
        System.out.println(hellos[0].hashCode());
        System.out.println(hellos[1].hashCode());
    }
}
/* Output:
69609650
69609650
*/
```

对于String而言，hashCode() 明显是基于String的内容的。

因此，要想使hashCode()  实用，它必须速度快，并且必须有意义。也就是说，它必须基于对象的内容生成散列码。记得吗，散列码不必是独一无二的（应该更关注生成速度，而不是唯一性），但是通过 hashCode() 和 equals() ，必须能够完全确定对象的身份。

因为在生成桶的下标前，hashCode()还需要做进一步的处理，所以散列码的生成范围并不重要，只要是int即可。

还有另一个影响因素：好的hashCode() 应该产生分布均匀的散列码。如果散列码都集中在一块，那么HashMap或者HashSet在某些区域的负载会很重，这样就不如分布均匀的散列函数快。

在Effective Java Programming Language Guide（Addison-Wesley 2001）这本书中，Joshua Bloch为怎样写出一份像样的hashCode()给出了基本的指导： 

1. 给int变量result赋予某个非零值常量，例如17。
2. 为对象内每个有意义的字段（即每个可以做equals）操作的字段计算出一个int散列码c：

| 字段类型                                               | 计算公式                                                     |
| ------------------------------------------------------ | ------------------------------------------------------------ |
| boolean                                                | c = (f ? 0 : 1)                                              |
| byte ,  char ,  short , or  int                        | c = (int)f                                                   |
| long                                                   | c = (int)(f ^ (f>>>32))                                      |
| float                                                  | c = Float.floatToIntBits(f);                                 |
| double                                                 | long l =Double.doubleToLongBits(f); <br>c = (int)(l ^ (l >>> 32)) |
| Object , where  equals() calls equals() for this field | c = f.hashCode()                                             |
| Array                                                  | 应用以上规则到每一个元素中                                   |

3. 合并计算得到的散列码： **result = 37 * result + c;​**
4.  返回 result。
5. 检查hashCode()最后生成的结果，确保相同的对象有相同的散列码。

下面便是遵循这些指导的一个例子。提示，你没有必要书写像如下的代码 —— 相反，使用 `Objects.hash()` 去用于散列多字段的对象（如同在本例中的那样），然后使用 `Objects.hashCode()` 如散列单字段的对象。

```java
// equalshashcode/CountedString.java
// Creating a good hashCode()
import java.util.*;
public class CountedString {
    private static List<String> created =
            new ArrayList<>();
    private String s;
    private int id = 0;
    public CountedString(String str) {
        s = str;
        created.add(s);
// id is the total number of instances
// of this String used by CountedString:
        for(String s2 : created)
            if(s2.equals(s))
                id++;
    }
    @Override
    public String toString() {
        return "String: " + s + " id: " + id +
                " hashCode(): " + hashCode();
    }
    @Override
    public int hashCode() {
// The very simple approach:
// return s.hashCode() * id;
// Using Joshua Bloch's recipe:
        int result = 17;
        result = 37 * result + s.hashCode();
        result = 37 * result + id;
        return result;
    }
    @Override
    public boolean equals(Object o) {
        return o instanceof CountedString &&
                Objects.equals(s, ((CountedString)o).s) &&
                Objects.equals(id, ((CountedString)o).id);
    }
    public static void main(String[] args) {
        Map<CountedString,Integer> map = new HashMap<>();
        CountedString[] cs = new CountedString[5];
        for(int i = 0; i < cs.length; i++) {
            cs[i] = new CountedString("hi");
            map.put(cs[i], i); // Autobox int to Integer
        }
        System.out.println(map);
        for(CountedString cstring : cs) {
            System.out.println("Looking up " + cstring);
            System.out.println(map.get(cstring));
        }
    }
}
/* Output:
{String: hi id: 4 hashCode(): 146450=3, String: hi id:
5 hashCode(): 146451=4, String: hi id: 2 hashCode():
146448=1, String: hi id: 3 hashCode(): 146449=2,
String: hi id: 1 hashCode(): 146447=0}
Looking up String: hi id: 1 hashCode(): 146447
0
Looking up String: hi id: 2 hashCode(): 146448
1
Looking up String: hi id: 3 hashCode(): 146449
2
Looking up String: hi id: 4 hashCode(): 146450
3
Looking up String: hi id: 5 hashCode(): 146451
4
*/
```

CountedString由一个String和一个id组成，此id代表包含相同String的CountedString对象的编号。所有的String都被存储在static ArrayList中，在构造器中通过选代遍历此ArrayList完成对id的计算。

hashCode()和equals() 都基于CountedString的这两个字段来生成结果；如果它们只基于String或者只基于id，不同的对象就可能产生相同的值。

在main）中，使用相同的String创建了多个CountedString对象。这说明，虽然String相同，但是由于id不同，所以使得它们的散列码并不相同。在程序中，HashMap被打印了出来，因此可以看到它内部是如何存储元素的（以无法辨别的次序），然后单独查询每一个键，以此证明查询机制工作正常。

作为第二个示例，请考虑Individual类，它被用作[类型信息]()中所定义的typeinfo.pet类库的基类。Individual类在那一章中就用到了，而它的定义则放到了本章，因此你可以正确地理解其实现。

在这里替换了手工去计算 `hashCode()`，我们使用了更合适的方式 ` Objects.hash() `：

```java
// typeinfo/pets/Individual.java
package typeinfo.pets;
import java.util.*;
public class
Individual implements Comparable<Individual> {
    private static long counter = 0;
    private final long id = counter++;
    private String name;
    public Individual(String name) { this.name = name; }
    // 'name' is optional:
    public Individual() {}
    @Override
    public String toString() {
        return getClass().getSimpleName() +
                (name == null ? "" : " " + name);
    }
    public long id() { return id; }
    @Override
    public boolean equals(Object o) {
        return o instanceof Individual &&
                Objects.equals(id, ((Individual)o).id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
    @Override
    public int compareTo(Individual arg) {
        // Compare by class name first:
        String first = getClass().getSimpleName();
        String argFirst = arg.getClass().getSimpleName();
        int firstCompare = first.compareTo(argFirst);
        if(firstCompare != 0)
            return firstCompare;
        if(name != null && arg.name != null) {
            int secondCompare = name.compareTo(arg.name);
            if(secondCompare != 0)
                return secondCompare;
        }
        return (arg.id < id ? -1 : (arg.id == id ? 0 : 1));
    }
}
```

compareTo() 方法有一个比较结构，因此它会产生一个排序序列，排序的规则首先按照实际类型排序，然后如果有名字的话，按照name排序，最后按照创建的顺序排序。下面的示例说明了它是如何工作的：

```java
// equalshashcode/IndividualTest.java
import collections.MapOfList;
import typeinfo.pets.*;
import java.util.*;
public class IndividualTest {
    public static void main(String[] args) {
        Set<Individual> pets = new TreeSet<>();
        for(List<? extends Pet> lp :
                MapOfList.petPeople.values())
            for(Pet p : lp)
                pets.add(p);
        pets.forEach(System.out::println);
    }
}
/* Output:
Cat Elsie May
Cat Pinkola
Cat Shackleton
Cat Stanford
Cymric Molly
Dog Margrett
Mutt Spot
Pug Louie aka Louis Snorkelstein Dupree
Rat Fizzy
Rat Freckly
Rat Fuzzy
*/
```

由于所有的宠物都有名字，因此它们首先按照类型排序，然后在同类型中按照名字排序。

<!-- Tuning a HashMap -->

## 调优 HashMap

我们有可能手动调优HashMap以提高其在特定应用程序中的性能。为了理解调整HashMap时的性能问题，一些术语是必要的：

- 容量（Capacity）：表中存储的桶数量。
- 初始容量（Initial Capacity）：当表被创建时，桶的初始个数。 HashMap 和 HashSet 有可以让你指定初始容量的构造器。
- 个数（Size）：目前存储在表中的键值对的个数。
- 负载因子（Load factor）：通常表现为 $\frac{size}{capacity}$。当负载因子大小为 0 的时候表示为一个空表。当负载因子大小为 0.5 表示为一个半满表（half-full table），以此类推。轻负载的表几乎没有冲突，因此是插入和查找的最佳选择（但会减慢使用迭代器进行遍历的过程）。 HashMap 和 HashSet 有可以让你指定负载因子的构造器。当表内容量达到了负载因子，集合就会自动扩充为原始容量（桶的数量）的两倍，并且会将原始的对象存储在新的桶集合中（也被称为 rehashing）

HashMap 中负载因子的大小为 0.75（当表内容量大小不足四分之三的时候，不会发生 rehashing 现象）。这看起来是一个非常好的同时考虑到时间和空间消耗的平衡策略。更高的负载因子会减少空间的消耗，但是会增加查询的耗时。重要的是，查询操作是你使用的最频繁的一个操作（包括 `get()` 和 `put()` 方法）。

如果你知道存储在 HashMap 中确切的条目个数，直接创建一个足够容量大小的 HashMap，以避免自动发生的 rehashing 操作。

[^1]: 
[^2]:  事实证明，质数实际上并不是散列桶的理想容量。近来，（经过广泛的测试）Java的散列函数都使用2的整数次方。对现代的处理器来说，除法与求余数是最慢的操作。使用2的整数次方长度的散列表，可用掩码代替除法。

<!-- 分页 -->

<div style="page-break-after: always;"></div>
