[TOC]

<!-- Annotations -->

# 第二十三章 注解

注解（也被称为元数据）为我们在代码中添加信息提供了一种形式化的方式，使我们可以在稍后的某个时刻更容易的使用这些数据。

注解在一定程度上是把元数据和源代码文件结合在一起的趋势所激发的，而不是保存在外部文档。这同样是对像 C# 语言对于 Java 语言特性压力的一种回应。

注解是 Java 5 所引入的众多语言变化之一。它们提供了 Java 无法表达的但是你需要完整表述程序所需的信息。因此，注解使得我们可以以编译器验证的格式存储程序的额外信息。注解可以生成描述符文件，甚至是新的类定义，并且有助于减轻编写“样板”代码的负担。通过使用注解，你可以将元数据保存在 Java 源代码中。并拥有如下优势：简单易读的代码，编译器类型检查，使用 annotation API 为自己的注解构造处理工具。即使 Java 定义了一些类型的元数据，但是一般来说注解类型的添加和如何使用完全取决于你。

注解的语法十分简单，主要是在现有语法中添加 @ 符号。Java 5 引入了前三种定义在 **java.lang** 包中的注解：

- **@Override**：表示当前的方法定义将覆盖基类的方法。如果你不小心拼写错误，或者方法签名被错误拼写的时候，编译器就会发出错误提示。
- **@Deprecated**：如果使用该注解的元素被调用，编译器就会发出警告信息。
- **@SuppressWarnings**：关闭不当的编译器警告信息。
- **@SafeVarargs**：在 Java 7 中加入用于禁止对具有泛型varargs参数的方法或构造函数的调用方发出警告。
- **@FunctionalInterface**：Java 8 中加入用于表示类型声明为函数式接口

还有 5 种额外的注解类型用于创造新的注解。你将会在这一章学习它们。

每当创建涉及重复工作的类或接口时，你通常可以使用注解来自动化和简化流程。例如在 Enterprise JavaBean（EJB）中的许多额外工作就是通过注解来消除的。

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

被注解标注的方法和其他的方法没有任何区别。在这个例子中，注解 `@Test` 可以和任何修饰符共同用于方法，诸如 **public**、**static** 或 **void**。从语法的角度上看，注解的使用方式和修饰符的使用方式一致。

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

注解的元素在使用时表现为 名-值 对的形式，并且需要放置在 `@UseCase` 声明之后的括号内。在 `encryptPassword()` 方法的注解中，并没有给出 **description** 的值，所以在 **@interface UseCase** 的注解处理器分析处理这个类的时候会使用该元素的默认值。

你应该能够想象到如何使用这套工具来“勾勒”出将要建造的系统，然后在建造的过程中逐渐实现系统的各项功能。

### 元注解

Java 语言中目前有 5 种标准注解（前面介绍过），以及 5 种元注解。元注解用于注解其他的注解

| 注解        | 解释                                                         |
| ----------- | ------------------------------------------------------------ |
| @Target     | 表示注解可以用于哪些地方。可能的 **ElementType** 参数包括：<br/>**CONSTRUCTOR**：构造器的声明<br/>**FIELD**：字段声明（包括 enum 实例）<br/>**LOCAL_VARIABLE**：局部变量声明<br/>**METHOD**：方法声明<br/>**PACKAGE**：包声明<br/>**PARAMETER**：参数声明<br/>**TYPE**：类、接口（包括注解类型）或者 enum 声明 |
| @Retention  | 表示注解信息保存的时长。可选的 **RetentionPolicy** 参数包括：<br/>**SOURCE**：注解将被编译器丢弃<br/>**CLASS**：注解在 class 文件中可用，但是会被 VM 丢弃。<br/>**RUNTIME**：VM 将在运行期也保留注解，因此可以通过反射机制读取注解的信息。 |
| @Documented | 将此注解保存在 Javadoc 中                                    |
| @Inherited  | 允许子类继承父类的注解                                       |
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

假设你想提供一些基本的对象/关系映射功能，能够自动生成数据库表。你可以使用 XML 描述文件来指明类的名字、每个成员以及数据库映射的相关信息。但是，通过使用注解，你可以把所有信息都保存在 **JavaBean** 源文件中。为此你需要一些用于定义数据库表名称、数据库列以及将 SQL 类型映射到属性的注解。

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

**@Constraints** 注解允许处理器提供数据库表的元数据。**@Constraints** 代表了数据库通常提供的约束的一小部分，但是它所要表达的思想已经很清楚了。`primaryKey()`，`allowNull()` 和 `unique()` 元素明显的提供了默认值，从而使得在大多数情况下，该注解的使用者不需要输入太多东西。

另外两个 **@interface** 定义的是 SQL 类型。如果希望这个框架更有价值的话，我们应该为每个 SQL 类型都定义相应的注解。不过为为示例，两个元素足够了。

这些 SQL 类型具有 `name()` 元素和 `constraints()` 元素。后者利用了嵌套注解的功能，将数据库列的类型约束信息嵌入其中。注意 `constraints()` 元素的默认值是 **@Constraints**。由于在 **@Constraints** 注解类型之后，没有在括号中指明 **@Constraints** 元素的值，因此，**constraints()** 的默认值为所有元素都为默认值的 **@Constraints** 注解。如果要使得嵌入的  **@Constraints**  注解中的 `unique()` 元素为 true，并作为 `constraints()` 元素的默认值，你可以像如下定义：

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

类注解 **@DBTable** 注解给定了元素值 MEMBER，它将会作为标的名字。类的属性 **firstName** 和 **lastName** 都被注解为 **@SQLString** 类型并且给了默认元素值分别为 30 和 50。这些注解都有两个有趣的地方：首先，他们都使用了嵌入的 **@Constraints** 注解的默认值；其次，它们都是用了快捷方式特性。如果你在注解中定义了名为 **value** 的元素，并且在使用该注解时，**value** 为唯一一个需要赋值的元素，你就不需要使用名—值对的语法，你只需要在括号中给出 **value** 元素的值即可。这可以应用于任何合法类型的元素。这也限制了你必须将元素命名为 **value**，不过在上面的例子中，这样的注解语句也更易于理解：

```java
@SQLString(30)
```

处理器将在创建表的时候使用该值设置 SQL 列的大小。

默认值的语法虽然很灵巧，但是它很快就变的复杂起来。以 **reference** 字段的注解为例，上面拥有 **@SQLString** 注解，但是这个字段也将成为表的主键，因此在嵌入的 **@Constraint** 注解中设定 **primaryKey** 元素的值。这时事情就变的复杂了。你不得不为这个嵌入的注解使用很长的键—值对的形式，来指定元素名称和 **@interface** 的名称。同时，由于有特殊命名的 **value** 也不是唯一需要赋值的元素，因此不能再使用快捷方式特性。如你所见，最终结果不算清晰易懂。

