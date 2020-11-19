// lowlevel/SettingDefaultHandler.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;

public class SettingDefaultHandler {
  public static void main(String[] args) {
    Thread.setDefaultUncaughtExceptionHandler(
      new MyUncaughtExceptionHandler());
    ExecutorService es =
      Executors.newCachedThreadPool();
    es.execute(new ExceptionThread());
    es.shutdown();
  }
}
/* Output:
caught java.lang.RuntimeException
*/
