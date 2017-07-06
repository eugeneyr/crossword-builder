package info.lynxnet.crossword;

public class Placement {
    private int position;
    private Direction direction;
    private int boardSize;

    public Placement(int position, Direction direction, int boardSize) {
        this.position = position;
        this.direction = direction;
        this.boardSize = boardSize;
    }

    public int getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getBoardSize() {
        return boardSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Placement placement = (Placement) o;

        if (position != placement.position) return false;
        if (boardSize != placement.boardSize) return false;
        return direction == placement.direction;
    }

    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + boardSize;
        return result;
    }
}
