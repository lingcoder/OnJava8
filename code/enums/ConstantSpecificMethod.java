// enums/ConstantSpecificMethod.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.text.*;

public enum ConstantSpecificMethod {
  DATE_TIME {
    @Override
    String getInfo() {
      return
        DateFormat.getDateInstance()
          .format(new Date());
    }
  },
  CLASSPATH {
    @Override
    String getInfo() {
      return System.getenv("CLASSPATH");
    }
  },
  VERSION {
    @Override
    String getInfo() {
      return System.getProperty("java.version");
    }
  };
  abstract String getInfo();
  public static void main(String[] args) {
    for(ConstantSpecificMethod csm : values())
      System.out.println(csm.getInfo());
  }
}
/* Output:
May 9, 2017
C:\Users\Bruce\Documents\GitHub\on-
java\ExtractedExamples\\gradle\wrapper\gradle-
wrapper.jar
1.8.0_112
*/
