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

这些 SQL 类型具有 `name()` 元素和 `constraints()` 元素。后者利用了嵌套注解的功能，将数据库列的类型约束信息嵌入其中。注意 `constraints()` 元素的默认值时 **@Constraints**。由于在 **@Constraints** 注解类型之后，没有在括号中指明 **@Constraints** 元素的值，因此，**constraints()** 的默认值为所有元素都为默认值的 **@Constraints** 注解。如果要使得嵌入的  **@Constraints**  注解中的 `unique()` 元素为 true，并作为 `constraints()` 元素的默认值，你可以像如下定义：

```java
// annotations/database/Uniqueness.java
// Sample of nested annotations
package annotations.database;
public @interface Uniqueness {
    Constraints constraints()
            default @Constraints(unique = true);
}
```

下面是一个简单的，使用了如上注解的类：

```java
// annotations/database/Member.java
package annotations.database;
@DBTable(name = "MEMBER")
public class Member {
    @SQLString(30) String firstName;
    @SQLString(50) String lastName;
    @SQLInteger Integer age;
    @SQLString(value = 30,
            constraints = @Constraints(primaryKey = true))
    String reference;
    static int memberCount;
    public String getReference() { return reference; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    @Override
    public String toString() { return reference; }
    public Integer getAge() { return age; }
}
```

类注解 **@DBTable** 注解给定了元素值 MEMBER，它将会作为标的名字。类的属性 **firstName** 和 **firstName** 都被注解为 **@SQLString** 类型并且给了默认元素值分别为 30 和 50。这些注解都有两个有趣的地方：首先，他们都使用了嵌入的 **@Constraints** 注解的默认值；其次，它们都是用了快捷方式特性。如果你在注解中定义了名为 **value** 的元素，并且在使用该注解时，**value** 为唯一一个需要赋值的元素，你就不需要使用名—值对的语法，你只需要在括号中给出 **value** 元素的值即可。这可以应用于任何合法类型的元素。这也限制了你必须将元素命名为 **value**，不过在上面的例子中，这样的注解语句也更易于理解：

```java
@SQLString(30)
```

处理器将在穿件表的时候使用该值设置 SQL 列的大小。

默认值的语法虽然很灵巧，但是它很快就变的复杂起来。以 **reference** 字段的注解为例，上面拥有 **@SQLString** 注解，但是这个字段也将成为表的主键，因此在嵌入的 **@Constraint** 注解中设定 **primaryKey** 元素的值。这时事情就变的复杂了。你不得不为这个嵌入的注解使用很长的名—值对的形式，来指定元素名称和 **@interface** 的名称。同时，由于有特殊命名的 **value** 也不是唯一需要赋值的元素，因此不能再使用快捷方式特性。如你所见，最终结果不算清晰易懂。

### 替代方案

可以使用多种不同的方式来定义自己的注解用于上述任务。例如，你可以使用一个单一的注解类 **@TableColumn**，它拥有一个 **enum** 元素，元素值定义了 **STRING**，**INTEGER**，**FLOAT** 等类型。这消除了每个 SQL 类型都需要定义一个 **@interface** 的负担，不过也使得用额外信息修饰 SQL 类型变的不可能，这些额外的信息例如长度或精度等，都可能是非常有用的。

你也可以使用一个 **String** 类型的元素来描述实际的 SQL 类型，比如 “VARCHAR(30)” 或者 “INTEGER”。这使得你可以修饰 SQL 类型，但是这也将 Java 类型到 SQL 类型的映射绑在了一起，这不是一个好的设计。你并不想在数据库更改之后重新编译你的代码；如果我们只需要告诉注解处理器，我们正在使用的是什么“口味（favor）”的 SQL，然后注解助力器来为我们处理 SQL 类型的细节，那将是一个优雅的设计。

第三种可行的方案是一起使用两个注解，**@Constraints** 和相应的 SQL 类型（例如，**@SQLInteger**）去注解同一个字段。这可能会让代码有些混乱，但是编译器允许你对同一个目标使用多个注解。在 Java 8，在使用多个注解的时候，你可以重复使用同一个注解。

### 注解不支持继承

