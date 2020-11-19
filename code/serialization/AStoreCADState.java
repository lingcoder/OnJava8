// serialization/AStoreCADState.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Saving the state of a fictitious CAD system
import java.io.*;
import java.util.*;
import java.util.stream.*;

enum Color { RED, BLUE, GREEN }

abstract class Shape implements Serializable {
  private int xPos, yPos, dimension;
  private static Random rand = new Random(47);
  private static int counter = 0;
  public abstract void setColor(Color newColor);
  public abstract Color getColor();
  Shape(int xVal, int yVal, int dim) {
    xPos = xVal;
    yPos = yVal;
    dimension = dim;
  }
  public String toString() {
    return getClass() + "color[" + getColor() +
      "] xPos[" + xPos + "] yPos[" + yPos +
      "] dim[" + dimension + "]\n";
  }
  public static Shape randomFactory() {
    int xVal = rand.nextInt(100);
    int yVal = rand.nextInt(100);
    int dim = rand.nextInt(100);
    switch(counter++ % 3) {
      default:
      case 0: return new Circle(xVal, yVal, dim);
      case 1: return new Square(xVal, yVal, dim);
      case 2: return new Line(xVal, yVal, dim);
    }
  }
}

class Circle extends Shape {
  private static Color color = Color.RED;
  Circle(int xVal, int yVal, int dim) {
    super(xVal, yVal, dim);
  }
  public void setColor(Color newColor) {
    color = newColor;
  }
  public Color getColor() { return color; }
}

class Square extends Shape {
  private static Color color = Color.RED;
  Square(int xVal, int yVal, int dim) {
    super(xVal, yVal, dim);
  }
  public void setColor(Color newColor) {
    color = newColor;
  }
  public Color getColor() { return color; }
}

class Line extends Shape {
  private static Color color = Color.RED;
  public static void
  serializeStaticState(ObjectOutputStream os)
  throws IOException { os.writeObject(color); }
  public static void
  deserializeStaticState(ObjectInputStream os)
  throws IOException, ClassNotFoundException {
    color = (Color)os.readObject();
  }
  Line(int xVal, int yVal, int dim) {
    super(xVal, yVal, dim);
  }
  public void setColor(Color newColor) {
    color = newColor;
  }
  public Color getColor() { return color; }
}

public class AStoreCADState {
  public static void main(String[] args) {
    List<Class<? extends Shape>> shapeTypes =
      Arrays.asList(
        Circle.class, Square.class, Line.class);
    List<Shape> shapes = IntStream.range(0, 10)
      .mapToObj(i -> Shape.randomFactory())
      .collect(Collectors.toList());
    // Set all the static colors to GREEN:
    shapes.forEach(s -> s.setColor(Color.GREEN));
    // Save the state vector:
    try(
      ObjectOutputStream out =
        new ObjectOutputStream(
          new FileOutputStream("CADState.dat"))
    ) {
      out.writeObject(shapeTypes);
      Line.serializeStaticState(out);
      out.writeObject(shapes);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    // Display the shapes:
    System.out.println(shapes);
  }
}
/* Output:
[class Circlecolor[GREEN] xPos[58] yPos[55] dim[93]
, class Squarecolor[GREEN] xPos[61] yPos[61] dim[29]
, class Linecolor[GREEN] xPos[68] yPos[0] dim[22]
, class Circlecolor[GREEN] xPos[7] yPos[88] dim[28]
, class Squarecolor[GREEN] xPos[51] yPos[89] dim[9]
, class Linecolor[GREEN] xPos[78] yPos[98] dim[61]
, class Circlecolor[GREEN] xPos[20] yPos[58] dim[16]
, class Squarecolor[GREEN] xPos[40] yPos[11] dim[22]
, class Linecolor[GREEN] xPos[4] yPos[83] dim[6]
, class Circlecolor[GREEN] xPos[75] yPos[10] dim[42]
]
*/
