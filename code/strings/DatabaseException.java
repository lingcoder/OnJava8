// strings/DatabaseException.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class DatabaseException extends Exception {
  public DatabaseException(int transactionID,
    int queryID, String message) {
    super(String.format("(t%d, q%d) %s", transactionID,
        queryID, message));
  }
  public static void main(String[] args) {
    try {
      throw new DatabaseException(3, 7, "Write failed");
    } catch(Exception e) {
      System.out.println(e);
    }
  }
}
/* Output:
DatabaseException: (t3, q7) Write failed
*/
