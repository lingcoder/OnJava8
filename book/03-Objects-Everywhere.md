# 第三章 万物皆对象

>如果我们说不同的语言，我们会感觉到一个不同的世界！— Ludwig Wittgenstein (1889-1951)

尽管 Java 基于 C++ ，但 Java 是一种更纯粹的面向对象程序设计语言。Java 和 C++ 都是混合语言。在 Java 中，语言设计者认为混合并不像 C++ 那样重要。混合语言允许多种编程风格；这也是 C++ 支持与 C 语言的向后兼容性原因。因为 C++ 是 C 语言的超集，所以它也包含了许多 C 语言的不良特性，这可能使得 C++ 在某些方面过于复杂。

 Java 语言预设你已经编写过面对对象的程序。在此之前，你必须将自己的思维置于面对对象的世界。在本章中你将了解 Java 语言的基本组成，学习 Java （几乎）万物皆对象的思想。

<!-- You Manipulate Objects with References -->
## 对象操纵

名字的意义在于，当我们听到“玫瑰”这个词时，就会想到一种闻起来很甜蜜的的花。（引用自 莎士比亚，《罗密欧与朱丽叶》）。

所有的编程语言都会操纵内存中的元素。有时程序员必须要有意识地直接或间接地操纵它们。示例：在 C/C++ 语言中是通过指针来完成操作的。

Java 使用万物皆对象的思想和特有的语法方式来简化问题。虽然万物皆可为对象，但你操纵的标识符实际上是只对象的“引用” [^1]。 示例：我们可以将这种“引用”想象成电视（对象）和遥控器（引用）之间的关系。只要拥有对象的“引用”，就可以操纵该“对象”。我们无需直接接触电视，只要掌握遥控器就可以在房中自由地控制电视（对象）的频道和音量。此外，没有电视机，遥控器也可以单独存在。引申来说，仅仅因为你有一个“引用”并不意味着你必然有一个关联的“对象”。 

下面来创建一个 String 的引用，用于保存单词语句。代码示例：

```java
    String s;
```

这里我们仅仅只是创建了一个 String 对象的引用，而非对象。直接拿来使用会出现错误：因为此时你并没有给变量 s 赋值--附加任何引用的对象。通常更安全的做法是：在声明变量引用的同时初始化对象信息。代码示例：

```java
    String s = "asdf";
```

Java 语法允许我们使用带双引号的文本内容来初始化字符串。同样，其他类型的对象也有相应的初始化方式。

<!-- You Must Create All the Objects -->
## 对象创建

“引用”用来连接“对象”。在 Java 中，通常我们使用`new`这个操作符来来创建一个新的对象。`new`关键字代表：创建一个新的对象实例。所以，前面的代码实例我们也可以这样来表示：

```java
    String s = new String("asdf");
```
以上的代码示例展示了字符串对象的创建过程，以及如何初始化生成字符串。Java 本身自带了许多现成的数据类型，在此基础之上我们还可以创建自己的数据类型。类型的创建是 Java 的基本操作。在本书后面的学习中将会接触到。

<!-- Where Storage Lives -->
### 数据存储

那么, 程序在运行时是如何存储的呢？尤其是内存。下面我们就来形象地描述下， Java 中数据存储的5个不同的地方：

1. **寄存器** （*Registers*） 最快的保存区域，位于CPU内部 [^2]。然而，寄存器的数量十分有限，所以寄存器是根据需要由编译器分配。我们对其没有直接的控制权，也无法在自己的程序里找到寄存器存在的踪迹（另一方面，C/C++ 允许开发者向编译器建议寄存器的分配）。

2. **栈内存**（*Stack*） 存在于常规内存（RAM）区域中，可通过栈指针获得处理器的直接支持。栈指针下移创建新内存，上移释放该内存，顺序后进先出，速度仅次于寄存器。创建程序时，Java 编译器必须准确地知道栈内保存的所有数据的“长度”以及生命周期。栈内存的这种约束限制了程序的灵活性。因此，虽然在栈内存上存在一些 Java 数据，特别是对象引用，但 Java 对象本身却是保存在堆内存的。

