// interfaces/filters/HighPass.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package interfaces.filters;

public class HighPass extends Filter {
  double cutoff;
  public HighPass(double cutoff) {
    this.cutoff = cutoff;
  }
  @Override
  public Waveform process(Waveform input) {
    return input;
  }
}
