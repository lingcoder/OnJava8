// streams/StreamOfStreams.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.stream.*;

public class StreamOfStreams {
  public static void main(String[] args) {
    Stream.of(1, 2, 3)
      .map(i -> Stream.of("Gonzo", "Kermit", "Beaker"))
      .map(e-> e.getClass().getName())
      .forEach(System.out::println);
  }
}
/* Output:
java.util.stream.ReferencePipeline$Head
java.util.stream.ReferencePipeline$Head
java.util.stream.ReferencePipeline$Head
*/