### 替代方案

可以使用多种不同的方式来定义自己的注解用于上述任务。例如，你可以使用一个单一的注解类 **@TableColumn**，它拥有一个 **enum** 元素，元素值定义了 **STRING**，**INTEGER**，**FLOAT** 等类型。这消除了每个 SQL 类型都需要定义一个 **@interface** 的负担，不过也使得用额外信息修饰 SQL 类型变的不可能，这些额外的信息例如长度或精度等，都可能是非常有用的。

你也可以使用一个 **String** 类型的元素来描述实际的 SQL 类型，比如 “VARCHAR(30)” 或者 “INTEGER”。这使得你可以修饰 SQL 类型，但是这也将 Java 类型到 SQL 类型的映射绑在了一起，这不是一个好的设计。你并不想在数据库更改之后重新编译你的代码；如果我们只需要告诉注解处理器，我们正在使用的是什么“口味（favor）”的 SQL，然后注解助力器来为我们处理 SQL 类型的细节，那将是一个优雅的设计。

第三种可行的方案是一起使用两个注解，**@Constraints** 和相应的 SQL 类型（例如，**@SQLInteger**）去注解同一个字段。这可能会让代码有些混乱，但是编译器允许你对同一个目标使用多个注解。在 Java 8，在使用多个注解的时候，你可以重复使用同一个注解。

### 注解不支持继承

你不能使用 **extends** 关键字来继承 **@interfaces**。这真是一个遗憾，如果可以定义 **@TableColumn** 注解（参考前面的建议），同时嵌套一个 **@SQLType** 类型的注解，将成为一个优雅的设计。按照这种方式，你可以通过继承 **@SQLType** 来创造各种 SQL 类型。例如 **@SQLInteger** 和 **@SQLString**。如果支持继承，就会大大减少打字的工作量并且使得语法更整洁。在 Java 的未来版本中，似乎没有任何关于让注解支持继承的提案，所以在当前情况下，上例中的解决方案可能已经是最佳方案了。

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

主方法会循环处理命令行传入的每一个类名。每一个类都是用 ` forName()` 方法进行加载，并使用 `getAnnotation(DBTable.class)` 来检查该类是否带有 **@DBTable** 注解。如果存在，将表名存储起来。然后读取这个类的所有字段，并使用 `getDeclaredAnnotations()` 进行检查。这个方法返回一个包含特定字段上所有注解的数组。然后使用 **instanceof** 操作符判断这些注解是否是 **@SQLInteger** 或者 **@SQLString** 类型。如果是的话，在对应的处理块中将构造出相应的数据库列的字符串片段。注意，由于注解没有继承机制，如果要获取近似多态的行为，使用 `getDeclaredAnnotations()` 似乎是唯一的方式。

嵌套的 **@Constraint** 注解被传递给 `getConstraints()`方法，并用它来构造一个包含 SQL 约束的 String 对象。

需要提醒的是，上面演示的技巧对于真实的对象/映射关系而言，是十分幼稚的。使用 **@DBTable** 的注解来获取表的名称，这使得如果要修改表的名字，则迫使你重新编译 Java 代码。这种效果并不理想。现在已经有了很多可用的框架，用于将对象映射到数据库中，并且越来越多的框架开始使用注解了。

<!-- Using javac to Process Annotations -->

## 使用javac处理注解

通过 **javac**，你可以通过创建编译时（compile-time）注解处理器在 Java 源文件上使用注解，而不是编译之后的 class 文件。但是这里有一个重大限制：你不能通过处理器来改变源代码。唯一影响输出的方式就是创建新的文件。

如果你的注解处理器创建了新的源文件，在新一轮处理中注解会检查源文件本身。工具在检测一轮之后持续循环，直到不再有新的源文件产生。然后它编译所有的源文件。

每一个你编写的注解都需要处理器，但是 **javac** 可以非常容易的将多个注解处理器合并在一起。你可以指定多个需要处理的类，并且你可以添加监听器用于监听注解处理完成后接到通知。

本节中的示例将帮助你开始学习，但如果你必须深入学习，请做好反复学习，大量访问 Google 和StackOverflow 的准备。

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

**Element** 只能执行那些编译器解析的所有基本对象共有的操作，而类和方法之类的东西有额外的信息需要提取。所以（如果你阅读了正确的文档，但是我没有在任何文档中找到——我不得不通过 StackOverflow 寻找线索）你检查它是哪种 **ElementKind**，然后将其向下转换为更具体的元素类型，注入针对 CLASS 的 TypeElement 和 针对 METHOD 的ExecutableElement。此时，可以为这些元素调用其他方法。

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

当你创建用于 javac 注解处理器时，你不能使用 Java 的反射特性，因为你处理的是源代码，而并非是编译后的 class 文件。各种 mirror[^3 ] 解决这个问题的方法是，通过允许你在未编译的源代码中查看方法、字段和类型。

如下是一个用于提取类中方法的注解，所以它可以被抽取成为一个接口：

```java
// annotations/ifx/ExtractInterface.java
// javac-based annotation processing
package annotations.ifx;
import java.lang.annotation.*;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ExtractInterface {
    String interfaceName() default "-!!-";
}
```

**RetentionPolicy** 的值为 **SOURCE**，这是为了在提取类中的接口之后不再将注解信息保留在 class 文件中。接下来的测试类提供了一些公用方法，这些方法可以成为接口的一部分：

```java
// annotations/ifx/Multiplier.java
// javac-based annotation processing
// {java annotations.ifx.Multiplier}
package annotations.ifx;
@ExtractInterface(interfaceName="IMultiplier")
public class Multiplier {
    public boolean flag = false;
    private int n = 0;
    public int multiply(int x, int y) {
        int total = 0;
        for(int i = 0; i < x; i++)
            total = add(total, y);
        return total;
    }
    public int fortySeven() { return 47; }
    private int add(int x, int y) {
        return x + y;
    }
    public double timesTen(double arg) {
        return arg * 10;
    }
    public static void main(String[] args) {
        Multiplier m = new Multiplier();
        System.out.println(
                "11 * 16 = " + m.multiply(11, 16));
    }
}
```

输出为：

```java
11 * 16 = 176
```

**Multiplier** 类（只能处理正整数）拥有一个 `multiply()` 方法，这个方法会多次调用私有方法 `add()` 来模拟乘法操作。` add()` 是私有方法，因此不能成为接口的一部分。其他的方法提供了语法多样性。注解被赋予 **IMultiplier** 的 **InterfaceName** 作为要创建的接口的名称。

