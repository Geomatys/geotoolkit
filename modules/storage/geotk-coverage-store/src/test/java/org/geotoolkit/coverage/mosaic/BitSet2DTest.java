/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.coverage.mosaic;

import org.apache.sis.coverage.grid.GridExtent;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test BitSet2D.
 *
 * @author Johann Sorel (Geomatys)
 */
public class BitSet2DTest {

    @Test
    public void testGetSet2D() {

        BitSet2D bs = new BitSet2D(100, 100);

        Assert.assertEquals(false, bs.get2D(10, 20));

        bs.set2D(10, 20, true);
        Assert.assertEquals(true, bs.get2D(10, 20));

        bs.set2D(30, 40, 10, 20, true);
        for (int x=30;x<40;x++) {
            for (int y=40;y<60;y++) {
                Assert.assertEquals(true, bs.get2D(x, y));
            }
        }
    }

    @Test
    public void testAreaSetted() {

        BitSet2D bs = new BitSet2D(100, 100);

        Assert.assertEquals(null, bs.areaSetted().orElse(null));

        bs.set2D(10, 20, true);
        Assert.assertEquals(new GridExtent(null, new long[]{10,20}, new long[]{10,20}, true), bs.areaSetted().get());

        bs.set2D(30, 40, true);
        Assert.assertEquals(new GridExtent(null, new long[]{10,20}, new long[]{30,40}, true), bs.areaSetted().get());

        bs.set2D(5, 30, true);
        bs.set2D(38, 25, true);
        Assert.assertEquals(new GridExtent(null, new long[]{5,20}, new long[]{38,40}, true), bs.areaSetted().get());

        bs.set2D(0, 99, true);
        Assert.assertEquals(new GridExtent(null, new long[]{0,20}, new long[]{38,99}, true), bs.areaSetted().get());

        bs.set2D(99, 99, true);
        bs.set2D(99, 0, true);
        Assert.assertEquals(new GridExtent(null, new long[]{0,0}, new long[]{99,99}, true), bs.areaSetted().get());

        bs.clear();
        bs.set2D(99, 99, true);
        Assert.assertEquals(new GridExtent(null, new long[]{99,99}, new long[]{99,99}, true), bs.areaSetted().get());
    }

    @Test
    public void testAreaCleared() {

        BitSet2D bs = new BitSet2D(100, 100);
        bs.set(0, 100*100);

        Assert.assertEquals(null, bs.areaCleared().orElse(null));

        bs.set2D(10, 20, false);
        Assert.assertEquals(new GridExtent(null, new long[]{10,20}, new long[]{10,20}, true), bs.areaCleared().get());

        bs.set2D(30, 40, false);
        Assert.assertEquals(new GridExtent(null, new long[]{10,20}, new long[]{30,40}, true), bs.areaCleared().get());

        bs.set2D(5, 30, false);
        bs.set2D(38, 25, false);
        Assert.assertEquals(new GridExtent(null, new long[]{5,20}, new long[]{38,40}, true), bs.areaCleared().get());

        bs.set2D(0, 99, false);
        Assert.assertEquals(new GridExtent(null, new long[]{0,20}, new long[]{38,99}, true), bs.areaCleared().get());

        bs.set2D(99, 99, false);
        bs.set2D(99, 0, false);
        Assert.assertEquals(new GridExtent(null, new long[]{0,0}, new long[]{99,99}, true), bs.areaCleared().get());
    }

    @Test
    public void testSharedSetted() {

        BitSet2D bs1 = new BitSet2D(100, 100);
        BitSet2D bs2 = new BitSet2D(100, 100);

        Assert.assertEquals(null, bs1.intersectSetted(bs2).orElse(null));

        bs1.set2D(10, 20, true);
        Assert.assertEquals(null, bs1.intersectSetted(bs2).orElse(null));

        bs2.set2D(10, 20, true);
        Assert.assertEquals(new GridExtent(null, new long[]{10,20}, new long[]{10,20}, true), bs1.intersectSetted(bs2).get());

        bs1.set2D(5, 30, true);
        bs2.set2D(38, 25, true);
        Assert.assertEquals(new GridExtent(null, new long[]{10,20}, new long[]{10,20}, true), bs1.intersectSetted(bs2).get());

    }
}
