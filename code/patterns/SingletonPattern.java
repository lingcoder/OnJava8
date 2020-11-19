// patterns/SingletonPattern.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

interface Resource {
  int getValue();
  void setValue(int x);
}

// This isn't inherited from a Cloneable
// base class and cloneability isn't added so
// making it final prevents cloneability from
// being added through inheritance. This also
// implements thread-safe lazy initialization:

final class Singleton {
  private static final class
  ResourceImpl implements Resource {
    private int i;
    private ResourceImpl(int i) {
      this.i = i;
    }
    public synchronized int getValue() {
      return i;
    }
    public synchronized void setValue(int x) {
      i = x;
    }
  }
  private static class ResourceHolder {
    private static Resource resource =
      new ResourceImpl(47);
  }
  public static Resource getResource() {
    return ResourceHolder.resource;
  }
}

public class SingletonPattern {
  public static void main(String[] args) {
    Resource r = Singleton.getResource();
    System.out.println(r.getValue());
    Resource s2 = Singleton.getResource();
    s2.setValue(9);
    System.out.println(r.getValue());
    try {
      // Can't do this: compile-time error.
      // Singleton s3 = (Singleton)s2.clone();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }
}
/* Output:
47
9
*/
