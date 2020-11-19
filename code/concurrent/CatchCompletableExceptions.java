// concurrent/CatchCompletableExceptions.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.concurrent.*;

public class CatchCompletableExceptions {
  static void handleException(int failcount) {
    // Call the Function only if there's an
    // exception, must produce same type as came in:
    CompletableExceptions
      .test("exceptionally", failcount)
      .exceptionally((ex) -> { // Function
        if(ex == null)
          System.out.println("I don't get it yet");
        return new Breakable(ex.getMessage(), 0);
      })
      .thenAccept(str ->
        System.out.println("result: " + str));

    // Create a new result (recover):
    CompletableExceptions
      .test("handle", failcount)
      .handle((result, fail) -> { // BiFunction
        if(fail != null)
          return "Failure recovery object";
        else
          return result + " is good";
      })
      .thenAccept(str ->
        System.out.println("result: " + str));

    // Do something but pass the same result through:
    CompletableExceptions
      .test("whenComplete", failcount)
      .whenComplete((result, fail) -> { // BiConsumer
        if(fail != null)
          System.out.println("It failed");
        else
          System.out.println(result + " OK");
      })
      .thenAccept(r ->
        System.out.println("result: " + r));
  }
  public static void main(String[] args) {
    System.out.println("**** Failure Mode ****");
    handleException(2);
    System.out.println("**** Success Mode ****");
    handleException(0);
  }
}
/* Output:
**** Failure Mode ****
Breakable_exceptionally [1]
Throwing Exception for exceptionally
result: Breakable_java.lang.RuntimeException:
Breakable_exceptionally failed [0]
Breakable_handle [1]
Throwing Exception for handle
result: Failure recovery object
Breakable_whenComplete [1]
Throwing Exception for whenComplete
It failed
**** Success Mode ****
Breakable_exceptionally [-1]
Breakable_exceptionally [-2]
Breakable_exceptionally [-3]
Breakable_exceptionally [-4]
result: Breakable_exceptionally [-4]
Breakable_handle [-1]
Breakable_handle [-2]
Breakable_handle [-3]
Breakable_handle [-4]
result: Breakable_handle [-4] is good
Breakable_whenComplete [-1]
Breakable_whenComplete [-2]
Breakable_whenComplete [-3]
Breakable_whenComplete [-4]
Breakable_whenComplete [-4] OK
result: Breakable_whenComplete [-4]
*/
