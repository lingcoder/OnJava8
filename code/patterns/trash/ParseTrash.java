// patterns/trash/ParseTrash.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Open a file and parse its contents into
// Trash objects, placing each into a List
// {java patterns.trash.ParseTrash}
package patterns.trash;
import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.Files;

public class ParseTrash {
  public static <T extends Trash> void
  fillBin(String pckg, Fillable<T> bin) {
    try {
      Files.lines(Paths.get("trash", "Trash.dat"))
        // Remove empty lines and comment lines:
        .filter(line -> line.trim().length() != 0)
        .filter(line -> !line.startsWith("//"))
        .forEach( line -> {
          String type = "patterns." + pckg + "." +
            line.substring(
              0, line.indexOf(':')).trim();
          double weight = Double.valueOf(
            line.substring(line.indexOf(':') + 1)
            .trim());
          bin.addTrash(Trash.factory(
            new Trash.Info(type, weight)));
        });
    } catch(IOException |
            NumberFormatException |
            Trash.TrashClassNotFoundException |
            Trash.CannotCreateTrashException e) {
      throw new RuntimeException(e);
    }
  }
  // Special case to handle List:
  public static <T extends Trash> void
  fillBin(String pckg, List<T> bin) {
    fillBin(pckg, new FillableList<>(bin));
  }
  // Basic test:
  public static void main(String[] args) {
    List<Trash> t = new ArrayList<>();
    fillBin("trash", t);
    t.forEach(System.out::println);
  }
}
/* Output:
Loading patterns.trash.Glass
Loading patterns.trash.Paper
Loading patterns.trash.Aluminum
Loading patterns.trash.Cardboard
patterns.trash.Glass w:54.0 v:0.23
patterns.trash.Paper w:22.0 v:0.10
patterns.trash.Paper w:11.0 v:0.10
patterns.trash.Glass w:17.0 v:0.23
patterns.trash.Aluminum w:89.0 v:1.67
patterns.trash.Paper w:88.0 v:0.10
patterns.trash.Aluminum w:76.0 v:1.67
patterns.trash.Cardboard w:96.0 v:0.23
patterns.trash.Aluminum w:25.0 v:1.67
patterns.trash.Aluminum w:34.0 v:1.67
patterns.trash.Glass w:11.0 v:0.23
patterns.trash.Glass w:68.0 v:0.23
patterns.trash.Glass w:43.0 v:0.23
patterns.trash.Aluminum w:27.0 v:1.67
patterns.trash.Cardboard w:44.0 v:0.23
patterns.trash.Aluminum w:18.0 v:1.67
patterns.trash.Paper w:91.0 v:0.10
patterns.trash.Glass w:63.0 v:0.23
patterns.trash.Glass w:50.0 v:0.23
patterns.trash.Glass w:80.0 v:0.23
patterns.trash.Aluminum w:81.0 v:1.67
patterns.trash.Cardboard w:12.0 v:0.23
patterns.trash.Glass w:12.0 v:0.23
patterns.trash.Glass w:54.0 v:0.23
patterns.trash.Aluminum w:36.0 v:1.67
patterns.trash.Aluminum w:93.0 v:1.67
patterns.trash.Glass w:93.0 v:0.23
patterns.trash.Paper w:80.0 v:0.10
patterns.trash.Glass w:36.0 v:0.23
patterns.trash.Glass w:12.0 v:0.23
patterns.trash.Glass w:60.0 v:0.23
patterns.trash.Paper w:66.0 v:0.10
patterns.trash.Aluminum w:36.0 v:1.67
patterns.trash.Cardboard w:22.0 v:0.23
*/
