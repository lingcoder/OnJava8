// streams/RandomWords.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.io.*;
import java.nio.file.*;

public class RandomWords implements Supplier<String> {
  List<String> words = new ArrayList<>();
  Random rand = new Random(47);
  RandomWords(String fname) throws IOException {
    List<String> lines =
      Files.readAllLines(Paths.get(fname));
    // Skip the first line:
    for(String line : lines.subList(1, lines.size())) {
      for(String word : line.split("[ .?,]+"))
        words.add(word.toLowerCase());
    }
  }
  public String get() {
    return words.get(rand.nextInt(words.size()));
  }
  @Override
  public String toString() {
    return words.stream()
      .collect(Collectors.joining(" "));
  }
  public static void
  main(String[] args) throws Exception {
    System.out.println(
      Stream.generate(new RandomWords("Cheese.dat"))
        .limit(10)
        .collect(Collectors.joining(" ")));
  }
}
/* Output:
it shop sir the much cheese by conclusion district is
*/
