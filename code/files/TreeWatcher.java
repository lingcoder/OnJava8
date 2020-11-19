// files/TreeWatcher.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {ExcludeFromGradle}
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.util.concurrent.*;

public class TreeWatcher {
  static void watchDir(Path dir) {
    try {
      WatchService watcher =
        FileSystems.getDefault().newWatchService();
      dir.register(watcher, ENTRY_DELETE);
      Executors.newSingleThreadExecutor().submit(() -> {
        try {
          WatchKey key = watcher.take();
          for(WatchEvent evt : key.pollEvents()) {
            System.out.println(
              "evt.context(): " + evt.context() +
              "\nevt.count(): " + evt.count() +
              "\nevt.kind(): " + evt.kind());
            System.exit(0);
          }
        } catch(InterruptedException e) {
          return;
        }
      });
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  public static void
  main(String[] args) throws Exception {
    Directories.refreshTestDir();
    Directories.populateTestDir();
    Files.walk(Paths.get("test"))
      .filter(Files::isDirectory)
      .forEach(TreeWatcher::watchDir);
    PathWatcher.delTxtFiles();
  }
}
/* Output:
deleting test\bag\foo\bar\baz\File.txt
deleting test\bar\baz\bag\foo\File.txt
evt.context(): File.txt
evt.count(): 1
evt.kind(): ENTRY_DELETE
*/
