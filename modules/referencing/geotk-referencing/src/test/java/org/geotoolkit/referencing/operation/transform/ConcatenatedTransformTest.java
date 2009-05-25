/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.junit.*;
import org.geotoolkit.test.Depend;


/**
 * Tests the {@link ConcatenatedTransformTest} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(ProjectiveTransformTest.class)
public final class ConcatenatedTransformTest extends TransformTestCase {
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
        transform = ConcatenatedTransform.create(first, second);
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
        final MathTransform first = ProjectiveTransform.create(
                ProjectiveTransform.createSelectMatrix(4, new int[] {1,3}));

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
}
