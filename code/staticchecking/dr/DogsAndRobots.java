// staticchecking/dr/DogsAndRobots.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java staticchecking.dr.DogsAndRobots}
package staticchecking.dr;

interface Speaks { void talk(); }

class Dog implements Speaks {
  public void talk() {
    System.out.println("Woof!");
  }
  public void reproduce() { }
}

class Robot implements Speaks {
  public void talk() {
    System.out.println("Click!");
  }
  public void oilChange() { }
}

class Communicate {
  public static <T extends Speaks>
  void speak(T speaker) {
    speaker.talk();
  }
}

public class DogsAndRobots {
  public static void main(String[] args) {
    Dog d = new Dog();
    Robot r = new Robot();
    Communicate.speak(d);
    Communicate.speak(r);
  }
}
/* Output:
Woof!
Click!
*/
