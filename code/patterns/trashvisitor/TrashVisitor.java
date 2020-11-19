// patterns/trashvisitor/TrashVisitor.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java patterns.trashvisitor.TrashVisitor}
package patterns.trashvisitor;
import patterns.trash.*;
import java.util.*;

// Specific group of algorithms packaged
// in each implementation of Visitor:
class PriceVisitor implements Visitor {
  private double alSum; // Aluminum
  private double pSum; // Paper
  private double gSum; // Glass
  private double cSum; // Cardboard
  public static void show(String s) {
    System.out.println(s);
  }
  @Override
  public void visit(Aluminum al) {
    double v = al.weight() * al.value();
    show("value of Aluminum= " + v);
    alSum += v;
  }
  @Override
  public void visit(Paper p) {
    double v = p.weight() * p.value();
    show("value of Paper= " + v);
    pSum += v;
  }
  @Override
  public void visit(Glass g) {
    double v = g.weight() * g.value();
    show("value of Glass= " + v);
    gSum += v;
  }
  @Override
  public void visit(Cardboard c) {
    double v = c.weight() * c.value();
    show("value of Cardboard = " + v);
    cSum += v;
  }
  @Override
  public void total() {
    show(
      "Total Aluminum: $" + alSum + "\n" +
      "Total Paper: $" + pSum + "\n" +
      "Total Glass: $" + gSum + "\n" +
      "Total Cardboard: $" + cSum);
  }
}

class WeightVisitor implements Visitor {
  private double alSum; // Aluminum
  private double pSum; // Paper
  private double gSum; // Glass
  private double cSum; // Cardboard
  public static void show(String s) {
    System.out.println(s);
  }
  @Override
  public void visit(Aluminum al) {
    alSum += al.weight();
    show("Aluminum weight = " + al.weight());
  }
  @Override
  public void visit(Paper p) {
    pSum += p.weight();
    show("Paper weight = " + p.weight());
  }
  @Override
  public void visit(Glass g) {
    gSum += g.weight();
    show("Glass weight = " + g.weight());
  }
  @Override
  public void visit(Cardboard c) {
    cSum += c.weight();
    show("Cardboard weight = " + c.weight());
  }
  @Override
  public void total() {
    show("Total weight Aluminum:" + alSum);
    show("Total weight Paper:" + pSum);
    show("Total weight Glass:" + gSum);
    show("Total weight Cardboard:" + cSum);
  }
}

public class TrashVisitor {
  public static void main(String[] args) {
    List<Trash> bin = new ArrayList<>();
    // ParseTrash still works, without changes:
    ParseTrash.fillBin("trashvisitor", bin);
    List<Visitor> visitors = Arrays.asList(
      new PriceVisitor(), new WeightVisitor());
    bin.forEach( t -> {
      Visitable v = (Visitable) t;
      visitors.forEach(visitor -> v.accept(visitor));
    });
    visitors.forEach(Visitor::total);
  }
}
/* Output: (First and Last 10 Lines)
Loading patterns.trashvisitor.Glass
Loading patterns.trashvisitor.Paper
Loading patterns.trashvisitor.Aluminum
Loading patterns.trashvisitor.Cardboard
value of Glass= 12.420000225305557
Glass weight = 54.0
value of Paper= 2.2000000327825546
Paper weight = 22.0
value of Paper= 1.1000000163912773
Paper weight = 11.0
...________...________...________...________...
value of Cardboard = 5.060000091791153
Cardboard weight = 22.0
Total Aluminum: $860.0499778985977
Total Paper: $35.80000053346157
Total Glass: $150.1900027245283
Total Cardboard: $40.02000072598457
Total weight Aluminum:515.0
Total weight Paper:358.0
Total weight Glass:653.0
Total weight Cardboard:174.0
*/