你不能使用 **extends** 关键字来继承 **@interfaces**。这真是一个遗憾，如果可以定义**@TableColumn** 注解（参考前面的建议），同时嵌套一个 **@SQLType** 类型的注解，讲究将成为一个优雅的设计。按照这种方式，你可以通过继承 **@SQLType** 来创造各种 SQL 类型。例如 **@SQLInteger** 和 **@SQLString**。如果支持继承，就会大大减少打字的工作量并且使得语法更整洁。在 Java 的未来版本中，似乎没有任何关于让注解支持继承的提案，所以在当前情况下，上例中的解决方案可能已经是最佳方案了。

### 实现处理器

下面是一个注解处理器的例子，他将读取一个类文件，检查上面的数据库注解，并生成用于创建数据库的 SQL 命令：

```java
// annotations/database/TableCreator.java
// Reflection-based annotation processor
// {java annotations.database.TableCreator
// annotations.database.Member}
package annotations.database;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableCreator {
    public static void
    main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println(
                    "arguments: annotated classes");
            System.exit(0);
        }
        for (String className : args) {
            Class<?> cl = Class.forName(className);
            DBTable dbTable = cl.getAnnotation(DBTable.class);
            if (dbTable == null) {
                System.out.println(
                        "No DBTable annotations in class " +
                                className);
                continue;
            }
            String tableName = dbTable.name();
            // If the name is empty, use the Class name:
            if (tableName.length() < 1)
                tableName = cl.getName().toUpperCase();
            List<String> columnDefs = new ArrayList<>();
            for (Field field : cl.getDeclaredFields()) {
                String columnName = null;
                Annotation[] anns =
                        field.getDeclaredAnnotations();
                if (anns.length < 1)
                    continue; // Not a db table column
                if (anns[0] instanceof SQLInteger) {
                    SQLInteger sInt = (SQLInteger) anns[0];
                    // Use field name if name not specified
                    if (sInt.name().length() < 1)
                        columnName = field.getName().toUpperCase();
                    else
                        columnName = sInt.name();
                    columnDefs.add(columnName + " INT" +
                            getConstraints(sInt.constraints()));
                }
                if (anns[0] instanceof SQLString) {
                    SQLString sString = (SQLString) anns[0];
                    // Use field name if name not specified.
                    if (sString.name().length() < 1)
                        columnName = field.getName().toUpperCase();
                    else
                        columnName = sString.name();
                    columnDefs.add(columnName + " VARCHAR(" +
                            sString.value() + ")" +
                            getConstraints(sString.constraints()));
                }
                StringBuilder createCommand = new StringBuilder(
                        "CREATE TABLE " + tableName + "(");
                for (String columnDef : columnDefs)
                    createCommand.append(
                            "\n " + columnDef + ",");
                // Remove trailing comma
                String tableCreate = createCommand.substring(
                        0, createCommand.length() - 1) + ");";
                System.out.println("Table Creation SQL for " +
                        className + " is:\n" + tableCreate);
            }
        }
    }

    private static String getConstraints(Constraints con) {
        String constraints = "";
        if (!con.allowNull())
            constraints += " NOT NULL";
        if (con.primaryKey())
            constraints += " PRIMARY KEY";
        if (con.unique())
            constraints += " UNIQUE";
        return constraints;
    }
}
```

输出为：

```sql
Table Creation SQL for annotations.database.Member is:
CREATE TABLE MEMBER(
    FIRSTNAME VARCHAR(30));
Table Creation SQL for annotations.database.Member is:
CREATE TABLE MEMBER(
    FIRSTNAME VARCHAR(30),
    LASTNAME VARCHAR(50));
Table Creation SQL for annotations.database.Member is:
CREATE TABLE MEMBER(
    FIRSTNAME VARCHAR(30),
    LASTNAME VARCHAR(50),
    AGE INT);
Table Creation SQL for annotations.database.Member is:
CREATE TABLE MEMBER(
    FIRSTNAME VARCHAR(30),
    LASTNAME VARCHAR(50),
    AGE INT,
    REFERENCE VARCHAR(30) PRIMARY KEY);
```

