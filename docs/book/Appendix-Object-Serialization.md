[TOC]

<!-- Appendix: Object Serialization -->
# 附录:对象序列化

当你创建对象时，只要你需要，它就会一直存在，但是在程序终止时，无论如何它都不会继续存在。尽管这么做肯定是有意义的，但是仍旧存在某些情况，如果对象能够在程序不运行的情况下仍能存在并保存其信息，那将非常有用。这样，在下次运行程序时，该对象将被重建并且拥有的信息与在程序上次运行时它所拥有的信息相同。当然，你可以通过将信息写入文件或数据库来达到相同的效果，但是在使万物都成为对象的精神中，如果能够将一个对象声明为是“持久性”的，并为我们处理掉所有细节，那将会显得十分方便。

Java 的对象序列化将那些实现了 Serializable 接口的对象转换成一个字节序列，并能够在以后将这个字节序列完全恢复为原来的对象。这一过程甚至可通过网络进行，这意味着序列化机制能自动弥补不同操作系统之间的差异。也就是说，可以在运行 Windows 系统的计算机上创建一个对象，将其序列化，通过网络将它发送给一台运行 Unix 系统的计算机，然后在那里准确地重新组装，而却不必担心数据在不同机器上的表示会不同，也不必关心宇节的顺序或者其他任何细节。

就其本身来说，对象序列化可以实现轻量级持久性（lightweight persistence），“持久性”意味着一个对象的生存周期并不取决于程序是否正在执行它可以生存于程序的调用之间。通过将一个序列化对象写入磁盘，然后在重新调用程序时恢复该对象，就能够实现持久性的效果。之所以称其为“轻量级”，是因为不能用某种"persistent"（持久）关键字来简单地定义一个对象，并让系统自动维护其他细节问题（尽管将来有可能实现）。相反，对象必须在程序中显式地序列化（serialize）和反序列化还原（deserialize），如果需要个更严格的持久性机制，可以考虑像 Hibernate 之类的工具。

对象序列化的概念加入到语言中是为了支持两种主要特性。一是 Java 的远程方法调用（Remote Method Invocation，RMI），它使存活于其他计算机上的对象使用起来就像是存活于本机上一样。当向远程对象发送消息时，需要通过对象序列化来传输参数和返回值。

再者，对 Java Beans 来说，对象的序列化也是必需的（在撰写本文时被视为失败的技术），使用一个 Bean 时，一般情况下是在设计阶段对它的状态信息进行配置。这种状态信息必须保存下来，并在程序启动时进行后期恢复，这种具体工作就是由对象序列化完成的。

只要对象实现了 Serializable 接口（该接口仅是一个标记接口，不包括任何方法），对象的序列化处理就会非常简单。当序列化的概念被加入到语言中时，许多标准库类都发生了改变，以便具备序列化特性-其中包括所有基本数据类型的封装器、所有容器类以及许多其他的东西。甚至 Class 对象也可以被序列化。

要序列化一个对象，首先要创建某些 OutputStream 对象，然后将其封装在一个 ObjectOutputStream 对象内。这时，只需调用 writeObject() 即可将对象序列化，并将其发送给 OutputStream（对象化序列是基于字节的，因要使用 InputStream 和 OutputStream 继承层次结构）。要反向进行该过程（即将一个序列还原为一个对象），需要将一个 InputStream 封装在 ObjectInputStream 内，然后调用 readObject()。和往常一样，我们最后获得的是一个引用，它指向一个向上转型的 Object，所以必须向下转型才能直接设置它们。

对象序列化特别“聪明”的一个地方是它不仅保存了对象的“全景图”，而且能追踪对象内所包含的所有引用，并保存那些对象；接着又能对对象内包含的每个这样的引用进行追踪，依此类推。这种情况有时被称为“对象网”，单个对象可与之建立连接，而且它还包含了对象的引用数组以及成员对象。如果必须保持一套自己的对象序列化机制，那么维护那些可追踪到所有链接的代码可能会显得非常麻烦。然而，由于 Java 的对象序列化似乎找不出什么缺点，所以请尽量不要自己动手，让它用优化的算法自动维护整个对象网。下面这个例子通过对链接的对象生成一个 worm（蠕虫）对序列化机制进行了测试。每个对象都与 worm 中的下一段链接，同时又与属于不同类（Data）的对象引用数组链接：

