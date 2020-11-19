// functional/FunctionWithWrapped.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.function.*;

public class FunctionWithWrapped {
  public static void main(String[] args) {
    Function<Integer, Double> fid = i -> (double)i;
    IntToDoubleFunction fid2 = i -> i;
  }
}