3. **堆内存**（*Heap*） 这是一种常规用途的内存池（也在 RAM区域），所有 Java 对象都存在于其中。与栈内存不同，编译器不需要知道对象必须在堆内存上停留多长时间。因此，用堆内存保存数据更具灵活性。创建一个对象时，只需用 new 命令实例化代码即可。执行这些代码时，数据会在堆内存里自动进行保存。这种灵活性是有代价的：分配和清理堆内存要比栈内存需要更多的时间（如果你甚至可以用 Java 在栈内存上创建对象，就像在C++ 中那样）。随着时间的推移，Java 的堆内存分配机制现已非常快，因此这不是一个值得关心的问题了。

4. **常量存储** （*Constant storage*） 常量值通常直接放在程序代码中，因为它们永远不会改变。如需严格保护，可考虑将它们置于只读存储器（ROM）中 [^3]。

5. **非 RAM 存储** （*Non-RAM storage*） 数据完全存在于程序之外，在程序未运行以及脱离程序控制后依然存在。两个主要的例子：（1）序列化对象：对象被转换为字节流，通常被发送到另一台机器；（2）持久化对象：对象被放置在磁盘上，即使程序终止，数据依然存在。这些存储的方式都是将对象转存于另一个介质中，并在需要时恢复到常规内存中。Java 为轻量级持久性提供支持。诸如 JDBC 和 Hibernate 之类的库为使用数据库存储和检索对象信息提供了更复杂的支持。


<!-- Special Case: Primitive Types -->
### 基本类型的存储

有一组类型在 Java 中使用频率很高,这就是 Java 的基本类型。对于此类数据的存储我们需要特别对待。之所以说这么说，是因为它们的创建并不是通过 `new` 关键字来产生。通常 `new` 出来的对象都是保存在 **Heap** 内存中的，  用它来创建小、简单的基本类型的数据是不划算的。所以对于这些基本类型的创建方法， Java 使用了和 C/C++ 一样的策略。也就是说，不是使用 `new` 创建变量，而是使用一个“自动”变量。 这个变量容纳了具体的值，并置于栈内存中，能够更高效地存取。

Java 预设了每种基本类型的初始内存占用大小。 这些大小标准不会随着机器环境的变化而变化。这种不变性也是Java 的跨平台的一个原因。

| 基本类型  |    大小 |  最小值  | 最大值  | 包装类型 |
| :------: | :------: | :------: | :------: | :------: |
| boolean | —  | — | — | Boolean |
| char | 16 bits | Unicode 0  | Unicode $2^{16}-1$  | Character |
| byte | 8 bits | $-128$ | $+127$ | Byte |
| short | 16 bits | $-2^{15}$ | $+2^{15}-1$ | Short |
| int | 32 bits | $-2^{31}$ | $-2^{31}-1$ | Integer |
| long | 64 bits | $-2^{63}$ | $-2^{63}-1$ | Long |
| float | 32 bits | IEEE754 | IEEE754 | Float |
| double | 64 bits |IEEE754 | IEEE754 | Double |
| void | — | — | — | Void |

所有的数值类型都是有正/负符号的。布尔（boolean）类型的大小没有明确的规定，通常定义为采用文字 “true” 和 “false”。基本类型有自己对应的包装类型，如果你希望在堆内存里表示基本类型的数据，就需要用到它们的包装类。代码示例：

```java
char c = 'x';
Character ch = new Character(c);
```
或者你也可以使用下面的形式 基本类型自动转换成包装类型（自动装箱）：

```java
Character ch = new Character('x');
```
相对的，包装类型转化为基本类型（自动拆箱）：

```java
char c = ch;
```

个中原因将在以后的章节里解释。

<!-- High-Precision Numbers -->
### 高精数值的存储


Java includes two classes for performing high-precision arithmetic:
BigInteger and BigDecimal. Although these fit approximately
the same category as the “wrapper” classes, neither one has a
corresponding primitive.
Both classes have methods that provide analogues for the operations
you perform on primitive types. That is, you can do anything with a
BigInteger or BigDecimal you can with an int or float, it’s
just that you must use method calls instead of operators. Also, since
there are more calculations involved, the operations are slower. You’re
exchanging speed for accuracy.
BigInteger supports arbitrary-precision integers. This means you
can accurately represent integral values of any size without losing any
information during operations.
BigDecimal is for arbitrary-precision fixed-point numbers; you can
use these for accurate monetary calculations, for example.
Consult the JDK documentation for details about these two classes.

<!-- Arrays in Java -->
### 数组的存储

