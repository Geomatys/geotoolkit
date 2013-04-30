/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.referencing.operation.provider.Affine;

import org.apache.sis.test.DependsOn;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests the {@link CopyTransform} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 */
@DependsOn(ProjectiveTransformTest.class)
public final strictfp class CopyTransformTest extends TransformTestBase {
    /**
     * Creates a new test suite.
     */
    public CopyTransformTest() {
        super(CopyTransform.class, null);
    }

    /**
     * Generates random ordinates and inserts a few random NaN values in the array.
     */
    private double[] generateRandom(final int randomSeed) {
        final double[] array = generateRandomCoordinates(CoordinateDomain.GEOGRAPHIC, randomSeed);
        final Random random = new Random(~randomSeed);
        for (int i=array.length/20; --i>=0;) {
            array[random.nextInt(array.length)] = Double.NaN;
        }
        return array;
    }

    /**
     * Replaces the current {@link CopyTransform} by an instance of {@link ProjectiveTransform}.
     */
    private void makeProjectiveTransform() {
        transform = new ProjectiveTransform(((CopyTransform) transform).getMatrix());
    }

    /**
     * Tests an identity transform.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void testIdentity() throws TransformException {
        transform = new CopyTransform(3, 0, 1, 2);
        validate();
        verifyParameters(Affine.PARAMETERS, null);
        assertTrue(((LinearTransform) transform).getMatrix().isIdentity());
        assertTrue(transform.isIdentity());

        final double[] source = generateRandom(373766338);
        final double[] target = source.clone();
        verifyTransform(source, target);
        stress(source);

        makeProjectiveTransform();
        verifyTransform(source, target);
        stress(source);
    }

    /**
     * Tests transform from 3D to 3D.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void test3D() throws TransformException {
        transform = new CopyTransform(3, 2, 1, 0);
        validate();
        assertFalse(transform.isIdentity());
        assertFalse(((LinearTransform) transform).getMatrix().isIdentity());

        final double[] source = generateRandom(812283341);
        final double[] target = new double[source.length];
        for (int i=0; i<source.length; i++) {
            final int r = i % 3;
            final int b = i - r;
            target[b + (2-r)] = source[i];
        }
        verifyTransform(source, target);
        stress(source);

        makeProjectiveTransform();
        verifyTransform(source, target);
        stress(source);
    }

    /**
     * Tests transform from 3D to 2D.
     *
     * @throws TransformException should never happen.
     */
    @Test
    public void test3Dto2D() throws TransformException {
        transform = new CopyTransform(3, 0, 1);
        isInverseTransformSupported = false;
        validate();
        assertFalse(transform.isIdentity());
        assertFalse(((LinearTransform) transform).getMatrix().isIdentity());

        final double[] source = generateRandom(78541666);
        final double[] target = new double[source.length * 2/3];
        for (int i=0,j=0; i<source.length; i++) {
            target[j++] = source[i++];
            target[j++] = source[i++];
            // Skip one i (in the for loop).
        }
        verifyTransform(source, target);
        stress(source);

        makeProjectiveTransform();
        verifyTransform(source, target);
        stress(source);
    }
}
