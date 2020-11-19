// interfaces/AbstractWithoutAbstracts.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

abstract class Basic3 {
  int f() { return 111; }
  // No abstract methods
}

public class AbstractWithoutAbstracts {
  // Basic3 b3 = new Basic3();
  // error: Basic3 is abstract; cannot be instantiated
}
