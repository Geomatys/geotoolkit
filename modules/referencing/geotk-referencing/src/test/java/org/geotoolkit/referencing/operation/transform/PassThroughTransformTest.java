/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Arrays;
import java.util.Random;
import java.awt.geom.AffineTransform;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.test.ToleranceModifier;
import org.opengis.test.CalculationType;

import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;

import org.junit.*;

import static org.apache.sis.util.Arrays.hasNaN;
import static org.junit.Assert.*;


/**
 * Tests {@link PassThroughTransform}. This class tests especially the behavior of
 * the following methods when given a passthrough transform.
 * <p>
 * <ul>
 *   <li>{@link MathTransformFactory#createPassthroughTransform}</li>
 *   <li>{@link MathTransformFactory#createSubTransform}</li>
 *   <li>{@link MathTransformFactory#createFilterTransform}</li>
 * </ul>
 * <p>
 * The {@link DimensionFilter} class is also tested in order to ensure that it
 * can gives back the original transform.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.0
 */
public final strictfp class PassThroughTransformTest extends TransformTestBase {
    /**
     * The random number generator to be used in this test.
     */
    private static final Random random = new Random(259537798);

    /**
     * Creates a new test.
     */
    public PassThroughTransformTest() {
        super(PassThroughTransform.class, null);
    }

    /**
     * Tests the general passthrough transform.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testPassthrough() throws FactoryException, TransformException {
        final ParameterValueGroup param = mtFactory.getDefaultParameters("Exponential");
        runTest(mtFactory.createParameterizedTransform(param), PassThroughTransform.class);
    }

    /**
     * Tests the pass through transform using an affine transform.
     * The "passthrough" of such transform are optimized in a special way.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testLinear() throws FactoryException, TransformException {
        final GeneralMatrix matrix = new GeneralMatrix(AffineTransform.getScaleInstance(4,3));
        assertFalse(LinearTransform.class.isAssignableFrom(PassThroughTransform.class));
        runTest(mtFactory.createAffineTransform(matrix), LinearTransform.class);
    }

    /**
     * Tests the pass through transform using an identity transform.
     * The "passthrough" of such transform are optimized in a special way.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testIdentity() throws FactoryException, TransformException {
        final GeneralMatrix matrix = new GeneralMatrix(new AffineTransform());
        runTest(mtFactory.createAffineTransform(matrix), IdentityTransform.class);
    }

    /**
     * Tests a passthrough transform built using the given sub-transform.
     *
     * @param  subTransform the sub-transform to use for building passthrough transform.
     * @param  expectedClass The expected class of the passthrough transform to be created.
     * @throws FactoryException If the transform can't be created.
     * @throws TransformException If a transform failed.
     */
    private void runTest(final MathTransform subTransform,
                         final Class<? extends MathTransform> expectedClass)
            throws FactoryException, TransformException
    {
        try {
            mtFactory.createPassThroughTransform(-1, subTransform, 0);
            fail("An illegal argument should have been detected");
        } catch (FactoryException e) {
            // This is the expected exception.
        }
        try {
            mtFactory.createPassThroughTransform(0, subTransform, -1);
            fail("An illegal argument should have been detected");
        } catch (FactoryException e) {
            // This is the expected exception.
        }
        /*
         * Tests many combinations of "first affected ordinate" and "number of trailing ordinates"
         * parameters. For each combination we create a passthrough transform, test it with the
         * "verifyTransform" method, then try to split it back to the original transform using
         * the DimensionFilter class.
         */
        final DimensionFilter filter = new DimensionFilter(mtFactory);
        for (int firstAffectedOrdinate=0; firstAffectedOrdinate<=3; firstAffectedOrdinate++) {
            final int firstTrailingOrdinate = firstAffectedOrdinate + subTransform.getSourceDimensions();
            for (int numTrailingOrdinates=0; numTrailingOrdinates<=3; numTrailingOrdinates++) {
                final int numAdditionalOrdinates = firstAffectedOrdinate + numTrailingOrdinates;
                //
                // Create the PassthroughTransform.
                //
                transform = mtFactory.createPassThroughTransform(
                        firstAffectedOrdinate, subTransform, numTrailingOrdinates);
                //
                // Test the PassthroughTransform.
                //
                if (numAdditionalOrdinates == 0) {
                    assertSame("Failed to recognize that no passthrough was needed.", subTransform, transform);
                } else {
                    assertNotSame(subTransform, transform);
                    assertTrue("Wrong transform class.", expectedClass.isInstance(transform));
                }
                assertEquals("Wrong number of source dimensions.",
                        subTransform.getSourceDimensions() + numAdditionalOrdinates, transform.getSourceDimensions());
                assertEquals("Wrong number of target dimensions.",
                        subTransform.getTargetDimensions() + numAdditionalOrdinates, transform.getTargetDimensions());
                verifyTransform(subTransform, firstAffectedOrdinate);
                //
                // Split the PassthroughTransform back to the original sub-transform.
                //
                if (firstAffectedOrdinate != 0) {
                    filter.addSourceDimensionRange(0, firstAffectedOrdinate);
                    assertTrue("Expected an identity transform.", filter.separate(transform).isIdentity());
                    filter.clear();
                }
                if (numTrailingOrdinates != 0) {
                    filter.addSourceDimensionRange(firstTrailingOrdinate, transform.getSourceDimensions());
                    assertTrue("Expected an identity transform.", filter.separate(transform).isIdentity());
                    filter.clear();
                }
                filter.addSourceDimensionRange(firstAffectedOrdinate, firstTrailingOrdinate);
                assertEquals("Expected the sub-transform.", subTransform, filter.separate(transform));
                final int[] expectedDimensions = new int[subTransform.getTargetDimensions()];
                for (int i=0; i<expectedDimensions.length; i++) {
                    expectedDimensions[i] = firstAffectedOrdinate + i;
                }
                assertTrue("Unexpected output dimensions",
                        Arrays.equals(expectedDimensions, filter.getTargetDimensions()));
                filter.clear();
            }
        }
    }

    /**
     * Tests the current {@linkplain #transform transform} using an array of random coordinate
     * values, and compares the result against the expected ones. This method computes itself
     * the expected results.
     *
     * @param  subTransform The sub transform used by the current passthrough transform.
     * @param  firstAffectedOrdinate First input/output dimension used by {@code subTransform}.
     * @throws TransformException If a transform failed.
     */
    private void verifyTransform(final MathTransform subTransform, final int firstAffectedOrdinate)
            throws TransformException
    {
        validate();
        /*
         * Builds an array of source coordinates filled with random values. Copies in a
         * temporary array only the ordinates relevant to the sub-transform. Transforms
         * that array and stores the values in the target array which will contains the
         * expected ordinate values. This is only preparation - at this point we have
         * not yet tested the pass-through transform.
         */
        final int fullDimension = transform.getSourceDimensions();
        final int subtDimension = subTransform.getSourceDimensions();
        assertTrue("firstAffectedOrdinate argument value is too high.",
                firstAffectedOrdinate + subtDimension <= fullDimension);
        final int numPts = ORDINATE_COUNT / fullDimension;
        final double[] sourceData = new double[numPts * fullDimension];
        final double[]  subTrData = new double[numPts * subtDimension];
        Arrays.fill(sourceData, Double.NaN);
        Arrays.fill( subTrData, Double.NaN);
        for (int i=0; i<sourceData.length; i++) {
            sourceData[i] = random.nextDouble() * 100 - 50;
        }
        for (int i=0; i<numPts; i++) {
            System.arraycopy(sourceData, firstAffectedOrdinate + i*fullDimension,
                             subTrData, i*subtDimension, subtDimension);
        }
        assertFalse ("Error building test arrays.", hasNaN(sourceData));
        assertFalse ("Error building test arrays.", hasNaN( subTrData));
        assertEquals("Error building test arrays.", (subtDimension == fullDimension), Arrays.equals(subTrData, sourceData));
        subTransform.transform(subTrData, 0, subTrData, 0, numPts);
        final double[] targetData = sourceData.clone();
        for (int i=0; i<numPts; i++) {
            System.arraycopy(subTrData, i*subtDimension, targetData,
                             firstAffectedOrdinate + i*fullDimension, subtDimension);
        }
        assertFalse ("Error building test arrays.", hasNaN(targetData));
        assertEquals("Error building test arrays.", subTransform.isIdentity(), Arrays.equals(sourceData, targetData));
        /*
         * Now process to the transform and compares the results with the expected ones.
         * We perform a copy of the source data before to transform them in order to use
         * the same values for running the GeoAPI tests as the next step.
         *
         * Note: we assume that the transform is invertible if source and target dimensions
         * are the same. This is not true in the general case, but it is for this particular
         * test suite.
         */
        tolerance = 0; // Results should be strictly identical because we use the same inputs.
        toleranceModifier = null;
        isInverseTransformSupported = (subtDimension == subTransform.getTargetDimensions());
        final float[] sourceAsFloat = new float[sourceData.length];
        for (int i=0; i<sourceAsFloat.length; i++) {
            sourceAsFloat[i] = (float) sourceData[i];
        }
        transform.transform(sourceData, 0, sourceData, 0, numPts);
        assertCoordinatesEqual("Expected a plain copy.", fullDimension,
                targetData, 0, sourceData, 0, numPts, CalculationType.IDENTITY);
        /*
         * Below is a relatively high tolerance value, because result are
         * computed using inputs stored as float values.
         */
        tolerance = 1E-4f;
        toleranceModifier = ToleranceModifier.RELATIVE;
        final float[] targetAsFloat = verifyConsistency(sourceAsFloat);
        assertEquals("Unexpected length of transformed array.", targetData.length, targetAsFloat.length);
        assertCoordinatesEqual("A transformed value is wrong.", fullDimension,
                targetData, 0, targetAsFloat, 0, numPts, CalculationType.DIRECT_TRANSFORM);
        if (isInverseTransformSupported) {
            transform.inverse().transform(sourceData, 0, sourceData, 0, numPts);
            assertCoordinatesEqual("A transformed value is wrong.", fullDimension,
                    sourceAsFloat, 0, sourceData, 0, numPts, CalculationType.DIRECT_TRANSFORM);
        } else try {
            assertNotNull(transform.inverse());
            fail("Expected a non-invertible transform.");
        } catch (NoninvertibleTransformException e) {
            // This is the expected exception.
        }
    }
}
