// collectiontopics/ReadOnly.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using the Collections.unmodifiable methods
import java.util.*;
import onjava.*;

public class ReadOnly {
  static Collection<String> data =
    new ArrayList<>(Countries.names(6));
  public static void main(String[] args) {
    Collection<String> c =
      Collections.unmodifiableCollection(
        new ArrayList<>(data));
    System.out.println(c); // Reading is OK
    //- c.add("one"); // Can't change it

    List<String> a = Collections.unmodifiableList(
        new ArrayList<>(data));
    ListIterator<String> lit = a.listIterator();
    System.out.println(lit.next()); // Reading is OK
    //- lit.add("one"); // Can't change it

    Set<String> s = Collections.unmodifiableSet(
      new HashSet<>(data));
    System.out.println(s); // Reading is OK
    //- s.add("one"); // Can't change it

    // For a SortedSet:
    Set<String> ss =
      Collections.unmodifiableSortedSet(
        new TreeSet<>(data));

    Map<String,String> m =
      Collections.unmodifiableMap(
        new HashMap<>(Countries.capitals(6)));
    System.out.println(m); // Reading is OK
    //- m.put("Ralph", "Howdy!");

    // For a SortedMap:
    Map<String,String> sm =
      Collections.unmodifiableSortedMap(
        new TreeMap<>(Countries.capitals(6)));
  }
}
/* Output:
[ALGERIA, ANGOLA, BENIN, BOTSWANA, BURKINA FASO,
BURUNDI]
ALGERIA
[BENIN, BOTSWANA, ANGOLA, BURKINA FASO, ALGERIA,
BURUNDI]
{BENIN=Porto-Novo, BOTSWANA=Gaberone, ANGOLA=Luanda,
BURKINA FASO=Ouagadougou, ALGERIA=Algiers,
BURUNDI=Bujumbura}
*/
