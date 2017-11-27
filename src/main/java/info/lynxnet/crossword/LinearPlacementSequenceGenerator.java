package info.lynxnet.crossword;

import java.util.ArrayList;
import java.util.List;

public class LinearPlacementSequenceGenerator extends AbstractPlacementSequenceGenerator {
    public LinearPlacementSequenceGenerator(int boardSize) {
        super(boardSize);
    }

    @Override
    protected List<Integer> getIndexes() {
        List<Integer> indexes = new ArrayList<>(boardSize);
        for (int i = 0; i < boardSize; i++) {
            indexes.add(i);
        }
        return indexes;
    }
}
