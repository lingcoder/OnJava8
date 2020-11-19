// generics/DynamicProxyMixin.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.lang.reflect.*;
import java.util.*;
import onjava.*;
import static onjava.Tuple.*;

class MixinProxy implements InvocationHandler {
  Map<String, Object> delegatesByMethod;
  @SuppressWarnings("unchecked")
  MixinProxy(Tuple2<Object, Class<?>>... pairs) {
    delegatesByMethod = new HashMap<>();
    for(Tuple2<Object, Class<?>> pair : pairs) {
      for(Method method : pair.a2.getMethods()) {
        String methodName = method.getName();
        // The first interface in the map
        // implements the method.
        if(!delegatesByMethod.containsKey(methodName))
          delegatesByMethod.put(methodName, pair.a1);
      }
    }
  }
  @Override
  public Object invoke(Object proxy, Method method,
    Object[] args) throws Throwable {
    String methodName = method.getName();
    Object delegate = delegatesByMethod.get(methodName);
    return method.invoke(delegate, args);
  }
  @SuppressWarnings("unchecked")
  public static Object newInstance(Tuple2... pairs) {
    Class[] interfaces = new Class[pairs.length];
    for(int i = 0; i < pairs.length; i++) {
      interfaces[i] = (Class)pairs[i].a2;
    }
    ClassLoader cl =
      pairs[0].a1.getClass().getClassLoader();
    return Proxy.newProxyInstance(
      cl, interfaces, new MixinProxy(pairs));
  }
}

public class DynamicProxyMixin {
  public static void main(String[] args) {
    @SuppressWarnings("unchecked")
    Object mixin = MixinProxy.newInstance(
      tuple(new BasicImp(), Basic.class),
      tuple(new TimeStampedImp(), TimeStamped.class),
      tuple(new SerialNumberedImp(),
          SerialNumbered.class));
    Basic b = (Basic)mixin;
    TimeStamped t = (TimeStamped)mixin;
    SerialNumbered s = (SerialNumbered)mixin;
    b.set("Hello");
    System.out.println(b.get());
    System.out.println(t.getStamp());
    System.out.println(s.getSerialNumber());
  }
}
/* Output:
Hello
1494331653339
1
*/
