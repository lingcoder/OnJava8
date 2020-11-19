// validating/Inverter4.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package validating;
import static java.lang.Character.*;

public class Inverter4 implements StringInverter {
  static final String ALLOWED =
    "abcdefghijklmnopqrstuvwxyz ,." +
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public String invert(String str) {
    if(str.length() > 30)
      throw new RuntimeException("argument too long!");
    String result = "";
    for(int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if(ALLOWED.indexOf(c) == -1)
        throw new RuntimeException(c + " Not allowed");
      result += isUpperCase(c) ?
                toLowerCase(c) :
                toUpperCase(c);
    }
    return result;
  }
}
