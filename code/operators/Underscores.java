// operators/Underscores.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class Underscores {
  public static void main(String[] args) {
    double d = 341_435_936.445_667;
    System.out.println(d);
    int bin = 0b0010_1111_1010_1111_1010_1111_1010_1111;
    System.out.println(Integer.toBinaryString(bin));
    System.out.printf("%x%n", bin);  // [1]
    long hex = 0x7f_e9_b7_aa;
    System.out.printf("%x%n", hex);
  }
}
/* Output:
3.41435936445667E8
101111101011111010111110101111
2fafafaf
7fe9b7aa
*/
