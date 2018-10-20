package info.lynxnet.crossword;

public interface Constants {
    char EMPTY_CELL_FILLER = '.';
    int MAX_PERM_SET_SIZE = 5;
    Class<? extends PlacementSequenceGenerator> PLACEMENT_GENERATOR_CLASS = LinearPlacementSequenceGenerator.class;
    int[] DEFAULT_WEIGHTS = {6, 8, 7, 10};
    String DEFAULT_FILE_NAME = "data/crossword/topcoder.txt";
    int DEFAULT_BOARD_SIZE = 11;
    BuilderType DEFAULT_BUILDER_TYPE = BuilderType.DEFAULT;
}
