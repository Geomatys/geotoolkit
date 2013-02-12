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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.operation.transform;

import java.util.Arrays;
import java.io.Serializable;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import net.jcip.annotations.Immutable;

import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.parameter.MatrixParameters;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.provider.Affine;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;

import static org.geotoolkit.util.Utilities.hash;


/**
 * A usually affine, or otherwise a projective transform. A projective transform is capable of
 * mapping an arbitrary quadrilateral into another arbitrary quadrilateral, while preserving the
 * straightness of lines. In the special case where the transform is affine, the parallelism of
 * lines in the source is preserved in the output.
 * <p>
 * Such a coordinate transformation can be represented by a square {@linkplain GeneralMatrix matrix}
 * of arbitrary size. Point coordinates must have a dimension equal to the matrix size minus one.
 * For example a square matrix of size 4&times;4 is used for transforming three-dimensional
 * coordinates. The transformed points {@code (x',y',z')} are computed as below (note that this
 * computation is similar to {@link javax.media.jai.PerspectiveTransform} in
 * <cite>Java Advanced Imaging</cite>):
 *
 * <table><tr><td nowrap>
 * {@preformat text
 *     x' = u/t
 *     y' = v/t
 *     y' = w/t
 * }
 * </td><td><blockquote>
 * where {@code (u,v,w)} are obtained by:
 * </blockquote></td><td nowrap>
 * {@preformat text
 *     ┌   ┐     ┌                    ┐ ┌   ┐
 *     │ u │     │ m00  m01  m02  m03 │ │ x │
 *     │ v │  =  │ m10  m11  m12  m13 │ │ y │
 *     │ w │     │ m20  m21  m22  m23 │ │ z │
 *     │ t │     │ m30  m31  m32  m33 │ │ 1 │
 *     └   ┘     └                    ┘ └   ┘
 * }
 * </td></tr></table>
 *
 * In the special case of an affine transform, the last row contains only zero
 * values except in the last column, which contains 1.
 * <p>
 * See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Affine} -
 *       "<cite>Affine general parametric transformation</cite>" (EPSG 9624) with default matrix size of
 *       {@value org.geotoolkit.parameter.MatrixParameterDescriptors#DEFAULT_MATRIX_SIZE}&times;{@value
 *       org.geotoolkit.parameter.MatrixParameterDescriptors#DEFAULT_MATRIX_SIZE}.</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LongitudeRotation} -
 *       "<cite>Longitude rotation</cite>" (EPSG 9601)</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see javax.media.jai.PerspectiveTransform
 * @see java.awt.geom.AffineTransform
 * @see <A HREF="http://mathworld.wolfram.com/AffineTransformation.html">Affine transformation on MathWorld</A>
 *
 * @since 1.2
 * @module
 */
