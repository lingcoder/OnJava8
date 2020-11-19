// typeinfo/SelectingMethods.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Looking for particular methods in a dynamic proxy
import java.lang.reflect.*;

class MethodSelector implements InvocationHandler {
  private Object proxied;
  MethodSelector(Object proxied) {
    this.proxied = proxied;
  }
  @Override
  public Object
  invoke(Object proxy, Method method, Object[] args)
  throws Throwable {
    if(method.getName().equals("interesting"))
      System.out.println(
        "Proxy detected the interesting method");
    return method.invoke(proxied, args);
  }
}

interface SomeMethods {
  void boring1();
  void boring2();
  void interesting(String arg);
  void boring3();
}

class Implementation implements SomeMethods {
  @Override
  public void boring1() {
    System.out.println("boring1");
  }
  @Override
  public void boring2() {
    System.out.println("boring2");
  }
  @Override
  public void interesting(String arg) {
    System.out.println("interesting " + arg);
  }
  @Override
  public void boring3() {
    System.out.println("boring3");
  }
}

class SelectingMethods {
  public static void main(String[] args) {
    SomeMethods proxy =
      (SomeMethods)Proxy.newProxyInstance(
        SomeMethods.class.getClassLoader(),
        new Class[]{ SomeMethods.class },
        new MethodSelector(new Implementation()));
    proxy.boring1();
    proxy.boring2();
    proxy.interesting("bonobo");
    proxy.boring3();
  }
}
/* Output:
boring1
boring2
Proxy detected the interesting method
interesting bonobo
boring3
*/
