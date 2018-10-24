package info.lynxnet.crossword.rectangular;

import com.google.common.collect.Lists;

import java.util.List;

public class RadiantPlacementSequenceGenerator extends SpiralPlacementSequenceGenerator {
    public RadiantPlacementSequenceGenerator(int boardWidth, int boardHeight) {
        super(boardWidth, boardHeight);
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
    }

    protected List<Integer> getIndexes() {
        List<Integer> indexes = super.getIndexes();
        return Lists.reverse(indexes);
    }
}
