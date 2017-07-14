package info.lynxnet.crossword;

public interface Constants {
    char EMPTY_CELL_FILLER = '.';
    int MAX_PERM_SET_SIZE = 8;
    boolean RANK_BY_SCORES = true;
    Class<? extends PlacementSequenceGenerator> PLACEMENT_GENERATOR_CLASS = SpiralPlacementSequenceGenerator.class;
}
