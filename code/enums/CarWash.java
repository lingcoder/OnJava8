// enums/CarWash.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;

public class CarWash {
  public enum Cycle {
    UNDERBODY {
      @Override
      void action() {
        System.out.println("Spraying the underbody");
      }
    },
    WHEELWASH {
      @Override
      void action() {
        System.out.println("Washing the wheels");
      }
    },
    PREWASH {
      @Override
      void action() {
        System.out.println("Loosening the dirt");
      }
    },
    BASIC {
      @Override
      void action() {
        System.out.println("The basic wash");
      }
    },
    HOTWAX {
      @Override
      void action() {
        System.out.println("Applying hot wax");
      }
    },
    RINSE {
      @Override
      void action() {
        System.out.println("Rinsing");
      }
    },
    BLOWDRY {
      @Override
      void action() {
        System.out.println("Blowing dry");
      }
    };
    abstract void action();
  }
  EnumSet<Cycle> cycles =
    EnumSet.of(Cycle.BASIC, Cycle.RINSE);
  public void add(Cycle cycle) {
    cycles.add(cycle);
  }
  public void washCar() {
    for(Cycle c : cycles)
      c.action();
  }
  @Override
  public String toString() {
    return cycles.toString();
  }
  public static void main(String[] args) {
    CarWash wash = new CarWash();
    System.out.println(wash);
    wash.washCar();
    // Order of addition is unimportant:
    wash.add(Cycle.BLOWDRY);
    wash.add(Cycle.BLOWDRY); // Duplicates ignored
    wash.add(Cycle.RINSE);
    wash.add(Cycle.HOTWAX);
    System.out.println(wash);
    wash.washCar();
  }
}
/* Output:
[BASIC, RINSE]
The basic wash
Rinsing
[BASIC, HOTWAX, RINSE, BLOWDRY]
The basic wash
Applying hot wax
Rinsing
Blowing dry
*/
