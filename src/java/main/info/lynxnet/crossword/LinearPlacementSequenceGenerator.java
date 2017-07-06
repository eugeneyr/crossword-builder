package info.lynxnet.crossword;

public class LinearPlacementSequenceGenerator implements PlacementSequenceGenerator {
    private int boardSize;

    public LinearPlacementSequenceGenerator(int boardSize) {
        super();
        this.boardSize = boardSize;
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

    @Override
    public Placement getFirst() {
        Placement head = new Placement(0, Direction.ACROSS, this.boardSize);
        Placement curr = head;
        do {
            curr.next = getNext(curr);
            curr = curr.next;
        } while (curr != null);
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
