package info.lynxnet.crossword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPlacementSequenceGenerator implements PlacementSequenceGenerator {
    private int boardSize;

    public RandomPlacementSequenceGenerator(int boardSize) {
        super();
        this.boardSize = boardSize;
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

    private List<Integer> getIndexes() {
        List<Integer> indexes = new ArrayList<>(boardSize);
        for (int i = 0; i < boardSize; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        return indexes;
    }

    @Override
    public Placement getFirst() {
        List<Integer> rows = getIndexes();
        //List<Integer> cols = getIndexes();

        Placement head = null;
        Placement curr = null;
        for (int i = 0; i < boardSize; i++) {
            for (Direction dir : Direction.values()) {
                int pos = rows.get(i);//dir == Direction.ACROSS ? rows.get(i) : cols.get(i);
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

    private Placement getNext(Placement current) {
        if (current == null) {
            return getFirst();
        }
        switch (current.direction) {
            case DOWN:
                if (current.position >= 0 && current.position < this.boardSize - 1) {
                    return new Placement(current.position + 1, Direction.ACROSS, this.boardSize);
                }
                break;
            case ACROSS:
                if (current.position >= 0 && current.position < this.boardSize) {
                    return new Placement(current.position, Direction.DOWN, this.boardSize);
                }
                break;
        }
        return null;
    }
}