Many programming languages support some kind of array. Using
arrays in C and C++ is perilous because those arrays are only blocks of
memory. If a program accesses the array outside of its memory block
or uses the memory before initialization (common programming
errors), the results are unpredictable.
One of the primary goals of Java is safety, so many of the problems
that plague programmers in C and C++ are not repeated in Java. A
Java array is guaranteed to be initialized and cannot be accessed
outside of its range. This range checking comes at the price of having a
small amount of memory overhead for each array as well as extra time
to verify the index at run time, but the assumption is that the safety
and increased productivity are worth the expense (and Java can often
optimize these operations).
When you create an array of objects, you are really creating an array of
references, and each of those references is automatically initialized to
a special value with its own keyword: null. When Java sees null, it
recognizes that the reference in question isn’t pointing to an object.
You must assign an object to each reference before you use it, and if
you try to use a reference that’s still null, the problem is reported at
run time. Thus, typical array errors are prevented in Java.
You can also create an array of primitives. The compiler guarantees
initialization by zeroing the memory for that array.
Arrays are covered in detail later in the book, and specifically in the
Arrays chapter.

  [^1]: 脚注预留
  [^2]: 脚注预留
  [^3]: 脚注预留

<!-- Comments -->
## 代码注释

There are two types of comments in Java. The first are the traditional
C-style comment which begin with a /* and continue, possibly across
many lines, until a */. Note that many programmers begin each line
of a multiline comment with a *, so you’ll often see:
/* This is a comment
* that continues
* across lines
*/
Remember, however, that everything inside the /* and */ is ignored,
so there’s no difference if you say instead:
/* This is a comment that
continues across lines */
The second form of comment comes from C++. It is the single-line
comment, which starts with a // and continues until the end of the
line. This type of comment is convenient and commonly used because
it’s easy. So you often see:
// This is a one-line comment

<!-- You Never Need to Destroy an Object -->
## 对象清理

