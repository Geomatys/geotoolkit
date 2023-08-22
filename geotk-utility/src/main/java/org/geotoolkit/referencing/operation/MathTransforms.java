/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.referencing.operation;

import java.awt.geom.AffineTransform;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.geotoolkit.lang.Static;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.util.j2d.AffineTransform2D;

import static org.apache.sis.util.ArgumentChecks.*;


/**
 * Utility methods related to {@link MathTransform}s.
 *
 * @deprecate Moved to Apache SIS {@link org.apache.sis.referencing.operation.transform.MathTransforms}.
 */
@Deprecated
public final class MathTransforms extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private MathTransforms() {
    }

    /**
     * Creates an affine transform from the specified Java2D object.
     * The matrix coefficients are used in the same way than {@link #linear(Matrix)}.
     *
     * @param matrix The matrix used to define the affine transform.
     * @return The affine transform.
     */
    public static LinearTransform linear(final AffineTransform matrix) {
        ensureNonNull("matrix", matrix);
        if (matrix instanceof LinearTransform) {
            return (LinearTransform) matrix;
        }
        return matrix.isIdentity() ? org.apache.sis.referencing.operation.transform.MathTransforms.identity(2) : new AffineTransform2D(matrix);
    }
}
