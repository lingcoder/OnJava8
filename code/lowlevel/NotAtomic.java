// lowlevel/NotAtomic.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// javap -c NotAtomic
// {ExcludeFromGradle}
// {VisuallyInspectOutput}

public class NotAtomic {
  int i;
  void f1() { i++; }
  void f2() { i += 3; }
}
/* Output:
Compiled from "NotAtomic.java"
public class NotAtomic {
  int i;

  public NotAtomic();
    Code:
       0: aload_0
       1: invokespecial #1 // Method java/lang/Object."<init>":()V
       4: return

  void f1();
    Code:
       0: aload_0
       1: dup
       2: getfield      #2 // Field
i:I
       5: iconst_1
       6: iadd
       7: putfield      #2 // Field
i:I
      10: return

  void f2();
    Code:
       0: aload_0
       1: dup
       2: getfield      #2 // Field
i:I
       5: iconst_3
       6: iadd
       7: putfield      #2 // Field
i:I
      10: return
}
*/
