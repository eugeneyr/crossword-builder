package info.lynxnet.crossword.rectangular;

import info.lynxnet.crossword.Direction;
import info.lynxnet.crossword.WordPlacement;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

class PermutationComparator implements Comparator<Collection<WordPlacement>> {
    private Board board;
    private int width;
    private int height;
    private int i;
    private Direction direction;
    private int[] weights;

    public PermutationComparator(Board board, int i, Direction direction, int[] weights) {
        this.board = board;
        this.width = board.width;
        this.height = board.height;
        this.i = i;
        this.direction = direction;
        this.weights = weights;
    }

    @Override
    public int compare(Collection<WordPlacement> o1, Collection<WordPlacement> o2) {
        double score1 = computeScore(o1);
        double score2 = computeScore(o2);
        if (score2 == score1) {
            return 0;
        }
        if (score1 > score2) {
            return -1;
        }
        return 1;
    }

    double computeScore(Collection<WordPlacement> perm) {
//        char[] existingLine = board.getLine(i, direction);
//        char[] permLine = toChars(perm);
//        char[] newLine = superpose(existingLine, permLine);

        int totalLength = perm.stream().map(wp -> wp.getWord().length()).reduce(0, (a, b) -> a + b);

        return totalLength;
        /*
        double avgLength = (double) totalLength / perm.size();

        double newWordsFillScore = (double) totalLength / width;

        double symmetryScore = 0.0;

        for (int j = 0; j < width / 2; j++) {
            if (permLine[j] != Constants.EMPTY_CELL_FILLER && permLine[width - 1 - j] != Constants.EMPTY_CELL_FILLER) {
                symmetryScore += (width / 2 - j) / width;
            }
        }

        symmetryScore /= width * width;

//        for (int j = 0; j < width / 2; j++) {
//            if (permLine[j] != Constants.EMPTY_CELL_FILLER && permLine[width - 1 - j] != Constants.EMPTY_CELL_FILLER) {
//                symmetryScore += 1;
//            }
//        }
//
//        symmetryScore /= width;

        int crossings = 0;

        long filling = new String(newLine).chars().filter(c -> c != Constants.EMPTY_CELL_FILLER).count();
        double fillingScore = filling / width / width;

        for (int j = 0; j < width; j++) {
            crossings += permLine[j] != Constants.EMPTY_CELL_FILLER && existingLine[j] != Constants.EMPTY_CELL_FILLER
                    ? 1 : 0;
        }

        double crossingScore = filling != 0 ? (double) crossings / filling : 0;
        return weights[0] * fillingScore + weights[2] * symmetryScore + weights[3] * crossingScore;
        */
    }

    char[] toChars(Collection<WordPlacement> perm) {
        char[] rv = new char[direction == Direction.ACROSS ? width : height];
        Arrays.fill(rv, Constants.EMPTY_CELL_FILLER);
        for (WordPlacement wp : perm) {
            int baseIdx = wp.getDirection() == Direction.ACROSS ? wp.getX() : wp.getY();
            System.arraycopy(wp.getWord().toCharArray(), 0, rv, baseIdx, wp.getWord().length());
        }
        return rv;
    }

    char[] superpose(char[] a, char[] b) {
        if (a == null || b == null || a.length != b.length) {
            throw new IllegalArgumentException("Garbage in, exception out");
        }
        char[] rv = new char[a.length];
        for (int i = 0; i < a.length; i++) {
            char c = Constants.EMPTY_CELL_FILLER;
            if (a[i] != Constants.EMPTY_CELL_FILLER) {
                c = a[i];
            }
            if (b[i] != Constants.EMPTY_CELL_FILLER) {
                if (c != Constants.EMPTY_CELL_FILLER && c != b[i]) {
                    throw new IllegalStateException(
                            String.format("Lines A and B cannot be superposed at position %d: %s %s",
                                    i, new String(a), new String(b))
                    );
                }
                c = b[i];
            }
            rv[i] = c;
        }
        return rv;
    }

}
