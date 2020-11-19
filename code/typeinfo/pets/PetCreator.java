// typeinfo/pets/PetCreator.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Creates random sequences of Pets
package typeinfo.pets;
import java.util.*;
import java.util.function.*;
import java.lang.reflect.InvocationTargetException;

public abstract
class PetCreator implements Supplier<Pet> {
  private Random rand = new Random(47);
  // The List of the different types of Pet to create:
  public abstract List<Class<? extends Pet>> types();
  public Pet get() { // Create one random Pet
    int n = rand.nextInt(types().size());
    try {
      return types().get(n)
             .getConstructor().newInstance();
    } catch(InstantiationException |
            NoSuchMethodException |
            InvocationTargetException |
            IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
