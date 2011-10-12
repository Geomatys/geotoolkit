/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.display.shape;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the {@link ShapeUtilities} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class ShapeUtilitiesTest {
    /**
     * Tolerance factor for the tests in this class.
     */
    private static final double EPS = 1E-8;

    /**
     * Tests {@link ShapeUtilities#cubicCurveExtremum}.
     */
    @Test
    public void testCubicCurveExtremum() {
        final Point2D.Double P1 = new Point2D.Double();
        final Point2D.Double P2 = new Point2D.Double();
        double dy1, dy2;
        Line2D extremums;

        P1.x =  0; P1.y =  0; dy1 =   7;
        P2.x = -4; P2.y =  0; dy2 = -12;
        extremums = ShapeUtilities.cubicCurveExtremum(P1, dy1, P2, dy2);
        assertEquals("X1",   3.31741507, extremums.getX1(), EPS);
        assertEquals("Y1",  17.31547745, extremums.getY1(), EPS);
        assertEquals("X2",  -2.25074840, extremums.getX2(), EPS);
        assertEquals("Y2",  -9.65918115, extremums.getY2(), EPS);

        P1.x = 0; P1.y =  0; dy1 = 5;
        P2.x = 5; P2.y = 20; dy2 = 1;
        extremums = ShapeUtilities.cubicCurveExtremum(P1, dy1, P2, dy2);
        assertEquals("X1",   5.47313697, extremums.getX1(), EPS);
        assertEquals("Y1",  20.24080512, extremums.getY1(), EPS);
        assertEquals("X2",  -3.80647030, extremums.getX2(), EPS);
        assertEquals("Y2", -11.72228660, extremums.getY2(), EPS);
    }
}
