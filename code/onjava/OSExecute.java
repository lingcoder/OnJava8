// onjava/OSExecute.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Run an operating system command
// and send the output to the console
package onjava;
import java.io.*;

public class OSExecute {
  public static void command(String command) {
    boolean err = false;
    try {
      Process process = new ProcessBuilder(
        command.split(" ")).start();
      try(
        BufferedReader results = new BufferedReader(
          new InputStreamReader(
            process.getInputStream()));
        BufferedReader errors = new BufferedReader(
          new InputStreamReader(
            process.getErrorStream()))
      ) {
        results.lines()
          .forEach(System.out::println);
        err = errors.lines()
          .peek(System.err::println)
          .count() > 0;
      }
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
    if(err)
      throw new OSExecuteException(
        "Errors executing " + command);
  }
}
