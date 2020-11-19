// validating/SLF4JLevels.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import org.slf4j.*;

public class SLF4JLevels {
  private static Logger log =
    LoggerFactory.getLogger(SLF4JLevels.class);
  public static void main(String[] args) {
    log.trace("Hello");
    log.debug("Logging");
    log.info("Using");
    log.warn("the SLF4J");
    log.error("Facade");
  }
}
/* Output:
2017-05-09T06:07:52.846
[main] TRACE SLF4JLevels - Hello
2017-05-09T06:07:52.849
[main] DEBUG SLF4JLevels - Logging
2017-05-09T06:07:52.849
[main] INFO  SLF4JLevels - Using
2017-05-09T06:07:52.850
[main] WARN  SLF4JLevels - the SLF4J
2017-05-09T06:07:52.851
[main] ERROR SLF4JLevels - Facade
*/