`main()` 方法会循环处理命令行传入的每一个类名。每一个类都是用 ` forName()` 方法进行加载，并使用 `getAnnotation(DBTable.class)` 来检查该类是否带有 **@DBTable** 注解。如果存在，将表名存储起来。然后读取这个类的所有字段，并使用 `getDeclaredAnnotations()` 进行检查。这个方法返回一个包含特定字段上所有注解的数组。然后使用 **instanceof** 操作符判断这些注解是否是 **@SQLInteger** 或者 **@SQLString** 类型。如果是的话，在对应的处理块中将构造出相应的数据库列的字符串片段。注意，由于注解没有继承机制，如果要获取近似多台的行为，使用 `getDeclaredAnnotations()` 似乎是唯一的方式。

嵌套的 **@Constraint** 注解被传递给 `getConstraints()`方法，并用它来构造一个包含 SQL 约束的 String 对象。

需要提醒的是，上面演示的技巧对于真实的对象/映射关系而言，是十分幼稚的。使用 **@DBTable** 的注解来获取表的的名称，这使得如果要修改表的名字，则迫使你重新编译 Java 代码。这种效果并不理想。现在已经有了很多可用的框架，用于将对象映射到数据库中，并且越来越多的框架开始使用注解了。

<!-- Using javac to Process Annotations -->

## 使用javac处理注解

通过 **javac**，你可以通过创建编译时（compile-time）注解处理器在 Java 源文件上使用注解，而不是编译之后的 class 文件。但是这里有一个重大限制：你不能通过处理器来改变源代码。唯一影响输出的方式就是创建新的文件。

如果你的注解处理器创建了新的源文件，在新一轮处理中注解会检查源文件本身。工具在检测一轮之后持续循环，直到不再有新的源文件产生。然后它编译所有的源文件。

每一个你编写的注解都需要处理器，但是 **javac** 可以非常容易的将多个注解处理器合并在一起。你可以指定多个需要处理的类，并且你可以添加监听器用于监听注解处理完成后接到通知。

本节中的示例将帮助您开始学习，但如果您必须深入学习，请做好反复学习，大量访问 Google 和StackOverflow 的准备。

### 最简单的处理器

让我们开始定义我们能想到的最简单的处理器，只是为了编译和测试。如下是注解的定义：

```java
// annotations/simplest/Simple.java
// A bare-bones annotation
package annotations.simplest;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD,
        ElementType.CONSTRUCTOR,
        ElementType.ANNOTATION_TYPE,
        ElementType.PACKAGE, ElementType.FIELD,
        ElementType.LOCAL_VARIABLE})
public @interface Simple {
    String value() default "-default-";
}
```

**@Retention** 的参数现在为 **SOURCE**，这意味着注解不会再存留在编译后的代码。这在编译时处理注解是没有必要的，它只是指出，在这里，**javac** 是唯一有机会处理注解的代理。

**@Target** 声明了几乎所有的目标类型（除了 **PACKAGE**） ，同样是为了演示。下面是一个测试示例。

```java
// annotations/simplest/SimpleTest.java
// Test the "Simple" annotation
// {java annotations.simplest.SimpleTest}
package annotations.simplest;
@Simple
public class SimpleTest {
    @Simple
    int i;
    @Simple
    public SimpleTest() {}
    @Simple
    public void foo() {
        System.out.println("SimpleTest.foo()");
    }
    @Simple
    public void bar(String s, int i, float f) {
        System.out.println("SimpleTest.bar()");
    }
    @Simple
    public static void main(String[] args) {
        @Simple
        SimpleTest st = new SimpleTest();
        st.foo();
    }
}
```

输出为：

```java
SimpleTest.foo()
```

在这里我们使用 **@Simple** 注解了所有 **@Target** 声明允许的地方。

**SimpleTest.java** 只需要 **Simple.java** 就可以编译成功。当我们编译的时候什么都没有发生。

**javac** 允许 **@Simple** 注解（只要它存在）在我们创建处理器并将其 hook 到编译器之前，不做任何事情。

如下是一个十分简单的处理器，其所作的事情就是把注解相关的信息打印出来：

