// typeinfo/RegisteredFactories.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Registering Factories in the base class
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

class Part implements Supplier<Part> {
  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
  static List<Supplier<? extends Part>> prototypes =
    Arrays.asList(
      new FuelFilter(),
      new AirFilter(),
      new CabinAirFilter(),
      new OilFilter(),
      new FanBelt(),
      new PowerSteeringBelt(),
      new GeneratorBelt()
    );
  private static Random rand = new Random(47);
  public Part get() {
    int n = rand.nextInt(prototypes.size());
    return prototypes.get(n).get();
  }
}

class Filter extends Part {}

class FuelFilter extends Filter {
  @Override
  public FuelFilter get() { return new FuelFilter(); }
}

class AirFilter extends Filter {
  @Override
  public AirFilter get() { return new AirFilter(); }
}

class CabinAirFilter extends Filter {
  @Override
  public CabinAirFilter get() {
    return new CabinAirFilter();
  }
}

class OilFilter extends Filter {
  @Override
  public OilFilter get() { return new OilFilter(); }
}

class Belt extends Part {}

class FanBelt extends Belt {
  @Override
  public FanBelt get() { return new FanBelt(); }
}

class GeneratorBelt extends Belt {
  @Override
  public GeneratorBelt get() {
    return new GeneratorBelt();
  }
}

class PowerSteeringBelt extends Belt {
  @Override
  public PowerSteeringBelt get() {
    return new PowerSteeringBelt();
  }
}

public class RegisteredFactories {
  public static void main(String[] args) {
    Stream.generate(new Part())
      .limit(10)
      .forEach(System.out::println);
  }
}
/* Output:
GeneratorBelt
CabinAirFilter
GeneratorBelt
AirFilter
PowerSteeringBelt
CabinAirFilter
FuelFilter
PowerSteeringBelt
PowerSteeringBelt
FuelFilter
*/