```java
// serialization/Worm.java
// Demonstrates object serialization
import java.io.*;
import java.util.*;
class Data implements Serializable {
    private int n;
    Data(int n) { this.n = n; }
    @Override
    public String toString() {
        return Integer.toString(n);
    }
}
public class Worm implements Serializable {
    private static Random rand = new Random(47);
    private Data[] d = {
            new Data(rand.nextInt(10)),
            new Data(rand.nextInt(10)),
            new Data(rand.nextInt(10))
    };
    private Worm next;
    private char c;
    // Value of i == number of segments
    public Worm(int i, char x) {
        System.out.println("Worm constructor: " + i);
        c = x;
        if(--i > 0)
            next = new Worm(i, (char)(x + 1));
    }
    public Worm() {
        System.out.println("No-arg constructor");
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(":");
        result.append(c);
        result.append("(");
        for(Data dat : d)
            result.append(dat);
        result.append(")");
        if(next != null)
            result.append(next);
        return result.toString();
    }
    public static void
    main(String[] args) throws ClassNotFoundException,
            IOException {
        Worm w = new Worm(6, 'a');
        System.out.println("w = " + w);
        try(
                ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream("worm.dat"))
        ) {
            out.writeObject("Worm storage\n");
            out.writeObject(w);
        }
        try(
                ObjectInputStream in = new ObjectInputStream(
                        new FileInputStream("worm.dat"))
        ) {
            String s = (String)in.readObject();
            Worm w2 = (Worm)in.readObject();
            System.out.println(s + "w2 = " + w2);
        }
        try(
                ByteArrayOutputStream bout =
                        new ByteArrayOutputStream();
                ObjectOutputStream out2 =
                        new ObjectOutputStream(bout)
        ) {
            out2.writeObject("Worm storage\n");
            out2.writeObject(w);
            out2.flush();
            try(
                    ObjectInputStream in2 = new ObjectInputStream(
                            new ByteArrayInputStream(
                                    bout.toByteArray()))
            ) {
                String s = (String)in2.readObject();
                Worm w3 = (Worm)in2.readObject();
                System.out.println(s + "w3 = " + w3);
            }
        }
    }
}
```

输出为：

```
Worm constructor: 6
Worm constructor: 5
Worm constructor: 4
Worm constructor: 3
Worm constructor: 2
Worm constructor: 1
w = :a(853):b(119):c(802):d(788):e(199):f(881)
Worm storage
w2 = :a(853):b(119):c(802):d(788):e(199):f(881)
Worm storage
w3 = :a(853):b(119):c(802):d(788):e(199):f(881)
```

更有趣的是，Worm 内的 Data 对象数组是用随机数初始化的（这样就不用怀疑编译器保留了某种原始信息），每个 Worm 段都用一个 char 加以标记。该 char 是在递归生成链接的 Worm 列表时自动产生的。要创建一个 Worm，必须告诉构造器你所希望的它的长度。在产生下一个引用时，要调用 Worm 构造器，并将长度减 1，以此类推。最后一个 next 引用则为 null（空），表示已到达 Worm 的尾部

以上这些操作都使得事情变得更加复杂，从而加大了对象序列化的难度。然而，真正的序列化过程却是非常简单的。一旦从另外某个流创建了 ObjectOutputstream，writeObject() 就会将对象序列化。注意也可以为一个 String 调用 writeObject() 也可以用与 DataOutputStream 相同的方法写人所有基本数据类型（它们具有同样的接口）。

有两段看起来相似的独立的代码。一个读写的是文件，而另一个读写的是字节数组（ByteArray），可利用序列化将对象读写到任何 DatalnputStream 或者 DataOutputStream。

从输出中可以看出，被还原后的对象确实包含了原对象中的所有链接。

注意在对一个 Serializable 对象进行还原的过程中，没有调用任何构造器，包括默认的构造器。整个对象都是通过从 InputStream 中取得数据恢复而来的。

<!-- Finding the Class -->

## 查找类

你或许会奇怪，将一个对象从它的序列化状态中恢复出来，有哪些工作是必须的呢？举个例子来说，假如我们将一个对象序列化，并通过网络将其作为文件传送给另一台计算机，那么，另一台计算机上的程序可以只利用该文件内容来还原这个对象吗？

回答这个问题的最好方法就是做一个实验。下面这个文件位于本章的子目录下：

```java
// serialization/Alien.java
// A serializable class
import java.io.*;
public class Alien implements Serializable {}
```

而用于创建和序列化一个 Alien 对象的文件也位于相同的目录下：

```java
// serialization/FreezeAlien.java
// Create a serialized output file
import java.io.*;
public class FreezeAlien {
    public static void main(String[] args) throws Exception {
        try(
                ObjectOutputStream out = new ObjectOutputStream(
                        new FileOutputStream("X.file"));
        ) {
            Alien quellek = new Alien();
            out.writeObject(quellek);
        }
    }
}
```

一旦该程序被编译和运行，它就会在 c12 目录下产生一个名为 X.file 的文件。以下代码位于一个名为 xiles 的子目录下：

```java
// serialization/xfiles/ThawAlien.java
// Recover a serialized file
// {java serialization.xfiles.ThawAlien}
// {RunFirst: FreezeAlien}
package serialization.xfiles;
import java.io.*;
public class ThawAlien {
    public static void main(String[] args) throws Exception {
        ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(new File("X.file")));
        Object mystery = in.readObject();
        System.out.println(mystery.getClass());
    }
}
```

输出为：

```java
class Alien
```

为了正常运行，必须保证 Java 虚拟机能找到相关的.class 文件。

<!-- Controlling Serialization -->

## 控制序列化

正如大家所看到的，默认的序列化机制并不难操纵。然而，如果有特殊的需要那又该怎么办呢？例如，也许要考虑特殊的安全问题，而且你不希望对象的某一部分被序列化；或者一个对象被还原以后，某子对象需要重新创建，从而不必将该子对象序列化。

在这些特殊情况下，可通过实现 Externalizable 接口——代替实现 Serializable 接口-来对序列化过程进行控制。这个 Externalizable 接口继承了 Serializable 接口，同时增添了两个方法：writeExternal0 和 readExternal0。这两个方法会在序列化和反序列化还原的过程中被自动调用，以便执行一些特殊操作。

下面这个例子展示了 Externalizable 接口方法的简单实现。注意 Blip1 和 Blip2 除了细微的差别之外，几乎完全一致（研究一下代码，看看你能否发现）：

