// files/PathInfo.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.nio.file.*;
import java.net.URI;
import java.io.File;
import java.io.IOException;

public class PathInfo {
  static void show(String id, Object p) {
    System.out.println(id + ": " + p);
  }
  static void info(Path p) {
    show("toString", p);
    show("Exists", Files.exists(p));
    show("RegularFile", Files.isRegularFile(p));
    show("Directory", Files.isDirectory(p));
    show("Absolute", p.isAbsolute());
    show("FileName", p.getFileName());
    show("Parent", p.getParent());
    show("Root", p.getRoot());
    System.out.println("******************");
  }
  public static void main(String[] args) {
    System.out.println(System.getProperty("os.name"));
    info(Paths.get(
      "C:", "path", "to", "nowhere", "NoFile.txt"));
    Path p = Paths.get("PathInfo.java");
    info(p);
    Path ap = p.toAbsolutePath();
    info(ap);
    info(ap.getParent());
    try {
      info(p.toRealPath());
    } catch(IOException e) {
      System.out.println(e);
    }
    URI u = p.toUri();
    System.out.println("URI: " + u);
    Path puri = Paths.get(u);
    System.out.println(Files.exists(puri));
    File f = ap.toFile(); // Don't be fooled
  }
}
/* Output:
Windows 10
toString: C:\path\to\nowhere\NoFile.txt
Exists: false
RegularFile: false
Directory: false
Absolute: true
FileName: NoFile.txt
Parent: C:\path\to\nowhere
Root: C:\
******************
toString: PathInfo.java
Exists: true
RegularFile: true
Directory: false
Absolute: false
FileName: PathInfo.java
Parent: null
Root: null
******************
toString: C:\Users\Bruce\Documents\GitHub\on-
java\ExtractedExamples\files\PathInfo.java
Exists: true
RegularFile: true
Directory: false
Absolute: true
FileName: PathInfo.java
Parent: C:\Users\Bruce\Documents\GitHub\on-
java\ExtractedExamples\files
Root: C:\
******************
toString: C:\Users\Bruce\Documents\GitHub\on-
java\ExtractedExamples\files
Exists: true
RegularFile: false
Directory: true
Absolute: true
FileName: files
Parent: C:\Users\Bruce\Documents\GitHub\on-
java\ExtractedExamples
Root: C:\
******************
toString: C:\Users\Bruce\Documents\GitHub\on-
java\ExtractedExamples\files\PathInfo.java
Exists: true
RegularFile: true
Directory: false
Absolute: true
FileName: PathInfo.java
Parent: C:\Users\Bruce\Documents\GitHub\on-
java\ExtractedExamples\files
Root: C:\
******************
URI: file:///C:/Users/Bruce/Documents/GitHub/on-
java/ExtractedExamples/files/PathInfo.java
true
*/