In some programming languages, managing storage lifetime requires
significant effort. How long does a variable last? If you are supposed to
destroy it, when should you? Confusion over storage lifetime can lead
to many bugs, and this section shows how Java simplifies the issue by
releasing storage for you.
Scoping
Most procedural languages have the concept of scope. This determines
both the visibility and lifetime of the names defined within that scope.
In C, C++, and Java, scope is determined by the placement of curly
braces {}. Here is a fragment of Java code demonstrating scope:
{
int x = 12;
// Only x available
{
int q = 96;
// Both x & q available
}
// Only x available
// q is "out of scope"
}
A variable defined within a scope is available only until the end of that
scope.
Indentation makes Java code easier to read. Since Java is a free-form
language, the extra spaces, tabs, and carriage returns do not affect the
resulting program.
You cannot do the following, even though it is legal in C and C++:
{
int x = 12;
{
int x = 96; // Illegal
}
}
The Java compiler will announce that the variable x has already been
defined. Thus the C and C++ ability to “hide” a variable in a larger
scope is not allowed, because the Java designers thought it led to
confusing programs.
Scope of Objects
Java objects do not have the same lifetimes as primitives. When you
create a Java object using new, it persists past the end of the scope.
Thus, if you say:
{
String s = new String("a string");
} // End of scope
the reference s vanishes at the end of the scope. However, the
String object that s points to is still occupying memory. In this bit
of code, there is no way to access the object after the end of the scope,
because the only reference to it is out of scope. In later chapters you’ll
see how the reference to the object can be passed around and
duplicated during the course of a program.
Because objects created with new exist as long as you need them, a
whole slew of C++ programming problems vanish in Java. In C++ you
must not only make sure that the objects stay around as long as
necessary, you must also destroy the objects when you’re done with
them.
This brings up a question. If Java leaves the objects lying around, what
keeps them from filling up memory and halting your program, which
is exactly the kind of problem that occurs in C++? In Java, a bit of
magic happens: the garbage collector looks at all the objects created
with new and finds those that are no longer referenced. It then
releases the memory for those objects, so the memory can be used for
new objects. This means you don’t worry about reclaiming memory
yourself. You simply create objects, and when you no longer need
them, they go away by themselves. This prevents an important class of
programming problem: the so-called “memory leak,” when a
programmer forgets to release memory.
Creating New Data
Types: class
If everything is an object, what determines how a particular class of
object looks and behaves? Put another way, what establishes the type
of an object? You might expect a keyword called “type,” and that would
certainly make sense. Historically, however, most object-oriented
languages use the keyword class to describe a new kind of object.
The class keyword (so common it will often not be bold-faced
throughout this book) is followed by the name of the new type. For
example:
class ATypeName {
// Class body goes here
}
This introduces a new type, although here the class body consists only
of a comment, so there is not too much you can do with it. However,
you can create an object of ATypeName using new:
ATypeName a = new ATypeName();
You can’t tell it to do much of anything—that is, you cannot send it any
interesting messages—until you define some methods for it.
Fields
When you define a class, you can put two types of elements in your
class: fields (sometimes called data members), and methods
(sometimes called member functions). A field is an object of any type you
can talk to via its reference. A field can also be a primitive type. If
it is a reference to an object, you must initialize that reference to
connect it to an actual object (using new, as seen earlier).
Each object keeps its own storage for its fields. Ordinarily, fields are
not shared among objects. Here is an example of a class with some
fields:
class DataOnly {
int i;
double d;
boolean b;
}
This class doesn’t do anything except hold data. As before, you create
an object like this:
DataOnly data = new DataOnly();
You can assign values to the fields by referring to object members. To
do this, you state the name of the object reference, followed by a
period (dot), followed by the name of the member inside the object:
objectReference.member
For example:
data.i = 47;
data.d = 1.1;
data.b = false;
What if your object contains other objects that contain data you want
to modify? You just keep “connecting the dots.” For example:
myPlane.leftTank.capacity = 100;
You can nest many objects this way (although such a design might
become confusing).
Default Values for Primitive Members
When a primitive data type is a field in a class, it is guaranteed to get a
default value if you do not initialize it:
Primitive
Default
boolean
false
\u0000
char
(null)
byte
(byte)0
short
(short)0
int
0
long
0L
float
0.0f
double
0.0d
The default values are only what Java guarantees when the variable is
used as a member of a class. This ensures that primitive fields will
always be initialized (something C++ doesn’t do), reducing a source of
bugs. However, this initial value might not be correct or even legal for
the program you are writing. It’s best to always explicitly initialize
your variables.
This guarantee doesn’t apply to local variables—those that are not
fields of a class. Thus, if within a method definition you have:
int x;
Then x will get some arbitrary value (as it does in C and C++); it will
not automatically be initialized to zero. You are responsible for
assigning an appropriate value before you use x. If you forget, Java
definitely improves on C++: You get a compile-time error telling you
the variable might not be initialized. (C++ compilers often warn you
about uninitialized variables, but in Java these are errors.)

<!-- Methods, Arguments,and Return Values -->
### 方法使用

Methods, Arguments,
and Return Values
In many languages (like C and C++), the term function is used to
describe a named subroutine. In Java, we use the term method, as in
“a way to do something.”
Methods in Java determine the messages an object can receive. The
fundamental parts of a method are the name, the arguments, the
return type, and the body. Here is the basic form:
ReturnType methodName( /* Argument list */ ) {
// Method body
}
ReturnType indicates the type of value produced by the method
when you call it. The argument list gives the types and names for the
information you pass into the method. The method name and
argument list are collectively called the signature of the method. The
signature uniquely identifies that method.
Methods in Java can only be created as part of a class. A method can
be called only for an object4, and that object must be able to perform that
method call. If you try to call the wrong method for an object,
you’ll get an error message at compile time.
You call a method for an object by giving the object reference followed
by a period (dot), followed by the name of the method and its
argument list, like this:
objectReference.methodName(arg1, arg2, arg3);
Consider a method f() that takes no arguments and returns a value
of type int. For a reference a that accepts calls to f(), you can say this:
int x = a.f();
The type of the return value must be compatible with the type of x.
This act of calling a method is sometimes termed sending a message
to an object. In the preceding example, the message is f() and the
object is a. Object-oriented programming can be summarized as
“sending messages to objects.”
The Argument List
The method argument list specifies the information you pass into the
method. As you might guess, this information—like everything else in
Java—takes the form of objects. The argument list must specify the
object types and the name of each object. As always, where you seem
to be handing objects around, you are actually passing references.5
The type of the reference must be correct, however. If a String
argument is expected, you must pass in a String or the compiler will
give an error.
Here is the definition for a method that takes a String as its
argument. It must be placed within a class for it to compile:
int storage(String s) {
return s.length() * 2;
}
This method calculates and delivers the number of bytes required to
hold the information in a particular String. The argument s is of
type String. Once s is passed into storage(), you can treat it like
any other object—you can send it messages. Here, we call length(),
which is a String method that returns the number of characters in a
String. The size of each char in a String is 16 bits, or two bytes.
You can also see the return keyword, which does two things. First, it
means “Leave the method, I’m done.” Second, if the method produces
a value, that value is placed right after the return statement. Here,
the return value is produced by evaluating the expression
s.length() * 2.
You can return any type you want, but if you don’t return anything at
all, you do so by indicating that the method produces void (nothing).
Here are some examples:
boolean flag() { return true; }
double naturalLogBase() { return 2.718; }
void nothing() { return; }
void nothing2() {}
When the return type is void, the return keyword is used only to
exit the method, and is therefore unnecessary if called at the end of the
method. You can return from a method at any point, but if you’ve
given a non-void return type, the compiler will force you to return
the appropriate type of value regardless of where you return.
It might look like a program is just a bunch of objects with methods
that take other objects as arguments and send messages to those other
objects. That is indeed much of what goes on, but in the following
Operators chapter you’ll learn how to do the detailed low-level work by
making decisions within a method. For this chapter, sending messages
will suffice.