```java
// serialization/Blips.java
// Simple use of Externalizable & a pitfall
import java.io.*;
class Blip1 implements Externalizable {
    public Blip1() {
        System.out.println("Blip1 Constructor");
    }
    @Override
    public void writeExternal(ObjectOutput out)
            throws IOException {
        System.out.println("Blip1.writeExternal");
    }
    @Override
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {
        System.out.println("Blip1.readExternal");
    }
}
class Blip2 implements Externalizable {
    Blip2() {
        System.out.println("Blip2 Constructor");
    }
    @Override
    public void writeExternal(ObjectOutput out)
            throws IOException {
        System.out.println("Blip2.writeExternal");
    }
    @Override
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {
        System.out.println("Blip2.readExternal");
    }
}
public class Blips {
    public static void main(String[] args) {
        System.out.println("Constructing objects:");
        Blip1 b1 = new Blip1();
        Blip2 b2 = new Blip2();
        try(
                ObjectOutputStream o = new ObjectOutputStream(
                        new FileOutputStream("Blips.serialized"))
        ) {
            System.out.println("Saving objects:");
            o.writeObject(b1);
            o.writeObject(b2);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        // Now get them back:
        System.out.println("Recovering b1:");
        try(
                ObjectInputStream in = new ObjectInputStream(
                        new FileInputStream("Blips.serialized"))
        ) {
            b1 = (Blip1)in.readObject();
        } catch(IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // OOPS! Throws an exception:
        //- System.out.println("Recovering b2:");
        //- b2 = (Blip2)in.readObject();
    }
}
```

输出为：

```
Constructing objects:
Blip1 Constructor
Blip2 Constructor
Saving objects:
Blip1.writeExternal
Blip2.writeExternal
Recovering b1:
Blip1 Constructor
Blip1.readExternal
```

没有恢复 Blip2 对象的原因是那样做会导致一个异常。你找出 Blip1 和 Blip2 之间的区别了吗？Blipl 的构造器是“公共的”（pablic），Blip2 的构造器却不是，这样就会在恢复时造成异常。试试将 Blip2 的构造器变成 public 的，然后删除//注释标记，看看是否能得到正确的结果。

恢复 b1 后，会调用 Blip1 默认构造器。这与恢复一个 Serializable 对象不同。对于 Serializable 对象，对象完全以它存储的二进制位为基础来构造，而不调用构造器。而对于一个 Externalizable 对象，所有普通的默认构造器都会被调用（包括在字段定义时的初始化），然后调用 readExternal() 必须注意这一点--所有默认的构造器都会被调用，才能使 Externalizable 对象产生正确的行为。

下面这个例子示范了如何完整保存和恢复一个 Externalizable 对象：

```java
// serialization/Blip3.java
// Reconstructing an externalizable object
import java.io.*;
public class Blip3 implements Externalizable {
    private int i;
    private String s; // No initialization
    public Blip3() {
        System.out.println("Blip3 Constructor");
// s, i not initialized
    }
    public Blip3(String x, int a) {
        System.out.println("Blip3(String x, int a)");
        s = x;
        i = a;
// s & i initialized only in non-no-arg constructor.
    }
    @Override
    public String toString() { return s + i; }
    @Override
    public void writeExternal(ObjectOutput out)
            throws IOException {
        System.out.println("Blip3.writeExternal");
// You must do this:
        out.writeObject(s);
        out.writeInt(i);
    }
    @Override
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {
        System.out.println("Blip3.readExternal");
// You must do this:
        s = (String)in.readObject();
        i = in.readInt();
    }
    public static void main(String[] args) {
        System.out.println("Constructing objects:");
        Blip3 b3 = new Blip3("A String ", 47);
        System.out.println(b3);
        try(
                ObjectOutputStream o = new ObjectOutputStream(
                        new FileOutputStream("Blip3.serialized"))
        ) {
            System.out.println("Saving object:");
            o.writeObject(b3);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
// Now get it back:
        System.out.println("Recovering b3:");
        try(
                ObjectInputStream in = new ObjectInputStream(
                        new FileInputStream("Blip3.serialized"))
        ) {
            b3 = (Blip3)in.readObject();
        } catch(IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(b3);
    }
}
```

输出为：

```
Constructing objects:
Blip3(String x, int a)
A String 47
Saving object:
Blip3.writeExternal
Recovering b3:
Blip3 Constructor
Blip3.readExternal
A String 47
```

其中，字段 s 和只在第二个构造器中初始化，而不是在默认的构造器中初始化。这意味着假如不在 readExternal0 中初始化 s 和 i，s 就会为 null，而就会为零（因为在创建对象的第一步中将对象的存储空间清理为 0）。如果注释掉跟随于"You must do this”后面的两行代码，然后运行程序，就会发现当对象被还原后，s 是 null，而 i 是零。

我们如果从一个 Externalizable 对象继承，通常需要调用基类版本的 writeExternal() 和 readExternal() 来为基类组件提供恰当的存储和恢复功能。

因此，为了正常运行，我们不仅需要在 writeExternal() 方法（没有任何默认行为来为 Externalizable 对象写入任何成员对象）中将来自对象的重要信息写入，还必须在 readExternal() 方法中恢复数据。起先，可能会有一点迷惑，因为 Externalizable 对象的默认构造行为使其看起来似乎像某种自动发生的存储与恢复操作。但实际上并非如此。

