// generics/EpicBattle.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Bounds in Java generics
import java.util.*;

interface SuperPower {}

interface XRayVision extends SuperPower {
  void seeThroughWalls();
}

interface SuperHearing extends SuperPower {
  void hearSubtleNoises();
}

interface SuperSmell extends SuperPower {
  void trackBySmell();
}

class SuperHero<POWER extends SuperPower> {
  POWER power;
  SuperHero(POWER power) { this.power = power; }
  POWER getPower() { return power; }
}

class SuperSleuth<POWER extends XRayVision>
extends SuperHero<POWER> {
  SuperSleuth(POWER power) { super(power); }
  void see() { power.seeThroughWalls(); }
}

class
CanineHero<POWER extends SuperHearing & SuperSmell>
extends SuperHero<POWER> {
  CanineHero(POWER power) { super(power); }
  void hear() { power.hearSubtleNoises(); }
  void smell() { power.trackBySmell(); }
}

class SuperHearSmell
implements SuperHearing, SuperSmell {
  @Override
  public void hearSubtleNoises() {}
  @Override
  public void trackBySmell() {}
}

class DogPerson extends CanineHero<SuperHearSmell> {
  DogPerson() { super(new SuperHearSmell()); }
}

public class EpicBattle {
  // Bounds in generic methods:
  static <POWER extends SuperHearing>
  void useSuperHearing(SuperHero<POWER> hero) {
    hero.getPower().hearSubtleNoises();
  }
  static <POWER extends SuperHearing & SuperSmell>
  void superFind(SuperHero<POWER> hero) {
    hero.getPower().hearSubtleNoises();
    hero.getPower().trackBySmell();
  }
  public static void main(String[] args) {
    DogPerson dogPerson = new DogPerson();
    useSuperHearing(dogPerson);
    superFind(dogPerson);
    // You can do this:
    List<? extends SuperHearing> audioPeople;
    // But you can't do this:
    // List<? extends SuperHearing & SuperSmell> dogPs;
  }
}
