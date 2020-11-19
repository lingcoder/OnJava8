// operators/RoundingNumbers.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Rounding floats and doubles

public class RoundingNumbers {
  public static void main(String[] args) {
    double above = 0.7, below = 0.4;
    float fabove = 0.7f, fbelow = 0.4f;
    System.out.println(
      "Math.round(above): " + Math.round(above));
    System.out.println(
      "Math.round(below): " + Math.round(below));
    System.out.println(
      "Math.round(fabove): " + Math.round(fabove));
    System.out.println(
      "Math.round(fbelow): " + Math.round(fbelow));
  }
}
/* Output:
Math.round(above): 1
Math.round(below): 0
Math.round(fabove): 1
Math.round(fbelow): 0
*/
