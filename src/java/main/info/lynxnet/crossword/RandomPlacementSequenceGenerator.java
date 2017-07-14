package info.lynxnet.crossword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPlacementSequenceGenerator extends AbstractPlacementSequenceGenerator {

    public RandomPlacementSequenceGenerator(int boardSize) {
        super(boardSize);
    }

    protected List<Integer> getIndexes() {
        List<Integer> indexes = new ArrayList<>(boardSize);
        for (int i = 0; i < boardSize; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        return indexes;
    }

}
