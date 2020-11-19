// annotations/simplest/Simple.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A bare-bones annotation
package annotations.simplest;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD,
         ElementType.CONSTRUCTOR,
         ElementType.ANNOTATION_TYPE,
         ElementType.PACKAGE, ElementType.FIELD,
         ElementType.LOCAL_VARIABLE})
public @interface Simple {
    String value() default "-default-";
}
