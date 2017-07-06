package info.lynxnet.crossword;

import java.util.HashMap;
import java.util.Map;

public class PlacementSequenceGeneratorFactory {
    private static Map<Integer, PlacementSequenceGenerator> generators = new HashMap<>();

    public static PlacementSequenceGenerator getGenerator(int boardSize) {
        PlacementSequenceGenerator gen = generators.getOrDefault(boardSize, new LinearPlacementSequenceGenerator(boardSize));
        generators.putIfAbsent(boardSize, gen);
        return gen;
    }
}
