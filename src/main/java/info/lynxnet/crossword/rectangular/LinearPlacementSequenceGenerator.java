package info.lynxnet.crossword.rectangular;

import java.util.ArrayList;
import java.util.List;

public class LinearPlacementSequenceGenerator extends AbstractPlacementSequenceGenerator {
    public LinearPlacementSequenceGenerator(int boardWidth, int boardHeight) {
        super(boardWidth, boardHeight);
    }

    @Override
    protected List<Integer> getIndexes() {
        int maxDimension = Math.max(boardWidth, boardHeight);
        List<Integer> indexes = new ArrayList<>(maxDimension);
        for (int i = 0; i < maxDimension; i++) {
            indexes.add(i);
        }
        return indexes;
    }
}
