package info.lynxnet.crossword;

public class Placement {
    public int position;
    public Direction direction;
    public int boardSize;
    public Placement next;

    public Placement(int position, Direction direction, int boardSize) {
        this.position = position;
        this.direction = direction;
        this.boardSize = boardSize;
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

    @Override
    public String toString() {
        return "Placement{" +
                "position=" + position +
                ", direction=" + direction +
                ", boardSize=" + boardSize +
                '}';
    }
}
