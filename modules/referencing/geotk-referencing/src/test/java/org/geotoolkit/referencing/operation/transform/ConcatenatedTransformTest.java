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

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.operation.matrix.Matrix4;
import org.geotoolkit.referencing.operation.MathTransforms;

import org.junit.*;
import org.apache.sis.test.DependsOn;
import static org.opengis.test.Assert.*;


/**
 * Tests the {@link ConcatenatedTransformTest} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.00
 */
@DependsOn(ProjectiveTransformTest.class)
public final strictfp class ConcatenatedTransformTest extends TransformTestBase {
    /**
     * Creates a new test suite.
     */
    public ConcatenatedTransformTest() {
        super(ConcatenatedTransform.class, null);
    }

    /**
     * Tests the concatenation of two affine transforms than can be represented
     * as a {@link ConcatenatedTransformDirect2D}.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testDirect2D() throws TransformException {
        final AffineTransform2D first = new AffineTransform2D();
        first.mutable = true;
        first.translate(2,4);
        first.mutable = false;

        final AffineTransform2D second = new AffineTransform2D();
        second.mutable = true;
        second.translate(0.25, 0.75);
        second.mutable = false;

        // Direct for 2D case.
        tolerance = 1E-10;
        transform = new ConcatenatedTransformDirect2D(first, second);
        validate();
        final double[] source = generateRandomCoordinates(CoordinateDomain.PROJECTED, 89844167);
        final double[] target = new double[source.length];
        first .transform(source, 0, target, 0, source.length/2);
        second.transform(target, 0, target, 0, target.length/2);
        verifyTransform(source, target);
        stress(source);

        // Non-direct for 2D case.
        transform = new ConcatenatedTransform2D(first, second);
        validate();
        verifyTransform(source, target);
        stress(source);

        // Direct for general case - can't be validated.
        transform = new ConcatenatedTransformDirect(first, second);
        verifyTransform(source, target);
        stress(source);

        // Most general case - can't be validated.
        transform = new ConcatenatedTransform(first, second);
        verifyTransform(source, target);
        stress(source);

        // Optimized case.
        transform = MathTransforms.concatenate(first, second);
        assertInstanceOf("Expected optimized concatenation through matrix multiplication.",
                AffineTransform2D.class, transform);
        validate();
        verifyTransform(source, target);
        stress(source);
    }

    /**
     * Tests the concatenation of two affine transforms than can not be represented as a
     * {@link ConcatenatedTransformDirect}. The slower {@link ConcatenatedTransform} must
     * be used.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testGeneric() throws TransformException {
        final MathTransform first = MathTransforms.dimensionFilter(4, new int[] {1,3});

        final AffineTransform2D second = new AffineTransform2D();
        second.mutable = true;
        second.scale(0.5, 0.25);
        second.mutable = false;

        transform = new ConcatenatedTransform(first, second);
        isInverseTransformSupported = false;
        validate();
        final double[] source = generateRandomCoordinates(CoordinateDomain.PROJECTED, 677008208);
        final double[] target = new double[source.length / 2]; // Going from 4 to 2 dimensions.
        first .transform(source, 0, target, 0, target.length/2);
        second.transform(target, 0, target, 0, target.length/2);
        verifyTransform(source, target);
        stress(source);

        // Optimized case.
        transform = ConcatenatedTransform.create(first, second);
        assertInstanceOf("Expected optimized concatenation through matrix multiplication.",
                ProjectiveTransform.class, transform);
        validate();
        verifyTransform(source, target);
        stress(source);
    }

    /**
     * Tests the concatenation of a 3D affine transform with a pass-through transform.
     * The {@link ConcatenatedTransform#create(MathTransform, MathTransform)} method
     * should optimize this case.
     *
     * @since 3.16
     */
    @Test
    public void testPassthrough() {
        final MathTransform kernel = new PseudoTransform(2, 3); // Any non-linear transform.
        final MathTransform passth = PassThroughTransform.create(0, kernel, 1);
        final Matrix4 matrix = new Matrix4();
        transform = ConcatenatedTransform.create(MathTransforms.linear(matrix), passth);
        assertSame("Identity transform should be ignored.", passth, transform);
        assertEquals("Source dimensions", 3, transform.getSourceDimensions());
        assertEquals("Target dimensions", 4, transform.getTargetDimensions());
        /*
         * Put scale or offset factors only in the dimension to be processed by the sub-transform.
         * The matrix should be concatenated to the sub-transform rather than to the passthrough
         * transform.
         */
        matrix.m00 = 3;
        matrix.m13 = 2;
        transform = ConcatenatedTransform.create(MathTransforms.linear(matrix), passth);
        assertInstanceOf("Expected a new passthrough transform.", PassThroughTransform.class, transform);
        final MathTransform subTransform = ((PassThroughTransform) transform).getSubTransform();
        assertInstanceOf("Expected a new concatenated transform.", ConcatenatedTransform.class, subTransform);
        assertSame(kernel, ((ConcatenatedTransform) subTransform).transform2);
        assertEquals("Source dimensions", 3, transform.getSourceDimensions());
        assertEquals("Target dimensions", 4, transform.getTargetDimensions());
        /*
         * Put scale or offset factors is a passthrough dimension. Now, the affine transform
         * can not anymore be concatenated with the sub-transform.
         */
        matrix.m22 = 4;
        transform = ConcatenatedTransform.create(MathTransforms.linear(matrix), passth);
        assertInstanceOf("Expected a new concatenated transform.", ConcatenatedTransform.class, transform);
        assertSame(passth, ((ConcatenatedTransform) transform).transform2);
        assertEquals("Source dimensions", 3, transform.getSourceDimensions());
        assertEquals("Target dimensions", 4, transform.getTargetDimensions());
    }
}
