// patterns/strategy/StrategyPattern.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// {java patterns.strategy.StrategyPattern}
package patterns.strategy;
import java.util.function.*;
import java.util.*;

// The common strategy base type:
class FindMinima {
  Function<List<Double>, List<Double>> algorithm;
}

// The various strategies:
class LeastSquares extends FindMinima {
  LeastSquares() {
    // Line is a sequence of points (Dummy data):
    algorithm = (line) -> Arrays.asList(1.1, 2.2);
  }
}

class Perturbation extends FindMinima {
  Perturbation() {
    algorithm = (line) -> Arrays.asList(3.3, 4.4);
  }
}

class Bisection extends FindMinima {
  Bisection() {
    algorithm = (line) -> Arrays.asList(5.5, 6.6);
  }
}

// The "Context" controls the strategy:
class MinimaSolver {
  private FindMinima strategy;
  MinimaSolver(FindMinima strat) {
    strategy = strat;
  }
  List<Double> minima(List<Double> line) {
    return strategy.algorithm.apply(line);
  }
  void changeAlgorithm(FindMinima newAlgorithm) {
    strategy = newAlgorithm;
  }
}

public class StrategyPattern {
  public static void main(String[] args) {
    MinimaSolver solver =
      new MinimaSolver(new LeastSquares());
    List<Double> line = Arrays.asList(
      1.0, 2.0, 1.0, 2.0, -1.0,
      3.0, 4.0, 5.0, 4.0 );
    System.out.println(solver.minima(line));
    solver.changeAlgorithm(new Bisection());
    System.out.println(solver.minima(line));
  }
}
/* Output:
[1.1, 2.2]
[5.5, 6.6]
*/
