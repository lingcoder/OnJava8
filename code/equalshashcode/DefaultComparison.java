// equalshashcode/DefaultComparison.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

class DefaultComparison {
  private int i, j, k;
  DefaultComparison(int i, int j, int k) {
    this.i = i;
    this.j = j;
    this.k = k;
  }
  public static void main(String[] args) {
    DefaultComparison
      a = new DefaultComparison(1, 2, 3),
      b = new DefaultComparison(1, 2, 3);
    System.out.println(a == a);
    System.out.println(a == b);
  }
}
/* Output:
true
false
*/
