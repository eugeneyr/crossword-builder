package info.lynxnet.crossword.rectangular;

import java.util.*;

import info.lynxnet.crossword.Cell;
import info.lynxnet.crossword.Direction;
import info.lynxnet.crossword.WordPlacement;

public class Board implements Cloneable {
    protected int width;
    protected int height;
    protected Map<Direction, Collection<WordPlacement>> wordPlacements;
    protected Collection<String> words;
    protected Map<Direction, Collection<String>> wordsByDirection;
    protected Cell[][] grid;


    public Map<Direction, Collection<WordPlacement>> getWordPlacements() {
        return wordPlacements;
    }

    public Collection<String> getWords() {
        return words;
    }

    public Map<Direction, Collection<String>> getWordsByDirection() {
        return wordsByDirection;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public Board(int width, int height) {
        this.width = width;
        wordPlacements = new HashMap<>();
        wordPlacements.put(Direction.ACROSS, new HashSet<>());
        wordPlacements.put(Direction.DOWN, new HashSet<>());
        wordsByDirection = new HashMap<>();
        wordsByDirection.put(Direction.ACROSS, new HashSet<>());
        wordsByDirection.put(Direction.DOWN, new HashSet<>());
        words = new HashSet<>();
        grid = new Cell[height][];
        for (int i = 0; i < height; i++) {
            grid[i] = new Cell[width];
            for (int j = 0; j < width; j++) {
                grid[i][j] = new Cell(j, i, null, null);
            }
        }
    }

    public void addWordPlacement(WordPlacement wp) {
        if (wp == null) {
            throw new IllegalArgumentException("Word placement is null");
        }
        if (wp.getX() < 0 || wp.getY() < 0 || wp.getX() >= width || wp.getY() >= height) {
            throw new IllegalArgumentException(
                    String.format("Word placement start is out of bounds: %d, %d", wp.getX(), wp.getY())
            );
        }
        String word = wp.getWord();
        if (word != null && words.contains(word)) {
            throw new IllegalArgumentException(String.format("Word already placed on the board: %s", word));
        }
        Direction dir = wp.getDirection();
        if (dir == null) {
            throw new IllegalArgumentException("Direction is null");
        }
        if (word != null) {
            int len = word.length();
            switch (dir) {
                case ACROSS:
                    int xAfter = wp.getX() + len;
                    if (xAfter > width) {
                        throw new IllegalArgumentException(
                                String.format("Word too long to fit on the board: %d, %d", wp.getX(), len)
                        );
                    }
                    Cell[] row = grid[wp.getY()];
                    // boundary conditions
                    if (wp.getX() > 0) {
                        Cell prevCell = row[wp.getX() - 1];
                        if (!prevCell.isEmpty()) {
                            throw new IllegalArgumentException(
                                    String.format("There is a letter in the preceding cell: %c", prevCell.getLetter())
                            );
                        }
                    }

                    // the cell immediately after the word should be empty!
                    if (xAfter < width) {
                        Cell afterCell = row[xAfter];
                        if (!afterCell.isEmpty()) {
                            throw new IllegalArgumentException(
                                    String.format("There is a letter in the following cell: %c", afterCell.getLetter())
                            );
                        }
                    }
                    // for each cell check if we can place the word
                    for (int x = wp.getX(); x < xAfter; x++) {
                        Cell cell = row[x];
                        // 1) If the cell is initialized and has a letter in it:
                        if (!cell.isEmpty()) {
                            //  - check if there are no other words going in the same direction associated with it
                            if (cell.getWord(wp.getDirection()) != null) {
                                throw new IllegalArgumentException(
                                        String.format("The cell already has a word in the same direction: %d, %d", x, wp.getY())
                                );
                            }
                            //  - check if the word has the same letter at this position as the cell
                            if (cell.getLetter() != word.charAt(x - wp.getX())) {
                                throw new IllegalArgumentException(
                                        String.format("The cell value differs from the one in the word: %c, %c",
                                                cell.getLetter(), word.charAt(x - wp.getX()))
                                );
                            }
                        } else {
                            // 2) If the cell is not initialized or empty,
                            //    check the cells directly above in and below it (if the rows are there)
                            //    Both should be empty.
                            if (wp.getY() > 0) {
                                // check the cell above
                                Cell cellAbove = grid[wp.getY() - 1][x];
                                if (!cellAbove.isEmpty()) {
                                    throw new IllegalArgumentException(
                                            String.format("The cell above an empty cell is not empty: %c",
                                                    cellAbove.getLetter()
                                            )
                                    );
                                }
                            }
                            if (wp.getY() < height - 1) {
                                // check the cell below
                                Cell cellBelow = grid[wp.getY() + 1][x];
                                if (!cellBelow.isEmpty()) {
                                    throw new IllegalArgumentException(
                                            String.format("The cell below an empty cell is not empty: %c",
                                                    cellBelow.getLetter()
                                            )
                                    );
                                }
                            }
                        }
                    }

                    // all checks are passed, set the cell values
                    for (int x = wp.getX(); x < xAfter; x++) {
                        Cell cell = row[x];
                        cell.setWord(wp);
                    }
                    break;
                case DOWN:
                    int yAfter = wp.getY() + len;
                    if (yAfter > height) {
                        throw new IllegalArgumentException(
                                String.format("Word too long to fit on the board: %d, %d", wp.getY(), len)
                        );
                    }
                    // boundary conditions
                    if (wp.getY() > 0) {
                        Cell prevCell = grid[wp.getY() - 1][wp.getX()];
                        if (!prevCell.isEmpty()) {
                            throw new IllegalArgumentException(
                                    String.format("There is a letter in the preceding cell: %c", prevCell.getLetter())
                            );
                        }
                    }
                    if (yAfter < height) {
                        Cell afterCell = grid[yAfter][wp.getX()];
                        if (!afterCell.isEmpty()) {
                            throw new IllegalArgumentException(
                                    String.format("There is a letter in the following cell: %c", afterCell.getLetter())
                            );
                        }
                    }
                    // for each cell check if we can place the word
                    for (int y = wp.getY(); y < yAfter; y++) {
                        Cell cell = grid[y][wp.getX()];
                        // 1) If the cell is initialized and has a letter in it:
                        if (!cell.isEmpty()) {
                            //  - check if there are no other words going in the same direction associated with it
                            if (cell.getWord(wp.getDirection()) != null) {
                                throw new IllegalArgumentException(
                                        String.format("The cell already has a word in the same direction: %d, %d", y, wp.getY())
                                );
                            }
                            //  - check if the word has the same letter at this position as the cell
                            if (cell.getLetter() != word.charAt(y - wp.getY())) {
                                throw new IllegalArgumentException(
                                        String.format("The cell value differs from the one in the word: %c, %c",
                                                cell.getLetter(), word.charAt(y - wp.getY()))
                                );
                            }
                        } else {
                            // 2) If the cell is not initialized or empty,
                            //    check the cells directly above in and below it (if the rows are there)
                            //    Both should be empty.
                            if (wp.getX() > 0) {
                                // check the cell to the left
                                Cell cellToTheLeft = grid[y][wp.getX() - 1];
                                if (!cellToTheLeft.isEmpty()) {
                                    throw new IllegalArgumentException(
                                            String.format("The cell to the left of an empty cell is not empty: %c",
                                                    cellToTheLeft.getLetter()
                                            )
                                    );
                                }
                            }
                            if (wp.getX() < width - 1) {
                                // check the cell to the right
                                Cell cellToTheRight = grid[y][wp.getX() + 1];
                                if (!cellToTheRight.isEmpty()) {
                                    throw new IllegalArgumentException(
                                            String.format("The cell to the right an empty cell is not empty: %c",
                                                    cellToTheRight.getLetter()
                                            )
                                    );
                                }
                            }
                        }
                    }
                    // all checks are passed, set the cell values
                    for (int y = wp.getY(); y < yAfter; y++) {
                        Cell cell = grid[y][wp.getX()];
                        cell.setWord(wp);
                    }
                    break;
            }
            // common operations for word addition
            words.add(word);
            wordsByDirection.get(wp.getDirection()).add(word);
        }
        wordPlacements.get(wp.getDirection()).add(wp);
    }

    public Collection<String> getAvailablePatterns(int x, int y, Direction dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Direction is null");
        }
        Set<String> result = new LinkedHashSet<>();
        // the starting cell is out of bounds
        if (x < 0 || y < 0 || x > width - 1 || y > height - 1) {
            return result;
        }
        StringBuilder currPattern = new StringBuilder();

        switch (dir) {
            case ACROSS:
                int currX = x;
                // - is there a letter cell immediately preceding the first one?
                // (only need to check it once)

                if (currX > 0) {
                    Cell prevCell = grid[y][currX - 1];
                    if (!prevCell.isEmpty()) {
                        break;
                    }
                }

                for (; currX < width; currX++) {
                    Cell cell = grid[y][currX];
                    // is there a word in the same direction already occupying this cell?
                    if (cell.getWord(dir) != null) {
                        break;
                    }
                    // if the cell is empty, are there letters in its immediate neighbors in the orthogonal direction?
                    if (cell.isEmpty()) {
                        if (y > 0) {
                            Cell adjacent = grid[y - 1][currX];
                            if (!adjacent.isEmpty()) {
                                break;
                            }
                        }
                        if (y < height - 1) {
                            Cell adjacent = grid[y + 1][currX];
                            if (!adjacent.isEmpty()) {
                                break;
                            }
                        }
                    }

                    if (currX < width - 1) {
                        // is there a word in the same direction that begins in the cell immediately following this one?
                        Cell nextCell = grid[y][currX + 1];
                        if (nextCell.getWord(dir) != null) {
                            break;
                        }

                        if (!nextCell.isEmpty()) {
                            // possible branching:
                            // a candidate can stop at the previous cell or at the next one, but not at the current one

                            if (currPattern.length() > 0) {
                                currPattern.append(cell.getLetter());
                                continue;
                            }
                        }
                    }
                    // none of the checks failed, we can expand the current pattern and add it it the resulting set
                    currPattern.append(cell.getLetter());
                    result.add(currPattern.toString());
                }
                break;
            case DOWN:
                int currY = y;
                // - is there a letter cell immediately preceding the first one?
                // (only need to check it once)

                if (currY > 0) {
                    Cell prevCell = grid[currY - 1][x];
                    if (!prevCell.isEmpty()) {
                        break;
                    }
                }

                for (; currY < height; currY++) {
                    Cell cell = grid[currY][x];
                    // is there a word in the same direction already occupying this cell?
                    if (cell.getWord(dir) != null) {
                        break;
                    }
                    // if the cell is empty, are there letters in its immediate neighbors in the orthogonal direction?
                    if (cell.isEmpty()) {
                        if (x > 0) {
                            Cell adjacent = grid[currY][x - 1];
                            if (!adjacent.isEmpty()) {
                                break;
                            }
                        }
                        if (x < width - 1) {
                            Cell adjacent = grid[currY][x + 1];
                            if (!adjacent.isEmpty()) {
                                break;
                            }
                        }
                    }

                    if (currY < height - 1) {
                        // is there a word in the same direction that begins in the cell immediately following this one?
                        Cell nextCell = grid[currY + 1][x];
                        if (nextCell.getWord(dir) != null) {
                            break;
                        }

                        if (!nextCell.isEmpty()) {
                            // possible branching:
                            // a candidate can stop at the previous cell or at the next one, but not at the current one

                            if (currPattern.length() > 0) {
                                currPattern.append(cell.getLetter());
                                continue;
                            }
                        }
                    }
                    // none of the checks failed, we can expand the current pattern and add it it the resulting set
                    currPattern.append(cell.getLetter());
                    result.add(currPattern.toString());
                }
                break;
        }
        return result;
    }

