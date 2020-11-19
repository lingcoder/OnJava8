// validating/LoaderAssertions.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using the class loader to enable assertions
// {ThrowsException}

public class LoaderAssertions {
  public static void main(String[] args) {
    ClassLoader.getSystemClassLoader()
      .setDefaultAssertionStatus(true);
    new Loaded().go();
  }
}

class Loaded {
  public void go() {
    assert false: "Loaded.go()";
  }
}
/* Output:
___[ Error Output ]___
Exception in thread "main" java.lang.AssertionError:
Loaded.go()
        at Loaded.go(LoaderAssertions.java:15)
        at
LoaderAssertions.main(LoaderAssertions.java:9)
*/
