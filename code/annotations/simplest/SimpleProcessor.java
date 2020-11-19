// annotations/simplest/SimpleProcessor.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// A bare-bones annotation processor
package annotations.simplest;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.*;

@SupportedAnnotationTypes(
  "annotations.simplest.Simple")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleProcessor
extends AbstractProcessor {
  @Override
  public boolean process(
    Set<? extends TypeElement> annotations,
    RoundEnvironment env) {
    for(TypeElement t : annotations)
      System.out.println(t);
    for(Element el :
      env.getElementsAnnotatedWith(Simple.class))
      display(el);
    return false;
  }
  private void display(Element el) {
    System.out.println("==== " + el + " ====");
    System.out.println(el.getKind() +
      " : " + el.getModifiers() +
      " : " + el.getSimpleName() +
      " : " + el.asType());
    if(el.getKind().equals(ElementKind.CLASS)) {
      TypeElement te = (TypeElement)el;
      System.out.println(te.getQualifiedName());
      System.out.println(te.getSuperclass());
      System.out.println(te.getEnclosedElements());
    }
    if(el.getKind().equals(ElementKind.METHOD)) {
      ExecutableElement ex = (ExecutableElement)el;
      System.out.print(ex.getReturnType() + " ");
      System.out.print(ex.getSimpleName() + "(");
      System.out.println(ex.getParameters() + ")");
    }
  }
}
