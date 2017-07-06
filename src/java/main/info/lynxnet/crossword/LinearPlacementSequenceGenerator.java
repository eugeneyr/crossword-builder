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
        return new Placement(0, Direction.ACROSS, this.boardSize);
    }

    @Override
    public Placement getNext(Placement current) {
        if (current == null) {
            return getFirst();
        }
        switch (current.getDirection()) {
            case DOWN:
                if (current.getPosition() >= 0 && current.getPosition() < this.boardSize - 1) {
                    return new Placement(current.getPosition() + 1, Direction.ACROSS, this.boardSize);
                }
                break;
            case ACROSS:
                if (current.getPosition() >= 0 && current.getPosition() < this.boardSize) {
                    return new Placement(current.getPosition(), Direction.DOWN, this.boardSize);
                }
                break;
        }
        return null;
    }
}
