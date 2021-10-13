/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test.gui;

import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.HeadlessException;

import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests a Java2D {@link Shape} implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 3.00)
 */
public strictfp class ShapeTestBase {
    /** The bounds of the shape to test. */
    protected static final int SHAPE_X=25, SHAPE_Y=25, SHAPE_WIDTH=150, SHAPE_HEIGHT=125;

    /** The size of area where to test for intersection. */
    static final int TEST_AREA_WIDTH=200, TEST_AREA_HEIGHT=200;

    /** The size of a small rectangle to create for testing intersection. */
    static final int TEST_SAMPLING_WIDTH=5, TEST_SAMPLING_HEIGHT=5;

    /** The interval between test rectangles. */
    static final int TEST_INTERVAL_X=10, TEST_INTERVAL_Y=10;

    /**
     * For sub-class constructors.
     */
    protected ShapeTestBase() {
    }

    /**
     * Returns {@code true} if the display of widgets is enabled.
     *
     * @return {@code true} if the display of widgets is enabled.
     */
    public static boolean isDisplayEnabled() {
        return Boolean.getBoolean(SwingTestBase.SHOW_PROPERTY_KEY);
    }

    /**
     * If the widgets are to be show, prepares the desktop pane which will contain them.
     * This method is invoked by JUnit and should not be invoked directly.
     *
     * @throws HeadlessException If the current environment does not allow the display of widgets.
     */
    @BeforeClass
    public static void prepareDesktop() throws HeadlessException {
        if (isDisplayEnabled()) {
            DesktopPane.prepareDesktop();
        }
    }

    /**
     * Asserts that {@link Shape#contains(double,double)}, {@link Shape#contains(Rectangle2D)}
     * and {@link Shape#intersects(Rectangle2D)} gives the same result between the shape to test
     * and a reference shape.
     *
     * @param expected The shape to use as a reference.
     * @param toTest   The shape to compare against the reference.
     */
    protected static void testContainsAndIntersectsMethods(final Shape expected, final Shape toTest) {
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
     * Shows the given shape if widgets viewing is enabled.
     *
     * @param shape The shape to show.
     * @param reference The shape to use as a reference, or {@code null} if none.
     * @param withSamples {@code true} if the panel should contain sample points for
     *        {@code contains} and {@code intersects} methods, or {@code false} for
     *        displaying the shape alone.
     */
    protected void show(final Shape shape, final Shape reference, final boolean withSamples) {
        if (isDisplayEnabled()) {
            DesktopPane.show(ShapeViewer.createPanel(shape, reference, withSamples));
        }
    }

    /**
     * If a frame has been created, wait for its disposal. This method is invoked by JUnit
     * and should not be invoked directly.
     *
     * @throws InterruptedException If the current thread has been interrupted while
     *         we were waiting for the frame disposal.
     */
    @AfterClass
    public static void waitForFrameDisposal() throws InterruptedException {
        if (isDisplayEnabled()) {
            DesktopPane.waitForFrameDisposal();
        }
    }
}
