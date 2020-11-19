// streams/RandomGenerators.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;

public class RandomGenerators {
  public static <T> void show(Stream<T> stream) {
      stream
        .limit(4)
        .forEach(System.out::println);
      System.out.println("++++++++");
  }
  public static void main(String[] args) {
    Random rand = new Random(47);
    show(rand.ints().boxed());
    show(rand.longs().boxed());
    show(rand.doubles().boxed());
    // Control the lower and upper bounds:
    show(rand.ints(10, 20).boxed());
    show(rand.longs(50, 100).boxed());
    show(rand.doubles(20, 30).boxed());
    // Control the stream size:
    show(rand.ints(2).boxed());
    show(rand.longs(2).boxed());
    show(rand.doubles(2).boxed());
    // Control the stream size and bounds:
    show(rand.ints(3, 3, 9).boxed());
    show(rand.longs(3, 12, 22).boxed());
    show(rand.doubles(3, 11.5, 12.3).boxed());
  }
}
/* Output:
-1172028779
1717241110
-2014573909
229403722
++++++++
2955289354441303771
3476817843704654257
-8917117694134521474
4941259272818818752
++++++++
0.2613610344283964
0.0508673570556899
0.8037155449603999
0.7620665811558285
++++++++
16
10
11
12
++++++++
65
99
54
58
++++++++
29.86777681078574
24.83968447804611
20.09247112332014
24.046793846338723
++++++++
1169976606
1947946283
++++++++
2970202997824602425
-2325326920272830366
++++++++
0.7024254510631527
0.6648552384607359
++++++++
6
7
7
++++++++
17
12
20
++++++++
12.27872414236691
11.732085449736195
12.196509449817267
++++++++
*/
