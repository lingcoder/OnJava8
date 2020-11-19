// lowlevel/SynchronizedSerialNumbers.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class
SynchronizedSerialNumbers extends SerialNumbers {
  private int serialNumber = 0;
  public synchronized int nextSerialNumber() {
    return serialNumber++;
  }
  public static void main(String[] args) {
    SerialNumberChecker.test(
      new SynchronizedSerialNumbers());
  }
}
/* Output:
No duplicates detected
*/
