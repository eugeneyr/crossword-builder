package info.lynxnet.crossword;

import java.util.*;
import java.util.concurrent.Callable;

public class CrosswordBuilder implements Callable<Void> {
    private int n;
    private BeautifulCrossword context;
    private Placement placement;
    private Board board;

    public CrosswordBuilder(BeautifulCrossword context, Board board, Placement placement) {
        this.n = placement.boardSize;
        this.context = context;
        this.board = board;
        this.placement = placement;
    }

    public int getI() {
        return placement.position;
    }

    public Direction getDirection() {
        return placement.direction;
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

        Collection<Collection<WordPlacement>> permutations = getAllPermutations(
                board, placement.position, placement.direction);

        for (Collection<WordPlacement> perm : permutations) {
            Board newBoard = board.clone();
            for (WordPlacement wp : perm) {
                newBoard.addWordPlacement(wp);
            }
            Placement newPlace = placement.next;
            // - if all placements are exhausted, add B0 to the list of results.
            if (newPlace == null) {
                context.addKnownPuzzle(newBoard);
            } else {
                context.execute(new CrosswordBuilder(context, newBoard, newPlace));
            }
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

        if (Constants.RANK_BY_SCORES) {
            result.sort(new PermutationComparator(board, n, i, direction, context.getWeights()));
        }

        if (result.size() > Constants.MAX_PERM_SET_SIZE) {
            result = result.subList(0, Constants.MAX_PERM_SET_SIZE);
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
        return Objects.equals(placement, that.placement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, board, placement);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CrosswordBuilder{");
        sb.append("n=").append(n);
        sb.append(", i=").append(placement.position);
        sb.append(", direction=").append(placement.direction);
        sb.append(", board=\n").append(String.join("\n", board.asStringArray()));
        sb.append("\n}");
        return sb.toString();
    }
}
