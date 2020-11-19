// generics/DogsAndRobotMethodReferences.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// "Assisted Latent Typing"
import typeinfo.pets.*;
import java.util.function.*;

class PerformingDogA extends Dog {
  public void speak() { System.out.println("Woof!"); }
  public void sit() { System.out.println("Sitting"); }
  public void reproduce() {}
}

class RobotA {
  public void speak() { System.out.println("Click!"); }
  public void sit() { System.out.println("Clank!"); }
  public void oilChange() {}
}

class CommunicateA {
  public static <P> void perform(P performer,
    Consumer<P> action1, Consumer<P> action2) {
    action1.accept(performer);
    action2.accept(performer);
  }
}

public class DogsAndRobotMethodReferences {
  public static void main(String[] args) {
    CommunicateA.perform(new PerformingDogA(),
      PerformingDogA::speak, PerformingDogA::sit);
    CommunicateA.perform(new RobotA(),
      RobotA::speak, RobotA::sit);
    CommunicateA.perform(new Mime(),
      Mime::walkAgainstTheWind,
      Mime::pushInvisibleWalls);
  }
}
/* Output:
Woof!
Sitting
Click!
Clank!
*/
