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

### 元注解

Java 语言中目前有 5 种标准注解（前面介绍过），以及 5 种元注解。元注解用于注解其他的注解

| 注解        | 解释                                                         |
| ----------- | ------------------------------------------------------------ |
| @Target     | 表示注解可以用于哪些地方。可能的 **ElementType** 参数包括：<br/>**CONSTRUCTOR**：构造器的声明<br/>**FIELD**：字段声明（包括 enum 实例）<br/>**LOCAL_VARIABLE**：局部变量声明<br/>**METHOD**：方法声明<br/>**PACKAGE**：包声明<br/>**PARAMETER**：参数声明<br/>**TYPE**：类、接口（包括注解类型）或者 enum 声明 |
| @Retention  | 表示注解信息保存的时长。可选的 **RetentionPolicy** 参数包括：<br/>**SOURCE**：注解将被编译器丢弃<br/>**CLASS**：注解在 class 文件中可用，但是会被 VM 丢弃。<br/>**RUNTIME**：VM 将在运行期也保留注解，因此可以通过反射机制读取注解的信息。 |
| @Documented | 将此注解保存在 Javadoc 中                                    |
| @Interited  | 允许子类继承父类的注解                                       |
| @Repeatable | 允许一个注解可以被使用一次或者多次（Java 8）。               |

大多数时候，程序员定义自己的注解，并编写自己的处理器来处理他们。

## 编写注解处理器

如果没有用于读取注解的工具，那么注解不会比注释更有用。使用注解中一个很重要的部分就是，创建与使用注解处理器。Java 拓展了反射机制的 API 用于帮助你创造这类工具。同时他还提供了 javac 编译器钩子在编译时使用注解。

下面是一个非常简单的注解处理器，我们用它来读取被注解的 **PasswordUtils** 类，并且使用反射机制来寻找 **@UseCase** 标记。给定一组 **id** 值，然后列出在 **PasswordUtils** 中找到的用例，以及缺失的用例。

```java
// annotations/UseCaseTracker.java
import java.util.*;
import java.util.stream.*;
import java.lang.reflect.*;
public class UseCaseTracker {
    public static void
    trackUseCases(List<Integer> useCases, Class<?> cl) {
        for(Method m : cl.getDeclaredMethods()) {
            UseCase uc = m.getAnnotation(UseCase.class);
            if(uc != null) {
                System.out.println("Found Use Case " +
                        uc.id() + "\n " + uc.description());
                useCases.remove(Integer.valueOf(uc.id()));
            }
        }
        useCases.forEach(i ->
                System.out.println("Missing use case " + i));
    }
    public static void main(String[] args) {
        List<Integer> useCases = IntStream.range(47, 51)
                .boxed().collect(Collectors.toList());
        trackUseCases(useCases, PasswordUtils.class);
    }
}
```

输出为：

```java
Found Use Case 48
no description
Found Use Case 47
Passwords must contain at least one numeric
Found Use Case 49
New passwords can't equal previously used ones
Missing use case 50
```

这个程序用了两个反射的方法：`getDeclaredMethods()`  和 `getAnnotation()`，它们都属于 **AnnotatedElement** 接口（**Class**，**Method** 与 **Field** 类都实现了该接口）。`getAnnotation()` 方法返回指定类型的注解对象，在本例中就是 “**UseCase**”。如果被注解的方法上没有该类型的注解，返回值就为 **null**。我们通过调用 `id()` 和 `description()` 方法来提取元素值。注意 `encryptPassword()` 方法在注解的时候没有指定 **description** 的值，因此处理器在处理它对应的注解时，通过 `description()` 取得的是默认值 “no description”。

### 注解元素

在 **UseCase.java** 中定义的 **@UseCase** 的标签包含 int 元素 **id** 和 String 元素 **description**。注解元素可用的类型如下所示：

- 所有基本类型（int、float、boolean等）
- String
- Class
- enum
- Annotation
- 以上类型的数组

如果你使用了其他类型，编译器就会报错。注意，也不允许使用任何包装类型，但是由于自动装箱的存在，这不算是什么限制。注解也可以作为元素的类型。稍后你会看到，注解嵌套是一个非常有用的技巧。

