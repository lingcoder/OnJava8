// typeinfo/pets/Pets.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Facade to produce a default PetCreator
package typeinfo.pets;
import java.util.*;
import java.util.stream.*;

public class Pets {
  public static final PetCreator CREATOR =
    new LiteralPetCreator();
  public static Pet get() {
    return CREATOR.get();
  }
  public static Pet[] array(int size) {
    Pet[] result = new Pet[size];
    for(int i = 0; i < size; i++)
      result[i] = CREATOR.get();
    return result;
  }
  public static List<Pet> list(int size) {
    List<Pet> result = new ArrayList<>();
    Collections.addAll(result, array(size));
    return result;
  }
  public static Stream<Pet> stream() {
    return Stream.generate(CREATOR);
  }
}
