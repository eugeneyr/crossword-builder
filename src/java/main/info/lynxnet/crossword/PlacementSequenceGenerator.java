package info.lynxnet.crossword;

public interface PlacementSequenceGenerator {
    int getBoardSize();
    Placement getFirst();
    Placement getNext(Placement current);
}
