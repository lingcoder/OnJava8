// housekeeping/InitialValues.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Shows default initial values

public class InitialValues {
  boolean t;
  char c;
  byte b;
  short s;
  int i;
  long l;
  float f;
  double d;
  InitialValues reference;
  void printInitialValues() {
    System.out.println("Data type   Initial value");
    System.out.println("boolean     " + t);
    System.out.println("char        [" + c + "]");
    System.out.println("byte        " + b);
    System.out.println("short       " + s);
    System.out.println("int         " + i);
    System.out.println("long        " + l);
    System.out.println("float       " + f);
    System.out.println("double      " + d);
    System.out.println("reference   " + reference);
  }
  public static void main(String[] args) {
    new InitialValues().printInitialValues();
  }
}
/* Output:
Data type   Initial value
boolean     false
char        [NUL]
byte        0
short       0
int         0
long        0
float       0.0
double      0.0
reference   null
*/
