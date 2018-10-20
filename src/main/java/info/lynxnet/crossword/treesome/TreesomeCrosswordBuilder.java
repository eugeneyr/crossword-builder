package info.lynxnet.crossword.treesome;

import info.lynxnet.crossword.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

public class TreesomeCrosswordBuilder extends CrosswordBuilder implements Callable<Void> {
    private int n;
    private BeautifulTreesomeCrossword context;
    private info.lynxnet.crossword.treesome.PlacementTreeNode placement;
    private Board board;

    public TreesomeCrosswordBuilder(BeautifulTreesomeCrossword context, Board board, PlacementTreeNode placement) {
        super(context, board, null);
        this.n = board.getN();
        this.context = context;
        this.board = board;
        if (placement != null) {
            this.placement = placement;
        } else {
            // create the "root" placement:
            Collection<String> words = context.getStore().getWords();
            if (words.isEmpty()) {
                throw new IllegalStateException("The collection of available words is empty");
            }
            String firstWord = words.iterator().next();
            this.placement = new info.lynxnet.crossword.treesome.PlacementTreeNode(firstWord, null, words, board);
        }
    }

    public Board getBoard() {
        return board;
    }

    /**
     *  Build the tree of possible valid info.lynxnet.crossword boards using the set of all available words and the empty board.
     */
    @Override
    public Void call() throws Exception {
        long myNo = Metrics.builderInstances.incrementAndGet();
        if (myNo % 10000 == 0) {
            System.out.printf("Instantiated builders = %d %s\nCurrent Best Score = %.3f\nMax perm Size: %d\nCurrent Board:\n",
                    myNo,
                    context.getState(),
                    context.getTopScore(),
                    Metrics.maxPermSetSize.get());
            context.printBoard(board);
        }

        if (placement.word == null) {
            if (placement.availableWords.isEmpty()) {
                context.addKnownPuzzle(placement.board);
                return null;
            } else {
                placement.word = placement.availableWords.iterator().next();
                placement.availableWords.remove(placement.word);
            }
        }

        Map<info.lynxnet.crossword.treesome.ChildPosition, info.lynxnet.crossword.treesome.PlacementTreeNode> children = new LinkedHashMap<>();
        // all possible ways to place the current word on the current board
        for (Direction dir : Direction.values()) {
            for (int row = 0; row < n; row++) {
                for (int col = 0; col < n; col++) {
                    Board clonedBoard = board.clone();
                    try {
                        WordPlacement wp = new WordPlacement(placement.word, col, row, dir);
                        Metrics.triedPlacements.incrementAndGet();
                        clonedBoard.addWordPlacement(wp);
                    } catch (IllegalArgumentException iae) {
                        // it's OK, we'll just skip this cell / direction combo
                        Metrics.blockedPlacements.incrementAndGet();
                        continue;
                    }
                    info.lynxnet.crossword.treesome.ChildPosition pos = new info.lynxnet.crossword.treesome.ChildPosition(placement.word, col, row, dir, false);
                    info.lynxnet.crossword.treesome.PlacementTreeNode child = new info.lynxnet.crossword.treesome.PlacementTreeNode(null, null, placement.availableWords, clonedBoard);
                    children.put(pos, child);
                }
            }
        }

        if (placement.children.isEmpty()) {
            context.addKnownPuzzle(placement.board);
        }

        // no placement of the current word
        if (!placement.availableWords.isEmpty()) {
            ChildPosition noWordPos = new ChildPosition(placement.word, -1, -1, null, true);
            PlacementTreeNode child = new info.lynxnet.crossword.treesome.PlacementTreeNode(null, null, placement.availableWords, placement.board);
            children.put(noWordPos, child);
            Metrics.triedPlacements.incrementAndGet();
        }

        for (PlacementTreeNode ptn : children.values()) {
            context.execute(new TreesomeCrosswordBuilder(context, ptn.getBoard(), ptn));
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreesomeCrosswordBuilder that = (TreesomeCrosswordBuilder) o;
        return Objects.equals(placement, that.placement);
    }

    @Override
    public int hashCode() {
        int result = n;
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + (placement != null ? placement.hashCode() : 0);
        result = 31 * result + (board != null ? board.hashCode() : 0);
        return result;
    }
}
