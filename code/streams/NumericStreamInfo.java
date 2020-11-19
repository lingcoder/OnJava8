// streams/NumericStreamInfo.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.stream.*;
import static streams.RandInts.*;

public class NumericStreamInfo {
  public static void main(String[] args) {
    System.out.println(rands().average().getAsDouble());
    System.out.println(rands().max().getAsInt());
    System.out.println(rands().min().getAsInt());
    System.out.println(rands().sum());
    System.out.println(rands().summaryStatistics());
  }
}
/* Output:
507.94
998
8
50794
IntSummaryStatistics{count=100, sum=50794, min=8,
average=507.940000, max=998}
*/
