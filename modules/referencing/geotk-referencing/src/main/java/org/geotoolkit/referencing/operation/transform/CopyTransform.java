/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation.transform;

import java.util.Arrays;
import java.io.Serializable;
import java.awt.geom.Point2D;

import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.referencing.operation.provider.Affine;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.referencing.operation.matrix.XMatrix;


/**
 * A transform which copy the ordinates in the source array to different locations in the target
 * array. This is a special case of {@link ProjectiveTransform} where the matrix coefficients
 * are zero everywhere, except one value by row which is set to 1 and is not the translation term.
 * Those transforms are used for swapping axis order, or selecting the dimension to retain when
 * converting from a large dimension to a smaller one. This transform has the particularity to
 * involve no floating point operation - just copy of values with no change - and consequently
 * works well with NaN ordinate values.
 * <p>
 * We do not provide a subclass for the 2D case because our policy is to use
 * an {@link java.awt.geom.AffineTransform} for every 2D affine conversions.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.08
 * @module
 */
@Immutable
final class CopyTransform extends AbstractMathTransform implements LinearTransform, Serializable {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 5457032501070947956L;

    /**
     * The dimension of source coordinates.
     * Must be greater than the highest value in {@link #indices}.
     */
    private final int srcDim;

    /**
     * The indices of ordinates to copy in the source array. The length of this array
     * is the target dimension.
     */
    private final int[] indices;

    /**
     * The inverse transform. Will be created only when first needed.
     */
    private transient MathTransform inverse;

    /**
     * Creates a new transform.
     *
     * @param srcDim The dimension of source coordinates.
     *        Must be greater than the highest value in {@code indices}.
     * @param indices The indices of ordinates to copy in the source array.
     *        The length of this array is the target dimension.
     */
    CopyTransform(final int srcDim, final int... indices) {
        this.srcDim  = srcDim;
        this.indices = indices;
    }

    /**
     * If a transform can be created from the given matrix, returns it.
     * Otherwise returns {@code null}.
     */
    static CopyTransform create(final Matrix matrix) {
        final int srcDim = matrix.getNumCol() - 1;
        final int dstDim = matrix.getNumRow() - 1;
        for (int i=0; i<=srcDim; i++) {
            if (matrix.getElement(dstDim, i) != (i == srcDim ? 1 : 0)) {
                // Not an affine transform.
                return null;
            }
        }
        final int[] indices = new int[dstDim];
        for (int j=0; j<dstDim; j++) {
            if (matrix.getElement(j, srcDim) != 0) {
                // The matrix has translation terms.
                return null;
            }
            boolean found = false;
            for (int i=0; i<srcDim; i++) {
                final double elt = matrix.getElement(j, i);
                if (elt != 0) {
                    if (elt != 1 || found) {
                        // Not a simple copy operation.
                        return null;
                    }
                    indices[j] = i;
                    found = true;
                }
            }
            if (!found) {
                // Target ordinate inconditionaly set to 0 (not a copy).
                return null;
            }
        }
        return new CopyTransform(srcDim, indices);
    }

    /**
     * Gets the dimension of input points.
     */
    @Override
    public final int getSourceDimensions() {
        return srcDim;
    }

    /**
     * Gets the dimension of output points.
     */
    @Override
    public final int getTargetDimensions() {
        return indices.length;
    }