这里有一个编译时处理器用于提取有趣的方法，并创建一个新的 interface 源代码文件（这个源文件将会在下一轮中被自动编译）：

```java
// annotations/ifx/IfaceExtractorProcessor.java
// javac-based annotation processing
package annotations.ifx;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.*;
import java.util.*;
import java.util.stream.*;
import java.io.*;
@SupportedAnnotationTypes(
        "annotations.ifx.ExtractInterface")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class IfaceExtractorProcessor
        extends AbstractProcessor {
    private ArrayList<Element>
            interfaceMethods = new ArrayList<>();
    Elements elementUtils;
    private ProcessingEnvironment processingEnv;
    @Override
    public void init(
            ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        elementUtils = processingEnv.getElementUtils();
    }
    @Override
    public boolean process(
            Set<? extends TypeElement> annotations,
            RoundEnvironment env) {
        for(Element elem:env.getElementsAnnotatedWith(
                ExtractInterface.class)) {
            String interfaceName = elem.getAnnotation(
                    ExtractInterface.class).interfaceName();
            for(Element enclosed :
                    elem.getEnclosedElements()) {
                if(enclosed.getKind()
                        .equals(ElementKind.METHOD) &&
                        enclosed.getModifiers()
                                .contains(Modifier.PUBLIC) &&
                        !enclosed.getModifiers()
                                .contains(Modifier.STATIC)) {
                    interfaceMethods.add(enclosed);
                }
            }
            if(interfaceMethods.size() > 0)
                writeInterfaceFile(interfaceName);
        }
        return false;
    }
    private void
    writeInterfaceFile(String interfaceName) {
        try(
                Writer writer = processingEnv.getFiler()
                        .createSourceFile(interfaceName)
                        .openWriter()
        ) {
            String packageName = elementUtils
                    .getPackageOf(interfaceMethods
                            .get(0)).toString();
            writer.write(
                    "package " + packageName + ";\n");
            writer.write("public interface " +
                    interfaceName + " {\n");
            for(Element elem : interfaceMethods) {
                ExecutableElement method =
                        (ExecutableElement)elem;
                String signature = " public ";
                signature += method.getReturnType() + " ";
                signature += method.getSimpleName();
                signature += createArgList(
                        method.getParameters());
                System.out.println(signature);
                writer.write(signature + ";\n");
            }
            writer.write("}");
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    private String createArgList(
            List<? extends VariableElement> parameters) {
        String args = parameters.stream()
                .map(p -> p.asType() + " " + p.getSimpleName())
                .collect(Collectors.joining(", "));
        return "(" + args + ")";
    }
}
```

**Elements** 对象实例 **elementUtils** 是一组静态方法的工具；我们用它来寻找 **writeInterfaceFile()** 中含有的包名。

`getEnclosedElements()`方法会通过指定的元素生成所有的“闭包”元素。在这里，这个类闭包了它的所有元素。通过使用 `getKind()` 我们会找到所有的 **public** 和 **static** 方法，并将其添加到 **interfaceMethods** 列表中。接下来 `writeInterfaceFile()` 使用 **interfaceMethods** 列表里面的值生成新的接口定义。注意，在 `writeInterfaceFile()` 使用了向下转型到 **ExecutableElement**，这使得我们可以获取所有的方法信息。**createArgList()** 是一个帮助方法，用于生成参数列表。

**Filer**是 `getFiler()` 生成的，并且是 **PrintWriter** 的一种实例，可以用于创建新文件。我们使用 **Filer** 对象，而不是原生的 **PrintWriter** 原因是，这个对象可以运行 **javac** 追踪你创建的新文件，这使得它可以在新一轮中检查新文件中的注解并编译文件。

如下是一个命令行，可以在编译的时候使用处理器：

```shell
javac -processor annotations.ifx.IfaceExtractorProcessor Multiplier.java
```

新生成的 **IMultiplier.java** 的文件，正如你通过查看上面处理器的 `println()` 语句所猜测的那样，如下所示：

```java
package annotations.ifx;
public interface IMultiplier {
    public int multiply(int x, int y);
    public int fortySeven();
    public double timesTen(double arg);
}
```

这个类同样会被 **javac** 编译（在某一轮中），所以你会在同一个目录中看到 **IMultiplier.class** 文件。

<!-- Annotation-Based Unit Testing -->

## 基于注解的单元测试

单元测试是对类中每个方法提供一个或者多个测试的一种事件，其目的是为了有规律的测试一个类中每个部分是否具备正确的行为。在 Java 中，最著名的单元测试工具就是 **JUnit**。**JUnit** 4 版本已经包含了注解。在注解版本之前的 JUnit 一个最主要的问题是，为了启动和运行 **JUnit** 测试，有大量的“仪式”需要标注。这种负担已经减轻了一些，**但是**注解使得测试更接近“可以工作的最简单的测试系统”。

在注解版本之前的 JUnit，你必须创建一个单独的文件来保存单元测试。通过注解，我们可以将单元测试集成在需要被测试的类中，从而将单元测试的时间和麻烦降到了最低。这种方式有额外的好处，就是使得测试私有方法和公有方法变的一样容易。

这个基于注解的测试框架叫做 **@Unit**。其最基本的测试形式，可能也是你使用的最多的一个注解是 **@Test**，我们使用 **@Test** 来标记测试方法。测试方法不带参数，并返回 **boolean** 结果来说明测试方法成功或者失败。你可以任意命名它的测试方法。同时 **@Unit** 测试方法可以是任意你喜欢的访问修饰方法，包括 **private**。

要使用 **@Unit**，你必须导入 **onjava.atunit** 包，并且使用 **@Unit** 的测试标记为合适的方法和字段打上标签（在接下来的例子中你会学到），然后让你的构建系统对编译后的类运行 **@Unit**，下面是一个简单的例子：

```java
// annotations/AtUnitExample1.java
// {java onjava.atunit.AtUnit
// build/classes/main/annotations/AtUnitExample1.class}
package annotations;
import onjava.atunit.*;
import onjava.*;
public class AtUnitExample1 {
    public String methodOne() {
        return "This is methodOne";
    }
    public int methodTwo() {
        System.out.println("This is methodTwo");
        return 2;
    }
    @Test
    boolean methodOneTest() {
        return methodOne().equals("This is methodOne");
    }
    @Test
    boolean m2() { return methodTwo() == 2; }
    @Test
    private boolean m3() { return true; }
    // Shows output for failure:
    @Test
    boolean failureTest() { return false; }
    @Test
    boolean anotherDisappointment() {
        return false;
    }
}
```

输出为：

