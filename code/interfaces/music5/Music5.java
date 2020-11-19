// interfaces/music5/Music5.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java interfaces.music5.Music5}
package interfaces.music5;
import polymorphism.music.Note;

interface Instrument {
  // Compile-time constant:
  int VALUE = 5; // static & final
  default void play(Note n) {  // Automatically public
    System.out.println(this + ".play() " + n);
  }
  default void adjust() {
    System.out.println("Adjusting " + this);
  }
}

class Wind implements Instrument {
  @Override
  public String toString() { return "Wind"; }
}

class Percussion implements Instrument {
  @Override
  public String toString() { return "Percussion"; }
}

class Stringed implements Instrument {
  @Override
  public String toString() { return "Stringed"; }
}

class Brass extends Wind {
  @Override
  public String toString() { return "Brass"; }
}

class Woodwind extends Wind {
  @Override
  public String toString() { return "Woodwind"; }
}

public class Music5 {
  // Doesn't care about type, so new types
  // added to the system still work right:
  static void tune(Instrument i) {
    // ...
    i.play(Note.MIDDLE_C);
  }
  static void tuneAll(Instrument[] e) {
    for(Instrument i : e)
      tune(i);
  }
  public static void main(String[] args) {
    // Upcasting during addition to the array:
    Instrument[] orchestra = {
      new Wind(),
      new Percussion(),
      new Stringed(),
      new Brass(),
      new Woodwind()
    };
    tuneAll(orchestra);
  }
}
/* Output:
Wind.play() MIDDLE_C
Percussion.play() MIDDLE_C
Stringed.play() MIDDLE_C
Brass.play() MIDDLE_C
Woodwind.play() MIDDLE_C
*/