```java
// annotations/simplest/SimpleProcessor.java
// A bare-bones annotation processor
package annotations.simplest;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.*;
@SupportedAnnotationTypes(
        "annotations.simplest.Simple")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleProcessor
        extends AbstractProcessor {
    @Override
    public boolean process(
            Set<? extends TypeElement> annotations,
            RoundEnvironment env) {
        for(TypeElement t : annotations)
            System.out.println(t);
        for(Element el :
                env.getElementsAnnotatedWith(Simple.class))
            display(el);
        return false;
    }
    private void display(Element el) {
        System.out.println("==== " + el + " ====");
        System.out.println(el.getKind() +
                " : " + el.getModifiers() +
                " : " + el.getSimpleName() +
                " : " + el.asType());
        if(el.getKind().equals(ElementKind.CLASS)) {
            TypeElement te = (TypeElement)el;
            System.out.println(te.getQualifiedName());
            System.out.println(te.getSuperclass());
            System.out.println(te.getEnclosedElements());
        }
        if(el.getKind().equals(ElementKind.METHOD)) {
            ExecutableElement ex = (ExecutableElement)el;
            System.out.print(ex.getReturnType() + " ");
            System.out.print(ex.getSimpleName() + "(");
            System.out.println(ex.getParameters() + ")");
        }
    }
}
```

（旧的，失效的）**apt** 版本的处理器需要额外的方法来确定支持哪些注解以及支持的 Java 版本。不过，你现在可以简单的使用 **@SupportedAnnotationTypes** 和 **@SupportedSourceVersion** 注解（这是一个很好的示例关于注解如何简化你的代码）。

你唯一需要实现的方法就是 `process()`，这里是所有行为发生的地方。第一个参数告诉你哪个注解是存在的，第二个参数保留了剩余信息。我们所做的事情只是打印了注解（这里只存在一个），可以看 **TypeElement** 文档中的其他行为。通过使用 `process()` 的第二个操作，我们循环所有被 **@Simple** 注解的元素，并且针对每一个元素调用我们的 `display()` 方法。所有 **Element** 展示了本身的基本信息；例如，`getModifiers()` 告诉你它是否为 **public** 和 **static** 的。

**Element** 只能执行那些编译器解析的所有基本对象共有的操作，而类和方法之类的东西有额外的信息需要提取。所以（如果你阅读了正确的文档，但是我没有在任何文档中找到——我不得不通过 StackOverflow 寻找线索）你检查它是哪种 **ElementKind**，让后将其向下转换为更具体的元素类型，注入针对 CLASS 的 TypeElement 和 针对 METHOD 的ExecutableElement。此时，可以为这些元素调用其他方法。

动态向下转型（在编译期不进行检查）并不像是 Java 的做事方式，这非常不直观这也是为什么我从未想过要这样做事。相反，我花了好几天的时间，试图发现你应该如何访问这些信息，而这些信息至少在某种程度上是用不起作用的恰当方法简单明了的。我还没有遇到任何东西说上面是规范的形式，但在我看来是。

如果只是通过平常的方式来编译 **SimpleTest.java**，你不会得到任何结果。为了得到注解输出，你必须增加一个 **processor** 标志并且连接注解处理器类

```shell
javac -processor annotations.simplest.SimpleProcessor SimpleTest.java
```

现在编译器有了输出

```shell
annotations.simplest.Simple
==== annotations.simplest.SimpleTest ====
CLASS : [public] : SimpleTest : annotations.simplest.SimpleTest
annotations.simplest.SimpleTest
java.lang.Object
i,SimpleTest(),foo(),bar(java.lang.String,int,float),main(java.lang.String[])
==== i ====
FIELD : [] : i : int
==== SimpleTest() ====
CONSTRUCTOR : [public] : <init> : ()void
==== foo() ====
METHOD : [public] : foo : ()void
void foo()
==== bar(java.lang.String,int,float) ====
METHOD : [public] : bar : (java.lang.String,int,float)void
void bar(s,i,f)
==== main(java.lang.String[]) ====
METHOD : [public, static] : main : (java.lang.String[])void
void main(args)
```

这给了你一些可以发现的东西，包括参数名和类型、返回值等。

### 更复杂的处理器

<!-- Annotation-Based Unit Testing -->

## 基于注解的单元测试


<!-- Summary -->
## 本章小结



<!-- 分页 -->

<div style="page-break-after: always;"></div>