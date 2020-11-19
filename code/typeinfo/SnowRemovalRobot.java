// typeinfo/SnowRemovalRobot.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class SnowRemovalRobot implements Robot {
  private String name;
  public SnowRemovalRobot(String name) {
    this.name = name;
  }
  @Override
  public String name() { return name; }
  @Override
  public String model() { return "SnowBot Series 11"; }
  private List<Operation> ops = Arrays.asList(
    new Operation(
      () -> name + " can shovel snow",
      () -> System.out.println(
        name + " shoveling snow")),
    new Operation(
      () -> name + " can chip ice",
      () -> System.out.println(name + " chipping ice")),
    new Operation(
      () -> name + " can clear the roof",
      () -> System.out.println(
        name + " clearing roof")));
  public List<Operation> operations() { return ops; }
  public static void main(String[] args) {
    Robot.test(new SnowRemovalRobot("Slusher"));
  }
}
/* Output:
Robot name: Slusher
Robot model: SnowBot Series 11
Slusher can shovel snow
Slusher shoveling snow
Slusher can chip ice
Slusher chipping ice
Slusher can clear the roof
Slusher clearing roof
*/
