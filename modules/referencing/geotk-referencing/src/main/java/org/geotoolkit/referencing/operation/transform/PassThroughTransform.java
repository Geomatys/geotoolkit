/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.io.Serializable;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.geometry.DirectPosition;

import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.internal.referencing.DirectPositionView;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.IterationStrategy;

import static org.geotoolkit.util.Utilities.hash;
import static org.apache.sis.util.ArgumentChecks.ensureDimensionMatches;
import static org.geotoolkit.referencing.operation.matrix.Matrices.*;


/**
 * Transform which passes through a subset of ordinates to another transform.
 * This allows transforms to operate on a subset of ordinates. For example giving
 * (<var>latitude</var>, <var>longitude</var>, <var>height</var>) coordinates,
 * {@code PassThroughTransform} can convert the height values from feet to
 * meters without affecting the latitude and longitude values.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see DimensionFilter
 *
 * @since 1.2
 * @module
 */
@Immutable
public class PassThroughTransform extends AbstractMathTransform implements Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1673997634240223449L;

    /**
     * Index of the first affected ordinate.
     */
    protected final int firstAffectedOrdinate;

    /**
     * Number of unaffected ordinates after the affected ones.
     * Always 0 when used through the strict OpenGIS API.
     */
    protected final int numTrailingOrdinates;

    /**
     * The sub transform.
     *
     * @see #getSubTransform
     */
    protected final MathTransform subTransform;

    /**
     * The inverse transform. This field will be computed only when needed,
     * but is part of serialization in order to avoid rounding error.
     */
    PassThroughTransform inverse;

    /**
     * Creates a pass through transform.
     *
     * @param firstAffectedOrdinate Index of the first affected ordinate.
     * @param subTransform The sub transform.
     * @param numTrailingOrdinates Number of trailing ordinates to pass through.
     *
     * @see #create
     */
    protected PassThroughTransform(final int firstAffectedOrdinate,
                                   final MathTransform subTransform,
                                   final int numTrailingOrdinates)
    {
        ArgumentChecks.ensurePositive("firstAffectedOrdinate", firstAffectedOrdinate);
        ArgumentChecks.ensurePositive("numTrailingOrdinates",  numTrailingOrdinates);
        if (subTransform instanceof PassThroughTransform) {
            final PassThroughTransform passThrough = (PassThroughTransform) subTransform;
            this.firstAffectedOrdinate = passThrough.firstAffectedOrdinate + firstAffectedOrdinate;
            this.numTrailingOrdinates  = passThrough.numTrailingOrdinates  + numTrailingOrdinates;
            this.subTransform          = passThrough.subTransform;
        }  else {
            this.firstAffectedOrdinate = firstAffectedOrdinate;
            this.numTrailingOrdinates  = numTrailingOrdinates;
            this.subTransform          = subTransform;
        }
    }

    /**
     * Creates a transform which passes through a subset of ordinates to another transform.
     * This method returns a transform having the following dimensions:
     *
     * {@preformat java
     *     Source: firstAffectedOrdinate + subTransform.getSourceDimensions() + numTrailingOrdinates
     *     Target: firstAffectedOrdinate + subTransform.getTargetDimensions() + numTrailingOrdinates
     * }
     *
     * Affected ordinates will range from {@code firstAffectedOrdinate} inclusive to
     * {@code dimTarget - numTrailingOrdinates} exclusive.
     *
     * @param  firstAffectedOrdinate Index of the first affected ordinate.
     * @param  subTransform The sub transform.
     * @param  numTrailingOrdinates Number of trailing ordinates to pass through.
     * @return A pass through transform.
     */
    public static MathTransform create(final int firstAffectedOrdinate,
                                       final MathTransform subTransform,
                                       final int numTrailingOrdinates)
    {
        ArgumentChecks.ensurePositive("firstAffectedOrdinate", firstAffectedOrdinate);
        ArgumentChecks.ensurePositive("numTrailingOrdinates",  numTrailingOrdinates);
        if (firstAffectedOrdinate == 0 && numTrailingOrdinates == 0) {
            return subTransform;
        }
        /*
         * Optimizes the "Identity transform" case.
         */
        if (subTransform.isIdentity()) {
            final int dimension = subTransform.getSourceDimensions();
            if (dimension == subTransform.getTargetDimensions()) {
                return ProjectiveTransform.identity(firstAffectedOrdinate + dimension + numTrailingOrdinates);
            }
        }
        /*
         * Special case for transformation backed by a matrix. Is is possible to use a
         * new matrix for such transform, instead of wrapping the sub-transform into a
         * PassThroughTransform object. It is faster and easier to concatenate.
         */
        if (subTransform instanceof LinearTransform) {
            GeneralMatrix matrix = toGeneralMatrix(((LinearTransform) subTransform).getMatrix());
            matrix = expand(matrix, firstAffectedOrdinate, numTrailingOrdinates, 1);
            return ProjectiveTransform.create(matrix);
        }
        /*
         * Constructs the general PassThroughTransform object. An optimization is done right in
         * the constructor for the case where the sub-transform is already a PassThroughTransform.
         */
        int dim = subTransform.getSourceDimensions();
        if (subTransform.getTargetDimensions() == dim) {
            dim += firstAffectedOrdinate + numTrailingOrdinates;
            if (dim == 2) {
                return new PassThroughTransform2D(firstAffectedOrdinate, subTransform, numTrailingOrdinates);
            }
        }
        return new PassThroughTransform(firstAffectedOrdinate, subTransform, numTrailingOrdinates);
    }

    /**
     * If the given matrix to be concatenated to this transform, can be concatenated to the
     * sub-transform instead, returns the matrix to be concatenated to the sub-transform.
     * Otherwise returns {@code null}.
     * <p>
     * This method assumes that the matrix size is compatible with this transform source
     * dimension. It should have been verified by the caller.
     */
    final Matrix toSubMatrix(final Matrix matrix) {
        final int numRow = matrix.getNumRow();
        final int numCol = matrix.getNumCol();
        if (numRow != numCol) {
            // Current implementation requires a square matrix.
            return null;
        }
        final int subDim = subTransform.getSourceDimensions();
        final Matrix sub = Matrices.create(subDim + 1);
        /*
         * Ensure that every dimensions which are scaled by the affine transform are one
         * of the dimensions modified by the sub-transform, and not any other dimension.
         */
        for (int j=numRow; --j>=0;) {
            final int sj = j - firstAffectedOrdinate;
            for (int i=numCol; --i>=0;) {
                final double element = matrix.getElement(j, i);
                if (sj >= 0 && sj < subDim) {
                    final int si;
                    final boolean pass;
                    if (i == numCol-1) { // Translation term (last column)
                        si = subDim;
                        pass = true;
                    } else { // Any term other than translation.
                        si = i - firstAffectedOrdinate;
                        pass = (si >= 0 && si < subDim);
                    }
                    if (pass) {
                        sub.setElement(sj, si, element);
                        continue;
                    }
                }
                if (element != (i == j ? 1 : 0)) {
                    // Found a dimension which perform some scaling or translation.
                    return null;
                }
            }
        }
        return sub;
    }

    /**
     * Returns the sub transform.
     *
     * @return The sub transform.
     *
     * @since 2.2
     */
    public final MathTransform getSubTransform() {
        return subTransform;
    }

    /**
     * Ordered sequence of positive integers defining the positions in a coordinate
     * tuple of the coordinates affected by this pass-through transform. The returned
     * index are for source coordinates.
     *
     * @return The modified coordinates.
     */
    public final int[] getModifiedCoordinates() {
        final int[] index = new int[subTransform.getSourceDimensions()];
        for (int i=0; i<index.length; i++) {
            index[i] = i + firstAffectedOrdinate;
        }
        return index;
    }

    /**
     * Gets the dimension of input points.
     */
    @Override
    public final int getSourceDimensions() {
        return firstAffectedOrdinate + subTransform.getSourceDimensions() + numTrailingOrdinates;
    }

    /**
     * Gets the dimension of output points.
     */
    @Override
    public final int getTargetDimensions() {
        return firstAffectedOrdinate + subTransform.getTargetDimensions() + numTrailingOrdinates;
    }

    /**
     * Tests whether this transform does not move any points.
     */
    @Override
    public boolean isIdentity() {
        return subTransform.isIdentity();
    }

    /**
     * Transforms a single coordinate in a list of ordinal values, and opportunistically
     * computes the transform derivative if requested.
     *
     * @throws TransformException If the {@linkplain #subTransform sub-transform} failed.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate) throws TransformException
    {
        Matrix derivative = null;
        if (derivate) {
            derivative = derivative(new DirectPositionView(srcPts, srcOff, getSourceDimensions()));
        }
        if (dstPts != null) {
            transform(srcPts, srcOff, dstPts, dstOff, 1);
        }
        return derivative;
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     *
     * @throws TransformException If the {@linkplain #subTransform sub-transform} failed.
     */
    @Override
    public void transform(double[] srcPts, int srcOff, final double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        final int subDimSource = subTransform.getSourceDimensions();
        final int subDimTarget = subTransform.getTargetDimensions();
        int srcStep = numTrailingOrdinates;
        int dstStep = numTrailingOrdinates;
        if (srcPts == dstPts) {
            final int add = firstAffectedOrdinate + numTrailingOrdinates;
            final int dimSource = subDimSource + add;
            final int dimTarget = subDimTarget + add;
            switch (IterationStrategy.suggest(srcOff, dimSource, dstOff, dimTarget, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    srcOff += (numPts - 1) * dimSource;
                    dstOff += (numPts - 1) * dimTarget;
                    srcStep -= 2*dimSource;
                    dstStep -= 2*dimTarget;
                    break;
                }
                default: {
                    srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*dimSource);
                    srcOff = 0;
                }
            }
        }
        while (--numPts >= 0) {
            System.arraycopy(      srcPts, srcOff,
                                   dstPts, dstOff,   firstAffectedOrdinate);
            subTransform.transform(srcPts, srcOff += firstAffectedOrdinate,
                                   dstPts, dstOff += firstAffectedOrdinate, 1);
            System.arraycopy(      srcPts, srcOff += subDimSource,
                                   dstPts, dstOff += subDimTarget, numTrailingOrdinates);
            srcOff += srcStep;
            dstOff += dstStep;
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     *
     * @throws TransformException If the {@linkplain #subTransform sub-transform} failed.
     */
    @Override
    public void transform(float[] srcPts, int srcOff, final float[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        final int subDimSource = subTransform.getSourceDimensions();
        final int subDimTarget = subTransform.getTargetDimensions();
        int srcStep = numTrailingOrdinates;
        int dstStep = numTrailingOrdinates;
        if (srcPts == dstPts) {
            final int add = firstAffectedOrdinate + numTrailingOrdinates;
            final int dimSource = subDimSource + add;
            final int dimTarget = subDimTarget + add;
            switch (IterationStrategy.suggest(srcOff, dimSource, dstOff, dimTarget, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    srcOff += (numPts - 1) * dimSource;
                    dstOff += (numPts - 1) * dimTarget;
                    srcStep -= 2*dimSource;
                    dstStep -= 2*dimTarget;
                    break;
                }
                default: {
                    srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*dimSource);
                    srcOff = 0;
                }
            }
        }
        while (--numPts >= 0) {
            System.arraycopy(      srcPts, srcOff,
                                   dstPts, dstOff,   firstAffectedOrdinate);
            subTransform.transform(srcPts, srcOff += firstAffectedOrdinate,
                                   dstPts, dstOff += firstAffectedOrdinate, 1);
            System.arraycopy(      srcPts, srcOff += subDimSource,
                                   dstPts, dstOff += subDimTarget, numTrailingOrdinates);
            srcOff += srcStep;
            dstOff += dstStep;
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     *
     * @throws TransformException If the {@linkplain #subTransform sub-transform} failed.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff, final float[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        final int subDimSource = subTransform.getSourceDimensions();
        final int subDimTarget = subTransform.getTargetDimensions();
        while (--numPts >= 0) {
            for (int i=0; i<firstAffectedOrdinate; i++) {
                dstPts[dstOff++] = (float) srcPts[srcOff++];
            }
            subTransform.transform(srcPts, srcOff, dstPts, dstOff, 1);
            srcOff += subDimSource;
            dstOff += subDimTarget;
            for (int i=0; i<numTrailingOrdinates; i++) {
                dstPts[dstOff++] = (float) srcPts[srcOff++];
            }
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     *
     * @throws TransformException If the {@linkplain #subTransform sub-transform} failed.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff, final double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        final int subDimSource = subTransform.getSourceDimensions();
        final int subDimTarget = subTransform.getTargetDimensions();
        while (--numPts >= 0) {
            for (int i=0; i<firstAffectedOrdinate; i++) {
                dstPts[dstOff++] = srcPts[srcOff++];
            }
            subTransform.transform(srcPts, srcOff, dstPts, dstOff, 1);
            srcOff += subDimSource;
            dstOff += subDimTarget;
            for (int i=0; i<numTrailingOrdinates; i++) {
                dstPts[dstOff++] = srcPts[srcOff++];
            }
        }
    }

    /**
     * Gets the derivative of this transform at a point.
     */
    @Override
    public Matrix derivative(final DirectPosition point) throws TransformException {
        final int nSkipped = firstAffectedOrdinate + numTrailingOrdinates;
        final int transDim = subTransform.getSourceDimensions();
        ensureDimensionMatches("point", transDim + nSkipped, point);
        final GeneralDirectPosition subPoint = new GeneralDirectPosition(transDim);
        for (int i=0; i<transDim; i++) {
            subPoint.ordinates[i] = point.getOrdinate(i + firstAffectedOrdinate);
        }
        return expand(toGeneralMatrix(subTransform.derivative(subPoint)),
                firstAffectedOrdinate, numTrailingOrdinates, 0);
    }

    /**
     * Creates a pass through transform from a matrix. This method is invoked when the
     * sub-transform can be express as a matrix. It is also invoked for computing the
     * matrix returned by {@link #derivative}.
     *
     * @param subMatrix The sub-transform as a matrix.
     * @param firstAffectedOrdinate Index of the first affected ordinate.
     * @param numTrailingOrdinates Number of trailing ordinates to pass through.
     * @param affine 0 if the matrix do not contains translation terms, or 1 if
     *        the matrix is an affine transform with translation terms.
     */
    private static GeneralMatrix expand(final GeneralMatrix subMatrix,
                                        final int firstAffectedOrdinate,
                                        final int numTrailingOrdinates,
                                        final int affine)
    {
        final int nSkipped = firstAffectedOrdinate + numTrailingOrdinates;
        final int numRow   = subMatrix.getNumRow() - affine;
        final int numCol   = subMatrix.getNumCol() - affine;
        final GeneralMatrix matrix = new GeneralMatrix(
                numRow + (nSkipped + affine),
                numCol + (nSkipped + affine));
        matrix.setZero();
        /*                      ┌                  ┐
         *  Set UL part to 1:   │ 1  0             │
         *                      │ 0  1             │
         *                      │                  │
         *                      │                  │
         *                      │                  │
         *                      └                  ┘
         */
        for (int j=0; j<firstAffectedOrdinate; j++) {
            matrix.setElement(j, j, 1);
        }
        /*                      ┌                  ┐
         *  Set central part:   │ 1  0  0  0  0  0 │
         *                      │ 0  1  0  0  0  0 │
         *                      │ 0  0  ?  ?  ?  0 │
         *                      │ 0  0  ?  ?  ?  0 │
         *                      │                  │
         *                      └                  ┘
         */
        subMatrix.copySubMatrix(0, 0, numRow, numCol,
                                firstAffectedOrdinate, firstAffectedOrdinate, matrix);
        /*                      ┌                  ┐
         *  Set LR part to 1:   │ 1  0  0  0  0  0 │
         *                      │ 0  1  0  0  0  0 │
         *                      │ 0  0  ?  ?  ?  0 │
         *                      │ 0  0  ?  ?  ?  0 │
         *                      │ 0  0  0  0  0  1 │
         *                      └                  ┘
         */
        final int offset = numCol-numRow;
        final int numRowOut = numRow + nSkipped;
        for (int j=numRowOut-numTrailingOrdinates; j<numRowOut; j++) {
            matrix.setElement(j, j+offset, 1);
        }
        if (affine != 0) {
            // Copy the translation terms in the last column.
            subMatrix.copySubMatrix(0, numCol, numRow, affine,
                                    firstAffectedOrdinate, numCol+nSkipped, matrix);
            // Copy the last row as a safety, but it should contains only 0.
            subMatrix.copySubMatrix(numRow, 0, affine, numCol,
                                    numRow+nSkipped, firstAffectedOrdinate, matrix);
            // Copy the lower right corner, which should contains only 1.
            subMatrix.copySubMatrix(numRow, numCol, affine, affine,
                                    numRow+nSkipped, numCol+nSkipped, matrix);
        }
        return matrix;
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public synchronized MathTransform inverse() throws NoninvertibleTransformException {
        if (inverse == null) {
            inverse = new PassThroughTransform(
                    firstAffectedOrdinate, subTransform.inverse(), numTrailingOrdinates);
            inverse.inverse = this;
        }
        return inverse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        // Note that numTrailingOrdinates is related to source and
        // target dimensions, which are computed by the super-class.
        return hash(subTransform, hash(firstAffectedOrdinate, super.computeHashCode()));
    }

    /**
     * Compares the specified object with this math transform for equality.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (super.equals(object, mode)) {
            final PassThroughTransform that = (PassThroughTransform) object;
            return this.firstAffectedOrdinate == that.firstAffectedOrdinate &&
                   this.numTrailingOrdinates  == that.numTrailingOrdinates  &&
                   Utilities.deepEquals(this.subTransform, that.subTransform, mode);
        }
        return false;
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#PASSTHROUGH_MT"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name, which is {@code "PASSTHROUGH_MT"}.
     *
     * @todo The {@link #numTrailingOrdinates} parameter is not part of OpenGIS specification.
     *       We should returns a more complex WKT when {@code numTrailingOrdinates != 0},
     *       using an affine transform to change the coordinates order.
     */
    @Override
    public String formatTo(final Formatter formatter) {
        formatter.append(firstAffectedOrdinate);
        if (numTrailingOrdinates != 0) {
            formatter.append(numTrailingOrdinates);
            formatter.setInvalidWKT(PassThroughTransform.class, null);
        }
        formatter.append(subTransform);
        return "PASSTHROUGH_MT";
    }
}
