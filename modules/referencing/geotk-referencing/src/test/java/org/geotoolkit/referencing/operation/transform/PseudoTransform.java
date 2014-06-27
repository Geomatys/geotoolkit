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

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.internal.referencing.DirectPositionView;
import static java.lang.StrictMath.*;


/**
 * A pseudo-transform for debugging purpose. The input points can be random numbers between
 * 0 and 1. The transformed points are build as below (when formatted in base 10):
 *
 * {@preformat text
 *     [1 digit for dimension] [3 first fraction digits] . [random digits from source]
 * }
 *
 * For example if the first input coordinate is (0.2, 0.5, 0.3), then the transformed
 * coordinate will be:
 *
 * {@preformat text
 *     1002.2
 *     2005.5
 *     3003.3
 * }
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
strictfp class PseudoTransform extends AbstractMathTransform {
    /**
     * The source and target dimensions.
     */
    protected final int sourceDimension, targetDimension;

    /**
     * Temporary buffer for copying the ordinate of a single source points.
     * Used in order to be compliant with {@link IterationStrategy} contract.
     */
    private final double[] buffer;

    /**
     * Creates a transform for the given dimensions.
     *
     * @param sourceDimension The source dimension.
     * @param targetDimension The target dimension.
     */
    public PseudoTransform(final int sourceDimension, final int targetDimension) {
        this.sourceDimension = sourceDimension;
        this.targetDimension = targetDimension;
        this.buffer = new double[sourceDimension];
    }

    /**
     * Returns the source dimension.
     */
    @Override
    public int getSourceDimensions() {
        return sourceDimension;
    }

    /**
     * Returns the target dimension.
     */
    @Override
    public int getTargetDimensions() {
        return targetDimension;
    }

    /**
     * Pseudo-transform a point in the given array.
     *
     * @throws TransformException should never occurs in this class,
     *         but can occur in method overridden in subclasses.
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate) throws TransformException
    {
        final Matrix derivative = derivate ? derivative(
                new DirectPositionView(srcPts, srcOff, getSourceDimensions())) : null;
        System.arraycopy(srcPts, srcOff, buffer, 0, sourceDimension);
        for (int i=0; i<targetDimension; i++) {
            double v = buffer[i % sourceDimension];
            v += (i+1)*1000 + round(v * 1000);
            dstPts[dstOff + i] = v;
        }
        return derivative;
    }
}