### transient 关键字

当我们对序列化进行控制时，可能某个特定子对象不想让 Java 的序列化机制自动保存与恢复。如果子对象表示的是我们不希望将其序列化的敏感信息（如密码），通常就会面临这种情况。即使对象中的这些信息是 private（私有）属性，一经序列化处理，人们就可以通过读取文件或者拦截网络传输的方式来访问到它。

有一种办法可防止对象的敏感部分被序列化，就是将类实现为 Externalizable，如前面所示。这样一来，没有任何东西可以自动序列化，并且可以在 writeExternal() 内部只对所需部分进行显式的序列化。

然而，如果我们正在操作的是一个 Seralizable 对象，那么所有序列化操作都会自动进行。为了能够予以控制，可以用 transient（瞬时）关键字逐个字段地关闭序列化，它的意思是“不用麻烦你保存或恢复数据——我自己会处理的"。

例如，假设某个 Logon 对象保存某个特定的登录会话信息，登录的合法性通过校验之后，我们想把数据保存下来，但不包括密码。为做到这一点，最简单的办法是实现 Serializable，并将 password 字段标志为 transient，下面是具体的代码：

```java
// serialization/Logon.java
// Demonstrates the "transient" keyword
import java.util.concurrent.*;
import java.io.*;
import java.util.*;
import onjava.Nap;
public class Logon implements Serializable {
    private Date date = new Date();
    private String username;
    private transient String password;
    public Logon(String name, String pwd) {
        username = name;
        password = pwd;
    }
    @Override
    public String toString() {
        return "logon info: \n username: " +
                username + "\n date: " + date +
                "\n password: " + password;
    }
    public static void main(String[] args) {
        Logon a = new Logon("Hulk", "myLittlePony");
        System.out.println("logon a = " + a);
        try(
                ObjectOutputStream o =
                        new ObjectOutputStream(
                                new FileOutputStream("Logon.dat"))
        ) {
            o.writeObject(a);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        new Nap(1);
// Now get them back:
        try(
                ObjectInputStream in = new ObjectInputStream(
                        new FileInputStream("Logon.dat"))
        ) {
            System.out.println(
                    "Recovering object at " + new Date());
            a = (Logon)in.readObject();
        } catch(IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("logon a = " + a);
    }
}
```

输出为：

```
logon a = logon info:
username: Hulk
date: Tue May 09 06:07:47 MDT 2017
password: myLittlePony
Recovering object at Tue May 09 06:07:49 MDT 2017
logon a = logon info:
username: Hulk
date: Tue May 09 06:07:47 MDT 2017
password: null
```

可以看到，其中的 date 和 username 是一般的（不是 transient 的），所以它们会被自动序列化。而 password 是 transient 的，所以不会被自动保存到磁盘；另外，自动序列化机制也不会尝试去恢复它。当对象被恢复时，password 就会变成 null。注意，虽然 toString() 是用重载后的+运算符来连接 String 对象，但是 null 引用会被自动转换成字符串 null。

我们还可以发现：date 字段被存储了到磁盘并从磁盘上被恢复了出来，而且没有再重新生成。由于 Externalizable 对象在默认情况下不保存它们的任何字段，所以 transient 关键字只能和 Serializable 对象一起使用。

### Externalizable 的替代方法

如果不是特别坚持实现 Externalizable 接口，那么还有另一种方法。我们可以实现 Serializable 接口，并添加（注意我说的是“添加”，而非“覆盖”或者“实现”）名为 writeObject() 和 readObject() 的方法。这样一旦对象被序列化或者被反序列化还原，就会自动地分别调用这两个方法。也就是说，只要我们提供了这两个方法，就会使用它们而不是默认的序列化机制。

这些方法必须具有准确的方法特征签名：

```java
private void writeObject(ObjectOutputStream stream) throws IOException

private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
```

从设计的观点来看，现在事情变得真是不可思议。首先，我们可能会认为由于这些方法不是基类或者 Serializable 接口的一部分，所以应该在它们自己的接口中进行定义。但是注意它们被定义成了 private，这意味着它们仅能被这个类的其他成员调用。然而，实际上我们并没有从这个类的其他方法中调用它们，而是 ObjectOutputStream 和 ObjectInputStream 对象的 writeObject() 和 readobject() 方法调用你的对象的 writeObject() 和 readObject() 方法（注意关于这里用到的相同方法名，我尽量抑制住不去谩骂。一句话：混乱）。读者可能想知道 ObjectOutputStream 和 ObjectInputStream 对象是怎样访问你的类中的 private 方法的。我们只能假设这正是序列化神奇的一部分。

在接口中定义的所有东西都自动是 public 的，因此如果 writeObject() 和 readObject() 必须是 private 的，那么它们不会是接口的一部分。因为我们必须要完全遵循其方法特征签名，所以其效果就和实现了接口一样。

在调用 ObjectOutputStream.writeObject() 时，会检查所传递的 Serializable 对象，看看是否实现了它自己的 writeObject()。如果是这样，就跳过正常的序列化过程并调用它的 writeObiect()。readObject() 的情形与此相同。

还有另外一个技巧。在你的 writeObject() 内部，可以调用 defaultWriteObject() 来选择执行默认的 writeObject()。类似地，在 readObject() 内部，我们可以调用 defaultReadObject()，下面这个简单的例子演示了如何对一个 Serializable 对象的存储与恢复进行控制：

