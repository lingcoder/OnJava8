// interfaces/filters/Waveform.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package interfaces.filters;

public class Waveform {
  private static long counter;
  private final long id = counter++;
  @Override
  public String toString() {
    return "Waveform " + id;
  }
}
