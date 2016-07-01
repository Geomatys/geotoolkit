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
@Deprecated
public class Matrices extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    Matrices() {
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
