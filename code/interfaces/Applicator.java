// interfaces/Applicator.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

class Processor {
  public String name() {
    return getClass().getSimpleName();
  }
  public Object process(Object input) {
    return input;
  }
}

class Upcase extends Processor {
  @Override // Covariant return:
  public String process(Object input) {
    return ((String)input).toUpperCase();
  }
}

class Downcase extends Processor {
  @Override
  public String process(Object input) {
    return ((String)input).toLowerCase();
  }
}

class Splitter extends Processor {
  @Override
  public String process(Object input) {
    // split() divides a String into pieces:
    return Arrays.toString(((String)input).split(" "));
  }
}

public class Applicator {
  public static void apply(Processor p, Object s) {
    System.out.println("Using Processor " + p.name());
    System.out.println(p.process(s));
  }
  public static void main(String[] args) {
    String s =
      "We are such stuff as dreams are made on";
    apply(new Upcase(), s);
    apply(new Downcase(), s);
    apply(new Splitter(), s);
  }
}
/* Output:
Using Processor Upcase
WE ARE SUCH STUFF AS DREAMS ARE MADE ON
Using Processor Downcase
we are such stuff as dreams are made on
Using Processor Splitter
[We, are, such, stuff, as, dreams, are, made, on]
*/
