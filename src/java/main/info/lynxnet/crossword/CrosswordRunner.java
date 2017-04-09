package info.lynxnet.crossword;

import java.util.ArrayList;
import java.util.List;

public class CrosswordRunner {

    public static BeautifulCrossword getCrosswordInstance(String arg) {
        if ("parallel".equals(arg)) {
            return new ParallelBeautifulCrossword();
        }
        return new BeautifulCrossword();
    }

    public static void main(String[] args) {
        String fileName = args[0];
        int n = Integer.parseInt(args[1]);
        int[] weights = {
                Integer.parseInt(args[2]),
                Integer.parseInt(args[3]),
                Integer.parseInt(args[4]),
                Integer.parseInt(args[5]),
        };
        BeautifulCrossword bc = getCrosswordInstance(args[6]);
        bc.generateCrossword(n, fileName, weights);
        List<Board> puzzles = new ArrayList<>(bc.getBestPuzzles());

        if (puzzles.size() > 0) {
            Board best = puzzles.get(puzzles.size() - 1);
            System.out.println("Best:");
            bc.printBoard(best);
        } else {
            System.out.println("Not found");
        }
        bc.shutdown();
    }

}