```java
// serialization/SerialCtl.java
// Controlling serialization by adding your own
// writeObject() and readObject() methods
import java.io.*;
public class SerialCtl implements Serializable {
    private String a;
    private transient String b;
    public SerialCtl(String aa, String bb) {
        a = "Not Transient: " + aa;
        b = "Transient: " + bb;
    }
    @Override
    public String toString() { return a + "\n" + b; }
    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(b);
    }
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        b = (String)stream.readObject();
    }
    public static void main(String[] args) {
        SerialCtl sc = new SerialCtl("Test1", "Test2");
        System.out.println("Before:\n" + sc);
        try (
                ByteArrayOutputStream buf =
                        new ByteArrayOutputStream();
                ObjectOutputStream o =
                        new ObjectOutputStream(buf);
        ) {
            o.writeObject(sc);
// Now get it back:
            try (
                    ObjectInputStream in =
                            new ObjectInputStream(
                                    new ByteArrayInputStream(
                                            buf.toByteArray()));
            ) {
                SerialCtl sc2 = (SerialCtl)in.readObject();
                System.out.println("After:\n" + sc2);
            }
        } catch(IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
```

输出为：

```
Before:
Not Transient: Test1
Transient: Test2
After:
Not Transient: Test1
Transient: Test2
```

在这个例子中，有一个 String 字段是普通字段，而另一个是 transient 字段，用来证明非 transient 字段由 defaultWriteObject() 方法保存，而 transient 字段必须在程序中明确保存和恢复。字段是在构造器内部而不是在定义处进行初始化的，以此可以证实它们在反序列化还原期间没有被一些自动化机制初始化。

如果我们打算使用默认机制写入对象的非 transient 部分，那么必须调用 defaultwriteObject() 作为 writeObject() 中的第一个操作，并让 defaultReadObject() 作为 readObject() 中的第一个操作。这些都是奇怪的方法调用。例如，如果我们正在为 ObjectOutputStream 调用 defaultWriteObject() 且没有传递任何参数，然而不知何故它却可以运行，并且知道对象的引用以及如何写入非 transient 部分。真是奇怪之极。

对 transient 对象的存储和恢复使用了我们比较熟悉的代码。请再考虑一下在这里所发生的事情。在 main0）中，创建 SerialCtl 对象，然后将其序列化到 ObjectOutputStream（注意在这种情况下，使用的是缓冲区而不是文件-这对于 ObjectOutputStream 来说是完全一样的）。序列化发生在下面这行代码当中

```java
o.writeObject(sc);
```

writeObject() 方法必须检查 sc，判断它是否拥有自己的 writeObject() 方法（不是检查接口——这里根本就没有接口，也不是检查类的类型，而是利用反射来真正地搜索方法）。如果有，那么就会使用它。对 readObject() 也采用了类似的方法。或许这是解决这个问题的唯一切实可行的方法，但它确实有点古怪。

### 版本控制

有时可能想要改变可序列化类的版本（比如源类的对象可能保存在数据库中）。虽然 Java 支持这种做法，但是你可能只在特殊的情况下才这样做，此外，还需要对它有相当深程度的了解（在这里我们就不再试图达到这一点）。从 http://java.oracle.com 下的 JDK 文档中对这一主题进行了非常彻底的论述。

<!-- Using Persistence -->

## 使用持久化

个比较诱人的使用序列化技术的想法是：存储程序的一些状态，以便我们随后可以很容易地将程序恢复到当前状态。但是在我们能够这样做之前，必须回答几个问题。如果我们将两个对象-它们都具有指向第三个对象的引用-进行序列化，会发生什么情况？当我们从它们的序列化状态恢复这两个对象时，第三个对象会只出现一次吗？如果将这两个对象序列化成独立的文件，然后在代码的不同部分对它们进行反序列化还原，又会怎样呢？

下面这个例子说明了上述问题：

```java
// serialization/MyWorld.java
import java.io.*;
import java.util.*;
class House implements Serializable {}
class Animal implements Serializable {
    private String name;
    private House preferredHouse;
    Animal(String nm, House h) {
        name = nm;
        preferredHouse = h;
    }
    @Override
    public String toString() {
        return name + "[" + super.toString() +
                "], " + preferredHouse + "\n";
    }
}
public class MyWorld {
    public static void main(String[] args) {
        House house = new House();
        List<Animal> animals = new ArrayList<>();
        animals.add(
                new Animal("Bosco the dog", house));
        animals.add(
                new Animal("Ralph the hamster", house));
        animals.add(
                new Animal("Molly the cat", house));
        System.out.println("animals: " + animals);
        try(
                ByteArrayOutputStream buf1 =
                        new ByteArrayOutputStream();
                ObjectOutputStream o1 =
                        new ObjectOutputStream(buf1)
        ) {
            o1.writeObject(animals);
            o1.writeObject(animals); // Write a 2nd set
// Write to a different stream:
            try(
                    ByteArrayOutputStream buf2 = new ByteArrayOutputStream();
                    ObjectOutputStream o2 = new ObjectOutputStream(buf2)
            ) {
                o2.writeObject(animals);
// Now get them back:
                try(
                        ObjectInputStream in1 =
                                new ObjectInputStream(
                                        new ByteArrayInputStream(
                                                buf1.toByteArray()));
                        ObjectInputStream in2 =
                                new ObjectInputStream(
                                        new ByteArrayInputStream(
                                                buf2.toByteArray()))
                ) {
                    List
                            animals1 = (List)in1.readObject(),
                            animals2 = (List)in1.readObject(),
                            animals3 = (List)in2.readObject();
                    System.out.println(
                            "animals1: " + animals1);
                    System.out.println(
                            "animals2: " + animals2);
                    System.out.println(
                            "animals3: " + animals3);
                }
            }
        } catch(IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
```

