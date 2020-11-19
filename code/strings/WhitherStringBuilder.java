// strings/WhitherStringBuilder.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.

public class WhitherStringBuilder {
  public String implicit(String[] fields) {
    String result = "";
    for(String field : fields) {
      result += field;
    }
    return result;
  }
  public String explicit(String[] fields) {
    StringBuilder result = new StringBuilder();
    for(String field : fields) {
      result.append(field);
    }
    return result.toString();
  }
}
