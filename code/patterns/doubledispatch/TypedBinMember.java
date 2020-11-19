// patterns/doubledispatch/TypedBinMember.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// An interface for adding the double dispatching
// method to the trash hierarchy without
// modifying the original hierarchy
package patterns.doubledispatch;
import java.util.*;

public interface TypedBinMember {
  // The new method:
  boolean addToBin(List<TypedBin> bins);
}
