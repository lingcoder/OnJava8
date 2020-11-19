// generics/Wildcards.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Exploring the meaning of wildcards

public class Wildcards {
   // Raw argument:
  static void rawArgs(Holder holder, Object arg) {
    //- holder.set(arg);
    // warning: [unchecked] unchecked call to set(T)
    // as a member of the raw type Holder
    //     holder.set(arg);
    //               ^
    //   where T is a type-variable:
    //     T extends Object declared in class Holder
    // 1 warning

    // Can't do this; don't have any 'T':
    // T t = holder.get();

    // OK, but type information is lost:
    Object obj = holder.get();
  }
  // Like rawArgs(), but errors instead of warnings:
  static void
  unboundedArg(Holder<?> holder, Object arg) {
    //- holder.set(arg);
    // error: method set in class Holder<T>
    // cannot be applied to given types;
    //     holder.set(arg);
    //           ^
    //   required: CAP#1
    //   found: Object
    //   reason: argument mismatch;
    //     Object cannot be converted to CAP#1
    //   where T is a type-variable:
    //     T extends Object declared in class Holder
    //   where CAP#1 is a fresh type-variable:
    //     CAP#1 extends Object from capture of ?
    // 1 error

    // Can't do this; don't have any 'T':
    // T t = holder.get();

    // OK, but type information is lost:
    Object obj = holder.get();
  }
  static <T> T exact1(Holder<T> holder) {
    return holder.get();
  }
  static <T> T exact2(Holder<T> holder, T arg) {
    holder.set(arg);
    return holder.get();
  }
  static <T>
  T wildSubtype(Holder<? extends T> holder, T arg) {
    //- holder.set(arg);
    // error: method set in class Holder<T#2>
    // cannot be applied to given types;
    //     holder.set(arg);
    //           ^
    //   required: CAP#1
    //   found: T#1
    //   reason: argument mismatch;
    //     T#1 cannot be converted to CAP#1
    //   where T#1,T#2 are type-variables:
    //     T#1 extends Object declared in method
    //     <T#1>wildSubtype(Holder<? extends T#1>,T#1)
    //     T#2 extends Object declared in class Holder
    //   where CAP#1 is a fresh type-variable:
    //     CAP#1 extends T#1 from
    //       capture of ? extends T#1
    // 1 error

    return holder.get();
  }
  static <T>
  void wildSupertype(Holder<? super T> holder, T arg) {
    holder.set(arg);
    //- T t = holder.get();
    // error: incompatible types:
    // CAP#1 cannot be converted to T
    //     T t = holder.get();
    //                     ^
    //   where T is a type-variable:
    //     T extends Object declared in method
    //       <T>wildSupertype(Holder<? super T>,T)
    //   where CAP#1 is a fresh type-variable:
    //     CAP#1 extends Object super:
    //       T from capture of ? super T
    // 1 error

    // OK, but type information is lost:
    Object obj = holder.get();
  }
  public static void main(String[] args) {
    Holder raw = new Holder<>();
    // Or:
    raw = new Holder();
    Holder<Long> qualified = new Holder<>();
    Holder<?> unbounded = new Holder<>();
    Holder<? extends Long> bounded = new Holder<>();
    Long lng = 1L;

    rawArgs(raw, lng);
    rawArgs(qualified, lng);
    rawArgs(unbounded, lng);
    rawArgs(bounded, lng);

    unboundedArg(raw, lng);
    unboundedArg(qualified, lng);
    unboundedArg(unbounded, lng);
    unboundedArg(bounded, lng);

    //- Object r1 = exact1(raw);
    // warning: [unchecked] unchecked method invocation:
    // method exact1 in class Wildcards is applied
    // to given types
    //      Object r1 = exact1(raw);
    //                        ^
    //   required: Holder<T>
    //   found: Holder
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>exact1(Holder<T>)
    // warning: [unchecked] unchecked conversion
    //      Object r1 = exact1(raw);
    //                         ^
    //   required: Holder<T>
    //   found:    Holder
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>exact1(Holder<T>)
    // 2 warnings

    Long r2 = exact1(qualified);
    Object r3 = exact1(unbounded); // Must return Object
    Long r4 = exact1(bounded);

    //- Long r5 = exact2(raw, lng);
    // warning: [unchecked] unchecked method invocation:
    // method exact2 in class Wildcards is
    // applied to given types
    //     Long r5 = exact2(raw, lng);
    //                     ^
    //   required: Holder<T>,T
    //   found: Holder,Long
    //   where T is a type-variable:
    //     T extends Object declared in
    //       method <T>exact2(Holder<T>,T)
    // warning: [unchecked] unchecked conversion
    //     Long r5 = exact2(raw, lng);
    //                      ^
    //   required: Holder<T>
    //   found:    Holder
    //   where T is a type-variable:
    //     T extends Object declared in
    //       method <T>exact2(Holder<T>,T)
    // 2 warnings

    Long r6 = exact2(qualified, lng);

    //- Long r7 = exact2(unbounded, lng);
    // error: method exact2 in class Wildcards
    // cannot be applied to given types;
    //     Long r7 = exact2(unbounded, lng);
    //               ^
    //   required: Holder<T>,T
    //   found: Holder<CAP#1>,Long
    //   reason: inference variable T has
    //     incompatible bounds
    //     equality constraints: CAP#1
    //     lower bounds: Long
    //   where T is a type-variable:
    //     T extends Object declared in
    //       method <T>exact2(Holder<T>,T)
    //   where CAP#1 is a fresh type-variable:
    //     CAP#1 extends Object from capture of ?
    // 1 error

    //- Long r8 = exact2(bounded, lng);
    // error: method exact2 in class Wildcards
    // cannot be applied to given types;
    //      Long r8 = exact2(bounded, lng);
    //                ^
    //   required: Holder<T>,T
    //   found: Holder<CAP#1>,Long
    //   reason: inference variable T
    //     has incompatible bounds
    //     equality constraints: CAP#1
    //     lower bounds: Long
    //   where T is a type-variable:
    //     T extends Object declared in
    //       method <T>exact2(Holder<T>,T)
    //   where CAP#1 is a fresh type-variable:
    //     CAP#1 extends Long from
    //       capture of ? extends Long
    // 1 error

    //- Long r9 = wildSubtype(raw, lng);
    // warning: [unchecked] unchecked method invocation:
    // method wildSubtype in class Wildcards
    // is applied to given types
    //     Long r9 = wildSubtype(raw, lng);
    //                          ^
    //   required: Holder<? extends T>,T
    //   found: Holder,Long
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>wildSubtype(Holder<? extends T>,T)
    // warning: [unchecked] unchecked conversion
    //     Long r9 = wildSubtype(raw, lng);
    //                           ^
    //   required: Holder<? extends T>
    //   found:    Holder
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>wildSubtype(Holder<? extends T>,T)
    // 2 warnings

    Long r10 = wildSubtype(qualified, lng);
    // OK, but can only return Object:
    Object r11 = wildSubtype(unbounded, lng);
    Long r12 = wildSubtype(bounded, lng);

    //- wildSupertype(raw, lng);
    // warning: [unchecked] unchecked method invocation:
    //   method wildSupertype in class Wildcards
    //   is applied to given types
    //     wildSupertype(raw, lng);
    //                  ^
    //   required: Holder<? super T>,T
    //   found: Holder,Long
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>wildSupertype(Holder<? super T>,T)
    // warning: [unchecked] unchecked conversion
    //     wildSupertype(raw, lng);
    //                   ^
    //   required: Holder<? super T>
    //   found:    Holder
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>wildSupertype(Holder<? super T>,T)
    // 2 warnings

    wildSupertype(qualified, lng);

    //- wildSupertype(unbounded, lng);
    // error: method wildSupertype in class Wildcards
    // cannot be applied to given types;
    //     wildSupertype(unbounded, lng);
    //     ^
    //   required: Holder<? super T>,T
    //   found: Holder<CAP#1>,Long
    //   reason: cannot infer type-variable(s) T
    //     (argument mismatch; Holder<CAP#1>
    //     cannot be converted to Holder<? super T>)
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>wildSupertype(Holder<? super T>,T)
    //   where CAP#1 is a fresh type-variable:
    //     CAP#1 extends Object from capture of ?
    // 1 error

    //- wildSupertype(bounded, lng);
    // error: method wildSupertype in class Wildcards
    // cannot be applied to given types;
    //     wildSupertype(bounded, lng);
    //     ^
    //   required: Holder<? super T>,T
    //   found: Holder<CAP#1>,Long
    //   reason: cannot infer type-variable(s) T
    //     (argument mismatch; Holder<CAP#1>
    //     cannot be converted to Holder<? super T>)
    //   where T is a type-variable:
    //     T extends Object declared in
    //     method <T>wildSupertype(Holder<? super T>,T)
    //   where CAP#1 is a fresh type-variable:
    //     CAP#1 extends Long from capture of
    //     ? extends Long
    // 1 error
  }
}
