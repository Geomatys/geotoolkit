/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.matrix;

import java.util.Random;
import javax.measure.converter.ConversionException;
import javax.measure.unit.SI;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.referencing.cs.AbstractCS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import static org.opengis.referencing.cs.AxisDirection.*;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests some operation steps involved in coordinate operation creation.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.16
 *
 * @since 2.2
 */
public final strictfp class LinearConversionTest {
    /**
     * Small tolerance value for floating point comparisons.
     */
    private static final double EPS = 1E-9;

    /**
     * Tests matrix inversion and multiplication using {@link Matrix2}.
     */
    @Test
    public void testMatrix2() {
        final Matrix2 m = new Matrix2();
        assertTrue(m.isAffine());
        assertTrue(m.isIdentity());
        final Random random = new Random(8447482612423035360L);
        final GeneralMatrix identity = new GeneralMatrix(2);
        for (int i=0; i<100; i++) {
            m.setElement(0,0, 100*random.nextDouble());
            m.setElement(0,1, 100*random.nextDouble());
            m.setElement(1,0, 100*random.nextDouble());
            m.setElement(1,1, 100*random.nextDouble());
            final Matrix2 original = m.clone();
            final GeneralMatrix check = new GeneralMatrix(m);
            m.invert();
            check.invert();
            assertTrue(check.equals(m, EPS));
            m.multiply(original);
            assertTrue(identity.equals(m, EPS));
        }
    }

    /**
     * Tests axis swapping using {@link GeneralMatrix}.
     */
    @Test
    public void testAxisSwapping() {
        AxisDirection[] srcAxis = {NORTH, EAST, UP};
        AxisDirection[] dstAxis = {NORTH, EAST, UP};
        GeneralMatrix   matrix  = new GeneralMatrix(srcAxis, dstAxis);
        assertTrue(matrix.isAffine  ());
        assertTrue(matrix.isIdentity());
        dstAxis = new AxisDirection[] {WEST, UP, SOUTH};
        matrix  = new GeneralMatrix(srcAxis, dstAxis);
        assertTrue (matrix.isAffine  ());
        assertFalse(matrix.isIdentity());
        assertEquals(new GeneralMatrix(new double[][] {
            { 0,-1, 0, 0},
            { 0, 0, 1, 0},
            {-1, 0, 0, 0},
            { 0, 0, 0, 1}
        }), matrix);
        dstAxis = new AxisDirection[] {DOWN, NORTH};
        matrix  = new GeneralMatrix(srcAxis, dstAxis);
        assertFalse(matrix.isIdentity());
        assertEquals(new GeneralMatrix(new double[][] {
            {0, 0,-1, 0},
            {1, 0, 0, 0},
            {0, 0, 0, 1}
        }), matrix);
        dstAxis = new AxisDirection[] {DOWN, DOWN};
        matrix  = new GeneralMatrix(srcAxis, dstAxis);
        assertFalse(matrix.isIdentity());
        assertEquals(new GeneralMatrix(new double[][] {
            {0, 0,-1, 0},
            {0, 0,-1, 0},
            {0, 0, 0, 1}
        }), matrix);
        dstAxis = new AxisDirection[] {DOWN, GEOCENTRIC_X};
        try {
            matrix = new GeneralMatrix(srcAxis, dstAxis);
            fail();
        } catch (IllegalArgumentException exception) {
            // This is the expected exception (axis not in source).
        }
        srcAxis = dstAxis;
        dstAxis = new AxisDirection[] {NORTH, EAST, UP, WEST};
        try {
            matrix = new GeneralMatrix(srcAxis, dstAxis);
            fail();
        } catch (IllegalArgumentException exception) {
            // This is the expected exception (colinear axis).
        }
    }

    /**
     * Tests an example similar to the one provided in the
     * {@link AbstractCS#testScaleAndSwapAxis} javadoc.
     *
     * @throws ConversionException Should not happen.
     */
    @Test
    public void testScaleAndSwapAxis() throws ConversionException {
        final AbstractCS cs = new DefaultCartesianCS("Test",
              new DefaultCoordinateSystemAxis("y", SOUTH, SI.CENTIMETRE),
              new DefaultCoordinateSystemAxis("x", EAST,  SI.MILLIMETRE));
        Matrix matrix;
        matrix = AbstractCS.swapAndScaleAxis(DefaultCartesianCS.GENERIC_2D, cs);
        assertEquals(new GeneralMatrix(new double[][] {
            {0,  -100,    0},
            {1000,  0,    0},
            {0,     0,    1}
        }), matrix);
        matrix = AbstractCS.swapAndScaleAxis(DefaultCartesianCS.GENERIC_3D, cs);
        assertEquals(new GeneralMatrix(new double[][] {
            {0,  -100,   0,   0},
            {1000,  0,   0,   0},
            {0,     0,   0,   1}
        }), matrix);
    }
}
