// exceptions/LostMessage.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// How an exception can be lost

class VeryImportantException extends Exception {
  @Override
  public String toString() {
    return "A very important exception!";
  }
}

class HoHumException extends Exception {
  @Override
  public String toString() {
    return "A trivial exception";
  }
}

public class LostMessage {
  void f() throws VeryImportantException {
    throw new VeryImportantException();
  }
  void dispose() throws HoHumException {
    throw new HoHumException();
  }
  public static void main(String[] args) {
    try {
      LostMessage lm = new LostMessage();
      try {
        lm.f();
      } finally {
        lm.dispose();
      }
    } catch(VeryImportantException |
            HoHumException e) {
      System.out.println(e);
    }
  }
}
/* Output:
A trivial exception
*/
