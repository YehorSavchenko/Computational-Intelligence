# Evolutionary algorithm

## Goal
Find the coordinates of the centers of the vertices so that the graph has as few intersecting edges as possible.

## Input 
The graph is in the form of a text file<br>
N<br>
x1 x2 ....<br>
Where N is number of vertices and subsequent numbers are edges.<br>
For example<br>
4<br>
1 3 1 4 2 3 2 4<br>
Describes a graph with 4 vertices and 4 edges connecting vertices 1 with 3, 1 with 4, 2 with 3 and 2 with 4.

## Classes

### GraphDrawer
This is a class that is used to draw a graph using the Jenetics library. It reads in a file that contains 
information about the edges of a graph, and uses genetic algorithms to find the best position to place the vertices in 
a 2D space. The final result is displayed in a window using the Graphstream library.

The `main` method reads the edges from a file and creates a `Graph` object using the `createGraph` method. It then sets up 
the parameters for the genetic algorithm, such as the population size, crossover probability, and mutation probability. 
The algorithm is run using the `Engine` class from the Jenetics library, and the progress of the algorithm is displayed in
the console. Once the algorithm completes, the positions of the vertices are decoded from the final result of the 
algorithm and set on the `Graph` object, which is then displayed in a window.

### GraphDrawerTest
This is a JUnit test class for the `GraphDrawer` class, specifically for testing different combinations of selector, 
crossover, crossover probability, and mutation probability.
The test method `testTypes()` takes in one parameter, `path` which is the file path of the input data. The test method 
creates a map to store the results of the tests, with the key being a string containing the test's configuration and the
value being the string returned by the `executeAndGetResults()` method.
The test method then loops through a list of selectors, a list of booleans for crossover, a list of crossover probabilities,
a list of crossover numbers and a list of mutation probabilities. For each combination of these values, 
the `executeAndGetResults()` method is called with the corresponding parameters.
The results of the tests are then printed out in the final `test()` method that calls the `testTypes()` method. 
This test method is decorated with the `@Test` annotation, which means that when this class is run as a JUnit test, 
this method will be executed.

## Parameters

### Coding
We chose DoubleChromosome coding in the form of real numbers.<br>
Length of chromosomes depends on vertices numbers.<br>

Here is line of code which represents coding initialisation
`gtf = Genotype.of(DoubleChromosome.of(minC, maxC, vertexNumber * 2));` <br>

`minC` and `maxC` are used to define the range of values for the `DoubleChromosome` object created in this line.
`DoubleChromosome` is an implementation of the `Chromosome` interface from the Jenetics library, and it holds an array of
`DoubleGene` objects. The `of` method of the `DoubleChromosome` class is used to create an instance of `DoubleChromosome` and 
takes three parameters:

- `minC` represents the minimum value that each `DoubleGene` in the chromosome can hold.
- `maxC` represents the maximum value that each DoubleGene in the chromosome can hold.
- `vertexNumber * 2` represents the number of `DoubleGene` objects that the chromosome will hold.
  

`gtf` is a `Factory` object that is used to generate new `Genotype` objects with the `DoubleChromosome` object created here. 
The `Genotype` class is another class from the Jenetics library, and it represents the genotype of an individual 
in a population. The `Genotyp`e is composed of one or more `Chromosome` objects, in this case it is composed of 
one `DoubleChromosome` object.

### Crossover

- `SinglePointCrossover` chooses a random point on the two parents, splits parents at this crossover point and creates children by exchanging tails
- `MultiPointCrossover` chooses n random crossover points, splits along those points and then glues parts, alternating between parents

We chose `SinglePointCrossover` and `MultiPointCrossover` with 2 and 5 crossover points for the experiments

## Fitness function
 - Firstly, we decode our genotype to get all vertexes. We store them as `List<Point2D>`.
Then using this list we built another List to store edges `List<Line2D>`. 
- Secondly we are comparing every edge with another one.
We increase counter `numberOfIntersections` when our vertexes are different and edges are intersected.
As a result we get `-numberOfIntersections` as final fitness score. The more intersections we have, the less fitness score is.

### Selector

- `TournamentSelector` in tournament selection the best individual from a random sample of s individuals is chosen from the population.
  The samples are drawn with replacement. An individual will win a tournament only if the fitness is greater than the fitness of the other s − 1 competitors.
- `EliteSelector` copies a small proportion of the fittest candidates, without changes, into the next generation
- `RouletteWheelSelector` assigns to each individual a part of the roulette wheel, then spins the wheel n times to select n
  individuals

### Crossover probability

Typically in range (0.6, 0.9), so we chose three values: 0.6, 0.75 and 0.9 for the tests

### Mutation probability

Typically between 1/pop_size and 1/chromosome_length, but in our case we can't determine the chromosome length, because it depends
on the number of vertices. So we arbitrarily chose values: 0.1, 0.01 and 0.001 for the tests

## Results

Checked for 4 different graphs.
Each graph has a different number of vertices and difficulty level for the algorithm.
Two of them were planar, one turan graph and one mixed graph.
Also, for getting such result we used test function with different parameters which we described above.

 - `planar graph 1` - no matter
 - `planar graph 2` - EliteSelector, MultiPointCrossover(5), 0.75, 0.001 Intersections: 0.0 Generations: 5-7 iteration=100 steady_state=5
 - `turan` - EliteSelector, MultiPointCrossover(5), 0.9, 0.1  Intersections: 18.0 Generations: 65-128 iteration=1000 steady_state=50
 - `mixed graph` - EliteSelector, MultiPointCrossover(5), 0.9, 0.1  Intersections: 1.0 Generations: 5-11 iteration=100 steady_state=5


For graphs with a small amount of vertexes it doesn't matter which parameters choose.


The results described above show the outcome of running an algorithm on four different types of graphs. 
The first two graphs are planar, the third is a Turan graph, and the fourth is a mixed graph. 
The algorithm used to solve each of these graphs is different, with varying parameters for selection, crossover, 
mutation rate, and number of generations.

- `planar graph 1` - no matter
- `planar graph 2` - EliteSelector, MultiPointCrossover(5), 0.75, 0.001 Intersections: 0.0 Generations: 5-7 iteration=100 steady_state=5
- `turan` - EliteSelector, MultiPointCrossover(5), 0.9, 0.1  Intersections: 18.0 Generations: 65-128 iteration=1000 steady_state=50
- `mixed graph` - EliteSelector, MultiPointCrossover(5), 0.9, 0.1  Intersections: 1.0 Generations: 5-11 iteration=100 steady_state=5

For the `first planar graph`, no matter which parameters we use.

The `second planar graph` was solved using the `EliteSelector` method, with a `MultiPointCrossover` of `5 random crossover points`,
a `crossover probability of 0.75`, and a `mutation probability of 0.001`. The algorithm found `0.0 intersections`
and took between `5-7 generations` to solve, with an `iteration of 100` and a `steady-state of 5`.

The `Turan` graph was solved using the `EliteSelector` method, with a `MultiPointCrossover` of `5 random crossover points`, 
a `crossover probability of 0.9`, and a `mutation probability of 0.1`. The algorithm found `18.0 intersections` 
and took between `65-128 generations` to solve, with an `iteration of 1000` and a `steady-state of 50`.

The `mixed graph` was solved using the `EliteSelector` method, with a `MultiPointCrossove`r of `5 random crossover points`,
a `crossover probability of 0.9`, and a `mutation probability of 0.1`. The algorithm found `1.0 intersections`
and took between `5-11 generations` to solve, with an `iteration of 100` and a `steady-state of 5`.


