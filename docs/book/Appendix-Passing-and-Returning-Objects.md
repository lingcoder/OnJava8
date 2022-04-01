[TOC]

<!-- Appendix: Passing and Returning Objects -->
# 附录:对象传递和返回

> 到现在为止，你已经对“传递”对象实际上是传递引用这一想法想法感到满意。

在许多编程语言中，你可以使用该语言的“常规”方式来传递对象，并且大多数情况下一切正常。 但是通常会出现这种情况，你必须做一些不平常的事情，突然事情变得更加复杂。 Java也不例外，当您传递对象并对其进行操作时，准确了解正在发生的事情很重要。 本附录提供了这种见解。

提出本附录问题的另一种方法是，如果你之前使用类似C++的编程语言，则是“ Java是否有指针？” Java中的每个对象标识符（除原语外）都是这些指针之一，但它们的用法是不仅受编译器的约束，而且受运行时系统的约束。 换一种说法，Java有指针，但没有指针算法。 这些就是我一直所说的“引用”，您可以将它们视为“安全指针”，与小学的安全剪刀不同-它们不敏锐，因此您不费吹灰之力就无法伤害自己，但是它们有时可能很乏味。

<!-- Passing References -->

## 传递引用

<!-- Making Local Copies -->

当你将引用传递给方法时，它仍指向同一对象。 一个简单的实验演示了这一点：

```java
// references/PassReferences.java
public class PassReferences {
public static void f(PassReferences h) {
    	System.out.println("h inside f(): " + h);
    }
    public static void main(String[] args) {
        PassReferences p = new PassReferences();
        System.out.println("p inside main(): " + p);
        f(p);
    }
}
/* Output:
p inside main(): PassReferences@15db9742
h inside f(): PassReferences@15db9742
*/
```

方法  `toString() ` 在打印语句中自动调用，并且 `PassReferences` 直接从 `Object` 继承而无需重新定义 `toString（）` 。 因此，使用的是 `Object` 的 `toString（）` 版本，它打印出对象的类，然后打印出该对象所在的地址（不是引用，而是实际的对象存储）。

## 本地拷贝


<!-- Controlling Cloneability -->
## 控制克隆


<!-- Immutable Classes -->
## 不可变类


<!-- Summary -->
## 本章小结



<!-- 分页 -->

<div style="page-break-after: always;"></div>