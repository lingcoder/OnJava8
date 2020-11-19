// references/HorrorFlick.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Insert Cloneability at any level of inheritance

class Person {}

class Hero extends Person {}

class Scientist extends Person implements Cloneable {
  @Override
  public Scientist clone() {
    try {
      return (Scientist)super.clone();
    } catch(CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}

class MadScientist extends Scientist {}

public class HorrorFlick {
  public static void main(String[] args) {
    Person p = new Person();
    Hero h = new Hero();
    Scientist s = new Scientist();
    MadScientist m = new MadScientist();
    //- p = (Person)p.clone(); // Compile error
    //- h = (Hero)h.clone(); // Compile error
    s = s.clone();
    m = (MadScientist)m.clone();
  }
}
