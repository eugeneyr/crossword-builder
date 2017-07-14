package info.lynxnet.crossword.treesome;

import info.lynxnet.crossword.Direction;
import info.lynxnet.crossword.WordPlacement;

public class ChildPosition extends WordPlacement {
    public boolean missing;

    public ChildPosition(String word, int x, int y, Direction direction, boolean missing) {
        super(word, x, y, direction);
        this.missing = missing;
    }

    public boolean isMissing() {
        return missing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ChildPosition that = (ChildPosition) o;

        return missing == that.missing;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (missing ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChildPosition{" +
                "word='" + word + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", direction=" + direction +
                ", missing=" + missing +
                '}';
    }
}
