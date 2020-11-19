// collectiontopics/Stacks.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Demonstration of Stack Class
import java.util.*;

enum Month { JANUARY, FEBRUARY, MARCH, APRIL,
  MAY, JUNE, JULY, AUGUST, SEPTEMBER,
  OCTOBER, NOVEMBER }

public class Stacks {
  public static void main(String[] args) {
    Stack<String> stack = new Stack<>();
    for(Month m : Month.values())
      stack.push(m.toString());
    System.out.println("stack = " + stack);
    // Treating a stack as a Vector:
    stack.addElement("The last line");
    System.out.println(
      "element 5 = " + stack.elementAt(5));
    System.out.println("popping elements:");
    while(!stack.empty())
      System.out.print(stack.pop() + " ");

    // Using a LinkedList as a Stack:
    LinkedList<String> lstack = new LinkedList<>();
    for(Month m : Month.values())
      lstack.addFirst(m.toString());
    System.out.println("lstack = " + lstack);
    while(!lstack.isEmpty())
      System.out.print(lstack.removeFirst() + " ");

    // Using the Stack class from
    // the Collections Chapter:
    onjava.Stack<String> stack2 =
      new onjava.Stack<>();
    for(Month m : Month.values())
      stack2.push(m.toString());
    System.out.println("stack2 = " + stack2);
    while(!stack2.isEmpty())
      System.out.print(stack2.pop() + " ");

  }
}
/* Output:
stack = [JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER]
element 5 = JUNE
popping elements:
The last line NOVEMBER OCTOBER SEPTEMBER AUGUST JULY
JUNE MAY APRIL MARCH FEBRUARY JANUARY lstack =
[NOVEMBER, OCTOBER, SEPTEMBER, AUGUST, JULY, JUNE, MAY,
APRIL, MARCH, FEBRUARY, JANUARY]
NOVEMBER OCTOBER SEPTEMBER AUGUST JULY JUNE MAY APRIL
MARCH FEBRUARY JANUARY stack2 = [NOVEMBER, OCTOBER,
SEPTEMBER, AUGUST, JULY, JUNE, MAY, APRIL, MARCH,
FEBRUARY, JANUARY]
NOVEMBER OCTOBER SEPTEMBER AUGUST JULY JUNE MAY APRIL
MARCH FEBRUARY JANUARY
*/
