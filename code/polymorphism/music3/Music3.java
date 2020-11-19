// polymorphism/music3/Music3.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// An extensible program
// {java polymorphism.music3.Music3}
package polymorphism.music3;
import polymorphism.music.Note;

class Instrument {
  void play(Note n) {
    System.out.println("Instrument.play() " + n);
  }
  String what() { return "Instrument"; }
  void adjust() {
    System.out.println("Adjusting Instrument");
  }
}

class Wind extends Instrument {
  @Override
  void play(Note n) {
    System.out.println("Wind.play() " + n);
  }
  @Override
  String what() { return "Wind"; }
  @Override
  void adjust() {
    System.out.println("Adjusting Wind");
  }
}

class Percussion extends Instrument {
  @Override
  void play(Note n) {
    System.out.println("Percussion.play() " + n);
  }
  @Override
  String what() { return "Percussion"; }
  @Override
  void adjust() {
    System.out.println("Adjusting Percussion");
  }
}

class Stringed extends Instrument {
  @Override
  void play(Note n) {
    System.out.println("Stringed.play() " + n);
  }
  @Override
  String what() { return "Stringed"; }
  @Override
  void adjust() {
    System.out.println("Adjusting Stringed");
  }
}

class Brass extends Wind {
  @Override
  void play(Note n) {
    System.out.println("Brass.play() " + n);
  }
  @Override
  void adjust() {
    System.out.println("Adjusting Brass");
  }
}

class Woodwind extends Wind {
  @Override
  void play(Note n) {
    System.out.println("Woodwind.play() " + n);
  }
  @Override
  String what() { return "Woodwind"; }
}

public class Music3 {
  // Doesn't care about type, so new types
  // added to the system still work right:
  public static void tune(Instrument i) {
    // ...
    i.play(Note.MIDDLE_C);
  }
  public static void tuneAll(Instrument[] e) {
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
