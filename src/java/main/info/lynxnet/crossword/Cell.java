package info.lynxnet.crossword;

public class Cell implements Cloneable {
    private int x;
    private int y;
    private char letter = Constants.EMPTY_CELL_FILLER;
    private WordPlacement acrossWord;
    private WordPlacement downWord;

    public Cell(int x, int y, WordPlacement acrossWord, WordPlacement downWord) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException(String.format("Incorrect coordinates: %d, %d", x, y));
        }
        this.x = x;
        this.y = y;
        this.acrossWord = acrossWord;
        this.downWord = downWord;

        if (acrossWord != null) {
            if (acrossWord.getY() != y) {
                throw new IllegalArgumentException(
                        String.format(
                                "The y coordinate of the Accross word does not match: %d, %d", y, acrossWord.getY()));
            }
            if (acrossWord.getX() > x) {
                throw new IllegalArgumentException(
                        String.format(
                                "The Accross word lays after the cell: %d, %d", x, acrossWord.getX()));
            }
            if (acrossWord.getWord().length() + acrossWord.getX() <= x) {
                throw new IllegalArgumentException(
                        String.format(
                                "The Accross word does not reach the cell: %d, %d", x, acrossWord.getX()));
            }
            this.letter = acrossWord.getWord().charAt(x - acrossWord.getX());
        }

        if (downWord != null) {
            if (downWord.getX() != x) {
                throw new IllegalArgumentException(
                        String.format(
                                "The x coordinate of the Down word does not match: %d, %d", x, downWord.getY()));
            }
            if (downWord.getY() > y) {
                throw new IllegalArgumentException(
                        String.format(
                                "The Down word lays after the cell: %d, %d", y, downWord.getY()));
            }
            if (downWord.getWord().length() + downWord.getY() <= y) {
                throw new IllegalArgumentException(
                        String.format(
                                "The Down word does not reach the cell: %d, %d", y, downWord.getY()));
            }
            char letter = downWord.getWord().charAt(y - downWord.getY());
            if (this.letter != Constants.EMPTY_CELL_FILLER && letter != this.letter) {
                throw new IllegalArgumentException(
                        String.format(
                                "The letter in the Down word does not match the letter in the Across word:: %c, %c",
                                this.letter, letter)
                );
            }
            if (this.letter == Constants.EMPTY_CELL_FILLER) {
                this.letter = letter;
            }
        }
    }

    public Cell(int x, int y) throws PlacementException {
        if (x < 0 || y < 0) {
            throw new PlacementException(String.format("Incorrect coordinates: %d, %d", x, y));
        }
        this.x = x;
        this.y = y;
    }

    public Cell() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public char getLetter() {
        return letter;
    }

    public boolean isEmpty() {
        return acrossWord == null && downWord == null;
    }

    public WordPlacement getAcrossWord() {
        return acrossWord;
    }

    public void setAcrossWord(WordPlacement acrossWord) {
        if (acrossWord != null) {
            if (acrossWord.getY() != y) {
                throw new PlacementException(
                        String.format(
                                "The y coordinate of the Accross word does not match: %d, %d", y, acrossWord.getY()));
            }
            if (acrossWord.getX() > x) {
                throw new PlacementException(
                        String.format(
                                "The Accross word lays after the cell: %d, %d", x, acrossWord.getX()));
            }
            if (acrossWord.getWord().length() + acrossWord.getX() <= x) {
                throw new PlacementException(
                        String.format(
                                "The Accross word does not reach the cell: %d, %d", x, acrossWord.getX()));
            }
            char letter = acrossWord.getWord().charAt(x - acrossWord.getX());
            if (this.letter != Constants.EMPTY_CELL_FILLER && letter != this.letter) {
                throw new PlacementException(
                        String.format(
                                "The letter in the new Across word does not match the letter in the Down word:: %c, %c",
                                this.letter, letter));
            }
            if (this.letter == Constants.EMPTY_CELL_FILLER) {
                this.letter = letter;
            }

            this.letter = acrossWord.getWord().charAt(x - acrossWord.getX());
        }

        this.acrossWord = acrossWord;

        if (this.downWord == null && this.acrossWord == null) {
            this.letter = Constants.EMPTY_CELL_FILLER;
        }
    }

    public WordPlacement getDownWord() {
        return downWord;
    }

    public void setDownWord(WordPlacement downWord) {
        if (downWord != null) {
            if (downWord.getX() != x) {
                throw new PlacementException(
                        String.format(
                                "The x coordinate of the Down word does not match: %d, %d", x, downWord.getY()));
            }
            if (downWord.getY() > y) {
                throw new PlacementException(
                        String.format(
                                "The Down word lays after the cell: %d, %d", y, downWord.getY()));
            }
            if (downWord.getWord().length() + downWord.getY() <= y) {
                throw new PlacementException(
                        String.format(
                                "The Down word does not reach the cell: %d, %d", y, downWord.getY()));
            }
            char letter = downWord.getWord().charAt(y - downWord.getY());
            if (this.letter != Constants.EMPTY_CELL_FILLER && letter != this.letter) {
                throw new PlacementException(
                        String.format(
                                "The letter in the new Down word does not match the letter in the Across word:: %c, %c",
                                this.letter, letter));
            }
            if (this.letter == Constants.EMPTY_CELL_FILLER) {
                this.letter = letter;
            }
        }

        this.downWord = downWord;

        if (this.downWord == null && this.acrossWord == null) {
            this.letter = Constants.EMPTY_CELL_FILLER;
        }
    }

    public WordPlacement getWord(Direction direction) {
        switch (direction) {
            case ACROSS:
                return this.acrossWord;
            case DOWN:
                return this.downWord;
        }
        throw new IllegalArgumentException(
                String.format(
                        "Direction not supported: %s", direction == null ? "" : direction.toString()));
    }

    public void setWord(WordPlacement wordPlacement) {
        if (wordPlacement == null) {
            throw new IllegalArgumentException("Word placement is null");
        }
        Direction direction = wordPlacement.getDirection();
        switch (direction) {
            case ACROSS:
                setAcrossWord(wordPlacement);
                break;
            case DOWN:
                setDownWord(wordPlacement);
                break;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cell{");
        sb.append("x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", letter=").append(letter);
        sb.append(", acrossWord=").append(acrossWord);
        sb.append(", downWord=").append(downWord);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Cell clone() throws CloneNotSupportedException {
        return (Cell) super.clone();
    }
}
