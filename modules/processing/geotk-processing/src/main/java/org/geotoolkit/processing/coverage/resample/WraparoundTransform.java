/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.processing.coverage.resample;

import org.opengis.util.FactoryException;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.factory.InvalidGeodeticParameterException;
import org.apache.sis.internal.referencing.CoordinateOperations;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.resources.Errors;


/**
 * Enforces coordinate values in the range of a wraparound axis (typically longitude).
 * This transform is usually not needed for the [-180 … +180]° range since it is the
 * range of trigonometric functions. However this transform is useful for shifting
 * transformation results in the [0 … 360]° range.
 *
 * <p>{@code WraparoundTransform}s are not created automatically by {@link org.apache.sis.referencing.CRS#findOperation
 * CRS.findOperation(…)} because they introduce a discontinuity in coordinate transformations. Such discontinuities are
 * hurtless when transforming only a cloud of points, but produce undesirable artifacts when transforming geometries.
 * Callers need to invoke {@link #create create} explicitely if discontinuities are acceptable.</p>
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 0.8
 * @since   0.8
 * @module
 *
 * @todo We should implement {@link #tryConcatenate(boolean, MathTransform, MathTransformFactory)} by moving affine
 *       transform before or after this transform (depending where an affine transform already exist), except for
 *       the dimension of the "wrap around" axis. If an affine transform exist on both size, we should try to cancel
 *       one of them.
 */
final class WraparoundTransform extends AbstractMathTransform {
    /**
     * The dimension of source and target coordinates.
     */
    private final int dimension;

    /**
     * The dimension where to apply wraparound.
     */
    private final int wraparoundDimension;

    /**
     * Creates a new transform with a wraparound behavior in the given dimension.
     * Input and output values in the wraparound dimension shall be normalized in
     * the [0 … 1] range.
     */
    private WraparoundTransform(final int dimension, final int wraparoundDimension) {
        this.dimension = dimension;
        this.wraparoundDimension = wraparoundDimension;
    }

    /**
     * Returns the transform of the given coordinate operation augmented with a "wrap around" behavior if applicable.
     *
     * @param  factory  the factory to use for creating math transforms.
     * @param  op       the coordinate operation for which to get the math transform.
     * @return the math transform for the given coordinate operation.
     * @throws FactoryException in an error occurred while creating the math transform.
     *
     * @todo currently inefficient, especially since {@link #tryConcatenate(boolean, MathTransform, MathTransformFactory)}
     *       is not implemented. We need to create only one affine transform before the sequence of wraparound transforms,
     *       and only one affine transform after.
     */
    public static MathTransform create(final MathTransformFactory factory, final CoordinateOperation op)
            throws FactoryException
    {
        MathTransform tr = op.getMathTransform();
        final CoordinateSystem cs = op.getTargetCRS().getCoordinateSystem();
        final int dimension = cs.getDimension();
        for (final int wraparoundDimension : CoordinateOperations.wrapAroundChanges(op)) {
            final CoordinateSystemAxis axis = cs.getAxis(wraparoundDimension);
            tr = factory.createConcatenatedTransform(tr,
                    create(factory, dimension, wraparoundDimension, axis.getMinimumValue(), axis.getMaximumValue()));
        }
        return tr;
    }

    /**
     * Creates a transform with a "wrap around" behavior in the given dimension.
     *
     * @param  factory    the factory to use for creating math transforms.
     * @param  dimension  the number of source and target dimensions.
     * @param  wraparoundDimension  the dimension where "wrap around" behavior apply.
     * @param  minimum    minimal value in the "wrap around" dimension.
     * @param  maximum    maximal value in the "wrap around" dimension.
     * @return the math transform with "wrap around" behavior in the specified dimension.
     * @throws FactoryException in an error occurred while creating the math transform.
     */
    public static MathTransform create(final MathTransformFactory factory, final int dimension, final int wraparoundDimension,
            final double minimum, final double maximum) throws FactoryException
    {
        ArgumentChecks.ensureStrictlyPositive("dimension", dimension);
        ArgumentChecks.ensureBetween("wraparoundDimension", 0, dimension - 1, wraparoundDimension);
        NoninvertibleTransformException cause = null;
        final double span = maximum - minimum;
        if (span > 0 && span != Double.POSITIVE_INFINITY) {
            final MatrixSIS m = Matrices.createIdentity(dimension + 1);
            m.setElement(wraparoundDimension, wraparoundDimension, span);
            m.setElement(wraparoundDimension, dimension, minimum);
            final MathTransform denormalize = factory.createAffineTransform(m);
            try {
                return factory.createConcatenatedTransform(denormalize.inverse(),
                       factory.createConcatenatedTransform(new WraparoundTransform(dimension, wraparoundDimension), denormalize));
            } catch (NoninvertibleTransformException e) {
                // Matrix is non-invertible only if the range given in argument is illegal.
                cause = e;
            }
        }
        throw new InvalidGeodeticParameterException(Errors.format(Errors.Keys.IllegalRange_2, minimum, maximum), cause);
    }