    /**
     * Deep copy the Board object.
     *
     * @return a deep copy of the original Board object.
     * @throws CloneNotSupportedException
     */
    @Override
    public Board clone() throws CloneNotSupportedException {
        Board other = (Board) super.clone();
        other.wordPlacements = new HashMap<>();
        other.wordsByDirection = new HashMap<>();
        for (Direction dir : Direction.values()) {
            other.wordsByDirection.put(dir, new HashSet<>(this.wordsByDirection.get(dir)));
            other.wordPlacements.put(dir, new HashSet<>(this.wordPlacements.get(dir)));
        }
        other.words = new HashSet<>(this.words);
        other.grid = new Cell[height][];
        for (int i = 0; i < height; i++) {
            other.grid[i] = new Cell[width];
            for (int j = 0; j < width; j++) {
                other.grid[i][j] = this.grid[i][j].clone();
            }
        }
        return other;
    }

    public String[] asStringArray() {
        List<String> list = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < width; j++) {
                sb.append(grid[i][j].getLetter());
            }
            list.add(sb.toString());
        }
        return list.toArray(new String[height]);
    }

    public boolean isSupersetOf(Board other) {
        if (other == null) {
            return false;
        }
        for (Direction dir : Direction.values()) {
            for (WordPlacement wp : other.wordPlacements.get(dir)) {
                if (!this.wordPlacements.get(dir).contains(wp)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Board board = (Board) o;
        return width == board.width && height == board.height &&  this.isSupersetOf(board) && board.isSupersetOf(this);
    }

    @Override
    public int hashCode() {
        String[] strs = this.asStringArray();
        String singleString = String.join("", strs);
        return Objects.hash(width, height, singleString);
    }

    public boolean canBePlaced(WordPlacement wp) {
        if (wp == null) {
            return false;
        }
        if (wp.getX() < 0 || wp.getY() < 0 || wp.getX() >= width || wp.getY() >= height) {
            return false;
        }
        String word = wp.getWord();
        if (word != null && words.contains(word)) {
            return false;
        }
        Direction dir = wp.getDirection();
        if (dir == null) {
            return false;
        }
        if (word != null) {
            int len = word.length();
            switch (dir) {
                case ACROSS:
                    int xAfter = wp.getX() + len;
                    if (xAfter > width) {
                        return false;
                    }
                    Cell[] row = grid[wp.getY()];
                    // boundary conditions
                    if (wp.getX() > 0) {
                        Cell prevCell = row[wp.getX() - 1];
                        if (!prevCell.isEmpty()) {
                            return false;
                        }
                    }

                    // the cell immediately after the word should be empty!
                    if (xAfter < width) {
                        Cell afterCell = row[xAfter];
                        if (!afterCell.isEmpty()) {
                            return false;
                        }
                    }
                    // for each cell check if we can place the word
                    for (int x = wp.getX(); x < xAfter; x++) {
                        Cell cell = row[x];
                        // 1) If the cell is initialized and has a letter in it:
                        if (!cell.isEmpty()) {
                            //  - check if there are no other words going in the same direction associated with it
                            if (cell.getWord(wp.getDirection()) != null) {
                                return false;
                            }
                            //  - check if the word has the same letter at this position as the cell
                            if (cell.getLetter() != word.charAt(x - wp.getX())) {
                                return false;
                            }
                        } else {
                            // 2) If the cell is not initialized or empty,
                            //    check the cells directly above in and below it (if the rows are there)
                            //    Both should be empty.
                            if (wp.getY() > 0) {
                                // check the cell above
                                Cell cellAbove = grid[wp.getY() - 1][x];
                                if (!cellAbove.isEmpty()) {
                                    return false;
                                }
                            }
                            if (wp.getY() < height - 1) {
                                // check the cell below
                                Cell cellBelow = grid[wp.getY() + 1][x];
                                if (!cellBelow.isEmpty()) {
                                    return false;
                                }
                            }
                        }
                    }
                    break;
                case DOWN:
                    int yAfter = wp.getY() + len;
                    if (yAfter > height) {
                        return false;
                    }
                    // boundary conditions
                    if (wp.getY() > 0) {
                        Cell prevCell = grid[wp.getY() - 1][wp.getX()];
                        if (!prevCell.isEmpty()) {
                            return false;
                        }
                    }
                    if (yAfter < height) {
                        Cell afterCell = grid[yAfter][wp.getX()];
                        if (!afterCell.isEmpty()) {
                            return false;
                        }
                    }
                    // for each cell check if we can place the word
                    for (int y = wp.getY(); y < yAfter; y++) {
                        Cell cell = grid[y][wp.getX()];
                        // 1) If the cell is initialized and has a letter in it:
                        if (!cell.isEmpty()) {
                            //  - check if there are no other words going in the same direction associated with it
                            if (cell.getWord(wp.getDirection()) != null) {
                                return false;
                            }
                            //  - check if the word has the same letter at this position as the cell
                            if (cell.getLetter() != word.charAt(y - wp.getY())) {
                                return false;
                            }
                        } else {
                            // 2) If the cell is not initialized or empty,
                            //    check the cells directly above in and below it (if the rows are there)
                            //    Both should be empty.
                            if (wp.getX() > 0) {
                                // check the cell to the left
                                Cell cellToTheLeft = grid[y][wp.getX() - 1];
                                if (!cellToTheLeft.isEmpty()) {
                                    return false;
                                }
                            }
                            if (wp.getX() < width - 1) {
                                // check the cell to the right
                                Cell cellToTheRight = grid[y][wp.getX() + 1];
                                if (!cellToTheRight.isEmpty()) {
                                    return false;
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return true;
    }
}
