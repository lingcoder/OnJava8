// lowlevel/CaptureUncaughtException.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;

class ExceptionThread2 implements Runnable {
  @Override
  public void run() {
    Thread t = Thread.currentThread();
    System.out.println("run() by " + t.getName());
    System.out.println(
      "eh = " + t.getUncaughtExceptionHandler());
    throw new RuntimeException();
  }
}

class MyUncaughtExceptionHandler implements
Thread.UncaughtExceptionHandler {
  @Override
  public void uncaughtException(Thread t, Throwable e) {
    System.out.println("caught " + e);
  }
}

class HandlerThreadFactory implements ThreadFactory {
  @Override
  public Thread newThread(Runnable r) {
    System.out.println(this + " creating new Thread");
    Thread t = new Thread(r);
    System.out.println("created " + t);
    t.setUncaughtExceptionHandler(
      new MyUncaughtExceptionHandler());
    System.out.println(
      "eh = " + t.getUncaughtExceptionHandler());
    return t;
  }
}

public class CaptureUncaughtException {
  public static void main(String[] args) {
    ExecutorService exec =
      Executors.newCachedThreadPool(
        new HandlerThreadFactory());
    exec.execute(new ExceptionThread2());
    exec.shutdown();
  }
}
/* Output:
HandlerThreadFactory@4e25154f creating new Thread
created Thread[Thread-0,5,main]
eh = MyUncaughtExceptionHandler@70dea4e
run() by Thread-0
eh = MyUncaughtExceptionHandler@70dea4e
caught java.lang.RuntimeException
*/
