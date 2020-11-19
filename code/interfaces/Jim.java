// interfaces/Jim.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

interface Jim1 {
  default void jim() {
    System.out.println("Jim1::jim");
  }
}

interface Jim2 {
  default void jim() {
    System.out.println("Jim2::jim");
  }
}

public class Jim implements Jim1, Jim2 {
  @Override
  public void jim() { Jim2.super.jim(); }
  public static void main(String[] args) {
    new Jim().jim();
  }
}
/* Output:
Jim2::jim
*/
