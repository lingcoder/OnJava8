// collectiontopics/References.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Demonstrates Reference objects
import java.lang.ref.*;
import java.util.*;

class VeryBig {
  private static final int SIZE = 10000;
  private long[] la = new long[SIZE];
  private String ident;
  VeryBig(String id) { ident = id; }
  @Override
  public String toString() { return ident; }
  @SuppressWarnings("deprecation")
  @Override
  protected void finalize() {
    System.out.println("Finalizing " + ident);
  }
}

public class References {
  private static ReferenceQueue<VeryBig> rq =
    new ReferenceQueue<>();
  public static void checkQueue() {
    Reference<? extends VeryBig> inq = rq.poll();
    if(inq != null)
      System.out.println("In queue: " + inq.get());
  }
  public static void main(String[] args) {
    int size = 10;
    // Or, choose size via the command line:
    if(args.length > 0)
      size = Integer.valueOf(args[0]);
    LinkedList<SoftReference<VeryBig>> sa =
      new LinkedList<>();
    for(int i = 0; i < size; i++) {
      sa.add(new SoftReference<>(
        new VeryBig("Soft " + i), rq));
      System.out.println(
        "Just created: " + sa.getLast());
      checkQueue();
    }
    LinkedList<WeakReference<VeryBig>> wa =
      new LinkedList<>();
    for(int i = 0; i < size; i++) {
      wa.add(new WeakReference<>(
        new VeryBig("Weak " + i), rq));
      System.out.println(
        "Just created: " + wa.getLast());
      checkQueue();
    }
    SoftReference<VeryBig> s =
      new SoftReference<>(new VeryBig("Soft"));
    WeakReference<VeryBig> w =
      new WeakReference<>(new VeryBig("Weak"));
    System.gc();
    LinkedList<PhantomReference<VeryBig>> pa =
      new LinkedList<>();
    for(int i = 0; i < size; i++) {
      pa.add(new PhantomReference<>(
        new VeryBig("Phantom " + i), rq));
      System.out.println(
        "Just created: " + pa.getLast());
      checkQueue();
    }
  }
}
/* Output: (First and Last 10 Lines)
Just created: java.lang.ref.SoftReference@15db9742
Just created: java.lang.ref.SoftReference@6d06d69c
Just created: java.lang.ref.SoftReference@7852e922
Just created: java.lang.ref.SoftReference@4e25154f
Just created: java.lang.ref.SoftReference@70dea4e
Just created: java.lang.ref.SoftReference@5c647e05
Just created: java.lang.ref.SoftReference@33909752
Just created: java.lang.ref.SoftReference@55f96302
Just created: java.lang.ref.SoftReference@3d4eac69
Just created: java.lang.ref.SoftReference@42a57993
...________...________...________...________...
Just created: java.lang.ref.PhantomReference@45ee12a7
In queue: null
Just created: java.lang.ref.PhantomReference@330bedb4
In queue: null
Just created: java.lang.ref.PhantomReference@2503dbd3
In queue: null
Just created: java.lang.ref.PhantomReference@4b67cf4d
In queue: null
Just created: java.lang.ref.PhantomReference@7ea987ac
In queue: null
*/
