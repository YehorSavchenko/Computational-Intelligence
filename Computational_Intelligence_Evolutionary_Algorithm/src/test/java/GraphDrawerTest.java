import io.jenetics.*;
import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;

public class GraphDrawerTest {
    //    selector - TournamentSelector, RouletteWheelSelector, EliteSelector
    //    crossover - SinglePointCrossover, MultiPointCrossover(2), MultiPointCrossover(5)
    //    crossover probability - 0.9, 0.75, 0.6
    //    mutation probability - 0.001, 0.01, 0.1
    private final List<Selector<DoubleGene, Integer>> selectors = new ArrayList<>(asList(new TournamentSelector<>(), new RouletteWheelSelector<>(), new EliteSelector<>()));
    private final List<Boolean> crossovers = new ArrayList<>(asList(true, false));
    private final List<Double> crossoverProbabilities = new ArrayList<>(asList(0.9, 0.75, 0.6));
    private final List<Integer> crossoverNumbers = new ArrayList<>(asList(2, 5));
    private final List<Double> mutationProbabilities = new ArrayList<>(asList(0.001, 0.01, 0.1));

    public Map<String, List<Number>> testTypes(String path) throws Exception {
        Map<String, List<Number>> resultsOfTests = new HashMap<>();
        for(Selector<DoubleGene, Integer> selector : selectors) {
            for(Boolean crossover : crossovers) {
                if(crossover) {
                    for(Double crossoverProbability : crossoverProbabilities) {
                        for(Double mutationProbability : mutationProbabilities) {
                            var result = GraphDrawer.executeAndGetResults(path, selector, new SinglePointCrossover<>(crossoverProbability), mutationProbability);
                            resultsOfTests.put(selector.toString() + ", " + "SinglePointCrossover" + ", " + crossoverProbability.toString() + ", " + mutationProbability.toString(), result);
                        }
                    }
                }
                else {
                    for(Double crossoverProbability : crossoverProbabilities) {
                        for(Double mutationProbability : mutationProbabilities) {
                            for( Integer crossoverNumber : crossoverNumbers) {
                                var result = GraphDrawer.executeAndGetResults(path, selector, new MultiPointCrossover<>(crossoverProbability, crossoverNumber), mutationProbability);
                                resultsOfTests.put(selector.toString() + ", " + "MultiPointCrossover" + "(" + crossoverNumber.toString() + ")" + ", " + crossoverProbability.toString() + ", " + mutationProbability.toString(), result);
                            }
                        }
                    }
                }
            }
        }

        return resultsOfTests;
    }

    @Test
    public void test() throws Exception {
        var map = testTypes("./src/main/java/turan.txt");

        // Sort the map by the first value in the list
        List<Map.Entry<String, List<Number>>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparing(e -> (e.getValue().get(1).doubleValue())));

        //Print the sorted map
        for (Map.Entry<String, List<Number>> entry : entries) {
            System.out.println(fixedLengthString(entry.getKey(), 80)+ "| Intersections: " + entry.getValue().get(0) + " Generations: " + entry.getValue().get(1));
        }

    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$-"+length+ "s", string);
    }
}