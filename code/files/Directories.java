// files/Directories.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.nio.file.*;
import onjava.RmDir;

public class Directories {
  static Path test = Paths.get("test");
  static String sep =
    FileSystems.getDefault().getSeparator();
  static List<String> parts =
    Arrays.asList("foo", "bar", "baz", "bag");
  static Path makeVariant() {
    Collections.rotate(parts, 1);
    return Paths.get("test", String.join(sep, parts));
  }
  static void refreshTestDir() throws Exception {
    if(Files.exists(test))
      RmDir.rmdir(test);
    if(!Files.exists(test))
      Files.createDirectory(test);
  }
  public static void
  main(String[] args) throws Exception {
    refreshTestDir();
    Files.createFile(test.resolve("Hello.txt"));
    Path variant = makeVariant();
    // Throws exception (too many levels):
    try {
      Files.createDirectory(variant);
    } catch(Exception e) {
      System.out.println("Nope, that doesn't work.");
    }
    populateTestDir();
    Path tempdir =
      Files.createTempDirectory(test, "DIR_");
    Files.createTempFile(tempdir, "pre", ".non");
    Files.newDirectoryStream(test)
      .forEach(System.out::println);
    System.out.println("*********");
    Files.walk(test).forEach(System.out::println);
  }
  static void populateTestDir() throws Exception {
    for(int i = 0; i < parts.size(); i++) {
      Path variant = makeVariant();
      if(!Files.exists(variant)) {
        Files.createDirectories(variant);
        Files.copy(Paths.get("Directories.java"),
          variant.resolve("File.txt"));
        Files.createTempFile(variant, null, null);
      }
    }
  }
}
/* Output:
Nope, that doesn't work.
test\bag
test\bar
test\baz
test\DIR_5142667942049986036
test\foo
test\Hello.txt
*********
test
test\bag
test\bag\foo
test\bag\foo\bar
test\bag\foo\bar\baz
test\bag\foo\bar\baz\8279660869874696036.tmp
test\bag\foo\bar\baz\File.txt
test\bar
test\bar\baz
test\bar\baz\bag
test\bar\baz\bag\foo
test\bar\baz\bag\foo\1274043134240426261.tmp
test\bar\baz\bag\foo\File.txt
test\baz
test\baz\bag
test\baz\bag\foo
test\baz\bag\foo\bar
test\baz\bag\foo\bar\6130572530014544105.tmp
test\baz\bag\foo\bar\File.txt
test\DIR_5142667942049986036
test\DIR_5142667942049986036\pre7704286843227113253.non
test\foo
test\foo\bar
test\foo\bar\baz
test\foo\bar\baz\bag
test\foo\bar\baz\bag\5412864507741775436.tmp
test\foo\bar\baz\bag\File.txt
test\Hello.txt
*/
