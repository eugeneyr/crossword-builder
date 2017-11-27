package info.lynxnet.crossword;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class CrosswordBuilderTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetAllPermutations() throws Exception {
        BeautifulCrossword bc = new BeautifulCrossword(new String[] {"A", "B"}, 3, new int[] {1, 1, 1, 1});
        CrosswordBuilder b = new CrosswordBuilder(bc, new Board(3),
                Constants.PLACEMENT_GENERATOR_CLASS.getConstructor(Integer.TYPE).newInstance(3).getFirst());
        Collection<?> perms = b.getAllPermutations(b.getBoard(), 0, Direction.ACROSS);
        assertEquals(9, perms.size());

        perms = b.getAllPermutations(b.getBoard(), 0, Direction.DOWN);
        assertEquals(9, perms.size());
    }
}