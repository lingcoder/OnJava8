// innerclasses/Parcel9.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class Parcel9 {
  // Argument must be final or "effectively final"
  // to use within the anonymous inner class:
  public Destination destination(final String dest) {
    return new Destination() {
      private String label = dest;
      @Override
      public String readLabel() { return label; }
    };
  }
  public static void main(String[] args) {
    Parcel9 p = new Parcel9();
    Destination d = p.destination("Tasmania");
  }
}