    /**
     * Tests whether this transform does not move any points.
     */
    @Override
    public final boolean isIdentity() {
        if (srcDim != indices.length) {
            return false;
        }
        for (int i=indices.length; --i>=0;) {
            if (indices[i] != i) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests whether this transform does not move any points by using the provided tolerance.
     */
    @Override
    public final boolean isIdentity(double tolerance) {
        return isIdentity();
    }

    /**
     * Transforms a single coordinate point.
     */
    @Override
    protected final void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff) {
        transform(srcPts, srcOff, dstPts, dstOff, 1);
    }

    /**
     * Transforms an array of floating point coordinates by this matrix.
     */
    @Override
    public final void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        final int srcDim, dstDim;
        final int[] indices = this.indices;
        int srcInc = srcDim = this.srcDim;
        int dstInc = dstDim = indices.length;
        if (srcPts == dstPts) {
            switch (IterationStrategy.suggest(srcOff, srcDim, dstOff, dstDim, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    srcOff += (numPts-1) * srcDim;
                    dstOff += (numPts-1) * dstDim;
                    srcInc = -srcInc;
                    dstInc = -dstInc;
                    break;
                }
                default: {
                    srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*srcDim);
                    srcOff = 0;
                    break;
                }
            }
        }
        if (srcPts != dstPts) {
            // Optimisation for a common case.
            while (--numPts >= 0) {
                for (int i=0; i<dstDim; i++) {
                    dstPts[dstOff++] = srcPts[srcOff + indices[i]];
                }
                srcOff += srcDim;
            }
        } else {
            // General case: there is a risk that two coordinates overlap.
            final double[] buffer = new double[dstDim];
            while (--numPts >= 0) {
                for (int i=0; i<dstDim; i++) {
                    buffer[i] = srcPts[srcOff + indices[i]];
                }
                System.arraycopy(buffer, 0, dstPts, dstOff, dstDim);
                srcOff += srcInc;
                dstOff += dstInc;
            }
        }
    }

    /**
     * Transforms an array of floating point coordinates by this matrix.
     */
    @Override
    public final void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
        final int srcDim, dstDim;
        final int[] indices = this.indices;
        int srcInc = srcDim = this.srcDim;
        int dstInc = dstDim = indices.length;
        if (srcPts == dstPts) {
            switch (IterationStrategy.suggest(srcOff, srcDim, dstOff, dstDim, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    srcOff += (numPts-1) * srcDim;
                    dstOff += (numPts-1) * dstDim;
                    srcInc = -srcInc;
                    dstInc = -dstInc;
                    break;
                }
                default: {
                    srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*srcDim);
                    srcOff = 0;
                    break;
                }
            }
        }
        if (srcPts != dstPts) {
            // Optimisation for a common case.
            while (--numPts >= 0) {
                for (int i=0; i<dstDim; i++) {
                    dstPts[dstOff++] = srcPts[srcOff + indices[i]];
                }
                srcOff += srcDim;
            }
        } else {
            // General case: there is a risk that two coordinates overlap.
            final float[] buffer = new float[dstDim];
            while (--numPts >= 0) {
                for (int i=0; i<dstDim; i++) {
                    buffer[i] = srcPts[srcOff + indices[i]];
                }
                System.arraycopy(buffer, 0, dstPts, dstOff, dstDim);
                srcOff += srcInc;
                dstOff += dstInc;
            }
        }
    }

    /**
     * Transforms an array of floating point coordinates by this matrix.
     */
    @Override
    public final void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
        final int[] indices = this.indices;
        final int srcDim = this.srcDim;
        final int dstDim = indices.length;
        while (--numPts >= 0) {
            for (int i=0; i<dstDim; i++) {
                dstPts[dstOff++] = (float) srcPts[srcOff + indices[i]];
            }
            srcOff += srcDim;
        }
    }

    /**
     * Transforms an array of floating point coordinates by this matrix.
     */
    @Override
    public final void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        final int[] indices = this.indices;
        final int srcDim = this.srcDim;
        final int dstDim = indices.length;
        while (--numPts >= 0) {
            for (int i=0; i<dstDim; i++) {
                dstPts[dstOff++] = srcPts[srcOff + indices[i]];
            }
            srcOff += srcDim;
        }
    }

    /**
     * Returns the parameter descriptors for this math transform.
     */
    @Override
    public final ParameterDescriptorGroup getParameterDescriptors() {
        return Affine.PARAMETERS;
    }

    /**
     * Returns the matrix elements as a group of parameters values.
     */
    @Override
    public final ParameterValueGroup getParameterValues() {
        return ProjectiveTransform.getParameterValues(getMatrix());
    }

    /**
     * Returns the matrix.
     */
    @Override
    public final Matrix getMatrix() {
        final int dstDim = indices.length;
        final GeneralMatrix matrix = new GeneralMatrix(dstDim+1, srcDim+1);
        for (int j=0; j<dstDim; j++) {
            matrix.setElement(j, j, 0);
            matrix.setElement(j, indices[j], 1);
        }
        if (srcDim > dstDim) {
            matrix.setElement(dstDim, dstDim, 0);
            matrix.setElement(dstDim, srcDim, 1);
        }
        assert equals(create(matrix)) : matrix;
        return matrix;
    }

    /**
     * Gets the derivative of this transform at a point.
     * For a matrix transform, the derivative is the same everywhere.
     */
    @Override
    public final Matrix derivative(final DirectPosition point) {
        final GeneralMatrix matrix = new GeneralMatrix(indices.length, srcDim);
        for (int j=0; j<indices.length; j++) {
            matrix.setElement(j, j, 0);
            matrix.setElement(j, indices[j], 1);
        }
        return matrix;
    }

    /**
     * Gets the derivative of this transform at a point.
     * For a matrix transform, the derivative is the same everywhere.
     */
    @Override
    public final Matrix derivative(final Point2D point) {
        return derivative((DirectPosition) null);
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public synchronized MathTransform inverse() throws NoninvertibleTransformException {
        if (inverse == null) {
            CopyTransform copyInverse = this;
            if (!isIdentity()) {
                final int srcDim = this.srcDim;
                final int dstDim = indices.length;
                final int[] reverse = new int[srcDim];
                Arrays.fill(reverse, -1);
                for (int i=dstDim; --i>=0;) {
                    reverse[indices[i]] = i;
                }
                /*
                 * Check if there is any unassigned dimension. In such case,
                 * delegates to the generic ProjectiveTransform with a matrix
                 * which set the missing values to NaN.
                 */
                for (int j=srcDim; --j>=0;) {
                    if (reverse[j] < 0) {
                        final XMatrix matrix = MatrixFactory.create(srcDim + 1, dstDim + 1);
                        for (j=0; j<srcDim; j++) {
                            if (j < dstDim) {
                                matrix.setElement(j, j, 0);
                            }
                            final int i = reverse[j];
                            if (i >= 0) {
                                matrix.setElement(j, i, 1);
                            } else {
                                matrix.setElement(j, dstDim, Double.NaN);
                            }
                        }
                        matrix.setElement(srcDim, dstDim, 1);
                        return inverse = ProjectiveTransform.create(matrix);
                    }
                }
                /*
                 * At this point, we known that we can create the inverse transform.
                 */
                if (!Arrays.equals(reverse, indices)) {
                    copyInverse = new CopyTransform(indices.length, reverse);
                    copyInverse.inverse = this;
                }
            }
            inverse = copyInverse;
        }
        return inverse;
    }

    /**
     * Returns a hash value for this transform.
     */
    @Override
    public final int hashCode() {
        return Arrays.hashCode(indices) + (31*srcDim) ^ (int) serialVersionUID;
    }

    /**
     * Compares the specified object with this math transform for equality.
     */
    @Override
    public final boolean equals(final Object object) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object)) {
            final CopyTransform that = (CopyTransform) object;
            return srcDim == that.srcDim && Arrays.equals(indices, that.indices);
        }
        return false;
    }
}
