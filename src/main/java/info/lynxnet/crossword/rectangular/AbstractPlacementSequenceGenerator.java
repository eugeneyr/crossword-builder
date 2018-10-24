package info.lynxnet.crossword.rectangular;

import info.lynxnet.crossword.Direction;
import info.lynxnet.crossword.Placement;

import java.util.List;

public abstract class AbstractPlacementSequenceGenerator implements PlacementSequenceGenerator {
    protected int boardWidth;
    protected int boardHeight;

    public AbstractPlacementSequenceGenerator(int boardWidth, int boardHeight) {
        super();
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
    }

    protected abstract List<Integer> getIndexes();

    @Override
    public int getBoardWidth() {
        return boardWidth;
    }

    @Override
    public int getBoardHeight() {
        return boardHeight;
    }

    @Override
    public Placement getFirst() {
        List<Integer> indexes = getIndexes();

        Placement head = null;
        Placement curr = null;
        for (Direction dir : Direction.values()) {
            for (int i = 0; i < (dir == Direction.ACROSS ? boardWidth : boardHeight); i++) {
                int pos = indexes.get(i);
                Placement p = new Placement(pos, dir, (dir == Direction.ACROSS ? boardWidth : boardHeight));
                if (head == null) {
                    head = p;
                    curr = p;
                } else {
                    curr.next = p;
                    curr = p;
                }
            }
        }
        return head;
    }
}
