// collectiontopics/Synchronization.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Using the Collections.synchronized methods
import java.util.*;

public class Synchronization {
  public static void main(String[] args) {
    Collection<String> c =
      Collections.synchronizedCollection(
        new ArrayList<>());
    List<String> list = Collections
      .synchronizedList(new ArrayList<>());
    Set<String> s = Collections
      .synchronizedSet(new HashSet<>());
    Set<String> ss = Collections
      .synchronizedSortedSet(new TreeSet<>());
    Map<String,String> m = Collections
      .synchronizedMap(new HashMap<>());
    Map<String,String> sm = Collections
      .synchronizedSortedMap(new TreeMap<>());
  }
}
