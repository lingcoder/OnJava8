# 第二章 安装Java和本书用例

现在，我们来为这次阅读之旅作些准备吧！

在开始学习 Java 之前，你必须要先安装好 Java 和本书的源代码示例。因为考虑到可能有“专门的初学者”从本书开始学习编程，所以我会仔细解释计算机命令行 Shell 的这个过程。 如果你已经有此方面的经验了，可以跳过这段安装说明。如果你对此处描述的任何术语或过程仍不清楚，还可以通过 Google 搜索找到答案。具体的问题或困难请试着在 StackOverflow 上提问。或者去 YouTube 看有没有相关的安装说明。


## 编辑器

首先你需要安装一个编辑器来创建和修改本书用例里的 Java 代码。有可能你还需要一个编辑器来更改系统配置文件。

相比一些重量级的 IDE（Integrated Development Environments，开发集成环境）软件，如Eclipse、NetBeans和IntelliJ IDEA (译者注：做项目强烈推荐IDEA)，编辑器是一种基础的运行程序的文本编辑器。如果你已经有了一个 IDE 用着还顺手，那就可以直接用了。为了方便后面的学习和统一下教学环境，我推荐大家使用 Atom 这个编辑器。大家可以在 [atom.io](http://atom.io) 网站下载。
 
 Atom 是一个免费开源、易于安装且跨平台（支持 Window、Mac和Linux）的文本编辑器。内置支持 Java 文件。相比 IDE 的厚重，她比较轻量级，是学习本书的理想工具。Atom 包含了许多方便的编辑功能,相信你一定会爱上她！更多关于 Atom 使用的细节问题可以到她们的网站上。

还有很多其他的编辑器。有一种亚文化的群体，他们热衷于争论哪个更好用！如果你找到一个你更喜欢的编辑器，换一种使用也没什么难度。重要的是，你要找一个用着舒服的。


## Shell

如果你之前没有接触过编程，那么有可能对 Shell（命令行窗口） 不太熟悉。shell 的历史可以追溯到早期的计算时代，当时在计算机上的操作是都通过输入命令进行的，计算机通过回显响应。所有的操作都是基于文本的。

尽管和现在的图形用户界面相比，Shell 操作方式很原始。但是同时 shell 也为我们提供了许多有用的功特性。在学习本书的过程中，我们会经常使用到 Shell，包括现在这部分的安装，还有运行 Java 程序。

Mac：单击聚光灯（屏幕右上角的放大镜图标），然后键入“terminal”。单击看起来像小电视屏幕的应用程序（您也可以单击“return”）。这就启动了你的用户下的 shell窗口。

    windows：首先，通过目录打开 windows 资源管理器：
    Windows 7: 单击屏幕左下角的“开始”图标，输入“explorer”后按回车键。
    Windows 8: 按 Windows+Q, 输入 “explorer” 后按回车键。
    Windows 10: 按 Windows+E 打开资源管理器，导航到所需目录，单击窗口左上角的“文件“选项卡，选择“打开 Window PowerShell”启动 Shell。
    Linux: 在 home 目录打开 Shell。
    Debian: 按 Alt+F2， 在弹出的对话框中输入“gnome-terminal”
    Ubuntu: 在屏幕中鼠标右击，选择 “打开终端”, 或者按住 Ctrl+Alt+T
    Redhat: 在屏幕中鼠标右击，选择 “打开终端”
    Fedora: 按 Alt+F2，在弹出的对话框中输入“gnome-terminal”

**目录**
目录是 Shell 的基础元素之一。目录用来保存文件和其他目录。目录就好比树的分支。如果书籍是您系统上的一个目录，并且它有两个其他目录作为分支，例如数学和艺术，那么我们就可以说你有一个书籍目录里，它包含数学和艺术两个子目录。注意：Windows 使用“\”而不是“/”来分隔路径。

**Shell基本操作**
我在这展示的 Shell 操作和系统中大体相同。出于本书的原因，下面列举一些在 Shell 中的基本操作：

```shell
更改目录： cd <路径> 
          cd .. 移动到上级目录 
          pushd <路径> 记住来源的同时移动到其他目录,popd 返回上一个目录

目录列举： ls 列举出当前目录下所有的文件和子目录名（不包含隐藏文件），
             可以选择使用通配符 * 来缩小搜索的范围。
             示例(1)： 列举所有以“.java”结尾的文件，输入ls *.java
             示例(2)： 列举所有以“F”开头，“.java”结尾的文件，输入ls F*.java

增加目录： 
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
              示例：rm -r somefile.java
    Windows   系统：deltree 
              示例：deltree somefile.java

重复命令： !!  重复上条命令
              示例：!n 重复倒数第n条命令

命令历史：     
    Mac/Linux 系统：history
    Windows   系统：按 F7 键

文件解压：
    Linux/Mac 都有命令行解压程序，您可以通过互联网为Windows安装命令行解压程序。
    图形界面下（Windows 资源管理器、Mac Finder ，Linux Nautilus 或其他等效软件）右键单击该文件，在 Mac 上选择“open”，在 Linux 上选择“extract here”，或在 Windows 上选择“extract all…”。要了解关于shell 的更多信息，请在维基百科中搜索 Windows shell，或者在 Mac/Linux 中搜索 bash shell。

```


## Java安装

To compile and run the examples, you must first install the Java
development kit. In this book we use JDK8 (Java 1.8).
Windows
1. Follow the instructions at this link to Install Chocolatey.
2. At a shell prompt, type: choco install jdk8. This takes
some time, but when it’s finished Java is installed and the
necessary environment variables are set.
Macintosh
The Mac comes with a much older version of Java that won’t work for
the examples in this book, so you must first update it to Java 8. You
will need administration rights to perform these steps.
1. Follow the instructions at this link to Install HomeBrew. Then at a shell
prompt, execute brew update to make sure you have the
latest changes.
2. At a shell prompt, execute brew cask install java.
Once HomeBrew and Java are installed, all other activities described
in this book can be accomplished within a guest account, if that suits
your needs.
Linux
Use the standard package installer with the following shell commands:
Ubuntu/Debian:
1. sudo apt-get update
2. sudo apt-get install default-jdk
Fedora/Redhat:
1. su-c "yum install java-1.8.0-openjdk"



## 校验安装

Open a new shell and type:
java -version
You should see something like the following (Version numbers and
actual text will vary):
java version "1.8.0_112"
Java(TM) SE Runtime Environment (build 1.8.0_112-b15)
Java HotSpot(TM) 64-Bit Server VM (build 25.112-b15, mixed mode)
If you see a message that the command is not found or not recognized,
review the installation instructions in this chapter. If you still can’t get
it to work, check StackOverflow.


## 安装和运行本书用例

Once you have Java installed, the process to install and run the book
examples is the same for all platforms:
1. Download the book examples from the GitHub Repository.
2. unzip (as described in Basic Shell Operations) the downloaded file into the
directory of your choice.
3. Use the Windows Explorer, the Mac Finder, or Nautilus or
equivalent on Linux to browse to the directory where you
unzipped OnJava8-Examples, and open a shell there.
4. If you’re in the right directory, you should see files named
gradlew and gradlew.bat in that directory, along with
numerous other files and directories. The directories correspond
to the chapters in the book.
5. At the shell prompt, type gradlew run (Windows) or
./gradlew run (Mac/Linux).
The first time you do this, Gradle will install itself and numerous other
packages, so it will take some time. After everything is installed,
subsequent builds and runs are faster.
Note you must be connected to the Internet the first time you run
gradlew so that Gradle can download the necessary packages.
Basic Gradle Tasks
There are a large number of Gradle tasks automatically available with
this book’s build. Gradle uses an approach called convention over
configuration which results in the availability of many tasks even if
you’re only trying to accomplish something very basic. Some of the
tasks that “came along for the ride” with this book are inappropriate or
don’t successfully execute. Here is a list of the Gradle tasks you will
typically use:
gradlew compileJava: Compiles all the Java files in the
book that can be compiled (some files don’t compile, to
demonstrate incorrect language usage).
gradlew run: First compiles, then executes all the Java files in
the book that can be executed (some files are library
components).
gradlew test: Executes all the unit tests (you’ll learn about
these in Validating Your Code).
gradlew chapter: ExampleName: Compiles and runs a specific
example program. For instance, gradlew
objects:HelloDate.



<!-- 分页 -->
<div style="page-break-after: always;"></div>

