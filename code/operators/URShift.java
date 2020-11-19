// operators/URShift.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Test of unsigned right shift

public class URShift {
  public static void main(String[] args) {
    int i = -1;
    System.out.println(Integer.toBinaryString(i));
    i >>>= 10;
    System.out.println(Integer.toBinaryString(i));
    long l = -1;
    System.out.println(Long.toBinaryString(l));
    l >>>= 10;
    System.out.println(Long.toBinaryString(l));
    short s = -1;
    System.out.println(Integer.toBinaryString(s));
    s >>>= 10;
    System.out.println(Integer.toBinaryString(s));
    byte b = -1;
    System.out.println(Integer.toBinaryString(b));
    b >>>= 10;
    System.out.println(Integer.toBinaryString(b));
    b = -1;
    System.out.println(Integer.toBinaryString(b));
    System.out.println(Integer.toBinaryString(b>>>10));
  }
}
/* Output:
11111111111111111111111111111111
1111111111111111111111
1111111111111111111111111111111111111111111111111111111
111111111
111111111111111111111111111111111111111111111111111111
11111111111111111111111111111111
11111111111111111111111111111111
11111111111111111111111111111111
11111111111111111111111111111111
11111111111111111111111111111111
1111111111111111111111
*/
