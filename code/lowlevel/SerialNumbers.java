// lowlevel/SerialNumbers.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class SerialNumbers {
  private volatile int serialNumber = 0;
  public int nextSerialNumber() {
    return serialNumber++; // Not thread-safe
  }
}
