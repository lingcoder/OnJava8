// generics/CaptureConversion.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class CaptureConversion {
  static <T> void f1(Holder<T> holder) {
    T t = holder.get();
    System.out.println(t.getClass().getSimpleName());
  }
  static void f2(Holder<?> holder) {
    f1(holder); // Call with captured type
  }
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    Holder raw = new Holder<>(1);

    f1(raw);
    // warning: [unchecked] unchecked method invocation:
    // method f1 in class CaptureConversion
    // is applied to given types
    //     f1(raw);
    //       ^
    //   required: Holder<T>
    //   found: Holder
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>f1(Holder<T>)
    // warning: [unchecked] unchecked conversion
    //     f1(raw);
    //        ^
    //   required: Holder<T>
    //   found:    Holder
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>f1(Holder<T>)
    // 2 warnings

    f2(raw); // No warnings
    Holder rawBasic = new Holder();

    rawBasic.set(new Object());
    // warning: [unchecked] unchecked call to set(T)
    // as a member of the raw type Holder
    //     rawBasic.set(new Object());
    //                 ^
    //   where T is a type-variable:
    //     T extends Object declared in class Holder
    // 1 warning

    f2(rawBasic); // No warnings
    // Upcast to Holder<?>, still figures it out:
    Holder<?> wildcarded = new Holder<>(1.0);
    f2(wildcarded);
  }
}
/* Output:
Integer
Integer
Object
Double
*/
