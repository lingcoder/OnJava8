// staticchecking/petspeak/PetSpeak.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Speaking pets in Java
// {java staticchecking.petspeak.PetSpeak}
package staticchecking.petspeak;

interface Pet {
  void speak();
}

class Cat implements Pet {
  public void speak() {
    System.out.println("meow!");
  }
}

class Dog implements Pet {
  public void speak() {
    System.out.println("woof!");
  }
}

public class PetSpeak {
  static void command(Pet p) { p.speak(); }
  public static void main(String[] args) {
    Pet[] pets = { new Cat(), new Dog() };
    for(Pet pet : pets)
      command(pet);
  }
}
/* Output:
meow!
woof!
*/
