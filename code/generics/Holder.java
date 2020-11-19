// generics/Holder.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.Objects;

public class Holder<T> {
  private T value;
  public Holder() {}
  public Holder(T val) { value = val; }
  public void set(T val) { value = val; }
  public T get() { return value; }
  @Override
  public boolean equals(Object o) {
    return o instanceof Holder &&
      Objects.equals(value, ((Holder)o).value);
  }
  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
  public static void main(String[] args) {
    Holder<Apple> apple = new Holder<>(new Apple());
    Apple d = apple.get();
    apple.set(d);
    // Holder<Fruit> Fruit = apple; // Cannot upcast
    Holder<? extends Fruit> fruit = apple; // OK
    Fruit p = fruit.get();
    d = (Apple)fruit.get(); // Returns 'Object'
    try {
      Orange c = (Orange)fruit.get(); // No warning
    } catch(Exception e) { System.out.println(e); }
    // fruit.set(new Apple()); // Cannot call set()
    // fruit.set(new Fruit()); // Cannot call set()
    System.out.println(fruit.equals(d)); // OK
  }
}
/* Output:
java.lang.ClassCastException: Apple cannot be cast to
Orange
false
*/
