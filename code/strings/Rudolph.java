// strings/Rudolph.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class Rudolph {
  public static void main(String[] args) {
    for(String pattern : new String[]{
      "Rudolph",
      "[rR]udolph",
      "[rR][aeiou][a-z]ol.*",
      "R.*" })
      System.out.println("Rudolph".matches(pattern));
  }
}
/* Output:
true
true
true
true
*/
