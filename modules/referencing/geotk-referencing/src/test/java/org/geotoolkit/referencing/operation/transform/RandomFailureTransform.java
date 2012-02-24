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

import java.util.Set;
import java.util.Random;
import java.util.HashSet;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;

import static org.junit.Assert.*;


/**
 * A pseudo-transform where some coordinate fail to be transformed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final strictfp class RandomFailureTransform extends PseudoTransform {
    /**
     * The random number generator for determining if a transform should fail.
     */
    private final Random random;

    /**
     * The denominator of the fraction that determines the frequency of failures.
     * See constructor javadoc.
     */
    private final int denominator;

    /**
     * The {@link #ordinal} values of coordinates that failed to be transformed. This is provided
     * for information purpose but not used by this class. This is user's responsibility to clear
     * this set before to start transforming a new array.
     */
    public final Set<Integer> failures;

    /**
     * Incremented after every transformed point and stored in the {@link #failures} set in
     * case of failure. This is user's responsibility to set this field to O before to start
     * transforming a new array.
     */
    public int ordinal;

    /**
     * Creates a transform for the given dimensions. The argument is the denominator of the
     * fraction that determines the frequency of failures. For example if this value is 20,
     * then 1/20 (i.e. 5%) of the points to transform will fail.
     *
     * @param denominator The denominator of the fraction that determines the frequency of failures.
     */
    public RandomFailureTransform(final int denominator) {
        super(4,3);
        this.denominator = denominator;
        random = new Random(891914828L * denominator);
        failures = new HashSet<>();
    }

    /**
     * Fills the given array with random number.
     *
     * @param array The array to fill.
     */
    public void fill(final double[] array) {
        for (int i=0; i<array.length; i++) {
            array[i] = random.nextDouble();
        }
    }

    /**
     * Fills the given array with random number.
     *
     * @param array The array to fill.
     */
    public void fill(final float[] array) {
        for (int i=0; i<array.length; i++) {
            array[i] = random.nextFloat();
        }
    }

    /**
     * Pseudo-transform a point in the given array.
     *
     * @throws TransformException Throws randomly at the frequency given at construction time.
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate) throws TransformException
    {
        final Matrix derivative = super.transform(srcPts, srcOff, dstPts, dstOff, derivate);
        final int index = ordinal++;
        if (random.nextInt(denominator) == 0) {
            assertTrue("Clash in coordinate ordinal.", failures.add(index));
            throw new TransformException("Random exception for testing purpose.");
        }
        return derivative;
    }
}
