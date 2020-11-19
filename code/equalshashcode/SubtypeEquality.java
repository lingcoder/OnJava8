// equalshashcode/SubtypeEquality.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

enum Size { SMALL, MEDIUM, LARGE }

class Animal {
  private static int counter = 0;
  private final int id = counter++;
  private final String name;
  private final Size size;
  Animal(String name, Size size) {
    this.name = name;
    this.size = size;
  }
  @Override
  public boolean equals(Object rval) {
    return rval instanceof Animal &&
      // Objects.equals(id, ((Animal)rval).id) && // [1]
      Objects.equals(name, ((Animal)rval).name) &&
      Objects.equals(size, ((Animal)rval).size);
  }
  @Override
  public int hashCode() {
    return Objects.hash(name, size);
    // return Objects.hash(name, size, id);  // [2]
  }
  @Override
  public String toString() {
    return String.format("%s[%d]: %s %s %x",
      getClass().getSimpleName(), id,
      name, size, hashCode());
  }
}

class Dog extends Animal {
  Dog(String name, Size size) {
    super(name, size);
  }
}

class Pig extends Animal {
  Pig(String name, Size size) {
    super(name, size);
  }
}

public class SubtypeEquality {
  public static void main(String[] args) {
    Set<Animal> pets = new HashSet<>();
    pets.add(new Dog("Ralph", Size.MEDIUM));
    pets.add(new Pig("Ralph", Size.MEDIUM));
    pets.forEach(System.out::println);
  }
}
/* Output:
Dog[0]: Ralph MEDIUM a752aeee
*/
