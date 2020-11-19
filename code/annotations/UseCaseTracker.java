// annotations/UseCaseTracker.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.lang.reflect.*;

public class UseCaseTracker {
  public static void
  trackUseCases(List<Integer> useCases, Class<?> cl) {
    for(Method m : cl.getDeclaredMethods()) {
      UseCase uc = m.getAnnotation(UseCase.class);
      if(uc != null) {
        System.out.println("Found Use Case " +
          uc.id() + "\n  " + uc.description());
        useCases.remove(Integer.valueOf(uc.id()));
      }
    }
    useCases.forEach(i ->
      System.out.println("Missing use case " + i));
  }
  public static void main(String[] args) {
    List<Integer> useCases = IntStream.range(47, 51)
      .boxed().collect(Collectors.toList());
    trackUseCases(useCases, PasswordUtils.class);
  }
}
/* Output:
Found Use Case 48
  no description
Found Use Case 47
  Passwords must contain at least one numeric
Found Use Case 49
  New passwords can't equal previously used ones
Missing use case 50
*/
