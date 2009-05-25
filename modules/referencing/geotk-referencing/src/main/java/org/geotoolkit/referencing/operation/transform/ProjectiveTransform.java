/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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
import javax.vecmath.MismatchedSizeException;
import javax.vecmath.SingularMatrixException;

import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.parameter.MatrixParameters;
import org.geotoolkit.referencing.operation.provider.Affine;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.internal.referencing.MatrixUtilities.*;


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
 * {@preformat text
 *     x' = u/t
 *     y' = v/t
 *     y' = w/t
 * }
 *
 * where {@code (u,v,w)} are obtained by:
 *
 * {@preformat text
 *     ┌   ┐     ┌                    ┐ ┌   ┐
 *     │ u │     │ m00  m01  m02  m03 │ │ x │
 *     │ v │  =  │ m10  m11  m12  m13 │ │ y │
 *     │ w │     │ m20  m21  m22  m23 │ │ z │
 *     │ t │     │ m30  m31  m32  m33 │ │ 1 │
 *     └   ┘     └                    ┘ └   ┘
 * }
 *
 * In the special case of an affine transform, the last row contains only zero
 * values except in the last column, which contains 1.
 * <p>
 * This transform is used for the following operations:
 * <ul>
 *   <li>"<cite>Longitude rotation</cite>" (EPSG 9601)</li>
 *   <li>"<cite>Affine general parametric transformation</cite>"
 *       (EPSG 9624, OGC's name {@code "Affine"}) with default matrix size of
 *       {@value org.geotoolkit.parameter.MatrixParameterDescriptors#DEFAULT_MATRIX_SIZE}&times;{@value
 *       org.geotoolkit.parameter.MatrixParameterDescriptors#DEFAULT_MATRIX_SIZE}.</li>
 * </ul>
 * <p>
 * See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Affine}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LongitudeRotation}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see javax.media.jai.PerspectiveTransform
 * @see java.awt.geom.AffineTransform
 * @see <A HREF="http://mathworld.wolfram.com/AffineTransformation.html">Affine transformation on MathWorld</A>
 *
 * @since 1.2
 * @module
 */
public class ProjectiveTransform extends AbstractMathTransform implements LinearTransform, Serializable {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -2104496465933824935L;

    /**
     * The number of rows.
     */
    private final int numRow;

    /**
     * The number of columns.
     */
    private final int numCol;

    /**
     * Elements of the matrix. Column indice vary fastest.
     */
    private final double[] elt;

    /**
     * The inverse transform. Will be created only when first needed. This field is part of
     * the serialization form in order to avoid rounding errors if a user asks for the inverse
     * of the inverse (i.e. the original transform) after deserialization.
     */
    private ProjectiveTransform inverse;

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
     */
    public static LinearTransform create(final Matrix matrix) {
        final int sourceDimension = matrix.getNumCol() - 1;
        final int targetDimension = matrix.getNumRow() - 1;
        if (sourceDimension == targetDimension) {
            if (matrix.isIdentity()) {
                return IdentityTransform.create(sourceDimension);
            }
            final XMatrix m = toXMatrix(matrix);
            if (m.isAffine()) {
                switch (sourceDimension) {
                    case 1: return LinearTransform1D.create(m.getElement(0,0), m.getElement(0,1));
                    case 2: return create(toAffineTransform(m));
                }
            } else if (sourceDimension == 2) {
                return new ProjectiveTransform2D(matrix);
            }
        }
        return new ProjectiveTransform(matrix);
    }

    /**
     * Creates a transform for the specified matrix as a Java2D object.
     * This method is provided for interoperability with
     * <A HREF="http://java.sun.com/products/java-media/2D/index.jsp">Java2D</A>.
     *
     * @param matrix The affine transform as a matrix.
     * @return The transform for the given matrix.
     */
    public static LinearTransform create(final AffineTransform matrix) {
        if (matrix.isIdentity()) {
            return IdentityTransform.create(2);
        }
        return new AffineTransform2D(matrix);
    }

    /**
     * Creates a transform that apply a uniform scale along all axis.
     *
     * @param dimension The input and output dimensions.
     * @param scale The scale factor.
     * @return The scale transform.
     *
     * @since 2.3
     */
    public static LinearTransform createScale(final int dimension, final double scale) {
        if (scale == 1) {
            return IdentityTransform.create(dimension);
        }
        final Matrix matrix = new GeneralMatrix(dimension + 1);
        for (int i=0; i<dimension; i++) {
            matrix.setElement(i, i, scale);
        }
        return create(matrix);
    }

