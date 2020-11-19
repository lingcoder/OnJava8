// files/PathWatcher.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {ExcludeFromGradle}
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.util.concurrent.*;

public class PathWatcher {
  static Path test = Paths.get("test");
  static void delTxtFiles() {
    try {
      Files.walk(test)
        .filter(f ->
          f.toString().endsWith(".txt"))
        .forEach(f -> {
          try {
            System.out.println("deleting " + f);
            Files.delete(f);
          } catch(IOException e) {
            throw new RuntimeException(e);
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
    Files.createFile(test.resolve("Hello.txt"));
    WatchService watcher =
      FileSystems.getDefault().newWatchService();
    test.register(watcher, ENTRY_DELETE);
    Executors.newSingleThreadScheduledExecutor()
      .schedule(
        PathWatcher::delTxtFiles,
        250, TimeUnit.MILLISECONDS);
    WatchKey key = watcher.take();
    for(WatchEvent evt : key.pollEvents()) {
      System.out.println(
        "evt.context(): " + evt.context() +
        "\nevt.count(): " + evt.count() +
        "\nevt.kind(): " + evt.kind());
      System.exit(0);
    }
  }
}
/* Output:
deleting test\bag\foo\bar\baz\File.txt
deleting test\bar\baz\bag\foo\File.txt
deleting test\baz\bag\foo\bar\File.txt
deleting test\foo\bar\baz\bag\File.txt
deleting test\Hello.txt
evt.context(): Hello.txt
evt.count(): 1
evt.kind(): ENTRY_DELETE
*/
