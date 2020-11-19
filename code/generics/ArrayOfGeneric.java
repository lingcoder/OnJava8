// generics/ArrayOfGeneric.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class ArrayOfGeneric {
  static final int SIZE = 100;
  static Generic<Integer>[] gia;
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    try {
      gia = (Generic<Integer>[])new Object[SIZE];
    } catch(ClassCastException e) {
      System.out.println(e.getMessage());
    }
    // Runtime type is the raw (erased) type:
    gia = (Generic<Integer>[])new Generic[SIZE];
    System.out.println(gia.getClass().getSimpleName());
    gia[0] = new Generic<>();
    //- gia[1] = new Object(); // Compile-time error
    // Discovers type mismatch at compile time:
    //- gia[2] = new Generic<Double>();
  }
}
/* Output:
[Ljava.lang.Object; cannot be cast to [LGeneric;
Generic[]
*/