输出为：

```
animals: [Bosco the dog[Animal@15db9742],
House@6d06d69c
, Ralph the hamster[Animal@7852e922], House@6d06d69c
, Molly the cat[Animal@4e25154f], House@6d06d69c
]
animals1: [Bosco the dog[Animal@7ba4f24f],
House@3b9a45b3
, Ralph the hamster[Animal@7699a589], House@3b9a45b3
, Molly the cat[Animal@58372a00], House@3b9a45b3
]
animals2: [Bosco the dog[Animal@7ba4f24f],
House@3b9a45b3
, Ralph the hamster[Animal@7699a589], House@3b9a45b3
, Molly the cat[Animal@58372a00], House@3b9a45b3
]
animals3: [Bosco the dog[Animal@4dd8dc3],
House@6d03e736
, Ralph the hamster[Animal@568db2f2], House@6d03e736
, Molly the cat[Animal@378bf509], House@6d03e736
]
```



这里有一件有趣的事：我们可以通过一个字节数组来使用对象序列化，从而实现对任何可 Serializable 对象的“深度复制"（deep copy）—— 深度复制意味着我们复制的是整个对象网，而不仅仅是基本对象及其引用。复制对象将在本书的 [附录：传递和返回对象 ]() 一章中进行深入地探讨。

在这个例子中，Animal 对象包含有 House 类型的字段。在 main() 方法中，创建了一个 Animal 列表并将其两次序列化，分别送至不同的流。当其被反序列化还原并被打印时，我们可以看到所示的执行某次运行后的结果（每次运行时，对象将会处在不同的内存地址）。

当然，我们期望这些反序列化还原后的对象地址与原来的地址不同。但请注意，在 animals1 和 animals2 中却出现了相同的地址，包括二者共享的那个指向 House 对象的引用。另一方面，当恢复 animals3 时，系统无法知道另一个流内的对象是第一个流内的对象的别名，因此它会产生出完全不同的对象网。

只要将任何对象序列化到单一流中，就可以恢复出与我们写出时一样的对象网，并且没有任何意外重复复制出的对象。当然，我们可以在写出第一个对象和写出最后一个对象期间改变这些对象的状态，但是这是我们自己的事，无论对象在被序列化时处于什么状态（无论它们和其他对象有什么样的连接关系），它们都可以被写出。

最安全的做法是将其作为“原子”操作进行序列化。如果我们序列化了某些东西，再去做其他一些工作，再来序列化更多的东西，如此等等，那么将无法安全地保存系统状态。取而代之的是，将构成系统状态的所有对象都置入单一容器内，并在一个操作中将该容器直接写出。然后同样只需一次方法调用，即可以将其恢复。

下面这个例子是一个想象的计算机辅助设计（CAD）系统，该例演示了这一方法。此外，它还引入了 static 字段的问题：如果我们查看 JDK 文档，就会发现 Class 是 Serializable 的，因此只需直接对 Class 对象序列化，就可以很容易地保存 static 字段。在任何情况下，这都是一种明智的做法。

```java
// serialization/AStoreCADState.java
// Saving the state of a fictitious CAD system
import java.io.*;
import java.util.*;
import java.util.stream.*;
enum Color { RED, BLUE, GREEN }
abstract class Shape implements Serializable {
    private int xPos, yPos, dimension;
    private static Random rand = new Random(47);
    private static int counter = 0;
    public abstract void setColor(Color newColor);
    public abstract Color getColor();
    Shape(int xVal, int yVal, int dim) {
        xPos = xVal;
        yPos = yVal;
        dimension = dim;
    }
    public String toString() {
        return getClass() + "color[" + getColor() +
                "] xPos[" + xPos + "] yPos[" + yPos +
                "] dim[" + dimension + "]\n";
    }
    public static Shape randomFactory() {
        int xVal = rand.nextInt(100);
        int yVal = rand.nextInt(100);
        int dim = rand.nextInt(100);
        switch(counter++ % 3) {
            default:
            case 0: return new Circle(xVal, yVal, dim);
            case 1: return new Square(xVal, yVal, dim);
            case 2: return new Line(xVal, yVal, dim);
        }
    }
}
class Circle extends Shape {
    private static Color color = Color.RED;
    Circle(int xVal, int yVal, int dim) {
        super(xVal, yVal, dim);
    }
    public void setColor(Color newColor) {
        color = newColor;
    }
    public Color getColor() { return color; }
}
class Square extends Shape {
    private static Color color = Color.RED;
    Square(int xVal, int yVal, int dim) {
        super(xVal, yVal, dim);
    }
    public void setColor(Color newColor) {
        color = newColor;
    }
    public Color getColor() { return color; }
}
class Line extends Shape {
    private static Color color = Color.RED;
    public static void
    serializeStaticState(ObjectOutputStream os)
            throws IOException { os.writeObject(color); }
    public static void
    deserializeStaticState(ObjectInputStream os)
            throws IOException, ClassNotFoundException {
        color = (Color)os.readObject();
    }
    Line(int xVal, int yVal, int dim) {
        super(xVal, yVal, dim);
    }
    public void setColor(Color newColor) {
        color = newColor;
    }
    public Color getColor() { return color; }
}
public class AStoreCADState {
    public static void main(String[] args) {
        List<Class<? extends Shape>> shapeTypes =
                Arrays.asList(
                        Circle.class, Square.class, Line.class);
        List<Shape> shapes = IntStream.range(0, 10)
                .mapToObj(i -> Shape.randomFactory())
                .collect(Collectors.toList());
        // Set all the static colors to GREEN:
        shapes.forEach(s -> s.setColor(Color.GREEN));
        // Save the state vector:
        try(
                ObjectOutputStream out =
                        new ObjectOutputStream(
                                new FileOutputStream("CADState.dat"))
        ) {
            out.writeObject(shapeTypes);
            Line.serializeStaticState(out);
            out.writeObject(shapes);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        // Display the shapes:
        System.out.println(shapes);
    }
}
```

