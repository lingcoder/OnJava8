// onjava/RmDir.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package onjava;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;

public class RmDir {
  public static void rmdir(Path dir)
  throws IOException {
    Files.walkFileTree(dir,
      new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult
      visitFile(Path file, BasicFileAttributes attrs)
      throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }
      @Override
      public FileVisitResult
      postVisitDirectory(Path dir, IOException exc)
      throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
