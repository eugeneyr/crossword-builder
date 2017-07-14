package info.lynxnet.crossword;

import java.util.List;

public abstract class AbstractPlacementSequenceGenerator implements PlacementSequenceGenerator {
    protected int boardSize;

    public AbstractPlacementSequenceGenerator(int boardSize) {
        super();
        this.boardSize = boardSize;
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

    protected abstract List<Integer> getIndexes();

    @Override
    public Placement getFirst() {
        List<Integer> indexes = getIndexes();

        Placement head = null;
        Placement curr = null;
        for (int i = 0; i < boardSize; i++) {
            for (Direction dir : Direction.values()) {
                int pos = indexes.get(i);
                Placement p = new Placement(pos, dir, boardSize);
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
