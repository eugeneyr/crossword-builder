package info.lynxnet.crossword.rectangular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPlacementSequenceGenerator extends AbstractPlacementSequenceGenerator {

    public RandomPlacementSequenceGenerator(int boardWidth, int boardHeight) {
        super(boardWidth, boardHeight);
    }

    protected List<Integer> getIndexes() {
        int maxDimension = Math.max(boardWidth, boardHeight);
        List<Integer> indexes = new ArrayList<>(maxDimension);
        for (int i = 0; i < maxDimension; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        return indexes;
    }

}