<!-- Writing a Java Program -->
## 程序编写

There are several other issues you must understand before seeing your
first Java program.
Name Visibility
A problem in any programming language is the control of names. If
you use a name in one module of the program, and another
programmer uses the same name in another module, how do you
distinguish one name from another and prevent the two names from
“clashing?” In C this is especially challenging because a program is
often an unmanageable sea of names. C++ classes (on which Java
classes are modeled) nest functions within classes so they cannot clash
with function names nested within other classes. However, C++
continues to allow global data and global functions, so clashing is still
possible. To solve this problem, C++ introduced namespaces using
additional keywords.
Java avoided all these problems by taking a fresh approach. To
produce an unambiguous name for a library, the Java creators want
you to use your Internet domain name in reverse, because domain
names are guaranteed to be unique. Since my domain name is
MindviewInc.com, my foibles utility library is named
com.mindviewinc.utility.foibles. Following your
reversed domain name, the dots are intended to represent
subdirectories.
In Java 1.0 and Java 1.1 the domain extensions com, edu, org, net, etc., were
capitalized by convention, so the library would appear:
Com.mindviewinc.utility.foibles. Partway through the
development of Java 2, however, they discovered this caused
problems, so now the entire package name is lowercase.
This mechanism means all your files automatically live in their own
namespaces, and each class within a file has a unique identifier. This
way, the language prevents name clashes.
Using reversed URLs was a new approach to namespaces, never before
tried in another language. Java has a number of these “inventive”
approaches to problems. As you might imagine, adding a feature
without experimenting with it first risks discovering problems with
that feature in the future, after the feature is used in production code,
typically when it’s too late to do anything about it (some mistakes were
bad enough to actually remove things from the language).
The problem with associating namespaces with file paths using
reversed URLs is by no means one that causes bugs, but it does make
it challenging to manage source code. By using
com.mindviewinc.utility.foibles, I create a directory
hierarchy with “empty” directories com and mindviewinc whose
only job is to reflect the reversed URL. This approach seemed to open
the door to what you will encounter in production Java programs:
deep directory hierarchies filled with empty directories, not just for the
reversed URLs but also to capture other information. These long paths
are basically being used to store data about what is in the directory. If
you expect to use directories in the way they were originally designed,
this approach lands anywhere from “frustrating” to “maddening,” and
for production Java code you are essentially forced to use one of the
IDEs specifically designed to manage code that is laid out in this
fashion, such as NetBeans, Eclipse, or IntelliJ IDEA. Indeed, those
IDEs both manage and create the deep empty directory hierarchies for
you.
For this book’s examples, I didn’t want to burden you with the extra
annoyance of the deep hierarchies, which would have effectively
required you to learn one of the big IDEs before getting started. The
examples for each chapter are in a shallow subdirectory with a name
reflecting the chapter title. This caused me occasional struggles with
tools that follow the deep-hierarchy approach.
Using Other Components
Whenever you use a predefined class in your program, the compiler
must locate that class. In the simplest case, the class already exists in
the source-code file it’s being called from. In that case, you simply use
the class—even if the class doesn’t get defined until later in the file
(Java eliminates the so-called “forward referencing” problem).
What about a class that exists in some other file? You might think the
compiler should be smart enough to go and find it, but there is a
problem. Imagine you use a class with a particular name, but more
than one definition for that class exists (presumably these are different
definitions). Or worse, imagine that you’re writing a program, and as
you’re building it you add a new class to your library that conflicts with
the name of an existing class.
To solve this problem, you must eliminate all potential ambiguities by
telling the Java compiler exactly what classes you want using the
import keyword. import tells the compiler to bring in a package,
which is a library of classes. (In other languages, a library could
consist of functions and data as well as classes, but remember that all
activities in Java take place within classes.)
Much of the time you’ll use components from the standard Java
libraries that come with your compiler. With these, you don’t worry
about long, reversed domain names; you just say, for example:
import java.util.ArrayList;
This tells the compiler to use Java’s ArrayList class, located in its
util library.
However, util contains a number of classes, and you might want to
use several of them without declaring them all explicitly. This is easily
accomplished by using * to indicate a wild card:
import java.util.*;
The examples in this book are small and for simplicity’s sake we’ll
usually use the * form. However, many style guides specify that each
class should be individually imported.
The static Keyword
Creating a class describes the look of its objects and the way they
behave. You don’t actually get an object until you create one using
new, at which point storage is allocated and methods become
available.
This approach is insufficient in two cases. Sometimes you want only a
single, shared piece of storage for a particular field, regardless of how
many objects of that class are created, or even if no objects are created.
The second case is if you need a method that isn’t associated with any
particular object of this class. That is, you need a method you can call
even if no objects are created.
The static keyword (adopted from C++) produces both these
effects. When you say something is static, it means the field or
method is not tied to any particular object instance. Even if you’ve
never created an object of that class, you can call a static method or
access a static field. With ordinary, non-static fields and
methods, you must create an object and use that object to access the
field or method, because non-static fields and methods must target
a particular object.6
Some object-oriented languages use the terms class data and class
methods, meaning that the data and methods exist only for the class as
a whole, and not for any particular objects of the class. Sometimes
Java literature uses these terms too.
To make a field or method static, you place the keyword before the
definition. The following produces and initializes a static field:
class StaticTest {
static int i = 47;
}
Now even if you make two StaticTest objects, there is still only
one piece of storage for StaticTest.i. Both objects share the same
i. For example:
StaticTest st1 = new StaticTest();
StaticTest st2 = new StaticTest();
Both st1.i and st2.i have the same value of 47 since they are the
same piece of memory.
There are two ways to refer to a static variable. As in the preceding
example, you can name it via an object; for example, st2.i. You can
also refer to it directly through its class name, something you cannot
do with a non-static member:
StaticTest.i++;
The ++ operator adds one to the variable. Now both st1.i and
st2.i have the value 48.
Using the class name is the preferred way to refer to a static
variable because it emphasizes the variable’s static nature7.
Similar logic applies to static methods. You can refer to a static
method either through an object as you can with any method, or with
the special additional syntax ClassName.method(). You define a
static method like this:
class Incrementable {
static void increment() { StaticTest.i++; }
}
The Incrementable method increment() increments the
static int i using the ++ operator. You can call increment()
in the typical way, through an object:
Incrementable sf = new Incrementable();
sf.increment();
However, the preferred approach is to call it directly through its class:
Incrementable.increment();
static applied to a field definitely changes the way the data is
created—one for each class versus the non-static one for each
object. When applied to a method, static allows you to call that
method without creating an object. This is essential, as you will see, in
defining the main() method that is the entry point for running an
application.

