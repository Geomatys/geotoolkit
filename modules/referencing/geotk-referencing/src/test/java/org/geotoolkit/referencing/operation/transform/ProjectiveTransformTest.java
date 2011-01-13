/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.awt.geom.AffineTransform;

import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.referencing.operation.provider.Affine;
import org.geotoolkit.referencing.operation.matrix.Matrix3;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;

import org.geotoolkit.test.Depend;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the {@link ProjectiveTransform} class. We use the {@link AffineTransform2D} class
 * as a reference, so we need to avoid NaN values. Note that {@link CopyTransformTest} will
 * use {@code ProjectiveTransform} as a reference, this time with NaN values.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.00
 */
@Depend(AbstractMathTransformTest.class)
public final class ProjectiveTransformTest extends TransformTestBase {
    /**
     * Creates a new test suite.
     */
    public ProjectiveTransformTest() {
        super(ProjectiveTransform.class, null);
    }

    /**
     * Tests using a uniform scale transform.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testUniformScale() throws TransformException {
        final AffineTransform reference = AffineTransform.getScaleInstance(5,5);
        final Matrix3 matrix = new Matrix3(reference);
        transform = new ProjectiveTransform2D(matrix);
        tolerance = 1E-10;
        validate();
        assertParameterEquals(Affine.PARAMETERS, null);
        assertEquals(matrix, ((LinearTransform) transform).getMatrix());

        final double[] source = generateRandomCoordinates(CoordinateDomain.GEOGRAPHIC, 706838196);
        final double[] target = new double[source.length];
        reference.transform(source, 0, target, 0, source.length/2);
        verifyTransform(source, target);
        stress(source);
    }

    /**
     * Tests using a non-uniform scale transform.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testScale() throws TransformException {
        final AffineTransform reference = AffineTransform.getScaleInstance(3,4);
        final Matrix3 matrix = new Matrix3(reference);
        transform = new ProjectiveTransform2D(matrix);
        tolerance = 1E-10;
        validate();
        assertParameterEquals(Affine.PARAMETERS, null);
        assertEquals(matrix, ((LinearTransform) transform).getMatrix());

        final double[] source = generateRandomCoordinates(CoordinateDomain.GEOGRAPHIC, 867119969);
        final double[] target = new double[source.length];
        reference.transform(source, 0, target, 0, source.length/2);
        verifyTransform(source, target);
        stress(source);
    }

    /**
     * Tests using a translation.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testTranslate() throws TransformException {
        final AffineTransform reference = AffineTransform.getTranslateInstance(1000,-2000);
        final Matrix3 matrix = new Matrix3(reference);
        transform = new ProjectiveTransform2D(matrix);
        tolerance = 1E-10;
        validate();
        assertParameterEquals(Affine.PARAMETERS, null);
        assertEquals(matrix, ((LinearTransform) transform).getMatrix());

        final double[] source = generateRandomCoordinates(CoordinateDomain.PROJECTED, 542967228);
        final double[] target = new double[source.length];
        reference.transform(source, 0, target, 0, source.length/2);
        verifyTransform(source, target);
        stress(source);
    }

    /**
     * Tests using a scaled, rotated and translated transform.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testGeneral() throws TransformException {
        final AffineTransform reference = AffineTransform.getTranslateInstance(10,-20);
        reference.rotate(0.5);
        reference.scale(0.2, 0.3);
        reference.translate(3000,5000);
        final Matrix3 matrix = new Matrix3(reference);
        transform = new ProjectiveTransform2D(matrix);
        tolerance = 1E-8;
        validate();
        assertParameterEquals(Affine.PARAMETERS, null);
        assertEquals(matrix, ((LinearTransform) transform).getMatrix());

        final double[] source = generateRandomCoordinates(CoordinateDomain.PROJECTED, 542967228);
        final double[] target = new double[source.length];
        reference.transform(source, 0, target, 0, source.length/2);
        verifyTransform(source, target);
        stress(source);
    }

    /**
     * Tests using a non-square matrix.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testNonSquare() throws TransformException {
        transform = new ProjectiveTransform(MatrixFactory.create(3, 5, new double[] {
            2, 0, 0, 0, 8,
            0, 0, 4, 0, 5,
            0, 0, 0, 0, 1
        }));
        tolerance = 1E-12;
        validate();

        final int numPts    = 3;
        final int sourceDim = 4;
        final double[] source = new double[] {0,0,0,0 , 1,1,1,1 , 8,3,-7,5};
        final double[] target = new double[] {8,5 , 10,9 , 24,-23};
        final double[] result = new double[numPts * sourceDim];
        transform.transform(source, 0, result, 0, numPts);
        for (int i=0; i<target.length; i++) {
            assertEquals("Direct transform", target[i], result[i], tolerance);
        }
        /*
         * Inverse the transform (this is the interesting part of this test) and try again.
         * The ordinates at index 1 and 3 (they are the index of columns were all elements
         * are 0 in the above matrix) are expected to be NaN.
         */
        transform = transform.inverse();
        transform.transform(target, 0, result, 0, numPts);
        for (int i=0; i<source.length; i += sourceDim) {
            source[i + 1] = Double.NaN;
            source[i + 3] = Double.NaN;
        }
        for (int i=0; i<source.length; i++) {
            assertEquals("Inverse transform", source[i], result[i], tolerance);
        }
    }

    /**
     * Tests the {@link ProjectiveTransform#derivative(Point2D)} method. We use the same
     * values than {@link AffineTransform2DTest#testDerivative()} for easier comparison.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.15
     */
    @Test
    public void testDerivative() throws TransformException {
        /*
         * Create any kind of transform having different coefficients for every values.
         */
        final AffineTransform tr = new AffineTransform();
        tr.scale(-8, 12);
        tr.translate(42, 50);
        tr.rotate(0.2);
        /*
         * Tests derivative.
         */
        transform = new ProjectiveTransform(new Matrix3(tr));
        tolerance = 1E-10;
        checkDerivative(new DirectPosition2D(-10, 5), 3);
    }
}
