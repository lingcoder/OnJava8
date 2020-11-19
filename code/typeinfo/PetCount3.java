// typeinfo/PetCount3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using isInstance()
import java.util.*;
import java.util.stream.*;
import onjava.*;
import typeinfo.pets.*;

public class PetCount3 {
  static class Counter extends
  LinkedHashMap<Class<? extends Pet>, Integer> {
    Counter() {
      super(LiteralPetCreator.ALL_TYPES.stream()
        .map(lpc -> Pair.make(lpc, 0))
        .collect(
          Collectors.toMap(Pair::key, Pair::value)));
    }
    public void count(Pet pet) {
      // Class.isInstance() eliminates instanceofs:
      entrySet().stream()
        .filter(pair -> pair.getKey().isInstance(pet))
        .forEach(pair ->
          put(pair.getKey(), pair.getValue() + 1));
    }
    @Override
    public String toString() {
      String result = entrySet().stream()
        .map(pair -> String.format("%s=%s",
          pair.getKey().getSimpleName(),
          pair.getValue()))
        .collect(Collectors.joining(", "));
      return "{" + result + "}";
    }
  }
  public static void main(String[] args) {
    Counter petCount = new Counter();
    Pets.stream()
      .limit(20)
      .peek(petCount::count)
      .forEach(p -> System.out.print(
        p.getClass().getSimpleName() + " "));
    System.out.println("\n" + petCount);
  }
}
/* Output:
Rat Manx Cymric Mutt Pug Cymric Pug Manx Cymric Rat
EgyptianMau Hamster EgyptianMau Mutt Mutt Cymric Mouse
Pug Mouse Cymric
{Rat=2, Pug=3, Mutt=3, Mouse=2, Cat=9, Dog=6, Cymric=5,
EgyptianMau=2, Rodent=5, Hamster=1, Manx=7, Pet=20}
*/
