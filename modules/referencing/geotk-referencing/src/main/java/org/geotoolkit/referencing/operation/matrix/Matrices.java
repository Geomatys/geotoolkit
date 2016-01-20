/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.matrix;

import java.awt.geom.AffineTransform;
import org.opengis.referencing.operation.Matrix;
import org.geotoolkit.lang.Static;


/**
 * Static utility methods for creating and manipulating matrices. The factory methods select one of
 * the {@link Matrix1}, {@link Matrix2}, {@link Matrix3}, {@link Matrix4} or {@link GeneralMatrix}
 * implementations according the desired matrix size. Note that if the matrix size is know at compile
 * time, it may be more efficient to invoke directly the constructor of the appropriate class instead.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 4.00
 *
 * @since 3.20 (derived from 2.2)
 * @module
 */
public class Matrices extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    Matrices() {
    }

    /**
     * Returns {@code true} if the given matrix is affine.
     *
     * @param matrix The matrix to test.
     * @return {@code true} if the matrix is affine.
     *
     * @see XMatrix#isAffine()
     *
     * @since 3.20 (derived from 3.17)
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.referencing.operation.matrix.Matrices#isAffine(Matrix)},
     *             at the difference that SIS requires the matrix to be square.
     */
    @Deprecated
    public static boolean isAffine(final Matrix matrix) {
        if (matrix instanceof AffineTransform) {
            return true;
        }
        if (matrix instanceof GeneralMatrix) {
            return ((GeneralMatrix) matrix).isAffine();
        }
        double expected = 1;
        final int lastRow = matrix.getNumRow() - 1;
        for (int i=matrix.getNumCol(); --i>=0;) {
            if (matrix.getElement(lastRow, i) != expected) {
                return false;
            }
            expected = 0;
        }
        return true;
    }

    /**
     * Returns the given matrix resized to the given dimensions. This method can be invoked for
     * adding or removing dimensions to an affine transform. The added or removed dimensions are
     * always the last ones. More specifically:
     * <p>
     * <ul>
     *   <li>If source and target dimensions are added, the corresponding offset and scale factors
     *       will be 0 and 1 respectively. In other words, new dimensions are propagated unchanged.</li>
     *   <li>New source dimensions have no impact on existing dimensions (the corresponding scale
     *       factors are set to zero).</li>
     * </ul>
     * <p>
     * The caller should ensure that {@link #isAffine(Matrix)} returns {@code true} before to invoke
     * this method, since this is not verified by this method.
     *
     * @param  matrix The matrix to resize.
     * @param  sourceDimension The desired number of source dimensions.
     * @param  targetDimension The desired number of target dimensions.
     * @return The matrix for the given number of dimensions. This will be the {@code matrix}
     *         argument itself if no resizing was needed.
     *
     * @since 3.20 (derived from 3.16)
     *
     * @deprecated Replace by Apache SIS {@link org.apache.sis.referencing.operation.matrix.Matrices#createPassThrough(int, Matrix, int)}.
     */
    @Deprecated
    public static Matrix resizeAffine(Matrix matrix, final int sourceDimension, final int targetDimension) {
        final int oldSrcDim = matrix.getNumCol() - 1;
        final int oldTgtDim = matrix.getNumRow() - 1;
        if (oldSrcDim != sourceDimension && oldTgtDim != targetDimension) {
            final Matrix resized = org.apache.sis.referencing.operation.matrix.Matrices.createDiagonal(targetDimension+1, sourceDimension+1);
            final int commonRows = Math.min(targetDimension, oldTgtDim);
            final int commonCols = Math.min(sourceDimension, oldSrcDim);
            for (int j=0; j<commonRows; j++) {
                // Set the scale factor to zero only for existing dimensions
                // (not for new target dimensions added by this method call).
                if (j >= commonCols && j < targetDimension) {
                    resized.setElement(j, j, 0);
                }
                // Copy the scale and shear factors.
                for (int i=0; i<commonCols; i++) {
                    resized.setElement(j, i, matrix.getElement(j, i));
                }
                // Copy the translation term.
                resized.setElement(j, sourceDimension, matrix.getElement(j, oldSrcDim));
            }
            matrix = resized;
        }
        return matrix;
    }

    /**
     * Modifies the given matrix in order to reverse the direction of the axis at the given
     * dimension. The matrix is assumed affine, but this is not verified.
     *
     * @param matrix    The matrix to modify.
     * @param dimension The dimension of the axis to reverse.
     * @param span      The envelope span at the dimension of the axis to be reversed,
     *                  in units of the source coordinate system.
     *
     * @since 3.16
     *
     * @deprecated No replacement, since experience has shown that this operation causes more problems
     *             than solutions.
     */
    @Deprecated
    public static void reverseAxisDirection(final Matrix matrix, final int dimension, final double span) {
        final int numRows = matrix.getNumRow();
        final int lastCol = matrix.getNumCol() - 1;
        for (int j=0; j<numRows; j++) {
            final double scale = matrix.getElement(j, dimension);
            if (scale != 0) {
                // The formula below still work with scale=0, but we don't want
                // to change the scale sign from positive zero to negative zero.
                matrix.setElement(j, dimension, -scale);
                matrix.setElement(j, lastCol, matrix.getElement(j, lastCol) + scale*span);
            }
        }
    }
}
