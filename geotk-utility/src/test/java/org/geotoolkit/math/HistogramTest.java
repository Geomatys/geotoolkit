/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.math;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class HistogramTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000001;

    @Test
    public void histogramTest(){

        final Histogram histo = new Histogram(new long[]{1,2,3,4,5,5,4,3,2,1}, 2.0, 12.0);
        assertEquals(2.0, histo.getStart(), DELTA);
        assertEquals(12.0, histo.getEnd(), DELTA);
        assertEquals(30, histo.getSum());
        assertEquals(1.0, histo.getBucketSize(), DELTA);
        assertArrayEquals(new double[]{5.0,6.0}, histo.getBucketRange(3),DELTA);

        assertEquals(2.0, histo.getValueAt(0),DELTA);
        assertEquals(12.0, histo.getValueAt(1),DELTA);
        assertEquals(7.0, histo.getValueAt(0.5),DELTA);
        assertEquals(3.5, histo.getValueAt(2.0/30.0),DELTA);

    }

}
