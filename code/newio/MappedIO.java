// newio/MappedIO.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {ExcludeFromGradle} Runs too long under WSL2
import java.util.*;
import java.nio.*;
import java.nio.channels.*;
import java.io.*;

public class MappedIO {
  private static int numOfInts =      4_000_000;
  private static int numOfUbuffInts = 100_000;
  private abstract static class Tester {
    private String name;
    Tester(String name) {
      this.name = name;
    }
    public void runTest() {
      System.out.print(name + ": ");
      long start = System.nanoTime();
      test();
      double duration = System.nanoTime() - start;
      System.out.format("%.3f%n", duration/1.0e9);
    }
    public abstract void test();
  }
  private static Tester[] tests = {
    new Tester("Stream Write") {
      @Override
      public void test() {
        try(
          DataOutputStream dos =
            new DataOutputStream(
              new BufferedOutputStream(
                new FileOutputStream(
                  new File("temp.tmp"))))
        ) {
          for(int i = 0; i < numOfInts; i++)
            dos.writeInt(i);
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Mapped Write") {
      @Override
      public void test() {
        try(
          FileChannel fc =
            new RandomAccessFile("temp.tmp", "rw")
              .getChannel()
        ) {
          IntBuffer ib =
            fc.map(FileChannel.MapMode.READ_WRITE,
              0, fc.size()).asIntBuffer();
          for(int i = 0; i < numOfInts; i++)
            ib.put(i);
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Stream Read") {
      @Override
      public void test() {
        try(
          DataInputStream dis =
            new DataInputStream(
              new BufferedInputStream(
                new FileInputStream("temp.tmp")))
        ) {
          for(int i = 0; i < numOfInts; i++)
            dis.readInt();
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Mapped Read") {
      @Override
      public void test() {
        try(
          FileChannel fc = new FileInputStream(
            new File("temp.tmp")).getChannel()
        ) {
          IntBuffer ib =
            fc.map(FileChannel.MapMode.READ_ONLY,
              0, fc.size()).asIntBuffer();
          while(ib.hasRemaining())
            ib.get();
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Stream Read/Write") {
      @Override
      public void test() {
        try(
          RandomAccessFile raf =
            new RandomAccessFile(
              new File("temp.tmp"), "rw")
        ) {
          raf.writeInt(1);
          for(int i = 0; i < numOfUbuffInts; i++) {
            raf.seek(raf.length() - 4);
            raf.writeInt(raf.readInt());
          }
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    },
    new Tester("Mapped Read/Write") {
      @Override
      public void test() {
        try(
          FileChannel fc = new RandomAccessFile(
            new File("temp.tmp"), "rw").getChannel()
        ) {
          IntBuffer ib =
            fc.map(FileChannel.MapMode.READ_WRITE,
              0, fc.size()).asIntBuffer();
          ib.put(0);
          for(int i = 1; i < numOfUbuffInts; i++)
            ib.put(ib.get(i - 1));
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
  };
  public static void main(String[] args) {
    Arrays.stream(tests).forEach(Tester::runTest);
  }
}
/* Output:
Stream Write: 0.615
Mapped Write: 0.050
Stream Read: 0.577
Mapped Read: 0.015
Stream Read/Write: 4.069
Mapped Read/Write: 0.013
*/
