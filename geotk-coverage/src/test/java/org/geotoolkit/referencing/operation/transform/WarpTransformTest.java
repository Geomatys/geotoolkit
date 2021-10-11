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

import java.util.Random;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.media.jai.Warp;
import javax.media.jai.WarpAffine;
import javax.media.jai.WarpQuadratic;
import javax.media.jai.WarpPolynomial;

import org.opengis.util.Factory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.test.referencing.TransformTestCase;

import org.junit.*;
import static org.opengis.test.Assert.*;


/**
 * Tests the {@link WarpTransform2D} class.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.15
 *
 * @since 2.1
 */
public final strictfp class WarpTransformTest extends TransformTestCase {
    /**
     * Width and height of a pseudo-image source image.
     */
    private static final int WIDTH = 1000, HEIGHT = 2000;

    /**
     * Creates a new test case without factory.
     */
    public WarpTransformTest() {
        super(new Factory[0]);
    }

    /**
     * Transforms in place a point. This is used for testing
     * affine, quadratic and cubic warp from know formulas.
     */
    private static interface Formula {
        String message();
        void transform(Point point);
    }

    /**
     * Constructs a warp and tests the transformations.
     * Coefficients will be tested later (by the caller).
     *
     * @param  formula The formula to use for generating points.
     * @param  degree  The polynomial degree for the wrap transform to be created.
     * @return The created warp transform, returned for information purpose.
     */
    private WarpPolynomial executeTest(final Formula formula, final int degree)
            throws TransformException
    {
        /*
         * Creates a set of points and transform them according the formula supplied in argument.
         */
        final Random  random  = new Random(-854734760285695284L);
        final Point[] sources = new Point[100];
        final Point[] dest    = new Point[sources.length];
        for (int i=0; i<dest.length; i++) {
            Point p;
            sources[i] = p = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
            dest   [i] = p = new Point(p);
            formula.transform(p);
        }
        /*
         * Gets the transform. We specify a bounding box which contains all points.
         */
        final Point ext = new Point(WIDTH, HEIGHT);
        formula.transform(ext);
        transform = new WarpTransform2D(
                new Rectangle(0, 0, WIDTH, HEIGHT), sources, 0,
                new Rectangle(0, 0, ext.x, ext.y),  dest,    0,
                sources.length, degree);
        final MathTransform inverse = transform.inverse();
        final String wkt = transform.toString();
        assertNotNull("WKT formatting test", wkt);
        /*
         * Checks Warp properties.
         */
        final Warp warp = ((WarpTransform2D) transform).getWarp();
        assertInstanceOf("Expected a polynomial warp.", WarpPolynomial.class, warp);
        final WarpPolynomial poly = (WarpPolynomial) warp;
        /*
         * Compares transformations to the expected points.
         */
        for (int i=0; i<sources.length; i++) {
            final String  message  = formula.message() + " Point #" + i;
            final Point   source   = sources[i];
            final Point   expected = dest   [i];
            final Point2D computed = new Point2D.Double(source.x, source.y);
            assertSame  (message, computed, ((MathTransform2D) transform).transform(computed, computed));
            assertEquals(message, expected.x, computed.getX(), tolerance*expected.x);
            assertEquals(message, expected.y, computed.getY(), tolerance*expected.y);
            //
            // Tries using transform(float[], ...)
            //
            if (true) {
                final float[] array = new float[] {source.x, source.y};
                transform.transform(array, 0, array, 0, 1);
                assertEquals(message, expected.x, array[0], (float) (tolerance*expected.x));
                assertEquals(message, expected.y, array[1], (float) (tolerance*expected.y));
            }
            //
            // Tries using transform(double[], ...)
            //
            if (true) {
                final double[] array = new double[] {source.x, source.y};
                transform.transform(array, 0, array, 0, 1);
                assertEquals(message, expected.x, array[0], tolerance*expected.x);
                assertEquals(message, expected.y, array[1], tolerance*expected.y);
            }
            //
            // Tests inverse transform
            //
            if (degree == 1) {
                computed.setLocation(expected.x, expected.y);
                assertSame  (message, computed, ((MathTransform2D) inverse).transform(computed, computed));
                assertEquals(message, source.x, computed.getX(), tolerance*expected.x);
                assertEquals(message, source.y, computed.getY(), tolerance*expected.y);
            }
        }
        return poly;
    }

    /**
     * Tests an affine warp.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    @Ignore("MathTransformFactory not found for unknown reason. Will be revisited in Apache SIS.")
    public void testAffine() throws TransformException {
        tolerance = 1E-5;
        final int[] scalesX = {1,2,3,4,5,6,  2,7,3,1,8};
        final int[] scalesY = {1,2,3,4,5,6,  6,2,5,9,1};
        for (int i=0; i<scalesX.length; i++) {
            final int scaleX = scalesX[i];
            final int scaleY = scalesY[i];
            final WarpPolynomial warp = executeTest(new Formula() {
                @Override public String message() {
                    return "WarpAffine[" + scaleX + ',' + scaleY + ']';
                }
                @Override public void transform(final Point point) {
                    point.x *= scaleX;
                    point.y *= scaleY;
                }
            }, 1);
            assertInstanceOf("Expected an affine warp.", WarpAffine.class, warp);
        }
    }

    /**
     * Tests a quadratic warp.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    @Ignore("MathTransformFactory not found for unknown reason. Will be revisited in Apache SIS.")
    public void testQuadratic() throws TransformException {
        tolerance = 1E-2;
        final int[] scalesX = {1,2,3,4,5,6,  2,7,3,1,8};
        final int[] scalesY = {1,2,3,4,5,6,  6,2,5,9,1};
        for (int i=0; i<scalesX.length; i++) {
            final int scaleX = scalesX[i];
            final int scaleY = scalesY[i];
            final WarpPolynomial warp = executeTest(new Formula() {
                @Override public String message() {
                    return "WarpQuadratic[" + scaleX + ',' + scaleY + ']';
                }
                @Override public void transform(final Point point) {
                    point.x *= scaleX*point.x;
                    point.y *= scaleY;
                }
            }, 2);
            assertInstanceOf("Expected a quatratic warp.", WarpQuadratic.class, warp);
        }
    }
}
