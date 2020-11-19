// innerclasses/controller/Event.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// The common methods for any control event
package innerclasses.controller;
import java.time.*; // Java 8 time classes

public abstract class Event {
  private Instant eventTime;
  protected final Duration delayTime;
  public Event(long millisecondDelay) {
    delayTime = Duration.ofMillis(millisecondDelay);
    start();
  }
  public void start() { // Allows restarting
    eventTime = Instant.now().plus(delayTime);
  }
  public boolean ready() {
    return Instant.now().isAfter(eventTime);
  }
  public abstract void action();
}
