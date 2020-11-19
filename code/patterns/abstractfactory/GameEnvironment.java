// patterns/abstractfactory/GameEnvironment.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// An example of the Abstract Factory pattern
// {java patterns.abstractfactory.GameEnvironment}
package patterns.abstractfactory;
import java.util.function.*;

interface Obstacle {
  void action();
}

interface Player {
  void interactWith(Obstacle o);
}

class Kitty implements Player {
  @Override
  public void interactWith(Obstacle ob) {
    System.out.print("Kitty has encountered a ");
    ob.action();
  }
}

class KungFuGuy implements Player {
  @Override
  public void interactWith(Obstacle ob) {
    System.out.print("KungFuGuy now battles a ");
    ob.action();
  }
}

class Puzzle implements Obstacle {
  @Override
  public void action() {
    System.out.println("Puzzle");
  }
}

class NastyWeapon implements Obstacle {
  @Override
  public void action() {
    System.out.println("NastyWeapon");
  }
}

// The Abstract Factory:
class GameElementFactory {
  Supplier<Player> player;
  Supplier<Obstacle> obstacle;
}

// Concrete factories:
class KittiesAndPuzzles
extends GameElementFactory {
  KittiesAndPuzzles() {
    player = Kitty::new;
    obstacle = Puzzle::new;
  }
}

class KillAndDismember
extends GameElementFactory {
  KillAndDismember() {
    player = KungFuGuy::new;
    obstacle = NastyWeapon::new;
  }
}

public class GameEnvironment {
  private Player p;
  private Obstacle ob;
  public
  GameEnvironment(GameElementFactory factory) {
    p = factory.player.get();
    ob = factory.obstacle.get();
  }
  public void play() {
    p.interactWith(ob);
  }
  public static void main(String[] args) {
    GameElementFactory
      kp = new KittiesAndPuzzles(),
      kd = new KillAndDismember();
    GameEnvironment
      g1 = new GameEnvironment(kp),
      g2 = new GameEnvironment(kd);
    g1.play();
    g2.play();
  }
}
/* Output:
Kitty has encountered a Puzzle
KungFuGuy now battles a NastyWeapon
*/
