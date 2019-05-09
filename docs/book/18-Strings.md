[TOC]

<!--  -->
# 第十八章 字符串
可以证明，字符串操作是计算机程序设计中最常见的行为之一。
在Java大展拳脚的Web系统中更是如此。在本章中，我们将深入学习在Java语言中应用最广泛的**String**类，并研究与之相关的类及工具。
<!-- Immutable Strings -->
## 字符串的不可变
**String**对象是不可变的。查看JDK文档你就会发现，**String**类中每一个看起来会修改**String**值的方法，实际上都是创建了一个全新的**String**对象,以包含修改后的字符串内容。而最初的**String**对象则丝毫未动。

看看下面的代码：
```java
// strings/Immutable.java
public class Immutable { 
    public static String upcase(String s) { 
        return s.toUpperCase(); 
    } 
    public static void main(String[] args) { 
        String q = "howdy"; System.out.println(q); // howdy 
        String qq = upcase(q); 
        System.out.println(qq); // HOWDY 
        System.out.println(q); // howdy 
    } 
} 
/* Output: 
howdy
HOWDY 
howdy
*/ 
```
当把q传递给upcase()方法时，实际传递的是引用的一个拷贝。其实，每当把String对象作为方法的参数时，都会复制一份引用，而该引用所指向的对象其实一直待在单一的物理位置上，从未动过。

回到upcase()的定义，传入其中的引用有了名字s，只有upcase()运行的时候，局部引用s才存在。一旦upcase()运行结束，s就消失了。当然了，upcase()的返回值，其实是最终结果的引用。这足以说明，upcase()返回的引用已经指向了一个新的对象，而q仍然在原来的位置。

**String**的这种行为正是我们想要的。例如：
```java
String s = "asdf";
String x = Immutable.upcase(s);
```
难道你真的希望upcase()方法改变其参数吗？对于一个方法而言，参数是为该方法提供信息的，而不是想让该方法改变自己的。在阅读这段代码时，读者自然会有这样的感觉。这一点很重要，正是有了这种保障，才使得代码易于编写和阅读。
<!-- Overloading + vs. StringBuilder -->
## 重载和StringBuilder
**String**对象是不可变的，你可以给一个**String**对象添加任意多的别名。因为**String**是只读的，所以指向它的任何引用都不可能改它的值，因此，也就不会影响到其他引用。

不可变性会带来一定的效率问题。为**String**对象重载的“+”操作符就是一个例子。重载的意思是，一个操作符在用于特定的类时，被赋予了特殊的意义(用于**String**的“+”与“+=”是Java中仅有的两个重载过的操作符，Java不允许程序员重载任何其他的操作符)。

操作符“+”可以用来连接**String**
```java
// strings/Concatenation.java

public class Concatenation {
     public static void main(String[] args) { 
         String mango = "mango"; 
         String s = "abc" + mango + "def" + 47; 
         System.out.println(s);
    } 
} 
/* Output:
abcmangodef47 
*/
```
可以想象一下，这段代码可能是这样工作的：**String**可能有一个append()方法，它会生成一个新的**String**对象，以包含“abc”与**mango**连接后的字符串。然后，该对象再与“def”相连，生成另一个新的对象，依此类推。

这种方式当然是可行的，但是为了生成最终的**String**对象，会产生一大堆需要垃圾回收的中间对象。我猜想，Java设计者一开始就是这么做的(这也是软件设计中的一个教训：除非你用代码将系统实现，并让它运行起来，否则你无法真正了解它会有什么问题)，然后他们发现其性能相当糟糕。

想看看以上代码到底是如何工作的吗？可以用JDK自带的javap来反编译以上代码。命令如下：
```java
javap -c Concatenation
```
这里的-c标志表示将生成JVM字节码。我们剔除不感兴趣的部分，然后做细微的修改，于是有了一下的字节码：
```
public static void main(java.lang.String[]); 
Code:
Stack=2, Locals=3, Args_size=1
0: ldc #2; //String mango 
2: astore_1 
3: new #3; //class StringBuilder 
6: dup 
7: invokespecial #4; //StringBuilder."<init>":() 
10: ldc #5; //String abc 
12: invokevirtual #6; //StringBuilder.append:(String) 
15: aload_1 
16: invokevirtual #6; //StringBuilder.append:(String) 
19: ldc #7; //String def 
21: invokevirtual #6; //StringBuilder.append:(String) 
24: bipush 47 
26: invokevirtual #8; //StringBuilder.append:(I) 
29: invokevirtual #9; //StringBuilder.toString:() 
32: astore_2 
33: getstatic #10; //Field System.out:PrintStream;
36: aload_2 
37: invokevirtual #11; //PrintStream.println:(String) 
40: return
```
如果你有汇编语言的经验，以上代码应该很眼熟(其中的**dup**和**invokevirtual**语句相当于Java虚拟机上的汇编语句。即使你完全不了解汇编语言也无需担心)。需要重点注意的是：编译器自动引入了**java.lang.StringBuilder**类。虽然源代码中并没有使用**StringBuilder**类，但是编译器却自作主张地使用了它，就因为它更高效。
在这里，编译器创建了一个**StringBuilder**对象，用于构建最终的**String s**，并对每个字符串调用了一次**append()**方法，共计4次。最后调用**toString()**生成结果，并存为**s**(使用的命令为astore_2)。
<!-- Unintended Recursion -->
## 意外递归


<!-- Operations on Strings -->
## 字符串操作


<!-- Formatting Output -->
## 格式化输出


<!-- Regular Expressions -->
## 常规表达式


<!-- Scanning Input -->
## 扫描输入


<!-- StringTokenizer -->
## StringTokenizer类


<!-- Summary -->
## 本章小结







<!-- 分页 -->

<div style="page-break-after: always;"></div>