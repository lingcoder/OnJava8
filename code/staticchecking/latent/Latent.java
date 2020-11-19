// staticchecking/latent/Latent.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java staticchecking.latent.Latent}
package staticchecking.latent;
import java.lang.reflect.*;

class Dog {
  public void talk() {
    System.out.println("Woof!");
  }
  public void reproduce() {}
}

class Robot {
  public void talk() {
    System.out.println("Click!");
  }
  public void oilChange() {}
}

class Mime {
  public void walkAgainstTheWind() {}
  public String toString() { return "Mime"; }
}

class Communicate {
  public static void speak(Object speaker) {
    try {
     Class<? extends Object> spkr =
       speaker.getClass();
     Method talk =
       spkr.getMethod("talk", (Class[])null);
     talk.invoke(speaker, new Object[]{});
    } catch(NoSuchMethodException e) {
     System.out.println(
       speaker + " cannot talk");
    } catch(IllegalAccessException e) {
     System.out.println(
       speaker + " IllegalAccessException");
    } catch(InvocationTargetException e) {
     System.out.println(
       speaker + " InvocationTargetException");
    }
  }
}

public class Latent {
  public static void main(String[] args) {
    Communicate.speak(new Dog());
    Communicate.speak(new Robot());
    Communicate.speak(new Mime());
  }
}
/* Output:
Woof!
Click!
Mime cannot talk
*/