```java
annotations.AtUnitExample1
. m3
. methodOneTest
. m2 This is methodTwo
. failureTest (failed)
. anotherDisappointment (failed)
(5 tests)
>>> 2 FAILURES <<<
annotations.AtUnitExample1: failureTest
annotations.AtUnitExample1: anotherDisappointment
```

使用 **@Unit** 进行测试的类必须定义在某个包中（即必须包括 **package** 声明）。

**@Test** 注解被置于 `methodOneTest()`、 `m2()`、`m3()`、`failureTest()` 以及 a`notherDisappointment()` 方法之前，它们告诉 **@Unit** 方法作为单元测试来运行。同时 **@Test** 确保这些方法没有任何参数并且返回值为 **boolean** 或者 **void**。当你填写单元测试时，唯一需要做的就是决定测试是成功还是失败，（对于返回值为 **boolean** 的方法）应该返回 **ture** 还是 **false**。

如果你熟悉 **JUnit**，你还将注意到 **@Unit** 输出的信息更多。你会看到现在正在运行的测试的输出更有用，最后它会告诉你导致失败的类和测试。

你并非必须将测试方法嵌入到原来的类中，有时候这种事情根本做不到。要生产一个非嵌入式的测试，最简单的方式就是继承：

```java
// annotations/AUExternalTest.java
// Creating non-embedded tests
// {java onjava.atunit.AtUnit
// build/classes/main/annotations/AUExternalTest.class}
package annotations;
import onjava.atunit.*;
import onjava.*;
public class AUExternalTest extends AtUnitExample1 {
    @Test
    boolean _MethodOne() {
        return methodOne().equals("This is methodOne");
    }
    @Test
    boolean _MethodTwo() {
        return methodTwo() == 2;
    }
}
```

输出为：

```java
annotations.AUExternalTest
. tMethodOne
. tMethodTwo This is methodTwo
OK (2 tests)
```

这个示例还表现出灵活命名的价值。在这里，**@Test** 方法被命名为下划线前缀加上要测试的方法名称（我并不认为这是一种理想的命名形式，这只是表现一种可能性罢了）。

你也可以使用组合来创建非嵌入式的测试：

```java
// annotations/AUComposition.java
// Creating non-embedded tests
// {java onjava.atunit.AtUnit
// build/classes/main/annotations/AUComposition.class}
package annotations;
import onjava.atunit.*;
import onjava.*;
public class AUComposition {
    AtUnitExample1 testObject = new AtUnitExample1();
    @Test
    boolean tMethodOne() {
        return testObject.methodOne()
                .equals("This is methodOne");
    }
    @Test
    boolean tMethodTwo() {
        return testObject.methodTwo() == 2;
    }
}
```

输出为：

```java
annotations.AUComposition
. tMethodTwo This is methodTwo
. tMethodOne
OK (2 tests)
```

因为在每一个测试里面都会创建 **AUComposition** 对象，所以创建新的成员变量 **testObject** 用于以后的每一个测试方法。

因为 **@Unit** 中没有 **JUnit** 中特殊的 **assert** 方法，不过另一种形式的 **@Test** 方法仍然允许返回值为 **void**（如果你还想使用 **true** 或者 **false** 的话，也可以使用 **boolean** 作为方法返回值类型）。为了表示测试成功，可以使用 Java 的 **assert** 语句。Java 断言机制需要你在 java 命令行行加上 **-ea** 标志来开启，但是 **@Unit** 已经自动开启了该功能。要表示测试失败的话，你甚至可以使用异常。**@Unit** 的设计目标之一就是尽可能减少添加额外的语法，而 Java 的 **assert** 和异常对于报告错误而言，即已经足够了。一个失败的 **assert** 或者从方法从抛出的异常都被视为测试失败，但是 **@Unit** 不会在这个失败的测试上卡住，它会继续运行，直到所有测试完毕，下面是一个示例程序：

```java
// annotations/AtUnitExample2.java
// Assertions and exceptions can be used in @Tests
// {java onjava.atunit.AtUnit
// build/classes/main/annotations/AtUnitExample2.class}
package annotations;
import java.io.*;
import onjava.atunit.*;
import onjava.*;
public class AtUnitExample2 {
    public String methodOne() {
        return "This is methodOne";
    }
    public int methodTwo() {
        System.out.println("This is methodTwo");
        return 2;
    }
    @Test
    void assertExample() {
        assert methodOne().equals("This is methodOne");
    }
    @Test
    void assertFailureExample() {
        assert 1 == 2: "What a surprise!";
    }
    @Test
    void exceptionExample() throws IOException {
        try(FileInputStream fis =
                    new FileInputStream("nofile.txt")) {} // Throws
    }
    @Test
    boolean assertAndReturn() {
        // Assertion with message:
        assert methodTwo() == 2: "methodTwo must equal 2";
        return methodOne().equals("This is methodOne");
    }
}
```

输出为：

```java
annotations.AtUnitExample2
. exceptionExample java.io.FileNotFoundException:
nofile.txt (The system cannot find the file specified)
(failed)
. assertExample
. assertAndReturn This is methodTwo
. assertFailureExample java.lang.AssertionError: What
a surprise!
(failed)
(4 tests)
>>> 2 FAILURES <<<
annotations.AtUnitExample2: exceptionExample
annotations.AtUnitExample2: assertFailureExample
```

如下是一个使用非嵌入式测试的例子，并且使用了断言，它将会对 **java.util.HashSet** 进行一些简单的测试：

```java
// annotations/HashSetTest.java
// {java onjava.atunit.AtUnit
// build/classes/main/annotations/HashSetTest.class}
package annotations;
import java.util.*;
import onjava.atunit.*;
import onjava.*;
public class HashSetTest {
    HashSet<String> testObject = new HashSet<>();
    @Test
    void initialization() {
        assert testObject.isEmpty();
    }
    @Test
    void _Contains() {
        testObject.add("one");
        assert testObject.contains("one");
    }
    @Test
    void _Remove() {
        testObject.add("one");
        testObject.remove("one");
        assert testObject.isEmpty();
    }
}
```

采用继承的方式可能会更简单，也没有一些其他的约束。

对每一个单元测试而言，**@Unit** 都会使用默认的无参构造器，为该测试类所属的类创建出一个新的实例。并在此新创建的对象上运行测试，然后丢弃该对象，以免对其他测试产生副作用。如此创建对象导致我们依赖于类的默认构造器。如果你的类没有默认构造器，或者对象需要复杂的构造过程，那么你可以创建一个 **static** 方法专门负责构造对象，然后使用 **@TestObjectCreate** 注解标记该方法，例子如下：

