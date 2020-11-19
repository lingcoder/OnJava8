// validating/Assert2.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Assert with an information-expression
// {java Assert2 -ea}
// {ThrowsException}

public class Assert2 {
  public static void main(String[] args) {
    assert false:
      "Here's a message saying what happened";
  }
}
/* Output:
___[ Error Output ]___
Exception in thread "main" java.lang.AssertionError:
Here's a message saying what happened
        at Assert2.main(Assert2.java:8)
*/
