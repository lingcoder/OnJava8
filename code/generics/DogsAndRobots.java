// generics/DogsAndRobots.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// No (direct) latent typing in Java
import typeinfo.pets.*;

class PerformingDog extends Dog implements Performs {
  @Override
  public void speak() { System.out.println("Woof!"); }
  @Override
  public void sit() { System.out.println("Sitting"); }
  public void reproduce() {}
}

class Robot implements Performs {
  public void speak() { System.out.println("Click!"); }
  public void sit() { System.out.println("Clank!"); }
  public void oilChange() {}
}

class Communicate {
  public static <T extends Performs>
  void perform(T performer) {
    performer.speak();
    performer.sit();
  }
}

public class DogsAndRobots {
  public static void main(String[] args) {
    Communicate.perform(new PerformingDog());
    Communicate.perform(new Robot());
  }
}
/* Output:
Woof!
Sitting
Click!
Clank!
*/
