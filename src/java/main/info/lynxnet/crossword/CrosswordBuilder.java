package info.lynxnet.crossword;

import java.util.*;
import java.util.concurrent.Callable;

public class CrosswordBuilder implements Callable<Void> {
    private int n;
    private BeautifulCrossword context;
    private int i;
    private Board board;
    private Direction direction;

    public static final int MAX_PERM_SET_SIZE = 9;
    public static final boolean RANK_BY_SCORES = true;

    public CrosswordBuilder(BeautifulCrossword context, Board board, int n, int i, Direction direction) {
        this.n = n;
        this.context = context;
        this.i = i;
        this.board = board;
        this.direction = direction;
    }

    public int getN() {
        return n;
    }

    public int getI() {
        return i;
    }

    public Direction getDirection() {
        return direction;
    }

    public Board getBoard() {
        return board;
    }

    /*
         The outline of the algorithm to implement:

         * The movement / sweep direction is along the main diagonal, from (0, 0) to (N - 1, N - 1)

         * The prerequisites for each step:
           - the "current" board B0
           - the value of I

         * The "action":
           - if I = N, add B0 to the list of results.

           - generate all possible ways to place words not used in B1 on the Ith row of B0.
             The outcome is a collection (list) WPaccr of WordPlacement lists.
             (One of the elements will be an empty list)

           - For each element (WordPlacement list) WPj in WPaccr:
             - create a new board B1 by placing WPj on B0
             - generate all possible ways to place words not used in B1 on the Ith column of B1.
               The outcome is a collection (list) WPdown of WordPlacement lists.
               (One of the elements will be an empty list)

               - For each element WPk in WPdown:
                 - create a new board B2 by placing WPk on B1 and launch a new instance of the builder with B2, I + 1

         * Postprocessing:
             - range the result list by the score.
             - pick the top score one.
             + Variation: store only the result with the highest score.

         For first few rows the number of elements in WPaccr and WPdown
         */
    @Override
    public Void call() throws Exception {
        long myNo = Metrics.builderInstances.incrementAndGet();
        if (myNo % 1000000 == 0) {
            double avgPermLength = 0.0;
            long perms = Metrics.permCount.get();
            long permGens = Metrics.permGenCount.get();
            if (permGens > 0) {
                avgPermLength = (double) perms / permGens;
            }
            System.out.printf("Instantiated builders = %d %s\nCurrent Best Score = %.3f\n" +
                            "Avg Perm Length = %.3f\nMax Seen Perm Size Length=%d\nCurrent Board:\n",
                    myNo,
                    context.getState(),
                    context.getTopScore(),
                    avgPermLength,
                    Metrics.maxPermSetSize.get());
            context.printBoard(board);
        }
        // - if I = N, add B0 to the list of results.
        if (i >= n) {
            context.addKnownPuzzle(board);
            return null;
        }

        Collection<Collection<WordPlacement>> permutations = getAllPermutations(board, i, direction);

        for (Collection<WordPlacement> perm : permutations) {
            Board newBoard = board.clone();
            for (WordPlacement wp : perm) {
                newBoard.addWordPlacement(wp);
            }
            CrosswordBuilder newBuilder = null;
            switch (direction) {
                case ACROSS:
                    newBuilder = new CrosswordBuilder(context, newBoard, n, i, Direction.DOWN);
                    break;
                case DOWN:
                    newBuilder = new CrosswordBuilder(context, newBoard, n, i + 1, Direction.ACROSS);
                    break;
            }
            context.execute(newBuilder);
        }
        return null;
    }

    public Collection<Collection<WordPlacement>> getAllPermutations(Board board, int i, Direction direction) {
        List<Collection<WordPlacement>> result = new ArrayList<>();

        int x = direction == Direction.ACROSS ? 0 : i;
        int y = direction == Direction.ACROSS ? i : 0;

        generatePermutations(board, x, y, direction, Collections.EMPTY_SET, result, board.getWords());
        Metrics.permGenCount.incrementAndGet();
        Metrics.permCount.addAndGet(result.size());
        long maxSize = Metrics.maxPermSetSize.get();

        if (maxSize < result.size()) {
            Metrics.maxPermSetSize.compareAndSet(maxSize, result.size());
        }

        if (RANK_BY_SCORES) {
            result.sort(new PermutationComparator(board, n, i, direction, context.getWeights()));
        }

        if (result.size() > MAX_PERM_SET_SIZE) {
            result = result.subList(0, MAX_PERM_SET_SIZE);
        }

        return result;
    }

    private void generatePermutations(
            Board board, int x, int y, Direction direction, Collection<WordPlacement> perm,
            Collection<Collection<WordPlacement>> result,
            Collection<String> blacklist) {
        if (x >= n || y >= n) {
            result.add(perm);
            return;
        }
        Map<String, Collection<String>> candidates = getWordPlacementCandidates(board, x, y, direction, blacklist);
        for (Map.Entry<String, Collection<String>> entry : candidates.entrySet()) {
            Collection<String> words = entry.getValue();
            for (String word : words) {
                WordPlacement newPlacement = new WordPlacement(word, x, y, direction);
                Set<WordPlacement> newPerm = new HashSet<>(perm);
                newPerm.add(newPlacement);
                Set<String> newBlackList = new HashSet<>(blacklist);
                newBlackList.add(word);
                int newX = direction == Direction.ACROSS ? x + word.length() + 1 : x;
                int newY = direction == Direction.DOWN ? y + word.length() + 1 : y;
                generatePermutations(board, newX, newY, direction, newPerm, result, newBlackList);
            }
        }
        int newX = direction == Direction.ACROSS ? x + 1 : x;
        int newY = direction == Direction.DOWN ? y + 1 : y;
        generatePermutations(board, newX, newY, direction, perm, result, blacklist);
    }

    private Map<String, Collection<String>> getWordPlacementCandidates(
            Board board, int i, int j, Direction direction, Collection<String> blacklist) {
        Collection<String> patterns = board.getAvailablePatterns(i, j, direction);
        Map<String, Collection<String>> candidates = new HashMap<>();
        if (blacklist == null) {
            blacklist = board.getWords();
        }
        for (String pattern : patterns) {
            Set<String> words = context.getStore().getWordsByPattern(pattern, blacklist);
            if (!words.isEmpty()) {
                candidates.put(pattern, context.getStore().getWordsByPattern(pattern, blacklist));
            }
        }
        return candidates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrosswordBuilder that = (CrosswordBuilder) o;
        return n == that.n &&
                i == that.i &&
                Objects.equals(board, that.board) &&
                direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, i, board, direction);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CrosswordBuilder{");
        sb.append("n=").append(n);
        sb.append(", i=").append(i);
        sb.append(", direction=").append(direction);
        sb.append(", board=\n").append(String.join("\n", board.asStringArray()));
        sb.append("\n}");
        return sb.toString();
    }
}
