// typeinfo/SimpleProxyDemo.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

interface Interface {
  void doSomething();
  void somethingElse(String arg);
}

class RealObject implements Interface {
  @Override
  public void doSomething() {
    System.out.println("doSomething");
  }
  @Override
  public void somethingElse(String arg) {
    System.out.println("somethingElse " + arg);
  }
}

class SimpleProxy implements Interface {
  private Interface proxied;
  SimpleProxy(Interface proxied) {
    this.proxied = proxied;
  }
  @Override
  public void doSomething() {
    System.out.println("SimpleProxy doSomething");
    proxied.doSomething();
  }
  @Override
  public void somethingElse(String arg) {
    System.out.println(
      "SimpleProxy somethingElse " + arg);
    proxied.somethingElse(arg);
  }
}

class SimpleProxyDemo {
  public static void consumer(Interface iface) {
    iface.doSomething();
    iface.somethingElse("bonobo");
  }
  public static void main(String[] args) {
    consumer(new RealObject());
    consumer(new SimpleProxy(new RealObject()));
  }
}
/* Output:
doSomething
somethingElse bonobo
SimpleProxy doSomething
doSomething
SimpleProxy somethingElse bonobo
somethingElse bonobo
*/
