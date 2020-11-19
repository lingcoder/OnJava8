// validating/Inverter3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package validating;
import static java.lang.Character.*;

public class Inverter3 implements StringInverter {
  public String invert(String str) {
    if(str.length() > 30)
      throw new RuntimeException("argument too long!");
    String result = "";
    for(int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      result += isUpperCase(c) ?
                toLowerCase(c) :
                toUpperCase(c);
    }
    return result;
  }
}