输出为：

```java
[class Circlecolor[GREEN] xPos[58] yPos[55] dim[93]
, class Squarecolor[GREEN] xPos[61] yPos[61] dim[29]
, class Linecolor[GREEN] xPos[68] yPos[0] dim[22]
, class Circlecolor[GREEN] xPos[7] yPos[88] dim[28]
, class Squarecolor[GREEN] xPos[51] yPos[89] dim[9]
, class Linecolor[GREEN] xPos[78] yPos[98] dim[61]
, class Circlecolor[GREEN] xPos[20] yPos[58] dim[16]
, class Squarecolor[GREEN] xPos[40] yPos[11] dim[22]
, class Linecolor[GREEN] xPos[4] yPos[83] dim[6]
, class Circlecolor[GREEN] xPos[75] yPos[10] dim[42]
]
```

Shape 类实现了 Serializable，所以任何自 Shape 继承的类也都会自动是 Serializable 的。每个 Shape 都含有数据，而且每个派生自 Shape 的类都包含一个 static 字段，用来确定各种 Shape 类型的颜色（如果将 static 字段置入基类，只会产生一个 static 字段，因为 static 字段不能在派生类中复制）。可对基类中的方法进行重载，以便为不同的类型设置颜色（static 方法不会动态绑定，所以这些都是普通的方法）。每次调用 randomFactory() 方法时，它都会使用不同的随机数作为 Shape 的数据，从而创建不同的 Shape。

在 main() 中，一个 ArrayList 用于保存 Class 对象，而另一个用于保存几何形状。

恢复对象相当直观：

```java
// serialization/RecoverCADState.java
// Restoring the state of the fictitious CAD system
// {RunFirst: AStoreCADState}
import java.io.*;
import java.util.*;
public class RecoverCADState {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try(
                ObjectInputStream in =
                        new ObjectInputStream(
                                new FileInputStream("CADState.dat"))
        ) {
// Read in the same order they were written:
            List<Class<? extends Shape>> shapeTypes =
                    (List<Class<? extends Shape>>)in.readObject();
            Line.deserializeStaticState(in);
            List<Shape> shapes =
                    (List<Shape>)in.readObject();
            System.out.println(shapes);
        } catch(IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
```

输出为：

```java
[class Circlecolor[RED] xPos[58] yPos[55] dim[93]
, class Squarecolor[RED] xPos[61] yPos[61] dim[29]
, class Linecolor[GREEN] xPos[68] yPos[0] dim[22]
, class Circlecolor[RED] xPos[7] yPos[88] dim[28]
, class Squarecolor[RED] xPos[51] yPos[89] dim[9]
, class Linecolor[GREEN] xPos[78] yPos[98] dim[61]
, class Circlecolor[RED] xPos[20] yPos[58] dim[16]
, class Squarecolor[RED] xPos[40] yPos[11] dim[22]
, class Linecolor[GREEN] xPos[4] yPos[83] dim[6]
, class Circlecolor[RED] xPos[75] yPos[10] dim[42]
]
```

可以看到，xPos，yPos 以及 dim 的值都被成功地保存和恢复了，但是对 static 信息的读取却出现了问题。所有读回的颜色应该都是“3”，但是真实情况却并非如此。Circle 的值为 1（定义为 RED），而 Square 的值为 0（记住，它们是在构造器中被初始化的）。看上去似乎 static 数据根本没有被序列化！确实如此——尽管 Class 类是 Serializable 的，但它却不能按我们所期望的方式运行。所以假如想序列化 static 值，必须自己动手去实现。

这正是 Line 中的 serializeStaticState() 和 deserializeStaticState() 两个 static 方法的用途。可以看到，它们是作为存储和读取过程的一部分被显式地调用的。（注意必须维护写入序列化文件和从该文件中读回的顺序。）因此，为了使 CADStatejava 正确运转起来，你必须：

1. 为几何形状添加 serializeStaticState() 和 deserializeStaticState()
2. 移除 ArrayList shapeTypes 以及与之有关的所有代码。
3. 在几何形状内添加对新的序列化和反序列化还原静态方法的调用。

另一个要注意的问题是安全，因为序列化也会将 private 数据保存下来。如果你关心安全问题，那么应将其标记成 transient，但是这之后，还必须设计一种安全的保存信息的方法，以便在执行恢复时可以复位那些 private 变量。

## XML

