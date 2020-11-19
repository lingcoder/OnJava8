// equalshashcode/SubtypeEquality2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

class Dog2 extends Animal {
  Dog2(String name, Size size) {
    super(name, size);
  }
  @Override
  public boolean equals(Object rval) {
    return rval instanceof Dog2 &&
      super.equals(rval);
  }
}

class Pig2 extends Animal {
  Pig2(String name, Size size) {
    super(name, size);
  }
  @Override
  public boolean equals(Object rval) {
    return rval instanceof Pig2 &&
      super.equals(rval);
  }
}

public class SubtypeEquality2 {
  public static void main(String[] args) {
    Set<Animal> pets = new HashSet<>();
    pets.add(new Dog2("Ralph", Size.MEDIUM));
    pets.add(new Pig2("Ralph", Size.MEDIUM));
    pets.forEach(System.out::println);
  }
}
/* Output:
Dog2[0]: Ralph MEDIUM a752aeee
Pig2[1]: Ralph MEDIUM a752aeee
*/
