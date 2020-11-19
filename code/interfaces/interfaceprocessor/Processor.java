// interfaces/interfaceprocessor/Processor.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
package interfaces.interfaceprocessor;

public interface Processor {
  default String name() {
    return getClass().getSimpleName();
  }
  Object process(Object input);
}
