package info.lynxnet.crossword;

public interface Constants {
    char EMPTY_CELL_FILLER = '.';
    int MAX_PERM_SET_SIZE = 6;
    boolean RANK_BY_SCORES = true;
    Class<? extends PlacementSequenceGenerator> PLACEMENT_GENERATOR_CLASS = LinearPlacementSequenceGenerator.class;
}