```java
// annotations/AtUnitExample3.java
// {java onjava.atunit.AtUnit
// build/classes/main/annotations/AtUnitExample3.class}
package annotations;
import onjava.atunit.*;
import onjava.*;
public class AtUnitExample3 {
    private int n;
    public AtUnitExample3(int n) { this.n = n; }
    public int getN() { return n; }
    public String methodOne() {
        return "This is methodOne";
    }
    public int methodTwo() {
        System.out.println("This is methodTwo");
        return 2;
    }
    @TestObjectCreate
    static AtUnitExample3 create() {
        return new AtUnitExample3(47);
    }
    @Test
    boolean initialization() { return n == 47; }
    @Test
    boolean methodOneTest() {
        return methodOne().equals("This is methodOne");
    }
    @Test
    boolean m2() { return methodTwo() == 2; }
}
```

输出为：

```java
annotations.AtUnitExample3
. initialization
. m2 This is methodTwo
. methodOneTest
OK (3 tests)
```

**@TestObjectCreate** 修饰的方法必须声明为 **static** ，且必须返回一个你正在测试的类型对象，这一切都由 **@Unit** 负责确保成立。

有的时候，你需要向单元测试中增加一些字段。这时候可以使用 **@TestProperty** 注解，由它注解的字段表示只在单元测试中使用（因此，在你将产品发布给客户之前，他们应该被删除）。在下面的例子中，一个 **String** 通过 `String.split()` 方法进行分割，从其中读取一个值，这个值将会被生成测试对象：

```java
// annotations/AtUnitExample4.java
// {java onjava.atunit.AtUnit
// build/classes/main/annotations/AtUnitExample4.class}
// {VisuallyInspectOutput}
package annotations;
import java.util.*;
import onjava.atunit.*;
import onjava.*;
public class AtUnitExample4 {
    static String theory = "All brontosauruses " +
            "are thin at one end, much MUCH thicker in the " +
            "middle, and then thin again at the far end.";
    private String word;
    private Random rand = new Random(); // Time-based seed
    public AtUnitExample4(String word) {
        this.word = word;
    }
    public String getWord() { return word; }
    public String scrambleWord() {
        List<Character> chars = Arrays.asList(
                ConvertTo.boxed(word.toCharArray()));
        Collections.shuffle(chars, rand);
        StringBuilder result = new StringBuilder();
        for(char ch : chars)
            result.append(ch);
        return result.toString();
    }
    @TestProperty
    static List<String> input =
            Arrays.asList(theory.split(" "));
    @TestProperty
    static Iterator<String> words = input.iterator();
    @TestObjectCreate
    static AtUnitExample4 create() {
        if(words.hasNext())
            return new AtUnitExample4(words.next());
        else
            return null;
    }
    @Test
    boolean words() {
        System.out.println("'" + getWord() + "'");
        return getWord().equals("are");
    }
    @Test
    boolean scramble1() {
// Use specific seed to get verifiable results:
        rand = new Random(47);
        System.out.println("'" + getWord() + "'");
        String scrambled = scrambleWord();
        System.out.println(scrambled);
        return scrambled.equals("lAl");
    }
    @Test
    boolean scramble2() {
        rand = new Random(74);
        System.out.println("'" + getWord() + "'");
        String scrambled = scrambleWord();
        System.out.println(scrambled);
        return scrambled.equals("tsaeborornussu");
    }
}
```

输出为：

```java
annotations.AtUnitExample4
. words 'All'
(failed)
. scramble1 'brontosauruses'
ntsaueorosurbs
(failed)
. scramble2 'are'
are
(failed)
(3 tests)
>>> 3 FAILURES <<<
annotations.AtUnitExample4: words
annotations.AtUnitExample4: scramble1
annotations.AtUnitExample4: scramble2
```

**@TestProperty** 也可以用来标记那些只在测试中使用的方法，但是它们本身不是测试方法。

如果你的测试对象需要执行某些初始化工作，并且使用完成之后还需要执行清理工作，那么可以选择使用 **static** 的  **@TestObjectCleanup** 方法，当测试对象使用结束之后，该方法会为你执行清理工作。在下面的示例中，**@TestObjectCleanup** 为每一个测试对象都打开了一个文件，因此必须在丢弃测试的时候关闭该文件：

```java
// annotations/AtUnitExample5.java
// {java onjava.atunit.AtUnit
// build/classes/main/annotations/AtUnitExample5.class}
package annotations;
import java.io.*;
import onjava.atunit.*;
import onjava.*;
public class AtUnitExample5 {
    private String text;
    public AtUnitExample5(String text) {
        this.text = text;
    }
    @Override
    public String toString() { return text; }
    @TestProperty
    static PrintWriter output;
    @TestProperty
    static int counter;
    @TestObjectCreate
    static AtUnitExample5 create() {
        String id = Integer.toString(counter++);
        try {
            output = new PrintWriter("Test" + id + ".txt");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return new AtUnitExample5(id);
    }
    @TestObjectCleanup
    static void cleanup(AtUnitExample5 tobj) {
        System.out.println("Running cleanup");
        output.close();
    }
    @Test
    boolean test1() {
        output.print("test1");
        return true;
    }
    @Test
    boolean test2() {
        output.print("test2");
        return true;
    }
    @Test
    boolean test3() {
        output.print("test3");
        return true;
    }
}
```

输出为：

```java
annotations.AtUnitExample5
. test1
Running cleanup
. test3
Running cleanup
. test2
Running cleanup
OK (3 tests)
```

在输出中我们可以看到，清理方法会在每个测试方法结束之后自动运行。

### 在 @Unit 中使用泛型

泛型为 **@Unit** 出了一个难题，因为我们不可能“通用测试”。我们必须针对某个特定类型的参数或者参数集才能进行测试。解决方法十分简单，让测试类继承自泛型类的一个特定版本即可：

下面是一个 **stack** 的简单实现：

```java
// annotations/StackL.java
// A stack built on a LinkedList
package annotations;
import java.util.*;
public class StackL<T> {
    private LinkedList<T> list = new LinkedList<>();
    public void push(T v) { list.addFirst(v); }
    public T top() { return list.getFirst(); }
    public T pop() { return list.removeFirst(); }
}
```

为了测试 String 版本，我们直接让测试类继承一个 Stack\<String\> ：

```java
// annotations/StackLStringTst.java
// Applying @Unit to generics
// {java onjava.atunit.AtUnit
// build/classes/main/annotations/StackLStringTst.class}
package annotations;
import onjava.atunit.*;
import onjava.*;
public class
StackLStringTst extends StackL<String> {
    @Test
    void tPush() {
        push("one");
        assert top().equals("one");
        push("two");
        assert top().equals("two");
    }
    @Test
    void tPop() {
        push("one");
        push("two");
        assert pop().equals("two");
        assert pop().equals("one");
    }
    @Test
    void tTop() {
        push("A");
        push("B");
        assert top().equals("B");
        assert top().equals("B");
    }
}
```

