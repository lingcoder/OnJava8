[TOC]

<!-- Annotations -->

# 第二十三章 注解

注解（也被成为元数据）为我们在代码中添加信息提供了一种形式化的方式，使我们可以在稍后的某个时刻更容易的使用这些数据。

注解在一定程度上是把元数据和源代码文件结合在一起的趋势所激发的，而不是保存在外部文档。这同样是对像 C# 语言对于 Java 语言特性压力的一种回应。

注解是 Java 5 所引入的众多语言变化之一。它们提供了 Java 无法表达的但是你需要完整表述程序所需的信息。因此，注解使得我们可以以编译器验证的格式存储程序的额外信息。注解可以生成描述符文件，甚至是新的类定义，并且有助于减轻编写“样板”代码的负担。通过使用注解，你可以将元数据保存在 Java 源代码中。并拥有如下有下优势：简单易读的代码，编译器类型检查，使用 annotation API 为自己的注解构造处理工具。即使 Java 定义了一些类型的元数据，但是一般来说注解类型的添加和如何使用完全取决于你。

注解的语法十分简单，主要是在现有语法中添加 @ 符号。Java 5 引入了前三种定义在 **java.lang** 包中的注解：

- **@Override**：表示当前的方法定义将覆盖基类的方法。如果你不小心拼写错误，或者方法签名被错误拼写的时候，编译器就会发出错误提示。
- **@Deprecated**：如果使用该注解的元素被调用，编译器就会发出警告信息。
- **@SuppressWarnings**：关闭不当的编译器警告信息。
- **@SafeVarargs**：在 Java 7 中加入用于禁止对具有泛型varargs参数的方法或构造函数的调用方发出警告。
- **@FunctionalInterface**：Java 8 中加入用于表示类型声明为函数式接口

还有 5 种额外的注解类型用于创造新的注解。你将会在这一章学习它们。

每当创建涉及重复工作的类或接口时，你通常可以使用注释来自动化和简化流程。例如在 Enterprise JavaBean（EJB）中的许多额外工作就是通过注解来消除的。

注解的出现可以替代一些现有的系统，例如 XDoclet，它是一种独立的文档化工具，专门设计用来生成注解风格的文档。与之相比，注解是真正语言层级的概念，以前构造出来就享有编译器的类型检查保护。注解在源代码级别保存所有信息而不是通过注释文字，这使得代码更加整洁和便于维护。通过使用拓展的 annotation API 或稍后在本章节可以看到的外部的字节码工具类库，你会拥有对源代码及字节码强大的检查与操作能力。

<!-- Basic Syntax -->

## 基本语法

<!-- Writing Annotation Processors -->

在下面的例子中，使用 `@Test` 对 `testExecute()` 进行注解。该注解本身不做任何事情，但是编译器要保证其类路径上有 `@Test` 注解的定义。你将在本章看到，我们通过注解创建了一个工具用于运行这个方法：

```java
// annotations/Testable.java
package annotations;
import onjava.atunit.*;
public class Testable {
    public void execute() {
        System.out.println("Executing..");
    }
    @Test
    void testExecute() { execute(); }
}
```

被注解标注的方法和其他的方法没有任何区别。在这个例子中，注解 `@Test` 可以和任何修饰符共同用于方法，注入 **public**、**static** 或 **void**。用语法的角度上看，注解和使用方式和修饰符的使用方式一致。

### 定义注解

如下是一个注解的定义。注解的定义看起来很像接口的定义。事实上，它们和其他 Java 接口一样，也会被编译成 class 文件。

```java
// onjava/atunit/Test.java
// The @Test tag
package onjava.atunit;
import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {}
```

除了 @ 符号之外， `@Test` 的定义看起来更像一个空接口。注解的定义也需要一些元注解（meta-annoation），比如 `@Target` 和 `@Retention`。`@Target` 定义你的注解可以应用在哪里（例如是方法还是字段）。`@Retention` 定义了注解在哪里可用，在源代码中（SOURCE），class文件（CLASS）中或者是在运行时（RUNTIME）。

注解通常会包含一些表示特定值的元素。当分析处理注解的时候，程序或工具可以利用这些值。注解的元素看起来就像接口的方法，但是可以为其指定默认值。

不包含任何元素的注解称为标记注解（marker annotation），例如上例中的 `@Test` 就是标记注解。

下面是一个简单的注解，我们可以用它来追踪项目中的用例。程序员可以使用该注解来标注满足特定用例的一个方法或者一组方法。于是，项目经理可以通过统计已经实现的用例来掌控项目的进展，而开发者在维护项目时可以轻松的找到用例用于更新，或者他们可以调试系统中业务逻辑。

```java
// annotations/UseCase.java
import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseCase {
    int id();
    String description() default "no description";
}
```

注意 **id** 和 **description** 与方法定义类似。由于编译器会对 **id** 进行类型检查，因此将跟踪数据库与用例文档和源代码相关联是可靠的方式。**description** 元素拥有一个 **default** 值，如果在注解某个方法时没有给出 **description** 的值。则该注解的处理器会使用此元素的默认值。

在下面的类中，有三个方法被注解为用例：

```java
// annotations/PasswordUtils.java
import java.util.*;
public class PasswordUtils {
    @UseCase(id = 47, description =
            "Passwords must contain at least one numeric")
    public boolean validatePassword(String passwd) {
        return (passwd.matches("\\w*\\d\\w*"));
    }
    @UseCase(id = 48)
    public String encryptPassword(String passwd) {
        return new StringBuilder(passwd)
                .reverse().toString();
    }
    @UseCase(id = 49, description =
            "New passwords can't equal previously used ones")
    public boolean checkForNewPassword(
            List<String> prevPasswords, String passwd) {
        return !prevPasswords.contains(passwd);
    }
}
```

注解的元素在使用时表现为 名-值 对的形式，并且需要放置在 `@UseCase` 声明之后的括号内。在 `encryptPassword()` 方法的注解中，并没有给出 **description** 的默认值，所以在 **@interface UseCase** 的注解处理器分析处理这个类的时候会使用该元素的默认值。

你应该能够想象到如何使用这套工具来“勾勒”出将要建造的凶，然后在建造的过程中逐渐实现系统的各项功能。

## 编写注解处理器


<!-- Using javac to Process Annotations -->
## 使用javac处理注解


<!-- Annotation-Based Unit Testing -->
## 基于注解的单元测试


<!-- Summary -->
## 本章小结



<!-- 分页 -->

<div style="page-break-after: always;"></div>