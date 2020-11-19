// interfaces/interfaceprocessor/FilterProcessor.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java interfaces.interfaceprocessor.FilterProcessor}
package interfaces.interfaceprocessor;
import interfaces.filters.*;

class FilterAdapter implements Processor {
  Filter filter;
  FilterAdapter(Filter filter) {
    this.filter = filter;
  }
  @Override
  public String name() { return filter.name(); }
  @Override
  public Waveform process(Object input) {
    return filter.process((Waveform)input);
  }
}

public class FilterProcessor {
  public static void main(String[] args) {
    Waveform w = new Waveform();
    Applicator.apply(
      new FilterAdapter(new LowPass(1.0)), w);
    Applicator.apply(
      new FilterAdapter(new HighPass(2.0)), w);
    Applicator.apply(
      new FilterAdapter(new BandPass(3.0, 4.0)), w);
  }
}
/* Output:
Using Processor LowPass
Waveform 0
Using Processor HighPass
Waveform 0
Using Processor BandPass
Waveform 0
*/
