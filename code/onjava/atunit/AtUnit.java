// onjava/atunit/AtUnit.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// An annotation-based unit-test framework
// {java onjava.atunit.AtUnit}
package onjava.atunit;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;
import onjava.*;

public class AtUnit implements ProcessFiles.Strategy {
  static Class<?> testClass;
  static List<String> failedTests= new ArrayList<>();
  static long testsRun = 0;
  static long failures = 0;
  public static void
  main(String[] args) throws Exception {
    ClassLoader.getSystemClassLoader()
      .setDefaultAssertionStatus(true); // Enable assert
    new ProcessFiles(new AtUnit(), "class").start(args);
    if(failures == 0)
      System.out.println("OK (" + testsRun + " tests)");
    else {
      System.out.println("(" + testsRun + " tests)");
      System.out.println(
        "\n>>> " + failures + " FAILURE" +
        (failures > 1 ? "S" : "") + " <<<");
      for(String failed : failedTests)
        System.out.println("  " + failed);
    }
  }
  @Override
  public void process(File cFile) {
    try {
      String cName = ClassNameFinder.thisClass(
        Files.readAllBytes(cFile.toPath()));
      if(!cName.startsWith("public:"))
        return;
      cName = cName.split(":")[1];
      if(!cName.contains("."))
        return; // Ignore unpackaged classes
      testClass = Class.forName(cName);
    } catch(IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    TestMethods testMethods = new TestMethods();
    Method creator = null;
    Method cleanup = null;
    for(Method m : testClass.getDeclaredMethods()) {
      testMethods.addIfTestMethod(m);
      if(creator == null)
        creator = checkForCreatorMethod(m);
      if(cleanup == null)
        cleanup = checkForCleanupMethod(m);
    }
    if(testMethods.size() > 0) {
      if(creator == null)
        try {
          if(!Modifier.isPublic(testClass
             .getDeclaredConstructor()
             .getModifiers())) {
            System.out.println("Error: " + testClass +
              " no-arg constructor must be public");
            System.exit(1);
          }
        } catch(NoSuchMethodException e) {
          // Synthesized no-arg constructor; OK
        }
      System.out.println(testClass.getName());
    }
    for(Method m : testMethods) {
      System.out.print("  . " + m.getName() + " ");
      try {
        Object testObject = createTestObject(creator);
        boolean success = false;
        try {
          if(m.getReturnType().equals(boolean.class))
            success = (Boolean)m.invoke(testObject);
          else {
            m.invoke(testObject);
            success = true; // If no assert fails
          }
        } catch(InvocationTargetException e) {
          // Actual exception is inside e:
          System.out.println(e.getCause());
        }
        System.out.println(success ? "" : "(failed)");
        testsRun++;
        if(!success) {
          failures++;
          failedTests.add(testClass.getName() +
            ": " + m.getName());
        }
        if(cleanup != null)
          cleanup.invoke(testObject, testObject);
      } catch(IllegalAccessException |
              IllegalArgumentException |
              InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }
  public static
  class TestMethods extends ArrayList<Method> {
    void addIfTestMethod(Method m) {
      if(m.getAnnotation(Test.class) == null)
        return;
      if(!(m.getReturnType().equals(boolean.class) ||
          m.getReturnType().equals(void.class)))
        throw new RuntimeException("@Test method" +
          " must return boolean or void");
      m.setAccessible(true); // If it's private, etc.
      add(m);
    }
  }
  private static
  Method checkForCreatorMethod(Method m) {
    if(m.getAnnotation(TestObjectCreate.class) == null)
      return null;
    if(!m.getReturnType().equals(testClass))
      throw new RuntimeException("@TestObjectCreate " +
        "must return instance of Class to be tested");
    if((m.getModifiers() &
         java.lang.reflect.Modifier.STATIC) < 1)
      throw new RuntimeException("@TestObjectCreate " +
        "must be static.");
    m.setAccessible(true);
    return m;
  }
  private static
  Method checkForCleanupMethod(Method m) {
    if(m.getAnnotation(TestObjectCleanup.class) == null)
      return null;
    if(!m.getReturnType().equals(void.class))
      throw new RuntimeException("@TestObjectCleanup " +
        "must return void");
    if((m.getModifiers() &
        java.lang.reflect.Modifier.STATIC) < 1)
      throw new RuntimeException("@TestObjectCleanup " +
        "must be static.");
    if(m.getParameterTypes().length == 0 ||
       m.getParameterTypes()[0] != testClass)
      throw new RuntimeException("@TestObjectCleanup " +
        "must take an argument of the tested type.");
    m.setAccessible(true);
    return m;
  }
  private static Object
  createTestObject(Method creator) {
    if(creator != null) {
      try {
        return creator.invoke(testClass);
      } catch(IllegalAccessException |
              IllegalArgumentException |
              InvocationTargetException e) {
        throw new RuntimeException("Couldn't run " +
          "@TestObject (creator) method.");
      }
    } else { // Use the no-arg constructor:
      try {
        return testClass
          .getConstructor().newInstance();
      } catch(InstantiationException |
              NoSuchMethodException |
              InvocationTargetException |
              IllegalAccessException e) {
        throw new RuntimeException(
          "Couldn't create a test object. " +
          "Try using a @TestObject method.");
      }
    }
  }
}
