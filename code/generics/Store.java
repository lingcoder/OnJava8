// generics/Store.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Building a complex model using generic collections
import java.util.*;
import java.util.function.*;
import onjava.*;

class Product {
  private final int id;
  private String description;
  private double price;
  Product(int idNumber, String descr, double price) {
    id = idNumber;
    description = descr;
    this.price = price;
    System.out.println(toString());
  }
  @Override
  public String toString() {
    return id + ": " + description +
      ", price: $" + price;
  }
  public void priceChange(double change) {
    price += change;
  }
  public static Supplier<Product> generator =
    new Supplier<Product>() {
      private Random rand = new Random(47);
      @Override
      public Product get() {
        return new Product(rand.nextInt(1000), "Test",
          Math.round(
            rand.nextDouble() * 1000.0) + 0.99);
      }
    };
}

class Shelf extends ArrayList<Product> {
  Shelf(int nProducts) {
    Suppliers.fill(this, Product.generator, nProducts);
  }
}

class Aisle extends ArrayList<Shelf> {
  Aisle(int nShelves, int nProducts) {
    for(int i = 0; i < nShelves; i++)
      add(new Shelf(nProducts));
  }
}

class CheckoutStand {}
class Office {}

public class Store extends ArrayList<Aisle> {
  private ArrayList<CheckoutStand> checkouts =
    new ArrayList<>();
  private Office office = new Office();
  public Store(
    int nAisles, int nShelves, int nProducts) {
    for(int i = 0; i < nAisles; i++)
      add(new Aisle(nShelves, nProducts));
  }
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for(Aisle a : this)
      for(Shelf s : a)
        for(Product p : s) {
          result.append(p);
          result.append("\n");
        }
    return result.toString();
  }
  public static void main(String[] args) {
    System.out.println(new Store(5, 4, 3));
  }
}
/* Output: (First 8 Lines)
258: Test, price: $400.99
861: Test, price: $160.99
868: Test, price: $417.99
207: Test, price: $268.99
551: Test, price: $114.99
278: Test, price: $804.99
520: Test, price: $554.99
140: Test, price: $530.99
                  ...
*/