<!-- Your First Java Program -->
## 小试牛刀

Finally, here’s the first complete program. It starts by displaying a
String, followed by the date, using the Date class from the Java
standard library.
// objects/HelloDate.java
import java.util.*;
public class HelloDate {
public static void main(String[] args) {
System.out.println("Hello, it's: ");
System.out.println(new Date());
}
}
In this book I treat the first line specially; it’s always a comment line
containing the the path information to the file (using the directory
name objects for this chapter) followed by the file name. I have
tools to automatically extract and test the book’s code based on this
information, and you will easily find the code example in the
repository by referring to the first line.
At the beginning of each program file, you must place import
statements to bring in any extra classes you need for the code in that
file. I say “extra” because there’s a certain library of classes
automatically included in every Java file: java.lang. Start up your
Web browser and look at the documentation from Oracle. If you
haven’t downloaded the JDK documentation from the Oracle Java site, do so
now8, or find it on the Internet. If you look at the list of packages, you’ll see
all the different class libraries that come with Java.
Select java.lang. This will bring up a list of all the classes that are
part of that library. Since java.lang is implicitly included in every
Java code file, these classes are automatically available. There’s no
Date class listed in java.lang, which means you must import
another library to use that. If you don’t know the library where a
particular class is, or if you want to see all classes, select “Tree” in the
Java documentation. Now you can find every single class that comes
with Java. Use the browser’s “find” function to find Date. You’ll see it
listed as java.util.Date, which tells you it’s in the util library
and you must import java.util.* in order to use Date.
If inside the documentation you select java.lang, then System,
you’ll see that the System class has several fields, and if you select
out, you’ll discover it’s a static PrintStream object. Since it’s
static, you don’t need to use new—the out object is always there,
and you can just use it. What you can do with this out object is
determined by its type: PrintStream. Conveniently,
PrintStream is shown in the description as a hyperlink, so if you
click on that, you’ll see a list of all the methods you can call for
PrintStream. There are quite a few, and these are covered later in
the book. For now all we’re interested in is println(), which in
effect means “Print what I’m giving you out to the console and end
with a newline.” Thus, in any Java program you can write something
like this:
System.out.println("A String of things");
whenever you want to display information to the console.
One of the classes in the file must have the same name as the file. (The
compiler complains if you don’t do this.) When you’re creating a
standalone program such as this one, the class with the name of the
file must contain an entry point from which the program starts. This
special method is called main(), with the following signature and
return type:
public static void main(String[] args) {
The public keyword means the method is available to the outside
world (described in detail in the Implementation Hiding chapter). The
argument to main() is an array of String objects. The args won’t
be used in the current program, but the Java compiler insists they be
there because they hold the arguments from the command line.
The line that prints the date is interesting:
System.out.println(new Date());
The argument is a Date object that is only created to send its value
(automatically converted to a String) to println(). As soon as
this statement is finished, that Date is unnecessary, and the garbage
collector can come along and get it anytime. We don’t worry about
cleaning it up.
When you look at the JDK documentation, you see that System has
many other useful methods (one of Java’s assets is its large set of
standard libraries). For example:
// objects/ShowProperties.java
public class ShowProperties {
public static void main(String[] args) {
System.getProperties().list(System.out);
System.out.println(System.getProperty("user.name"));
System.out.println(
System.getProperty("java.library.path"));
}
}
/* Output: (First 20 Lines)
-- listing properties --
java.runtime.name=Java(TM) SE Runtime Environment
sun.boot.library.path=C:\Program
Files\Java\jdk1.8.0_112\jr...
java.vm.version=25.112-b15
java.vm.vendor=Oracle Corporation
java.vendor.url=http://java.oracle.com/
path.separator=;
java.vm.name=Java HotSpot(TM) 64-Bit Server VM
file.encoding.pkg=sun.io
user.script=
user.country=US
sun.java.launcher=SUN_STANDARD
sun.os.patch.level=
java.vm.specification.name=Java Virtual Machine
Specification
user.dir=C:\Users\Bruce\Documents\GitHub\on-ja...
java.runtime.version=1.8.0_112-b15
java.awt.graphicsenv=sun.awt.Win32GraphicsEnvironment
java.endorsed.dirs=C:\Program
Files\Java\jdk1.8.0_112\jr...
os.arch=amd64
java.io.tmpdir=C:\Users\Bruce\AppData\Local\Temp\
...
*/
The first line in main() displays all “properties” from the system
where you are running the program, so it gives you environment
information. The list() method sends the results to its argument,
System.out. You will see later in the book that you can send the
results elsewhere, to a file, for example. You can also ask for a specific
property—here, user.name and java.library.path.
The /* Output: tag at the end of the file indicates the beginning of
the output generated by this file. Most examples in this book that
produce output will contain the output in this commented form, so
you see the output and know it is correct. The tag allows the output to
be automatically updated into the text of this book after being checked
with a compiler and executed.
Compiling and Running
To compile and run this program, and all the other programs in this
book, you must first have a Java programming environment. The
installation process was described in Installing Java and the Book
Examples. If you followed these instructions, you are using the Java
Developer’s Kit (JDK), free from Oracle. If you use another
development system, look in the documentation for that system to
determine how to compile and run programs.
Installing Java and the Book Examples also describes how to install the
examples for this book. Move to the subdirectory named
objects and type:
javac HelloDate.java
This command should produce no response. If you get any kind of an
error message, it means you haven’t installed the JDK properly and
you must investigate those problems.
On the other hand, if you just get your command prompt back, you can
type:
java HelloDate
and you’ll get the message and the date as output.
This is the process to compile and run each program (containing a
main()) in this book9. However, the source code for this book also has a file
called build.gradle in the root directory, and this
contains the Gradle configuration for automatically building, testing,
and running the files for the book. When you run the gradlew
command for the first time, Gradle will automatically install itself
(assuming you have Java installed).

<!-- Coding Style -->
## 编码风格

The style described in the document Code Conventions for the Java
Programming Language 10 is to capitalize the first letter of a class name. If
the class name consists of several words, they are run
together (that is, you don’t use underscores to separate the names),
and the first letter of each embedded word is capitalized, such as:
class AllTheColorsOfTheRainbow { // ...
This is sometimes called “camel-casing.” For almost everything else—
methods, fields (member variables), and object reference names—the
accepted style is just as it is for classes except that the first letter of the
identifier is lowercase. For example:
class AllTheColorsOfTheRainbow {
int anIntegerRepresentingColors;
void changeTheHueOfTheColor(int newHue) {
// ...
}
// ...
}
The user must also type these long names, so be merciful.
The Java code you find in the Oracle libraries also follows the
placement of open-and-close curly braces in this book.

## 本章小结

This chapter shows you just enough Java so you understand how to
write a simple program. You’ve also seen an overview of the language
and some of its basic ideas. However, the examples so far have all been
of the form “Do this, then do that, then do something else.” The next
two chapters will introduce the basic operators used in Java
programming, and show you how to control the flow of your program.
1. This can be a flashpoint. There are those who say, “Clearly, it’s a
pointer,” but this presumes an underlying implementation. Also,
the syntax of Java references is much more akin to C++ references
than to pointers. In the 1st edition of Thinking in Java, I chose to
invent a new term, “handle,” because C++ references and Java
references have some important differences. I was coming out of
C++ and did not want to confuse the C++ programmers whom I
assumed would be the largest audience for Java. In the 2nd
edition of Thinking in Java, I decided that “reference” was the
more commonly used term, and that anyone changing from C++
would have a lot more to cope with than the terminology of
references, so they might as well jump in with both feet. However,
there are people who disagree even with the term “reference.” In
one book I read that it was “completely wrong to say that Java
supports pass by reference,” because Java object identifiers
(according to that author) are actually “object references.” And
(he goes on) everything is actually pass by value. So you’re not
passing by reference, you’re “passing an object reference by
value.” One could argue for the precision of such convoluted
explanations, but I think my approach simplifies the
understanding of the concept without hurting anything (well,
language lawyers may claim I’m lying to you, but I’ll say that I’m
providing an appropriate abstraction).↩
2. Most microprocessor chips do have additional cache memory but
this is organized as traditional memory and not as registers↩
3. An example of this is the String pool. All literal Strings and
String-valued constant expressions are interned automatically
and put into special static storage. ↩
4. static methods, which you’ll learn about soon, can be called for
the class, without an object.↩
5. With the usual exception of the aforementioned “special” data
types boolean, char, byte, short, int, long, float, and double. In general,
though, you pass objects, which really means
you pass references to objects. ↩
6. static methods don’t require objects to be created before they
are used, so they cannot directly access non-static members or
methods by calling those other members without referring to a
named object (since non-static members and methods must be
tied to a particular object).↩
7. In some cases it also gives the compiler better opportunities for
optimization↩
8. Note this documentation doesn’t come packed with the JDK; you
must do a separate download to get it. ↩
9. For every program in this book to compile and run through the
command line, you might also need to set your CLASSPATH. ↩
10. (Search the Internet; also look for “Google Java Style”). To keep
code listings narrow for this book, not all these guidelines could
be followed, but you’ll see that the style I use here matches the
Java standard as much as possible.↩
<!-- 分页 -->
<div style="page-break-after: always;"></div>

