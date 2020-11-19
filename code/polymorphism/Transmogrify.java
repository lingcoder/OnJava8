// polymorphism/Transmogrify.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Dynamically changing the behavior of an object
// via composition (the "State" design pattern)

class Actor {
  public void act() {}
}

class HappyActor extends Actor {
  @Override
  public void act() {
    System.out.println("HappyActor");
  }
}

class SadActor extends Actor {
  @Override
  public void act() {
    System.out.println("SadActor");
  }
}

class Stage {
  private Actor actor = new HappyActor();
  public void change() { actor = new SadActor(); }
  public void performPlay() { actor.act(); }
}

public class Transmogrify {
  public static void main(String[] args) {
    Stage stage = new Stage();
    stage.performPlay();
    stage.change();
    stage.performPlay();
  }
}
/* Output:
HappyActor
SadActor
*/
