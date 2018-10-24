package info.lynxnet.crossword.rectangular;

import info.lynxnet.crossword.Placement;

public interface PlacementSequenceGenerator {
    int getBoardWidth();
    int getBoardHeight();
    Placement getFirst();
}
