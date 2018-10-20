package info.lynxnet.crossword.krisskross;

import info.lynxnet.crossword.*;

import java.util.*;
import java.util.concurrent.Callable;

public class KrissKrossCrosswordBuilder extends CrosswordBuilder implements Callable<Void> {
    private int n;
    private BeautifulKrissKrossCrossword context;
    private Board board;
    private Stack<WordPlacement> wordPlacementStack = new Stack<>();
    private Set<WordPlacement> usedPositions = new HashSet<>();

    public KrissKrossCrosswordBuilder(BeautifulKrissKrossCrossword context, Board board) {
        super(context, board, null);
        this.n = board.getN();
        this.context = context;
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    /**
     * The idea is similar to Treesome, with some changes:
     * <p>
     * - The "start" word - at the level 0 - along with its position gets marked as "used" once we're done with it.
     * The word selection step does not pick "used" word positions.
     * <p>
     * - We'll try to use a stack data structure instead of recursion in order to save time wasted on cloning in Treesome.
     * <p>
     * The outline of the algorithm:
     * <p>
     * Preparations:
     * * load the list of words
     * * fill "word buckets" - their structiure and behavior are TBD. The goal is to have a data structure
     * efficient at taking the mask of a row, the offset of the "crossing" point, and the two rows before and after it
     * as the input and returns all available words that might be placed on the row while covering the crossing point,
     * plus the "empty" placement - no word at all.
     * <p>
     * Level 0:
     * * pick the "next" starting position [X, Y]. The first one is [0, 0]. The starting direction is always Across.
     * * pick the "next" word in the list. Take care of skipping "used" word/position combinations.
     * There is a special "no word here" combination in the list.
     * <p>
     * Level 1:
     * * Put the word on top of the stack.
     * * Place the word at the position.
     * <p>
     * * For each "crossing point" in the word:
     * * use the word bucket structure to compute the list of possible placements crossing the point
     * * For each item in the list:
     * * Do Level 1.
     * * Remove the word from the stack and from the board.
     * * If the length of the list is 1 (the only item there is the empty marker),
     * compute the weight of the found puzzle and store it if it a current max
     * <p>
     * Back to the Level 1 loop:
     * * add the word/position combo to the "used"
     * * remove the word from the stack and the board
     * <p>
     * Q. How is that different from the "treesome" algorithm?
     * A. It does not try to put a word in all possible positions in the main loop.
     */
    @Override
    public Void call() throws Exception {
//        long myNo = Metrics.builderInstances.incrementAndGet();
//        if (myNo % 10000 == 0) {
//            System.out.printf("Instantiated builders = %d %s\nCurrent Best Score = %.3f\nCurrent Board:\n",
//                    myNo,
//                    context.getState(),
//                    context.getTopScore());
//            context.printBoard(board);
//        }

        Direction dir = Direction.ACROSS;
        /*
         *  * pick the "next" starting position [X, Y]. The first one is [0, 0]. The starting direction is always Across.
         *  * pick the "next" word in the list. Take care of skipping "used" word/position combinations.
                    *    There is a special "no word here" combination in the list.
        */
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                for (String word0 : context.getStore().getWords()) {
                    if (word0.length() + col >= n || usedPositions.contains(new WordPlacement(word0, col, row, dir))) {
                        continue;
                    }
                    WordPlacement wp = new WordPlacement(word0, col, row, dir);
                    usedPositions.add(wp);

                    doWord(board, wp, 1L);
                }
            }
        }

        // The part with "no word here"
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                WordPlacement wp = new WordPlacement(null, col, row, dir);
                if (usedPositions.contains(wp)) {
                    continue;
                }
                // part deux here

                doWord(board, wp, 1L);
            }
        }

        return null;
    }

    void doWord(Board originalBoard, WordPlacement wp, long level) throws Exception {
        /*
         *  * For each "crossing point" in the word:
         *    * use the word bucket structure to compute the list of possible placements crossing the point
         *    * For each item in the list:
         *      * Do Level 1.
         *      * Remove the word from the stack and from the board.
         *    * If the length of the list is 1 (the only item there is the empty marker),
         *      compute the weight for the found puzzle and store it if it a current max
         */
        // TODO a big ?:
        // what do we do with "no word to be put here" placements?
        // For now, just get tf outta here
        if (wp.getWord() == null) {
            return;
        }

        wordPlacementStack.push(wp);
        Board board = originalBoard.clone();
        board.addWordPlacement(wp);

        Direction otherDirection = wp.getDirection() == Direction.ACROSS ? Direction.DOWN : Direction.ACROSS;
        int startOffset = otherDirection == Direction.ACROSS ? wp.getY() : wp.getX();
        int endOffset = startOffset + wp.getWord().length();
        for (int offset = startOffset; offset < endOffset; offset++) {
            char charAtCrossing = wp.getWord().charAt(offset - startOffset);
            Set<String> candidates = context.getStore().getWordsByCharacter(charAtCrossing, board.getWords());
            // for every candidate:
            // find all possible placements using the word
            List<WordPlacement> newPlacements = new ArrayList<>(candidates.size());
            for (String candidate : candidates) {
                int fromPos = 0, nextPos = -1;
                // Look at all occurrences of the character in the candidate, filter out irrelevant ones
                do {
                    nextPos = candidate.indexOf(charAtCrossing, fromPos);
                    if (nextPos >= 0) {
                        fromPos = nextPos + 1;
                        if (otherDirection == Direction.ACROSS) {
                            int x = wp.getX() - nextPos;
                            int y = wp.getY() + offset;

                            if (x >= 0 && x + candidate.length() <= board.getN()) {
                                WordPlacement candidacy = new WordPlacement(candidate, x, y, otherDirection);
                                if (board.canBePlaced(candidacy)) {
                                    newPlacements.add(candidacy);
                                }
                            }
                        } else {
                            int x = wp.getX() + offset;
                            int y = wp.getY() - nextPos;

                            if (y >= 0 && y + candidate.length() <= board.getN()) {
                                WordPlacement candidacy = new WordPlacement(candidate, x, y, otherDirection);
                                if (board.canBePlaced(candidacy)) {
                                    newPlacements.add(candidacy);
                                }
                            }
                        }
                    }
                } while (nextPos >= 0);
            }
            int x = otherDirection == Direction.ACROSS ? wp.getX() : offset;
            int y = otherDirection == Direction.ACROSS ? offset: wp.getY();
            newPlacements.add(new WordPlacement(null, x, y, otherDirection));
            // at this point, we gathered all candidate placements
            // if the length of the list is just 1, the only possible move is to ignore the cell at (x, y)
            if (newPlacements.size() == 1) {
                context.addKnownPuzzle(board);
            }
            // review
            for (WordPlacement candidacy: newPlacements) {
                doWord(board, candidacy, level + 1);
            }
        }

        wordPlacementStack.pop();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KrissKrossCrosswordBuilder that = (KrissKrossCrosswordBuilder) o;
        return Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        int result = n;
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + (board != null ? board.hashCode() : 0);
        return result;
    }
}
