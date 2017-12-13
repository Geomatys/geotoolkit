package org.geotoolkit.coverage.grid;

import java.util.NoSuchElementException;
import org.geotoolkit.test.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.GridEnvelope;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class GridIteratorTest {

    @Test
    public void test2D() {
        final GridEnvelope grid = new GeneralGridEnvelope(new int[]{2, 4}, new int[]{19, 13}, true);
        GridIterator it = new GridIterator(grid, new int[]{0, 0});
        Assert.assertTrue("Iterator should have at least one element", it.hasNext());
        Assert.assertEquals("Returned grid envelope should match source one", grid, it.next());
        Assert.assertFalse("Iterator should have exactly one element.", it.hasNext());

        // Check use directly next()
        it = new GridIterator(grid, new int[]{0, 0});
        Assert.assertEquals("Returned grid envelope should match source one", grid, it.next());
        try {
            final GridEnvelope next = it.next();
            org.junit.Assert.fail("We were expecting iteration to stop, but the following has been returned: "+next);
        } catch (NoSuchElementException e) {
            // Expected behavior;
        }

        // Check that we can iterate over coordinates in the 2D grid
        it = new GridIterator(grid, new int[]{7, 5});
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{2, 4}, new int[]{2, 4}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{2, 9}, new int[]{2, 9}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{9, 4}, new int[]{9, 4}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{9, 9}, new int[]{9, 9}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{16, 4}, new int[]{16, 4}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{16, 9}, new int[]{16, 9}, true), it.next());
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void test3D() {
        final GridEnvelope grid = new GeneralGridEnvelope(new int[]{0, 0, 0}, new int[]{100, 100, 2}, true);
        GridIterator it = new GridIterator(grid, new int[]{0, 0, 1});
        Assert.assertTrue("Iterator should have at least one element", it.hasNext());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 0}, new int[]{100, 100, 0}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 1}, new int[]{100, 100, 1}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 2}, new int[]{100, 100, 2}, true), it.next());
        Assert.assertFalse(it.hasNext());
    }

    public void test4D() {
        final GridEnvelope grid = new GeneralGridEnvelope(new int[]{0, 0, 0, 10}, new int[]{100, 100, 2, 16}, true);
        GridIterator it = new GridIterator(grid, new int[]{0, 0, 1, 2});
        Assert.assertTrue("Iterator should have at least one element", it.hasNext());

        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 0, 10}, new int[]{100, 100, 0, 10}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 0, 12}, new int[]{100, 100, 0, 12}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 0, 14}, new int[]{100, 100, 0, 14}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 0, 16}, new int[]{100, 100, 0, 16}, true), it.next());

        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 1, 10}, new int[]{100, 100, 1, 10}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 1, 12}, new int[]{100, 100, 1, 12}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 1, 14}, new int[]{100, 100, 1, 14}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 1, 16}, new int[]{100, 100, 1, 16}, true), it.next());

        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 2, 10}, new int[]{100, 100, 2, 10}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 2, 12}, new int[]{100, 100, 2, 12}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 2, 14}, new int[]{100, 100, 2, 14}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 2, 16}, new int[]{100, 100, 2, 16}, true), it.next());

        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testDisjoint4D() {
        final GridEnvelope grid = new GeneralGridEnvelope(new int[]{0, 0, 0, 10}, new int[]{2, 100, 100, 16}, true);
        GridIterator it = new GridIterator(grid, new int[]{1, 0, 0, 3});
        Assert.assertTrue("Iterator should have at least one element", it.hasNext());

        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 0, 10}, new int[]{0, 100, 100, 10}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 0, 13}, new int[]{0, 100, 100, 13}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{0, 0, 0, 16}, new int[]{0, 100, 100, 16}, true), it.next());

        Assert.assertEquals(new GeneralGridEnvelope(new int[]{1, 0, 0, 10}, new int[]{1, 100, 100, 10}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{1, 0, 0, 13}, new int[]{1, 100, 100, 13}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{1, 0, 0, 16}, new int[]{1, 100, 100, 16}, true), it.next());

        Assert.assertEquals(new GeneralGridEnvelope(new int[]{2, 0, 0, 10}, new int[]{2, 100, 100, 10}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{2, 0, 0, 13}, new int[]{2, 100, 100, 13}, true), it.next());
        Assert.assertEquals(new GeneralGridEnvelope(new int[]{2, 0, 0, 16}, new int[]{2, 100, 100, 16}, true), it.next());

        Assert.assertFalse(it.hasNext());
    }
}