对象序列化的一个重要限制是它只是 Java 的解决方案：只有 Java 程序才能反序列化这种对象。一种更具互操作性的解决方案是将数据转换为 XML 格式，这可以使其被各种各样的平台和语言使用。

因为 XML 十分流行，所以用它来编程时的各种选择不胜枚举，包括随 JDK 发布的 javax.xml.*类库。我选择使用 Elliotte Rusty Harold 的开源 XOM 类库（可从 www.xom.nu 下载并获得文档），因为它看起来最简单，同时也是最直观的用 Java 产生和修改 XML 的方式。另外，XOM 还强调了 XML 的正确性。

作为一个示例，假设有一个 APerson 对象，它包含姓和名，你想将它们序列化到 XML 中。下面的 APerson 类有一个 getXML() 方法，它使用 XOM 来产生被转换为 XML 的 Element 对象的 APerson 数据；还有一个构造器，接受 Element 并从中抽取恰当的 APerson 数据（注意，XML 示例都在它们自己的子目录中）：

```java
// serialization/APerson.java
// Use the XOM library to write and read XML
// nu.xom.Node comes from http://www.xom.nu
import nu.xom.*;
import java.io.*;
import java.util.*;
public class APerson {
    private String first, last;
    public APerson(String first, String last) {
        this.first = first;
        this.last = last;
    }
    // Produce an XML Element from this APerson object:
    public Element getXML() {
        Element person = new Element("person");
        Element firstName = new Element("first");
        firstName.appendChild(first);
        Element lastName = new Element("last");
        lastName.appendChild(last);
        person.appendChild(firstName);
        person.appendChild(lastName);
        return person;
    }
    // Constructor restores a APerson from XML:
    public APerson(Element person) {
        first = person
                .getFirstChildElement("first").getValue();
        last = person
                .getFirstChildElement("last").getValue();
    }
    @Override
    public String toString() {
        return first + " " + last;
    }
    // Make it human-readable:
    public static void
    format(OutputStream os, Document doc)
            throws Exception {
        Serializer serializer =
                new Serializer(os,"ISO-8859-1");
        serializer.setIndent(4);
        serializer.setMaxLength(60);
        serializer.write(doc);
        serializer.flush();
    }
    public static void main(String[] args) throws Exception {
        List<APerson> people = Arrays.asList(
                new APerson("Dr. Bunsen", "Honeydew"),
                new APerson("Gonzo", "The Great"),
                new APerson("Phillip J.", "Fry"));
        System.out.println(people);
        Element root = new Element("people");
        for(APerson p : people)
            root.appendChild(p.getXML());
        Document doc = new Document(root);
        format(System.out, doc);
        format(new BufferedOutputStream(
                new FileOutputStream("People.xml")), doc);
    }
}
```

输出为：

```xml
[Dr. Bunsen Honeydew, Gonzo The Great, Phillip J. Fry]
<?xml version="1.0" encoding="ISO-8859-1"?>
<people>
    <person>
        <first>Dr. Bunsen</first>
        <last>Honeydew</last>
    </person>
    <person>
        <first>Gonzo</first>
        <last>The Great</last>
    </person>
    <person>
        <first>Phillip J.</first>
        <last>Fry</last>
    </person>
</people>
```

XOM 的方法都具有相当的自解释性，可以在 XOM 文档中找到它们。XOM 还包含一个 Serializer 类，你可以在 format() 方法中看到它被用来将 XML 转换为更具可读性的格式。如果只调用 toXML()，那么所有东西都会混在一起，因此 Serializer 是一种便利工具。

从 XML 文件中反序列化 Person 对象也很简单：


```java
// serialization/People.java
// nu.xom.Node comes from http://www.xom.nu
// {RunFirst: APerson}
import nu.xom.*;
import java.io.File;
import java.util.*;
public class People extends ArrayList<APerson> {
    public People(String fileName) throws Exception {
        Document doc =
                new Builder().build(new File(fileName));
        Elements elements =
                doc.getRootElement().getChildElements();
        for(int i = 0; i < elements.size(); i++)
            add(new APerson(elements.get(i)));
    }
    public static void main(String[] args) throws Exception {
        People p = new People("People.xml");
        System.out.println(p);
    }
}
/* Output:
[Dr. Bunsen Honeydew, Gonzo The Great, Phillip J. Fry]
*/
```

People 构造器使用 XOM 的 Builder.build() 方法打开并读取一个文件，而 getChildElements() 方法产生了一个 Elements 列表（不是标准的 Java List，只是一个拥有 size() 和 get() 方法的对象，因为 Harold 不想强制人们使用特定版本的 Java，但是仍旧希望使用类型安全的容器）。在这个列表中的每个 Element 都表示一个 Person 对象，因此它可以传递给第二个 Person 构造器。注意，这要求你提前知道 XML 文件的确切结构，但是这经常会有些问题。如果文件结构与你预期的结构不匹配，那么 XOM 将抛出异常。对你来说，如果你缺乏有关将来的 XML 结构的信息，那么就有可能会编写更复杂的代码去探测 XML 文档，而不是只对其做出假设。

为了获取这些示例去编译它们，你必须将 XOM 发布包中的 JAR 文件放置到你的类路径中。

这里只给出了用 Java 和 XOM 类库进行 XML 编程的简介，更详细的信息可以浏览 www.xom.nu 。



<!-- 分页 -->

<div style="page-break-after: always;"></div>
