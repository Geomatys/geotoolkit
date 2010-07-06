/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransformFactory;

import org.geotoolkit.test.Depend;
import org.geotoolkit.factory.FactoryFinder;
import static org.geotoolkit.referencing.datum.DefaultEllipsoid.*;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link MolodenskyTransform} class.
 *
 * @author Tara Athan
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 */
@Depend(AbstractMathTransformTest.class)
public final class MolodenskyTransformTest extends TransformTestCase {
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
     * Tests the optimized cases for identity transform.
     */
    @Test
    public void testIdentities() {
        final double a  = 6378137.0;
        final double b  = 6356752.0;
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
                    transform = MolodenskyTransform.create(abridged, a, b, source3D, a, b, target3D, 0.0, 0.0, 0.0);
                    assertInstanceOf("Expected optimized type.", expected, transform);
                    assertEquals(source3D ? 3 : 2, transform.getSourceDimensions());
                    assertEquals(target3D ? 3 : 2, transform.getTargetDimensions());
                    validate();
                } while ((abridged = !abridged) == true);
            } while ((target3D = !target3D) == true);
        } while ((source3D = !source3D) == true);
    }

    /**
     * Tests overwriting the source array, with a target offset slightyly greater than
     * the source offset. Source ellipsoid is WGS84. Target ellipsoid is the same (that
     * is, we are testing an identity transform).
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testArrayOverwrite() throws TransformException {
        final double a  = 6378137.0;
        final double b  = 6356752.0;
        transform = new MolodenskyTransform2D(false, a, b, a, b, 0.0, 0.0, 0.0);
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
        transform = MolodenskyTransform.create(false, a, b, false, a, b, false, 0.0, 0.0, 0.0);
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
}
