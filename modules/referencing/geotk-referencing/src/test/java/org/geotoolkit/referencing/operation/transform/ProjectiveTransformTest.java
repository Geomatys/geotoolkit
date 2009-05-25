/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import org.geotoolkit.referencing.operation.provider.Affine;
import org.geotoolkit.referencing.operation.matrix.Matrix3;

import org.geotoolkit.test.Depend;
import org.junit.*;


/**
 * Tests the {@link ProjectiveTransform} class. We use the {@link AffineTransform2D} class
 * as a reference.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(AbstractMathTransformTest.class)
public final class ProjectiveTransformTest extends TransformTestCase {
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
    }
}
