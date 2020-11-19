// lowlevel/SyncOnObject.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Synchronizing on another object
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import onjava.Nap;

class DualSynch {
  ConcurrentLinkedQueue<String> trace =
    new ConcurrentLinkedQueue<>();
  public synchronized void f(boolean nap) {
    for(int i = 0; i < 5; i++) {
      trace.add(String.format("f() " + i));
      if(nap) new Nap(0.01);
    }
  }
  private Object syncObject = new Object();
  public void g(boolean nap) {
    synchronized(syncObject) {
      for(int i = 0; i < 5; i++) {
        trace.add(String.format("g() " + i));
        if(nap) new Nap(0.01);
      }
    }
  }
}

public class SyncOnObject {
  static void test(boolean fNap, boolean gNap) {
    DualSynch ds = new DualSynch();
    List<CompletableFuture<Void>> cfs =
      Arrays.stream(new Runnable[] {
        () -> ds.f(fNap), () -> ds.g(gNap) })
        .map(CompletableFuture::runAsync)
        .collect(Collectors.toList());
    cfs.forEach(CompletableFuture::join);
    ds.trace.forEach(System.out::println);
  }
  public static void main(String[] args) {
    test(true, false);
    System.out.println("****");
    test(false, true);
  }
}
/* Output:
f() 0
g() 0
g() 1
g() 2
g() 3
g() 4
f() 1
f() 2
f() 3
f() 4
****
f() 0
g() 0
f() 1
f() 2
f() 3
f() 4
g() 1
g() 2
g() 3
g() 4
*/
