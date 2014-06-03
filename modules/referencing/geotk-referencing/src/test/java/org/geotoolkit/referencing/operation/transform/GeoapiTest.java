/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import org.apache.sis.parameter.Parameterized;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.test.referencing.AffineTransformTest;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.provider.Affine;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.opengis.test.Assert.*;


/**
 * Runs the suite of tests provided in the GeoAPI project. The test suite is run using
 * the {@link MathTransformFactory} instance registered in {@link FactoryFinder}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see org.apache.sis.util.iso.GeoapiTest
 * @see org.geotoolkit.referencing.factory.GeoapiTest
 * @see org.geotoolkit.referencing.factory.epsg.GeoapiTest
 * @see org.geotoolkit.referencing.operation.transform.GeoapiTest
 * @see org.geotoolkit.referencing.operation.projection.GeoapiTest
 * @see org.geotoolkit.GeoapiTest
 *
 * @since 3.19
 */
@RunWith(JUnit4.class)
public final strictfp class GeoapiTest extends AffineTransformTest {
    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public GeoapiTest() {
        super(FactoryFinder.getMathTransformFactory(null));
    }

    /**
     * Executed after every test in order to ensure that the {@linkplain #transform transform}
     * implements the {@link MathTransform1D} or {@link MathTransform2D} interface as needed.
     * In addition, all Geotk classes should implement {@link LinearTransform} and {@link Parameterized}.
     */
    @After
    public void ensureImplementRightInterface() {
        final int dimension = transform.getSourceDimensions();
        if (transform.getTargetDimensions() == dimension) {
            assertEquals("MathTransform1D", dimension == 1, (transform instanceof MathTransform1D));
            assertEquals("MathTransform2D", dimension == 2, (transform instanceof MathTransform2D));
        } else {
            assertFalse("MathTransform1D", transform instanceof MathTransform1D);
            assertFalse("MathTransform2D", transform instanceof MathTransform2D);
        }
        assertInstanceOf("Not Parameterized.", Parameterized.class, transform);
        TransformTestBase.verifyParameters(Affine.PARAMETERS, null, (Parameterized) transform, tolerance);
        assertInstanceOf("Not a LinearTransform.", LinearTransform.class, transform);
        final Matrix matrix = ((LinearTransform) transform).getMatrix();
        assertEquals("Matrix.isIdentity()", transform.isIdentity(), matrix.isIdentity());
        assertTrue("The matrix declared by the MathTransform is not equal to the one given at creation time.",
                Matrices.equals(this.matrix, matrix, tolerance, false));
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testIdentity1D() throws FactoryException, TransformException {
        super.testIdentity1D();
        assertInstanceOf("Unexpected implementation.", IdentityTransform1D.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testIdentity2D() throws FactoryException, TransformException {
        super.testIdentity2D();
        assertInstanceOf("Unexpected implementation.", AffineTransform2D.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testIdentity3D() throws FactoryException, TransformException {
        super.testIdentity3D();
        assertInstanceOf("Unexpected implementation.", IdentityTransform.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testAxisSwapping2D() throws FactoryException, TransformException {
        super.testAxisSwapping2D();
        assertInstanceOf("Unexpected implementation.", AffineTransform2D.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testSouthOrientated2D() throws FactoryException, TransformException {
        super.testSouthOrientated2D();
        assertInstanceOf("Unexpected implementation.", AffineTransform2D.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testTranslatation2D() throws FactoryException, TransformException {
        super.testTranslatation2D();
        assertInstanceOf("Unexpected implementation.", AffineTransform2D.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testUniformScale2D() throws FactoryException, TransformException {
        super.testUniformScale2D();
        assertInstanceOf("Unexpected implementation.", AffineTransform2D.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testGenericScale2D() throws FactoryException, TransformException {
        super.testGenericScale2D();
        assertInstanceOf("Unexpected implementation.", AffineTransform2D.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testRotation2D() throws FactoryException, TransformException {
        super.testRotation2D();
        assertInstanceOf("Unexpected implementation.", AffineTransform2D.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testGeneral() throws FactoryException, TransformException {
        super.testGeneral();
        assertInstanceOf("Unexpected implementation.", AffineTransform2D.class, transform);
    }

    /**
     * Runs the GeoAPI tests, then perform implementation-specific checks.
     */
    @Test
    @Override
    public void testDimensionReduction() throws FactoryException, TransformException {
        super.testDimensionReduction();
        assertInstanceOf("Unexpected implementation.", ProjectiveTransform.class, transform);
    }
}
