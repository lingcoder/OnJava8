// annotations/AtUnitExample4.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java onjava.atunit.AtUnit
// build/classes/java/main/annotations/AtUnitExample4.class}
// {VisuallyInspectOutput}
package annotations;
import java.util.*;
import onjava.atunit.*;
import onjava.*;

public class AtUnitExample4 {
  static String theory = "All brontosauruses " +
    "are thin at one end, much MUCH thicker in the " +
    "middle, and then thin again at the far end.";
  private String word;
  private Random rand = new Random(); // Time-based seed
  public AtUnitExample4(String word) {
    this.word = word;
  }
  public String getWord() { return word; }
  public String scrambleWord() {
    List<Character> chars = Arrays.asList(
      ConvertTo.boxed(word.toCharArray()));
    Collections.shuffle(chars, rand);
    StringBuilder result = new StringBuilder();
    for(char ch : chars)
      result.append(ch);
    return result.toString();
  }
  @TestProperty
  static List<String> input =
    Arrays.asList(theory.split(" "));
  @TestProperty
  static Iterator<String> words = input.iterator();
  @TestObjectCreate
  static AtUnitExample4 create() {
    if(words.hasNext())
      return new AtUnitExample4(words.next());
    else
      return null;
  }
  @Test
  boolean words() {
    System.out.println("'" + getWord() + "'");
    return getWord().equals("are");
  }
  @Test
  boolean scramble1() {
    // Use specific seed to get verifiable results:
    rand = new Random(47);
    System.out.println("'" + getWord() + "'");
    String scrambled = scrambleWord();
    System.out.println(scrambled);
    return scrambled.equals("lAl");
  }
  @Test
  boolean scramble2() {
    rand = new Random(74);
    System.out.println("'" + getWord() + "'");
    String scrambled = scrambleWord();
    System.out.println(scrambled);
    return scrambled.equals("tsaeborornussu");
  }
}
/* Output:
annotations.AtUnitExample4
  . words 'All'
(failed)
  . scramble1 'brontosauruses'
ntsaueorosurbs
(failed)
  . scramble2 'are'
are
(failed)
(3 tests)

>>> 3 FAILURES <<<
  annotations.AtUnitExample4: words
  annotations.AtUnitExample4: scramble1
  annotations.AtUnitExample4: scramble2
*/
