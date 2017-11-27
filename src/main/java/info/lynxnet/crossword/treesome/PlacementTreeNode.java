package info.lynxnet.crossword.treesome;

import info.lynxnet.crossword.Board;

import java.util.*;

public class PlacementTreeNode {
//    protected PlacementTreeNode parent;
    protected String word;
    protected boolean valid;
    protected Board board;
    protected Set<String> availableWords = new LinkedHashSet<>();
    protected Map<ChildPosition, PlacementTreeNode> children = new LinkedHashMap<>();

//    public PlacementTreeNode getParent() {
//        return parent;
//    }
//
//    public void setParent(PlacementTreeNode parent) {
//        this.parent = parent;
//    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Set<String> getAvailableWords() {
        return availableWords;
    }

    public PlacementTreeNode(String word, PlacementTreeNode parent,
                             Collection<String> availableWords, Board board) {
        this.word = word;
        if (board != null) {
            this.board = board;
        } else if (parent != null) {
            try {
                this.board = parent.getBoard().clone();
            } catch (CloneNotSupportedException cex) {
                cex.printStackTrace(System.err);
            }
        }
        if (board == null) {
            throw new IllegalArgumentException("No board defined");
        }
        this.availableWords.addAll(availableWords);
        if (word != null) {
            this.availableWords.remove(word);
        }
    }
}