@Immutable
public class ProjectiveTransform extends AbstractMathTransform implements LinearTransform, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -2104496465933824935L;

    /**
     * Identity transforms for dimensions ranging from to 0 to 7.
     * Elements in this array will be created only when first requested.
     *
     * @see #identity(int)
     */
    private static final LinearTransform[] IDENTITIES = new LinearTransform[8];

    /**
     * The number of rows.
     */
    private final int numRow;

    /**
     * The number of columns.
     */
    private final int numCol;

    /**
     * Elements of the matrix. Column indices vary fastest.
     */
    private final double[] elt;

    /**
     * The inverse transform. Will be created only when first needed. This field is part of
     * the serialization form in order to avoid rounding errors if a user asks for the inverse
     * of the inverse (i.e. the original transform) after deserialization.
     */
    AbstractMathTransform inverse;

    /**
     * Constructs a transform from the specified matrix.
     * The matrix is usually square and affine, but this is not be enforced.
     *
     * @param matrix The matrix.
     */
    protected ProjectiveTransform(final Matrix matrix) {
        numRow = matrix.getNumRow();
        numCol = matrix.getNumCol();
        elt = new double[numRow * numCol];
        int index = 0;
        for (int j=0; j<numRow; j++) {
            for (int i=0; i<numCol; i++) {
                elt[index++] = matrix.getElement(j,i);
            }
        }
    }

    /**
     * Creates a transform for the specified matrix.
     * The matrix is usually square and affine, but this is not be enforced.
     *
     * @param matrix The affine transform as a matrix.
     * @return The transform for the given matrix.
     *
     * @see MathTransforms#linear(Matrix)
     */
    public static LinearTransform create(final Matrix matrix) {
        final int sourceDimension = matrix.getNumCol() - 1;
        final int targetDimension = matrix.getNumRow() - 1;
        if (sourceDimension == targetDimension) {
            if (matrix.isIdentity()) {
                return identity(sourceDimension);
            }
            if (Matrices.isAffine(matrix)) {
                switch (sourceDimension) {
                    case 1: return LinearTransform1D.create(matrix.getElement(0,0), matrix.getElement(0,1));
                    case 2: return MathTransforms.linear(Matrices.toAffineTransform(matrix));
                }
            } else if (sourceDimension == 2) {
                return new ProjectiveTransform2D(matrix);
            }
        }
        final LinearTransform candidate = CopyTransform.create(matrix);
        if (candidate != null) {
            return candidate;
        }
        return new ProjectiveTransform(matrix);
    }

    /**
     * Returns an identity transform of the specified dimension. In the special case of
     * dimension 1 and 2, this method returns instances of {@link LinearTransform1D} or
     * {@link AffineTransform2D} respectively.
     *
     * @param dimension The dimension of the transform to be returned.
     * @return An identity transform of the specified dimension.
     *
     * @since 3.20
     *
     * @see MathTransforms#identity(int)
     */
    public static LinearTransform identity(final int dimension) {
        LinearTransform candidate;
        synchronized (IDENTITIES) {
            if (dimension < IDENTITIES.length) {
                candidate = IDENTITIES[dimension];
                if (candidate != null) {
                    return candidate;
                }
            }
            switch (dimension) {
                case 1:  candidate = LinearTransform1D.IDENTITY;       break;
                case 2:  candidate = new AffineTransform2D();          break;
                default: candidate = new IdentityTransform(dimension); break;
            }
            if (dimension < IDENTITIES.length) {
                IDENTITIES[dimension] = candidate;
            }
        }
        return candidate;
    }

    /**
     * Returns the parameter descriptors for this math transform.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return Affine.PARAMETERS;
    }

    /**
     * Returns the matrix elements as a group of parameters values. The number of parameters
     * depends on the matrix size. Only matrix elements different from their default value
     * will be included in this group.
     *
     * @param  matrix The matrix to returns as a group of parameters.
     * @return A copy of the parameter values for this math transform.
     */
    static ParameterValueGroup getParameterValues(final Matrix matrix) {
        final MatrixParameters values;
        values = (MatrixParameters) Affine.PARAMETERS.createValue();
        values.setMatrix(matrix);
        return values;
    }

    /**
     * Returns the matrix elements as a group of parameters values. The number of parameters
     * depends on the matrix size. Only matrix elements different from their default value
     * will be included in this group.
     *
     * @return A copy of the parameter values for this math transform.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        return getParameterValues(getMatrix());
    }

    /**
     * Converts a single coordinate point in a list of ordinal values,
     * and optionally computes the derivative at that location.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate)
    {
        transform(srcPts, srcOff, dstPts, dstOff, 1);
        return derivate ? derivative((DirectPosition) null) : null;
    }

    /**
     * Transforms an array of floating point coordinates by this matrix. Point coordinates
     * must have a dimension equal to <code>{@link Matrix#getNumCol}-1</code>. For example,
     * for square matrix of size 4&times;4, coordinate points are three-dimensional and
     * stored in the arrays starting at the specified offset ({@code srcOff}) in the order
     * <code>[x<sub>0</sub>, y<sub>0</sub>, z<sub>0</sub>,
     *        x<sub>1</sub>, y<sub>1</sub>, z<sub>1</sub>...,
     *        x<sub>n</sub>, y<sub>n</sub>, z<sub>n</sub>]</code>.
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     * @param dstOff The offset to the location of the first transformed point that is stored
     *               in the destination array. The source and destination array sections can
     *               be overlaps.
     * @param numPts The number of points to be transformed.
     */
    @Override
    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        final int srcDim, dstDim;
        int srcInc = srcDim = numCol-1; // The last ordinate will be assumed equal to 1.
        int dstInc = dstDim = numRow-1;
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
        final double[] buffer = new double[numRow];
        while (--numPts >= 0) {
            int mix = 0;
            for (int j=0; j<numRow; j++) {
                double sum = elt[mix + srcDim];
                for (int i=0; i<srcDim; i++) {
                    final double e = elt[mix++];
                    if (e != 0) {
                        /*
                         * The purpose of the test for non-zero value is not performance
                         * (it is actually more likely to slow down the calculation), but
                         * to get a valid sum even if some source ordinates are NaN. This
                         * occurs when the ProjectiveTransform is used for excluding some
                         * dimensions, for example getting 2D points from 3D points. In
                         * such case, the fact that the excluded dimensions had NaN values
                         * should not force the retained dimensions to get NaN values.
                         */
                        sum += srcPts[srcOff + i] * e;
                    }
                }
                buffer[j] = sum;
                mix++;
            }
            final double w = buffer[dstDim];
            for (int j=0; j<dstDim; j++) {
                // 'w' is equal to 1 if the transform is affine.
                dstPts[dstOff + j] = buffer[j] / w;
            }
            srcOff += srcInc;
            dstOff += dstInc;
        }
    }

    /**
     * Transforms an array of floating point coordinates by this matrix. Point coordinates
     * must have a dimension equal to <code>{@link Matrix#getNumCol}-1</code>. For example,
     * for square matrix of size 4&times;4, coordinate points are three-dimensional and
     * stored in the arrays starting at the specified offset ({@code srcOff}) in the order
     * <code>[x<sub>0</sub>, y<sub>0</sub>, z<sub>0</sub>,
     *        x<sub>1</sub>, y<sub>1</sub>, z<sub>1</sub>...,
     *        x<sub>n</sub>, y<sub>n</sub>, z<sub>n</sub>]</code>.
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     * @param dstOff The offset to the location of the first transformed point that is stored
     *               in the destination array. The source and destination array sections can
     *               be overlaps.
     * @param numPts The number of points to be transformed.
     */
    @Override
    public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
        final int srcDim, dstDim;
        int srcInc = srcDim = numCol-1;
        int dstInc = dstDim = numRow-1;
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
        final double[] buffer = new double[numRow];
        while (--numPts >= 0) {
            int mix = 0;
            for (int j=0; j<numRow; j++) {
                double sum = elt[mix + srcDim];
                for (int i=0; i<srcDim; i++) {
                    final double e = elt[mix++];
                    if (e != 0) { // See comment in transform(double[], ...)
                        sum += srcPts[srcOff + i] * e;
                    }
                }
                buffer[j] = sum;
                mix++;
            }
            final double w = buffer[dstDim];
            for (int j=0; j<dstDim; j++) {
                dstPts[dstOff + j] = (float) (buffer[j] / w);
            }
            srcOff += srcInc;
            dstOff += dstInc;
        }
    }

    /**
     * Transforms an array of floating point coordinates by this matrix.
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     * @param dstOff The offset to the location of the first transformed point that is stored
     *               in the destination array.
     * @param numPts The number of points to be transformed.
     */
    @Override
    public void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
        final int srcDim = numCol-1;
        final int dstDim = numRow-1;
        final double[] buffer = new double[numRow];
        while (--numPts >= 0) {
            int mix = 0;
            for (int j=0; j<numRow; j++) {
                double sum = elt[mix + srcDim];
                for (int i=0; i<srcDim; i++) {
                    final double e = elt[mix++];
                    if (e != 0) { // See comment in transform(double[], ...)
                        sum += srcPts[srcOff + i] * e;
                    }
                }
                buffer[j] = sum;
                mix++;
            }
            final double w = buffer[dstDim];
            for (int j=0; j<dstDim; j++) {
                dstPts[dstOff++] = (float) (buffer[j] / w);
            }
            srcOff += srcDim;
        }
    }

    /**
     * Transforms an array of floating point coordinates by this matrix.
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     * @param dstOff The offset to the location of the first transformed point that is stored
     *               in the destination array.
     * @param numPts The number of points to be transformed.
     */
    @Override
    public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        final int srcDim = numCol-1;
        final int dstDim = numRow-1;
        final double[] buffer = new double[numRow];
        while (--numPts >= 0) {
            int mix = 0;
            for (int j=0; j<numRow; j++) {
                double sum = elt[mix + srcDim];
                for (int i=0; i<srcDim; i++) {
                    final double e = elt[mix++];
                    if (e != 0) { // See comment in transform(double[], ...)
                        sum += srcPts[srcOff + i] * e;
                    }
                }
                buffer[j] = sum;
                mix++;
            }
            final double w = buffer[dstDim];
            for (int j=0; j<dstDim; j++) {
                dstPts[dstOff++] = buffer[j] / w;
            }
            srcOff += srcDim;
        }
    }

    /**
     * Gets the derivative of this transform at a point.
     * For a matrix transform, the derivative is the same everywhere.
     *
     * @param point Ignored (can be {@code null}).
     */
    @Override
    public Matrix derivative(final Point2D point) {
        return derivative((DirectPosition) null);
    }

    /**
     * Gets the derivative of this transform at a point.
     * For a matrix transform, the derivative is the same everywhere.
     *
     * @param point Ignored (can be {@code null}).
     */
    @Override
    public Matrix derivative(final DirectPosition point) {
        final GeneralMatrix matrix = new GeneralMatrix(numRow, numCol, elt);
        matrix.setSize(numRow-1, numCol-1);
        return matrix;
    }

    /**
     * Returns a copy of the matrix.
     */
    @Override
    public Matrix getMatrix() {
        return Matrices.create(numRow, numCol, elt);
    }

    /**
     * Gets the dimension of input points.
     */
    @Override
    public int getSourceDimensions() {
        return numCol - 1;
    }

    /**
     * Gets the dimension of output points.
     */
    @Override
    public int getTargetDimensions() {
        return numRow - 1;
    }

    /**
     * Tests whether this transform does not move any points.
     */
    @Override
    public boolean isIdentity() {
        if (numRow != numCol) {
            return false;
        }
        int index = 0;
        for (int j=0; j<numRow; j++) {
            for (int i=0; i<numCol; i++) {
                if (elt[index++] != (i == j ? 1 : 0)) {
                    return false;
                }
            }
        }
        assert isIdentity(0);
        return true;
    }

    /**
     * Tests whether this transform does not move any points by using the provided tolerance.
     * This method work in the same way than
     * {@link org.geotoolkit.referencing.operation.matrix.XMatrix#isIdentity(double)}.
     *
     * @since 2.4
     */
    @Override
    public boolean isIdentity(double tolerance) {
        tolerance = Math.abs(tolerance);
        if (numRow != numCol) {
            return false;
        }
        int index=0;
        for (int j=0; j<numRow; j++) {
            for (int i=0; i<numCol; i++) {
                double e = elt[index++];
                if (i == j) {
                    e--;
                }
                // Uses '!' in order to catch NaN values.
                if (!(Math.abs(e) <= tolerance)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public synchronized MathTransform inverse() throws NoninvertibleTransformException {
        if (inverse == null) {
            if (isIdentity()) {
                inverse = this;
            } else {
                Matrix matrix = Matrices.create(numRow, numCol, elt);
                matrix = Matrices.invert(matrix);
                ProjectiveTransform inv = createInverse(matrix);
                inv.inverse = this;
                inverse = inv;
            }
        }
        return inverse;
    }

    /**
     * Creates an inverse transform using the specified matrix.
     * To be overridden by {@link GeocentricTranslation}.
     */
    ProjectiveTransform createInverse(final Matrix matrix) {
        return new ProjectiveTransform(matrix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(Arrays.hashCode(elt), super.computeHashCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) { // Slight optimization
            return true;
        }
        if (mode != ComparisonMode.STRICT) {
            return equals(this, object, mode);
        }
        if (super.equals(object, mode)) {
            final ProjectiveTransform that = (ProjectiveTransform) object;
            return this.numRow == that.numRow &&
                   this.numCol == that.numCol &&
                   Arrays.equals(this.elt, that.elt);
        }
        return false;
    }
}
