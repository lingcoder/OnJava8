// validating/NonNullConstruction.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import static com.google.common.base.Preconditions.*;

public class NonNullConstruction {
  private Integer n;
  private String s;
  NonNullConstruction(Integer n, String s) {
    this.n = checkNotNull(n);
    this.s = checkNotNull(s);
  }
  public static void main(String[] args) {
    NonNullConstruction nnc =
      new NonNullConstruction(3, "Trousers");
  }
}
