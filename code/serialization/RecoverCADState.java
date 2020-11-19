// serialization/RecoverCADState.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Restoring the state of the fictitious CAD system
// {RunFirst: AStoreCADState}
import java.io.*;
import java.util.*;

public class RecoverCADState {
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    try(
      ObjectInputStream in =
        new ObjectInputStream(
          new FileInputStream("CADState.dat"))
    ) {
      // Read in the same order they were written:
      List<Class<? extends Shape>> shapeTypes =
        (List<Class<? extends Shape>>)in.readObject();
      Line.deserializeStaticState(in);
      List<Shape> shapes =
        (List<Shape>)in.readObject();
      System.out.println(shapes);
    } catch(IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
/* Output:
[class Circlecolor[RED] xPos[58] yPos[55] dim[93]
, class Squarecolor[RED] xPos[61] yPos[61] dim[29]
, class Linecolor[GREEN] xPos[68] yPos[0] dim[22]
, class Circlecolor[RED] xPos[7] yPos[88] dim[28]
, class Squarecolor[RED] xPos[51] yPos[89] dim[9]
, class Linecolor[GREEN] xPos[78] yPos[98] dim[61]
, class Circlecolor[RED] xPos[20] yPos[58] dim[16]
, class Squarecolor[RED] xPos[40] yPos[11] dim[22]
, class Linecolor[GREEN] xPos[4] yPos[83] dim[6]
, class Circlecolor[RED] xPos[75] yPos[10] dim[42]
]
*/