    /**
     * Creates a transform that apply the same translation along all axis.
     *
     * @param dimension The input and output dimensions.
     * @param offset The translation.
     * @return The offset transform.
     *
     * @since 2.3
     */
    public static LinearTransform createTranslation(final int dimension, final double offset) {
        if (offset == 0) {
            return IdentityTransform.create(dimension);
        }
        final Matrix matrix = new GeneralMatrix(dimension + 1);
        for (int i=0; i<dimension; i++) {
            matrix.setElement(i, dimension, offset);
        }
        return create(matrix);
    }

    /**
     * Creates a matrix that keep only a subset of the ordinate values.
     * The dimension of source coordinates is {@code sourceDim} and
     * the dimension of target coordinates is {@code toKeep.length}.
     *
     * @param  sourceDim the dimension of source coordinates.
     * @param  toKeep the indices of ordinate values to keep.
     * @return The matrix to give to the {@link #create(Matrix)}
     *         method in order to create the transform.
     * @throws IndexOutOfBoundsException if a value of {@code toKeep}
     *         is lower than 0 or not smaller than {@code sourceDim}.
     */
    public static XMatrix createSelectMatrix(final int sourceDim, final int[] toKeep)
            throws IndexOutOfBoundsException
    {
        final int targetDim = toKeep.length;
        final XMatrix matrix = MatrixFactory.create(targetDim+1, sourceDim+1);
        matrix.setZero();
        for (int j=0; j<targetDim; j++) {
            matrix.setElement(j, toKeep[j], 1);
        }
        matrix.setElement(targetDim, sourceDim, 1);
        return matrix;
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
     * Transforms a single coordinate point.
     */
    @Override
    protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff) {
        transform(srcPts, srcOff, dstPts, dstOff, 1);
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
     * @param numPts The number of points to be transformed
     */
    @Override
    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        final int srcDim, dstDim;
        int srcInc = srcDim = numCol-1; // The last ordinate will be assumed equal to 1.
        int dstInc = dstDim = numRow-1;
        final double[] buffer = new double[numRow];
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
        while (--numPts >= 0) {
            int mix = 0;
            for (int j=0; j<numRow; j++) {
                double sum = elt[mix + srcDim];
                for (int i=0; i<srcDim; i++) {
                    sum += srcPts[srcOff + i] * elt[mix++];
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
     * @param numPts The number of points to be transformed
     */
    @Override
    public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts) {
        final int srcDim, dstDim;
        int srcInc = srcDim = numCol-1;
        int dstInc = dstDim = numRow-1;
        final double[] buffer = new double[numRow];
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
        while (--numPts >= 0) {
            int mix = 0;
            for (int j=0; j<numRow; j++) {
                double sum = elt[mix + srcDim];
                for (int i=0; i<srcDim; i++) {
                    sum += srcPts[srcOff + i] * elt[mix++];
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
     * @param numPts The number of points to be transformed
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
                    sum += srcPts[srcOff + i] * elt[mix++];
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
     * @param numPts The number of points to be transformed
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
                    sum += srcPts[srcOff + i] * elt[mix++];
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
     */
    @Override
    public Matrix derivative(final Point2D point) {
        return derivative((DirectPosition)null);
    }

    /**
     * Gets the derivative of this transform at a point.
     * For a matrix transform, the derivative is the same everywhere.
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
        return MatrixFactory.create(numRow, numCol, elt);
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
                final XMatrix matrix = MatrixFactory.create(numRow, numCol, elt);
                try {
                    matrix.invert();
                } catch (SingularMatrixException exception) {
                    throw new NoninvertibleTransformException(Errors.format(
                            Errors.Keys.NONINVERTIBLE_TRANSFORM), exception);
                } catch (MismatchedSizeException exception) {
                    // This exception is thrown if the matrix is not square.
                    throw new NoninvertibleTransformException(Errors.format(
                            Errors.Keys.NONINVERTIBLE_TRANSFORM), exception);
                }
                inverse = createInverse(matrix);
                inverse.inverse = this;
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
     * Returns a hash value for this transform. This value need not remain
     * consistent between different implementations of the same class.
     */
    @Override
    public int hashCode() {
        return (numRow + 31*numCol)*31 + Arrays.hashCode(elt);
    }

    /**
     * Compares the specified object with this math transform for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object)) {
            final ProjectiveTransform that = (ProjectiveTransform) object;
            return this.numRow == that.numRow &&
                   this.numCol == that.numCol &&
                   Arrays.equals(this.elt, that.elt);
        }
        return false;
    }
}
