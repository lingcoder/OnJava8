// concurrent/QuittingTasks.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import onjava.Nap;

public class QuittingTasks {
  public static final int COUNT = 150;
  public static void main(String[] args) {
    ExecutorService es =
      Executors.newCachedThreadPool();
    List<QuittableTask> tasks =
      IntStream.range(1, COUNT)
        .mapToObj(QuittableTask::new)
        .peek(qt -> es.execute(qt))
        .collect(Collectors.toList());
    new Nap(1);
    tasks.forEach(QuittableTask::quit);
    es.shutdown();
  }
}
/* Output:
24 27 31 8 11 7 19 12 16 4 23 3 28 32 15 20 63 60 68 67
64 39 47 52 51 55 40 43 48 59 44 56 36 35 71 72 83 103
96 92 88 99 100 87 91 79 75 84 76 115 108 112 104 107
111 95 80 147 120 127 119 123 144 143 116 132 124 128
136 131 135 139 148 140 2 126 6 5 1 18 129 17 14 13 21
22 9 10 30 33 58 37 125 26 34 133 145 78 137 141 138 62
74 142 86 65 73 146 70 42 149 121 110 134 105 82 117
106 113 122 45 114 118 38 50 29 90 101 89 57 53 94 41
61 66 130 69 77 81 85 93 25 102 54 109 98 49 46 97
*/
