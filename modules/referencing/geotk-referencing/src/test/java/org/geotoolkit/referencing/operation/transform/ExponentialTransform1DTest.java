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
package org.geotoolkit.referencing.operation.transform;

import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.geometry.DirectPosition1D;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the {@link ExponentialTransform1D} class. This test case will also tests
 * indirectly the {@link LogarithmicTransform1D} class since it is the inverse of
 * the exponential transform.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 */
public final class ExponentialTransform1DTest extends TransformTestBase {
    /**
     * Arbitrary coefficients used for this test.
     */
    private static final double BASE=10, SCALE=2, C0=-3, C1=0.25;

    /**
     * Arbitrary seed used for this test.
     */
    private static final int SEED = 259859034;

    /**
     * Creates the test suite.
     */
    public ExponentialTransform1DTest() {
        super(ExponentialTransform1D.class, null);
    }

    /**
     * A simple (non-concatenated) test case.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testSimple() throws TransformException {
        transform = ExponentialTransform1D.create(BASE, SCALE);
        validate();
        assertFalse(transform.isIdentity());

        final double[] source = generateRandomCoordinates(CoordinateDomain.GAUSSIAN, SEED);
        final double[] target = new double[source.length];
        for (int i=0; i<source.length; i++) {
            target[i] = SCALE * Math.pow(BASE, source[i]);
        }
        tolerance = 1E-15;
        verifyTransform(source, target);
        stress(source);

        // Tests the derivative at a single point.
        tolerance = 0.002;
        checkDerivative(new DirectPosition1D(2.5), 0.001);
    }

    /**
     * Tests the concatenation of a linear operation before the exponential one.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testAffinePreConcatenation() throws TransformException {
        transform = ConcatenatedTransform.create(
                LinearTransform1D.create(C1, C0),
                ExponentialTransform1D.create(BASE, SCALE));
        validate();
        assertFalse(transform.isIdentity());
        assertTrue("Expected mathematical identities.", transform instanceof ExponentialTransform1D);

        final double[] source = generateRandomCoordinates(CoordinateDomain.GAUSSIAN, SEED);
        final double[] target = new double[source.length];
        for (int i=0; i<source.length; i++) {
            target[i] = SCALE * Math.pow(BASE, C0 + C1 * source[i]);
        }
        tolerance = 1E-14;
        verifyTransform(source, target);
        stress(source);

        // Tests the derivative at a single point.
        tolerance = 1E-9;
        checkDerivative(new DirectPosition1D(2.5), 0.001);

        // Find back the original linear coefficients as documented in the ExpentionalTransform1D
        // class javadoc. Then check that the transform results are the expected ones.
        final double lnBase =  Math.log(BASE);
        final double offset = -Math.log(SCALE) / lnBase;
        final MathTransform1D log = LogarithmicTransform1D.create(BASE, offset);
        transform = (LinearTransform1D) ConcatenatedTransform.create(transform, log);
        assertTrue("Expected mathematical identities.", transform instanceof LinearTransform1D);
        assertEquals(C1, ((LinearTransform1D) transform).scale,  1E-12);
        assertEquals(C0, ((LinearTransform1D) transform).offset, 1E-12);
        for (int i=0; i<source.length; i++) {
            target[i] = Math.log(target[i]) / lnBase + offset;
        }
        tolerance = 1E-14;
        verifyTransform(source, target);
    }

    /**
     * Tests the concatenation of a linear operation after the exponential one.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testAffinePostConcatenation() throws TransformException {
        transform = ConcatenatedTransform.create(
                ExponentialTransform1D.create(BASE, SCALE),
                LinearTransform1D.create(C1, C0));
        validate();
        assertFalse(transform.isIdentity());

        final double[] source = generateRandomCoordinates(CoordinateDomain.GAUSSIAN, SEED);
        final double[] target = new double[source.length];
        for (int i=0; i<source.length; i++) {
            target[i] = C0 + C1 * (SCALE * Math.pow(BASE, source[i]));
        }
        tolerance = 1E-12;
        verifyTransform(source, target);
        stress(source);

        // Tests the derivative at a single point.
        tolerance = 0.01;
        checkDerivative(new DirectPosition1D(2.5), 0.001);
    }

    /**
     * Tests the concatenation of a logarithmic operation with the exponential one.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testLogarithmicConcatenation() throws TransformException {
        final double offset = -3;
        final double base   = 8;
        final double lnBase = Math.log(base);
        transform = ConcatenatedTransform.create(
                LogarithmicTransform1D.create(base, offset),
                ExponentialTransform1D.create(BASE, SCALE));
        validate();
        assertFalse(transform.isIdentity());

        final double[] source = generateRandomCoordinates(CoordinateDomain.GAUSSIAN, SEED);
        final double[] target = new double[source.length];
        for (int i=0; i<source.length; i++) {
            source[i] = Math.abs(source[i]) + 0.001;
            target[i] = SCALE * Math.pow(BASE, Math.log(source[i]) / lnBase + offset);
        }
        tolerance = 1E-14;
        verifyTransform(source, target);
        stress(source);

        // Tests the derivative at a single point.
        tolerance = 1E-10;
        checkDerivative(new DirectPosition1D(2.5), 0.001);
    }
}
