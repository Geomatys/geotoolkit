/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree.hilbert.iterator;
import java.util.BitSet;
import java.util.Iterator;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test Hilbert curve creation in dimension n at order 9 if it is possible.
 *
 * <blockquote><font size=-1>
 * <strong>NOTE: test is possible if dimension*order &lt; 30.({@code int} size limit value).</strong>
 * </font></blockquote>
 *
 * @author Rémi Marechal(Geomatys).
 */
public abstract class HilbertTest {

    final boolean brid = true;
    final int dimension;
    BitSet validPath;
    Iterator<int[]> hilbertIterator;
    public HilbertTest(final int dimension) {
        this.dimension = dimension;
    }

    @Test
    public void order1Test(){
        orderNTest(1);
    }

    @Test
    public void order2Test(){
        orderNTest(2);
    }

    @Test
    public void order3Test(){
        orderNTest(3);
    }

    @Test
    public void order4Test(){
        orderNTest(4);
    }

    @Test
    public void order5Test(){
        orderNTest(5);
    }

    @Test
    public void order6Test(){
        orderNTest(6);
    }

    @Test
    public void order7Test(){
        orderNTest(7);
    }

    @Test
    public void order8Test(){
        orderNTest(8);
    }

    @Test
    public void order9Test(){
        orderNTest(9);
    }

    /**
     * Compute Hilbert curve and fill validPath table box.
     *
     * @param order Hilbert curve order.
     */
    private void orderNTest(final int order) {
        int valMax = (brid) ? 24 : 30;
        if (dimension*order > valMax) return;
        hilbertIterator = new HilbertIterator(order, dimension);
        validPath = new BitSet(2<<dimension*order-1);
        int length = 2 << order-1;
        while (hilbertIterator.hasNext()) {
            int[] coords = hilbertIterator.next();
            int val = coords[0];
            assert (coords[0] < 2E9) : ("coordinate more longer");
            for (int j=1; j<dimension; j++) {
                assert (coords[j]<2E9) : ("coordinate more longer");
                val += coords[j]*length;
            }
            validPath.set(val, true);
        }
        validPath();
    }

    /**
     * Verify all bitset table box.
     */
    private void validPath() {
        for(int j = 0, l=validPath.length(); j<l; j++) {
            assertTrue (validPath.get(j));
        }
    }
}
