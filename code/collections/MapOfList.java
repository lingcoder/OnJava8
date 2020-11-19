// collections/MapOfList.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java collections.MapOfList}
package collections;
import typeinfo.pets.*;
import java.util.*;

public class MapOfList {
  public static final Map<Person, List< ? extends Pet>>
    petPeople = new HashMap<>();
  static {
    petPeople.put(new Person("Dawn"),
      Arrays.asList(
        new Cymric("Molly"),
        new Mutt("Spot")));
    petPeople.put(new Person("Kate"),
      Arrays.asList(new Cat("Shackleton"),
        new Cat("Elsie May"), new Dog("Margrett")));
    petPeople.put(new Person("Marilyn"),
      Arrays.asList(
        new Pug("Louie aka Louis Snorkelstein Dupree"),
        new Cat("Stanford"),
        new Cat("Pinkola")));
    petPeople.put(new Person("Luke"),
      Arrays.asList(
        new Rat("Fuzzy"), new Rat("Fizzy")));
    petPeople.put(new Person("Isaac"),
      Arrays.asList(new Rat("Freckly")));
  }
  public static void main(String[] args) {
    System.out.println("People: " + petPeople.keySet());
    System.out.println("Pets: " + petPeople.values());
    for(Person person : petPeople.keySet()) {
      System.out.println(person + " has:");
      for(Pet pet : petPeople.get(person))
        System.out.println("    " + pet);
    }
  }
}
/* Output:
People: [Person Dawn, Person Kate, Person Isaac, Person
Marilyn, Person Luke]
Pets: [[Cymric Molly, Mutt Spot], [Cat Shackleton, Cat
Elsie May, Dog Margrett], [Rat Freckly], [Pug Louie aka
Louis Snorkelstein Dupree, Cat Stanford, Cat Pinkola],
[Rat Fuzzy, Rat Fizzy]]
Person Dawn has:
    Cymric Molly
    Mutt Spot
Person Kate has:
    Cat Shackleton
    Cat Elsie May
    Dog Margrett
Person Isaac has:
    Rat Freckly
Person Marilyn has:
    Pug Louie aka Louis Snorkelstein Dupree
    Cat Stanford
    Cat Pinkola
Person Luke has:
    Rat Fuzzy
    Rat Fizzy
*/
