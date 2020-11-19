// typeinfo/pets/LiteralPetCreator.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using class literals
// {java typeinfo.pets.LiteralPetCreator}
package typeinfo.pets;
import java.util.*;

public class LiteralPetCreator extends PetCreator {
  // No try block needed.
  @SuppressWarnings("unchecked")
  public static
  final List<Class<? extends Pet>> ALL_TYPES =
    Collections.unmodifiableList(Arrays.asList(
      Pet.class, Dog.class, Cat.class, Rodent.class,
      Mutt.class, Pug.class, EgyptianMau.class,
      Manx.class, Cymric.class, Rat.class,
      Mouse.class, Hamster.class));
  // Types for random creation:
  private static final
  List<Class<? extends Pet>> TYPES =
    ALL_TYPES.subList(ALL_TYPES.indexOf(Mutt.class),
      ALL_TYPES.size());
  @Override
  public List<Class<? extends Pet>> types() {
    return TYPES;
  }
  public static void main(String[] args) {
    System.out.println(TYPES);
  }
}
/* Output:
[class typeinfo.pets.Mutt, class typeinfo.pets.Pug,
class typeinfo.pets.EgyptianMau, class
typeinfo.pets.Manx, class typeinfo.pets.Cymric, class
typeinfo.pets.Rat, class typeinfo.pets.Mouse, class
typeinfo.pets.Hamster]
*/
