/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the shape implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 */
public class ShapeTest {
    /** The bounds of the shape to test. */
    static final int SHAPE_X=25, SHAPE_Y=25, SHAPE_WIDTH=150, SHAPE_HEIGHT=125;

    /** The size of area where to test for intersection. */
    static final int TEST_AREA_WIDTH=200, TEST_AREA_HEIGHT=200;

    /** The size of a small rectangle to create for testing intersection. */
    static final int TEST_SAMPLING_WIDTH=5, TEST_SAMPLING_HEIGHT=5;

    /** The interval between test rectangles. */
    static final int TEST_INTERVAL_X=10, TEST_INTERVAL_Y=10;

    /**
     * Asserts that {@link Shape#contains(double,double)}, {@link Shape#contains(Rectangle2D)}
     * and {@link Shape#intersects(Rectangle2D)} gives the same result between the shape to test
     * and a reference shape.
     */
    private static void compareMethods(final Shape expected, final Shape toTest) {
        final Point2D.Double center = new Point2D.Double();
        final Rectangle test = new Rectangle(TEST_SAMPLING_WIDTH, TEST_SAMPLING_HEIGHT);
        for (test.y=0; test.y<TEST_AREA_HEIGHT; test.y+=TEST_INTERVAL_Y) {
            for (test.x=0; test.x<TEST_AREA_WIDTH; test.x+=TEST_INTERVAL_X) {
                center.x = test.getCenterX();
                center.y = test.getCenterY();
                assertEquals("contains(Point2D)", expected.contains(center), toTest.contains(center));
                final boolean contains = toTest.contains(test);
                assertEquals("contains(Rectangle2D)", expected.contains(test), contains);
                /*
                 * Do not compare insersects(Rectangle2D) directly because our computation
                 * is more accurate than the generic one provided in Path2D - the later is
                 * allowed to be conservative according javadoc.
                 */
                if (contains) {
                    assertTrue(toTest.intersects(test));
                } else if (!toTest.intersects(test)) {
                    assertFalse(contains);
                }
            }
        }
    }

    /**
     * Tests the {@link Arrow2D} shape.
     */
    @Test
    public void testArrow2D() {
        final Shape shape = new Arrow2D(SHAPE_X, SHAPE_Y, SHAPE_WIDTH, SHAPE_HEIGHT);
        final Path2D reference = new Path2D.Double(shape);
        compareMethods(reference, shape);
    }
}