    /**
     * Gets the dimension of input points.
     *
     * @return the dimension of input points.
     */
    @Override
    public int getSourceDimensions() {
        return dimension;
    }

    /**
     * Gets the dimension of output points.
     *
     * @return the dimension of output points.
     */
    @Override
    public int getTargetDimensions() {
        return dimension;
    }

    /**
     * Gets the derivative of this transform at a point.
     */
    @Override
    public Matrix derivative(final DirectPosition point) {
        final MatrixSIS derivative = Matrices.createIdentity(dimension);
        final double v = point.getOrdinate(wraparoundDimension);
        if (v == Math.floor(v)) {
            derivative.setElement(wraparoundDimension, wraparoundDimension, Double.NEGATIVE_INFINITY);
        }
        return derivative;
    }

    /**
     * Wraparounds a single coordinate point in an array,
     * and optionally computes the transform derivative at that location.
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff, final boolean derivate)
    {
        double v = srcPts[srcOff];
        v -= Math.floor(v);
        if (dstPts != null) {
            System.arraycopy(srcPts, srcOff, dstPts, dstOff, dimension);
            dstPts[dstOff + wraparoundDimension] = v;
        }
        if (!derivate) {
            return null;
        }
        final MatrixSIS derivative = Matrices.createIdentity(dimension);
        if (v == 0) {
            derivative.setElement(wraparoundDimension, wraparoundDimension, Double.NEGATIVE_INFINITY);
        }
        return derivative;
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * dimension);
        dstOff += wraparoundDimension;
        while (--numPts >= 0) {
            dstPts[dstOff] -= Math.floor(dstPts[dstOff]);
            dstOff += dimension;
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff,
                          final float[] dstPts, int dstOff, int numPts)
    {
        System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts * dimension);
        dstOff += wraparoundDimension;
        while (--numPts >= 0) {
            dstPts[dstOff] -= Math.floor(dstPts[dstOff]);
            dstOff += dimension;
        }
    }

    /**
     * Returns the identity transform as the pseudo-inverse of this transform.
     * We do not return another {@code WraparoundTransform} for three reasons:
     *
     * <ol>
     *   <li>The inverse wraparound would work on a different range of values, but we do not know that range.</li>
     *   <li>Even if we knew the original range of values, creating the inverse transform would require the affine
     *       transforms before and after {@code WraparoundTransform} to be different; it would not be their normal
     *       inverse. This is impractical, especially since the transform matrices may have been multiplied with
     *       other affine transforms.</li>
     *   <li>Even if we were able to build the inverse {@code WraparoundTransform}, it would not necessarily be
     *       appropriate. For example in "ProjectedCRS → BaseCRS → GeographicCRS" operation chain, wraparound
     *       may happen after the geographic CRS. But in the "GeographicCRS → BaseCRS → ProjectedCRS" inverse
     *       operation, the wraparound would be between BaseCRS and ProjectedCRS, which is often not needed.</li>
     * </ol>
     */
    @Override
    public MathTransform inverse() {
        return MathTransforms.identity(dimension);
    }

    /**
     * If the other transform is also a {@code WraparoundTransform} for the same dimension, then there
     * is no need to concatenate two consecutive such transforms.
     */
    @Override
    protected MathTransform tryConcatenate(boolean applyOtherFirst, MathTransform other, MathTransformFactory factory) {
        return equals(other, null) ? this : null;
    }

    /**
     * Compares this transform with the given object for equality.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object instanceof WraparoundTransform) {
            final WraparoundTransform other = (WraparoundTransform) object;
            return other.dimension == dimension && other.wraparoundDimension == wraparoundDimension;
        }
        return false;
    }

    /**
     * Computes a hash code value for this transform.
     */
    @Override
    protected int computeHashCode() {
        return dimension * 31 + wraparoundDimension;
    }
}
