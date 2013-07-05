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

import java.util.Random;
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
     * Tests {@link Line#setLine(Point2D,Point2D)}.
     */
    @Test
    public void testLine() {
        final Line line = new Line();
        line.setLine(new Point2D.Double(-2, 2), new Point2D.Double(8, 22));
        assertEquals("slope", 2, line.getSlope(), EPS);
        assertEquals("x0",   -3, line.getX0(),    EPS);
        assertEquals("y0",    6, line.getY0(),    EPS);

        // Horizontal line
        line.setLine(new Point2D.Double(-2, 2), new Point2D.Double(8, 2));
        assertEquals("slope", 0, line.getSlope(), EPS);
        assertTrue  ("x0", Double.isInfinite(line.getX0()));
        assertEquals("y0",    2, line.getY0(),    EPS);

        // Vertical line
        line.setLine(new Point2D.Double(-2, 2), new Point2D.Double(-2, 22));
        assertTrue  ("slope", Double.isInfinite(line.getSlope()));
        assertEquals("x0", -2, line.getX0(), EPS);
        assertTrue  ("y0", Double.isInfinite(line.getY0()));

        // Horizontal line on the x axis
        line.setLine(new Point2D.Double(-2, 0), new Point2D.Double(8, 0));
        assertEquals("slope", 0, line.getSlope(), EPS);
        assertTrue  ("x0", Double.isInfinite(line.getX0()));
        assertEquals("y0", 0, line.getY0(), EPS);

        // Vertical line on the y axis
        line.setLine(new Point2D.Double(0, 2), new Point2D.Double(0, 22));
        assertTrue  ("slope", Double.isInfinite(line.getSlope()));
        assertEquals("x0", 0, line.getX0(), EPS);
        assertTrue  ("y0", Double.isInfinite(line.getY0()));
    }

    /**
     * Tests {@link #fit(double[],double[]).
     */
    @Test
    public void testFit() {
        final int    n = 10000;
        final double slope = 5;
        final double offset = 10;
        final double[] x = new double[n];
        final double[] y = new double[n];
        final Random random = new Random(888576070);
        for (int i=0; i<n; i++) {
            final double xi = random.nextDouble() * (20*n) - 10*n;
            final double yi = random.nextGaussian() * 100 + (slope * xi + offset);
            x[i] = xi;
            y[i] = yi;
        }
        final Line line = new Line();
        final double correlation = line.fit(x, y);
        assertEquals("slope", slope,        line.getSlope(), 1E-6);
        assertEquals("x0",    offset,       line.getY0(),    0.5 );
        assertEquals("y0",   -offset/slope, line.getX0(),    0.1 );
        assertEquals("corr",  1.0,          correlation,     1E-6);
    }

    /**
     * Tests {@link Line#isoscelesTriangleBase}.
     */
    @Test
    public void testIsoscelesTriangleBase() {
        final Line test = new Line();
        test.setLine(new Point2D.Double(20,30), new Point2D.Double(80,95));
        final double slope  = 1.083333333333333333333333;
        final double offset = 8.333333333333333333333333;
        assertEquals("slope", slope,        test.getSlope(), EPS);
        assertEquals("y0",    offset,       test.getY0(),    EPS);
        assertEquals("x0",   -offset/slope, test.getX0(),    EPS);

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

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        final Line local = new Line(9.5, -3.7);
        assertNotSame(local, assertSerializedEquals(local));
    }
}
