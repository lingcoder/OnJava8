// enums/Competitor.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Switching one enum on another
package enums;

public interface Competitor<T extends Competitor<T>> {
  Outcome compete(T competitor);
}
