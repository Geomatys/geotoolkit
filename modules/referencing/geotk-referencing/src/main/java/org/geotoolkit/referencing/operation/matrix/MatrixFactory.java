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


/**
 * Static utility methods for creating matrix. This factory selects one of the {@link Matrix1},
 * {@link Matrix2}, {@link Matrix3}, {@link Matrix4} or {@link GeneralMatrix} implementation
 * according the desired matrix size. Note that if the matrix size is know at compile time,
 * it may be more efficient to invoke directly the constructor of the appropriate class instead.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.2
 * @module
 *
 * @deprecated Renamed {@link Matrices} in order to allow addition of non-factory methods.
 */
@Deprecated
public final class MatrixFactory extends Matrices {
    /**
     * Do not allows instantiation of this class.
     */
    private MatrixFactory() {
    }

    /**
     * Creates a new matrix which is a copy of the specified matrix.
     *
     * @param matrix The matrix to copy.
     * @return A copy of the given matrix.
     *
     * @deprecated Renamed {@link #copy(Matrix)}
     */
    @Deprecated
    public static XMatrix create(final Matrix matrix) {
        return copy(matrix);
    }
}
