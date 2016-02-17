/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import java.util.Random;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

import javax.media.jai.Warp;
import javax.media.jai.WarpAffine;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link WarpAdapter} class.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.15
 *
 * @since 3.15 (derived from 2.1)
 */
public final strictfp class WarpAdapterTest extends org.geotoolkit.test.TestBase {
    /**
     * Tolerance factor for floating point comparisons.
     */
    private static final double TOLERANCE = 1E-5;

    /**
     * Tests the {@link WarpAdapter} class using an affine transform.
     */
    @Test
    public void testAdapter() {
        final AffineTransform atr = AffineTransform.getScaleInstance(0.25, 0.5);
        atr.translate(4, 2);
        final AffineTransform2D transform = new AffineTransform2D(atr);
        final WarpAffine        warp      = new WarpAffine       (atr);
        final WarpAdapter       adapter   = new WarpAdapter("test", transform);
        final Random            random    = new Random(-854734760285695284L);
        for (int i=0; i<200; i++) {
            Point2D source   = new Point2D.Double(random.nextDouble()*100, random.nextDouble()*100);
            Point2D expected = warp   .mapDestPoint(source);
            Point2D computed = adapter.mapDestPoint(source);
            assertEquals("X", expected.getX(), computed.getX(), TOLERANCE);
            assertEquals("Y", expected.getY(), computed.getY(), TOLERANCE);

            // Tries inverse transform.
            expected = warp   .mapSourcePoint(source);
            computed = adapter.mapSourcePoint(source);
            assertEquals("X", expected.getX(), computed.getX(), TOLERANCE);
            assertEquals("Y", expected.getY(), computed.getY(), TOLERANCE);

            // Tries warpPoint
            final float[] exp = warp   .warpPoint((int)source.getX(), (int)source.getY(), null);
            final float[] com = adapter.warpPoint((int)source.getX(), (int)source.getY(), null);
            assertEquals("X", exp[0], com[0], TOLERANCE);
            assertEquals("Y", exp[1], com[1], TOLERANCE);
        }
    }

    /**
     * Creates a warp transform from the given affine transform coefficients,
     * and ensure that it produces the same result than the original transform,
     * taking in account the 0.5 offset.
     *
     * @since 3.14
     */
    private static void createAffine(final double scaleX, final double scaleY,
            final double translateX, final double translateY)
    {
        final AffineTransform2D tr = new AffineTransform2D(scaleX, 0, 0, scaleY, translateX, translateY);
        final Point2D.Double referencePoint = new Point2D.Double(0.5, 0.5);
        assertSame(referencePoint, tr.transform(referencePoint, referencePoint));
        /*
         * Following loop is executed twice: once that allow the WarpFactory.create(...)
         * method to returns an optimized transform, and once that prevent it, thus
         * forcing the usage of WarpAdapter.
         */
        boolean direct = true;
        do {
            Warp warp = WarpFactory.DEFAULT.create(null, direct ? tr : new PrivateTransform2D(tr));
            assertEquals(direct ? WarpAffine.class : WarpAdapter.class, warp.getClass());

            final float[] coordinates = warp.warpPoint(0, 0, null);
            assertEquals(2, coordinates.length);
            assertEquals("X value", referencePoint.x - 0.5, coordinates[0], TOLERANCE);
            assertEquals("Y value", referencePoint.y - 0.5, coordinates[1], TOLERANCE);
        } while ((direct = !direct) == false);
    }

    /**
     * Ensures that {@link WarpAdapter} computes the same values than {@link WarpAffine}.
     *
     * @since 3.14
     */
    @Test
    public void testGetWarpAffine() {
        createAffine(1, 1, 8, 9);
        createAffine(2, 3, 8, 9);
    }
}
