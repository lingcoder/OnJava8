// innerclasses/ClassInInterface.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java ClassInInterface$Test}

public interface ClassInInterface {
  void howdy();
  class Test implements ClassInInterface {
    @Override
    public void howdy() {
      System.out.println("Howdy!");
    }
    public static void main(String[] args) {
      new Test().howdy();
    }
  }
}
/* Output:
Howdy!
*/
