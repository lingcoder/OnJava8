// innerclasses/Parcel7.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Returning an instance of an anonymous inner class

public class Parcel7 {
  public Contents contents() {
    return new Contents() { // Insert class definition
      private int i = 11;
      @Override
      public int value() { return i; }
    }; // Semicolon required
  }
  public static void main(String[] args) {
    Parcel7 p = new Parcel7();
    Contents c = p.contents();
  }
}
