// arrays/jmh/ParallelSort.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package arrays.jmh;
import java.util.*;
import onjava.*;
import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
public class ParallelSort {
  private long[] la;
  @Setup
  public void setup() {
    la = new Rand.Plong().array(100_000);
  }
  @Benchmark
  public void sort() {
    Arrays.sort(la);
  }
  @Benchmark
  public void parallelSort() {
    Arrays.parallelSort(la);
  }
}
