package info.lynxnet.crossword.rectangular;

import java.util.ArrayList;
import java.util.List;

public class SpiralPlacementSequenceGenerator extends AbstractPlacementSequenceGenerator {
    public SpiralPlacementSequenceGenerator(int boardWidth, int boardHeight) {
        super(boardWidth, boardHeight);
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
    }

    protected List<Integer> getIndexes() {
        int maxDimension = Math.max(boardWidth, boardHeight);
        List<Integer> indexes = new ArrayList<>(maxDimension);
        for (int i = 0; i < maxDimension; i++) {
            indexes.add(i);
        }
        List<Integer> newIndexes = new ArrayList<>(maxDimension);
        while (!indexes.isEmpty()) {
            Integer head = indexes.remove(0);
            newIndexes.add(head);
            if (!indexes.isEmpty()) {
                Integer tail = indexes.remove(indexes.size() - 1);
                newIndexes.add(tail);
            }
        }
        return newIndexes;
    }
}