输出为：

```java
annotations.StackLStringTst
. tTop
. tPush
. tPop
OK (3 tests)
```

这种方法存在的唯一缺点是，继承使我们失去了访问被测试的类中 **private** 方法的能力。这对你非常重要，那你要么把 private 方法变为 **protected**，要么添加一个非 **private** 的 **@TestProperty** 方法，由它来调用 **private** 方法（稍后我们会看到，**AtUnitRemover** 会删除产品中的 **@TestProperty** 方法）。

**@Unit** 搜索那些包含合适注解的类文件，然后运行 **@Test** 方法。我的主要目标就是让 **@Unit** 测试系统尽可能的透明，使得人们使用它的时候只需要添加 **@Test** 注解，而不需要特殊的编码和知识（现在版本的 **JUnit** 符合这个实践）。不过，如果说编写测试不会遇到任何困难，也不太可能，因此 **@Unit** 会尽量让这些困难变的微不足道，希望通过这种方式，你们会更乐意编写测试。

### 实现 @Unit

首先我们需要定义所有的注解类型。这些都是简单的标签，并且没有任何字段。@Test 标签在本章开头已经定义过了，这里是其他所需要的注解：

```java
// onjava/atunit/TestObjectCreate.java
// The @Unit @TestObjectCreate tag
package onjava.atunit;
import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestObjectCreate {}
```

```java
// onjava/atunit/TestObjectCleanup.java
// The @Unit @TestObjectCleanup tag
package onjava.atunit;
import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestObjectCleanup {}
```

```java
// onjava/atunit/TestProperty.java
// The @Unit @TestProperty tag
package onjava.atunit;
import java.lang.annotation.*;
// Both fields and methods can be tagged as properties:
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestProperty {}
```

所有测试的保留属性都为 **RUNTIME**，这是因为 **@Unit** 必须在编译后的代码中发现这些注解。

要实现系统并运行测试，我们还需要反射机制来提取注解。下面这个程序通过注解中的信息，决定如何构造测试对象，并在测试对象上运行测试。正是由于注解帮助，这个程序才会如此短小而直接：

```java
// onjava/atunit/AtUnit.java
// An annotation-based unit-test framework
// {java onjava.atunit.AtUnit}
package onjava.atunit;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;
import onjava.*;
public class AtUnit implements ProcessFiles.Strategy {
    static Class<?> testClass;
    static List<String> failedTests= new ArrayList<>();
    static long testsRun = 0;
    static long failures = 0;
    public static void
    main(String[] args) throws Exception {
        ClassLoader.getSystemClassLoader()
                .setDefaultAssertionStatus(true); // Enable assert
        new ProcessFiles(new AtUnit(), "class").start(args);
        if(failures == 0)
            System.out.println("OK (" + testsRun + " tests)");
        else {
            System.out.println("(" + testsRun + " tests)");
            System.out.println(
                    "\n>>> " + failures + " FAILURE" +
                            (failures > 1 ? "S" : "") + " <<<");
            for(String failed : failedTests)
                System.out.println(" " + failed);
        }
    }
    @Override
    public void process(File cFile) {
        try {
            String cName = ClassNameFinder.thisClass(
                    Files.readAllBytes(cFile.toPath()));
            if(!cName.startsWith("public:"))
                return;
            cName = cName.split(":")[1];
            if(!cName.contains("."))
                return; // Ignore unpackaged classes
            testClass = Class.forName(cName);
        } catch(IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        TestMethods testMethods = new TestMethods();
        Method creator = null;
        Method cleanup = null;
        for(Method m : testClass.getDeclaredMethods()) {
            testMethods.addIfTestMethod(m);
            if(creator == null)
                creator = checkForCreatorMethod(m);
            if(cleanup == null)
                cleanup = checkForCleanupMethod(m);
        }
        if(testMethods.size() > 0) {
            if(creator == null)
                try {
                    if(!Modifier.isPublic(testClass
                            .getDeclaredConstructor()
                            .getModifiers())) {
                        System.out.println("Error: " + testClass +
                                " no-arg constructor must be public");
                        System.exit(1);
                    }
                } catch(NoSuchMethodException e) {
// Synthesized no-arg constructor; OK
                }
            System.out.println(testClass.getName());
        }
        for(Method m : testMethods) {
            System.out.print(" . " + m.getName() + " ");
            try {
                Object testObject = createTestObject(creator);
                boolean success = false;
                try {
                    if(m.getReturnType().equals(boolean.class))
                        success = (Boolean)m.invoke(testObject);
                    else {
                        m.invoke(testObject);
                        success = true; // If no assert fails
                    }
                } catch(InvocationTargetException e) {
// Actual exception is inside e:
                    System.out.println(e.getCause());
                }
                System.out.println(success ? "" : "(failed)");
                testsRun++;
                if(!success) {
                    failures++;
                    failedTests.add(testClass.getName() +
                            ": " + m.getName());
                }
                if(cleanup != null)
                    cleanup.invoke(testObject, testObject);
            } catch(IllegalAccessException |
                    IllegalArgumentException |
                    InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static
    class TestMethods extends ArrayList<Method> {
        void addIfTestMethod(Method m) {
            if(m.getAnnotation(Test.class) == null)
                return;
            if(!(m.getReturnType().equals(boolean.class) ||
                    m.getReturnType().equals(void.class)))
                throw new RuntimeException("@Test method" +
                        " must return boolean or void");
            m.setAccessible(true); // If it's private, etc.
            add(m);
        }
    }
    private static
    Method checkForCreatorMethod(Method m) {
        if(m.getAnnotation(TestObjectCreate.class) == null)
            return null;
        if(!m.getReturnType().equals(testClass))
            throw new RuntimeException("@TestObjectCreate " +
                    "must return instance of Class to be tested");
        if((m.getModifiers() &
                java.lang.reflect.Modifier.STATIC) < 1)
            throw new RuntimeException("@TestObjectCreate " +
                    "must be static.");
        m.setAccessible(true);
        return m;
    }
    private static
    Method checkForCleanupMethod(Method m) {
        if(m.getAnnotation(TestObjectCleanup.class) == null)
            return null;
        if(!m.getReturnType().equals(void.class))
            throw new RuntimeException("@TestObjectCleanup " +
                    "must return void");
        if((m.getModifiers() &
                java.lang.reflect.Modifier.STATIC) < 1)
            throw new RuntimeException("@TestObjectCleanup " +
                    "must be static.");
        if(m.getParameterTypes().length == 0 ||
                m.getParameterTypes()[0] != testClass)
            throw new RuntimeException("@TestObjectCleanup " +
                    "must take an argument of the tested type.");
        m.setAccessible(true);
        return m;
    }
    private static Object
    createTestObject(Method creator) {
        if(creator != null) {
            try {
                return creator.invoke(testClass);
            } catch(IllegalAccessException |
                    IllegalArgumentException |
                    InvocationTargetException e) {
                throw new RuntimeException("Couldn't run " +
                        "@TestObject (creator) method.");
            }
        } else { // Use the no-arg constructor:
            try {
                return testClass.newInstance();
            } catch(InstantiationException |
                    IllegalAccessException e) {
                throw new RuntimeException(
                        "Couldn't create a test object. " +
                                "Try using a @TestObject method.");
            }
        }
    }
}
```

