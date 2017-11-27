package info.lynxnet.crossword;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MetricsTest {
    private String[] words = {
            "BORER",
            "BOTAS",
            "OVER",
            "SINEW",
            "BRASH",
            "FUSEL",
            "STACKER",
            "COCCOID",
            "TRAY",
            "SINUS",
            "UMPS",
            "SIMA",
            "AHED",
            "INDEX"
    };

    private String[] puzzle = {
            "BORER.BOTAS",
            "R......V..I",
            "A....C.E..N",
            "S.F..O.R..E",
            "H.U..C..T.W",
            "..STACKER..",
            "..E..O..A..",
            "S.L..I..Y.U",
            "I..INDEX..M",
            "M.........P",
            "AHED..SINUS"
    };

    private int[] weights = {6, 8, 7, 10};

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCalculateScore() throws Exception {
        Arrays.sort(words);
        double score = Metrics.calculateScore(puzzle, 11, weights, words);
        System.out.println(score);
    }
}