### 默认值限制

编译器对于元素的默认值有些过于挑剔。首先，元素不能有不确定的值。也就是说，元素要么有默认值，要么就在使用注解时提供元素的值。

这里有另外一个限制：任何非基本类型的元素， 无论是在源代码声明时还是在注解接口中定义默认值时，都不能使用 null 作为其值。这个限制使得处理器很难表现一个元素的存在或者缺失的状态，因为在每个注解的声明中，所有的元素都存在，并且具有相应的值。为了绕开这个约束，可以自定义一些特殊的值，比如空字符串或者负数用于表达某个元素不存在。

```java
// annotations/SimulatingNull.java
import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimulatingNull {
    int id() default -1;
    String description() default "";
}
```

这是一个在定义注解的习惯用法。

### 生成外部文件



<!-- Using javac to Process Annotations -->

## 使用javac处理注解

当有些框架需要一些额外的信息才能与你的源代码协同工作，这种情况下注解就会变得十分有用。像 Enterprise JavaBeans (EJB3 之前)这样的技术，每一个 Bean 都需要需要大量的接口和部署描述文件，而这些就是“样板”文件。Web Service，自定义标签库以及对象/关系映射工具（例如 Toplink 和 Hibernate）通常都需要 XML 描述文件，而这些文件脱离于代码之外。除了定义 Java 类，程序员还必须忍受沉闷，重复的提供某些信息，例如类名和包名等已经在原始类中已经提供的信息。每当你使用外部描述文件时，他就拥有了一个类的两个独立信息源，这经常导致代码的同步问题。同时这也要求了为项目工作的程序员在知道如何编写 Java 程序的同时，也必须知道如何编辑描述文件。

假设你想提供一些基本的对象/关系映射功能，能够自动生成数据库表。你可以使用 XML 描述文件来指明类的名字、每个成员以及数据库映射的相关信息。但是，通过使用注解，你可以把所有信息都保存在 **JavaBean** 源文件中。为此你需要一些用于定义数据库名称、数据库列以及将 SQL 类型映射到属性的注解。

以下是一个注解的定义，它告诉注解处理器应该创建一个数据库表：

```java
// annotations/database/DBTable.java
package annotations.database;
import java.lang.annotation.*;
@Target(ElementType.TYPE) // Applies to classes only
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {
    String name() default "";
}
```

在 `@Target` 注解中指定的每一个 **ElementType** 就是一个约束，它告诉编译器，这个自定义的注解只能用于指定的类型。你可以指定 **enum ElementType** 中的一个值，或者以逗号分割的形式指定多个值。如果想要将注解应用于所有的 **ElementType**，那么可以省去 `@Target` 注解，但是这并不常见。

注意 **@DBTable** 中有一个 `name()` 元素，该注解通过这个元素为处理器创建数据库时提供表的名字。

如下是修饰字段的注解：

```java
// annotations/database/Constraints.java
package annotations.database;
import java.lang.annotation.*;
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraints {
    boolean primaryKey() default false;
    boolean allowNull() default true;
    boolean unique() default false;
}
```

```java
// annotations/database/SQLString.java
package annotations.database;
import java.lang.annotation.*;
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLString {
    int value() default 0;
    String name() default "";
    Constraints constraints() default @Constraints;
}
```

```java
// annotations/database/SQLInteger.java
package annotations.database;
import java.lang.annotation.*;
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLInteger {
    String name() default "";
    Constraints constraints() default @Constraints;
}
```

**@Constraints** 注解允许处理器提供数据库表的元数据。**@Constraints** 代表了数据库通常提供的约束的一小部分，但是它索要表达的思想已经很清楚了。`primaryKey()`，`allowNull()` 和 `unique()` 元素明智的提供了默认值，从而使得在大多数情况下，该注解的使用者不需要输入太多东西。

另外两个 **@interface** 定义的是 SQL 类型。如果希望这个框架更有价值的话，我们应该为每个 SQL 类型都定义相应的注解。不过为为示例，两个元素足够了。





<!-- Annotation-Based Unit Testing -->

## 基于注解的单元测试


<!-- Summary -->
## 本章小结



<!-- 分页 -->

<div style="page-break-after: always;"></div>