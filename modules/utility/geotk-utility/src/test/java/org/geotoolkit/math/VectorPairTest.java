/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.math;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link VectorPair} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class VectorPairTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests the {@link VectorPair#omitColinearPoints} method.
     */
    @Test
    public void testOmitColinearPoints() {
        final double[]  x = {1, 2, 3, 4, 5, 6, 7, 8};
        final double[]  y = {4, 4, 3, 3, 3, 4, 5, 6};
        final double[] ey = {4, 4, 3,    3,       6}; // Expected result
        final double[] ex = {1, 2, 3,    5,       8};
        final VectorPair pair = new VectorPair(Vector.create(x), Vector.create(y));
        pair.omitColinearPoints(1E-6, 1E-6);
        assertEquals(Vector.create(ey), pair.getY());
        assertEquals(Vector.create(ex), pair.getX());
    }

    /**
     * Tests the {@link VectorPair#makeStepwise} method.
     */
    @Test
    public void testMakeStepwise() {
        final double[]  x = {1, 2, 3, 4, 5, 6, 7};
        final double[]  y = {4, 5, 6, 6, 3, 4};
        final double[] ey = {4, 4, 5, 5, 6, 6, 6, 6, 3, 3, 4, 4};
        final double[] ex = {1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7};
        final VectorPair pair = new VectorPair(Vector.create(x), Vector.create(y));
        pair.makeStepwise(0);
        assertEquals(Vector.create(ey), pair.getY());
        assertEquals(Vector.create(ex), pair.getX());
    }

    /**
     * Tests the {@link VectorPair#makeStepwise} method with higher <var>y</var> values.
     */
    @Test
    public void testMakeStepwiseUp() {
        double[]  x = {1, 2, 3, 4, 5, 6, 7};
        double[]  y = {4, 5, 6, 6, 3, 4};
        double[] ey = {4, 5, 5, 6, 6, 6, 6, 6, 3, 4, 4, 4};
        double[] ex = {1, 1, 2, 2, 3, 3, 4, 5, 5, 5, 6, 7};
        VectorPair pair = new VectorPair(Vector.create(x), Vector.create(y));
        pair.makeStepwise(+1);
        assertEquals(Vector.create(ey), pair.getY());
        assertEquals(Vector.create(ex), pair.getX());
        /*
         * Try again, now asking to also remove the lower point when the line
         * segments go down and up again at the same X value. The value 3 in
         * the expected Y array should be dropped.
         */
        ey = new double[] {4, 5, 5, 6, 6, 6, 6, 6, 4, 4, 4};
        ex = new double[] {1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 7};
        pair = new VectorPair(Vector.create(x), Vector.create(y));
        pair.makeStepwise(+2);
        assertEquals(Vector.create(ey), pair.getY());
        assertEquals(Vector.create(ex), pair.getX());
        /*
         * Opportunist additional test of omitColinearPoints().
         */
        ey = new double[] {4, 5, 5, 6, 6, 4, 4};
        ex = new double[] {1, 1, 2, 2, 5, 5, 7};
        pair.omitColinearPoints(1E-6, 1E-6);
        assertEquals(Vector.create(ey), pair.getY());
        assertEquals(Vector.create(ex), pair.getX());
    }
}
