# 第二章 安装Java和本书用例

现在，我们来为这次阅读之旅作些准备吧！

在开始学习 Java 之前，你必须要先安装好 Java 和本书的源代码示例。因为考虑到可能有“专门的初学者”从本书开始学习编程，所以我会仔细解释计算机命令行 Shell 的这个过程。 如果你已经有此方面的经验了，可以跳过这段安装说明。如果你对此处描述的任何术语或过程仍不清楚，还可以通过 Google 搜索找到答案。具体的问题或困难请试着在 StackOverflow 上提问。或者去 YouTube 看有没有相关的安装说明。


## 编辑器


To create and modify Java program files—the code listings shown in
this book—you need a program called an editor. You’ll also need an
editor to make changes to your system configuration files, which is
sometimes required during installation.
Programming editors vary from heavyweight Integrated Development
Environments (IDEs, like Eclipse, NetBeans and IntelliJ IDEA) to
more basic text manipulation applications. If you already have an IDE
and are comfortable with it, feel free to use that for this book, but in
the interest of keeping things simple, I recommend the Atom editor.
Find it at atom.io.
Atom is free and open-source, is very simple to install, works on all
platforms (Windows, Mac and Linux), and has a built-in Java mode
that is automatically invoked when you open a Java file. It isn’t a
heavy-duty IDE so it doesn’t get confusing, which is ideal for this book.
On the other hand, it has some handy editing features that you’ll
probably come to love. More details are on their site.
There are many other editors; these are a subculture unto themselves
and people even get into heated arguments about their merits. If you
find one you like better, it’s not too hard to change. The important
thing is to choose one and get comfortable with it.

## Shell

If you haven’t programmed before, you might be unfamiliar with your
operating system shell (also called the command prompt in Windows).
The shell harkens back to the early days of computing when everything
happened by typing commands and the computer responded by
displaying responses; it was all text-based.
Although it can seem primitive in the age of graphical user interfaces,
a shell provides a surprising number of valuable features. We’ll use the
shell regularly in this book, both as part of the installation process and
to run Java programs.
Starting a Shell
Mac: Click on the Spotlight (the magnifying-glass icon in the upper-
right corner of the screen) and type “terminal.” Click on the
application that looks like a little TV screen (you might also be able to
hit “Return”). This starts a shell in your home directory.
Windows: First, start the Windows Explorer to navigate through
your directories:
Windows 7: click the “Start” button in the lower left corner of the
screen. In the Start Menu search box area type “explorer” then
press the “Enter” key.
Windows 8: click Windows+Q, type “explorer” then press the
“Enter” key.
Windows 10: click Windows+E.
Once the Windows Explorer is running, move through the folders on
your computer by double-clicking on them with the mouse. Navigate
to the desired folder. Now click the file tab at the top left of the
Explorer window and select “Open command prompt.” This opens a
shell in the destination directory.
Linux: To open a shell in your home directory:
Debian: Press Alt+F2. In the dialog that pops up, type ‘gnome-
terminal’
Ubuntu: Either right-click on the desktop and select ‘Open
Terminal’, or press Ctrl+Alt+T
Redhat: Right-click on the desktop and select ‘Open Terminal’
Fedora: Press Alt+F2. In the dialog that pops up, type ‘gnome-
terminal’
Directories
Directories are one of the fundamental elements of a shell. Directories
hold files, as well as other directories. Think of a directory as a tree
with branches. If books is a directory on your system and it has two
other directories as branches, for example math and art, we say you
have a directory books with two subdirectories math and art. We refer to
them as books/math and books/art since books is their
parent directory. Note that Windows uses backslashes rather than
forward slashes to separate the parts of a directory.
Basic Shell Operations
The shell operations I show here are approximately identical across
operating systems. For the purposes of this book, here are the essential
operations in a shell:
Change directory: Use cd followed by the name of the
directory where you want to move, or cd .. if you want to move
up a directory. If you want to move to a different directory while
remembering where you came from, use pushd followed by the
different directory name. Then, to return to the previous
directory, just say popd.
Directory listing: ls (dir in Windows) displays all the files
and subdirectory names in the current directory. Use the wildcard
* (asterisk) to narrow your search. For example, if you want to list
all the files ending in “.java,” you say ls *.java (Windows:
dir *.java). If you want to list the files starting with “F” and
ending in “.java,” you say ls F*.java (Windows: dir
F*.java).
Create a directory: use the mkdir (“make directory”)
command (Windows: md), followed by the name of the directory
you want to create. For example, mkdir books (Windows: md
books).
Remove a file: Use rm (“remove”) followed by the name of the
file you wish to remove (Windows: del). For example, rm
somefile.java (Windows: del somefile.java).
Remove a directory: use the rm -r command to remove the
files in the directory and the directory itself (Windows:
deltree). For example, rm -r books (Windows: deltree
books).
Repeat a command: The “up arrow” on all three operating
systems moves through previous commands so you can edit and
repeat them. On Mac/Linux, !! repeats the last command and !n
repeats the nth command.
Command history: Use history in Mac/Linux or press the
F7 key in Windows. This gives you a list of all the commands
you’ve entered. Mac/Linux provides numbers to refer to when you
want to repeat a command.
Unpacking a zip archive: A file name ending with .zip is an
archive containing other files in a compressed format. Both Linux
and Mac have command-line unzip utilities, and you can install
a command-line unzip for Windows via the Internet. However,
in all three systems the graphical file browser (Windows Explorer,
the Mac Finder, or Nautilus or equivalent on Linux) will browse to
the directory containing your zip file. Then right-mouse-click on
the file and select “Open” on the Mac, “Extract Here” on Linux, or
“Extract all …” on Windows.
To learn more about your shell, search Wikipedia for Windows Shell
or, for Mac/Linux, Bash Shell.

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

