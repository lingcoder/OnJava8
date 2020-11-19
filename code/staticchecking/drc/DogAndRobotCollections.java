// staticchecking/drc/DogAndRobotCollections.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java staticchecking.drc.DogAndRobotCollections}
package staticchecking.drc;
import java.util.*;

class Dog {
  public void talk() {
    System.out.println("Woof!");
  }
  public void reproduce() { }
}

class Robot {
  public void talk() {
    System.out.println("Click!");
  }
  public void oilChange() { }
}

public class DogAndRobotCollections {
  public static void main(String[] args) {
    List<Dog> dogList = new ArrayList<>();
    List<Robot> robotList = new ArrayList<>();
    for(int i = 0; i < 10; i++)
      dogList.add(new Dog());
    //- dogList.add(new Robot()); // Compile-time error
    for(int i = 0; i < 10; i++)
      robotList.add(new Robot());
    //- robotList.add(new Dog()); // Compile-time error
    dogList.forEach(Dog::talk);
    robotList.forEach(Robot::talk);
  }
}
/* Output:
Woof!
Woof!
Woof!
Woof!
Woof!
Woof!
Woof!
Woof!
Woof!
Woof!
Click!
Click!
Click!
Click!
Click!
Click!
Click!
Click!
Click!
Click!
*/
