// validating/jmh/JMH3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package validating.jmh;
import java.util.*;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(1)
public class JMH3 {
  private long[] la;
  @Param({
    "1",
    "10",
    "100",
    "1000",
    "10000",
    "100000",
    "1000000",
    "10000000",
    "100000000",
    "250000000"
  })
  int size;

  @Setup
  public void setup() {
    la = new long[size];
  }
  public static long f(long x) {
    long quadratic = 42 * x * x + 19 * x + 47;
    return Long.divideUnsigned(quadratic, x + 1);
  }
  @Benchmark
  public void setAll() {
    Arrays.setAll(la, n -> f(n));
  }
  @Benchmark
  public void parallelSetAll() {
    Arrays.parallelSetAll(la, n -> f(n));
  }
}
