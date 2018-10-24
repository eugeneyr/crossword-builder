package info.lynxnet.crossword.rectangular;

import java.util.ArrayList;
import java.util.List;

public class CrosswordRunner {

    public static void main(String[] args) {
        Settings settings = Settings.configure(args, true);
        BeautifulCrossword bc = BuilderType.getBuilder(settings.getBuilderName());
        System.out.println("Settings to be used: " + settings.toString());

        bc.generateCrossword(settings.getBoardWidth(), settings.getBoardHeight(), settings.getFileName(), settings.getWeights());
        List<Board> puzzles = new ArrayList<>(bc.getBestPuzzles());

        System.out.println("FINAL STATS: " + bc.getState());

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
