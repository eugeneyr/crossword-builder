package info.lynxnet.crossword;

import com.google.common.collect.Lists;

import java.util.List;

public class RadiantPlacementSequenceGenerator extends SpiralPlacementSequenceGenerator {
    public RadiantPlacementSequenceGenerator(int boardSize) {
        super(boardSize);
        this.boardSize = boardSize;
    }

    protected List<Integer> getIndexes() {
        List<Integer> indexes = super.getIndexes();
        return Lists.reverse(indexes);
    }
}
