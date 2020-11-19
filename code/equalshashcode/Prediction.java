// equalshashcode/Prediction.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Predicting the weather
import java.util.*;

public class Prediction {
  private static Random rand = new Random(47);
  @Override
  public String toString() {
    return rand.nextBoolean() ?
      "Six more weeks of Winter!" : "Early Spring!";
  }
}
