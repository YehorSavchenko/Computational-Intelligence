// Importing necessary classes from the io.jenetics package for genetic algorithm operations
import io.jenetics.*;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.Factory;

// Importing classes from the org.graphstream package for graph manipulation and visualization
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

// Importing classes from the java.awt.geom package for geometry operations
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

// Importing classes for file reading and IO operations
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Importing classes for collections
import java.util.*;

// Importing a static method for setting limits on the evolution process
import static io.jenetics.engine.Limits.bySteadyFitness;

// Class for drawing graph using genetic algorithm
public class GraphDrawer {

    // Declaring variables for storing edges and vertex number
    private static List<Integer> edges;
    private static int vertexNumber;

    // Declaring variable for storing genotype factory
    private static Factory<Genotype<DoubleGene>> gtf;

    // Main method of the class
    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("./src/main/java/turan.txt"))) {
            // Reading vertex number from file
            vertexNumber = Integer.parseInt(br.readLine());
            // Reading edges from file and storing as a list of integers
            edges = Arrays.stream(br.readLine().split(" ")).map(Integer::parseInt).toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creating a new graph
        Graph graph = createGraph(edges);

        // Declaring variables for setting graph visualization parameters
        int minC = 0;
        int maxC = 100;
        int limit = 1000;

        // Setting visualization parameters on the graph
        graph.addAttribute("ui.viewBox", minC + " " + minC + " " + maxC + " " + maxC);

        // Creating a map for storing intermediate results of the evolution process
        Map<String, List<Point2D>> graphs_points = new HashMap<>();

        // Creating a list of steps for storing intermediate results
        List<Long> steps = Arrays.asList(10L, 100L, 300L, 400L, 500L);

        // Initializing the genotype factory
        gtf = Genotype.of(DoubleChromosome.of(minC, maxC, vertexNumber * 2));

        // Building the genetic algorithm engine
        Engine<DoubleGene, Integer> engine = Engine.builder(GraphDrawer::fitnessFunction, gtf)
                .populationSize(100)
                .offspringSelector(new RouletteWheelSelector<>())
                .alterers(
                        // Using Mutator as an alterer with a probability of 0.9
                        new Mutator<>(0.1),

                        // Using SinglePointCrossover as an alterer with a probability of 0.9
                        new SinglePointCrossover<>(0.9)
                )
                .build();

        // Creating an EvolutionStatistics object for tracking statistics of the evolution process
        EvolutionStatistics<Integer, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();

        // Running the evolution process and collecting the best genotype
        Genotype<DoubleGene> result = engine.stream()

                // Stops evolution when steady state is reached
                .limit(bySteadyFitness(50))

                // Stops evolution after a specified number of generations
                .limit(limit)
                .peek(er -> {

                    // Accepting statistics for the current generation
                    statistics.accept(er);
                    System.out.println(statistics);

                    // Storing intermediate results at specified steps
                    if (steps.contains(er.generation())) {
                        graphs_points.put(String.valueOf(er.generation()), decode(er.bestPhenotype().genotype()));
                    }
                })
                .collect(EvolutionResult.toBestGenotype());

        var arrResult = decode(result);

        for (String key : graphs_points.keySet()) {
            Graph graph1 = createGraph(edges);

            // Decoding the final result of the evolution process
            var r = graphs_points.get(key);

            // Looping through the intermediate results and displaying the graphs
            for (int ed = 0; ed < r.size(); ed++) {
                graph1.getNode(String.valueOf(ed + 1)).setAttribute("xy", r.get(ed).getX(), r.get(ed).getY());
            }
            graph1.setAttribute("ui.title", "Graph " + key);
            graph1.display(false);
        }

        // Setting the final result on the graph and displaying it
        for (int ed = 0; ed < arrResult.size(); ed++) {
            graph.getNode(String.valueOf(ed + 1)).setAttribute("xy", arrResult.get(ed).getX(), arrResult.get(ed).getY());
        }

        graph.display(false);
    }

    // Method for creating a new graph from the given edges
    private static Graph createGraph(List<Integer> edges) {
        Graph graph = new SingleGraph("My Graph");
        //CSS FOR FIVE :)
        graph.setAttribute("ui.stylesheet", "node{\n" +
                "    size: 20px, 20px;\n" +
                "    text-color: 'red';\n" +
                "    fill-color: #f7f7f0;\n" +
                "    text-mode: normal; \n" +
                "}");
        graph.setStrict(false);
        graph.setAutoCreate(true);

        Set<Integer> vertexes = new HashSet<>(edges);
        for (Integer vertex : vertexes) {
            graph.addNode(vertex.toString()).setAttribute("ui.label", vertex.toString());
        }

        Iterator<Integer> edge = edges.iterator();
        while (edge.hasNext()) {
            String firstEdge = edge.next().toString();
            String secondEdge = edge.next().toString();
            graph.addEdge(firstEdge + secondEdge, firstEdge, secondEdge);
        }

        return graph;
    }

    // Method for calculating the fitness function
    private static int fitnessFunction(final Genotype<DoubleGene> gt) {

        // Decoding the genotype
        List<Point2D> vertexes = decode(gt);

        // Initializing the number of intersections
        int numberOfIntersections = 0;

        // Creating a list of line segments based on the edges and vertex coordinates
        List<Line2D> lines = new ArrayList<>();
        for (int i = 0; i < edges.size(); i = i + 2) {
            lines.add(new Line2D.Double(vertexes.get(edges.get(i) - 1), vertexes.get(edges.get(i + 1) - 1)));
        }

        // Looping through the line segments and checking for intersections
        for (int i = 0; i < lines.size(); i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                var line1 = lines.get(i);
                var line2 = lines.get(j);
                if (!arePointsEqual(line1.getP1(), line1.getP2(), line2.getP1(), line2.getP2()) && line1.intersectsLine(line2)) {
                    numberOfIntersections++;
                }
            }
        }

        // Returning the negative number of intersections as the fitness value
        return -numberOfIntersections;
    }

    private static boolean arePointsEqual(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        return p1.equals(p3) || p1.equals(p4) || p2.equals(p3) || p2.equals(p4);
    }

    // Method for decoding the genotype
    private static List<Point2D> decode(final Genotype<DoubleGene> gt) {
        var list = gt.chromosome().stream().map(DoubleGene::doubleValue).toList();
        List<Point2D> listOfPoints = new ArrayList<>();

        for (int i = 0; i < list.size(); i += 2) {
            Point2D point = new Point2D.Double(list.get(i), list.get(i + 1));
            listOfPoints.add(point);
        }

        return listOfPoints;
    }

    // Method for executing a genetic algorithm on a graph drawing problem and returning the results for tests
    public static List<Number> executeAndGetResults(String path, Selector<DoubleGene, Integer> selector, MultiPointCrossover<DoubleGene, Integer> crossover, double mutationProbability) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            // Reading vertex number from file
            vertexNumber = Integer.parseInt(br.readLine());

            // Reading edges from file and storing as a list of integers
            edges = Arrays.stream(br.readLine().split(" ")).map(Integer::parseInt).toList();
        }

        // Setting the minimum and maximum values for the coordinates
        int minC = 0;
        int maxC = 100;

        // Initializing the genotype factory
        gtf = Genotype.of(DoubleChromosome.of(minC, maxC, vertexNumber * 2));

        // Building the genetic algorithm engine with the given selector, crossover and mutation probability
        Engine<DoubleGene, Integer> engine = Engine.builder(GraphDrawer::fitnessFunction, gtf)
                .offspringSelector(selector)
                .alterers(
                        new Mutator<>(mutationProbability),
                        crossover)
                .build();

        // Creating an EvolutionStatistics object for tracking statistics of the evolution process
        EvolutionStatistics<Integer, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();

        // Running the evolution process and collecting the best genotype
        engine.stream()

                // Stops evolution when steady state is reached
                .limit(bySteadyFitness(50))

                // Stops evolution after a specified number of generations
                .limit(1000)

                // Accepting statistics for the current generation
                .peek(statistics)
                .collect(EvolutionResult.toBestGenotype());

        // Returning a string containing the maximum fitness and the number of generations
        return Arrays.asList(Math.abs(statistics.fitness().max()), statistics.altered().count());
    }
}