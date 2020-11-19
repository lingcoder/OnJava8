// operators/Literals.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class Literals {
  public static void main(String[] args) {
    int i1 = 0x2f; // Hexadecimal (lowercase)
    System.out.println(
      "i1: " + Integer.toBinaryString(i1));
    int i2 = 0X2F; // Hexadecimal (uppercase)
    System.out.println(
      "i2: " + Integer.toBinaryString(i2));
    int i3 = 0177; // Octal (leading zero)
    System.out.println(
      "i3: " + Integer.toBinaryString(i3));
    char c = 0xffff; // max char hex value
    System.out.println(
      "c: " + Integer.toBinaryString(c));
    byte b = 0x7f; // max byte hex value 10101111;
    System.out.println(
      "b: " + Integer.toBinaryString(b));
    short s = 0x7fff; // max short hex value
    System.out.println(
      "s: " + Integer.toBinaryString(s));
    long n1 = 200L; // long suffix
    long n2 = 200l; // long suffix (can be confusing)
    long n3 = 200;
    // Java 7 Binary Literals:
    byte blb = (byte)0b00110101;
    System.out.println(
      "blb: " + Integer.toBinaryString(blb));
    short bls = (short)0B0010111110101111;
    System.out.println(
      "bls: " + Integer.toBinaryString(bls));
    int bli = 0b00101111101011111010111110101111;
    System.out.println(
      "bli: " + Integer.toBinaryString(bli));
    long bll = 0b00101111101011111010111110101111;
    System.out.println(
      "bll: " + Long.toBinaryString(bll));
    float f1 = 1;
    float f2 = 1F; // float suffix
    float f3 = 1f; // float suffix
    double d1 = 1d; // double suffix
    double d2 = 1D; // double suffix
    // (Hex and Octal also work with long)
  }
}
/* Output:
i1: 101111
i2: 101111
i3: 1111111
c: 1111111111111111
b: 1111111
s: 111111111111111
blb: 110101
bls: 10111110101111
bli: 101111101011111010111110101111
bll: 101111101011111010111110101111
*/
