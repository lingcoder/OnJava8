// generics/Mixins.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

interface TimeStamped { long getStamp(); }

class TimeStampedImp implements TimeStamped {
  private final long timeStamp;
  TimeStampedImp() {
    timeStamp = new Date().getTime();
  }
  @Override
  public long getStamp() { return timeStamp; }
}

interface SerialNumbered { long getSerialNumber(); }

class SerialNumberedImp implements SerialNumbered {
  private static long counter = 1;
  private final long serialNumber = counter++;
  @Override
  public long getSerialNumber() { return serialNumber; }
}

interface Basic {
  void set(String val);
  String get();
}

class BasicImp implements Basic {
  private String value;
  @Override
  public void set(String val) { value = val; }
  @Override
  public String get() { return value; }
}

class Mixin extends BasicImp
implements TimeStamped, SerialNumbered {
  private TimeStamped timeStamp = new TimeStampedImp();
  private SerialNumbered serialNumber =
    new SerialNumberedImp();
  @Override
  public long getStamp() {
    return timeStamp.getStamp();
  }
  @Override
  public long getSerialNumber() {
    return serialNumber.getSerialNumber();
  }
}

public class Mixins {
  public static void main(String[] args) {
    Mixin mixin1 = new Mixin(), mixin2 = new Mixin();
    mixin1.set("test string 1");
    mixin2.set("test string 2");
    System.out.println(mixin1.get() + " " +
      mixin1.getStamp() +  " " +
      mixin1.getSerialNumber());
    System.out.println(mixin2.get() + " " +
      mixin2.getStamp() +  " " +
      mixin2.getSerialNumber());
  }
}
/* Output:
test string 1 1494331663026 1
test string 2 1494331663027 2
*/
