/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.junit.*;
import static org.apache.sis.test.Assert.*;


/**
 * Tests the {@link Line} class.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 */
public final strictfp class LineTest {
    /**
     * Tolerance factor for comparisons.
     */
    private static final double EPS = 1E-8;

    /**
     * Tests {@link Line#isoscelesTriangleBase}.
     */
    @Test
    public void testIsoscelesTriangleBase() {
        final Line test = new Line();
        test.setFromPoints(20,30, 80,95);
        final double slope  = 1.083333333333333333333333;
        final double offset = 8.333333333333333333333333;
        assertEquals("slope", slope,        test.slope(), EPS);
        assertEquals("y0",    offset,       test.y0(),    EPS);
        assertEquals("x0",   -offset/slope, test.x0(),    EPS);

        final double distance = 40;
        final Point2D summit = new Point2D.Double(27, -9); // An arbitrary point.
        final Line2D base = test.isoscelesTriangleBase(summit, distance);
        assertEquals("distance P1", distance, base.getP1().distance(summit), EPS);
        assertEquals("distance P2", distance, base.getP2().distance(summit), EPS);

        final double x = 10; // Can be any arbitrary point.
        final double y = 8;
        assertEquals("nearest colinear point", base.ptLineDist(x,y),
                test.nearestColinearPoint(new Point2D.Double(x,y)).distance(x,y), EPS);
    }
}
