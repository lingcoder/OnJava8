// equalshashcode/CountedString.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Creating a good hashCode()
import java.util.*;

public class CountedString {
  private static List<String> created =
    new ArrayList<>();
  private String s;
  private int id = 0;
  public CountedString(String str) {
    s = str;
    created.add(s);
    // id is the total number of instances
    // of this String used by CountedString:
    for(String s2 : created)
      if(s2.equals(s))
        id++;
  }
  @Override
  public String toString() {
    return "String: " + s + " id: " + id +
      " hashCode(): " + hashCode();
  }
  @Override
  public int hashCode() {
    // The very simple approach:
    // return s.hashCode() * id;
    // Using Joshua Bloch's recipe:
    int result = 17;
    result = 37 * result + s.hashCode();
    result = 37 * result + id;
    return result;
  }
  @Override
  public boolean equals(Object o) {
    return o instanceof CountedString &&
      Objects.equals(s, ((CountedString)o).s) &&
      Objects.equals(id, ((CountedString)o).id);
  }
  public static void main(String[] args) {
    Map<CountedString,Integer> map = new HashMap<>();
    CountedString[] cs = new CountedString[5];
    for(int i = 0; i < cs.length; i++) {
      cs[i] = new CountedString("hi");
      map.put(cs[i], i); // Autobox int to Integer
    }
    System.out.println(map);
    for(CountedString cstring : cs) {
      System.out.println("Looking up " + cstring);
      System.out.println(map.get(cstring));
    }
  }
}
/* Output:
{String: hi id: 4 hashCode(): 146450=3, String: hi id:
5 hashCode(): 146451=4, String: hi id: 2 hashCode():
146448=1, String: hi id: 3 hashCode(): 146449=2,
String: hi id: 1 hashCode(): 146447=0}
Looking up String: hi id: 1 hashCode(): 146447
0
Looking up String: hi id: 2 hashCode(): 146448
1
Looking up String: hi id: 3 hashCode(): 146449
2
Looking up String: hi id: 4 hashCode(): 146450
3
Looking up String: hi id: 5 hashCode(): 146451
4
*/
