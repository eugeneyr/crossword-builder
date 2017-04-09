package info.lynxnet.crossword;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Metrics {
    public static final long START_TIME = System.currentTimeMillis();
    public static AtomicLong builderInstances = new AtomicLong(0);
    public static AtomicLong knownPuzzles = new AtomicLong(0);
    public static final AtomicBoolean LOGGING_ON = new AtomicBoolean(false);
    public static final AtomicLong permGenCount = new AtomicLong(0);
    public static final AtomicLong permCount = new AtomicLong(0);
    public static final AtomicLong maxPermSetSize = new AtomicLong(0);

    static void addFatalError(String message) {
        System.out.println(message);
    }

    static void log(String message) {
        if (LOGGING_ON.get()) {
            System.out.println(message);
        }
    }

    public static double calculateScore(String[] puzzle, int n, int[] weights, String[] words) {
        int i;
        int j;

        char[][] board = new char[n][n];
        for (i = 0; i < n; i++) {
            if (puzzle[i] == null || puzzle[i].length() != n) {
                addFatalError("Element " + i + " of your return contained invalid number of characters.");
                return 0;
            }
            for (j = 0; j < n; j++) {
                board[i][j] = puzzle[i].charAt(j);
                if ((board[i][j] < 'A' || board[i][j] > 'Z') && board[i][j] != '.') {
                    addFatalError("Character [" + i + "][" + j + "] of your return was invalid.");
                    return 0;
                }
            }
        }

        // score the return
        // 0. validity with respect to uniqueness of words used and words
        // coverage for all sequences of at least 2 letters in row
        int totalLetters = 0;
        boolean[] usedWord = new boolean[words.length];
        Arrays.fill(usedWord, false);
        StringBuffer sb = new StringBuffer();
        for (i = 0; i < n; i++) {
            sb.append(new String(board[i]));
            sb.append(".");
        }
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++)
                sb.append(board[j][i]);
            sb.append(".");
        }
        String[] met = sb.toString().split("[\\.]+");
        for (i = 0; i < met.length; i++) {
            totalLetters += met[i].length();
            if (met[i].length() >= 2) { // check only words of length 2 and
                // more
                j = Arrays.binarySearch(words, met[i]);
                // check whether this is a word
                if (j < 0) {
                    addFatalError("Your crossword contains word \""
                            + met[i] + "\" which is not present in dictionary.");
                    return 0;
                }
                // check whether this word was already used
                if (usedWord[j]) {
                    addFatalError("Your crossword contains word \"" + met[i] + "\" twice or more.");
                    return 0;
                }
                // mark the word as used
                usedWord[j] = true;
            }
        }
        totalLetters /= 2; // each letter was counted twice

        // 0. validity with respect to "each letter must be part of a word"
        // = each letter cell must have a letter cell neighbor
        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++)
                if (board[i][j] != '.'
                        && (i == 0 || board[i - 1][j] == '.')
                        && (i == n - 1 || board[i + 1][j] == '.')
                        && (j == 0 || board[i][j - 1] == '.')
                        && (j == n - 1 || board[i][j + 1] == '.')) {
                    // all neighbors are . or outside of the board
                    addFatalError("Your crossword contains a letter which is not part of any word (at "
                            + i + ", " + j + ").");
                    return 0;
                }

        // 1. board filling score = no of letters / no of cells
        double boardFillingScore = totalLetters * 1.0 / (n * n);
        log("Board filling score = " + boardFillingScore);

        // 2. rows/cols filling score - no of cols with at least 1 char * no
        // of rows with at least 1 char / n*n
        int filledCols = 0, filledRows = 0;
        boolean emptyCol, emptyRow;
        for (i = 0; i < n; i++) {
            emptyCol = emptyRow = true;
            for (j = 0; j < n; j++) {
                if (board[i][j] != '.')
                    emptyRow = false;
                if (board[j][i] != '.')
                    emptyCol = false;
            }
            if (!emptyCol)
                filledCols++;
            if (!emptyRow)
                filledRows++;
        }
        double rcFillingScore = filledCols * filledRows * 1.0 / (n * n);
        log("Rows/columns filling score = " + rcFillingScore);

        // 3. symmetry score
        double symmetryScore = 0.0, nc = 0, cellScore;
        int nEqual;
        for (i = 0; i < (n + 1) / 2; i++)
            for (j = 0; j <= i; j++) {
                nEqual = (board[i][j] == '.' ? 1 : 0)
                        + (board[i][n - j - 1] == '.' ? 1 : 0)
                        + (board[n - i - 1][j] == '.' ? 1 : 0)
                        + (board[n - i - 1][n - j - 1] == '.' ? 1 : 0)
                        + (board[j][i] == '.' ? 1 : 0)
                        + (board[j][n - i - 1] == '.' ? 1 : 0)
                        + (board[n - j - 1][i] == '.' ? 1 : 0)
                        + (board[n - j - 1][n - i - 1] == '.' ? 1 : 0);
                nEqual = Math.max(nEqual, 8 - nEqual);
                cellScore = 0;
                if (nEqual == 8)
                    cellScore = 1;
                if (nEqual == 7)
                    cellScore = 0.5;
                if (nEqual == 6)
                    cellScore = 0.1;
                symmetryScore += cellScore;
                nc++;
                // System.out.println(i+" "+j+" "+nEqual+": "+board[i][j]+board[i][n-j-1]+board[n-i-1][j]+board[n-i-1][n-j-1]+board[j][i]+board[j][n-i-1]+board[n-j-1][i]+board[n-j-1][n-i-1]+" -> "+cellScore);
            }
        symmetryScore /= nc;
        log("Symmetry score = " + symmetryScore);

        // 4. words crossings score - no of letters which are parts of 2
        // words, divided by no of letters overall
        // for each letter, check whether it's part of a vertical word, and
        // part of horizontal word
        double crossingsScore = 0;
        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++)
                if (board[i][j] != '.'
                        && (i > 0 && board[i - 1][j] != '.' || i < n - 1
                        && board[i + 1][j] != '.')
                        && (j > 0 && board[i][j - 1] != '.' || j < n - 1
                        && board[i][j + 1] != '.'))
                    crossingsScore++;
        if (totalLetters > 0)
            crossingsScore /= totalLetters;
        log("Crossings score = " + crossingsScore);

        return (boardFillingScore * weights[0] + rcFillingScore
                * weights[1] + symmetryScore * weights[2] + crossingsScore
                * weights[3])
                / (weights[0] + weights[1] + weights[2] + weights[3]);
    }
}
