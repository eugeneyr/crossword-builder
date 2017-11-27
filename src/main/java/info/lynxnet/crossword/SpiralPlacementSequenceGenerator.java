package info.lynxnet.crossword;

import java.util.ArrayList;
import java.util.List;

public class SpiralPlacementSequenceGenerator extends AbstractPlacementSequenceGenerator {
    public SpiralPlacementSequenceGenerator(int boardSize) {
        super(boardSize);
        this.boardSize = boardSize;
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

    protected List<Integer> getIndexes() {
        List<Integer> indexes = new ArrayList<>(boardSize);
        for (int i = 0; i < boardSize; i++) {
            indexes.add(i);
        }
        List<Integer> newIndexes = new ArrayList<>(boardSize);
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