虽然它可能是“过早的重构”（因为它只在书中使用过一次），**AtUnit.java** 使用了 **ProcessFiles** 工具逐步判断命令行中的参数，决定它是一个目录还是文件，并采取相应的行为。这可以应用于不同的解决方法，是因为它包含了一个 可用于自定义的 **Strategy** 接口：

```java
// onjava/ProcessFiles.java
package onjava;
import java.io.*;
import java.nio.file.*;
public class ProcessFiles {
    public interface Strategy {
        void process(File file);
    }
    private Strategy strategy;
    private String ext;
    public ProcessFiles(Strategy strategy, String ext) {
        this.strategy = strategy;
        this.ext = ext;
    }
    public void start(String[] args) {
        try {
            if(args.length == 0)
                processDirectoryTree(new File("."));
            else
                for(String arg : args) {
                    File fileArg = new File(arg);
                    if(fileArg.isDirectory())
                        processDirectoryTree(fileArg);
                    else {
// Allow user to leave off extension:
                        if(!arg.endsWith("." + ext))
                            arg += "." + ext;
                        strategy.process(
                                new File(arg).getCanonicalFile());
                    }
                }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void processDirectoryTree(File root) throws IOException {
        PathMatcher matcher = FileSystems.getDefault()
                .getPathMatcher("glob:**/*.{" + ext + "}");
        Files.walk(root.toPath())
                .filter(matcher::matches)
                .forEach(p -> strategy.process(p.toFile()));
    }
}
```

**AtUnit** 类实现了 **ProcessFiles.Strategy**，其包含了一个 `process()` 方法。在这种方式下，**AtUnit** 实例可以作为参数传递给 **ProcessFiles** 构造器。第二个构造器的参数告诉 **ProcessFiles** 如寻找所有包含 “class” 拓展名的文件。

如下是一个简单的使用示例：

```java
// annotations/DemoProcessFiles.java
import onjava.ProcessFiles;
public class DemoProcessFiles {
    public static void main(String[] args) {
        new ProcessFiles(file -> System.out.println(file),
                "java").start(args);
    }
}
```

输出为：

```java
.\AtUnitExample1.java
.\AtUnitExample2.java
.\AtUnitExample3.java
.\AtUnitExample4.java
.\AtUnitExample5.java
.\AUComposition.java
.\AUExternalTest.java
.\database\Constraints.java
.\database\DBTable.java
.\database\Member.java
.\database\SQLInteger.java
.\database\SQLString.java
.\database\TableCreator.java
.\database\Uniqueness.java
.\DemoProcessFiles.java
.\HashSetTest.java
.\ifx\ExtractInterface.java
.\ifx\IfaceExtractorProcessor.java
.\ifx\Multiplier.java
.\PasswordUtils.java
.\simplest\Simple.java
.\simplest\SimpleProcessor.java
.\simplest\SimpleTest.java
.\SimulatingNull.java
.\StackL.java
.\StackLStringTst.java
.\Testable.java
.\UseCase.java
.\UseCaseTracker.java
```

如果没有命令行参数，这个程序会遍历当前的目录树。你还可以提供多个参数，这些参数可以是类文件（带或不带.class扩展名）或目录。

回到我们对 **AtUnit.java** 的讨论，因为 **@Unit** 会自动找到可测试的类和方法，所以不需要“套件”机制。

**AtUnit.java** 中存在的一个我们必须要解决的问题是，当它发现类文件时，类文件名中的限定类名（包括包）不明显。为了发现这个信息，必须解析类文件 - 这不是微不足道的，但也不是不可能的。 找到 .class 文件时，会打开它并读取其二进制数据并将其传递给 `ClassNameFinder.thisClass()`。 在这里，我们正在进入“字节码工程”领域，因为我们实际上正在分析类文件的内容：

```java
// onjava/atunit/ClassNameFinder.java
// {java onjava.atunit.ClassNameFinder}
package onjava.atunit;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import onjava.*;
public class ClassNameFinder {
    public static String thisClass(byte[] classBytes) {
        Map<Integer,Integer> offsetTable = new HashMap<>();
        Map<Integer,String> classNameTable = new HashMap<>();
        try {
            DataInputStream data = new DataInputStream(
                    new ByteArrayInputStream(classBytes));
            int magic = data.readInt(); // 0xcafebabe
            int minorVersion = data.readShort();
            int majorVersion = data.readShort();
            int constantPoolCount = data.readShort();
            int[] constantPool = new int[constantPoolCount];
            for(int i = 1; i < constantPoolCount; i++) {
                int tag = data.read();
                // int tableSize;
                switch(tag) {
                    case 1: // UTF
                        int length = data.readShort();
                        char[] bytes = new char[length];
                        for(int k = 0; k < bytes.length; k++)
                            bytes[k] = (char)data.read();
                        String className = new String(bytes);
                        classNameTable.put(i, className);
                        break;
                    case 5: // LONG
                    case 6: // DOUBLE
                        data.readLong(); // discard 8 bytes
                        i++; // Special skip necessary
                        break;
                    case 7: // CLASS
                        int offset = data.readShort();
                        offsetTable.put(i, offset);
                        break;
                    case 8: // STRING
                        data.readShort(); // discard 2 bytes
                        break;
                    case 3: // INTEGER
                    case 4: // FLOAT
                    case 9: // FIELD_REF
                    case 10: // METHOD_REF
                    case 11: // INTERFACE_METHOD_REF
                    case 12: // NAME_AND_TYPE
                    case 18: // Invoke Dynamic
                        data.readInt(); // discard 4 bytes
                        break;
                    case 15: // Method Handle
                        data.readByte();
                        data.readShort();
                        break;
                    case 16: // Method Type
                        data.readShort();
                        break;
                    default:
                        throw
                                new RuntimeException("Bad tag " + tag);
                }
            }
            short accessFlags = data.readShort();
            String access = (accessFlags & 0x0001) == 0 ?
                    "nonpublic:" : "public:";
            int thisClass = data.readShort();
            int superClass = data.readShort();
            return access + classNameTable.get(
                    offsetTable.get(thisClass)).replace('/', '.');
        } catch(IOException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    // Demonstration:
    public static void main(String[] args) throws Exception {
        PathMatcher matcher = FileSystems.getDefault()
                .getPathMatcher("glob:**/*.class");
// Walk the entire tree:
        Files.walk(Paths.get("."))
                .filter(matcher::matches)
                .map(p -> {
                    try {
                        return thisClass(Files.readAllBytes(p));
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(s -> s.startsWith("public:"))
// .filter(s -> s.indexOf('$') >= 0)
                .map(s -> s.split(":")[1])
                .filter(s -> !s.startsWith("enums."))
                .filter(s -> s.contains("."))
                .forEach(System.out::println);
    }
}
```

