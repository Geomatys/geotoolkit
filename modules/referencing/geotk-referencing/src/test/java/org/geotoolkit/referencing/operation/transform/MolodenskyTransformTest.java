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
package org.geotoolkit.referencing.operation.transform;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.test.referencing.ParameterizedTransformTest;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.factory.FactoryFinder;
import static org.geotoolkit.referencing.datum.DefaultEllipsoid.*;

import org.junit.*;
import static java.lang.StrictMath.*;
import static org.opengis.test.Assert.*;


/**
 * Tests the {@link MolodenskyTransform} class.
 *
 * @author Tara Athan
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 2.5
 */
@DependsOn(AbstractMathTransformTest.class)
public final strictfp class MolodenskyTransformTest extends TransformTestBase {
    /**
     * Tolerance factor.
     */
    private static final float TOLERANCE = 1E-6f;

    /**
     * Creates a new test suite.
     */
    public MolodenskyTransformTest() {
        super(MolodenskyTransform.class, null);
    }

    /**
     * Tests the variants for different number of dimensions.
     *
     * @throws NoninvertibleTransformException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testForDimensions() throws NoninvertibleTransformException {
        final double a = 6378137;
        final double b = 6356752;
        final MolodenskyTransform tr2D, tr3D, tr23, tr32;

        tr2D = new MolodenskyTransform2D(false, a, b, a, b, 0, 0, 0);
        assertTrue(tr2D instanceof MathTransform2D);
        assertEquals(2, tr2D.getSourceDimensions());
        assertEquals(2, tr2D.getTargetDimensions());
        assertSame(tr2D, tr2D.forDimensions(false, false));
        assertSame(tr2D, tr2D.inverse().inverse());

        tr3D = tr2D.forDimensions(true, true);
        assertNotSame(tr2D, tr3D);
        assertFalse(tr3D instanceof MathTransform2D);
        assertEquals(3, tr3D.getSourceDimensions());
        assertEquals(3, tr3D.getTargetDimensions());
        assertSame(tr3D, tr2D.forDimensions(true,  true));
        assertSame(tr3D, tr3D.forDimensions(true,  true));
        assertSame(tr2D, tr3D.forDimensions(false, false));
        assertSame(tr3D, tr3D.inverse().inverse());
        assertSame(tr2D, ((EllipsoidalTransform) tr3D.inverse()).forDimensions(false, false).inverse());

        tr23 = tr2D.forDimensions(false, true);
        assertNotSame(tr2D, tr23);
        assertNotSame(tr3D, tr23);
        assertFalse(tr23 instanceof MathTransform2D);
        assertEquals(2, tr23.getSourceDimensions());
        assertEquals(3, tr23.getTargetDimensions());
        assertSame(tr23, tr2D.forDimensions(false, true));
        assertSame(tr23, tr3D.forDimensions(false, true));
        assertSame(tr23, tr23.forDimensions(false, true));
        assertSame(tr2D, tr23.forDimensions(false, false));
        assertSame(tr3D, tr23.forDimensions(true,  true));
        assertSame(tr23, tr23.inverse().inverse());
        assertSame(tr23, ((EllipsoidalTransform) tr23.inverse()).forDimensions(true, false).inverse());

        tr32 = tr3D.forDimensions(true, false);
        assertNotSame(tr2D, tr23);
        assertNotSame(tr3D, tr23);
        assertNotSame(tr23, tr32);
        assertFalse(tr32 instanceof MathTransform2D);
        assertEquals(3, tr32.getSourceDimensions());
        assertEquals(2, tr32.getTargetDimensions());
        assertSame(tr32, tr2D.forDimensions(true,  false));
        assertSame(tr32, tr3D.forDimensions(true,  false));
        assertSame(tr32, tr23.forDimensions(true,  false));
        assertSame(tr32, tr32.forDimensions(true,  false));
        assertSame(tr2D, tr32.forDimensions(false, false));
        assertSame(tr3D, tr32.forDimensions(true,  true));
        assertSame(tr23, tr32.forDimensions(false, true));
        assertSame(tr32, tr32.inverse().inverse());
        assertSame(tr32, ((EllipsoidalTransform) tr32.inverse()).forDimensions(false, true).inverse());
    }

    /**
     * Tests the optimized cases for identity transform.
     */
    @Test
    public void testIdentities() {
        final double a = 6378137;
        final double b = 6356752;
        boolean source3D = false;
        do {
            boolean target3D = false;
            do {
                final Class<? extends LinearTransform> expected;
                if (source3D == target3D) {
                    expected = source3D ? IdentityTransform.class : AffineTransform2D.class;
                } else {
                    expected = source3D ? CopyTransform.class : ProjectiveTransform.class;
                }
                boolean abridged = false;
                do {
                    transform = MolodenskyTransform.create(abridged, a, b, source3D, a, b, target3D, 0, 0, 0);
                    assertInstanceOf("Expected optimized type.", expected, transform);
                    assertEquals(source3D ? 3 : 2, transform.getSourceDimensions());
                    assertEquals(target3D ? 3 : 2, transform.getTargetDimensions());
                    validate();
                } while ((abridged = !abridged) == true);
            } while ((target3D = !target3D) == true);
        } while ((source3D = !source3D) == true);
    }

    /**
     * Tests overwriting the source array, with a target offset slightly greater than
     * the source offset. Source ellipsoid is WGS84. Target ellipsoid is the same (that
     * is, we are testing an identity transform).
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testArrayOverwrite() throws TransformException {
        final double a = 6378137;
        final double b = 6356752;
        transform = new MolodenskyTransform2D(false, a, b, a, b, 0, 0, 0);
        assertTrue(transform.isIdentity());
        validate();

        final float[] srcFloat = {
            0.0f, 0.0f, 0.0f, 89.999f, 0.0f, -89.999f, 179.999f, 0.0f,
            -179.999f, 0.0f, 0.0f, 0.0f, -123.19641f, 39.26859f
        };
        final int dim    = 2;
        final int srcOff = 0;
        final int dstOff = 2;
        final int numPts = 2;
        float[] overWriteTestArray = srcFloat.clone();
        transform.transform(overWriteTestArray, srcOff, overWriteTestArray, dstOff, numPts);
        for (int i=0; i<numPts; i++) {
            assertEquals(srcFloat[srcOff+dim*i  ], overWriteTestArray[dstOff+dim*i  ], TOLERANCE);
            assertEquals(srcFloat[srcOff+dim*i+1], overWriteTestArray[dstOff+dim*i+1], TOLERANCE);
        }
        /*
         * Tests the optimized case for identity transform.
         * This is an opportunist test.
         */
        transform = MolodenskyTransform.create(false, a, b, false, a, b, false, 0, 0, 0);
        assertInstanceOf("Expected optimized type.", AffineTransform2D.class, transform);
        assertTrue(transform.isIdentity());
        validate();
    }

    /**
     * Tests using the same EPSG example than the one provided in {@link GeocentricTransformTest}.
     *
     * Source point in WGS84: 53°48'33.820"N, 02°07'46.380"E, 73.00 metres.
     * Target point in ED50:  53°48'36.565"N, 02'07"51.477"E, 28.02 metres.
     * Datum shift: dX = +84.87m, dY = +96.49m, dZ = +116.95m.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testEpsgExample() throws TransformException {
        final double[] source = new double[] {
             2 + ( 7 + 46.38/60)/60,   // Longitude
            53 + (48 + 33.82/60)/60,   // Latitude
            73.0                       // Height
        };
        final double[] target = new double[] {
             2 + ( 7 + 51.477/60)/60,  // Longitude
            53 + (48 + 36.565/60)/60,  // Latitude
            28.02                      // Height
        };
        final double  a = WGS84.getSemiMajorAxis();
        final double  b = WGS84.getSemiMinorAxis();
        final double ta = INTERNATIONAL_1924.getSemiMajorAxis();
        final double tb = INTERNATIONAL_1924.getSemiMinorAxis();
        final double dx =  84.87;
        final double dy =  96.49;
        final double dz = 116.95;
        boolean abridged = false;
        do {
            tolerance = abridged ? 0.08 : 0.015;
            transform = MolodenskyTransform.create(abridged, a, b, true, ta, tb, true, dx, dy, dz);
            assertInstanceOf("Expected Molodensky.", MolodenskyTransform.class, transform);
            assertFalse(transform.isIdentity());
            validate();
            verifyTransform(source, target);
            stress(CoordinateDomain.GEOGRAPHIC, 208129394);
        } while ((abridged = !abridged) == true);
    }

    /**
     * Tests the creation through the provider.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testProvider() throws FactoryException {
        final MathTransformFactory factory = FactoryFinder.getMathTransformFactory(null);
        final ParameterValueGroup parameters = factory.getDefaultParameters("Molodenski");
        parameters.parameter("dim").setValue(3);
        parameters.parameter("dx").setValue(-3.0);
        parameters.parameter("dy").setValue(142.0);
        parameters.parameter("dz").setValue(183.0);
        parameters.parameter("src_semi_major").setValue(6378206.4);
        parameters.parameter("src_semi_minor").setValue(6356583.8);
        parameters.parameter("tgt_semi_major").setValue(6378137.0);
        parameters.parameter("tgt_semi_minor").setValue(6356752.31414036);
        transform = factory.createParameterizedTransform(parameters);
        assertInstanceOf("Expected Molodensky.", MolodenskyTransform.class, transform);
        assertEquals(3, transform.getSourceDimensions());
        assertEquals(3, transform.getTargetDimensions());
    }

    /**
     * Creates a transform and derivates a few points.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.19
     */
    @Test
    public void testDerivative() throws TransformException {
        final double  a = WGS84.getSemiMajorAxis();
        final double  b = WGS84.getSemiMinorAxis();
        final double ta = INTERNATIONAL_1924.getSemiMajorAxis();
        final double tb = INTERNATIONAL_1924.getSemiMinorAxis();
        final double dx =  84.87;
        final double dy =  96.49;
        final double dz = 116.95;
        boolean abridged = false;
        tolerance = 1E-6;
        transform = MolodenskyTransform.create(abridged, a, b, true, ta, tb, true, dx, dy, dz);
        validate();

        final double delta = toRadians(1.0 / 60) / 1852; // Approximatively one metre.
        derivativeDeltas = new double[] {delta, delta};

        verifyDerivative( 0,  0,  0);
        verifyDerivative(-3, 30,  7);
        verifyDerivative(+6, 60, 20);
    }

    /**
     * Runs the test defined in the GeoAPI-conformance module.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     *
     * @since 3.19
     */
    @Test
    public void runGeoapiTest() throws FactoryException, TransformException {
        new ParameterizedTransformTest(mtFactory).testAbridgedMolodensky();
    }
}
