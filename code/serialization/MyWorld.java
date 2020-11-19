// serialization/MyWorld.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.io.*;
import java.util.*;

class House implements Serializable {}

class Animal implements Serializable {
  private String name;
  private House preferredHouse;
  Animal(String nm, House h) {
    name = nm;
    preferredHouse = h;
  }
  @Override
  public String toString() {
    return name + "[" + super.toString() +
      "], " + preferredHouse + "\n";
  }
}

public class MyWorld {
  public static void main(String[] args) {
    House house = new House();
    List<Animal> animals = new ArrayList<>();
    animals.add(
      new Animal("Bosco the dog", house));
    animals.add(
      new Animal("Ralph the hamster", house));
    animals.add(
      new Animal("Molly the cat", house));
    System.out.println("animals: " + animals);
    try(
      ByteArrayOutputStream buf1 =
        new ByteArrayOutputStream();
      ObjectOutputStream o1 =
        new ObjectOutputStream(buf1)
    ) {
      o1.writeObject(animals);
      o1.writeObject(animals); // Write a 2nd set
      // Write to a different stream:
      try(
        ByteArrayOutputStream buf2 =
          new ByteArrayOutputStream();
        ObjectOutputStream o2 =
          new ObjectOutputStream(buf2)
      ) {
        o2.writeObject(animals);
        // Now get them back:
        try(
          ObjectInputStream in1 =
            new ObjectInputStream(
              new ByteArrayInputStream(
                buf1.toByteArray()));
          ObjectInputStream in2 =
            new ObjectInputStream(
              new ByteArrayInputStream(
                buf2.toByteArray()))
        ) {
          List
            animals1 = (List)in1.readObject(),
            animals2 = (List)in1.readObject(),
            animals3 = (List)in2.readObject();
          System.out.println(
            "animals1: " + animals1);
          System.out.println(
            "animals2: " + animals2);
          System.out.println(
            "animals3: " + animals3);
        }
      }
    } catch(IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
/* Output:
animals: [Bosco the dog[Animal@15db9742],
House@6d06d69c
, Ralph the hamster[Animal@7852e922], House@6d06d69c
, Molly the cat[Animal@4e25154f], House@6d06d69c
]
animals1: [Bosco the dog[Animal@7ba4f24f],
House@3b9a45b3
, Ralph the hamster[Animal@7699a589], House@3b9a45b3
, Molly the cat[Animal@58372a00], House@3b9a45b3
]
animals2: [Bosco the dog[Animal@7ba4f24f],
House@3b9a45b3
, Ralph the hamster[Animal@7699a589], House@3b9a45b3
, Molly the cat[Animal@58372a00], House@3b9a45b3
]
animals3: [Bosco the dog[Animal@4dd8dc3],
House@6d03e736
, Ralph the hamster[Animal@568db2f2], House@6d03e736
, Molly the cat[Animal@378bf509], House@6d03e736
]
*/
