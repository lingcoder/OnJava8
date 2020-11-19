// exceptions/TryAnything.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {WillNotCompile}

class Anything {}

public class TryAnything {
  public static void main(String[] args) {
    try(
      Anything a = new Anything()
    ) {
    }
  }
}