输出为：

```java
onjava.ArrayShow
onjava.atunit.AtUnit$TestMethods
onjava.atunit.AtUnit
onjava.atunit.ClassNameFinder
onjava.atunit.Test
onjava.atunit.TestObjectCleanup
onjava.atunit.TestObjectCreate
onjava.atunit.TestProperty
onjava.BasicSupplier
onjava.CollectionMethodDifferences
onjava.ConvertTo
onjava.Count$Boolean
onjava.Count$Byte
onjava.Count$Character
onjava.Count$Double
onjava.Count$Float
onjava.Count$Integer
onjava.Count$Long
onjava.Count$Pboolean
onjava.Count$Pbyte
onjava.Count$Pchar
onjava.Count$Pdouble
onjava.Count$Pfloat
onjava.Count$Pint
onjava.Count$Plong
onjava.Count$Pshort
onjava.Count$Short
onjava.Count
onjava.CountingIntegerList
onjava.CountMap
onjava.Countries
onjava.Enums
onjava.FillMap
onjava.HTMLColors
onjava.MouseClick
onjava.Nap
onjava.Null
onjava.Operations
onjava.OSExecute
onjava.OSExecuteException
onjava.Pair
onjava.ProcessFiles$Strategy
onjava.ProcessFiles
onjava.Rand$Boolean
onjava.Rand$Byte
onjava.Rand$Character
onjava.Rand$Double
onjava.Rand$Float
onjava.Rand$Integer
onjava.Rand$Long
onjava.Rand$Pboolean
onjava.Rand$Pbyte
onjava.Rand$Pchar
onjava.Rand$Pdouble
onjava.Rand$Pfloat
onjava.Rand$Pint
onjava.Rand$Plong
onjava.Rand$Pshort
onjava.Rand$Short
onjava.Rand$String
onjava.Rand
onjava.Range
onjava.Repeat
onjava.RmDir
onjava.Sets
onjava.Stack
onjava.Suppliers
onjava.TimedAbort
onjava.Timer
onjava.Tuple
onjava.Tuple2
onjava.Tuple3
onjava.Tuple4
onjava.Tuple5
onjava.TypeCounter
```

 虽然无法在这里介绍其中所有的细节，但是每个类文件都必须遵循一定的格式，而我已经尽力用有意义的字段来表示这些从 **ByteArrayInputStream** 中提取出来的数据片段。通过施加在输入流上的读操作，你能看出每个信息片的大小。例如每一个类的头 32 个 bit 总是一个 “神秘数字” **0xcafebabe**，而接下来的两个 **short** 值是版本信息。常量池包含了程序的常量，所以这是一个可变的值。接下来的 **short** 告诉我们这个常量池有多大，然后我们为其创建一个尺寸合适的数组。常量池中的每一个元素，其长度可能是固定式，也可能是可变的值，因此我们必须检查每一个常量的起始标记，然后才能知道该怎么做，这就是 switch 语句的工作。我们并不打算精确的分析类中所有的数据，仅仅是从文件的起始一步一步的走，直到取得我们所需的信息，因此你会发现，在这个过程中我们丢弃了大量的数据。关于类的信息都保存在 **classNameTable** 和 **offsetTable** 中。在读取常量池之后，就找到了 **this_class** 信息，这是 **offsetTable** 的一个坐标，通过它可以找到进入  **classNameTable** 的坐标，然后就可以得到我们所需的类的名字了。

现在让我们回到 **AtUtil.java** 中，process() 方法中拥有了类的名字，然后检查它是否包含“.”，如果有就表示该类定义于一个包中。没有包的类会被忽略。如果一个类在包中，那么我们就可以使用标准的类加载器通过 `Class.forName()`  将其加载进来。现在我们可以对这个类进行 **@Unit** 注解的分析工作了。

我们只需要关注三件事：首先是 **@Test** 方法，它们被保存在 **TestMehtods** 列表中，然后检查其是否具有 @TestObjectCreate 和 **@TestObjectCleanup****** 方法。从代码中可以看到，我们通过调用相应的方法来查询注解从而找到这些方法。

每找到一个 @Test 方法，就打印出来当前类的名字，于是观察者立刻就可以知道发生了什么。接下来开始执行测试，也就是打印出方法名，然后调用 createTestObject() （如果存在一个加了 @TestObjectCreate 注解的方法），或者调用默认构造器。一旦创建出来测试对象，如果调用其上的测试方法。如果测试的返回值为 boolean，就捕获该结果。如果测试方法没有返回值，那么就没有异常发生，我们就假设测试成功，反之，如果当 assert 失败或者有任何异常抛出的时候，就说明测试失败，这时将异常信息打印出来以显示错误的原因。如果有失败的测试发生，那么还要统计失败的次数，并将失败所属的类和方法加入到 failedTests 中，以便最后报告给用户。

<!-- Summary -->

## 本章小结

注解是 Java 引入的一项非常受欢迎的补充，它提供了一种结构化，并且具有类型检查能力的新途径，从而使得你能够为代码中加入元数据，而且不会导致代码杂乱并难以阅读。使用注解能够帮助我们避免编写累赘的部署描述性文件，以及其他的生成文件。而 Javadoc 中的 @deprecated 被 @Deprecated 注解所替代的事实也说明，与注释性文字相比，注解绝对更适用于描述类相关的信息。

Java 提供了很少的内置注解。这意味着如果你在别处找不到可用的类库，那么就只能自己创建新的注解以及相应的处理器。通过将注解处理器链接到 javac，你可以一步完成编译新生成的文件，简化了构造过程。

API 的提供方和框架将会将注解作为他们工具的一部分。通过 @Unit 系统，我们可以想象，注解会极大的改变我们的 Java 编程体验。

<!-- 分页 -->

<div style="page-break-after: always;"></div>

[^3 ]: The Java designers coyly suggest that a mirror is where you find a reflection.
