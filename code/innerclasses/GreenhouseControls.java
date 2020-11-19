// innerclasses/GreenhouseControls.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// This produces a specific application of the
// control system, all in a single class. Inner
// classes allow you to encapsulate different
// functionality for each type of event.
import innerclasses.controller.*;

public class GreenhouseControls extends Controller {
  private boolean light = false;
  public class LightOn extends Event {
    public LightOn(long delayTime) {
      super(delayTime);
    }
    @Override
    public void action() {
      // Put hardware control code here to
      // physically turn on the light.
      light = true;
    }
    @Override
    public String toString() {
      return "Light is on";
    }
  }
  public class LightOff extends Event {
    public LightOff(long delayTime) {
      super(delayTime);
    }
    @Override
    public void action() {
      // Put hardware control code here to
      // physically turn off the light.
      light = false;
    }
    @Override
    public String toString() {
      return "Light is off";
    }
  }
  private boolean water = false;
  public class WaterOn extends Event {
    public WaterOn(long delayTime) {
      super(delayTime);
    }
    @Override
    public void action() {
      // Put hardware control code here.
      water = true;
    }
    @Override
    public String toString() {
      return "Greenhouse water is on";
    }
  }
  public class WaterOff extends Event {
    public WaterOff(long delayTime) {
      super(delayTime);
    }
    @Override
    public void action() {
      // Put hardware control code here.
      water = false;
    }
    @Override
    public String toString() {
      return "Greenhouse water is off";
    }
  }
  private String thermostat = "Day";
  public class ThermostatNight extends Event {
    public ThermostatNight(long delayTime) {
      super(delayTime);
    }
    @Override
    public void action() {
      // Put hardware control code here.
      thermostat = "Night";
    }
    @Override
    public String toString() {
      return "Thermostat on night setting";
    }
  }
  public class ThermostatDay extends Event {
    public ThermostatDay(long delayTime) {
      super(delayTime);
    }
    @Override
    public void action() {
      // Put hardware control code here.
      thermostat = "Day";
    }
    @Override
    public String toString() {
      return "Thermostat on day setting";
    }
  }
  // An example of an action() that inserts a
  // new one of itself into the event list:
  public class Bell extends Event {
    public Bell(long delayTime) {
      super(delayTime);
    }
    @Override
    public void action() {
      addEvent(new Bell(delayTime.toMillis()));
    }
    @Override
    public String toString() {
      return "Bing!";
    }
  }
  public class Restart extends Event {
    private Event[] eventList;
    public
    Restart(long delayTime, Event[] eventList) {
      super(delayTime);
      this.eventList = eventList;
      for(Event e : eventList)
        addEvent(e);
    }
    @Override
    public void action() {
      for(Event e : eventList) {
        e.start(); // Rerun each event
        addEvent(e);
      }
      start(); // Rerun this Event
      addEvent(this);
    }
    @Override
    public String toString() {
      return "Restarting system";
    }
  }
  public static class Terminate extends Event {
    public Terminate(long delayTime) {
      super(delayTime);
    }
    @Override
    public void action() { System.exit(0); }
    @Override
    public String toString() {
      return "Terminating";
    }
  }
}
