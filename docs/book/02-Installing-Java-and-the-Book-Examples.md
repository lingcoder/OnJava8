[TOC]

# 第二章 安装Java和本书用例

现在，我们来为这次阅读之旅做些准备吧！

在开始学习 Java 之前，你必须要先安装好 Java 和本书的源代码示例。因为考虑到可能有“专门的初学者”从本书开始学习编程，所以我会详细地教你如何使用命令行。 如果你已经有此方面的经验了，可以跳过这段安装说明。如果你对此处描述的任何术语或过程仍不清楚，还可以通过 [Google](https://google.com/) 搜索找到答案。具体的问题或困难请试着在 [StackOverflow](https://stackoverflow.com/) 上提问。或者去 [YouTube](https://youtube.com) 看有没有相关的安装说明。

## 编辑器

首先你需要安装一个编辑器来创建和修改本书用例里的 Java 代码。有可能你还需要使用编辑器来更改系统配置文件。

相比一些重量级的 IDE（Integrated Development Environments，集成开发环境），如 Eclipse、NetBeans 和 IntelliJ IDEA (译者注：做项目强烈推荐IDEA)，编辑器是一种更纯粹的文本编辑器。如果你已经有了一个用着顺手的 IDE，那就可以直接用了。为了方便后面的学习和统一下教学环境，我推荐大家使用 Atom 这个编辑器。大家可以在 [atom.io](https://atom.io) 上下载。

Atom 是一个免费开源、易于安装且跨平台（支持 Window、Mac和Linux）的文本编辑器。内置支持 Java 文件。相比 IDE 的厚重，它比较轻量级，是学习本书的理想工具。Atom 包含了许多方便的编辑功能，相信你一定会爱上它！更多关于 Atom 使用的细节问题可以到它的网站上寻找。

还有很多其他的编辑器。有一种亚文化的群体，他们热衷于争论哪个更好用！如果你找到一个你更喜欢的编辑器，换一种使用也没什么难度。重要的是，你要找一个用着舒服的。

## Shell

如果你之前没有接触过编程，那么有可能对 Shell（命令行窗口） 不太熟悉。shell 的历史可以追溯到早期的计算时代，当时在计算机上的操作是都通过输入命令进行的，计算机通过回显响应。所有的操作都是基于文本的。

尽管和现在的图形用户界面相比，Shell 操作方式很原始。但是同时 shell 也为我们提供了许多有用的功能特性。在学习本书的过程中，我们会经常使用到 Shell，包括现在这部分的安装，还有运行 Java 程序。

Mac：单击聚光灯（屏幕右上角的放大镜图标），然后键入 `terminal`。单击看起来像小电视屏幕的应用程序（你也可以单击“return”）。这就启动了你的用户下的 shell 窗口。

windows：首先，通过目录打开 windows 资源管理器：

- Windows 7: 单击屏幕左下角的“开始”图标，输入“explorer”后按回车键。
- Windows 8: 按 Windows+Q，输入 “explorer” 后按回车键。
- Windows 10: 按 Windows+E 打开资源管理器，导航到所需目录，单击窗口左上角的“文件“选项卡，选择“打开 Window PowerShell”启动 Shell。

Linux: 在 home 目录打开 Shell。

- Debian: 按 Alt+F2， 在弹出的对话框中输入“gnome-terminal”
- Ubuntu: 在屏幕中鼠标右击，选择 “打开终端”，或者按住 Ctrl+Alt+T
- Redhat: 在屏幕中鼠标右击，选择 “打开终端”
- Fedora: 按 Alt+F2，在弹出的对话框中输入“gnome-terminal”

**目录**

目录是 Shell 的基础元素之一。目录用来保存文件和其他目录。目录就好比树的分支。如果书籍是你系统上的一个目录，并且它有两个其他目录作为分支，例如数学和艺术，那么我们就可以说你有一个书籍目录，它包含数学和艺术两个子目录。注意：Windows 使用 `\` 而不是 `/` 来分隔路径。

**Shell基本操作**

我在这展示的 Shell 操作和系统中大体相同。出于本书的原因，下面列举一些在 Shell 中的基本操作：

```bash
更改目录： cd <路径> 
          cd .. 移动到上级目录 
          pushd <路径> 记住来源的同时移动到其他目录，popd 返回来源

目录列举： ls 列举出当前目录下所有的文件和子目录名（不包含隐藏文件），
             可以选择使用通配符 * 来缩小搜索范围。
             示例(1)： 列举所有以“.java”结尾的文件，输入 ls *.java (Windows: dir *.java)
             示例(2)： 列举所有以“F”开头，“.java”结尾的文件，输入ls F*.java (Windows: dir F*.java)

创建目录： 
    Mac/Linux 系统：mkdir  
              示例：mkdir books 
    Windows   系统：md 
              示例：md books

移除文件： 
    Mac/Linux 系统：rm
              示例：rm somefile.java
    Windows   系统：del 
              示例：del somefile.java

移除目录： 
    Mac/Linux 系统：rm -r
              示例：rm -r books
    Windows   系统：deltree 
              示例：deltree books

重复命令： !!  重复上条命令
              示例：!n 重复倒数第n条命令

命令历史：     
    Mac/Linux 系统：history
    Windows   系统：按 F7 键

文件解压：
    Linux/Mac 都有命令行解压程序 unzip，你可以通过互联网为 Windows 安装命令行解压程序 unzip。
    图形界面下（Windows 资源管理器，Mac Finder，Linux Nautilus 或其他等效软件）右键单击该文件，
    在 Mac 上选择“open”，在 Linux 上选择“extract here”，或在 Windows 上选择“extract all…”。
    要了解关于 shell 的更多信息，请在维基百科中搜索 Windows shell，Mac/Linux用户可搜索 bash shell。

```


## Java安装

为了编译和运行代码示例，首先你必须安装 JDK（Java Development Kit，JAVA 软件开发工具包）。本书中采用的是 JDK 8。


**Windows**

1. 以下为 Chocolatey 的[安装说明](https://chocolatey.org/)。
2. 在命令行提示符下输入下面的命令，等待片刻，结束后 Java 安装完成并自动完成环境变量设置。

```bash
 choco install jdk8
```

**Macintosh**

Mac 系统自带的 Java 版本太老，为了确保本书的代码示例能被正确执行，你必须将它先更新到 Java 8。我们需要管理员权限来运行下面的步骤：

1. 以下为 HomeBrew 的[安装说明](https://brew.sh/)。安装完成后执行命令 `brew update` 更新到最新版本
2. 在命令行下执行下面的命令来安装 Java。

```bash
 brew cask install java
```

当以上安装都完成后，如果你有需要，可以使用游客账户来运行本书中的代码示例。

**Linux**

* **Ubuntu/Debian**：

```bash
     sudo apt-get update
     sudo apt-get install default-jdk
```
* **Fedora/Redhat**：

```bash
    su-c "yum install java-1.8.0-openjdk"(注：执行引号内的内容就可以安装)
```


## 校验安装

打开新的命令行输入：

```bash
java -version
```

正常情况下 你应该看到以下类似信息(版本号信息可能不一样）：

```bash
java version "1.8.0_112"
Java(TM) SE Runtime Environment (build 1.8.0_112-b15)
Java HotSpot(TM) 64-Bit Server VM (build 25.112-b15, mixed mode)
```
如果提示命令找不到或者无法被识别，请根据安装说明重试；如果还不行，尝试到 [StackOverflow](https://stackoverflow.com/search?q=installing+java) 寻找答案。

## 安装和运行代码示例

当 Java 安装完毕，下一步就是安装本书的代码示例了。安装步骤所有平台一致：

1. 从 [GitHub 仓库](https://github.com/BruceEckel/OnJava8-Examples/archive/master.zip)中下载本书代码示例
2. 解压到你所选目录里。
3. 使用 Windows 资源管理器，Mac Finder，Linux 的 Nautilus 或其他等效工具浏览，在该目录下打开 Shell。
4. 如果你在正确的目录中，你应该看到该目录中名为 gradlew 和 gradlew.bat 的文件，以及许多其他文件和目录。目录与书中的章节相对应。
5. 在shell中输入下面的命令运行：

```bash
     Windows 系统：
          gradlew run

     Mac/Linux 系统：
        ./gradlew run
```

第一次安装时 Gradle 需要安装自身和其他的相关的包，请稍等片刻。安装完成后，后续的安装将会快很多。

**注意**： 第一次运行 gradlew 命令时必须连接互联网。

**Gradle 基础任务**

本书构建的大量 Gradle 任务都可以自动运行。Gradle 使用约定大于配置的方式，简单设置即可具备高可用性。本书中“一起去骑行”的某些任务不适用于此或无法执行成功。以下是你通常会使用上的 Gradle 任务列表：

```bash
    编译本书中的所有 java 文件，除了部分错误示范的
    gradlew compileJava

    编译并执行 java 文件（某些文件是库组件）
    gradlew run

    执行所有的单元测试（在本书第16章会有详细介绍）
    gradlew test

    编译并运行一个具体的示例程序
    gradlew <本书章节>:<示例名称>
    示例：gradlew objects:HelloDate
```
<!-- 分页 -->

<div style="page-break-after: always;"></div>

